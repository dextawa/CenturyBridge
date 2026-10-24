"""Wiring for 1.21.1's ItemCooldowns (per Item) on 1.21.2 (per cooldown group)
(s7item, step 1.21.1 -> 1.21.2; semantics in rt/CbItemCooldowns).

  calls / super calls of the six Item shapes (ServerItemCooldowns' two protected ones included) -> CbItemCooldowns
  overrides of them in a converted subclass -> overrideHooks: 1.21.2's group/stack shape forwards to the override
  with the stack's item / the group's item (wrap), guarded so the override's super call passes to super

  python gen_s7item_cooldowns.py <out fragment.json>
"""
import json
import sys

RT = "top/dext/centurybridge/step/v1_21_2/rt/CbItemCooldowns"
GUARD = "top/dext/centurybridge/step/v1_21_2/rt/Guard"
CD = "net/minecraft/class_1796"
SERVER = "net/minecraft/class_1817"  # ServerItemCooldowns: declares the two protected shapes itself
ITEM = "Lnet/minecraft/class_1792;"
STACK = "Lnet/minecraft/class_1799;"
RL = "Lnet/minecraft/class_2960;"
# (name, old args, new args, return, wrap from)
SHAPES = [
    ("method_7904", ITEM, STACK, "Z", STACK),
    ("method_7905", ITEM + "F", STACK + "F", "F", STACK),
    ("method_7906", ITEM + "I", RL + "I", "V", RL),
    ("method_7900", ITEM, RL, "V", RL),
    ("method_7902", ITEM + "I", RL + "I", "V", RL),
    ("method_7901", ITEM, RL, "V", RL),
]


def main():
    out = {"instanceRedirects": {}, "superRedirects": {}, "overrideHooks": []}
    for name, old, new, ret, frm in SHAPES:
        key = CD + "." + name + "(" + old + ")" + ret
        out["instanceRedirects"][key] = RT
        out["superRedirects"][key] = RT
        out["overrideHooks"].append({
            "old": name + "(" + old + ")" + ret, "new": name + "(" + new + ")" + ret, "onlyUnder": CD,
            "wrap": RT + ".item(" + frm + ")" + ITEM, "guard": GUARD,
            "basis": "ItemCooldowns keys cooldowns by group since 1.21.2 (an item's group is its id): 1.21.2's shape "
                     "forwards to the 1.21.1 override with the item (CbItemCooldowns)"})
    for name, old, new, ret, frm in SHAPES[4:]:
        key = SERVER + "." + name + "(" + old + ")" + ret
        out["instanceRedirects"][key] = RT
        out["superRedirects"][key] = RT
    json.dump(out, open(sys.argv[1], "w", encoding="utf-8", newline="\n"), indent=1)


if __name__ == "__main__":
    main()
