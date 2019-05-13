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
import os

KAFKA_CONSUMER_TIMEOUT = 10000
KAFKA_PORT = '9092'

DOCKER_HOST_IP=os.getenv('DOCKER_HOST_IP')
assert DOCKER_HOST_IP != None, "Failed to get DOCKER_HOST_IP from environment variable"

def upload_file(filepath, ode_upload_url):
    with open(filepath, 'rb') as file:
        return requests.post(ode_upload_url, files={'name':'file', 'file':file}, timeout=2)

def send_rest_request(filepath, ode_rest_url):
    return requests.post(ode_rest_url, 
        headers={'Content-type': 'application/json', 'Accept': 'application/json'}, 
        data=open(filepath, 'rb'), 
        timeout=2)

def listen_to_kafka_topics(msg_queue, *topics):
    consumer=KafkaConsumer(*topics, bootstrap_servers=DOCKER_HOST_IP+':'+KAFKA_PORT, consumer_timeout_ms=KAFKA_CONSUMER_TIMEOUT)
    for msg in consumer:
        msg_queue.put(str(msg.value, 'utf-8'))

# main function using old functionality
def main():
    parser = ArgumentParser()
    parser.add_argument("--data-file", dest="data_file_path", help="Path to log data file that will be sent to the ODE for validation.", metavar="DATAFILEPATH", required=True)
    parser.add_argument("--ode-upload-url", dest="ode_upload_url", help="Full URL of the ODE upload directory to which the data-file will be sent, e.g. https://ode.io:8443/upload/bsmlog. " +
        "This argument expects the --data-file being a binary ODE log file format and can be compressed .zip or .gz. " + 
        "Either --ode-upload-url or --ode-rest-url option is REQUIRED. ", metavar="ODEUPLOADURL", required=False)
    parser.add_argument("--ode-rest-url", dest="ode_rest_url", help="Full URL of the ODE REST endpoint to which the data-file containing the body of the REST request will be POSTed, e.g. https://ode.io:8443/tim. " + 
        "This argument expects the --data-file contain a JSON object containing the body of the REST request. "
        "Either --ode-upload-url or --ode-rest-url option is REQUIRED.", metavar="ODERESTURL", required=False)
    parser.add_argument("--config-file", dest="config_file_path", help="Path to ini configuration file used for testing.", metavar="CONFIGFILEPATH", required=False)
    parser.add_argument("--kafka-topics", dest="kafka_topics", help="Kafka topics on which the harness will listen and validate messages.", metavar="KAFKATOPICS", required=True)
    parser.add_argument("--output-file", dest="output_file_path", help="Output file to which detailed validation results will be printed.", metavar="LOGFILEPATH", required=False)
    args = parser.parse_args()

    # Parse test config and create test case
    validator = TestCase(args.config_file_path)

    # Setup logger and set output to file if specified
    logger = logging.getLogger('test-harness')
    logger.setLevel(logging.INFO)
    if args.output_file_path is not None:
        logger.addHandler(logging.FileHandler(args.output_file_path, 'w'))

    # Create a kafka consumer and wait for it to connect
    msg_queue = queue.Queue()
    list_of_kafka_topics = args.kafka_topics.split(",")
    for topic in list_of_kafka_topics: topic.strip()
    print("[INFO] Creating Kafka consumer listenting on %s ..." % list_of_kafka_topics)
    kafkaListenerThread=threading.Thread(target=listen_to_kafka_topics, args=(msg_queue, *list_of_kafka_topics))
    kafkaListenerThread.start()
    time.sleep(3)
    print("[INFO] Kafka consumer preparation complete.")

    # Upload the test file with known data to the ODE
    print("[INFO] Uploading test file to ODE: %s ..." % args.data_file_path)
    try:
        if args.ode_upload_url:
            upload_response = upload_file(args.data_file_path, args.ode_upload_url)
            if upload_response.status_code == 200:
                print("[INFO] Test file uploaded successfully.")
            else:
                print("[ERROR] Aborting test! Test file (%s) failed to upload to (%s), response code %d" % (args.data_file_path, args.ode_upload_url, upload_response.status_code))
                return
        elif args.ode_rest_url:
            upload_response = send_rest_request(args.data_file_path, args.ode_rest_url)
            if upload_response.status_code == 200:
                print("[INFO] Request sent successfully.")
            else:
                print("[ERROR] Aborting test! Request failed for file (%s), end-point (%s), response code %d" % (args.data_file_path, args.ode_rest_url, upload_response.status_code))
                return
        else:
            print("[ERROR] Either --ode-upload-url or --ode-rest-url command line option is REQUIRED.")
            return

    except (requests.exceptions.ConnectionError, requests.exceptions.ConnectTimeout) as e:
        print("[ERROR] Aborting test! Test failed (unable to reach ODE). Error: '%s'" % str(e))
        return

    # Wait for as many messages as possible to be collected
    print("[INFO] Waiting for all messages to be received...")
    kafkaListenerThread.join()
    print("[INFO] Received %d messages from Kafka topic %s." % (msg_queue.qsize(), args.kafka_topics))

    if msg_queue.qsize() == 0:
        print("[ERROR] Aborting test routine! Received no messages from the Kafka topic %s." % args.kafka_topics)
        return

    # After all messages were received, log them to a file
    validation_results = validator.validate_queue(msg_queue)

    # Count the number of validations and failed validations
    num_errors = 0
    num_validations = 0
    for result in validation_results:
        validations = result.field_validations
        num_validations += len(validations)
        for validation in validations:
            if validation.valid == False:
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
        logger.info("Validation Results for data file: %s" % args.data_file_path)
        for result in validation_results:
            logger.info(result)
        print("[END] Results logged to '%s'." % args.output_file_path)
    else:
        print("[END] Output file not specified, detailed results not logged.")

    print("[END] File validation complete.")

if __name__ == '__main__':
    main()
