CREATE TABLE `conference` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,

    `name` VARCHAR(255) NOT NULL,
    `begin_date` DATE NOT NULL,
    `end_date` DATE NOT NULL,
    `website` VARCHAR(255) NOT NULL DEFAULT '',

    PRIMARY KEY (`id`)
);

CREATE INDEX `conference_names` ON `conference` (`name`);
CREATE INDEX `conference_dates` ON `conference` (`begin_date`, `end_date`);

CREATE TABLE `configuration` (
    `key` VARCHAR(255) NOT NULL,
    `value` MEDIUMTEXT NOT NULL DEFAULT '',

    PRIMARY KEY (`key`)
);

CREATE TABLE `mail_template` (
    `id` VARCHAR(255) NOT NULL,
    `subject` VARCHAR(255) NOT NULL,
    `content_text` LONGTEXT NOT NULL,
    `content_html` LONGTEXT NOT NULL,

    PRIMARY KEY (`id`)
);

CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,

    `first_name` VARCHAR(255) NOT NULL,
    `last_name` VARCHAR(255) NOT NULL,
    `email` VARCHAR(255) NOT NULL,
    `admin` BOOLEAN NOT NULL DEFAULT 0,
    `password_hash` VARCHAR(255) NULL,
    `password_change` BOOLEAN NOT NULL DEFAULT 0,

    PRIMARY KEY (`id`)
);

CREATE INDEX `user_names` ON `user` (`first_name`, `last_name`);
CREATE UNIQUE INDEX `user_email` ON `user` (`email`);
