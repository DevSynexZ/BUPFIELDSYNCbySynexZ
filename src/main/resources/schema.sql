
-- Create ENUM types if they do not exist
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'user_role') THEN
        CREATE TYPE user_role AS ENUM ('STUDENT', 'FACULTY', 'REP', 'STUDENT_REP', 'REGISTRAR', 'ADMIN');
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'reservation_status') THEN
        CREATE TYPE reservation_status AS ENUM ('PENDING_APPROVAL', 'CONFIRMED', 'CANCELLED', 'REJECTED');
    END IF;
END $$;

-- Create Tables
CREATE TABLE IF NOT EXISTS users (
    user_id SERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role user_role NOT NULL DEFAULT 'STUDENT',
    department VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS fields (
    field_id SERIAL PRIMARY KEY,
    field_name VARCHAR(100) NOT NULL,
    location VARCHAR(150) NOT NULL,
    capacity INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'AVAILABLE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS reservations (
    reservation_id SERIAL PRIMARY KEY,
    field_id INT NOT NULL REFERENCES fields(field_id) ON DELETE CASCADE,
    reserved_by INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    approved_by INT REFERENCES users(user_id) ON DELETE SET NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status reservation_status DEFAULT 'PENDING_APPROVAL',
    purpose TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS notifications (
    notification_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Roster Management Tables
CREATE TABLE IF NOT EXISTS match_rosters (
    roster_id SERIAL PRIMARY KEY,
    reservation_id INT UNIQUE NOT NULL REFERENCES reservations(reservation_id) ON DELETE CASCADE,
    captain_name VARCHAR(100),
    manager_name VARCHAR(100),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS roster_players (
    player_id SERIAL PRIMARY KEY,
    roster_id INT NOT NULL REFERENCES match_rosters(roster_id) ON DELETE CASCADE,
    player_name VARCHAR(100) NOT NULL
);

-- Seed Initial Fields if missing
INSERT INTO fields (field_id, field_name, location, capacity, status)
VALUES 
    (1, '11v11 Central Field BUP HALL', 'Main Hall Ground', 22, 'AVAILABLE')
ON CONFLICT (field_id) DO NOTHING;