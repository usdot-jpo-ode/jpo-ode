# bin/bash
echo "------------------------------------------"
echo "Kafka connector creation started."
echo "------------------------------------------"


declare -A OdeBsmJson=([name]="topic.OdeBsmJson" [collection]="OdeBsmJson"
    [convert_timestamp]=false [timefield]="" [use_key]=false [key]="" [add_timestamp]=true)

declare -A OdeMapJson=([name]="topic.OdeMapJson" [collection]="OdeMapJson"
    [convert_timestamp]=false [timefield]="" [use_key]=false [key]="" [add_timestamp]=true)

declare -A OdeSpatJson=([name]="topic.OdeSpatJson" [collection]="OdeSpatJson"
    [convert_timestamp]=false [timefield]="" [use_key]=false [key]="" [add_timestamp]=true)

declare -A OdeTimJson=([name]="topic.OdeTimJson" [collection]="OdeTimJson"
    [convert_timestamp]=false [timefield]="" [use_key]=false [key]="" [add_timestamp]=true)

declare -A OdePsmJson=([name]="topic.OdePsmJson" [collection]="OdePsmJson"
    [convert_timestamp]=false [timefield]="" [use_key]=false [key]="" [add_timestamp]=true)

function createSink() {
    local -n topic=$1
    local name=${topic[name]}
    local collection=${topic[collection]}
    local timefield=${topic[timefield]}
    local convert_timestamp=${topic[convert_timestamp]}
    local use_key=${topic[use_key]}
    local key=${topic[key]}
    local add_timestamp=${topic[add_timestamp]}

    echo "Creating sink connector with parameters:"
    echo "name=$name"
    echo "collection=$collection"
    echo "timefield=$timefield"
    echo "convert_timestamp=$convert_timestamp"
    echo "use_key=$use_key"
    echo "key=$key"
    echo "add_timestamp=$add_timestamp"

    local connectConfig=' {
        "group.id":"connector-consumer",
        "connector.class":"com.mongodb.kafka.connect.MongoSinkConnector",
        "tasks.max":3,
        "topics":"'$name'",
        "connection.uri":"'$MONGO_URI'",
        "database":"'$MONGO_DB_NAME'",
        "collection":"'$collection'",
        "key.converter":"org.apache.kafka.connect.storage.StringConverter",
        "key.converter.schemas.enable":false,
        "value.converter":"org.apache.kafka.connect.json.JsonConverter",
        "value.converter.schemas.enable":false,
        "errors.tolerance": "all",
        "mongo.errors.tolerance": "all",
        "errors.deadletterqueue.topic.name": "",
	    "errors.log.enable": false,
        "errors.log.include.messages": false,
	    "errors.deadletterqueue.topic.replication.factor": 0' 


    if [ "$convert_timestamp" == true ]
    then
        local connectConfig=''$connectConfig',
        "transforms": "TimestampConverter",
        "transforms.TimestampConverter.field": "'$timefield'",
        "transforms.TimestampConverter.type": "org.apache.kafka.connect.transforms.TimestampConverter$Value",
        "transforms.TimestampConverter.target.type": "Timestamp"'
    fi

    if [ "$add_timestamp" == true ]
    then
        local connectConfig=''$connectConfig',
        "transforms": "AddTimestamp,AddedTimestampConverter",
        "transforms.AddTimestamp.type": "org.apache.kafka.connect.transforms.InsertField$Value",
        "transforms.AddTimestamp.timestamp.field": "recordGeneratedAt",
        "transforms.AddedTimestampConverter.field": "recordGeneratedAt",
        "transforms.AddedTimestampConverter.type": "org.apache.kafka.connect.transforms.TimestampConverter$Value",
        "transforms.AddedTimestampConverter.target.type": "Timestamp"'
    fi

    if [ "$use_key" == true ]
    then
        local connectConfig=''$connectConfig',
        "document.id.strategy": "com.mongodb.kafka.connect.sink.processor.id.strategy.PartialValueStrategy",
        "document.id.strategy.partial.value.projection.list": "'$key'",
        "document.id.strategy.partial.value.projection.type": "AllowList",
        "document.id.strategy.overwrite.existing": true'
    fi

    local connectConfig=''$connectConfig' }'

    echo " Creating connector with Config : $connectConfig"

    curl -X PUT http://localhost:8083/connectors/MongoSink.${name}/config -H "Content-Type: application/json" -d "$connectConfig"
}

createSink OdeBsmJson
createSink OdeMapJson
createSink OdeSpatJson
createSink OdeTimJson
createSink OdePsmJson

echo "----------------------------------"
echo "ODE Kafka connector creation complete!"
echo "----------------------------------"