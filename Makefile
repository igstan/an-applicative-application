.PHONY: build run

VSN := $(shell git describe --always --dirty)

all: build

build:
	docker build -f Dockerfile -t meetup-034:$(VSN) .

run:
	docker run -it --rm meetup-034:$(VSN)
