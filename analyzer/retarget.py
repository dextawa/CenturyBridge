"""CenturyBridge retarget PoC: rewrite a 1.20.1-era Fabric mod jar into the
26.x Mojmap namespace, statically verify it links against the target inventory.

Mechanism: intermediary tokens (class_N / method_N / field_N) are globally
unique, so a constant-pool Utf8 rewrite catches bytecode refs, mixin annotation
strings, refmaps and reflection strings in one uniform pass. The class file is
rebuilt byte-surgically: only Utf8 entries change, everything after the
constant pool is copied verbatim.
"""

from __future__ import annotations

import io
import json
import re
import struct
import sys
import zipfile
from pathlib import Path

from inventory import Inventory
from translate import Translator
from scan_mods import lenient_json

DATA = Path(__file__).resolve().parent.parent / "data"
SOURCE, MID, TARGET = "1.20.1", "1.21.11", "26.2"

TOKEN = re.compile(r"(net[/.]minecraft[/.]class_\d+(?:\$class_\d+)*)|\b(method_\d+)\b|\b(field_\d+)\b")


class Renamer:
    def __init__(self, t: Translator):
        self.t = t
        self.unmapped: set[str] = set()

    def rewrite(self, s: str) -> str:
        def sub(m: re.Match) -> str:
            if m.group(1):
                dotted = "." in m.group(1).split("$", 1)[0]
                int_name = m.group(1).replace(".", "/")
                named = self.t.cls_int_to_named.get(int_name)
                if named is None:
                    self.unmapped.add(int_name)
                    return m.group(0)
                return named.replace("/", ".") if dotted else named
            tok = m.group(2) or m.group(3)
            table = self.t.method_int_to_named if tok.startswith("method") else self.t.field_int_to_named
            named = table.get(tok)
            if named is None:
                self.unmapped.add(tok)
                return tok
            return named
        return TOKEN.sub(sub, s)


def rewrite_class(data: bytes, renamer: Renamer) -> bytes:
    """Re-encode only the Utf8 constant-pool entries; copy the rest verbatim."""
    out = io.BytesIO()
    out.write(data[:8])  # magic + minor + major
    (cp_count,) = struct.unpack_from(">H", data, 8)
    out.write(data[8:10])
    i = 10
    n = 1
    while n < cp_count:
        tag = data[i]
        if tag == 1:
            (ln,) = struct.unpack_from(">H", data, i + 1)
            raw = data[i + 3 : i + 3 + ln]
            try:
                s = raw.decode("utf-8")
                s2 = renamer.rewrite(s)
                raw2 = s2.encode("utf-8") if s2 != s else raw
            except UnicodeDecodeError:
                raw2 = raw  # modified-UTF8 oddity: never contains our tokens
            out.write(bytes([1]))
            out.write(struct.pack(">H", len(raw2)))
            out.write(raw2)
            i += 3 + ln
        else:
            size = {7: 3, 8: 3, 16: 3, 19: 3, 20: 3, 15: 4, 3: 5, 4: 5, 9: 5,
                    10: 5, 11: 5, 12: 5, 17: 5, 18: 5, 5: 9, 6: 9}[tag]
            out.write(data[i : i + size])
            i += size
            if tag in (5, 6):
                n += 1
        n += 1
    out.write(data[i:])
    return out.getvalue()


def patch_fmj(data: bytes) -> bytes:
    fmj = lenient_json(data)
    dep = fmj.get("depends") or {}
    for k in list(dep):
        dep[k] = ">=26.0-" if k == "minecraft" else "*"
    if "fabric" in dep:  # old fabric-api umbrella id; renamed on modern versions
        dep["fabric-api"] = dep.pop("fabric")
    fmj["depends"] = dep
    fmj.pop("breaks", None)
    return json.dumps(fmj, indent=1, ensure_ascii=False).encode("utf-8")


def retarget_jar(src: Path, dst: Path, renamer: Renamer) -> None:
    with zipfile.ZipFile(src) as zin, zipfile.ZipFile(dst, "w", zipfile.ZIP_DEFLATED) as zout:
        for info in zin.infolist():
            data = zin.read(info.filename)
            name = info.filename
            if name.endswith(".class"):
                data = rewrite_class(data, renamer)
            elif name == "fabric.mod.json":
                data = patch_fmj(data)
            elif name.endswith(".json"):
                try:
                    data = renamer.rewrite(data.decode("utf-8")).encode("utf-8")
                except UnicodeDecodeError:
                    pass
            elif name.startswith("META-INF/jars/") and name.endswith(".jar"):
                buf = Path(dst.parent / "_jij.tmp")
                buf.write_bytes(data)
                out_buf = dst.parent / "_jij_out.tmp"
                retarget_jar(buf, out_buf, renamer)
                data = out_buf.read_bytes()
                buf.unlink(); out_buf.unlink()
            elif name.startswith("META-INF/") and name.endswith((".SF", ".RSA", ".DSA")):
                continue  # signatures are invalid after rewrite
            zout.writestr(info.filename, data)


def verify(dst: Path, inv: Inventory) -> dict:
    import classfile
    missing_cls: set[str] = set()
    missing_member: set[str] = set()
    leftovers: set[str] = set()
    total = 0
    with zipfile.ZipFile(dst) as zf:
        for name in zf.namelist():
            if not name.endswith(".class"):
                continue
            try:
                cf = classfile.parse(zf.read(name), want_annotations=False)
            except Exception:
                continue
            for c in cf.class_refs:
                if re.match(r"net/minecraft/class_\d+$", c):
                    leftovers.add(c)
                    continue
                if c.startswith(("net/minecraft/", "com/mojang/blaze3d/")):
                    total += 1
                    if not inv.has_class(c):
                        missing_cls.add(c)
            for kind, refs in (("m", cf.method_refs), ("f", cf.field_refs)):
                for owner, mn, md in refs:
                    if re.match(r"(method|field)_\d+$", mn):
                        leftovers.add(mn)
                        continue
                    if owner.startswith(("net/minecraft/", "com/mojang/blaze3d/")):
                        total += 1
                        lvl = inv.resolve_member(kind, owner, mn, md)
                        if lvl != "L1_intact":
                            missing_member.add(f"{owner.rsplit('/',1)[-1]}.{mn}:{lvl}")
    return {
        "vanilla_refs": total,
        "missing_classes": sorted(missing_cls),
        "missing_members": sorted(missing_member),
        "untranslated_tokens": sorted(leftovers),
    }


def repair_kinds(dst: Path) -> None:
    """Second pass: fix class<->interface owner-kind drift (e.g. DFU DataResult)."""
    import bytecode_shim as bs
    server_libs = DATA / "runtime" / f"server-{TARGET}" / "libraries"
    kinds = bs.build_kind_map(TARGET, [server_libs] if server_libs.exists() else [])
    reg = bs.ShimRegistry()
    total_flips = total_sites = 0
    entries: list[tuple[zipfile.ZipInfo, bytes]] = []
    with zipfile.ZipFile(dst) as zin:
        for info in zin.infolist():
            data = zin.read(info.filename)
            if info.filename.endswith(".class"):
                try:
                    data2, sites = bs.patch_class(data, kinds, reg)
                    if data2 is not data:
                        total_flips += 1
                        total_sites += sites
                        data = data2
                except Exception as e:
                    print(f"  kind-repair skipped {info.filename}: {e}")
            entries.append((info, data))
    with zipfile.ZipFile(dst, "w", zipfile.ZIP_DEFLATED) as zout:
        for info, data in entries:
            zout.writestr(info.filename, data)
        if reg.shims:
            zout.writestr(f"{bs.ShimRegistry.SHIM_CLASS}.class", bs.synthesize_shim_class(reg))
    if total_flips:
        print(f"kind repair: {total_flips} classes tag-flipped, {total_sites} call sites redirected, {len(reg.shims)} shims generated")


def pick_candidates(limit: int = 10) -> list[dict]:
    scan = json.loads((DATA / "reports" / f"scan-{SOURCE}-to-{TARGET}.json").read_text(encoding="utf-8"))
    good = [m for m in scan if m.get("verdict") in ("A_clean", "B_sig_bridge") and m.get("meta", {}).get("kpi")]
    good.sort(key=lambda m: (len(m.get("mixins", [])), sum(m.get("refs", {}).values())))
    return good[:limit]


def main() -> None:
    slug = sys.argv[1] if len(sys.argv) > 1 else None
    if slug is None:
        print("candidates (A/B bucket, fewest mixins first):")
        for m in pick_candidates():
            meta = m["meta"]
            print(f"  {meta['slug']:40s} verdict={m['verdict']} mixins={len(m['mixins'])} refs={sum(m['refs'].values())}")
        return
    t = Translator(MID)
    renamer = Renamer(t)
    inv = Inventory(TARGET)
    src = DATA / "corpus" / SOURCE / "jars" / f"{slug}.jar"
    out_dir = DATA / "retargeted"
    out_dir.mkdir(exist_ok=True)
    dst = out_dir / f"{slug}+{TARGET}.jar"
    retarget_jar(src, dst, renamer)
    repair_kinds(dst)
    print(f"retargeted -> {dst}")
    if renamer.unmapped:
        print(f"unmapped tokens ({len(renamer.unmapped)}): {sorted(renamer.unmapped)[:10]}")
    v = verify(dst, inv)
    print(f"vanilla refs checked: {v['vanilla_refs']}")
    print(f"missing classes: {len(v['missing_classes'])} {v['missing_classes'][:5]}")
    print(f"missing members: {len(v['missing_members'])} {v['missing_members'][:5]}")
    print(f"untranslated intermediary leftovers: {len(v['untranslated_tokens'])}")


if __name__ == "__main__":
    main()
