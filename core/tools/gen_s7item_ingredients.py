"""Wiring for 1.21.1's Ingredient surface on 1.21.2 (s7item, step 1.21.1 -> 1.21.2; rt/CbItemIngredients):
the empty ingredient, getItems / getStackingIds / isEmpty, the stack / tag / empty factories, ofStacks' generic drift
(1.21.1 passed ItemStacks where 1.21.2 takes ItemLikes), CODEC_NONEMPTY (1.21.2's CODEC is non-empty).

  python gen_s7item_ingredients.py <out fragment.json>
"""
import json
import sys

RT = "top/dext/centurybridge/step/v1_21_2/rt/CbItemIngredients"
ING = "net/minecraft/class_1856"


def main():
    out = {
        "fieldGetters": {ING + ".field_9017:Lnet/minecraft/class_1856;": RT},
        "fieldRedirects": {ING + ".field_46096:Lcom/mojang/serialization/Codec;": ING + "#field_46095"},
        "instanceRedirects": {
            ING + ".method_8105()[Lnet/minecraft/class_1799;": RT,
            ING + ".method_8100()Lit/unimi/dsi/fastutil/ints/IntList;": RT,
            ING + ".method_8103()Z": RT,
        },
        "staticRedirects": {
            ING + ".method_35226()Lnet/minecraft/class_1856;": RT,
            ING + ".method_8101([Lnet/minecraft/class_1799;)Lnet/minecraft/class_1856;": RT,
            ING + ".method_8106(Lnet/minecraft/class_6862;)Lnet/minecraft/class_1856;": RT,
            ING + ".method_8091([Lnet/minecraft/class_1935;)Lnet/minecraft/class_1856;": RT,
            ING + ".method_26964(Ljava/util/stream/Stream;)Lnet/minecraft/class_1856;": RT,
        },
    }
    json.dump(out, open(sys.argv[1], "w", encoding="utf-8", newline="\n"), indent=1)


if __name__ == "__main__":
    main()
