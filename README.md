# JimmyTools

[![Fabric Mod](https://img.shields.io/badge/modloader-fabric-99999?logoColor=%23E6E6FA&labelColor=gray&color=%23ccccff)](https://fabricmc.net/use/)
[![Latest Version](https://img.shields.io/modrinth/v/jimmytools?color=blueviolet&label=latest%20version)](https://modrinth.com/mod/jimmytools)
[![Docs](https://img.shields.io/badge/docs-wiki-blueviolet)](https://duelistheart.github.io/jimmytools/)

Quality-of-life and utility features for the **Lords of Minecraft 2 (LoM2)** server, as a client-side Fabric mod.

JimmyTools started out as a way to keep character transformations tidy, and has since grown to cover
bag hotkeys, a custom tab list and more.

## Features

| Feature | What it does |
|---|---|
| **Bag Hotkeys** | Open the inventory bags with a single key press (arrow keys by default). |
| **Transformations** | Swap between a character's regular and transformed skin with one key, for characters like mermaids, magical girls and vampires. |
| **Custom Tab List** | A scroll-styled tab list showing players, nearby characters and the current district. |
| **Level-Up Messages** | Level-up notices are also written to chat, so you can read them later. |
| **Startup Commands** | Run `/names` automatically when you join the server. |

Everything is configurable from the in-game config screen. See the [wiki](https://duelistheart.github.io/jimmytools/) for details on each feature.

## Installation

Requires Minecraft **26.2** and [Fabric Loader](https://fabricmc.net/use/) 0.19.5 or newer.

1. Download JimmyTools from [Modrinth](https://modrinth.com/mod/jimmytools).
2. Put the jar and the required mods below in your `mods` folder.
3. Launch the game and join the server.

JimmyTools is client-side only, so nothing needs to be installed on the server.

### Dependencies

**Required**

* [Fabric API](https://modrinth.com/mod/fabric-api)
* [owo-lib](https://modrinth.com/mod/owo-lib)
* [YetAnotherConfigLib](https://modrinth.com/mod/yacl)

**Recommended**

* [Mod Menu](https://modrinth.com/mod/modmenu) – adds a config button to the mods list

## Building

Requires JDK 25 or newer.

```bash
./gradlew build       # jar is written to build/libs/
./gradlew runClient   # development client
```

## Issues

Found a bug? Open an [issue](https://github.com/DuelistHeart/jimmytools/issues) and attach your `latest.log`.

## License

[CC0 1.0](LICENSE)
