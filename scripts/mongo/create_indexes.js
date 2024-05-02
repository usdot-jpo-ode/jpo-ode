// Create indexes on all collections

/*
This script is responsible for initializing the replica set, creating collections, adding indexes and TTLs
*/
console.log("Running create_indexes.js");

const ode_db = process.env.MONGO_DB_NAME;
const ode_user = process.env.MONGO_ODE_DB_USER;
const ode_pass = process.env.MONGO_ODE_DB_PASS;

const ttlInDays = process.env.MONGO_COLLECTION_TTL; // TTL in days
const expire_seconds = ttlInDays * 24 * 60 * 60;
const retry_milliseconds = 5000;

console.log("ODE DB Name: " + ode_db);

try {
    console.log("Initializing replica set...");

    var config = {
        "_id": "rs0",
        "version": 1,
        "members": [
        {
            "_id": 0,
            "host": "mongo:27017",
            "priority": 2
        },
        ]
    };
    rs.initiate(config, { force: true });
    rs.status();
} catch(e) {
    rs.status().ok
}

// name -> collection name
// ttlField -> field to perform ttl on 
// timeField -> field to index for time queries

const collections = [
    {name: "OdeBsmJson", ttlField: "recordGeneratedAt", timeField: "metadata.odeReceivedAt"},
    {name: "OdeRawEncodedBSMJson", ttlField: "recordGeneratedAt", timeField: "none"},

    {name: "OdeMapJson", ttlField: "recordGeneratedAt", timeField: "metadata.odeReceivedAt"},
    {name: "OdeRawEncodedMAPJson", ttlField: "recordGeneratedAt", timeField: "none"},

    {name: "OdeSpatJson", ttlField: "recordGeneratedAt", timeField: "metadata.odeReceivedAt"},
    {name: "OdeRawEncodedSPATJson", ttlField: "recordGeneratedAt", timeField: "none"},
    
    {name: "OdeTimJson", ttlField: "recordGeneratedAt", timeField: "metadata.odeReceivedAt"},
    {name: "OdeRawEncodedTIMJson", ttlField: "recordGeneratedAt", timeField: "none"},

    {name: "OdePsmJson", ttlField: "recordGeneratedAt", timeField: "metadata.odeReceivedAt"},
    {name: "OdeRawEncodedPsmJson", ttlField: "recordGeneratedAt", timeField: "none"},
];

// Function to check if the replica set is ready
function isReplicaSetReady() {
    let status;
    try {
        status = rs.status();
    } catch (error) {
        console.error("Error getting replica set status: " + error);
        return false;
    }

    // Check if the replica set has a primary
    if (!status.hasOwnProperty('myState') || status.myState !== 1) {
        console.log("Replica set is not ready yet");
        return false;
    }

    console.log("Replica set is ready");
    return true;
}

try{

    // Wait for the replica set to be ready
    while (!isReplicaSetReady()) {
        sleep(retry_milliseconds);
    }
    sleep(retry_milliseconds);
    // creates another user
    console.log("Creating ODE user...");
    admin = db.getSiblingDB("admin");
    // Check if user already exists
    var user = admin.getUser(ode_user);
    if (user == null) {
        admin.createUser(
            {
                user: ode_user,
                pwd: ode_pass,
                roles: [
                    { role: "readWrite", db: ode_db },
                ]
            }
        );
    } else {
        console.log("User \"" + ode_user + "\" already exists.");
    }

} catch (error) {
    print("Error connecting to the MongoDB instance: " + error);
}


// Wait for the collections to exist in mongo before trying to create indexes on them
let missing_collection_count;
const db = db.getSiblingDB(ode_db);
do {
    try {
        missing_collection_count = 0;
        const collection_names = db.getCollectionNames();
        for (collection of collections) {
            console.log("Creating Indexes for Collection" + collection["name"]);
            // Create Collection if It doesn't exist
            let created = false;
            if(!collection_names.includes(collection.name)){
                created = createCollection(collection);
                // created = true;
            }else{
                created = true;
            }

            if(created){
                if (collection.hasOwnProperty('ttlField') && collection.ttlField !== 'none') {
                    createTTLIndex(collection);  
                }


            }else{
                missing_collection_count++;
                console.log("Collection " + collection.name + " does not exist yet");
            }
        }
        if (missing_collection_count > 0) {
            print("Waiting on " + missing_collection_count + " collections to be created...will try again in " + retry_milliseconds + " ms");
            sleep(retry_milliseconds);
        }
    } catch (err) {
        console.log("Error while setting up TTL indexes in collections");
        console.log(rs.status());
        console.error(err);
        sleep(retry_milliseconds);
    }
} while (missing_collection_count > 0);

console.log("Finished Creating All TTL indexes");


function createCollection(collection){
    try {
        db.createCollection(collection.name);
        return true;
    } catch (err) {
        console.log("Unable to Create Collection: " + collection.name);
        console.log(err);
        return false;
    }
}

// Create TTL Indexes
function createTTLIndex(collection) {
    if (ttlIndexExists(collection)) {
        console.log("TTL index already exists for " + collection.name);
        return;
    }

    const collection_name = collection.name;
    const timeField = collection.ttlField;

    console.log(
        "Creating TTL index for " + collection_name + " to remove documents after " +
        expire_seconds +
        " seconds"
    );

    try {
        var index_json = {};
        index_json[timeField] = 1;
        db[collection_name].createIndex(index_json,
            {expireAfterSeconds: expire_seconds}
        );
        console.log("Created TTL index for " + collection_name + " using the field: " + timeField + " as the timestamp");
    } catch (err) {
        var pattern_json = {};
        pattern_json[timeField] = 1;
        db.runCommand({
            "collMod": collection_name,
            "index": {
                keyPattern: pattern_json,
                expireAfterSeconds: expire_seconds
            }
        });
        console.log("Updated TTL index for " + collection_name + " using the field: " + timeField + " as the timestamp");
    }

}


function ttlIndexExists(collection) {
    return db[collection.name].getIndexes().find((idx) => idx.hasOwnProperty('expireAfterSeconds')) !== undefined;
}