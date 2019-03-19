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
from validator import TestCase

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
    parser.add_argument("-f", "--file", dest="filepath", help="Path to ini configuration file used for testing.", metavar="FILEPATH", required=True)
    args = parser.parse_args()
    assert Path(args.filepath).is_file(), "File '%s' could not be found" % args.filepath

    # Parse test config and create test case
    validator = TestCase(args.filepath)

    print("[START] Beginning test routine referencing configuration file '%s'." % args.filepath)

    # Create file logger for printing results
    fileh = logging.FileHandler(validator.output_file_path, 'w')
    logger = logging.getLogger('test-harness')
    logger.setLevel(logging.INFO)
    logger.addHandler(fileh)

    # Create a kafka consumer and wait for it to connect
    print("[INFO] Creating Kafka consumer...")
    msg_queue = queue.Queue()
    kafkaListenerThread=threading.Thread(target=listen_to_kafka_topic,args=(validator.kafka_topic, msg_queue,))
    kafkaListenerThread.start()
    time.sleep(3)
    print("[INFO] Kafka consumer preparation complete.")

    # Upload the test file with known data to the ODE
    print("[INFO] Uploading test file to ODE...")
    try:
        upload_response = upload_file(validator.input_file_path)
        if upload_response.status_code == 200:
            print("[INFO] Test file uploaded successfully.")
        else:
            print("[ERROR] Aborting test routine! Test file failed to upload, response code %d" % upload_response.status_code)
            raise SystemExit
    except requests.exceptions.ConnectTimeout as e:
        print("[ERROR] Aborting test routine! Test file upload failed (unable to reach to ODE). Error: '%s'" % str(e))
        return

    # Wait for as many messages as possible to be collected and verify it matches expected count
    print("[INFO] Waiting for all messages to be received...")
    kafkaListenerThread.join()
    msgs_received = msg_queue.qsize()
    print("[INFO] Found %d messages in file (expected %d)." % (msgs_received, validator.expected_messages))
    if validator.expected_messages != msgs_received:
        print("[FAILED] Expected %d messages but received %d." % (validator.expected_messages, msgs_received))
        return

    # After all messages were received, iterate and validate them
    num_errors = 0
    num_validations = 0
    while not msg_queue.empty():
        current_msg = json.loads(msg_queue.get())
        logger.info("======")
        logger.info("RECORD_ID: %s" % str(current_msg['metadata']['serialId']['recordId']))
        validation_results = validator.validate(current_msg, logger)
        num_errors += validation_results.num_errors
        num_validations += validation_results.num_validations

    if num_errors > 0:
        print('[FAILED] ============================================================================')
        print('[FAILED] Validation has failed! Detected %d errors out of %d total validation checks.' % (num_errors, num_validations))
        print('[FAILED] ============================================================================')
    else:
        print('[SUCCESS] ===========================================================================')
        print('[SUCCESS] Validation has passed. Detected 0 errors out of %d total validation checks.' % (num_validations))
        print('[SUCCESS] ===========================================================================')
    print("[END] File validation complete. Results logged to '%s'." % validator.output_file_path)

if __name__ == '__main__':
    main()
