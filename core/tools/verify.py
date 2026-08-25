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

    # Reasoning models sometimes emit their scratchpad as the answer. javac
    # catches most of it, but a stray batch can slip through when the error map
    # misses a file -- and prose that reaches the shim tree breaks the whole
    # build, not one method. Reject it explicitly before compiling.
    def is_code(body):
        """Every line must plausibly be Java. Reasoning models leak their
        scratchpad into the answer, and prose that reaches the shim tree breaks
        the entire build rather than one method, so screen it before javac."""
        for line in body.splitlines():
            t = line.strip()
            if not t or t.startswith("//") or t.startswith("*") or t.startswith("/*"):
                continue
            if "`" in t:
                return False
            # a statement ends in ; { } or , -- anything else is narration
            if not t.endswith((";", "{", "}", ",", ")", ":")):
                return False
            # English sentences that happen to end in a paren or colon
            if re.match(r"^(?:Wait|Actually|Let|Hmm|Looking|So|But|Or|Ah|I |The |In |This |That |It |We |You )", t):
                return False
        return True

    OBF_CALL = re.compile(r"\.\s*([a-z]{1,3}\d?)\s*\(")

    def calls_obf(body):
        """Intermediary only names members with a stable identity; everything
        else keeps a short obfuscated name that changes every release. Such a
        call compiles against the stub we happen to hold and then throws
        NoSuchMethodError on the real jar -- and if vanilla itself calls the
        method we overrode, the world dies on the first tick. This is exactly
        how class_1297.cw() reached a player."""
        for m in OBF_CALL.finditer(body or ""):
            name = m.group(1)
            if name in ("get", "put", "add", "map", "of", "abs", "min", "max",
                        "pow", "run", "set", "log", "cos", "sin", "tan"):
                continue
            return name
        return None

    screened, rejected_prose = [], []
    for r in recs:
        obf = calls_obf(r["body"] or "")
        if obf:
            rejected_prose.append({**r, "compileError":
                f"calls unmapped obfuscated name '{obf}' -- will not exist at runtime"})
        elif is_code(r["body"] or ""):
            screened.append(r)
        else:
            rejected_prose.append({**r, "compileError": "body is prose, not code"})
    if rejected_prose:
        print(f"  screened out {len(rejected_prose)} prose bodies before javac")
    recs = screened

    # The stub alone is not the compile environment a shim really sees: bodies
    # legitimately touch gson, DFU, authlib and friends, and without them on the
    # path javac rejects correct code for a missing import. Pull in whatever the
    # game runtime ships so a rejection means the BODY is wrong.
    cp_parts = [stub]
    extra = os.environ.get("CB_VERIFY_CP", "")
    if extra:
        cp_parts.append(extra)
    for root in ("data/runtime/client-1.20.4/libraries",
                 "data/runtime/server-1.20.4/libraries"):
        if os.path.isdir(root):
            for dirpath, _, files in os.walk(root):
                cp_parts.extend(os.path.join(dirpath, f)
                                for f in files if f.endswith(".jar"))
            break
    cp = os.pathsep.join(cp_parts)

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
    bad.extend(rejected_prose)
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
