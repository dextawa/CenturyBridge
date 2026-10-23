# cb-armortest

A 1.20.4-era server mod (intermediary names; fabric-lifecycle-events-v1, fabric-events-interaction-v0 for
FakePlayer) for the s4fin world/item bridges of step 1.20.4 -> 1.20.5. When the server has started it checks,
printing `CBARMORTEST OK|FAIL ...` per check and `CBARMORTEST DONE n/m` at the end:

- a mod `ArmorMaterial` on a mod `ArmorItem`: defense, toughness, durability (from the material),
  `getMaterial()` is the mod's object, default attribute modifiers by slot, an armor stand's armor value;
  the vanilla `ArmorMaterials` enum;
- a mod `DyeableArmorItem`: get/set/clear color, `dyeArmor`, vanilla's armor dye recipe, washing in a water
  cauldron (the mod registers its item with `DYED_ITEM`, as 1.20.4 mods had to);
- a mod `HorseArmorItem(protection, name)`: `getProtection()`, texture name, a horse equipped through
  `equipArmor` getting the armor value;
- a `MerchantOffer` with 1.20.4 `ItemStack` costs: `satisfiedBy` / `take`, `createTag` and `new
  MerchantOffer(tag)`, `MerchantOffers` tag round trip, a villager saving and loading it;
- recipe serializers: vanilla's shaped `toNetwork` against 1.20.4's byte layout written by hand, and a mod
  serializer extending it that writes a marker before `super.toNetwork` (marker exactly once, 1.20.4's layout
  after it, round trip with nothing left over).

Build against 1.20.4 (`--release 17`) with the 1.20.4 intermediary jar, its libraries and the two Fabric API
modules; convert one step (`Main convert core/segments 1.20.4 1.20.5 ...`, `-Dcb.wiring=` the step wiring);
boot a Fabric 1.20.5 server with fabric-api and the built 1.20.5 step layer.

Client-only parts the client fixture should cover: the mod armor's texture (`textures/models/armor/
testium_layer_1.png`, namespace cb_armortest), the dyed helmet's tint, the horse armor's texture, trim
textures through `ArmorTrim.innerTexture/outerTexture(material)`.
