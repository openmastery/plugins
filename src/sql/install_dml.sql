/**
 * This script is called from install.sh, after src/sql/install_ddl.sql
 *
 * This script contains the data defaults as they should exist at install.
 */
INSERT INTO `request_map` (`id`, `version`, `config_attribute`, `url`) VALUES(2, 0, 'IS_AUTHENTICATED_ANONYMOUSLY', '/login/**');
INSERT INTO `request_map` (`id`, `version`, `config_attribute`, `url`) VALUES(3, 0, 'IS_AUTHENTICATED_ANONYMOUSLY', '/logout/**');
INSERT INTO `request_map` (`id`, `version`, `config_attribute`, `url`) VALUES(4, 0, 'IS_AUTHENTICATED_ANONYMOUSLY', '/css/**');
INSERT INTO `request_map` (`id`, `version`, `config_attribute`, `url`) VALUES(5, 0, 'IS_AUTHENTICATED_ANONYMOUSLY', '/js/**');
INSERT INTO `request_map` (`id`, `version`, `config_attribute`, `url`) VALUES(6, 0, 'IS_AUTHENTICATED_ANONYMOUSLY', '/images/**');
INSERT INTO `request_map` (`id`, `version`, `config_attribute`, `url`) VALUES(7, 0, 'IS_AUTHENTICATED_ANONYMOUSLY', '/plugins/**');
INSERT INTO `request_map` (`id`, `version`, `config_attribute`, `url`) VALUES(8, 0, 'IS_AUTHENTICATED_REMEMBERED', '/');
INSERT INTO `request_map` (`id`, `version`, `config_attribute`, `url`) VALUES(9, 0, 'ROLE_ADMIN', '/requestMap/**');

INSERT INTO `role` (`id`, `version`, `authority`) VALUES(1, 0, 'ROLE_ADMIN');

INSERT INTO `user` (`id`, `version`, `account_expired`, `account_locked`, `enabled`, `password`, `password_expired`, `username`) VALUES(1, 0, b'0', b'0', b'1', '$2a$10$wEixnjn2VL/dpPvt9d3IoumSomS18vVXOTbjIH7MfamxskFuNUvGe', b'0', 'newiron');

INSERT INTO `user_role` (`role_id`, `user_id`) VALUES(1, 1);