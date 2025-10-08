#!/usr/bin/env bash
set -euo pipefail

# 이전 잔여 파일 정리(옵션)
find /opt/qmate/release -maxdepth 1 -name "*.jar" -type f -mtime +7 -exec rm -f {} \; || true

# 권한 보정
chown -R ec2-user:ec2-user /opt/qmate/release
