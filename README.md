# Donut META

A **client-side, read-only** Fabric mod for comparing DonutSMP order prices with auction-house listings. It only starts on `donutsmp.net` or its subdomains, and it never sends buy, sell, inventory-click, or item-transfer actions.

## Features

- Mod Menu configuration, plus a `G` live results panel.
- Configurable `R` start, `F` stop, and `G` panel keybindings through Minecraft Controls.
- Configurable minimum profit (in millions), minimum/maximum pacing delay, jitter, and semicolon-separated item/enchantment queries.
- `/order <query>` and `/ah <query>` reads with tooltip parsing for prices such as `$ 335M` and order progress such as `0/8`.
- Read-only META flagging when AH sell price minus order buy price meets the configured threshold.
- No automatic trading. Delays rate-limit requests and are not intended to evade server moderation or rules.

## Build

This project targets Minecraft **1.21.11**, uses official Mojang mappings through Fabric Loom, Fabric API, Java 21, and a compile-only Mod Menu integration.

```bash
gradle build
```

The mod JAR is written to `build/libs/`. Install it alongside Fabric API; Mod Menu is optional but enables its Mods-screen configuration button.

## Release workflow

`.github/workflows/release.yml` builds every pull request and push, uploads the JAR as an artifact, and creates a GitHub Release with the built non-sources JAR when a tag named `v*` is pushed:

```bash
git tag v1.0.0
git push origin v1.0.0
```
