INSERT INTO roles (role_id, role_name) VALUES (1, 'ROLE_ADMIN');
INSERT INTO roles (role_id, role_name) VALUES (2, 'ROLE_USER');

INSERT INTO execution_status (name, description) VALUES
('pending', 'Execution has not started yet'),
('running', 'Execution is currently in progress'),
('passed', 'Step executed successfully'),
('failed', 'Step failed to meet the expected outcome'),
('error', 'An unexpected error occurred'),
('skipped', 'Step was skipped in this run');


INSERT INTO action_types (name, description) VALUES
('click', 'Click on an element'),
('input_text', 'Input text into a field'),
('assert_text', 'Assert that specific text is present'),
('wait', 'Wait for a specified duration or condition'),
('screenshot', 'Take a screenshot'),
('navigate', 'Navigate to a URL'),
('select', 'Select an option from a dropdown'),
('hover', 'Hover over an element'),
('scroll', 'Scroll to an element or position'),
('submit', 'Submit a form');
