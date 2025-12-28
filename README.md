# quaestio
quaestio

# Description / Описание
- Final project for the Java Developer training program from Skillbox.
- Итоговый проект по программе обучения Java Разработчик от компании Skillbox.

# Testing on / Испытания на ПО
- Java 25
- MySQL 8.0.43 Community Server

# Prepare / Подготовка
## MySQL
- Install MySQL 8.0.43
- Установить MySQL 8.0.43

- Execute sql script for create database:
- Выполнить sql script для создания базы данных:
```sql
    CREATE DATABASE
    search_engine
    CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_unicode_ci;
```
- Yes, need utf8mb4.
- Да, необходима именно кодировка utf8mb4 (для хранения специфичных символов, типа имодзи)

## Java
- Install Java Oracle 25
- Установить Java Oracle 25

## Properties
1.
- Set username and password for connection to MySQL
- Выставить параметры username и password для соединения с MySQL
```yaml
spring:
  datasource:
    username: user
    password: pass
```

2.
- Set params for sites
- Выставить параметры для переменной sites
```yaml
indexing-settings:
  sites:
  - url: https://www.lenta.ru/
    name: Лента.ру
  - url: https://www.skillbox.ru/
    name: Skillbox
```

3.
- Set params for http client
- Выставить параметры для http client при скачивании контента с сайтов
```yaml
jsoup-settings:
  user-agent: SearchBot
  #user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36
  referrer: http://www.google.com
```

4.
- Set local port for http handler of app
- Выставить параметры для http порта, который будет слушать приложение
```yaml
server:
  port: 8080
```