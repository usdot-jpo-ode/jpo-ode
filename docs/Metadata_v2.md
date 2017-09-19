# Data Types

### Wyoming CV Data

Near real-time feed of CV data coming in from the [Wyoming Connected Vehicle Pilot]( https://www.its.dot.gov/pilots/pilots_wydot.htm).

### BSM Data Format

All files are in a JSON format and are broken into three core fields:

- metadata - Includes all additional metadata information added to the file to provide additional contex for the data
- payload - The [J2735 Standard](http://standards.sae.org/j2735_201603/) information that includes information like vehicle location, speed, and heading
- schemaVersion - Version number of the full file schema

#### Metadata 

Field Name | Definition 
--- | --- 
generatedAt | Closest time to which the message was created, either signed or received by On Board Unit (OBU) in UTC format. This information is taken from the communication header.
logFileName | Name of the original file that deposited the message into the ODE
payloadType | ODE specific Java class identifying the type of payload included with the message
serialId | Unique record identifier for the message
serialId/streamId |Stream that process the original log file
serialId/bundleSize | Size of the bundle within the processed file
serialId/bundleId | Bundle identifier
serialId/recordId | Record identier within the bundle
serialid/serialNumber | Combined identifier within open stream
receivedAt | Time ODE received the data in UTC format
schemaVersion | Version of the metadata schema
sanitized | Boolean value indicating whether the data has been sanitized by the [Privacy Module](https://github.com/usdot-jpo-ode/jpo-cvdp)
proccessedBy | Array of identifier tracking the santizations that have occurred
validSignature | Boolean of signed vs unsigned data based on the SCMS System

#### Payload 


datatype | ODE classpath for the information represented in the data field of the payload
schemaVersion | Version number of the payload schema
data | ODE JSON representation of the J2735 spec, as definted in [J2735 Standard](http://standards.sae.org/j2735_201603/)

#### Sample Data 
*Schema Version: 2*

```json
{
  "metadata": {
    "generatedAt": "2017-0714T15:46:47.707Z[UTC]",            
    "logFileName": "signed_bsm.coer",                         
    "payloadType": "us.dot.its.jpo.ode.model.OdeVehicleData", 
    "serialId": {
      "streamId": "8375ca99-8e7d-4847-908e-0df12d03dd6d",     
      "bundleSize": 1,                                        
      "bundleId": 0,                                          
      "recordId": 0,                                          
      "serialNumber": 0
    },
    "receivedAt": "2017-07-27T12:03:35.509Z[UTC]",                 
    "schemaVersion": 2,
    "sanitized": true,
    "validSignature" : true
  }
  "payload": {                                                
    "dataType": "us.dot.its.jpo.ode.plugin.j2735.J2735Bsm",
    "schemaVersion": 1,
    "data": {                                                 
      "coreData": {
        "msgCnt": 102,
        "id": "BEA10000",
        "secMark": 36799,
        "position": {
          "latitude": 41.1641851,
          "longitude": -104.843423,
          "elevation": 1896.9
        },        
        "accelSet": {
          "accelYaw": 0
        },
        "accuracy": {},
        "speed": 0,
        "heading": 450.123,
        "brakes": {
          "wheelBrakes": {
            "leftFront": false,
            "rightFront": false,
            "unavailable": true,
            "leftRear": false,
            "rightRear": false
          },
          "traction": "unavailable",
          "abs": "unavailable",
          "scs": "unavailable",
          "brakeBoost": "unavailable",
          "auxBrakes": "unavailable"
        },
        "size": {}
      },
      "partII": [
        {
          "id": "VEHICLESAFETYEXT",
          "value": {
            "pathHistory": {
              "crumbData": [
                {
                  "elevationOffset": 9.5,
                  "latOffset": 0.0000035,
                  "lonOffset": 0.0131071,
                  "timeOffset": 33.2
                },             
              ]
            },
            "pathPrediction": {
              "confidence": 0,
              "radiusOfCurve": 0
            }
          }
        }
      ]
    }
  },
  "schemaVersion": 2
}
```

