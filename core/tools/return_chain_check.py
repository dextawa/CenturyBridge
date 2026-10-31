#!/usr/bin/env python3
"""Return-value chain check over the climbed CenturyBridge layers of one runtime (climb_mixin_check.sh runs it).

A cancellable @Inject at RETURN / TAIL that calls CallbackInfoReturnable.setReturnValue cancels: Mixin's callback
returns the value at once, so every later injector at the same return point -- another layer's, Fabric API's, a mod's
-- never runs. When two or more CenturyBridge layer mixins do that on one target method, only the first-applied one's
answer survives (26.2: the bed layers of 26.2's CbS26BedLayersMixin lost to 26.1's / 1.20.5's createRoots RETURN
injectors, "No model for layer minecraft:bed_head#main"). Such a target is a broken return-value chain; each of those
injectors chains the value instead with MixinExtras @ModifyReturnValue.

  python3 return_chain_check.py <out.tsv> <layer.jar>...
      reads each jar's mixin configs (the mixins they list) and the listed classes' bytecode; writes one row per
      target method that two or more layer mixins answer this way: target, method, then every layer:mixin.handler.
      Exit 1 when there is any such row.
Judged from bytecode only: the class-level @Mixin (value / targets), each handler's @Inject (method, at, cancellable)
and whether its body calls setReturnValue.
"""
import json
import struct
import sys
import zipfile
from collections import defaultdict

CIR = "org/spongepowered/asm/mixin/injection/callback/CallbackInfoReturnable"
INJECT = "Lorg/spongepowered/asm/mixin/injection/Inject;"
MIXIN = "Lorg/spongepowered/asm/mixin/Mixin;"
AT = "Lorg/spongepowered/asm/mixin/injection/At;"


class R:
    def __init__(self, b):
        self.b, self.i = b, 0

    def u1(self):
        v = self.b[self.i]
        self.i += 1
        return v

    def u2(self):
        v = struct.unpack_from(">H", self.b, self.i)[0]
        self.i += 2
        return v

    def u4(self):
        v = struct.unpack_from(">I", self.b, self.i)[0]
        self.i += 4
        return v


def parse(b):
    """(class name, class annotations, [(method name, desc, annotations, code bytes)]) with the constant pool resolved"""
    r = R(b)
    if r.u4() != 0xCAFEBABE:
        return None
    r.u2(), r.u2()
    n = r.u2()
    cp = [None] * n
    k = 1
    while k < n:
        t = r.u1()
        if t == 1:
            ln = r.u2()
            cp[k] = ("utf", b[r.i:r.i + ln].decode("utf-8", "replace"))
            r.i += ln
        elif t in (3, 4):
            cp[k] = ("num", r.u4())
        elif t in (5, 6):
            r.i += 8
            cp[k] = ("num", 0)
            k += 1
        elif t in (7, 8, 16, 19, 20):
            cp[k] = (t, r.u2())
        elif t in (9, 10, 11, 12, 17, 18):
            cp[k] = (t, r.u2(), r.u2())
        elif t == 15:
            cp[k] = (t, r.u1(), r.u2())
        else:
            return None
        k += 1

    def utf(x):
        return cp[x][1]

    def cls(x):
        return utf(cp[x][1])

    def value(rr):
        tag = chr(rr.u1())
        if tag in "BCDFIJSZs":
            x = rr.u2()
            return utf(x) if tag == "s" else (tag, cp[x][1])
        if tag == "e":
            return ("enum", utf(rr.u2()), utf(rr.u2()))
        if tag == "c":
            return ("class", utf(rr.u2()))
        if tag == "@":
            return annotation(rr)
        if tag == "[":
            return [value(rr) for _ in range(rr.u2())]
        raise ValueError(tag)

    def annotation(rr):
        t = utf(rr.u2())
        vals = {}
        for _ in range(rr.u2()):
            name = utf(rr.u2())
            vals[name] = value(rr)
        return (t, vals)

    def attrs(rr):
        anns, code = [], None
        for _ in range(rr.u2()):
            name = utf(rr.u2())
            ln = rr.u4()
            end = rr.i + ln
            if name in ("RuntimeVisibleAnnotations", "RuntimeInvisibleAnnotations"):
                for _ in range(rr.u2()):
                    anns.append(annotation(rr))
            elif name == "Code":
                rr.u2(), rr.u2()
                cl = rr.u4()
                code = b[rr.i:rr.i + cl]
            rr.i = end
        return anns, code

    r.u2()
    this = cls(r.u2())
    r.u2()
    ni = r.u2()  # interfaces: read the count before advancing (r.i += 2 * r.u2() would add to the stale offset)
    r.i += 2 * ni
    for _ in range(r.u2()):
        r.u2(), r.u2(), r.u2()
        attrs(r)
    methods = []
    for _ in range(r.u2()):
        r.u2()
        mn, md = utf(r.u2()), utf(r.u2())
        anns, code = attrs(r)
        methods.append((mn, md, anns, code))
    canns, _ = attrs(r)
    # setReturnValue: an invokevirtual / invokeinterface whose Methodref names CallbackInfoReturnable.setReturnValue
    srv = set()
    for x, e in enumerate(cp):
        if e and e[0] in (10, 11):
            owner = cls(e[1])
            nat = cp[e[2]]
            if owner == CIR and utf(nat[1]) == "setReturnValue":
                srv.add(x)
    return this, canns, methods, srv


def calls_set_return(code, srv):
    """a coarse scan: any u2 operand after invokevirtual (0xb6) / invokeinterface (0xb9) equal to a setReturnValue ref"""
    if not code:
        return False
    for i in range(len(code) - 2):
        if code[i] in (0xB6, 0xB9) and struct.unpack_from(">H", code, i + 1)[0] in srv:
            return True
    return False


def targets(canns):
    out = []
    for t, v in canns:
        if t != MIXIN:
            continue
        for c in v.get("value", []):
            if isinstance(c, tuple) and c[0] == "class":
                out.append(c[1][1:-1])
        for s in v.get("targets", []):
            if isinstance(s, str):
                out.append(s.replace(".", "/"))
    return out


def ats(at):
    vals = at if isinstance(at, list) else [at]
    return [a[1].get("value") for a in vals if isinstance(a, tuple)]


def scan(jar):
    rows = []
    with zipfile.ZipFile(jar) as z:
        names = set(z.namelist())
        listed = []
        for n in names:
            if not n.endswith(".json") or "/" in n:
                continue
            try:
                cfg = json.loads(z.read(n))
            except Exception:
                continue
            if not isinstance(cfg, dict) or "package" not in cfg:
                continue
            pkg = cfg["package"].replace(".", "/")
            for sec in ("mixins", "client", "server"):
                for m in cfg.get(sec) or []:
                    listed.append(f"{pkg}/{m.replace('.', '/')}.class")
        for p in listed:
            if p not in names:
                continue
            parsed = parse(z.read(p))
            if not parsed:
                continue
            this, canns, methods, srv = parsed
            tg = targets(canns)
            for mn, md, anns, code in methods:
                for t, v in anns:
                    if t != INJECT or v.get("cancellable") != ("Z", 1):
                        continue
                    if not any(a in ("RETURN", "TAIL") for a in ats(v.get("at", []))):
                        continue
                    if not calls_set_return(code, srv):
                        continue
                    for sel in v.get("method", []):
                        if not isinstance(sel, str):
                            continue
                        name = sel.split("(")[0].split(";")[-1]
                        for target in tg:
                            rows.append((target, name, f"{this.rsplit('/', 1)[-1]}.{mn}"))
    return rows


def main():
    out, jars = sys.argv[1], sys.argv[2:]
    by = defaultdict(list)
    for j in jars:
        layer = j.rsplit("/", 1)[-1].removesuffix(".jar")
        for target, name, who in scan(j):
            by[(target, name)].append(f"{layer}:{who}")
    broken = {k: v for k, v in by.items() if len(v) > 1}
    with open(out, "w", encoding="utf-8", newline="\n") as f:
        for (target, name), who in sorted(broken.items()):
            f.write("\t".join([target, name] + sorted(who)) + "\n")
    print(f"return-value chain: {len(broken)} target method(s) answered by two or more layers' cancelling RETURN injectors"
          f" ({len(by)} with one or more) -> {out}")
    for (target, name), who in sorted(broken.items()):
        print(f"    return-value chain broken: {target}.{name}: {', '.join(sorted(who))}")
    return 1 if broken else 0


if __name__ == "__main__":
    sys.exit(main())
