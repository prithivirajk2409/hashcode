CREATE TABLE hcusers (
    user_id serial  primary key,
    user_name text unique not null,
    password text NOT NULL,
    email_id text unique NOT NULL,
    created timestamp without time zone NOT NULL,
    updated timestamp without time zone NOT NULL
);


CREATE TABLE hcproblems (
    problem_id integer primary key,
    problem_name text NOT NULL,
    slug text unique NOT NULL,
    content text NOT NULL,
    difficulty text NOT NULL,
    sample_test_case jsonb NOT NULL,
    driver_code jsonb NOT NULL,
    template_code jsonb NOT NULL,
    created timestamp without time zone NOT NULL,
    updated timestamp without time zone NOT NULL
);


CREATE TABLE hccodeexecutionjobs (
    execution_job_id bigserial primary key,
    status integer NOT NULL,
    execution_type text NOT NULL,
    submission_id bigint,
    metadata jsonb,
    created timestamp without time zone NOT NULL,
    updated timestamp without time zone NOT NULL
);




CREATE TABLE hcusersubmissions (
    submission_id bigserial primary key,
    problem_id integer NOT NULL,
    user_id integer NOT NULL,
    acceptance_status text,
    programming_language text NOT NULL,
    submitted_code text NOT NULL,
    execution_time bigint,
    execution_memory bigint,
    execution_job_id bigint NOT NULL,
    metadata jsonb,
    created timestamp without time zone NOT NULL,
    updated timestamp without time zone NOT NULL,
    CONSTRAINT fk_hcusersubmissions_hccodeexecutionjobs_execution_job_id FOREIGN KEY (execution_job_id) REFERENCES hccodeexecutionjobs(execution_job_id),
    CONSTRAINT fk_hcusersubmissions_hcproblems_problem_id FOREIGN KEY (problem_id) REFERENCES hcproblems(problem_id),
    CONSTRAINT fk_hcusersubmissions_hcusers_user_id FOREIGN KEY (user_id) REFERENCES hcusers(user_id)
);



CREATE TABLE hctestcases(
    problem_id int primary key,
    test_case jsonb not null,
    CONSTRAINT fk_hctestcases_hcproblems_problem_id FOREIGN KEY (problem_id) REFERENCES hcproblems(problem_id)
);



