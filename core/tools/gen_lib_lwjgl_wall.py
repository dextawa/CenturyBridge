"""LWJGL 3.3.3 (1.21.11) -> 3.4.1 (26.1): the static fields LWJGL 3.4 dropped, given back with 3.3.3's values.

  python3 core/tools/gen_lib_lwjgl_wall.py <V libraries dir>

Two families of FIELD rows in core/segments/damage-lib/1.21.11__26.1.tsv:
  - Pointer.Default's protected statics, inherited by every struct and buffer class (UNSAFE, ADDRESS, BUFFER_CONTAINER,
    BUFFER_MARK, BUFFER_POSITION, BUFFER_LIMIT, BUFFER_CAPACITY): 3.3.3 computed them as sun.misc.Unsafe and the field
    offsets of Pointer.Default.address and CustomBuffer.container/mark/position/limit/capacity -- fields 3.4.1 still has, so
    rt/CbLibLwjglOffsets computes the same values the same way;
  - every callback interface's (and class's) FFICIF CIF: 3.3.3 built it in the class initializer with
    APIUtil.apiCreateCIF(abi, return type, argument types...), read here off 3.3.3's bytecode (javap) and rebuilt once on
    3.4.1 with its APIUtil.apiCreateCIF (rt/CbLibLwjglCifs, generated).
Writes core/shims/steps/26.1/src/top/dext/centurybridge/step/v26_1/rt/CbLibLwjglCifs.java and
core/segments/steps/1.21.11__26.1.json.d/lib-lwjgl-gen.json (fieldGetters). Regenerate, never hand-edit.
"""
import hashlib
import json
import os
import re
import subprocess
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.join(HERE, "..")
PKG = "top.dext.centurybridge.step.v26_1.rt"
OWNER = PKG.replace(".", "/")
OFFSETS = {"UNSAFE:Lsun/misc/Unsafe;", "ADDRESS:J", "BUFFER_CONTAINER:J", "BUFFER_MARK:J", "BUFFER_POSITION:J",
           "BUFFER_LIMIT:J", "BUFFER_CAPACITY:J"}


def main():
    libdir = sys.argv[1]
    table = os.path.join(ROOT, "segments", "damage-lib", "1.21.11__26.1.tsv")
    getters, cif_owners = {}, []
    for line in open(table, encoding="utf-8"):
        f = line.rstrip("\n").split("\t")
        if len(f) < 4 or f[2] != "FIELD" or "lwjgl" not in f[0]:
            continue
        owner, field = f[3].split(".", 1)
        if field in OFFSETS:
            getters[f[3]] = OWNER + "/CbLibLwjglOffsets#" + field.split(":")[0]
        elif field == "CIF:Lorg/lwjgl/system/libffi/FFICIF;":
            cif_owners.append(owner)
    jars = [os.path.join(r, n) for r, _, fs in os.walk(libdir) for n in fs
            if n.endswith(".jar") and "lwjgl" in r and "natives" not in n]
    cp = os.pathsep.join(jars)
    methods = []
    inherited = {}
    for owner in sorted(set(cif_owners)):
        out = subprocess.run(["javap", "-c", "-p", "-cp", cp, owner.replace("/", ".")], capture_output=True, text=True).stdout
        clinit = out[out.find("static {};"):]
        m = re.search(r"invokestatic\s+#\d+\s+// Method org/lwjgl/system/APIUtil\.(apiCreateCIF\w*):\(([^)]*)\)", clinit)
        if not m:
            # a callback class: its CIF is the one of the interface it implements (Callback class X implements XI)
            inherited[owner] = owner + "I"
            continue
        # the instructions before the call: abi (getstatic LibFFI.FFI_*:I), for the Var form the fixed-argument count,
        # the return type, the array length, then one getstatic ffi_type_* per element
        ins = []
        for ln in clinit[:m.start()].splitlines():
            mm = re.match(r"\s*\d+: (\w+)\s*(.*)$", ln)
            if not mm:
                continue
            fld = re.search(r"// Field org/lwjgl/system/libffi/LibFFI\.(\w+):", mm.group(2))
            if "Method org/lwjgl/system/APIUtil.apiStdcall:()I" in mm.group(2):
                fld = re.match(r"(.*)", "FFI_STDCALL_OF_APIUTIL")  # the platform's stdcall ABI, as 3.3.3 asked APIUtil
            num = re.match(r"(-?\d+)$", mm.group(2).strip())
            ins.append((mm.group(1), fld.group(1) if fld else None, num.group(1) if num else None))
        start = max(i for i, (op, f, n) in enumerate(ins) if f and f.startswith("FFI_"))
        ins = ins[start:]
        abi = next(f for op, f, n in ins if f and f.startswith("FFI_"))
        types = [f for op, f, n in ins if f and f.startswith("ffi_type_")]
        consts = [int(n) if n else int(op.split("_")[1].replace("m1", "-1")) for op, f, n in ins
                  if op.startswith("iconst_") or op in ("bipush", "sipush")]
        args = ["APIUtil.apiStdcall()" if abi == "FFI_STDCALL_OF_APIUTIL" else "LibFFI." + abi]
        if m.group(1) == "apiCreateCIFVar":
            args.append(str(consts[0]))
        args.extend("LibFFI." + t for t in types)
        name = "CIF_" + re.sub(r"[^A-Za-z0-9]", "_", owner.split("/")[-1]) + "_" + hashlib.sha1(owner.encode()).hexdigest()[:6]
        methods.append((owner, name, m.group(1), args))
        getters[owner + ".CIF:Lorg/lwjgl/system/libffi/FFICIF;"] = OWNER + "/CbLibLwjglCifs#" + name
    for owner, iface in inherited.items():
        key = iface + ".CIF:Lorg/lwjgl/system/libffi/FFICIF;"
        if key not in getters:
            sys.exit(f"{owner}: inherits CIF from {iface}, which has no row")
        getters[owner + ".CIF:Lorg/lwjgl/system/libffi/FFICIF;"] = getters[key]
    # getCallInterface(): 3.3.3's CallbackI default answered the type's CIF (a callback class: its interface's)
    cif_of = {k.split(".")[0]: v.split("#")[1] for k, v in getters.items() if k.endswith(".CIF:Lorg/lwjgl/system/libffi/FFICIF;")}
    redirects, supers, stack, callif = {}, {}, [], []
    for line in open(table, encoding="utf-8"):
        f = line.rstrip("\n").split("\t")
        if len(f) < 4 or f[2] != "METHOD" or "lwjgl" not in f[0]:
            continue
        owner, member = f[3].split(".", 1)
        name, desc = member[:member.index("(")], member[member.index("("):]
        jn = owner.replace("/", ".").replace("$", ".")
        sname = re.sub(r"[^A-Za-z0-9]", "_", owner.split("/")[-1]) + "_" + hashlib.sha1(owner.encode()).hexdigest()[:6]
        if name in ("mallocStack", "callocStack"):
            alloc = name[:-len("Stack")]
            ret = desc[desc.index(")") + 1:][1:-1].replace("/", ".").replace("$", ".")
            params = {"()": ("", "MemoryStack.stackGet()"), "(I)": ("int capacity", "capacity, MemoryStack.stackGet()"),
                      "(Lorg/lwjgl/system/MemoryStack;)": ("MemoryStack stack", "stack"),
                      "(ILorg/lwjgl/system/MemoryStack;)": ("int capacity, MemoryStack stack", "capacity, stack")}[desc[:desc.index(")") + 1]]
            mname = f"{name}_{sname}_{len(stack)}"
            stack.append(f"    /** {owner}.{member} */\n    public static {ret} {mname}({params[0]}) {{\n"
                         f"        return {ret.rsplit('.Buffer', 1)[0] if ret.endswith('.Buffer') else ret}.{alloc}({params[1]});\n    }}\n")
            redirects.setdefault("static", {})[f[3]] = OWNER + "/CbLibLwjglStack#" + mname
        elif name == "getCallInterface" and desc == "()Lorg/lwjgl/system/libffi/FFICIF;":
            base = owner if owner in cif_of else (owner + "I" if owner + "I" in cif_of else None)
            if base is None:
                continue
            mname = f"getCallInterface_{sname}"
            callif.append(f"    /** {owner}.getCallInterface() */\n    public static FFICIF {mname}({jn} callback) {{\n"
                          f"        return CbLibLwjglCifs.{cif_of[base]}();\n    }}\n")
            redirects.setdefault("instance", {})[f[3]] = OWNER + "/CbLibLwjglCallbacks#" + mname
            supers[f[3]] = OWNER + "/CbLibLwjglCallbacks#" + mname
    for cls, body_methods, imports in (("CbLibLwjglStack", stack, "import org.lwjgl.system.MemoryStack;\n"),
                                       ("CbLibLwjglCallbacks", callif, "import org.lwjgl.system.libffi.FFICIF;\n")):
        doc = ("the deprecated mallocStack / callocStack of every LWJGL 3.3.3 struct and struct buffer 3.4.1 dropped: 3.3.3's "
               "bodies, malloc / calloc on the given stack or MemoryStack.stackGet()") if cls == "CbLibLwjglStack" else \
              ("CallbackI.getCallInterface() of every LWJGL 3.3.3 callback type 3.4.1 dropped: 3.3.3's default answered the "
               "type's CIF (rt/CbLibLwjglCifs)")
        with open(os.path.join(ROOT, "shims", "steps", "26.1", "src", *PKG.split("."), cls + ".java"), "w",
                  encoding="utf-8", newline="\n") as fo:
            fo.write(f"package {PKG};\n\nimport net.fabricmc.api.EnvType;\nimport net.fabricmc.api.Environment;\n{imports}\n"
                     f"/**\n * Generated by core/tools/gen_lib_lwjgl_wall.py -- regenerate, never hand-edit. {doc}.\n */\n"
                     f"@Environment(EnvType.CLIENT)\npublic final class {cls} {{\n    private {cls}() {{\n    }}\n\n"
                     + "\n".join(body_methods) + "}\n")
    body = []
    for owner, name, fn, args in methods:
        body.append(f"    private static FFICIF {name.lower()};\n\n    /** {owner}.CIF */\n    public static synchronized FFICIF {name}() {{\n"
                    f"        if ({name.lower()} == null) {{\n            {name.lower()} = APIUtil.{fn}({', '.join(args)});\n        }}\n"
                    f"        return {name.lower()};\n    }}\n")
    src = f"""package {PKG};

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.system.APIUtil;
import org.lwjgl.system.libffi.FFICIF;
import org.lwjgl.system.libffi.LibFFI;

/**
 * Generated by core/tools/gen_lib_lwjgl_wall.py -- regenerate, never hand-edit. The FFICIF CIF constant of each LWJGL 3.3.3
 * callback type 3.4.1 dropped, built once as 3.3.3's class initializer built it (APIUtil.apiCreateCIF with the same ABI,
 * return and argument types, read off 3.3.3's bytecode).
 */
@Environment(EnvType.CLIENT)
public final class CbLibLwjglCifs {{
    private CbLibLwjglCifs() {{
    }}

""" + "\n".join(body) + "}\n"
    with open(os.path.join(ROOT, "shims", "steps", "26.1", "src", *PKG.split("."), "CbLibLwjglCifs.java"), "w",
              encoding="utf-8", newline="\n") as f:
        f.write(src)
    with open(os.path.join(ROOT, "segments", "steps", "1.21.11__26.1.json.d", "lib-lwjgl-gen.json"), "w",
              encoding="utf-8", newline="\n") as f:
        json.dump({"_lib_lwjgl_basis": "generated by core/tools/gen_lib_lwjgl_wall.py -- regenerate, never hand-edit",
                   "fieldGetters": dict(sorted(getters.items())),
                   "staticRedirects": dict(sorted(redirects.get("static", {}).items())),
                   "instanceRedirects": dict(sorted(redirects.get("instance", {}).items())),
                   "superRedirects": dict(sorted(supers.items()))}, f, indent=1)
        f.write("\n")
    print(f"{len(getters)} field getters ({len(methods)} call interfaces)")


if __name__ == "__main__":
    main()
