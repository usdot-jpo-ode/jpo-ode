#!/bin/sh
# usage: ./release-prepare.sh <currentVersion> [<remote>]
if [ $# -lt 1 ]
  then
    echo usage: "./release-prepare.sh <releaseVersion> [<remote>]"
	echo 
    echo        "<releaseVersion> should be numeric version spec only. DO NOT add -SNAPSHOT"
    echo        "<remote> argument is optional. It will push to the origin if not given."
    echo        "For example: release-prepare.sh 1.0.1"
	exit -1
fi

if [ "_$2_" = "__" ]
  then
    remote=origin
  else
    remote=$2
fi

releaseVersion=$1

echo Releasing version $releaseVersion and preparing for next development iteration
mvn -DautoVersionSubmodules -DpushChanges=false -dreleaseVersion=$releaseVersion release:prepare

#Checkout and fetch the `stage` branch
git checkout -B stage $remote/stage
git clean --force
git fetch --recurse-submodules=yes

#Merge from release tag to `stage`
git merge --strategy=recursive -Xtheirs --quiet -m "merged $releaseVersion to 'stage'" jpo-ode-$releaseVersion

#Push to SCM
git push $remote dev
git push $remote stage

echo "Release $releaseVersion Complete"
