# Trailmark Showcase Pack

A full quest book that shows off nearly everything Trailmark can do: rich quest text, every icon type, decorations,
music and video, repeatable and timed quests, branching paths, seasonal chapters, reward tables, styling and the
mod integrations. Every chapter is a real, playable quest line, so it doubles as a pile of copy-paste examples.

You're welcome to copy and adapt anything here for your own packs.

## Installing it

1. Copy the `quests` folder into `config/trailmark/` of your instance (or your server), so you end up with
   `config/trailmark/quests/book.json`. Back up any quests you already have first; this replaces them.
2. Start the game and press **N**. If the game was already running, the book reloads on its own within a few seconds.

Optional extras in [`extras/`](extras):

- `trailmark-showcase-datapack.zip`: a Patchouli guide book and a Curios ring slot used by two quests. Add it as a
  data pack when you create the world.
- `trailmark-showcase-resourcepack`: the text for that Patchouli book. Put it in `resourcepacks` and turn it on.

## What's inside

| Tab | Chapter | Shows off |
|---|---|---|
| Story | Welcome | The quest book itself: rich text, tables, embedded items, mobs, blocks and players, YouTube, links, decorations |
| Story | First Steps | Item, craft, mine and kill tasks, sequential tasks, branching paths, daily and cooldown repeats, a timed quest, a secret quest |
| Story | Sights and Sounds | Every icon type (sound, fluid, structure, posed player, animated mob, item components, block data), quest audio and video players, chapter music, a locked video, tilted labels |
| World | Into the World | Biome, structure, dimension and location tasks, structure decorations |
| World | Builder's Guild | Building and placing |
| World | Community | Custom signals, stages and team counters |
| Events | Autumn Harvest, Winter Festival, Weekend Challenge | Seasonal chapters that open on their own dates or days |
| Mods | Recipe Viewers, Maps and Waypoints, Storage Networks, Accessories, Cobblemon, FTB Teams and Chunks, Create, Mekanism | The integrations |

Recipe Viewers and Maps and Waypoints load without their mods; their recipe keys and waypoints just stay quiet
until one is installed. The other mod chapters use tasks, rewards or items from their mods. Without the mod, those
tasks and rewards show as unavailable (nothing breaks, and they come back once the mod is installed). Delete the
chapters you don't need, or keep them for later:

| Chapter | Needs |
|---|---|
| Storage Networks | Refined Storage (Applied Energistics 2 quests are added on NeoForge) |
| Accessories | Curios (NeoForge) or Trinkets (Fabric) |
| Cobblemon | Cobblemon |
| FTB Teams and Chunks | FTB Teams and FTB Chunks |
| Create | Create (NeoForge) |
| Mekanism | Mekanism (NeoForge) |

Some media plays from the internet (a YouTube trailer, a background picture and a music track in the Welcome
chapter). Videos and MP3 music need FFmpeg; when it's missing, the quest book offers to download it for you.

## Where to look

- `book.json`: the book's title, tabs (chapter groups), claiming and map settings.
- `chapters/`: one file per chapter.
- `tables/`: reward tables used by the loot rewards.
- `lang/en_us.json`: translatable text used through `{lang:...}`.
- `media/`: pictures and animations the quests use.
- `style.css`: the pack's own look on top of Trailmark's theme.

Every field is explained in [`DOCUMENTATION.md`](../DOCUMENTATION.md).
