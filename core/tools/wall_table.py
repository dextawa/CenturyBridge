"""The namespace wall's rename table, aimed at one unobfuscated release.

The table the converter renames a 1.21.11 mod with (intermediary -> mojang, C/M/F rows) has to spell
every class and member the way the TARGET release declares it. compose_mojang.py gives 1.21.11's own
mojang names; Mojang then moved and renamed some of them by the time the target shipped (26.1:
GuiGraphics is GuiGraphicsExtractor, hLine is horizontalLine, MaterialSet is sprite/SpriteGetter; 26.2
moved advancements/criterion/* to predicates/* and triggers/*). A row naming something the target lacks
sends every converted reference to a class that is not there, and the step's damage table records a
rename as "class gone".

This decides, from bytecode only, where each 1.21.11 class and member lives in the target:

  class    the 1.21.11 name when the target declares a class there that is the same class (member
           overlap; a different class that took the name is a collision, reported), else the one
           target class not claimed by any other 1.21.11 class that answers its members -- scored by
           its members' names and descriptors, and by bodies that are the same code (each instruction
           equal once names are translated). Never by simple name alone (26.1's sprite/SpriteGetter is
           1.21.11's MaterialSet, not its SpriteGetter).
  member   the 1.21.11 name when the target class (or a supertype) has it and its body is the same code,
           or no other member of the class is; else a target member of the
           same class whose body is the same code as 1.21.11's (one candidate, not trivial), or which
           the implementations of the same method family (an override elsewhere) or the bodies that
           use it name consistently. A member with no such evidence keeps its 1.21.11 name: it is
           damage of the step, to be bridged, not guessed.
           Names the target reused for other code are collisions: 26.1's RenderTypes.entityCutout is
           1.21.11's entityCutoutNoCull, 1.21.11's culling entityCutout is entityCutoutCull -- keeping the
           name links and renders the other type. When the same-named member is not the same code and
           exactly one other member of the class is (two references or more matched as they are, not
           already the same code as the V member of its own name), the member maps there; more than one
           is listed (member-collision-ambiguous), never guessed. Every collision is in the report.
           A static constant whose name is gone maps to the one static field of the same erased type
           and generic signature naming the class (26.1's loot entries and functions: MapCodec<LootItem>
           CODEC -> MAP_CODEC), when it is the one such constant on both sides; or to the one target
           constant no V field answers by name that is computed by the very same initializer, string
           constants included, the pair unique both ways (EnderDragonRenderer.DRAGON_LOCATION ->
           DRAGON_TEXTURE_LOCATION, Identifier "textures/entity/enderdragon/dragon.png" in both).
           Fields are also found by their uses: a body that kept its name (and descriptor) names them
           -- when it is the same code with one reference matched as it is (the member around it is
           decided by its name: UniformFloat.sample reads min and max around Mth.randomBetween), and
           when it is not, where a stretch of it is the same code around an instance field's use:
           growing outward from the use, the stretch between its first and last anchor holds two
           references or constants matched as they are, one of them a constant or a game member, and
           reads that way only once in each body (LecternRenderer's constructor stores
           Context.sprites() into sprites where 1.21.11 stored it into materials; 26.1's
           CustomFeatureRenderer$Storage clears a solid and a translucent map alike, so 1.21.11's one
           map is neither). These field votes need the same generic signature on both sides; a target
           field two V fields of one class would take goes to neither (EnderDragonRenderer RENDER_TYPE
           and DECAL vs one DYING_RENDER_TYPE).

Matching is a fixed point: a class found moved makes descriptors comparable, which finds members, whose
bodies vote for more classes. Undecided candidates are listed in the report, never emitted.

  python3 wall_table.py <base.tsv> <V code jar> <W jar> <out.tsv> <report.tsv> [--extra <rows.tsv>]
      base.tsv   compose_mojang.py's table (intermediary -> 1.21.11 mojang names)
      --extra    hand-verified rows for tokens older than 1.21.11 (core/tools/pkgmoves-blaze3d.tsv):
                 their targets are translated the same way; a row for a key the base table has is
                 dropped and reported (the base row decides)
  python3 wall_table.py --identity <V jar> <W jar> <out.tsv> <report.tsv>
      two mojang-named releases (26.1.2 -> 26.2): only moved/renamed classes and renamed members are
      written, as C/M/F rows V name -> W name

  python3 wall_table.py --identity <V jar> <W jar> <out.tsv> <report.tsv> --fragment <fragment.json> <step>
      also writes the step's classRenames (pkgmoves-gen.json), each class checked member by member

Writes nothing but the files named.

Regenerating the climb's wall (from the repo root; java nowhere):
  1. python3 core/tools/compose_mojang.py 1.21.11 data/jars/client-26.1.jar.dump.txt <tmp>/table-base.tsv
  2. python3 core/tools/wall_table.py <tmp>/table-base.tsv data/jars/client-1.21.11-intermediary-code.jar \
        data/jars/client-26.1.jar <tmp>/inter-to-mojang-1.21.11-26.1.tsv <tmp>/report.tsv \
        --extra core/tools/pkgmoves-blaze3d.tsv
     -> deploy as data/mappings/inter-to-mojang-1.21.11-26.1.tsv (core/segments/wall-tables.tsv names it)
  3. the wall segment, measured with that table (core/segments/1.21.11__26.1.json):
        python3 core/tools/jar_dump.py data/jars/client-26.1.jar <tmp>/dump-26.1.txt
        python3 core/tools/gen_frontier_segment.py <table> <tmp>/dump-26.1.txt core/segments/1.21.11__26.1.json
        python3 core/tools/wall_desc_diff.py <table> data/jars/client-1.21.11-intermediary.jar data/jars/client-26.1.jar <tmp>/diff.tsv
        python3 core/tools/augment_frontier_segment.py <table> <tmp>/diff.tsv core/segments/1.21.11__26.1.json
  4. the 26.1.2 -> 26.2 step's moves:
        python3 core/tools/wall_table.py --identity data/jars/client-26.1.2.jar data/jars/client-26.2.jar \
            <tmp>/id.tsv <tmp>/id-report.tsv --fragment core/segments/steps/26.1.2__26.2.json.d/pkgmoves-gen.json 26.1.2__26.2
  5. the damage tables (probe_segments.sh / fapi_probe_segments.sh 1.21.11 on beta2) and mechanical.json
     (gen_step_reshapes.py --wall <table>).
"""
import collections
import difflib
import json
import os
import re
import sys
import zipfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import bytecode  # noqa: E402

TOKEN = re.compile(r"^(method|field|comp)_\d+$")
# members every class of a kind declares (enums, records, codecs): no evidence of which class it is
GENERIC = {"<init>", "<clinit>", "values", "valueOf", "$values", "$VALUES", "equals", "hashCode", "toString",
           "CODEC", "STREAM_CODEC", "MAP_CODEC", "LOGGER", "INSTANCE", "TYPE", "ID"}
DESC_CLS = re.compile(r"L([^;]+);")
SIG_CLS = re.compile(r"L([^;<]+)[;<]")
REFOPS = {0xb2, 0xb3, 0xb4, 0xb5, 0xb6, 0xb7, 0xb8, 0xb9}
ANON = re.compile(r"\$\d+$")


def load(jar):
    out = {}
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if n.endswith(".class") and n.startswith(("net/minecraft/", "com/mojang/")):
                k = bytecode.read(z.read(n))
                if k:
                    out[k.name] = k
    return out


class Wild:
    """a V class no target class is known for yet: matches any target class, and says so"""
    __slots__ = ("v",)

    def __init__(self, v):
        self.v = v

    def __eq__(self, o):
        return isinstance(o, Wild) and o.v == self.v

    def __hash__(self):
        return hash(("wild", self.v))


class Table:
    def __init__(self, V, W, base_c, base_m, identity):
        self.V, self.W = V, W
        self.base_c = base_c          # V class -> its name before the target (1.21.11 mojang)
        self.base_m = base_m          # "Vowner.vname" -> mojang member name
        self.bare = {}                # vname -> mojang name (unambiguous ids only), as JarRemapper
        amb = set()
        for k, v in base_m.items():
            n = k[k.rindex(".") + 1:]
            if self.bare.setdefault(n, v) != v:
                amb.add(n)
        for n in amb:
            del self.bare[n]
        self.identity = identity
        self.cm = {}                  # V class -> W class (decided)
        self.claimed = {}             # W class -> V class
        self.ren = {}                 # V member family name (token, or "owner.name" in identity mode) -> W name
        self.ren_basis = {}
        self.collisions = {}
        self.reuse = {}               # (V class, V method) -> (kind, W name, W member it moved to, why)
        self.reuse_amb = {}           # (V class, V method) -> (kind, W name, candidates, why)
        self.segs = {}
        self.weak = {}                # V class -> its same-named W pair whose members say little
        self.wsup = {}
        self.nested = collections.defaultdict(set)  # W class -> its named nested classes
        for w in W:
            if "$" in w and not ANON.search(w):
                self.nested[w[:w.rfind("$")]].add(w)

    # ---------------------------------------------------------------- names
    def base_name(self, c):
        if c in self.base_c:
            return self.base_c[c]
        d = c.rfind("$")
        return self.base_name(c[:d]) + c[d:] if d > 0 else c

    def tcls(self, c):
        """V class as the target names it, or a Wild when it is a V class not decided yet"""
        if c.startswith("["):
            return c
        if c in self.cm:
            return self.cm[c]
        if c in self.V:
            return Wild(c)
        # not a game class of V (JDK, library): the same everywhere
        return c

    def tdesc(self, d):
        """a descriptor as a token list: plain text pieces and translated classes (Wild for undecided)"""
        out, i = [], 0
        for m in DESC_CLS.finditer(d):
            if m.start() > i:
                out.append(d[i:m.start()])
            out.append(("L", self.tcls(m.group(1))))
            i = m.end()
        if i < len(d):
            out.append(d[i:])
        return out

    def mname(self, owner, name):
        """V member name translated (base table, then the renames this run found)"""
        if self.identity:
            return self.ren.get(owner + "." + name, name)
        if not TOKEN.match(name):
            return name
        if name in self.ren:
            return self.ren[name]
        n = self.base_m.get(owner + "." + name)
        return n if n is not None else self.bare.get(name, name)

    # ---------------------------------------------------------------- W lookups
    def wsupers(self, w):
        if w in self.wsup:
            return self.wsup[w]
        out, todo = [], [w]
        while todo:
            x = todo.pop(0)
            if x is None or x in out:
                continue
            out.append(x)
            k = self.W.get(x)
            if k:
                todo.append(k.super)
                todo.extend(k.interfaces)
        self.wsup[w] = out
        return out

    def w_method(self, w, name, desc_tokens):
        """the method target class w (or a supertype) declares under name with a descriptor unifying with these"""
        for s in self.wsupers(w):
            k = self.W.get(s)
            if not k:
                continue
            for m in k.methods:
                if m.name == name and unify_desc(desc_tokens, m.desc, []):
                    return m
        return None

    def w_has(self, w, kind, name, desc_tokens, votes):
        """does target class w (or a supertype) declare kind/name with a descriptor unifying with these?"""
        for s in self.wsupers(w):
            k = self.W.get(s)
            if not k:
                continue
            for m in (k.methods if kind != "F" else k.fields):
                if m.name == name and unify_desc(desc_tokens, m.desc, votes):
                    return True
        return False

    # ---------------------------------------------------------------- body comparison
    def norm_operand(self, x, votes, wx):
        """compare a V operand (translated) with the W one; append implied pairs to votes"""
        if isinstance(x, tuple) and x:
            t = x[0]
            if t == "class":
                return isinstance(wx, tuple) and wx[0] == "class" and self.unify_cls(x[1], wx[1], votes)
            if t == "ref":
                return isinstance(wx, tuple) and wx[0] == "ref" and self.unify_ref(x, wx, votes)
            if t == "mhandle":
                return isinstance(wx, tuple) and wx[0] == "mhandle" and x[1] == wx[1] and \
                    self.unify_ref(("ref", "M" if x[1] not in (1, 2, 3, 4) else "F", x[2], x[3], x[4]),
                                   ("ref", "M" if wx[1] not in (1, 2, 3, 4) else "F", wx[2], wx[3], wx[4]), votes)
            if t == "mtype":
                return isinstance(wx, tuple) and wx[0] == "mtype" and unify_desc(self.tdesc(x[1]), wx[1], votes)
            if t == "indy":
                return isinstance(wx, tuple) and wx[0] == "indy" and x[1] == wx[1] and \
                    unify_desc(self.tdesc(x[2]), wx[2], votes) and \
                    self.norm_operand(x[3], votes, wx[3]) and len(x[4]) == len(wx[4]) and \
                    all(self.norm_operand(a, votes, b) for a, b in zip(x[4], wx[4]))
            if not isinstance(wx, tuple) or len(x) != len(wx):
                return False
            return all(self.norm_operand(a, votes, b) for a, b in zip(x, wx))
        return x == wx

    def unify_cls(self, vc, wc, votes):
        if vc.startswith("["):
            return unify_desc(self.tdesc(vc), wc, votes)
        t = self.tcls(vc)
        if isinstance(t, Wild):
            if wc in self.claimed and self.claimed[wc] != vc:
                return False
            votes.append(("C", vc, wc))
            return True
        return t == wc

    def unify_ref(self, x, wx, votes):
        _, kind, owner, name, desc = x
        _, wkind, wowner, wname, wdesc = wx
        if kind != wkind or not self.unify_cls(owner, wowner, votes):
            return False
        if not unify_desc(self.tdesc(desc), wdesc, votes):
            return False
        n = self.mname(owner, name)
        if n == wname:
            return True
        # a different name: the same member only when V's (translated) name is not there any more
        tow = self.tcls(owner)
        if isinstance(tow, Wild):
            tow = wowner
        if self.w_has(tow, "F" if kind == "F" else "M", n, self.tdesc(desc), []):
            return False
        if not self.identity and not TOKEN.match(name):
            return False
        # a library member (the JDK's, DFU's) has the same name in both: never a rename
        if owner not in self.V:
            return False
        votes.append(("F" if kind == "F" else "M", owner, name, wowner, wname))
        return True

    def same_code(self, vm, wm):
        """(True, votes) when wm's body is vm's under the names known so far, with the implied pairs"""
        if vm.insns is None or wm.insns is None or len(vm.insns) != len(wm.insns):
            return False, None
        votes = []
        anchors = 0
        for (op, a), (wop, wa) in zip(vm.insns, wm.insns):
            before = len(votes)
            if op != wop or not self.norm_operand(a, votes, wa):
                return False, None
            # evidence is what matched as it is: a reference or constant equal without any rename
            # implied (two setters of different fields are "the same code" only by assuming the rename)
            if len(votes) == before and (isinstance(a, tuple) or op == 0x12):
                anchors += 1
        self.last_anchors = anchors
        return True, votes

    # ---------------------------------------------------------------- uses in aligned code
    def reached(self, w):
        """the field names of target class w some V field answers to by its own (translated) name"""
        c = self.claimed.get(w)
        if c is None or c not in self.V:
            return frozenset()
        return frozenset(self.mname(c, x.name) for x in self.V[c].fields)

    def free_field(self, a):
        """a V field reference whose (translated) name its target owner does not declare"""
        _, kind, owner, name, desc = a
        if kind != "F" or owner not in self.V or not (self.identity or TOKEN.match(name)):
            return False
        tow = self.tcls(owner)
        return not isinstance(tow, Wild) and not self.w_has(tow, "F", self.mname(owner, name), self.tdesc(desc), [])

    def tdesc_str(self, d):
        """a descriptor translated, or a token that equals nothing in the target while a class is undecided"""
        out = []
        for t in self.tdesc(d):
            if isinstance(t, str):
                out.append(t)
            elif isinstance(t[1], Wild):
                return ("wild", d)
            else:
                out.append("L" + t[1] + ";")
        return "".join(out)

    def akey(self, op, a, side, reached):
        """an instruction as a comparable key, V's translated into the target's names; a member name
        neither side answers by name is a wildcard (what the aligned code then says it is)"""
        if not isinstance(a, tuple) or not a:
            return (op, a)
        t = a[0]
        if t == "ref":
            _, kind, owner, name, desc = a
            if side == "V":
                tow = self.tcls(owner)
                if kind == "F" and self.free_field(a):
                    name = "?"
                else:
                    name = self.mname(owner, name)
                return (op, kind, tow, name, self.tdesc_str(desc))
            if kind == "F" and name not in reached(owner):
                name = "?"
            return (op, kind, owner, name, desc)
        if t == "class":
            return (op, "class", self.tcls(a[1]) if side == "V" else a[1])
        if t in ("str", "int", "float", "long", "double"):
            return (op, a)
        return (op, "opaque")

    def aligned_field_votes(self, c, m, x):
        """fields V's body m uses where the target's body x of the same member uses another field, in a
        stretch of code that is otherwise the same (each instruction equal once names are translated)
        with at least two references or constants in it that matched as they are. A body reworked around
        a field still uses it in the same surroundings (26.1 LecternRenderer's constructor stores
        Context.sprites() into sprites where 1.21.11 stored the same call's answer into materials).
        -> [("F", V owner, V name, W name, anchors, why)]"""
        if m.insns is None or x.insns is None:
            return []
        if not any(op in (0xb4, 0xb5) and self.free_field(a) for op, a in m.insns):
            return []
        cache = {}

        def reached(o):
            if o not in cache:
                cache[o] = self.reached(o)
            return cache[o]
        vk = [self.akey(op, a, "V", reached) for op, a in m.insns]
        wk = [self.akey(op, a, "W", reached) for op, a in x.insns]
        sm = difflib.SequenceMatcher(None, vk, wk, autojunk=False)

        def loose(k):
            # local slots aside: a second copy of a stretch keeps its values in other locals
            op = k[0]
            if 0x15 <= op <= 0x2d:
                return "load"
            if 0x36 <= op <= 0x4e:
                return "store"
            if op == 0x84:
                return "iinc"
            return k
        vl, wl = [loose(k) for k in vk], [loose(k) for k in wk]

        def once(keys, s, n):
            """the stretch keys[s:s+n] reads that way nowhere else in its body"""
            pat = keys[s:s + n]
            return not any(keys[q:q + n] == pat for q in range(len(keys) - n + 1) if q != s)
        out = []
        for i, j, n in sm.get_matching_blocks():
            # per instruction of the block: matched?, its votes, anchor (matched as it is), telling anchor
            # (a constant, or a game member or class: "new HashMap()" is every map's)
            st = []
            for t in range(n):
                (op, a), (wop, wa) = m.insns[i + t], x.insns[j + t]
                v = []
                ok = op == wop and self.norm_operand(a, v, wa)
                anc = ok and not v and (isinstance(a, tuple) or op == 0x12)
                tel = anc and (op == 0x12 or (a[0] == "ref" and a[2] in self.V) or (a[0] == "class" and a[1] in self.V))
                st.append((ok, v, anc, tel, op))
            t = 0
            while t < n:
                if not st[t][0]:
                    t += 1
                    continue
                s = t
                while t < n and st[t][0]:
                    t += 1
                run = range(s, t)
                if any(e[0] == "C" for q in run for e in st[q][1]):
                    continue  # the stretch is the same code only by assuming a class move
                for p in run:
                    if st[p][4] not in (0xb4, 0xb5):
                        continue
                    for e in st[p][1]:
                        if e[0] != "F" or not self.same_field_type(e[1], e[2], e[4]):
                            continue
                        # the use is pinned by the code around it: growing outward from it, the stretch from
                        # its first to its last anchor holds two anchors, one of them telling, and reads that
                        # way only once in each body (26.1's CustomFeatureRenderer$Storage clears its solid
                        # and its translucent map alike: 1.21.11's one map is neither; 1.21.11's
                        # SheepWoolUndercoatLayer bakes two models alike, told apart by the layer it bakes)
                        lo = hi = p
                        found = False
                        while True:
                            ap = [q for q in range(lo, hi + 1) if st[q][2]]
                            if len(ap) >= 2 and any(st[q][3] for q in ap):
                                a0, a1 = min(ap[0], p), max(ap[-1], p)
                                if once(vl, i + a0, a1 - a0 + 1) and once(wl, j + a0, a1 - a0 + 1):
                                    found = True
                                    break
                            if lo == s and hi == t - 1:
                                break
                            lo, hi = max(s, lo - 1), min(t - 1, hi + 1)
                        if not found:
                            continue
                        anc = len(ap)
                        out.append(("F", e[1], e[2], e[4], anc,
                                    f"used where the same code uses it ({anc} references matched as they are "
                                    f"around it, in {c}.{m.name})"))
        # (Not static fields: a stretch of a list of constants -- a bootstrap fetching
        # VegetationFeatures.PATCH_* one after another, the tails of RenderPipelines' builder chains --
        # aligns any slot with any other; a static constant is judged by its own initializer,
        # same_init_constants.)
        return out

    def tsig(self, s):
        """a generic signature with its V classes translated, or None while one is undecided"""
        out, i = [], 0
        for m in SIG_CLS.finditer(s):
            t = self.tcls(m.group(1))
            if isinstance(t, Wild):
                return None
            out.append(s[i:m.start(1)] + t)
            i = m.end(1)
        return "".join(out) + s[i:]

    def own_constant(self, c, w, f, n):
        """the target field V's static constant f went to when its name n is gone from w: the one static
        field of w with the same erased type and the same generic signature, naming the class itself,
        while f is the one such constant of V's class (26.1 renamed the MapCodec constant of loot
        entries, loot functions and value providers CODEC -> MAP_CODEC, the class initializers that
        build them reworked: 1.21.11 LootItem.CODEC is MapCodec<LootItem>, 26.1's MAP_CODEC is too)"""
        if not f.access & bytecode.ACC_STATIC or not f.signature:
            return None
        if ("L" + c + ";") not in f.signature and ("L" + c + "<") not in f.signature:
            return None
        if self.w_has(w, "F", n, self.tdesc(f.desc), []):
            return None
        sig = self.tsig(f.signature)
        if sig is None:
            return None
        mine = [g for g in self.V[c].fields if g.access & bytecode.ACC_STATIC and g.signature == f.signature]
        if len(mine) != 1:
            return None
        theirs = [y for y in self.W[w].fields if y.access & bytecode.ACC_STATIC and not y.access & bytecode.ACC_SYNTHETIC
                  and y.signature == sig and unify_desc(self.tdesc(f.desc), y.desc, [])]
        if len(theirs) != 1:
            return None
        y = theirs[0]
        # not a field another V member of the class already answers to by its own name
        if any(g is not f and self.mname(c, g.name) == y.name for g in self.V[c].fields):
            return None
        return y

    # ---------------------------------------------------------------- static constants by initializer
    def init_seg(self, side, cls, fname):
        """the class initializer's code that computes static field fname of cls (from the previous
        static store up to its own), or None"""
        key = (side, cls, fname)
        if key in self.segs:
            return self.segs[key]
        k = (self.V if side == "V" else self.W).get(cls)
        out = None
        clinit = next((m for m in k.methods if m.name == "<clinit>"), None) if k else None
        if clinit is not None and clinit.insns:
            start = 0
            for i, (op, a) in enumerate(clinit.insns):
                if op != 0xb3:
                    continue
                if a[2] == cls and a[3] == fname:
                    out = clinit.insns[start:i]
                    break
                start = i + 1
        self.segs[key] = out
        return out

    def loose_code(self, vi, wi, vcls, wcls, depth=2):
        """(string distance, votes) when W code wi is V code vi under the names known so far with only
        string constants differing (each differing pair adds 1 - its similarity ratio); a lambda of the class itself is compared by its body (two
        levels). None when they are other code."""
        if vi is None or wi is None or len(vi) != len(wi):
            return None
        votes, diff = [], 0
        for (op, a), (wop, wa) in zip(vi, wi):
            if op != wop:
                return None
            if isinstance(a, tuple) and a and a[0] == "str":
                if not (isinstance(wa, tuple) and wa and wa[0] == "str"):
                    return None
                if a[1] != wa[1]:
                    # how far apart (resource ids of the same family: "pipeline/entity_cutout" is nearer
                    # "pipeline/entity_cutout_no_cull" than "..._no_cull_z_offset")
                    diff += 1 - difflib.SequenceMatcher(None, a[1], wa[1]).ratio()
                continue
            if op == 0xba and depth > 0 and isinstance(a, tuple) and isinstance(wa, tuple) and \
                    a[1] == wa[1] and len(a[4]) == len(wa[4]) and a[3] == wa[3]:
                if not unify_desc(self.tdesc(a[2]), wa[2], votes):
                    return None
                for x, y in zip(a[4], wa[4]):
                    if isinstance(x, tuple) and x and x[0] == "mhandle" and isinstance(y, tuple) and y and \
                            y[0] == "mhandle" and x[2] == vcls and y[2] == wcls:
                        vm = next((m for m in self.V[vcls].methods if m.name == x[3] and m.desc == x[4]), None)
                        wm = next((m for m in self.W[wcls].methods if m.name == y[3] and m.desc == y[4]), None)
                        if vm is None or wm is None or not unify_desc(self.tdesc(x[4]), y[4], votes):
                            return None
                        r = self.loose_code(vm.insns, wm.insns, vcls, wcls, depth - 1)
                        if r is None:
                            return None
                        diff += r[0]
                        votes.extend(r[1])
                    elif not self.norm_operand(x, votes, y):
                        return None
                continue
            if not self.norm_operand(a, votes, wa):
                return None
        return diff, votes

    def init_diff(self, vc, vf, wc, wf):
        """string distance of two constants' initializers, None when they are other code -- or only the
        same code by assuming where an undecided V class went (1.21.11's RandomPatchFeature, gone in
        26.1, would otherwise be any feature class)"""
        r = self.loose_code(self.init_seg("V", vc, vf), self.init_seg("W", wc, wf), vc, wc)
        if r is None or any(v[0] == "C" for v in r[1]):
            return None
        return r[0]

    def field_reuse(self, c, w):
        """static constants whose name the target passed along: the same-named target constant is
        computed by other code, which is the initializer of another V constant of the class (string
        constants aside), while one other target constant is computed by this one's (26.1
        RenderPipelines: ENTITY_CUTOUT is 1.21.11's ENTITY_CUTOUT_NO_CULL -- withCull(false) -- and
        1.21.11's culling ENTITY_CUTOUT is ENTITY_CUTOUT_CULL). Both ends of the chain are decided
        together: {V field: (W field, why)}. Fewest differing string constants wins; a tie is listed."""
        vk, wk = self.V[c], self.W[w]
        # (not enum constants: an enum constant is its name -- valueOf, the serialized form -- and the
        # ordinal in its initializer moves whenever one is inserted before it)
        vst = [f for f in vk.fields if f.access & bytecode.ACC_STATIC and not f.access & (bytecode.ACC_SYNTHETIC | bytecode.ACC_ENUM)
               and (self.identity or TOKEN.match(f.name)) and (self.identity or f.name not in self.ren)]
        wst = [y for y in wk.fields if y.access & bytecode.ACC_STATIC and not y.access & bytecode.ACC_SYNTHETIC]
        if not vst or not any(m.name == "<clinit>" for m in wk.methods):
            return {}
        out = {}

        def best(f, pool):
            dt = self.tdesc(f.desc)
            sc = []
            for y in pool:
                if not unify_desc(dt, y.desc, []):
                    continue
                d = self.init_diff(c, f.name, w, y.name)
                if d is not None:
                    sc.append((d, y.name))
            sc.sort()
            if not sc:
                return None, None
            if len(sc) > 1 and sc[1][0] == sc[0][0]:
                return None, sorted(n for d, n in sc if d == sc[0][0])
            return sc[0], None

        for f in vst:
            n = self.mname(c, f.name)
            x = next((y for y in wst if y.name == n and unify_desc(self.tdesc(f.desc), y.desc, [])), None)
            if x is None or self.init_seg("V", c, f.name) is None or self.init_seg("W", w, x.name) is None:
                continue
            if self.init_diff(c, f.name, w, x.name) is not None:
                continue
            # who now holds the name: another V constant of the class whose own name the target has not
            # kept for it (gone, or itself passed along)
            holders = []
            for g in vst:
                if g is f or not unify_desc(self.tdesc(g.desc), x.desc, []):
                    continue
                gn = self.mname(c, g.name)
                gx = next((y for y in wst if y.name == gn), None)
                if gx is not None and self.init_diff(c, g.name, w, gx.name) is not None:
                    continue
                d = self.init_diff(c, g.name, w, x.name)
                if d is not None:
                    holders.append((d, g))
            holders.sort(key=lambda e: e[0])
            if not holders or (len(holders) > 1 and holders[1][0] == holders[0][0]):
                if holders:
                    self.reuse_amb[(c, f.name)] = ("constant name passed along", n, ", ".join(sorted(self.mname(c, g.name) for d, g in holders)),
                                                   "more than one V constant is computed by the target constant's initializer")
                continue
            g = holders[0][1]
            pick, tie = best(f, [y for y in wst if y is not x])
            if tie:
                self.reuse_amb[(c, f.name)] = ("constant name passed along", n, ", ".join(tie),
                                               "more than one target constant is computed by this one's initializer")
                continue
            if pick is None:
                continue
            why = (f"constant name passed along: {w}.{n} is computed as V's {self.mname(c, g.name)} was "
                   f"(string distance {holders[0][0]:.3f}), {pick[1]} as this one was (string distance {pick[0]:.3f})")
            out[f.name] = (pick[1], why)
            self.reuse[(c, f.name)] = ("constant name passed along", n, pick[1] + "(" + f.desc + ")", why)
            if g.name not in out:
                gwhy = f"constant name passed along: {w}.{n} is computed as this one was (string distance {holders[0][0]:.3f})"
                out[g.name] = (n, gwhy)
                self.reuse[(c, g.name)] = ("constant name passed along", self.mname(c, g.name), n + "(" + g.desc + ")", gwhy)
        return out

    # ---------------------------------------------------------------- member matching of a class pair
    def name_reuse(self, c, w, own_w):
        """V methods of class c whose (translated) name the target class w reused for other code: the
        target member of that name is not the same code as V's body, and exactly one other member of w
        is (26.1 RenderTypes: entityCutout(Identifier) is 1.21.11's entityCutoutNoCull, and 1.21.11's
        culling entityCutout is entityCutoutCull). A same-named member that only takes the old name links
        and does something else, silently. Also a same name kept with another descriptor while V's body
        lives on, the same code, under another name. -> {V name: (W member, votes, anchors, why)}

        A candidate counts only when its body has at least two references that matched as they are, and
        is not already answered, the same code, by the V member of its own name (V's body is then merely
        alike, not moved). More than one candidate is listed (ambiguous), never guessed."""
        vk = self.V[c]
        out = {}

        def answered(y):
            for z in vk.methods:
                if z.insns is None or self.mname(c, z.name) != y.name or not unify_desc(self.tdesc(z.desc), y.desc, []):
                    continue
                ok, _ = self.same_code(z, y)
                if ok:
                    return z.name
            return None

        for m in vk.methods:
            if m.insns is None or m.access & bytecode.ACC_SYNTHETIC or m.name in GENERIC:
                continue
            if not (self.identity or TOKEN.match(m.name)):
                continue
            n = self.mname(c, m.name)
            if n in GENERIC:
                continue
            dt = self.tdesc(m.desc)
            x = next((x for x in own_w if x.name == n and unify_desc(dt, x.desc, [])), None)
            if x is not None:
                if x.insns is None:
                    continue
                ok, _ = self.same_code(m, x)
                if ok:
                    continue
                kind = "name reused"
            elif any(x.name == n for x in own_w):
                kind = "name reshaped"
            else:
                continue
            # the members the same-named one hands over to: then it is the same thing (26.1
            # Registries.elementsDirPath calls the private registryDirPath, which has 1.21.11's body)
            called = {(a[3], a[4]) for op, a in (x.insns if x is not None else ())
                      if op in (0xb6, 0xb7, 0xb8, 0xb9) and isinstance(a, tuple) and a[2] == w}
            cands = []
            for y in own_w:
                # never to a member the old references could not reach
                if y.access & bytecode.ACC_PRIVATE and not m.access & bytecode.ACC_PRIVATE:
                    continue
                if y is x or y.name == n or y.insns is None or \
                        bool(y.access & bytecode.ACC_STATIC) != bool(m.access & bytecode.ACC_STATIC):
                    continue
                v = []
                if not unify_desc(dt, y.desc, v):
                    continue
                ok, bv = self.same_code(m, y)
                if ok and self.last_anchors >= 2:
                    cands.append((y, v + bv, self.last_anchors))
            if not cands or any((e[0].name, e[0].desc) in called for e in cands):
                # the same-named member being the same code as another V member instead (whose own
                # name went) is a collision too, with no home for this one's body: listed, the row
                # keeps the name (a bridge decides)
                if x is not None and not cands and not called:
                    for z in vk.methods:
                        if z is m or z.insns is None or z.access & bytecode.ACC_SYNTHETIC or \
                                not unify_desc(self.tdesc(z.desc), x.desc, []):
                            continue
                        zn = self.mname(c, z.name)
                        if zn == n:
                            continue
                        ok, _ = self.same_code(z, x)
                        if ok and self.last_anchors >= 2:
                            self.reuse_amb[(c, m.name)] = (kind, n, f"{w}.{n} is the same code as V's {zn}",
                                                           "the name went to another member's code; this body has no same-code home")
                            break
                continue
            key = (c, m.name)
            free = [e for e in cands if answered(e[0]) is None]
            if len(free) == 1:
                y, v, anchors = free[0]
                why = (f"{kind}: {w}.{n}{x.desc if x is not None else ''} is other code, "
                       f"{y.name}{y.desc} is the same code as V's body")
                out[m.name] = (y, v, anchors, why)
                self.reuse[key] = (kind, n, y.name + y.desc, why)
            elif len(free) > 1:
                # the bodies differ only in the constants of the class they read (26.1 RenderTypes:
                # each factory returns its memoized function's answer): the constant computed by the
                # same code as V's, string constants aside, tells them apart
                scored = []
                for e in free:
                    fv = [x for x in e[1] if x[0] == "F" and x[1] == c and x[3] == w]
                    ds = [self.init_diff(c, x[2], w, x[4]) for x in fv]
                    if fv and None not in ds:
                        scored.append((sum(ds), e))
                scored.sort(key=lambda s: s[0])
                if scored and (len(scored) == 1 or scored[1][0] > scored[0][0]):
                    y, v, anchors = scored[0][1]
                    why = (f"{kind}: {w}.{n}{x.desc if x is not None else ''} is other code; {len(free)} members are the "
                           f"same code as V's body, {y.name}{y.desc} the one whose constants are computed as V's "
                           f"(string distance {scored[0][0]:.3f})")
                    out[m.name] = (y, v, anchors, why)
                    self.reuse[key] = (kind, n, y.name + y.desc, why)
                else:
                    self.reuse_amb[key] = (kind, n, ", ".join(sorted(e[0].name + e[0].desc for e in free)),
                                           "more than one target member is the same code as V's body")
        return out

    def kept_body_votes(self, c, m, x):
        """the renames a V body m implies whose member kept its name on the target as x: when x is the
        same code, the members it names (two references or more matched as they are; for a field one is
        enough, the member around it being decided by its name -- 1.21.11 UniformFloat.sample reads the
        two bounds around one Mth.randomBetween call as 26.1's does, min and max); when it is not, the
        fields it uses in stretches that are the same code (aligned_field_votes)"""
        ok, bv = self.same_code(m, x)
        if not ok:
            return self.aligned_field_votes(c, m, x)
        a = self.last_anchors
        why = "named in a kept body that is the same code"
        out = [("M" if e[0] == "M" else "F", e[1], e[2], e[4], a, why) for e in bv if e[0] in ("M", "F") and a >= 2]
        if a == 1:
            out.extend(("F", e[1], e[2], e[4], a, why + " (one reference matched as it is, its member kept)")
                       for e in bv if e[0] == "F" and self.same_field_type(e[1], e[2], e[4]))
        return out

    def same_field_type(self, o, vname, wname):
        """V field o.vname and its target namesake wname (on o's target class or a supertype) have the same
        generic signature once V's classes are translated (no signature on either side: the erased types
        already unified; a signature on one side only, or a V class in it not decided yet, is not shown
        the same). A field not found on either side says nothing here (True)."""
        f = next((x for x in self.V[o].fields if x.name == vname), None) if o in self.V else None
        w = self.cm.get(o)
        if f is None or w is None:
            return True
        y = None
        for s in self.wsupers(w):
            k = self.W.get(s)
            y = next((x for x in k.fields if x.name == wname), None) if k else None
            if y is not None:
                break
        if y is None:
            return True
        if not f.signature and not y.signature:
            return True
        if not f.signature or not y.signature:
            return False
        sig = self.tsig(f.signature)
        return sig is not None and sig == y.signature

    def same_init_constants(self, c, w):
        """static constants whose name is gone, computed by the very code (string constants included) that
        computes one target constant no V field answers by name -- the one such pair both ways, and an
        initializer that says something (a constant, or a member of a game class: "new ArrayList()" is
        every list). 26.1's EnderDragonRenderer.DRAGON_TEXTURE_LOCATION is
        Identifier.withDefaultNamespace("textures/entity/enderdragon/dragon.png"), 1.21.11's DRAGON_LOCATION.
        -> {V field: (W field, why)}"""
        vk, wk = self.V[c], self.W[w]
        if not any(m.name == "<clinit>" for m in wk.methods):
            return {}
        reached = self.reached(w)

        def says_something(seg):
            return any(op == 0x12 or (isinstance(a, tuple) and a and a[0] == "ref" and a[2] in self.V) for op, a in seg)
        vst = []
        for f in vk.fields:
            if not f.access & bytecode.ACC_STATIC or f.access & bytecode.ACC_SYNTHETIC or f.name in self.ren:
                continue
            if not (self.identity or TOKEN.match(f.name)):
                continue
            if self.w_has(w, "F", self.mname(c, f.name), self.tdesc(f.desc), []):
                continue
            seg = self.init_seg("V", c, f.name)
            if seg and says_something(seg):
                vst.append(f)
        if not vst:
            return {}
        wst = [y for y in wk.fields if y.access & bytecode.ACC_STATIC and not y.access & bytecode.ACC_SYNTHETIC
               and y.name not in reached and self.init_seg("W", w, y.name)]
        pairs = collections.defaultdict(list)
        for f in vst:
            dt = self.tdesc(f.desc)
            for y in wst:
                if not unify_desc(dt, y.desc, []):
                    continue
                if (f.signature or y.signature) and (not f.signature or not y.signature or self.tsig(f.signature) != y.signature):
                    continue
                if self.init_diff(c, f.name, w, y.name) == 0:
                    pairs[f.name].append(y.name)
        back = collections.Counter(n for ys in pairs.values() for n in ys)
        out = {}
        for fname, ys in pairs.items():
            if len(ys) == 1 and back[ys[0]] == 1:
                out[fname] = (ys[0], f"the same initializer as {w}.{ys[0]} ({self.mname(c, fname)} gone)")
        return out

    def match_members(self, c, w, commit):
        """members of V class c found on W class w: (score parts, rename votes, class votes)"""
        vk, wk = self.V[c], self.W[w]
        own_w = [m for m in wk.methods if not m.access & bytecode.ACC_SYNTHETIC and m.name not in GENERIC]
        own_wf = [f for f in wk.fields if not f.access & bytecode.ACC_SYNTHETIC and f.name not in GENERIC]
        matched, total = 0.0, 0
        renames, cvotes = [], []
        # W members V's members already reach by their own (translated) name: no rename may land there
        # (1.21.11 DerivedLevelData.isThundering delegates like isInitialized does; 26.1 dropped the
        # former, and its body is "the same code" as the latter's)
        moved = self.name_reuse(c, w, own_w) if commit else {}
        wused = set()
        for m in vk.methods:
            if m.name in moved:
                y = moved[m.name][0]
                wused.add((y.name, y.desc))
                continue
            n = self.mname(c, m.name)
            dt = self.tdesc(m.desc)
            for x in own_w:
                if x.name == n and unify_desc(dt, x.desc, []):
                    wused.add((x.name, x.desc))
        # the class initializer and constructors say nothing about which class it is (every class has
        # them), but a kept one that is the same code tells the renames it implies: the constant it
        # assigns (26.1's AllOfCondition.<clinit> stores into MAP_CODEC what 1.21.11's stored into CODEC)
        if commit:
            for m in vk.methods:
                if m.name not in ("<clinit>", "<init>") or m.insns is None:
                    continue
                for x in wk.methods:
                    if x.name == m.name and x.insns is not None and unify_desc(self.tdesc(m.desc), x.desc, []):
                        renames.extend(self.kept_body_votes(c, m, x))
                        break
        for m in vk.methods:
            if m.access & bytecode.ACC_SYNTHETIC or m.name in GENERIC:
                continue
            n = self.mname(c, m.name)
            if n in GENERIC:
                continue
            total += 1
            dt = self.tdesc(m.desc)
            if m.name in moved:
                y, v, anchors, why = moved[m.name]
                matched += 1
                cvotes.extend(e for e in v if e[0] == "C")
                renames.append(("M", c, m.name, y.name, anchors, why))
                renames.extend(("M" if e[0] == "M" else "F", e[1], e[2], e[4], anchors, "named in a body that is the same code")
                               for e in v if e[0] in ("M", "F"))
                continue
            v = []
            hit = next((x for x in own_w if x.name == n and unify_desc(dt, x.desc, v)), None)
            if hit is not None:
                matched += 1
                cvotes.extend(v)
                # a body that kept its name and is the same code tells the renames it implies (the
                # fields it reads and writes, the methods it calls: 26.1's loot classes named their
                # MapCodec constant MAP_CODEC where 1.21.11 said CODEC, the class initializer that
                # builds it otherwise unchanged)
                if commit and m.insns is not None and hit.insns is not None:
                    renames.extend(self.kept_body_votes(c, m, hit))
                continue
            if self.w_has(w, "M", n, dt, []):
                matched += 0.5  # inherited on W: an override every subclass of the parent may have
                continue
            # the same name with another descriptor: reshaped in place, unless the body moved to another member of
            # the class and the name went to new code (26.1 BundleContents: 1.21.11's int getSelectedItem() is
            # getSelectedItemIndex(), the same code; getSelectedItem() now answers an ItemStackTemplate) -- looked
            # for below, the same name counting half when nothing else is the same code
            reshaped = any(x.name == n for x in own_w)
            if not (self.identity or TOKEN.match(m.name)):
                if reshaped:
                    matched += 0.5
                continue
            cands = []
            for x in own_w:
                if (x.name, x.desc) in wused or bool(x.access & bytecode.ACC_STATIC) != bool(m.access & bytecode.ACC_STATIC):
                    continue
                v = []
                if not unify_desc(dt, x.desc, v):
                    continue
                ok, bv = self.same_code(m, x)
                if ok:
                    cands.append((x, v + bv, self.last_anchors))
            if len(cands) == 1 and not (reshaped and cands[0][2] < 1):
                x, v, anchors = cands[0]
                matched += 1
                wused.add((x.name, x.desc))
                cvotes.extend(e for e in v if e[0] == "C")
                renames.append(("M", c, m.name, x.name, anchors,
                                "same code (its name went to another descriptor)" if reshaped else "same code"))
                renames.extend(("M" if e[0] == "M" else "F", e[1], e[2], e[4], anchors, "named in a body that is the same code")
                               for e in v if e[0] in ("M", "F"))
            elif reshaped:
                matched += 0.5  # the same name, reshaped in place
        fmoved = self.field_reuse(c, w) if commit else {}
        if commit:
            for k, v in self.same_init_constants(c, w).items():
                fmoved.setdefault(k, v)
        for f in vk.fields:
            if f.access & bytecode.ACC_SYNTHETIC or f.name in GENERIC:
                continue
            n = self.mname(c, f.name)
            if f.name in fmoved:
                wn, why = fmoved[f.name]
                total += 1
                matched += 1
                renames.append(("F", c, f.name, wn, 1, why))
                continue
            if commit and f.name not in self.ren and (self.identity or TOKEN.match(f.name)):
                y = self.own_constant(c, w, f, n)
                if y is not None:
                    renames.append(("F", c, f.name, y.name, 1,
                                    f"the one static constant of its generic type {y.signature} in both ({n} gone)"))
            if n in GENERIC:
                continue
            total += 1
            dt = self.tdesc(f.desc)
            v = []
            hit = next((x for x in own_wf if x.name == n and unify_desc(dt, x.desc, v)), None)
            if hit is not None:
                matched += 1
                cvotes.extend(v)
            elif any(x.name == n for x in own_wf):
                matched += 0.5
        nw = len(own_w) + len(own_wf)
        return matched, total, nw, renames, cvotes

    def match_synthetics(self, log):
        """Synthetic methods (lambda bodies, bridges) of decided class pairs, by body.

        The rounds above never look at a synthetic member: it keeps the name the base table gives it, which
        is the 1.21.11 compiler's own numbering (lambda$static$3). The target release's compiler numbers its
        lambdas anew, so that name is often another lambda of the class or none (26.1 rebuilt ItemStack's
        codecs: 1.21.11's DataComponentPatch (ItemStack) getter lambda$static$3 is 26.1's lambda$static$2;
        26.1's lambda$static$3 is ItemStack (Optional)). A mixin selecting a lambda, and a converted
        invokedynamic naming one, then names the wrong code or nothing. A synthetic V method keeps its
        translated name only when the target class declares it with that descriptor AND the same code;
        otherwise it maps to the one synthetic method of the target class (same static-ness, descriptor
        unifying) whose body is its code, with at least one reference or constant matched as it is (one
        candidate; several V synthetics of identical code may share it). Lambdas calling other renumbered lambdas pair over rounds (a pair found
        makes the caller's body comparable). Bodies that changed stay as they are: damage, not guessed.
        Intermediary names only (identity mode's lambda$ names are not tokens: 26.1.2 -> 26.2 is not covered).
        """
        V, W = self.V, self.W
        done = set()
        for rnd in range(8):
            props = collections.defaultdict(list)   # (W class, W name, W desc) -> [(V class, V method, anchors)]
            for c, w in self.cm.items():
                vk, wk = V.get(c), W.get(w)
                if vk is None or wk is None:
                    continue
                wsyn = [x for x in wk.methods if x.access & bytecode.ACC_SYNTHETIC and x.insns is not None]
                if not wsyn:
                    continue
                keep, todo = set(), []
                for m in vk.methods:
                    if not m.access & bytecode.ACC_SYNTHETIC or m.insns is None or not TOKEN.match(m.name):
                        continue
                    n = self.mname(c, m.name)
                    dt = self.tdesc(m.desc)
                    x = next((x for x in wk.methods if x.name == n and unify_desc(dt, x.desc, [])), None)
                    if x is not None and self.same_code(m, x)[0]:
                        keep.add((x.name, x.desc))
                        continue
                    if m.name not in done:
                        todo.append(m)
                for m in todo:
                    dt = self.tdesc(m.desc)
                    cands = []
                    for x in wsyn:
                        if (x.name, x.desc) in keep or bool(x.access & bytecode.ACC_STATIC) != bool(m.access & bytecode.ACC_STATIC):
                            continue
                        if not unify_desc(dt, x.desc, []):
                            continue
                        ok, _ = self.same_code(m, x)
                        if ok and self.last_anchors >= 1:
                            cands.append((x, self.last_anchors))
                    if len(cands) == 1:
                        x, anchors = cands[0]
                        props[(w, x.name, x.desc)].append((c, m, anchors))
            changed = 0
            for (w, wn, wd), ev in props.items():
                # several V synthetics whose only counterpart is this one body: their code is the same as each
                # other's and the target keeps it once (26.1's ItemStack builds CODEC and the single-item
                # codec on one MapCodec, so 1.21.11's two "stack -> components patch" getters are its one
                # lambda$static$2); each maps there -- a private synthetic member nothing overrides, and every
                # role its V copies had runs through it
                for c, m, anchors in ev:
                    if self.mname(c, m.name) == wn:
                        done.add(m.name)
                        continue
                    # identity mode keys a member "owner.name" (mname), token mode by its token
                    rk = c + "." + m.name if self.identity else m.name
                    self.ren[rk] = wn
                    self.ren_basis[rk] = (f"synthetic: same code as {w}.{wn}{wd} (strength {anchors}; base name "
                                              f"{self.base_m.get(c + '.' + m.name, m.name)}"
                                              + (f"; one of {len(ev)} V synthetics with this code" if len(ev) > 1 else "") + ")")
                    done.add(m.name)
                    changed += 1
            log(f"synthetic round {rnd}: {changed} renames")
            if not changed:
                break

    def same_bodies(self, c, w):
        """how many of V class c's bodies (the class initializer and constructors included) are the same
        code as the W class w's member of that (translated) name, with something in them that matched as
        it is"""
        n = 0
        wm = {}
        for m in self.W[w].methods:
            wm.setdefault(m.name, []).append(m)
        for m in self.V[c].methods:
            if m.insns is None or m.access & bytecode.ACC_SYNTHETIC:
                continue
            for x in wm.get(self.mname(c, m.name), ()):
                ok, _ = self.same_code(m, x)
                if ok and self.last_anchors >= 1:
                    n += 1
                    break
        return n

    def keeps_instance(self, c, w):
        """does W class w still declare most of V class c's own instance methods with a body? (overrides
        of a supertype's method say nothing: 26.1's ice-spike SpikeFeature overrides Feature.place as
        1.21.11's end-spike SpikeFeature did)"""
        sup = self.wsupers(w)[1:]
        inst = [m for m in self.V[c].methods if m.insns is not None and not m.access & (bytecode.ACC_STATIC | bytecode.ACC_SYNTHETIC)
                and m.name not in GENERIC
                and not any(self.w_has(s, "M", self.mname(c, m.name), self.tdesc(m.desc), []) for s in sup)]
        if len(inst) < 2:
            return False
        hit = sum(self.w_has(w, "M", self.mname(c, m.name), self.tdesc(m.desc), []) for m in inst)
        return 2 * hit >= len(inst)

    def wraps(self, w, other):
        """does W class w hold an instance of W class other (a field or a component of that type)?"""
        k = self.W.get(w)
        return bool(k) and any(not f.access & bytecode.ACC_STATIC and ("L" + other + ";") in f.desc for f in k.fields)

    def coverage(self, c, w):
        """the share of V class c's members a reference through W class w resolves (inherited ones
        count in full: the JVM finds them)"""
        k = self.V[c]
        n = hit = 0
        for m in k.methods:
            if m.access & bytecode.ACC_SYNTHETIC or m.name in GENERIC:
                continue
            n += 1
            hit += self.w_has(w, "M", self.mname(c, m.name), self.tdesc(m.desc), [])
        for f in k.fields:
            if f.access & bytecode.ACC_SYNTHETIC or f.name in GENERIC:
                continue
            n += 1
            hit += self.w_has(w, "F", self.mname(c, f.name), self.tdesc(f.desc), [])
        return hit / n if n else 0.0

    def score(self, c, w):
        """(dice, matched, V members, W members) of V class c against W class w"""
        m, t, nw, _, _ = self.match_members(c, w, False)
        return (2 * m / (t + nw) if t + nw else 0.0), m, t, nw

    # ---------------------------------------------------------------- the fixed point
    def run(self, log):
        V, W = self.V, self.W
        # start: every V class whose base name the target declares, checked below
        for c in V:
            b = self.base_name(c)
            if b in W and b not in self.claimed:
                self.cm[c] = b
                self.claimed[b] = c
        for rnd in range(8):
            changed = 0
            # (a) a same-named pair whose members say little: the class went elsewhere only when an
            #     unclaimed target class answers them clearly better (step c); else it was reworked in
            #     place and keeps its name -- its members are the step's damage
            if rnd == 1:
                for c in list(self.cm):
                    w = self.cm[c]
                    d, m, t, nw = self.score(c, w)
                    if t >= 4 and d < 0.3:
                        self.weak[c] = (w, d, m, t, nw)
            # (b) member renames inside decided pairs, and the class pairs their bodies imply
            fam = collections.defaultdict(lambda: collections.defaultdict(list))
            fkeys = set()
            cvote = collections.defaultdict(collections.Counter)
            self.reuse_amb = {}       # what the names known now leave ambiguous (a later round may decide)
            for c, w in list(self.cm.items()):
                _, _, _, rens, cv = self.match_members(c, w, True)
                for kind, owner, vname, wname, st, why in rens:
                    key = (owner + "." + vname) if self.identity else vname
                    fam[key][wname].append((owner, st, why))
                    if kind == "F":
                        fkeys.add(key)
                for e in cv:
                    cvote[e[1]][e[2]] += 1
            # a target field two V fields of one class would both take: neither (26.1's EnderDragonRenderer
            # has one DYING_RENDER_TYPE where 1.21.11 had RENDER_TYPE and DECAL)
            fwant = collections.defaultdict(set)
            for key in fkeys:
                opts = fam[key]
                if key not in self.ren and len(opts) == 1:
                    (wname, ev), = opts.items()
                    for o in {e[0] for e in ev}:
                        fwant[(o, wname)].add(key)
            shared = {}
            for (o, wname), keys in fwant.items():
                if len(keys) > 1:
                    for key in keys:
                        shared[key] = f"conflict: {len(keys)} fields of {o} would take {wname}"
            for key, opts in fam.items():
                if key in self.ren:
                    continue
                if len(opts) != 1:
                    self.ren_basis[key] = "conflict: " + ", ".join(sorted(opts))
                    continue
                (wname, ev), = opts.items()
                st = max(e[1] for e in ev)
                # the name and descriptor another V member of that class already answers to are taken:
                # two V members would merge into one (an override of either would clash)
                vname = key.rsplit(".", 1)[1] if self.identity else key

                def taken_in(o):
                    if o not in V:
                        return False
                    mine = [x.desc for x in V[o].methods + V[o].fields if x.name == vname]
                    return any(m2.name != vname and self.mname(o, m2.name) == wname
                               and any(self.tdesc(m2.desc) == self.tdesc(d) for d in mine)
                               for m2 in V[o].methods + V[o].fields)
                taken = next((o for o, _, _ in ev if taken_in(o)), None)
                if taken:
                    self.ren_basis[key] = f"conflict: {wname} is another member of {taken}"
                    continue
                if key in shared:
                    self.ren_basis[key] = shared[key]
                    continue
                # a body with nothing in it that matched as it is (an empty setter, "return 0", a getter
                # of a field only assumed renamed) is the same code as any other such body: no
                # evidence, however many sites agree
                if st >= 1:
                    self.ren[key] = wname
                    self.ren_basis[key] = f"{ev[0][2]} ({len(ev)} site(s), strength {st}, in {ev[0][0]})"
                    changed += 1
            # (c) undecided V classes, and weak same-named pairs: candidates from the bodies' votes,
            #     shared member names and the outer class's move
            free = [w for w in W if w not in self.claimed]
            by_simple = collections.defaultdict(set)
            for w in free:
                by_simple[w[w.rfind("$") + 1:] if "$" in w else simple(w)].add(w)
            by_name = collections.defaultdict(set)
            for w in free:
                k = W[w]
                for m in k.methods:
                    if m.name not in GENERIC and not m.access & bytecode.ACC_SYNTHETIC:
                        by_name[m.name].add(w)
                for f in k.fields:
                    if f.name not in GENERIC:
                        by_name[f.name].add(w)
            for c in V:
                if c in self.cm and c not in self.weak:
                    continue
                cands = set(w for w in cvote.get(c, {}) if w in W and w not in self.claimed)
                k = V[c]
                names = collections.Counter()
                for m in k.methods:
                    n = self.mname(c, m.name)
                    if n not in GENERIC and len(by_name.get(n, ())) <= 25:
                        for w in by_name.get(n, ()):
                            names[w] += 1
                for f in k.fields:
                    n = self.mname(c, f.name)
                    if n not in GENERIC and len(by_name.get(n, ())) <= 25:
                        for w in by_name.get(n, ()):
                            names[w] += 1
                cands |= {w for w, _ in names.most_common(8)}
                # the unclaimed target classes of the same simple name are always looked at (a class
                # whose members all have common names never reaches the shared-name count above)
                tail = self.base_name(c)
                tail = tail[tail.rfind("$") + 1:] if "$" in tail else simple(tail)
                same_named = {w for w in by_simple.get(tail, ()) if w not in self.claimed
                              and not (W[w].access ^ k.access) & bytecode.ACC_INTERFACE}
                if not ANON.search(c):
                    cands |= same_named
                outer = c[:c.rfind("$")] if "$" in c else None
                follow = None
                if outer and outer in self.cm:
                    # the nested part as V's mojang names spell it (class_5235$class_5236 is
                    # EditGameRulesScreen$BooleanRuleEntry)
                    follow = self.cm[outer] + self.base_name(c)[len(self.base_name(outer)):]
                    if follow in W and follow not in self.claimed:
                        cands.add(follow)
                # a nested class may live on in a supertype its outer class's body was hoisted into
                # (26.2: Slime$SlimeAttackGoal is AbstractCubeMob$CubeMobAttackGoal, cubemob/Slime
                # extending AbstractCubeMob)
                hoisted = set()
                if outer and outer in self.cm and not ANON.search(c):
                    for s in self.wsupers(self.cm[outer])[1:]:
                        if s in W:
                            hoisted |= {w for w in self.nested.get(s, ()) if w not in self.claimed}
                    cands |= hoisted
                # an anonymous class is the same only where its outer class went, with the same number
                anon = bool(ANON.search(c))
                if anon:
                    cands = {follow} if follow in W and follow not in self.claimed else set()
                scored = []
                for w in cands:
                    if w in self.claimed or (W[w].access ^ k.access) & bytecode.ACC_INTERFACE:
                        continue
                    # a named class is never an anonymous one: no reference can name it (1.21.11's
                    # EmptyBlockAndTintGetter.INSTANCE is 26.1's BlockAndTintGetter.EMPTY, a field)
                    if ANON.search(w) and not anon:
                        continue
                    d, m, t, nw = self.score(c, w)
                    scored.append((d, cvote.get(c, {}).get(w, 0), w, m, t, nw))
                scored.sort(reverse=True)
                if c in self.weak:
                    w0, d0, m0, t0, nw0 = self.weak[c]
                    # the same-named class is still the type when it kept the instance behaviour (26.2's
                    # BlockEntityType: its constants went to BlockEntityTypes, its create/isValid stayed)
                    # or now wraps the class that took the members (26.1's WolfSoundVariant holds two
                    # WolfSoundSets; 26.2's Gui holds the Hud): registries and fields still hold it, so
                    # its old members are damage to bridge on it, not a move
                    best = scored[0][2] if scored else None
                    if best is not None and (self.keeps_instance(c, w0) or self.wraps(w0, best)):
                        continue
                    if scored and scored[0][0] >= 0.5 and scored[0][0] - d0 >= 0.25 and scored[0][3] >= 2:
                        del self.cm[c]
                        del self.claimed[w0]
                        del self.weak[c]
                        self.collisions[c] = (w0, f"its name went to another class: members {m0}/{t0} vs {nw0} (dice {d0:.2f})")
                    else:
                        continue
                if not scored:
                    continue
                d, nv, w, m, t, nw = scored[0]
                second = scored[1][0] if len(scored) > 1 else 0.0
                votes_all = sum(cvote.get(c, {}).values())
                unanimous = nv and nv == votes_all
                # hoisted siblings resemble each other (goals of one mob): the pairing must be mutual --
                # w answers c better than it answers any other nested class of c's outer
                followed = w == follow or (w in hoisted and d >= 0.5 and all(
                    self.score(o, w)[0] < d for o in V
                    if o != c and o.startswith(outer + "$") and o.count("$") == c.count("$") and o not in self.cm))
                # a class split into an abstract parent and a thin concrete child of the old name (26.2:
                # Slime -> AbstractCubeMob + cubemob/Slime) is the child: instances are of it, and every
                # old member still resolves on it through the parent
                # (classes only: an interface split in two -- 1.21.11 BlockAndTintGetter, which Level
                # implemented, is 26.1's common BlockAndLightGetter plus a client-only BlockAndTintGetter
                # -- has no one successor; the bodies' votes say so by splitting)
                child = next((s for s in same_named if s != w and w in self.wsupers(s)
                              and self.coverage(c, s) >= self.coverage(c, w)), None) \
                    if d >= 0.6 and d - second >= 0.1 and not k.access & bytecode.ACC_INTERFACE else None
                if child is not None:
                    parent = f"; split: the members live on its parent {w} (dice {d:.2f})"
                    w = child
                    d, m, t, nw = self.score(c, w)
                    nv = cvote.get(c, {}).get(w, 0)
                same_simple = w in same_named
                unique_name = same_simple and len(same_named) == 1
                # a simple name corrected in its spelling (26.2: InstantenousMobEffect -> InstantaneousMobEffect,
                # one letter) only corroborates bytecode evidence, never decides on its own: the pair must
                # already be the best member overlap (dice >= 0.6, ahead of the next candidate) AND have
                # code saying so -- a body that is the same code (same_bodies), a body elsewhere voting for
                # the pair, or three members or more matched by name and descriptor. What the name buys is
                # only the margin rule (d - second >= 0.25) of the name-free member clause below.
                near_simple = not same_simple and spelling_fix(simple(c), simple(w))
                near_evidence = near_simple and d >= 0.6 and second < d and (
                    m >= 3 or nv >= 1 or self.same_bodies(c, w) >= 1)
                # A move is a claim that the class IS the other one (every reference follows it), so a
                # wrong one merges two types: the evidence must be specific. Bodies that are the same
                # code naming it (votes) are; members are when there are enough of them and nothing
                # else answers as well; the simple name only corroborates. Votes for another class
                # veto members.
                vetoed = votes_all > 0 and 2 * nv < votes_all
                # members alone (no same name, no same code, no votes) with a W class that has fewer
                # members than V's: a part of it, not the class (26.1.2's CriterionTrigger$Listener record
                # (trigger, advancement, criterion) and 26.2's TriggerInstanceKey (advancement, criterion))
                if not same_simple and not votes_all and child is None and nw < t and m < 0.6 * t:
                    vetoed = True
                # two W classes answer c's members alike (1.21.11 WaterlilyBlock: 26.1's LilyPadBlock and
                # NetherRootsBlock, both a plant block with one SHAPE and a CODEC): the code tells them
                # apart -- the bodies (the class initializer's VoxelShape boxes included) that are the
                # same code, constants equal
                # (not where the bodies' votes split between classes: 1.21.11's BlockAndTintGetter went
                # half to BlockAndLightGetter, half to the client BlockAndTintGetter -- no one successor)
                by_code = False
                if d >= 0.6 and m >= 2 and len(scored) > 1 and d - second < 0.25 and (votes_all == 0 or unanimous) \
                        and (same_simple or not same_named):
                    mine = self.same_bodies(c, w)
                    best_other = max(self.same_bodies(c, o[2]) for o in scored[1:4])
                    by_code = mine >= 2 and mine > best_other
                if not vetoed and (
                        by_code or
                        child is not None or
                        (unanimous and nv >= 2 and d >= 0.25 and second < d) or
                        (same_simple and d >= 0.4 and m >= 1 and second < d) or
                        near_evidence or
                        (unique_name and m >= 1 and (d >= 0.25 or unanimous)) or
                        (d >= 0.6 and m >= 3 and d - second >= 0.25) or
                        (followed and (d >= 0.3 or t + nw == 0 or unanimous))):
                    self.cm[c] = w
                    self.claimed[w] = c
                    self.ren_basis["class " + c] = (f"members {m}/{t} vs {nw} (dice {d:.2f}, 2nd {second:.2f}), "
                                                   f"body votes {nv}/{votes_all}" + (", follows its outer class" if followed else "")
                                                   + (", simple name a spelling correction" if near_simple else "")
                                                   + (parent if child is not None else ""))
                    changed += 1
                else:
                    self.ren_basis["class? " + c] = (f"best {w}: dice {d:.2f} (members {m}/{t} vs {nw}), "
                                                    f"2nd {second:.2f}, votes {nv}/{votes_all}")
            log(f"round {rnd}: {changed} decisions; classes {len(self.cm)}/{len(V)}, member renames {len(self.ren)}")
            if not changed and rnd >= 1:
                break
        self.match_synthetics(log)


def spelling_fix(a, b):
    """two simple names one or two letters apart (inserted, dropped or changed), both long enough that this says
    something: a corrected spelling, not another word"""
    if a == b or min(len(a), len(b)) < 8 or abs(len(a) - len(b)) > 2:
        return False
    prev = list(range(len(b) + 1))
    for i, x in enumerate(a, 1):
        cur = [i]
        for j, y in enumerate(b, 1):
            cur.append(min(prev[j] + 1, cur[j - 1] + 1, prev[j - 1] + (x != y)))
        prev = cur
    return prev[-1] <= 2


def simple(c):
    return c[c.rfind("/") + 1:]


def unify_desc(tokens, wdesc, votes):
    """a translated V descriptor (token list) against a W descriptor, Wild classes unifying"""
    i = 0
    for t in tokens:
        if isinstance(t, str):
            if not wdesc.startswith(t, i):
                return False
            i += len(t)
        else:
            if i >= len(wdesc) or wdesc[i] != "L":
                return False
            j = wdesc.index(";", i)
            wc = wdesc[i + 1:j]
            cls = t[1]
            if isinstance(cls, Wild):
                votes.append(("C", cls.v, wc))
            elif cls != wc:
                return False
            i = j + 1
    return i == len(wdesc)


def read_table(path, full=False):
    c, m = {}, {}
    kinds = {}
    for line in open(path, encoding="utf-8"):
        f = line.rstrip("\n").split("\t")
        if len(f) < 3 or line.startswith("#"):
            continue
        if f[0] == "C":
            c[f[1]] = f[2]
        elif f[0] in ("M", "F"):
            m[f[1]] = f[2] if full else f[2][f[2].rindex(".") + 1:]
            kinds[f[1]] = f[0]
    return c, m, kinds


def main():
    args = sys.argv[1:]
    identity = "--identity" in args
    if identity:
        args.remove("--identity")
    extra = None
    if "--extra" in args:
        k = args.index("--extra")
        extra = args[k + 1]
        args = args[:k] + args[k + 2:]
    fragment, step = None, None
    if "--fragment" in args:
        k = args.index("--fragment")
        fragment, step = args[k + 1], args[k + 2]
        args = args[:k] + args[k + 3:]
    if identity:
        vjar, wjar, out, report = args
        base_c, base_m, kinds = {}, {}, {}
    else:
        base, vjar, wjar, out, report = args
        base_c, base_m, kinds = read_table(base)
    V, W = load(vjar), load(wjar)
    print(f"V {len(V)} classes, W {len(W)} classes", flush=True)
    t = Table(V, W, base_c, base_m, identity)
    t.run(lambda s: print(s, flush=True))

    rows, rep = [], []
    if identity:
        for c in sorted(V):
            w = t.cm.get(c)
            if w and w != c:
                rows.append(("C", c, w))
        for key in sorted(t.ren):
            owner, name = key.rsplit(".", 1)
            rows.append(("M" if any(m.name == name for m in V[owner].methods) else "F", key, t.cm.get(owner, owner) + "." + t.ren[key]))
    else:
        # every base row, its target translated; the classes and members the base lacks are left alone
        def wname_of(c):
            if c in t.cm:
                return t.cm[c]
            d = c.rfind("$")
            if d > 0 and c[:d] in t.cm and c not in V:
                return t.cm[c[:d]] + c[d:]
            return base_c.get(c, c)
        for c in base_c:
            rows.append(("C", c, wname_of(c)))
        for key, n in base_m.items():
            owner, name = key.rsplit(".", 1)
            nn = t.ren.get(name, n) if TOKEN.match(name) else n
            rows.append((kinds[key], key, wname_of(owner) + "." + nn))
        if extra:
            ec, em, ek = read_table(extra, full=True)
            vby = {v: k for k, v in base_c.items()}
            for k, v in ec.items():
                if k in base_c:
                    rep.append(("extra-dropped", k, v, base_c[k], "the base table decides this key"))
                    continue
                # the extra row names the class by its 1.21.11 name: that V class's target name
                src = vby.get(v, v)
                tgt = t.cm.get(src, v if v in W else None)
                if tgt is None:
                    rep.append(("extra-gone", k, v, "", "no target class"))
                    tgt = v
                rows.append(("C", k, tgt))
            for k, v in em.items():
                if k in base_m:
                    continue
                owner, _ = k.rsplit(".", 1)
                vo = v[:v.rindex(".")]
                src = vby.get(vo, vo)
                rows.append((ek[k], k, t.cm.get(src, vo) + v[v.rindex("."):]))
    with open(out, "w", encoding="utf-8", newline="\n") as o:
        for r in rows:
            o.write("\t".join(r) + "\n")

    for c in sorted(V):
        b = t.base_name(c)
        w = t.cm.get(c)
        if c in t.collisions:
            rep.append(("class-collision", c, b, t.collisions[c][0], t.collisions[c][1]))
        if c in t.weak:
            w0, d0, m0, t0, nw0 = t.weak[c]
            rep.append(("class-reworked", c, b, w0, f"kept in place: members {m0}/{t0} vs {nw0} (dice {d0:.2f}), no better home"))
        if w is None:
            rep.append(("class-gone" if "class? " + c not in t.ren_basis else "class-undecided", c, b, "",
                        t.ren_basis.get("class? " + c, "")))
        elif w != b:
            rep.append(("class-moved", c, b, w, t.ren_basis.get("class " + c, "")))
    for key in sorted(t.ren_basis):
        if key.startswith("class"):
            continue
        rep.append(("member-renamed" if key in t.ren else "member-undecided", key, "", t.ren.get(key, ""), t.ren_basis[key]))
    # every same-name collision: applied (the row now names the member that is the same code), not
    # applied (the rename lost to a conflict: see member-undecided), or ambiguous (listed, not guessed)
    for (c, vname), (kind, n, tgt, why) in sorted(t.reuse.items()):
        key = (c + "." + vname) if identity else vname
        applied = t.ren.get(key) == tgt[:tgt.index("(")]
        rep.append(("member-collision" if applied else "member-collision-unapplied", c + "." + vname, n, tgt,
                    why + ("" if applied else "; not applied: " + t.ren_basis.get(key, "no decision"))))
    for (c, vname), (kind, n, cands, why) in sorted(t.reuse_amb.items()):
        rep.append(("member-collision-ambiguous", c + "." + vname, n, cands, f"{kind}: {why}"))
    with open(report, "w", encoding="utf-8", newline="\n") as o:
        o.write("kind\tv\tbase\ttarget\tevidence\n")
        for r in rep:
            o.write("\t".join(r) + "\n")
    cnt = collections.Counter(r[0] for r in rep)
    print(f"-> {out} ({len(rows)} rows); report {report}: " + ", ".join(f"{k} {v}" for k, v in sorted(cnt.items())))
    if identity and fragment:
        write_fragment(t, V, W, fragment, step)


def write_fragment(t, V, W, path, step):
    """classRenames for a step between two mojang-named releases (26.1.2 -> 26.2): every class the run
    found moved, V name -> W name, after a member-by-member check -- each public or protected member
    of V's class looked up on W's (its own and inherited members, descriptors translated through the
    moves): answered as it is, answered under a renamed name (a member rename the step still has to
    wire: classRenames move the class, not its members), or not answered (the step's damage on the
    moved class). The check goes into the fragment's "_check" as counts, the missing members listed.

    The member renames the run found (t.ren, moved classes or not) are wired too, each V member whose
    renamed W member answers with the translated descriptor: methodRenames ("owner.name(desc)" -> W name)
    and fieldRenames ("owner.name:desc" -> W name), so calls, super calls, method references and field
    accesses follow; and for an overridable method (not static, final, private or a constructor, of a
    class that is neither final nor a record) an overrideRenames entry under the V class with super calls
    renamed, so a mod's override is what W's callers of the new name reach."""
    renames, check = {}, {}
    mren, fren, oren, diverged, type_gone = {}, {}, [], [], []
    for key in sorted(t.ren):
        c, name = key.rsplit(".", 1)
        if c not in V:
            continue
        w = t.cm.get(c, c)
        n = t.ren[key]
        klass = V[c]
        for m in klass.methods + klass.fields:
            if m.name != name or m.access & (bytecode.ACC_SYNTHETIC | bytecode.ACC_PRIVATE):
                continue
            kind = "M" if isinstance(m, bytecode.Meth) else "F"
            toks = t.tdesc(m.desc)
            if any(not isinstance(x, str) and isinstance(x[1], Wild) for x in toks):
                # the descriptor names a V class W has no decided counterpart for (gone or undecided): the
                # renamed member would keep a type W lacks (26.2 SubmitNodeStorage.submitParticleGroup(
                # ParticleGroupRenderer) -> submitQuadParticleGroup(QuadParticleRenderState)), so the rename
                # is not one this table can state; left for the step's own wiring
                type_gone.append(f"{c}.{name}{m.desc if kind == 'M' else ':' + m.desc} -> {n}")
                continue
            if not t.w_has(w, kind, n, toks, []):
                continue
            if kind == "F":
                fren[f"{c}.{name}:{m.desc}"] = n
                continue
            # a rename found from the bodies around the member (it is named where a kept body names it)
            # says where the member went, not that it still does the same: a method whose own body on W
            # is not the same code is left for the step to wire (26.2's SpeleothemBlock.isFreeHangingStalactite
            # tests the #speleothems tag where 26.1.2's PointedDripstoneBlock.canDrip tested the block)
            wm = t.w_method(w, n, t.tdesc(m.desc))
            if m.insns is not None and wm is not None and wm.insns is not None and not t.same_code(m, wm)[0]:
                diverged.append(f"{c}.{name}{m.desc} -> {n}")
                continue
            mren[f"{c}.{name}{m.desc}"] = n
            if not (m.access & (bytecode.ACC_STATIC | bytecode.ACC_FINAL)) and name != "<init>" and \
                    not klass.access & bytecode.ACC_FINAL and klass.super != "java/lang/Record":
                oren.append({"old": name + m.desc, "new": n, "onlyUnder": c, "superCalls": "rename",
                             "basis": f"wall_table.py --identity: {c}.{name} is {w}.{n} on W ({t.ren_basis.get(key, '')})"})
    for c in sorted(t.cm):
        w = t.cm[c]
        if w == c:
            continue
        kept, renamed, missing = 0, [], []
        for m in V[c].methods + V[c].fields:
            if m.access & (bytecode.ACC_SYNTHETIC | bytecode.ACC_PRIVATE) or m.name in ("<clinit>",):
                continue
            kind = "M" if isinstance(m, bytecode.Meth) else "F"
            dt = t.tdesc(m.desc)
            n = t.mname(c, m.name)
            if t.w_has(w, kind, n, dt, []):
                if n == m.name:
                    kept += 1
                else:
                    renamed.append(f"{m.name}{m.desc if kind == 'M' else ':' + m.desc} -> {n}")
            else:
                missing.append(m.name + (m.desc if kind == "M" else ":" + m.desc))
        renames[c] = w
        check[c] = {"to": w, "kept": kept}
        if renamed:
            check[c]["renamed"] = renamed
        if missing:
            check[c]["missing"] = missing
        check[c]["basis"] = t.ren_basis.get("class " + c, "")
    out = {
        "_pkgmoves_generated": f"core/tools/wall_table.py --identity ... --fragment <this> {step}: the classes this "
                               "release moved or renamed, each found by member overlap and same-code bodies; "
                               "regenerate, never hand-edit",
        "classRenames": renames,
        "methodRenames": mren,
        "fieldRenames": fren,
        "overrideRenames": oren,
        "_pkgmoves_check": check,
        "_pkgmoves_member_renames": {k: t.ren[k] for k in sorted(t.ren)},
        "_pkgmoves_renamed_not_same_code": diverged,
        "_pkgmoves_renamed_type_gone": type_gone,
    }
    with open(path, "w", encoding="utf-8", newline="\n") as o:
        json.dump(out, o, indent=1, ensure_ascii=False)
        o.write("\n")
    n_miss = sum(1 for v in check.values() if "missing" in v)
    n_ren = sum(1 for v in check.values() if "renamed" in v)
    print(f"fragment -> {path}: {len(renames)} classRenames, {len(mren)} methodRenames, {len(fren)} fieldRenames, {len(oren)} overrideRenames, {len(diverged)} renamed members not the same code, {len(type_gone)} with a type W lacks ({n_miss} with members the target lacks, "
          f"{n_ren} with renamed members)")


if __name__ == "__main__":
    main()
