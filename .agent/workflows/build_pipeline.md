---
description: Workflow to clean code, run unit tests, and build the APK
---

1. Run Spotless check/apply and Unit Tests
// turbo
2. Build Debug APK

```bash
./gradlew spotlessApply testDebugUnitTest assembleDebug
```
