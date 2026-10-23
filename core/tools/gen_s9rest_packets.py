"""Step 1.21.3 -> 1.21.4, area s9rest: Packet.type() / Packet.handle(listener) under their new intermediary names.

1.21.4 kept Packet's two abstract members -- type() (PacketType of the packet) and handle(T listener) -- with the
same Mojang names, descriptors and bodies, but Fabric's intermediary gave both new names (method_55846 ->
method_65080, method_11054 -> method_65081) in Packet and in every packet class. A 1.21.3 mod's call of either
names a member W no longer has, and a 1.21.3 packet class's own type() / handle() is never called by 1.21.4.

For every class of the 1.21.3 jar under Packet this checks, from both releases' bytecode, that:
  * the class still exists in 1.21.4 and is still under Packet;
  * each of its 1.21.3 declarations of the old name has, in 1.21.4, a declaration of the new name with the same
    descriptor, access and (constant pool indices aside, the rename applied) the same instructions;
  * 1.21.4 has no member of the old name left on the class.
Then it emits:
  * "methodRenames"  owner.old(desc) -> new for every such class (the class a call names, as the engine looks it up;
                     a call naming a mod class that inherits the member is looked up on the vanilla class it
                     inherits from)
  * "overrideHooks"  old -> new for classes under Packet, same descriptor: a mod packet's own type() / handle()
                     answers 1.21.4's calls through a synthesized method of the new name; its super call (onto
                     a vanilla packet's) is rewritten to the new name. The mod's method keeps its name, so calls
                     naming the mod's own class still resolve.
A class that fails a check is reported and gets no entry (left to hand-written wiring).

  python gen_s9rest_packets.py <V code jar> <W code jar> <out fragment.json>
"""
import json
import os
import re
import subprocess
import sys
import zipfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import classfile  # noqa: E402

PACKET = "net/minecraft/class_2596"
RENAMES = {"method_55846": "method_65080", "method_11054": "method_65081"}
OLD_DESC = {"method_55846": "()Lnet/minecraft/class_9145;", "method_11054": "(Lnet/minecraft/class_2547;)V"}


def load(jar):
    out = {}
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if n.endswith(".class") and n.startswith("net/minecraft/"):
                c = classfile.read(z.read(n))
                if c:
                    out[c.name] = c
    return out


def supers(h, c):
    out, todo = [], [c]
    while todo:
        x = todo.pop(0)
        if x is None or x in out:
            continue
        out.append(x)
        k = h.get(x)
        if k:
            todo.append(k.super)
            todo.extend(k.interfaces)
    return out


def bodies(jar, classes):
    """javap -c of each class's methods: {(class, name, desc): normalized instruction text}"""
    out = {}
    names = sorted(classes)
    for i in range(0, len(names), 60):
        chunk = [n.replace("/", ".") for n in names[i:i + 60]]
        txt = subprocess.run(["javap", "-c", "-p", "-s", "-cp", jar] + chunk, capture_output=True, text=True).stdout
        cls, key, lines = None, None, []
        for line in txt.splitlines():
            m = re.match(r"^(?:public |final |abstract |private |protected |sealed |non-sealed )*(?:class|interface|enum) (\S+)", line)
            if m or line == "}":
                if key and len(key) == 3:
                    out[key] = "\n".join(lines)
                key, lines = None, []
                if m:
                    cls = m.group(1).replace(".", "/").split("<")[0]
                continue
            m = re.match(r"^    descriptor: (\S+)", line)
            if m and key is not None:
                key = (cls, key[1], m.group(1))
                continue
            m = re.match(r"^  \S.*?(\w+)\(.*\);$", line)
            if m:
                if key and len(key) == 3:
                    out[key] = "\n".join(lines)
                key, lines = (cls, m.group(1)), []
                continue
            if key is not None and re.match(r"^\s+\d+: ", line):
                # constant pool indices differ between the two jars; the comment names the referent
                lines.append(re.sub(r"#\d+(,\s*\d+)?", "#", line.split(":", 1)[1].strip()))
        if key and len(key) == 3:
            out[key] = "\n".join(lines)
    return out


def main():
    vjar, wjar, frag = sys.argv[1:4]
    v, w = load(vjar), load(wjar)
    packets = sorted(c for c in v if PACKET in supers(v, c))
    vb = bodies(vjar, packets)
    wb = bodies(wjar, [c for c in packets if c in w])
    renames, problems = {}, []
    for c in packets:
        if c not in w or PACKET not in supers(w, c):
            problems.append(f"{c}: not a 1.21.4 packet class")
            continue
        vm = {(m.name, m.desc): m for m in v[c].methods}
        wm = {(m.name, m.desc): m for m in w[c].methods}
        ok = True
        for old, new in RENAMES.items():
            if any(n == old for n, _ in wm):
                problems.append(f"{c}: 1.21.4 still has {old}")
                ok = False
            for (n, d), m in vm.items():
                if n != old:
                    continue
                x = wm.get((new, d))
                if x is None or x.access != m.access:
                    problems.append(f"{c}.{old}{d}: no {new}{d} of the same access in 1.21.4")
                    ok = False
                    continue
                a = vb.get((c, old, d), "")
                b = wb.get((c, new, d), "")
                for o2, n2 in RENAMES.items():
                    a = a.replace("." + o2 + ":", "." + n2 + ":").replace(" " + o2 + ":", " " + n2 + ":")
                if a != b:
                    problems.append(f"{c}.{old}{d}: body differs\n--- 1.21.3\n{a}\n--- 1.21.4\n{b}")
                    ok = False
        if ok:
            for old, new in RENAMES.items():
                renames[f"{c}.{old}{OLD_DESC[old]}"] = new
    hooks = [{
        "old": old + OLD_DESC[old], "new": new + OLD_DESC[old], "onlyUnder": PACKET, "superCalls": "rewrite",
        "basis": "generated (gen_s9rest_packets.py): 1.21.4's Packet." + ("type()" if old == "method_55846" else "handle(listener)")
                 + " under its new intermediary name, same descriptor and body in every packet class; a 1.21.3 packet's"
                 " own method answers the new name, its super call goes to the new name"}
        for old, new in RENAMES.items()]
    out = {"methodRenames": dict(sorted(renames.items())), "overrideHooks": hooks}
    with open(frag, "w", encoding="utf-8", newline="\n") as o:
        json.dump(out, o, indent=1)
        o.write("\n")
    print(f"{len(packets)} packet classes in 1.21.3, {len(renames)} renames, {len(problems)} problems -> {frag}")
    for p in problems:
        print("  " + p)


if __name__ == "__main__":
    main()
