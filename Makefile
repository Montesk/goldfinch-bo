#!/bin/bash

build:
	docker compose -f ./infrastructure/database/docker-compose.yml up

run:
	sbt run 9000