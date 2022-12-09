-- [jooq ignore start]

INSERT INTO mail_template (`id`, `subject`, `content_text`, `content_html`)
VALUES ('SECURITY_RESET_PASSWORD','Reset your password','To reset your password, use the following one time password to login:\n\n${password}','<p>To reset your password, use the following one time password to login:</p>\n<pre>${password}\n</pre>');

-- [jooq ignore stop]
