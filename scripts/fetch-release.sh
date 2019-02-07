#!/bin/bash
# usage: ./fetch-release.sh releaseVersion

if [ $# -eq 0 ]
  then
    echo usage: ./release.sh releaseVersion
	exit -1
fi

git checkout -B stage origin/stage
git pull --recurse-submodules=yes
git checkout tags/jpo-ode-$1
git submodule update --recursive --init
