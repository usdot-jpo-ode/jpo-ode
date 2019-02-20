import threading
import requests
import time
import queue
import json
import os
from kafka import KafkaConsumer

DOCKER_HOST_IP=os.getenv('DOCKER_HOST_IP')
assert DOCKER_HOST_IP != None, "Failed to get DOCKER_HOST_IP from environment variable"

class TestCase:
    def __init__(self, input, expectedOutput):
        self.input = input
        self.expectedOutput = expectedOutput

        mcin = str(input['tim']['msgCnt'])
        mcout = str(expectedOutput['payload']['data']['MessageFrame']['value']['TravelerInformation']['msgCnt'])
        assert mcin == mcout, "Message count input and output do not match: input=%s, output=%s" % (mcin, mcout)
        self.msgCnt = mcin

    def compare(actualOutput):
        assert str(self.expectedOutput) == str(actualOutput), "Input file does not match expected output: expected <%s> but got <%s>" % (str(self.expectedOutput), str(actualOutput))

def sendRESTRequest(body):
    response=requests.post('http://' + DOCKER_HOST_IP + ':8080/tim', data = body)
    print("Response code: " + str(response.status_code))

def listenToKafkaTopic(topic, msgQueue, expectedMsgCount):
   print("Listening on topic: " + topic)
   consumer=KafkaConsumer(topic, bootstrap_servers=DOCKER_HOST_IP+':9092')
   msgsReceived=0
   for msg in consumer:
       msgsReceived += 1
       print(str(msgsReceived))
       msgQueue.put(msg)
       if msgsReceived >= expectedMsgCount:
           return

def createOutputList(fileDict):
    outputList=[]
    for infile, outfile in fileDict.items():
        outfileData=json.load(open(outfile, 'r').read())
        outputList.append(outfileData)
    return outputList

def findOutputFile(msgCnt, fileDict):
    for outfile in fileDict.queue:
        if json.loads(outfile.value)['payload']['data']['MessageFrame']['value']['TravelerInformation']['msgCnt'] == msgCnt:
            return outfile
    return None

def validate(testCaseList, actualMsgQueue):
    assert len(testCaseList) == actualMsgQueue.qsize(), "Input list length does not match output list length, input: %d, output: %d" % (len(testCaseList), actualMsgQueue.qsize())
    for msg in actualMsgQueue.queue:
        # Find corresponding output file
        msgCnt=json.loads(msg.value)['payload']['data']['MessageFrame']['value']['TravelerInformation']['msgCnt']
        expectedOutputFile=findOutputFile(msgCnt, actualMsgQueue)
        assert expectedOutputFile != None, "Unable to find matching output file for input file msgCnt=%s" % str(msgCnt)
        assert msg.value == expectedOutputFile.value, "Input file does not match expected output: expected <%s> but got <%s>" % (msg.value, expectedOutputFile.value)

def main():
    print("Test routine started...")
    testFileDict={
      "tim1_input.json": "tim1_output.json"
    }
    timBroadcastTopic='topic.J2735TimBroadcastJson'
    msgQueue=queue.Queue()

    # Create a kafka consumer and wait for it to connect
    kafkaListenerThread=threading.Thread(target=listenToKafkaTopic,args=(timBroadcastTopic,msgQueue, len(testFileDict)),)
    kafkaListenerThread.start()
    time.sleep(5)

    # Create test cases
    print("Creating test cases...")
    tcList = []
    for inputFilename, expectedOutputFilename in testFileDict.items():
       inputData = json.loads(open(inputFilename, 'r').read())
       expectedOutputData = json.loads(open(expectedOutputFilename, 'r').read())
       tc = TestCase(inputData, expectedOutputData)
       tcList.append(tc)

    print("Test cases created.")

    # Send test data
    for index, testCase in enumerate(tcList, start=1):
       print("Sending REST request for test case %d/%d" % (index, len(tcList)))
       sendRESTRequest(json.dumps(testCase.input))

    # Perform validation
    print("Request sending complete.")

    print("Waiting for all messages to be received...")
    kafkaListenerThread.join()
    print("All messages received.")

    print("Validating output...")
    validate(tcList, msgQueue)
    print("Output validated.")

    print("All tests have passed")
    print("End.")

if __name__ == "__main__":
    main()
