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

## Estrutura

| Caminho | Conteúdo |
|---|---|
| `db/init/` | schema, seed sintético determinístico |
| `docs/matriz-autorizacao.md` | matriz de autorização — fonte da verdade dos vereditos |

## Roadmap

- [x] Fase 0 — ambiente isolado
- [ ] Fase 1 — base + matriz
- [ ] Fase 2 — baseline vulnerável
- [ ] Fase 3 — camadas de defesa com toggles
- [ ] Fase 4 — catálogo de ataques + runner
- [ ] Fase 5 — experimentos e métricas
- [ ] Fase 6 — análise
