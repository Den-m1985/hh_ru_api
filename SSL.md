### Создание / обновление SSL серитфиката
```shell
apt update
apt install nginx
```

Установка Certbot от Let’s Encrypt
```shell
sudo apt install certbot python3-certbot-nginx
sudo certbot --nginx -d xn--80adtd9b.tech -d www.xn--80adtd9b.tech
```

Включи конфиг и перезапусти NGINX:
```shell
sudo ln -s /etc/nginx/sites-available/spring-ssl /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```
Обновление сертификатов:
```shell
sudo certbot renew --dry-run
```
