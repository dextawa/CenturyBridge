"""Compose intermediary -> mojang mappings at one version, then measure the
namespace wall against a later unobfuscated jar.

The year-versioned era (26.1+) ships with real Mojang names and no
intermediary, so the 1.21.11 -> 26.1 segment is a whole-namespace rename plus
ordinary API drift. This tool builds the rename table and the drift lists:

  inputs:  mojang-<ver>.txt        (proguard log: mojang -> obf)
           intermediary-<ver>.tiny (tiny v2: obf -> intermediary)
           stub dump of the target jar (StubDump text, mojang names)
  outputs: inter-to-mojang-<ver>.tsv   C/M/F  <intermediary>  <mojang>
           wall stats on stdout

  python3 compose_mojang.py <ver> <target-stub-dump> <out-tsv>
"""
import io
import re
import sys
from collections import defaultdict

PRIM = {"void": "V", "boolean": "Z", "byte": "B", "short": "S", "char": "C",
        "int": "I", "long": "J", "float": "F", "double": "D"}


def jvm_type(java_type, cls_to_obf):
    dim = java_type.count("[]")
    base = java_type.replace("[]", "")
    if base in PRIM:
        d = PRIM[base]
    else:
        internal = cls_to_obf.get(base, base).replace(".", "/")
        d = "L" + internal + ";"
    return "[" * dim + d


def main():
    ver, target_dump, out_tsv = sys.argv[1], sys.argv[2], sys.argv[3]

    # ---- proguard log: mojang class -> obf class, then members ----
    moj_cls_to_obf = {}
    sections = []  # (mojang_class, obf_class, [member lines])
    cur = None
    for ln in io.open(f"data/mappings/mojang-{ver}.txt", encoding="utf-8"):
        if ln.startswith("#"):
            continue
        if not ln.startswith(" "):
            m = re.match(r"(\S+) -> (\S+):", ln.strip())
            if m:
                moj_cls_to_obf[m.group(1)] = m.group(2)
                # a class proguard left unobfuscated is named in dotted form (net.minecraft.server
                # .MinecraftServer); tiny names it with slashes: without this its row went missing
                cur = (m.group(1), m.group(2).replace(".", "/"), [])
                sections.append(cur)
        elif cur:
            cur[2].append(ln.strip())

    # ---- tiny v2: obf -> intermediary ----
    obf_to_inter_cls = {}
    inter_members = {}  # (obf_cls, obf_name, obf_desc) -> intermediary name
    cur_obf = None
    for ln in io.open(f"data/mappings/intermediary-{ver}.tiny", encoding="utf-8"):
        ln = ln.rstrip("\n")
        if ln.startswith("c\t"):
            p = ln.split("\t")
            cur_obf = p[1]
            obf_to_inter_cls[p[1]] = p[2]
        elif ln.startswith("\tm\t") or ln.startswith("\tf\t"):
            p = ln.split("\t")
            inter_members[(cur_obf, p[3], p[2])] = p[4]

    # ---- compose per member ----
    out = io.open(out_tsv, "w", encoding="utf-8", newline="\n")
    n_cls = n_m = n_f = 0
    m_keys = set()  # the M rows' keys: a comp_ field whose accessor has its id is covered by the M row
    inter_syms = []  # (kind, inter_owner, inter_name, moj_owner, moj_name, moj_sig)
    for moj_cls, obf_cls, members in sections:
        inter_cls = obf_to_inter_cls.get(obf_cls)
        if not inter_cls:
            continue
        moj_internal = moj_cls.replace(".", "/")
        out.write(f"C\t{inter_cls}\t{moj_internal}\n")
        n_cls += 1
        for ml in members:
            mm = re.match(r"(?:\d+:\d+:)?(\S+) (\S+?)\((.*?)\) -> (\S+)", ml)
            if mm:
                ret, name, args, obf = mm.group(1), mm.group(2), mm.group(3), mm.group(4)
                arg_desc = "".join(jvm_type(a, moj_cls_to_obf)
                                   for a in args.split(",") if a)
                obf_desc = "(" + arg_desc + ")" + jvm_type(ret, moj_cls_to_obf)
                inter = inter_members.get((obf_cls, obf, obf_desc))
                if inter:
                    out.write(f"M\t{inter_cls}.{inter}\t{moj_internal}.{name}\n")
                    m_keys.add(f"{inter_cls}.{inter}")
                    n_m += 1
                    inter_syms.append(("M", inter_cls, inter, moj_internal, name,
                                       f"({args})"))
                continue
            fm = re.match(r"(\S+) (\S+) -> (\S+)", ml)
            if fm:
                _t, name, obf = fm.groups()
                inter = None
                for desc_try in (None,):
                    pass
                # field descs: try all tiny entries for this obf name
                for (oc, on, od), iv in ():
                    pass
                inter = None
                # cheap lookup: tiny field entries keyed with desc; scan once below
                inter_syms.append(("F?", obf_cls, obf, moj_internal, name, _t))
    # second pass for fields (desc-insensitive match by obf name)
    field_by_obf = defaultdict(list)
    for (oc, on, od), iv in inter_members.items():
        field_by_obf[(oc, on)].append(iv)
    for kind, obf_cls, obf_name, moj_internal, name, _t in [s for s in inter_syms if s[0] == "F?"]:
        cands = field_by_obf.get((obf_cls, obf_name), [])
        inter_cls = obf_to_inter_cls.get(obf_cls)
        for iv in cands:
            if iv.startswith("field_") or iv == name:
                out.write(f"F\t{inter_cls}.{iv}\t{moj_internal}.{name}\n")
                n_f += 1
                break
        else:
            # a record component's backing field (comp_N, matched with its descriptor: the obfuscated name
            # is shared with methods). Its accessor usually carries the same id and its M row covers the
            # key, but an accessor that implements an interface method takes that method's id (1.21.11
            # WritableBookContent: field comp_2418, accessor pages() is BookContent's comp_2422), and the
            # field had no row at all: a mixin naming it kept the intermediary id across the wall
            iv = inter_members.get((obf_cls, obf_name, jvm_type(_t, moj_cls_to_obf)))
            if iv is not None and iv.startswith("comp_") and f"{inter_cls}.{iv}" not in m_keys:
                out.write(f"F\t{inter_cls}.{iv}\t{moj_internal}.{name}\n")
                n_f += 1
    out.close()
    print(f"composed: {n_cls} classes, {n_m} methods, {n_f} fields -> {out_tsv}")

    # ---- wall measurement against the target dump ----
    target_cls = set()
    target_members = set()  # "owner.name" (mojang internal)
    for ln in io.open(target_dump, encoding="utf-8"):
        if ln.startswith("C "):
            target_cls.add(ln[2:].strip())
        elif ln.startswith(("M ", "F ")):
            full = ln[2:].strip()
            owner_name = full.split("(")[0].split(":")[0]
            target_members.add(owner_name)

    cls_alive = cls_gone = 0
    for ln in io.open(out_tsv, encoding="utf-8"):
        p = ln.rstrip("\n").split("\t")
        if p[0] == "C":
            if p[2] in target_cls:
                cls_alive += 1
            else:
                cls_gone += 1
    m_alive = m_gone = 0
    for ln in io.open(out_tsv, encoding="utf-8"):
        p = ln.rstrip("\n").split("\t")
        if p[0] in ("M", "F"):
            if p[2] in target_members:
                m_alive += 1
            else:
                m_gone += 1
    print(f"wall {ver} -> target: classes alive {cls_alive} / gone {cls_gone}; "
          f"members alive {m_alive} / gone {m_gone}")


if __name__ == "__main__":
    main()
