# quaestio
quaestio

# Description / Описание
- Final project for the Java Developer training program from Skillbox.

The application creates an index based on the specified websites and saves the results in a database.
Using the saved data, the user can search for relevant information and navigate to a page with relevant information.

The formula for relevant information is:
```
lemma - lemma, the initial, dictionary form of a word.
lemma_runk - the frequency of a lemma's appearance on a page.
Rabs(page) - Absolute relevance
Rabs(page) = lemma_1_runk + ... + lemma_n_runk
Rabs(site) - Absolute maximum relevance among all pages on the site
Rrel(page) - Relative relevance
Rrel(page) = Rabs(page) / Rabs(site)
```

- Итоговый проект по программе обучения Java Разработчик от компании Skillbox.

Приложение по указанным сайтам формирует индекс и сохраняет результат в базу данных. 
По сохранённым данным пользователь может производить поиск релевантной информации, осуществлять переход на страницу с релевантной информацией. 

Формула релевантной информации:
```
lemma - лемма, начальная, словарная форма слова.
lemma_runk - частота появления леммы на странице.
Rabs(page) - Абсолютная релевантность
Rabs(page) = lemma_1_runk + ... + lemma_n_runk
Rabs(site) - Абсолютная максимальная релевантность среди всех страниц на сайте 
Rrel(page) - Относительная релевантность
Rrel(page) = Rabs(page) / Rabs(site)
```



# Technology stack / Используемый стек технологий

- <img src="https://img.shields.io/badge/java-25-green.svg"/>
- <img src="https://img.shields.io/badge/maven-4-green.svg"/>
- <img src="https://img.shields.io/badge/spring_boot-4.0.0-green.svg"/>
- <img src="https://img.shields.io/badge/spring_boot-starter_parent-blue.svg"/>
- <img src="https://img.shields.io/badge/spring_boot-starter_web-blue.svg"/>
- <img src="https://img.shields.io/badge/spring_boot-starter_thymeleaf-blue.svg"/>
- <img src="https://img.shields.io/badge/spring_boot-starter_data_jpa-blue.svg"/>
- <img src="https://img.shields.io/badge/spring_boot-starter_liquibase-blue.svg"/>
- <img src="https://img.shields.io/badge/lombok-1.18.42-green.svg"/>
- <img src="https://img.shields.io/badge/jsoup-1.21.2-green.svg"/>
- <img src="https://img.shields.io/badge/mysql_connector_java-8.0.33-green.svg"/>
- <img src="https://img.shields.io/badge/checkstyle-12.3.0-green.svg"/>

- <img src="https://img.shields.io/badge/morph-1.5-blue.svg"/>
- <img src="https://img.shields.io/badge/morphology-1.5-blue.svg"/>
- <img src="https://img.shields.io/badge/english-1.5-blue.svg"/>
- <img src="https://img.shields.io/badge/russian-1.5-blue.svg"/>


# Testing on / Испытания на ПО
- <img src="https://img.shields.io/badge/java-25-green.svg"/>
- <img src="https://img.shields.io/badge/MySQL_Community_Server-8.0.43-green.svg"/>

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

## Properties of aplication
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

# Usage / Использование
- Command to launch the application
```
java -jar SearchEngine.jar --spring.config.location=file:./application.yaml
```

- Команда для запуска приложения
```
java -jar SearchEngine.jar --spring.config.location=file:./application.yaml
```

# Information for developers / Информация для разработчиков
## Before work with code / Перед началом работы с кодом
- To work with the code, you will need:
- - A development environment that supports Java language level 25 (for example: IntelliJ IDEA 2025.2+)
- - Maven 4 build tool (apache-maven-4.0.0-rc-4)


- Для работы с кодом потребуется:
- - Среда разработки, поддерживающая 25 java language level (например: Intellij idea 2025.2+) 
- - Сборщик maven 4 (apache-maven-4.0.0-rc-4)

## Design App / Дизайн приложения

| layer        |             |              | \\/ |
|--------------|-------------|--------------|-----|
| controllers  |             |              | \\/ |
| services     |             |              | \\/ |
| componenets  | core.engine | core.utility | \\/ |
| repositories |             |              | \\/ |

## System Structure / Структура системы
Client (web browser) <-> Spring Boot App <-> Database

## Database Model / Модель базы данных
### site — информация о сайтах и статусах их индексации
- id INT NOT NULL AUTO_INCREMENT;
- status ENUM('INDEXING', 'INDEXED', 'FAILED') NOT NULL — текущий
статус полной индексации сайта, отражающий готовность поискового
движка осуществлять поиск по сайту — индексация или переиндексация
в процессе, сайт полностью проиндексирован (готов к поиску) либо его не
удалось проиндексировать (сайт не готов к поиску и не будет до
устранения ошибок и перезапуска индексации);
- status_time DATETIME NOT NULL — дата и время статуса (в случае
статуса INDEXING дата и время должны обновляться регулярно при
добавлении каждой новой страницы в индекс);
- last_error TEXT — текст ошибки индексации или NULL, если её не было;
- url VARCHAR(255) NOT NULL — адрес главной страницы сайта;
- name VARCHAR(255) NOT NULL — имя сайта.

### page — проиндексированные страницы сайта
- id INT NOT NULL AUTO_INCREMENT;
- site_id INT NOT NULL — ID веб-сайта из таблицы site;
- path TEXT NOT NULL — адрес страницы от корня сайта (должен
начинаться со слэша, например: /news/372189/);
- code INT NOT NULL — код HTTP-ответа, полученный при запросе
страницы (например, 200, 404, 500 или другие);
- content MEDIUMTEXT NOT NULL — контент страницы (HTML-код).
По полю path должен быть установлен индекс, чтобы поиск по нему был
быстрым, когда в нём будет много ссылок. Индексы рассмотрены в курсе «Язык
запросов SQL».

### lemma — леммы, встречающиеся в текстах (см. справочно:
лемматизация).
- id INT NOT NULL AUTO_INCREMENT;
- site_id INT NOT NULL — ID веб-сайта из таблицы site;
- lemma VARCHAR(255) NOT NULL — нормальная форма слова (лемма);
- frequency INT NOT NULL — количество страниц, на которых слово
встречается хотя бы один раз. Максимальное значение не может
превышать общее количество слов на сайте.

### index — поисковый индекс
- id INT NOT NULL AUTO_INCREMENT;
- page_id INT NOT NULL — идентификатор страницы;
- lemma_id INT NOT NULL — идентификатор леммы;
- rank FLOAT NOT NULL — количество данной леммы для данной
страницы.

## API
### Запуск полной индексации — GET /api/startIndexing
Метод запускает полную индексацию всех сайтов или полную
переиндексацию, если они уже проиндексированы.

Если в настоящий момент индексация или переиндексация уже
запущена, метод возвращает соответствующее сообщение об ошибке.

**Параметры**:

Метод без параметров

**Формат ответа в случае успеха**:
```
{
    'result': true
}
```
**Формат ответа в случае ошибки**:
```
{
    'result': false,
    'error': "Индексация уже запущена"
}
```

### Остановка текущей индексации — GET /api/stopIndexing

Метод останавливает текущий процесс индексации (переиндексации).

Если в настоящий момент индексация или переиндексация не происходит,
метод возвращает соответствующее сообщение об ошибке.

**Параметры**:

Метод без параметров.

**Формат ответа в случае успеха**:
```
{
    'result': true
}
```
**Формат ответа в случае ошибки**:
```
{
    'result': false,
    'error': "Индексация не запущена"
}
```

### Добавление или обновление отдельной страницы — POST /api/indexPage
Метод добавляет в индекс или обновляет отдельную страницу, адрес
которой передан в параметре.

Если адрес страницы передан неверно, метод должен вернуть
соответствующую ошибку.

**Параметры**:
- url — адрес страницы, которую нужно переиндексировать.

**Формат ответа в случае успеха**:
```
{
    'result': true
}
```
**Формат ответа в случае ошибки**:
```
{
    'result': false,
    'error': "Данная страница находится за пределами сайтов, указанных в конфигурационном файле"
}
```

### Статистика — GET /api/statistics
Метод возвращает статистику и другую служебную информацию о
состоянии поисковых индексов и самого движка.

Если ошибок индексации того или иного сайта нет, задавать ключ error не
нужно.

**Параметры**:

Метод без параметров.

**Формат ответа**:
```
{
    'result': true,
    'statistics': {
        "total": {
            "sites": 10,
            "pages": 436423,
            "lemmas": 5127891,
            "indexing": true
        },
        "detailed": [
            {
                "url": "http://www.site.com",
                "name": "Имя сайта",
                "status": "INDEXED",
                "statusTime": 1600160357,
                "error": "Ошибка индексации: главная
                страница сайта недоступна",
                "pages": 5764,
                "lemmas": 321115
            },
            ...
        ]
    }
}
```
### Получение данных по поисковому запросу — GET /api/search
Метод осуществляет поиск страниц по переданному поисковому запросу
(параметр query).

Чтобы выводить результаты порционно, также можно задать параметры
offset (сдвиг от начала списка результатов) и limit (количество результатов,
которое необходимо вывести).

В ответе выводится общее количество результатов (count), не зависящее
от значений параметров offset и limit, и массив data с результатами поиска.

Каждый результат — это объект, содержащий свойства результата поиска (см.
ниже структуру и описание каждого свойства).

Если поисковый запрос не задан или ещё нет готового индекса (сайт, по
которому ищем, или все сайты сразу не проиндексированы), метод должен
вернуть соответствующую ошибку (см. ниже пример). Тексты ошибок должны
быть понятными и отражать суть ошибок.

**Параметры**:
- query — поисковый запрос;
- site — сайт, по которому осуществлять поиск (если не задан, поиск
должен происходить по всем проиндексированным сайтам); задаётся в
формате адреса, например: http://www.site.com (без слэша в конце);
- offset — сдвиг от 0 для постраничного вывода (параметр
необязательный; если не установлен, то значение по умолчанию равно
нулю);
- limit — количество результатов, которое необходимо вывести (параметр
необязательный; если не установлен, то значение по умолчанию равно
20).

**Формат ответа в случае успеха**:
```
{
    'result': true,
    'count': 574,
    'data': [
        {
            "site": "http://www.site.com",
            "siteName": "Имя сайта",
            "uri": "/path/to/page/6784",
            "title": "Заголовок страницы,
            которую выводим",
            "snippet": "Фрагмент текста,
            в котором найдены
            совпадения, <b>выделенные
            жирным</b>, в формате HTML",
            "relevance": 0.93362
        },
        ...
    ]
}
```

**Формат ответа в случае ошибки**:
```
{
    'result': false,
    'error': "Задан пустой поисковый запрос"
}
```

### Ответы в случае ошибок
Во всех командах API необходимо реализовать корректные ответы в
случае возникновения ошибок. Любой метод API может возвращать ошибку,
если она произошла. В этом случае ответ должен выглядеть стандартным
образом:
```
{
    'result': false,
    'error': "Указанная страница не найдена"
}
```

Такие ответы должны сопровождаться соответствующими статус-кодами.
Желательно ограничиться использованием кодов 400, 401, 403, 404, 405 и 500
при возникновении соответствующих им типов ошибок.