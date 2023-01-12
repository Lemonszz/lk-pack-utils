# LK Utils

## ???

Additional Utiliites for Mod/Data/Resource Packs

## License

Do whatever idc (CC0)

## Docs

### Advanced Ore Feature

Vanilla's `minecraft:ore` features doesn't use a `BlockStateProvider` for the ore block, this one does.

#### Example:
```json
{
  "type": "lkutils:ore",
  "config": {
    "discard_chance_on_air_exposure": 0.0,
    "size": 9,
    "targets": [
      {
        "state": {
          "type": "minecraft:weighted_state_provider",
          "entries": [
            {
              "data": {
                "Name": "minecraft:iron_ore"
              },
              "weight": 25
            },
            {
              "data": {
                "Name": "minecraft:raw_iron_block"
              },
              "weight": 1
            }
          ]
        },
        "target": {
          "predicate_type": "minecraft:tag_match",
          "tag": "minecraft:stone_ore_replaceables"
        }
      },
      {
        "state": {
          "type": "minecraft:weighted_state_provider",
          "entries": [
            {
              "data": {
                "Name": "minecraft:deepslate_iron_ore"
              },
              "weight": 25
            },
            {
              "data": {
                "Name": "minecraft:raw_iron_block"
              },
              "weight": 1
            }
          ]
        },
        "target": {
          "predicate_type": "minecraft:tag_match",
          "tag": "minecraft:deepslate_ore_replaceables"
        }
      }
    ]
  }
}
```