-- docker run -d \
--            --name InSpaceDb_pg\
--            -e POSTGRES_USER=postgres \
--            -e POSTGRES_PASSWORD=66c#Abi^Xqjj \
--            -e POSTGRES_DB=InSpaceDb \
--            -p 5432:5432 \
--            postgres

-- Create the InSpaceDb Database
CREATE DATABASE ;

-- Connect to the InSpaceDb Database
\c InSpaceDb;

-- Roles Table
CREATE TABLE roles (
    role_id SERIAL PRIMARY KEY,
    role_name VARCHAR(255) UNIQUE NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert Initial Roles
INSERT INTO roles (role_name, description) VALUES
('ROLE_USER', 'User role'),
('ROLE_SUPER_USER', 'Super User role'),
('ROLE_ADMIN', 'Administrator role');

-- Users Table
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    is_super BOOLEAN DEFAULT False,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
	role_id INT,
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles (role_id) ON DELETE SET NULL
);

-- Admins Table
CREATE TABLE admins (
    admin_id SERIAL PRIMARY KEY,
    user_id INT UNIQUE REFERENCES users(user_id) ON DELETE CASCADE,
    access_level VARCHAR(50),
    additional_info JSONB
);

-- Tests Table
 CREATE TABLE tests (
 test_id SERIAL PRIMARY KEY,

);

-- Runs Table
CREATE TABLE runs (
    run_id SERIAL PRIMARY KEY,
    test_id INT REFERENCES tests(test_id) ON DELETE CASCADE,
    user_id INT REFERENCES users(student_id) ON DELETE CASCADE,
    submission_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    stat BOOLEAN,
    err TEXT
);

-- Progress Tracking Table
CREATE TABLE progress_tracking (
    progress_id SERIAL PRIMARY KEY,
    student_id INT REFERENCES students(student_id) ON DELETE CASCADE,
    course_id INT REFERENCES courses(course_id) ON DELETE CASCADE,
    quiz_scores JSONB,
    assignments_submitted BIGINT,
    attendance_count DOUBLE PRECISION DEFAULT 0.0,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Notifications Table
CREATE TABLE notifications (
    notification_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(user_id) ON DELETE CASCADE,
    type VARCHAR(50) CHECK (type IN ('Test Failed','GenerateOtp')) NOT NULL, -- Type of notification
    content TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for Optimization
--
--
--
--
-- Stored Procedures
