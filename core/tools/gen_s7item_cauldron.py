"""Wiring for 1.21.1's CauldronInteraction (interact answering an ItemInteractionResult) on 1.21.2 (an
InteractionResult) (s7item, step 1.21.1 -> 1.21.2; semantics in rt/CbItemCauldron).

  python gen_s7item_cauldron.py <out fragment.json>
"""
import json
import sys

RT = "top/dext/centurybridge/step/v1_21_2/rt/CbItemCauldron"
CI = "net/minecraft/class_5620"
ARGS = "(Lnet/minecraft/class_2680;Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;Lnet/minecraft/class_1657;Lnet/minecraft/class_1268;Lnet/minecraft/class_1799;)"
OLD = "interact" + ARGS + "Lnet/minecraft/class_9062;"
NEW = "interact" + ARGS + "Lnet/minecraft/class_1269;"


def main():
    out = {
        "instanceRedirects": {CI + "." + OLD: RT},
        "staticRedirects": {
            CI + ".method_32210(Lnet/minecraft/class_2680;Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;Lnet/minecraft/class_1657;"
                 "Lnet/minecraft/class_1268;Lnet/minecraft/class_1799;Lnet/minecraft/class_1799;Ljava/util/function/Predicate;"
                 "Lnet/minecraft/class_3414;)Lnet/minecraft/class_9062;": RT,
            CI + ".method_32207(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;Lnet/minecraft/class_1657;Lnet/minecraft/class_1268;"
                 "Lnet/minecraft/class_1799;Lnet/minecraft/class_2680;Lnet/minecraft/class_3414;)Lnet/minecraft/class_9062;": RT,
        },
        "fieldGetters": {CI + "." + f + ":Lnet/minecraft/class_5620;": RT
                         for f in ("field_27778", "field_27779", "field_28012", "field_27780", "field_27781", "field_27782")},
        "lambdaAdapters": {CI + "." + OLD: {
            "iface": RT + "$Legacy",
            "adapt": RT + "#fromLambda(L" + RT + "$Legacy;)L" + CI + ";"}},
        "overrideHooks": [{
            "old": OLD, "new": NEW, "onlyUnder": CI,
            "unwrap": "top/dext/centurybridge/step/v1_21_2/facade/ItemInteractionResult.toResult("
                      "Ltop/dext/centurybridge/step/v1_21_2/facade/ItemInteractionResult;)Lnet/minecraft/class_1269;",
            "basis": "CauldronInteraction.interact answers an InteractionResult since 1.21.2: a 1.21.1 class's answer "
                     "converted (facade ItemInteractionResult.toResult)"}],
    }
    json.dump(out, open(sys.argv[1], "w", encoding="utf-8", newline="\n"), indent=1)


if __name__ == "__main__":
    main()
