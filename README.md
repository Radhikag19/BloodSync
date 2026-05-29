# BloodSync

BloodSync is the Android client app for the BloodSync project. This repository contains the app source, Gradle configuration, and related project files.

## Repository layout

- `app/` — Android app module (source, resources, manifests).
- `gradle/` — dependency version catalog and Gradle wrapper config.
- `build.gradle.kts`, `settings.gradle.kts` — top-level build configuration.

## Key points

- Production secrets and private keys are not included in this repository.
- Firebase configuration such as `app/google-services.json` and signing keystores are intentionally excluded and should be distributed securely to maintainers only.

