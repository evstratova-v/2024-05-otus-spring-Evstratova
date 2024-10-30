## Команды для запуска приложения в локальном minikube

### Запускаем minikube

```shell
minikube start
```

### Получаем инструкцию для настройки использования docker внутри minikube

```shell
minikube docker-env
```

### Выполняем полученную команду (может отличаться в зависимости от терминала)

```shell
minikube -p minikube docker-env --shell powershell | Invoke-Expression
```

### Создаём image нашего приложения

```shell
docker build -t hw16-app:v1 .
```

### Скачиваем image postgres

```shell
docker pull postgres:13
```

### (Опционально) Проверяем список image на наличие hw16-app:v1 и postgres:13

```shell
minikube image ls --format table
```

### Включаем ingress

```shell
minikube addons enable ingress
```

### (В отдельной вкладке) Выполняем команду, чтобы ingress ресурсы были доступны по адресу http://localhost

```shell
minikube tunnel
```

### Создаём ресурсы из папки kube, после этой команды приложение доступно по адресу http://localhost

```shell
kubectl apply -f kube/.
```

### (Опционально, в отдельной вкладке) Открываем веб-интерфейс minikube

```shell
minikube dashboard
```

### Останавливаем minikube

```shell
minikube stop
```

### Удаляем локальный кластер

```shell
minikube delete --all
```
