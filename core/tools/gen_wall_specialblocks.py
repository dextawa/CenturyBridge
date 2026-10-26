"""gen_wall_specialblocks.py: 1.21.11's SpecialModelRenderers block table (class_10517.field_55454, the special model
1.21.11's SpecialBlockModelRenderer drew for a block) as 26.1 layer code: rt/CbWallSpecialBlockMap.java, one
put(block, special model) per entry, in 1.21.11's order, each special model built the way the wall wires a 1.21.11
mod's construction of it (the same rt statics: banner GROUND, sign attachments, chest SINGLE, whole bed).

Input: the javap -c -p text of the 1.21.11 intermediary code jar's net.minecraft.class_10517 and
data/mappings/inter-to-mojang-1.21.11.tsv. Run from the repo root:
  javap -c -p -cp data/jars/client-1.21.11-intermediary-code.jar net.minecraft.class_10517 > /tmp/smr.txt
  python3 core/tools/gen_wall_specialblocks.py /tmp/smr.txt data/mappings/inter-to-mojang-1.21.11.tsv
Every constructor and argument shape of the table must be known below; anything else stops the generator."""
import re
import sys

OUT = 'core/shims/steps/26.1/src/top/dext/centurybridge/step/v26_1/rt/CbWallSpecialBlockMap.java'

javap, mapping = sys.argv[1], sys.argv[2]
fields = {}
for line in open(mapping, encoding='utf-8'):
    p = line.rstrip('\n').split('\t')
    if p[0] == 'F':
        fields[p[1]] = p[2].rsplit('.', 1)[1]

# constant owners the table reads: intermediary class -> 26.1 Java expression prefix
OWNERS = {
    'net/minecraft/class_2246': 'Blocks',
    'net/minecraft/class_2484$class_2486': 'SkullBlock.Types',
    'net/minecraft/class_1767': 'DyeColor',
    'net/minecraft/class_4719': 'WoodType',
    'net/minecraft/class_5955$class_5811': 'WeatheringCopper.WeatherState',
    'net/minecraft/class_11583$class_11584': 'CopperGolemStatueBlock.Pose',
}
# 1.21.11's chest texture constants: 26.1 keeps them as multiblock resources (rt/CbWallSpriteIds gives back
# the single texture each 1.21.11 constant named, as the wall wires a mod's read); ENDER_CHEST kept its own
CHEST = {
    'field_55429': 'CbWallSpriteIds.giftChest()',
    'field_55430': 'CbWallSpriteIds.normalChest()',
    'field_55431': 'CbWallSpriteIds.trappedChest()',
    'field_55432': 'ChestSpecialRenderer.ENDER_CHEST',
    'field_61848': 'CbWallSpriteIds.copperChest()',
    'field_61849': 'CbWallSpriteIds.exposedCopperChest()',
    'field_61850': 'CbWallSpriteIds.weatheredCopperChest()',
    'field_61851': 'CbWallSpriteIds.oxidizedCopperChest()',
}
# 1.21.11 constructor -> 26.1 construction (the wall's ctorRedirects target for the same constructor, or 26.1's own
# constructor where its descriptor did not change)
CTORS = {
    ('net/minecraft/class_10513$class_10514', '(Lnet/minecraft/class_2484$class_2485;)V'): 'new SkullSpecialRenderer.Unbaked({0})',
    ('net/minecraft/class_11533$class_11535', '()V'): 'new PlayerHeadSpecialRenderer.Unbaked()',
    ('net/minecraft/class_10498$class_10499', '(Lnet/minecraft/class_1767;)V'): 'CbWallItemModels.banner({0})',
    ('net/minecraft/class_10500$class_10501', '(Lnet/minecraft/class_1767;)V'): 'CbWallBed.unbaked({0})',
    ('net/minecraft/class_10511$class_10512', '()V'): 'new ShulkerBoxSpecialRenderer.Unbaked()',
    ('net/minecraft/class_10511$class_10512', '(Lnet/minecraft/class_1767;)V'): 'new ShulkerBoxSpecialRenderer.Unbaked({0})',
    ('net/minecraft/class_10535$class_10536', '(Lnet/minecraft/class_4719;)V'): 'CbWallItemModels.standingSign({0})',
    ('net/minecraft/class_10533$class_10534', '(Lnet/minecraft/class_4719;)V'): 'CbWallItemModels.hangingSign({0})',
    ('net/minecraft/class_10504$class_10505', '()V'): 'new ConduitSpecialRenderer.Unbaked()',
    ('net/minecraft/class_10502$class_10503', '(Lnet/minecraft/class_2960;)V'): 'CbWallItemModels.chest({0}, 0.0F)',
    ('net/minecraft/class_11693$class_11694', '(Lnet/minecraft/class_5955$class_5811;Lnet/minecraft/class_11583$class_11584;)V'):
        'new CopperGolemStatueSpecialRenderer.Unbaked({0}, {1})',
    ('net/minecraft/class_10506$class_10507', '()V'): 'new DecoratedPotSpecialRenderer.Unbaked()',
}


def const(arg):
    m = re.match(r'Field (\S+)\.(field_\d+):', arg)
    if not m:
        sys.exit('unknown argument shape: ' + arg)
    owner, name = m.groups()
    if owner == 'net/minecraft/class_10502':
        return CHEST[name]
    if owner not in OWNERS:
        sys.exit('unknown constant owner: ' + arg)
    return OWNERS[owner] + '.' + fields[owner + '.' + name]


lines = open(javap, encoding='utf-8').read().splitlines()
start = next(i for i, l in enumerate(lines) if l.strip() == 'static {};')
ins = []
for l in lines[start:]:
    m = re.match(r'\s*\d+: (\w+)\s*(.*)', l)
    if m:
        op, rest = m.groups()
        c = re.search(r'// (.*)', rest)
        ins.append((op, c.group(1) if c else rest.strip()))

entries, cur = [], None
for op, arg in ins:
    if op == 'putstatic' and 'field_55454' in arg:
        break
    if op == 'getstatic' and 'class_2246.' in arg and cur is None:
        cur = {'block': const(arg), 'args': []}
    elif cur is not None:
        if op == 'new':
            cur['new'] = arg.split()[-1]
        elif op == 'invokespecial' and arg.startswith('Method ' + cur.get('new', '?') + '."<init>"'):
            cur['ctor'] = arg.split(':', 1)[1]
        elif op == 'invokevirtual' and 'ImmutableMap$Builder.put' in arg:
            key = (cur['new'], cur['ctor'])
            if key not in CTORS:
                sys.exit('unknown constructor: %s%s' % key)
            entries.append((cur['block'], CTORS[key].format(*[const(a) for a in cur['args']])))
            cur = None
        elif op != 'dup':
            cur['args'].append(arg if op == 'getstatic' else op + ' ' + arg)

body = '\n'.join(f'        put.accept({b}, {u});' for b, u in entries)
src = f'''package top.dext.centurybridge.step.v26_1.rt;

import java.util.function.BiConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.special.ChestSpecialRenderer;
import net.minecraft.client.renderer.special.ConduitSpecialRenderer;
import net.minecraft.client.renderer.special.CopperGolemStatueSpecialRenderer;
import net.minecraft.client.renderer.special.DecoratedPotSpecialRenderer;
import net.minecraft.client.renderer.special.PlayerHeadSpecialRenderer;
import net.minecraft.client.renderer.special.ShulkerBoxSpecialRenderer;
import net.minecraft.client.renderer.special.SkullSpecialRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CopperGolemStatueBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.properties.WoodType;

/**
 * Generated by core/tools/gen_wall_specialblocks.py from javap of 1.21.11's SpecialModelRenderers (class_10517
 * static initializer, field_55454): the special model 1.21.11 drew for each block, {len(entries)} entries in 1.21.11's
 * order, each built as the wall builds a 1.21.11 mod's construction of it. Do not edit; regenerate.
 */
@Environment(EnvType.CLIENT)
final class CbWallSpecialBlockMap {{

    private CbWallSpecialBlockMap() {{
    }}

    static void put(BiConsumer<Block, SpecialModelRenderer.Unbaked<?>> put) {{
{body}
    }}
}}
'''
open(OUT, 'w', encoding='utf-8', newline='\n').write(src)
print(OUT, len(entries), 'entries')
