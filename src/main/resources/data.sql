delete from USERS;
delete from USER_ROLES;
delete from RESTAURANT;
delete from DISH;
delete from VOTE;

INSERT INTO USERS (name, email, password)
VALUES ('User', 'user@yandex.ru', '{noop}password'),
       ('Admin', 'admin@gmail.com', '{noop}admin'),
       ('User2', 'user2@yandex.ru', '{noop}328328');

INSERT INTO USER_ROLES (role, user_id)
VALUES ('USER', 1),
       ('ADMIN', 2),
       ('USER', 2),
       ('USER', 3);

insert into RESTAURANT (name)
values ('Duo Gastrobar'),
       ('tartarbar'),
       ('Duo Asia'),
       ('Мастер Кебаб');

insert into DISH (restaurant_id, title, cost)
values (1, 'Тартар из говядины с хумусом и пармезаном', 490),
       (1, 'Паста орзо с шеей бычка', 490),
       (1, 'Говядина Топ-блейд с трюфельным пюре', 590),
       (1, 'Чернослив с соленой карамелью и орехом пекан', 390),
       (2, 'Тартар из говядины со шпинатом и трюфельным понзу', 490),
       (2, 'Тартар из говядины с пармезаном', 550),
       (2, 'Тартар из оленины с соусом из оливок и халапеньо', 550),
       (2, 'Севиче из тунца', 590),
       (2, 'Татаки из говядины с фуа-гра', 850),
       (3, 'Сашими из лосося с трюфельным соусом и свежим хреном', 590),
       (3, 'Сашими из тунца с васаби', 550),
       (3, 'Татаки из говядины с кимчи', 550),
       (3, 'Гёдза с грибами и трюфельным соусом', 490),
       (3, 'Шея бычка в устричном соусе', 750);

insert into VOTE (user_id, voting_date, restaurant_id)
values (1, current_date-3, 1),
       (2, current_date-3, 2),
       (1, current_date-2, 3),
       (2, current_date-2, 3),
       (1, current_date-1, 4),
       (2, current_date-1, 1),
       (1, current_date,   2),
       (2, current_date,   2);
