import json
import logging
import os
import queue
import requests
import threading
import time
from argparse import ArgumentParser
from kafka import KafkaConsumer
from pathlib import Path
from odevalidator import TestCase

KAFKA_CONSUMER_TIMEOUT = 10000
KAFKA_PORT = '9092'
ODE_PORT = '8080'
ODE_REST_ENDPOINT = '/upload/bsmlog'

DOCKER_HOST_IP=os.getenv('DOCKER_HOST_IP')
assert DOCKER_HOST_IP != None, "Failed to get DOCKER_HOST_IP from environment variable"

def upload_file(filepath):
    destination_url = 'http://' + DOCKER_HOST_IP + ':' + ODE_PORT + ODE_REST_ENDPOINT
    with open(filepath, 'rb') as file:
        return requests.post(destination_url, files={'name':'file', 'file':file}, timeout=2)

def listen_to_kafka_topic(topic, msg_queue):
   consumer=KafkaConsumer(topic, bootstrap_servers=DOCKER_HOST_IP+':'+KAFKA_PORT, consumer_timeout_ms=KAFKA_CONSUMER_TIMEOUT)
   for msg in consumer:
       msg_queue.put(msg.value)

# main function using old functionality
def main():
    parser = ArgumentParser()
    parser.add_argument("--data-file", dest="data_file_path", help="Path to log data file that will be sent to the ODE for validation.", metavar="DATAFILEPATH", required=True)
    parser.add_argument("--config-file", dest="config_file_path", help="Path to ini configuration file used for testing.", metavar="CONFIGFILEPATH", required=True)
    parser.add_argument("--kafka-topic", dest="kafka_topic", help="Kafka topic to which to the test harness should listen for output messages.", metavar="KAFKATOPIC", required=True)
    parser.add_argument("--output-file", dest="output_file_path", help="Output file to which detailed validation results will be printed.", metavar="LOGFILEPATH", required=False)
    args = parser.parse_args()
    assert Path(args.config_file_path).is_file(), "Configuration file '%s' could not be found" % args.config_file_path

    # Parse test config and create test case
    validator = TestCase(args.config_file_path)

    print("[START] Beginning test routine referencing configuration file '%s'." % args.config_file_path)

    # Setup logger and set output to file if specified
    logger = logging.getLogger('test-harness')
    logger.setLevel(logging.INFO)
    if args.output_file_path is not None:
        logger.addHandler(logging.FileHandler(args.output_file_path, 'w'))

    # Create a kafka consumer and wait for it to connect
    print("[INFO] Creating Kafka consumer...")
    msg_queue = queue.Queue()
    kafkaListenerThread=threading.Thread(target=listen_to_kafka_topic,args=(args.kafka_topic, msg_queue,))
    kafkaListenerThread.start()
    time.sleep(3)
    print("[INFO] Kafka consumer preparation complete.")

    # Upload the test file with known data to the ODE
    print("[INFO] Uploading test file to ODE...")
    try:
        upload_response = upload_file(args.data_file_path)
        if upload_response.status_code == 200:
            print("[INFO] Test file uploaded successfully.")
        else:
            print("[ERROR] Aborting test routine! Test file failed to upload, response code %d" % upload_response.status_code)
            return
    except requests.exceptions.ConnectTimeout as e:
        print("[ERROR] Aborting test routine! Test file upload failed (unable to reach to ODE). Error: '%s'" % str(e))
        return

    # Wait for as many messages as possible to be collected
    print("[INFO] Waiting for all messages to be received...")
    kafkaListenerThread.join()
    print("[INFO] Received %d messages from Kafka consumer." % msg_queue.qsize())

    if msg_queue.qsize() == 0:
        print("[ERROR] Aborting test routine! Received no messages from the Kafka consumer.")
        return

    # After all messages were received, log them to a file
    validation_results = validator.validate_queue(msg_queue)

    # Count the number of validations and failed validations
    num_errors = 0
    num_validations = 0
    for result in validation_results['Results']:
        num_validations += len(result['Validations'])
        for validation in result['Validations']:
            if validation['Valid'] == False:
                num_errors += 1

    if num_errors > 0:
        print('[FAILED] ============================================================================')
        print('[FAILED] Validation has failed! Detected %d errors out of %d total validation checks.' % (num_errors, num_validations))
        print('[FAILED] ============================================================================')
    else:
        print('[SUCCESS] ===========================================================================')
        print('[SUCCESS] Validation has passed. Detected no errors out of %d total validation checks.' % (num_validations))
        print('[SUCCESS] ===========================================================================')

    # Print the validation results to a file if the user has specified one
    if args.output_file_path is not None:
        logger.info(json.dumps(validation_results))
        print("[END] Results logged to '%s'." % args.output_file_path)
    else:
        print("[END] Output file not specified, detailed results not logged.")

    print("[END] File validation complete.")

if __name__ == '__main__':
    main()
