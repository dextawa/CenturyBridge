"""Wiring for 1.21.1's advancement predicate builders / triggers whose 1.21.2 shapes take a HolderGetter, recipe
keys, a firing weapon or an input predicate (s7item, step 1.21.1 -> 1.21.2; values in rt/CbItemPredicates).

  python gen_s7item_predicates.py <out fragment.json>
"""
import json
import sys

RT = "top/dext/centurybridge/step/v1_21_2/rt/CbItemPredicates"
GUARD = "top/dext/centurybridge/step/v1_21_2/rt/Guard"
HG = "Lnet/minecraft/class_7871;"
ITEMS = "push " + RT + "#items()" + HG
BLOCKS = "push " + RT + "#blocks()" + HG
TYPES = "push " + RT + "#entityTypes()" + HG
RECIPE = "conv " + RT + "#recipe(Lnet/minecraft/class_2960;)Lnet/minecraft/class_5321;"
EMPTY = "push " + RT + "#empty()Ljava/util/Optional;"
CROSSBOW = "push " + RT + "#crossbow()Lnet/minecraft/class_1799;"

# (owner, name, old args, return, new args, ops)
SHAPES = [
    ("net/minecraft/class_4550$class_4710", "method_27962", "[Lnet/minecraft/class_2248;", "Lnet/minecraft/class_4550$class_4710;",
     HG + "[Lnet/minecraft/class_2248;", [BLOCKS, "keep"]),
    ("net/minecraft/class_4550$class_4710", "method_37214", "Ljava/util/Collection;", "Lnet/minecraft/class_4550$class_4710;",
     HG + "Ljava/util/Collection;", [BLOCKS, "keep"]),
    ("net/minecraft/class_4550$class_4710", "method_29233", "Lnet/minecraft/class_6862;", "Lnet/minecraft/class_4550$class_4710;",
     HG + "Lnet/minecraft/class_6862;", [BLOCKS, "keep"]),
    ("net/minecraft/class_2048$class_2049", "method_8921", "Lnet/minecraft/class_1299;", "Lnet/minecraft/class_2048$class_2049;",
     HG + "Lnet/minecraft/class_1299;", [TYPES, "keep"]),
    ("net/minecraft/class_2048$class_2049", "method_8922", "Lnet/minecraft/class_6862;", "Lnet/minecraft/class_2048$class_2049;",
     HG + "Lnet/minecraft/class_6862;", [TYPES, "keep"]),
    ("net/minecraft/class_2050", "method_8929", "Lnet/minecraft/class_1299;", "Lnet/minecraft/class_2050;",
     HG + "Lnet/minecraft/class_1299;", [TYPES, "keep"]),
    ("net/minecraft/class_2050", "method_8926", "Lnet/minecraft/class_6862;", "Lnet/minecraft/class_2050;",
     HG + "Lnet/minecraft/class_6862;", [TYPES, "keep"]),
    ("net/minecraft/class_2073$class_2074", "method_8977", "[Lnet/minecraft/class_1935;", "Lnet/minecraft/class_2073$class_2074;",
     HG + "[Lnet/minecraft/class_1935;", [ITEMS, "keep"]),
    ("net/minecraft/class_2073$class_2074", "method_8975", "Lnet/minecraft/class_6862;", "Lnet/minecraft/class_2073$class_2074;",
     HG + "Lnet/minecraft/class_6862;", [ITEMS, "keep"]),
    ("net/minecraft/class_2076$class_2078", "method_8986", "[Lnet/minecraft/class_2048$class_2049;", "Lnet/minecraft/class_175;",
     HG + "[Lnet/minecraft/class_2048$class_2049;", [ITEMS, "keep"]),
    ("net/minecraft/class_2076$class_2078", "method_8987", "Lnet/minecraft/class_2096$class_2100;", "Lnet/minecraft/class_175;",
     HG + "Lnet/minecraft/class_2096$class_2100;", [ITEMS, "keep"]),
    ("net/minecraft/class_2076$class_2078", "method_8988", "Ljava/util/Collection;I", "Z",
     "Ljava/util/Collection;ILnet/minecraft/class_1799;", ["keep", "keep", CROSSBOW]),
    ("net/minecraft/class_2076", "method_8980", "Lnet/minecraft/class_3222;Ljava/util/Collection;", "V",
     "Lnet/minecraft/class_3222;Ljava/util/Collection;Lnet/minecraft/class_1799;", ["keep", "keep", CROSSBOW]),
    ("net/minecraft/class_170$class_171", "method_753", "Lnet/minecraft/class_2960;", "Lnet/minecraft/class_170$class_171;",
     "Lnet/minecraft/class_5321;", [RECIPE]),
    ("net/minecraft/class_170$class_171", "method_752", "Lnet/minecraft/class_2960;", "Lnet/minecraft/class_170$class_171;",
     "Lnet/minecraft/class_5321;", [RECIPE]),
    ("net/minecraft/class_4553$class_4557", "method_35315", "Lnet/minecraft/class_2960;Z", "Lnet/minecraft/class_4553$class_4557;",
     "Lnet/minecraft/class_5321;Z", [RECIPE, "keep"]),
]
# builder methods a 1.21.1 subclass may override: 1.21.2's shape forwards to the override (wrap key -> id)
OVERRIDES = [
    ("net/minecraft/class_170$class_171", "method_752", "Lnet/minecraft/class_2960;", "Lnet/minecraft/class_5321;", "Lnet/minecraft/class_170$class_171;"),
    ("net/minecraft/class_4553$class_4557", "method_35315", "Lnet/minecraft/class_2960;Z", "Lnet/minecraft/class_5321;Z", "Lnet/minecraft/class_4553$class_4557;"),
]


def main():
    out = {"callReshapes": {}, "superCtorReshapes": {}, "overrideHooks": []}
    for owner, name, old, ret, new, ops in SHAPES:
        out["callReshapes"][owner + "." + name + "(" + old + ")" + ret] = {"desc": "(" + new + ")" + ret, "ops": ops}
    out["superCtorReshapes"]["net/minecraft/class_2076$class_2078.<init>(Ljava/util/Optional;Ljava/util/List;Lnet/minecraft/class_2096$class_2100;)V"] = {
        "desc": "(Ljava/util/Optional;Ljava/util/List;Lnet/minecraft/class_2096$class_2100;Ljava/util/Optional;)V",
        "ops": ["keep", "keep", "keep", EMPTY]}
    out["superCtorReshapes"]["net/minecraft/class_4553.<init>(Lnet/minecraft/class_2096$class_2100;Lnet/minecraft/class_9789;Ljava/util/List;"
                             "Lit/unimi/dsi/fastutil/objects/Object2BooleanMap;Ljava/util/Map;Ljava/util/Optional;)V"] = {
        "desc": "(Lnet/minecraft/class_2096$class_2100;Lnet/minecraft/class_9789;Ljava/util/List;Lit/unimi/dsi/fastutil/objects/Object2BooleanMap;"
                "Ljava/util/Map;Ljava/util/Optional;Ljava/util/Optional;)V",
        "ops": ["keep", "keep", "keep",
                "conv " + RT + "#recipes(Lit/unimi/dsi/fastutil/objects/Object2BooleanMap;)Lit/unimi/dsi/fastutil/objects/Object2BooleanMap;",
                "keep", "keep", EMPTY]}
    for owner, name, old, new, ret in OVERRIDES:
        out["overrideHooks"].append({
            "old": name + "(" + old + ")" + ret, "new": name + "(" + new + ")" + ret, "onlyUnder": owner,
            "wrap": RT + ".id(Lnet/minecraft/class_5321;)Lnet/minecraft/class_2960;", "guard": GUARD,
            "basis": "the builder takes a recipe key since 1.21.2: 1.21.2's shape forwards to the 1.21.1 override with the id"})
    json.dump(out, open(sys.argv[1], "w", encoding="utf-8", newline="\n"), indent=1)


if __name__ == "__main__":
    main()
