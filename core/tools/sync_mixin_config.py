"""List every compiled @Mixin of a shim tree in its mixin config, on the right side.

A mixin source the config does not name compiles, passes mixin-check and
satisfies the covers audit, yet never loads (build.sh now refuses that). This
adds the missing names. Side: a mixin goes to "client" when its target, or any
net/minecraft class its bytecode mentions, is absent from the dedicated server
jar -- applying it on a server would load a class that is not there.

  python3 sync_mixin_config.py <ver> <compiledClassesDir> <server-intermediary.jar> [--check]

--check: only report how the existing entries would be classified (sanity
check of the side rule against hand-placed entries), write nothing.
"""
import glob, json, os, re, struct, sys, zipfile

ver, classes, server_jar = sys.argv[1:4]
check = "--check" in sys.argv
shim = f"core/shims/{ver}"
cfg_p = glob.glob(f"{shim}/resources/*.mixins.json")[0]
cfg = json.load(open(cfg_p, encoding="utf-8"))
pkg = cfg["package"].replace(".", "/")

with zipfile.ZipFile(server_jar) as z:
    server = {n[:-6] for n in z.namelist() if n.endswith(".class")}


def class_refs(data):
    """every class name the constant pool mentions (Class entries + descriptors)"""
    cnt = struct.unpack(">H", data[8:10])[0]
    idx, i = 10, 1
    utf8, cls = {}, []
    sz = {7: 3, 8: 3, 16: 3, 19: 3, 20: 3, 15: 4, 9: 5, 10: 5, 11: 5, 12: 5, 17: 5, 18: 5, 3: 5, 4: 5}
    while i < cnt:
        t = data[idx]
        if t == 1:
            n = struct.unpack(">H", data[idx + 1:idx + 3])[0]
            utf8[i] = data[idx + 3:idx + 3 + n].decode("utf-8", "replace")
            idx += 3 + n
        elif t in (5, 6):
            idx += 9; i += 2; continue
        else:
            if t == 7:
                cls.append(struct.unpack(">H", data[idx + 1:idx + 3])[0])
            idx += sz.get(t, 3)
        i += 1
    names = {utf8.get(c, "") for c in cls}
    for s in utf8.values():  # descriptors, signatures, annotation Type values
        names.update(re.findall(r"L(net/minecraft/[\w/$]+);", s))
    return {n for n in names if n.startswith("net/minecraft/")}


def side_of(name):
    p = os.path.join(classes, pkg, name.replace(".", "/") + ".class")
    if not os.path.exists(p):
        return None
    missing = sorted(r for r in class_refs(open(p, "rb").read()) if r not in server)
    return ("client", missing) if missing else ("mixins", [])


listed = {m: side for side in ("mixins", "client", "server") for m in cfg.get(side, [])}
if check:
    agree = disagree = 0
    for m, side in sorted(listed.items()):
        r = side_of(m)
        if r is None:
            continue
        if r[0] == side or (side == "server" and r[0] == "mixins"):
            agree += 1
        else:
            disagree += 1
            print(f"  {m}: config={side} rule={r[0]} {r[1][:3]}")
    print(f"side rule vs existing entries: {agree} agree, {disagree} disagree")
    sys.exit(0)

srcdir = os.path.join(shim, "src", pkg)
added = {"mixins": [], "client": []}
for f in sorted(os.listdir(srcdir)):
    if not f.endswith(".java") or f[:-5] in listed:
        continue
    if "@Mixin" not in open(os.path.join(srcdir, f), encoding="utf-8").read():
        continue
    r = side_of(f[:-5])
    if r is None:
        sys.exit(f"{f[:-5]}: not compiled under {classes} -- build first")
    added[r[0]].append(f[:-5])
for side, names in added.items():
    cfg[side] = sorted(set(cfg.get(side, [])) | set(names))
json.dump(cfg, open(cfg_p, "w", encoding="utf-8", newline="\n"), indent=2)
open(cfg_p, "a", encoding="utf-8", newline="\n").write("\n")
print(f"{cfg_p}: +{len(added['mixins'])} mixins, +{len(added['client'])} client")
