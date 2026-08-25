"""Remove generated mixin files whose @Mixin target class no longer exists.

prune_failed.py maps javac errors to method spans; an error on the @Mixin
annotation line sits outside every span, so a dead TARGET class needs the
whole file cut -- bridges, wiring, config entry -- with each symbol recorded
for re-forge.

  python3 prune_dead_targets.py <javac-errors.txt> <shims.json> <mixin-config.json> <retry-out.json>
"""
import io
import json
import os
import re
import sys


def main():
    err_path, shims_path, cfg_path, retry_path = sys.argv[1:5]
    errs = io.open(err_path, encoding="utf-8", errors="replace").read()
    dead = set()
    pat = (r"(core[^ ]+\.java):\d+: error: cannot find symbol\n"
           r"@Mixin\(net\.minecraft\.(class_[\d$.]+)\.class\)")
    for m in re.finditer(pat, errs):
        dead.add((m.group(1).replace("\\", "/"), m.group(2)))
    if not dead:
        print("no dead-target files in error output")
        return

    shims = json.load(io.open(shims_path, encoding="utf-8"))
    cfg = json.load(io.open(cfg_path, encoding="utf-8"))
    retry = []
    try:
        retry = json.load(io.open(retry_path, encoding="utf-8"))
    except Exception:
        pass

    for path, target in sorted(dead):
        text = io.open(path, encoding="utf-8").read()
        syms = re.findall(r"/\*\* was (\S+?) \*/", text)
        for sym in syms:
            flat = sym.replace(":", "")
            if sym in shims.get("covers", []):
                shims["covers"].remove(sym)
            for mk in ("staticRedirects", "instanceRedirects", "fieldRedirects"):
                mp = shims.get(mk, {})
                for k in [k for k in mp if k.replace(":", "") == flat]:
                    del mp[k]
            retry.append({"symbol": sym,
                          "compileErrors": [f"@Mixin target {target} gone at this boundary"]})
        cls = os.path.basename(path)[:-5]
        if cls in cfg.get("mixins", []):
            cfg["mixins"].remove(cls)
        os.remove(path)
        print(f"removed {path} ({len(syms)} bridges, target {target})")

    json.dump(shims, io.open(shims_path, "w", encoding="utf-8", newline="\n"),
              indent=1, ensure_ascii=False)
    json.dump(cfg, io.open(cfg_path, "w", encoding="utf-8", newline="\n"), indent=1)
    json.dump(retry, io.open(retry_path, "w", encoding="utf-8", newline="\n"),
              indent=1, ensure_ascii=False)


if __name__ == "__main__":
    main()
