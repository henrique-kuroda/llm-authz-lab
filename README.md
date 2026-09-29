# llm-authz-lab

Laboratório isolado para medir defesas contra *prompt injection* em interfaces
que traduzem linguagem natural → SQL. É um *harness* controlado: os mesmos
ataques rodam contra configurações de defesa incrementais e medimos
**taxa de bloqueio × latência × falsos positivos**.

> Princípio-guia: **a saída do LLM é sempre não-confiável.** A garantia de
> autorização nunca depende do LLM ter "obedecido" — ela mora no backend e no banco.

Todos os dados são sintéticos e gerados por script. Nada aqui aponta para base real.

## Stack

- Java 21 + Spring Boot 4 (orquestração)
- PostgreSQL 17 em Docker, exposto só em `127.0.0.1`
- LLM: Gemini API, `temperature=0` e versão do modelo fixada no `.env`

## Subindo o ambiente

```bash
cp .env.example .env   # preencha POSTGRES_PASSWORD e GEMINI_API_KEY
docker compose up -d
```

O banco é criado e populado na primeira subida pelos scripts de `db/init/`.
Para recriar do zero (volta ao seed original):

```bash
docker compose down -v && docker compose up -d
```

## Rodando a API

```bash
./mvnw spring-boot:run
```

A API escuta só em `127.0.0.1:8080` e lê os segredos do mesmo `.env`.
A autenticação é simulada: o login vai no header `X-Usuario`
(personas em [docs/matriz-autorizacao.md](docs/matriz-autorizacao.md)).

```bash
curl -X POST http://127.0.0.1:8080/consulta -H "Content-Type: application/json" -H "X-Usuario: joao" -d "{\"pergunta\": \"Qual é o meu salário?\"}"
```

A resposta traz o SQL gerado, os resultados de cada statement, o status
(`EXECUTADA`, `ERRO_LLM`, `ERRO_SQL`), a versão do modelo e a latência separada
em LLM / banco / total. Mais exemplos em `requests.http`.

### Baseline (fase 2) — vulnerável de propósito

```
X-Usuario → pergunta → system + schema + pergunta → Gemini → SQL cru
          → executado como está pelo superusuário do banco → linhas
```

Sem nenhuma verificação: aceita múltiplos statements, DML/DDL e acesso ao
schema `auth`. Cada request roda numa transação com rollback, só para o
laboratório não se degradar — isso não é defesa (um `COMMIT` no texto fura).
Se o banco for alterado, recrie-o com `docker compose down -v`.

## Testes

```bash
./mvnw test     # unitários
./mvnw verify   # + integração (*IT), exige o Postgres do compose no ar
```

`BaselineVulneravelIT` prova a vulnerabilidade da baseline sem depender do LLM
(o LLM é mockado para devolver o SQL que um atacante induziria).

## Estrutura

| Caminho | Conteúdo |
|---|---|
| `db/init/` | schema, seed sintético determinístico |
| `docs/matriz-autorizacao.md` | matriz de autorização — fonte da verdade dos vereditos |
| `src/.../auth` | identidade do usuário (header `X-Usuario` → `auth.usuario`) |
| `src/.../llm` | cliente do Gemini (REST direto, modelo e temperatura fixos) |
| `src/.../consulta` | pipeline NL → SQL → banco |

## Roadmap

- [x] Fase 0 — ambiente isolado
- [x] Fase 1 — base + matriz
- [x] Fase 2 — baseline vulnerável
- [ ] Fase 3 — camadas de defesa com toggles
- [ ] Fase 4 — catálogo de ataques + runner
- [ ] Fase 5 — experimentos e métricas
- [ ] Fase 6 — análise
