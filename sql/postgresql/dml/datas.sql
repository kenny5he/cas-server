/**
 * 初始化注册表单数据 - email
 */
INSERT INTO login.cas_property_t
(id, "name", "label", title, pattern, category, "type", required, disabled, css_class, valid_message, tenant_id, creation_date, created_by, last_update_date, last_update_by, "order")
VALUES(1, 'username', 'cas.screen.acct.label.username', 'cas.screen.acct.title.username', '', 'email', '', 1, 0, '', '', 0, CURRENT_DATE, '', CURRENT_DATE, '', 10);
INSERT INTO login.cas_property_t
(id, "name", "label", title, pattern, category, "type", required, disabled, css_class, valid_message, tenant_id, creation_date, created_by, last_update_date, last_update_by, "order")
VALUES(2, 'firstName', 'cas.screen.acct.label.firstName', 'cas.screen.acct.title.firstName', '', 'email', '', 1, 0, '', '', 0, CURRENT_DATE, '', CURRENT_DATE, '', 20);
INSERT INTO login.cas_property_t
(id, "name", "label", title, pattern, category, "type", required, disabled, css_class, valid_message, tenant_id, creation_date, created_by, last_update_date, last_update_by, "order")
VALUES(3, 'lastName', 'cas.screen.acct.label.lastName', 'cas.screen.acct.title.lastName', '', 'email', '', 1, 0, '', '', 0, CURRENT_DATE, '', CURRENT_DATE, '', 30);
INSERT INTO login.cas_property_t
(id, "name", "label", title, pattern, category, "type", required, disabled, css_class, valid_message, tenant_id, creation_date, created_by, last_update_date, last_update_by, "order")
VALUES(4, 'email', 'cas.screen.acct.label.email', 'cas.screen.acct.title.email', '', 'email', 'email', 1, 0, '', '', 0, CURRENT_DATE, '', CURRENT_DATE, '', 40);
INSERT INTO login.cas_property_t
(id, "name", "label", title, pattern, category, "type", required, disabled, css_class, valid_message, tenant_id, creation_date, created_by, last_update_date, last_update_by, "order")
VALUES(5, 'country', 'cas.screen.acct.label.country', 'cas.screen.acct.title.country', '', 'email', 'select', 1, 0, '', '', 0, CURRENT_DATE, '', CURRENT_DATE, '', 50);

/**
 * 初始化注册表单数据 - sms
 */
INSERT INTO login.cas_property_t
(id, "name", "label", title, pattern, category, "type", required, disabled, css_class, valid_message, tenant_id, creation_date, created_by, last_update_date, last_update_by, "order")
VALUES(6, 'username', 'cas.screen.acct.label.username', 'cas.screen.acct.title.username', '', 'sms', '', 1, 0, '', '', 0, CURRENT_DATE, '', CURRENT_DATE, '', 10);
INSERT INTO login.cas_property_t
(id, "name", "label", title, pattern, category, "type", required, disabled, css_class, valid_message, tenant_id, creation_date, created_by, last_update_date, last_update_by, "order")
VALUES(7, 'firstName', 'cas.screen.acct.label.firstName', 'cas.screen.acct.title.firstName', '', 'sms', '', 1, 0, '', '', 0, CURRENT_DATE, '', CURRENT_DATE, '', 20);
INSERT INTO login.cas_property_t
(id, "name", "label", title, pattern, category, "type", required, disabled, css_class, valid_message, tenant_id, creation_date, created_by, last_update_date, last_update_by, "order")
VALUES(8, 'lastName', 'cas.screen.acct.label.lastName', 'cas.screen.acct.title.lastName', '', 'sms', '', 1, 0, '', '', 0, CURRENT_DATE, '', CURRENT_DATE, '', 30);
INSERT INTO login.cas_property_t
(id, "name", "label", title, pattern, category, "type", required, disabled, css_class, valid_message, tenant_id, creation_date, created_by, last_update_date, last_update_by, "order")
VALUES(9, 'phoneNumber', 'cas.screen.acct.label.phoneNumber', 'cas.screen.acct.title.phoneNumber', '', 'sms', '', 1, 0, '', '', 0, CURRENT_DATE, '', CURRENT_DATE, '', 40);
INSERT INTO login.cas_property_t
(id, "name", "label", title, pattern, category, "type", required, disabled, css_class, valid_message, tenant_id, creation_date, created_by, last_update_date, last_update_by, "order")
VALUES(10, 'country', 'cas.screen.acct.label.country', 'cas.screen.acct.title.country', '', 'sms', 'select', 1, 0, '', '', 0, CURRENT_DATE, '', CURRENT_DATE, '', 50);

/**
 * 初始化选项数据 - country
 */
INSERT INTO login.cas_property_value_t
(id, property_id, code, value, enabled, creation_date, created_by, last_update_date, last_update_by)
VALUES(1, 5, 'CN', 'cs.data.country.china', 1, CURRENT_DATE, '', CURRENT_DATE, '');

INSERT INTO login.cas_property_value_t
(id, property_id, code, value, enabled, creation_date, created_by, last_update_date, last_update_by)
VALUES(2, 5, 'UK', 'cs.data.country.unitedkingdom', 1, CURRENT_DATE, '', CURRENT_DATE, '');

INSERT INTO login.cas_property_value_t
(id, property_id, code, value, enabled, creation_date, created_by, last_update_date, last_update_by)
VALUES(3, 5, 'CA', 'cs.data.country.canada', 1, CURRENT_DATE, '', CURRENT_DATE, '');