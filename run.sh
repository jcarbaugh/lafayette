#!/bin/bash
java -Xmx512m -classpath conf:lib:lib/lafayette.jar edu.american.weiss.lafayette.Application $1 > logs/run.log