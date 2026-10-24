"""Ledger the library damage rows a mod cannot reach as API, with the library's own evidence.

  python3 core/tools/gen_lib_internal.py <librariesRoot> <V> <W> <out.json>

A row of core/segments/damage-lib/<V>__<W>.tsv is judged unreachable to mods only when V's jar of that artifact
says its class is not public API:
  - its package is not among the packages the jar's OSGi Export-Package manifest header declares (the
    library's own statement of its public packages: ICU's com.ibm.icu.impl, Gson's com.google.gson.internal);
  - the class carries the library's internal-API marker (log4j's @org.apache.logging.log4j.util.InternalApi);
  - the class or a class enclosing it is not public in its class file (javac cannot name it from another
    package: JOML's package-private MemUtil and its nested MemUtilNIO / MemUtilUnsafe).
Every other row stays open. The output is a generated wiring fragment ("ledger" only):
core/segments/steps/<V>__<W>.json.d/lib-internal-gen.json (step 1: core/segments/shims-1.20.2.json.d/).
<librariesRoot>/<V> is V's libraries directory (data/runtime/client-<V>/libraries; 1.20.1's own libraries are
in ~/cbtest/client-1.20.1/libraries on beta2).
"""
import json
import os
import re
import sys
import zipfile

INTERNAL_MARKERS = {"Lorg/apache/logging/log4j/util/InternalApi;": "log4j's @InternalApi"}


def manifest_exports(z):
    try:
        mf = z.read("META-INF/MANIFEST.MF").decode("utf-8", "replace")
    except KeyError:
        return None
    joined = re.sub(r"\r?\n ", "", mf)
    for line in joined.splitlines():
        if line.startswith("Export-Package:"):
            body, pkgs, quoted, cur = line[len("Export-Package:"):], [], False, ""
            for ch in body:
                if ch == '"':
                    quoted = not quoted
                if ch == "," and not quoted:
                    pkgs.append(cur)
                    cur = ""
                else:
                    cur += ch
            pkgs.append(cur)
            return {p.split(";")[0].strip().replace(".", "/") for p in pkgs if p.strip()}
    return None


def class_info(b):
    """(access flags, set of annotation descriptors in RuntimeInvisible/Visible class annotations) of a class file"""
    n = int.from_bytes(b[8:10], "big")
    i, idx, utf8 = 10, 1, {}
    while idx < n:
        tag = b[i]
        if tag == 1:
            ln = int.from_bytes(b[i + 1:i + 3], "big")
            utf8[idx] = b[i + 3:i + 3 + ln].decode("utf-8", "replace")
            i += 3 + ln
        elif tag in (3, 4):
            i += 5
        elif tag in (5, 6):
            i += 9
            idx += 1
        elif tag in (7, 8, 16, 19, 20):
            i += 3
        elif tag == 15:
            i += 4
        else:
            i += 5
        idx += 1
    access = int.from_bytes(b[i:i + 2], "big")
    i += 6
    nif = int.from_bytes(b[i:i + 2], "big")
    i += 2 + 2 * nif

    def skip_members(i):
        cnt = int.from_bytes(b[i:i + 2], "big")
        i += 2
        for _ in range(cnt):
            i += 6
            na = int.from_bytes(b[i:i + 2], "big")
            i += 2
            for _ in range(na):
                i += 6 + int.from_bytes(b[i + 2:i + 6], "big")
        return i

    i = skip_members(skip_members(i))
    na = int.from_bytes(b[i:i + 2], "big")
    i += 2
    annos = set()
    for _ in range(na):
        name = utf8.get(int.from_bytes(b[i:i + 2], "big"))
        ln = int.from_bytes(b[i + 2:i + 6], "big")
        body = b[i + 6:i + 6 + ln]
        if name in ("RuntimeInvisibleAnnotations", "RuntimeVisibleAnnotations"):
            # a marker annotation has no elements: its type index appears in the attribute body; a constant-pool
            # entry naming the marker exists only because something in the class refers to it
            for k in range(2, len(body) - 1):
                d = utf8.get(int.from_bytes(body[k:k + 2], "big"))
                if d in INTERNAL_MARKERS:
                    annos.add(d)
        i += 6 + ln
    return access, annos


def main():
    root, v, w, out = sys.argv[1:5]
    here = os.path.dirname(os.path.abspath(__file__))
    seg = os.path.join(here, "..", "segments")
    damage = os.path.join(seg, "damage-lib", f"{v}__{w}.tsv")
    versions = {}
    for line in open(os.path.join(seg, "damage-lib", "artifacts.tsv"), encoding="utf-8"):
        f = line.rstrip("\n").split("\t")
        if len(f) == 4 and f[0] == v:
            versions[f[1]] = f[2]
    rows = [l.rstrip("\n").split("\t") for l in open(damage, encoding="utf-8")
            if not l.startswith("#") and not l.startswith("artifact\t")]
    jars, exports = {}, {}
    for art in {r[0] for r in rows}:
        ver = versions.get(art)
        p = os.path.join(root, *art.split("/"), ver, f"{art.split('/')[-1]}-{ver}.jar") if ver else None
        if p and os.path.exists(p):
            jars[art] = zipfile.ZipFile(p)
            exports[art] = manifest_exports(jars[art])
    ledger = {}
    for r in rows:
        art, kind, sym = r[0], r[2], r[3]
        if art not in jars or sym in ledger:
            continue
        cls = sym.split(".", 1)[0]
        pkg = cls[:cls.rfind("/")]
        name, ver = art.split("/")[-1], versions[art]
        exp = exports[art]
        reason = None
        if exp is not None and pkg not in exp:
            reason = (f"internal to {name} {ver}: package {pkg} is not among the {len(exp)} packages the jar's OSGi "
                      f"Export-Package manifest header declares public")
        else:
            z, c = jars[art], cls
            while reason is None:
                try:
                    access, annos = class_info(z.read(c + ".class"))
                except KeyError:
                    break
                marks = [INTERNAL_MARKERS[a] for a in annos if a in INTERNAL_MARKERS]
                if marks:
                    reason = f"internal to {name} {ver}: {c} carries {marks[0]} (the library's marker for non-public API)"
                elif not access & 0x0001:
                    reason = (f"not nameable by a mod: {c} is not public in {name} {ver}'s class file (access 0x{access:04x})"
                              + ("" if c == cls else f", so javac cannot name {cls} from another package"))
                if "$" not in c:
                    break
                c = c[:c.rfind("$")]
        if reason:
            ledger[sym] = reason
            # the class as a whole is no more mod-reachable than its member: every site through it (an implementation of
            # an internal interface, a field read) is judged with it
            if cls != sym and cls not in ledger:
                ledger[cls] = reason
    step = f"{v}__{w}"
    frag = {"_lib_internal_basis": f"generated by core/tools/gen_lib_internal.py from core/segments/damage-lib/{step}.tsv"
            " and V's library jars -- regenerate, never hand-edit", "ledger": dict(sorted(ledger.items()))}
    with open(out, "w", encoding="utf-8", newline="\n") as f:
        json.dump(frag, f, indent=1)
        f.write("\n")
    print(f"{step}: {len(ledger)} ledger entries (rows and their classes) of {len(rows)} rows judged not mod-reachable API -> {out}")


if __name__ == "__main__":
    main()
