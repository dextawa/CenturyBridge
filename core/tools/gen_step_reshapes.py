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
constructor). Arguments of one type are never paired by their order: types alone cannot tell them
apart (1.20.5's PanoramaRenderer.render(graphics, width, height, fade, partialTick) is 1.20.4's
render(delta, alpha) with the two floats the other way round). Each is paired by what V's and W's
bodies (the member's declaration and every override) and vanilla callers do with it -- which calls it
reaches at which argument position, which fields it is stored to or read from, what arithmetic and
constants it meets (bytecode_uses.py; the rule is Evidence.pair). A pairing the uses do not single out
is "design" with the reason, never a guess. Erased descriptors
are not enough either: where both releases' generic signatures are known and a connected parameter
or the return value changed its type arguments (a List of 1.20.1 goal records that is a List of
1.20.2 payload records now), the value would pass the verifier and fail at the first element. Everything else -- a value the new shape
needs that the old call does not have, a narrowing, a dropped argument V used, a converted
return -- is listed as "design" with the reason. Members the step's hand-written wiring already
handles are skipped ("wired"). The output is a wiring fragment for the step
(<step wiring>.d/mechanical.json, see engine/Wiring.java) and a report of every row.

  python gen_step_reshapes.py <damage.tsv> <V code jar> <W stub jar> <step wiring.json> <guard class> <out report.tsv>
         [--wcode <W code jar>]  (W's bodies, for pairing same-typed arguments: data/jars/client-<W>-intermediary-code.jar;
                                  a 26.x W jar carries its code and needs none)
         [--wall <wall table>]   (the 1.21.11 -> 26.1 step: V intermediary, W mojang; the table the
                                  step's target reads names with: python3 core/tools/wall_tables.py 26.1)
  every step at once: core/tools/regen_mechanical.sh
"""
import collections
import itertools
import json
import os
import re
import sys
import zipfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import classfile  # noqa: E402
import bytecode_uses  # noqa: E402
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


def match(src, dst, h, resolve=None):
    """Map every dst argument to the src argument that supplies it.

    Returns (mapping, problem): mapping[j] is the src index feeding dst[j]. Exact types must occur as
    often on both sides; a type that occurs once pairs up directly, two or more of one type are paired
    by resolve(type, src indices, dst indices) -> (src index for each dst index, None) or (None,
    reason) -- by what the bodies and callers do with each of them, never by their order alone. A dst
    type absent from src takes the one unused src argument of a subtype. problem is a reason string
    when no such mapping exists."""
    if resolve is not None:
        # the types first: a shape that cannot take the call at all is not one whose pairing is undecided
        _, problem = _match(src, dst, h, lambda t, s, d: (list(s), None))
        if problem:
            return None, problem
    return _match(src, dst, h, resolve)


def _match(src, dst, h, resolve):
    """match() itself; resolve as there (None: same-typed arguments are a problem)"""
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
            srcs = src_pos[t]
            if len(js) > 1:
                if resolve is None:
                    return None, f"{t} occurs {len(js)}x: which one is which is not decidable by type"
                srcs, why = resolve(t, src_pos[t], js)
                if srcs is None:
                    return None, why
            for i, j in zip(srcs, js):
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


# a type variable where a type signature starts (TFoo;) -- compared by position, not by name
TYPEVAR = re.compile(r"(?:^|(?<=[<;(\[)*+\-:]))T[^;<>/]+;")


def v_signature(V, hv, owner, name, desc):
    """the generic signature of the member as V declares it on the owner or a supertype"""
    for c in [owner] if name == "<init>" else hv.supers(owner):
        k = V.get(c)
        m = next((m for m in k.methods if m.name == name and m.desc == desc), None) if k else None
        if m:
            return m.signature
    return None


def generic_change(v_sig, w_sig, pairs, compare_return, wall):
    """Where the erased descriptors agree but the generic types do not -- a List<A> that is a List<B>
    now -- the value passes the verifier and fails later with a ClassCastException. pairs: (V parameter,
    W parameter) the bridge would connect. A reason string, or None when nothing generic changed (or
    either side has no signature to compare)."""
    if not v_sig or not w_sig:
        return None
    vs, ws = classfile.split_signature(v_sig), classfile.split_signature(w_sig)
    if not vs or not ws:
        return None

    def norm(s, translate):
        s = TYPEVAR.sub("T*;", s)
        return re.sub(r"L([^;<>]+)", lambda m: "L" + wall.cls(m.group(1)), s) if translate else s

    def type_args(s):
        """the type-argument part of a type signature, '' for a raw or non-generic type: what has to
        agree (the erased types are the matcher's business, a subtype may stand for its supertype)"""
        k = s.find("<")
        return s[k:s.rfind(">") + 1] if k >= 0 else ""

    checks = [(vs[0][i], ws[0][j]) for i, j in pairs if i < len(vs[0]) and j < len(ws[0])]
    if compare_return:
        checks.append((vs[1], ws[1]))
    for a, b in checks:
        ta, tb = type_args(norm(a, True)), type_args(norm(b, False))
        # a raw type on one side (a class generified in W) carries no element type to disagree with
        if ta and tb and ta != tb:
            return f"generic type {a} -> {b}"
        # a bare type variable on exactly one side: the erasure (Object, or the bound) makes the other
        # side "assignable", but the value is another thing altogether -- 1.21's PlaceRecipe.addItemToSlot
        # takes the element T where 1.20.6's took the Iterator<T> it came from
        if (a.lstrip("[").startswith("T")) != (b.lstrip("[").startswith("T")):
            return f"generic type {a} -> {b}"
    return None


class Evidence:
    """What V's and W's bodies and vanilla callers do with each parameter of a member (bytecode_uses.py),
    and the pairing of same-typed arguments it justifies.

    For one type occurring k times on both sides, every pairing of V's k parameters with W's k is scored
    by how alike each pair is used: the Jaccard overlap of their use features in the member's bodies (V's
    declaration and overrides, W's), plus that of what vanilla callers pass at the two positions. The
    pairing is taken only when it scores best on its own and every pair in it is the best match of both
    its parameters (a pair sharing no use is then the one left once the others are placed by theirs).
    Anything else is undecidable and becomes "design"."""

    MAX_GROUP = 8

    def __init__(self, V, hv, Wc, hwc, wall):
        self.V, self.hv, self.Wc, self.hwc, self.wall = V, hv, Wc, hwc, wall
        self._calls = {}
        self._vrename = (lambda o, n: wall.member(o, n, hv)) if wall.mmap else None
        self._vcrename = wall.cls if wall.cmap else None
        self._cmap = {}

    def _callers(self, side):
        if side not in self._calls:
            if side == "V":
                self._calls[side] = bytecode_uses.call_sites(self.V, self._vrename, self._vcrename)
            else:
                self._calls[side] = bytecode_uses.call_sites(self.Wc, None)
        return self._calls[side]

    def _stable(self):
        """members V and W declare with one descriptor (V's translated across the wall)"""
        if not hasattr(self, "_stable_set"):
            w = {(m.name, m.desc) for k in self.Wc.values() for m in k.methods}
            v = set()
            for c, k in self.V.items():
                for m in k.methods:
                    nm = self.wall.member(c, m.name, self.hv) if self.wall.mmap else m.name
                    v.add((nm, self.wall.desc(m.desc)))
            self._stable_set = v & w
        return self._stable_set

    def _callee_map(self, owner, name, desc):
        """A call in a V body to a member the release reshaped too puts the value at the callee's new
        position: V's argument index -> (W's descriptor, W's index), from the callee's own pairing -- an
        argument of a type the callee has once on both sides directly, several of one type by use one level
        deep (their bodies' calls to stable members and their callers). None when the callee has no single
        W shape of the name, or nothing is placed."""
        key = (owner, name, desc)
        if key not in self._cmap:
            self._cmap[key] = None
            wowner = self.wall.cls(owner)
            wname = self._vname(owner, name)
            oa = split_args(self.wall.desc(desc))[0]
            found = []
            for c in self.hwc.supers(wowner):
                k = self.Wc.get(c)
                for m in (k.methods if k else []):
                    if m.name != wname or m.access & (ACC_BRIDGE | ACC_SYNTHETIC):
                        continue
                    na = split_args(m.desc)[0]
                    vpos, wpos = collections.defaultdict(list), collections.defaultdict(list)
                    for i, t in enumerate(oa):
                        vpos[t].append(i)
                    for j, t in enumerate(na):
                        wpos[t].append(j)
                    # only the callee's arguments of a type it has as often on both sides are placed (the
                    # rest are what the callee's own change is about); several of one type by use
                    cmap, ev_pair = {}, None
                    for t, vs in vpos.items():
                        ws = wpos.get(t, [])
                        if len(vs) != len(ws):
                            continue
                        if len(vs) == 1:
                            cmap[vs[0]] = ws[0]
                            continue
                        if ev_pair is None:
                            ev_pair = (self.side("V", owner, wname, desc, depth=1, vname=name),
                                       self.side("W", c, m.name, m.desc, depth=1))
                        perm, _ = self.pair(t, vs, ws, ev_pair[0], ev_pair[1], lambda i: f"#{i}")
                        if perm is not None:
                            cmap.update(zip(vs, perm))
                    if cmap:
                        found.append((m.desc, cmap))
            if len(found) == 1:
                self._cmap[key] = found[0]
        return self._cmap[key]

    def _vname(self, owner, name):
        return self.wall.member(owner, name, self.hv) if self.wall.mmap else name

    def _keep(self, feats, vside, depth):
        """use features as both sides compare them: a call position as ordinals (from the start and from
        the end, among arguments of its type) in W's terms -- a V call to a stable member as it is, to a
        reshaped one through the callee's own pairing (depth 0 only), dropped when that is undecided"""
        crename = self.wall.cls if vside and self.wall.cmap else None
        out = set()
        for f in feats:
            if f[0] not in ("call", "indy") or len(f) != 5:
                out.add(f)
                continue
            kind, name, k, desc, owner = f
            if kind == "indy" or not vside:
                args = split_args(desc)[0]
                nm = self._vname(owner, name) if vside and owner and kind == "call" else name
                out.add((kind, nm, bytecode_uses.ordinal(args, k, crename)))
                out.add((kind, nm, bytecode_uses.ordinal(args, k, crename, True)))
                continue
            wname = self._vname(owner, name) if owner else name
            d = self.wall.desc(desc)
            if (wname, d) in self._stable():
                args = split_args(d)[0]
                j = k
            elif depth == 0 and owner:
                cm = self._callee_map(owner, name, desc)
                if cm is None or k not in cm[1]:
                    continue
                args, j = split_args(cm[0])[0], cm[1][k]
            else:
                continue
            out.add((kind, wname, bytecode_uses.ordinal(args, j)))
            out.add((kind, wname, bytecode_uses.ordinal(args, j, None, True)))
        return out

    def side(self, side, owner, name, desc, depth=0, vname=None):
        """(per-parameter body features or None, per-position caller features or None). For V, name is
        the member's W name across the wall (features are compared in W's terms) and vname its own."""
        classes, h = (self.V, self.hv) if side == "V" else (self.Wc, self.hwc)
        rename, crename = (self._vrename, self._vcrename) if side == "V" else (None, None)
        lookup = vname or name if side == "V" else name
        n = len(split_args(desc)[0])
        body = [set() for _ in range(n)]
        seen = False
        for c, m in implementations(classes, h, owner, lookup, desc):
            u = bytecode_uses.uses(classes[c], m, rename, crename)
            if u is not None:
                seen = True
                for k in range(n):
                    body[k] |= self._keep(u[k], side == "V", depth)
        calls = [set() for _ in range(n)]
        cseen = False
        for o, per in self._callers(side).get((lookup, desc), {}).items():
            if o == owner or o in h.supers(owner) or owner in h.supers(o):
                cseen = True
                for k in range(min(n, len(per))):
                    calls[k] |= per[k]
        return (body if seen else None), (calls if cseen else None)

    @staticmethod
    def _jac(a, b):
        return len(a & b) / len(a | b) if a and b else 0.0

    def pair(self, t, vs, ws, v_ev, w_ev, label):
        """V parameter indices vs, W parameter indices ws (all of type t): a W index for each V index in
        order, or (None, reason). label(i) names a V parameter for the reason."""
        k = len(vs)
        if k > self.MAX_GROUP:
            return None, f"{k} arguments of type {t}: too many to pair by use"
        (vb, vc), (wb, wc) = v_ev, w_ev

        def feats(b, c, i):
            return (b[i] if b else set()), (c[i] if c else set())

        sim = {}
        for a in vs:
            for b in ws:
                (b1, c1), (b2, c2) = feats(vb, vc, a), feats(wb, wc, b)
                sim[a, b] = self._jac(b1, b2) + self._jac(c1, c2)
        scored = [(sum(sim[a, b] for a, b in zip(vs, perm)), perm) for perm in itertools.permutations(ws)]
        best = max(sc for sc, _ in scored)
        if best <= 0:
            vside, wside = bool(vb or vc), bool(wb or wc)
            if vside and wside:
                return None, f"{k} arguments of type {t}: V's and W's bodies and callers share no use of them"
            missing = "either side" if not vside and not wside else ("V's side" if not vside else "W's side")
            return None, f"{k} arguments of type {t}: no body or caller on {missing} to tell them apart"
        tops = [perm for sc, perm in scored if abs(sc - best) < 1e-9]
        if len(tops) != 1:
            return None, f"{k} arguments of type {t}: their uses fit {len(tops)} pairings equally"
        perm = tops[0]
        for a, b in zip(vs, perm):
            # every pair is the best match of both its parameters; a pair sharing no use is taken only
            # when no use ties either of them to anything else -- the one left once the others are
            # placed by their uses (the unique best pairing guarantees there is just one such way)
            if any(sim[a, b2] > sim[a, b] for b2 in ws) or any(sim[a2, b] > sim[a, b] for a2 in vs):
                return None, f"argument {label(a)} ({t}): its best match by use is not the pairing's"
        return list(perm), None


# marks a shape whose types fit but whose same-typed arguments the uses do not pair: it still counts as a
# fitting shape (two such shapes are as ambiguous as before), it is just not one the generator can emit
UNDECIDED = "[by use] "


def typed_fits(fits):
    """the shapes whose types fit: emittable ones and those only the pairing by use left undecided"""
    return [f for f in fits if f[2] is not None or (f[3] and UNDECIDED in f[3])]


def resolver(ev, owner, name, desc, wowner, wm, v_is_src, notes):
    """match()'s resolve for one V member against one W shape: same-typed arguments paired by use
    (Evidence.pair). v_is_src: the V parameters are match's src (a call; else an override's dst).
    Every pairing taken goes to notes, for the entry's basis."""
    cache = {}

    def evidence():
        if not cache:
            cache["v"] = ev.side("V", owner, name, desc)
            cache["w"] = ev.side("W", wowner, wm.name, wm.desc)
        return cache["v"], cache["w"]

    def resolve(t, src, dst):
        vs, ws = (src, dst) if v_is_src else (dst, src)
        v_ev, w_ev = evidence()
        perm, why = ev.pair(t, vs, ws, v_ev, w_ev, lambda i: f"#{i}")
        if perm is None:
            return None, UNDECIDED + why
        pairs = dict(zip(vs, perm))  # V index -> W index
        notes.append(f"{t} x{len(vs)} paired by use " + ", ".join(f"V#{a}->W#{pairs[a]}" for a in vs)
                     + ("" if list(perm) == sorted(perm) else " (not their order)"))
        if v_is_src:
            back = {w: v for v, w in pairs.items()}
            return [back[j] for j in dst], None
        return [pairs[j] for j in dst], None

    return resolve


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
    # an adapter hook reconnects vanilla's new shape to the mod's old methods only through its synth
    # methods; one with no synth just types the mod class with the adapter interface (calls reach the
    # old shapes through it), so its requires are not served overrides
    for ah in wiring.get("adapterHooks", []):
        if ah.get("synth"):
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
    wcode = None
    if "--wcode" in args:
        k = args.index("--wcode")
        wcode = args[k + 1]
        args = args[:k] + args[k + 2:]
    damage, vjar, wjar, wiring_path, guard, out_report = args[0:6]
    V = load(vjar, True)
    W = load(wjar, wcode is None)
    hv, hw = Hierarchy(V), Hierarchy(W)
    # W's bodies: its code jar (an intermediary stub has none), else the jar itself (26.x jars carry code)
    Wc = load(wcode, True) if wcode else W
    hwc = Hierarchy(Wc) if wcode else hw
    ev = Evidence(V, hv, Wc, hwc, wall)
    base = json.load(open(wiring_path, encoding="utf-8")) if os.path.exists(wiring_path) else {}
    # what the step's hand-written wiring serves: the base table and every area fragment (not the
    # generated one), so an area that bridged a member -- or renamed its class -- keeps it
    merged = json.loads(json.dumps(base))
    frag_dir = wiring_path + ".d"
    for f in sorted(os.listdir(frag_dir)) if os.path.isdir(frag_dir) else []:
        if f.endswith(".json") and f != "mechanical.json":
            for k, v in json.load(open(os.path.join(frag_dir, f), encoding="utf-8")).items():
                if isinstance(v, dict):
                    merged.setdefault(k, {}).update(v)
                elif isinstance(v, list):
                    merged.setdefault(k, []).extend(v)
    wired_calls, wired_ctors, wired_shapes = wired_keys(merged)
    renamed = set(merged.get("classRenames", {})) | set(merged.get("superRenames", {}))
    ledgered = set(base.get("ledger", {}).keys())
    # rows an area has shown the generator cannot judge (owner<TAB>name+desc<TAB>reason): an
    # intermediary name Fabric's matcher moved to another class (1.20.2 class_7566 is not 1.20.1's)
    excluded = {}
    excl = os.path.join(frag_dir, "mechanical-exclude.tsv")
    if os.path.exists(excl):
        for line in open(excl, encoding="utf-8"):
            f = line.rstrip("\n").split("\t")
            if len(f) >= 3 and not line.startswith("#"):
                excluded[(f[0], f[1])] = f[2]
    calls, ctors, hooks, report = {}, {}, [], []
    fit_notes = {}
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
        if owner in renamed or (owner, name + desc) in excluded:
            note(r, "excluded", excluded.get((owner, name + desc), "the owner class is renamed by the step's wiring"))
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
        # across the wall: W declares the member under the translated name with the translated descriptor
        # -- the whole-jar rename links it (a member the table renames: GuiGraphics.drawString is
        # GuiGraphicsExtractor.text, same code); a reshape to the same shape would do nothing
        tdesc = "(" + "".join(oa) + ")" + orr
        if wall.cmap and any(m.desc == tdesc for _, m in shapes_w):
            note(r, "renamed", f"the wall table's rename links it as {wowner}.{wname}{tdesc}")
            continue
        if override:
            if name == "<init>":
                continue
            fits = []
            for c, m in shapes_w:
                if m.access & (ACC_FINAL | ACC_STATIC):
                    continue
                na, nr = split_args(m.desc)
                notes = []
                amap, problem = match(na, oa, hw, resolver(ev, owner, name, desc, c, m, False, notes))  # each OLD parameter from a NEW argument
                if problem:
                    fits.append((c, m, None, f"old method needs: {problem}"))
                    continue
                # vanilla used what the old method returned; a new shape that returns nothing gets its
                # result some other way, so the old method's answer would be lost
                if not (nr == orr or hw.assignable(orr, nr)):
                    fits.append((c, m, None, f"returns {orr}, the new shape {nr}"))
                    continue
                gen = generic_change(v_signature(V, hv, owner, name, desc), m.signature,
                                     [(i, amap[i]) for i in range(len(oa))], True, wall)
                if gen:
                    fits.append((c, m, None, gen))
                    continue
                fits.append((c, m, amap, None))
                fit_notes[id(m)] = notes
            good = [f for f in fits if f[2] is not None]
            typed = typed_fits(fits)
            if len(good) != 1 or len(typed) != 1:
                note(r, "design", "; ".join(f"{m.desc}: {p}" for _, m, _, p in fits) if not typed
                     else f"{len(typed)} new shapes fit" if len(typed) > 1
                     else f"{typed[0][1].desc}: {typed[0][3]}")
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
                                   + "".join(f"; {x}" for x in fit_notes.get(id(m), []))
                                   + ("" if amap == sorted(amap) else ", reordered")
                                   + (f"; new {','.join(unpassed)} not passed" if unpassed else "")})
            note(r, "hook", f"-> {nd} args {amap}")
            continue
        # a call: the new shape's arguments from the old call's
        fits = []
        for c, m in shapes_w:
            if name != "<init>" and bool(m.access & ACC_STATIC) != (usage in ("call-static", "call-static-protected", "call-static-inherited")):
                fits.append((c, m, None, "static-ness changed"))
                continue
            na, nr = split_args(m.desc)
            notes = []
            amap, problem = match(oa, na, hw, resolver(ev, owner, name, desc, c, m, True, notes))
            if problem:
                fits.append((c, m, None, problem))
                continue
            if not (nr == orr or orr == "V" or hw.assignable(nr, orr)):
                fits.append((c, m, None, f"returns {nr}, the old call wants {orr}"))
                continue
            # the return is compared whenever its value passes (not only when the erased types agree: a
            # bare T -- erased to Object -- that is an Optional<Holder<T>> now is "assignable" and wrong)
            gen = generic_change(v_signature(V, hv, owner, name, desc), m.signature,
                                 [(amap[j], j) for j in range(len(na))], orr != "V", wall)
            if gen:
                fits.append((c, m, None, gen))
                continue
            fits.append((c, m, amap, None))
            fit_notes[id(m)] = notes
        good = [f for f in fits if f[2] is not None]
        typed = typed_fits(fits)
        if len(good) != 1 or len(typed) != 1:
            note(r, "design", "; ".join(f"{m.desc}: {p}" for _, m, _, p in fits) if not typed
                 else f"{len(typed)} new shapes fit" if len(typed) > 1
                 else f"{typed[0][1].desc}: {typed[0][3]}")
            continue
        c, m, amap, _ = good[0]
        na, nr = split_args(m.desc)
        dropped = [i for i in range(len(oa)) if i not in amap]
        abstract = None
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
            # an abstract declaration (an interface method, an abstract class's) is implemented outside
            # V too: V's own bodies say the call may drop the argument for vanilla's implementations, but
            # a mod's implementation is reached through its override hook, which has no value for it (a
            # mod's SimpleCraftingRecipeSerializer.Factory stored the recipe id it was handed). The
            # basis says so, so whoever owns the area can judge whether the value must travel
            abstract = next((bc for bc, bm in bodies if bm.code is None), None)
            if readers:
                note(r, "design", f"drops {[oa[i] for i in dropped]}, which V reads in {readers[:3]}")
                continue
        ops = [f"keep:{i}" + ("" if oa[i] == na[j] else f" {wall.back_desc(na[j])}") for j, i in enumerate(amap)]
        ops += [f"drop:{i}" for i in dropped]
        nd = wall.back_desc(m.desc)
        entry = {"desc": nd, "ops": ops}
        if nr != orr:
            entry["ret"] = "pop" if orr == "V" else "keep"
        entry["basis"] = "mechanical: same values" + ("" if amap == sorted(amap) else ", reordered")             + "".join(f"; {x}" for x in fit_notes.get(id(m), []))             + (f"; dropped {','.join(oa[i] for i in dropped)}, never read in V" if dropped and not abstract else "")             + (f"; dropped {','.join(oa[i] for i in dropped)}: no body of V reads it, but {abstract} declares the method"
               " abstract -- an implementation outside V (a mod's) gets its override hook's value instead"
               if dropped and abstract else "") \
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
