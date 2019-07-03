# Тестовое задание "Организации и сотрудники"

# Как запустить:

$ git clone https://github.com/Zimins/myTestTask.git
$ cd myTestTask.git

# БД

Запустить скрипт \myTestTask.git\backend\src\main\resources\scriptDB.sql

/* САМ СКРИПТ */

create database orgstructdb
    with owner postgres;

create table organizations
(
    id        serial      not null
        constraint organizations_pk
            primary key,
    name      varchar(45) not null,
    id_parent integer
);

alter table organizations
    owner to postgres;

create unique index organizations_id_uindex
    on organizations (id);

create unique index organizations_name_uindex
    on organizations (name);

create table employees
(
    id              serial      not null
        constraint employees_pk
            primary key,
    name            varchar(45) not null,
    id_organization integer
        constraint employees_organizations_id_fk
            references organizations,
    id_director     integer
);

alter table employees
    owner to postgres;

create unique index employees_name_uindex
    on employees (name);

# Подключение к БД 

В файле \myTestTask.git\backend\src\main\resources\application.properties
Подставить свои настройки конфигурации:

spring.datasource.url=<Ваш url>
spring.datasource.username=<Ваш username>
spring.datasource.password=<Ваш password>

# Front-end

Поместить файл index.html (\myTestTask.git\frontend\index.html) в один каталог с каталогом dist (\myTestTask.git\frontend\dist) 
Или оставить как есть.

# Nginx

В файле nginx.conf в http прописать следующее

server {
      listen 80 default_server;
      server_name localhost;

      location / {
          root html;
          index <Путь к index.html>;
      }

      location ^~ /employees {
          proxy_pass http://127.0.0.1:8080;
          expires -1;
          proxy_set_header Host $host;
          proxy_set_header X-Real-IP $remote_addr;
      }

      location ^~ /organizations {
          proxy_pass http://127.0.0.1:8080;
          expires -1;
          proxy_set_header Host $host;
          proxy_set_header X-Real-IP $remote_addr;
      }
}

# Запустить сервер backend (Application)

# Запустить nginx

# Ввести в строку браузера адрес: http://localhost
