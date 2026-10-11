#!/usr/bin/env python3
"""The 1.20.5 holderization, bridged for every tree past it.

1.20.5 turned the built-in registry types MobEffect, Potion, GameEvent, SoundEvent,
Attribute and MapDecorationType into Holder<T> in the API: their constants
(MobEffects.SPEED: MobEffect -> Holder<MobEffect>) and the methods and constructors taking
or returning them (LivingEntity.hasEffect(MobEffect) -> hasEffect(Holder<MobEffect>)). A 1.20.1
mod still reads the constant as the value and passes the value. The bridge is exact: the
constant's holder's value(); an argument wrapped by its registry (Registry.wrapAsHolder: the
registered reference, or a direct holder for an unregistered value -- as 1.20.5's own callers
do); a returned holder's value().

Everything is derived from the stubs: the 1.20.4 -> 1.20.5 members whose descriptor changed by
exactly that (and nothing else), kept only where the tree's own stub still has the holder
shape (by intermediary name, or through the namespace wall for 26.x). Keys a tree already
wires are left alone and listed. Writes rt/<pkg>/CbHolders.java (+ ClientCbHolders for
client-only owners) and the wiring into shims-<ver>.json.

    python3 core/tools/gen_holder_bridges.py [version...]
"""
import json
import os
import re
import struct
import sys
import zipfile

TREES = {'1.21.1': 'v1_21_1', '1.21.4': 'v1_21_4', '1.21.8': 'v1_21_8', '1.21.11': 'v1_21_11', '26.2': 'v26_2'}
OLD, NEW = 'data/jars/client-1.20.4-intermediary.jar', 'data/jars/client-1.20.5-intermediary.jar'
WALL = 'data/mappings/inter-to-mojang-1.21.11.tsv'
SIDES = 'core/segments/sides-1.20.1.json'
HOLDER = 'net/minecraft/class_6880'
# value type -> its built-in registry (BuiltInRegistries field, intermediary) and a helper name
REGISTRY = {
    'net/minecraft/class_1291': ('field_41174', 'MobEffect'),
    'net/minecraft/class_1842': ('field_41179', 'Potion'),
    'net/minecraft/class_5712': ('field_41171', 'GameEvent'),
    'net/minecraft/class_3414': ('field_41172', 'SoundEvent'),
    'net/minecraft/class_1320': ('field_41190', 'Attribute'),
    'net/minecraft/class_20$class_21': ('field_50078', 'MapDecorationType'),
}


# ---------------------------------------------------------------- class files

def parse(b):
    pos = 8
    cnt = struct.unpack('>H', b[pos:pos + 2])[0]; pos += 2
    cp = [None] * cnt
    i = 1
    while i < cnt:
        tag = b[pos]; pos += 1
        if tag == 1:
            ln = struct.unpack('>H', b[pos:pos + 2])[0]; pos += 2
            cp[i] = b[pos:pos + ln].decode('utf-8', 'replace'); pos += ln
        elif tag in (3, 4):
            pos += 4
        elif tag in (5, 6):
            pos += 8; i += 1
        elif tag in (7, 8, 16, 19, 20):
            cp[i] = ('ref', struct.unpack('>H', b[pos:pos + 2])[0]); pos += 2
        elif tag in (9, 10, 11, 12, 17, 18):
            pos += 4
        elif tag == 15:
            pos += 3
        i += 1
    acc = struct.unpack('>H', b[pos:pos + 2])[0]; pos += 2
    this = struct.unpack('>H', b[pos:pos + 2])[0]; pos += 4
    name = cp[cp[this][1]]
    ic = struct.unpack('>H', b[pos:pos + 2])[0]; pos += 2 + 2 * ic
    members = {'f': {}, 'm': {}}
    for kind in ('f', 'm'):
        n = struct.unpack('>H', b[pos:pos + 2])[0]; pos += 2
        for _ in range(n):
            a, ni, di, ac = struct.unpack('>HHHH', b[pos:pos + 8]); pos += 8
            for _ in range(ac):
                pos += 2
                ln = struct.unpack('>I', b[pos:pos + 4])[0]; pos += 4 + ln
            members[kind][(cp[ni], cp[di])] = a
    # a nested class's real access is in its own InnerClasses entry (the header says public
    # for a protected one): javac reads that
    n = struct.unpack('>H', b[pos:pos + 2])[0]; pos += 2
    for _ in range(n):
        an, ln = struct.unpack('>HI', b[pos:pos + 6]); pos += 6
        if cp[an] == 'InnerClasses':
            q = pos
            k = struct.unpack('>H', b[q:q + 2])[0]; q += 2
            for _ in range(k):
                inner, outer, iname, iacc = struct.unpack('>HHHH', b[q:q + 8]); q += 8
                if inner and cp[cp[inner][1]] == name:
                    acc = iacc
        pos += ln
    return name, acc, members


def load(jar):
    out = {}
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if n.endswith('.class'):
                try:
                    c, acc, m = parse(z.read(n))
                    out[c] = (acc, m)
                except Exception:
                    pass
    return out


def params(desc):
    return re.findall(r'\[*(?:[ZBCSIJFD]|L[^;]+;)', desc[1:desc.index(')')]), desc[desc.index(')') + 1:]


# ---------------------------------------------------------------- the 1.20.5 change set

def change_set():
    a, b = load(OLD), load(NEW)
    hd = 'L' + HOLDER + ';'
    consts, methods = [], []
    for c, (cacc, ma) in a.items():
        if c not in b:
            continue
        mb = b[c][1]
        for (n, d), acc in ma['f'].items():
            if not acc & 0x8 or d[1:-1] not in REGISTRY:
                continue
            nd = next((x for (fn, x) in mb['f'] if fn == n), None)
            if nd == hd:
                consts.append((c, n, d))
        by_name = {}
        for (n, d), acc in mb['m'].items():
            by_name.setdefault(n, []).append((d, acc))
        for (n, d), acc in ma['m'].items():
            if acc & 0x1000 or (n, d) in mb['m']:
                continue
            op, orr = params(d)
            for nd, nacc in by_name.get(n, []):
                np_, nr = params(nd)
                if len(np_) != len(op):
                    continue
                wraps = [i for i, (x, y) in enumerate(zip(op, np_)) if x != y]
                if not all(np_[i] == hd and op[i][1:-1] in REGISTRY for i in wraps):
                    continue
                unwrap = orr != nr
                if unwrap and not (nr == hd and orr[1:-1] in REGISTRY):
                    continue
                if wraps or unwrap:
                    methods.append({'owner': c, 'name': n, 'old': d, 'new': nd, 'static': bool(acc & 8),
                                    'wraps': wraps, 'unwrap': unwrap})
    return consts, methods


# ---------------------------------------------------------------- namespaces

class Names:
    """Java source spelling and target existence of intermediary types/members, per tree."""

    def __init__(self, ver):
        self.ver = ver
        self.mojang = ver.startswith('26.')
        stub = f'data/jars/client-{ver}.jar' if self.mojang else f'data/jars/client-{ver}-intermediary.jar'
        self.stub = load(stub)
        self.cls, self.mem = {}, {}
        if self.mojang:
            for ln in open(WALL, encoding='utf-8'):
                p = ln.rstrip('\n').split('\t')
                if len(p) < 3:
                    continue
                if p[0] == 'C':
                    self.cls[p[1]] = p[2]
                else:
                    self.mem[p[1]] = p[2].rsplit('.', 1)[1]

    def cname(self, inter):
        return self.cls.get(inter, inter) if self.mojang else inter

    def mname(self, owner, name):
        if not self.mojang or name.startswith('<'):
            return name
        return self.mem.get(owner + '.' + name, name)

    def desc(self, d):
        return re.sub(r'L([^;]+);', lambda m: 'L' + self.cname(m.group(1)) + ';', d)

    def has_class(self, inter):
        return self.cname(inter) in self.stub

    def all_types_exist(self, *descs):
        """every class a bridge would name exists in this tree, is public (the bridge is
        compiled code naming it) and is not anonymous"""
        for d in descs:
            for t in re.findall(r'L([^;]+);', d):
                if re.search(r'\$\d', t) or not self.has_class(t):
                    return False
                if not self.stub[self.cname(t)][0] & 0x1:
                    return False
        return True

    def is_static(self, owner, name, desc):
        c = self.stub.get(self.cname(owner))
        acc = c[1]['m'].get((self.mname(owner, name), self.desc(desc))) if c else None
        return acc is not None and acc & 0x8 != 0

    def public_member(self, owner, kind, name, desc):
        """the bridge calls it from outside: public in this tree. (A protected or private one
        a 1.20.1 mod reached as a subclass or through its own access widener is not reachable
        from a static bridge -- left to a super-call bridge.)"""
        c = self.stub.get(self.cname(owner))
        acc = c[1][kind].get((self.mname(owner, name), self.desc(desc))) if c else None
        return acc is not None and acc & 0x1 != 0

    def has_method(self, owner, name, desc):
        c = self.stub.get(self.cname(owner))
        return c is not None and (self.mname(owner, name), self.desc(desc)) in c[1]['m']

    def has_field(self, owner, name, desc):
        c = self.stub.get(self.cname(owner))
        return c is not None and (self.mname(owner, name), self.desc(desc)) in c[1]['f']

    def java(self, t):
        """a descriptor type as Java source"""
        prim = {'Z': 'boolean', 'B': 'byte', 'C': 'char', 'S': 'short', 'I': 'int', 'J': 'long', 'F': 'float', 'D': 'double', 'V': 'void'}
        dims = len(t) - len(t.lstrip('['))
        base = t.lstrip('[')
        s = prim[base] if base in prim else self.cname(base[1:-1]).replace('/', '.').replace('$', '.')
        return s + '[]' * dims

    def holder(self, value_type):
        return f'{self.java("L" + HOLDER + ";")}<{self.java(value_type)}>'

    def value_call(self):
        return 'value' if self.mojang else 'comp_349'

    def wrap_expr(self, value_type, x):
        reg = REGISTRY[value_type[1:-1]][0]
        registries = self.java('Lnet/minecraft/class_7923;')
        field = self.mname('net/minecraft/class_7923', reg)
        call = 'wrapAsHolder' if self.mojang else 'method_47983'
        return f'({x} == null ? null : {registries}.{field}.{call}({x}))'


# ---------------------------------------------------------------- generation

def wired(d):
    keys = set()
    for sec in ('instanceRedirects', 'staticRedirects', 'ctorRedirects', 'superCtorReshapes', 'fieldGetters',
                'superRedirects', 'fieldRedirects'):
        keys |= set(d.get(sec, {}))
    keys |= set(d.get('covers', []) if isinstance(d.get('covers'), list) else d.get('covers', {}))
    return keys


def main(versions):
    consts, methods = change_set()
    print(f'1.20.5 holderization: {len(consts)} constants, {len(methods)} methods/constructors')
    # sides-1.20.1.json: {"client": [classes that exist only on the client], "datagen": [...]}
    client_only = set(json.load(open(SIDES, encoding='utf-8')).get('client', [])) if os.path.exists(SIDES) else set()
    for v in versions:
        pkg = TREES[v]
        nm = Names(v)
        sp = f'core/segments/shims-{v}.json'
        d = json.load(open(sp, encoding='utf-8'))
        have = wired(d)
        common, client = [], []
        wiring = {'fieldGetters': {}, 'instanceRedirects': {}, 'staticRedirects': {}, 'superCtorReshapes': {}}
        skipped = gone = 0
        wraps_used = set()
        for (c, n, od) in consts:
            key = f'{c}.{n}:{od}'
            if key in have:
                skipped += 1
                continue
            if (not nm.has_field(c, n, 'L' + HOLDER + ';') or not nm.all_types_exist(od, 'L' + c + ';')
                    or not nm.public_member(c, 'f', n, 'L' + HOLDER + ';')):
                gone += 1
                continue
            body = (f'    /** {key}: the holder\'s value, as 1.20.1 read the constant */\n'
                    f'    public static {nm.java(od)} {n}() {{\n'
                    f'        return ({nm.java(od)}) {nm.java("L" + c + ";")}.{nm.mname(c, n)}.{nm.value_call()}();\n'
                    f'    }}\n')
            (client if c in client_only else common).append(body)
            wiring['fieldGetters'][key] = 'client' if c in client_only else 'common'
        for m in methods:
            c, n, od, nd = m['owner'], m['name'], m['old'], m['new']
            key = f'{c}.{n}{od}'
            if key in have:
                skipped += 1
                continue
            if (not nm.has_method(c, n, nd) or not nm.all_types_exist(od, nd, 'L' + c + ';')
                    or not nm.public_member(c, 'm', n, nd) or nm.is_static(c, n, nd) != m['static']):
                gone += 1
                continue
            op, orr = params(od)
            side = 'client' if c in client_only else 'common'
            if n == '<init>':
                # a constructor: every NEW and super() site reshaped, the changed arguments wrapped
                ops = []
                for i, t in enumerate(op):
                    if i in m['wraps']:
                        helper = 'wrap' + REGISTRY[t[1:-1]][1]
                        wraps_used.add(t)
                        cls = f'top/dext/centurybridge/rt/{pkg}/' + ('ClientCbHolders' if side == 'client' else 'CbHolders')
                        ops.append(f'conv:{i} {cls}#{helper}({t})L{HOLDER};')
                    else:
                        ops.append(f'keep:{i}')
                wiring['superCtorReshapes'][key] = {'desc': nd, 'ops': ops, 'side': side}
                continue
            args = [f'{nm.java(t)} a{i}' for i, t in enumerate(op)]
            call_args = [nm.wrap_expr(t, f'a{i}') if i in m['wraps'] else f'a{i}' for i, t in enumerate(op)]
            if m['wraps']:
                wraps_used.update(op[i] for i in m['wraps'])
            target = (nm.java('L' + c + ';') if m['static'] else 'self') + f'.{nm.mname(c, n)}({", ".join(call_args)})'
            ret = nm.java(orr)
            if m['unwrap']:
                expr = f'{target}'
                stmt = f'        {nm.holder(orr)} h = {expr};\n        return h == null ? null : ({ret}) h.{nm.value_call()}();\n'
            elif orr == 'V':
                stmt = f'        {target};\n'
            else:
                stmt = f'        return {target};\n'
            recv = [] if m['static'] else [f'{nm.java("L" + c + ";")} self']
            body = (f'    /** {key} */\n'
                    f'    public static {ret} {n}({", ".join(recv + args)}) {{\n{stmt}    }}\n')
            (client if side == 'client' else common).append(body)
            wiring['staticRedirects' if m['static'] else 'instanceRedirects'][key] = side
        helpers = {'common': [], 'client': []}
        for t in sorted(wraps_used):
            name = 'wrap' + REGISTRY[t[1:-1]][1]
            h = (f'    /** {t[1:-1]} -> its built-in registry\'s holder (Registry.wrapAsHolder) */\n'
                 f'    public static {nm.holder(t)} {name}({nm.java(t)} value) {{\n'
                 f'        return {nm.wrap_expr(t, "value")};\n    }}\n')
            # constructors on client-only owners name the client class's helper; keep one per side
            helpers['common'].append(h)
            helpers['client'].append(h)
        base = f'core/shims/{v}/src/top/dext/centurybridge/rt/{pkg}'
        header = ('package top.dext.centurybridge.rt.{pkg};\n\n/**\n'
                  ' * 1.20.5 holderization (core/tools/gen_holder_bridges.py -- generated, do not edit): a 1.20.1\n'
                  ' * mod reads MobEffect/Potion/GameEvent/SoundEvent/Attribute/MapDecorationType constants as\n'
                  ' * values and passes values; the API now holds and takes Holder<T>. Constants answer their\n'
                  ' * holder\'s value(); arguments are wrapped by their registry, returned holders unwrapped.\n */\n')
        for side, body, clsname in (('common', common, 'CbHolders'), ('client', client, 'ClientCbHolders')):
            path = f'{base}/{clsname}.java'
            needed = body or (side == 'common')
            if not needed:
                if os.path.exists(path):
                    os.remove(path)
                continue
            env = '@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)\n' if side == 'client' else ''
            src = header.format(pkg=pkg) + env + f'public final class {clsname} {{\n\n' + '\n'.join(helpers[side] + body) + \
                f'\n    private {clsname}() {{\n    }}\n}}\n'
            open(path, 'w', encoding='utf-8', newline='\n').write(src)
        rt = f'top/dext/centurybridge/rt/{pkg}/'
        for sec in ('fieldGetters', 'instanceRedirects', 'staticRedirects'):
            for key, side in wiring[sec].items():
                d.setdefault(sec, {})[key] = rt + ('ClientCbHolders' if side == 'client' else 'CbHolders')
        for key, spec in wiring['superCtorReshapes'].items():
            d.setdefault('superCtorReshapes', {})[key] = {'desc': spec['desc'], 'ops': spec['ops']}
        led = d.get('ledger', {})
        for key in [k for k in led if k in wiring['fieldGetters'] or k in wiring['instanceRedirects']
                    or k in wiring['staticRedirects'] or k in wiring['superCtorReshapes']]:
            del led[key]
        open(sp, 'w', encoding='utf-8', newline='\n').write(json.dumps(d, indent=1, ensure_ascii=False) + '\n')
        print(f'{v}: {sum(len(x) for x in wiring.values())} bridged '
              f'({len(wiring["fieldGetters"])} constants, {len(wiring["instanceRedirects"]) + len(wiring["staticRedirects"])} methods, '
              f'{len(wiring["superCtorReshapes"])} constructors), {skipped} already wired, {gone} not in this tree')


if __name__ == '__main__':
    main(sys.argv[1:] or list(TREES))
