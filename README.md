# Java-приложение для синхронизации и выгрузки данных при работе с базой данных

## Инструкции по запуску

### Предварительные условия

1.  **Установка СУБД:** Убедитесь, что у вас установлена и запущена база данных (например, PostgreSQL, H2, MySQL).
2.  **Настройка базы данных:**
    * Используйте скрипт init.sql в папке sql для создания таблицы и наполнения.
3.  **Файл настроек `application.properties`:**
    * Файл с настройками располагается в папке resources.
    * Пример содержания:
        ```properties
        dataSource.driverClassName=org.postgresql.Driver
        dataSource.url=jdbc:postgresql://localhost:5432/postgres
        dataSource.username=postgres
        dataSource.password=postgres
        
        logging.file.path=logs/application.log
        ```
      (Замените на свои параметры подключения к БД.)

### Сборка проекта

**Для Maven:**
```bash
mvn clean install
```

### Запуск проекта
С консоли:
```bash
java -jar ./target/job-statistic-1.0-SNAPSHOT.jar sync ./job.xml
```
```bash
java -jar ./target/job-statistic-1.0-SNAPSHOT.jar save ./job.xml
```

Есть готовые скрипты по запуску приложения, они расположены в корне репозитория с расширениями sh и bat.

Для синхронизации предварительно создайте файл job.xml в корне репозитория, если используете готовые скрипты.