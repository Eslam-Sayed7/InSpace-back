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

CREATE TABLE test_suites (
                             suite_id SERIAL PRIMARY KEY,
                             name VARCHAR(255) NOT NULL,
                             description TEXT,
                             created_at TIMESTAMP DEFAULT NOW(),
                             updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE test_scenarios (
                                scenario_id SERIAL PRIMARY KEY,
                                suite_id INT NOT NULL REFERENCES test_suites(suite_id) ON DELETE CASCADE,
                                name VARCHAR(255) NOT NULL,
                                description TEXT,
                                priority SMALLINT DEFAULT 3 CHECK (priority BETWEEN 1 AND 5),
                                created_at TIMESTAMP DEFAULT NOW(),
                                updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE tags (
                      tag_id SERIAL PRIMARY KEY,
                      name VARCHAR(100) UNIQUE NOT NULL
);

-- would help for optimization
CREATE TABLE scenario_tags (
                               scenario_id INT NOT NULL REFERENCES test_scenarios(scenario_id) ON DELETE CASCADE,
                               tag_id INT NOT NULL REFERENCES tags(tag_id) ON DELETE CASCADE,
                               PRIMARY KEY (scenario_id, tag_id)
);

CREATE TABLE action_types (
                              action_type_id SERIAL PRIMARY KEY,
                              name VARCHAR(50) UNIQUE NOT NULL, -- click, input_text, assert_text, wait, screenshot
                              description TEXT
);

CREATE TABLE test_steps (
                            step_id SERIAL PRIMARY KEY,
                            scenario_id INT NOT NULL REFERENCES test_scenarios(scenario_id) ON DELETE CASCADE,
                            sequence_order INT NOT NULL,
                            action_type_id INT NOT NULL REFERENCES action_types(action_type_id),
                            selector TEXT,
                            input_value TEXT,
                            expected_outcome TEXT,
                            created_at TIMESTAMP DEFAULT NOW(),
                            updated_at TIMESTAMP DEFAULT NOW(),
                            UNIQUE (scenario_id, sequence_order)
);

CREATE TABLE test_executions (
                                 execution_id SERIAL PRIMARY KEY,
                                 scenario_id INT NOT NULL REFERENCES test_scenarios(scenario_id) ON DELETE CASCADE,
                                 step_id INT NOT NULL REFERENCES test_steps(step_id) ON DELETE CASCADE,
                                 run_identifier UUID NOT NULL, -- groups all steps from one run together
                                 status_id INT NOT NULL DEFAULT 1 REFERENCES execution_status(status_id);
actual_output TEXT,
    screenshot_url TEXT,
    started_at TIMESTAMP DEFAULT NOW(),
    finished_at TIMESTAMP
);

CREATE TABLE execution_status (
                                  status_id SERIAL PRIMARY KEY,
                                  name VARCHAR(20) UNIQUE NOT NULL,  -- pending, running, passed, failed, error, skipped
                                  description TEXT
);

INSERT INTO execution_status (name, description) VALUES
                                                     ('pending', 'Execution has not started yet'),
                                                     ('running', 'Execution is currently in progress'),
                                                     ('passed', 'Step executed successfully'),
                                                     ('failed', 'Step failed to meet the expected outcome'),
                                                     ('error', 'An unexpected error occurred'),
                                                     ('skipped', 'Step was skipped in this run');

-- Indexes
CREATE INDEX idx_scenarios_suite_id ON test_scenarios(suite_id);
CREATE INDEX idx_scenario_tags_tag_id ON scenario_tags(tag_id);
CREATE INDEX idx_test_steps_scenario_id ON test_steps(scenario_id);
CREATE INDEX idx_test_executions_run_identifier ON test_executions(run_identifier);

-- Stored Procedures

