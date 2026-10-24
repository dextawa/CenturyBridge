"""Recipe assemble(input, registries) -> assemble(input) at the 1.21.11 -> 26.1 wall, per recipe class.

1.21.11's Recipe.assemble(input, registries) took the registries; 26.1's assemble(input) does not. Every vanilla
recipe class declared its own assemble for its input type (CraftingInput, SingleRecipeInput, SmithingRecipeInput),
an intermediary name of its own with a bridge for Recipe's. For each such method of the step's damage table whose
26.1 class still declares assemble(<input>):
  - callReshapes: the call -- a mod's super call included -- drops the registries (26.1's body never had them;
    1.21.11's vanilla bodies only passed them on to ItemStack copies that 26.1 makes without them);
  - overrideHooks (bridge mode): a mod class under the vanilla class declaring the old shape gets 26.1's
    assemble(<input>), which asks the old override with the registries 1.21.11's callers passed
    (rt/CbWallRecipes.assembleCrafting / assembleSingle / assembleSmithing). One hook per method name, under the
    class that declares it first (a subclass overriding it shares the name and the hook).
Classes 26.1 removed, or whose assemble 26.1 does not declare, are listed in the report.

  python gen_wall_recipe_assemble.py <damage.tsv> <V jar> <W jar> <wall table> <fragment.json> <report.tsv>

Regenerating (from the repo root):
  python3 core/tools/gen_wall_recipe_assemble.py core/segments/damage/1.21.11__26.1.damage.tsv \n      data/jars/client-1.21.11-intermediary.jar data/jars/client-26.1.jar \
      data/mappings/inter-to-mojang-1.21.11-26.1.tsv core/segments/steps/1.21.11__26.1.json.d/swall-recipes-gen.json <tmp>/recipes-report.tsv
"""
import collections
import json
import os
import sys
import zipfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import classfile  # noqa: E402

REGISTRIES = "Lnet/minecraft/class_7225$class_7874;"
STACK = "Lnet/minecraft/class_1799;"
KINDS = {
    "Lnet/minecraft/class_9694;": ("Crafting", "Lnet/minecraft/world/item/crafting/CraftingInput;"),
    "Lnet/minecraft/class_9696;": ("Single", "Lnet/minecraft/world/item/crafting/SingleRecipeInput;"),
    "Lnet/minecraft/class_9697;": ("Smithing", "Lnet/minecraft/world/item/crafting/SmithingRecipeInput;"),
}
RT = "top/dext/centurybridge/step/v26_1/rt/CbWallRecipes"
GUARD = "top/dext/centurybridge/step/v26_1/rt/Guard"


def main():
    dmg, vjar, wjar, table, frag, report = sys.argv[1:7]
    vz = zipfile.ZipFile(vjar)

    def v_ancestors(owner):
        out = []
        while owner and owner + ".class" in vz.namelist():
            owner = classfile.read(vz.read(owner + ".class")).super
            out.append(owner)
        return out
    cmap = {}
    for line in open(table, encoding="utf-8"):
        f = line.rstrip("\n").split("\t")
        if len(f) >= 3 and f[0] == "C":
            cmap[f[1]] = f[2]
    with zipfile.ZipFile(wjar) as z:
        names = set(z.namelist())

        def w_class(owner):
            n = cmap.get(owner, owner) + ".class"
            return classfile.read(z.read(n)) if n in names else None

        rows = set()
        for line in open(dmg, encoding="utf-8"):
            f = line.rstrip("\n").split("\t")
            if len(f) < 4 or not f[3].endswith(REGISTRIES[:-1] + ";)" + STACK):
                continue
            for inp in KINDS:
                if f[3] == "(" + inp + REGISTRIES + ")" + STACK:
                    rows.add((f[1], f[2], inp))
        reshapes = collections.OrderedDict()
        hooks = {}
        rep = []
        # a subclass overriding the method shares its name: the hook goes under the topmost declaring class
        rows = sorted(rows, key=lambda r: (len(v_ancestors(r[0])), r))
        for owner, name, inp in rows:
            kind, winput = KINDS[inp]
            c = w_class(owner)
            if c is None:
                rep.append((owner, name, "26.1 has no such class"))
                continue
            if not any(m.name == "assemble" and m.desc == "(" + winput + ")Lnet/minecraft/world/item/ItemStack;" for m in c.methods):
                rep.append((owner, name, "26.1's class declares no assemble(" + winput + ")"))
                continue
            old = name + "(" + inp + REGISTRIES + ")" + STACK
            reshapes[owner + "." + old] = collections.OrderedDict([
                ("desc", "(" + inp + ")" + STACK),
                ("ops", ["keep:0", "drop:1"]),
                ("basis", "26.1's assemble takes no registries; 1.21.11's vanilla body only handed them to its result's copy, "
                          "which 26.1 makes without them"),
            ])
            hooks.setdefault(old, []).append(owner)
            rep.append((owner, name, "reshape" + (" + hook" if len(hooks[old]) == 1 else " (hook under " + hooks[old][0] + ")")))
        out = collections.OrderedDict()
        out["callReshapes"] = reshapes
        out["overrideHooks"] = []
        for old, owners in hooks.items():
            name = old.split("(")[0]
            inp = old[len(name) + 1:].split(REGISTRIES)[0]
            kind = KINDS[inp][0]
            out["overrideHooks"].append(collections.OrderedDict([
                ("old", old),
                ("new", name + "(" + inp + ")" + STACK),
                ("onlyUnder", owners[0]),
                ("adapter", RT + "$LegacyAssemble" + kind),
                ("bridge", RT + ".assemble" + kind + "(L" + RT + "$LegacyAssemble" + kind + ";" + inp + ")" + STACK),
                ("guard", GUARD),
                ("basis", "26.1 dropped assemble's registries: a 1.21.11 recipe assembles with the registries 1.21.11 callers "
                          "passed (the level registry access), as for Recipe.assemble"),
            ]))
    with open(frag, "w", encoding="utf-8", newline="\n") as fh:
        json.dump(out, fh, indent=1)
        fh.write("\n")
    with open(report, "w", encoding="utf-8", newline="\n") as fh:
        for r in rep:
            fh.write("\t".join(r) + "\n")
    print("%d reshapes, %d hooks, report %s" % (len(reshapes), len(out["overrideHooks"]), report))


if __name__ == "__main__":
    main()
