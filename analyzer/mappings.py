"""Intermediary mapping download, tiny-v2 parsing, and cross-version diff.

Namespace note: published Fabric mods reference Minecraft in the *intermediary*
namespace (net/minecraft/class_N, method_N, field_N), which is also the
production runtime namespace. Survival of a symbol therefore means: its
intermediary name still exists in the target version's mapping file.

Descriptors in tiny v2 are written in the *official* (obfuscated) namespace,
so they must be remapped through the same version's class map before they can
be compared across versions.
"""

from __future__ import annotations

import json
import re
import sys
import urllib.request
import zipfile
from dataclasses import dataclass, field
from pathlib import Path

DATA_DIR = Path(__file__).resolve().parent.parent / "data" / "mappings"
MAVEN = "https://maven.fabricmc.net/net/fabricmc/intermediary/{v}/intermediary-{v}-v2.jar"

METHOD_PAT = re.compile(r"^method_\d+$")
FIELD_PAT = re.compile(r"^field_\d+$")
CLASS_PAT = re.compile(r"^net/minecraft/class_\d+$")


def fetch_tiny(version: str) -> Path:
    DATA_DIR.mkdir(parents=True, exist_ok=True)
    out = DATA_DIR / f"intermediary-{version}.tiny"
    if out.exists():
        return out
    jar_path = DATA_DIR / f"intermediary-{version}.jar"
    url = MAVEN.format(v=version)
    print(f"downloading {url}")
    urllib.request.urlretrieve(url, jar_path)
    with zipfile.ZipFile(jar_path) as zf:
        data = zf.read("mappings/mappings.tiny")
    out.write_bytes(data)
    jar_path.unlink()
    return out


@dataclass
class Mappings:
    version: str
    # obf class name -> intermediary class name
    class_map: dict[str, str] = field(default_factory=dict)
    # intermediary class names
    classes: set[str] = field(default_factory=set)
    # intermediary method name -> set of (owner_intermediary, desc_intermediary)
    methods: dict[str, set[tuple[str, str]]] = field(default_factory=dict)
    # intermediary field name -> set of (owner_intermediary, desc_intermediary)
    fields: dict[str, set[tuple[str, str]]] = field(default_factory=dict)

    def remap_desc(self, desc: str) -> str:
        """Rewrite Lobf; class refs in a descriptor to intermediary names."""
        out = []
        i = 0
        n = len(desc)
        while i < n:
            ch = desc[i]
            if ch == "L":
                j = desc.index(";", i)
                obf = desc[i + 1 : j]
                out.append("L" + self.class_map.get(obf, obf) + ";")
                i = j + 1
            else:
                out.append(ch)
                i += 1
        return "".join(out)


def load(version: str) -> Mappings:
    path = fetch_tiny(version)
    m = Mappings(version)
    raw_members: list[tuple[str, str, str, str]] = []  # kind, owner_int, desc_obf, name_int
    current_class_int = None
    with path.open(encoding="utf-8") as f:
        header = f.readline().rstrip("\n").split("\t")
        assert header[0] == "tiny" and header[3] == "official" and header[4] == "intermediary", header
        for line in f:
            parts = line.rstrip("\n").split("\t")
            if parts[0] == "c":
                obf, inter = parts[1], parts[2]
                m.class_map[obf] = inter
                m.classes.add(inter)
                current_class_int = inter
            elif len(parts) > 1 and parts[1] in ("m", "f") and parts[0] == "":
                # "\tm\t<desc_obf>\t<name_obf>\t<name_int>"
                kind, desc_obf, name_int = parts[1], parts[2], parts[4]
                raw_members.append((kind, current_class_int, desc_obf, name_int))
    # second pass: remap descriptors now that the class map is complete
    for kind, owner, desc_obf, name_int in raw_members:
        desc_int = m.remap_desc(desc_obf)
        target = m.methods if kind == "m" else m.fields
        target.setdefault(name_int, set()).add((owner, desc_int))
    return m


# survival levels
L1 = "L1_intact"        # name exists, same descriptor exists somewhere
L2 = "L2_desc_changed"  # name exists, no matching descriptor
L3 = "L3_gone"          # intermediary name absent from target version
STABLE = "assumed_stable"  # non-intermediary name (JDK-contract etc.), not tracked


class Diff:
    def __init__(self, old: Mappings, new: Mappings):
        self.old = old
        self.new = new

    def class_survival(self, name: str) -> str:
        if not name.startswith("net/minecraft/"):
            return STABLE
        if not CLASS_PAT.match(name):
            # named (unobfuscated) vanilla class: check presence directly
            return L1 if name in self.new.classes else L3
        return L1 if name in self.new.classes else L3

    def member_survival(self, kind: str, name: str, desc: str | None) -> str:
        pat = METHOD_PAT if kind == "m" else FIELD_PAT
        table = self.new.methods if kind == "m" else self.new.fields
        if not pat.match(name):
            return STABLE
        entries = table.get(name)
        if not entries:
            return L3
        if desc is None:
            return L1
        if any(d == desc for _, d in entries):
            return L1
        return L2

    def stats(self) -> dict:
        out: dict[str, dict[str, int]] = {}
        cls_old = {c for c in self.old.classes if CLASS_PAT.match(c)}
        out["classes"] = {
            "total_old": len(cls_old),
            "survived": sum(1 for c in cls_old if c in self.new.classes),
        }
        for kind, old_table in (("methods", self.old.methods), ("fields", self.old.fields)):
            pat = METHOD_PAT if kind == "methods" else FIELD_PAT
            new_table = self.new.methods if kind == "methods" else self.new.fields
            total = intact = desc_changed = gone = 0
            for name, entries in old_table.items():
                if not pat.match(name):
                    continue
                total += 1
                new_entries = new_table.get(name)
                if not new_entries:
                    gone += 1
                    continue
                old_descs = {d for _, d in entries}
                new_descs = {d for _, d in new_entries}
                if old_descs & new_descs:
                    intact += 1
                else:
                    desc_changed += 1
            out[kind] = {
                "total_old": total,
                "intact": intact,
                "desc_changed": desc_changed,
                "gone": gone,
            }
        return out


def main() -> None:
    old_v, new_v = sys.argv[1], sys.argv[2]
    diff = Diff(load(old_v), load(new_v))
    s = diff.stats()
    print(json.dumps(s, indent=2))
    c = s["classes"]
    print(f"\nclass survival: {c['survived']}/{c['total_old']} = {c['survived']/c['total_old']:.1%}")
    for kind in ("methods", "fields"):
        k = s[kind]
        t = k["total_old"]
        print(
            f"{kind}: intact {k['intact']/t:.1%}, desc-changed {k['desc_changed']/t:.1%}, "
            f"gone {k['gone']/t:.1%}  (n={t})"
        )


if __name__ == "__main__":
    main()
