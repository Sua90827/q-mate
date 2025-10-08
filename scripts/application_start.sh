#!/usr/bin/env bash
set -euo pipefail

# 유닛 없으면 생성
if [ ! -f /etc/systemd/system/qmate.service ]; then
  cat >/etc/systemd/system/qmate.service <<'UNIT'
[Unit]
Description=QMate Spring Boot Service
After=network.target

[Service]
User=ec2-user
WorkingDirectory=/opt/qmate/release
ExecStart=/usr/bin/env java -jar /opt/qmate/release/app.jar --spring.profiles.active=prod
Restart=always
RestartSec=5
SuccessExitStatus=143

[Install]
WantedBy=multi-user.target
UNIT
fi

# systemd 적용 및 재시작
systemctl daemon-reload
systemctl enable qmate.service || true
systemctl restart qmate.service
