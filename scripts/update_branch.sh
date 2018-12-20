#!/bin/bash
# usage: ./update_branch.sh branch_name

if [ $# -eq 0 ]
  then
    echo usage: ./update_branch.sh branch_name
	exit -1
fi

# Backup user provided source or configuration files used by submodules
cp asn1_codec/asn1c_combined/J2735_201603DA.ASN .

#Run the following commands to reset existing branch
git reset --hard
git submodule foreach --recursive git reset --hard

# Pull from the desired branch
git checkout <branch name>
git pull origin <branch name>

# The next command wipes out all of the submodules and re-initializes them. 
git submodule deinit -f . && git submodule update --recursive --init

# Restore user provided source or configuration files used by submodules
cp ./J2735_201603DA.ASN asn1_codec/asn1c_combined/


