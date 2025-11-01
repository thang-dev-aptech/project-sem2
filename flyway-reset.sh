#!/bin/bash
# Flyway Reset - Clean + Migrate (quick reset)
cd "$(dirname "$0")"
./flyway-clean.sh && ./flyway-migrate.sh

