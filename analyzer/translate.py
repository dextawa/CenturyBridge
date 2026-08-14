"""Compose intermediary -> Mojmap(named) translation for an old version.

Published 1.20.1-era Fabric mods reference intermediary names; the 26.x
runtime is unobfuscated Mojmap. Join path:

    intermediary <- (tiny) <- official/obf -> (ProGuard client_mappings) -> named

The join key on the obf side is (owner_obf, name_obf, desc_obf).
"""

from __future__ import annotations

import json
import re
import sys
import urllib.request
from pathlib import Path

from mappings import fetch_tiny

DATA_DIR = Path(__file__).resolve().parent.parent / "data" / "mappings"
MANIFEST = "https://launchermeta.mojang.com/mc/game/version_manifest_v2.json"

PRIMS = {
    "void": "V", "boolean": "Z", "byte": "B", "char": "C",
    "short": "S", "int": "I", "long": "J", "float": "F", "double": "D",
}
METHOD_LINE = re.compile(r"^(?:\d+:\d+:)?([\w.$\[\]]+) ([\w$<>]+)\(([^)]*)\) -> (.+)$")
FIELD_LINE = re.compile(r"^([\w.$\[\]]+) ([\w$]+) -> (.+)$")
INT_METHOD = re.compile(r"^method_\d+$")
INT_FIELD = re.compile(r"^field_\d+$")
INT_CLASS = re.compile(r"^net/minecraft/class_\d+$")


def fetch_proguard(version: str) -> Path:
    DATA_DIR.mkdir(parents=True, exist_ok=True)
    out = DATA_DIR / f"mojang-{version}.txt"
    if out.exists():
        return out
    manifest = json.load(urllib.request.urlopen(MANIFEST))
    entry = next(v for v in manifest["versions"] if v["id"] == version)
    vjson = json.load(urllib.request.urlopen(entry["url"]))
    url = vjson["downloads"]["client_mappings"]["url"]
    print(f"downloading client_mappings {version}")
    urllib.request.urlretrieve(url, out)
    return out


def _java_type_to_desc(t: str, named_to_obf_cls: dict[str, str] | None = None) -> str:
    dims = 0
    while t.endswith("[]"):
        dims += 1
        t = t[:-2]
    if t in PRIMS:
        d = PRIMS[t]
    else:
        internal = t.replace(".", "/")
        if named_to_obf_cls is not None:
            internal = named_to_obf_cls.get(internal, internal)
        d = "L" + internal + ";"
    return "[" * dims + d


class Translator:
    def __init__(self, version: str):
        self.version = version
        # ---- parse ProGuard (named -> obf) ----
        named_to_obf_cls: dict[str, str] = {}
        pg_members: list[tuple[str, str, str, str, str]] = []  # kind, named_cls, named_name, java_sig, obf_name
        current_named = None
        for line in fetch_proguard(version).read_text(encoding="utf-8").splitlines():
            if line.startswith("#") or not line.strip():
                continue
            if not line.startswith("    "):
                named, obf = line.rstrip(":").split(" -> ")
                current_named = named.replace(".", "/")
                named_to_obf_cls[current_named] = obf.replace(".", "/")
                continue
            body = line.strip()
            m = METHOD_LINE.match(body)
            if m:
                ret, name, args, obf = m.groups()
                sig = f"{ret} ({args})"
                pg_members.append(("m", current_named, name, sig, obf))
                continue
            m = FIELD_LINE.match(body)
            if m:
                ftype, name, obf = m.groups()
                pg_members.append(("f", current_named, name, ftype, obf))

        obf_to_named_cls = {v: k for k, v in named_to_obf_cls.items()}

        # obf-side member lookup: (kind, owner_obf, name_obf, desc_obf) -> named_name
        pg_lookup: dict[tuple[str, str, str, str], str] = {}
        for kind, named_cls, named_name, sig, obf_name in pg_members:
            owner_obf = named_to_obf_cls[named_cls]
            if kind == "m":
                ret, args = sig.split(" (")
                args = args.rstrip(")")
                arg_desc = "".join(
                    _java_type_to_desc(a.strip(), named_to_obf_cls) for a in args.split(",") if a.strip()
                )
                desc_obf = f"({arg_desc}){_java_type_to_desc(ret, named_to_obf_cls)}"
            else:
                desc_obf = _java_type_to_desc(sig, named_to_obf_cls)
            pg_lookup[(kind, owner_obf, obf_name, desc_obf)] = named_name

        # ---- parse tiny (obf -> intermediary), keeping obf-side identities ----
        obf_to_int_cls: dict[str, str] = {}
        tiny_members: list[tuple[str, str, str, str, str]] = []  # kind, owner_obf, name_obf, desc_obf, name_int
        current_obf = None
        with fetch_tiny(version).open(encoding="utf-8") as f:
            f.readline()
            for line in f:
                parts = line.rstrip("\n").split("\t")
                if parts[0] == "c":
                    current_obf = parts[1]
                    obf_to_int_cls[current_obf] = parts[2]
                elif parts[0] == "" and len(parts) >= 5 and parts[1] in ("m", "f"):
                    tiny_members.append((parts[1], current_obf, parts[3], parts[2], parts[4]))

        # ---- compose ----
        self.cls_int_to_named: dict[str, str] = {}
        for obf, inter in obf_to_int_cls.items():
            named = obf_to_named_cls.get(obf)
            if named:
                self.cls_int_to_named[inter] = named
        self.method_int_to_named: dict[str, str] = {}
        self.field_int_to_named: dict[str, str] = {}
        # full member records for whole-mapping survival stats:
        # (kind, owner_int, name_int, named_owner, named_name, desc_named)
        self.members: list[tuple[str, str, str, str | None, str | None, str | None]] = []
        unjoined = 0
        for kind, owner_obf, name_obf, desc_obf, name_int in tiny_members:
            named_name = pg_lookup.get((kind, owner_obf, name_obf, desc_obf))
            owner_int = obf_to_int_cls[owner_obf]
            named_owner = obf_to_named_cls.get(owner_obf)
            desc_named = self._remap_desc(desc_obf, obf_to_named_cls) if named_name else None
            if named_name:
                target = self.method_int_to_named if kind == "m" else self.field_int_to_named
                target[name_int] = named_name
            else:
                unjoined += 1
            self.members.append((kind, owner_int, name_int, named_owner, named_name, desc_named))
        self.unjoined = unjoined

    @staticmethod
    def _remap_desc(desc: str, cls_map: dict[str, str]) -> str:
        out, i, n = [], 0, len(desc)
        while i < n:
            ch = desc[i]
            if ch == "L":
                j = desc.index(";", i)
                name = desc[i + 1 : j]
                out.append("L" + cls_map.get(name, name) + ";")
                i = j + 1
            else:
                out.append(ch)
                i += 1
        return "".join(out)

    # ---- mod-facing API: translate an intermediary-namespace reference ----
    def named_class(self, int_name: str) -> str | None:
        if not int_name.startswith("net/minecraft/"):
            return int_name
        if INT_CLASS.match(int_name):
            return self.cls_int_to_named.get(int_name)
        return int_name  # unobfuscated vanilla class, passes through

    def named_member(self, kind: str, int_name: str) -> str | None:
        pat = INT_METHOD if kind == "m" else INT_FIELD
        if not pat.match(int_name):
            return int_name  # JDK-contract or unmapped name, passes through
        table = self.method_int_to_named if kind == "m" else self.field_int_to_named
        return table.get(int_name)

    def named_desc(self, int_desc: str) -> str:
        return self._remap_desc(int_desc, self.cls_int_to_named)


if __name__ == "__main__":
    t = Translator(sys.argv[1])
    print(f"classes translated: {len(t.cls_int_to_named)}")
    print(f"methods translated: {len(t.method_int_to_named)}")
    print(f"fields  translated: {len(t.field_int_to_named)}")
    print(f"unjoined members:   {t.unjoined}")
    probe = "net/minecraft/class_1799"  # ItemStack in 1.20.1
    print(f"probe {probe} -> {t.named_class(probe)}")
