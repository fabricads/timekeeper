DROP SEQUENCE seq_contact;


CREATE SEQUENCE seq_contact;


DROP SEQUENCE seq_organization;


CREATE SEQUENCE seq_organization;


DROP SEQUENCE seq_person;


CREATE SEQUENCE seq_person;


DROP SEQUENCE seq_project;


CREATE SEQUENCE seq_project;


DROP SEQUENCE seq_role;


CREATE SEQUENCE seq_role;


DROP SEQUENCE seq_task;


CREATE SEQUENCE seq_task;


DROP SEQUENCE seq_timecard;


CREATE SEQUENCE seq_timecard;


DROP SEQUENCE seq_timecard_entry;


CREATE SEQUENCE seq_timecard_entry;


DROP TABLE org_contact CASCADE CONSTRAINTS PURGE;


DROP TABLE person_task CASCADE CONSTRAINTS PURGE;


DROP TABLE timecard_entry CASCADE CONSTRAINTS PURGE;


DROP TABLE timecard CASCADE CONSTRAINTS PURGE;


DROP TABLE task CASCADE CONSTRAINTS PURGE;


DROP TABLE project CASCADE CONSTRAINTS PURGE;


DROP TABLE person CASCADE CONSTRAINTS PURGE;


DROP TABLE role CASCADE CONSTRAINTS PURGE;


DROP TABLE organization CASCADE CONSTRAINTS PURGE;


CREATE TABLE organization
(
	id_org  INTEGER   DEFAULT  nextval('seq_organization') NOT NULL ,
	name  VARCHAR(200)  NULL ,
	enabled  boolean  NULL ,
	registration_date  TIMESTAMP  NULL ,
CONSTRAINT  XPKorganization PRIMARY KEY (id_org)
);


CREATE TABLE role
(
	id_role  INTEGER   DEFAULT  nextval('seq_role') NOT NULL ,
	name  VARCHAR(50)  NULL ,
	description  VARCHAR(100)  NULL ,
	short_name  VARCHAR(30)  NULL ,
CONSTRAINT  XPKrole PRIMARY KEY (id_role)
);


CREATE TABLE person
(
	id_person  INTEGER   DEFAULT  nextval('seq_person') NOT NULL ,
	id_org  INTEGER  NOT NULL ,
	id_role  INTEGER  NOT NULL ,
	name  VARCHAR(100)  NULL ,
	city  VARCHAR(100)  NULL ,
	state  VARCHAR(20)  NULL ,
	country  VARCHAR(100)  NULL ,
	email  VARCHAR(100)  NULL ,
	enabled  boolean  NULL ,
	pa_number  INTEGER  NULL ,
	password  VARCHAR(200)  NULL ,
	person_type  INTEGER  NULL ,
	telephone1  bigint  NULL ,
	telephone2  bigint  NULL ,
	registration_date  TIMESTAMP  NULL ,
	last_modification  TIMESTAMP  NULL ,
CONSTRAINT  XPKperson PRIMARY KEY (id_person),
CONSTRAINT  fk_org_to_person FOREIGN KEY (id_org) REFERENCES organization(id_org) ON DELETE CASCADE,
CONSTRAINT  fk_role_to_person FOREIGN KEY (id_role) REFERENCES role(id_role)
);


CREATE TABLE project
(
	id_project  INTEGER   DEFAULT  nextval('seq_project') NOT NULL ,
	id_pm  INTEGER  NOT NULL ,
	description  VARCHAR(200)  NULL ,
	enabled  boolean  NULL ,
	end_date  DATE  NULL ,
	initial_date  DATE  NULL ,
	last_modification  TIMESTAMP  NULL ,
	name  VARCHAR(200)  NULL ,
	pa_number  INTEGER  NULL ,
	registration_date  TIMESTAMP  NULL ,
	use_pm_substitute  boolean  NULL ,
CONSTRAINT  XPKproject PRIMARY KEY (id_project),
CONSTRAINT  fk_pm_to_project FOREIGN KEY (id_pm) REFERENCES person(id_person) ON DELETE CASCADE
);


CREATE TABLE task
(
	id_task  INTEGER   DEFAULT  nextval('seq_task') NOT NULL ,
	id_project  INTEGER  NOT NULL ,
	name  VARCHAR(100)  NULL ,
	task_type  INTEGER  NULL ,
CONSTRAINT  XPKtask PRIMARY KEY (id_task),
CONSTRAINT  fk_project_to_task FOREIGN KEY (id_project) REFERENCES project(id_project) ON DELETE CASCADE
);


CREATE TABLE timecard
(
	id_timecard  INTEGER   DEFAULT  nextval('seq_timecard') NOT NULL ,
	id_consultant  INTEGER  NOT NULL ,
	id_project  INTEGER  NOT NULL ,
	comment_consultant  VARCHAR(200)  NULL ,
	comment_pm  VARCHAR(200)  NULL ,
	status  INTEGER  NULL ,
CONSTRAINT  XPKtimecard PRIMARY KEY (id_timecard),
CONSTRAINT  fk_person_to_timecard FOREIGN KEY (id_consultant) REFERENCES person(id_person) ON DELETE CASCADE,
CONSTRAINT  fk_project_to_timecard FOREIGN KEY (id_project) REFERENCES project(id_project) ON DELETE CASCADE
);


CREATE TABLE timecard_entry
(
	id_timecard_entry  INTEGER   DEFAULT  nextval('seq_timecard_entry') NOT NULL ,
	id_task  INTEGER  NOT NULL ,
	id_timecard  INTEGER  NOT NULL ,
	day  DATE  NULL ,
	work_description  VARCHAR(200)  NULL ,
	work_hours  DOUBLE PRECISION  NULL ,
CONSTRAINT  XPKtimecard_entry PRIMARY KEY (id_timecard_entry),
CONSTRAINT  fk_task_to_tcentry FOREIGN KEY (id_task) REFERENCES task(id_task) ON DELETE CASCADE,
CONSTRAINT  fk_timecard_to_tcentry FOREIGN KEY (id_timecard) REFERENCES timecard(id_timecard) ON DELETE CASCADE
);


CREATE TABLE person_task
(
	id_person  INTEGER  NOT NULL ,
	id_task  INTEGER  NOT NULL ,
CONSTRAINT  fk_person_to_persontask FOREIGN KEY (id_person) REFERENCES person(id_person) ON DELETE CASCADE,
CONSTRAINT  fk_task_to_persontask FOREIGN KEY (id_task) REFERENCES task(id_task) ON DELETE CASCADE
);


CREATE TABLE org_contact
(
	id_contact  INTEGER  NOT NULL ,
	id_org  INTEGER  NOT NULL ,
	name  VARCHAR(100)  NULL ,
	telephone  bigint  NULL ,
CONSTRAINT  fk_org_to_contact FOREIGN KEY (id_org) REFERENCES organization(id_org) ON DELETE CASCADE
);


