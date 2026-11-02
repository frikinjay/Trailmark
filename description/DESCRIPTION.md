![Trailmark banner. On the left, the Trailmark icon (a green folded map with a gold pin), the name TRAILMARK in large blocky white letters, a short gold rule, the line "A quest book you can restyle from top to bottom, with 3D icons and built-in sound and video." and two tags, Fabric and NeoForge. On the right, a screenshot of the quest book in its default dark teal theme, open on the Welcome chapter: chapter tabs along the top, a chapter list with progress on the left, a map of linked quests in the middle, and an open quest page on the right with formatted text, a picture, a quote, a list, a small table of keys and a gold Claim Rewards button.](https://raw.githubusercontent.com/frikinjay/Trailmark/main/description/hero.png)

> **Trailmark** is a quest book for Fabric and NeoForge. Chapters are laid out as maps, quest pages hold proper formatted text, and the book's whole look comes from stylesheets that packs, servers and players can each replace.

| [**Documentation**](https://github.com/frikinjay/Trailmark/blob/main/DOCUMENTATION.md) | [**Example quest pack**](https://github.com/frikinjay/Trailmark/tree/main/example-pack) | [**Developer API**](https://github.com/frikinjay/Trailmark/tree/main/src/main/java/com/frikinjay/trailmark/api) |
|:---:|:---:|:---:|
| Every file and field, explained | A full, playable book to learn from | Custom tasks, rewards and events |

---

![01, Icons: "Whole scenes as icons". Text: "A quest icon can be a full structure, a mob mid-animation, a player in any skin and pose, a fluid, a video or a sound." Left screenshot, captioned "Map: structures on the map, an ender dragon as an icon", shows the Into the World chapter, with small 3D structures beside its quests and an animated ender dragon as the icon of the quest The End. Right screenshot, captioned "Icon editor: any icon, with a live preview", shows the icon editor set to a player icon, with fields for name, skin, arms, scale, camera and animation beside a 3D preview of the player.](https://raw.githubusercontent.com/frikinjay/Trailmark/main/description/icons.png)

Icons are drawn by the game's own renderers, so modded blocks and mobs look exactly as they do in the world, and GeckoLib models can play any of their animations. Blocks keep their states and block entity data, a player icon can wear the skin of whoever opens the book, and modded fluids work even without a bucket.

---

![02, Sound and video: "Players on the page". Text: "Quests carry their own sound or video player, chapters can have music, and YouTube plays in place." Left screenshot, captioned "Quest: play, stop and seek without leaving the quest", shows the Sights and Sounds chapter with the quest Press Play open, its play and stop buttons, seek bar and volume slider above the description. Right screenshot, captioned "Map: chapter music and a locked video", is a close-up of that chapter's map: a chapter music button, a row of quests, a tilted gold label and a locked video thumbnail labelled "Unlocks after Fluids".](https://raw.githubusercontent.com/frikinjay/Trailmark/main/description/media.png)

Videos open in a player with full screen and keyboard controls, or play right on the map. Sounds can be any sound ID, a file in the pack, or a link. A server sends media only when a quest that shows it is opened, at a capped speed behind game traffic, and players keep it cached so it downloads once.

---

![03, Themes: "Restyle all of it". Text: "One CSS file styles the whole book, from colours and fonts to quest shapes. Below, the chapter from above in a vanilla theme." Left screenshot, captioned "Vanilla theme: slot-style quests in Minecraft's colours", shows the same Sights and Sounds chapter restyled with grey stone panels, square inventory-slot quests, yellow headings and green buttons. Right screenshot, captioned "Quest page: text styles follow the theme", is a close-up of the Welcome quest page in that theme, with a yellow "Welcome, traveller!" heading, a yellow-framed icon and coloured sample text.](https://raw.githubusercontent.com/frikinjay/Trailmark/main/description/themes.png)

| What you can style | How |
|---|---|
| Every screen, the HUD tracker, toasts and pop-ups | A `style.css` with selectors for every part, plus states like `:hover`, `:locked` and `:claimable` |
| Motion | Transitions, keyframe animations, gradients and shadows, and `@media` rules for small screens |
| Single quests or whole chapters | Give them a `style` or tags and target them as classes |
| Fonts | Your own, from a resource pack or the config folder, for any element |
| Where themes come from | Resource packs, the quest pack (servers send theirs on join), and each player's own `style.css` |
| Players who'd rather keep their look | **Prefer My Style** puts their theme above the server's |

The vanilla theme above ships with the [example pack](https://github.com/frikinjay/Trailmark/tree/main/example-pack), so you can start from it.

---

![04, For pack makers: "Edit while you play". Text: "Lay out the map in game. JSON files reload as you save, and FTB Quests books import with their progress." Left screenshot, captioned "Edit mode: right-click the map to add anything", shows the book in edit mode with the right-click menu open, offering a new quest, label, picture, video or sound, or editing the chapter. Right screenshot, captioned "Settings: book-wide options in one form", shows the Book Settings form with the pack title, icon, tabs, starting chapter and reward options.](https://raw.githubusercontent.com/frikinjay/Trailmark/main/description/editor.png)

- **On the map:** drag, link, resize and box-select, nudge with the arrow keys, and undo or redo. Editing is for operators by default, with a configurable permission level.
- **Templates:** save a group of quests and stamp it anywhere. Templates are plain files, so they're easy to share.
- **Decorations:** labels, pictures, videos, sounds and 3D icons, attached to quests, layered, tilted, or locked until a quest is done.
- **Safe to tinker:** every save keeps a backup, and a quest that fails to load is reported by `/trailmark problems`, never deleted.
- **Translations:** export a language file with one command, or edit text in game for yourself or the whole server.
- **Moving over:** `/trailmark import ftbquests` converts an FTB Quests book along with its team progress, and lists anything it couldn't match.

---

## Quests

| | |
|---|---|
| **Tasks** | 21 built in: items, crafting, mining, placing, using, kills, interacting, observing, stats, advancements, locations, biomes, structures, dimensions, fluids, XP, check marks, custom signals, counters, stages and player data |
| **Rewards** | Items, pick-one choices, random rolls, loot tables, XP, commands, advancements, stages, counters, effects, recipes and toasts, claimed per player or per team |
| **Unlocking** | Needs all, any or a number of earlier quests. Quests can stay hidden until unlocked, or be secret |
| **Flow** | Repeat instantly, daily, weekly or on a cooldown, run against a timer, lock out other branches, or sit in chapters that open on set dates or weekdays |
| **Look** | Eight quest shapes, milestones, and connection lines per quest or per link |
| **Text** | Markdown, HTML, tables, call-outs, spoilers and colour codes, with items, blocks, mobs, structures and players shown inline |
| **Links** | To other quests, chapters, Patchouli entries and websites |
| **Getting around** | A HUD tracker with a compass arrow for places to visit, toasts, and a quest book tab on the inventory and advancements screens |
| **Teams** | Trailmark's own parties, or FTB Teams |

## Works with

| | |
|---|---|
| **Recipes** | JEI · REI · EMI (press R or U over any item in the book) |
| **Maps** | JourneyMap · Xaero's Minimap (tracked locations show as waypoints) |
| **Guides** | Patchouli |
| **Teams and claims** | FTB Teams · FTB Chunks (chunk tasks and rewards) |
| **Accessories** | Accessories · Curios · Trinkets (worn items count, rewards go into slots) |
| **Storage** | Backpacks and shulker boxes · Refined Storage · Applied Energistics 2 |
| **Machines** | Create · Mekanism · any mod that stores Forge Energy (NeoForge) |
| **Scripting and NPCs** | KubeJS (NeoForge) · NPC mods like EasyNPC, through `/trailmark signal` |
| **More** | Cobblemon · GeckoLib |

All of these are optional. A quest that needs a missing mod shows as unavailable until it's installed.

## Good to know

| | |
|---|---|
| **Install on** | Client and server |
| **Needs** | Fabric API on Fabric. Dungeon Config is bundled |
| **Open the book** | Press **I**, click the quest book tab in your inventory, or use the quest book item |
| **On servers** | The book syncs file by file and players only download what changed |
| **Video and MP3** | Uses FFmpeg. If it's missing, Trailmark offers to download it |
| **First time?** | Grab the [example quest pack](https://github.com/frikinjay/Trailmark/tree/main/example-pack) and change what you like |
