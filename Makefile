IMAGE_NAME := palette-generator
OUTPUT_DIR := $(shell pwd)/build/palette

.PHONY: all build run clean

all: run

build:
	docker build -t $(IMAGE_NAME) .

run: build
	mkdir -p $(OUTPUT_DIR)
	@echo "Running generator..."
	docker run --rm \
		-v "$(OUTPUT_DIR):/app/build/palette" \
		$(IMAGE_NAME)
	@echo "Success! Output files are in $(OUTPUT_DIR)"

clean:
	rm -rf build/
	docker rmi $(IMAGE_NAME) || true
