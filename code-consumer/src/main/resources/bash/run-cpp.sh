#!/bin/bash

cd /app/execution/info

> AcceptanceStatus.txt
> Metadata.txt
> Statistics.txt
> Output.txt
> RuntimeOutput.txt

memory_limit_mb=256

start_time=$(date +%s%N)

#input="$1"
#expected="$2"


ulimit -v $((memory_limit_mb * 1024)) -s $((memory_limit_mb * 1024)) && /usr/bin/time -v timeout 1s ./code < Input.txt > Output.txt 2> Statistics.txt

exit_status=$?

end_time=$(date +%s%N)

#echo $exit_status

execution_time=$(printf "%.0f" $(( (end_time - start_time) / 1000000 )))

sed '/Command/Q' Statistics.txt > RuntimeOutput.txt

if [ $exit_status -eq 137 ] || [ $exit_status -eq 134 ] || [ $exit_status -eq 135 ] || grep -q "memory allocation" Statistics.txt || grep -q "bad_alloc" Statistics.txt || grep -q "dumped core" Statistics.txt; then
    echo -n "MEMORY_LIMIT_EXCEEDED" > AcceptanceStatus.txt
elif [ $exit_status -eq 124 ]; then
    echo -n "TIME_LIMIT_EXCEEDED" > AcceptanceStatus.txt
elif [ $exit_status -eq 139 ]; then
    echo -n "RUNTIME_ERROR" > AcceptanceStatus.txt
elif [ $exit_status -ne 0 ]; then
    echo -n "UNKNOWN_ERROR" > AcceptanceStatus.txt
else
    if cmp -s Output.txt Expected.txt ; then
        echo -n "ACCEPTED" > AcceptanceStatus.txt
    else
        echo -n "WRONG_ANSWER" > AcceptanceStatus.txt
    fi
    memory_usage=$(printf "%.0f"  $(grep "Maximum resident set size" Statistics.txt | awk '{print $6/1024}'))
    echo "$execution_time" > Metadata.txt
    echo -n "$memory_usage" >> Metadata.txt
fi