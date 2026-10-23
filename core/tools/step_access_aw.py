"""Give back, through the step layer's access widener, the access a release took away.

A damage row whose kinds say ACCESS is a V member a mod could reach (call it, read or write it)
that W still has under the same name and descriptor, only narrower: 1.20.5 made every
BlockBehaviour method protected that 1.20.4 had public (deprecated), so a mod calling
block.getShape(..) from outside the block's hierarchy is an IllegalAccessError. The reference is
resolved in W the way the JVM resolves it (the owner's class chain, then its superinterfaces);
the declaration it lands on gets the line the loader needs to make the reference legal again,
chosen as Main access-diff chooses (AwRules: "accessible" alone turns a private instance method
public final, so an overridable one gets "extendable" too; a write to a field made final needs
"mutable").

Only rows of one area (mojang owner regex), and only rows no wiring of the step handles another
way: a member the step redirects (a constant answered with V's value, a call routed to a bridge)
keeps W's access. The lines land in the layer's access widener between markers named after the
area, replacing the previous block, so areas of one step regenerate independently.

  [CB_DATA=<data dir>] python step_access_aw.py <damage.tsv> <W stub jar> <step wiring.json> <layer.accesswidener> <area tag> [<area regex>]

Across the namespace wall (the 1.21.11 -> 26.1 step) the rows are translated into W's names with the
table the step's target reads names with (core/segments/wall-tables.tsv, wall_tables.py), and the lines
name W's members, as the layer's (official-namespace) access widener does.
"""
import json
import os
import re
import sys
import zipfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import classfile  # noqa: E402
from classfile import ACC_FINAL, ACC_INTERFACE, ACC_PRIVATE, ACC_PROTECTED, ACC_PUBLIC, ACC_STATIC  # noqa: E402

ACC_ENUM, ACC_RECORD = 0x4000, 0x10000
DATA = os.environ.get("CB_DATA", "data")  # the gitignored working set (a worktree has none of its own)
NAMES = os.path.join(DATA, "mappings/inter-to-mojang-classes.tsv")
import wall_tables  # noqa: E402


def step_of(wiring_path):
    """(V, W) of the step a wiring file belongs to (core/segments/steps/<V>__<W>.json)"""
    base = os.path.basename(wiring_path)
    if "__" in base:
        v, w = base[:-5].split("__") if base.endswith(".json") else base.split("__")
        return v, w
    return None, None


class Wall:
    """V's intermediary names as the wall step's target declares them (the 1.21.11 -> 26.1 step only;
    identity elsewhere): the rows are V's, the W jar and the access widener W's"""

    def __init__(self, table):
        self.c, self.m = {}, {}
        if table:
            for line in open(table, encoding="utf-8"):
                f = line.rstrip("\n").split("\t")
                if len(f) >= 3:
                    if f[0] == "C":
                        self.c[f[1]] = f[2]
                    else:
                        self.m[f[1]] = f[2][f[2].rindex(".") + 1:]

    def cls(self, c):
        if not self.c or c in self.c:
            return self.c.get(c, c)
        d = c.rfind("$")
        return self.cls(c[:d]) + c[d:] if d > 0 else c

    def desc(self, d):
        return re.sub(r"L([^;]+);", lambda x: "L" + self.cls(x.group(1)) + ";", d) if self.c else d

    def member(self, classes_v, owner, name):
        """the target name of V's member, looked up on the owner and then up V's hierarchy"""
        if not self.m:
            return name
        seen, todo = set(), [owner]
        while todo:
            c = todo.pop(0)
            if c is None or c in seen:
                continue
            seen.add(c)
            if c + "." + name in self.m:
                return self.m[c + "." + name]
            k = classes_v.get(c)
            if k:
                todo.append(k.super)
                todo.extend(k.interfaces)
        return name


def load(jar):
    out = {}
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if n.endswith(".class"):
                c = classfile.read(z.read(n))
                if c:
                    out[c.name] = c
    return out


def wiring(path):
    """the step's wiring with its fragments merged (engine/Wiring.java, without the conflict check)"""
    root = json.load(open(path, encoding="utf-8")) if os.path.exists(path) else {}
    d = path + ".d"
    if os.path.isdir(d):
        for f in sorted(os.listdir(d)):
            if f.endswith(".json"):
                for k, v in json.load(open(os.path.join(d, f), encoding="utf-8")).items():
                    if isinstance(v, dict):
                        root.setdefault(k, {}).update(v)
                    elif isinstance(v, list):
                        root.setdefault(k, []).extend(v)
                    else:
                        root[k] = v
    return root


def mojang_names(wall_table=None):
    cm = {}
    for p in (NAMES, wall_table) if wall_table else (NAMES,):
        if os.path.exists(p):
            for line in open(p, encoding="utf-8"):
                f = line.rstrip("\n").split("\t")
                if len(f) >= 3 and f[0] == "C":
                    cm.setdefault(f[1], f[2])
    def moj(c):
        if c in cm:
            return cm[c]
        d = c.rfind("$")
        return moj(c[:d]) + c[d:] if d > 0 else c
    return moj


def resolve(classes, owner, name, desc):
    """(declaring class, access) of the member a reference through owner resolves to in W"""
    method = desc.startswith("(")
    if not method:
        seen = set()
        def field(c):
            if c in seen or c not in classes:
                return None
            seen.add(c)
            k = classes[c]
            for n, d, a, *_ in k.fields:
                if n == name and d == desc:
                    return c, a
            for i in k.interfaces:
                r = field(i)
                if r:
                    return r
            return field(k.super) if k.super else None
        return field(owner)
    ifaces = []
    c = owner
    while c in classes:
        k = classes[c]
        for m in k.methods:
            if m.name == name and m.desc == desc:
                return c, m.access
        ifaces += k.interfaces
        c = k.super
    seen = set()
    while ifaces:
        i = ifaces.pop(0)
        if i in seen or i not in classes:
            continue
        seen.add(i)
        for m in classes[i].methods:
            if m.name == name and m.desc == desc and not m.access & (ACC_STATIC | ACC_PRIVATE):
                return i, m.access
        ifaces += classes[i].interfaces
    return None


def fixed_shape(k):
    return bool(k.access & (ACC_RECORD | ACC_ENUM)) or k.super in ("java/lang/Record", "java/lang/Enum")


def lines_for(row, classes):
    usage, owner, name, desc = row[0], row[1], row[2], row[3]
    r = resolve(classes, owner, name, desc)
    if r is None:
        return [], "W does not resolve it"
    decl, acc = r
    k = classes[decl]
    at = f" {decl} {name} {desc}"
    # what V allowed this usage: a public member from anywhere, a protected one from a subclass
    was_public = not usage.endswith("-protected")
    out = []
    # the JVM checks the class a reference names before the member: a public member of a class W
    # made package-private (26.1's GlRenderPass) is an IllegalAccessError too
    oc = classes.get(owner)
    if oc is not None and not oc.access & ACC_PUBLIC:
        out.append(f"accessible class {owner}")
    if not desc.startswith("("):
        if was_public and not acc & ACC_PUBLIC or not was_public and not acc & (ACC_PUBLIC | ACC_PROTECTED):
            out.append("accessible field" + at)
        if usage.startswith("put") and acc & ACC_FINAL and not (k.access & ACC_INTERFACE and acc & ACC_STATIC):
            out.append("mutable field" + at)
        return out, None
    overridable = not acc & (ACC_FINAL | ACC_STATIC) and not name.startswith("<") \
        and not k.access & ACC_INTERFACE and not fixed_shape(k)
    narrowed = not acc & ACC_PUBLIC if was_public else not acc & (ACC_PUBLIC | ACC_PROTECTED)
    if narrowed:
        if not overridable or was_public:
            out.append("accessible method" + at)
        if overridable and (not was_public or acc & ACC_PRIVATE):
            out.append("extendable method" + at)
    return out, None


def main():
    damage, wjar, wiring_path, aw, tag = sys.argv[1:6]
    area = re.compile(sys.argv[6]) if len(sys.argv) > 6 else None
    v, w = step_of(wiring_path)
    # across the namespace wall the rows name V's intermediary members and W's jar its mojang ones:
    # translated with the table the step's target reads names with (core/segments/wall-tables.tsv)
    table = wall_tables.table_for(w, DATA) if v == "1.21.11" and w and w.startswith("26.") else None
    wall = Wall(table)
    moj = mojang_names(table)
    classes = load(wjar)
    classes_v = {}
    if table:
        vjar = os.path.join(DATA, "jars", f"client-{v}-intermediary.jar")
        classes_v = load(vjar)
    w = wiring(wiring_path)
    handled = set()
    for sec in ("fieldRedirects", "fieldGetters", "instanceFieldGetters", "staticRedirects", "instanceRedirects",
                "callReshapes", "superRedirects"):
        handled.update(w.get(sec, {}).keys())
    rows = []
    for i, line in enumerate(open(damage, encoding="utf-8")):
        if i == 0:
            continue
        r = line.rstrip("\n").split("\t")
        if "ACCESS" not in r[4].split(","):
            continue
        if area and not area.search(moj(r[1])):
            continue
        rows.append(r)
    out, skipped, unresolved = [], 0, []
    for r in rows:
        key = r[1] + "." + r[2] + (r[3] if r[3].startswith("(") else ":" + r[3])
        if key in handled:
            skipped += 1
            continue
        if table:
            r = [r[0], wall.cls(r[1]), wall.member(classes_v, r[1], r[2]), wall.desc(r[3])] + r[4:]
        ls, why = lines_for(r, classes)
        if why:
            unresolved.append(f"{r[0]} {r[1]}.{r[2]}{r[3]}: {why}")
        out += ls
    out = sorted(set(out))
    begin = f"# ---- BEGIN GENERATED ({tag}): access narrowed at this step (core/tools/step_access_aw.py) ----"
    end = f"# ---- END GENERATED ({tag}) ----"
    s = open(aw, encoding="utf-8").read()
    if begin in s:
        s = s[:s.index(begin)].rstrip("\n") + "\n" + s[s.index(end) + len(end):].lstrip("\n")
    block = [begin,
             "# members a mod of the previous release could reach that this release narrowed: the same",
             "# members, given back so a converted mod's call, read or write links again"] + out + [end]
    s = s.rstrip("\n") + "\n\n" + "\n".join(block) + "\n"
    open(aw, "w", encoding="utf-8", newline="\n").write(s)
    print(f"{tag}: {len(rows)} ACCESS rows, {skipped} wired another way, {len(out)} lines -> {aw}")
    for u in unresolved:
        print("  unresolved:", u)


if __name__ == "__main__":
    main()
