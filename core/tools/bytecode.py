"""A class-file reader that resolves method bodies into instruction streams, for comparing two
releases' code: the wall table's generator (wall_table.py) asks whether a 1.21.11 method and a 26.1
method are the same code under other names.

  read(bytes) -> Klass | None
  Klass.methods: [Meth(name, desc, access, insns, signature)]
  Meth.insns: [(opcode, operand)] with every constant-pool operand resolved:
      field/method refs     ("ref", kind, owner, name, desc)
      class refs            ("class", internal name)
      ldc constants         ("str"|"int"|"float"|"long"|"double", value) / ("class", name) /
                            ("mtype", desc) / ("mhandle", tag, owner, name, desc)
      invokedynamic         ("indy", name, desc, (bootstrap handle), (bootstrap args...))
      local slots, iinc, newarray type, multianewarray dims, switch keys: plain ints / tuples
  Branch targets are dropped (an ldc that became ldc_w moves every offset; the instruction stream
  without offsets still says what the code does, and switch keys stay).
"""
import struct
from dataclasses import dataclass, field

ACC_PUBLIC, ACC_PRIVATE, ACC_PROTECTED, ACC_STATIC, ACC_FINAL = 0x1, 0x2, 0x4, 0x8, 0x10
ACC_BRIDGE, ACC_INTERFACE, ACC_ABSTRACT, ACC_SYNTHETIC, ACC_ENUM = 0x40, 0x200, 0x400, 0x1000, 0x4000


@dataclass
class Meth:
    name: str
    desc: str
    access: int
    insns: list = None   # None: no Code attribute (abstract / native)
    signature: str = None


@dataclass
class Fld:
    name: str
    desc: str
    access: int
    signature: str = None
    const: object = None  # ConstantValue, resolved


@dataclass
class Klass:
    name: str
    access: int
    super: str
    interfaces: list
    fields: list = field(default_factory=list)
    methods: list = field(default_factory=list)
    signature: str = None


# operand layout per opcode: None = no operands; 'b' u1, 'B' s1, 'h' s2, 'H' cp index u2, 'L' cp u1,
# 'S' slot u1, 'I' iinc, 'J' branch s2, 'K' branch s4, 'D' invokeinterface, 'Y' invokedynamic,
# 'N' newarray, 'M' multianewarray
_OPS = {}
for _op in range(0x00, 0x10):
    _OPS[_op] = None
_OPS.update({0x10: "B", 0x11: "h", 0x12: "L", 0x13: "H", 0x14: "H"})
for _op in range(0x15, 0x1a):
    _OPS[_op] = "S"
for _op in range(0x1a, 0x36):
    _OPS[_op] = None
for _op in range(0x36, 0x3b):
    _OPS[_op] = "S"
for _op in range(0x3b, 0x84):
    _OPS[_op] = None
_OPS[0x84] = "I"
for _op in range(0x85, 0x99):
    _OPS[_op] = None
for _op in range(0x99, 0xa9):
    _OPS[_op] = "J"
_OPS[0xa9] = "S"
for _op in range(0xac, 0xb2):
    _OPS[_op] = None
for _op in range(0xb2, 0xb9):
    _OPS[_op] = "H"
_OPS.update({0xb9: "D", 0xba: "Y", 0xbb: "H", 0xbc: "N", 0xbd: "H", 0xbe: None, 0xbf: None, 0xc0: "H",
             0xc1: "H", 0xc2: None, 0xc3: None, 0xc5: "M", 0xc6: "J", 0xc7: "J", 0xc8: "K", 0xc9: "K"})
_LDC = (0x12, 0x13, 0x14)


def read(b):
    if b[:4] != b"\xca\xfe\xba\xbe":
        return None
    n = struct.unpack(">H", b[8:10])[0]
    cp = [None] * n
    i, idx = 10, 1
    while idx < n:
        tag = b[i]
        if tag == 1:
            ln = struct.unpack(">H", b[i + 1:i + 3])[0]
            cp[idx] = (1, b[i + 3:i + 3 + ln].decode("utf-8", "replace"))
            i += 3 + ln
        elif tag == 3:
            cp[idx] = (3, struct.unpack(">i", b[i + 1:i + 5])[0])
            i += 5
        elif tag == 4:
            cp[idx] = (4, struct.unpack(">f", b[i + 1:i + 5])[0])
            i += 5
        elif tag == 5:
            cp[idx] = (5, struct.unpack(">q", b[i + 1:i + 9])[0])
            i += 9
            idx += 1
        elif tag == 6:
            cp[idx] = (6, struct.unpack(">d", b[i + 1:i + 9])[0])
            i += 9
            idx += 1
        elif tag in (7, 8, 16, 19, 20):
            cp[idx] = (tag, struct.unpack(">H", b[i + 1:i + 3])[0])
            i += 3
        elif tag in (9, 10, 11, 12, 17, 18):
            cp[idx] = (tag,) + struct.unpack(">HH", b[i + 1:i + 5])
            i += 5
        elif tag == 15:
            cp[idx] = (15, b[i + 1], struct.unpack(">H", b[i + 2:i + 4])[0])
            i += 4
        else:
            return None
        idx += 1

    def utf(k):
        return cp[k][1]

    def cname(k):
        return utf(cp[k][1])

    def nat(k):
        return utf(cp[k][1]), utf(cp[k][2])

    def ref(k):
        e = cp[k]
        nm, ds = nat(e[2])
        return ("ref", {9: "F", 10: "M", 11: "I"}[e[0]], cname(e[1]), nm, ds)

    def handle(k):
        e = cp[k]
        r = ref(e[2])
        return ("mhandle", e[1], r[2], r[3], r[4])

    def const(k):
        e = cp[k]
        t = e[0]
        if t == 8:
            return ("str", utf(e[1]))
        if t == 3:
            return ("int", e[1])
        if t == 4:
            return ("float", e[1])
        if t == 5:
            return ("long", e[1])
        if t == 6:
            return ("double", e[1])
        if t == 7:
            return ("class", utf(e[1]))
        if t == 16:
            return ("mtype", utf(e[1]))
        if t == 15:
            return handle(k)
        if t == 17:
            nm, ds = nat(e[2])
            return ("condy", nm, ds)
        return ("?", t)

    def u2(at):
        return struct.unpack(">H", b[at:at + 2])[0]

    acc = u2(i)
    name = cname(u2(i + 2))
    sup_i = u2(i + 4)
    sup = cname(sup_i) if sup_i else None
    i += 6
    nif = u2(i)
    itfs = [cname(u2(i + 2 + 2 * k)) for k in range(nif)]
    i += 2 + 2 * nif
    k = Klass(name, acc, sup, itfs)
    pending = []  # methods whose code needs the bootstrap table
    for kind in ("fields", "methods"):
        cnt = u2(i)
        i += 2
        for _ in range(cnt):
            a, nm, ds = u2(i), utf(u2(i + 2)), utf(u2(i + 4))
            i += 6
            na = u2(i)
            i += 2
            code, sig, cv = None, None, None
            for _ in range(na):
                an = utf(u2(i))
                ln = struct.unpack(">I", b[i + 2:i + 6])[0]
                if kind == "methods" and an == "Code":
                    clen = struct.unpack(">I", b[i + 10:i + 14])[0]
                    code = b[i + 14:i + 14 + clen]
                elif an == "Signature":
                    sig = utf(u2(i + 6))
                elif an == "ConstantValue":
                    cv = const(u2(i + 6))
                i += 6 + ln
            if kind == "fields":
                k.fields.append(Fld(nm, ds, a, sig, cv))
            else:
                m = Meth(nm, ds, a, None, sig)
                k.methods.append(m)
                if code is not None:
                    pending.append((m, code))
    bootstraps = []
    for _ in range(u2(i)):
        an = utf(u2(i + 2))
        ln = struct.unpack(">I", b[i + 4:i + 8])[0]
        if an == "Signature":
            k.signature = utf(u2(i + 8))
        elif an == "BootstrapMethods":
            j = i + 8
            for _ in range(u2(j)):
                mh, nargs = u2(j + 2), u2(j + 4)
                bargs = tuple(const(u2(j + 6 + 2 * q)) for q in range(nargs))
                bootstraps.append((handle(mh), bargs))
                j += 4 + 2 * nargs
        i += 6 + ln

    for m, code in pending:
        m.insns = _decode(code, const, ref, cname, cp, nat, bootstraps)
    return k


def _decode(code, const, ref, cname, cp, nat, bootstraps):
    out = []
    i = 0
    n = len(code)
    while i < n:
        op = code[i]
        if op == 0xaa:  # tableswitch
            j = i + 1 + (3 - i % 4)
            low, high = struct.unpack(">ii", code[j + 4:j + 12])
            out.append((op, (low, high)))
            i = j + 12 + 4 * (high - low + 1)
            continue
        if op == 0xab:  # lookupswitch
            j = i + 1 + (3 - i % 4)
            np_ = struct.unpack(">i", code[j + 4:j + 8])[0]
            keys = tuple(struct.unpack(">i", code[j + 8 + 8 * q:j + 12 + 8 * q])[0] for q in range(np_))
            out.append((op, keys))
            i = j + 8 + 8 * np_
            continue
        if op == 0xc4:  # wide
            sub = code[i + 1]
            slot = struct.unpack(">H", code[i + 2:i + 4])[0]
            if sub == 0x84:
                out.append((0x84, (slot, struct.unpack(">h", code[i + 4:i + 6])[0])))
                i += 6
            else:
                out.append((sub, slot))
                i += 4
            continue
        kind = _OPS.get(op, "?")
        if kind == "?":
            raise ValueError(f"unknown opcode 0x{op:02x}")
        if kind is None:
            out.append((op, None))
            i += 1
        elif kind == "B":
            out.append((op, struct.unpack(">b", code[i + 1:i + 2])[0]))
            i += 2
        elif kind == "h":
            out.append((op, struct.unpack(">h", code[i + 1:i + 3])[0]))
            i += 3
        elif kind == "L":
            out.append((0x12, const(code[i + 1])))
            i += 2
        elif kind == "H":
            k = struct.unpack(">H", code[i + 1:i + 3])[0]
            if op in _LDC:
                out.append((0x12 if op == 0x13 else op, const(k)))
            elif 0xb2 <= op <= 0xb8:
                out.append((op, ref(k)))
            else:  # new, anewarray, checkcast, instanceof
                out.append((op, ("class", cname(k))))
            i += 3
        elif kind == "S":
            out.append((op, code[i + 1]))
            i += 2
        elif kind == "I":
            out.append((op, (code[i + 1], struct.unpack(">b", code[i + 2:i + 3])[0])))
            i += 3
        elif kind == "J":
            out.append((op, None))
            i += 3
        elif kind == "K":
            out.append((op, None))
            i += 5
        elif kind == "D":
            out.append((op, ref(struct.unpack(">H", code[i + 1:i + 3])[0])))
            i += 5
        elif kind == "Y":
            e = cp[struct.unpack(">H", code[i + 1:i + 3])[0]]
            nm, ds = nat(e[2])
            bsm, bargs = bootstraps[e[1]] if e[1] < len(bootstraps) else (None, ())
            out.append((op, ("indy", nm, ds, bsm, bargs)))
            i += 5
        elif kind == "N":
            out.append((op, code[i + 1]))
            i += 2
        elif kind == "M":
            out.append((op, (("class", cname(struct.unpack(">H", code[i + 1:i + 3])[0])), code[i + 3])))
            i += 4
    return out
