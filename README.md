# One trace, many strangers

Demo for the blog post *One trace, many strangers: when trace context crosses a trust boundary*.

It shows what happens when an outside caller sends the same `traceparent` on
unrelated requests, and how stripping trace headers at the gateway fixes it.

## Run it

```bash
./scripts/reproduce.sh
```

Needs Docker Compose, `curl`, `jq` and `openssl`. The first run builds the app.

## Expected output

| Gateway mode  | Server spans under caller's trace | New traces created |
|---------------|-----------------------------------|--------------------|
| `passthrough` | 2                                 | 1                  |
| `strip`       | 0                                 | 2                  |

With `passthrough`, both requests join the caller's trace. With `strip`, each
request starts its own trace.

## Try it by hand

```bash
docker compose up -d --build                     # passthrough
TP="00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01"
curl -H "traceparent: $TP" http://localhost:8081/status/123
curl -H "traceparent: $TP" http://localhost:8081/orders/456
# open http://localhost:16686 and look up trace 4bf92f3577b34da6a3ce929d0e0e4736

GATEWAY_MODE=strip docker compose up -d --force-recreate gateway
# repeat the curls: two separate traces
```

## Layout

- `app/`: single-file Java backend, run with the OpenTelemetry Java agent
- `gateway/`: two nginx configs, one passes headers through and one strips them
- `collector/`: OpenTelemetry Collector forwarding to Jaeger
- `scripts/reproduce.sh`: runs both modes and prints the result

## Versions

Java agent 2.30.0 (`OTEL_AGENT_VERSION` build arg), Jaeger 2.4.0, Collector
0.119.0, nginx 1.27, JDK 21.

MIT licensed.
