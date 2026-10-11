"""ClientWorld.addEntity(int id, Entity) (1.20.1 putNonPlayerEntity / addEntity(int, Entity))
for every tree: gone in 1.20.2, which added addEntity(Entity). 1.20.1 removed whatever
entity held that id (DISCARDED) and then added the new one -- the same two calls now.
Idempotent; run from the repo root."""
import json
import re

TREES = {'1.20.2': 'v1_20_2', '1.20.4': 'v1_20_4', '1.21.1': 'v1_21_1', '1.21.4': 'v1_21_4',
         '1.21.8': 'v1_21_8', '1.21.11': 'v1_21_11', '26.2': 'v26_2'}
KEY = 'net/minecraft/class_638.method_2942(ILnet/minecraft/class_1297;)V'
INTER = '''    /** was net/minecraft/class_638.method_2942(ILnet/minecraft/class_1297;)V -- ClientWorld.addEntity(id, entity):
     *  1.20.1 discarded whatever entity held that id, then added this one */
    public static void method_2942(net.minecraft.class_638 self, int id, net.minecraft.class_1297 e) {
        self.method_2945(id, net.minecraft.class_1297.class_5529.field_26999);
        self.method_53875(e);
    }
'''
MOJ = '''    /** was net/minecraft/class_638.method_2942(ILnet/minecraft/class_1297;)V -- ClientWorld.addEntity(id, entity):
     *  1.20.1 discarded whatever entity held that id, then added this one */
    public static void method_2942(net.minecraft.client.multiplayer.ClientLevel self, int id, net.minecraft.world.entity.Entity e) {
        self.removeEntity(id, net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);
        self.addEntity(e);
    }
'''
for v, pkg in TREES.items():
    p = f'core/shims/{v}/src/top/dext/centurybridge/rt/{pkg}/ClientStatics.java'
    s = open(p, encoding='utf-8').read()
    body = MOJ if v == '26.2' else INTER
    # replace an existing bridge (1.20.2/1.20.4 dropped the remove) or add one before the closing brace
    s2 = re.sub(r'    /\*\* was net/minecraft/class_638\.method_2942[^\n]*\*/\n    public static void method_2942\([^\n]*\n(?:        [^\n]*\n)*?    \}\n|'
                r'    /\*\* was net/minecraft/class_638\.method_2942[^\n]*\*/\n    public static void method_2942\([^\n]*\}\n',
                lambda m: body, s, count=1)
    if s2 == s:
        i = s.rstrip().rfind('}')
        s2 = s[:i] + '\n' + body + s[i:]
    open(p, 'w', encoding='utf-8', newline='\n').write(s2)
    sp = f'core/segments/shims-{v}.json'
    d = json.load(open(sp, encoding='utf-8'))
    d.setdefault('instanceRedirects', {})[KEY] = f'top/dext/centurybridge/rt/{pkg}/ClientStatics'
    for k in [k for k in d.get('ledger', {}) if k.startswith('net/minecraft/class_638.method_2942')]:
        del d['ledger'][k]
    open(sp, 'w', encoding='utf-8', newline='\n').write(json.dumps(d, indent=1, ensure_ascii=False) + '\n')
    print(v, 'ok')
