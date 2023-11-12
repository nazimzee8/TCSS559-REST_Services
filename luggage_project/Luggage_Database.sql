DROP DATABASE IF EXISTS `project`;
CREATE DATABASE IF NOT EXISTS `project`;
USE `project`;

CREATE TABLE USERS (
    UserID INTEGER NOT NULL,
    Username Varchar(32) NOT NULL,
    Password Varchar(32) NOT NULL,
    PRIMARY KEY (UserID)
);

CREATE TABLE PASSENGERS (
    UserID INTEGER NOT NULL,
    FlightNum Varchar(32) NOT NULL,
    PRIMARY KEY (UserID, FlightNum)
);

CREATE TABLE FLIGHTS (
    FlightNum Varchar(32) NOT NULL,
    DepartingAirport Varchar(32) NOT NULL,
    ArrivingAirport Varchar(32) NOT NULL,
    FlightStatus Varchar(32) NOT NULL,
    PRIMARY KEY (FlightNum)
);

CREATE TABLE LUGGAGE (
    UserID INTEGER NOT NULL,
    RFID Varchar(32) NOT NULL, 
    Location Varchar(32) NOT NULL,
    FlightNum Varchar(32) NOT NULL,
    PRIMARY KEY (RFID)
);

CREATE INDEX user_index 
ON PASSENGER (Username, Password);

CREATE INDEX luggage_index
ON LUGGAGE (RFID)