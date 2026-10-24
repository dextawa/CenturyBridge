"""Wiring for 1.21.1's ArmorItem / ArmorMaterial / ArmorMaterials / ArmorItem.Type on 1.21.2
(s7item, step 1.21.1 -> 1.21.2; semantics in rt/CbItemArmor and the facades ArmorMaterial, ArmorMaterials, ArmorItem).

  classRenames   ArmorMaterial (1.21.2 reused the name for another record) and its Layer, ArmorMaterials
                 (an interface of 1.21.2 materials now) -> the facades; ArmorItem.Type -> 1.21.2's ArmorType
                 (the same constants and members, hasTrims() aside); Equipable -> Object and ElytraItem -> Item
                 (gone; their 1.21.1 instances include the vanilla elytra and carved pumpkin, which are no facade
                 instances): type tests and casts ask CbItemTypes, members are statics (CbItemEquipable)
  superRenames   ArmorItem / AnimalArmorItem / Equipable / ElytraItem -> facades: a subclass (implementor) keeps
                 1.21.1's constructor and its super calls reach 1.21.1's bodies there
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
EQUIPABLE = "net/minecraft/class_5151"
ELYTRA = "net/minecraft/class_1770"
TYPES = "top/dext/centurybridge/step/v1_21_2/rt/CbItemTypes"
EQUIP = "top/dext/centurybridge/step/v1_21_2/rt/CbItemEquipable"

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
            # Equipable: the slot/sound/swap interface 1.21.2 replaced by the EQUIPPABLE component (CbItemEquipable).
            # Its 1.21.1 instances on 1.21.2 are the facade's implementors and two vanilla objects with no class of
            # their own any more (the elytra item, the carved pumpkin block): a reference to it is an Object, a type
            # test and a cast ask CbItemTypes, its members are statics
            EQUIPABLE: "java/lang/Object",
            # ElytraItem: a plain item with components since 1.21.2: a reference to it is an Item (CbItemTypes)
            ELYTRA: "net/minecraft/class_1792",
        },
        "instanceOfRedirects": {EQUIPABLE: TYPES + "#isEquipable", ELYTRA: TYPES + "#isElytraItem"},
        "checkCastRedirects": {EQUIPABLE: TYPES + "#castEquipable", ELYTRA: TYPES + "#castElytraItem"},
        # 1.21.1's vanilla Equipables implement facade Equipable again (CbItemEquipable*Mixin): their members exist
        "covers": [
            "net/minecraft/class_1819.method_7685()Lnet/minecraft/class_1304;",
            "net/minecraft/class_2190.method_7685()Lnet/minecraft/class_1304;",
            "net/minecraft/class_5815.method_7685()Lnet/minecraft/class_1304;",
            "net/minecraft/class_5815.method_31570()Lnet/minecraft/class_6880;",
        ],
        "superRenames": {ARMOR: FAC + "ArmorItem", ANIMAL: FAC + "AnimalArmorItem",
                         EQUIPABLE: FAC + "Equipable", ELYTRA: FAC + "ElytraItem"},
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
    # ElytraItem's members through an ElytraItem (an Item since the rename) reference
    out["instanceRedirects"][ELYTRA + ".method_7836(Lnet/minecraft/class_1937;Lnet/minecraft/class_1657;"
                             "Lnet/minecraft/class_1268;)Lnet/minecraft/class_1271;"] = EQUIP
    out["instanceRedirects"][ELYTRA + ".method_7878(Lnet/minecraft/class_1799;Lnet/minecraft/class_1799;)Z"] =         "top/dext/centurybridge/step/v1_21_2/rt/CbItemMembers"
    out["staticRedirects"][ELYTRA + ".method_7804(Lnet/minecraft/class_1799;)Z"] = FAC + "ElytraItem"
    out["ctorRedirects"][ELYTRA + ".<init>(Lnet/minecraft/class_1792$class_1793;)V"] = EQUIP + "#newElytraItem"
    # Equipable's members (on an Equipable reference: an Object; on an ElytraItem one: an Item)
    for owner in (EQUIPABLE, ELYTRA):
        out["instanceRedirects"][owner + ".method_7685()Lnet/minecraft/class_1304;"] = EQUIP
        out["instanceRedirects"][owner + ".method_31570()Lnet/minecraft/class_6880;"] = EQUIP
    out["instanceRedirects"][EQUIPABLE + ".method_48576(Lnet/minecraft/class_1792;Lnet/minecraft/class_1937;"
                             "Lnet/minecraft/class_1657;Lnet/minecraft/class_1268;)Lnet/minecraft/class_1271;"] = EQUIP
    out["staticRedirects"][EQUIPABLE + ".method_48957(Lnet/minecraft/class_1799;)Lnet/minecraft/class_5151;"] = EQUIP
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
