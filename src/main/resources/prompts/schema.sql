CREATE TABLE departamento (
  id              SERIAL PRIMARY KEY,
  nome            TEXT NOT NULL
);

CREATE TABLE funcionario (
  id              SERIAL PRIMARY KEY,
  nome            TEXT NOT NULL,
  cargo           TEXT NOT NULL,
  departamento_id INT REFERENCES departamento(id),
  usuario_login   TEXT UNIQUE
);

CREATE TABLE salario (
  funcionario_id  INT PRIMARY KEY REFERENCES funcionario(id),
  valor           NUMERIC(12,2) NOT NULL
);

CREATE TABLE avaliacao_desempenho (
  id              SERIAL PRIMARY KEY,
  funcionario_id  INT REFERENCES funcionario(id),
  nota            INT,
  observacao      TEXT
);

CREATE TABLE projeto (
  id              SERIAL PRIMARY KEY,
  nome            TEXT,
  departamento_id INT REFERENCES departamento(id)
);
