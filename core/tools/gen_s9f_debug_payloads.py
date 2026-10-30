"""gen_s9f_debug_payloads.py: layer facades for the debug payloads 1.21.9 removed (step 1.21.8 -> 1.21.9).

1.21.9 turned debug information into subscriptions (util/debug) and deleted the 19 debug payload records of
network/protocol/common/custom (BeeDebugPayload ... WorldGenAttemptDebugPayload, BreezeDebugPayload,
RedstoneWireOrientationsDebugPayload) with their nested records. Their bodies are straight-line record code (fields,
canonical constructor, a FriendlyByteBuf constructor, write, STREAM_CODEC, TYPE, type(), small helpers) whose every
outside reference still links on 1.21.9. This script reads 1.21.8's bytecode (javap -c -p -v of the V code jar) and
writes each class back as Java source with the same members under the same intermediary names: every instruction is
translated one to one (no branch exists in these classes; the script refuses one), invokedynamic lambdas become
lambdas calling the same handle, ObjectMethods toString keeps 1.21.8's component labels. Members are public and the
records are emitted as final classes (a record's component fields are private): an access widener never reaches a
layer class.

    python3 core/tools/gen_s9f_debug_payloads.py <V code jar> <class names tsv> <layer facade dir> <wiring fragment>
"""
import json, os, re, subprocess, sys

PKG = "top.dext.centurybridge.step.v1_21_9.facade"
PKG_PATH = PKG.replace(".", "/")
OUTERS = ["class_8707", "class_8708", "class_8712", "class_8713", "class_8714", "class_8715", "class_8716", "class_8718",
          "class_8720", "class_8721", "class_8722", "class_8723", "class_8724", "class_8725", "class_8726", "class_8728",
          "class_8729", "class_9004", "class_9831"]

jar, names_tsv, out_dir, wiring_out = sys.argv[1:5]

# outside members the bodies call that 1.21.9 removed, given back by this layer as statics taking the receiver first
# (the same statics the step's wiring sends converted mods' calls to)
GONE = {
    ("net/minecraft/class_2540", "method_19456"): "top.dext.centurybridge.step.v1_21_9.rt.CbSectionPosBuf",
    ("net/minecraft/class_2540", "method_36131"): "top.dext.centurybridge.step.v1_21_9.rt.CbSectionPosBuf",
}
RAW = ("java.util.List", "java.util.Set", "java.util.Collection", "java.util.Map", "java.util.Optional")

mojang = {}
for line in open(names_tsv, encoding="utf-8"):
    p = line.rstrip("\n").split("\t")
    if p[0] == "C":
        mojang[p[1]] = p[2]


def classes_of(outer):
    listing = subprocess.run(["unzip", "-Z1", jar], capture_output=True, text=True).stdout.split()
    own = [n[:-6] for n in listing if n.endswith(".class") and (n == f"net/minecraft/{outer}.class" or n.startswith(f"net/minecraft/{outer}$"))]
    return sorted(own, key=len)


ALL = {}  # internal name -> (facade internal name, source name)
for o in OUTERS:
    for c in classes_of(o):
        simple = mojang[c].rsplit("/", 1)[1]
        ALL[c] = (f"{PKG_PATH}/{simple}", simple.replace("$", "."))


def src_name(internal):
    internal = internal.replace(".", "/")
    if internal in ALL:
        return ALL[internal][1]
    return internal.replace("/", ".").replace("$", ".")


PRIM = {"I": "int", "F": "float", "Z": "boolean", "V": "void", "D": "double", "J": "long", "B": "byte", "S": "short", "C": "char"}


def parse_desc(desc):
    """'(A;I)V' -> ([types], ret) as descriptors"""
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


def jtype(d):
    dims = 0
    while d.startswith("["):
        dims += 1
        d = d[1:]
    t = PRIM[d] if d in PRIM else src_name(d[1:-1])
    return t + "[]" * dims


def gtype(text):
    """a javap generic declaration type -> source"""
    return re.sub(r"[A-Za-z_$][\w$]*(?:\.[A-Za-z_$][\w$]*)+", lambda m: src_name(m.group(0)), text)


def split_top(s):
    out, depth, cur = [], 0, ""
    for ch in s:
        if ch == "<":
            depth += 1
        elif ch == ">":
            depth -= 1
        if ch == "," and depth == 0:
            out.append(cur.strip())
            cur = ""
        else:
            cur += ch
    if cur.strip():
        out.append(cur.strip())
    return out


class New:
    def __init__(self, cls):
        self.cls = cls
        self.expr = None


def parse(internal):
    text = subprocess.run(["javap", "-c", "-p", "-v", "-cp", jar, internal.replace("/", ".")],
                          capture_output=True, text=True, errors="replace").stdout
    lines = text.splitlines()
    header = next(l for l in lines if re.match(r"^(public |final |abstract )*.*class net\.minecraft\.", l))
    impl = re.search(r" implements (.+?)\s*\{?\s*$", header)
    bsms, cur = {}, None
    in_bsm = False
    for l in lines:
        if l.startswith("BootstrapMethods:"):
            in_bsm = True
            continue
        if in_bsm:
            m = re.match(r"^  (\d+): #\d+ REF_\w+ (\S+)", l)
            if m:
                cur = [m.group(2), []]
                bsms[int(m.group(1))] = cur
            elif re.match(r"^      #\d+( |$)", l) and cur is not None:
                parts = l.split(None, 1)
                cur[1].append(parts[1].strip() if len(parts) > 1 else "")
            elif not l.startswith(" "):
                in_bsm = False
    members, i = [], 0
    body_start = lines.index("{")
    i = body_start + 1
    while i < len(lines):
        l = lines[i]
        if re.match(r"^  \S.*;$", l) and not l.startswith("  #"):
            m = {"head": l.strip(), "desc": None, "code": []}
            j = i + 1
            while j < len(lines) and lines[j].strip() and lines[j] != "}":
                s = lines[j].strip()
                if s.startswith("descriptor:"):
                    m["desc"] = s.split(":", 1)[1].strip()
                im = re.match(r"^\s+(\d+): (\w+)\s*(.*)$", lines[j])
                if im and lines[j].startswith("      "):
                    m["code"].append((im.group(2), im.group(3)))
                if s.startswith(("LineNumberTable", "LocalVariableTable", "Exceptions", "Signature", "RuntimeVisible", "MethodParameters")):
                    if s.startswith(("LineNumberTable", "LocalVariableTable")):
                        while j + 1 < len(lines) and lines[j + 1].startswith("        line"):
                            j += 1
                j += 1
            members.append(m)
            i = j
        elif l == "}":
            break
        else:
            i += 1
    return header, (impl.group(1) if impl else None), bsms, members


def ref(comment, owner):
    """'Method net/x.m:(..)V' / 'Field comp_1:I' -> (owner, name, desc)"""
    body = comment.split(None, 1)[1]
    name_part, desc = body.rsplit(":", 1)
    if "." in name_part:
        o, n = name_part.rsplit(".", 1)
    else:
        o, n = owner, name_part
    return o, n.strip('"'), desc


def lambda_expr(bsm, captured, own):
    kind_handle = bsm[1][1]
    inst = bsm[1][2]
    kind, handle = kind_handle.split(" ", 1)
    o, n = handle.split(":", 1)[0].rsplit(".", 1)
    hdesc = handle.split(":", 1)[1]
    n = n.strip('"')
    iargs, _ = parse_desc(inst)
    types = [jtype(t) for t in iargs]
    # a raw generic parameter would pin a codec factory's type variable to the raw type: let the target type it
    params = [f"a{k}" for k in range(len(iargs))] if any(t in RAW for t in types) else [f"{t} a{k}" for k, t in enumerate(types)]
    vals = captured + [f"a{k}" for k in range(len(iargs))]
    if kind == "REF_newInvokeSpecial":
        body = f"new {src_name(o)}({', '.join(vals)})"
    elif kind == "REF_invokeStatic":
        body = f"{src_name(o)}.{n}({', '.join(vals)})"
    elif kind in ("REF_invokeVirtual", "REF_invokeInterface") and (o, n) in GONE:
        body = f"{GONE[(o, n)]}.{n}({', '.join(vals)})"
    elif kind in ("REF_invokeVirtual", "REF_invokeInterface"):
        body = f"{vals[0]}.{n}({', '.join(vals[1:])})"
    else:
        raise SystemExit(f"{own}: lambda handle {kind}")
    return f"(({', '.join(params)}) -> {body})"


def translate(own, m, bsms, static, labels):
    args, ret = parse_desc(m["desc"])
    local = {}
    slot = 0
    if not static:
        local[0] = "this"
        slot = 1
    for k, a in enumerate(args):
        local[slot] = f"p{k}"
        slot += 2 if a in ("J", "D") else 1
    stack, out = [], []
    is_ctor = '"<init>"' in m["head"] or re.search(r"net\.minecraft\.\S+\(", m["head"].split("(")[0] + "(") is not None
    for op, arg in m["code"]:
        comment = arg.split("//", 1)[1].strip() if "//" in arg else ""
        if re.match(r"^[ailfd]load(_\d)?$", op):
            idx = int(op.split("_")[1]) if "_" in op else int(arg.split()[0])
            stack.append(local[idx])
        elif op in ("getfield",):
            o, n, d = ref(comment, own)
            recv = stack.pop()
            stack.append(f"{recv}.{n}")
        elif op == "getstatic":
            o, n, d = ref(comment, own)
            stack.append(f"{src_name(o)}.{n}")
        elif op == "putstatic":
            o, n, d = ref(comment, own)
            out.append(f"{n} = {stack.pop()};")
        elif op == "putfield":
            raise SystemExit(f"{own}: putfield outside the canonical constructor")
        elif op == "ldc":
            if comment.startswith("String "):
                stack.append(json.dumps(comment[len("String "):]))
            elif comment.startswith("int "):
                stack.append(comment[4:])
            else:
                raise SystemExit(f"{own}: ldc {comment}")
        elif op in ("sipush", "bipush"):
            stack.append(arg.split()[0])
        elif op == "new":
            stack.append(New(comment.split(None, 1)[1]))
        elif op == "dup":
            stack.append(stack[-1])
        elif op == "pop":
            out.append(f"{stack.pop()};")
        elif op == "checkcast":
            v = stack.pop()
            t = comment.split(None, 1)[1]
            stack.append(f"(({jtype(t if t.startswith('[') else 'L' + t + ';')}) {v})")
        elif op.startswith("invoke") and op != "invokedynamic":
            o, n, d = ref(comment.replace("InterfaceMethod", "Method"), own)
            a, r = parse_desc(d)
            vals = [stack.pop() for _ in a][::-1]
            vals = [v.expr if isinstance(v, New) else v for v in vals]
            if op == "invokestatic":
                e = f"{src_name(o)}.{n}({', '.join(vals)})"
            elif n == "<init>":
                recv = stack.pop()
                if isinstance(recv, New):
                    recv.expr = f"new {src_name(o)}({', '.join(vals)})"
                    if stack and stack[-1] is recv:
                        stack[-1] = recv.expr
                    continue
                if recv == "this" and o == "java/lang/Record":
                    continue
                if recv == "this" and o == own:
                    out.append(f"this({', '.join(vals)});")
                    continue
                raise SystemExit(f"{own}: constructor call {o}")
            else:
                recv = stack.pop()
                recv = recv.expr if isinstance(recv, New) else recv
                if (o, n) in GONE:
                    e = f"{GONE[(o, n)]}.{n}({', '.join([recv] + vals)})"
                else:
                    e = f"{recv}.{n}({', '.join(vals)})"
            if r == "V":
                out.append(e + ";")
            else:
                stack.append(e)
        elif op == "invokedynamic":
            idx, n, d = re.search(r"InvokeDynamic #(\d+):(\w+):(\S+)", comment).groups()
            bsm = bsms[int(idx)]
            a, r = parse_desc(d)
            caps = [stack.pop() for _ in a][::-1]
            if bsm[0].startswith("java/lang/runtime/ObjectMethods"):
                return None  # record default; toString is written with the labels by the caller
            stack.append(lambda_expr(bsm, caps, own))
        elif op in ("areturn", "ireturn", "freturn", "lreturn", "dreturn"):
            out.append(f"return {stack.pop()};")
        elif op == "return":
            pass
        else:
            raise SystemExit(f"{own}: unhandled {op} (the generator only takes straight-line code)")
    if stack:
        raise SystemExit(f"{own}: stack left {stack} in {m['head']}")
    return out


def emit_class(internal, indent, nested):
    header, impl, bsms, members = parse(internal)
    simple = ALL[internal][1].rsplit(".", 1)[-1]
    fields = [m for m in members if "(" not in m["head"] and not m["head"].startswith("static {}")]
    comps = [f for f in fields if " static " not in f" {f['head']} "]
    statics = [f for f in fields if f not in comps]
    labels = None
    for b in bsms.values():
        if b[0].startswith("java/lang/runtime/ObjectMethods"):
            labels = b[1][1].split(";") if b[1][1] else []
    comp_src = []
    for f in comps:
        h = f["head"].rstrip(";").split()
        name = h[-1]
        t = gtype(" ".join(h[:-1]).replace("private ", "").replace("final ", "").strip())
        comp_src.append((t, name))
    ind = " " * indent
    impl_src = f" implements {gtype(impl)}" if impl else ""
    vname = mojang[internal]
    lines = [f"{ind}/** 1.21.8's {vname} ({internal}), from its bytecode. */",
             f"{ind}@SuppressWarnings({{\"unchecked\", \"rawtypes\"}})",
             f"{ind}public {'static ' if nested else ''}final class {simple}{impl_src} {{"]
    # not a Java record: a record's component fields are private, and an access widener never reaches a layer class,
    # so a mod that widened 1.21.8's component field could not link it. The class keeps the record's members and
    # behaviour (public final fields, canonical constructor, accessors, component-wise equals/hashCode/toString).
    for t, n in comp_src:
        lines.append(f"{ind}    public final {t} {n};")
    lines.append(f"{ind}    public {simple}({', '.join(f'{t} {n}' for t, n in comp_src)}) {{")
    lines += [f"{ind}        this.{n} = {n};" for _, n in comp_src]
    lines.append(f"{ind}    }}")
    lines.append(f"{ind}    @Override")
    lines.append(f"{ind}    public boolean equals(Object o) {{")
    eq = " && ".join(f"java.util.Objects.equals(this.{n}, other.{n})" for _, n in comp_src) or "true"
    lines.append(f"{ind}        return this == o || o instanceof {simple} other && {eq};")
    lines.append(f"{ind}    }}")
    lines.append(f"{ind}    @Override")
    lines.append(f"{ind}    public int hashCode() {{")
    lines.append(f"{ind}        int h = 0;")
    lines += [f"{ind}        h = 31 * h + java.util.Objects.hashCode(this.{n});" for _, n in comp_src]
    lines.append(f"{ind}        return h;")
    lines.append(f"{ind}    }}")
    for f in statics:
        h = f["head"].rstrip(";").split()
        t = gtype(" ".join(x for x in h[:-1] if x not in ("public", "private", "protected", "static", "final")))
        lines.append(f"{ind}    public static final {t} {h[-1]};")
    canon = [t for t, _ in comp_src]
    for m in members:
        h = m["head"]
        if h.startswith("static {}"):
            body = translate(internal, m, bsms, True, labels)
            lines.append(f"{ind}    static {{")
            lines += [f"{ind}        {s}" for s in body]
            lines.append(f"{ind}    }}")
            continue
        if "(" not in h:
            continue
        before, after = h.split("(", 1)
        params = split_top(after.rsplit(")", 1)[0])
        words = before.split()
        static = "static" in words
        name = words[-1]
        is_ctor = name == internal.replace("/", ".")
        ptypes = [gtype(p) for p in params]
        if is_ctor and ptypes == canon:
            continue  # the record's canonical constructor: only field stores (checked below)
        body = translate(internal, m, bsms, static, labels)
        if body is None:
            if name == "toString":
                parts = " + \", \" + ".join(f'"{lab}=" + this.{n}' for lab, (_, n) in zip(labels, comp_src))
                lines.append(f"{ind}    @Override")
                lines.append(f"{ind}    public String toString() {{")
                lines.append(f"{ind}        return \"{simple}[\"{' + ' + parts if parts else ''} + \"]\";")
                lines.append(f"{ind}    }}")
            continue
        sig = ", ".join(f"{t} p{k}" for k, t in enumerate(ptypes))
        mods = ["public"] + (["static"] if static else [])
        tparams = [w for w in words if w.startswith("<")]
        if is_ctor:
            lines.append(f"{ind}    {' '.join(mods)} {simple}({sig}) {{")
        else:
            rt = gtype(words[-2])
            if tparams:
                mods.append(gtype(tparams[0]))
            lines.append(f"{ind}    {' '.join(mods)} {rt} {name}({sig}) {{")
        lines += [f"{ind}        {s}" for s in body]
        lines.append(f"{ind}    }}")
    # the canonical constructor must be field stores in order, which the record's own constructor repeats
    for m in members:
        h = m["head"]
        if "(" in h and h.split("(")[0].split()[-1] == internal.replace("/", "."):
            ptypes = [gtype(p) for p in split_top(h.split("(", 1)[1].rsplit(")", 1)[0])]
            if ptypes == canon:
                stores = [a for o, a in m["code"] if o == "putfield"]
                ops = [o for o, _ in m["code"] if o not in ("aload_0", "putfield", "return", "invokespecial") and not re.match(r"[ailfd]load", o)]
                names = [re.search(r"Field (\w+):", a).group(1) for a in stores]
                if ops or names != [n for _, n in comp_src]:
                    raise SystemExit(f"{internal}: canonical constructor is not plain field stores")
    if nested:
        lines.append(f"{ind}}}")
    return lines


renames = {}
for o in OUTERS:
    cs = classes_of(o)
    outer = f"net/minecraft/{o}"
    simple = ALL[outer][1]
    src = [f"package {PKG};", "", "/**",
           f" * Generated by core/tools/gen_s9f_debug_payloads.py from 1.21.8's {mojang[outer]} ({outer}), which 1.21.9",
           " * removed when debug information became subscriptions (util/debug). Same members, intermediary names, 1.21.8 bodies.",
           " */"]
    body = emit_class(outer, 0, False)
    inner = []
    for c in cs:
        if c != outer:
            inner += [""] + emit_class(c, 4, True)
    src += body + inner + ["}"]
    path = os.path.join(out_dir, simple + ".java")
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        f.write("\n".join(src) + "\n")
    for c in cs:
        renames[c] = ALL[c][0]

frag = {"_s9f_payloads_basis": "1.21.9 removed the debug payload records of network/protocol/common/custom (javap: absent from the "
        "1.21.9 jar) when debug information became subscriptions. They come back as layer records with 1.21.8's members and "
        "bodies, generated from 1.21.8's bytecode by core/tools/gen_s9f_debug_payloads.py (facade/*DebugPayload.java); "
        "converted mods' references move onto them.",
        "classRenames": dict(sorted(renames.items()))}
with open(wiring_out, "w", encoding="utf-8", newline="\n") as f:
    json.dump(frag, f, indent=1)
    f.write("\n")
print(f"{len(OUTERS)} payloads, {len(renames)} classes")
