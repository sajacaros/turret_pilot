-- INSERT INTO account_role (role_id, role_name) values (null, 'ROLE_THIRD');
INSERT INTO account_role (role_id, role_name) values (null, 'ROLE_ADMIN');
INSERT INTO account_role (role_id, role_name) values (null, 'ROLE_THIRD');


INSERT INTO account (account_id, account_non_expired, account_non_locked, credentials_non_expired, enabled, password, username, name, created_date, updated_date)
    VALUES (1,1,1,1,1,'$2a$10$rsBgVfahJyLO2KoMXleEzeieCYWZPnTiTVezdfU/cGCOlvgwm/qq6','master','master', '2021-05-26 13:22:23', '2021-05-26 13:22:23');

insert into user_role (user_id, role_id) values (1, 1);