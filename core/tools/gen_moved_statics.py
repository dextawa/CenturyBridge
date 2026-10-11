#!/usr/bin/env python3
"""Static members 26.x moved out of a class that still exists (EntityType.PIG ->
EntityTypes.PIG, LightTexture.pack -> LightCoordsUtil.pack): a 1.20.1 mod's GETSTATIC /
INVOKESTATIC still names the old owner, which no longer has them -- NoSuchFieldError /
NoSuchMethodError at the first use. Writes fieldRedirects / staticRedirects into
shims-26.2.json for every member of a reviewed (old owner -> new owner) pair that is static
in 1.21.11, gone from the old owner in 26.2 and present -- same name, same descriptor --
on the new one. The pairs are reviewed by hand: same name and descriptor alone also
matches unrelated constants (BlockTags/ItemTags share tag names, feature and placement keys
share ResourceKey). Members are keyed in the intermediary analysis namespace through the
1.21.11 wall table.

    python3 core/tools/gen_moved_statics.py
"""
import json
import struct
import zipfile

OLD = 'data/jars/client-1.21.11-mojang.jar'
NEW = 'data/jars/client-26.2.jar'
WALL = 'data/mappings/inter-to-mojang-1.21.11.tsv'
SHIMS = 'core/segments/shims-26.2.json'
PAIRS = [
    ('net/minecraft/world/entity/EntityType', 'net/minecraft/world/entity/EntityTypes'),
    ('net/minecraft/world/level/block/entity/BlockEntityType', 'net/minecraft/world/level/block/entity/BlockEntityTypes'),
    ('net/minecraft/util/valueproviders/IntProvider', 'net/minecraft/util/valueproviders/IntProviders'),
    ('net/minecraft/core/cauldron/CauldronInteraction', 'net/minecraft/core/cauldron/CauldronInteractions'),
    ('net/minecraft/client/renderer/LightTexture', 'net/minecraft/util/LightCoordsUtil'),
    ('net/minecraft/util/Brightness', 'net/minecraft/util/LightCoordsUtil'),
]
ACC_STATIC = 0x0008


def read_class(b):
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
    pos += 2
    this = struct.unpack('>H', b[pos:pos + 2])[0]; pos += 4
    name = cp[cp[this][1]]
    ic = struct.unpack('>H', b[pos:pos + 2])[0]; pos += 2 + 2 * ic
    out = {}
    for kind in ('f', 'm'):
        n = struct.unpack('>H', b[pos:pos + 2])[0]; pos += 2
        for _ in range(n):
            acc, ni, di, ac = struct.unpack('>HHHH', b[pos:pos + 8]); pos += 8
            for _ in range(ac):
                pos += 2
                ln = struct.unpack('>I', b[pos:pos + 4])[0]; pos += 4 + ln
            out[(kind, cp[ni], cp[di])] = acc
    return name, out


def load(jar, wanted):
    res = {}
    with zipfile.ZipFile(jar) as z:
        for c in wanted:
            try:
                n, m = read_class(z.read(c + '.class'))
                res[n] = m
            except KeyError:
                pass
    return res


# wall table, reversed: mojang class -> intermediary, (mojang owner, mojang member) -> intermediary member
cls_back, mem_back = {}, {}
for ln in open(WALL, encoding='utf-8'):
    p = ln.rstrip('\n').split('\t')
    if len(p) < 3:
        continue
    if p[0] == 'C':
        cls_back[p[2]] = p[1]
    elif p[0] in ('M', 'F'):
        io, im = p[1].rsplit('.', 1)
        mo, mm = p[2].rsplit('.', 1)
        mem_back[(p[0], mo, mm)] = (io, im)


def back_desc(desc):
    out, i = [], 0
    while i < len(desc):
        ch = desc[i]
        if ch == 'L':
            j = desc.index(';', i)
            out.append('L' + cls_back.get(desc[i + 1:j], desc[i + 1:j]) + ';')
            i = j + 1
        else:
            out.append(ch)
            i += 1
    return ''.join(out)


names = {a for a, _ in PAIRS} | {b for _, b in PAIRS}
old = load(OLD, names)
new = load(NEW, names)
d = json.load(open(SHIMS, encoding='utf-8'))
fr = d.setdefault('fieldRedirects', {})
sr = d.setdefault('staticRedirects', {})
total = 0
for a, b in PAIRS:
    oa, na, nb = old.get(a, {}), new.get(a, {}), new.get(b, {})
    n = 0
    for (k, name, desc), acc in oa.items():
        if not acc & ACC_STATIC or name.startswith('<') or (k, name, desc) in na or (k, name, desc) not in nb:
            continue
        if not nb[(k, name, desc)] & ACC_STATIC:
            continue
        inter = mem_back.get(('F' if k == 'f' else 'M', a, name))
        if inter is None:
            continue  # born after the wall: no 1.20.1 mod can name it
        io, im = inter
        idesc = back_desc(desc)
        if k == 'f':
            fr[f'{io}.{im}:{idesc}'] = b
        else:
            sr[f'{io}.{im}{idesc}'] = f'{b}#{name}'
        n += 1
    print(f'{n:4d}  {a} -> {b}')
    total += n
for k in [k for k in d.get('ledger', {}) if k.split(':')[0].split('(')[0] in
          {key.split(':')[0].split('(')[0] for key in list(fr) + list(sr)}]:
    del d['ledger'][k]
open(SHIMS, 'w', encoding='utf-8', newline='\n').write(json.dumps(d, indent=1, ensure_ascii=False) + '\n')
print('total', total)
