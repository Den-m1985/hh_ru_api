
Копирование файлов на сервер
```shell
scp -r vaiti.zip root@<ip>:/var/www/
```

Затем зайдите на сервер и распакуйте архив:
```shell
cd /var/www/
unzip vaiti.zip
```

Перезапусти Nginx
```shell
nginx -t
systemctl reload nginx
```