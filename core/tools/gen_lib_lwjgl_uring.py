#!/usr/bin/env python3
"""gen_lib_lwjgl_uring.py: LWJGL 3.3.3 (1.21.11) -> 3.4.1 (26.1), the liburing struct accessors 3.4.1 hid.

  python3 core/tools/gen_lib_lwjgl_uring.py <V libraries dir> <W libraries dir>

LWJGL 3.4.1 regenerated its liburing bindings from newer kernel headers. For the reserved / padding members of the
io_uring structs it dropped the instance accessors (IOURingBuf.resv(), IOURingParams.resv(int, int), their $Buffer twins)
and the set(...) overloads that took them, but kept the static n-accessors at the same offsets (IOURingBuf.nresv(long)).
Every such row of core/segments/damage-lib/1.21.11__26.1.tsv is bridged from 3.3.3's own bytecode:
  - an accessor whose 3.3.3 body is `n<x>(address(), args...)` (returning the value, or this for a setter) becomes a static
    doing exactly that with 3.4.1's n<x> of the same descriptor;
  - a set(...) whose 3.3.3 body calls this.<field>(arg) for each field in turn becomes a static making the same calls in the
    same order: the field's 3.4.1 instance setter where it has one, else its 3.3.3 body (the n-accessor).
A member whose body does not have one of these shapes, or whose n-accessor 3.4.1 lacks, is reported and left alone.
Writes core/shims/steps/26.1/src/top/dext/centurybridge/step/v26_1/rt/CbLibLwjglUring.java and
core/segments/steps/1.21.11__26.1.json.d/lib-lwjgl-uring-gen.json (instanceRedirects + superRedirects). Regenerate, never
hand-edit.
"""
import json
import os
import re
import subprocess
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.join(HERE, "..")
PKG = "top.dext.centurybridge.step.v26_1.rt"
CLS = "CbLibLwjglUring"
OWNER = PKG.replace(".", "/") + "/" + CLS
PREFIX = "org/lwjgl/system/linux/liburing/"
PRIM = {"I": "int", "J": "long", "S": "short", "B": "byte", "Z": "boolean", "F": "float", "D": "double", "C": "char", "V": "void"}


def jtype(desc):
    if desc in PRIM:
        return PRIM[desc]
    if desc.startswith("["):
        return jtype(desc[1:]) + "[]"
    return desc[1:-1].replace("/", ".").replace("$", ".")


def split_args(desc):
    args, i = [], 1
    while desc[i] != ")":
        j = i
        while desc[j] == "[":
            j += 1
        if desc[j] == "L":
            j = desc.index(";", j)
        args.append(desc[i:j + 1])
        i = j + 1
    return args, desc[i + 1:]


def javap(cp, cls, flags):
    return subprocess.run(["javap", *flags, "-cp", cp, cls.replace("/", ".")], capture_output=True, text=True).stdout


def methods(out):
    """javap -c -s -p output -> {name+desc: [instruction text]}"""
    res, cur, code = {}, None, None
    lines = out.splitlines()
    for i, ln in enumerate(lines):
        s = ln.strip()
        if s.startswith("descriptor:") and i > 0:
            head = lines[i - 1].strip()
            m = re.search(r"([\w$<>]+)\(", head)
            name = m.group(1) if m else head.rstrip(";").split()[-1]
            if "." in name:
                name = "<init>"
            cur = name + s.split(":", 1)[1].strip()
            res[cur] = []
        elif re.match(r"\d+: ", s) and cur is not None:
            res[cur].append(re.sub(r"\s+", " ", s.split(":", 1)[1]).strip())
    return res


def jars(libdir):
    return os.pathsep.join(os.path.join(r, n) for r, _, fs in os.walk(libdir) for n in fs
                           if n.endswith(".jar") and "lwjgl" in r and "natives" not in n)


def main():
    vcp, wcp = jars(sys.argv[1]), jars(sys.argv[2])
    table = os.path.join(ROOT, "segments", "damage-lib", "1.21.11__26.1.tsv")
    damaged = {}
    for line in open(table, encoding="utf-8"):
        f = line.rstrip("\n").split("\t")
        if len(f) > 3 and f[2] == "METHOD" and f[3].startswith(PREFIX):
            owner, member = f[3].split(".", 1)
            damaged.setdefault(owner, []).append(member)
    vcode, wdecl = {}, {}

    def vmeth(cls):
        if cls not in vcode:
            vcode[cls] = methods(javap(vcp, cls, ["-c", "-s", "-p"]))
        return vcode[cls]

    def wmeth(cls):
        if cls not in wdecl:
            wdecl[cls] = methods(javap(wcp, cls, ["-s", "-p"]))
        return wdecl[cls]

    accessor = re.compile(r"invokestatic #\d+ Method (?:([\w/$]+)\.)?(n\w+):(\(.*)$")

    def as_accessor(owner, member):
        """3.3.3 body `aload_0; invokevirtual address; loads; invokestatic O.n<x>(desc); xreturn|aload_0 areturn` ->
        (static owner, n-name, n-desc, returns self)"""
        code = vmeth(owner).get(member)
        if not code or code[0] != "aload_0" or "address:()J" not in code[1]:
            return None
        calls = [c for c in code if c.startswith("invokestatic")]
        if len(calls) != 1 or any(c.startswith("invoke") and c not in calls and "address:()J" not in c for c in code):
            return None
        m = accessor.search(calls[0].replace("// ", ""))
        if not m:
            return None
        sowner = m.group(1) or (owner.split("$")[0] if owner.endswith("$Buffer") else owner)
        self_ret = code[-2:] == ["aload_0", "areturn"]
        return sowner, m.group(2), m.group(3), self_ret

    statics, redirects, skipped = [], {}, []
    for owner, members in sorted(damaged.items()):
        if owner[len(PREFIX):].startswith("Lib"):
            continue  # the function classes (LibURing, LibIOURing): not struct accessors, bridged by hand
        for member in sorted(set(members)):
            name, desc = member[:member.index("(")], member[member.index("("):]
            code = vmeth(owner).get(member)
            if code is None or name.startswith("n") and code == []:
                continue
            args, ret = split_args(desc)
            if code and code[0] == "aload_0" and "address:()J" not in code[1] and name == "set":
                # this.<field>(arg) for each field: aload_0, load, invokevirtual owner.f(T)owner, pop ... aload_0 areturn
                # each call must take exactly the next argument: the instruction before it loads that argument's slot
                slots, slot = [], 1
                for a in args:
                    slots.append(slot)
                    slot += 2 if a in ("J", "D") else 1
                body, ok = [], True
                for k, c in enumerate(code):
                    if not c.startswith("invokevirtual"):
                        continue
                    m = re.search(r"Method (\w+):(\(.*)$", c.replace("// ", ""))
                    load = re.match(r"[ilfda]load(?:_| )(\d+)$", code[k - 1]) if k > 0 else None
                    if not m or not load or len(body) >= len(args) or int(load.group(1)) != slots[len(body)]:
                        ok = False
                        break
                    f, fdesc = m.group(1), m.group(2)
                    fargs, _ = split_args(fdesc)
                    if len(fargs) != 1:
                        ok = False
                        break
                    body.append((f, fdesc))
                if not ok or len(body) != len(args):
                    skipped.append(owner + "." + member + " (set body)")
                    continue
                lines = []
                for i, (f, fdesc) in enumerate(body):
                    if f + fdesc in wmeth(owner):
                        lines.append(f"        self.{f}(a{i});")
                    else:
                        acc = as_accessor(owner, f + fdesc)
                        if not acc or acc[1] + acc[2] not in wmeth(acc[0]):
                            ok = False
                            break
                        lines.append(f"        {jtype('L' + acc[0] + ';')}.{acc[1]}(self.address(), a{i});")
                if not ok:
                    skipped.append(owner + "." + member + " (a field with no 3.4.1 setter or n-accessor)")
                    continue
                params = ", ".join([f"{jtype('L' + owner + ';')} self"] + [f"{jtype(a)} a{i}" for i, a in enumerate(args)])
                statics.append(f"    /** 3.3.3's {owner[len(PREFIX):].replace('$', '.')}.{name}{desc}: its fields set in its order */\n"
                               f"    public static {jtype(ret)} {name}({params}) {{\n" + "\n".join(lines)
                               + "\n        return self;\n    }\n")
                redirects[owner + "." + member] = OWNER
                continue
            acc = as_accessor(owner, member)
            if not acc:
                skipped.append(owner + "." + member + " (body shape)")
                continue
            sowner, nname, ndesc, self_ret = acc
            if nname + ndesc not in wmeth(sowner):
                skipped.append(owner + "." + member + f" (3.4.1 lacks {sowner}.{nname}{ndesc})")
                continue
            params = ", ".join([f"{jtype('L' + owner + ';')} self"] + [f"{jtype(a)} a{i}" for i, a in enumerate(args)])
            call = f"{jtype('L' + sowner + ';')}.{nname}(" + ", ".join(["self.address()"] + [f"a{i}" for i in range(len(args))]) + ")"
            body = f"        {call};\n        return self;" if self_ret else f"        return {call};"
            statics.append(f"    /** 3.3.3's {owner[len(PREFIX):].replace('$', '.')}.{name}{desc}: {nname}(address(){', ...' if args else ''}) */\n"
                           f"    public static {jtype(ret)} {name}({params}) {{\n{body}\n    }}\n")
            redirects[owner + "." + member] = OWNER
    src = (f"package {PKG};\n\nimport net.fabricmc.api.EnvType;\nimport net.fabricmc.api.Environment;\n\n"
           "/**\n * LWJGL 3.3.3's liburing struct accessors that 3.4.1 hid (generated by core/tools/gen_lib_lwjgl_uring.py from 3.3.3's\n"
           " * bytecode; never hand-edit). 3.4.1 kept the static n-accessors at the same offsets, so each body is 3.3.3's own.\n */\n"
           "@Environment(EnvType.CLIENT)\n"
           f"public final class {CLS} {{\n    private {CLS}() {{\n    }}\n\n" + "\n".join(statics) + "}\n")
    jpath = os.path.join(ROOT, "shims", "steps", "26.1", "src", *PKG.split("."), CLS + ".java")
    with open(jpath, "w", encoding="utf-8", newline="\n") as f:
        f.write(src)
    out = {"_basis_lib_lwjgl_uring_gen": "generated by core/tools/gen_lib_lwjgl_uring.py -- do not edit; LWJGL 3.3.3 liburing struct accessors "
                     "3.4.1 hid, bridged with 3.3.3's bodies (slib)",
           "instanceRedirects": redirects, "superRedirects": dict(redirects)}
    jout = os.path.join(ROOT, "segments", "steps", "1.21.11__26.1.json.d", "lib-lwjgl-uring-gen.json")
    with open(jout, "w", encoding="utf-8", newline="\n") as f:
        json.dump(out, f, indent=1)
        f.write("\n")
    print(f"{len(statics)} statics -> {jpath}\n{len(redirects)} redirects -> {jout}")
    for s in skipped:
        print("  skipped:", s)


if __name__ == "__main__":
    main()
