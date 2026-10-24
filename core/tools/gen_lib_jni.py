"""LWJGL org.lwjgl.system.JNI call variants a release dropped, given back through LWJGL's bundled libffi.

  python3 core/tools/gen_lib_jni.py <V> <W> <java source root> <java package> <fragment.json>

LWJGL's JNI class is a set of natives `call<letters>(args..., long functionAddress)` that call a native function pointer with
a fixed C signature; each release ships only the variants its own bindings use, so a mod that called one of the dropped
ones (core/segments/damage-lib/<V>__<W>.tsv, METHOD rows on org/lwjgl/system/JNI) meets NoSuchMethodError. The C signature
is spelled by the variant itself: the name's letters after "call" are the long arguments in order (P a pointer, J an
int64) and, last, the return type; every other argument is its descriptor type (B jbyte = signed 8-bit, S jshort = signed
16-bit, C jchar = unsigned 16-bit, Z jboolean = unsigned 8-bit, I, F, D). The generated static makes exactly that call
through libffi (org.lwjgl.system.libffi.LibFFI, part of every LWJGL 3 core jar and its natives): one call interface per
signature, prepared once, arguments in stack memory, the return value read at its own width. Writes
<root>/<package>/CbLibJni.java (generated, client-only) and the staticRedirects fragment. Regenerate, never hand-edit.
"""
import json
import os
import re
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
FFI = {"B": "ffi_type_sint8", "S": "ffi_type_sint16", "C": "ffi_type_uint16", "Z": "ffi_type_uint8", "I": "ffi_type_sint32",
       "F": "ffi_type_float", "D": "ffi_type_double", "J": "ffi_type_sint64", "P": "ffi_type_pointer", "V": "ffi_type_void",
       "US": "ffi_type_uint16", "UB": "ffi_type_uint8"}
SIZE = {"B": 1, "S": 2, "C": 2, "I": 4, "J": 8, "F": 4, "D": 8}
BUF = {"B": "memByteBuffer", "S": "memShortBuffer", "C": "memCharBuffer", "I": "memIntBuffer", "J": "memLongBuffer",
       "F": "memFloatBuffer", "D": "memDoubleBuffer"}
JT = {"B": "byte", "S": "short", "C": "char", "Z": "boolean", "I": "int", "F": "float", "D": "double", "J": "long", "V": "void"}
PUT = {"B": "memPutByte({s}, {a})", "S": "memPutShort({s}, {a})", "C": "memPutShort({s}, (short) {a})",
       "Z": "memPutByte({s}, (byte) ({a} ? 1 : 0))", "I": "memPutInt({s}, {a})", "F": "memPutFloat({s}, {a})",
       "D": "memPutDouble({s}, {a})", "J": "memPutLong({s}, {a})"}
GET = {"B": "(byte) memGetLong(r)", "S": "(short) memGetLong(r)", "C": "(char) memGetLong(r)", "Z": "memGetLong(r) != 0",
       "I": "(int) memGetLong(r)", "F": "memGetFloat(r)", "D": "memGetDouble(r)", "J": "memGetLong(r)", "P": "memGetLong(r)"}


def main():
    v, w, root, pkg, frag = sys.argv[1:6]
    table = os.path.join(HERE, "..", "segments", "damage-lib", f"{v}__{w}.tsv")
    rows = sorted({l.split("\t")[3] for l in open(table, encoding="utf-8")
                   if "\torg/lwjgl/system/JNI." in l and l.split("\t")[2] == "METHOD"})
    methods, redirects = [], {}
    owner = pkg.replace(".", "/") + "/CbLibJni"
    for sym in rows:
        m = sym.split(".", 1)[1]
        name, desc = m[:m.index("(")], m[m.index("("):]
        args = re.findall(r"\[*(?:L[^;]+;|[ZBCSIJFD])", desc[1:desc.index(")")])
        if any(len(a) > 1 and not re.fullmatch(r"\[[BSCIJFD]", a) for a in args):
            sys.exit(f"{m}: object arguments are not a plain C call")
        letters = name[len("invoke"):] if name.startswith("invoke") else name[len("call"):]
        jlong_kinds = list(letters[:-1])
        ret_letter = letters[-1]
        ret = desc[desc.index(")") + 1:]
        # LWJGL 3.3.3 also names its unsigned narrow arguments: C an unsigned 16-bit (a jshort), U an unsigned 8-bit (a jbyte)
        kinds = []  # C kind per argument except the function address; an array is a pointer to its elements
        pos = 0
        for a in args[:-1]:
            if a == "J" or a.startswith("["):
                if pos >= len(jlong_kinds) or jlong_kinds[pos] not in "PJ":
                    sys.exit(f"{m}: name letters do not match the arguments")
                kinds.append(jlong_kinds[pos])
                pos += 1
            elif a == "S" and pos < len(jlong_kinds) and jlong_kinds[pos] == "C":
                kinds.append("US")
                pos += 1
            elif a == "B" and pos < len(jlong_kinds) and jlong_kinds[pos] == "U":
                kinds.append("UB")
                pos += 1
            else:
                kinds.append(a)
        if pos != len(jlong_kinds):
            sys.exit(f"{m}: name letters do not match the arguments")
        params = ", ".join(f"{JT[a] if len(a) == 1 else JT[a[1]] + '[]'} a{i}" for i, a in enumerate(args[:-1])) + (", " if len(args) > 1 else "") + "long fn"
        key = ret_letter + ":" + "".join(kinds)
        body = [f"        long cif = cif(\"{key}\", LibFFI.{FFI[ret_letter]}" + "".join(f", LibFFI.{FFI[k]}" for k in kinds) + ");",
                "        try (MemoryStack st = MemoryStack.stackPush()) {",
                f"            long values = st.nmalloc(8, {max(1, len(kinds))} * 8);",
                f"            long slots = st.nmalloc(8, {max(1, len(kinds))} * 8);"]
        back = []
        for i, (a, k) in enumerate(zip(args[:-1], kinds)):
            if a.startswith("["):
                # JNI pinned the array for the call: the elements, copied to native memory and back afterwards
                e = a[1]
                body.append(f"            long p{i} = a{i} == null ? NULL : st.nmalloc(8, Math.max(1, a{i}.length) * {SIZE[e]});")
                body.append(f"            if (a{i} != null) {{ {BUF[e]}(p{i}, a{i}.length).put(a{i}); }}")
                body.append(f"            memPutAddress(slots + {i * 8}, p{i});")
                back.append(f"            if (a{i} != null) {{ {BUF[e]}(p{i}, a{i}.length).get(a{i}); }}")
            else:
                body.append("            " + PUT[a].format(s=f"slots + {i * 8}", a=f"a{i}") + ";")
            body.append(f"            memPutAddress(values + {i * 8}, slots + {i * 8});")
        body.append("            long r = st.nmalloc(8, 16);")
        body.append("            LibFFI.nffi_call(cif, fn, r, values);")
        body.extend(back)
        if ret != "V":
            body.append("            return " + GET[ret_letter if ret_letter in "P" else ret] + ";")
        body.append("        }")
        methods.append(f"    /** JNI.{m} */\n    public static {JT[ret]} {name}({params}) {{\n" + "\n".join(body) + "\n    }\n")
        redirects["org/lwjgl/system/JNI." + m] = owner
    src = f"""package {pkg};

import static org.lwjgl.system.MemoryUtil.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Pointer;
import org.lwjgl.system.libffi.FFICIF;
import org.lwjgl.system.libffi.FFIType;
import org.lwjgl.system.libffi.LibFFI;

/**
 * Generated by core/tools/gen_lib_jni.py ({v} -> {w}) -- regenerate, never hand-edit. The org.lwjgl.system.JNI call variants
 * {w}'s LWJGL dropped, each the same C call made through LWJGL's bundled libffi: the variant's own C signature (the name's
 * letters for its long arguments, P pointer / J int64, and its return; the descriptor for the rest: B signed 8-bit, S signed
 * 16-bit, C unsigned 16-bit, Z unsigned 8-bit), one call interface per signature prepared once and kept for the run.
 */
@Environment(EnvType.CLIENT)
public final class CbLibJni {{
    private CbLibJni() {{
    }}

    private static final Map<String, Long> CIFS = new ConcurrentHashMap<>();

    /** the prepared libffi call interface for one C signature (allocated once, never freed: one per signature) */
    private static long cif(String key, FFIType ret, FFIType... args) {{
        return CIFS.computeIfAbsent(key, k -> {{
            long cif = nmemCalloc(1, FFICIF.SIZEOF);
            long types = nmemCalloc(Math.max(1, args.length), Pointer.POINTER_SIZE);
            for (int i = 0; i < args.length; i++) {{
                memPutAddress(types + (long) i * Pointer.POINTER_SIZE, args[i].address());
            }}
            int status = LibFFI.nffi_prep_cif(cif, LibFFI.FFI_DEFAULT_ABI, args.length, ret.address(), types);
            if (status != LibFFI.FFI_OK) {{
                throw new IllegalStateException("libffi could not prepare the call " + k + ": " + status);
            }}
            return cif;
        }});
    }}

""" + "\n".join(methods) + "}\n"
    path = os.path.join(root, *pkg.split("."), "CbLibJni.java")
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        f.write(src)
    with open(frag, "w", encoding="utf-8", newline="\n") as f:
        json.dump({"_lib_jni_basis": f"generated by core/tools/gen_lib_jni.py {v} {w} -- regenerate, never hand-edit",
                   "staticRedirects": redirects}, f, indent=1)
        f.write("\n")
    print(f"{len(redirects)} JNI variants -> {path}, {frag}")


if __name__ == "__main__":
    main()
