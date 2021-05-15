DROP TABLE IF EXISTS GAMES, USERS;


CREATE TABLE USERS (
                       ID INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
                       NAME VARCHAR(255) NOT NULL,
                       PASSWORD VARCHAR(255) NOT NULL,
                       GAME VARCHAR(255),
                       /*FOREIGN KEY (GAME) REFERENCES GAMES(NAME),*/
                       CONSTRAINT NAME_IS_UNIQUE UNIQUE KEY NAME);


CREATE TABLE GAMES (
                       ID INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
                       NAME VARCHAR(255) NOT NULL,
                       HOST VARCHAR(255),
                       PASSWORD VARCHAR(255),
                       GAMESTATE VARCHAR(255),
                       NUMPLAYERS INT NOT NULL,
                       CONSTRAINT GAME_NAME_IS_UNIQUE UNIQUE NAME,
                       PRIMARY KEY (ID),
                       FOREIGN KEY (HOST) REFERENCES USERS(NAME));