"""Scan the corpus: per-mod vanilla symbol refs, Mixin details, JIJ, verdicts.

Verdict buckets (per PLAN.md):
  A_clean      every vanilla ref intact, every mixin fully intact
  B_sig_bridge worst damage is L2 (descriptor drift) -> automatic signature bridges
  C_shim_or_degrade  L3 confined to regular code (facade shims) and/or
                     decorative mixins (warn-and-skip degradation)
  D_loadbearing_dead a load-bearing mixin lost its target class/method/anchor
  E_unscannable      no fabric.mod.json / parse failure
"""

from __future__ import annotations

import io
import json
import re
import zipfile
from collections import Counter
from pathlib import Path

import classfile
from chain import ChainResolver, L1, L2, L3, STABLE, _RANK, _worst

SOURCE = "1.20.1"
TARGET = "26.2"
DATA = Path(__file__).resolve().parent.parent / "data"

INJECTOR_HINT_KEYS = ("method", "at", "target", "constant", "slice")
MIXIN_ANNO = "Lorg/spongepowered/asm/mixin/Mixin;"
OVERWRITE_ANNO = "Lorg/spongepowered/asm/mixin/Overwrite;"
SHADOW_ANNO = "Lorg/spongepowered/asm/mixin/Shadow;"
ACCESSOR_ANNOS = (
    "Lorg/spongepowered/asm/mixin/gen/Accessor;",
    "Lorg/spongepowered/asm/mixin/gen/Invoker;",
)


def lenient_json(data: bytes):
    text = data.decode("utf-8-sig", errors="replace")
    try:
        return json.loads(text, strict=False)
    except json.JSONDecodeError:
        text = re.sub(r",\s*([}\]])", r"\1", text)  # trailing commas
        return json.loads(text, strict=False)


def parse_member_spec(spec: str):
    """'Lowner;name(desc)V' | 'Lowner;name:Ldesc;' | 'name(desc)V' | 'name'."""
    owner = None
    if spec.startswith("L") and ";" in spec:
        i = spec.index(";")
        owner = spec[1:i]
        spec = spec[i + 1 :]
    if "(" in spec:
        i = spec.index("(")
        return owner, spec[:i], spec[i:], "m"
    if ":" in spec:
        name, desc = spec.split(":", 1)
        return owner, name, desc, "f"
    return owner, spec, None, "m"


class ModScanner:
    def __init__(self, chain: ChainResolver):
        self.chain = chain

    # ---- per-jar entry point ----
    def scan_jar(self, jar_bytes: bytes, provenance: str = "main") -> dict | None:
        try:
            zf = zipfile.ZipFile(io.BytesIO(jar_bytes))
        except Exception:
            return None
        names = set(zf.namelist())
        if "fabric.mod.json" not in names:
            return None
        try:
            fmj = lenient_json(zf.read("fabric.mod.json"))
        except Exception:
            return None

        mixin_cfgs = fmj.get("mixins", []) or []
        mixin_classes: set[str] = set()
        # loom leaves source-form (Yarn/Mojmap) strings inside mixin annotations and
        # ships a refmap that Mixin consults at runtime; we must do the same lookup.
        refmap: dict[str, dict[str, str]] = {}  # mixin class -> {src string -> intermediary string}
        for entry in mixin_cfgs:
            cfg_name = entry.get("config") if isinstance(entry, dict) else entry
            if not cfg_name or cfg_name not in names:
                continue
            try:
                cfg = lenient_json(zf.read(cfg_name))
            except Exception:
                continue
            rm_name = cfg.get("refmap")
            if rm_name and rm_name in names:
                try:
                    rm = lenient_json(zf.read(rm_name))
                    for cls, table in (rm.get("mappings") or {}).items():
                        refmap.setdefault(cls, {}).update(table)
                except Exception:
                    pass
            pkg = (cfg.get("package") or "").replace(".", "/")
            for key in ("mixins", "client", "server"):
                for cls in cfg.get(key, []) or []:
                    mixin_classes.add(f"{pkg}/{cls.replace('.', '/')}" if pkg else cls.replace(".", "/"))

        ref_levels: Counter[str] = Counter()
        damaged: dict[str, str] = {}  # symbol -> level, for the frequency table
        fabric_refs = 0
        mixins: list[dict] = []

        for name in names:
            if not name.endswith(".class"):
                continue
            try:
                cf = classfile.parse(zf.read(name))
            except Exception:
                continue
            cls_name = cf.name
            if cls_name in mixin_classes:
                mixins.append(self._analyze_mixin(cf, refmap.get(cls_name, {})))
                continue  # a mixin class's own body refs get shimmed with it; skip double-count
            for owner in cf.class_refs:
                if owner.startswith("net/fabricmc/fabric/"):
                    fabric_refs += 1
                    continue
                if not owner.startswith("net/minecraft/"):
                    continue
                lvl = self.chain.resolve_class(owner)
                ref_levels[lvl] += 1
                if lvl in (L2, L3):
                    damaged[f"C:{owner}"] = lvl
            for kind, refs in (("m", cf.method_refs), ("f", cf.field_refs)):
                for owner, mname, mdesc in refs:
                    if owner.startswith("net/fabricmc/fabric/"):
                        fabric_refs += 1
                        continue
                    if not owner.startswith("net/minecraft/"):
                        continue
                    lvl = self.chain.resolve_member(kind, mname, mdesc)
                    ref_levels[lvl] += 1
                    if lvl in (L2, L3):
                        damaged[f"{kind.upper()}:{owner}|{mname}"] = lvl

        # ---- nested JIJ jars ----
        jij: list[dict] = []
        for name in names:
            if name.startswith("META-INF/jars/") and name.endswith(".jar"):
                sub = self.scan_jar(zf.read(name), provenance="bundled")
                if sub:
                    jij.append(sub)

        return {
            "id": fmj.get("id"),
            "provenance": provenance,
            "depends": sorted((fmj.get("depends") or {}).keys()),
            "refs": dict(ref_levels),
            "damaged": damaged,
            "fabric_refs": fabric_refs,
            "mixins": mixins,
            "jij": jij,
        }

    # ---- mixin analysis ----
    def _analyze_mixin(self, cf: classfile.ClassFile, refmap: dict[str, str]) -> dict:
        def rm(s: str) -> str:
            if s in refmap:
                return refmap[s]
            # refmap keys may be descriptor-qualified ("name:Ldesc;" / "name(args)V")
            base = s.split("(")[0].split(":")[0]
            for k, v in refmap.items():
                if k.split("(")[0].split(":")[0] == base:
                    return v
            return s

        def resolve_spec(kd: str, nm: str, dsc: str | None) -> str:
            lvl = self.chain.resolve_member(kd, nm, dsc)
            # a stable-named target (e.g. <init>) can still die via a dead class
            # buried in its descriptor
            if dsc:
                for m in re.finditer(r"net/minecraft/class_\d+", dsc):
                    lvl = _worst(lvl, self.chain.resolve_class(m.group(0)))
            return lvl

        target_classes: list[str] = []
        for a in cf.annotations:
            if a.type != MIXIN_ANNO:
                continue
            for v in a.values.get("value", []) or []:
                if isinstance(v, str) and v.startswith("L"):
                    target_classes.append(v[1:-1])
            for v in a.values.get("targets", []) or []:
                if isinstance(v, str):
                    t = rm(v).replace(".", "/")
                    if t.startswith("L") and t.endswith(";"):
                        t = t[1:-1]
                    target_classes.append(t)

        vanilla = any(t.startswith("net/minecraft/") for t in target_classes)
        worst = L1
        anchors = 0
        broken_details: list[str] = []

        def touch(lvl: str, what: str):
            nonlocal worst
            if _RANK.get(lvl, 0) > 0:
                broken_details.append(f"{lvl}:{what}")
            worst = _worst(worst, lvl)

        for t in target_classes:
            if t.startswith("net/minecraft/"):
                touch(self.chain.resolve_class(t), f"target {t}")

        load_bearing = bool(cf.interfaces)  # interface merge onto the target
        for m in cf.methods:
            for a in m.annotations:
                if a.type == OVERWRITE_ANNO:
                    load_bearing = True
                    touch(resolve_spec("m", m.name, m.desc), f"overwrite {m.name}")
                elif a.type == SHADOW_ANNO:
                    touch(resolve_spec("m", m.name, m.desc), f"shadow {m.name}")
                elif a.type in ACCESSOR_ANNOS:
                    load_bearing = True
                    tgt = a.values.get("value")
                    if not (isinstance(tgt, str) and tgt):
                        # implicit accessor: target inferred from getX/isX/setX method name
                        mm = re.match(r"(?:get|is|set)([A-Z].*)", m.name)
                        tgt = mm.group(1)[0].lower() + mm.group(1)[1:] if mm else m.name
                    kd = "m" if a.type.endswith("Invoker;") else "f"
                    _, nm, dsc, _ = parse_member_spec(rm(tgt))
                    touch(resolve_spec(kd, nm, dsc), f"accessor {tgt}")
                elif any(k in a.values for k in ("method", "at", "constant")):
                    # generic injector (@Inject/@Redirect/@ModifyX/MixinExtras)
                    specs = a.values.get("method") or []
                    if isinstance(specs, str):
                        specs = [specs]
                    for spec in specs:
                        if not isinstance(spec, str) or spec in ("*",) or spec.startswith("/"):
                            continue
                        _, nm, dsc, kd = parse_member_spec(rm(spec))
                        touch(resolve_spec(kd, nm, dsc), f"inject-target {nm}")
                    ats = a.values.get("at")
                    if isinstance(ats, classfile.Annotation):
                        ats = [ats]
                    for at in ats or []:
                        if not isinstance(at, classfile.Annotation):
                            continue
                        anchors += 1
                        tgt = at.values.get("target")
                        if isinstance(tgt, str) and tgt:
                            owner, nm, dsc, kd = parse_member_spec(rm(tgt))
                            if owner and owner.startswith("net/minecraft/"):
                                touch(self.chain.resolve_class(owner), f"at-owner {owner}")
                            touch(resolve_spec(kd, nm, dsc), f"at {nm}")
        for f in cf.fields:
            for a in f.annotations:
                if a.type == SHADOW_ANNO:
                    touch(resolve_spec("f", f.name, f.desc), f"shadow-field {f.name}")

        return {
            "class": cf.name,
            "targets": target_classes,
            "vanilla": vanilla,
            "load_bearing": load_bearing,
            "worst": worst,
            "anchors": anchors,
            "broken": broken_details[:20],
        }


def verdict(mod: dict) -> str:
    """Classify one scanned mod (bundled JIJ jars folded in)."""
    def all_units(m):
        yield m
        for s in m.get("jij", []):
            yield from all_units(s)

    worst_code = L1
    lb_dead = False
    deco_dead = False
    has_l2 = False
    for u in all_units(mod):
        for lvl, n in u["refs"].items():
            if n and lvl == L3:
                worst_code = L3
            elif n and lvl == L2:
                has_l2 = True
        for mx in u["mixins"]:
            if not mx["vanilla"]:
                continue
            if mx["worst"] == L3:
                if mx["load_bearing"]:
                    lb_dead = True
                else:
                    deco_dead = True
            elif mx["worst"] == L2:
                has_l2 = True
    if lb_dead:
        return "D_loadbearing_dead"
    if worst_code == L3 or deco_dead:
        return "C_shim_or_degrade"
    if has_l2:
        return "B_sig_bridge"
    return "A_clean"


def main() -> None:
    corpus = DATA / "corpus" / SOURCE
    index = json.loads((corpus / "index.json").read_text(encoding="utf-8"))
    chain = ChainResolver(SOURCE, TARGET)
    scanner = ModScanner(chain)

    results = []
    for i, meta in enumerate(index, 1):
        jar = corpus / "jars" / meta["file"]
        if not jar.exists():
            continue
        scanned = scanner.scan_jar(jar.read_bytes())
        if scanned is None:
            results.append({"meta": meta, "verdict": "E_unscannable"})
            continue
        scanned["meta"] = meta
        scanned["verdict"] = verdict(scanned)
        results.append(scanned)
        if i % 25 == 0:
            print(f"scanned {i}/{len(index)}")

    out = DATA / "reports" / f"scan-{SOURCE}-to-{TARGET}.json"
    out.write_text(json.dumps(results), encoding="utf-8")
    print(f"scanned {len(results)} mods -> {out}")
    print(Counter(r["verdict"] for r in results).most_common())


if __name__ == "__main__":
    main()
