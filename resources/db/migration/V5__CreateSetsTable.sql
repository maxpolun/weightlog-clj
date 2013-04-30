CREATE TYPE weight_unit AS ENUM ('lb', 'kg');

CREATE TABLE sets (
	id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
	completed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
	exersize varchar(80) REFERENCES exersizes(name),
	reps INTEGER NOT NULL,
	user_id uuid REFERENCES users(id),
	notes TEXT,
	weight INTEGER NOT NULL,
	unit weight_unit NOT NULL
);
