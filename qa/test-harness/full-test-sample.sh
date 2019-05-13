#!/bin/sh
echo "[FULL TEST] Starting all tests..."
echo "====="
python test-harness.py --data-file ../../data/bsmLogDuringEvent.gz --ode-upload-url http://${DOCKER_HOST_IP}:8080/upload/bsmlog --kafka-topics topic.OdeBsmJson --output-file bsmLogDuringEvent.log
echo "====="
python test-harness.py --data-file ../../data/bsmTx.gz --ode-upload-url http://${DOCKER_HOST_IP}:8080/upload/bsmlog --kafka-topics topic.OdeBsmJson --output-file bsmTx.log
echo "====="
python test-harness.py --data-file ../../data/dnMsg.gz --ode-upload-url http://${DOCKER_HOST_IP}:8080/upload/bsmlog --kafka-topics topic.OdeTimJson --output-file dnMsg.log
echo "====="
python test-harness.py --data-file ../../data/driverAlert.gz --ode-upload-url http://${DOCKER_HOST_IP}:8080/upload/bsmlog --kafka-topics topic.OdeDriverAlertJson --output-file driverAlert.log
echo "====="
python test-harness.py --data-file ../../data/rxMsg_BSM_and_TIM.gz --ode-upload-url http://${DOCKER_HOST_IP}:8080/upload/bsmlog --kafka-topics topic.OdeBsmJson,topic.OdeTimJson --output-file rxMsg_BSM_and_TIM.log
echo "====="
python test-harness.py --data-file ../../data/rxMsg_TIM_GeneratedBy_RSU.gz --ode-upload-url http://${DOCKER_HOST_IP}:8080/upload/bsmlog --kafka-topics topic.OdeTimJson --output-file rxMsg_TIM_GeneratedBy_RSU.log
echo "====="
python test-harness.py --data-file ../../data/rxMsg_TIM_GeneratedBy_TMC_VIA_SAT.gz --ode-upload-url http://${DOCKER_HOST_IP}:8080/upload/bsmlog --kafka-topics topic.OdeTimJson --output-file rxMsg_TIM_GeneratedBy_TMC_VIA_SAT.log
echo "====="
python test-harness.py --data-file ../../data/rxMsg_TIM_GeneratedBy_TMC_VIA_SNMP.gz --ode-upload-url http://${DOCKER_HOST_IP}:8080/upload/bsmlog --kafka-topics topic.OdeTimJson --output-file rxMsg_TIM_GeneratedBy_TMC_VIA_SNMP.log
echo "====="
for filename in $( ls ../../data/TIM_Message_Testing_Files | grep tim_ ); do
  echo "Posting request from $filename"
  python test-harness.py --data-file ../../data/TIM_Message_Testing_Files/"$filename" --ode-rest-url http://${DOCKER_HOST_IP}:8080/tim --kafka-topics topic.J2735TimBroadcastJson --output-file "$filename".log
  echo "====="
done

echo "[FULL TEST] Testing complete."
