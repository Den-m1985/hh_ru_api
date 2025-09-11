
Deploy на сервер:
Собираем образ приложения:
docker build -t hh-ru-api .

Сохраняем контейнер в файл (в ту же дирректорию, где и образ)
docker save -o hh-ru-api.tar hh-ru-api

Копируем на сервер по SSH (~ означает сохранить в домашнюю директорию)
scp hh-ru-api.tar root@<ip>:~

Перенеси docker-compose.db.yml и .env на сервер
scp docker-compose.db.yml .env root@<ip>:~
_______________________________________________________
Заходим на наш сервер:
ssh root@<ip>

Создай общую network один раз:
docker network create --label com.docker.compose.network=hh_network hh_network

Скачиваем и запускаем базу данных на сервере
docker compose -f docker-compose.db.yml up -d

Импортируем образ из файла на сервер:
docker load < hh-ru-api.tar

Запускаем контейнер на сервере в режиме демона:
docker run -d --env-file .env --network=hh_network -p 8080:8080 hh-ru-api

Удаляем файл на сервере
rm hh-ru-api.tar
