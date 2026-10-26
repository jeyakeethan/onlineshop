# Local development

The application runs natively by default. MySQL is required; Redis and Kafka are optional and disabled by default.

## Native startup

Start MySQL and create `online_shopping_db`, then from the repository root run:

```powershell
$env:SPRING_DATASOURCE_URL = "jdbc:mysql://localhost:3306/online_shopping_db"
$env:SPRING_DATASOURCE_USERNAME = "root"
$env:SPRING_DATASOURCE_PASSWORD = "your-password"
./gradlew bootRun
```

In a second terminal, start the Next.js frontend:

```powershell
cd frontend
$env:BACKEND_BASE_URL = "http://localhost:8080"
pnpm install
pnpm dev
```

The backend runs on `http://localhost:8080` and the frontend on `http://localhost:3000`.

## Compose parity

Copy `.env.example` to `.env`, replace the placeholder passwords, and start the core stack:

```powershell
docker compose up --build
```

This starts MySQL, the Spring Boot backend, and the frontend. Redis and Kafka remain disabled. To enable those optional adapters:

```powershell
$env:APP_REDIS_ENABLED = "true"
$env:APP_KAFKA_ENABLED = "true"
docker compose --profile infra up --build
```

The database remains the source of truth when optional infrastructure is disabled. Do not commit `.env` or credentials.
