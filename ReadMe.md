### Регистрация нового приложение:
https://dev.hh.ru/admin

После регистрации Вы получите:  
Redirect URI  
Client ID  
Client Secret

Redirect URI нужно прописать:
http://localhost:8080/callback


### Запуск приложения:
Запускаем в среде разработке.   
Получаем токен доступа:  
В консоле получаем ссылку по которой надо перейти.

```shell
https://hh.ru/oauth/authorize?response_type=code&client_id=...
```
При переходе по ссылке откроется браузер. Авторизуемся.  
Далее сайт редеректит на: Redirect URI