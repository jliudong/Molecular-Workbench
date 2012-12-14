use mysql;
insert user (host,user,password,ssl_cipher,x509_issuer,x509_subject) values ('localhost','qxie',password('123456'),'','','');
insert user (host,user,password,ssl_cipher,x509_issuer,x509_subject) values ('%','qxie',password('123456'),'','','');
flush privileges;

create database molo_qxie;

GRANT ALL PRIVILEGES ON molo_qxie.* TO qxie@localhost IDENTIFIED BY '123456' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON molo_qxie.* TO qxie@'%' IDENTIFIED BY '123456' WITH GRANT OPTION;
flush privileges;

use molo_qxie;


CREATE TABLE Activity (
    id              int             not null,    --自增
    title           varchar(1000)   null,
    instruction     varchar(1000)   null,
    url             varchar(1000)   null,
    userID          varchar(1000)   null,
    clazzID         varchar(1000)   null,  -- 不确定该字段是否有用
    classID         varchar(1000)   null,   
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE author (
    id              int             not null auto_increment,
    filename        varchar(50)     not null,
    zipfile         longblob        null,
    title           varchar(255)    null,
    level           varchar(20)     null,
    type            varchar(20)     null,
    name            varchar(50)     null,
    email           varchar(50)     null,
    school          varchar(100)    null,
    password        varchar(20)     null,
    teacher         varchar(50)     null,
    ip              varchar(100)    not null,
    server_time     timestamp       not null,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE clazz (
    clazzID         varchar(250)    not null,
    teacherID       varchar(250)    null,
    archived        tinyint         null,
    PRIMARY KEY (clazzID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE comment (
    id              int             not null auto_increment,
    url             varchar(1000)   null,
    title           varchar(1000)   null,
    body            longblob        null,
    ip              varchar(1000)   null,
    timeMillis      bigint          not null,
    userID          varchar(1000)   null,
    User_comments   varchar(255)    null,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE LaunchRecord (
    id              int             not null auto_increment,
    userName        varchar(1000)   null,
    clientHost      varchar(1000)   null,
    ipAddress       varchar(4000)   null,
    os              varchar(1000)   null,
    javaVersion     varchar(1000)   null,
    mwVersion       varchar(1000)   null,
    jwsLaunch       varchar(1000)   null,
    timeMillis      bigint          not null,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE model (
    id              int             not null auto_increment,
    zipFile         varchar(1000)   null,
    zipSize         int             null,
    title           varchar(1000)   null,
    timeMillis      bigint          null,
    url             varchar(1000)   null,
    userID          varchar(1000)   null,
    teacher         varchar(1000)   null,
    klass           varchar(1000)   null,
    deleted         int             null,
    privacy         varchar(1000)   null,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE Report (
    id              int             not null auto_increment,
    url             varchar(1000)   null,
    title           varchar(1000)   null,
    timeMillis      bigint          null,
    userID          varchar(1000)   null,
    teacher         varchar(1000)   null,
    deleted         int             null,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE User (
    userID          varchar(255)    not null,
    password        varchar(1000)   null,
    email           varchar(1000)   null,
    memberSince     bigint          not null,
    firstName       varchar(1000)   null,
    lastName        varchar(1000)   null,
    institution     varchar(1000)   null,
    state           varchar(1000)   null,
    country         varchar(1000)   null,
    type            int             null,
    teacher         varchar(1000)   null,
    klass           varchar(1000)   null,
    notifyModel     int             null,
    notifyReport    int             null,
    acceptNewsletter    int         null,
    Class_students  varchar(250)    null,
    PRIMARY KEY (userID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;