CREATE EXTENSION "uuid-ossp";

CREATE TABLE users (
       id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
       email varchar(80) NOT NULL,
       pw_hash varchar(88) NOT NULL
);
 
