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