"""Generated bridges for climb step 1.20.4 -> 1.20.5, area s4rest: every owner outside world/item,
world/effect, core, advancements, world/level/storage, data, world/food, world/level (except levelgen,
saveddata, gameevent and pathfinder, which are this area's), network, server/network and
client/multiplayer.

1.20.5 changed whole families of signatures the same way. Where a damage row's member is still there
under the same name, and its 1.20.4 and 1.20.5 shapes differ only by such changes (every other
parameter and the return the same), the bridge is the change's converters applied by the engine's
generic mechanisms. Every rule is checked against both releases' class files before it emits
anything; the 1.20.5 generic signature has to say the holder / key holds the 1.20.4 type.

  changes (1.20.4 -> 1.20.5: to-new converter / to-old converter)
    holder    GameEvent, SoundEvent, MobEffect, Attribute, CatVariant, FrogVariant -> Holder<T>:
              the registry's wrapAsHolder (the registered reference; a direct holder for a value never
              registered, as 1.20.5's own callers do) / holder.value(). GameEvent, SoundEvent and
              MobEffect use the step's shared rt/CbHolders.
    key       T -> ResourceKey<T> (fields and returns only): - / the registry's value for the key
    codec     Codec -> MapCodec (the dispatched types' codecs): CbCodecs.toMapCodec / fromMapCodec
    loot      ResourceLocation -> ResourceKey<LootTable>: ResourceKey.create(LOOT_TABLE, id) /
              key.location()
    provider  a HolderLookup.Provider parameter 1.20.4 did not have: CbRegistries.current(), the
              registries of the running world (the step's one lookup) / dropped
    mapid     int map id -> MapId: new MapId(id) / id.id()  (rt/CbRestMaps)
    mapid-optional
              OptionalInt map id -> nullable MapId (ItemFrame.getFramedMapId): empty as null / null as
              empty  (rt/CbRestMaps)
    decoration-type
              MapDecoration.Type (the step's enum facade) -> Holder<MapDecorationType>: the type
              1.20.5 registers under the constant's name / the constant of the holder's name, null for
              a type 1.20.4 had no constant for  (rt/CbRestMaps)
    optional  a nullable T -> Optional<T>: Optional.ofNullable / orElse(null)

  rows
    get-static        the field holds the changed type: a getter answers the converted value. A codec
                      field only when 1.20.4's initializer built it as a map codec's codec
                      (RecordCodecBuilder.create, Codec.unit, MapCodec.codec(), or a 1.20.4 method that
                      returns one of those), which fromMapCodec answers exactly.
    get / put         an instance field of the changed type: instanceFieldGetters / Setters
    call...           callReshapes: changed arguments converted (a provider supplied), a changed
                      return converted back; a mod's own super call is reshaped the same way
    new / super-ctor  superCtorReshapes, the same converters
    override / implement
                      overrideHooks: the new shape vanilla calls forwards to the mod's old override, a
                      changed parameter converted to the old type (a provider dropped), a changed return
                      to the new one; the mod's super call is reshaped by a callReshape on the same key
    lambda            lambdaAdapters: the functional interface's method answers the changed type; the
                      mod's lambda is built as a CenturyBridge interface of the old shape and adapted. A
                      named class implementing the interface gets the override hook.
    getter (call / override of a member 1.20.5 dropped)
                      1.20.4's method was a plain getter (this.field, returned) and 1.20.5 keeps that field
                      -- the same Mojang name and type -- behind a plain getter of another name (a record's
                      accessor, most often): the 1.20.4 method comes back as a cover returning the field
                      (a generated mixin, mixin/CbRestRec*), and an override hook makes 1.20.5's getter
                      call a mod's override of the 1.20.4 one (its super call is the cover).

Keys sit on the topmost 1.20.4 class declaring the member (calls and hooks climb to it); a key whose
home lies in another area's owner is left to that area. A row no rule covers is reported as "design"
and left to hand-written wiring (s4rest.json).

  python core/tools/gen_s4rest.py <damage.tsv> <V code jar> <W stub jar> <V server jar>
         <inter-to-mojang-classes.tsv> <layer src root> <out fragment.json> <out report.tsv>
         <W code jar> <mappings dir>
The mappings dir holds intermediary-<v>.tiny; Mojang's client mappings of both releases (field names,
for the getter rule) are read from $CB_MOJANG/mojang-<v>.txt when present, else downloaded (not kept).
The getter rule's mixins are listed in the layer's mixin config (entries CbRestRec*, kept in sync).
"""
import collections
import json
import os
import re
import subprocess
import sys
import zipfile

import urllib.request

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import classfile  # noqa: E402
from classfile import ACC_PUBLIC, ACC_STATIC, split_args  # noqa: E402

AREA = re.compile(r'^(?!net/minecraft/(world/item/|world/effect/|core/|advancements/|world/level/storage/|data/|'
                  r'world/food/|world/level/(?!levelgen/|saveddata/|gameevent/|pathfinder/)|network/|server/network/|'
                  r'client/multiplayer/))')
PKG = 'top/dext/centurybridge/step/v1_20_5/rt'
FIELDS = PKG + '/CbRestFields'
CLIENT_FIELDS = PKG + '/CbRestClientFields'
HOLDERS = PKG + '/CbRestHolders'
LEGACY = PKG + '/CbRestLegacy'
MAPS = PKG + '/CbRestMaps'
SHARED = PKG + '/CbHolders'
CODECS = PKG + '/CbCodecs'
REGISTRIES = PKG + '/CbRegistries'

HOLDER = 'net/minecraft/class_6880'
HOLDER_REF = 'net/minecraft/class_6880$class_6883'
CODEC = 'com/mojang/serialization/Codec'
MAP_CODEC = 'com/mojang/serialization/MapCodec'
RESOURCE_LOCATION = 'net/minecraft/class_2960'
RESOURCE_KEY = 'net/minecraft/class_5321'
LOOT_TABLE = 'net/minecraft/class_52'
PROVIDER = 'net/minecraft/class_7225$class_7874'
MAP_ID = 'net/minecraft/class_9209'
DECORATION_TYPE_V = 'net/minecraft/class_20$class_21'  # renamed to the facade after every rewrite
DECORATION_TYPE_W = 'net/minecraft/class_9428'
OPTIONAL = 'java/util/Optional'
OPTIONAL_INT = 'java/util/OptionalInt'

# registry value type -> (BuiltInRegistries field in 1.20.5, short name, CbHolders pair or None)
REGISTRY_TYPES = {
    'net/minecraft/class_5712': ('field_41171', 'GameEvent', ('gameEvent', 'gameEventValue')),
    'net/minecraft/class_3414': ('field_41172', 'SoundEvent', ('soundEvent', 'soundEventValue')),
    'net/minecraft/class_1291': ('field_41174', 'MobEffect', ('mobEffect', 'mobEffectValue')),
    'net/minecraft/class_1320': ('field_41190', 'Attribute', None),
    'net/minecraft/class_7375': ('field_41163', 'CatVariant', None),
    'net/minecraft/class_7106': ('field_41164', 'FrogVariant', None),
}
MAP_BUILDERS = ('com/mojang/serialization/codecs/RecordCodecBuilder.create:',
                'com/mojang/serialization/Codec.unit:', 'com/mojang/serialization/MapCodec.codec:')

# Parameters 1.20.5 appended to a method mods call and override, each with the value 1.20.5's own caller
# passes, computed from the 1.20.4 arguments (judged by hand from 1.20.5's bytecode). A family: every
# 1.20.4 method under `root` whose arguments match `old` and whose 1.20.5 shape of the same name is the
# 1.20.4 one with `tail` appended. `value` is (static name, the old argument it reads, Java expression of
# that argument `a`): a generated static of the client-side shapes class per argument type, used by the
# call reshape and (with the receiver and every old argument) as the frames hook's default.
APPENDED = [
    {'root': 'net/minecraft/class_922', 'old': re.compile(r'^\(L[^;]+;Lnet/minecraft/class_4587;FFF\)V$'),
     'tail': ['F'], 'value': ('scale', 0, 'a.method_55693()'),
     'basis': "LivingEntityRenderer.setupRotations: 1.20.5 appends the entity's scale, which its render() "
              'passes as entity.getScale() (the pose is scaled by it first)'},
]
SHAPES_CLIENT = PKG + '/CbRestShapesClient'


def class_names(path):
    cm = {}
    for line in open(path, encoding='utf-8'):
        f = line.rstrip('\n').split('\t')
        if len(f) >= 3 and f[0] == 'C':
            cm[f[1]] = f[2]
    return cm


def mojang(c, cm):
    if c in cm:
        return cm[c]
    d = c.rfind('$')
    return mojang(c[:d], cm) + c[d:] if d > 0 else c


def load(jar):
    out = {}
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if n.endswith('.class') and (n.startswith('net/minecraft/') or n.startswith('com/mojang/')):
                c = classfile.read(z.read(n), False)
                if c:
                    out[c.name] = c
    return out


def supers(classes, c):
    out, todo = [], [c]
    while todo:
        x = todo.pop(0)
        if x is None or x in out:
            continue
        out.append(x)
        k = classes.get(x)
        if k:
            todo.append(k.super)
            todo.extend(k.interfaces)
    return out


def find_methods(classes, owner, name):
    """(declaring class, Method) for every method of this name the owner declares or inherits"""
    out = []
    for c in supers(classes, owner):
        k = classes.get(c)
        if k:
            out.extend((c, m) for m in k.methods if m.name == name)
    return out


def topmost(classes, owner, name, desc):
    """the topmost class in owner's (1.20.4) hierarchy declaring name+desc"""
    top = None
    for c in supers(classes, owner):
        k = classes.get(c)
        if k and any(m.name == name and m.desc == desc for m in k.methods):
            top = c
    return top


def type_arg(sig, raw):
    """the single type argument of a signature 'Lraw<...>;' as a class name (or the raw inner text)"""
    if not sig or not sig.startswith('L' + raw + '<') or not sig.endswith('>;'):
        return None
    inner = sig[len(raw) + 2:-2]
    if inner.startswith('L') and inner.endswith(';') and '<' not in inner:
        return inner[1:-1]
    return inner


def jt(d):
    """java source type of a descriptor (raw types)"""
    if d.startswith('['):
        return jt(d[1:]) + '[]'
    if d.startswith('L'):
        return d[1:-1].replace('/', '.').replace('$', '.')
    return {'I': 'int', 'Z': 'boolean', 'J': 'long', 'F': 'float', 'D': 'double', 'B': 'byte', 'S': 'short',
            'C': 'char', 'V': 'void'}[d]


# ---------------------------------------------------------------- changes and their converters

def swap_of(old, new, new_sig):
    """the change turning 1.20.4 descriptor old into 1.20.5 descriptor new (new_sig: the 1.20.5 generic
    signature of that position); None when they are equal, False when no known change explains it"""
    if old == new:
        return None
    if old.startswith('L') and old[1:-1] in REGISTRY_TYPES:
        t = old[1:-1]
        if new in ('L' + HOLDER + ';', 'L' + HOLDER_REF + ';'):
            return ('holder', t) if type_arg(new_sig, new[1:-1]) == t else False
        if new == 'L' + RESOURCE_KEY + ';':
            return ('key', t) if type_arg(new_sig, RESOURCE_KEY) == t else False
        return False
    if old == 'L' + CODEC + ';' and new == 'L' + MAP_CODEC + ';':
        return ('codec', None)
    if old == 'L' + RESOURCE_LOCATION + ';' and new == 'L' + RESOURCE_KEY + ';':
        return ('loot', None) if type_arg(new_sig, RESOURCE_KEY) == LOOT_TABLE else False
    if old == 'I' and new == 'L' + MAP_ID + ';':
        return ('mapid', None)
    if old == 'L' + OPTIONAL_INT + ';' and new == 'L' + MAP_ID + ';':
        return ('mapid-optional', None)
    if old == 'L' + DECORATION_TYPE_V + ';' and new == 'L' + HOLDER + ';':
        return ('decoration-type', None) if type_arg(new_sig, HOLDER) == DECORATION_TYPE_W else False
    if old.startswith('L') and new == 'L' + OPTIONAL + ';':
        return ('optional', old[1:-1]) if type_arg(new_sig, OPTIONAL) == old[1:-1] else False
    return False


CONVERTERS = {}  # (name, desc) -> java source lines of a CbRestHolders static


def _mine(name, desc, doc, ret, param, body):
    CONVERTERS[(name, desc)] = [f'    /** {doc} */', f'    public static {ret} {name}({param}) {{',
                                f'        {body}', '    }', '']
    return (HOLDERS, name, desc)


def simple(t):
    """a short name for a class in converter names: the Mojang simple name where known"""
    return SIMPLE.get(t) or t.rsplit('/', 1)[-1].replace('$', '_')


SIMPLE = {}  # intermediary class -> Mojang simple name, filled by main()


def appended_family(V, W, owner, name, desc):
    """(family, 1.20.4 declaring class, 1.20.5 descriptor) when owner.name(desc) is one of APPENDED's"""
    decl = next((c for c, m in find_methods(V, owner, name) if m.desc == desc and not (m.access & ACC_STATIC)), None)
    if decl is None or decl not in W:
        return None
    oa, orr = split_args(desc)
    for fam in APPENDED:
        if not fam['old'].match(desc) or fam['root'] not in supers(V, decl):
            continue
        want = '(' + ''.join(oa + fam['tail']) + ')' + orr
        if any(m.name == name and m.desc == want for m in W[decl].methods):
            return fam, decl, want
    return None


def to_new(sw, new_desc):
    """(owner, name, desc) of the static taking a 1.20.4 value to the 1.20.5 type new_desc, or None"""
    kind, t = sw
    if kind == 'holder':
        if new_desc != 'L' + HOLDER + ';':
            return None  # a Holder.Reference exists only for a registered value
        reg, n, shared = REGISTRY_TYPES[t]
        if shared:
            return (SHARED, shared[0], f'(L{t};)L{HOLDER};')
        return _mine(f'cb{n}Holder', f'(L{t};)L{HOLDER};',
                     f'a 1.20.4 {n} as the holder 1.20.5 takes: its registered reference, or a direct holder for a '
                     "value never registered (Registry.wrapAsHolder, as 1.20.5's own callers do); null for null",
                     jt('L' + HOLDER + ';'), jt('L' + t + ';') + ' value',
                     f'return value == null ? null : net.minecraft.class_7923.{reg}.method_47983(value);')
    if kind == 'codec':
        return (CODECS, 'toMapCodec', f'(L{CODEC};)L{MAP_CODEC};')
    if kind == 'mapid':
        return (MAPS, 'mapId', f'(I)L{MAP_ID};')
    if kind == 'mapid-optional':
        return (MAPS, 'mapIdOf', f'(L{OPTIONAL_INT};)L{MAP_ID};')
    if kind == 'decoration-type':
        return (MAPS, 'typeHolder', f'(L{DECORATION_TYPE_V};)L{HOLDER};')
    if kind == 'optional':
        n = simple(t)
        return _mine(f'cbOptional{n}', f'(L{t};)L{OPTIONAL};',
                     f'a nullable 1.20.4 {n} as the Optional 1.20.5 takes: empty for null',
                     'java.util.Optional<' + jt('L' + t + ';') + '>', jt('L' + t + ';') + ' value',
                     'return java.util.Optional.ofNullable(value);')
    if kind == 'loot':
        return _mine('cbLootTableKey', f'(L{RESOURCE_LOCATION};)L{RESOURCE_KEY};',
                     'a 1.20.4 loot table id as the key 1.20.5 names the same loot table by; null for null',
                     'net.minecraft.class_5321', 'net.minecraft.class_2960 id',
                     'return id == null ? null : net.minecraft.class_5321.method_29179(net.minecraft.class_7924.field_50079, id);')
    return None  # key: a value has a key only once registered


def to_old(sw, new_desc):
    """(owner, name, desc) of the static taking a value of 1.20.5 type new_desc back to the 1.20.4 type"""
    kind, t = sw
    if kind == 'holder':
        reg, n, shared = REGISTRY_TYPES[t]
        if shared and new_desc == 'L' + HOLDER + ';':
            return (SHARED, shared[1], f'(L{HOLDER};)L{t};')
        return _mine(f'cb{n}', f'({new_desc})L{t};', f'the 1.20.4 {n} a 1.20.5 holder holds; null for null',
                     jt('L' + t + ';'), jt(new_desc) + ' holder',
                     f'return holder == null ? null : ({jt("L" + t + ";")}) holder.comp_349();')
    if kind == 'key':
        reg, n, _ = REGISTRY_TYPES[t]
        return _mine(f'cb{n}FromKey', f'(L{RESOURCE_KEY};)L{t};',
                     f'the registered 1.20.4 {n} a 1.20.5 key names; null for null',
                     jt('L' + t + ';'), 'net.minecraft.class_5321 key',
                     f'return key == null ? null : ({jt("L" + t + ";")}) net.minecraft.class_7923.{reg}.method_29107(key);')
    if kind == 'codec':
        return (CODECS, 'fromMapCodec', f'(L{MAP_CODEC};)L{CODEC};')
    if kind == 'mapid':
        return (MAPS, 'mapIdNumber', f'(L{MAP_ID};)I')
    if kind == 'mapid-optional':
        return (MAPS, 'mapIdOptional', f'(L{MAP_ID};)L{OPTIONAL_INT};')
    if kind == 'decoration-type':
        return (MAPS, 'typeOf', f'(L{HOLDER};)L{DECORATION_TYPE_V};')
    if kind == 'optional':
        n = simple(t)
        return _mine(f'cbOrNull{n}', f'(L{OPTIONAL};)L{t};',
                     f'the 1.20.4 {n} a 1.20.5 Optional holds: null for empty',
                     jt('L' + t + ';'), 'java.util.Optional<' + jt('L' + t + ';') + '> value',
                     'return value.orElse(null);')
    return _mine('cbLootTableId', f'(L{RESOURCE_KEY};)L{RESOURCE_LOCATION};',
                 'the 1.20.4 loot table id a 1.20.5 loot table key names; null for null',
                 'net.minecraft.class_2960', 'net.minecraft.class_5321 key',
                 'return key == null ? null : key.method_29177();')


PROVIDER_SUPPLIER = f'{REGISTRIES}#current()L{PROVIDER};'


def ref(s):
    return f'{s[0]}#{s[1]}{s[2]}'


def dotted(s):
    return f'{s[0]}.{s[1]}{s[2]}'


def match(old_desc, new_desc, new_sig):
    """how 1.20.5's shape is made from 1.20.4's: (per new argument ('provider',) or ('old', i, change),
    return change) -- or None when the shapes are equal or no known change explains the difference"""
    oa, orr = split_args(old_desc)
    na, nr = split_args(new_desc)
    sigs = classfile.split_signature(new_sig) if new_sig else None
    nsa = sigs[0] if sigs and len(sigs[0]) == len(na) else [None] * len(na)
    nsr = sigs[1] if sigs else None
    amap, i = [], 0
    for j, n in enumerate(na):
        if n == 'L' + PROVIDER + ';' and (i >= len(oa) or oa[i] != n):
            amap.append(('provider',))
            continue
        if i >= len(oa):
            return None
        sw = swap_of(oa[i], n, nsa[j])
        if sw is False:
            return None
        amap.append(('old', i, sw))
        i += 1
    if i != len(oa):
        return None
    rsw = swap_of(orr, nr, nsr)
    if rsw is False:
        return None
    if rsw is None and all(a[0] == 'old' and a[2] is None for a in amap):
        return None
    return amap, rsw


def kinds_of(amap, rsw):
    k = {a[2][0] for a in amap if a[0] == 'old' and a[2]} | ({rsw[0]} if rsw else set())
    if any(a[0] == 'provider' for a in amap):
        k.add('provider')
    return ', '.join(sorted(k))


def reshape_entry(amap, rsw, new_desc):
    """callReshapes / superCtorReshapes entry, or None when an argument cannot be converted to the new type"""
    nargs, nret = split_args(new_desc)
    ops = []
    for j, a in enumerate(amap):
        if a[0] == 'provider':
            ops.append('push ' + PROVIDER_SUPPLIER)
        elif a[2] is None:
            ops.append(f'keep:{a[1]}')
        else:
            cv = to_new(a[2], nargs[j])
            if cv is None:
                return None
            ops.append(f'conv:{a[1]} ' + ref(cv))
    entry = {'desc': new_desc, 'ops': ops}
    if rsw is not None:
        entry['ret'] = 'conv ' + ref(to_old(rsw, nret))
    entry['basis'] = 'generated: ' + kinds_of(amap, rsw)
    return entry


# ---------------------------------------------------------------- Mojang field names, method bodies

MANIFEST = 'https://piston-meta.mojang.com/mc/game/version_manifest_v2.json'


def mojang_text(v):
    d = os.environ.get('CB_MOJANG')
    if d and os.path.exists(os.path.join(d, f'mojang-{v}.txt')):
        return open(os.path.join(d, f'mojang-{v}.txt'), encoding='utf-8').read()
    manifest = json.load(urllib.request.urlopen(MANIFEST))
    vj = json.load(urllib.request.urlopen(next(x['url'] for x in manifest['versions'] if x['id'] == v)))
    return urllib.request.urlopen(vj['downloads']['client_mappings']['url']).read().decode('utf-8')


def field_names(mdir, v):
    """intermediary class -> {intermediary field: its Mojang name} of release v"""
    off, cur = {}, None
    for line in mojang_text(v).splitlines():
        if line.startswith('#'):
            continue
        if not line.startswith(' '):
            m = re.match(r'(\S+) -> (\S+):', line)
            cur = off.setdefault(m.group(2).replace('.', '/'), {}) if m else None
            continue
        m = re.match(r'(\S+) (\S+) -> (\S+)$', line.strip())
        if cur is not None and m and '(' not in line:
            cur[m.group(3)] = m.group(2)
    out, owner = {}, None
    for line in open(os.path.join(mdir, f'intermediary-{v}.tiny'), encoding='utf-8'):
        f = line.rstrip('\n').split('\t')
        if f[0] == 'c' and len(f) >= 3:
            owner = (f[1], f[2])
        elif owner and len(f) >= 5 and f[0] == '' and f[1] == 'f':
            mj = off.get(owner[0], {}).get(f[3])
            if mj:
                out.setdefault(owner[1], {})[f[4]] = mj
    return out


_bodies = {}


def bodies(jar, owner):
    """(name, desc) -> the method's instructions (opcode and operand text, javap -c), of jar's class"""
    k = (jar, owner)
    if k not in _bodies:
        out = subprocess.run(['javap', '-c', '-p', '-s', '-cp', jar, owner.replace('/', '.')],
                             capture_output=True, text=True, encoding='utf-8', errors='replace').stdout
        res, name, desc, code = {}, None, None, None
        for line in out.splitlines():
            if line.startswith('  ') and not line.startswith('   ') and line.rstrip().endswith(';'):
                head = line.strip()
                name = re.search(r'(\S+)\(', head)
                name = name.group(1).rsplit('.', 1)[-1] if name else None
                if name == owner.replace('/', '.').rsplit('.', 1)[-1] or (name and name == owner.replace('/', '.')):
                    name = '<init>'
                desc, code = None, None
            elif line.strip().startswith('descriptor:') and name:
                desc = line.strip().split(' ', 1)[1]
                code = res.setdefault((name, desc), [])
            elif code is not None and re.match(r'^\s+\d+: ', line):
                code.append(re.sub(r'^\s+\d+: ', '', line).strip())
        _bodies[k] = res
    return _bodies[k]


def getter_field(jar, owner, name, desc):
    """the field owner.name(desc) returns as a plain getter (this.f, returned), or None"""
    code = bodies(jar, owner).get((name, desc))
    if not code or len(code) != 3 or code[0] != 'aload_0' or not code[2].endswith('return'):
        return None
    m = re.match(r'getfield\s+#\d+\s+// Field (?:(\S+)\.)?(\w+):(\S+)$', code[1])
    if not m or m.group(3) != split_args(desc)[1]:
        return None
    return m.group(1) or owner, m.group(2)


# ---------------------------------------------------------------- 1.20.4 initializers

_javap = {}


def javap_lines(jar, owner):
    if owner not in _javap:
        out = subprocess.run(['javap', '-c', '-p', '-cp', jar, owner.replace('/', '.')],
                             capture_output=True, text=True, encoding='utf-8', errors='replace').stdout
        _javap[owner] = out.splitlines()
    return _javap[owner]


def returns_map_codec(jar, owner, name, desc):
    """whether 1.20.4's owner.name(desc) answers what one of the map-codec builders made (its last call)"""
    lines = javap_lines(jar, owner)
    inside, last = False, None
    for line in lines:
        if not line.startswith('    ') and line.strip().endswith(';') and f' {name}(' in line:
            inside = True
            last = None
            continue
        if inside:
            if 'invoke' in line:
                last = line
            if line.strip().endswith('areturn'):
                return last is not None and any(b in last for b in MAP_BUILDERS)
            if not line.strip():
                inside = False
    return False


def initializer_kind(jar, owner, field, V):
    """'map' when 1.20.4's <clinit> stored a map codec's codec in the field, else what it stored"""
    lines = javap_lines(jar, owner)
    try:
        start = lines.index('  static {};')
    except ValueError:
        return 'no static initializer'
    last = None
    for line in lines[start:]:
        if 'invoke' in line:
            last = line
        if 'putstatic' in line:
            if f'Field {field}:' in line:
                if last is None:
                    return 'none'
                if any(b in last for b in MAP_BUILDERS):
                    return 'map'
                # a static helper of 1.20.4 (Structure.simpleCodec): javac names the class the call is
                # made in when the method is inherited, and javap leaves out that class
                m = re.search(r'Method (?:(net/minecraft/[\w$/]+)\.)?(method_\d+):(\([^)]*\)Lcom/mojang/serialization/Codec;)', last)
                if m and 'invokestatic' in last:
                    for c in supers(V, m.group(1) or owner):
                        k = V.get(c)
                        if k and any(x.name == m.group(2) and x.desc == m.group(3) for x in k.methods):
                            if returns_map_codec(jar, c, m.group(2), m.group(3)):
                                return 'map'
                            break
                return last.split('//', 1)[-1].strip()
            last = None
    return 'not stored'


# ---------------------------------------------------------------- main

def main():
    damage, vjar, wjar, vserver, names, src, out_json, out_report, wcode, mdir = sys.argv[1:11]
    vnames, wnames = field_names(mdir, '1.20.4'), field_names(mdir, '1.20.5')
    cm = class_names(names)
    SIMPLE.update({c: m.rsplit('/', 1)[-1].replace('$', '') for c, m in cm.items()})
    V = load(vjar)
    W = load(wjar)
    with zipfile.ZipFile(vserver) as z:
        server = {n[:-6] for n in z.namelist() if n.endswith('.class')}

    def side(owner):
        return 'common' if owner.split('$', 1)[0] in server else 'client'

    def mine(c):
        return AREA.search(mojang(c, cm)) is not None

    rows = []
    for i, line in enumerate(open(damage, encoding='utf-8')):
        if i:
            r = line.rstrip('\n').split('\t')
            if mine(r[1]):
                rows.append(r)

    frag = collections.OrderedDict((k, {}) for k in (
        'fieldGetters', 'instanceFieldGetters', 'instanceFieldSetters', 'callReshapes', 'superCtorReshapes'))
    frag['overrideHooks'] = []
    frag['lambdaAdapters'] = {}
    static_getters = {}    # (owner, name, desc) -> (class, converter)
    inst_getters = {}      # (owner, name, desc, wdesc) -> (class, converter)
    inst_setters = {}
    legacy = {}            # iface -> (sam, old desc, converter, short, side)
    hooks = {}             # (old shape, onlyUnder) -> hook
    super_keys = {}        # callReshapes key -> entry, for hooks' super calls
    report = []
    covers = set()         # generated covers (the getter rule)
    frame_defaults = {}    # old shape -> {new argument index: static} (the appended rule)
    shapes_client = {}     # (static name, argument type) -> (family, Java expression)
    shapes_client_defaults = {}  # (static name, old desc) -> (family, argument type, argument index)
    rec_mixins = {}        # class -> {old shape: (name, desc, 1.20.5 field, Mojang field name)}

    for r in rows:
        usage, owner, name, desc, kinds = r[:5]
        fate, detail = 'design', ''
        if usage in ('get-static', 'get-static-protected') and kinds == 'FIELD':
            wf = [f for f in (W[owner].fields if owner in W else []) if f[0] == name]
            if not wf:
                detail = 'no 1.20.5 field of this name'
            else:
                _, wdesc, wacc, wsig = wf[0]
                sw = swap_of(desc, wdesc, wsig)
                init = initializer_kind(vjar, owner, name, V) if sw and sw[0] == 'codec' else 'map'
                if not sw:
                    detail = f'{desc} -> {wdesc} is no known change'
                elif not (wacc & ACC_STATIC) or not (wacc & ACC_PUBLIC):
                    detail = 'not public static in 1.20.5'
                elif init != 'map':
                    detail = 'codec field 1.20.4 built as ' + init
                else:
                    cls = FIELDS if side(owner) == 'common' else CLIENT_FIELDS
                    frag['fieldGetters'][f'{owner}.{name}:{desc}'] = cls
                    # in source the shared Holder converters take a Holder.Reference too
                    conv = to_old(sw, 'L' + HOLDER + ';' if sw[0] == 'holder' else wdesc)
                    static_getters[(owner, name, desc)] = (cls, conv)
                    fate, detail = 'getter', sw[0]
        elif usage in ('get', 'put') and kinds == 'FIELD':
            wf = [f for f in (W[owner].fields if owner in W else []) if f[0] == name]
            if not wf:
                detail = 'no 1.20.5 field of this name'
            else:
                _, wdesc, wacc, wsig = wf[0]
                sw = swap_of(desc, wdesc, wsig)
                cv = None if not sw else (to_old(sw, 'L' + HOLDER + ';' if sw[0] == 'holder' else wdesc)
                                          if usage == 'get' else to_new(sw, wdesc))
                if not sw:
                    detail = f'{desc} -> {wdesc} is no known change'
                elif (wacc & ACC_STATIC) or not (wacc & ACC_PUBLIC):
                    detail = 'not a public instance field in 1.20.5'
                elif cv is None:
                    detail = f'no converter to {wdesc}'
                else:
                    cls = FIELDS if side(owner) == 'common' else CLIENT_FIELDS
                    sec = 'instanceFieldGetters' if usage == 'get' else 'instanceFieldSetters'
                    frag[sec][f'{owner}.{name}:{desc}'] = cls
                    (inst_getters if usage == 'get' else inst_setters)[(owner, name, desc, wdesc)] = (cls, cv)
                    fate, detail = 'field-' + usage, sw[0]
        elif usage in ('call', 'override+super') and 'METHOD' in kinds and desc.startswith('()') \
                and not desc.endswith(')V') and not find_methods(W, owner, name):
            decl = [c for c, m in find_methods(V, owner, name) if m.desc == desc and not (m.access & ACC_STATIC)]
            g = getter_field(vjar, decl[0], name, desc) if decl else None
            vf = vnames.get(g[0], {}).get(g[1]) if g else None
            home = decl[0] if decl else owner
            wk = W.get(home)
            wf = [f for f in (wk.fields if wk else []) if not (f[2] & ACC_STATIC) and f[1] == split_args(desc)[1]
                  and wnames.get(home, {}).get(f[0]) == vf] if vf else []
            acc = [m for m in (wk.methods if wk and wf else []) if m.desc == desc and (m.access & ACC_PUBLIC)
                   and not (m.access & ACC_STATIC) and getter_field(wcode, home, m.name, desc) == (home, wf[0][0])]
            if len(acc) > 1:  # a record's accessor is named as its field (toString may read the same field)
                acc = [m for m in acc if m.name == wf[0][0]] or acc
            if not decl:
                detail = 'no instance method of this shape in 1.20.4'
            elif g is None:
                detail = '1.20.4 body is not a plain getter'
            elif g[0] != home:
                detail = "the getter reads another class's field"
            elif not wf:
                detail = f'1.20.5 {mojang(home, cm)} keeps no field {vf} of that type'
            elif len(acc) != 1:
                detail = f'{len(acc)} plain 1.20.5 getters of field {vf}'
            elif not mine(home):
                fate, detail = 'other-area', mojang(home, cm)
            else:
                covers.add(f'{home}.{name}{desc}')
                rec_mixins.setdefault(home, {})[name + desc] = (name, desc, wf[0][0], vf)
                hooks[(name + desc, home)] = {
                    'old': name + desc, 'new': acc[0].name + desc, 'onlyUnder': home,
                    'basis': f'generated: getter; 1.20.5 reads {vf} through {acc[0].name}(), a mod\'s 1.20.4 '
                             f'{name}() override answers it (its super call is the cover)'}
                fate, detail = 'getter-cover', f'{vf} -> {acc[0].name}()'
        elif usage in ('call', 'call-protected', 'call-inherited', 'override+super') and 'METHOD' in kinds \
                and appended_family(V, W, owner, name, desc):
            fam, decl, wdesc = appended_family(V, W, owner, name, desc)
            oa = split_args(desc)[0]
            vname, vi, expr = fam['value']
            if side(decl) != 'client':
                raise SystemExit(f'appended family on a common class {decl}: give it a common shapes class first')
            if not mine(decl):
                fate, detail = 'other-area', mojang(decl, cm)
            else:
                ops = [f'keep:{i}' for i in range(len(oa))]
                ops.append(f'conv:{vi} {SHAPES_CLIENT}#{vname}({oa[vi]}){fam["tail"][0]}')
                frag['callReshapes'][f'{decl}.{name}{desc}'] = {'desc': wdesc, 'ops': ops,
                                                                'basis': 'generated: appended; ' + fam['basis']}
                hooks[(name + desc, decl)] = {
                    'old': name + desc, 'new': name + wdesc, 'args': list(range(len(oa))), 'onlyUnder': decl,
                    'frames': PKG + '/Guard', 'superCalls': 'rewrite',
                    'basis': 'generated: appended; the old override gets its arguments, its super call the '
                             'frame\'s ' + vname + ' (' + fam['basis'] + ')'}
                frame_defaults[name + desc] = {str(len(oa)): f'{SHAPES_CLIENT}#{vname}(Ljava/lang/Object;'
                                                             f'{"".join(oa)}){fam["tail"][0]}'}
                shapes_client[(vname, oa[vi])] = (fam, expr)
                shapes_client_defaults[(vname, desc)] = (fam, oa[vi], vi)
                fate, detail = 'appended', vname
        elif usage.startswith('call') and kinds == 'METHOD':
            hits = {m.desc: (c, m, match(desc, m.desc, m.signature)) for c, m in find_methods(W, owner, name)
                    if m.desc != desc}
            hits = [h for h in hits.values() if h[2]]
            if len(hits) != 1:
                detail = f'{len(hits)} 1.20.5 shapes of this name differ by known changes'
            else:
                c, m, (amap, rsw) = hits[0]
                home = topmost(V, owner, name, desc) or owner
                entry = reshape_entry(amap, rsw, m.desc)
                if not mine(home):
                    fate, detail = 'other-area', mojang(home, cm)
                elif entry is None:
                    detail = 'an argument 1.20.5 takes as a Holder.Reference / key'
                else:
                    frag['callReshapes'][f'{home}.{name}{desc}'] = entry
                    fate, detail = 'reshape', entry['basis']
        elif usage in ('new', 'super-ctor') and kinds == 'METHOD':
            cands = [m for m in (W[owner].methods if owner in W else []) if m.name == '<init>']
            hits = [(m, match(desc, m.desc, m.signature)) for m in cands]
            hits = [h for h in hits if h[1] and h[1][1] is None]
            entry = reshape_entry(hits[0][1][0], None, hits[0][0].desc) if len(hits) == 1 else None
            if len(hits) != 1:
                detail = f'{len(hits)} 1.20.5 constructors differ by known changes'
            elif entry is None:
                detail = 'an argument 1.20.5 takes as a Holder.Reference / key'
            else:
                frag['superCtorReshapes'][f'{owner}.<init>{desc}'] = entry
                fate, detail = 'ctor-reshape', entry['basis']
        elif usage in ('override+super', 'implement', 'implement-inherited') and ('METHOD' in kinds or 'ORPHAN' in kinds):
            hits = {m.desc: (c, m, match(desc, m.desc, m.signature)) for c, m in find_methods(W, owner, name)
                    if m.desc != desc and not (m.access & ACC_STATIC)}
            hits = [h for h in hits.values() if h[2]]
            if len(hits) != 1:
                detail = f'{len(hits)} 1.20.5 shapes of this name differ by known changes'
            else:
                c, m, (amap, rsw) = hits[0]
                nargs, nret = split_args(m.desc)
                changed = sorted({(a[2], nargs[j]) for j, a in enumerate(amap) if a[0] == 'old' and a[2]})
                home = topmost(V, owner, name, desc) or owner
                unwrap = to_new(rsw, nret) if rsw is not None else None
                sup = reshape_entry(amap, rsw, m.desc)
                if not mine(home):
                    fate, detail = 'other-area', mojang(home, cm)
                elif len(changed) > 1:
                    detail = 'two different parameter changes (one wrap per hook)'
                elif rsw is not None and unwrap is None:
                    detail = '1.20.5 answers a Holder.Reference / key'
                elif sup is None:
                    detail = "the mod's super call cannot be converted (Holder.Reference / key parameter)"
                else:
                    h = {'old': name + desc, 'new': name + m.desc}
                    if len(amap) != len(split_args(desc)[0]):
                        h['args'] = [j for j, a in enumerate(amap) if a[0] == 'old']
                    if changed:
                        h['wrap'] = dotted(to_old(*changed[0]))
                    if unwrap is not None:
                        h['unwrap'] = dotted(unwrap)
                    h['onlyUnder'] = home
                    h['basis'] = 'generated: ' + kinds_of(amap, rsw) + "; the mod's super call is reshaped (callReshapes)"
                    hooks[(h['old'], home)] = h
                    super_keys[f'{home}.{name}{desc}'] = sup
                    fate, detail = 'hook', kinds_of(amap, rsw)
        elif usage == 'lambda':
            ms = [m for m in (W[owner].methods if owner in W else []) if m.name == name and (m.access & 0x400)]
            if len(ms) != 1:
                detail = f'{len(ms)} abstract 1.20.5 methods of this name'
            else:
                m = ms[0]
                mt = match(desc, m.desc, m.signature)
                conv = to_new(mt[1], split_args(m.desc)[1]) if mt and mt[1] else None
                if not mt or mt[1] is None or any(a[0] != 'old' or a[2] for a in mt[0]):
                    detail = 'the SAM change is not a change of the answer alone'
                elif conv is None:
                    detail = 'no converter to the 1.20.5 answer'
                else:
                    short = mojang(owner, cm).rsplit('/', 1)[-1].replace('$', '')
                    li = f'{LEGACY}$Cb{short}'
                    frag['lambdaAdapters'][f'{owner}.{name}{desc}'] = {
                        'iface': li, 'adapt': f'{LEGACY}#adapt{short}(L{li};)L{owner};'}
                    legacy[owner] = (name, desc, conv, short, side(owner))
                    hooks[(name + desc, owner)] = {
                        'old': name + desc, 'new': name + m.desc, 'unwrap': dotted(conv), 'onlyUnder': owner,
                        'basis': 'generated: ' + mt[1][0] + ' (a named class implementing the interface)'}
                    fate, detail = 'lambda', mt[1][0]
        report.append((usage, owner, mojang(owner, cm), name, desc, fate, detail))

    for k, e in super_keys.items():
        frag['callReshapes'].setdefault(k, e)
    frag['covers'] = sorted(covers)
    frag['frameDefaults'] = dict(sorted(frame_defaults.items()))
    frag['overrideHooks'] = sorted(hooks.values(), key=lambda h: (h['onlyUnder'], h['old']))
    frag = {k: v for k, v in frag.items() if v}

    # ---- Java: field accessors, per side
    for cls, client in ((FIELDS, False), (CLIENT_FIELDS, True)):
        sg = sorted((k, v) for k, v in static_getters.items() if v[0] == cls)
        ig = sorted((k, v) for k, v in inst_getters.items() if v[0] == cls)
        isx = sorted((k, v) for k, v in inst_setters.items() if v[0] == cls)
        path = os.path.join(src, cls + '.java')
        if not (sg or ig or isx):
            if os.path.exists(path):
                os.remove(path)
            continue
        simple = cls.rsplit('/', 1)[1]
        out = ['package ' + PKG.replace('/', '.') + ';', '']
        if client:
            out += ['import net.fabricmc.api.EnvType;', 'import net.fabricmc.api.Environment;', '']
        out += ['/**',
                ' * Generated by core/tools/gen_s4rest.py: the 1.20.4 value of every field 1.20.5 holds under another',
                ' * type (a holder of it, its key, its map codec, its loot table key). A 1.20.4 read of the field',
                ' * (fieldGetters / instanceFieldGetters) answers the value converted back, a 1.20.4 write',
                ' * (instanceFieldSetters) stores it converted.',
                ' */']
        if client:
            out.append('@Environment(EnvType.CLIENT)')
        out += [f'public final class {simple} {{', '']
        for (owner, name, desc), (_, conv) in sg:
            out += [f'    /** {mojang(owner, cm)}.{name} */',
                    f'    public static {jt(desc)} {name}() {{',
                    f'        return {conv[0].rsplit("/", 1)[1]}.{conv[1]}({jt("L" + owner + ";")}.{name});',
                    '    }', '']
        for (owner, name, desc, wdesc), (_, conv) in ig:
            out += [f'    /** {mojang(owner, cm)}.{name}, read */',
                    f'    public static {jt(desc)} {name}({jt("L" + owner + ";")} self) {{',
                    f'        return {conv[0].rsplit("/", 1)[1]}.{conv[1]}(self.{name});',
                    '    }', '']
        for (owner, name, desc, wdesc), (_, conv) in isx:
            out += [f'    /** {mojang(owner, cm)}.{name}, written */',
                    f'    public static void {name}({jt("L" + owner + ";")} self, {jt(desc)} value) {{',
                    f'        self.{name} = {conv[0].rsplit("/", 1)[1]}.{conv[1]}(value);',
                    '    }', '']
        out += [f'    private {simple}() {{', '    }', '}', '']
        open(path, 'w', encoding='utf-8', newline='\n').write('\n'.join(out))

    # ---- Java: converters CbHolders does not have
    out = ['package ' + PKG.replace('/', '.') + ';', '',
           '/**',
           ' * Generated by core/tools/gen_s4rest.py: converters for the 1.20.4 -> 1.20.5 changes of this area',
           ' * that the step\'s shared helpers (CbHolders, CbCodecs) do not provide: holders of Attribute,',
           ' * CatVariant and FrogVariant, registry keys, loot table keys. Each takes null to null.',
           ' */',
           'public final class CbRestHolders {', '']
    for key in sorted(CONVERTERS):
        out.extend(CONVERTERS[key])
    out += ['    private CbRestHolders() {', '    }', '}', '']
    open(os.path.join(src, HOLDERS + '.java'), 'w', encoding='utf-8', newline='\n').write('\n'.join(out))

    # ---- Java: legacy functional interfaces and their adapters
    if any(v[4] != 'common' for v in legacy.values()):
        raise SystemExit('a client-only functional interface: split CbRestLegacy by side first')
    out = ['package ' + PKG.replace('/', '.') + ';', '',
           '/**',
           ' * Generated by core/tools/gen_s4rest.py: the 1.20.4 shape of every functional interface whose method',
           ' * answers another type in 1.20.5 (a map codec where 1.20.4 answered a codec). A mod\'s lambda or',
           ' * method reference is built as the matching interface here (lambdaAdapters) and handed to vanilla',
           ' * through its adapter, whose method answers what the mod\'s function answers, converted.',
           ' */',
           'public final class CbRestLegacy {', '']
    for owner, (sam, odesc, conv, short, _) in sorted(legacy.items()):
        ret = split_args(odesc)[1]
        jo = jt('L' + owner + ';')
        out += [f'    /** {mojang(owner, cm)}.{sam}{odesc} as 1.20.4 declared it */',
                '    @FunctionalInterface',
                f'    public interface Cb{short} {{',
                f'        {jt(ret)} {sam}();',
                '    }', '',
                f'    /** the 1.20.5 {short} answering the converted answer of the 1.20.4 function */',
                '    @SuppressWarnings({"unchecked", "rawtypes"})',
                f'    public static {jo} adapt{short}(Cb{short} legacy) {{',
                f'        return legacy == null ? null : ({jo}) () -> {conv[0].rsplit("/", 1)[1]}.{conv[1]}(legacy.{sam}());',
                '    }', '']
    out += ['    private CbRestLegacy() {', '    }', '}', '']
    path = os.path.join(src, LEGACY + '.java')
    if legacy:
        open(path, 'w', encoding='utf-8', newline='\n').write('\n'.join(out))
    elif os.path.exists(path):
        os.remove(path)

    # ---- Java: the appended rule's values (client side), one overload per argument type
    path = os.path.join(src, SHAPES_CLIENT + '.java')
    if shapes_client:
        out = ['package ' + PKG.replace('/', '.') + ';', '', 'import net.fabricmc.api.EnvType;',
               'import net.fabricmc.api.Environment;', '',
               '/**',
               ' * Generated by core/tools/gen_s4rest.py (appended rule): the value of a parameter 1.20.5 appended',
               ' * to a client method mods call and override, as 1.20.5\'s own caller computes it, from the 1.20.4',
               ' * arguments -- for a 1.20.4 call (callReshapes), and as the frames default when a mod\'s 1.20.4',
               ' * override is entered directly (frameDefaults: the receiver and every old argument).',
               ' */', '@Environment(EnvType.CLIENT)', 'public final class CbRestShapesClient {', '']
        for (vname, at), (fam, expr) in sorted(shapes_client.items()):
            rt = jt(fam['tail'][0])
            out += [f'    /** {fam["basis"]} */', f'    public static {rt} {vname}({jt(at)} a) {{',
                    f'        return {expr};', '    }', '']
        for (vname, odesc), (fam, at, vi) in sorted(shapes_client_defaults.items()):
            oa = split_args(odesc)[0]
            params = ', '.join(['Object self'] + [f'{jt(t)} a{i}' for i, t in enumerate(oa)])
            out += [f'    /** the frames default of {vname} for the 1.20.4 shape {odesc} */',
                    f'    public static {jt(fam["tail"][0])} {vname}({params}) {{',
                    f'        return {vname}(a{vi});', '    }', '']
        out += ['    private CbRestShapesClient() {', '    }', '}', '']
        open(path, 'w', encoding='utf-8', newline='\n').write('\n'.join(out))
    elif os.path.exists(path):
        os.remove(path)

    # ---- Java: the getter rule's covers, one mixin per class, listed in the layer's mixin config
    mixin_dir = os.path.join(src, PKG.rsplit('/', 1)[0], 'mixin')
    for f in os.listdir(mixin_dir):
        if f.startswith('CbRestRec') and f.endswith('Mixin.java'):
            os.remove(os.path.join(mixin_dir, f))
    listed = {'common': [], 'client': []}
    for owner in sorted(rec_mixins):
        short = mojang(owner, cm).rsplit('/', 1)[-1].replace('$', '')
        cls = f'CbRestRec{short}Mixin'
        client = side(owner) == 'client'
        listed['client' if client else 'common'].append(cls)
        out = ['package ' + PKG.rsplit('/', 1)[0].replace('/', '.') + '.mixin;', '']
        if client:
            out += ['import net.fabricmc.api.EnvType;', 'import net.fabricmc.api.Environment;']
        out += ['import org.spongepowered.asm.mixin.Final;', 'import org.spongepowered.asm.mixin.Mixin;',
                'import org.spongepowered.asm.mixin.Shadow;', '',
                '/**',
                f' * Generated by core/tools/gen_s4rest.py (getter rule): 1.20.4 {mojang(owner, cm)} getters 1.20.5',
                ' * dropped, with 1.20.4\'s bodies -- each returns the field 1.20.5 still keeps under that Mojang name',
                ' * (read in 1.20.5 through the getter the override hooks connect a mod\'s override to).',
                ' */']
        if client:
            out.append('@Environment(EnvType.CLIENT)')
        out += [f'@Mixin({jt("L" + owner + ";")}.class)', f'public abstract class {cls} {{', '']
        fields = {}
        for name, desc, wfield, vf in rec_mixins[owner].values():
            fields[wfield] = split_args(desc)[1]
        for wfield, fdesc in sorted(fields.items()):
            out += ['    @Shadow', '    @Final', f'    private {jt(fdesc)} {wfield};', '']
        for key in sorted(rec_mixins[owner]):
            name, desc, wfield, vf = rec_mixins[owner][key]
            out += [f'    /** 1.20.4 {vf} getter */', f'    public {jt(split_args(desc)[1])} {name}() {{',
                    f'        return this.{wfield};', '    }', '']
        out += ['}', '']
        open(os.path.join(mixin_dir, cls + '.java'), 'w', encoding='utf-8', newline='\n').write('\n'.join(out))
    config = os.path.join(os.path.dirname(src.rstrip('/\\')), 'resources', 'cb-step-1.20.5.mixins.json')
    mc = json.load(open(config, encoding='utf-8'))
    for section, names_ in (('mixins', listed['common']), ('client', listed['client'])):
        mc[section] = [m for m in mc.get(section, []) if not m.startswith('CbRestRec')] + names_
    with open(config, 'w', encoding='utf-8', newline='\n') as o:
        o.write(json.dumps(mc, indent=2) + '\n')

    with open(out_json, 'w', encoding='utf-8', newline='\n') as o:
        json.dump(frag, o, indent=1)
        o.write('\n')
    with open(out_report, 'w', encoding='utf-8', newline='\n') as o:
        o.write('usage\towner\tmojang\tname\tdesc\tfate\tdetail\n')
        for rr in report:
            o.write('\t'.join(rr) + '\n')
    c = collections.Counter(rr[5] for rr in report)
    print(f'{len(rows)} area rows: {dict(c)}')
    print('  ->', out_json, {k: len(v) for k, v in frag.items()})


if __name__ == '__main__':
    main()
