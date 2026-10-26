"""gen_s9c_renumbered.py: methods 1.21.9's intermediary renumbered -- the same method under a new intermediary name.

  python core/tools/gen_s9c_renumbered.py <V code jar> <W code jar> [--write]
      (data/jars/client-1.21.8-intermediary-code.jar data/jars/client-1.21.9-intermediary-code.jar)

1.21.9's intermediary gave some unchanged methods new names (Entity.level(): method_37908 -> method_73183, with
OwnableEntity/ContainerEntity's level() and every override; Mojang's own name and the descriptor stayed). The
probe sees the old name gone ("method not declared on the owner", TOMBSTONE) and every 1.21.8 call, override and
mixin of it dies. A pair is taken only from bytecode, never by name:
  * a class declaring, in 1.21.8, a method (name n, descriptor d) that 1.21.9's same class no longer declares;
  * 1.21.9's class declaring exactly one method of descriptor d (and the same static-ness) that 1.21.8's class did
    not, whose body is 1.21.8's instruction for instruction, method references to other renumbered or new names
    masked (a renumbered method calling another renumbered one);
  * abstract methods (no body) pair only when the class has exactly one gone and one new abstract method of that
    descriptor and a concrete pair proven by body in a 1.21.9 implementor (directly or by inheritance) resolves
    the same way (OwnableEntity.level() is implemented by Entity.level()).
Output (--write): core/segments/steps/1.21.8__1.21.9.json.d/s9c-renumbered-gen.json -- methodRenames for every
declaring class (methodRenameHome stops at a source declaration, so each declarer needs its key) and guarded
overrideHooks old name -> new name (vanilla calls the new name; a 1.21.8 override answers it, its super call
falls through to vanilla's), mixinTargets follow from methodRenames.
"""
import json
import os
import sys
import zipfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import bytecode  # noqa: E402

GUARD = "top/dext/centurybridge/step/v1_21_9/rt/Guard"
OUT = "core/segments/steps/1.21.8__1.21.9.json.d/s9c-renumbered-gen.json"


def load(jar):
    out = {}
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if n.endswith(".class") and n.startswith("net/minecraft/") or n.startswith("com/mojang/"):
                if not n.endswith(".class"):
                    continue
                k = bytecode.read(z.read(n))
                if k is not None:
                    out[k.name] = k
    return out


def norm(insns, mask):
    out = []
    for op, arg in insns:
        if isinstance(arg, tuple) and arg and arg[0] == "ref" and arg[1] == "method" and (arg[2], arg[3], arg[4]) in mask:
            arg = ("ref", "method", arg[2], "?", arg[4])
        if isinstance(arg, tuple) and arg and arg[0] == "ref" and len(arg) > 4 and (arg[2], arg[3], arg[4]) in mask:
            arg = ("ref", arg[1], arg[2], "?", arg[4])
        out.append((op, arg))
    return out


def main():
    vj, wj = sys.argv[1], sys.argv[2]
    V, W = load(vj), load(wj)
    gone, new = {}, {}
    for cn, vk in V.items():
        wk = W.get(cn)
        if wk is None:
            continue
        vs = {(m.name, m.desc) for m in vk.methods}
        ws = {(m.name, m.desc) for m in wk.methods}
        g = [m for m in vk.methods if (m.name, m.desc) not in ws and not m.name.startswith("<") and not m.name.startswith("lambda$")]
        n = [m for m in wk.methods if (m.name, m.desc) not in vs and not m.name.startswith("<") and not m.name.startswith("lambda$")]
        if g and n:
            gone[cn], new[cn] = g, n
    mask = set()
    for cn in gone:
        for m in gone[cn]:
            mask.add((cn, m.name, m.desc))
        for m in new[cn]:
            mask.add((cn, m.name, m.desc))
    pairs = {}
    for cn in gone:
        for gm in gone[cn]:
            st = gm.access & bytecode.ACC_STATIC
            cands = [m for m in new[cn] if m.desc == gm.desc and (m.access & bytecode.ACC_STATIC) == st]
            if len(cands) != 1 or gm.insns is None or cands[0].insns is None:
                continue
            nm = cands[0]
            if len([m for m in gone[cn] if m.desc == gm.desc and (m.access & bytecode.ACC_STATIC) == st]) != 1:
                continue
            if norm(gm.insns, mask) == norm(nm.insns, mask):
                pairs[(cn, gm.name, gm.desc)] = nm.name
    # abstract pairs, proven by a concrete pair in an implementor
    def supers(cn):
        k = W.get(cn)
        seen, todo = [], [cn]
        while todo:
            c = todo.pop()
            if c in seen or c not in W:
                continue
            seen.append(c)
            todo.append(W[c].super)
            todo.extend(W[c].interfaces)
        return seen
    for cn in gone:
        for gm in gone[cn]:
            if gm.insns is not None or (cn, gm.name, gm.desc) in pairs:
                continue
            ga = [m for m in gone[cn] if m.desc == gm.desc and m.insns is None]
            na = [m for m in new[cn] if m.desc == gm.desc and m.insns is None]
            if len(ga) != 1 or len(na) != 1:
                continue
            # a 1.21.9 class under this one (an implementor) inheriting a concrete pair of the same names
            proof = [x for x in W if cn in supers(x) and x != cn
                     and any(pairs.get((c, gm.name, gm.desc)) == na[0].name for c in supers(x))]
            if proof:
                pairs[(cn, gm.name, gm.desc)] = na[0].name
    # a method another fragment already wires (getBuffer's overrideRenames, getVisualRotationYInDegrees' hooks,
    # particles' getRenderType, whose meaning changed) is that fragment's: left out by name and descriptor
    d0 = os.path.dirname(OUT)
    wired = ""
    for f in sorted(os.listdir(d0)):
        if f.endswith(".json") and f != os.path.basename(OUT):
            wired += open(os.path.join(d0, f), encoding="utf-8").read()
    for k in [k for k in pairs if k[1] + k[2] in wired]:
        print(f"  (wired elsewhere) {k[0]}.{k[1]}{k[2]}", file=sys.stderr)
        del pairs[k]
    for (cn, n, d), nn in sorted(pairs.items()):
        print(f"{cn}.{n}{d} -> {nn}")
    print(f"{len(pairs)} renumbered", file=sys.stderr)
    if "--write" in sys.argv:
        renames = {f"{cn}.{n}{d}": nn for (cn, n, d), nn in sorted(pairs.items())}
        hooks = {}
        for (cn, n, d), nn in sorted(pairs.items()):
            vk = V[cn]
            m = next(x for x in vk.methods if x.name == n and x.desc == d)
            if m.access & (bytecode.ACC_STATIC | bytecode.ACC_PRIVATE | bytecode.ACC_FINAL):
                continue
            key = (n, d, nn)
            hooks.setdefault(key, []).append(cn)
        oh = []
        abstract_tops = {cn for (cn, n, d) in pairs
                         if next(x for x in V[cn].methods if x.name == n and x.desc == d).insns is None}
        for (n, d, nn), owners in sorted(hooks.items()):
            # one hook per renamed shape, under the topmost declaring class of that family
            tops = [c for c in owners if not any(o != c and o in supers(c) for o in owners)]
            for top in sorted(tops):
                oh.append({
                    "old": n + d, "new": nn + d, "guard": GUARD, "onlyUnder": top,
                    "basis": "s9c renumbered: 1.21.9's intermediary renamed " + top + "." + n + " to " + nn
                    + (" (same descriptor, abstract: implemented by the renumbered method of a 1.21.9 implementor; gen_s9c_renumbered.py)"
                       if top in abstract_tops else " (same descriptor, same body: gen_s9c_renumbered.py)")
                    + "; vanilla calls the new name, the 1.21.8 override"
                    " answers it, its super call falls through to vanilla's",
                })
        frag = {
            "methodRenames": renames,
            "overrideHooks": oh,
        }
        with open(OUT, "w", encoding="utf-8", newline="\n") as f:
            json.dump(frag, f, indent=1, sort_keys=False)
            f.write("\n")
        print("wrote " + OUT, file=sys.stderr)


if __name__ == "__main__":
    main()
