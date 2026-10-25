"""1.21.8 -> 1.21.9: GameProfile parameters that became NameAndId (player lists, ops / bans / whitelist, the server's
op-level and singleplayer-owner checks, the user cache).

For every damage row whose member W keeps under the same name with each GameProfile of its descriptor replaced by
NameAndId (net/minecraft/class_11560), this writes
  - callReshapes / superCtorReshapes on the row's owner: each GameProfile argument converted (CbProfiles.toNameAndId),
    a NameAndId answer converted back (CbProfiles.toProfile);
  - overrideHooks (wrap / unwrap, guarded) on the topmost V owner among the rows naming the member: the 1.21.9 shape
    vanilla calls hands the old override a GameProfile (CbProfiles.toProfile: the online player's own profile, else
    id and name);
plus a fieldGetters entry for MinecraftServer.ANONYMOUS_PLAYER_PROFILE.

  python3 core/tools/gen_s9profile.py <damage.tsv> <V code jar> <W stub jar> <out fragment.json>
  (core/segments/damage/1.21.8__1.21.9.damage.tsv data/jars/client-1.21.8-intermediary-code.jar
   data/jars/client-1.21.9-intermediary.jar core/segments/steps/1.21.8__1.21.9.json.d/s9-profile-gen.json)
"""
import json
import os
import sys
import zipfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import classfile  # noqa: E402
from classfile import ACC_STATIC, split_args  # noqa: E402

GP = "Lcom/mojang/authlib/GameProfile;"
NI = "Lnet/minecraft/class_11560;"
RT = "top/dext/centurybridge/step/v1_21_9/rt/CbProfiles"
TO_NI = RT + "#toNameAndId(" + GP + ")" + NI
TO_GP = RT + "#toProfile(" + NI + ")" + GP


def load(jar):
    out = {}
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if n.endswith(".class") and n.startswith("net/minecraft/"):
                k = classfile.read(z.read(n))
                if k:
                    out[k.name] = k
    return out


def supers(classes, c):
    out, todo = [], [c]
    while todo:
        x = todo.pop(0)
        if x is None or x in out:
            continue
        out.append(x)
        k = classes.get(x)
        if k:
            todo.append(k.super)
            todo.extend(k.interfaces)
    return out


def find(classes, owner, name, desc):
    for s in supers(classes, owner):
        k = classes.get(s)
        if k:
            for m in k.methods:
                if m.name == name and m.desc == desc:
                    return m
    return None


def main():
    dmg, vjar, wjar, out = sys.argv[1:5]
    V, W = load(vjar), load(wjar)
    members = {}
    for line in open(dmg, encoding="utf-8"):
        p = line.rstrip("\n").split("\t")
        if p[0] == "usage" or "(" not in p[3] or GP not in p[3]:
            continue
        members.setdefault((p[2], p[3]), set()).add(p[1])
    frag = {"callReshapes": {}, "superCtorReshapes": {}, "overrideHooks": [], "fieldGetters": {}}
    skipped = []
    for (name, desc), owners in sorted(members.items()):
        new = desc.replace(GP, NI)
        args, _ = split_args(desc)
        ops = ["conv " + TO_NI if a == GP else "keep" for a in args]
        old_ret, new_ret = desc[desc.index(")") + 1:], new[new.index(")") + 1:]
        live = sorted(o for o in owners if find(W, o, name, new))
        for o in sorted(owners - set(live)):
            skipped.append(f"{o}.{name}{desc}: W has no {name}{new}")
        if not live:
            continue
        for o in live:
            e = {"desc": new, "ops": ops, "basis": "s9 profile: 1.21.9 takes the id and name (NameAndId) where 1.21.8 took the GameProfile"}
            if name == "<init>":
                frag["superCtorReshapes"][f"{o}.{name}{desc}"] = e
                continue
            if old_ret != new_ret:
                e["ret"] = "conv " + TO_GP
            frag["callReshapes"][f"{o}.{name}{desc}"] = e
        if name == "<init>":
            continue
        vm = find(V, live[0], name, desc)
        if vm is None or vm.access & ACC_STATIC:
            continue
        changed = [i for i, a in enumerate(args) if a == GP]
        if len(changed) > 1:
            skipped.append(f"{name}{desc}: {len(changed)} profile arguments, no single wrap")
            continue
        tops = [o for o in live if not any(s in live for s in supers(V, o)[1:])]
        for top in tops:
            h = {"old": name + desc, "new": name + new, "onlyUnder": top,
                 "guard": "top/dext/centurybridge/step/v1_21_9/rt/Guard",
                 "basis": "s9 profile: vanilla calls the NameAndId shape; the old override gets a GameProfile (CbProfiles.toProfile: the online player's own, else id and name)"}
            if changed:
                h["wrap"] = RT + ".toProfile(" + NI + ")" + GP
            if old_ret != new_ret:
                h["unwrap"] = RT + ".toNameAndId(" + GP + ")" + NI
            frag["overrideHooks"].append(h)
    frag["fieldGetters"]["net/minecraft/server/MinecraftServer.field_35641:" + GP] = RT
    with open(out, "w", encoding="utf-8", newline="\n") as f:
        json.dump(frag, f, indent=1, sort_keys=True)
        f.write("\n")
    print(f"{len(frag['callReshapes'])} callReshapes, {len(frag['superCtorReshapes'])} superCtorReshapes, "
          f"{len(frag['overrideHooks'])} overrideHooks -> {out}")
    for s in skipped:
        print("  skipped " + s)


if __name__ == "__main__":
    main()
