"""mixinTargets of the s7item area, step 1.21.1 -> 1.21.2: the area's share of `Main gen-mixin-targets` (V/W bytecode:
a V method W no longer declares with V's descriptor, the one W method of that name, arguments one for one by type).

  On beta2 (Java there only):
    zcat core/segments/damage-mixin/1.21.1__1.21.2.tsv.gz > dm.tsv
    java -cp core/build/classes:<asm+gson> top.dext.centurybridge.Main gen-mixin-targets \
        data/jars/client-1.21.1-intermediary-code.jar data/jars/client-1.21.2-intermediary-code.jar \
        core/segments/steps/1.21.1__1.21.2.json dm.tsv mt-gen.json
  Here, from the repository root:
    python3 core/tools/gen_s7item_mixintargets.py mt-gen.json core/segments/steps/1.21.1__1.21.2.json.d/s7item-mixintargets-gen.json

Keeps the entries whose owner's Mojang name (mixin_areas.names) is in the s7item area, minus EXCLUDE (judged from
both bodies not to hold V's code). Regenerate, never hand-edit.
"""
import json
import os
import re
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from mixin_areas import names  # noqa: E402

AREA = re.compile(r"^net/minecraft/(world/(?!entity/|level/)|core/|data/|advancements/|stats/|tags/|recipebook/"
                  r"|client/RecipeBookCategories)")

# V method -> why gen-mixin-targets' same-name pick is not where V's code lives (javap of both bodies)
EXCLUDE = {
    "net/minecraft/class_1741.comp_2301()Ljava/util/function/Supplier;":
        "1.21.1 ArmorMaterial is the layer's facade (classRenames); 1.21.2's class_1741 is another record whose "
        "repairIngredient answers a TagKey, not V's Supplier<Ingredient>",
    "net/minecraft/class_1749.method_42296(Lnet/minecraft/class_1937;Lnet/minecraft/class_239;Lnet/minecraft/class_1799;"
    "Lnet/minecraft/class_1657;)Lnet/minecraft/class_1690;":
        "BoatItem.getBoat answers AbstractBoat on 1.21.2: a bamboo raft (Raft, ChestRaft) is no longer a Boat, so a "
        "1.21.1 handler reading its CallbackInfoReturnable<Boat> would fail its cast where 1.21.1 handed it a Boat",
}


def main():
    src, out = sys.argv[1], sys.argv[2]
    cm = names()

    def moj(c):
        if c in cm:
            return cm[c]
        d = c.rfind("$")
        return moj(c[:d]) + c[d:] if d > 0 else c

    mt = json.load(open(src, encoding="utf-8"))["mixinTargets"]
    keep = {}
    for k, v in sorted(mt.items()):
        owner = k[:k.rindex(".", 0, k.index("("))]
        if AREA.search(moj(owner)) and k not in EXCLUDE:
            keep[k] = v
    root = {"_s7item_mixinTargets_basis": "gen_s7item_mixintargets.py over Main gen-mixin-targets (V/W bytecode); regenerate, "
                                   "never hand-edit", "mixinTargets": keep}
    json.dump(root, open(out, "w", encoding="utf-8", newline="\n"), indent=1)
    print(len(keep), "of", len(mt), "entries in the s7item area ->", out)


if __name__ == "__main__":
    main()
