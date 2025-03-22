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

maxExecutionTime=0
maxExecutionMemory=0
passed_test_cases=0
total_test_cases=$(echo "$unescaped_json" | jq 'length')
temp_passed_test_cases=0


while read -r json_item; do
  input=$(echo "$json_item" | jq -r '.input')
  expected=$(echo "$json_item" | jq -r '.expected')
  ./run.sh "$input" "$expected"
  
  if [ "$(cat AcceptanceStatus.txt)" != "ACCEPTED" ]; then
    temp_result="{"
    if [ "$(cat AcceptanceStatus.txt)" == "WRONG_ANSWER" ]; then
      temp_result+="\"output\":$(printf '%s' "$(<Output.txt)" | jq -Rs .),"
    fi
    temp_result+="\"input\":$(printf '%s' "$input" | jq -Rs .),"
    temp_result+="\"expected\":$(printf '%s' "$expected" | jq -Rs .),"
    temp_result+="\"acceptanceStatus\":\"$(cat AcceptanceStatus.txt)\","
    temp_result+="\"runtimeOutput\":$(printf '%s' "$(<RuntimeOutput.txt)" | jq -Rs .),"
    temp_result+="\"processedTestCases\":$passed_test_cases,"
    temp_result+="\"totalTestCases\":$total_test_cases"
    temp_result+="}"
    echo "$temp_result" > FinalResult.txt
    exit 0
  fi
  
  executionTime=$(sed -n '1p' Metadata.txt)
  executionMemory=$(sed -n '2p' Metadata.txt)
  
  if [[ $executionTime =~ ^[0-9]+$ ]] && ((executionTime > maxExecutionTime)); then
    maxExecutionTime=$executionTime
  fi
  
  if [[ $executionMemory =~ ^[0-9]+$ ]] && ((executionMemory > maxExecutionMemory)); then
    maxExecutionMemory=$executionMemory
  fi
  
  ((passed_test_cases++))
done < <(echo "$unescaped_json" | jq -c '.[]')

temp_result="{"
temp_result+="\"executionTime\": $maxExecutionTime,"
temp_result+="\"executionMemory\": $maxExecutionMemory,"
temp_result+="\"acceptanceStatus\":\"$(cat AcceptanceStatus.txt)\","
temp_result+="\"processedTestCases\": $passed_test_cases,"
temp_result+="\"totalTestCases\": $total_test_cases"
temp_result+="}"

echo "$temp_result" > FinalResult.txt

> input_array.txt
