### Config nginx
Создай или отредактируй файл:
```shell
nano /etc/nginx/sites-available/spring-ssl
```

```text
server {
    listen 443 ssl;
    server_name xn--80adtd9b.tech www.xn--80adtd9b.tech;

    ssl_certificate /etc/letsencrypt/live/xn--80adtd9b.tech/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/xn--80adtd9b.tech/privkey.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;

    root /var/www;
    index index.html;

    # Фронт доступен по https://вайти.tech/vaiti-web/
    location /vaiti-web/ {
        alias /var/www/;
        try_files $uri $uri/ =404;
    }

    # Все остальные запросы идут на Spring Boot (порт 8080)
    location / {
        proxy_pass http://localhost:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}

# Автоматический редирект с http → https
server {
    listen 80;
    server_name xn--80adtd9b.tech www.xn--80adtd9b.tech;
    return 301 https://$host$request_uri;
}
```