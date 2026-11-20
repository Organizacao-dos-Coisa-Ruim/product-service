# product-service

Implementação do microsserviço Product: catálogo de produtos à venda, persistido no
PostgreSQL (schema `products`) com JPA e Flyway. O contrato (rotas e DTOs) está no
repositório da interface `product`.

| Método | Rota | Resposta |
|--------|------|----------|
| `POST` | `/products` | `201` + produto criado · `400` campo inválido |
| `GET` | `/products` | `200` + lista de produtos (ordenada por nome) |
| `GET` | `/products/{id}` | `200` + produto · `404` não existe |
| `DELETE` | `/products/{id}` | `204` · `404` não existe |
| `GET` | `/products/health-check` | `200` |

```json
POST /products
{ "name": "Tomato", "price": 10.12, "unit": "kg" }
```

## Estrutura

```
ProductResource (implementa ProductController) → ProductService → ProductRepository (JPA)
                                     ProductParser: ProductIn/ProductOut ↔ ProductModel
```

- A validação dos campos é feita pelo `@Valid` declarado na interface (`ProductIn`): dado inválido
  vira `400` antes de chegar no service. Os limites batem com as colunas da tabela, para valores
  grandes não virarem erro `500` no banco.
- O `id` é um UUID gerado pelo `ProductService`.
- A tabela é criada pela migration `V2026.11.20.001__create_table_product.sql` (Flyway).

## Configuração

Configure `DATABASE_HOST`, `DATABASE_PORT`, `DATABASE_DB`, `DATABASE_USERNAME` e
`DATABASE_PASSWORD`.

```bash
# No PowerShell, execute primeiro no repositório api/product
.\mvnw.cmd clean install
# Depois, no repositório api/product-service
.\mvnw.cmd clean verify
```

## Testes

- `ProductResourceIntegrationTest`: rotas de ponta a ponta (MockMvc → JPA → H2 no modo
  PostgreSQL, com as mesmas migrations do Flyway).
- `ProductServiceTest`: regras do service com o repositório mockado.

## Docker

O `Dockerfile` monta a imagem a partir do jar já buildado (a interface `product` precisa estar
instalada no Maven local):

```bash
./mvnw clean package
docker build -t product .
```

No `pma26`, o serviço sobe pelo `docker/compose.yml` (nome `product`, porta 8080 só na rede
interna), buildado a partir do código-fonte pelo `docker/java-service.Dockerfile`. O acesso é
pelo gateway (`http://localhost:8080/products`), e o Prometheus coleta `/actuator/prometheus`.
