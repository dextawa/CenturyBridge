"""Compile forged bridge bodies against the real stub. javac is the judge.

Each body is assembled into a standalone probe class and compiled against the
target-version stub jar. This is the same compiler-as-detector trick the shim
tree already uses: a body that references a member the new version does not
have fails here, at build time, instead of as a NoSuchMethodError under a
player three hours into a world.

Survivors are emitted as ready-to-paste shim sources plus the JSON wiring;
failures carry their compiler error back to forge.py for a retry round.

  verify.py <forged.json> <stubJar> <outDir> [--retry-out <retry.json>]
"""
import json
import os
import re
import subprocess
import sys
import tempfile

PROBE = """package cbprobe;

@SuppressWarnings({{"unused", "deprecation", "removal", "unchecked", "rawtypes"}})
public class {cls} {{
{member}
}}
"""


def probe_source(rec, idx):
    sig = rec["javaSig"]
    body = rec["body"] or ""
    if rec["shimKind"] == "MIXIN_OVERLOAD":
        # `this` is the mixin target at runtime; in the probe it is a plain field
        # so the cast idiom still type-checks
        owner = "net.minecraft." + rec["owner"].split("/")[-1].replace("$", ".")
        member = (f"  private final Object thisRef = null;\n"
                  f"  private Object thisObj() {{ return thisRef; }}\n"
                  f"  {sig} {{\n{indent(body)}\n  }}")
        member = member.replace("(Object) this", "(Object) thisObj()")
    else:
        member = f"  {sig} {{\n{indent(body)}\n  }}"
    return PROBE.format(cls=f"P{idx}", member=member)


def indent(body):
    return "\n".join("    " + line for line in body.splitlines())


def main():
    forged_path, stub, out_dir = sys.argv[1], sys.argv[2], sys.argv[3]
    retry_out = None
    if "--retry-out" in sys.argv:
        retry_out = sys.argv[sys.argv.index("--retry-out") + 1]

    recs = [r for r in json.load(open(forged_path, encoding="utf-8")) if r.get("body")]
    os.makedirs(out_dir, exist_ok=True)

    cp_extra = os.environ.get("CB_VERIFY_CP", "")
    cp = stub + (os.pathsep + cp_extra if cp_extra else "")

    ok, bad = [], []
    batch = 60
    for start in range(0, len(recs), batch):
        chunk = recs[start:start + batch]
        with tempfile.TemporaryDirectory() as tmp:
            src_dir = os.path.join(tmp, "cbprobe")
            os.makedirs(src_dir)
            paths = {}
            for i, rec in enumerate(chunk):
                idx = start + i
                p = os.path.join(src_dir, f"P{idx}.java")
                with open(p, "w", encoding="utf-8") as f:
                    f.write(probe_source(rec, idx))
                paths[idx] = (p, rec)
            # compile the batch; javac reports every file's errors in one pass
            proc = subprocess.run(
                ["javac", "-nowarn", "-proc:none", "-cp", cp, "-d", tmp]
                + [p for p, _ in paths.values()],
                capture_output=True, text=True)
            errs = {}
            for line in (proc.stderr or "").splitlines():
                m = re.match(r".*[/\\]P(\d+)\.java:(\d+): (?:error|错误)[:：](.*)", line)
                if m:
                    errs.setdefault(int(m.group(1)), []).append(
                        f"line {m.group(2)}: {m.group(3).strip()}")
            for idx, (_, rec) in paths.items():
                if idx in errs:
                    bad.append({**rec, "compileError": "\n".join(errs[idx][:4])})
                else:
                    ok.append(rec)
        print(f"  verified {min(start + batch, len(recs))}/{len(recs)}  "
              f"ok={len(ok)} bad={len(bad)}", flush=True)

    json.dump(ok, open(os.path.join(out_dir, "verified.json"), "w", encoding="utf-8"),
              indent=1, ensure_ascii=False)
    if retry_out:
        json.dump(bad, open(retry_out, "w", encoding="utf-8"), indent=1, ensure_ascii=False)

    tomb = sum(1 for r in ok if "UnsupportedOperationException" in (r["body"] or ""))
    print(f"\nverified {len(ok)}/{len(recs)} bodies compile "
          f"({tomb} are honest tombstones), {len(bad)} rejected")
    if bad:
        print("sample rejections:")
        for r in bad[:5]:
            print(f"  {r['owner']}.{r['name']}{r['oldDesc']}")
            print(f"    {r['compileError'].splitlines()[0]}")


if __name__ == "__main__":
    main()
