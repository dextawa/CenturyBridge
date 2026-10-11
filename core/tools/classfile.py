"""A small class-file reader for the step tools: the class's shape and, when asked, method bodies.

  read(bytes, code=False) -> ClassInfo | None
  param_reads(method, desc) -> set of parameter indices the body reads (None: no body)

Only what the generators need: names, access, supertypes, fields and methods with descriptors, and
for a method body the local-variable slots it loads -- enough to tell whether a parameter that a
new release dropped was ever used.
"""
import struct
from dataclasses import dataclass, field

ACC_PUBLIC, ACC_PRIVATE, ACC_PROTECTED, ACC_STATIC, ACC_FINAL = 0x1, 0x2, 0x4, 0x8, 0x10
ACC_BRIDGE, ACC_INTERFACE, ACC_ABSTRACT, ACC_SYNTHETIC, ACC_ENUM = 0x40, 0x200, 0x400, 0x1000, 0x4000


@dataclass
class Method:
    name: str
    desc: str
    access: int
    code: bytes = None


@dataclass
class ClassInfo:
    name: str
    access: int
    super: str
    interfaces: list
    fields: list = field(default_factory=list)   # (name, desc, access)
    methods: list = field(default_factory=list)  # Method


def read(b, code=False):
    if b[:4] != b"\xca\xfe\xba\xbe":
        return None
    n = struct.unpack(">H", b[8:10])[0]
    i, idx = 10, 1
    utf8, cls = {}, {}
    while idx < n:
        tag = b[i]
        if tag == 1:
            ln = struct.unpack(">H", b[i + 1:i + 3])[0]
            utf8[idx] = b[i + 3:i + 3 + ln].decode("utf-8", "replace")
            i += 3 + ln
        elif tag in (3, 4):
            i += 5
        elif tag in (5, 6):
            i += 9
            idx += 1
        elif tag == 7:
            cls[idx] = struct.unpack(">H", b[i + 1:i + 3])[0]
            i += 3
        elif tag in (8, 16, 19, 20):
            i += 3
        elif tag in (9, 10, 11, 12, 17, 18):
            i += 5
        elif tag == 15:
            i += 4
        else:
            return None
        idx += 1

    def u2(at):
        return struct.unpack(">H", b[at:at + 2])[0]

    acc = u2(i)
    name = utf8.get(cls.get(u2(i + 2)))
    sup_i = u2(i + 4)
    sup = utf8.get(cls.get(sup_i)) if sup_i else None
    i += 6
    nif = u2(i)
    itfs = [utf8.get(cls.get(u2(i + 2 + 2 * k))) for k in range(nif)]
    i += 2 + 2 * nif
    info = ClassInfo(name, acc, sup, itfs)
    for kind in ("fields", "methods"):
        cnt = u2(i)
        i += 2
        for _ in range(cnt):
            a, nm, ds = u2(i), utf8.get(u2(i + 2)), utf8.get(u2(i + 4))
            i += 6
            na = u2(i)
            i += 2
            body = None
            for _ in range(na):
                an = utf8.get(u2(i))
                ln = struct.unpack(">I", b[i + 2:i + 6])[0]
                if code and kind == "methods" and an == "Code":
                    clen = struct.unpack(">I", b[i + 10:i + 14])[0]
                    body = b[i + 14:i + 14 + clen]
                i += 6 + ln
            if kind == "fields":
                info.fields.append((nm, ds, a))
            else:
                info.methods.append(Method(nm, ds, a, body))
    return info


def split_args(desc):
    """argument type descriptors and the return descriptor of a method descriptor"""
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


# operand bytes after each opcode, for the fixed-length instructions
_LEN = {}
for op in range(0x00, 0x10):
    _LEN[op] = 0
_LEN.update({0x10: 1, 0x11: 2, 0x12: 1, 0x13: 2, 0x14: 2})
for op in range(0x15, 0x1a):
    _LEN[op] = 1  # xload index
for op in range(0x1a, 0x36):
    _LEN[op] = 0
for op in range(0x36, 0x3b):
    _LEN[op] = 1  # xstore index
for op in range(0x3b, 0x84):
    _LEN[op] = 0
_LEN[0x84] = 2  # iinc
for op in range(0x85, 0x99):
    _LEN[op] = 0
for op in range(0x99, 0xa9):
    _LEN[op] = 2  # if*, goto, jsr
_LEN[0xa9] = 1  # ret
for op in range(0xac, 0xb2):
    _LEN[op] = 0
for op in range(0xb2, 0xb9):
    _LEN[op] = 2
_LEN.update({0xb9: 4, 0xba: 4, 0xbb: 2, 0xbc: 1, 0xbd: 2, 0xbe: 0, 0xbf: 0, 0xc0: 2, 0xc1: 2,
             0xc2: 0, 0xc3: 0, 0xc5: 3, 0xc6: 2, 0xc7: 2, 0xc8: 4, 0xc9: 4})


def slots_read(code):
    """local-variable slots the body loads (xload, iinc, ret, and their wide forms)"""
    out = set()
    i = 0
    while i < len(code):
        op = code[i]
        if 0x15 <= op <= 0x19 or op == 0xa9:
            out.add(code[i + 1])
        elif 0x1a <= op <= 0x2d:
            out.add((op - 0x1a) % 4)
        elif op == 0x84:
            out.add(code[i + 1])
        if op == 0xaa:  # tableswitch
            j = i + 1 + (3 - i % 4)
            low, high = struct.unpack(">ii", code[j + 4:j + 12])
            i = j + 12 + 4 * (high - low + 1)
            continue
        if op == 0xab:  # lookupswitch
            j = i + 1 + (3 - i % 4)
            npairs = struct.unpack(">i", code[j + 4:j + 8])[0]
            i = j + 8 + 8 * npairs
            continue
        if op == 0xc4:  # wide
            sub = code[i + 1]
            slot = struct.unpack(">H", code[i + 2:i + 4])[0]
            if 0x15 <= sub <= 0x19 or sub in (0x84, 0xa9):
                out.add(slot)
            i += 6 if sub == 0x84 else 4
            continue
        if op not in _LEN:
            raise ValueError(f"unknown opcode 0x{op:02x} at {i}")
        i += 1 + _LEN[op]
    return out


def param_slots(desc, static):
    """the local slot of each parameter"""
    args, _ = split_args(desc)
    slot = 0 if static else 1
    out = []
    for a in args:
        out.append(slot)
        slot += 2 if a in ("J", "D") else 1
    return out


def param_reads(method):
    """indices of the parameters the method's body reads; None when it has no body"""
    if method.code is None:
        return None
    read_slots = slots_read(method.code)
    slots = param_slots(method.desc, bool(method.access & ACC_STATIC))
    return {k for k, s in enumerate(slots) if s in read_slots}
