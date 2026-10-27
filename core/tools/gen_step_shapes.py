"""VoxelShape constants a release took away, answered with the shape the previous release built.

A damage row that reads a static VoxelShape field (get-static, kinds FIELD: the field is gone from W)
read a value: the shape V's static initializer built. 1.21.5 replaced most blocks' shape statics with
shape functions (Block.column, Shapes.rotateHorizontal ...); a 1.21.4 mod reading GrindstoneBlock.FLOOR_NORTH_*
gets NoSuchFieldError. The generator evaluates V's <clinit> symbolically -- only what builds shapes: double
constants, Block.box(6 doubles), Shapes.or(a, b), Shapes.or(a, b...), Shapes.join(a, b, op) with a
BooleanOp constant, Shapes.block() / empty(), VoxelShape arrays built element by element, and reads of
shape fields (of the same class or another one, evaluated the same way) -- and writes, for every
gone field whose value it could follow, a same-named static on one runtime class of the step built
from the same calls on W (each of them exists unchanged on W: javap). The read is redirected there
(fieldRedirects). A field whose value comes from anything else (a helper method, a lambda) is left
open and listed. Rows whose field W only narrowed (kinds ACCESS: 1.21.5 made the protected shapes private,
sometimes rebuilt differently) are answered the same way, so a mod reads V's shape either way, and the
step's access widener leaves those fields alone (step_access_aw: "wired another way"). The shapes are
equal to V's, not the same objects: a field V initialised from another field is built again.

  python gen_step_shapes.py <damage.tsv> <V code jar> <W stub jar> <fragment.json> <runtime class> <src root>
"""
import json
import os
import struct
import sys
import zipfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import classfile  # noqa: E402

SHAPE = "Lnet/minecraft/class_265;"
SHAPES = "[Lnet/minecraft/class_265;"
BOX = "net/minecraft/class_2248.method_9541(DDDDDD)Lnet/minecraft/class_265;"
OR2 = "net/minecraft/class_259.method_1084(Lnet/minecraft/class_265;Lnet/minecraft/class_265;)Lnet/minecraft/class_265;"
ORN = "net/minecraft/class_259.method_17786(Lnet/minecraft/class_265;[Lnet/minecraft/class_265;)Lnet/minecraft/class_265;"
JOIN = "net/minecraft/class_259.method_1072(Lnet/minecraft/class_265;Lnet/minecraft/class_265;Lnet/minecraft/class_247;)Lnet/minecraft/class_265;"
BLOCK = "net/minecraft/class_259.method_1077()Lnet/minecraft/class_265;"
EMPTY = "net/minecraft/class_259.method_1073()Lnet/minecraft/class_265;"
OPS = "net/minecraft/class_247"

# operand lengths of the opcodes a static initializer uses (beyond the opcode byte)
LEN = {0x10: 1, 0x11: 2, 0x12: 1, 0x13: 2, 0x14: 2, 0xb2: 2, 0xb3: 2, 0xb4: 2, 0xb5: 2, 0xb6: 2, 0xb7: 2,
       0xb8: 2, 0xb9: 4, 0xba: 4, 0xbb: 2, 0xbd: 2, 0xc0: 2, 0xc1: 2, 0x15: 1, 0x16: 1, 0x17: 1, 0x18: 1,
       0x19: 1, 0x36: 1, 0x37: 1, 0x38: 1, 0x39: 1, 0x3a: 1, 0xbc: 1, 0x84: 2, 0xc5: 3, 0xa7: 2, 0xc8: 4}


class Unknown:
    """a value the evaluator does not follow"""


UNK = Unknown()


def fmt(d):
    return repr(float(d)) + "D"


class Evaluator:
    def __init__(self, jar):
        self.jar = jar
        self.cache = {}  # owner -> {field: java expression or UNK}

    def fields(self, owner):
        if owner in self.cache:
            return self.cache[owner]
        self.cache[owner] = {}
        try:
            ci = classfile.read(self.jar.read(owner + ".class"), code=True)
        except KeyError:
            return self.cache[owner]
        cl = [m for m in ci.methods if m.name == "<clinit>" and m.code]
        if cl:
            self.cache[owner] = self.run(ci, cl[0].code)
        return self.cache[owner]

    def run(self, ci, c):
        pool, utf8 = ci.cp["pool"], ci.cp["utf8"]

        def member(i):
            _, (a, b) = pool[i]
            cn = utf8[pool[a][1]]
            nt = pool[b][1]
            return cn, utf8[nt[0]], utf8[nt[1]]

        out, st, i = {}, [], 0
        while i < len(c):
            op = c[i]
            if 0x99 <= op <= 0xa6 or op in (0xc6, 0xc7, 0xa7, 0xaa, 0xab):
                break  # control flow: what follows is not a straight line of shape building
            if op == 0x0e or op == 0x0f:      # dconst_0 / dconst_1
                st.append(fmt(op - 0x0e))
            elif 0x02 <= op <= 0x08:          # iconst_m1..5
                st.append(op - 0x03)
            elif op == 0x10:
                st.append(struct.unpack(">b", c[i + 1:i + 2])[0])
            elif op == 0x11:
                st.append(struct.unpack(">h", c[i + 1:i + 3])[0])
            elif op in (0x12, 0x13, 0x14):
                idx = c[i + 1] if op == 0x12 else struct.unpack(">H", c[i + 1:i + 3])[0]
                t, v = pool.get(idx, (0, None))
                st.append(fmt(v) if t in (4, 6) else (v if t == 3 else UNK))
            elif op == 0x59:                  # dup
                st.append(st[-1] if st else UNK)
            elif op == 0xbd:                  # anewarray
                n = st.pop() if st else UNK
                cname = utf8.get(pool.get(struct.unpack(">H", c[i + 1:i + 3])[0], (0, 0))[1])
                st.append([UNK] * n if cname == "net/minecraft/class_265" and isinstance(n, int) else UNK)
            elif op == 0x53:                  # aastore
                v, k, arr = st.pop(), st.pop(), st.pop()
                if isinstance(arr, list) and isinstance(k, int) and 0 <= k < len(arr):
                    arr[k] = v
            elif op == 0xb2:                  # getstatic
                cn, n, d = member(struct.unpack(">H", c[i + 1:i + 3])[0])
                if cn == OPS and d == "L" + OPS + ";":
                    st.append("net.minecraft.class_247." + n)
                elif d in (SHAPE, SHAPES):
                    v = out.get(n, UNK) if cn == ci.name else self.fields(cn).get(n, UNK)
                    st.append(v)
                else:
                    st.append(UNK)
            elif op == 0xb3:                  # putstatic
                cn, n, d = member(struct.unpack(">H", c[i + 1:i + 3])[0])
                v = st.pop() if st else UNK
                if cn == ci.name and d in (SHAPE, SHAPES):
                    out[n] = v
            elif op in (0xb6, 0xb7, 0xb8, 0xb9):
                cn, n, d = member(struct.unpack(">H", c[i + 1:i + 3])[0])
                key = cn + "." + n + d
                nargs = len(classfile.split_args(d)[0]) + (0 if op == 0xb8 else 1)
                if len(st) >= nargs:
                    args = [st.pop() for _ in range(nargs)][::-1]
                else:
                    args, st = None, []
                ret = d[d.index(")") + 1:]
                val = UNK
                ok = args is not None and all(not isinstance(a, Unknown) and not (isinstance(a, list) and any(isinstance(x, Unknown) for x in a)) for a in args)
                if ok and key == BOX:
                    val = "net.minecraft.class_2248.method_9541(" + ", ".join(args) + ")"
                elif ok and key == OR2:
                    val = "net.minecraft.class_259.method_1084(" + args[0] + ", " + args[1] + ")"
                elif ok and key == ORN:
                    val = "net.minecraft.class_259.method_17786(" + args[0] + ", " + arr_expr(args[1]) + ")"
                elif ok and key == JOIN:
                    val = "net.minecraft.class_259.method_1072(" + args[0] + ", " + args[1] + ", " + args[2] + ")"
                elif key == BLOCK:
                    val = "net.minecraft.class_259.method_1077()"
                elif key == EMPTY:
                    val = "net.minecraft.class_259.method_1073()"
                if ret != "V":
                    st.append(val)
            elif op == 0xba:                  # invokedynamic: a lambda or string concat
                st.append(UNK)
            elif op == 0x57:                  # pop
                if st:
                    st.pop()
            else:
                # anything else leaves the stack in a state this evaluator does not model
                st = [UNK] * len(st)
            i += 1 + LEN.get(op, 0)
        return out


def arr_expr(a):
    return "new net.minecraft.class_265[] {" + ", ".join(a) + "}"


def main():
    damage, vjar, wjar, frag, rtclass, src = sys.argv[1:7]
    w = zipfile.ZipFile(wjar)
    gone = {}
    for line in open(damage, encoding="utf-8"):
        f = line.rstrip("\n").split("\t")
        if len(f) >= 5 and f[0].startswith("get-static") and f[3] in (SHAPE, SHAPES) and set(f[4].split(",")) & {"FIELD", "ACCESS"}:
            gone[(f[1], f[2], f[3])] = True
    ev = Evaluator(zipfile.ZipFile(vjar))
    done, left = {}, []
    for (o, n, d) in sorted(gone):
        v = ev.fields(o).get(n, UNK)
        if isinstance(v, list):
            v = arr_expr(v) if all(isinstance(x, str) for x in v) else UNK
        if isinstance(v, str) and (d == SHAPE) == (not v.startswith("new ")):
            done[(o, n, d)] = v
        else:
            left.append(o + "." + n)
    pkg, simple = rtclass.rsplit("/", 1)
    lines = ["package " + pkg.replace("/", ".") + ";", "",
             "/**",
             " * VoxelShape statics of the previous release that this release removed, built as the previous",
             " * release's static initializers built them (Block.box / Shapes.or / Shapes.join on this release).",
             " * Generated by core/tools/gen_step_shapes.py from the step's damage table; do not edit.",
             " */",
             "public final class " + simple + " {", ""]
    for (o, n, d), v in sorted(done.items()):
        t = "net.minecraft.class_265" + ("[]" if d == SHAPES else "")
        lines.append("    /** " + o + "." + n + " */")
        lines.append("    public static final " + t + " " + n + " = " + v + ";")
        lines.append("")
    lines += ["    private " + simple + "() {", "    }", "}", ""]
    path = os.path.join(src, rtclass + ".java")
    os.makedirs(os.path.dirname(path), exist_ok=True)
    open(path, "w", encoding="utf-8", newline="\n").write("\n".join(lines))
    fr = json.load(open(frag, encoding="utf-8")) if os.path.exists(frag) else {}
    red = {k: v for k, v in fr.get("fieldRedirects", {}).items() if v != rtclass}
    for (o, n, d) in sorted(done):
        red[o + "." + n + ":" + d] = rtclass
    fr["fieldRedirects"] = red
    fr["_shapes_basis"] = ("gen_step_shapes.py: gone VoxelShape statics answered with the shape the previous release's "
                           "<clinit> built (same Block.box / Shapes calls on this release); not followed: "
                           + str(len(left)) + " (" + ", ".join(left[:40]) + ")")
    with open(frag, "w", encoding="utf-8", newline="\n") as fh:
        json.dump(fr, fh, indent=1)
        fh.write("\n")
    print(len(gone), "gone shape statics,", len(done), "built ->", path, "; not followed:", len(left))
    for x in left:
        print("   open", x)


if __name__ == "__main__":
    main()
