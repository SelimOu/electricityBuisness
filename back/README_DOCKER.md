Docker notes:
- Build and run with docker compose from project root:
  docker compose up --build -d
- MySQL credentials are root/root and DB name is `electricity`.
- The backend expects MySQL at host `mysql` (docker-compose service name).
