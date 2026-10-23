"""Wiring for 1.21.1's ArmorItem / ArmorMaterial / ArmorMaterials / ArmorItem.Type on 1.21.2
(s7item, step 1.21.1 -> 1.21.2; semantics in rt/CbItemArmor and the facades ArmorMaterial, ArmorMaterials, ArmorItem).

  classRenames   ArmorMaterial (1.21.2 reused the name for another record) and its Layer, ArmorMaterials
                 (an interface of 1.21.2 materials now) -> the facades; ArmorItem.Type -> 1.21.2's ArmorType
                 (the same constants and members, hasTrims() aside)
  superRenames   ArmorItem / AnimalArmorItem -> facades: a subclass keeps 1.21.1's constructor (material holder) and
                 its super calls reach 1.21.1's bodies there
  ctorRedirects  new ArmorItem / AnimalArmorItem -> 1.21.2's built from the converted material
  members        getType/getMaterial/getDefense/getToughness/getEquipmentSlot/getEquipSound: a call reaches the
                 class's own override (adapterHooks, Legacy*), else 1.21.1's body; a super call is 1.21.1's body
  fields         the protected type/material, DISPENSE_ITEM_BEHAVIOR, the ARMOR_MATERIAL registry and its key

  python gen_s7item_armor.py <out fragment.json>
"""
import json
import sys

RT = "top/dext/centurybridge/step/v1_21_2/rt/CbItemArmor"
FAC = "top/dext/centurybridge/step/v1_21_2/facade/"
ARMOR = "net/minecraft/class_1738"
TYPE = "net/minecraft/class_1738$class_8051"
ANIMAL = "net/minecraft/class_4059"

# (member, Legacy interface, basis)
MEMBERS = [
    ("method_48398()Lnet/minecraft/class_1738$class_8051;", "LegacyType", "getType()"),
    ("method_7686()Lnet/minecraft/class_6880;", "LegacyMaterial", "getMaterial()"),
    ("method_7687()I", "LegacyDefense", "getDefense()"),
    ("method_26353()F", "LegacyToughness", "getToughness()"),
    ("method_7685()Lnet/minecraft/class_1304;", "LegacyEquipmentSlot", "getEquipmentSlot()"),
    ("method_31570()Lnet/minecraft/class_6880;", "LegacyEquipSound", "getEquipSound()"),
]
# AnimalArmorItem's own (member, Legacy interface, basis); isEnchantable(stack) is the Item members' interface
ANIMAL_MEMBERS = [
    ("method_18454()Lnet/minecraft/class_2960;", "LegacyTexture", "getTexture()"),
    ("method_57335()Lnet/minecraft/class_2960;", "LegacyOverlayTexture", "getOverlayTexture()"),
    ("method_55756()Lnet/minecraft/class_4059$class_9076;", "LegacyBodyType", "getBodyType()"),
]


def main():
    out = {
        "classRenames": {
            "net/minecraft/class_1741": FAC + "ArmorMaterial",
            "net/minecraft/class_1741$class_9196": FAC + "ArmorMaterial$Layer",
            "net/minecraft/class_1740": FAC + "ArmorMaterials",
            TYPE: "net/minecraft/class_8051",
            # Equipable: the slot/sound/swap interface 1.21.2 replaced by the EQUIPPABLE component (CbItemEquipable)
            "net/minecraft/class_5151": FAC + "Equipable",
            # ElytraItem: a plain item with components since 1.21.2
            "net/minecraft/class_1770": FAC + "ElytraItem",
        },
        "facadeGaps": {
            "net/minecraft/class_1770.method_7836(Lnet/minecraft/class_1937;Lnet/minecraft/class_1657;Lnet/minecraft/class_1268;)Lnet/minecraft/class_1271;":
                "use(level, player, hand) answered an InteractionResultHolder in 1.21.1; 1.21.2's Item.use has the same "
                "parameters, which Java cannot declare a second time on the facade. Calls take the Item.use instance "
                "redirect and a subclass's super.use(..) the Item.use override hook's super bridge (CbItemResults); "
                "1.21.1's body was swapWithEquipmentSlot, which 1.21.2's Item.use does for the swappable EQUIPPABLE "
                "an ElytraItem gets (CbItemEquipable)",
        },
        # 1.21.1's vanilla Equipables implement facade Equipable again (CbItemEquipable*Mixin): their members exist
        "covers": [
            "net/minecraft/class_1819.method_7685()Lnet/minecraft/class_1304;",
            "net/minecraft/class_2190.method_7685()Lnet/minecraft/class_1304;",
            "net/minecraft/class_5815.method_7685()Lnet/minecraft/class_1304;",
            "net/minecraft/class_5815.method_31570()Lnet/minecraft/class_6880;",
        ],
        "superRenames": {ARMOR: FAC + "ArmorItem", ANIMAL: FAC + "AnimalArmorItem"},
        "ctorRedirects": {
            ARMOR + ".<init>(Lnet/minecraft/class_6880;Lnet/minecraft/class_1738$class_8051;Lnet/minecraft/class_1792$class_1793;)V":
                RT + "#newArmorItem",
            ANIMAL + ".<init>(Lnet/minecraft/class_6880;Lnet/minecraft/class_4059$class_9076;ZLnet/minecraft/class_1792$class_1793;)V":
                RT + "#newAnimalArmorItem",
        },
        "instanceRedirects": {},
        "superRedirects": {},
        "adapterHooks": [],
        "instanceFieldGetters": {
            ARMOR + ".field_41933:Lnet/minecraft/class_1738$class_8051;": RT,
            ARMOR + ".field_7881:Lnet/minecraft/class_6880;": RT,
        },
        "fieldGetters": {
            ARMOR + ".field_7879:Lnet/minecraft/class_2357;": RT,
            "net/minecraft/class_7923.field_48976:Lnet/minecraft/class_2378;": RT,
            "net/minecraft/class_7924.field_48977:Lnet/minecraft/class_5321;": RT,
        },
        "staticRedirects": {
            # ArmorItem.dispenseArmor(source, stack): 1.21.2's EquipmentDispenseItemBehavior.dispenseEquipment
            ARMOR + ".method_7684(Lnet/minecraft/class_2342;Lnet/minecraft/class_1799;)Z": "net/minecraft/class_10205#method_64106",
        },
    }
    out["instanceRedirects"][TYPE + ".method_56691()Z"] = RT
    out["instanceRedirects"]["net/minecraft/class_1770.method_7836(Lnet/minecraft/class_1937;Lnet/minecraft/class_1657;"
                             "Lnet/minecraft/class_1268;)Lnet/minecraft/class_1271;"] =         "top/dext/centurybridge/step/v1_21_2/rt/CbItemEquipable"
    for member, legacy, why in MEMBERS:
        name = member[:member.index("(")]
        out["instanceRedirects"][ARMOR + "." + member] = RT
        out["superRedirects"][ARMOR + "." + member] = RT + "#super_" + name
        out["adapterHooks"].append({"under": ARMOR, "adapter": RT + "$" + legacy, "requires": [member], "synth": [],
                                    "basis": "a 1.21.1 ArmorItem subclass overriding " + why
                                             + ": calls of it reach the override (CbItemArmor)"})
    # 1.21.1's AnimalArmorItem was an ArmorItem; 1.21.2's is not: its inherited ArmorItem members are keyed on it too,
    # so a call through it takes the static typed for it (the ArmorItem one would not verify)
    for member, legacy, why in MEMBERS:
        out["instanceRedirects"][ANIMAL + "." + member] = RT
    for member, legacy, why in ANIMAL_MEMBERS:
        out["instanceRedirects"][ANIMAL + "." + member] = RT
        out["adapterHooks"].append({"under": ANIMAL, "adapter": RT + "$" + legacy, "requires": [member], "synth": [],
                                    "basis": "a 1.21.1 AnimalArmorItem subclass overriding " + why
                                             + ": calls of it reach the override (CbItemArmor)"})
    out["instanceRedirects"][ANIMAL + ".method_7870(Lnet/minecraft/class_1799;)Z"] = RT
    json.dump(out, open(sys.argv[1], "w", encoding="utf-8", newline="\n"), indent=1)


if __name__ == "__main__":
    main()
