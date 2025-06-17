--liquibase formatted sql

--changeset maxvdovin:1
create extension if not exists "uuid-ossp";

--changeset maxvdovin:2
create table if not exists Users
(
    id       serial primary key,
    login    varchar(255) not null,
    password varchar(255) not null,
    constraint uk_users_login unique (login)
);

--changeset maxvdovin:3
create table if not exists Locations
(
    id        serial primary key,
    name      varchar(255)     not null,
    user_id   int references Users (id),
    latitude  double precision not null,
    longitude double precision not null
);

--changeset maxvdovin:4
create table if not exists Sessions
(
    id         uuid primary key default uuid_generate_v4(),
    user_id    int references Users (id),
    expires_at timestamp not null
);