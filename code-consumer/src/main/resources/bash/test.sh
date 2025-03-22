#!/bin/bash

cd /tmp

> FinalResult.txt

./compile.sh

if [ "$(cat AcceptanceStatus.txt)" == "COMPILATION_ERROR" ]; then
  temp_result="{"
  temp_result+="\"acceptanceStatus\":\"$(cat AcceptanceStatus.txt)\","
  temp_result+="\"compilationOutput\":$(printf '%s' "$(<CompilationOutput.txt)" | jq -Rs .)"
  temp_result+="}"
  echo "$temp_result" > FinalResult.txt
  exit 0
fi
  


unescaped_json=$(cat input_array.txt)

total_items=$(echo "$unescaped_json" | jq '. | length')
index=0
cumulativeAcceptanceStatus="ACCEPTED"
metadataList="["
while read -r json_item; do
  input=$(echo "$json_item" | jq -r '.input')
  expected=$(echo "$json_item" | jq -r '.expected')
  ./run.sh "$input" "$expected"
  
  if [ "$(cat AcceptanceStatus.txt)" != "ACCEPTED" ] && [ "$(cat AcceptanceStatus.txt)" != "WRONG_ANSWER" ]; then
    result="{"
    result+="\"input\":$(printf '%s' "$input" | jq -Rs .),"
    result+="\"expected\":$(printf '%s' "$expected" | jq -Rs .),"
    result+="\"acceptanceStatus\":\"$(cat AcceptanceStatus.txt)\","
    result+="\"runtimeOutput\":$(printf '%s' "$(<RuntimeOutput.txt)" | jq -Rs .)"
    result+="}"
    echo "$result" > FinalResult.txt
    exit 0
  fi

  if [ "$(cat AcceptanceStatus.txt)" == "WRONG_ANSWER" ]; then
    cumulativeAcceptanceStatus="WRONG_ANSWER"
  fi
  
  executionTime=$(sed -n '1p' Metadata.txt)
  executionMemory=$(sed -n '2p' Metadata.txt)
  temp_result="{"
  temp_result+="\"input\":$(printf '%s' "$input" | jq -Rs .),"
  temp_result+="\"output\":$(printf '%s' "$(<Output.txt)" | jq -Rs .),"
  temp_result+="\"expected\":$(printf '%s' "$expected" | jq -Rs .),"
  temp_result+="\"acceptanceStatus\":\"$(cat AcceptanceStatus.txt)\","
  temp_result+="\"executionTime\": $executionTime,"
  temp_result+="\"executionMemory\": $executionMemory"
  temp_result+="}"

  metadataList+=$temp_result
  ((index++))
  
  if [ "$index" -lt "$total_items" ]; then
    metadataList+=","
  fi
  
done < <(echo "$unescaped_json" | jq -c '.[]')

metadataList+="]"

result="{"
result+="\"metadata\": $metadataList,"
result+="\"acceptanceStatus\":\"$cumulativeAcceptanceStatus\""
result+="}"

echo "$result" > FinalResult.txt

> input_array.txt