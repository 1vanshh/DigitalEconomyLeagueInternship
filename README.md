# Backend

## Run database locally

1. Install PostgreSQL locally.

2. Create a database, for example:
   `createdb league_backend`

3. Copy environment file:
   `cp .env.example .env`

4. Update database settings in `.env`:
   - `DB_URL`
   - `DB_USERNAME`
   - `DB_PASSWORD`
   - `SERVER_PORT`

5. Make sure PostgreSQL is running on the host and port specified in `.env`.

Change the database url, user, password in `.env` before starting.

## Run backend service

1. Go to the project folder:
   `cd Backend`

2. Run the application:
   `mvn spring-boot:run`

3. Backend will be available at:
   `http://localhost:8080`

## API endpoints

- Create client  
  `POST /api/clients`

- Get all clients  
  `GET /api/clients`

- Get client by id  
  `GET /api/clients/{id}`

- Update client  
  `PUT /api/clients/{id}`

- Delete client  
  `DELETE /api/clients/{id}`

## Run tests

1. Run all tests:
   `mvn test`
