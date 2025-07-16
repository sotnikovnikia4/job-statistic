#!/bin/bash
# Script to run the save command for job-statistic on Linux/macOS

echo "Запуск: java -jar ./target/job-statistic-1.0-SNAPSHOT.jar save ./job.xml"
java -jar ./target/job-statistic-1.0-SNAPSHOT.jar save ./job.xml
echo ""

echo "Выполнение команды save завершено."
