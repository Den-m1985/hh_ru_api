### Настройка dump базы данных
Создай файл, например:
/opt/backup/pg_dump.sh
```shell
mkdir -p /opt/backup
```

pg_dump.sh  ->
```text
#!/bin/bash

# Настройки
TIMESTAMP=$(date +"%Y-%m-%d_%H-%M")
BACKUP_DIR="/opt/backup"
CONTAINER_NAME="hh_ru_db"
DB_NAME="db"
DB_USER="user"

# Убедись, что директория есть
mkdir -p "$BACKUP_DIR"

# Выполняем дамп
docker exec -t $CONTAINER_NAME pg_dump -U $DB_USER $DB_NAME > "$BACKUP_DIR/dump_$TIMESTAMP.sql"

# Очистка старых дампов (например, старше 7 дней)
find "$BACKUP_DIR" -type f -nameRequest "*.sql" -mtime +7 -exec rm {} \;
```

Сделай скрипт исполняемым:
```shell
chmod +x /opt/backup/pg_dump.sh
```

Протестируй вручную
```shell
/opt/backup/pg_dump.sh
```

Открой планировщик задач:
```shell
crontab -e
```

Добавь строку (например, каждый день в 03:00 ночи):
```shell
0 3 * * * /opt/backup/pg_dump.sh >> /opt/backup/backup.log 2>&1
```
