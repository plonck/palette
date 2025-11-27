# Palette

Palette is a lightweight Fabric utility mod designed to programmatically extract **Minecraft's internal map color data**. It generates raw CSV tables mapping block identifiers to map colors, and map color IDs to their specific RGB values.

This tool is useful for map tool developers, pixel art generators, and data enthusiasts who need accurate, version-specific color data directly from the game's code.

## Output

### `colors.csv`

This file contains the RGB values for every valid map color ID. It accounts for the four shading variants (brightness levels) that Minecraft applies to base map colors.

**Format:** `COLOR_ID, R, G, B`

- `COLOR_ID`: Calculated as `4 * COLOR_BASE_ID + COLOR_SHADE_ID`
- `R`, `G`, `B`: Standard 0-255 integer values for red, green, and blue


**Example:**

```
4,89,125,39
5,109,153,48
6,127,178,56
7,67,94,29
...
```

### `blocks.csv`

This file maps every registered Minecraft block to its base map color ID.

**Format:** `BLOCK_ID,COLOR_BASE_ID`

- `BLOCK_ID:` Namespaced resource location of the block (e.g., `minecraft:grass_block`).
- `COLOR_BASE_ID:` Integer ID of the base map color associated with the block's default state.

**Example:**

```
minecraft:air,0
minecraft:stone,11
minecraft:granite,10
minecraft:grass_block,1
minecraft:diorite,14
...
```

## Usage

You do not need to compile the code to get the data. CSV files for specific Minecraft versions are automatically generated and attached to a release.

1. Go to the [GitHub Releases](https://github.com/plonck/palette/releases) page.
2. Select the version matching your target Minecraft version.
3. Download `colors.csv` and `blocks.csv` from the *Assets* list.

If you do wish to compile the files locally, simply run the `build.sh` script.
