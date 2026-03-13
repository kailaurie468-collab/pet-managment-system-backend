-- 创建数据库
CREATE DATABASE IF NOT EXISTS pet_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE pet_management;

-- 创建用户表
CREATE TABLE IF NOT EXISTS user
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(100) NOT NULL,
    name       VARCHAR(50)  NOT NULL,
    phone      VARCHAR(20),
    address    VARCHAR(255),
    ident_card VARCHAR(20),
    role       VARCHAR(20)  NOT NULL
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
    description TEXT,
    photo       LONGTEXT,
    is_neutered TINYINT DEFAULT 0
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

-- 创建疫苗记录表
CREATE TABLE IF NOT EXISTS vaccine_record
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    pet_id        BIGINT      NOT NULL,
    vaccine_name  VARCHAR(100) NOT NULL,
    vaccine_date  DATETIME    NOT NULL,
    next_due_date DATETIME,
    notes         TEXT
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
    service_id   VARCHAR(255)   NOT NULL,
    doctor_id    BIGINT,
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

-- 1. 插入用户数据 (密码均为 123456)
insert into user (username, password, name, phone, address, ident_card, role)
VALUES ('admin', '$2a$10$NL8.qSHXkL7s3YOiVvGLeuHlSrcUBY0WsQ1JhpvraiDb5m4F.iiOi', '系统管理员', '13800000001', '上海市黄浦区中山东一路', '110101199001011234', 'ADMIN'),
       ('user1', '$2a$10$NL8.qSHXkL7s3YOiVvGLeuHlSrcUBY0WsQ1JhpvraiDb5m4F.iiOi', '张伟', '13911112222', '北京市朝阳区三里屯', '110105198805201111', 'OWNER'),
       ('user2', '$2a$10$NL8.qSHXkL7s3YOiVvGLeuHlSrcUBY0WsQ1JhpvraiDb5m4F.iiOi', '李华', '13733334444', '广州市天河区珠江新城', '440106199203152222', 'OWNER');

-- 2. 插入服务数据
INSERT INTO service (name, description, price, category)
VALUES ('洗澡', '宠物洁净护理，包含香波洗涤与吹干', 50.0, '清洁'),
       ('美容', '宠物毛发修剪与造型设计', 120.0, '美容'),
       ('全面体检', '包含血常规、内科检查等全方位健康评估', 300.0, '医疗'),
       ('狂犬疫苗', '每年一次预防接种', 80.0, '医疗'),
       ('体外驱虫', '预防跳蚤、蜱虫侵扰', 60.0, '护理');

-- 3. 插入医生数据
INSERT INTO doctor (name, experience_years, skills, introduction)
VALUES ('赵医生', 12, '外科手术、骨科康复', '赵医生是首席外科顾问，拥有超1000例成功手术案例。'),
       ('孙医生', 8, '内科诊断、皮肤病治疗', '孙医生擅长解决宠物过敏及各类顽固皮肤问题。'),
       ('周医生', 5, '疫苗接种、微量元素补充', '周医生耐心细致，深受小动物们的喜爱。');

-- 4. 插入宠物数据
INSERT INTO pet (name, type, age, gender, breed, owner_id, description, is_neutered)
VALUES ('旺财', '狗', 3, '公', '金毛犬', 2, '性格温顺，喜欢玩球', 1),
       ('咪咪', '猫', 2, '母', '英国短毛猫', 2, '有点高冷，喜欢晒太阳', 1),
       ('二哈', '狗', 1, '公', '哈士奇', 3, '精力充沛，偶尔拆家', 0);

-- 5. 插入健康记录
INSERT INTO health_record (pet_id, type, content, record_date, doctor, notes)
VALUES (1, '医疗', '伤口缝合术后复查，恢复良好', NOW(), '赵医生', '无需换药'),
       (2, '清洁', '耳朵深度清洁及剪指甲', NOW(), '周医生', '表现乖巧');

-- 6. 插入疫苗记录
INSERT INTO vaccine_record (pet_id, vaccine_name, vaccine_date, next_due_date, notes)
VALUES (1, '狂犬疫苗', '2025-01-10 10:00:00', '2026-01-10 10:00:00', '接种良好，无不良反应'),
       (3, '五联疫苗', '2025-02-15 14:30:00', '2025-05-15 14:30:00', '按期完成首针');

-- 7. 插入服务记录 (service_id 关联 service 表)
INSERT INTO service_record (pet_id, service_id, service_date, amount, notes)
VALUES (1, 1, NOW(), 50.0, '加急洗澡服务'),
       (3, 2, NOW(), 120.0, '赛级造型修剪');

ALTER TABLE pet MODIFY COLUMN photo LONGTEXT;
ALTER TABLE service_record MODIFY COLUMN service_id VARCHAR(255);
ALTER TABLE service_record ADD COLUMN doctor_id BIGINT AFTER service_id;