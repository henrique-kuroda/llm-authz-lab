-- Esquema do domínio (RH/corporativo). É o que o LLM "enxerga".

CREATE TABLE departamento (
  id              SERIAL PRIMARY KEY,
  nome            TEXT NOT NULL
);

CREATE TABLE funcionario (
  id              SERIAL PRIMARY KEY,
  nome            TEXT NOT NULL,
  cargo           TEXT NOT NULL,
  departamento_id INT REFERENCES departamento(id),
  usuario_login   TEXT UNIQUE          -- liga o funcionário ao usuário autenticado
);

CREATE TABLE salario (                 -- DADO SENSÍVEL
  funcionario_id  INT PRIMARY KEY REFERENCES funcionario(id),
  valor           NUMERIC(12,2) NOT NULL
);

CREATE TABLE avaliacao_desempenho (    -- DADO SENSÍVEL
  id              SERIAL PRIMARY KEY,
  funcionario_id  INT REFERENCES funcionario(id),
  nota            INT,
  observacao      TEXT
);

CREATE TABLE projeto (                 -- dado "público" interno
  id              SERIAL PRIMARY KEY,
  nome            TEXT,
  departamento_id INT REFERENCES departamento(id)
);

-- Identidade da aplicação. Fica fora do schema public e não é descrita ao LLM:
-- só o backend lê, para resolver quem está autenticado.
CREATE SCHEMA auth;

CREATE TABLE auth.usuario (
  login           TEXT PRIMARY KEY REFERENCES funcionario(usuario_login),
  perfil          TEXT NOT NULL CHECK (perfil IN ('colaborador', 'gerente', 'rh', 'admin'))
);
