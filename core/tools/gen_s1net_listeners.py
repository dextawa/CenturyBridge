"""Listener and packet-handler overrides of climb step 1.20.1 -> 1.20.2 (area s1net), from the jars.

Two mechanical rules, each written into the area's wiring fragment (entries a rule wrote before are
replaced; nothing else in the fragment is touched):

1. Renamed listener methods. 1.20.2 split the play packet listeners and their implementations: what
   every phase shares -- keep-alive, ping, custom payload, disconnect, resource packs, sending --
   moved from ClientGamePacketListener / ClientPacketListener (and the server twins) to the new
   ClientCommonPacketListener / ClientCommonPacketListenerImpl (ServerCommon...), under new
   intermediary names with the same descriptors. For every method an OWNERS class declared in
   1.20.1 that 1.20.2 no longer has under that name and descriptor, the rename is the one 1.20.2
   method of the same descriptor among the owner's 1.20.2 supertypes that no 1.20.1 supertype had:
     methodRenames  owner.old(desc) -> new     calls through any of the owners
     overrideHooks  old(desc) -> new(desc)     mod overrides / implementations: the new shape vanilla
                                               calls forwards to the mod's method (frames), and its
                                               super.old(..) becomes super.new(..)

2. Widened parameters. A method of an area class that 1.20.2 kept under its name but whose
   parameters it widened to supertypes, the return type unchanged (a packet's
   handle(ClientGamePacketListener) became handle(ClientCommonPacketListener): the packet now also
   arrives in the configuration phase). The call side is mechanical.json's callReshapes; the
   override side is a narrowing hook: vanilla's value reaches the mod's method only when it is of
   the 1.20.1 type, anything else goes to super as if the mod had not overridden it.

Methods rule 1 finds no counterpart for are listed; they need a hand-written bridge or a ledger reason.

  python gen_s1net_listeners.py <V stub jar> <W stub jar> <class-names.tsv> <area regex> <fragment.json> <frames class>
"""
import json
import os
import re
import sys
import zipfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import classfile  # noqa: E402
from classfile import ACC_BRIDGE, ACC_FINAL, ACC_PRIVATE, ACC_STATIC, ACC_SYNTHETIC, split_args  # noqa: E402

# the 1.20.1 listener interfaces and their vanilla implementations, topmost first
OWNERS = [
    "net/minecraft/class_2602",  # ClientGamePacketListener
    "net/minecraft/class_634",   # ClientPacketListener
    "net/minecraft/class_2792",  # ServerGamePacketListener
    "net/minecraft/class_3244",  # ServerGamePacketListenerImpl
]
RENAMED = "s1net listeners: same descriptor, moved to the 1.20.2 common listener under a new name"
WIDENED = "s1net listeners: parameter widened to a supertype; forwarded only for a value of the 1.20.1 type"
SKIP = ACC_STATIC | ACC_PRIVATE | ACC_BRIDGE | ACC_SYNTHETIC


def load(jar):
    out = {}
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if n.startswith("net/minecraft/") and n.endswith(".class"):
                c = classfile.read(z.read(n))
                if c:
                    out[c.name] = c
    return out


def supers(classes, c):
    out, todo = [], [c]
    while todo:
        x = todo.pop(0)
        if x is None or x in out or x not in classes:
            continue
        out.append(x)
        todo.append(classes[x].super)
        todo.extend(classes[x].interfaces)
    return out


def shapes(classes, owners):
    """name(desc) -> declaring classes, overridable members only"""
    got = {}
    for c in owners:
        for m in classes[c].methods:
            if m.access & SKIP or m.name.startswith("<"):
                continue
            got.setdefault(m.name + m.desc, []).append(c)
    return got


def renamed(V, W):
    renames, hooks, unmatched = {}, {}, []
    for owner in OWNERS:
        v_shapes, w_shapes = shapes(V, supers(V, owner)), shapes(W, supers(W, owner))
        for m in V[owner].methods:
            if m.access & SKIP or m.name.startswith("<") or m.name + m.desc in w_shapes:
                continue
            cands = sorted({s[:s.index("(")] for s in w_shapes
                            if s[s.index("("):] == m.desc and s not in v_shapes})
            if len(cands) != 1:
                unmatched.append(f"{owner}.{m.name}{m.desc}: {len(cands)} 1.20.2 candidates {cands}")
                continue
            renames[f"{owner}.{m.name}{m.desc}"] = cands[0]
            if (m.name, m.desc) not in hooks:  # one hook per shape, on the topmost 1.20.1 declarer
                hooks[(m.name, m.desc)] = {
                    "old": m.name + m.desc, "new": cands[0] + m.desc,
                    "args": list(range(len(split_args(m.desc)[0]))),
                    "onlyUnder": owner, "superCalls": "rewrite", "basis": RENAMED}
    return renames, list(hooks.values()), unmatched


def widened(V, W, area_classes):
    hooks = []
    for c in sorted(area_classes):
        if c not in W or W[c].access & ACC_FINAL and c not in EXTENDABLE:
            continue  # nothing can subclass it on 1.20.2
        w_shapes = shapes(W, supers(W, c))
        for m in V[c].methods:
            if m.access & SKIP or m.name.startswith("<") or m.name + m.desc in w_shapes:
                continue
            oa, orr = split_args(m.desc)
            for wm in W[c].methods:
                if wm.name != m.name or wm.access & SKIP:
                    continue
                na, nr = split_args(wm.desc)
                if len(na) != len(oa) or nr != orr or na == oa:
                    continue
                if all(a == b or a.startswith("L") and b.startswith("L") and b[1:-1] in supers(W, a[1:-1])
                       for a, b in zip(oa, na)):
                    hooks.append({"old": m.name + m.desc, "new": wm.name + wm.desc, "narrow": True,
                                  "onlyUnder": c, "basis": WIDENED})
    return hooks


# 1.20.2 records this area makes extendable again (cb.accesswidener): a subclass can exist
EXTENDABLE = set()


def main():
    vjar, wjar, names, area, frag_path, frames = sys.argv[1:7]
    V, W = load(vjar), load(wjar)
    cm = {}
    for line in open(names, encoding="utf-8"):
        f = line.rstrip("\n").split("\t")
        if len(f) >= 3 and f[0] == "C":
            cm[f[1]] = f[2]
    aw = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(frag_path))), "..", "shims", "1.20.2",
                      "resources", "cb.accesswidener")
    if os.path.exists(aw):
        for line in open(aw, encoding="utf-8"):
            p = line.split()
            if len(p) >= 3 and p[0] == "extendable" and p[1] == "class":
                EXTENDABLE.add(p[2])

    def moj(c):
        if c in cm:
            return cm[c]
        d = c.rfind("$")
        return moj(c[:d]) + c[d:] if d > 0 else c

    area_classes = {c for c in V if re.search(area, moj(c))}
    renames, rhooks, unmatched = renamed(V, W)
    for h in rhooks:
        h["frames"] = frames
    whooks = widened(V, W, area_classes)
    frag = json.load(open(frag_path, encoding="utf-8")) if os.path.exists(frag_path) else {}
    frag.setdefault("methodRenames", {}).update(renames)
    oh = [h for h in frag.get("overrideHooks", []) if h.get("basis") not in (RENAMED, WIDENED)]
    frag["overrideHooks"] = oh + rhooks + whooks
    with open(frag_path, "w", encoding="utf-8", newline="\n") as f:
        f.write(json.dumps(frag, indent=1, ensure_ascii=False) + "\n")
    print(f"{len(renames)} method renames, {len(rhooks)} rename hooks, {len(whooks)} narrowing hooks -> {frag_path}")
    for u in unmatched:
        print("  no rename:", u)
    for h in whooks:
        print("  narrow:", h["onlyUnder"], h["old"], "->", h["new"])


if __name__ == "__main__":
    main()
