#!/bin/bash
# Flyway Migrate - Apply all migrations
cd "$(dirname "$0")"
docker run --rm \
  -v "$PWD/db/migration:/flyway/sql" \
  flyway/flyway:10 \
  -url="jdbc:mysql://host.docker.internal:3306/gympro?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true" \
  -user="gympro_user" \
  -password="gympro_password" \
  -locations="filesystem:/flyway/sql" \
  -baselineOnMigrate=true \
  migrate

