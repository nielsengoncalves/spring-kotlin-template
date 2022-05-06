service-up-compose = docker-compose -f docker/base.yml

clean:
	./gradlew clean

test: clean
	./gradlew test

build: clean
	./gradlew bootJar

app.start: build
	$(service-up-compose) build
	$(service-up-compose) up -d

app.down:
	$(service-up-compose) down

db.up:
	$(service-up-compose) up spring-kotlin-template-db -d

db.down:
	$(service-up-compose) down spring-kotlin-template-db

db.wait:
	until docker inspect --format "{{json .State.Health }}" spring-kotlin-template-db | grep -m 1 "\"Status\":\"healthy\""; do echo "Waiting for postgresql initialization" && sleep 1 ; done

db.migrate:
	./gradlew flywayMigrate

jooq.run:
	./gradlew generateJooq

jooq.generate: db.up db.wait db.migrate jooq.run db.down
