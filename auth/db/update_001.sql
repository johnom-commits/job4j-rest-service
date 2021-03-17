create table person (
    id serial primary key not null,
    login varchar(25),
    password varchar(25)
);

insert into person (login, password) values ('parsentev', '123');
insert into person (login, password) values ('ban', '123');
insert into person (login, password) values ('ivan', '123');