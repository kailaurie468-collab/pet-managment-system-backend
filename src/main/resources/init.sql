-- 创建数据库
CREATE DATABASE IF NOT EXISTS pet_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE pet_management;

-- 创建用户表
CREATE TABLE IF NOT EXISTS user
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    name     VARCHAR(50)  NOT NULL,
    phone    VARCHAR(20),
    role     VARCHAR(20)  NOT NULL
);

-- 创建宠物表
CREATE TABLE IF NOT EXISTS pet
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50) NOT NULL,
    type        VARCHAR(50) NOT NULL,
    age         INT,
    gender      VARCHAR(10),
    breed       VARCHAR(50),
    owner_id    BIGINT,
    description TEXT
);

-- 创建健康记录表
CREATE TABLE IF NOT EXISTS health_record
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    pet_id      BIGINT      NOT NULL,
    type        VARCHAR(20) NOT NULL,
    content     TEXT        NOT NULL,
    record_date DATETIME    NOT NULL,
    doctor      VARCHAR(50),
    notes       TEXT
);

-- 创建服务表
CREATE TABLE IF NOT EXISTS service
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50) NOT NULL,
    description TEXT,
    price       DOUBLE      NOT NULL,
    category    VARCHAR(50)
);

-- 创建服务记录表
CREATE TABLE IF NOT EXISTS service_record
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    pet_id       BIGINT   NOT NULL,
    service_id   BIGINT   NOT NULL,
    service_date DATETIME NOT NULL,
    amount       DOUBLE   NOT NULL,
    notes        TEXT
);

create table if not exists pet_management.doctor
(
    id               bigint auto_increment
        primary key,
    name             varchar(100) not null,
    experience_years int          null,
    skills           varchar(255) null,
    introduction     text         null
);

insert into user (username, password, name, phone, role)
VALUES ('admin', '$2a$10$NL8.qSHXkL7s3YOiVvGLeuHlSrcUBY0WsQ1JhpvraiDb5m4F.iiOi', '管理员', '12345678901', 'ADMIN');

-- 插入初始服务数据
INSERT INTO service (name, description, price, category)
VALUES ('洗澡', '宠物洗澡服务', 50.0, '清洁'),
       ('美容', '宠物美容服务', 100.0, '美容'),
       ('体检', '宠物健康体检', 200.0, '医疗'),
       ('疫苗', '宠物疫苗接种', 80.0, '医疗');

INSERT INTO doctor (name, experience_years, skills, introduction)
VALUES ('张三', 10, '擅长宠物外科手术', '张三是一位经验丰富的兽医，擅长处理各种宠物外科手术。'),
       ('李四', 8, '擅长宠物内科疾病', '李四是一位经验丰富的兽医，擅长处理各种宠物内科疾病。'),
       ('王五', 12, '擅长宠物疫苗接种', '王五是一位经验丰富的兽医，擅长处理各种宠物疫苗接种。'),
       ('赵六', 6, '擅长宠物外科手术', '赵六是一位经验丰富的兽医，擅长处理各种宠物外科手术。')
;