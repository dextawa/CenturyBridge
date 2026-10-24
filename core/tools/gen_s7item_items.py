"""Wiring for 1.21.1's Items.registerBlock / registerItem taking a built item (1.21.2's take a factory and
Properties) (s7item, step 1.21.1 -> 1.21.2; semantics in rt/CbItemItems).

  python gen_s7item_items.py <out fragment.json>
"""
import json
import sys

RT = "top/dext/centurybridge/step/v1_21_2/rt/CbItemItems"
ITEMS = "net/minecraft/class_1802"
SHAPES = [
    "method_7993(Lnet/minecraft/class_1747;)Lnet/minecraft/class_1792;",
    "method_7992(Lnet/minecraft/class_2248;Lnet/minecraft/class_1792;)Lnet/minecraft/class_1792;",
    "method_7991(Ljava/lang/String;Lnet/minecraft/class_1792;)Lnet/minecraft/class_1792;",
    "method_7990(Lnet/minecraft/class_2960;Lnet/minecraft/class_1792;)Lnet/minecraft/class_1792;",
    "method_51348(Lnet/minecraft/class_5321;Lnet/minecraft/class_1792;)Lnet/minecraft/class_1792;",
]


def main():
    out = {"staticRedirects": {ITEMS + "." + s: RT for s in SHAPES}}
    json.dump(out, open(sys.argv[1], "w", encoding="utf-8", newline="\n"), indent=1)


if __name__ == "__main__":
    main()
