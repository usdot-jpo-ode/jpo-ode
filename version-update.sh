#!/bin/sh
# usage: ./version-update.sh <currentVersion> <nextVersion> [<remote>]
if [ $# -lt 2 ]
  then
    echo usage: "./version-update.sh <currentVersion> <nextVersion> [<remote>]"
	echo 
    echo        "<currentVersion> and <nextVersion> are numeric version spec only. DO NOT add -SNAPSHOT"
    echo        "For example: version-update.sh 1.0.1 1.0.2"
	exit -1
fi

if [ "_$3_" = "__" ]
  then
    remote=origin
  else
    remote=$3
fi

currentVersion=$1
nextVersion=$2

echo updating to from $currentVersion to $nextVersion for remote $remote

#Checkout and fetch the `dev` branch
git checkout -B dev $remote/dev
git fetch --recurse-submodules=yes

#Set the new version which should basically be removing -SNAPSHOT from `dev` branch
mvn versions:set -DnewVersion=$currentVersion
git add --update .
git commit -m "updated to version $currentVersion"

#Checkout and fetch the `stage` branch
git checkout -B stage $remote/stage
git fetch --recurse-submodules=yes

#Merge `dev` to `stage`
git merge --commit -m "merged `dev` to `stage`" dev

#Checkout and fetch the `dev` branch
git checkout -B dev
git fetch --recurse-submodules=yes

#Set the new SNAPSHOT version
mvn versions:set -DnewVersion=$nextVersion-SNAPSHOT
git add --update .
git commit -m "updated to version $nextVersion"

#Push to SCM
#git push dev
#git push stage
