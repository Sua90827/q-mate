#!/usr/bin/env bash
set -euo pipefail

# 기존 프로세스가 systemd로 관리된다고 가정
systemctl daemon-reload || true
systemctl enable qmate.service || true
systemctl restart qmate.service
