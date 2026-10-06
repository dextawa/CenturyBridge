"""Package-move map across the year era, and the 26.2-targeted wall table.

Between 1.21.11 and 26.2 Mojang MOVED a few hundred classes to other packages
without changing them (advancements/criterion/* -> predicates/* + triggers/*,
CriterionTrigger, ...). The composed wall table (compose_mojang.py) spells
1.21.11 mojang names, so a converted jar still emitted the old package and the
year-era segments recorded every such class as dead: NoClassDefFoundError at
runtime, wrong "gone" ledger entries, dangling wiring at the 26.2 build gate.

This tool matches every 1.21.11 class that is missing by full name in the
target jar against the target's classes (same simple name, then member-set
overlap; nested classes follow their outer), classifies each as moved /
renamed / gone, and rewrites the wall table so C rows (and the owners of M/F
rows) point straight at the target's names. Identity is judged by javac-level
facts only (members, supertypes, nesting) -- no name heuristics beyond the
simple name.

  python3 pkgmoves.py <old-mojang-jar> <target-jar> <table-in.tsv> \
        --map <pkgmoves.tsv> --table-out <table-out.tsv> --moves <moves-C.tsv>

  old-mojang-jar : 1.21.11 stub renamed by the (fixed) compose table
                   (remap-mojang <table-in> client-1.21.11-intermediary.jar ...)
  target-jar     : data/jars/client-26.2.jar (unobfuscated)
  table-in       : compose_mojang.py output (1.21.11 names)
  --map          : kind / old / new / confidence / evidence, one row per missing class
  --table-out    : the wall table with targets rewritten to the target's names
  --moves        : C-only table (old mojang -> new mojang) for remap-mojang on a
                   26.1.x jar, so stub-diff sees moved classes as survivors

Recipe for regenerating the wall (java steps are one process at a time):
  1. python3 core/tools/compose_mojang.py 1.21.11 data/jars/client-26.1.jar.dump.txt core/out-wall/table-fixed.tsv
  2. java ... Main remap-mojang core/out-wall/table-fixed.tsv data/jars/client-1.21.11-intermediary.jar core/out-wall/client-1.21.11-mojang.jar
  3. python3 core/tools/pkgmoves.py core/out-wall/client-1.21.11-mojang.jar data/jars/client-26.2.jar core/out-wall/table-fixed.tsv \
        --map core/out-diff/pkgmoves-26.2.tsv --table-out core/out-wall/inter-to-mojang-1.21.11.tsv --moves core/out-wall/pkgmoves-C.tsv
  4. java ... Main remap-mojang core/out-wall/pkgmoves-C.tsv data/jars/client-26.1.2.jar core/out-wall/client-26.1.2-moved.jar
     java ... Main stub-diff core/out-wall/client-26.1.2-moved.jar data/jars/client-26.2.jar core/out-diff/diff-26.1.2-26.2.tsv
     java ... Main stub-diff core/out-wall/client-1.21.11-mojang.jar data/jars/client-26.1.jar core/out-diff/diff-1.21.11-26.1.tsv
  5. python3 core/tools/gen_frontier_segment.py core/out-wall/table-fixed.tsv data/jars/client-26.1.jar.dump.txt core/segments/1.21.11__26.1.json
     python3 core/tools/augment_frontier_segment.py core/out-wall/table-fixed.tsv core/out-diff/diff-1.21.11-26.1.tsv core/segments/1.21.11__26.1.json
     python3 core/tools/gen_frontier_segment.py core/out-wall/inter-to-mojang-1.21.11.tsv data/jars/client-26.2.jar.dump.txt core/segments/26.1.2__26.2.json \
        --from 26.1.2 --to 26.2 --minus core/segments/1.21.11__26.1.json
     python3 core/tools/augment_frontier_segment.py core/out-wall/inter-to-mojang-1.21.11.tsv core/out-diff/diff-26.1.2-26.2.tsv core/segments/26.1.2__26.2.json
  6. DEPLOY the rewritten table to where the converter / build gates read it
     (Main.convert and the gates hard-code data/mappings/inter-to-mojang-1.21.11.tsv).
     This step is mandatory and easy to forget -- skipping it leaves the
     converter remapping surviving classes to their OLD package names, which
     exist nowhere on 26.2: every such ref dangles at runtime (NoClassDefFound /
     link-check BLIND CLASS) and SAM facades silently fail to match (CLAIMED
     LAMBDA). cp -- do not merge:
       cp core/out-wall/inter-to-mojang-1.21.11.tsv data/mappings/inter-to-mojang-1.21.11.tsv
  The 1.21.11 -> 26.1 segment is measured with the 1.21.11-named table (26.1
  still spells the old packages); only the 26.2-targeted table goes into
  data/mappings/ for the converter, the build gates and the 26.2 segment.
"""
import argparse
import io
import re
import struct
import sys
import zipfile
from collections import defaultdict


# ---------------------------------------------------------------- class files
def parse_class(data):
    """(name, access, super, ifaces, {field (name, desc)}, {method (name, desc)})"""
    if data[:4] != b"\xca\xfe\xba\xbe":
        return None
    n = struct.unpack(">H", data[8:10])[0]
    consts = [None] * n
    i, p = 1, 10
    while i < n:
        tag = data[p]
        if tag == 1:
            ln = struct.unpack(">H", data[p + 1:p + 3])[0]
            consts[i] = ("utf8", data[p + 3:p + 3 + ln].decode("utf-8", "replace"))
            p += 3 + ln
        elif tag in (3, 4):
            p += 5
        elif tag in (5, 6):
            p += 9
            i += 1
        elif tag == 7:
            consts[i] = ("class", struct.unpack(">H", data[p + 1:p + 3])[0])
            p += 3
        elif tag == 8:
            p += 3
        elif tag in (9, 10, 11, 12):
            p += 5
        elif tag == 15:
            p += 4
        elif tag == 16:
            p += 3
        elif tag in (17, 18):
            p += 5
        elif tag in (19, 20):
            p += 3
        else:
            return None
        i += 1

    def u(idx):
        c = consts[idx]
        return c[1] if c and c[0] == "utf8" else None

    def cls(idx):
        c = consts[idx]
        return u(c[1]) if c and c[0] == "class" else None

    acc, this_c, super_c = struct.unpack(">HHH", data[p:p + 6])
    p += 6
    name = cls(this_c)
    sup = cls(super_c) if super_c else None
    ic = struct.unpack(">H", data[p:p + 2])[0]
    p += 2
    ifaces = [cls(struct.unpack(">H", data[p + 2 * k:p + 2 * k + 2])[0]) for k in range(ic)]
    p += 2 * ic

    def members():
        nonlocal p
        cnt = struct.unpack(">H", data[p:p + 2])[0]
        p += 2
        out = set()
        for _ in range(cnt):
            a, nm, ds, ac = struct.unpack(">HHHH", data[p:p + 8])
            p += 8
            if not (a & 0x1000):  # synthetic members are compiler noise
                out.add((u(nm) or "", u(ds) or ""))
            for _ in range(ac):
                ln = struct.unpack(">I", data[p + 2:p + 6])[0]
                p += 6 + ln
        return out

    fields = members()
    methods = members()
    return name, acc, sup, [x for x in ifaces if x], fields, methods


def index_jar(path):
    idx = {}
    z = zipfile.ZipFile(path)
    for e in z.namelist():
        if e.endswith(".class") and e.startswith(("net/minecraft/", "com/mojang/")):
            r = parse_class(z.read(e))
            if r and r[0]:
                idx[r[0]] = r
    return idx


LREF = re.compile(r"L([^;]+);")


def simple(name):
    return name[name.rfind("/") + 1:]


def norm_desc(desc, cmap):
    """descriptor with class refs mapped through cmap, else reduced to simple names"""
    return LREF.sub(lambda m: "L" + simple(cmap.get(m.group(1), m.group(1))) + ";", desc)


def shape(entry, cmap):
    """member signature set of a class, descriptors normalised"""
    _n, _a, _s, _i, fields, methods = entry
    out = set()
    for nm, ds in fields:
        out.add("F" + nm + ":" + norm_desc(ds, cmap))
    for nm, ds in methods:
        if nm != "<clinit>":
            out.add("M" + nm + norm_desc(ds, cmap))
    return out


def sim(a, b):
    if not a and not b:
        return 1.0
    return len(a & b) / max(len(a), len(b))


def outer_of(name):
    d = name.rfind("$")
    return name[:d] if d > 0 else None


ANON = re.compile(r"\$\d+$")


# ------------------------------------------------------------------- matching
def inherited_shape(name, idx, cmap, seen=None):
    """own members plus everything inherited inside the jar: a class that was
    split into an abstract parent and a thin concrete child (Slime ->
    AbstractCubeMob + cubemob/Slime) still answers every old call through
    JVM resolution, so the child IS the old type."""
    seen = seen or set()
    if name in seen or name not in idx:
        return set()
    seen.add(name)
    out = set(shape(idx[name], cmap))
    e = idx[name]
    for sup in [e[2]] + list(e[3]):
        if sup:
            out |= inherited_shape(sup, idx, cmap, seen)
    return out


def match(old, new):
    """returns ({old name: (kind, new name, confidence, evidence)} for every
    old class missing by full name in new, {old: new} for the non-gone ones)."""
    missing = sorted(c for c in old if c not in new)
    fresh = {c for c in new if c not in old}
    by_simple = defaultdict(list)
    for c in new:
        by_simple[simple(c)].append(c)

    cmap = {}
    result = {}
    claimed = {}
    pkg_moves = defaultdict(int)  # "oldpkg -> newpkg" corroboration counts
    dest_moves = defaultdict(int)  # newpkg <- anywhere: packages that received moves

    def pkg(c):
        return c.rsplit("/", 1)[0]

    def survivor(cand):
        return cand in old and cand in new

    def accept(m, c, conf, ev, kind="moved"):
        result[m] = (kind, c, conf, ev)
        cmap[m] = c
        claimed[c] = m
        if "$" not in m:
            pkg_moves[pkg(m) + " -> " + pkg(c)] += 1
            dest_moves[pkg(c)] += 1

    # pass 1: same simple name (outer-consistent for nested); three rounds so
    # descriptors normalise through the moves already found and package-level
    # corroboration can vouch for reworked classes with weak member overlap
    for rnd in range(3):
        cmap_round = dict(cmap)
        for m in missing:
            if m in result and result[m][0] != "gone":
                continue
            if m.endswith("/package-info"):
                result[m] = ("gone", "", "n/a", "package-info")
                continue
            sname = simple(m)
            outer = outer_of(m)
            cands = []
            nested_follow = False
            if outer is not None:
                o_new = cmap.get(outer, outer if outer in new else None)
                if o_new is not None and o_new + m[len(outer):] in new:
                    cands = [o_new + m[len(outer):]]
                    nested_follow = o_new != outer
                if not cands and not ANON.search(m):
                    cands = [c for c in by_simple.get(sname, []) if c not in old]
            else:
                cands = [c for c in by_simple.get(sname, []) if c not in old or survivor(c)]
            if not cands:
                result.setdefault(m, ("gone", "", "n/a", "no class with that simple name in target"))
                continue
            sm = shape(old[m], cmap_round)
            scored = []
            for c in cands:
                if c in claimed and claimed[c] != m:
                    continue
                # coverage of the OLD class's members by what the candidate answers
                # (own + inherited): a thin subclass of a new abstract parent covers
                # everything, a same-named but unrelated class covers nothing
                sc = inherited_shape(c, new, cmap_round)
                s = len(sm & sc) / len(sm) if sm else (1.0 if not shape(new[c], cmap_round) else 0.5)
                if survivor(c):
                    so = shape(old[c], cmap_round)
                    if (len(so & sc) / len(so) if so else 1.0) >= s:
                        continue  # the same-named class that stayed in place owns that body
                scored.append((s, c, len(sm & sc)))
            scored.sort(reverse=True)
            if not scored:
                result[m] = ("gone", "", "n/a", "same-name candidates all claimed / survivors")
                continue
            s, c, common = scored[0]
            second = scored[1][0] if len(scored) > 1 else 0.0
            same_super = simple(old[m][2] or "") == simple(new[c][2] or "")
            ev = f"members {common}/{len(sm)} cov={s:.2f}"
            ev += " super=" + ("same" if same_super else f"{simple(old[m][2] or '')}->{simple(new[c][2] or '')}")
            if nested_follow:
                ev += " outer-moved"
            if len(scored) > 1:
                ev += f" alt={len(scored) - 1}(best-alt {second:.2f})"
            corroborated = pkg_moves.get(pkg(m) + " -> " + pkg(c), 0) if "$" not in m else 0
            if s >= 0.5 and (second < 0.5 or s - second >= 0.25):
                accept(m, c, "high" if s >= 0.75 or nested_follow else "medium", ev)
            elif nested_follow:
                accept(m, c, "medium", ev)
            elif len(scored) == 1 and len(sm) <= 2 and same_super:
                accept(m, c, "low", ev + " tiny-unique")
            elif len(scored) == 1 and corroborated >= 3:
                # reworked in place: the class moved with its package (>=3 siblings
                # made the same move) and nothing else carries the name -- the type
                # survives, the per-member segment accounting reports the rework
                accept(m, c, "low", ev + f" package-corroborated({corroborated})")
            elif len(scored) == 1 and "$" not in m and dest_moves.get(pkg(c), 0) >= 3:
                # same for a lone class whose destination package is where its old
                # neighbours' siblings went (CriterionTrigger -> advancements/triggers)
                accept(m, c, "low", ev + f" dest-corroborated({dest_moves[pkg(c)]})")
            else:
                result[m] = ("gone", "", "n/a", f"weak match vs {c}: " + ev)

    # pass 2: renamed + moved (different simple name), only unambiguous strong
    # overlaps of the class's OWN members against unclaimed newborn classes
    unclaimed_fresh = [c for c in fresh if c not in claimed]
    fresh_shapes = {c: shape(new[c], cmap) for c in unclaimed_fresh}
    for m in missing:
        if result[m][0] != "gone" or ANON.search(m) or m.endswith("/package-info"):
            continue
        sm = shape(old[m], cmap)
        if len(sm) < 4:
            continue
        outer = outer_of(m)
        # a nested class may resurface under the outer's new name OR under a
        # supertype the outer's body was hoisted into (Slime$SlimeAttackGoal ->
        # AbstractCubeMob$CubeMobAttackGoal, with cubemob/Slime extends AbstractCubeMob)
        outer_ok = set()
        if outer is not None:
            o = cmap.get(outer, outer)
            while o in new and o not in outer_ok:
                outer_ok.add(o)
                o = new[o][2]
        scored = []
        for c, sc in fresh_shapes.items():
            if c in claimed:
                continue
            if (outer is None) != (outer_of(c) is None):
                continue
            if outer is not None and outer_of(c) not in outer_ok:
                continue
            s = sim(sm, sc)
            if s >= 0.6:
                scored.append((s, c, len(sm & sc)))
        scored.sort(reverse=True)
        if scored and (len(scored) == 1 or scored[0][0] - scored[1][0] >= 0.2):
            s, c, common = scored[0]
            ev = f"members {common}/{len(sm)}~{len(fresh_shapes[c])} sim={s:.2f} simple {simple(m)}->{simple(c)}"
            oa, na = old[m][1], new[c][1]
            if (oa ^ na) & 0x0400:
                ev += " abstract-flip"
            if (oa ^ na) & 0x0200:
                continue  # class <-> interface: not the same type for any caller
            accept(m, c, "medium" if s >= 0.75 else "low", ev, kind="renamed")
    return result, cmap


# ------------------------------------------------------------------- worklist
def worklist(a, old, new, cmap, result):
    """What the shim owner must touch by hand. The wall table now carries the
    moves, so NO classRenames are needed for moved vanilla classes -- and
    adding them would hurt: the converter skips member accounting on a
    classRenames owner and hierarchyBreak cannot judge a real-name target.
    What does go stale: classRenames whose TARGET is an old name of a moved
    class (they map pre-1.21.11 tokens onto a 1.21.11 spelling), and ledger
    entries that call a moved class or a member surviving on it dead -- a
    class-level ledger key ledgers every member issue on that class."""
    import json
    inter_of = {}  # old mojang name -> intermediary key (table-in is 1.21.11-named)
    for line in io.open(a.table_in, encoding="utf-8"):
        p = line.rstrip("
").split("	")
        if len(p) >= 3 and p[0] == "C":
            inter_of[p[2]] = p[1]
    moved_inter = {inter_of[o]: n for o, n in cmap.items() if o in inter_of}
    inh_members = {}

    def members_of(c):
        if c not in inh_members:
            inh_members[c] = {x[1:].split("(")[0].split(":")[0] for x in inherited_shape(c, new, {})}
        return inh_members[c]

    # a mojang member name for an intermediary token on a moved class, from table-in
    moj_member = {}
    for line in io.open(a.table_in, encoding="utf-8"):
        p = line.rstrip("
").split("	")
        if len(p) >= 3 and p[0] in ("M", "F"):
            moj_member[p[1]] = p[2].rsplit(".", 1)[1]

    shims = json.load(io.open(a.shims, encoding="utf-8"))
    stale = {}
    for k, v in shims.get("classRenames", {}).items():
        if v in cmap:
            stale[k] = {"was": v, "now": cmap[v]}
    voided, reword = {}, {}
    for k, reason in shims.get("ledger", {}).items():
        owner, _, member = k.partition(".")
        member = member.split("(")[0].split(":")[0]
        target = moved_inter.get(owner)
        if target is None:
            # an outer-class ledger key also covers nested classes
            d = owner.find("$")
            if d > 0 and owner[:d] in moved_inter and not member:
                target = moved_inter[owner[:d]]
            else:
                continue
        if not member:
            voided[k] = {"reason": reason, "alive": target,
                         "note": "class-level key: every member issue on the class was ledgered by it"}
            continue
        moj = moj_member.get(owner + "." + member)
        if moj is not None and moj in members_of(target):
            voided[k] = {"reason": reason, "alive": target + "." + moj}
        else:
            reword[k] = {"reason": reason, "owner_now": target,
                         "member": moj or member, "note": "member really gone; reason must not blame the class"}
    out = {
        "note": "the wall table maps these classes straight to their 26.2 names: do NOT add classRenames "
                "for them (a classRenames owner loses member accounting and hierarchy judgement)",
        "movedClasses": {k: moved_inter[k] for k in sorted(moved_inter)},
        "classRenamesStale": stale,
        "ledgerVoided": voided,
        "ledgerReword": reword,
    }
    io.open(a.worklist, "w", encoding="utf-8", newline="
").write(json.dumps(out, indent=1))
    print(f"worklist: {len(moved_inter)} moved classes (intermediary keys), classRenames stale {len(stale)}, "
          f"ledger voided {len(voided)}, ledger reword {len(reword)} -> {a.worklist}")


# ----------------------------------------------------------------------- main
def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("old_jar")
    ap.add_argument("new_jar")
    ap.add_argument("table_in")
    ap.add_argument("--map", required=True)
    ap.add_argument("--table-out")
    ap.add_argument("--moves")
    ap.add_argument("--mid-dump", help="stub dump of an intermediate target (26.1): moves whose new "
                    "name already exists there go to --moves-mid")
    ap.add_argument("--moves-mid")
    ap.add_argument("--shims", help="shims-<target>.json: emit the owner's worklist (stale classRenames "
                    "targets, ledger entries voided by a move) to --worklist")
    ap.add_argument("--worklist")
    a = ap.parse_args()

    old = index_jar(a.old_jar)
    new = index_jar(a.new_jar)
    result, cmap = match(old, new)

    kinds = defaultdict(int)
    for k in result.values():
        kinds[k[0]] += 1
    with io.open(a.map, "w", encoding="utf-8", newline="\n") as f:
        f.write("kind\told\tnew\tconfidence\tevidence\n")
        for m in sorted(result):
            k, n, c, e = result[m]
            f.write(f"{k}\t{m}\t{n}\t{c}\t{e}\n")
    print(f"old {len(old)} classes, target {len(new)}; missing by name {len(result)}: "
          + ", ".join(f"{k} {v}" for k, v in sorted(kinds.items())) + f" -> {a.map}")
    by_conf = defaultdict(int)
    for k in result.values():
        if k[0] != "gone":
            by_conf[k[0] + "/" + k[2]] += 1
    print("  confidence: " + ", ".join(f"{k} {v}" for k, v in sorted(by_conf.items())))
    pkgs = defaultdict(int)
    for m, (k, n, c, e) in result.items():
        if k != "gone":
            pkgs[m.rsplit("/", 1)[0] + " -> " + n.rsplit("/", 1)[0]] += 1
    for p, v in sorted(pkgs.items(), key=lambda x: -x[1])[:12]:
        print(f"  {v:4d}  {p}")

    if a.moves:
        with io.open(a.moves, "w", encoding="utf-8", newline="\n") as f:
            for m in sorted(cmap):
                f.write(f"C\t{m}\t{cmap[m]}\n")
        print(f"moves (C-only table): {len(cmap)} -> {a.moves}")

    if a.mid_dump and a.moves_mid:
        mid = {ln[2:].strip() for ln in io.open(a.mid_dump, encoding="utf-8") if ln.startswith("C ")}
        n = 0
        with io.open(a.moves_mid, "w", encoding="utf-8", newline="\n") as f:
            for m in sorted(cmap):
                if cmap[m] in mid and m not in mid:
                    f.write(f"C\t{m}\t{cmap[m]}\n")
                    n += 1
        print(f"moves already effective in {a.mid_dump}: {n} -> {a.moves_mid}")

    if a.shims and a.worklist:
        worklist(a, old, new, cmap, result)

    if a.table_out:
        n_c = n_m = n_f = 0
        moved_members = 0
        new_members = defaultdict(set)
        for c, e in new.items():
            for nm, _d in e[4]:
                new_members[c].add(nm)
            for nm, _d in e[5]:
                new_members[c].add(nm)
        with io.open(a.table_in, encoding="utf-8") as src, \
                io.open(a.table_out, "w", encoding="utf-8", newline="\n") as dst:
            for line in src:
                p = line.rstrip("\n").split("\t")
                if len(p) < 3:
                    continue
                if p[0] == "C":
                    if p[2] in cmap:
                        p[2] = cmap[p[2]]
                        n_c += 1
                else:
                    owner, name = p[2].rsplit(".", 1)
                    if owner in cmap:
                        p[2] = cmap[owner] + "." + name
                        if p[0] == "M":
                            n_m += 1
                        else:
                            n_f += 1
                        if name in new_members.get(cmap[owner], ()):
                            moved_members += 1
                dst.write("\t".join(p) + "\n")
        print(f"table: {n_c} C rows retargeted, {n_m} M + {n_f} F rows re-owned "
              f"({moved_members} of them resolve on the target) -> {a.table_out}")


if __name__ == "__main__":
    main()
