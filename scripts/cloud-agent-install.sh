#!/usr/bin/env bash
set -euo pipefail

ANDROID_SDK_ROOT=/opt/android-sdk
CMD_TOOLS_URL="https://dl.google.com/android/repository/commandlinetools-linux-15859902_latest.zip"

if [[ ! -x "${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin/sdkmanager" ]]; then
  tmp="$(mktemp -d)"
  curl -fL --retry 3 -o "${tmp}/cmdtools.zip" "${CMD_TOOLS_URL}"
  unzip -q "${tmp}/cmdtools.zip" -d "${tmp}"
  sudo mkdir -p "${ANDROID_SDK_ROOT}/cmdline-tools"
  sudo rm -rf "${ANDROID_SDK_ROOT}/cmdline-tools/latest"
  sudo mv "${tmp}/cmdline-tools" "${ANDROID_SDK_ROOT}/cmdline-tools/latest"
  sudo chown -R "$(id -un):$(id -gn)" "${ANDROID_SDK_ROOT}"
  rm -rf "${tmp}"
fi

export ANDROID_HOME="${ANDROID_SDK_ROOT}"
export ANDROID_SDK_ROOT
export CHROME_BIN="${CHROME_BIN:-/usr/bin/google-chrome}"

yes | "${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin/sdkmanager" --sdk_root="${ANDROID_SDK_ROOT}" --licenses >/dev/null || true
"${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin/sdkmanager" --sdk_root="${ANDROID_SDK_ROOT}" \
  "platforms;android-36" \
  "build-tools;36.0.0" \
  "platform-tools"

printf 'sdk.dir=%s\n' "${ANDROID_SDK_ROOT}" > local.properties

chmod +x ./gradlew
./gradlew --no-daemon :composeApp:assembleDebug :composeApp:testDebugUnitTest :composeApp:wasmJsBrowserTest :composeApp:jsBrowserTest
