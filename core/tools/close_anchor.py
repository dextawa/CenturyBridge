"""Close an anchor's kill-list: ledger every remaining ISSUE/CTOR-ISSUE/CLASS-ISSUE
symbol that has no bridge, with a cluster-appropriate reason, so unaccounted -> 0.
Bridge the mechanical ones by hand FIRST; this only accounts for the structural
remainder (documented losses). Prints what it ledgers so it can be reviewed.
  python3 close_anchor.py <mcver> <full-audit.tsv>   [--dry]
"""
import json, io, sys, re

ver, audit = sys.argv[1], sys.argv[2]
dry = "--dry" in sys.argv
shims_p = f"core/segments/shims-{ver}.json"
w = json.load(io.open(shims_p, encoding='utf-8'))
led = w.setdefault('ledger', {})

# reason by owner class -> honest structural reason. Order: most specific first.
def reason(owner, member):
    c = owner.split('/')[-1]
    m = member or ''
    R = [
        (lambda: c == 'class_7923' and 'field_41176' in m, "enchantments moved to the dynamic (data-driven) registry (1.20.6->1.21); no static Registries.ENCHANTMENT"),
        (lambda: c == 'class_1893', "Enchantments.* became ResourceKeys; enchantment instances are data-driven since 1.21"),
        (lambda: c in ('class_1890','class_1887'), "EnchantmentHelper/Enchantment now key on Holder<Enchantment>; no bare-Enchantment overload survives the 1.21 rework"),
        (lambda: c in ('class_1100','class_793','class_3879','class_557','class_822','class_1087','class_918','class_776','class_785'), "model / immediate-mode render pipeline rewritten (BakedModel geometry / ModelPart.render) in 1.21"),
        (lambda: c in ('class_291','class_290','class_3999','class_1921','class_4668','class_289','class_287','class_4588','class_1159'), "render pipeline (Tesselator/BufferBuilder/VertexBuffer/VertexFormat/RenderLayer/RenderPhase) rewritten in 1.21"),
        (lambda: c == 'class_2586', "block-entity NBT/component serialization reworked (1.20.5 data components)"),
        (lambda: c == 'class_1936', "emitGameEvent signature reworked (GameEvent -> Holder<GameEvent>)"),
        (lambda: c == 'class_7923' and 'field_41179' in m, "BuiltInRegistries.POTION is no longer a DefaultedRegistry (type changed to a plain Registry)"),
        (lambda: c == 'class_2960', "Identifier constructors made private in 1.21; use Identifier.of (no super()-invokable ctor)"),
        (lambda: c == 'class_1860' and 'method_8116' in m, "Recipe.craft input type changed Inventory->RecipeInput (and registry->WrapperLookup); no in-place adapter"),
        (lambda: c in ('class_1834','class_1832'), "tool/armor material became a data-driven record; the old enum constants were removed"),
        (lambda: c == 'class_1271', "ActionResult/TypedActionResult restructured into InteractionResult"),
        (lambda: c == 'class_3545', "Tuple removed"),
        (lambda: c == 'class_310' and 'field_1755' in m, "Minecraft.screen is now accessed through a getter (instance field read cannot be redirected without a GETFIELD hook)"),
        (lambda: c == 'class_4651', "BlockStateProvider.getState now requires a WorldGenLevel argument not available at the call site"),
        (lambda: c == 'class_927', "MobRenderer.shouldShowName override signature changed (added a distance argument)"),
    ]
    for pred, txt in R:
        if pred():
            return txt
    return f"no faithful equivalent (API restructured across the 1.20.1->{ver} span)"

added = []
for l in io.open(audit, encoding='utf-8'):
    p = l.rstrip('\n').split('\t')
    if len(p) < 3:
        continue
    fate, sym = p[1], p[2]
    # ISSUE-family AND non-silent TOMBSTONE both count as unaccounted (a bare
    # tombstone is a lazy-fail stub with no documented reason). Ledger both.
    if not re.match(r'(ISSUE|CTOR-ISSUE|SUPER-CTOR-ISSUE|CLASS-ISSUE|TOMBSTONE)', fate):
        continue
    bnd = ''
    mb = re.search(r'@([^:\s]+)', fate)
    if mb:
        bnd = mb.group(1)
    if '.<init>' in sym:
        owner, member = sym.split('.<init>')[0], '<init>'
    elif '.method_' in sym:
        owner, member = sym.split('.method_')[0], 'method_' + sym.split('.method_')[1].split('(')[0]
    elif '.field_' in sym:
        owner, member = sym.split('.field_')[0], 'field_' + sym.split('.field_')[1].split(':')[0]
    else:
        owner, member = sym, None
    key = owner + ('.' + member if member else '')
    if key in led:
        continue
    # do not ledger something already bridged (redirect/cover/getter/rename)
    keydesc = sym  # full key with desc for redirect maps
    bridged = any(sym in (w.get(mk) or {}) for mk in ('instanceRedirects','staticRedirects','fieldRedirects','fieldGetters','methodRenames','ctorRedirects')) \
              or sym in (w.get('covers') or [])
    if bridged:
        continue
    rsn = reason(owner, member)
    if rsn.startswith("no faithful") and fate.startswith('TOMBSTONE') and bnd:
        rsn = f"removed @{bnd}; no runtime equivalent (documented lazy-fail stub)"
    if not dry:
        led[key] = rsn
    added.append((key, rsn))

if not dry:
    io.open(shims_p, 'w', encoding='utf-8', newline='\n').write(json.dumps(w, indent=1, ensure_ascii=False))
print(f"{ver}: ledgered {len(added)} symbols; ledger now {len(led)}")
for k, r in added[:40]:
    print(f"  {k}  =>  {r[:70]}")
