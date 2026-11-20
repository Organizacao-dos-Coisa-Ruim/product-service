# product-service

Implementação do microsserviço Product: catálogo de produtos à venda, persistido no
PostgreSQL (schema `products`) com JPA e Flyway.

Configure `DATABASE_HOST`, `DATABASE_PORT`, `DATABASE_DB`, `DATABASE_USERNAME` e
`DATABASE_PASSWORD`.

```bash
# No PowerShell, execute primeiro no repositório api/product
.\mvnw.cmd clean install
# Depois, no repositório api/product-service
.\mvnw.cmd clean package
```

Próximos passos: CRUD (#26), Dockerfile (#27) e testes (#28).
