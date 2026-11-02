# Trailmark

Quest books for Minecraft on Fabric and NeoForge. Chapters on a map you can pan and zoom, quests with real
3D icons, rich text, pictures, videos and music, a HUD tracker, team progress, and an editor that does everything
in game.

This repository is the public side of Trailmark. The mod itself is on
[CurseForge](https://www.curseforge.com/projects/1773243) and [Modrinth](https://modrinth.com/mod/vuUW6QpR).

| Folder | What's in it |
|---|---|
| [`DOCUMENTATION.md`](DOCUMENTATION.md) | The full guide: quest files, every task and reward, quest text, styling, media, integrations, commands, settings and the developer API |
| [`example-pack/`](example-pack) | A complete example quest book that uses nearly every feature. Drop it in and play it, or open the files next to the docs |
| [`src/main/java/com/frikinjay/trailmark/api`](src/main/java/com/frikinjay/trailmark/api) | The public API, for mods that want to add tasks, rewards, signals, teams or stages, or react to quest events |

## For players and pack makers

Quests live in `config/trailmark/quests` on the server (or in your singleplayer instance). Trailmark doesn't ship a
book of its own, so a fresh install starts empty. Operators can build one entirely in game: open the book with
**N**, then **Options > Edit Quests**. Prefer files? Everything is plain JSON, and the folder reloads by itself
while the game runs.

Coming from FTB Quests? `/trailmark import ftbquests` converts your book and your players' progress.

The [example pack](example-pack) is the quickest way to see what's possible. Its
[README](example-pack/README.md) explains how to install it.

## For mod developers

Everything you're meant to touch lives in `com.frikinjay.trailmark.api`. Depend on Trailmark as `compileOnly` and
only call into it when the mod is loaded. Trailmark isn't on a Maven repository of its own, so the easiest route is
Modrinth's, using the version ID from the file's page on Modrinth:

```gradle
repositories {
    exclusiveContent {
        forRepository { maven { url = "https://api.modrinth.com/maven" } }
        filter { includeGroup "maven.modrinth" }
    }
}

dependencies {
    modCompileOnly "maven.modrinth:vuUW6QpR:<version id>"
}
```

A taste of the API:

```java
TrailmarkApi.fireCustom(player, "found_ruins", 1);
TrailmarkApi.setStage(player, "nether_unlocked", true);
TrailmarkEvents.QUEST_COMPLETED.register((server, team, quest, player) -> rewardTheTeam(team));
TrailmarkApi.registerTaskType(MyTask.TYPE);
```

[`DOCUMENTATION.md`](DOCUMENTATION.md#for-developers) walks through the facade, events, custom task and reward
types, item sources, team and stage providers, and the client extension points.

The sources here are for reading and compiling against. They reference a few internal classes, so they don't build
on their own; the released mod jar has everything.

## License

Trailmark is All Rights Reserved; see [`LICENSE`](LICENSE). Mods may depend on and call the public API without
bundling or modifying Trailmark, and modpacks on CurseForge and Modrinth may include the unmodified mod file.
