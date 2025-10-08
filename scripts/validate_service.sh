#!/usr/bin/env bash
set -euo pipefail

# 헬스체크(필요 시 엔드포인트 변경)
# 127.0.0.1:8080/actuator/health 기준
for i in {1..20}; do
  if curl -sf http://127.0.0.1:8080/actuator/health | grep -q '"status":"UP"'; then
    exit 0
  fi
  sleep 3
done

echo "Health check failed"
exit 1
