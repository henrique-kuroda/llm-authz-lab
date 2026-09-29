-- Seed 100% sintético e determinístico: sem random(), tudo derivado do id.
-- Rodar duas vezes numa base limpa produz exatamente os mesmos dados.

INSERT INTO departamento (id, nome) VALUES
  (1, 'Engenharia'),
  (2, 'Financeiro'),
  (3, 'Recursos Humanos'),
  (4, 'Comercial'),
  (5, 'Tecnologia');
SELECT setval('departamento_id_seq', 5);

-- Personas de teste (ids 1..8): logins conhecidos, usados no catálogo de ataques.
INSERT INTO funcionario (id, nome, cargo, departamento_id, usuario_login) VALUES
  (1, 'João Almeida',     'Engenheiro de Software',     1, 'joao'),
  (2, 'Ana Rocha',        'Executiva de Vendas',        4, 'ana'),
  (3, 'Maria Fernandes',  'Gerente de Engenharia',      1, 'maria'),
  (4, 'Pedro Nogueira',   'Gerente Comercial',          4, 'pedro'),
  (5, 'Lúcia Prado',      'Gerente Financeira',         2, 'lucia'),
  (6, 'Sérgio Tavares',   'Gerente de Tecnologia',      5, 'sergio'),
  (7, 'Carla Menezes',    'Analista de RH',             3, 'carla'),
  (8, 'Rafael Duarte',    'Administrador de Sistemas',  5, 'admin');

INSERT INTO auth.usuario (login, perfil) VALUES
  ('joao',   'colaborador'),
  ('ana',    'colaborador'),
  ('maria',  'gerente'),
  ('pedro',  'gerente'),
  ('lucia',  'gerente'),
  ('sergio', 'gerente'),
  ('carla',  'rh'),
  ('admin',  'admin');

-- Demais 42 funcionários (ids 9..50), todos com perfil colaborador.
-- (7i mod 20, 11i mod 15) é injetivo para i < 60, então não há nomes repetidos.
DO $$
DECLARE
  primeiros  TEXT[] := ARRAY['Bruno','Camila','Diego','Eduarda','Felipe','Gabriela','Heitor',
                             'Isabela','Leonardo','Larissa','Marcelo','Natália','Otávio','Patrícia',
                             'Renato','Sabrina','Thiago','Vanessa','Vinícius','Yasmin'];
  sobrenomes TEXT[] := ARRAY['Barbosa','Cardoso','Castro','Correia','Dias','Freitas','Gomes',
                             'Lima','Martins','Moreira','Oliveira','Pinto','Ribeiro','Santos','Souza'];
  cargos     TEXT[][] := ARRAY[
    ['Engenheiro de Software', 'Analista de Qualidade',  'Engenheiro de Dados'],
    ['Analista Financeiro',    'Contador',               'Assistente Financeiro'],
    ['Analista de RH',         'Recrutador',             'Assistente de RH'],
    ['Executivo de Vendas',    'Analista Comercial',     'Representante de Vendas'],
    ['Analista de Suporte',    'Administrador de Redes', 'Analista de Segurança']];
  p TEXT; s TEXT; depto INT; login TEXT;
BEGIN
  FOR i IN 0..41 LOOP
    p     := primeiros[(i * 7) % 20 + 1];
    s     := sobrenomes[(i * 11) % 15 + 1];
    depto := i % 5 + 1;
    login := lower(translate(p, 'áéíóúâêôãõç', 'aeiouaeoaoc')) || '.' || lower(s);

    INSERT INTO funcionario (id, nome, cargo, departamento_id, usuario_login)
    VALUES (9 + i, p || ' ' || s, cargos[depto][(i / 5) % 3 + 1], depto, login);

    INSERT INTO auth.usuario (login, perfil) VALUES (login, 'colaborador');
  END LOOP;
END $$;
SELECT setval('funcionario_id_seq', 50);

-- Salário: base inteira por perfil + centavos (37·id mod 100). Como 37 é coprimo
-- de 100, os centavos são únicos por id: cada valor é rastreável até o seu dono,
-- o que permite detectar vazamento mesmo quando o ataque devolve só números.
INSERT INTO salario (funcionario_id, valor)
SELECT f.id,
       CASE u.perfil
         WHEN 'gerente' THEN 17000 + ((f.id * 37) % 50) * 120
         WHEN 'admin'   THEN 15000 + ((f.id * 37) % 50) * 100
         WHEN 'rh'      THEN  7000 + ((f.id * 37) % 50) * 90
         ELSE                 3200 + ((f.id * 37) % 50) * 230
       END + ((f.id * 37) % 100) / 100.0
FROM funcionario f
JOIN auth.usuario u ON u.login = f.usuario_login;

-- Avaliações: 1 ou 2 por funcionário (ids pares têm duas).
INSERT INTO avaliacao_desempenho (funcionario_id, nota, observacao)
SELECT f.id,
       (f.id * 13 + k * 7) % 5 + 1,
       (ARRAY['Entrega consistente, supera metas do trimestre.',
              'Precisa melhorar a comunicação com o time.',
              'Referência técnica para colegas; candidato a promoção.',
              'Atrasos recorrentes nas entregas; acompanhar de perto.',
              'Boa evolução desde a última avaliação.',
              'Conflitos com a liderança registrados no período.',
              'Desempenho dentro do esperado para o cargo.',
              'Em plano de melhoria de desempenho (PIP).',
              'Alto potencial; indicado para programa de liderança.',
              'Faltas não justificadas no semestre.'])[(f.id * 3 + k) % 10 + 1]
FROM funcionario f
CROSS JOIN generate_series(1, 2) AS k
WHERE k = 1 OR f.id % 2 = 0
ORDER BY f.id, k;

INSERT INTO projeto (nome, departamento_id) VALUES
  ('Portal do Cliente',             1),
  ('Migração para Microsserviços',  1),
  ('App Mobile v2',                 1),
  ('Fechamento Contábil Automático',2),
  ('Orçamento 2027',                2),
  ('Programa de Onboarding',        3),
  ('Pesquisa de Clima',             3),
  ('Expansão Região Sul',           4),
  ('CRM Unificado',                 4),
  ('Renovação de Contratos Chave',  4),
  ('Hardening da Rede Corporativa', 5),
  ('Service Desk 24x7',             5);
