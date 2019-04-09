#!/bin/sh
echo "[FULL TEST] Starting all tests..."
echo "====="
python test-harness.py --data-file ../../data/bsmLogDuringEvent.gz --kafka-topics topic.OdeBsmJson --output-file bsmLogDuringEvent.log
echo "====="
python test-harness.py --data-file ../../data/bsmTx.gz --kafka-topics topic.OdeBsmJson --output-file bsmTx.log
echo "====="
python test-harness.py --data-file ../../data/dnMsg.gz --kafka-topics topic.OdeTimJson --output-file dnMsg.log
echo "====="
python test-harness.py --data-file ../../data/driverAlert.gz --kafka-topics topic.OdeDriverAlertJson --output-file driverAlert.log
echo "====="
python test-harness.py --data-file ../../data/rxMsg_BSM_and_TIM.gz --kafka-topics topic.OdeBsmJson,topic.OdeTimJson --output-file rxMsg_BSM_and_TIM.log
echo "====="
python test-harness.py --data-file ../../data/rxMsg_TIM_GeneratedBy_RSU.gz --kafka-topics topic.OdeTimJson --output-file rxMsg_TIM_GeneratedBy_RSU.log
echo "====="
python test-harness.py --data-file ../../data/rxMsg_TIM_GeneratedBy_TMC_VIA_SAT.gz --kafka-topics topic.OdeTimJson --output-file rxMsg_TIM_GeneratedBy_TMC_VIA_SAT.log
echo "====="
python test-harness.py --data-file ../../data/rxMsg_TIM_GeneratedBy_TMC_VIA_SNMP.gz --kafka-topics topic.OdeTimJson --output-file results_rxMsg_TIM_GeneratedBy_TMC_VIA_SNMP.log
echo "====="
echo "[FULL TEST] Testing complete."
