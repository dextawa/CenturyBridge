"""Wiring for 1.21.1's ArmorItem / ArmorMaterial / ArmorMaterials / ArmorItem.Type on 1.21.2
(s7item, step 1.21.1 -> 1.21.2; semantics in rt/CbItemArmor and the facades ArmorMaterial, ArmorMaterials, ArmorItem).

  classRenames   ArmorMaterial (1.21.2 reused the name for another record) and its Layer, ArmorMaterials
                 (an interface of 1.21.2 materials now) -> the facades; ArmorItem.Type -> 1.21.2's ArmorType
                 (the same constants and members, hasTrims() aside)
  superRenames   ArmorItem -> facade ArmorItem: a subclass keeps 1.21.1's constructor (material holder)
  ctorRedirects  new ArmorItem(holder, type, props) -> 1.21.2's ArmorItem built from the converted material
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

# (member, Legacy interface, basis)
MEMBERS = [
    ("method_48398()Lnet/minecraft/class_1738$class_8051;", "LegacyType", "getType()"),
    ("method_7686()Lnet/minecraft/class_6880;", "LegacyMaterial", "getMaterial()"),
    ("method_7687()I", "LegacyDefense", "getDefense()"),
    ("method_26353()F", "LegacyToughness", "getToughness()"),
    ("method_7685()Lnet/minecraft/class_1304;", "LegacyEquipmentSlot", "getEquipmentSlot()"),
    ("method_31570()Lnet/minecraft/class_6880;", "LegacyEquipSound", "getEquipSound()"),
]


def main():
    out = {
        "classRenames": {
            "net/minecraft/class_1741": FAC + "ArmorMaterial",
            "net/minecraft/class_1741$class_9196": FAC + "ArmorMaterial$Layer",
            "net/minecraft/class_1740": FAC + "ArmorMaterials",
            TYPE: "net/minecraft/class_8051",
        },
        "superRenames": {ARMOR: FAC + "ArmorItem"},
        "ctorRedirects": {
            ARMOR + ".<init>(Lnet/minecraft/class_6880;Lnet/minecraft/class_1738$class_8051;Lnet/minecraft/class_1792$class_1793;)V":
                RT + "#newArmorItem",
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
    for member, legacy, why in MEMBERS:
        name = member[:member.index("(")]
        out["instanceRedirects"][ARMOR + "." + member] = RT
        out["superRedirects"][ARMOR + "." + member] = RT + "#super_" + name
        out["adapterHooks"].append({"under": ARMOR, "adapter": RT + "$" + legacy, "requires": [member], "synth": [],
                                    "basis": "a 1.21.1 ArmorItem subclass overriding " + why
                                             + ": calls of it reach the override (CbItemArmor)"})
    json.dump(out, open(sys.argv[1], "w", encoding="utf-8", newline="\n"), indent=1)


if __name__ == "__main__":
    main()
