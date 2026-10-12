"""Classes whose intermediary name stayed while the class behind it changed: where to look first.

Fabric's intermediary follows a class or member through releases by matching, and the match is usually
right -- which is what lets the step tools pair V's member with W's by name. When a release restructures
classes, the name can land on another class (1.20.1's class_7566 is ChatReportBuilder, 1.20.2's is
ChatReport; the builder became class_7566$class_8772). Then everything that pairs by name -- the
generated mechanical bridges, an override hook's owner -- pairs two unrelated things.

For every class both V and W name, this compares what the class declares: the share of V's methods and
fields (by intermediary name) W still declares there, and whether another W class declares most of what
this one lost (where it went). A low share with a better home elsewhere is listed as a suspected move;
the listing is a lead for a person, not a verdict.

  python class_moves.py <V stub jar> <W stub jar> [<min members>]
"""
import collections
import os
import re
import sys
import zipfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import classfile  # noqa: E402

MEMBER = re.compile(r"^(method|field|comp)_\d+$")


def load(jar):
    out = {}
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if n.endswith(".class") and n.startswith("net/minecraft/"):
                c = classfile.read(z.read(n))
                if c:
                    out[c.name] = c
    return out


def members(c):
    return {m.name for m in c.methods if MEMBER.match(m.name)} | {f[0] for f in c.fields if MEMBER.match(f[0])}


def main():
    V, W = load(sys.argv[1]), load(sys.argv[2])
    floor = int(sys.argv[3]) if len(sys.argv) > 3 else 4
    home = collections.defaultdict(set)  # member name -> W classes declaring it
    for c in W.values():
        for m in members(c):
            home[m].add(c.name)
    rows = []
    for name, vc in V.items():
        wc = W.get(name)
        vm = members(vc)
        if wc is None or len(vm) < floor:
            continue
        kept = vm & members(wc)
        share = len(kept) / len(vm)
        if share >= 0.5:
            continue
        lost = vm - kept
        where = collections.Counter(h for m in lost for h in home.get(m, ()) if h != name)
        best, n = where.most_common(1)[0] if where else (None, 0)
        if best and n / len(vm) > share:
            rows.append((share, name, len(vm), best, n))
    for share, name, total, best, n in sorted(rows):
        print(f"{name}\tkept {share:.0%} of {total}\tmost of the rest on {best} ({n})")
    print(f"{len(rows)} suspected moves", file=sys.stderr)


if __name__ == "__main__":
    main()
