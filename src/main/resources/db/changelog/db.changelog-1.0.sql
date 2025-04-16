--liquibase formatted sql

--changeset maxvdovin:1
create schema if not exists weather;

--changeset maxvdovin:2
create table if not exists Users
(
    id       serial primary key,
    login    varchar(255) not null unique,
    password varchar(255) not null
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
    id         uuid primary key,
    user_id    int references Users (id),
    expires_at timestamp not null
);