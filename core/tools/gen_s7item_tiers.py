"""Wiring for 1.21.1's Tier / Tiers / TieredItem / tool items / Tool.Rule factories on 1.21.2
(s7item, step 1.21.1 -> 1.21.2; semantics in rt/CbItemTiers and the facades Tier, Tiers, TieredItem, *Item).

  classRenames   Tier (an interface; 1.21.2's ToolMaterial is a record), Tiers -> the facades; TieredItem (gone) -> Item,
                 the widest type its 1.21.1 instances have on 1.21.2 (1.21.2's own swords and digger tools are no
                 TieredItems): a type test and a cast ask CbItemTypes (instanceOfRedirects / checkCastRedirects:
                 1.21.1's TieredItems -- the facade, 1.21.2's SwordItem and DiggerItem), its members are statics,
                 new TieredItem(..) builds the facade
  superRenames   TieredItem -> its facade (a subclass keeps 1.21.1's constructor and bodies); DiggerItem / SwordItem / AxeItem / PickaxeItem / ShovelItem / HoeItem -> facades with 1.21.1's
                 tier constructor; a subclass's super calls reach 1.21.1's TieredItem bodies there
  ctorRedirects  new SwordItem / AxeItem / PickaxeItem / ShovelItem / HoeItem(tier, props) -> 1.21.2's built from the
                 converted tier (the caller's attribute modifiers kept, as 1.21.1 did)
  statics        DiggerItem / SwordItem.createAttributes(tier, ..), Tool.Rule factories taking a tag or a block list
  getTier()      on 1.21.2's tool classes (no longer TieredItems): the tier they were built with, or the class's override

  python gen_s7item_tiers.py <out fragment.json>
"""
import json
import sys

RT = "top/dext/centurybridge/step/v1_21_2/rt/CbItemTiers"
FAC = "top/dext/centurybridge/step/v1_21_2/facade/"
TYPES = "top/dext/centurybridge/step/v1_21_2/rt/CbItemTypes"
MEMBERS = "top/dext/centurybridge/step/v1_21_2/rt/CbItemMembers"
CTOR = "(Lnet/minecraft/class_1832;Lnet/minecraft/class_1792$class_1793;)V"
TOOLS = [("class_1829", "SwordItem"), ("class_1743", "AxeItem"), ("class_1810", "PickaxeItem"),
         ("class_1821", "ShovelItem"), ("class_1794", "HoeItem")]
RULE = "net/minecraft/class_9424$class_9425"


def main():
    out = {
        "classRenames": {
            "net/minecraft/class_1832": FAC + "Tier",
            "net/minecraft/class_1834": FAC + "Tiers",
            "net/minecraft/class_1831": "net/minecraft/class_1792",
        },
        "superRenames": {"net/minecraft/class_1766": FAC + "DiggerItem", "net/minecraft/class_1831": FAC + "TieredItem"},
        "instanceOfRedirects": {"net/minecraft/class_1831": TYPES + "#isTieredItem"},
        "checkCastRedirects": {"net/minecraft/class_1831": TYPES + "#castTieredItem"},
        "ctorRedirects": {"net/minecraft/class_1831.<init>" + CTOR: RT + "#newTieredItem"},
        "staticRedirects": {
            "net/minecraft/class_1766.method_57346(Lnet/minecraft/class_1832;FF)Lnet/minecraft/class_9285;": RT,
            "net/minecraft/class_1829.method_57394(Lnet/minecraft/class_1832;IF)Lnet/minecraft/class_9285;": RT,
            RULE + ".method_58431(Ljava/util/List;F)Lnet/minecraft/class_9424$class_9425;": RT,
            RULE + ".method_58428(Lnet/minecraft/class_6862;F)Lnet/minecraft/class_9424$class_9425;": RT,
            RULE + ".method_58427(Lnet/minecraft/class_6862;)Lnet/minecraft/class_9424$class_9425;": RT,
            RULE + ".method_58433(Lnet/minecraft/class_6862;F)Lnet/minecraft/class_9424$class_9425;": RT,
            RULE + ".method_58434(Ljava/util/List;F)Lnet/minecraft/class_9424$class_9425;": RT,
        },
        "instanceRedirects": {
            # per-class names: 1.21.5 folds DiggerItem / SwordItem into Item (no overloads told apart by them)
            "net/minecraft/class_1766.method_8022()Lnet/minecraft/class_1832;": RT + "#method_8022Digger",
            "net/minecraft/class_1829.method_8022()Lnet/minecraft/class_1832;": RT + "#method_8022Sword",
            # through a TieredItem reference (an Item since the rename): the class's, the facade's or the 1.21.2 tool's
            "net/minecraft/class_1831.method_8022()Lnet/minecraft/class_1832;": RT,
            "net/minecraft/class_1831.method_7837()I": MEMBERS,
            "net/minecraft/class_1831.method_7878(Lnet/minecraft/class_1799;Lnet/minecraft/class_1799;)Z": MEMBERS,
        },
        "adapterHooks": [{
            "under": "net/minecraft/class_1831", "adapter": RT + "$LegacyTier",
            "requires": ["method_8022()Lnet/minecraft/class_1832;"], "synth": [],
            "basis": "a 1.21.1 tool subclass overriding getTier(): calls of it on 1.21.2's tool classes reach the "
                     "override (CbItemTiers)"}],
    }
    for cls, name in TOOLS:
        out["superRenames"]["net/minecraft/" + cls] = FAC + name
        out["ctorRedirects"]["net/minecraft/" + cls + ".<init>" + CTOR] = RT + "#new" + name
    json.dump(out, open(sys.argv[1], "w", encoding="utf-8", newline="\n"), indent=1)


if __name__ == "__main__":
    main()
