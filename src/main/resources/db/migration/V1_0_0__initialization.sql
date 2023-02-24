CREATE TABLE `conference` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,

    `name` VARCHAR(255) NOT NULL,
    `begin_date` DATE NOT NULL,
    `end_date` DATE NOT NULL,
    `website` VARCHAR(255) NOT NULL,
    `ticket` INT NOT NULL,
    `travel` INT NOT NULL,
    `accommodation` INT NOT NULL,

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

CREATE TABLE `employee` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,

    `first_name` VARCHAR(255) NOT NULL,
    `last_name` VARCHAR(255) NOT NULL,
    `email` VARCHAR(255) NOT NULL,
    `admin` BOOLEAN NOT NULL DEFAULT 0,
    `password_hash` VARCHAR(255) NULL,
    `password_change` BOOLEAN NOT NULL DEFAULT 0,
    `theme` ENUM('dark', 'light') NOT NULL DEFAULT 'light',

    PRIMARY KEY (`id`)
);

CREATE INDEX `employee_names` ON `employee` (`first_name`, `last_name`);
CREATE UNIQUE INDEX `employee_email` ON `employee` (`email`);

CREATE TABLE `registration` (
    `employee_id` BIGINT NOT NULL,
    `conference_id` BIGINT NOT NULL,
    `date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `role` ENUM('attendee', 'speaker', 'organizer') NOT NULL DEFAULT 'attendee',
    `reason` LONGTEXT NOT NULL,
    `status` ENUM('submitted', 'approved', 'declined', 'withdrawn') NOT NULL DEFAULT 'submitted',
    `status_date` DATETIME NULL,
    `status_comment` LONGTEXT NULL,

    PRIMARY KEY (`employee_id`, `conference_id`),
    FOREIGN KEY (`employee_id`) REFERENCES `employee`(`id`),
    FOREIGN KEY (`conference_id`) REFERENCES `conference`(`id`)
);

CREATE INDEX `registration_date` ON `registration` (`date`);
CREATE INDEX `registration_status` ON `registration` (`status`);
