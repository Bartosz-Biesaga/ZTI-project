# HireMe — Deployment

## Deploy

```bash
git clone https://github.com/Bartosz-Biesaga/ZTI-project.git hireme
cd hireme
cp .env.example .env   # optional: adjust credentials and port
docker compose up -d --build
```

## Verify

```bash
docker compose ps
docker compose logs -f api
curl http://localhost:8080/actuator/health
```

Expected health response: `{"status":"UP"}`.

Flyway applies migrations on first startup; check API logs for `Successfully applied` or `Schema is up to date`.