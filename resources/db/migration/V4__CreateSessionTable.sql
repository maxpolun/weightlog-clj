CREATE TABLE sessions (
	id CHAR(88) PRIMARY KEY,
	user_id uuid references users(id),
	created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
