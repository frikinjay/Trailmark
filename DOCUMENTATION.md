# Trailmark Documentation

Trailmark is a quest mod for Minecraft on Fabric and NeoForge. Quests live in plain JSON files, the quest screen is fully themeable with CSS, and quest text supports Markdown, HTML, pictures, GIFs and videos. This guide covers everything a pack maker needs, followed by a section for mod developers.

- [Getting Started](#getting-started)
- [The Quest Folder](#the-quest-folder)
- [Chapters and Quests](#chapters-and-quests)
- [Tasks](#tasks)
- [Rewards](#rewards)
- [Reward Tables](#reward-tables)
- [Quest Text](#quest-text)
- [Translations](#translations)
- [Styling with CSS](#styling-with-css)
- [Pictures, GIFs and Videos](#pictures-gifs-and-videos)
- [YouTube Videos](#youtube-videos)
- [Integrations](#integrations)
- [Importing from FTB Quests](#importing-from-ftb-quests)
- [Commands](#commands)
- [Settings](#settings)
- [For Developers](#for-developers)

---

## Getting Started

1. Install Trailmark (and Dungeon Config, which is bundled) on the client and the server.
2. Put your quest pack in `config/trailmark/quests` (see [The Quest Folder](#the-quest-folder)), or start a world and build one in game with the editor.
3. Press **I** to open the quest book. Players can also use the quest book item, or the quest book tab on top of the inventory (on the left of the creative inventory). The tab uses Minecraft's own tab textures, so resource packs that restyle tabs restyle it too.
4. To edit in game, operators open **Options** (bottom right, or **O**) and choose **Edit Quests**. Everything you change is saved to the same JSON files, with a backup in `config/trailmark/backups`.

Files reload by themselves: save a file in any editor and the quest book updates for everyone within a couple of seconds, and operators get a chat message with the number of problems found (turn this off with **Hot Reload** in the settings). `/trailmark reload` still works too. Any problems found while loading are listed by `/trailmark problems`, and nothing is ever lost: a quest that fails to load is reported, not deleted.

### Editing in Game

Operators open **Options** > **Edit Quests**. Everything can be changed without touching a file:

- **Quests**: right-click a quest > **Edit Quest** (or select it and press **E**): text, icon, shape, size, dependencies, connection lines (for the whole quest and each connection), visibility, repeating, tasks and rewards.
- **Chapters**: right-click a chapter in the list > **Edit Chapter**: title, icon, tab, order, background, default shape, connection lines, who can see it and chapter rewards.
- **Decorations**: click one to select it, double-click it (or press **Enter**) to edit it: text, picture, icon, the quest it's attached to, size, layer, opacity, link and when it appears. Drag to move it, drag its edges or corners to resize it (**Shift** keeps its shape, **Alt** skips snapping), right-click it for more, **Delete** removes the selected one. Right-click the map for new labels and pictures. While editing, a picture that is still loading or can't be found shows as a dashed box, so it can still be moved and fixed.
- **Icons**: every icon field has **Edit Icon**, with a live preview you can drag to turn. It only shows the options that fit the chosen type; for **Player**, **Player Name** picks whose skin the model wears (a username or UUID; empty shows whoever opens the book). Items have **Components** (written as in `/give`, such as `enchantment_glint_override=true`) and blocks have **Block Data** (the `{...}` part of `/setblock`).
- **Templates**: select quests (**Ctrl**-click), right-click the map > **Save as Template**; **Insert Template** stamps them at the cursor with fresh IDs and their links to each other. Templates are files in `config/trailmark/templates`, so share them by copying the file.
- **Book**: **Options** > **Book Settings**: title, icon, tabs (one `id: Title` per line), the chapter it opens on, claiming, lock badges, grid spacing and connection lines.
- On the map: drag quests to move them, drag a selected quest's corners to resize it, **Shift**-drag from one quest to another to link them, **Ctrl**-click quests and decorations to select several (or **Ctrl**-drag on empty map to box-select; dragging any selected item moves them all), the **arrow keys** to nudge everything selected one pixel at a time (hold to speed up, **Shift** for a whole cell), **Ctrl+Z** / **Ctrl+Y** to undo and redo, **Ctrl+D** to duplicate and **Delete** to remove.

Map changes are saved when you click **Save** in the footer (or press **Ctrl+S**); the footer shows **Unsaved Changes** until then. Closing the book, switching chapter or leaving edit mode with unsaved changes asks whether to save them. Undo and redo work on unsaved changes.

Every editor keeps a JSON option for anything unusual.

### Quest Book Keys

The footer of the quest book always shows the keys that do something right now. All of them can also be clicked.

| Key | Does |
|---|---|
| **I** | Open or close the quest book (rebindable). |
| **'** | Show or hide the quest tracker (rebindable). This flips the **Show Tracker** setting, so it stays that way after a restart. |
| **Q** / **E** | Previous or next tab. Tabs are the book's chapter groups; ungrouped chapters share the **Chapters** tab. |
| **/** or **Ctrl+F** | Search quests by title, task, reward or tag. **Enter** jumps to the first result. |
| **T** | Track or stop tracking the selected quest on the HUD. |
| **;** | Claim every reward that is ready (rebindable; in game it claims the latest finished quest). |
| **O** | Options: tracker, advancements, translations, settings and edit mode. |
| **Esc** | Close the details panel, then the book. |
| Edge handles | The small handles on the inner edges of the chapter list and the details panel fold them away for a bigger map. With the details panel folded, selecting a quest slides its details over the map until you close them. |
| **WASD**, arrows, **+**, **-**, **Home** | Pan, zoom and fit the map. Dragging and the mouse wheel work too. |
| **R** / **U** over an item | Show its recipes or uses in JEI, REI or EMI. |

---

## The Quest Folder

```
config/trailmark/quests/
├── book.json            Book settings and chapter groups
├── chapters/            One file per chapter (sub-folders are allowed)
│   └── getting_started.json
├── tables/              Reward tables
│   └── explorer_loot.json
├── lang/                Quest text translations (en_us.json, de_de.json, ...)
├── media/               Pictures and videos the server shares with players
└── style.css            The pack's stylesheet
```

The file name becomes the ID: `chapters/getting_started.json` is the chapter `pack:getting_started`, where `pack` is the **Pack Namespace** setting. Quests inside it are `pack:getting_started/<quest id>`.

Quests are read only from this folder, never from data packs or resource packs. On a server, the server's folder is the one that counts: players get the book, its translations, its `style.css` and its media from the server when they join, and again after every reload.

The book is sent file by file: `book.json`'s settings, `style.css`, every chapter and every reward table. The server first sends a list of the files with their hashes; players keep the files they've had before in `config/trailmark/cache/book` and only download the ones they don't have, checking each against its hash. Rejoining a server whose book hasn't changed downloads nothing, and after an edit only the changed chapter is sent.

JSON files may contain comments and trailing commas.

### book.json

```json
{
  "title": "My Pack",
  "icon": "minecraft:writable_book",
  "claim_mode": "player",
  "auto_claim": false,
  "show_lock_icons": true,
  "start_chapter": "pack:getting_started",
  "groups": [
    { "id": "story", "title": "Story", "icon": "minecraft:writable_book" },
    { "id": "side", "title": "Side Quests", "collapsed": true }
  ]
}
```

| Field | Default | Meaning |
|---|---|---|
| `title` | `""` | Shown in the top bar, in capitals. |
| `icon` | none | Header icon. See [Icons](#icons). |
| `claim_mode` | `player` | `player`: every team member claims their own rewards. `team`: the first member to claim takes them for everyone. |
| `auto_claim` | `false` | Give rewards automatically as soon as a quest is completed. |
| `locked_message` | `""` | Text shown on locked quests. |
| `show_lock_icons` | `true` | Draw a lock badge on locked quests. |
| `start_chapter` | none | The chapter opened first. |
| `groups` | `[]` | Chapter groups. Each group is a tab along the top; ungrouped chapters share the **Chapters** tab. |
| `grid_size` | `28` | Pixels between grid cells at 100% zoom. |
| `line_style` | `elbow` | How connections are drawn: `elbow` or `straight`. See [Connection Lines](#connection-lines). |
| `youtube_volume` | `50` | How loud [YouTube videos](#youtube-videos) start, 0 to 100, until a player picks their own volume. |

---

## Chapters and Quests

```json
{
  "title": "Getting Started",
  "subtitle": "Your first days in the world",
  "icon": "minecraft:oak_sapling",
  "group": "story",
  "order": 0,
  "default_shape": "rounded",
  "rewards": [ { "type": "item", "item": "minecraft:golden_apple", "count": 2 } ],
  "decorations": [
    { "x": 0, "y": -2, "width": 6, "height": 1, "text": "## Welcome to Trailmark" }
  ],
  "quests": [
    {
      "id": "first_tree",
      "title": "Punch a Tree",
      "icon": "minecraft:oak_log",
      "x": 2, "y": 0,
      "dependencies": "welcome",
      "description": "Any kind of log will do.",
      "tasks": [ { "type": "item", "item": "#minecraft:logs", "count": 16 } ],
      "rewards": [ { "type": "item", "item": "minecraft:torch", "count": 16 } ]
    }
  ]
}
```

### Chapter Fields

| Field | Default | Meaning |
|---|---|---|
| `title`, `subtitle`, `description` | `""` | Text. The description supports [Quest Text](#quest-text). |
| `icon` | none | Sidebar icon. |
| `group` | `""` | A group ID from `book.json`. |
| `order` | `0` | Sort order within the group. |
| `style` | `""` | Extra CSS classes for the chapter and its map. |
| `background` | none | A picture behind the map: texture ID, `media/` path or link. |
| `visibility` | `visible` | When the chapter appears in the sidebar. See [Visibility](#visibility). |
| `requires` | `[]` | Chapter IDs that must be completed first. |
| `quest_visibility` | `visible` | Default visibility for the chapter's quests. |
| `default_shape` | `rounded` | Default node shape for the chapter's quests. |
| `line_style` | book default | Connection lines in this chapter: `elbow` or `straight`. |
| `season` | none | When the chapter is open. See [Seasonal Chapters](#seasonal-chapters). |
| `rewards` | `[]` | Rewards for completing every non-optional quest in the chapter. |
| `decorations` | `[]` | Labels and pictures placed on the map. See [Decorations](#decorations). |
| `quests` | `[]` | The quests. |

### Quest Fields

| Field | Default | Meaning |
|---|---|---|
| `id` | required | Unique within the chapter. Lower-case letters, numbers, `_` and `/`. |
| `title`, `subtitle`, `description`, `hint` | `""` | Text. The hint is shown once the quest is unlocked. |
| `icon` | first task's icon | See [Icons](#icons). |
| `tags` | `[]` | Free-form tags, also usable as CSS classes. The tag `milestone` marks a [milestone](#milestones). |
| `style` | `""` | Extra CSS classes. |
| `x`, `y` | `0` | Position on the map, in grid units. |
| `size` | `1` | Node size multiplier. |
| `shape` | chapter default | `square`, `rounded`, `circle`, `hexagon`, `diamond`, `octagon`, `shield` or `star`. |
| `hide_dependency_lines` | `false` | Hide the lines to this quest's dependencies. |
| `z` | `0` | Its place in the [stacking order](#stacking-order) among the chapter's quests and decorations. |
| `line_style`, `line_styles` | chapter default | How lines into this quest are drawn, for all of them or per dependency. See [Connection Lines](#connection-lines). |
| `dependencies` | `[]` | One quest or a list. Quests in the same chapter by ID; quests elsewhere by full ID, such as `pack:into_the_depths/diamonds`. |
| `dependency_mode` | `all` | `all`, `any` or `at_least`. |
| `min_dependencies` | `0` | How many dependencies `at_least` needs. |
| `visibility` | chapter default | See [Visibility](#visibility). |
| `optional` | `false` | Optional quests don't count towards chapter completion. |
| `sequential` | `false` | Tasks must be completed in order. |
| `repeat` | `never` | See [Repeating Quests](#repeating-quests). |
| `time_limit` | `0` | Seconds the team has once the quest unlocks. `0` means no limit. |
| `exclusive_with` | `[]` | Completing one of these quests locks the others (choose-a-path quests). |
| `auto_claim` | book default | Give this quest's rewards automatically. |
| `tasks` | `[]` | See [Tasks](#tasks). |
| `rewards` | `[]` | See [Rewards](#rewards). |

### Seasonal Chapters

A chapter can be open only at certain times, for holiday events or weekend challenges:

```json
"season": { "from": "12-01", "until": "12-31", "hidden": true }
"season": { "from": "2026-11-01", "until": "2026-11-14" }
"season": { "days": ["saturday", "sunday"] }
```

`from` and `until` are inclusive; `MM-DD` repeats every year (a range like `12-20` to `01-06` wraps over New Year) and `YYYY-MM-DD` happens once. `days` limits it to some weekdays. Outside its season the chapter is locked (its tooltip says when it opens next) and its quests can't make progress; with `hidden` it disappears until then. Progress made during the season is kept. Dates follow the server's clock and are checked every minute, so chapters open on time without a reload. Set it in game under **Edit Chapter** > **Season**.

### Connection Lines

Lines between quests use right angles by default. Choose per connection, quest, chapter or book, most specific first:

```json
"line_styles": { "iron_age": "straight" },
"line_style": "elbow"
```

`line_styles` (on a quest, keyed like `dependencies`) sets single connections; `line_style` on a quest, a chapter or in `book.json` sets everything below it. Values are `elbow` and `straight`. Resource packs can set the default with `line { line-style: straight; }`, and players can override all of it in the settings (**Connection Lines**). Lines are solid and green between finished quests, solid with a moving highlight into quests you can start, dashed into quests that are still locked.

### Milestones

A milestone is a quest that should stand out: a boss fight, a big build, the goal of a chapter. Give it the tag `milestone` (or the style class `milestone`) and it gets the milestone colour, a round node and a slightly larger size, and the map legend lists it. The older `boss` tag and class still work. Recolour milestones with `--milestone` in CSS, or style them directly with `quest.milestone { ... }`.

### Visibility

| Value | Meaning |
|---|---|
| `visible` | Always shown. |
| `reveal_with_dependencies` | Shown once any of its dependencies is visible. |
| `hidden_until_unlocked` | Hidden until it can be started. |
| `secret` | Hidden until completed. |

### Repeating Quests

`"repeat": "daily"` is shorthand for `{ "mode": "daily" }`. The full form:

```json
"repeat": { "mode": "cooldown", "cooldown": 3600, "max_times": 5 }
```

Modes are `never`, `instant`, `cooldown` (seconds), `daily` and `weekly`. `max_times` of `0` means unlimited.

### Icons

Icons show up on the map, in the details panel, the chapter list, toasts, decorations and quest text. Blocks, structures, mobs and players are drawn by the game's own renderers, so they look exactly as they do in the world, modded and GeckoLib models included. In the editor, every icon field has an **Edit Icon** button: pick the type, fill in the fields and watch the preview (drag it to turn it).

As a string:

| String | Shows |
|---|---|
| `"minecraft:diamond"` | An item. |
| `"media/logo.png"`, `"minecraft:textures/item/apple.png"` or a web link | A picture, GIF or video. A quest whose icon is a video gets a **Watch Video** button in its details panel once it is unlocked. |
| `"block:minecraft:furnace[lit=true,facing=south]"` | A block in 3D, in any state, animated textures included. Block entity data goes in braces as in `/setblock`: `"block:minecraft:white_banner{patterns:[{pattern:\"minecraft:stripe_top\",color:\"red\"}]}"`. |
| `"entity:minecraft:zombie"` | A live mob or any other entity. |
| `"player:Notch"`, `"player:<uuid>"` or `"player:@self"` | A player with that person's skin; `@self` is whoever is looking. |
| `"structure:minecraft:igloo/top"` | A whole structure, as it would generate: structure blocks and structure voids are left out and jigsaw blocks show the block they turn into. |
| `"sound:media/theme.ogg"` | A sound, shown as a round speaker button. On a quest, its details panel gets a player (play, pause, stop, seek) that stays locked until the quest unlocks. Takes the same sounds as [decoration sounds](#videos-and-sounds). |
| `"fluid:minecraft:lava"` | A fluid, drawn with its own texture and colour like recipe viewers show fluids. Works for modded fluids without a bucket; fluid tasks use it automatically when the fluid has no bucket. |

As an object, with every option:

```json
{ "entity": "minecraft:warden", "nbt": { "CustomName": "'\"Guardian\"'" }, "scale": 1.2,
  "yaw": 30, "pitch": 10, "spin": 0, "animation": "walk", "play": "roar" }
```

| Field | Meaning |
|---|---|
| `item`, `image`, `block`, `entity`, `structure` | What to show (see above). `item` takes an ID or an item with components: `{ "id": "minecraft:diamond_sword", "components": { "minecraft:enchantment_glint_override": true } }`. |
| `nbt` | Extra entity data: a named villager, a saddled pig, a charged creeper. Items use components instead (1.21 has no item NBT); blocks take their data inside `block`. |
| `scale` | Size multiplier. |
| `yaw`, `pitch` | Camera turn and tilt in degrees. Leave out for the default three-quarter view. |
| `spin` | Degrees per second it turns on its own. Mobs and structures turn by default, blocks don't; `0` keeps it still. The Icon Editor calls it **Spin Speed**. |
| `animation_speed` | How fast its animations run, `0` to `10`: `1` is normal, `0.5` half speed, `2` twice as fast, `0` stops them. Applies to everything that moves: mobs (idle, walking and picked animations, vanilla, modded or GeckoLib), blocks (picked animations and anything their renderer times by the world) and GeckoLib items. |
| `animation` | For mobs: `live` (breathing and idle animations, the default), `walk` (also moves its legs) or `frozen`. |
| `time` | With `frozen`, the moment to freeze at, in ticks. |
| `play` | An animation the icon plays on a loop. The Icon Editor lists what the chosen model has. Vanilla mobs: warden `roar`, `sniff`, `emerge`, `digging`, `attack`, `sonic_boom`; sniffer `feeling_happy`, `scenting`, `sniffing`, `digging`, `rising`; camel `sit`, `sit_pose`, `sit_up`, `idle`, `dash`; frog `jump`, `croak`, `tongue`, `swim_idle`; armadillo `roll_out`, `roll_up`, `peek`; breeze `idle`, `slide`, `slide_back`, `long_jump`, `shoot`, `inhale`; bat `fly`, `rest` (or `#0`, `#1`... in that order); ender dragon, chicken and parrot `flap` (a dragon also flaps on its own when nothing is picked); parrot `dance`; squid and glow squid `swim`; iron golem `attack`, `offer_flower`; ravager `attack`, `roar`, `stunned`; hoglin and zoglin `attack`; sheep `eat`; goat `ram`; wolf `shake`; wolf, cat and parrot `sit`; cat `lie_down`; polar bear `stand`; fox `sit`, `crouch`, `pounce`, `sleep`; creeper `swell`; slime and magma cube `squish`; villager and wandering trader `shake_head`; vex and ghast `charge`; piglin and allay `dance`; axolotl `play_dead`; evoker and illusioner `cast`; pillager, vindicator, evoker and illusioner `celebrate`; horses, donkeys and mules `rear`, `eat`; shulker `peek`, `open`. Vanilla blocks: chest, trapped chest, ender chest and shulker box `open`; bell `ring`; decorated pot `wobble`; enchanting table `open` (the book); conduit `active`. Renderers that move with the world's time (an end portal, many modded machines) animate on their own. **Modded mobs and block entities** that use Minecraft's animation system have theirs found and listed automatically, named after their fields (`attackAnimationState` becomes `attack`) and replayed every four seconds. **GeckoLib** mobs, blocks and items: any animation in their animation file, or `controller/animation`. |
| `shift_x`, `shift_y` | Move the model inside its slot, in slot sizes: `0.25` moves it a quarter to the right (or down); negative numbers go left (or up). Works for every kind of icon. In rich text, use the same names as attributes. |

#### Players and Posed Mobs

```json
{ "player": "@self", "slim": true,
  "equipment": { "head": "minecraft:iron_helmet", "mainhand": "minecraft:iron_sword", "offhand": "minecraft:shield" },
  "pose": { "right_arm": [-110, 0, 10], "head": [10, -20, 0], "left_leg": [20, 0, 0] } }
```

- `player` is a username, a UUID or `@self` (also `{player}`). Skins are fetched the same way player heads fetch them and cached by the game.
- `skin` uses a texture instead, such as `mypack:textures/skins/guide.png` from a resource pack. `slim` picks the arm model; leave it out to use the skin's own.
- `equipment` works on players and every mob: `head`, `chest`, `legs`, `feet`, `mainhand`, `offhand`.
- `pose` sets `head`, `body`, `right_arm`, `left_arm`, `right_leg` and `left_leg` to `[x, y, z]` rotations in degrees, on players and every mob with a humanoid model (zombies, skeletons, piglins...). Armour and held items follow the posed limbs.

#### Structures

`structure` takes a structure ID, found in this order:

1. In singleplayer, the world's data packs and its `generated` folder (where structure blocks save).
2. Resource packs: `assets/<namespace>/structure/<path>.nbt`.
3. The server's data packs and `generated` folder. The server sends the file on request (at most 1 MB, 40 requests a minute per player).

So a structure saved with a structure block, or shipped in a data pack with your quest pack, just works. Structures up to 256 blocks on a side are supported; structure voids and air are skipped, and entities inside structures aren't shown. Use one as a big map decoration to show players what to build.

Anything that isn't available yet (a mod that registers late, a structure still on its way) is retried every few seconds; anything a mod's renderer fails on is skipped with a line in the log, never a crash.

### Decorations

```json
{ "x": 0, "y": -2, "width": 6, "height": 1, "text": "## Welcome", "image": "media/banner.png",
  "rotation": 0, "opacity": 1, "z": -1, "link": "quest:pack:getting_started/welcome",
  "visible_after": "pack:getting_started/welcome", "style": "banner" }
```

`text` supports [Quest Text](#quest-text). `z` is its place in the [stacking order](#stacking-order). `visible_after` hides the decoration until that quest is completed. `icon` fills the decoration with any [icon](#icons), such as a structure or a posed player.

`anchor` ties a decoration to a quest: `x` and `y` then count from that quest's centre, so `{ "anchor": "boss", "x": 0, "y": 0, "width": 3, "height": 3, "image": "media/glow.png" }` always sits centred behind the `boss` quest, whatever the zoom, screen size or where the quest moves.

In the editor, drag decorations to move them. A click picks whatever is on top; hold **Alt** to grab the thing just beneath it, and to place it off the half-cell grid.

#### Stacking Order

Quests and decorations stack on the map one by one: each has a `z`, and higher numbers draw on top of lower ones, whether they are quests or decorations. Connection lines sit just under the lowest quest. In edit mode, right-click anything and pick **Bring Forward** or **Send Backward**: it moves past the next thing it overlaps (or one step in the stack when nothing overlaps it), and the editor writes every object's `z` for you. **Bring to Front** and **Send to Back** move it past everything it overlaps. The same works from the keyboard on the selected object: **Ctrl+]** and **Ctrl+[**, with **Shift** for front and back. New quests and decorations go on top.

When several things overlap under the pointer, the right-click menu ends with a **Select** list of all of them, top first, so you can pick one buried under the others. Once something is selected, clicking or right-clicking where it is acts on it even when other things are drawn over it (hold **Alt** to reach past it instead).

Things players can't see yet still show in edit mode, faded: decorations waiting on `visible_after` (with an eye badge and a tooltip naming the quest) and quests hidden by their visibility. **Ctrl+D** copies the selected quest or decoration, and decorations have **Duplicate** in their menu too.

Without any `z`, older packs look as they always did: decorations with `"layer": "back"` (the default) under the quests (as if `z` were -1), quests at 0, and `"layer": "front"` decorations above them (1). With equal `z`, quests draw above decorations, then everything keeps its order in the file.

#### Videos and Sounds

A decoration's `image` can be a video (`media/intro.mp4`, `.webm` and the other formats FFmpeg reads, or a link). It never starts by itself: it shows its first frame with a play button, and clicking it opens the video player: a window over the book with the video's sound, play/pause, stop, a seek bar, a volume slider with a mute button and **Full Screen** (keys: **Space** play/pause, **F** full screen, **M** mute, **Left**/**Right** jump five seconds, **Esc** close). With `video_sound` it plays right on the map instead, with a control bar along its bottom (seek bar, play/pause, time, volume and a button for the big player) that shows while the mouse is over it. Sound players (a quest's sound, the Now Playing bar over the map) have a volume slider too. Volume picked with a slider multiplies the `volume` the pack set, and lasts until the game closes. Sounds are their own fields:

```json
{ "x": 4, "y": 1, "width": 1, "height": 1, "sound": "media/theme.ogg", "volume": 0.6, "loop": true, "autoplay": true }
{ "x": 0, "y": 4, "width": 6, "height": 3.5, "image": "media/intro.mp4", "video_sound": true }
```

| Field | Meaning |
|---|---|
| `sound` | Played when the decoration is clicked: a sound ID (`minecraft:music_disc.cat`, or one from a resource pack's `sounds.json`), an OGG or WAV file (`media/theme.ogg`, a link or a resource pack path), or MP3, FLAC, M4A, Opus and similar when FFmpeg is installed. |
| `volume` | 0 to 1, on top of the player's **Jukebox/Note Blocks** volume. |
| `loop` | Keep playing until clicked again. |
| `autoplay` | Start the sound when the chapter opens: chapter music or ambience. Videos never start by themselves. Map sounds stop when the player switches chapter or closes the book. |
| `video_sound` | With a video `image`, clicking plays and pauses it right on the map with its own sound (needs FFmpeg), instead of opening the video player. The video player always plays it. |
| `unlock_after` | A quest that must be completed first. Until then the decoration shows a lock and its sound or video can't be played. |

A decoration that is only a sound shows as a round play button; one with a picture or text gets a small speaker badge. While a sound plays away from its own player, a **Now Playing** bar at the bottom of the map has play/pause, stop and a seek bar (click or drag it). A quest's sound keeps going when the player closes its panel or moves to another chapter, so the bar stays useful; starting another quest's sound replaces it, and closing the book stops everything. Sound IDs are traced to their sound file, so they get the same controls. In the editor, right-click the map for **New Video** and **New Sound**, and right-click a decoration to preview its sound. Sound files and videos go through [the same media route](#how-media-reaches-players) as pictures; playback uses Dungeon Config's sound engine.

### Shared Task and Reward Fields

Every task and reward accepts these fields as well:

| Field | Meaning |
|---|---|
| `id` | Stable ID for progress. Generated if missing; set it if you plan to reorder tasks later. |
| `title` | Replaces the generated title. |
| `icon` | Replaces the generated icon. |
| `description` (tasks) | Extra text under the task. |
| `optional` (tasks) | Not needed to complete the quest. |
| `hidden` | Hidden until completed (tasks) or claimed (rewards). |
| `claim` (rewards) | `player` or `team`, overriding the book setting. |
| `auto_claim` (rewards) | Give this reward automatically. |
| `exclude_from_claim_all` (rewards) | Leave this reward out of the Claim All button. |

The `type` can omit `trailmark:`, so `"item"` and `"trailmark:item"` are the same.

In the quest book, hovering a task or reward about one item (item, craft, use, mine and place tasks, item rewards) shows that item's tooltip and recipe keys. Other types show their own title and details instead, such as an advancement's description or the items a recipe reward unlocks, even when their icon happens to be an item.

---

## Tasks

Item and registry targets accept an ID (`"minecraft:iron_ingot"`), a tag (`"#c:ingots/iron"`) or a list of either. Item targets can also be `{ "items": [...], "components": { ... } }` to require certain data on the stack.

| Type | Example | Notes |
|---|---|---|
| `item` | `{ "type": "item", "item": "#minecraft:logs", "count": 16 }` | `consume: true` makes players hand the items in. `auto_submit` hands them in automatically. `count_storage` (default `true`) also counts backpacks, accessories and linked storage networks. |
| `fluid` | `{ "type": "fluid", "fluid": "minecraft:lava", "amount": 1000 }` | In buckets and tanks the player carries. Amount in millibuckets. `consume` works as above. |
| `craft` | `{ "type": "craft", "item": "minecraft:crafting_table" }` | |
| `mine` | `{ "type": "mine", "block": "#minecraft:logs", "count": 32 }` | |
| `place` | `{ "type": "place", "block": "minecraft:torch", "count": 10 }` | |
| `use` | `{ "type": "use", "item": "minecraft:ender_pearl", "count": 3 }` | |
| `kill` | `{ "type": "kill", "entity": "minecraft:zombie", "count": 10 }` | Optional `custom_name`, `nbt` and `weapon` filters. |
| `interact` | `{ "type": "interact", "entity": "minecraft:villager" }` | `block` or `entity`, optionally `held`. |
| `observe` | `{ "type": "observe", "block": "minecraft:beacon", "seconds": 2 }` | Look at a block or entity. |
| `stat` | `{ "type": "stat", "stat": "minecraft:fish_caught", "value": 10 }` | Any custom statistic. Progress made before the quest unlocked counts. |
| `advancement` | `{ "type": "advancement", "advancement": "minecraft:story/mine_diamond" }` | Optional `criterion`. |
| `location` | `{ "type": "location", "x": 0, "y": 64, "z": 0, "radius": 8, "ignore_y": true }` | Optional `dimension`. The tracker shows a compass arrow. |
| `dimension` | `{ "type": "dimension", "dimension": "minecraft:the_nether" }` | |
| `biome` | `{ "type": "biome", "biome": "#minecraft:is_ocean" }` | |
| `structure` | `{ "type": "structure", "structure": "minecraft:fortress" }` | |
| `xp` | `{ "type": "xp", "levels": 10, "consume": false }` | |
| `checkmark` | `{ "type": "checkmark", "title": "I've Read This" }` | Completed with a button. |
| `custom` | `{ "type": "custom", "name": "talked_to_bob", "count": 1 }` | Completed by `/trailmark signal`, scripts or other mods. |
| `counter` | `{ "type": "counter", "name": "reputation", "value": 50 }` | Reaches a team counter value. |
| `stage` | `{ "type": "stage", "stage": "nether_unlocked" }` | Trailmark, FTB Teams or KubeJS stages. |
| `player_data` | `{ "type": "player_data", "path": "Health", "compare": "at_least", "value": 20 }` | Compares a value in the player's data. `compare` is `equals`, `at_least`, `at_most` or `exists`. |

Integration tasks are listed under [Integrations](#integrations).

---

## Rewards

| Type | Example | Notes |
|---|---|---|
| `item` | `{ "type": "item", "item": "minecraft:diamond", "count": 3 }` | `random_bonus` adds up to that many extra. `slot` puts the item straight into `head`, `chest`, `legs`, `feet`, `offhand`, `mainhand` or an accessory slot such as `curios:ring`. |
| `choice` | `{ "type": "choice", "choices": [ ... ] }` | The player picks one. |
| `random` | `{ "type": "random", "table": "pack:explorer_loot", "rolls": 1 }` | From a [reward table](#reward-tables), or inline `entries`. `all: true` gives every entry. |
| `loot` | `{ "type": "loot", "table": "minecraft:chests/simple_dungeon" }` | A vanilla loot table. |
| `xp` | `{ "type": "xp", "points": 100 }` or `{ "levels": 5 }` | |
| `command` | `{ "type": "command", "command": "say Hello, @p!" }` | Runs at `permission` level 2 by default, silently. `as_player: true` runs as the player. |
| `advancement` | `{ "type": "advancement", "advancement": "pack:secret" }` | Optional `criterion`. |
| `stage` | `{ "type": "stage", "stage": "nether_unlocked" }` | `remove: true` takes it away. |
| `counter` | `{ "type": "counter", "name": "reputation", "amount": 10 }` | |
| `effect` | `{ "type": "effect", "effect": "minecraft:speed", "seconds": 60, "amplifier": 1 }` | |
| `recipe` | `{ "type": "recipe", "recipes": ["minecraft:diamond_pickaxe"] }` | Unlocks recipes in the recipe book. |
| `toast` | `{ "type": "toast", "message": "Well done!" }` | Shows a notification. |

---

## Reward Tables

`tables/explorer_loot.json`:

```json
{
  "title": "Explorer's Cache",
  "icon": "minecraft:chest",
  "rolls": 2,
  "empty_weight": 1,
  "entries": [
    { "weight": 5, "reward": { "type": "item", "item": "minecraft:emerald", "count": 3 } },
    { "weight": 1, "reward": { "type": "item", "item": "minecraft:diamond" } }
  ]
}
```

`empty_weight` adds a chance of rolling nothing.

---

## Quest Text

Descriptions, hints, chapter descriptions and decorations support Markdown, safe HTML and legacy colour codes. A description can be one string or a list of lines.

### Markdown

```markdown
# Heading
**bold**, *italic*, __underline__, ~~strikethrough~~, `code`, ||spoiler||

- Lists, nested by indenting
1. Numbered lists

> Quotes

| Tables | Work |
|---|---:|
| left | right |

[A web link](https://example.com) · [Another quest](quest:pack:getting_started/welcome) · [A chapter](chapter:pack:into_the_depths)

![A picture](media/map.png){width=64}
```

Web links always ask before opening. `patchouli:<book>`, `patchouli:<book>#<entry>` and `patchouli:<book>#<entry>#<page>` open a Patchouli book, for example `[How mana works](patchouli:botania:lexicon#botania:mana/pools)`; without Patchouli the link says so instead.

### HTML

`span`, `div`, `p`, `br`, `center`, `img`, `video`, `youtube` (see [YouTube Videos](#youtube-videos)), `item`, `entity`, `block`, `structure`, `player`, `icon` (all four take the [icon](#icons) options as attributes: `size`, `scale`, `yaw`, `pitch`, `spin`, `animation`, `time`, `play`, `animation_speed`, `nbt`, `skin`, `slim`, `head`, `chest`, `legs`, `feet`, `mainhand`, `offhand`, `pose` and `title`), `kbd`, `spoiler`, `details`/`summary`, `a`, `b`, `i`, `u`, `s`, `small`, `code`, `pre`, headings, lists, `blockquote`, `hr` and tables, with `style`, `class`, `id`, `title` and `align` attributes. A `<style>` block applies only to that text.

```html
<item id="minecraft:diamond" count="3"/> Hover for the tooltip, press R or U for recipes.
<entity type="minecraft:creeper" scale="1.5"/>
<entity type="minecraft:camel" animation="walk" spin="0" yaw="40"/>
<block id="minecraft:blast_furnace[lit=true]" size="48"/>
<structure id="mypack:house" size="160" pitch="35"/>
<player name="@self" mainhand="minecraft:diamond_sword" pose='{"right_arm": [-110, 0, 0]}'/>
<icon src="block:minecraft:beacon" title="A beacon"/>
<details><summary>Spoilers</summary>Hidden until clicked.</details>
<kbd>Shift</kbd> + <kbd>Right-Click</kbd>
<div style="display: flex; gap: 8px"><div>Left</div><div>Right</div></div>
```

### Colour Codes and Placeholders

`&a` to `&f`, `&l`, `&o`, `&n`, `&m`, `&k`, `&r` and `&#RRGGBB` work as in FTB Quests (`§` too). `{lang:some.key}` inserts a translation.

---

## Translations

Every piece of quest text has a key, such as `trailmark.quest.pack.getting_started.welcome.title`. Text is looked up in this order, and the first match wins:

1. The player's own overrides, made in the in-game translation editor.
2. Resource packs: `assets/<any>/lang/<locale>.json`.
3. The quest pack's `lang/<locale>.json` for the player's language.
4. The quest pack's `lang/en_us.json`.
5. The text written in the quest file.

To translate a pack, run `/trailmark lang export de_de`. It writes every key with the current text to the quest folder's `lang/de_de.json`, ready to edit. In game, the globe button in the header opens the **Quest Text and Translations** editor:
- Search, or show only missing entries.
- Click a row to edit it.
- Save for yourself or, as an editor, for the whole server.

---

## Styling with CSS

Trailmark draws its own screens, and every part of them can be restyled. Stylesheets are layered, lowest first:

1. The built-in theme, `assets/trailmark/trailmark/default.css`. A resource pack can replace it entirely.
2. Every resource pack's `assets/trailmark/trailmark/style.css`, in pack order.
3. The player's own `config/trailmark/style.css`, which reloads as soon as you save it.
4. The quest pack's `style.css`. On a server this is the server's, sent to players when they join, so every server can have its own look.

So a server's theme wins by default. Players who'd rather keep their own look can turn on **Prefer My Style**, which moves the quest pack's `style.css` to just above the built-in theme, under their resource packs and their own `style.css`. Switching it restyles open screens straight away.

Broken CSS is reported in the log and skipped. It never breaks the screen.

### Elements

`screen`, `window` (class `editor` for the editor and translation screens), `header`, `title`, `search`, `sidebar`, `group`, `chapter`, `map`, `quest`, `line`, `legend`, `decoration`, `panel`, `panel-title`, `panel-subtitle`, `description`, `task`, `reward`, `badge`, `button` (classes `primary`, `gold`, `claim`, `danger`, `ghost`, `icon`, `book`), `progress` (class `pack`), `menu`, `scrollbar`, `tooltip`, `toast` (classes `completed`, `task`, `unlocked`, `chapter`, `reward`, `expired`, `repeatable`, `message`, `error`), `tracker` (class `key-hint`) and `tracker-quest`, plus the [screen kit](#screen-kit) elements below.

Quest text uses `h1` to `h6`, `p`, `a`, `code`, `pre`, `blockquote`, `ul`, `ol`, `li`, `table`, `th`, `td`, `hr`, `img`, `video`, `item`, `entity`, `kbd`, `spoiler`, `details`, `summary` and `span`/`div`.

Quests carry their tags and `style` as classes and their ID as `#id`; chapters carry their `style` as classes.

### Selectors

- Element, `*`, `.class`, `#id`, descendant (`chapter quest`) and child (`window > header`).
- State pseudo-classes: `:hover`, `:active`, `:selected`, `:focused`, `:disabled`, `:locked`, `:available`, `:in_progress`, `:claimable`, `:completed`, `:cooldown`, `:excluded`, `:tracked` and `:editing`.
- `:not(...)`, `:first-child`, `:last-child`, `:only-child`, `:nth-child(2n+1)` and `:nth-last-child(...)`.
- Attribute tests: `[href]`, `[href="..."]`, `[href^="quest:"]`, `[src$=".gif"]`, `[class~="x"]` and `[lang|="en"]`.

Position and attribute selectors apply inside quest text, where elements know their siblings and attributes. Sibling combinators (`+`, `~`) and pseudo-elements are not supported.

### Properties

| Property | Values |
|---|---|
| `color`, `background-color`, `border-color`, `accent-color`, `glow-color`, `shadow-color`, `grid-color` | `#rgb`, `#rrggbb`, `#aarrggbb`, `rgb()`, `rgba()`, `hsl()`, `hsla()` or a name |
| `background-image`, `background` | `url(...)` (any [media reference](#pictures-gifs-and-videos)), `linear-gradient()`, `radial-gradient()`, `repeating-*` |
| `background-opacity`, `opacity` | `0` to `1` |
| `border-width`, `border-radius`, `padding`, `margin`, `gap`, `size`, `width`, `height`, `max-width` | Pixels. Box values take one to four lengths. |
| `font-scale`, `font-size` | A scale, such as `1.5`, or pixels. |
| `font-family`, `title-font-family`, `heading-font-family`, `body-font-family`, `value-font-family`, `bold-font-family` | A font name, or a list such as `"my font", sanctum`; see [Fonts](#fonts). |
| `shape` | `quest` only: `square`, `rounded`, `circle`, `hexagon`, `diamond`, `octagon`, `shield` or `star`. |
| `font-weight`, `font-style`, `text-decoration`, `text-align`, `text-shadow` | As in CSS. |
| `box-shadow` | `x y blur spread colour`, several separated by commas, `inset` supported. |
| `transform` | `translate()`, `scale()`, `rotate()` and their X/Y forms. Visual only: click areas don't move. |
| `transition` | A duration and timing function, such as `150ms ease-out`. |
| `animation` | `name duration timing delay count direction fill`, as in CSS. |
| `sound` | A sound event ID, or `none`. |
| `display`, `flex-direction`, `justify-content`, `align-items`, `flex`, `flex-grow`, `flex-basis` | Quest text only: `display: none` and flex rows or columns. |

`calc()`, `min()`, `max()`, `clamp()` and `var(--name)` work in any value.

### Screen Kit

Every Trailmark screen, popup, tooltip and the HUD share one set of colours. Change them with these elements; each property takes a colour.

| Element | Properties |
|---|---|
| `palette` | `text-color`, `label-color`, `dim-color`, `faint-color`, `accent-color` (the gold accent), `accent-light-color`, `accent-dark-color`, `line-color`, `line-light-color`, `hover-color`, `shadow-color` |
| `backdrop` | `top-color`, `bottom-color`, `grid-color`, `trail-color`, `vignette-color` (the dark sides), `vignette-bottom-color`, `vignette-size` (how far in, a share of the width, 0 to 0.5; 0 turns it off). Set `background-image` (a picture, GIF, gradient, or a video, which stays on its first frame) to replace the trail map entirely; add `trails: true` to keep the trails on top. |
| `topbar` | `top-color`, `bottom-color` |
| `card` | `background-color` (details panel, editor windows, tracker, toasts) |
| `row` | `background-color`, `hover-color`, `selected-color` |
| `field` | `background-color` |
| `chip` | `background-color`, `border-color`, `color` (key hints) |
| `tooltip` | `background-color`, `border-color`, `accent-color` (the line along the top) |

Every colour on every screen starts from the custom properties in `:root`, so changing one recolours everything that uses it: `--window`, `--header`, `--frame`, `--raised`, `--raised-hover`, `--line`, `--text`, `--text-soft`, `--muted`, `--locked-text`, `--accent` (open quests), `--accent-dark`, `--accent-light`, `--done`, `--done-dark`, `--done-light`, `--claimable`, `--claimable-dark`, `--gold-light`, `--milestone`, `--milestone-dark`, `--milestone-light`, `--locked`, `--locked-dark`, `--danger`, `--danger-dark`, `--cooldown`, `--cooldown-dark`, `--chip-line`, `--gold-chip`, `--hud` and `--toast`. The `theme` element holds the same colours by name (`accent-color`, `done-color` and so on) if you'd rather set them there.

```css
palette { accent-color: #7ED957; }
backdrop { background-image: url("media/night_sky.gif"); }
card { background-color: #E0101418; }
```

### Every Part

Each part below has its own element, on top of the colours above. Anything you leave out keeps its built-in look. Sizes are in GUI pixels and held to safe limits (shown in brackets), so a stylesheet can resize things but can't break the layout.

| Element | Properties |
|---|---|
| `screen` | `margin`, `panel-gap` [2 to 40], `message-color` (empty book, no server), `hover-duration` (how fast hover highlights fade), `open-duration` |
| `header` | `height`, `background-color` (solid) or `top-color` and `bottom-color`, `border-color`, `shadow-color`, `divider-color` |
| `title` | `color`, `shadow-color` |
| `group` (the tabs) | `color`, `hover-color`, `selected-color`, `hover-background-color`, `accent-color`, `accent-light-color`, `glow-color`, `padding` [2 to 40], `underline-duration` |
| `claim-all` | `background-color`, `hover-background-color`, `border-color`, `hover-border-color`, `color`, `icon-color`, `shadow-color`, `pulse-duration` |
| `footer` | `height`, `top-color`, `bottom-color`, `notice-color` (the FFmpeg download status) |
| `save-status` (edit mode, by the Save hint) | `color` (saved), `unsaved-color`, `error-color` (read only), `pulse-color`, `pulse-duration` |
| `search` | `height` [up to 48], `background-color`, `border-color`, `hover-border-color`, `focus-border-color`, `color` (the magnifier), `focus-color`, `text-color`, `hint-color`, `caret-color` |
| `search-results` (the drop-down; quest states such as `:completed`) | `background-color`, `border-color`, `hover-color`, `color`, `dim-color` (the chapter name) |
| `chapter-header` (title row above the map) | `color`, `shadow-color`, `count-color`, `fill-color`, `done-color` |
| `sidebar` | `width` [80 to half the screen], `collapsed-width` [24 to 80], `background-color`, `color`, `gap` [0 to 16], `heading-height` [12 to 48], `pack-height` [24 to 80], `slide-duration` |
| `chapter` | also `height` [16 to 64], `lock-color`, `dot-color` (claimable), `value-color` (the percentage), `pulse-duration` |
| `progress.pack` | also `heading-color`, `color` (the line under the bar) |
| `panel` | also `width` [140 to half the screen], `inner-padding` [2 to 24], `footer-height` [18 to 60], `icon-size` [16 to 64], `accent-color` (the strip along the top, takes the selected quest's state), `slide-duration` |
| `panel-title`, `panel-subtitle` | `color`; `panel-title` also `shadow-color` |
| `panel-status` (the line under the title; quest states) | `color` |
| `panel-heading` (section headings) | `color`, `dim-color`, `divider-color` |
| `panel-hint` | `color` |
| `hint` | `color`, `icon-color` |
| `dependency` (`:completed`, `:locked`, `:hover`; class `excludes`) | `color`, `hover-color`, `icon-color` |
| `task` | also `hover-background-color`, `icon-color`, `count-color`, `optional-color`, `description-color`, `track-color` |
| `reward` | also `mystery-color`, `check-color`; chapter rewards have class `chapter` |
| `chapter-info` (`:completed`) | `accent-color`, `border-color`, `background-color`, `color`, `progress-color`, `track-color`, `subtitle-color` |
| `stat` (classes `done`, `claim`, `open`, `locked`) | `color`, `value-color`, `swatch-color` |
| `handle` (the drawer tabs) | `width` [6 to 24], `height` [12 to 80], `background-color`, `hover-background-color`, `border-color`, `hover-border-color`, `color`, `hover-color`, `shadow-color` |
| `map` | also `frame-color`, `frame-background`, `message-color`, `pan-duration` (camera and zoom glide) |
| `legend` | also `border-color`, `color`, `done-color`, `claim-color`, `open-color`, `locked-color`, `milestone-color` |
| `quest-tooltip` (quest states) | `color` (title), `subtitle-color`, `status-color` |
| `selection-box` (drag-select) | `background-color`, `border-color`, `color` (the "Moving" note) |
| `placeholder` (empty or missing media) | `background-color`, `border-color`, `color`, `dim-color` |
| `map-controls` (zoom) | `button-size` [10 to 32], `background-color`, `border-color`, `color`, `hover-color` |
| `player` | `height` [12 to 32], `color`, `idle-color`, `error-color`, `locked-color`, `track-color`, `fill-color`, `volume-color`, `knob-color` |
| `now-playing` | `width` [160 to 640], `background-color`, `border-color`, `color`, `icon-color`, `shadow-color` |
| `play-chip` (on paused videos) | `min-size`, `max-size`, `background-color`, `border-color`, `casing-color`, `color` |
| `map-video` (control bar on map videos) | `bar-color` |
| `sound-chip` (`:active` while playing) | `background-color`, `border-color`, `casing-color`, `glow-color`, `color` |
| `map-badge` (`:active` while playing) | `background-color`, `casing-color`, `color` |
| `corner-badge` (classes `status`, `pin`, `new`; `status` takes quest states such as `:claimable`) | `size` [7 to 32], `background-color`, `frame-color`, `glow-color`, `color` |
| `media-lock` | `background-color`, `color` |
| `selection-handle` | `background-color`, `color`, `outline-color` |
| `download` | `background-color`, `color`, `track-color`, `fill-color`, `bar-height` [1 to 8], `amount-color` |
| `window.editor` (the editor and translation windows) | `accent-color` (the strip along the top), `title-color`. Window and popup titles use the title font (Minecraft Ten). |
| `dialog` (classes `notice`, `video`, `choice`, `prompt`, `unsaved`, `confirm`) | `background-color`, `border-color`, `title-color`, `color`, `dim-color` (popups also read `dim-color` from plain `dialog` for the screen behind) |
| `dialog.video` | also `video-background`, `error-color` |
| `dialog.choice` | also `card-color`, `card-hover-color`, `card-selected-color`, `card-selected-border-color` |
| `dialog.prompt` | also `field-color`, `field-border-color` |
| `menu` | also `item-color`, `item-hover-color`, `hover-color`, `danger-color`, `danger-hover-color`, `separator-color` |
| `youtube` | `background-color`, `empty-background`, `error-background`, `dim-color`, `thumbnail-dim-color`, `title-background`, `play-color`, `play-hover-color`, `play-icon-color`, `button-background`, `bar-color`, `track-color`, `fill-color`, `volume-track-color`, `icon-color`, `icon-hover-color`, `icon-dim-color`, `ad-background`, `ad-color` |
| `form` (editors) | `label-color`, `focus-label-color`, `heading-color`, `heading-line-color`, `error-color`, `arrow-color`, `toggle-on-color`, `toggle-off-color`, `toggle-on-border-color`, `toggle-off-border-color`, `toggle-knob-on-color`, `toggle-knob-off-color` |
| `field` | also `border-color`, `hover-border-color`, `focus-border-color` |
| `chip`, `card`, `tooltip` | also `shadow-color` |
| `progress` | also `shine-color`, `head-color` |
| `line` | also `faded-color`, `flow-color`, `flow-duration` |
| `palette` | also `text-shadow-color` |

### Icons

Swap any built-in icon for a picture with the `glyph` element and the icon's name as its class. The picture can be a texture, a `media/` file or a link, and it's drawn at the icon's own size, in its own colours.

```css
glyph.play { background-image: url("media/my_play.png"); }
glyph.close { background-image: url("mypack:textures/gui/close.png"); }
```

Names: `check`, `lock`, `pin`, `search`, `close`, `pencil`, `globe`, `gear`, `star`, `clock`, `gift`, `plus`, `minus`, `bang`, `chevron_right`, `chevron_down`, `chevron_left`, `chevron_up`, `stop`, `expand`, `trophy`, `eye`, `list`, `bell`, `trash`, `center`, `arrow`, `sidebar`, `book`, `undo`, `save`, `warning`, `link`, `question`, `circle`, `circle_check`, `more`, `trail`, `dice`, `play`, `pause`, `speaker`, `muted` and `external`.

### Map Lines and Slots

`line` takes `color`, `width` (default `1.5px`), `line-style` and `casing-color` (the dark outline under every line). `quest` takes `background-color`, `border-color`, `casing-color` (the dark outline around each slot), `bevel-color` (an inner rim, off by default), `glow-color`, `shadow-color` and `size`, all per state, such as `quest:claimable { border-color: gold; }`.

Slots also take, per state:

| Property | Default | |
|---|---|---|
| `shape` | the quest's own | Overrides the quest's and chapter's shape, so `quest.boss { shape: star; }` works. |
| `icon-scale` | `0.6` | The icon's size, as a share of the slot [0.1 to 1.5]. |
| `icon-brightness` | `0.42` locked, `1` otherwise | Darkens the icon itself [0 to 1]. |
| `hover-grow` | `0.08` | How much a slot grows under the pointer [0 to 0.5]. |
| `hover-brightness` | `0.12` | How much the face lightens on hover; negative darkens [-1 to 1]. |
| `hover-border-color` | a lighter border | The ring under the pointer. |
| `hover-glow-color` | the border | |
| `glow-opacity` | `1` | Scales every glow [0 to 2]. |
| `pulse-duration`, `halo-pulse-duration` | `1.6s`, `1.4s` | The claimable glow and the selected ring. |
| `shadow-offset`, `shadow-opacity` | `2`, `0.4` | [-8 to 8], [0 to 1]. |
| `optional-opacity` | `0.6` | The ring of optional quests. |
| `hidden-opacity` | `0.45` | Edit mode: quests players can't see yet. `decoration` takes it too. |
| `hover-duration` | `0.15s` | How fast the hover effect follows the pointer. |

### Fonts

Trailmark's screens use crisp pixel fonts in four roles: `title` (screen titles), `heading` (tabs and section headers), `body` (everything else) and `value` (numbers and key hints). Bold words in body and heading text, from `**bold**`, `<b>`, `font-weight: bold` or `§l`, draw in **Sanctum Bold**, the bold cut of the body font, so a bold word in a sentence matches the words around it.

**Replacing a role's font.** Put a `.ttf` or `.otf` named after the role in either place:

1. `config/trailmark/fonts/<role>.ttf` for just you. A `fonts.json` next to them can pick a file and pixel size per role, such as `{ "body": { "file": "my_font.ttf", "pixelSize": 8 } }`; `"smooth": true` renders a non-pixel font smoothly.
2. `assets/trailmark/font/trailmark/<role>.ttf` (or `.otf`) in a resource pack, for everyone using the pack.

A role without its own font borrows its partner's (`title` and `value`, `heading` and `body`). A font named `bold` replaces Sanctum Bold. Characters a font can't draw are swapped for close equivalents, so text never shows question marks.

**Fonts for any element.** Ship any number of fonts in the same two places, under any name: `assets/trailmark/font/trailmark/fancy.ttf` in a resource pack, or `config/trailmark/fonts/fancy.ttf`. Then use the name in the stylesheet:

```css
* { font-family: fancy; }                          /* everything */
panel-title { font-family: "fancy", sanctum; }    /* the first that exists */
legend { body-font-family: minecraft-ten; }        /* one kind of text */
button { bold-font-family: fancy-bold; }
```

`font-family` sets all of an element's text; `title-font-family`, `heading-font-family`, `body-font-family` and `value-font-family` set one kind; `bold-font-family` sets bold words (in quest text, bold follows the font: `<name>-bold`). Inner elements inherit what outer ones set, as in CSS. Bold words in a custom font use `<name>-bold` (or `<name>_bold`) when you ship one, otherwise the font itself. Built-in names: `sanctum`, `sanctum-bold` and `minecraft-ten`. `fonts.json` can size named fonts too (`{ "fancy": { "pixelSize": 8 } }`). It works for every part of the quest book, its popups, editors and tooltips, and for quest text (`p`, `h1`, `li`...). Toast headings and the tracker's chapter headings use the title font (Minecraft Ten), so `toast` and `tracker` take `font-family` for them (the heading colour is `tracker-quest { heading-color }`); the rest of their text uses Minecraft's font to match the HUD. Toasts grow to fit their text, up to three quarters of the screen width.

Fonts are loaded once and kept until resource packs reload; a file changed in `config/trailmark/fonts` is picked up the next time a screen opens.

### At-Rules

```css
:root { --accent: #4B8CFF; }

@media (max-width: 480px) {
    panel { padding: 4px; }
}

@keyframes glow {
    from { border-color: var(--accent); }
    to   { border-color: #F5C542; }
}

quest:claimable { animation: glow 1.2s ease-in-out infinite alternate; }
button { transition: 150ms ease-out; }
button:hover { transform: scale(1.05); }
window { background-image: linear-gradient(160deg, #1B1D24, #2A1E3A); box-shadow: 0 4px 12px rgba(0, 0, 0, 0.6); }
```

`@media` supports `width`, `height`, their `min-` and `max-` forms, `orientation`, `aspect-ratio`, `and`, `not` and commas, measured in GUI pixels. Animations share one clock, so matching elements pulse together. `@import`, `@font-face` and other at-rules are ignored with a warning.

---

## Pictures, GIFs and Videos

Wherever Trailmark takes a picture (icons, quest text, backgrounds, decorations, CSS), it accepts:

- A texture ID, such as `minecraft:textures/item/apple.png`. Resource packs apply.
- `media/<file>`: a file in the quest pack's `media` folder. The server sends it to players who need it, and clients keep it in a cache by its hash, so it is downloaded once.
- A web link, when the **Web Media** setting is on.
- A YouTube link (`youtube.com/watch?v=`, `youtu.be/`, `/shorts/`...), anywhere a video file goes: a decoration on the map, a quest's picture (its **Watch Video** button opens it), the video player. It works like a video file: it shows the video's thumbnail until it's played, has the same controls, pauses while off screen, and plays its sound only where a video file's sound would (a decoration with **Video Sound** on, the video player). A `t=` in the link sets where it starts. See [YouTube Videos](#youtube-videos).

PNG, JPEG, BMP and animated GIF work out of the box, and so do OGG and WAV sounds. WebP, videos (MP4, WebM, MKV, MOV, M4V, AVI) and other sounds (MP3, FLAC, M4A, AAC, Opus) need FFmpeg. When quest media needs FFmpeg and it isn't installed, the quest book offers to download it the way Dungeon Config's settings do: into `config/dungeonconfig/bin`, shared with Dungeon Config and other mods, with **Don't Ask Again** shared too. An FFmpeg already on the system PATH is used as it is. Videos wait on their first frame until the player clicks play, and pause again while they're off screen. While a file is being fetched, its box and the sound and video players show how far along it is: a percentage, or the size so far if the server doesn't report one. Links connect the way browsers do, IPv6 first, falling back to Java's own client.

Servers can limit shared files with **Largest Shared Media File** or turn sharing off with **Share Quest Media**.

### How Media Reaches Players

Media from the server's `media` folder is sent only when a quest that shows it is opened, never all at once on join. The server first sends the file's hash, and a player who already has that file skips the download entirely. Downloads are built so they never get in the way of playing:

- Files go out in small pieces, read from disk as they're sent, so even a large video holds only a few hundred kilobytes of server memory per download.
- Each player gets at most **Media Upload Speed** (512 KB/s by default), and media waits whenever a player's connection is busy, so game traffic always goes first.
- Each player can have four files queued and 120 requests a minute; anything beyond that is refused, not queued.
- Only files inside the `media` folder with a picture or video extension are ever served.
- Players check every finished file against its hash and throw it away if it doesn't match, and never accept a file larger than their **Disk Cache Size** (or 256 MB).
- Downloading, decoding and cache upkeep run on their own low-priority threads, so they don't compete with chunk loading or the singleplayer server.

Downloaded files are kept in `config/trailmark/cache/media`, named by their hash, and reused across sessions and servers. The cache stays under **Disk Cache Size** (512 MB by default) by deleting the files used least recently. **Clear Media Cache** in the settings deletes all of it; quests on screen simply download what they need again.

## YouTube Videos

Put a YouTube video on a quest page with the `youtube` tag, or with a Markdown picture whose link is a YouTube video:

```html
<youtube src="https://www.youtube.com/watch?v=dQw4w9WgXcQ" title="Getting started" volume="40" start="1m30s">
```

```markdown
![Getting started](https://youtu.be/dQw4w9WgXcQ?t=90)
```

| Attribute | Default | Meaning |
|---|---|---|
| `src` (or `id`) | | A YouTube link (`watch?v=`, `youtu.be/`, `/embed/`, `/shorts/` and `/live/` all work) or the 11-character video ID. |
| `title` | none | Shown on the thumbnail before the video starts. |
| `start` | `0`, or the link's `t=` | Where to start: `90`, `90s` or `1m30s`. |
| `volume` | the book's `youtube_volume` | How loud this video starts, 0 to 100. |
| `width`, `height` | 240 wide, 16:9 | Size in GUI pixels. Never wider than the page. |

A YouTube link also works anywhere a video file does, such as a decoration on the map or a quest's picture (see [Pictures, GIFs and Videos](#pictures-gifs-and-videos)). Only one YouTube video plays at a time, wherever it is.

Videos play right inside the quest book, with nothing else to install: Trailmark comes with Velocity, a small YouTube player, and plays through the same FFmpeg that Dungeon Config uses. If FFmpeg isn't installed yet, starting a video brings up Trailmark's usual offer to download it. Sound goes through Minecraft's own audio, following the Master and Jukebox/Note Blocks volumes. A video has the same controls as every other video in the book: a seek strip you can click or drag, play and pause, the time, a volume slider with a mute button, and a button to continue on YouTube. They show while the mouse is over the video or it's paused. Clicking the video itself pauses or plays it, and clicking it once it has ended plays it again. The stream is picked to match the video's size on screen, up to 1080p.

The volume a player picks is remembered and used for every YouTube video from then on, in every quest pack. Until they pick one, each video starts at its own `volume`, or the book's `youtube_volume`.

Only one video plays at a time, and it stops as soon as it leaves the screen: another quest is opened, the page is closed, or the book is closed. A video that can't be played (it was removed, is private, or is age-restricted) shows a notice, and clicking it opens the video on YouTube. Only public videos play; Trailmark never signs in to YouTube.

With **Play YouTube in Game** turned off, or when FFmpeg can't be had (declined for good, or no build for the system), the embed shows the video's thumbnail and opens it in the player's browser, through Minecraft's usual link confirmation.

---

## Integrations

Every integration turns on by itself when its mod is installed, and nothing breaks when it isn't.

| Mod | What You Get |
|---|---|
| FTB Teams | Progress is shared by FTB team. FTB team stages count as stages. |
| FTB Chunks | `claim_chunks` task (`count`, `force_loaded`) and `extra_chunks` reward (`claim`, `force_load`). |
| JEI, REI, EMI | Press R or U over any item in the quest book for recipes or uses. |
| Accessories, Curios, Trinkets | Worn items count for item tasks; item rewards can go into accessory slots; `extra_slots` reward (`slot`, `amount`). |
| Backpacks and shulker boxes | Items inside count for item tasks and can be handed in. |
| Applied Energistics 2, Refined Storage 2 | Items in a network count while the player carries a linked, powered wireless terminal. Taking items out needs the **Take from Storage Networks** setting. |
| Cobblemon | `pokemon` task (`action`: `catch`, `defeat`, `evolve` or `trade`; `properties` in Cobblemon's syntax; `count`), `pokedex` task (`count`, `caught`) and `pokemon` reward (`properties`). |
| KubeJS | Scripts get `Trailmark` and `TrailmarkEvents`; KubeJS stages count as stages. |
| EasyNPC and similar | Add a command action `/trailmark signal @p talked_to_bob` and a `custom` task named `talked_to_bob`. |
| JourneyMap, Xaero's Minimap | Unfinished `location` tasks of tracked quests show as waypoints on the minimap and world map, and vanish once done or untracked. Xaero's waypoints are temporary, so they never end up in your saved waypoints. Turn off with **Map Waypoints**. |
| Patchouli | `patchouli:` links in quest text open books, entries and pages. |
| Create (NeoForge) | `create_rotation` task (`speed` in RPM) and `create_stress` task (`capacity` in stress units): a machine or network near the player. |
| Mekanism (NeoForge) | `mekanism_chemical` task (`chemical`, `amount` in mB): stored in a tank near the player. Also see `energy`. |
| Any energy mod (NeoForge) | `energy` task (`energy` in FE): stored in any block near the player that holds Forge Energy, such as Mekanism, Thermal, Powah or Immersive Engineering machines. |
| GeckoLib | Icons of GeckoLib mobs, blocks and items can play any of their animations (**Play Animation** in the Icon Editor lists them). |

The machine tasks (`create_rotation`, `create_stress`, `mekanism_chemical`, `energy`) look at blocks within `radius` (default 16) of the player, can be narrowed to one `block`, and keep the best value they've seen, so a machine that stops later doesn't undo the progress. If a pack uses one of these tasks without the mod, the quest still loads and shows the task as unavailable.

```json
{ "type": "pokemon", "action": "catch", "properties": "species=pikachu shiny=yes" }
{ "type": "pokemon", "properties": "eevee level=10" }
{ "type": "extra_slots", "slot": "ring", "amount": 1 }
{ "type": "create_rotation", "speed": 64, "block": "create:mechanical_press" }
{ "type": "create_stress", "capacity": 2048 }
{ "type": "mekanism_chemical", "chemical": "mekanism:hydrogen", "amount": 10000 }
{ "type": "energy", "energy": 1000000 }
```

---

## Importing from FTB Quests

Make sure the pack's FTB Quests files are in `config/ftbquests/quests`, then run:

```
/trailmark import ftbquests
```

Chapters, quests, tasks, rewards, reward tables, groups, images and dependencies are converted. Team progress is read from the world's `ftbquests` folder and imported too. Anything without an exact match is listed in `config/trailmark/ftb-import-report.md`, and the IDs are mapped in `ftb-import-map.json`. Add `overwrite` to replace an existing book.

---

## Commands

`/tm` is a shorter alias for `/trailmark`.

Progress is saved in the world's `data/trailmark/` folder, one `<uuid>.json` file per team. A solo player's team is the player, so their file is named after their own UUID and also holds their tracked quests; parties and team mods share one file named after the team. Worlds saved by older versions (`data/trailmark_progress.dat`) are converted on first load, and the old file is kept as `trailmark_progress.dat.migrated`. A file that can't be read is renamed to `.json.broken` and that team starts fresh, so check the log if a team's progress goes missing.

| Command | Permission | Purpose |
|---|---|---|
| `/trailmark open [quest]` | Everyone | Open the quest book, optionally on a quest. |
| `/trailmark reload` | 2 | Reload the quest files. |
| `/trailmark problems` | 2 | List problems found while loading. |
| `/trailmark complete <players> <quest>` | 2 | Complete a quest for the players' teams. |
| `/trailmark reset <players> <quest>` / `reset <players> all` | 2 | Reset a quest, or everything. |
| `/trailmark progress <players> <quest> <task> <value>` | 2 | Set a task's progress. |
| `/trailmark signal <players> <name> [amount]` | 2 | Fire a custom signal. |
| `/trailmark stage add\|remove <players> <stage>` | 2 | Change stages. |
| `/trailmark counter set\|add <players> <name> <value>` | 2 | Change team counters. |
| `/trailmark lang export <locale>` | 2 | Write a translation file. |
| `/trailmark import ftbquests [overwrite]` | 2 | Import FTB Quests. |
| `/trailmark party create\|invite\|join\|leave\|kick\|rename\|info` | Everyone | Built-in parties, when no team mod is installed. |

---

## Settings

Open them from the gear button in the quest book, or edit `config/trailmark.json5`.

| Setting | Default | Purpose |
|---|---|---|
| Quest Screen Scale | 100% | Size of the quest screen relative to the GUI scale. |
| Animation Speed | 100% | `0` turns animations off. |
| Reduced Motion | Off | Simple fades instead of slides and zooms. |
| Show Locked Quests | On | Quests the pack hides stay hidden either way. |
| Inventory Button, Advancements Button | On | The quest book tabs. They move aside if another mod's button is in the way. |
| Connection Lines | Pack Default | Right angles or straight lines between quests, overriding the quest pack's choice. |
| Notifications, Sounds | On | |
| Quest Language | Game language | |
| Show Tracker, Tracker Position, Maximum Tracked Quests, Tracker Opacity | On, Right, 3, 85% | |
| Avoid Other HUD Elements | On | The tracker slides along its edge, or moves to the other side of the screen, to stay clear of the scoreboard, effects and other mods' HUDs. A few mods draw their HUD outside Minecraft's usual drawing calls (JourneyMap's minimap is one); the tracker can't see those, so move it with the position and offset settings if it lands on one. |
| Map Waypoints | On | Tracked quests' destinations as JourneyMap or Xaero's Minimap waypoints. |
| Horizontal Offset, Vertical Offset | 0 | Fine-tune the tracker's position. |
| Track New Quests | Off | |
| Web Media, Play Videos, Media Cache Size | On, On, 128 MB | |
| Disk Cache Size, Clear Media Cache | 512 MB | Downloaded pictures and videos kept in `config/trailmark/cache/media`. See [How Media Reaches Players](#how-media-reaches-players). |
| Play YouTube in Game, YouTube Volume | On, pack default | See [YouTube Videos](#youtube-videos). The volume is set for you whenever you change it on a video. |
| Prefer My Style | Off | Your resource packs and `style.css` win over the server's theme. See [Styling with CSS](#styling-with-css). |
| Item Check Delay, World Check Interval | 10, 20 ticks | |
| Completion Announcements | Team | Who sees a chat message when a quest is completed. |
| Give Quest Book | Off | Give new players the book item. |
| Allow Parties, Use Team Mods | On | |
| Take from Storage Networks | Off | See [Integrations](#integrations). |
| Editor Permission Level | 2 | Who may edit quests in game. 1 to 4 (operator levels); players who aren't operators can never edit. The server checks every change, and the Edit Quests option appears or disappears as soon as a player is opped or de-opped. |
| Pack Namespace | `pack` | Changing it changes every quest ID and resets progress. |
| Share Quest Media, Largest Shared Media File | On, 32 MB | |
| Media Upload Speed | 512 KB/s | Most quest media sent to each player per second. |
| Hot Reload | On | Reload the quest book when its files change, and tell operators. |

---

## For Developers

Everything public lives in `com.frikinjay.trailmark.api`. Integrations should depend on Trailmark as `compileOnly` and only touch it when the mod is loaded.

### The Facade

`TrailmarkApi` covers what most mods need:

```java
TrailmarkApi.fireCustom(player, "found_ruins", 1);          // drives "custom" tasks
TrailmarkApi.completeQuest(server, teamId, questId);
TrailmarkApi.setTaskProgress(player, questId, "kill_bosses", 3);
TrailmarkApi.setStage(player, "nether_unlocked", true);
TrailmarkApi.addCounter(server, teamId, "reputation", 10);
TrailmarkApi.openBook(player, questId);
ProgressView view = TrailmarkApi.progress(player);           // read-only progress
```

### Events

`TrailmarkEvents` has `BOOK_LOADED`, `QUEST_UNLOCKED`, `TASK_COMPLETED`, `QUEST_COMPLETED`, `CHAPTER_COMPLETED`, `BEFORE_CLAIM` (return `false` to block a claim), `REWARD_CLAIMED` and `QUEST_RESET`.

```java
TrailmarkEvents.QUEST_COMPLETED.register((server, team, quest, player) -> { ... });
```

Vanilla advancements can react too, with the criteria `trailmark:quest_completed` (`quest`, `chapter`, `player`) and `trailmark:chapter_completed` (`chapter`, `player`).

### Custom Task Types

A task is a class with a `MapCodec`, registered once during setup. Tasks subscribe to signals; Trailmark only delivers signals to tasks that are unlocked and unfinished for the player's team, so even large packs stay cheap.

```java
public final class FishTask extends Task {
    public static final MapCodec<FishTask> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.LONG.optionalFieldOf("count", 1L).forGetter(t -> t.count)
    ).apply(i, FishTask::new));
    public static final TaskType<FishTask> TYPE = TaskType.of(ResourceLocation.fromNamespaceAndPath("mymod", "fish"), CODEC, () -> new FishTask(1));

    private final long count;

    public FishTask(long count) { this.count = count; }

    @Override public TaskType<?> type() { return TYPE; }
    @Override public long target() { return count; }

    @Override
    public void subscribe(TaskSubscriptions subscriptions) {
        subscriptions.on(MyFishSignal.class, (ctx, signal) -> ctx.addProgress(1));
    }

    @Override
    public Component defaultTitle() { return Component.translatable("mymod.task.fish", count); }
}

TrailmarkApi.registerTaskType(FishTask.TYPE);
TrailmarkApi.signal(new MyFishSignal(player));   // your signal implements Signal
```

Give a signal a `key()` (such as an entity type) and subscribe with `subscriptions.on(type, key, handler)` to receive only matching signals. Rewards work the same way with `Reward`, `RewardType` and `registerRewardType`. Name your types with `<namespace>.task_type.<namespace>.<path>` and `<namespace>.reward_type.<namespace>.<path>` lang keys.

Books that use a type from a mod that isn't installed keep the data and show the task as unavailable, so removing a mod never corrupts a pack.

### Item Sources, Teams and Stages

- `ItemSource`: another place item tasks look for items, such as a backpack or a storage network. Return `readOnly() == true` if items can't be taken out.
- `TeamProvider`: who shares progress. The highest-priority available provider wins; Trailmark's parties are priority 0 and FTB Teams is 100.
- `StageProvider`: answers "does this player have stage X" for another stage system, and optionally writes stages.

### Client Extension Points

- `RecipeViewers.register(viewer)` adds a recipe viewer.
- `RichText.setEngine(...)`, `Media.setLoader(...)` and `Styles.setEngine(...)` replace the rich text, media and style systems.
- `Styles.get(element, classes, id, states)` gives your own screens the pack's styling, and `Styles.transition(key, style)` adds CSS transitions.
