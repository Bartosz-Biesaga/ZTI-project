# HireMe — Wdrożenie

## Uruchomienie

```bash
git clone https://github.com/Bartosz-Biesaga/ZTI-project.git hireme
cd hireme
docker compose up -d --build
```

## Sprawdzenie

Otwórz w przeglądarce: `http://localhost`

Stan API:

```bash
curl http://localhost:8080/actuator/health
```

Oczekiwana odpowiedź: `{"status":"UP"}`

## Domyślne konto administratora

- E-mail: `admin@hireme.local`
- Hasło: `admin123`

## Jak przetestować

1. Wejdź na `/register` i utwórz konto
2. Po rejestracji powinna pojawić się strona główna
3. Kliknij „Wyloguj się”, a potem zaloguj ponownie na `/login`

## Tryb deweloperski (hot reload frontendu)

```bash
docker compose down
docker compose -f docker-compose.yml -f docker-compose.dev.yml up --build
```

Otwórz w przeglądarce: `http://localhost:5173`

Jeśli pojawi się błąd „port is already allocated”, zatrzymaj poprzedni stos (`docker compose down`) albo inny proces na porcie 80/5173.

Zmiany w `frontend/src/` są widoczne od razu w przeglądarce (bez `docker compose build`). Produkcja i ocena na VM nadal używają zwykłego `docker compose up --build` na porcie 80.