### Регистрация нового приложение:
https://dev.hh.ru/admin

После регистрации Вы получите:  
Redirect URI  
Client ID  
Client Secret

Redirect URI нужно прописать:
http://localhost:8080/callback

серверный код лежит в ветке server

### Swagger доступен по адресу, при запущенном приложении:
http://localhost:8080/swagger-ui/index.html

При запросе на endpoint /v1/hh_ru/get_auth_url ты получишь ссылку для авторизации пользователя на платформе hh.ru:
```shell
https://hh.ru/oauth/authorize?response_type=code&client_id=...
```
При переходе по ссылке откроется браузер. Авторизуемся.  
Далее сайт авторизации hh.ru редеректит на: Redirect URI и отдает в нем code, 
который обменивается на пару access refresh token

