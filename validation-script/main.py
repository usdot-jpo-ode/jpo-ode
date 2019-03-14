import threading
import requests
import time
import queue
import json
import os
from kafka import KafkaConsumer
import unittest
import validator as MetadataValidator
from argparse import ArgumentParser

DOCKER_HOST_IP=os.getenv('DOCKER_HOST_IP')
assert DOCKER_HOST_IP != None, "Failed to get DOCKER_HOST_IP from environment variable"

def upload_file(filepath):
    destination_url = 'http://' + DOCKER_HOST_IP + ':8080/upload/bsmlog'
    with open(filepath, 'rb') as file:
        return requests.post(destination_url, files={'name':'file', 'file':file}, timeout=2)

# listens to Kafka
def listenToKafkaTopic(topic, errored):
   consumer=KafkaConsumer(topic, bootstrap_servers=DOCKER_HOST_IP+':9092', consumer_timeout_ms=7000)
   msgsReceived=0
   for msg in consumer:
       MetadataValidator.validate_metadata(json.loads(msg.value)['metadata'], errored)

def self_test():
    self_test_data = '{"metadata":{"securityResultCode":"success","recordGeneratedBy":"TMC_VIA_SNMP","receivedMessageDetails":{"locationData":{"elevation":1818.7,"heading":0,"latitude":41.1553613,"speed":0.02,"longitude":-104.6599478},"rxSource":"SNMP"},"schemaVersion":6,"payloadType":"us.dot.its.jpo.ode.model.OdeTimPayload","serialId":{"recordId":254,"serialNumber":1644,"streamId":"8f02ae49-da48-4a8f-b21d-68bd1da649d1","bundleSize":393,"bundleId":8},"sanitized":false,"recordGeneratedAt":"2018-12-05T20:23:41.297Z","recordType":"rxMsg","logFileName":"rxMsg_BSM&TIM.gz","odeReceivedAt":"2019-03-11T13:51:42.361Z"},"payload":{"data":{"MessageFrame":{"messageId":31,"value":{"TravelerInformation":{"timeStamp":440100,"packetID":300000000000000015,"urlB":null,"dataFrames":{"TravelerDataFrame":{"regions":{"GeographicalPath":{"closedPath":{"false":""},"anchor":{"lat":263041699,"long":-801463230},"name":"sirius_sirius_1_2_SAT-7EFD0225","laneWidth":15000,"directionality":{"both":""},"description":{"path":{"offset":{"xy":{"nodes":{"NodeXY":[{"delta":{"node-LatLon":{"lon":-801458685,"lat":263041564}}},{"delta":{"node-LatLon":{"lon":-801443410,"lat":263041226}}}]}}},"scale":0}},"id":{"id":0,"region":0},"direction":1111111111111111}},"duratonTime":32000,"sspMsgRights1":1,"sspMsgRights2":1,"startYear":2018,"msgId":{"roadSignID":{"viewAngle":1111111111111111,"mutcdCode":{"warning":""},"position":{"lat":263041699,"long":-801463230}}},"priority":5,"content":{"advisory":{"SEQUENCE":{"item":{"itis":7169}}}},"url":null,"sspTimRights":1,"sspLocationRights":1,"frameType":{"advisory":""},"startTime":440100}},"msgCnt":1}}}},"dataType":"TravelerInformation"}}'
    print("[INFO] Running self-test of validation routine...")
    MetadataValidator.validate_metadata(json.loads(self_test_data)['metadata'])
    print("[INFO] Self test passed.")

# main
def main():
    parser = ArgumentParser()
    parser.add_argument("-f", "--file", dest="filepath", help="Path to file used for testing.", metavar="FILE", required=True)
    args = parser.parse_args()

    # local test
    # self_test()

    # Create a kafka consumer and wait for it to connect
    print("[INFO] Preparing Kafka listener...")
    topic_name='topic.OdeBsmJson'
    msg_queue = queue.Queue()
    errored = False
    kafkaListenerThread=threading.Thread(target=listenToKafkaTopic,args=(topic_name, msg_queue,))
    kafkaListenerThread.start()
    time.sleep(3)
    print("[INFO] Kafka listener preparation complete.")

    print("[INFO] Uploading test file to ODE...")
    upload_response = upload_file(args.filepath)
    if upload_response.status_code == 200:
        print("[INFO] Test file uploaded successfully.")
    else:
        print("[ERROR] Aborting test routine! Test file failed to upload, response code %d" % upload_response.status_code)
        raise SystemExit

    print("[INFO] Starting metadata validation...")
    kafkaListenerThread.join()
    print("[INFO] Metadata validation complete.")
    if msg_queue.empty():
        print("[SUCCESS] All validation tests have passed.")
    else:
        print("[FAILED] Message file '%s' has failed validation." % args.filepath)

if __name__ == '__main__':
    main()
