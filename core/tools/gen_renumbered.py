"""gen_renumbered.py: methods a release kept under a new name -- the same method, the same body, another name.

  python3 core/tools/gen_renumbered.py <V> <W> [--write]     one climb step (jars from data/jars)
  python3 core/tools/gen_renumbered.py --all                 every step of the climb: counts only

Intermediary renumbers some unchanged methods between releases (1.21.9: Entity.level() method_37908 -> method_73183,
with OwnableEntity / ContainerEntity's level() and every override; Mojang's name and the descriptor stayed); past the
namespace wall Mojang renames a method and keeps its code (26.1.1: RenderPipeline.getVertexFormatMode ->
getPrimitiveTopology). The probe sees the old name gone and every V call, override and mixin of it dies.

A pair is judged from bytecode only, never by name:
  * a class declares, in V, a method (n, d) that W's same class no longer declares (and W's hierarchy no longer
    resolves: an inherited (n, d) still links and needs no rename);
  * W's class declares a method of descriptor d, the same static-ness and abstract-ness, that V's class did not;
  * the two bodies are the same instruction for instruction (bytecode.py: constants and member references resolved,
    branch offsets dropped). A reference to a method that is itself gone or new is masked to its owner and
    descriptor, then checked against the pairs: V's reference must resolve to a gone method whose pair is the
    method W's reference resolves to (repeated until no pair drops: a renumbered method calling another renumbered
    one pairs, one calling a method that is gone for good does not);
  * the match is one-to-one in the class: the gone method matches exactly one new method and that one exactly one
    gone method;
  * abstract methods (no body) pair only when the class has exactly one gone and one new abstract method of that
    descriptor and static-ness, and a concrete pair proven by body in a W implementor (declared there or inherited)
    resolves the same way (OwnableEntity.level() is implemented by Entity.level()).
Listed and not wired (reported for review):
  * weak: the body has no member or class reference and at most 3 instructions (`iconst_0; ireturn`): sameness of
    such a body says nothing about identity;
  * narrowed: W's method is less accessible than V's (a V caller would hit IllegalAccessError);
  * wired elsewhere: another fragment of the step already names (n, d) (a hook or reshape that knows the meaning
    changed is that fragment's).
The wall step (1.21.11 -> 26.1) compares V's classes and members through the wall table (wall_tables.py, the
target's table); a pair there is a member the table missed, a wall_table.py matter: counted, never written.

Output (--write): core/segments/steps/<V>__<W>.json.d/renumbered-gen.json -- methodRenames for every declaring class
(methodRenameHome stops at a source declaration, so each declarer needs its key) and guarded overrideHooks old ->
new (vanilla calls the new name; a V override answers it, its super call falls through to vanilla's), one per
renamed shape under the topmost declaring class of that family; mixinTargets follow from methodRenames. Regenerate,
never hand-edit. Step 1 (1.20.1 -> 1.20.2) wires through shims-1.20.2.json: counted, not written.
"""
import json
import os
import sys
import zipfile

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
import bytecode  # noqa: E402
import wall_tables  # noqa: E402

ROOT = os.path.normpath(os.path.join(HERE, "..", ".."))
DATA = os.environ.get("CB_DATA", os.path.join(ROOT, "data"))
RELEASES = ["1.20.1", "1.20.2", "1.20.3", "1.20.4", "1.20.5", "1.20.6", "1.21", "1.21.1", "1.21.2", "1.21.3",
            "1.21.4", "1.21.5", "1.21.6", "1.21.7", "1.21.8", "1.21.9", "1.21.10", "1.21.11", "26.1", "26.1.1",
            "26.1.2", "26.2"]
WALL = ("1.21.11", "26.1")
OUT_NAME = "renumbered-gen.json"
# fragments this generator replaces (the 1.21.9 one it was generalized from)
SUPERSEDED = {"s9c-renumbered-gen.json"}
ACC_RANK = {bytecode.ACC_PUBLIC: 3, bytecode.ACC_PROTECTED: 2, 0: 1, bytecode.ACC_PRIVATE: 0}


def jar_of(ver):
    for n in (f"client-{ver}-intermediary-code.jar", f"client-{ver}.jar"):
        p = os.path.join(DATA, "jars", n)
        if os.path.exists(p):
            return p
    raise SystemExit(f"no code jar for {ver} under {DATA}/jars")


def load(jar):
    out = {}
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if n.endswith(".class") and (n.startswith("net/minecraft/") or n.startswith("com/mojang/")):
                k = bytecode.read(z.read(n))
                if k is not None:
                    out[k.name] = k
    return out


def rank(access):
    return ACC_RANK[access & (bytecode.ACC_PUBLIC | bytecode.ACC_PROTECTED | bytecode.ACC_PRIVATE)]


class Wall:
    """V's (intermediary) names spelled as W (26.1) declares them, through the wall table"""

    def __init__(self, table):
        self.cls, self.mem, self.byname = {}, {}, {}
        with open(table, encoding="utf-8") as f:
            for line in f:
                p = line.rstrip("\n").split("\t")
                if len(p) < 3:
                    continue
                if p[0] == "C":
                    self.cls[p[1]] = p[2]
                elif p[0] in ("M", "F"):
                    self.mem[p[1]] = p[2].rsplit(".", 1)[1]
                    # an intermediary number names one member and its overrides: a reference through any owner
                    # (a number whose rows disagree is left unspelled)
                    self.byname.setdefault(p[1].rsplit(".", 1)[1], set()).add(p[2].rsplit(".", 1)[1])

    def cname(self, c):
        return self.cls.get(c, c)

    def desc(self, d):
        out, i = [], 0
        while i < len(d):
            if d[i] == "L":
                j = d.index(";", i)
                out.append("L" + self.cname(d[i + 1:j]) + ";")
                i = j + 1
            else:
                out.append(d[i])
                i += 1
        return "".join(out)

    def member(self, owner, name):
        hit = self.mem.get(owner + "." + name)
        if hit:
            return hit
        alts = self.byname.get(name, ())
        return next(iter(alts)) if len(alts) == 1 else name


class Ident:
    def cname(self, c):
        return c

    def desc(self, d):
        return d

    def member(self, owner, name):
        return name


def resolver(classes):
    """(owner, name, desc) -> the declaring class's key, walking supers then interfaces (None: unresolved)"""
    memo = {}
    decl = {cn: {(m.name, m.desc) for m in k.methods} for cn, k in classes.items()}

    def resolve(o, n, d):
        key = (o, n, d)
        if key in memo:
            return memo[key]
        memo[key] = None
        seen, todo, hit = set(), [o], None
        while todo:
            c = todo.pop(0)
            if c in seen or c not in classes:
                continue
            seen.add(c)
            if (n, d) in decl[c]:
                hit = (c, n, d)
                break
            k = classes[c]
            if k.super:
                todo.append(k.super)
            todo.extend(k.interfaces)
        memo[key] = hit
        return hit
    return resolve


def is_ref(arg):
    return isinstance(arg, tuple) and arg and arg[0] in ("ref", "mhandle")


def ref_parts(arg):
    # ("ref", kind, owner, name, desc) / ("mhandle", tag, owner, name, desc)
    return arg[2], arg[3], arg[4]


def walk_refs(insns):
    """every member reference of a body in order, indy bootstrap arguments included"""
    for op, arg in insns:
        if is_ref(arg):
            yield arg
        elif isinstance(arg, tuple) and arg and arg[0] == "indy":
            for a in arg[4]:
                if is_ref(a):
                    yield a


def norm_arg(arg, masked):
    if is_ref(arg):
        o, n, d = ref_parts(arg)
        if masked(arg[0], o, n, d):
            return (arg[0], arg[1], o, "?", d)
        return arg
    if isinstance(arg, tuple) and arg and arg[0] == "indy":
        return ("indy", arg[1], arg[2], norm_arg(arg[3], masked), tuple(norm_arg(a, masked) for a in arg[4]))
    return arg


def weak(insns):
    if len(insns) > 3:
        return False
    for _, arg in insns:
        if isinstance(arg, tuple) and arg and arg[0] in ("ref", "mhandle", "class", "indy"):
            return False
    return True


def find(V, W, wall):
    """{(V class, name, desc): W name} plus the review lists"""
    rv, rw = resolver(V), resolver(W)
    gone, new = {}, {}
    for cn, vk in V.items():
        wk = W.get(wall.cname(cn))
        if wk is None:
            continue
        ws = {(m.name, m.desc) for m in wk.methods}
        vs = {(wall.member(cn, m.name), wall.desc(m.desc)) for m in vk.methods}
        g = [m for m in vk.methods if not m.name.startswith("<")
             and (wall.member(cn, m.name), wall.desc(m.desc)) not in ws]
        n = [m for m in wk.methods if not m.name.startswith("<") and (m.name, m.desc) not in vs]
        if g and n:
            gone[cn], new[cn] = g, n
    vchanged = {(cn, m.name, m.desc) for cn in gone for m in gone[cn]}
    wchanged = {(wall.cname(cn), m.name, m.desc) for cn in new for m in new[cn]}

    def vmask(kind, o, n, d):
        r = rv(o, n, d) if kind != "F" else None
        return r is not None and r in vchanged

    def wmask(kind, o, n, d):
        r = rw(o, n, d) if kind != "F" else None
        return r is not None and r in wchanged

    def vnorm(m):
        # V's references spelled in W's names, so unmasked ones compare to W's directly
        return [(op, spell(norm_arg(arg, vmask))) for op, arg in m.insns]

    def spell(a):
        if is_ref(a):
            o, n, d = ref_parts(a)
            nn = n if n == "?" else wall.member(o, n)
            return (a[0], a[1], wall.cname(o), nn, wall.desc(d))
        if isinstance(a, tuple) and a and a[0] == "class":
            return ("class", wall.cname(a[1]) if not a[1].startswith("[") else wall.desc(a[1]))
        if isinstance(a, tuple) and a and a[0] == "mtype":
            return ("mtype", wall.desc(a[1]))
        if isinstance(a, tuple) and a and a[0] == "indy":
            return ("indy", a[1], wall.desc(a[2]), spell(a[3]), tuple(spell(x) for x in a[4]))
        return a

    def wnorm(m):
        return [(op, norm_arg(arg, wmask)) for op, arg in m.insns]

    static = bytecode.ACC_STATIC
    pairs, cand_body = {}, {}
    for cn in gone:
        wcn = wall.cname(cn)
        nb = {id(m): (wnorm(m) if m.insns is not None else None) for m in new[cn]}
        matches = {}
        for gm in gone[cn]:
            if gm.insns is None:
                continue
            gb = vnorm(gm)
            matches[id(gm)] = [nm for nm in new[cn] if nm.insns is not None and nm.desc == wall.desc(gm.desc)
                               and (nm.access & static) == (gm.access & static) and nb[id(nm)] == gb]
        back = {}
        for gm in gone[cn]:
            for nm in matches.get(id(gm), []):
                back.setdefault(id(nm), []).append(gm)
        for gm in gone[cn]:
            ms = matches.get(id(gm), [])
            if len(ms) == 1 and len(back[id(ms[0])]) == 1:
                pairs[(cn, gm.name, gm.desc)] = ms[0].name
                cand_body[(cn, gm.name, gm.desc)] = (gm, ms[0], wcn)
    # consistency: masked references must follow the pairs (repeat until stable)
    while True:
        drop = []
        for key, (gm, nm, wcn) in cand_body.items():
            if key not in pairs:
                continue
            ok = True
            for va, wa in zip(walk_refs(gm.insns), walk_refs(nm.insns)):
                vo, vn, vd = ref_parts(va)
                if va[0] == "mhandle" or va[1] != "F":
                    r = rv(vo, vn, vd)
                    if r is not None and r in vchanged:
                        wo, wn, wd = ref_parts(wa)
                        wr = rw(wo, wn, wd)
                        if r not in pairs or wr is None or wr[0] != wall.cname(r[0]) or wr[1] != pairs[r]:
                            ok = False
                            break
            if not ok:
                drop.append(key)
        if not drop:
            break
        for k in drop:
            del pairs[k]

    def wsupers(c):
        seen, todo = [], [c]
        while todo:
            x = todo.pop()
            if x in seen or x not in W:
                continue
            seen.append(x)
            if W[x].super:
                todo.append(W[x].super)
            todo.extend(W[x].interfaces)
        return seen
    # abstract pairs, proven by a concrete pair a W implementor declares or inherits
    wpairs = {(wall.cname(c), wall.member(c, n), wall.desc(d)): nn for (c, n, d), nn in pairs.items()}
    subs = {}
    for x in W:
        for s in wsupers(x):
            if s != x:
                subs.setdefault(s, []).append(x)
    for cn in gone:
        wcn = wall.cname(cn)
        for gm in gone[cn]:
            if gm.insns is not None:
                continue
            st = gm.access & static
            ga = [m for m in gone[cn] if m.desc == gm.desc and m.insns is None and (m.access & static) == st]
            na = [m for m in new[cn] if m.desc == wall.desc(gm.desc) and m.insns is None and (m.access & static) == st]
            if len(ga) != 1 or len(na) != 1:
                continue
            wkey = (wall.member(cn, gm.name), wall.desc(gm.desc))
            if any(wpairs.get((c,) + wkey) == na[0].name for x in subs.get(wcn, []) for c in wsupers(x)):
                pairs[(cn, gm.name, gm.desc)] = na[0].name
    # review lists
    review = {"weak": [], "narrowed": [], "links": []}
    for key in sorted(pairs):
        cn, n, d = key
        gm = next(m for m in V[cn].methods if m.name == n and m.desc == d)
        wk = W[wall.cname(cn)]
        nm = next(m for m in wk.methods if m.name == pairs[key] and m.desc == wall.desc(d))
        if rw(wk.name, wall.member(cn, n), wall.desc(d)) is not None:
            review["links"].append(key)
        elif rank(nm.access) < rank(gm.access):
            review["narrowed"].append(key)
        elif gm.insns is not None and weak(gm.insns):
            review["weak"].append(key)
    for lst in review.values():
        for k in lst:
            pairs.pop(k, None)
    return pairs, review, wsupers


def wired_text(frag_dir):
    text = ""
    if os.path.isdir(frag_dir):
        for f in sorted(os.listdir(frag_dir)):
            if f.endswith(".json") and f != OUT_NAME and f not in SUPERSEDED:
                with open(os.path.join(frag_dir, f), encoding="utf-8") as fh:
                    text += fh.read()
    main = frag_dir[:-2] if frag_dir.endswith(".d") else None
    if main and os.path.exists(main):
        with open(main, encoding="utf-8") as fh:
            text += fh.read()
    return text


def step(v, w, V, W, write=False, quiet=False):
    is_wall = (v, w) == WALL
    wall = Wall(wall_tables.table_for(w, DATA)) if is_wall else Ident()
    pairs, review, wsupers = find(V, W, wall)
    frag_dir = os.path.join(ROOT, "core", "segments", "steps", f"{v}__{w}.json.d")
    wired = wired_text(frag_dir)
    elsewhere = [k for k in pairs if k[1] + k[2] in wired]
    for k in elsewhere:
        del pairs[k]
    if not quiet:
        for (cn, n, d), nn in sorted(pairs.items()):
            print(f"{cn}.{n}{d} -> {nn}")
        for what, lst in (("weak", review["weak"]), ("narrowed", review["narrowed"]),
                          ("still links", review["links"]), ("wired elsewhere", elsewhere)):
            for cn, n, d in lst:
                print(f"  ({what}) {cn}.{n}{d}", file=sys.stderr)
    synthetic = sum(1 for (cn, n, d) in pairs
                    if next(x for x in V[cn].methods if x.name == n and x.desc == d).access & bytecode.ACC_SYNTHETIC)
    counts = (len(pairs), synthetic, len(review["weak"]), len(review["narrowed"]), len(review["links"]), len(elsewhere))
    if write:
        if is_wall:
            raise SystemExit("the wall step's pairs are wall-table gaps (wall_table.py), never wiring")
        if v == RELEASES[0]:
            raise SystemExit("step 1 wires through shims-1.20.2.json, not a step fragment directory")
        guard = "top/dext/centurybridge/step/v" + w.replace(".", "_") + "/rt/Guard"
        renames = {f"{cn}.{n}{d}": nn for (cn, n, d), nn in sorted(pairs.items())}
        hooks = {}
        for (cn, n, d), nn in sorted(pairs.items()):
            m = next(x for x in V[cn].methods if x.name == n and x.desc == d)
            if m.access & (bytecode.ACC_STATIC | bytecode.ACC_PRIVATE | bytecode.ACC_FINAL | bytecode.ACC_SYNTHETIC):
                continue
            if V[cn].access & bytecode.ACC_FINAL:
                continue
            hooks.setdefault((n, d, nn), []).append(cn)
        abstract = {cn for (cn, n, d) in pairs if next(x for x in V[cn].methods if x.name == n and x.desc == d).insns is None}
        oh = []
        for (n, d, nn), owners in sorted(hooks.items()):
            tops = [c for c in owners if not any(o != c and o in wsupers(c) for o in owners)]
            for top in sorted(tops):
                oh.append({
                    "old": n + d, "new": nn + d, "guard": guard, "onlyUnder": top,
                    "basis": f"renumbered: {w} renamed {top}.{n} to {nn}"
                    + (" (same descriptor, abstract: implemented by the renamed method of a W implementor"
                       if top in abstract else " (same descriptor, same body")
                    + "; gen_renumbered.py); vanilla calls the new name, the V override answers it, its super call"
                    " falls through to vanilla's",
                })
        out = os.path.join(frag_dir, OUT_NAME)
        with open(out, "w", encoding="utf-8", newline="\n") as f:
            json.dump({"methodRenames": renames, "overrideHooks": oh}, f, indent=1)
            f.write("\n")
        print("wrote " + os.path.relpath(out, ROOT), file=sys.stderr)
    return counts


def main():
    args = [a for a in sys.argv[1:] if not a.startswith("--")]
    if "--all" in sys.argv:
        print("step\tpairs\tof_them_synthetic\tweak\tnarrowed\tstill_links\twired_elsewhere")
        V = load(jar_of(RELEASES[0]))
        for i in range(1, len(RELEASES)):
            v, w = RELEASES[i - 1], RELEASES[i]
            W = load(jar_of(w))
            c = step(v, w, V, W, quiet=True)
            print(f"{v}__{w}\t" + "\t".join(map(str, c)) + ("\t(wall: table gaps)" if (v, w) == WALL else ""),
                  flush=True)
            V = W
        return
    if len(args) != 2:
        raise SystemExit(__doc__)
    v, w = args
    c = step(v, w, load(jar_of(v)), load(jar_of(w)), write="--write" in sys.argv)
    print(f"{v}__{w}: {c[0]} pairs ({c[1]} synthetic); review: {c[2]} weak, {c[3]} narrowed, {c[4]} still link, "
          f"{c[5]} wired elsewhere",
          file=sys.stderr)


if __name__ == "__main__":
    main()
