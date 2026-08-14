"""CenturyBridge P1 pipeline prototype -- CLI front-end of the engine.

Usage:
    python cb.py <cbmods-dir> [--deploy <mods-dir>]

Per-jar pipeline: version gate -> mixin triage (intermediary space) ->
rename pass -> class/interface kind repair -> dead-mixin strip -> static
verify -> dependency check -> verdict + report.
Also assembles and compiles the centurybridge-rt runtime bridge mod from
cb/shims/ (Java sources + manifest: access wideners, covered signatures).
"""

from __future__ import annotations

import argparse
import io
import json
import re
import shutil
import subprocess
import sys
import zipfile
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
sys.path.insert(0, str(ROOT / "analyzer"))

from chain import ChainResolver           # noqa: E402
from inventory import Inventory           # noqa: E402
from retarget import Renamer, retarget_jar, repair_kinds, verify, TARGET, MID  # noqa: E402
from scan_mods import ModScanner, lenient_json  # noqa: E402
from translate import Translator          # noqa: E402

CB_DIR = Path(__file__).resolve().parent
CACHE: Path  # set per run: <cbmods>/recovered
PLATFORM = {"fabricloader", "fabric", "fabric-api", "minecraft", "java", "fabric-language-kotlin"}
FABRIC_MODULE = re.compile(r"^fabric-[a-z0-9-]+-v\d+$|^fabric-api-base$")


class Engine:
    def __init__(self):
        print("loading segment data (translator, inventory, chain)...")
        self.t_mid = Translator(MID)
        self.inv = Inventory(TARGET)
        self.chain = ChainResolver("1.20.1", TARGET)
        self.scanner = ModScanner(self.chain)
        manifest = json.loads((CB_DIR / "shims" / "manifest.json").read_text(encoding="utf-8"))
        self.aw_lines: list[str] = manifest["accessWidener"]
        covers = set(manifest["covers"])
        self.covers = covers | {c.split(".", 1)[1] for c in covers if "." in c}

    # ---------------- per-jar pipeline ----------------
    def process(self, jar: Path, present_ids: set[str]) -> dict:
        rpt: dict = {"file": jar.name, "verdict": None, "notes": []}
        try:
            with zipfile.ZipFile(jar) as zf:
                fmj = lenient_json(zf.read("fabric.mod.json"))
        except Exception:
            rpt["verdict"] = "unscannable"
            return rpt
        rpt["id"] = fmj.get("id")
        mc_dep = str((fmj.get("depends") or {}).get("minecraft", ""))

        # version gate (pilot corridor: 1.20.x sources only)
        if mc_dep and mc_dep not in ("*",) and "1.20" not in mc_dep:
            rpt["verdict"] = "blocked_version"
            rpt["notes"].append(f"declared minecraft {mc_dep!r}; pilot corridor handles 1.20.x only")
            return rpt
        if not mc_dep or mc_dep == "*":
            rpt["notes"].append("no explicit minecraft range declared; assuming 1.20.1")

        # mixin triage on the original (intermediary-space) jar
        scanned = self.scanner.scan_jar(jar.read_bytes())
        dead_mixins: list[dict] = []
        risky = False
        if scanned:
            for mx in scanned.get("mixins", []):
                if mx["vanilla"] and mx["worst"] == "L3_gone":
                    dead_mixins.append(mx)
                    if mx["load_bearing"]:
                        risky = True

        # transform
        out = CACHE / f"{jar.stem}+{TARGET}.jar"
        renamer = Renamer(self.t_mid)
        retarget_jar(jar, out, renamer)
        repair_kinds(out)
        stripped = self._strip_mixins(out, {m["class"] for m in dead_mixins})
        rpt["stripped_mixins"] = [
            {"class": m["class"].rsplit("/", 1)[-1], "load_bearing": m["load_bearing"], "broken": m["broken"][:3]}
            for m in dead_mixins
        ]

        # static verify, minus shim-covered signatures
        v = verify(out, self.inv)
        missing = [m for m in v["missing_members"] if m.split(":")[0] not in self.covers
                   and m.split(":")[0].split(".")[-1] not in self.covers]
        leftovers = [t for t in v["untranslated_tokens"] if t not in self.covers]
        rpt["missing"] = missing
        rpt["leftover_tokens"] = leftovers
        rpt["vanilla_refs"] = v["vanilla_refs"]

        # dependency check
        ext = [d for d in (fmj.get("depends") or {})
               if d not in PLATFORM and not FABRIC_MODULE.match(d) and d not in present_ids]
        rpt["missing_deps"] = ext

        if ext:
            rpt["verdict"] = "blocked_deps"  # loader-level resolution failure, genuinely blocking
        elif missing or leftovers:
            # tombstone philosophy: uncovered symbols degrade lazily (fault only
            # if the path executes), they do not block deployment
            rpt["verdict"] = "ok_partial"
        elif dead_mixins:
            rpt["verdict"] = "ok_degraded_risky" if risky else "ok_degraded"
        else:
            rpt["verdict"] = "ok_direct"
        rpt["out"] = out.name
        return rpt

    @staticmethod
    def _strip_mixins(out_jar: Path, dead: set[str]) -> int:
        if not dead:
            return 0
        removed = 0
        buf = io.BytesIO()
        with zipfile.ZipFile(out_jar) as zin, zipfile.ZipFile(buf, "w", zipfile.ZIP_DEFLATED) as zout:
            for info in zin.infolist():
                data = zin.read(info.filename)
                if info.filename.endswith(".mixins.json") and "/" not in info.filename:
                    try:
                        cfg = lenient_json(data)
                        pkg = (cfg.get("package") or "").replace(".", "/")
                        for key in ("mixins", "client", "server"):
                            if key in cfg and cfg[key]:
                                kept = []
                                for c in cfg[key]:
                                    full = f"{pkg}/{c.replace('.', '/')}" if pkg else c.replace(".", "/")
                                    if full in dead:
                                        removed += 1
                                    else:
                                        kept.append(c)
                                cfg[key] = kept
                        data = json.dumps(cfg, indent=1).encode()
                    except Exception:
                        pass
                zout.writestr(info.filename, data)
        out_jar.write_bytes(buf.getvalue())
        return removed

    # ---------------- single-artifact assembly ----------------
    def build_runtime_jar(self, embed: list[Path] = ()) -> Path:
        """The one user-installed jar: shim mixins + AW (+ optional JIJ payload
        for the zero-config fallback tier; canonical loading is fabric.addMods
        pointing at cbmods/recovered)."""
        src_dir = CB_DIR / "shims" / "src"
        build = CB_DIR / "build"
        out_cls = build / "classes"
        shutil.rmtree(build, ignore_errors=True)
        out_cls.mkdir(parents=True)

        client = ROOT / "data" / "jars" / f"client-{TARGET}.jar"
        mixin_jar = next((ROOT / "data" / "runtime" / f"server-{TARGET}" / "libraries").rglob("sponge-mixin-*.jar"))
        sources = [str(p) for p in src_dir.rglob("*.java")]
        subprocess.run(
            ["javac", "-nowarn", "-cp", f"{client};{mixin_jar}", "-d", str(out_cls)] + sources,
            check=True,
        )
        classes = sorted(p.stem for p in (src_dir / "cb").glob("*.java"))
        fmj = {
            "schemaVersion": 1, "id": "centurybridge", "version": "0.2.0",
            "name": "CenturyBridge", "environment": "*",
            "mixins": ["cb-rt.mixins.json"], "accessWidener": "cb.accesswidener",
            "depends": {"fabricloader": ">=0.15.0"},
        }
        if embed:
            fmj["jars"] = [{"file": f"META-INF/jars/{p.name}"} for p in embed]
        jar_out = CACHE / f"centurybridge-{TARGET}.jar"
        with zipfile.ZipFile(jar_out, "w", zipfile.ZIP_DEFLATED) as z:
            for cls in out_cls.rglob("*.class"):
                z.write(cls, cls.relative_to(out_cls).as_posix())
            z.writestr("cb-rt.mixins.json", json.dumps({
                "required": True, "minVersion": "0.8", "package": "cb",
                "compatibilityLevel": "JAVA_17", "mixins": classes,
                "injectors": {"defaultRequire": 0},
            }, indent=1))
            z.writestr("cb.accesswidener",
                       "accessWidener v2 official\n" + "\n".join(self.aw_lines) + "\n")
            z.writestr("fabric.mod.json", json.dumps(fmj, indent=1))
            for p in embed:  # nested mods stored uncompressed per JIJ convention
                z.write(p, f"META-INF/jars/{p.name}", compress_type=zipfile.ZIP_STORED)
        return jar_out


def main() -> None:
    ap = argparse.ArgumentParser()
    ap.add_argument("cbmods", type=Path)
    ap.add_argument("--deploy", type=Path, default=None, help="mods dir to install the single CB jar into")
    args = ap.parse_args()

    global CACHE
    CACHE = args.cbmods / "recovered"
    CACHE.mkdir(parents=True, exist_ok=True)
    jars = sorted(args.cbmods.glob("*.jar"))
    print(f"cbmods: {len(jars)} jars")

    # mod ids present in cbmods satisfy each other's deps (same-era subgraph)
    present: set[str] = set()
    for j in jars:
        try:
            with zipfile.ZipFile(j) as zf:
                present.add(lenient_json(zf.read("fabric.mod.json")).get("id"))
        except Exception:
            pass

    eng = Engine()
    reports = [eng.process(j, present) for j in jars]
    ok_jars = [CACHE / r["out"] for r in reports if r["verdict"] and r["verdict"].startswith("ok")]
    cb_jar = eng.build_runtime_jar()
    print(f"runtime jar -> {cb_jar.name}; {len(ok_jars)} bridged mods in {CACHE}")

    lines = [f"# CenturyBridge 转换报告（{len(jars)} 个 jar → {TARGET}）", ""]
    lines.append("| 模组 | 判定 | 摘除 mixin | 未覆盖缺失 | 缺失依赖 |")
    lines.append("|------|------|-----------|-----------|----------|")
    for r in reports:
        lines.append("| {} | **{}** | {} | {} | {} |".format(
            r.get("id") or r["file"], r["verdict"],
            len(r.get("stripped_mixins", [])),
            len(r.get("missing", [])) + len(r.get("leftover_tokens", [])),
            ", ".join(r.get("missing_deps", [])) or "-"))
    lines.append("")
    for r in reports:
        if r["verdict"] in (None, "ok_direct"):
            continue
        lines.append(f"## {r.get('id') or r['file']} — {r['verdict']}")
        for n in r.get("notes", []):
            lines.append(f"- {n}")
        for mx in r.get("stripped_mixins", []):
            lb = "（承重！运行可能不稳定）" if mx["load_bearing"] else ""
            lines.append(f"- 摘除 mixin `{mx['class']}`{lb}: {'; '.join(mx['broken'])}")
        for m in r.get("missing", []):
            lines.append(f"- 缺失符号（墓碑桩候选）: `{m}`")
        for t in r.get("leftover_tokens", []):
            lines.append(f"- 未翻译 intermediary 符号: `{t}`")
        if r.get("missing_deps"):
            lines.append(f"- 缺失依赖: {', '.join(r['missing_deps'])} —— 放入 cbmods 或安装新版")
        lines.append("")
    report_path = CACHE / "report.md"
    report_path.write_text("\n".join(lines), encoding="utf-8")
    print(f"report -> {report_path}\n")
    for r in reports:
        print(f"  {r.get('id') or r['file']:30s} {r['verdict']}")

    if args.deploy:
        shutil.copy(cb_jar, args.deploy / cb_jar.name)
        print(f"\ndeployed 1 jar -> {args.deploy / cb_jar.name}")
        add_mods = ";".join(str(p) for p in ok_jars)
        print(f"launch flag: -Dfabric.addMods={add_mods}")


if __name__ == "__main__":
    main()
