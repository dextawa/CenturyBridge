"""Wiring for 1.21.1's FoodProperties / FoodProperties.Builder / PossibleEffect / Foods on 1.21.2
(s7item, step 1.21.1 -> 1.21.2; semantics in rt/CbItemFood and facade FoodPossibleEffect).

  FoodProperties           the 6-argument constructor and eatSeconds / eatDurationTicks / usingConvertsTo / effects:
                           1.21.2's record plus the extras CbItemFood keeps for it
  Builder                  fast / effect / usingConvertsTo (call and super call): kept on the builder, attached by build()
  PossibleEffect           gone -> facade
  Foods                    the constant 1.21.2 dropped

  python gen_s7item_food.py <out fragment.json>
"""
import json
import sys

RT = "top/dext/centurybridge/step/v1_21_2/rt/CbItemFood"
FOOD = "net/minecraft/class_4174"
BUILDER = "net/minecraft/class_4174$class_4175"
BUILDER_MEMBERS = [  # (member, Legacy interface for a subclass overriding it)
    ("method_19241()Lnet/minecraft/class_4174$class_4175;", "LegacyFast"),
    ("method_19239(Lnet/minecraft/class_1293;F)Lnet/minecraft/class_4174$class_4175;", "LegacyEffect"),
    ("method_60500(Lnet/minecraft/class_1935;)Lnet/minecraft/class_4174$class_4175;", "LegacyConvertsTo"),
]


def main():
    out = {
        "classRenames": {FOOD + "$class_9423": "top/dext/centurybridge/step/v1_21_2/facade/FoodPossibleEffect"},
        "ctorRedirects": {FOOD + ".<init>(IFZFLjava/util/Optional;Ljava/util/List;)V": RT + "#newFoodProperties"},
        "instanceRedirects": {
            FOOD + ".method_58399()I": RT,
            FOOD + ".comp_2494()F": RT,
            FOOD + ".comp_2794()Ljava/util/Optional;": RT,
            FOOD + ".comp_2495()Ljava/util/List;": RT,
        },
        "superRedirects": {},
        "adapterHooks": [],
        "fieldGetters": {"net/minecraft/class_4176.field_50138:Lnet/minecraft/class_4174;": RT},
    }
    for m, legacy in BUILDER_MEMBERS:
        out["instanceRedirects"][BUILDER + "." + m] = RT
        out["superRedirects"][BUILDER + "." + m] = RT + "#super_" + m[:m.index("(")]
        out["adapterHooks"].append({"under": BUILDER, "adapter": RT + "$" + legacy, "requires": [m], "synth": [],
                                    "basis": "a 1.21.1 FoodProperties.Builder subclass overriding it: calls reach the "
                                             "override (CbItemFood)"})
    json.dump(out, open(sys.argv[1], "w", encoding="utf-8", newline="\n"), indent=1)


if __name__ == "__main__":
    main()
