#!/usr/bin/env python3
"""Static members a climb step moved to another class: V's owner still exists (or was renamed) but no longer
declares them, and one W class declares the same name with the same (translated) descriptor and the same
code. 26.2 moved EntityType's constants to EntityTypes, BlockEntityType's to BlockEntityTypes,
DripstoneUtils' helpers to SpeleothemUtils; a V-era mod's GETSTATIC / INVOKESTATIC still names the old owner.

For every static member of the step's damage table (core/segments/damage/<V>__<W>.damage.tsv, usages
get-static / call-static and their -inherited / -protected forms) whose owner W no longer gives it:

  method  a W class X (not the owner, not one of its supertypes) declares a static method of that name and
          translated descriptor whose body is V's body instruction for instruction, class names translated
          through the step's classRenames (pkgmoves-gen.json) and V's owner read as X (a moved helper calls
          its moved siblings). The static fields of the owner the body reads must hold the same in X too: their
          <clinit> stretches the same, and the owner's members those stretches name (a lambda's method
          included) the same code (deep_same: 26.1.2 BedRenderer.modelTransform and 26.2
          ShulkerBoxRenderer.modelTransform are both TRANSFORMATIONS.get(facing), over maps built by different
          transforms). -> staticRedirects key -> X
  field   X declares a static field of that name and translated descriptor, and the stretch of X.<clinit>
          computing it (from the previous PUTSTATIC to X's own field, to its own PUTSTATIC) is V's stretch in
          the owner's <clinit> instruction for instruction under the same translation; or, when the stretches
          differ, both name the same non-empty sequence of string constants (a registry id: EntityTypes.PIG
          is still registered as "pig") and no other field of X has that sequence.
          -> fieldRedirects key -> X

Exactly one X must qualify; same name and descriptor alone is never evidence (BlockTags and ItemTags share
tag names, feature keys share ResourceKey). A key another fragment of the step already wires is left to it.
Members without such evidence stay open (listed on stdout with the closest candidate and why).

    python3 core/tools/gen_static_moves.py <V code jar> <W jar> <V> <W>
        writes core/segments/steps/<V>__<W>.json.d/staticmoves-gen.json
"""
import collections
import glob
import json
import os
import re
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import bytecode  # noqa: E402
import wall_table  # noqa: E402

PUTSTATIC, GETSTATIC = 0xb3, 0xb2
DESC_CLS = re.compile(r"L([^;<]+)([;<])")
# id constants: a read of one stands for the strings of its own initializer
KEYS = {"Lnet/minecraft/resources/ResourceKey;", "Lnet/minecraft/resources/Identifier;"}


def main():
    vjar, wjar, v, w = sys.argv[1:5]
    step = f"{v}__{w}"
    d = f"core/segments/steps/{step}.json.d"
    out_path = f"{d}/staticmoves-gen.json"
    V, W = wall_table.load(vjar), wall_table.load(wjar)
    cr, mren = {}, {}
    wired = set()
    for f in sorted(glob.glob(f"{d}/*.json")):
        if f.replace("\\", "/") == out_path:
            continue
        j = json.load(open(f, encoding="utf-8"))
        cr.update(j.get("classRenames", {}))
        mren.update(j.get("_pkgmoves_member_renames", {}))
        for sec in ("staticRedirects", "fieldRedirects", "fieldGetters", "callReshapes", "ledger"):
            wired.update(j.get(sec, {}).keys())

    def tc(c):
        if c in cr:
            return cr[c]
        k = c.rfind("$")
        if k > 0 and c[:k] in cr and c not in V:
            return cr[c[:k]] + c[k:]
        return c

    def td(desc):
        return DESC_CLS.sub(lambda m: "L" + tc(m.group(1)) + m.group(2), desc)

    def supers(c, jar):
        out, todo = [], [c]
        while todo:
            x = todo.pop()
            if x is None or x in out:
                continue
            out.append(x)
            k = jar.get(x)
            if k:
                todo.append(k.super)
                todo.extend(k.interfaces)
        return out

    def declares(c, kind, name, desc, jar):
        for s in supers(c, jar):
            k = jar.get(s)
            if k and any(m.name == name and m.desc == desc for m in (k.fields if kind == "F" else k.methods)):
                return True
        return False

    def norm(x, owner, X):
        """a V operand as W would spell it, V's owner read as X"""
        if isinstance(x, tuple) and x:
            t = x[0]
            if t == "class":
                c = tc(x[1]) if not x[1].startswith("[") else td(x[1])
                return ("class", X if x[1] == owner else c)
            if t == "ref":
                _, kind, o, n, ds = x
                n = mren.get(o + "." + n, n)
                return ("ref", kind, X if o == owner else tc(o), n, td(ds))
            if t == "mhandle":
                _, tag, o, n, ds = x
                n = mren.get(o + "." + n, n)
                return ("mhandle", tag, X if o == owner else tc(o), n, td(ds))
            if t == "mtype":
                return ("mtype", td(x[1]))
            if t == "indy":
                return ("indy", x[1], td(x[2]), norm(x[3], owner, X), tuple(norm(a, owner, X) for a in x[4]))
            return tuple(norm(a, owner, X) for a in x)
        return x

    def same(vins, wins, owner, X):
        if vins is None or wins is None or len(vins) != len(wins):
            return False
        for (op, a), (wop, wa) in zip(vins, wins):
            if op != wop:
                return False
            if norm(a, owner, X) != wa:
                return False
        return True

    def stretches(k):
        """{field: [insns]} of a class's <clinit>: the code from the last PUTSTATIC to the class's own field"""
        out = {}
        cl = next((m for m in k.methods if m.name == "<clinit>"), None)
        if cl is None or cl.insns is None:
            return out
        start = 0
        for i, (op, a) in enumerate(cl.insns):
            if op == PUTSTATIC and isinstance(a, tuple) and a[2] == k.name:
                out.setdefault(a[3], cl.insns[start:i])
                start = i + 1
        return out

    def strings(ins, jar, depth=1):
        """the string constants a stretch names, a static key constant it reads (EntityTypeIds.PIG) read as the
        strings of its own initializer"""
        out = []
        for op, a in ins:
            if not (isinstance(a, tuple) and a):
                continue
            if a[0] == "str":
                out.append(a[1])
            elif op == GETSTATIC and a[0] == "ref" and depth > 0 and a[2] in jar and a[4] in KEYS:
                out.extend(strings(st(jar, a[2]).get(a[3], []), jar, depth - 1))
        return tuple(out)

    def tsig(s):
        return None if s is None else DESC_CLS.sub(lambda m: "L" + tc(m.group(1)) + m.group(2), s)

    # W statics by (name, desc)
    widx = collections.defaultdict(list)
    for c, k in W.items():
        for m in k.methods + k.fields:
            if m.access & bytecode.ACC_STATIC and not m.access & bytecode.ACC_PRIVATE:
                widx[("F" if isinstance(m, bytecode.Fld) else "M", m.name, m.desc)].append(c)
    stretch_cache = {}

    def st(jar, c):
        if (id(jar), c) not in stretch_cache:
            stretch_cache[(id(jar), c)] = stretches(jar[c])
        return stretch_cache[(id(jar), c)]

    def own_refs(ins, owner):
        """the members of owner a stretch of code names (fields read or written, methods called or referenced by
        a handle, a lambda's handle inside an invokedynamic included): kind, name, descriptor"""
        out = set()

        def walk(x):
            if isinstance(x, tuple) and x:
                if x[0] == "ref" and x[2] == owner:
                    out.add(("F" if x[1] == "F" else "M", x[3], x[4]))
                elif x[0] == "mhandle" and x[2] == owner:
                    out.add(("F" if not x[4].startswith("(") else "M", x[3], x[4]))
                else:
                    for a in x:
                        walk(a)
        for _, a in ins or ():
            walk(a)
        return out

    def deep_same(kind, name, desc, home, X, seen, state_only=True):
        """V's member of home and W's of X are the same, and so is the state of home its code reads (read as
        X's): a body that is the same instructions over its own class's static fields is the same code only where
        those fields hold the same -- their initializer stretches the same, and every member of home those
        stretches name (a lambda's method included) the same too (26.1.2 BedRenderer.modelTransform and 26.2
        ShulkerBoxRenderer.modelTransform both answer TRANSFORMATIONS.get(facing); the two maps are built by
        different createModelTransform bodies). A method body's calls to its own class's methods are not
        followed (state_only): W's method of that name is W's counterpart, which may differ by W's own API
        (26.2 LightCoordsUtil.getLightCoords(level, pos) calls its 4-argument form, whose emissiveRendering() lost
        its arguments)."""
        if (kind, name, desc) in seen:
            return True
        seen.add((kind, name, desc))
        wname, wdesc = mren.get(home + "." + name, name), td(desc)
        if kind == "M":
            vm = next((m for m in V[home].methods if m.name == name and m.desc == desc), None)
            wm = next((m for m in W[X].methods if m.name == wname and m.desc == wdesc), None) if X in W else None
            if vm is None or wm is None or not same(vm.insns, wm.insns, home, X):
                return False
            vins = vm.insns
        else:
            vf = next((m for m in V[home].fields if m.name == name and m.desc == desc), None)
            wf = next((m for m in W[X].fields if m.name == wname and m.desc == wdesc), None) if X in W else None
            if vf is None or wf is None:
                return False
            vs, wsx = st(V, home).get(name), st(W, X).get(wname)
            if vs is None or wsx is None:
                return vs is None and wsx is None and vf.const == wf.const
            if not same(vs, wsx, home, X):
                return False
            vins = vs
            state_only = False
        return all(deep_same(k, n, d, home, X, seen, state_only) for k, n, d in own_refs(vins, home)
                   if k == "F" or not state_only)

    rows = set()
    for line in open(f"core/segments/damage/{step}.damage.tsv", encoding="utf-8"):
        f = line.rstrip("\n").split("\t")
        if len(f) < 4 or "static" not in f[0]:
            continue
        rows.add((f[1], f[2], f[3]))

    fr, sr, basis, left = {}, {}, {}, []
    for owner, name, desc in sorted(rows):
        kind = "M" if desc.startswith("(") else "F"
        # the declaring V class (a site may name a subclass)
        home = next((s for s in supers(owner, V) if s in V and any(
            m.name == name and m.desc == desc and m.access & bytecode.ACC_STATIC
            for m in (V[s].fields if kind == "F" else V[s].methods))), None)
        if home is None:
            continue
        key = f"{home}.{name}{desc}" if kind == "M" else f"{home}.{name}:{desc}"
        if key in wired or key in fr or key in sr:
            continue
        wowner, wdesc = tc(home), td(desc)
        if wowner in W and declares(wowner, kind, mren.get(home + "." + name, name), wdesc, W):
            continue
        ws = set(supers(wowner, W))
        cands = [x for x in widx.get((kind, name, wdesc), []) if x not in ws]
        vm = next(m for m in (V[home].fields if kind == "F" else V[home].methods) if m.name == name and m.desc == desc)
        ok = []
        why = {}
        for X in cands:
            wm = next(m for m in (W[X].fields if kind == "F" else W[X].methods) if m.name == name and m.desc == wdesc)
            if kind == "M":
                if same(vm.insns, wm.insns, home, X):
                    if deep_same("M", name, desc, home, X, set()):
                        ok.append((X, "same code"))
                    else:
                        why[X] = "same instructions over members of the class that differ"
                else:
                    why[X] = "body differs"
            else:
                vs, wsx = st(V, home).get(name), st(W, X).get(name)
                if vs is None or wsx is None:
                    if vm.const is not None and vm.const == wm.const:
                        ok.append((X, f"same constant value {vm.const!r}"))
                    else:
                        why[X] = "no <clinit> stretch to compare"
                    continue
                if same(vs, wsx, home, X):
                    ok.append((X, "same initializer code"))
                    continue
                # a registry constant: the same id (a literal on V, often a key constant on W), the same
                # generic type (ResourceKey<Block> is not ResourceKey<ConfiguredFeature>), the one such field of X
                s = strings(vs, V)
                if not s or strings(wsx, W) != s:
                    why[X] = "initializer differs"
                elif tsig(vm.signature) != wm.signature:
                    why[X] = f"same strings, generic type differs ({vm.signature} / {wm.signature})"
                elif sum(1 for g, ins in st(W, X).items() if strings(ins, W) == s) != 1:
                    why[X] = "same strings, not unique in X"
                else:
                    ok.append((X, "registered under the same id " + json.dumps(list(s)) + ", same type"))
        if len(ok) == 1:
            X, b = ok[0]
            (sr if kind == "M" else fr)[key] = X
            basis[key] = f"{home} -> {X}: {b}"
        elif cands:
            left.append((key, "ambiguous: " + ", ".join(x for x, _ in ok) if ok else
                         "; ".join(f"{x}: {why[x]}" for x in cands)))
    out = {
        "_staticmoves_generated": f"core/tools/gen_static_moves.py ... {v} {w}: static members W moved to another "
                                  "class, each by same code (method) or same initializer (field); regenerate, never hand-edit",
        "staticRedirects": dict(sorted(sr.items())),
        "fieldRedirects": dict(sorted(fr.items())),
        "_staticmoves_basis": dict(sorted(basis.items())),
    }
    with open(out_path, "w", encoding="utf-8", newline="\n") as o:
        json.dump(out, o, indent=1, ensure_ascii=False)
        o.write("\n")
    per = collections.Counter(f"{k.split('.')[0]} -> {v}" for k, v in list(sr.items()) + list(fr.items()))
    for k, n in per.most_common():
        print(f"  {n:4d} {k}")
    print(f"-> {out_path}: {len(sr)} staticRedirects, {len(fr)} fieldRedirects; {len(left)} candidates without evidence:")
    for k, why in left:
        print(f"  open {k}: {why}")


if __name__ == "__main__":
    main()
