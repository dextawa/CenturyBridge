"""mixinTargets of step 1.20.6 -> 1.21 (smx6): `Main gen-mixin-targets` over V/W bytecode (a V method W no longer declares
with V's descriptor, the one W method of that name, arguments one for one by type), minus EXCLUDE (judged from both
bodies not to hold V's code, or left to a general mechanism).

  On beta2 (Java there only):
    zcat core/segments/damage-mixin/1.20.6__1.21.tsv.gz > dm.tsv
    java -cp core/build/classes:<asm+gson> top.dext.centurybridge.Main gen-mixin-targets \
        data/jars/client-1.20.6-intermediary-code.jar data/jars/client-1.21-intermediary-code.jar \
        core/segments/steps/1.20.6__1.21.json dm.tsv mt-gen.json
    (with this fragment absent, or gen-mixin-targets counts its own entries as "wired by the step")
  Here, from the repository root:
    python3 core/tools/gen_smx6_mixintargets.py mt-gen.json core/segments/steps/1.20.6__1.21.json.d/smx6-mixintargets-gen.json

Regenerate, never hand-edit.
"""
import json
import sys

# V method (or V class prefix ending in ".") -> why gen-mixin-targets' same-name pick is not taken (javap of both bodies)
EXCLUDE = {
    "net/minecraft/class_1887.":
        "1.20.6 Enchantment is the layer's facade (classRenames); 1.21's class_1887 is the data-driven final record, "
        "its constructor and getters another class's members",
    "net/minecraft/class_1887$class_9427.":
        "1.20.6 Enchantment.EnchantmentDefinition is the layer's facade (classRenames); 1.21's record holds HolderSets "
        "and a List where V held TagKeys and an array",
    "net/minecraft/class_1890.method_8220(Lnet/minecraft/class_1890$class_1891;Lnet/minecraft/class_1799;)V":
        "the EnchantmentVisitor argument is another interface on 1.21 (accept(Holder<Enchantment>, int) where V had "
        "accept(Enchantment, int)): a handler taking V's visitor would call a method W's visitor lacks",
    "net/minecraft/class_109.<init>(Ljava/util/List;Ljava/util/Optional;)V":
        "EnchantRandomlyFunction's Optional<HolderSet<Enchantment>> erases alike but holds 1.21's Enchantment records, "
        "where a 1.20.6 handler capturing it reads the layer's Enchantment facade (generic drift, no converter)",
    # loot / advancement / tag providers 1.21 made records of the registries: classRenames send mods to rewriting facades;
    # where the mixin lands is MixinBridge's (static -> instance on records, reported to smixin3), not a constructor map
    "net/minecraft/class_2432.<init>()V": "VanillaChestLoot is renamed to a rewriting facade (s6fin-loot-gen)",
    "net/minecraft/class_2437.<init>()V": "renamed to a rewriting facade (s6fin-loot-gen)",
    "net/minecraft/class_3714.<init>()V": "renamed to a rewriting facade (s6fin-loot-gen)",
    "net/minecraft/class_4845.<init>()V": "renamed to a rewriting facade (s6fin-loot-gen)",
    "net/minecraft/class_8255.<init>()V": "renamed to a rewriting facade (s6fin-loot-gen)",
    "net/minecraft/class_8794.<init>()V": "TradeRebalanceChestLoot is renamed to a rewriting facade (s6fin-loot-gen)",
    "net/minecraft/class_9340.<init>()V": "renamed to a rewriting facade (s6fin-loot-gen)",
    "net/minecraft/class_9486.<init>()V": "renamed to a rewriting facade (s6fin-loot-gen)",
}


def excluded(k):
    if k in EXCLUDE:
        return True
    owner = k[:k.rindex(".", 0, k.index("("))]
    return owner + "." in EXCLUDE


def main():
    src, out = sys.argv[1], sys.argv[2]
    mt = json.load(open(src, encoding="utf-8"))["mixinTargets"]
    keep = {k: v for k, v in sorted(mt.items()) if not excluded(k)}
    root = {"_smx6_mixinTargets_basis": "gen_smx6_mixintargets.py over Main gen-mixin-targets (V/W bytecode); regenerate, "
                                        "never hand-edit", "mixinTargets": keep}
    json.dump(root, open(out, "w", encoding="utf-8", newline="\n"), indent=1)
    print(len(keep), "of", len(mt), "entries ->", out)


if __name__ == "__main__":
    main()
