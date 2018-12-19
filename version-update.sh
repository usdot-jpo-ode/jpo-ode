#!/bin/sh
# usage: ./version-update.sh <newVersion> [<remote>]
echo $#
if [ $# -eq 0 ]
  then
    echo usage: "./version-update.sh <newVersion> [<remote>]"
	exit -1
fi

if [ "_$2_" = "__" ]
  then
    remote=origin
  else
    remote=$2
fi

newVersion=$1
echo updating to version $newVersion for remote $remote

#Checkout and fetch the `dev` branch
git checkout -B dev $remote/dev
git fetch --recurse-submodules=yes

#Set the new version which should basically be removing -SNAPSHOT from `dev` branch
mvn versions:set -DnewVersion=$newVersion
git add --update .
git commit -m "updated to version $newVersion"

#Checkout and fetch the `stage` branch
#git checkout -B stage stage
#git fetch --recurse-submodules=yes

#Merge `dev` to `stage`
#git merge --commit -m "merged `dev` to `stage`" dev
