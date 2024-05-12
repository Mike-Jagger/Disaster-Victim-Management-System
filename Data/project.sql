/*
 Each time this file is executed, it will reset the database to the original state defined below.
 You can import this directly in your database by (a) manually entering the first three lines of
 commands from this file, (b) removing the first five lines of commands from this file, and
 (c) \i 'path_to_file\project.sql' (with appropriate use of \ or / based on OS).

 During grading, TAs will assume that these two tables exist, but will enter different values.
 Thus you cannot assume that any of the values provided here exist, but you can assume the tables
 exist.

 You may optionally create additional tables in the ensf380project database with demonstration 
 data, provided that you provide the information in a valid SQL file which TAs can import and
 clearly include this information in your instructions.
 */


/*DROP DATABASE IF EXISTS ensf380project;
CREATE DATABASE ensf380project;
ALTER DATABASE ensf380project OWNER TO oop;
GRANT ALL PRIVILEGES ON DATABASE ensf380project TO oop;
\c ensf380project;*/

DROP TABLE IF EXISTS INQUIRER CASCADE;
CREATE TABLE INQUIRER (
    id SERIAL PRIMARY KEY,
    firstName VARCHAR(50) NOT NULL,
    lastName VARCHAR(50),
    phoneNumber VARCHAR(20) NOT NULL
);
/*Add INFO to use for determining if relief service is central or */
INSERT INTO INQUIRER (id, firstName, lastName, phoneNumber) VALUES
(1, 'Dominik', 'Pflug', '123-456-9831'),
(2, 'Yaa', 'Odei', '123-456-8913'),
(3, 'Cecilia', 'Cobos', '123-456-7891'),
(4, 'Hongjoo', 'Park', '123-456-8912');
INSERT INTO INQUIRER (id, firstName, phoneNumber) VALUES
(5, 'Saartje', '123-456-7234'),
(6, 'Urjoshi', '456-123-4281');
-- Adjust the sequence name as per actual sequence used for the id column
SELECT setval('inquiry_log_id_seq', (SELECT MAX(id) FROM INQUIRY_LOG));

DROP TABLE IF EXISTS INQUIRY_LOG CASCADE;
CREATE TABLE INQUIRY_LOG (
    id SERIAL PRIMARY KEY,
    inquirer INT NOT NULL,
    callDate DATE NOT NULL,
    details VARCHAR(500) NOT NULL,
    foreign key (inquirer) references INQUIRER(id) ON UPDATE CASCADE
);

INSERT INTO INQUIRY_LOG (id, inquirer, callDate, details) VALUES
(1, 1, '2024-02-28', 'Theresa Pflug'),
(2, 2, '2024-02-28', 'Offer to assist as volunteer'),
(3, 3, '2024-03-01', 'Valesk Souza'),
(4, 1, '2024-03-01', 'Theresa Pflug'),
(5, 1, '2024-03-02', 'Theresa Pflug'),
(6, 4, '2024-03-02', 'Yoyo Jefferson and Roisin Fitzgerald'),
(7, 5, '2024-03-02', 'Henk Wouters'),
(8, 3, '2024-03-03', 'Melinda'),
(9, 6, '2024-03-04', 'Julius');

DROP TABLE IF EXISTS FAMILY CASCADE;
CREATE TABLE FAMILY (
    family_id SERIAL PRIMARY KEY,
    family_name VARCHAR(255)
);

DROP TABLE IF EXISTS LOCATION CASCADE;
CREATE TABLE LOCATION (
    location_id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    address TEXT
);

DROP TABLE IF EXISTS DISASTER_VICTIM CASCADE;
CREATE TABLE DISASTER_VICTIM (
    victim_id SERIAL PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    date_of_birth DATE,
    approximate_age INT,
    entry_date DATE NOT NULL,
    comments TEXT,
    assigned_social_id INT UNIQUE,
    gender VARCHAR(50),
    location_id INT,
    family_id INT,
    FOREIGN KEY (location_id) REFERENCES LOCATION(location_id),
    FOREIGN KEY (family_id) REFERENCES FAMILY(family_id)
);

-- Linking family members with a family (assuming members are victims)
DROP TABLE IF EXISTS FAMILY_MEMBER CASCADE;
CREATE TABLE FAMILY_MEMBER (
    family_id INT,
    victim_id INT,
    PRIMARY KEY (family_id, victim_id),
    FOREIGN KEY (family_id) REFERENCES FAMILY(family_id),
    FOREIGN KEY (victim_id) REFERENCES DISASTER_VICTIM(victim_id)
);

-- For storing family connections (relationships between victims)
DROP TABLE IF EXISTS FAMILY_CONNECTION CASCADE;
CREATE TABLE FAMILY_CONNECTION (
    connection_id VARCHAR(510) PRIMARY KEY,
    family_id INT,
    victim_one_id INT,
    victim_two_id INT,
    relationship_type VARCHAR(255),
    FOREIGN KEY (family_id) REFERENCES  FAMILY(family_id),
    FOREIGN KEY (victim_one_id) REFERENCES DISASTER_VICTIM(victim_id),
    FOREIGN KEY (victim_two_id) REFERENCES DISASTER_VICTIM(victim_id)
);

DROP TABLE IF EXISTS MEDICAL_RECORD CASCADE;
CREATE TABLE MEDICAL_RECORD (
    record_id SERIAL PRIMARY KEY,
    victim_id INT NOT NULL,
    location_id INT NOT NULL,
    treatment_details TEXT,
    date_of_treatment DATE,
    FOREIGN KEY (victim_id) REFERENCES DISASTER_VICTIM(victim_id),
    FOREIGN KEY (location_id) REFERENCES LOCATION(location_id)
);

DROP TABLE IF EXISTS SUPPLY CASCADE;
CREATE TABLE SUPPLY (
    supply_id SERIAL PRIMARY KEY,
    type VARCHAR(255),
    quantity INT
);

-- Supplies available at specific locations
DROP TABLE IF EXISTS LOCATION_SUPPLY CASCADE;
CREATE TABLE LOCATION_SUPPLY (
    location_id INT,
    supply_id INT,
    quantity INT, -- Override the general quantity for specific locations
    PRIMARY KEY (location_id, supply_id),
    FOREIGN KEY (location_id) REFERENCES LOCATION(location_id),
    FOREIGN KEY (supply_id) REFERENCES SUPPLY(supply_id)
);

-- Victim specific supplies (personal belongings)
DROP TABLE IF EXISTS VICTIM_SUPPLY CASCADE;
CREATE TABLE VICTIM_SUPPLY (
    victim_id INT,
    supply_id INT,
    quantity INT,
    PRIMARY KEY (victim_id, supply_id),
    FOREIGN KEY (victim_id) REFERENCES DISASTER_VICTIM(victim_id),
    FOREIGN KEY (supply_id) REFERENCES SUPPLY(supply_id)
);

-- DietaryRestrictions can be stored as a separate table and linked to victims via a junction table
DROP TABLE IF EXISTS DIETARY_RESTRICTION CASCADE;
CREATE TABLE DIETARY_RESTRICTION (
    restriction_id VARCHAR(4) PRIMARY KEY,
    description TEXT
);

-- Junction table for many-to-many relationship between victims and dietary restrictions
DROP TABLE IF EXISTS VICTIM_DIETARY_RESTRICTION CASCADE;
CREATE TABLE VICTIM_DIETARY_RESTRICTION (
    victim_id INT,
    restriction_id VARCHAR(4),
    PRIMARY KEY (victim_id, restriction_id),
    FOREIGN KEY (victim_id) REFERENCES DISASTER_VICTIM(victim_id),
    FOREIGN KEY (restriction_id) REFERENCES DIETARY_RESTRICTION(restriction_id)
);

-- Insert predefined dietary restrictions (if needed)
INSERT INTO DIETARY_RESTRICTION (restriction_id, description) VALUES
('AVML', 'Asian vegetarian meal'),
('DBML', 'Diabetic meal'),
('GFML', 'Gluten intolerant meal'),
('KSML', 'Kosher meal'),
('LSML', 'Low salt meal'),
('MOML', 'Muslim meal'),
('PFML', 'Peanut-free meal'),
('VGML', 'Vegan meal'),
('VJML', 'Vegetarian Jain meal');

-- Insert data into Family
INSERT INTO FAMILY (family_id, family_name) VALUES
(1, 'Smith Family'),
(2, 'Johnson Family');

-- Insert data into Location
INSERT INTO LOCATION (location_id, name, address) VALUES
(1, 'Central Shelter', '1234 Relief Rd, Calgary, AB'),
(2, 'Community Hall', '2345 Help St, Calgary, AB');

-- Insert data into DisasterVictim
INSERT INTO DISASTER_VICTIM (victim_id, first_name, last_name, date_of_birth,
                            approximate_age, entry_date, comments, gender, location_id,
                            family_id) VALUES
(1, 'John', 'Smith', null, 42, '2024-01-01', 'No comments', 'man', 1, 1),
(2, 'Jane', 'Smith', '1983-07-05', null, '2024-01-01', 'Allergic to peanuts', 'woman', 1, 1),
(3, 'Michael', 'Johnson', '1990-11-15', null, '2024-01-01', 'Diabetic', 'man', 2, 2);

-- Insert data into FamilyMember
INSERT INTO FAMILY_MEMBER (family_id, victim_id) VALUES
(1, 1),
(1, 2),
(2, 3);

-- Insert data into FamilyConnection
INSERT INTO FAMILY_CONNECTION (connection_id, family_id, victim_one_id, victim_two_id, relationship_type) VALUES
('Jane-John', 1, 2, 1, 'Spouse');

-- Insert data into MedicalRecord
INSERT INTO MEDICAL_RECORD (victim_id, location_id, treatment_details, date_of_treatment) VALUES
(1, 1, 'Treated for minor injuries', '2024-01-02'),
(3, 2, 'Routine diabetes check-up', '2024-01-03');

-- Insert data into Supply
INSERT INTO SUPPLY (type, quantity) VALUES
('Water Bottle', 100),
('Blanket', 50);

-- Insert data into LocationSupply
INSERT INTO LOCATION_SUPPLY (location_id, supply_id, quantity) VALUES
(1, 1, 80), -- Central Shelter's remaining water bottle supplies
(1, 2, 30), -- Central Shelter's remaining blanket supplies
(2, 1, 20), -- Community Hall's remaining water bottle supplies
(2, 2, 20); -- Community Hall's remaining blanket supplies

-- Insert data into VictimSupply
INSERT INTO VICTIM_SUPPLY (victim_id, supply_id, quantity) VALUES
(1, 1, 5), -- John's supplies
(2, 2, 2); -- Jane's supplies

-- Insert data into VictimDietaryRestriction
INSERT INTO VICTIM_DIETARY_RESTRICTION (victim_id, restriction_id) VALUES
(2, 'PFML'),  -- Jane Smith has a peanut-free diet
(3, 'DBML');  -- Michael Johnson has a diabetic diet

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO oop;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO oop;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO oop;















