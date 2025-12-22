### Полезные команды

вход в контейнер и базу данных
```shell
docker exec -it hh_ru_db psql -U user -d hh_ru_db
```
копирование на сервер и запуск
```shell
scp docker-compose.db.yml root@<ip>:/root/
```
запуск контейнера в режиме демон
```shell
docker compose -f docker-compose.db.yml up -d
```
Пересобираем образ
```shell
docker compose up --build -d
```
или
Просто запускаем образ
```shell
docker compose up -d
```

делаем dump
```shell
docker exec -t hh_ru_db pg_dump -U user -d db > dump.sql
```

Смотрим ограничения в таблице:
```postgres-sql
SELECT conname, contype, conkey 
FROM pg_constraint 
WHERE conrelid = 'auto_response_schedules'::regclass;
```
Удаляем ограничение
```postgres-sql
ALTER TABLE auto_response_schedules
DROP CONSTRAINT uk2c1711kuotkmj134k03h691qb;
```

Для проверки можно получить полную информацию об ограничениях:
```postgres-sql
SELECT 
    c.conname AS constraint_name,
    c.contype AS constraint_type,
    a.attname AS column_name,
    pg_get_constraintdef(c.oid) AS constraint_definition
FROM 
    pg_constraint c
JOIN 
    pg_attribute a ON a.attnum = ANY(c.conkey) AND a.attrelid = c.conrelid
WHERE 
    c.conrelid = 'users'::regclass
ORDER BY 
    c.conname;
```

Переименовываем ограничения 
```postgres-sql
ALTER TABLE users DROP CONSTRAINT IF EXISTS uk6dotkott2kjsp8vw4d0m25fb7;
ALTER TABLE users DROP CONSTRAINT IF EXISTS ukdu5v5sr43g5bfnji4vb8hg5s3;
ALTER TABLE users DROP CONSTRAINT IF EXISTS ukrj1y4kd15te8mneadbqmfvdh9;

ALTER TABLE users 
  ADD CONSTRAINT uk_users_email UNIQUE (email),
  ADD CONSTRAINT uk_users_username UNIQUE (username),
  ADD CONSTRAINT uk_users_telegram_user_id UNIQUE (telegram_user_id),
  ADD CONSTRAINT uk_users_phone UNIQUE (phone);
```

Флаг -v обязательно — он удаляет volume, где хранятся данные.
```shell
docker compose down -v
```

отобразит информацию о ресурсах, используемых контейнером, включая использование памяти (в колонке MEM USAGE)
```shell
docker stats имя_контейнера
```


Создаем базу данных
```postgres-sql
CREATE DATABASE hh_ru_db;
```

Проверяем созданные базы данных
```postgres-sql
\l+
```

список таблиц в базе данных
```postgres-sql
\dt
```

выйти
```postgres-sql
\q
```

Посмотреть версию PostgreSQL
```postgres-sql
SELECT version();
```

создание архива образа приложения
```shell
docker build -t hh .
sudo docker save -o hh.tar hh
docker load < hh.tar
docker run --env-file .env --network=hh_network -p 8080:8080 hh
rm hh.tar
```
