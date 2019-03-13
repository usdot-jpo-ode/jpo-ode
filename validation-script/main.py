import threading
import requests
import time
import queue
import json
import os
from kafka import KafkaConsumer
import unittest
import validator as MetadataValidator

DOCKER_HOST_IP=os.getenv('DOCKER_HOST_IP')
assert DOCKER_HOST_IP != None, "Failed to get DOCKER_HOST_IP from environment variable"

TEST_DATA = '{"metadata":{"bsmSource":"RV","securityResultCode":"success","recordGeneratedBy":"TMC_VIA_SNMP","receivedMessageDetails":{"locationData":{"elevation":1818.7,"heading":0,"latitude":41.1553613,"speed":0.02,"longitude":-104.6599478},"rxSource":"SNMP"},"schemaVersion":6,"payloadType":"us.dot.its.jpo.ode.model.OdeTimPayload","serialId":{"recordId":254,"serialNumber":1644,"streamId":"8f02ae49-da48-4a8f-b21d-68bd1da649d1","bundleSize":393,"bundleId":8},"sanitized":false,"recordGeneratedAt":"2018-12-05T20:23:41.297Z","recordType":"rxMsg","logFileName":"rxMsg_BSM&TIM.gz","odeReceivedAt":"2019-03-11T13:51:42.361Z"},"payload":{"data":{"MessageFrame":{"messageId":31,"value":{"TravelerInformation":{"timeStamp":440100,"packetID":300000000000000015,"urlB":null,"dataFrames":{"TravelerDataFrame":{"regions":{"GeographicalPath":{"closedPath":{"false":""},"anchor":{"lat":263041699,"long":-801463230},"name":"sirius_sirius_1_2_SAT-7EFD0225","laneWidth":15000,"directionality":{"both":""},"description":{"path":{"offset":{"xy":{"nodes":{"NodeXY":[{"delta":{"node-LatLon":{"lon":-801458685,"lat":263041564}}},{"delta":{"node-LatLon":{"lon":-801443410,"lat":263041226}}}]}}},"scale":0}},"id":{"id":0,"region":0},"direction":1111111111111111}},"duratonTime":32000,"sspMsgRights1":1,"sspMsgRights2":1,"startYear":2018,"msgId":{"roadSignID":{"viewAngle":1111111111111111,"mutcdCode":{"warning":""},"position":{"lat":263041699,"long":-801463230}}},"priority":5,"content":{"advisory":{"SEQUENCE":{"item":{"itis":7169}}}},"url":null,"sspTimRights":1,"sspLocationRights":1,"frameType":{"advisory":""},"startTime":440100}},"msgCnt":1}}}},"dataType":"TravelerInformation"}}'

# fileType(s): bsmlog, obulog
def upload_file(filepath):
    destination_url = 'http://' + DOCKER_HOST_IP + ':8080/upload/bsmlog'
    with open(filepath, 'rb') as file:
        return requests.post(destination_url, files={'name':'file', 'file':file})


# listens to Kafka
def listenToKafkaTopic(topic, msgQueue, expectedMsgCount):
   consumer=KafkaConsumer(topic, bootstrap_servers=DOCKER_HOST_IP+':9092')
   msgsReceived=0
   for msg in consumer:
       msgsReceived += 1
       MetadataValidator.validate_metadata(json.loads(msg.value)['metadata'])
       msgQueue.put(msg)
       if msgsReceived >= expectedMsgCount:
          print("======")
          print("[SUCCESS] All validation tests have passed.")
          print("======")
          return

# main
def main():
    # Create a kafka consumer and wait for it to connect
    print("======")
    print("Preparing...")
    topic_name='topic.OdeBsmJson'
    msg_queue = queue.Queue()
    kafkaListenerThread=threading.Thread(target=listenToKafkaTopic,args=(topic_name, msg_queue, 222,))
    kafkaListenerThread.start()
    time.sleep(5)
    print("Preparation complete.")
    print("======")
    print("Uploading test file to ODE...")
    upload_response = upload_file('./data/bsmLogDuringEvent.gz')
    if upload_response.status_code == 200:
        print("Test file uploaded successfully.")
    else:
        print("Error, aborting test routine! Test file failed to upload, response code %d" % upload_response.status_code)
        raise SystemExit
    print("======")
    print("Starting metadata validation...")

if __name__ == '__main__':
    main()
