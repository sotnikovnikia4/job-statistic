@echo off
REM Скрипт для запуска команды save для job-statistic на Windows

chcp utf-8

echo Запуск: java -jar ./target/job-statistic-1.0-SNAPSHOT.jar save ./job.xml
java -jar ./target/job-statistic-1.0-SNAPSHOT.jar save ./job.xml
echo.

echo Выполнение команды save завершено.
pause
