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
docker compose -f docker-compose.yml -f docker-compose.dev.yml up --build
```

Otwórz w przeglądarce: `http://localhost:5173`