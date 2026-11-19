# 📚 Biblioteca Web

Sistema web para cadastro de **Autores** e **Livros**. Cada livro é escrito por um autor e um autor pode ter vários livros (relacionamento **1:N**).

## Tecnologias

| Camada | Tecnologia / versão |
|---|---|
| Backend | Java 17 + Spring Boot 3.2 (Web, Data JPA, Validation, Lombok) + Maven |
| Frontend | React 18 + React Router 6 + Axios (react-scripts 5) • Node.js 20 + npm |
| Banco de dados | PostgreSQL 15 (desenvolvimento) • H2 em memória (perfil `test`) |
| Testes | JUnit 5 + Mockito (`@WebMvcTest`, `@MockBean`, `MockMvc`) • `@SpringBootTest` • Jest • JaCoCo (cobertura) |
| Versão / CI / Containers | Git + GitHub • GitHub Actions • Docker + Docker Compose |

## Modelo de dados

```
AUTORES (1) ──── escreve ────< (N) LIVROS
id, nome, nacionalidade,          id, titulo, isbn, ano_publicacao,
data_nascimento, criado_em        genero, autor_id (FK), criado_em
```

## Estrutura de pastas

```
biblioteca-web/
├── backend/
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/
│       ├── main/java/com/biblioteca/
│       │   ├── model/        (Autor, Livro)
│       │   ├── repository/   (AutorRepository, LivroRepository)
│       │   └── controller/   (AutorController, LivroController)
│       └── test/java/com/biblioteca/
│           ├── controller/   (AutorControllerTest, LivroControllerTest)
│           └── IntegracaoTest.java
├── frontend/
│   ├── package.json, Dockerfile, nginx.conf, public/
│   └── src/
│       ├── components/  (AutorList, AutorForm, LivroList, LivroForm)
│       └── services/    (api.js)
├── docker-compose.yml
├── .github/workflows/ci-cd.yml
└── README.md
```

## Endpoints da API

| Verbo | URL | Descrição |
|---|---|---|
| POST | `/api/autores` | Cria um autor (201) |
| GET | `/api/autores` | Lista autores ordenados por nome |
| GET | `/api/autores/{id}` | Busca um autor (200/404) |
| PUT | `/api/autores/{id}` | Atualiza um autor (200/404) |
| DELETE | `/api/autores/{id}` | Remove um autor (200/404; 409 se tiver livros) |
| POST | `/api/livros` | Cria um livro vinculado a um autor (201) |
| GET | `/api/livros` | Lista livros ordenados por título |
| GET | `/api/livros/{id}` | Busca um livro (200/404) |
| PUT | `/api/livros/{id}` | Atualiza um livro, inclusive o autor (200/404) |
| DELETE | `/api/livros/{id}` | Remove um livro (200/404) |
| GET | `/api/livros/autor/{autorId}` | Lista os livros de um autor |

Exemplo para criar um livro:

```json
{ "titulo": "Dom Casmurro", "isbn": "978-8535910667", "anoPublicacao": 1899,
  "genero": "Romance", "autor": { "id": 1 } }
```

Swagger: http://localhost:8080/swagger-ui.html

## Como executar

### Com Docker (tudo de uma vez)

```bash
docker-compose up -d --build
```

- Frontend: http://localhost:3000
- Backend: http://localhost:8080/api
- PostgreSQL: localhost:5432 (banco `biblioteca_db`, usuário/senha `postgres`/`postgres`)

### Em modo desenvolvimento

```bash
# 1. Banco
docker-compose up -d postgres

# 2. Backend
cd backend
mvn spring-boot:run

# 3. Frontend (outro terminal)
cd frontend
npm install
npm start
```

## Como rodar os testes

```bash
# Backend – testes unitários dos controllers
cd backend
mvn test -Dtest="*ControllerTest"

# Backend – todos (unitários + integração com H2) + cobertura JaCoCo
mvn test
# relatório: backend/target/site/jacoco/index.html

# Frontend
cd frontend
npm test -- --watchAll=false --passWithNoTests
```

### Teste de aceitação com curl

```bash
curl -X POST http://localhost:8080/api/autores -H "Content-Type: application/json" \
  -d '{"nome":"Machado de Assis","nacionalidade":"Brasileira","dataNascimento":"1839-06-21"}'

curl -X POST http://localhost:8080/api/livros -H "Content-Type: application/json" \
  -d '{"titulo":"Dom Casmurro","anoPublicacao":1899,"genero":"Romance","autor":{"id":1}}'
```

## CI/CD

O workflow `.github/workflows/ci-cd.yml` roda em todo `push` e `pull_request` na `main`:

- **test-backend**: Java 17 + testes unitários, integração (`@SpringBootTest`) e relatório JaCoCo
- **test-frontend**: Node 20 + `npm ci`, `npm test`, `npm run build`
- **deploy** (extra, só em push): `docker compose up --build` + testes de aceitação com curl