"""Generate a year-era segment (1.21.11 -> 26.1, 26.1.2 -> 26.2) in intermediary names.

The year-versioned era ships mojang names, so this boundary is a rename plus
drift. Chain resolution still runs on intermediary-named mods, so the segment
keeps intermediary names in its gone lists: an intermediary symbol is gone iff
NONE of its composed mojang placements survive in the target dump (overrides
appear once per declaring class; alive-anywhere means call sites can resolve
via the hierarchy after the rename stage).

  python3 gen_frontier_segment.py <inter-to-mojang.tsv> <target-dump> <out.json>
        [--from 1.21.11 --to 26.1] [--minus <earlier-segment.json>] [--alt <table.tsv> ...]

  --minus : subtract what an earlier year-era segment already recorded, so the
            26.1.2 -> 26.2 segment carries only deaths past 26.1
  --alt   : more placements for the same intermediary symbols. Mojang moved
            ~300 classes between 1.21.11 and 26.2 (pkgmoves.py); the shipped
            table spells the 26.2 names, so measuring 26.1 with it alone would
            bury every class that moved only at 26.2. A symbol is alive when
            ANY table's placement is in the dump -- feed the 1.21.11-named
            compose table as --alt when measuring against 26.1.
"""
import argparse
import io
import json
from collections import defaultdict


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("tsv")
    ap.add_argument("dump")
    ap.add_argument("out")
    ap.add_argument("--from", dest="ver_from", default="1.21.11")
    ap.add_argument("--to", dest="ver_to", default="26.1")
    ap.add_argument("--minus")
    ap.add_argument("--alt", nargs="*", default=[])
    a = ap.parse_args()

    target_cls = set()
    target_members = set()  # "owner.name" mojang internal
    for ln in io.open(a.dump, encoding="utf-8"):
        if ln.startswith("C "):
            target_cls.add(ln[2:].strip())
        elif ln.startswith(("M ", "F ")):
            target_members.add(ln[2:].strip().split("(")[0].split(":")[0])

    c_alive = defaultdict(bool)
    c_seen = set()
    # intermediary member name -> [alive?] across all placements
    m_alive = defaultdict(bool)
    f_alive = defaultdict(bool)
    m_seen = set()
    f_seen = set()
    for tsv in [a.tsv] + a.alt:
        for ln in io.open(tsv, encoding="utf-8"):
            p = ln.rstrip("\n").split("\t")
            if p[0] == "C":
                c_seen.add(p[1])
                if p[2] in target_cls:
                    c_alive[p[1]] = True
            elif p[0] in ("M", "F"):
                name = p[1].split(".")[1]
                seen, alive = (m_seen, m_alive) if p[0] == "M" else (f_seen, f_alive)
                seen.add(name)
                if p[2] in target_members:
                    alive[name] = True

    seg = {
        "from": a.ver_from,
        "to": a.ver_to,
        "namespace": "intermediary",
        "classesGone": sorted(c for c in c_seen if not c_alive[c]),
        "methodsGone": sorted(n for n in m_seen if not m_alive[n]),
        "methodsDescChanged": [],
        "fieldsGone": sorted(n for n in f_seen if not f_alive[n]),
        "fieldsDescChanged": [],
        "depRenames": {},
    }
    if a.minus:
        prev = json.load(io.open(a.minus, encoding="utf-8"))
        for k in ("classesGone", "methodsGone", "fieldsGone"):
            done = set(prev.get(k, []))
            seg[k] = [x for x in seg[k] if x not in done]
    json.dump(seg, io.open(a.out, "w", encoding="utf-8", newline="\n"), indent=2)
    print(f"segment {a.ver_from} -> {a.ver_to}: classes gone {len(seg['classesGone'])} / seen {len(c_seen)}, "
          f"methods gone {len(seg['methodsGone'])} / seen {len(m_seen)}, "
          f"fields gone {len(seg['fieldsGone'])} / seen {len(f_seen)} -> {a.out}")


if __name__ == "__main__":
    main()
