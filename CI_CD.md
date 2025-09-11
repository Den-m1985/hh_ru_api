### Настройка окружения для CI/CD
Создать новый ключ БЕЗ пароля, специально для CI/CD
Это рекомендуемый способ, если ты хочешь, чтобы GitHub Actions мог автоматически подключаться к серверу:
```shell
ssh-keygen -t ed25519 -f deploy_key -C "github-actions" -N ""
```

Копируем содержимое deploy_key.pub (с локальной машины) на сервер в ~/.ssh/authorized_keys:
```shell
ssh -i deploy_key root@<ip>
```

Проверка (на локальной машине) что ты заходишь без пароля на сервер:
```shell
ssh -i deploy_key root@<ip>
```

Работа с GitHub:
Set Up Secret Variables in GitHub
Follow these steps:
Go to your GitHub repository.
Click on the Settings tab.
In the left sidebar, navigate to Secrets and variables → Actions.
Click New repository secret.
Enter a secret name (DEPLOY_SSH_KEY).
Paste the secret value (deploy_key) in the field.
Click Add secret.
