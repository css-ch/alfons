-- [jooq ignore start]

INSERT INTO employee (id, first_name, last_name, email, admin, password_hash, password_change, theme) VALUES (1, 'John', 'Doe', 'john.doe@localhost', 1, '$2a$10$st.FfS5vLj5chMAdpRWcnuAQDWFjlXT/zUvjh/qomR4Uh3MoQKvHi', 0, 'light');
INSERT INTO employee (id, first_name, last_name, email, admin, password_hash, password_change, theme) VALUES (2, 'Jane', 'Doe', 'jane.doe@localhost', 0, '$2a$10$gM1gF0gweLiBaukYpyKaBeUXQsQrsIfePUyhbGlf294cKyZ0tWN7u', 0, 'light');

INSERT INTO conference (id, name, begin_date, end_date, website, ticket, travel, accommodation) VALUES (1, 'Test Conference 1', '2020-02-02', '2020-02-02', 'https://localhost/', 0, 50, 0);
INSERT INTO conference (id, name, begin_date, end_date, website, ticket, travel, accommodation) VALUES (2, 'Test Conference 2', '2050-05-05', '2050-05-07', 'https://localhost/', 650, 250, 350);

INSERT INTO request (employee_id, conference_id, request_date, role, reason, status, status_date, status_comment) VALUES (1, 1, '2020-01-01 12:00:00', 'attendee', 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam.', 'submitted', '2023-01-01 12:00:00', '');
INSERT INTO request (employee_id, conference_id, request_date, role, reason, status, status_date, status_comment) VALUES (2, 2, '2023-01-01 12:00:00', 'attendee', 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam.', 'submitted', '2023-01-01 12:00:00', '');

-- [jooq ignore stop]
