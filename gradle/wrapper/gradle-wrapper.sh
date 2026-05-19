#!/usr/bin/env bash
# Convenience wrapper — just forwards to gradlew
exec "$(dirname "$0")/../gradlew" "$@"
