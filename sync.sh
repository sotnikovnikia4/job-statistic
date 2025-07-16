#!/bin/bash

echo "Запуск: java -jar ./target/job-statistic-1.0-SNAPSHOT.jar sync ./job.xml"
java -jar ./target/job-statistic-1.0-SNAPSHOT.jar sync ./job.xml
echo ""

echo "Выполнение команды save завершено."
