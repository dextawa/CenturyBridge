"""Class/interface owner-kind repair for retargeted jars.

Era break: libraries like DataFixerUpper turned classes into interfaces
(DataResult, DFU 6->7). Old constant pools then carry the wrong ref kind and
old code uses invokevirtual where invokeinterface is required.

Repairs, all length-preserving so no offset/StackMapTable fixups are needed:
  1. cp tag flip  Methodref(10) <-> InterfaceMethodref(11) to match the
     current owner kind (fixes invokestatic sites outright).
  2. invokevirtual (3B) on a now-interface owner  -> invokestatic (3B) to a
     synthesized delegation shim (receiver becomes arg 0).
  3. invokeinterface (5B) on a now-class owner    -> invokestatic + 2x nop.
Delegation shims are generated into centurybridge/gen/CbShims inside the jar.
"""

from __future__ import annotations

import gzip
import json
import struct
import zipfile
from pathlib import Path

DATA = Path(__file__).resolve().parent.parent / "data"

# ---------------------------------------------------------------- kind map

def build_kind_map(target: str, extra_jar_dirs: list[Path]) -> dict[str, bool]:
    """class internal name -> is_interface, from client jar + library jars."""
    cache = DATA / "inventory" / f"kinds-{target}.json.gz"
    if cache.exists():
        with gzip.open(cache, "rt", encoding="utf-8") as f:
            return json.load(f)
    kinds: dict[str, bool] = {}

    def scan(jar: Path) -> None:
        try:
            with zipfile.ZipFile(jar) as zf:
                for n in zf.namelist():
                    if not n.endswith(".class"):
                        continue
                    data = zf.read(n)
                    try:
                        name, access = _header(data)
                        kinds[name] = bool(access & 0x0200)
                    except Exception:
                        pass
        except Exception:
            pass

    scan(DATA / "jars" / f"client-{target}.jar")
    for d in extra_jar_dirs:
        for jar in d.rglob("*.jar"):
            scan(jar)
    with gzip.open(cache, "wt", encoding="utf-8") as f:
        json.dump(kinds, f)
    return kinds


def _header(data: bytes) -> tuple[str, int]:
    """Fast parse: skip cp, return (this_class_name, access_flags)."""
    cp_count = struct.unpack_from(">H", data, 8)[0]
    utf8: dict[int, bytes] = {}
    cls: dict[int, int] = {}
    i, n = 10, 1
    while n < cp_count:
        tag = data[i]
        if tag == 1:
            ln = struct.unpack_from(">H", data, i + 1)[0]
            utf8[n] = data[i + 3 : i + 3 + ln]
            i += 3 + ln
        elif tag == 7:
            cls[n] = struct.unpack_from(">H", data, i + 1)[0]
            i += 3
        elif tag in (8, 16, 19, 20):
            i += 3
        elif tag in (3, 4, 9, 10, 11, 12, 17, 18):
            i += 5
        elif tag in (5, 6):
            i += 9
            n += 1
        elif tag == 15:
            i += 4
        else:
            raise ValueError(f"tag {tag}")
        n += 1
    access, this_c = struct.unpack_from(">HH", data, i)
    return utf8[cls[this_c]].decode("utf-8", "replace"), access


# ---------------------------------------------------------------- opcode walk

_FIXED_LEN = {}
for op in range(0x00, 0x10): _FIXED_LEN[op] = 1            # consts
_FIXED_LEN.update({0x10: 2, 0x11: 3, 0x12: 2, 0x13: 3, 0x14: 3})
for op in range(0x15, 0x1A): _FIXED_LEN[op] = 2            # loads w/ index
for op in range(0x1A, 0x2E): _FIXED_LEN[op] = 1
for op in range(0x2E, 0x36): _FIXED_LEN[op] = 1            # array loads
for op in range(0x36, 0x3B): _FIXED_LEN[op] = 2            # stores w/ index
for op in range(0x3B, 0x53): _FIXED_LEN[op] = 1
for op in range(0x53, 0x84): _FIXED_LEN[op] = 1
_FIXED_LEN[0x84] = 3                                       # iinc
for op in range(0x85, 0x99): _FIXED_LEN[op] = 1
for op in range(0x99, 0xA9): _FIXED_LEN[op] = 3            # branches
_FIXED_LEN[0xA9] = 2                                       # ret
for op in range(0xAC, 0xB2): _FIXED_LEN[op] = 1            # returns
_FIXED_LEN.update({0xB2: 3, 0xB3: 3, 0xB4: 3, 0xB5: 3,     # field access
                   0xB6: 3, 0xB7: 3, 0xB8: 3, 0xB9: 5, 0xBA: 5,
                   0xBB: 3, 0xBC: 2, 0xBD: 3, 0xBE: 1, 0xBF: 1,
                   0xC0: 3, 0xC1: 3, 0xC2: 1, 0xC3: 1,
                   0xC6: 3, 0xC7: 3, 0xC8: 5, 0xC9: 5})


def _walk(code: bytearray):
    """Yield (offset, opcode); handles wide/switch variable lengths."""
    i, n = 0, len(code)
    while i < n:
        op = code[i]
        yield i, op
        if op == 0xC4:  # wide
            i += 6 if code[i + 1] == 0x84 else 4
        elif op == 0xAA:  # tableswitch
            pad = (4 - ((i + 1) % 4)) % 4
            lo, hi = struct.unpack_from(">ii", code, i + 1 + pad + 4)
            i += 1 + pad + 12 + (hi - lo + 1) * 4
        elif op == 0xAB:  # lookupswitch
            pad = (4 - ((i + 1) % 4)) % 4
            npairs = struct.unpack_from(">i", code, i + 1 + pad + 4)[0]
            i += 1 + pad + 8 + npairs * 8
        else:
            i += _FIXED_LEN[op]


# ---------------------------------------------------------------- cp model

class Pool:
    def __init__(self, data: bytes):
        self.entries: list[tuple[int, bytes]] = [(0, b"")]  # 1-indexed; (tag, raw sans tag)
        cp_count = struct.unpack_from(">H", data, 8)[0]
        i, n = 10, 1
        while n < cp_count:
            tag = data[i]
            if tag == 1:
                ln = struct.unpack_from(">H", data, i + 1)[0]
                raw = data[i + 1 : i + 3 + ln]
                i += 3 + ln
            elif tag in (7, 8, 16, 19, 20):
                raw = data[i + 1 : i + 3]; i += 3
            elif tag in (3, 4, 9, 10, 11, 12, 17, 18):
                raw = data[i + 1 : i + 5]; i += 5
            elif tag in (5, 6):
                raw = data[i + 1 : i + 9]; i += 9
            elif tag == 15:
                raw = data[i + 1 : i + 4]; i += 4
            else:
                raise ValueError(f"tag {tag}")
            self.entries.append((tag, raw))
            n += 1
            if tag in (5, 6):
                self.entries.append((0, b""))
                n += 1
        self.body_off = i  # offset of access_flags

    def utf8(self, idx: int) -> str:
        tag, raw = self.entries[idx]
        return raw[2:].decode("utf-8", "replace") if tag == 1 else ""

    def class_name(self, idx: int) -> str:
        tag, raw = self.entries[idx]
        if tag != 7:
            return ""
        return self.utf8(struct.unpack(">H", raw)[0])

    def add(self, tag: int, raw: bytes) -> int:
        self.entries.append((tag, raw))
        return len(self.entries) - 1

    def add_utf8(self, s: str) -> int:
        b = s.encode("utf-8")
        return self.add(1, struct.pack(">H", len(b)) + b)

    def add_class(self, name: str) -> int:
        return self.add(7, struct.pack(">H", self.add_utf8(name)))

    def add_methodref(self, cls: str, name: str, desc: str) -> int:
        nat = self.add(12, struct.pack(">HH", self.add_utf8(name), self.add_utf8(desc)))
        return self.add(10, struct.pack(">HH", self.add_class(cls), nat))

    def emit(self) -> bytes:
        out = bytearray(struct.pack(">H", len(self.entries)))
        for tag, raw in self.entries[1:]:
            if tag == 0:
                continue
            out.append(tag)
            out += raw
        return bytes(out)


# ---------------------------------------------------------------- shim registry

class ShimRegistry:
    SHIM_CLASS = "centurybridge/gen/CbShims"

    def __init__(self):
        self.shims: dict[tuple[str, str, str, bool], str] = {}  # (owner,name,desc,iface)->shim name

    def get(self, owner: str, name: str, desc: str, owner_is_iface: bool) -> tuple[str, str]:
        key = (owner, name, desc, owner_is_iface)
        if key not in self.shims:
            self.shims[key] = f"s{len(self.shims)}"
        static_desc = "(L" + owner + ";" + desc[1:]
        return self.shims[key], static_desc


# ---------------------------------------------------------------- class patch

def patch_class(data: bytes, kinds: dict[str, bool], reg: ShimRegistry) -> tuple[bytes, int]:
    pool = Pool(data)
    body = bytearray(data[pool.body_off:])

    # classify method refs
    ref_owner: dict[int, str] = {}
    ref_nat: dict[int, tuple[str, str]] = {}
    flipped = 0
    for idx, (tag, raw) in enumerate(pool.entries):
        if tag in (10, 11):
            ci, ni = struct.unpack(">HH", raw)
            owner = pool.class_name(ci)
            nt, nraw = pool.entries[ni]
            nn, nd = struct.unpack(">HH", nraw)
            ref_owner[idx] = owner
            ref_nat[idx] = (pool.utf8(nn), pool.utf8(nd))
            want = kinds.get(owner)
            if want is not None and want != (tag == 11):
                pool.entries[idx] = (11 if want else 10, raw)
                flipped += 1

    if not flipped:
        return data, 0

    # locate Code attributes and patch call sites (length-preserving only)
    def u2(off): return struct.unpack_from(">H", body, off)[0]

    patched_sites = 0
    off = 8 + u2(6) * 2  # access(2) this(2) super(2) ifcount(2) + interfaces
    for _section in range(2):  # fields, methods
        count = u2(off); off += 2
        for _ in range(count):
            off += 6
            attrs = u2(off); off += 2
            for _a in range(attrs):
                aname = pool.utf8(u2(off))
                alen = struct.unpack_from(">I", body, off + 2)[0]
                if aname == "Code" and _section == 1:
                    code_off = off + 6 + 4
                    code_len = struct.unpack_from(">I", body, code_off)[0]
                    cstart = code_off + 4
                    code = body[cstart : cstart + code_len]
                    changed = False
                    for ioff, op in _walk(bytearray(code)):
                        if op not in (0xB6, 0xB9):
                            continue
                        cpidx = struct.unpack_from(">H", code, ioff + 1)[0]
                        owner = ref_owner.get(cpidx)
                        if owner is None:
                            continue
                        want = kinds.get(owner)
                        if want is None:
                            continue
                        name, desc = ref_nat[cpidx]
                        if op == 0xB6 and want:      # invokevirtual on interface
                            shim, sdesc = reg.get(owner, name, desc, True)
                            sref = pool.add_methodref(ShimRegistry.SHIM_CLASS, shim, sdesc)
                            code[ioff] = 0xB8
                            struct.pack_into(">H", code, ioff + 1, sref)
                            changed = True; patched_sites += 1
                        elif op == 0xB9 and not want:  # invokeinterface on class
                            shim, sdesc = reg.get(owner, name, desc, False)
                            sref = pool.add_methodref(ShimRegistry.SHIM_CLASS, shim, sdesc)
                            code[ioff] = 0xB8
                            struct.pack_into(">H", code, ioff + 1, sref)
                            code[ioff + 3] = 0x00
                            code[ioff + 4] = 0x00
                            changed = True; patched_sites += 1
                    if changed:
                        body[cstart : cstart + code_len] = code
                off += 6 + alen
    # class attributes left untouched (already inside body tail)

    return data[:8] + pool.emit() + bytes(body), patched_sites


# ---------------------------------------------------------------- shim class gen

_LOAD = {"I": 0x15, "J": 0x16, "F": 0x17, "D": 0x18, "L": 0x19, "[": 0x19,
         "B": 0x15, "C": 0x15, "S": 0x15, "Z": 0x15}
_RET = {"I": 0xAC, "J": 0xAD, "F": 0xAE, "D": 0xAF, "L": 0xB0, "[": 0xB0,
        "B": 0xAC, "C": 0xAC, "S": 0xAC, "Z": 0xAC, "V": 0xB1}
_WIDTH = {"J": 2, "D": 2}


def _parse_args(desc: str) -> list[str]:
    args, i = [], 1
    while desc[i] != ")":
        start = i
        while desc[i] == "[":
            i += 1
        if desc[i] == "L":
            i = desc.index(";", i)
        args.append(desc[start : i + 1])
        i += 1
    return args


def synthesize_shim_class(reg: ShimRegistry) -> bytes:
    pool = Pool(struct.pack(">IHHH", 0xCAFEBABE, 0, 61, 1))  # seed with empty pool
    pool.entries = [(0, b"")]
    this_c = pool.add_class(ShimRegistry.SHIM_CLASS)
    super_c = pool.add_class("java/lang/Object")
    code_attr_name = pool.add_utf8("Code")

    methods = bytearray()
    for (owner, name, desc, iface), shim_name in reg.shims.items():
        static_desc = "(L" + owner + ";" + desc[1:]
        args = _parse_args(static_desc)
        ret = static_desc[static_desc.index(")") + 1]
        code = bytearray()
        slot = 0
        for a in args:
            code += bytes([_LOAD[a[0]], slot])
            slot += _WIDTH.get(a[0], 1)
        target_nat = pool.add(12, struct.pack(">HH", pool.add_utf8(name), pool.add_utf8(desc)))
        owner_c = pool.add_class(owner)
        if iface:
            mref = pool.add(11, struct.pack(">HH", owner_c, target_nat))
            code += bytes([0xB9]) + struct.pack(">H", mref) + bytes([slot, 0])
        else:
            mref = pool.add(10, struct.pack(">HH", owner_c, target_nat))
            code += bytes([0xB6]) + struct.pack(">H", mref)
        code.append(_RET[ret[0]])
        code_attr = struct.pack(">HHI", max(slot, 2), max(slot, 1), len(code)) + code + struct.pack(">HH", 0, 0)
        m = struct.pack(
            ">HHHH", 0x0009, pool.add_utf8(shim_name), pool.add_utf8(static_desc), 1
        ) + struct.pack(">HI", code_attr_name, len(code_attr)) + code_attr
        methods += m

    out = struct.pack(">IHH", 0xCAFEBABE, 0, 61)
    out += pool.emit()
    out += struct.pack(">HHHH", 0x1001, this_c, super_c, 0)  # access, this, super, 0 ifaces
    out += struct.pack(">H", 0)  # fields
    out += struct.pack(">H", len(reg.shims)) + methods
    out += struct.pack(">H", 0)  # class attrs
    return bytes(out)
