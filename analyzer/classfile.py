"""Minimal .class file parser (stdlib only).

Extracts what the analyzer needs and nothing more:
- constant-pool references (Class / Methodref / Fieldref / InterfaceMethodref)
- class / method / field declarations (for building symbol inventories)
- Runtime(In)VisibleAnnotations on class and members (for Mixin extraction)
"""

from __future__ import annotations

import struct
from dataclasses import dataclass, field


@dataclass
class Annotation:
    type: str  # e.g. "Lorg/spongepowered/asm/mixin/Mixin;"
    values: dict = field(default_factory=dict)


@dataclass
class Member:
    name: str
    desc: str
    access: int
    annotations: list[Annotation] = field(default_factory=list)


@dataclass
class ClassFile:
    name: str
    super_name: str | None
    interfaces: list[str]
    access: int
    class_refs: set[str]
    method_refs: set[tuple[str, str, str]]  # (owner, name, desc)
    field_refs: set[tuple[str, str, str]]
    methods: list[Member]
    fields: list[Member]
    annotations: list[Annotation]


class _Reader:
    __slots__ = ("b", "i")

    def __init__(self, b: bytes):
        self.b = b
        self.i = 0

    def u1(self) -> int:
        v = self.b[self.i]
        self.i += 1
        return v

    def u2(self) -> int:
        v = struct.unpack_from(">H", self.b, self.i)[0]
        self.i += 2
        return v

    def u4(self) -> int:
        v = struct.unpack_from(">I", self.b, self.i)[0]
        self.i += 4
        return v

    def skip(self, n: int) -> None:
        self.i += n

    def raw(self, n: int) -> bytes:
        v = self.b[self.i : self.i + n]
        self.i += n
        return v


def parse(data: bytes, want_annotations: bool = True, want_refs: bool = True) -> ClassFile:
    r = _Reader(data)
    if r.u4() != 0xCAFEBABE:
        raise ValueError("not a class file")
    r.skip(4)  # minor, major

    # ---- constant pool ----
    cp_count = r.u2()
    utf8: dict[int, str] = {}
    ints: dict[int, int] = {}
    cls_idx: dict[int, int] = {}  # index -> name_index
    nat: dict[int, tuple[int, int]] = {}  # index -> (name_index, desc_index)
    mrefs: list[tuple[int, int, bool]] = []  # (class_index, nat_index, is_field)
    i = 1
    while i < cp_count:
        tag = r.u1()
        if tag == 1:
            ln = r.u2()
            try:
                utf8[i] = r.raw(ln).decode("utf-8", errors="replace")
            except Exception:
                utf8[i] = ""
        elif tag == 7:
            cls_idx[i] = r.u2()
        elif tag in (9, 10, 11):
            c, n = r.u2(), r.u2()
            mrefs.append((c, n, tag == 9))
        elif tag == 12:
            nat[i] = (r.u2(), r.u2())
        elif tag in (8, 16, 19, 20):
            r.skip(2)
        elif tag == 3:
            ints[i] = struct.unpack_from(">i", r.b, r.i)[0]
            r.skip(4)
        elif tag in (4, 17, 18):
            r.skip(4)
        elif tag in (5, 6):
            r.skip(8)
            i += 1  # long/double take two slots
        elif tag == 15:
            r.skip(3)
        else:
            raise ValueError(f"bad cp tag {tag}")
        i += 1

    def cname(idx: int) -> str:
        return utf8.get(cls_idx.get(idx, -1), "")

    access = r.u2()
    this_name = cname(r.u2())
    super_idx = r.u2()
    super_name = cname(super_idx) if super_idx else None
    interfaces = [cname(r.u2()) for _ in range(r.u2())]

    class_refs: set[str] = set()
    method_refs: set[tuple[str, str, str]] = set()
    field_refs: set[tuple[str, str, str]] = set()
    if want_refs:
        for idx in cls_idx:
            n = cname(idx)
            if n.startswith("["):  # array class ref; extract element type if object
                depth = 0
                while depth < len(n) and n[depth] == "[":
                    depth += 1
                if depth < len(n) and n[depth] == "L" and n.endswith(";"):
                    n = n[depth + 1 : -1]
                else:
                    continue
            class_refs.add(n)
        for c, natx, is_field in mrefs:
            owner = cname(c)
            if owner.startswith("["):
                continue
            ni, di = nat.get(natx, (-1, -1))
            name, desc = utf8.get(ni, ""), utf8.get(di, "")
            (field_refs if is_field else method_refs).add((owner, name, desc))

    # ---- annotation parsing helpers ----
    def read_element_value(r: _Reader):
        tag = chr(r.u1())
        if tag == "s":
            return utf8.get(r.u2())
        if tag in "BCISZ":
            return ints.get(r.u2())
        if tag in "DFJ":
            r.skip(2)
            return None
        if tag == "e":
            ti, ci = r.u2(), r.u2()
            return (utf8.get(ti, ""), utf8.get(ci, ""))
        if tag == "c":
            return utf8.get(r.u2(), "")
        if tag == "@":
            return read_annotation(r)
        if tag == "[":
            return [read_element_value(r) for _ in range(r.u2())]
        raise ValueError(f"bad element_value tag {tag!r}")

    def read_annotation(r: _Reader) -> Annotation:
        a = Annotation(type=utf8.get(r.u2(), ""))
        for _ in range(r.u2()):
            name = utf8.get(r.u2(), "")
            a.values[name] = read_element_value(r)
        return a

    def read_attributes(owner_annos: list[Annotation] | None) -> None:
        for _ in range(r.u2()):
            attr_name = utf8.get(r.u2(), "")
            length = r.u4()
            end = r.i + length
            if (
                want_annotations
                and owner_annos is not None
                and attr_name in ("RuntimeVisibleAnnotations", "RuntimeInvisibleAnnotations")
            ):
                try:
                    for _ in range(r.u2()):
                        owner_annos.append(read_annotation(r))
                except Exception:
                    pass
            r.i = end

    # ---- fields & methods ----
    def read_members() -> list[Member]:
        out = []
        for _ in range(r.u2()):
            acc = r.u2()
            name = utf8.get(r.u2(), "")
            desc = utf8.get(r.u2(), "")
            m = Member(name, desc, acc)
            read_attributes(m.annotations if want_annotations else None)
            out.append(m)
        return out

    fields = read_members()
    methods = read_members()
    class_annos: list[Annotation] = []
    read_attributes(class_annos if want_annotations else None)

    return ClassFile(
        name=this_name,
        super_name=super_name,
        interfaces=interfaces,
        access=access,
        class_refs=class_refs,
        method_refs=method_refs,
        field_refs=field_refs,
        methods=methods,
        fields=fields,
        annotations=class_annos,
    )
