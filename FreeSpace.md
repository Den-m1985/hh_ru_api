### Если не хватает места на диске
Детализированная информация
```shell
df -h
```

Самые большие файлы/директории
```shell
du -h --max-depth=1 / | sort -h
```

Очистить кэш пакетов (если используется apt)
```shell
apt clean
```

Очищаем диск от остановленных контейнеров
```shell
docker system prune -af
```

Журнал с размероболее 100М  
```shell
journalctl --vacuum-size=100M
```

/: Start search from root.
-xdev: Stay on the same filesystem (don't cross mount points).
-type f: Only look for files.
-size +100M: Find files larger than 100 megabytes.
-print0 | xargs -0 du -h | sort -rh: Pipes the output to du for human-readable sizes and sorts them by size.
```shell
sudo find / -xdev -type f -size +100M -print0 | xargs -0 du -h | sort -rh
```


Остановка контейнера
```shell
sudo docker stop container_name
```
Удаление контейнера
```shell
sudo docker rm container_name
```
Удаление тома с данными (если нужно полное удаление)
```shell
sudo docker volume ls
sudo docker volume rm <volume_name>
```
