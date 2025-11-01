#!/bin/bash
# Flyway Clean - Reset database schema
cd "$(dirname "$0")"
docker run --rm \
  -v "$PWD/db/migration:/flyway/sql" \
  flyway/flyway:10 \
  -cleanDisabled=false \
  -url="jdbc:mysql://host.docker.internal:3306/gympro?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true" \
  -user="gympro_user" \
  -password="gympro_password" \
  -locations="filesystem:/flyway/sql" \
  clean

