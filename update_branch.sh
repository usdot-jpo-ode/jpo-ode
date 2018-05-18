#!/bin/bash
# usage: ./update_branch.sh branch_name

if [ $# -eq 0 ]
  then
    echo usage: ./update_branch.sh branch_name
	exit -1
fi

cp asn1_codec/asn1c_combined/J2735_201603DA.ASN .

#Run the following commands to reset existing branch
git reset --hard
git submodule foreach --recursive git reset --hard

# Pull from branch ode-769.
git checkout $1
git pull origin $1

# The next command wipes out all of the submodules and re initializes them. 
git submodule deinit -f . && git submodule update --recursive --init

# Restore the J2735 ASN file
cp ./J2735_201603DA.ASN asn1_codec/asn1c_combined/


