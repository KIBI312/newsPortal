\c newsPortal;

CREATE TABLE IF NOT EXISTS news (
    id UUID PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    brief VARCHAR(500),
    content TEXT,
    published_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS users(
	username VARCHAR(50) NOT NULL PRIMARY KEY,
	password VARCHAR(50) NOT NULL,
	enabled BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS authorities (
	username VARCHAR(50) NOT NULL,
	authority VARCHAR(50) NOT NULL,
	CONSTRAINT fk_authorities_users FOREIGN KEY(username) REFERENCES users(username)
);
CREATE UNIQUE INDEX ix_auth_username ON authorities (username,authority);