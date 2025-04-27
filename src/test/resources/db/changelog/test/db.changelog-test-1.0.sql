--liquibase formatted sql

--changeset maxvdovin:1
create table if not exists Users
(
    id       int auto_increment primary key,
    login    varchar(255) not null unique,
    password varchar(255) not null
);

--changeset maxvdovin:2
create table if not exists Locations
(
    id        int auto_increment primary key,
    name      varchar(255)     not null,
    user_id   int,
    latitude  double precision not null,
    longitude double precision not null,
    foreign key (user_id) references Users(id) on delete cascade
);

--changeset maxvdovin:3
create table if not exists Sessions
(
    id         uuid default random_uuid() primary key,
    user_id    int,
    expires_at timestamp not null,
    foreign key (user_id) references Users(id) on delete cascade
);