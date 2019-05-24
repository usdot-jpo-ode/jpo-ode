import configparser
import json
import logging
import os
import queue
import requests
import threading
import time
import yaml
from kafka import KafkaConsumer
from odevalidator import TestCase
from resultprinter import ValidationResultPrinter

DEFAULT_KAFKA_TIMEOUT = 15000
KAFKA_PORT = '9092'

DOCKER_HOST_IP=os.getenv('DOCKER_HOST_IP')
assert DOCKER_HOST_IP != None, "Failed to get DOCKER_HOST_IP from environment variable"

class TestHarnessException(Exception):
    pass

class TestHarnessIteration:
    def __init__(self, config, kafka_timeout):
        self.data_file_path = config.name
        self.kafka_timeout = kafka_timeout
        self.validator = TestCase(config.get('ConfigFile'))
        self.output_file_path = config.get('OutputFile')
        self.expected_messages = int(config.get('ExpectedMessages')) if config.get('ExpectedMessages') else None # must handle optional + conversion from string to int
        try:
            self.upload_format = config.get('UploadFormat')
            self.upload_url = config['UploadUrl']
            self.list_of_kafka_topics = config['KafkaTopics'].split(",")
            for topic in self.list_of_kafka_topics: topic.strip()
        except KeyError as e:
            raise TestHarnessException("Failed to parse configuration section '%s', missing required field. Error: %s" % (self.test_name, str(e)))

    def _upload_file(self, filepath, ode_upload_url):
        if self.upload_format == 'FILE':
            with open(filepath, 'rb') as file:
                return requests.post(ode_upload_url, files={'name':'file', 'file':file}, timeout=2)
        elif self.upload_format == 'BODY':
            with open(filepath, 'rb') as file:
                return requests.post(ode_upload_url,
                    headers={'Content-type': 'application/json', 'Accept': 'application/json'},
                    data=open(filepath, 'rb'),
                    timeout=2)
        else:
            raise TestHarnessException("Failed to parse configuration section '%s', unknown UploadFormat '%s', expected FILE or BODY." % (self.test_name, self.upload_format))

    def _listen_to_kafka_topics(self, msg_queue, *topics):
        consumer=KafkaConsumer(*topics, bootstrap_servers=DOCKER_HOST_IP+':'+KAFKA_PORT, consumer_timeout_ms=self.kafka_timeout)
        msgs_received = 0
        for msg in consumer:
            msg_queue.put(str(msg.value, 'utf-8'))
            msgs_received += 1
            if self.expected_messages != None and msgs_received >= self.expected_messages:
                return

    def run(self):
        msg_queue = queue.Queue()
        print("[START] Beginning test routine for test file %s" % self.data_file_path)
        print("[INFO] Creating Kafka consumer listenting on topic(s): %s ..." % self.list_of_kafka_topics)
        kafkaListenerThread=threading.Thread(target=self._listen_to_kafka_topics, args=(msg_queue, *self.list_of_kafka_topics))
        kafkaListenerThread.start()
        time.sleep(3)
        print("[INFO] Kafka consumer preparation complete.")

        # Upload the test file with known data to the ODE
        print("[INFO] Uploading test file to ODE...")
        try:
            upload_response = self._upload_file(self.data_file_path, self.upload_url)
            print("[INFO] Upload response received: %s %s" % (upload_response.status_code, upload_response.text))
            if upload_response.status_code == 200:
                print("[INFO] Test file uploaded successfully.")
            else:
                print("[ERROR] Aborting test routine! Test file (%s) failed to upload to (%s), response code %d" % (self.data_file_path, self.upload_url, upload_response.status_code))
                return
        except (requests.exceptions.ConnectionError, requests.exceptions.ConnectTimeout) as e:
            print("[ERROR] Aborting test routine! Test file upload failed (unable to reach ODE). Error: '%s'" % str(e))
            return

        # Wait for as many messages as possible to be collected
        print("[INFO] Waiting for all messages to be received...")
        kafkaListenerThread.join()
        print("[INFO] Received %d messages from Kafka consumer." % msg_queue.qsize())

        if msg_queue.qsize() == 0:
            raise TestHarnessException("[ERROR] Aborting test routine! Received no messages from the Kafka consumer.")
        if self.expected_messages != None and self.expected_messages != msg_queue.qsize():
            raise TestHarnessException("[ERROR] Aborting test routine! Did not receive expected number of messages, expected %d but got %d." % (self.expected_messages, msg_queue.qsize()))

        # After all messages were received, log them to a file
        self.validation_results = self.validator.validate_queue(msg_queue)
        self.bundle_id = self.validation_results[0].record['metadata']['serialId']['bundleId']
        self.stream_id = self.validation_results[0].record['metadata']['serialId']['streamId']

        # Count the number of validations and failed validations
        self.num_errors = 0
        self.num_validations = 0
        for result in self.validation_results:
            self.num_validations += len(result.field_validations)
            for validation in result.field_validations:
                if validation.valid == False:
                    self.num_errors += 1


        if self.num_errors > 0:
            print('[FAILED] ============================================================================')
            print('[FAILED] Validation has failed! Detected %d errors out of %d total validation checks.' % (self.num_errors, self.num_validations))
            print('[FAILED] ============================================================================')
        else:
            print('[SUCCESS] ===========================================================================')
            print('[SUCCESS] Validation has passed. Detected no errors out of %d total validation checks.' % (self.num_validations))
            print('[SUCCESS] ===========================================================================')

        # Print the validation results to a file if the user has specified one
        if self.output_file_path is not None:
            self.print_results_to_file()
            print("[END] Results logged to '%s'." % self.output_file_path)
        else:
            print("[END] Output file not specified, detailed results not logged.")

        print("[END] File validation complete.")

    def print_results_to_file(self):
        logger = logging.getLogger('test-harness')
        logger.setLevel(logging.INFO)
        logger.addHandler(logging.FileHandler(self.output_file_path, 'w'))
        printer = ValidationResultPrinter()
        printer.print_list(self.validation_results, True, logger)


class TestHarness:
    def __init__(self, config_file_path):
        config = configparser.SafeConfigParser(os.environ)
        config.read(config_file_path)

        self.test_harness_iterations = []
        for key in config.sections():
            if key == "_meta":
                self.perform_bundle_id_check = True if config[key]['PerformBundleIdCheck'] == 'True' else False
                self.kafka_timeout = int(config[key]['KafkaTimeout']) if config[key].get('KafkaTimeout') != None else DEFAULT_KAFKA_TIMEOUT
            else:
                self.test_harness_iterations.append(TestHarnessIteration(config[key], self.kafka_timeout))

    def run(self):
        bundle_streams = {}
        for iteration in self.test_harness_iterations:
            iteration.run()
            if self.perform_bundle_id_check:
                if iteration.stream_id not in bundle_streams:
                    bundle_streams[iteration.stream_id] = []
                bundle_streams[iteration.stream_id].append(iteration.bundle_id)

        if self.perform_bundle_id_check:
            for stream_id in bundle_streams:
                print("[INFO] Performing bundleId validation on StreamId %s" % stream_id)
                old_id = bundle_streams[stream_id][0]
                bundle_ids_error = False
                for cur_id in bundle_streams[stream_id][1:]:
                    if cur_id <= old_id or cur_id != old_id + 1:
                        print("[ERROR] BundleID not incremented correctly between test iterations! Expected %d but got %d. Bundle ID list: %s" % (old_id+1, cur_id, bundle_streams[stream_id]))
                        bundle_ids_error = True
                    old_id = cur_id
                if not bundle_ids_error:
                    print("[SUCCESS] BundleID validation passed for StreamId %s." % stream_id)
