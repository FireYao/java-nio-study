
-- 目的地公司
INSERT INTO "corp"."company" ("id", "name", "short_name" ,"code", "agent_name", "agent_mobile", "version") VALUES ('40000', '贵州简途', '贵州','GZ', '王亚博', '13922992736', '0');

-- 渠道
INSERT INTO "corp"."channel" ("id", "company_id", "channel_template_id", "name", "commission_rate", "version") VALUES ('7', '40000', '1', '线上渠道', '0', '0');
INSERT INTO "corp"."channel" ("id", "company_id", "channel_template_id", "name", "commission_rate", "version") VALUES ('8', '40000', '2', '线下渠道', '0', '0');

-- 应用
INSERT INTO plt.app (id,company_id,company_name,depot_id, name, app_key, app_secret, confirm_timeout) VALUES (6, 40000,'贵州简途',2, '贵州线下分销应用', '67190b918', '703c5a8bfb1b41fab582caccfed7e13b', 0);

-- 运营线路
INSERT INTO "corp"."line_group" ("id", "code", name, "company_id") VALUES ('43', '02012', '重庆-欢乐谷', '20000');
INSERT INTO "corp"."line_group" ("id", "code", name, "company_id") VALUES ('44', '01025', '四川-鹧鸪山', '10000');
INSERT INTO "corp"."line_group" ("id", "code", name, "company_id") VALUES ('45', '03008', '云南-拉市海', '30000');
INSERT INTO "corp"."line_group" ("id", "code", name, "company_id") VALUES ('46', '01026', '四川-嘉阳线', '10000');
INSERT INTO "corp"."line_group" ("id", "code", name, "company_id") VALUES ('47', '01027', '四川-明月村线', '10000');
INSERT INTO "corp"."line_group" ("id", "code", name, "company_id") VALUES ('48', '04001', '贵州-黄果树线', '40000');
INSERT INTO "corp"."line_group" ("id", "code", name, "company_id") VALUES ('49', '04002', '贵州-织金洞线', '40000');
INSERT INTO "corp"."line_group" ("id", "code", name, "company_id") VALUES ('50', '04003', '贵州-西江荔波线', '40000');
INSERT INTO "corp"."line_group" ("id", "code", name, "company_id") VALUES ('51', '03009', '云南-昆明线', '30000');
INSERT INTO "corp"."line_group" ("id", "code", name, "company_id") VALUES ('52', '02013', '重庆-赤水线', '20000');
INSERT INTO "corp"."line_group" ("id", "code", name, "company_id") VALUES ('53', '04004', '贵州-定制线', '40000');
INSERT INTO "corp"."line_group" ("id", "code", name, "company_id") VALUES ('54', '04005', '贵州-荔波线', '40000');
INSERT INTO "corp"."line_group" ("id", "code", name, "company_id") VALUES ('55', '04006', '贵州-贵阳线', '40000');
INSERT INTO "corp"."line_group" ("id", "code", name, "company_id") VALUES ('56', '04007', '贵州-天青文线', '40000');

-- 资源所属主体
INSERT INTO "corp"."resource_owner" ("id", "code", "name", "company_id") VALUES ('8', '08', '贵州简途', '40000');
