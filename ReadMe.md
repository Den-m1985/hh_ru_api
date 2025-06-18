
### Swagger доступен по адресу, при запущенном приложении:
http://localhost:8080/swagger-ui/index.html

### Как пользоваться:
Что бы пользоваться сервисом необходимо зарегистрироваться или авторизоваться.
Клиент получает пару JWT токенов. Refresh token отдается клиенту в cookies

При первом запросе на платформу hh.ru необходимо авторизироваться. 
Перейти по endpoint 
/v1/hh_ru/get_auth_url  
ты получишь ссылку для авторизации пользователя на платформе hh.ru:
```shell
https://hh.ru/oauth/authorize?response_type=code&client_id=...
```
При переходе по ссылке откроется браузер. Авторизуемся.  
Далее сайт авторизации hh.ru редиректит на: Redirect URI и отдает в нем code, 
который автоматически обменивается на пару access refresh token для работы с площадкой hh.ru

Далее необходимо получить список резюме пользователя по ендпоинту  
/v1/hh_ru/resume

Далее можно искать вакансии и откликаться на них.


### Работа с Docker
Создай общую network один раз:
```shell
docker network create --label com.docker.compose.network=hh_network hh_network
```
если надо удалить network:
```shell
docker network rm hh_network
```
Запусти базу данных:
```shell
docker compose -f docker-compose.db.yml up -d
```
Затем приложение (не забываем про файл .env рядом с docker-compose.app.yml):
```shell
docker compose -f docker-compose.app.yml up -d
   ```
Если надо пересобрать образ перед запуском docker compose, используй флаг --build:
```shell
docker compose -f docker-compose.app.yml up -d --build
```

Проверка(убедись, что оба контейнера в одной сети):
```shell
docker network inspect hh_network
```
Оба контейнера должны быть запущены:
```shell
docker ps
 ```
