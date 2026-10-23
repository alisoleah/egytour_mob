#!/usr/bin/env bash
# EgyTour local dev launcher.
# Usage: scripts/dev.sh [backend|android|ios|all]   (default: all)
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
SDK="${ANDROID_HOME:-$HOME/Library/Android/sdk}"
ADB="$SDK/platform-tools/adb"
EMULATOR="$SDK/emulator/emulator"
AVD="${AVD:-Pixel_3a_API_32_arm64-v8a}"
export JAVA_HOME="${JAVA_HOME:-/Applications/Android Studio.app/Contents/jbr/Contents/Home}"
export DEVELOPER_DIR="${DEVELOPER_DIR:-/Applications/Xcode.app/Contents/Developer}"
TARGET="${1:-all}"
LOG_DIR="$ROOT/.dev-logs"; mkdir -p "$LOG_DIR"

say() { printf "\n\033[1;33m▶ %s\033[0m\n" "$*"; }

start_backend() {
  say "Docker / PostGIS"
  if ! docker info >/dev/null 2>&1; then
    open -a Docker
    printf "Waiting for Docker"
    until docker info >/dev/null 2>&1; do printf "."; sleep 2; done; echo
  fi
  (cd "$ROOT/backend" && docker compose up -d)

  say "Migrations + seed (idempotent)"
  (cd "$ROOT/backend" && { [ -d node_modules ] || npm install; } && npm run db:migrate 2>&1 | grep -E "✅|rror")
  # Seed only when the DB is empty so POIs aren't duplicated.
  COUNT=$(docker exec egytour-postgres psql -U postgres -d egytour -tAc \
    "SELECT count(*) FROM points_of_interest" 2>/dev/null || echo 0)
  if [ "${COUNT:-0}" = "0" ]; then (cd "$ROOT/backend" && npm run db:seed); fi

  say "Backend API → http://localhost:3000"
  if curl -sf localhost:3000/health >/dev/null; then
    echo "already running"
  else
    (cd "$ROOT/backend" && nohup npm run dev >"$LOG_DIR/backend.log" 2>&1 &)
    until curl -sf localhost:3000/health >/dev/null; do sleep 1; done
    echo "up (logs: .dev-logs/backend.log)"
  fi
}

start_android() {
  say "Android emulator ($AVD)"
  if ! "$ADB" devices | grep -q "emulator-"; then
    nohup "$EMULATOR" -avd "$AVD" >"$LOG_DIR/emulator.log" 2>&1 &
    "$ADB" wait-for-device
    until [ "$("$ADB" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" = "1" ]; do sleep 2; done
  fi
  say "Build + install + launch"
  (cd "$ROOT/android" && ./gradlew installDebug)
  "$ADB" shell am start -n com.egytour.app/.MainActivity
}

start_ios() {
  say "iOS simulator"
  if ! xcodebuild -version >/dev/null 2>&1 || ! xcodebuild -checkFirstLaunchStatus >/dev/null 2>&1; then
    echo "Xcode not ready. Run once:  sudo xcodebuild -license accept && xcodebuild -runFirstLaunch"; return 1
  fi
  SIM="${SIM:-iPhone 15}"
  xcrun simctl boot "$SIM" 2>/dev/null || true
  open -a Simulator
  (cd "$ROOT/ios/EgyTour" && xcodebuild -project EgyTour.xcodeproj -scheme EgyTour \
      -destination "platform=iOS Simulator,name=$SIM" -derivedDataPath build CODE_SIGNING_ALLOWED=NO build)
  APP=$(find "$ROOT/ios/EgyTour/build" -name "EgyTour.app" -path "*iphonesimulator*" | head -1)
  xcrun simctl install booted "$APP"
  BID=$(/usr/libexec/PlistBuddy -c "Print CFBundleIdentifier" "$APP/Info.plist")
  xcrun simctl launch booted "$BID"
}

case "$TARGET" in
  backend) start_backend ;;
  android) start_backend; start_android ;;
  ios)     start_backend; start_ios ;;
  all)     start_backend; start_android; start_ios || echo "(iOS skipped)" ;;
  *) echo "usage: $0 [backend|android|ios|all]"; exit 1 ;;
esac
say "Done"
