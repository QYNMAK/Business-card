-- H2 本地开发用建表脚本（MySQL 兼容模式）
CREATE SCHEMA IF NOT EXISTS crm;
SET SCHEMA crm;

CREATE TABLE IF NOT EXISTS crm.tb_user (
    id          VARCHAR(32)  PRIMARY KEY,
    phone       VARCHAR(120) DEFAULT NULL,
    username    VARCHAR(50)  DEFAULT NULL,
    password    VARCHAR(50)  DEFAULT NULL,
    status      INT          DEFAULT 1,
    head_pic    VARCHAR(150) DEFAULT NULL,
    created     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    union_id    VARCHAR(30)  DEFAULT NULL,
    sex         INT          DEFAULT 0,
    is_delete   INT          DEFAULT 0,
    county      VARCHAR(20)  DEFAULT NULL,
    city        VARCHAR(20)  DEFAULT NULL,
    province    VARCHAR(20)  DEFAULT NULL,
    address     VARCHAR(100) DEFAULT NULL,
    email       VARCHAR(120) DEFAULT NULL,
    wechat_id   VARCHAR(64)  DEFAULT NULL,
    description VARCHAR(200) DEFAULT NULL,
    greeting    VARCHAR(200) DEFAULT NULL,
    biz_type    VARCHAR(50)  DEFAULT NULL,
    biz_name    VARCHAR(50)  DEFAULT NULL,
    msg_link    VARCHAR(500) DEFAULT NULL,
    qr          VARCHAR(500) DEFAULT NULL,
    platform    VARCHAR(50)  DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS crm.tb_audit_log (
    id             VARCHAR(32) PRIMARY KEY,
    action         VARCHAR(30) NOT NULL,
    target_user_id VARCHAR(32)  DEFAULT NULL,
    operator       VARCHAR(50)  DEFAULT NULL,
    before_json    CLOB         DEFAULT NULL,
    after_json     CLOB         DEFAULT NULL,
    remark         VARCHAR(200) DEFAULT NULL,
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
