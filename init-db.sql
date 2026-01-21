CREATE TABLE IF NOT EXISTS books (
	id UUID PRIMARY KEY,
	title VARCHAR(255) NOT NULL,
	author VARCHAR(255) NOT NULL,
	isbn VARCHAR(20) NOT NULL UNIQUE,
	published_year INTEGER,
	genre VARCHAR(255),
	description TEXT,
	total_copies INTEGER NOT NULL,
	available_copies INTEGER NOT NULL,
	image_url VARCHAR(1024)
);

CREATE TABLE IF NOT EXISTS members (
	id UUID PRIMARY KEY,
	first_name VARCHAR(128) NOT NULL,
	last_name VARCHAR(128) NOT NULL,
	email VARCHAR(255) NOT NULL UNIQUE,
	active BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS loans (
	id UUID PRIMARY KEY,
	book_id UUID NOT NULL REFERENCES books(id),
	member_id UUID NOT NULL REFERENCES members(id),
	loan_date DATE NOT NULL,
	due_date DATE NOT NULL,
	return_date DATE,
	status VARCHAR(32) NOT NULL,
	CONSTRAINT uq_member_book_status UNIQUE (member_id, book_id, status)
);

CREATE INDEX IF NOT EXISTS idx_loans_member ON loans(member_id);
CREATE INDEX IF NOT EXISTS idx_loans_book ON loans(book_id);
CREATE INDEX IF NOT EXISTS idx_loans_status ON loans(status);
