"""Mechanical bridges for one climb step, derived from its damage table.

A damage row (core/segments/damage/<V>__<W>.damage.tsv) is a V member, used one way, that W no
longer links. When W still has the member -- the same name on the owner or one of its supertypes
(intermediary names follow a member through releases), the same constructor owner -- under another
descriptor, the old and new shapes are compared argument by argument against the class hierarchy.
The bridge is mechanical only when every value can be passed on unchanged and nothing has to be
invented:

  calls (call, call-static, protected calls, a mod's super call)            -> "callReshapes"
  constructors (new X(..) and super(..) in a subclass)                      -> "superCtorReshapes"
      each new argument is the old argument of exactly its type in the same relative order, or
      the one old argument of a subtype; the return value is the same type, a subtype, or popped
      when the old shape returned nothing. An old argument the new shape has no place for is
      dropped only when no body in V -- the method's and every override's -- ever read it: then
      it never changed what V did.
  overrides (a mod method vanilla called in V)                              -> "overrideHooks"
      the new shape vanilla now calls forwards to the mod's old method, each old parameter fed
      from the new argument of its type (a new argument the old method never had is not passed),
      when what the old method returns is what the new shape returns (or a subtype of it). The
      hook carries its arguments ("frames": the layer's Guard) and rewrites the old method's own
      super call to the new shape ("superCalls"), so vanilla's implementation still gets every
      argument vanilla passed -- the ones the old method never saw included.

Arguments of distinct types may change places (1.20.3 moved a block's Settings to the end of its
constructor); arguments of one type keep their relative order, since types alone cannot tell them
apart -- a release that swapped two of them would need a hand-written entry. Everything else -- a value the new shape
needs that the old call does not have, a narrowing, a dropped argument V used, a converted
return -- is listed as "design" with the reason. Members the step's hand-written wiring already
handles are skipped ("wired"). The output is a wiring fragment for the step
(<step wiring>.d/mechanical.json, see engine/Wiring.java) and a report of every row.

  python gen_step_reshapes.py <damage.tsv> <V code jar> <W stub jar> <step wiring.json> <guard class> <out report.tsv>
         [--wall <inter-to-mojang-1.21.11.tsv>]   (the 1.21.11 -> 26.1 step: V intermediary, W mojang)
"""
import collections
import json
import os
import sys
import zipfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import classfile  # noqa: E402
from classfile import ACC_BRIDGE, ACC_FINAL, ACC_PRIVATE, ACC_STATIC, ACC_SYNTHETIC, split_args  # noqa: E402

PRIM = set("ZBCSIJFD")
OVERRIDES = ("override+super", "implement", "implement-inherited")


def load(jar, code):
    out = {}
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if n.endswith(".class") and (n.startswith("net/minecraft/") or n.startswith("com/mojang/")):
                c = classfile.read(z.read(n), code)
                if c:
                    out[c.name] = c
    return out


class Hierarchy:
    def __init__(self, classes):
        self.classes = classes
        self._sup = {}

    def supers(self, c):
        """c and every supertype the jar knows, nearest first"""
        if c in self._sup:
            return self._sup[c]
        out, todo = [], [c]
        while todo:
            x = todo.pop(0)
            if x is None or x in out:
                continue
            out.append(x)
            k = self.classes.get(x)
            if k:
                todo.append(k.super)
                todo.extend(k.interfaces)
        self._sup[c] = out
        return out

    def assignable(self, frm, to):
        """a value of descriptor frm passes unchanged where descriptor to is expected"""
        if frm == to:
            return True
        if frm in PRIM or to in PRIM or frm.startswith("[") or to.startswith("["):
            return False
        if to == "Ljava/lang/Object;":
            return True
        return to[1:-1] in self.supers(frm[1:-1])


def match(src, dst, h):
    """Map every dst argument to the src argument that supplies it.

    Returns (mapping, problem): mapping[j] is the src index feeding dst[j]. Exact types pair up in
    order and must occur as often on both sides; a dst type absent from src takes the one unused src
    argument of a subtype. problem is a reason string when no such mapping exists."""
    mapping = [None] * len(dst)
    used = set()
    src_pos = collections.defaultdict(list)
    dst_pos = collections.defaultdict(list)
    for i, t in enumerate(src):
        src_pos[t].append(i)
    for j, t in enumerate(dst):
        dst_pos[t].append(j)
    for t, js in dst_pos.items():
        if t in src_pos:
            if len(src_pos[t]) != len(js):
                return None, f"{t} occurs {len(src_pos[t])}x before and {len(js)}x after: which one is which is not decidable by type"
            for i, j in zip(src_pos[t], js):
                mapping[j] = i
                used.add(i)
    for j, t in enumerate(dst):
        if mapping[j] is not None:
            continue
        cand = [i for i, s in enumerate(src) if i not in used and s not in dst_pos and h.assignable(s, t)]
        if len(cand) != 1:
            return None, (f"needs {t}, which nothing passed supplies" if not cand
                          else f"{t} could come from any of {[src[i] for i in cand]}")
        mapping[j] = cand[0]
        used.add(cand[0])
    return mapping, None


def implementations(V, hv, owner, name, desc):
    """every body V has for the member: the declaration the owner resolves to and all overrides"""
    out = []
    if name == "<init>":
        k = V.get(owner)
        m = next((m for m in k.methods if m.name == name and m.desc == desc), None) if k else None
        return [(owner, m)] if m else []
    for c, k in V.items():
        for m in k.methods:
            if m.name == name and m.desc == desc and not m.access & (ACC_BRIDGE | ACC_SYNTHETIC):
                if c == owner or owner in hv.supers(c) or c in hv.supers(owner):
                    out.append((c, m))
    return out


def wired_keys(wiring):
    """what the step's own wiring already serves: call-side keys ("owner.name(desc)", matched through
    the owner's supertypes the way the engine resolves a call), constructor keys, and the old
    shapes of overrides it reconnects"""
    calls, ctors, shapes = set(), set(), set()
    for sec in ("staticRedirects", "instanceRedirects", "callReshapes", "covers", "methodRenames", "superRedirects"):
        v = wiring.get(sec, {})
        calls.update(v if isinstance(v, list) else v.keys())
    for sec in ("ctorRedirects", "superCtorReshapes", "superCtorAdapts"):
        ctors.update(wiring.get(sec, {}).keys())
    for oh in wiring.get("overrideHooks", []):
        shapes.add(oh["old"])
    for ah in wiring.get("adapterHooks", []):
        shapes.update(ah.get("requires", []))
    return calls, ctors, shapes


class Wall:
    """V's intermediary names onto W's mojang ones across the 1.21.11 -> 26.1 wall (identity without
    a table), and W's class names back: the engine rewrites the wall step's call sites in V's names and
    renames the whole jar afterwards, so emitted descriptors name a class by its intermediary name when
    1.21.11 had it and by its mojang name only when it is new in W."""

    def __init__(self, tsv=None):
        self.cmap, self.mmap, self.back = {}, {}, {}
        if tsv:
            for line in open(tsv, encoding="utf-8"):
                f = line.rstrip("\n").split("\t")
                if len(f) < 3:
                    continue
                if f[0] == "C":
                    self.cmap[f[1]] = f[2]
                    self.back[f[2]] = f[1]
                elif f[0] == "M":
                    self.mmap[f[1]] = f[2].rsplit(".", 1)[1]

    def _map(self, c, table):
        if c in table:
            return table[c]
        d = c.rfind("$")
        return self._map(c[:d], table) + c[d:] if d > 0 else c

    def _desc(self, d, table):
        out, i = [], 0
        while i < len(d):
            ch = d[i]
            out.append(ch)
            if ch == "L":
                j = d.index(";", i)
                out.append(self._map(d[i + 1:j], table) + ";")
                i = j
            i += 1
        return "".join(out)

    def cls(self, c):
        return self._map(c, self.cmap) if self.cmap else c

    def desc(self, d):
        return self._desc(d, self.cmap) if self.cmap else d

    def member(self, owner, name, hv):
        """the W name of V's member, looked up on the owner and then on its supertypes in V"""
        if not self.mmap or name.startswith("<"):
            return name
        for c in hv.supers(owner):
            m = self.mmap.get(f"{c}.{name}")
            if m:
                return m
        return name

    def back_desc(self, d):
        return self._desc(d, self.back) if self.back else d


def main():
    args = sys.argv[1:]
    wall = Wall()
    if "--wall" in args:
        k = args.index("--wall")
        wall = Wall(args[k + 1])
        args = args[:k] + args[k + 2:]
    damage, vjar, wjar, wiring_path, guard, out_report = args[0:6]
    V = load(vjar, True)
    W = load(wjar, False)
    hv, hw = Hierarchy(V), Hierarchy(W)
    base = json.load(open(wiring_path, encoding="utf-8")) if os.path.exists(wiring_path) else {}
    wired_calls, wired_ctors, wired_shapes = wired_keys(base)
    ledgered = set(base.get("ledger", {}).keys())
    calls, ctors, hooks, report = {}, {}, [], []
    hook_seen = set()

    def note(r, fate, detail):
        report.append((r[0], r[1], r[2], r[3], fate, detail))

    rows = [l.rstrip("\n").split("\t") for l in open(damage, encoding="utf-8")][1:]
    for r in rows:
        usage, owner, name, desc, kinds, _ = r
        # fields, and the class-level rows (extend / implement-interface: closed by their members' bridges)
        if usage.startswith(("get", "put")) or usage in ("extend", "implement-interface", "lambda") or not name \
                or not ({"METHOD", "ORPHAN", "ABSTRACT"} & set(kinds.split(","))):
            continue
        key = f"{owner}.{name}{desc}"
        override = usage in OVERRIDES
        if name == "<init>":
            wired = key in wired_ctors
        elif override:
            wired = name + desc in wired_shapes
        else:
            wired = any(f"{c}.{name}{desc}" in wired_calls for c in hv.supers(owner))
        if wired:
            note(r, "wired", "the step's wiring already serves it")
            continue
        # W is looked at in its own names (across the wall: V's intermediary names translated)
        wowner, wname = wall.cls(owner), wall.member(owner, name, hv)
        oa, orr = split_args(wall.desc(desc))
        # the member's shapes in W: the owner's constructors, or the name on the owner and its supertypes
        if name == "<init>":
            k = W.get(wowner)
            shapes_w = [(wowner, m) for m in (k.methods if k else []) if m.name == "<init>" and not m.access & ACC_PRIVATE]
        else:
            shapes_w = []
            for c in hw.supers(wowner):
                k = W.get(c)
                for m in (k.methods if k else []):
                    if m.name == wname and not m.access & (ACC_BRIDGE | ACC_SYNTHETIC | ACC_PRIVATE) \
                            and all(m.desc != y.desc for _, y in shapes_w):
                        shapes_w.append((c, m))
        if not shapes_w:
            note(r, "design", "W has no member of this name on the owner or its supertypes")
            continue
        if override:
            if name == "<init>":
                continue
            fits = []
            for c, m in shapes_w:
                if m.access & (ACC_FINAL | ACC_STATIC):
                    continue
                na, nr = split_args(m.desc)
                amap, problem = match(na, oa, hw)  # each OLD parameter from a NEW argument
                if problem:
                    fits.append((c, m, None, f"old method needs: {problem}"))
                    continue
                # vanilla used what the old method returned; a new shape that returns nothing gets its
                # result some other way, so the old method's answer would be lost
                if not (nr == orr or hw.assignable(orr, nr)):
                    fits.append((c, m, None, f"returns {orr}, the new shape {nr}"))
                    continue
                fits.append((c, m, amap, None))
            good = [f for f in fits if f[2] is not None]
            if len(good) != 1:
                note(r, "design", "; ".join(f"{m.desc}: {p}" for _, m, _, p in fits) if not good
                     else f"{len(good)} new shapes fit")
                continue
            c, m, amap, _ = good[0]
            nd = wall.back_desc(m.desc)
            hk = (name, desc, nd, owner)
            if hk in hook_seen:
                note(r, "hook", f"-> {nd} (same hook)")
                continue
            hook_seen.add(hk)
            na, _ = split_args(m.desc)
            unpassed = [na[j] for j in range(len(na)) if j not in amap]
            hooks.append({"old": name + desc, "new": name + nd, "args": amap, "onlyUnder": owner, "frames": guard, "superCalls": "rewrite",
                          "basis": "mechanical: the old method gets every parameter it had"
                                   + ("" if amap == sorted(amap) else ", reordered by type")
                                   + (f"; new {','.join(unpassed)} not passed" if unpassed else "")})
            note(r, "hook", f"-> {nd} args {amap}")
            continue
        # a call: the new shape's arguments from the old call's
        fits = []
        for c, m in shapes_w:
            if name != "<init>" and bool(m.access & ACC_STATIC) != (usage in ("call-static", "call-static-protected")):
                fits.append((c, m, None, "static-ness changed"))
                continue
            na, nr = split_args(m.desc)
            amap, problem = match(oa, na, hw)
            if problem:
                fits.append((c, m, None, problem))
                continue
            if not (nr == orr or orr == "V" or hw.assignable(nr, orr)):
                fits.append((c, m, None, f"returns {nr}, the old call wants {orr}"))
                continue
            fits.append((c, m, amap, None))
        good = [f for f in fits if f[2] is not None]
        if len(good) != 1:
            note(r, "design", "; ".join(f"{m.desc}: {p}" for _, m, _, p in fits) if not good else f"{len(good)} new shapes fit")
            continue
        c, m, amap, _ = good[0]
        na, nr = split_args(m.desc)
        dropped = [i for i in range(len(oa)) if i not in amap]
        if dropped:
            bodies = implementations(V, hv, owner, name, desc)
            readers = []
            for bc, bm in bodies:
                reads = classfile.param_reads(bm)
                if reads and reads & set(dropped):
                    readers.append(bc)
            if not bodies:
                note(r, "design", f"drops {[oa[i] for i in dropped]}; V has no body to tell whether it mattered")
                continue
            if readers:
                note(r, "design", f"drops {[oa[i] for i in dropped]}, which V reads in {readers[:3]}")
                continue
        ops = [f"keep:{i}" + ("" if oa[i] == na[j] else f" {wall.back_desc(na[j])}") for j, i in enumerate(amap)]
        ops += [f"drop:{i}" for i in dropped]
        nd = wall.back_desc(m.desc)
        entry = {"desc": nd, "ops": ops}
        if nr != orr:
            entry["ret"] = "pop" if orr == "V" else "keep"
        entry["basis"] = "mechanical: same values" + ("" if amap == sorted(amap) else ", reordered by type") + (f"; dropped {','.join(oa[i] for i in dropped)}, never read in V" if dropped else "") \
            + {"pop": "; return value popped", "keep": "; returns a subtype of the old type"}.get(entry.get("ret"), "")
        (ctors if name == "<init>" else calls)[key] = entry
        note(r, "ctor-reshape" if name == "<init>" else "reshape", f"-> {nd} {ops}"
             + ("; the base ledger entry for it should go" if key in ledgered else ""))
    # one hook per shape change, on the topmost owner: a mod class under two owners of one group
    # (Block and CropBlock both declared getPickStack) would otherwise get the new shape twice
    groups = collections.defaultdict(list)
    for hk in hooks:
        groups[(hk["old"], hk["new"])].append(hk)
    hooks = [hk for g in groups.values() for hk in g
             if not any(o is not hk and o["onlyUnder"] != hk["onlyUnder"] and o["onlyUnder"] in hv.supers(hk["onlyUnder"]) for o in g)]
    frag = {}
    if calls:
        frag["callReshapes"] = calls
    if ctors:
        frag["superCtorReshapes"] = ctors
    if hooks:
        frag["overrideHooks"] = hooks
    out_dir = wiring_path + ".d"
    os.makedirs(out_dir, exist_ok=True)
    with open(os.path.join(out_dir, "mechanical.json"), "w", encoding="utf-8", newline="\n") as f:
        f.write(json.dumps(frag, indent=1, ensure_ascii=False) + "\n")
    with open(out_report, "w", encoding="utf-8", newline="\n") as f:
        f.write("usage\towner\tname\tdesc\tfate\tdetail\n")
        for x in report:
            f.write("\t".join(x) + "\n")
    fates = collections.Counter(x[4] for x in report)
    print(f"{len(rows)} damage rows; method/constructor/override rows {sum(fates.values())}: {dict(fates)}")
    print(f"  -> {len(calls)} call reshapes, {len(ctors)} constructor reshapes, {len(hooks)} override hooks in {out_dir}/mechanical.json")


if __name__ == "__main__":
    main()
