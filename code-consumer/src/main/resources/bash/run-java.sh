#!/bin/bash

cd /app/execution/info

> AcceptanceStatus.txt
> Metadata.txt
> Statistics.txt
> Output.txt
> RuntimeOutput.txt


memory_limit_mb=256

start_time=$(date +%s%N)


/usr/bin/time -v timeout 1s java -Xmx256m -Xss256k  code < Input.txt  > Output.txt 2> Statistics.txt

exit_status=$?

end_time=$(date +%s%N)

echo $exit_status

execution_time=$(printf "%.0f" $(( (end_time - start_time) / 1000000 )))

sed '/Command/Q' Statistics.txt > RuntimeOutput.txt

if [ $exit_status -eq 137 ] || [ $exit_status -eq 134 ] || grep -q "OutOfMemoryError" Statistics.txt || grep -q "bad_alloc" Statistics.txt; then
    echo -n "MEMORY_LIMIT_EXCEEDED" > AcceptanceStatus.txt
elif [ $exit_status -eq 124 ]; then
    echo -n "TIME_LIMIT_EXCEEDED" > AcceptanceStatus.txt
elif [ $exit_status -eq 139 ] || grep -q "StackOverflowError" Statistics.txt || grep -q "ArrayIndexOutOfBoundsException" Statistics.txt || grep -q "NullPointerException" Statistics.txt || grep -q "NumberFormatException" Statistics.txt || grep -q "Exception" Statistics.txt  || grep -q "Error" Statistics.txt; then
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