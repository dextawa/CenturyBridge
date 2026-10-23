"""Which namespace-wall rename table a conversion to a target release uses.

The rule is core/segments/wall-tables.tsv ("target TAB table"), the same file the engine reads
(engine/WallTable.java): the climb's wall step (target 26.1) and every 26.1.x target take the 26.1-named
table, the old anchor jump to 26.2 the 26.2-named one. A table is a file name under <data>/mappings, or
a path (absolute, or relative to the working directory). Tools import table_for(); shell scripts print
the path:

  python3 wall_tables.py <target> [<data dir>]      (data dir default: data)

The rules file is looked for next to this script (core/tools/../segments) and then under the working
directory (core/segments: beta2's ~/cbwork keeps its helper scripts in probe/). CB_WALL_TABLE=<path>
answers that path for every target: a candidate table measured before it is deployed under data/mappings.
"""
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))


def rules_file():
    for p in (os.path.join(HERE, "..", "segments", "wall-tables.tsv"), os.path.join("core", "segments", "wall-tables.tsv")):
        if os.path.exists(p):
            return p
    raise SystemExit("wall-tables.tsv not found (core/segments/wall-tables.tsv)")


def table_for(target, data="data"):
    if os.environ.get("CB_WALL_TABLE"):
        return os.environ["CB_WALL_TABLE"]
    rules = rules_file()
    for line in open(rules, encoding="utf-8"):
        if not line.strip() or line.startswith("#"):
            continue
        f = line.rstrip("\n").split("\t")
        if len(f) >= 2 and f[0] == target:
            t = f[1].strip()
            return t if "/" in t else os.path.join(data, "mappings", t).replace(os.sep, "/")
    raise SystemExit(f"{rules} names no wall table for target {target}")


if __name__ == "__main__":
    print(table_for(sys.argv[1], sys.argv[2] if len(sys.argv) > 2 else "data"))
