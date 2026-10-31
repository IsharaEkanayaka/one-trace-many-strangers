# one-trace-many-strangers

Minimal reproduction scaffold for forwarding a `traceparent` header from an NGINX gateway to a Go backend.

## Project layout

```text
.
├── docker-compose.yml
├── gateway/
│   └── nginx.conf
├── backend/
│   ├── main.go
│   └── go.mod
├── collector/
│   └── config.yaml
├── scripts/
│   └── reproduce.sh
└── README.md
```

## Run

```bash
docker compose up
./scripts/reproduce.sh
```
