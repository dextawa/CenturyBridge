"""Wiring for 1.21.1's MobEffect hooks called without a level and FoodData's dropped members, on 1.21.2
(s7item, step 1.21.1 -> 1.21.2; semantics in rt/CbItemEffects and mixin/CbItemFoodDataMixin).

  MobEffect   applyEffectTick / applyInstantenousEffect / onMobRemoved / onMobHurt calls, and MobEffectInstance's
              onMobRemoved / onMobHurt: the entity's ServerLevel
              passed (1.21.2 runs them server-side only; mechanical.json's frames hooks reconnect overrides)
  FoodData    getLastFoodLevel / getExhaustionLevel / setExhaustion / tick(Player): declared again by the mixin (covers)

  python gen_s7item_effects.py <out fragment.json>
"""
import json
import sys

RT = "top/dext/centurybridge/step/v1_21_2/rt/CbItemEffects"
EFFECT = "net/minecraft/class_1291"
FOOD = "net/minecraft/class_1702"


def main():
    out = {
        "instanceRedirects": {
            EFFECT + ".method_5572(Lnet/minecraft/class_1309;I)Z": RT,
            EFFECT + ".method_5564(Lnet/minecraft/class_1297;Lnet/minecraft/class_1297;Lnet/minecraft/class_1309;ID)V": RT,
            EFFECT + ".method_58617(Lnet/minecraft/class_1309;ILnet/minecraft/class_1297$class_5529;)V": RT,
            EFFECT + ".method_58614(Lnet/minecraft/class_1309;ILnet/minecraft/class_1282;F)V": RT,
            "net/minecraft/class_1293.method_58622(Lnet/minecraft/class_1309;Lnet/minecraft/class_1297$class_5529;)V": RT,
            "net/minecraft/class_1293.method_58621(Lnet/minecraft/class_1309;Lnet/minecraft/class_1282;F)V": RT,
        },
        "covers": [
            FOOD + ".method_35217()I",
            FOOD + ".method_35219()F",
            FOOD + ".method_35218(F)V",
            FOOD + ".method_7588(Lnet/minecraft/class_1657;)V",
        ],
    }
    # frameDefaults for mechanical.json's frames hooks of these shapes: the entity's ServerLevel (CbItemEffects)
    L = RT + "#level("
    SL = ")Lnet/minecraft/class_3218;"
    out["frameDefaults"] = {
        "method_5572(Lnet/minecraft/class_1309;I)Z": {"0": L + "L" + EFFECT + ";Lnet/minecraft/class_1309;I" + SL},
        "method_5564(Lnet/minecraft/class_1297;Lnet/minecraft/class_1297;Lnet/minecraft/class_1309;ID)V":
            {"0": L + "L" + EFFECT + ";Lnet/minecraft/class_1297;Lnet/minecraft/class_1297;Lnet/minecraft/class_1309;ID" + SL},
        "method_58617(Lnet/minecraft/class_1309;ILnet/minecraft/class_1297$class_5529;)V":
            {"0": L + "L" + EFFECT + ";Lnet/minecraft/class_1309;ILnet/minecraft/class_1297$class_5529;" + SL},
        "method_58614(Lnet/minecraft/class_1309;ILnet/minecraft/class_1282;F)V":
            {"0": L + "L" + EFFECT + ";Lnet/minecraft/class_1309;ILnet/minecraft/class_1282;F" + SL},
        "method_58622(Lnet/minecraft/class_1309;Lnet/minecraft/class_1297$class_5529;)V":
            {"0": L + "Lnet/minecraft/class_1293;Lnet/minecraft/class_1309;Lnet/minecraft/class_1297$class_5529;" + SL},
        "method_58621(Lnet/minecraft/class_1309;Lnet/minecraft/class_1282;F)V":
            {"0": L + "Lnet/minecraft/class_1293;Lnet/minecraft/class_1309;Lnet/minecraft/class_1282;F" + SL},
    }
    json.dump(out, open(sys.argv[1], "w", encoding="utf-8", newline="\n"), indent=1)


if __name__ == "__main__":
    main()
