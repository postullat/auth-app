CREATE TABLE users
(
    id             BIGSERIAL,
    password       VARCHAR(80) NOT NULL,
    email          VARCHAR(50) UNIQUE,
    email_verified BOOLEAN,
    PRIMARY KEY (id)
);

CREATE TABLE roles
(
    id   SERIAL,
    name VARCHAR(50) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE users_roles
(
    user_id BIGINT NOT NULL,
    role_id INT    NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (role_id) REFERENCES roles (id)
);

INSERT INTO roles(name)
VALUES ('ROLE_USER'),
       ('ROLE_ADMIN');

INSERT INTO users (password, email, email_verified)
VALUES ('$2a$10$iNpmsmylKP.lY0YZPrmgP.MhTtRmqasGlcDmOETstXCZzbCHIsGOq', 'user@gmail.com', true),
       ('$2a$10$iNpmsmylKP.lY0YZPrmgP.MhTtRmqasGlcDmOETstXCZzbCHIsGOq', 'admin@gmail.com', true);

INSERT INTO users_roles(user_id, role_id)
VALUES (1, 1),
       (2, 2);

ALTER TABLE users
    ADD COLUMN created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;
