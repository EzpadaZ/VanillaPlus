#!/bin/bash

BASE_URL="https://repo.aikar.co/nexus/content/groups/aikar/co/aikar/acf-paper/0.5.1-SNAPSHOT/"
LIB_DIR="libs"

mkdir -p "$LIB_DIR"

echo "📡 Fetching latest ACF-Paper JAR from $BASE_URL..."

# Find the latest main .jar file (not sources or hashes)
JAR_NAME=$(curl -s "$BASE_URL" | \
  grep -o 'acf-paper-0\.5\.1-[^"]\+\.jar' | \
  grep -vE 'sources|javadoc|\.sha1|\.md5' | \
  sort -Vr | head -n 1)

if [[ -z "$JAR_NAME" ]]; then
  echo "❌ Failed to find any JAR file."
  exit 1
fi

JAR_URL="${BASE_URL}${JAR_NAME}"
TEMP_FILE="${LIB_DIR}/${JAR_NAME}"
FINAL_FILE="${LIB_DIR}/acf-paper.jar"

echo "📥 Downloading: $JAR_URL → $FINAL_FILE"
curl -s -L -o "$TEMP_FILE" "$JAR_URL"

if [[ $? -eq 0 ]]; then
  mv -f "$TEMP_FILE" "$FINAL_FILE"
  echo "✅ Saved as: $FINAL_FILE"
else
  echo "❌ Download failed."
fi