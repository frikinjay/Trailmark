![Trailmark banner. On the left, the Trailmark icon (a green folded map with a gold pin), the name TRAILMARK in large blocky white letters, a short gold rule, the line "A quest book you can restyle from top to bottom, with 3D icons and built-in sound and video." and two tags, Fabric and NeoForge. On the right, a screenshot of the quest book in its default dark teal theme, open on the Welcome chapter: chapter tabs along the top, a chapter list with progress on the left, a map of linked quests in the middle, and an open quest page on the right with formatted text, a picture, a quote, a list, a small table and a gold Claim Rewards button.](https://raw.githubusercontent.com/frikinjay/Trailmark/main/description/hero.png)

Trailmark is a quest book for Fabric and NeoForge. Chapters are laid out as maps, quest pages hold proper formatted text, and the book's whole look comes from a stylesheet your pack can replace.

[**Documentation**](https://github.com/frikinjay/Trailmark/blob/main/DOCUMENTATION.md) · [**Example quest pack**](https://github.com/frikinjay/Trailmark/tree/main/example-pack) · [**API for mod developers**](https://github.com/frikinjay/Trailmark/tree/main/src/main/java/com/frikinjay/trailmark/api)

![01, Icons: "Whole scenes as icons". Text: "A quest icon can be a full structure, a mob mid-animation, a player in any skin and pose, a fluid, a video or a sound." Left screenshot, captioned "Map: structures on the map, an ender dragon as an icon", shows the Into the World chapter, with small 3D structures beside its quests and an animated ender dragon as the icon of the quest The End. Right screenshot, captioned "Icon editor: any icon, with a live preview", shows the icon editor set to a player icon, with fields for name, skin, arms, scale, camera and animation beside a 3D preview of the player.](https://raw.githubusercontent.com/frikinjay/Trailmark/main/description/icons.png)

![02, Sound and video: "Players on the page". Text: "Quests carry their own sound or video player, chapters can have music, and YouTube plays in place." Left screenshot, captioned "Quest: play, stop and seek without leaving the quest", shows the Sights and Sounds chapter with the quest Press Play open, its play and stop buttons, seek bar and volume slider above the description. Right screenshot, captioned "Map: chapter music and a locked video", is a close-up of that chapter's map: a chapter music button, a row of quests, a tilted gold label and a locked video thumbnail labelled "Unlocks after Fluids".](https://raw.githubusercontent.com/frikinjay/Trailmark/main/description/media.png)

![03, Themes: "Restyle all of it". Text: "One CSS file styles the whole book, from colours and fonts to quest shapes. Below, the chapter from above in a vanilla theme." Left screenshot, captioned "Vanilla theme: slot-style quests in Minecraft's colours", shows the same Sights and Sounds chapter restyled with grey stone panels, square inventory-slot quests, yellow headings and green buttons. Right screenshot, captioned "Quest page: text styles follow the theme", is a close-up of the Welcome quest page in that theme, with a yellow "Welcome, traveller!" heading, a yellow-framed icon and coloured sample text.](https://raw.githubusercontent.com/frikinjay/Trailmark/main/description/themes.png)

- Every part of the book can be restyled, including the HUD tracker, toasts and pop-ups
- Load your own fonts from a resource pack or the config folder
- Give single quests or chapters their own style with a class name
- Players can switch on Prefer My Style to keep their own theme over the pack's

![04, For pack makers: "Edit while you play". Text: "Lay out the map in game. JSON files reload as you save, and FTB Quests books import with their progress." Left screenshot, captioned "Edit mode: right-click the map to add anything", shows the book in edit mode with the right-click menu open, offering a new quest, label, picture, video or sound, or editing the chapter. Right screenshot, captioned "Settings: book-wide options in one form", shows the Book Settings form with the pack title, icon, tabs, starting chapter and reward options.](https://raw.githubusercontent.com/frikinjay/Trailmark/main/description/editor.png)

- Drag, multi-select, resize and nudge with the arrow keys, with undo, redo and templates. Only operators can edit
- Map decorations: labels, pictures, videos, sounds and 3D icons, which can be layered, tilted, or hidden until a quest is done
- Changes sync to players file by file, and quest text can be translated in game

### Quests

- Over 20 task types, from gathering, crafting and kills to biomes, structures, dimensions, stats and custom signals
- Rewards include items, pick-one choices, loot tables, XP, commands and effects, claimed per player or per team
- Quests can repeat daily, weekly or on a cooldown, run against a timer, lock out other branches, or belong to seasonal chapters that open on their own dates
- Quest text supports Markdown, tables, call-outs and spoilers, shows items, mobs, blocks and players inline, and links to quests, chapters, Patchouli entries and websites
- A HUD tracker with a compass arrow for places to visit
- Teams share progress through Trailmark's own parties or FTB Teams

### Works with

JEI, REI and EMI · JourneyMap and Xaero's Minimap · Patchouli · FTB Teams and FTB Chunks · Curios and Trinkets · Refined Storage and Applied Energistics 2 · Create · Mekanism · Cobblemon

All optional. A quest that needs a missing mod just shows as unavailable until it's installed.

### Good to know

- Install on both the client and the server. Fabric also needs Fabric API; Dungeon Config is bundled.
- Press **N** to open the book.
- Video and MP3 playback use FFmpeg. If it's missing, Trailmark offers to download it.
- New to quest packs? Start from the [example quest pack](https://github.com/frikinjay/Trailmark/tree/main/example-pack) and change what you like.
