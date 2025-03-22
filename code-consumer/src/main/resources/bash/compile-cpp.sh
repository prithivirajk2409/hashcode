#!/bin/bash

cd /app/execution/info

> AcceptanceStatus.txt
> CompilationOutput.txt

g++ -Werror code.cpp -o code 2> CompilationOutput.txt

exit_status=$?

if [ $exit_status -ne 0 ]; then
    echo -n "COMPILATION_ERROR" >> AcceptanceStatus.txt
    exit 0
fi