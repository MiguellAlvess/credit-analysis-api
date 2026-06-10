CREATE TABLE companies (
    id UUID PRIMARY KEY,
    cnpj VARCHAR(14) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    registration_status VARCHAR(50) NOT NULL,
    postal_code VARCHAR(8) NOT NULL,
    city VARCHAR(100),
    state VARCHAR(2),
    founded_at DATE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE credit_requests (
    id UUID PRIMARY KEY,
    company_id UUID NOT NULL,
    requested_amount DECIMAL(15,2) NOT NULL,
    annual_revenue DECIMAL(15,2) NOT NULL,
    score INTEGER,
    risk_level VARCHAR(50),
    approved_limit DECIMAL(15,2),
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_credit_requests_company
        FOREIGN KEY (company_id)
        REFERENCES companies(id)
);

CREATE TABLE credit_decisions (
    id UUID PRIMARY KEY,
    credit_request_id UUID NOT NULL UNIQUE,
    decision VARCHAR(50) NOT NULL,
    approved_amount DECIMAL(15,2),
    reason TEXT,
    decided_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_credit_decisions_credit_request
        FOREIGN KEY (credit_request_id)
        REFERENCES credit_requests(id)
);