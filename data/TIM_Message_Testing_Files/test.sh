#!/bin/bash
for filename in $( ls | grep tim ); do
  echo "Sending $filename"
  curl -X POST http://localhost:8080/tim -H 'Content-Type: application/json' --data "@$filename"
  read -p "Press enter to continue"
done
