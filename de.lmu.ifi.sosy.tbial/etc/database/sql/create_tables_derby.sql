drop table games;
drop table users;

create table users (
	id int generated always as identity,
	name varchar(255) unique not null,
	password varchar(255) not null,
	game varchar(255) /*references games(name)*/);

create table games (
                       id int generated always as identity,
                       name varchar(255) unique not null,
                       host varchar(255) references users(name),
                       password varchar(255),
                       gamestate varchar(255),
                       numplayers int not null);