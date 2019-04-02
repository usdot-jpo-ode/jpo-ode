#!/bin/sh
echo "[FULL TEST] Starting all tests..."
echo "====="
python test-harness.py --config-file config/bsmLogDuringEvent.ini --data-file ../data/bsmLogDuringEvent.gz --kafka-topic topic.OdeBsmJson --output-file results_bsmLogDuringEvent.log
echo "====="
python test-harness.py --config-file config/bsmTx.ini --data-file ../data/bsmTx.gz --kafka-topic topic.OdeBsmJson --output-file results_bsmTx.log
echo "====="
python test-harness.py --config-file config/driverAlert.ini --data-file ../data/driverAlert.gz --kafka-topic topic.OdeDriverAlertJson --output-file results_driverAlert.log
echo "====="
python test-harness.py --config-file config/rxMsg_TIM_GeneratedBy_TMC_VIA_SNMP-bsmcheck.ini --data-file ../data/rxMsg_TIM_GeneratedBy_TMC_VIA_SNMP.gz --kafka-topic topic.OdeBsmJson --output-file results_rxMsg_TIM_GeneratedBy_TMC_VIA_SNMP_bsmcheck.log
echo "====="
python test-harness.py --config-file config/rxMsg_TIM_GeneratedBy_TMC_VIA_SNMP-timcheck.ini --data-file ../data/rxMsg_TIM_GeneratedBy_TMC_VIA_SNMP.gz --kafka-topic topic.OdeTimRxJson --output-file results_rxMsg_TIM_GeneratedBy_TMC_VIA_SNMP_timcheck.log
echo "====="
python test-harness.py --config-file config/rxMsg_TIM_and_BSM-bsmcheck.ini --data-file ../data/rxMsg_BSM_and_TIM.gz --kafka-topic topic.OdeBsmJson --output-file results_rxMsg_TIM_and_BSM_bsmcheck.log
echo "====="
python test-harness.py --config-file config/rxMsg_TIM_and_BSM-timcheck.ini --data-file ../data/rxMsg_BSM_and_TIM.gz --kafka-topic topic.OdeTimRxJson --output-file results_rxMsg_TIM_and_BSM_timcheck.log
echo "====="
echo "[FULL TEST] Testing complete."
