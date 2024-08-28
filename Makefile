default:
	$(info Make target options:)
	$(info `make start` to run the ODE)
	$(info `make build` to build the ODE)
	$(info `make stop` to stop the ODE)
	$(info `make delete` to stop the ODE and remove the volumes)
	$(info `make rebuild` to stop, delete, and then rebuild the containers)
	$(info `make clean-build` to rebuild the containers without using the cache)

start:
ifeq ("$(wildcard .env)", "")
	$(error "ERROR: jpo-ode Environment file `.env` not found in ${PWD}")
endif
ifeq ("$(wildcard ./jpo-utils/.env)", "")
	$(error "ERROR: jpo-utils Environment file `.env` not found in ${PWD}")
endif
	docker compose up -d

build:
	docker compose build

stop:
	docker compose down

delete:
	docker compose down -v

rebuild:
	$(MAKE) stop delete build start

clean-build:
	docker compose build --no-cache