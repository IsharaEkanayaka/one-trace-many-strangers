#!/usr/bin/env bash
# Sends two unrelated requests carrying the same traceparent through the gateway,
# once with the headers passed through and once with them stripped.
# Requires: docker compose, curl, jq, openssl.
set -euo pipefail
cd "$(dirname "$0")/.."

JAEGER=http://localhost:16686
GATEWAY=http://localhost:8081

count_traces() {
  curl -s "$JAEGER/api/traces?service=demo-backend&lookback=1h&limit=100" | jq '.data | length'
}

run() {
  local mode=$1
  echo
  echo "=== gateway mode: $mode ==="
  GATEWAY_MODE=$mode docker compose up -d --force-recreate gateway >/dev/null
  sleep 2

  local trace_id; trace_id=$(openssl rand -hex 16)
  local tp="00-${trace_id}-00f067aa0ba902b7-01"
  local before; before=$(count_traces)

  curl -s -H "traceparent: $tp" "$GATEWAY/status/123" >/dev/null
  curl -s -H "traceparent: $tp" "$GATEWAY/status/456" >/dev/null
  sleep 4   # let the agent, collector and Jaeger catch up

  local after; after=$(count_traces)
  local joined
  joined=$(curl -s "$JAEGER/api/traces/$trace_id" | jq '[.data[0].spans // [] | .[] | select(.references | length > 0)] | length')

  echo "caller's trace ID:               $trace_id"
  echo "server spans under that trace:   $joined"
  echo "new traces created by 2 calls:   $((after - before))"
}

docker compose up -d --build
sleep 5
run passthrough
run strip
echo
echo "Open $JAEGER to inspect the traces."
