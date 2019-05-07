#!/bin/bash
if [ $# != 3 ]; then
  echo "Missing parameters. Requires 3: 'script.sh SOURCEFILE DESTDIR NUMCOPIES'"
  exit 1
fi

mkdir -p $2

BASENAME=$(basename $1 .gz)
echo "$BASENAME"
echo "$1"


for (( i = 0; i <= $3; i++))
do
  echo "Creating $BASENAME$i.gz in $2"
  cp "$1" "./$2/$BASENAME$i.gz"
done
