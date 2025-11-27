#!/usr/bin/env sh

set -eu

if ! mkdir -p run build/palette; then
  echo "Error: Failed to create required directories" >&2
  exit 1
fi

if ! printf 'eula=true\n' > run/eula.txt; then
  echo "Error: Failed to accept EULA" >&2
  exit 1
fi

echo "Starting Minecraft server"
./gradlew --console plain --no-daemon runServer --info --stacktrace

if [ $? -ne 0 ]; then
  echo "Error: Could not start server" >&2
  exit 1
fi

echo "Copying generated files"

if ! cp run/palette/colors.csv run/palette/blocks.csv build/palette/; then
  echo "Error: Failed to copy generated files" >&2
  exit 1
fi

echo "Done"
