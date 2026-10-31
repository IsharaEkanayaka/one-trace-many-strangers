#!/usr/bin/env bash
set -euo pipefail

trace_id="0123456789abcdef0123456789abcdef"
span_id="0123456789abcdef"

curl -sS \
  -H "traceparent: 00-${trace_id}-${span_id}-01" \
  "${1:-http://localhost:8080/}"
