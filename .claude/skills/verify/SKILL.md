---
name: verify
description: Run the tiered build verification ladder (cheapest first) for Android, iOS, or both. Use before claiming any change works, before pushing, and after KMP source edits. Argument: android | ios | all (default all).
---

# Verify

Run checks cheapest-first and stop at the first failure — report it rather than continuing to more expensive steps. All commands run from the repo root.

## Android ladder

```bash
./gradlew :composeApp:compileDebugKotlinAndroid   # 1. source errors (~seconds warm)
./gradlew :composeApp:testDebugUnitTest           # 2. unit tests incl. commonTest + Konsist
./gradlew :composeApp:lintDebug                   # 3. Android Lint
./gradlew :composeApp:assembleDebug               # 4. full APK (only when packaging matters)
```

## iOS ladder

```bash
./gradlew :composeApp:compileKotlinIosSimulatorArm64        # 1. K/N source errors (~30s warm)
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64   # 2. framework link (~1m)
# 3. Full Xcode build — releases only, not per-change:
xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -configuration Debug \
  -destination 'platform=iOS Simulator,name=iPhone 16 Pro' build CODE_SIGNING_ALLOWED=NO
```

## Style (run for any non-trivial diff)

```bash
./gradlew ktlintCheck
./gradlew detekt
```

Fix style with `./gradlew ktlintFormat`. Never edit or regenerate the baselines (`composeApp/config/ktlint/baseline.xml`, `composeApp/detekt-baseline.xml`) to make a check pass.

## Guidance

- `all` (default): Android ladder steps 1–3, iOS steps 1–2, then style.
- Any edit in `commonMain`/`iosMain` requires the iOS ladder step 1 minimum; run step 2 before pushing KMP changes.
- Quote the failing output when reporting a failure; do not summarize it away.
