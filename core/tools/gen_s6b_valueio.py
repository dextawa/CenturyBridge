"""1.21.5 CompoundTag save data on 1.21.6 ValueInput / ValueOutput, for step 1.21.5 -> 1.21.6.

1.21.6 replaced the CompoundTag of every save/load method (Entity.readAdditionalSaveData /
addAdditionalSaveData / load / saveWithoutId, BlockEntity.loadAdditional / saveAdditional, ...) by a
ValueInput or ValueOutput -- views of one CompoundTag (TagValueInput / TagValueOutput) -- and dropped the
HolderLookup.Provider argument the view now carries. This generator takes every damage row of the step
whose member W keeps under the same name with that change and nothing else, and wires:

  calls (call, call-inherited, call-protected, call-static)     -> instanceRedirects / staticRedirects
      to a static of CbValueIoGen giving W's method a view of the mod's tag (CbValueIo.input /
      output: vanilla's own view when the tag came out of it, else a new one on the lookup the
      call passed or the receiver's registries) and answering V's return (the tag it filled,
      or the new output's tag when V made one);
  overrides (override+super, implement) of a member W still lets mods override
                                                                 -> overrideHooks
      the new shape vanilla calls forwards to the mod's old method with the view's own tag
      ("wrap": CbValueIo.tag, and CbValueIo.lookup for a dropped lookup argument), so what the mod
      reads is what vanilla stored and what it puts is what vanilla saves; the old method's
      super call goes through a super bridge (CbValueIoGen.super$*) to super's new shape with
      vanilla's view.

A parameter pairing is mechanical only when the old and new shapes are equal after replacing each
CompoundTag by one ValueInput / ValueOutput / TagValueOutput in its place and dropping the
HolderLookup.Provider; the return is the same, or V's CompoundTag where W returns nothing. Anything
else is printed ("hand") and left to the fragment s6b.json.

Usage (from the repository root; CB_DATA=<main checkout>/data from a worktree):
  python3 core/tools/gen_s6b_valueio.py
writes core/shims/steps/1.21.6/src/top/dext/centurybridge/step/v1_21_6/rt/CbValueIoGen.java and
core/segments/steps/1.21.5__1.21.6.json.d/s6b-valueio-gen.json, and prints the access widener lines the
statics need (protected W members they call).
"""
import csv
import json
import os
import sys
import zipfile

sys.path.insert(0, os.path.dirname(__file__))
import classfile  # noqa: E402

DATA = os.environ.get("CB_DATA", "data")  # a worktree: CB_DATA=<main checkout>/data
VJAR = DATA + "/jars/client-1.21.5-intermediary-code.jar"
WJAR = DATA + "/jars/client-1.21.6-intermediary.jar"
DAMAGE = "core/segments/damage/1.21.5__1.21.6.damage.tsv"
JAVA = "core/shims/steps/1.21.6/src/top/dext/centurybridge/step/v1_21_6/rt/CbValueIoGen.java"
JSON = "core/segments/steps/1.21.5__1.21.6.json.d/s6b-valueio-gen.json"
RT = "top/dext/centurybridge/step/v1_21_6/rt/"
IO = "top/dext/centurybridge/step/v1_21_6/rt/CbValueIo"
GEN = RT + "CbValueIoGen"

TAG = "Lnet/minecraft/class_2487;"
LOOKUP = "Lnet/minecraft/class_7225$class_7874;"
VIN = "Lnet/minecraft/class_11368;"
VOUT = "Lnet/minecraft/class_11372;"
TOUT = "Lnet/minecraft/class_11362;"
LEVELS = {"Lnet/minecraft/class_1937;", "Lnet/minecraft/class_3218;", "Lnet/minecraft/class_1936;",
          "Lnet/minecraft/class_5425;"}

PUBLIC, PRIVATE, PROTECTED, STATIC, FINAL, INTERFACE, ABSTRACT = 1, 2, 4, 8, 0x10, 0x200, 0x400


def load(jar):
    H = {}
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if n.endswith(".class"):
                c = classfile.read(z.read(n))
                if c:
                    H[c.name] = c
    return H


def declaring(H, c, name, desc):
    """the topmost class or interface above c (c included) that declares name(desc) in V: the member's
    root declaration, which every override below it overrides (hooks go there, calls resolve through it)"""
    seen = set()
    todo = [c]
    found = None
    while todo:
        k = todo.pop(0)
        if k in seen or k not in H:
            continue
        seen.add(k)
        for m in H[k].methods:
            if m.name == name and m.desc == desc and not (m.access & (STATIC | PRIVATE)) or (
                    m.name == name and m.desc == desc and k == c):
                found = (k, m)
        if H[k].super:
            todo.insert(0, H[k].super)
        todo.extend(H[k].interfaces)
    return found if found else (None, None)


def args(desc):
    return classfile.split_args(desc)[0]


def ret(desc):
    return desc[desc.index(")") + 1:]


def jtype(d):
    dims = 0
    while d.startswith("["):
        dims += 1
        d = d[1:]
    prim = {"Z": "boolean", "B": "byte", "C": "char", "S": "short", "I": "int", "J": "long", "F": "float",
            "D": "double", "V": "void"}
    t = prim.get(d) or d[1:-1].replace("/", ".").replace("$", ".")
    return t + "[]" * dims


def pair(vd, wd):
    """per V argument: ('io', w index) / ('lookup', None) / ('same', w index); None when not mechanical"""
    va, wa = args(vd), args(wd)
    out, wi = [], 0
    for p in va:
        if p == TAG:
            if wi < len(wa) and wa[wi] in (VIN, VOUT, TOUT):
                out.append(("io", wi))
                wi += 1
            else:
                return None
        elif p == LOOKUP and not (wi < len(wa) and wa[wi] == LOOKUP):
            out.append(("lookup", None))
        elif wi < len(wa) and wa[wi] == p:
            out.append(("same", wi))
            wi += 1
        else:
            return None
    extra = wa[wi:]
    if extra:
        # V made the output itself (saveWithId(provider) -> saveWithId(ValueOutput)): one trailing new output
        if extra == [VOUT] and ret(vd) == TAG and TAG not in va:
            out.append(("fresh", wi))
        else:
            return None
    vr, wr = ret(vd), ret(wd)
    if vr != wr and not (vr == TAG and wr == "V"):
        return None
    if not any(k in ("io", "fresh") for k, _ in out):
        return None
    return out


def main():
    V, W = load(VJAR), load(WJAR)
    rows = list(csv.DictReader(open(DAMAGE, encoding="utf-8"), delimiter="\t"))
    members = {}  # (decl, name, vdesc) -> set of usages
    hand = set()
    for r in rows:
        vd, w = r["desc"], r["w"]
        if TAG not in vd and "class_2499" not in vd:
            continue
        decl, m = declaring(V, r["owner"], r["name"], vd)
        if decl is None:
            hand.add((r["owner"], r["name"], vd, "not found in V"))
            continue
        members.setdefault((decl, r["name"], vd), set()).add(r["usage"])
    out = {"instanceRedirects": {}, "staticRedirects": {}, "overrideHooks": []}
    java, aw = [], set()
    n = 0
    for (decl, name, vd), uses in sorted(members.items()):
        vm = next(m for m in V[decl].methods if m.name == name and m.desc == vd)
        wc = W.get(decl)
        cands = [m for m in (wc.methods if wc else []) if m.name == name and m.desc != vd]
        wm = None
        for m in cands:
            if pair(vd, m.desc) is not None:
                wm = m
                break
        if wm is None:
            hand.add((decl, name, vd, "W: " + ", ".join(m.desc for m in cands) if cands else "W: gone"))
            continue
        p = pair(vd, wm.desc)
        static = bool(vm.access & STATIC)
        itf = bool(wc.access & INTERFACE)
        va, wa = args(vd), args(wm.desc)
        vr, wr = ret(vd), ret(wm.desc)
        n += 1
        simple = decl.split("/")[-1].replace("$", "_")
        tag_ = f"{name}_{simple}"
        params = ([] if static else [f"{jtype('L' + decl + ';')} self"]) + [f"{jtype(t)} a{i}" for i, t in enumerate(va)]
        # where a view's lookup comes from
        lk = next((f"a{i}" for i, (k, _) in enumerate(p) if k == "lookup"), None)
        if lk is None:
            lev = next((i for i, t in enumerate(va) if t in LEVELS), None)
            lk = "CbValueIo.lookupOf(self)" if not static else (f"CbValueIo.lookupOf(a{lev})" if lev is not None else "null")

        def body(call_on, wargs_from_super=False):
            """statements giving W's method views of the tags; returns lines"""
            lines = ["net.minecraft.class_8942.class_11340 rep = CbValueIo.reporter();", "try {"]
            wargs = [None] * len(wa)
            res = None
            for i, (k, j) in enumerate(p):
                if k == "io":
                    if wa[j] == VIN:
                        lines.append(f"    net.minecraft.class_11368 io{j} = CbValueIo.input(a{i}, {lk}, rep);")
                    else:
                        lines.append(f"    net.minecraft.class_11362 io{j} = CbValueIo.output(a{i}, {lk}, rep);")
                        res = res or f"a{i}"
                    wargs[j] = f"io{j}"
                elif k == "same":
                    wargs[j] = f"a{i}"
                elif k == "fresh":
                    lines.append(f"    net.minecraft.class_11362 io{j} = CbValueIo.fresh({lk}, rep);")
                    wargs[j] = f"io{j}"
                    res = f"CbValueIo.result(io{j})"
            call = f"{call_on}({', '.join(wargs)})"
            if vr == "V":
                lines.append(f"    {call};")
            elif vr == wr:
                lines.append(f"    return {call};")
            else:
                lines.append(f"    {call};")
                lines.append(f"    return {res};")
            lines += ["} finally {", "    CbValueIo.close(rep);", "}"]
            return lines

        # the call redirect
        jr = jtype(vr)
        mname = tag_
        recv = (jtype("L" + decl + ";") + "." if static else "self.") + name
        java.append(f"    /** 1.21.5 {decl}.{name}{vd} on 1.21.6's {name}{wm.desc} */")
        java.append(f"    public static {jr} {mname}({', '.join(params)}) {{")
        java += ["        " + l for l in body(recv)]
        java.append("    }")
        java.append("")
        key = f"{decl}.{name}{vd}"
        (out["staticRedirects"] if static else out["instanceRedirects"])[key] = f"{GEN}#{mname}"
        if wm.access & PROTECTED:
            aw.add(f"accessible method {decl} {name} {wm.desc}")
        overridable = (not static and not (vm.access & (FINAL | PRIVATE)) and not (wm.access & (FINAL | PRIVATE | STATIC))
                       and not (V[decl].access & FINAL))
        if overridable and any(u.startswith(("override", "implement")) for u in uses):
            iface = f"S_{tag_}"
            wps = [f"{jtype(t)} b{j}" for j, t in enumerate(wa)]
            java.append(f"    /** super's 1.21.6 {name}, reached from a mod's super.{name}(..) in 1.21.5's shape */")
            java.append(f"    public interface {iface} {{")
            java.append(f"        {jtype(wr)} cb$super${name}({', '.join(wps)});")
            java.append("    }")
            java.append("")
            sparams = [f"{iface} self"] + [f"{jtype(t)} a{i}" for i, t in enumerate(va)]
            java.append(f"    /** a mod's super.{name}{vd}: super's {name}{wm.desc} with vanilla's view of the tag */")
            java.append(f"    public static {jr} super${mname}({', '.join(sparams)}) {{")
            # the receiver's registries: the super-access object is the mod's entity / block entity
            sb = [l.replace("CbValueIo.lookupOf(self)", "CbValueIo.lookupOf((Object) self)") for l in body(f"self.cb$super${name}")]
            java += ["        " + l for l in sb]
            java.append("    }")
            java.append("")
            wraps, hargs = [], []
            for i, (k, j) in enumerate(p):
                if k == "io":
                    hargs.append(j)
                    w = f"{IO}.tag({wa[j] if wa[j] != TOUT else VOUT}){TAG}"
                    if w not in wraps:
                        wraps.append(w)
                elif k == "same":
                    hargs.append(j)
                elif k == "lookup":
                    src = next(jj for kk, jj in p if kk == "io")
                    hargs.append(src)
                    w = f"{IO}.lookup({wa[src]}){LOOKUP}"
                    if w not in wraps:
                        wraps.append(w)
                else:
                    hargs = None
                    break
            if hargs is None or TOUT in wa:
                hand.add((decl, name, vd, "override with a fresh output / TagValueOutput argument"))
                continue
            h = {"old": name + vd, "new": name + wm.desc, "args": hargs, "onlyUnder": decl,
                 "wrap": wraps if len(wraps) > 1 else wraps[0],
                 "superBridge": f"{GEN}.super${mname}(L{GEN}${iface};{vd[1:]}",
                 "superAccess": f"{GEN}${iface}",
                 "superAccessors": [name + wm.desc],
                 "basis": f"1.21.5 {name}{vd} is 1.21.6's {name}{wm.desc}: the view's own CompoundTag"
                          + (" and lookup" if any(k == "lookup" for k, _ in p) else "")
                          + "; super calls reach super's new shape with vanilla's view (CbValueIo)"}
            if h["args"] == list(range(len(va))) and len(va) == len(wa):
                del h["args"]
            out["overrideHooks"].append(h)
    src = ["package top.dext.centurybridge.step.v1_21_6.rt;", "",
           "/**",
           " * Generated by core/tools/gen_s6b_valueio.py -- do not edit; regenerate with the command in its header.",
           " *",
           " * 1.21.5's CompoundTag save / load methods on 1.21.6's ValueInput / ValueOutput (see CbValueIo).",
           " */",
           "@SuppressWarnings({\"rawtypes\", \"unchecked\"})",
           "public final class CbValueIoGen {", "",
           "    private CbValueIoGen() {", "    }", ""] + java + ["}", ""]
    open(JAVA, "w", encoding="utf-8", newline="\n").write("\n".join(src))
    out = {k: v for k, v in out.items() if v}
    open(JSON, "w", encoding="utf-8", newline="\n").write(json.dumps(out, indent=1) + "\n")
    print(f"{n} members bridged, {len(out.get('overrideHooks', []))} override hooks", file=sys.stderr)
    for h in sorted(hand):
        print("hand\t" + "\t".join(h), file=sys.stderr)
    for a in sorted(aw):
        print(a)


if __name__ == "__main__":
    main()
