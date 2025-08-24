Docker usage

From project root (where docker-compose.yml lives):

1) Build and start everything:
   docker compose up --build -d

2) Check logs:
   docker compose logs -f back

3) Stop and remove:
   docker compose down

Notes:
- Backend will connect to MySQL at host `mysql` (service name in compose).
- MySQL root password is `root` and DB `electricity` will be created automatically.
- Front will be served on http://localhost:4200 and backend on http://localhost:8080

 
