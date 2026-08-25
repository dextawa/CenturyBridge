"""Generate the frontier segment (1.21.11 -> 26.1) in intermediary names.

The year-versioned era ships mojang names, so this boundary is a rename plus
drift. Chain resolution still runs on intermediary-named mods, so the segment
keeps intermediary names in its gone lists: an intermediary symbol is gone iff
NONE of its composed mojang placements survive in the target dump (overrides
appear once per declaring class; alive-anywhere means call sites can resolve
via the hierarchy after the rename stage).

  python3 gen_frontier_segment.py <inter-to-mojang.tsv> <target-dump> <out.json>
"""
import io
import json
import sys
from collections import defaultdict


def main():
    tsv, dump, out = sys.argv[1], sys.argv[2], sys.argv[3]

    target_cls = set()
    target_members = set()  # "owner.name" mojang internal
    for ln in io.open(dump, encoding="utf-8"):
        if ln.startswith("C "):
            target_cls.add(ln[2:].strip())
        elif ln.startswith(("M ", "F ")):
            target_members.add(ln[2:].strip().split("(")[0].split(":")[0])

    cls_gone = []
    # intermediary member name -> [alive?] across all placements
    m_alive = defaultdict(bool)
    f_alive = defaultdict(bool)
    m_seen = set()
    f_seen = set()
    for ln in io.open(tsv, encoding="utf-8"):
        p = ln.rstrip("\n").split("\t")
        if p[0] == "C":
            if p[2] not in target_cls:
                cls_gone.append(p[1])
        elif p[0] in ("M", "F"):
            name = p[1].split(".")[1]
            seen, alive = (m_seen, m_alive) if p[0] == "M" else (f_seen, f_alive)
            seen.add(name)
            if p[2] in target_members:
                alive[name] = True

    seg = {
        "from": "1.21.11",
        "to": "26.1",
        "namespace": "intermediary",
        "classesGone": sorted(set(cls_gone)),
        "methodsGone": sorted(n for n in m_seen if not m_alive[n]),
        "methodsDescChanged": [],
        "fieldsGone": sorted(n for n in f_seen if not f_alive[n]),
        "fieldsDescChanged": [],
        "depRenames": {},
    }
    json.dump(seg, io.open(out, "w", encoding="utf-8", newline="\n"), indent=2)
    print(f"frontier segment: classes gone {len(seg['classesGone'])}, "
          f"methods gone {len(seg['methodsGone'])} / seen {len(m_seen)}, "
          f"fields gone {len(seg['fieldsGone'])} / seen {len(f_seen)} -> {out}")


if __name__ == "__main__":
    main()
