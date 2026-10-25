"""mixinTargets of step 1.20.1 -> 1.20.2 from `Main gen-mixin-targets` (V/W bytecode: a V method W no longer declares
with V's descriptor, the one W method of that name, arguments one for one by type), minus EXCLUDE, plus JUDGED
(hand-judged from both bodies).

  On beta2 (Java there only), in a checkout with the engine compiled:
    zcat core/segments/damage-mixin/1.20.1__1.20.2.tsv.gz > dm.tsv
    java -cp core/build/classes:<asm+gson> top.dext.centurybridge.Main gen-mixin-targets \
        data/jars/client-1.20.1-intermediary-code.jar data/jars/client-1.20.2-intermediary-code.jar \
        core/segments/shims-1.20.2.json dm.tsv mt-gen.json
  Here, from the repository root:
    python3 core/tools/gen_smx1_mixintargets.py mt-gen.json core/segments/shims-1.20.2.json.d/smx1-mixintargets-gen.json

A changed return type is left to MixinBridge's plan (it refuses a handler that reads or sets the return value and a
CallbackInfoReturnable handler on a target that now returns nothing). Regenerate, never hand-edit.
"""
import json
import sys

# V method -> why gen-mixin-targets' same-name pick cannot carry V's handlers (javap of both bodies)
EXCLUDE = {
    "net/minecraft/class_161$class_162.method_694(Ljava/util/function/Consumer;Ljava/lang/String;)Lnet/minecraft/class_161;":
        "Advancement.Builder.save: 1.20.2's Consumer takes AdvancementHolder where 1.20.1's took Advancement (same "
        "erased descriptor), so a 1.20.1 handler capturing the consumer and feeding it an Advancement would fail its "
        "cast inside vanilla's consumer",
}


LOOT_CTOR = ("1.20.2 made the loot / predicate classes codec records: the one constructor holds 1.20.1's body "
             "(same field stores; arrays became Lists, Item / Block / BlockEntityType / Potion / Enchantment became Holders)")

# V method -> (W method of the same class, basis): judged by hand from both bodies (javap -c -p) where
# gen-mixin-targets' same-name rule does not apply (MixinCause target-private-similar). The argument map is
# computed below: V argument i -> the one W argument of exactly its type, else -1 (only handlers taking none of
# the target's arguments follow then).
JUDGED = {
    "net/minecraft/class_638.method_18114(ILnet/minecraft/class_1297;)V":
        ("method_53875(Lnet/minecraft/class_1297;)V",
         "ClientLevel.addEntity(int, Entity) -> addEntity(Entity): same removeEntity(id, DISCARDED) and entityStorage add; "
         "the id is now entity.getId(), the value 1.20.1's callers passed"),
    "net/minecraft/class_2580.method_10934(I)Lnet/minecraft/class_1291;":
        ("method_53251(Lnet/minecraft/class_1291;)Lnet/minecraft/class_1291;",
         "BeaconBlockEntity.getValidEffectById(int) -> filterEffect(MobEffect): same VALID_EFFECTS.contains test answering "
         "the effect or null; 1.20.2 resolves the id before the call"),
    "net/minecraft/class_4398.method_21293()V":
        ("method_53798()V",
         "RealmsLongRunningMcoTaskScreen.cancelOrBackButtonClicked -> cancel(): same task abort and setScreen(lastScreen)"),
    "net/minecraft/class_445.method_2258(Lnet/minecraft/class_332;)V":
        ("method_25420(Lnet/minecraft/class_332;IIF)V",
         "WinScreen.renderBg(GuiGraphics) -> renderBackground override: same body (similarity 1.00)"),
    "net/minecraft/class_8221.method_49762(Lcom/mojang/serialization/Dynamic;)Lcom/mojang/serialization/Dynamic;":
        ("method_52474(Lcom/mojang/serialization/Dynamic;)Lcom/mojang/serialization/Dynamic;",
         "BlockEntitySignDoubleSidedEditableTextFix.getEmptyTextList: same body under a new id (similarity 0.91, next 0.45)"),
    "net/minecraft/class_8490.<init>(Lcom/google/gson/Gson;Ljava/util/function/BiFunction;Ljava/lang/String;Lnet/minecraft/class_8490$class_8491;)V":
        ("<init>(Lcom/mojang/serialization/Codec;Ljava/lang/String;Lnet/minecraft/class_8490$class_8491;)V",
         "LootDataType's one constructor: same directory / validator stores; the Gson parser became a Codec"),
    "net/minecraft/class_2463$class_4511.<init>(Ljava/lang/String;[BLjava/lang/String;Lcom/google/common/hash/HashCode;)V":
        ("<init>(Ljava/lang/String;[BLcom/google/common/hash/HashCode;)V",
         "SnbtToNbt.TaskResult's one constructor; 1.20.2 dropped the snbt payload string"),
    "net/minecraft/class_540$class_541.<init>(Lnet/minecraft/class_268;Ljava/util/List;Lnet/minecraft/class_2960;)V":
        ("<init>(Lnet/minecraft/class_268;Ljava/util/List;Ljava/util/function/Supplier;)V",
         "TeleportToTeamMenuCategory.TeamSelectionItem's one constructor (similarity 1.00); the skin location became a supplier"),
    "net/minecraft/class_7941$class_7942.<init>(Lnet/minecraft/class_7845;Lnet/minecraft/class_2561;)V":
        ("<init>(Lnet/minecraft/class_8133;Lnet/minecraft/class_2561;)V",
         "TelemetryEventWidget.Content's record constructor (similarity 1.00); the layout type changed"),
    "net/minecraft/class_5739$class_5740.<init>(JLnet/minecraft/class_5712;Lnet/minecraft/class_243;)V":
        ("<init>(JLnet/minecraft/class_5321;Lnet/minecraft/class_243;)V",
         "GameEventListenerRenderer.TrackedGameEvent's record constructor (similarity 1.00); the event is a ResourceKey now"),
}
for _k, _to in {
    "net/minecraft/class_55.<init>([Lnet/minecraft/class_79;[Lnet/minecraft/class_5341;[Lnet/minecraft/class_117;Lnet/minecraft/class_5658;Lnet/minecraft/class_5658;)V":
        "<init>(Ljava/util/List;Ljava/util/List;Ljava/util/List;Lnet/minecraft/class_5658;Lnet/minecraft/class_5658;)V",
    "net/minecraft/class_52.<init>(Lnet/minecraft/class_176;Lnet/minecraft/class_2960;[Lnet/minecraft/class_55;[Lnet/minecraft/class_117;)V":
        "<init>(Lnet/minecraft/class_176;Ljava/util/Optional;Ljava/util/List;Ljava/util/List;)V",
    "net/minecraft/class_134.<init>([Lnet/minecraft/class_5341;Lnet/minecraft/class_2591;Ljava/util/List;)V":
        "<init>(Ljava/util/List;Lnet/minecraft/class_6880;Ljava/util/List;)V",
    "net/minecraft/class_109.<init>([Lnet/minecraft/class_5341;Ljava/util/Collection;)V": "<init>(Ljava/util/List;Ljava/util/Optional;)V",
    "net/minecraft/class_5258.<init>([Lnet/minecraft/class_5341;)V": "<init>(Ljava/util/List;)V",
    "net/minecraft/class_137.<init>([Lnet/minecraft/class_5341;Ljava/util/List;)V": "<init>(Ljava/util/List;Ljava/util/List;)V",
    "net/minecraft/class_3837.<init>([Lnet/minecraft/class_5341;Lnet/minecraft/class_5651;Ljava/util/List;)V":
        "<init>(Ljava/util/List;Lnet/minecraft/class_5651;Ljava/util/List;)V",
    "net/minecraft/class_5642.<init>([Lnet/minecraft/class_5341;Ljava/util/Map;Z)V": "<init>(Ljava/util/List;Ljava/util/Map;Z)V",
    "net/minecraft/class_101.<init>([Lnet/minecraft/class_5341;Lnet/minecraft/class_101$class_102;)V": "<init>(Ljava/util/List;Lnet/minecraft/class_101$class_102;)V",
    "net/minecraft/class_106.<init>([Lnet/minecraft/class_5341;Lnet/minecraft/class_5658;Z)V": "<init>(Ljava/util/List;Lnet/minecraft/class_5658;Z)V",
    "net/minecraft/class_111.<init>([Lnet/minecraft/class_5341;Lnet/minecraft/class_6862;Lnet/minecraft/class_20$class_21;BIZ)V":
        "<init>(Ljava/util/List;Lnet/minecraft/class_6862;Lnet/minecraft/class_20$class_21;BIZ)V",
    "net/minecraft/class_114.<init>([Lnet/minecraft/class_5341;Lnet/minecraft/class_42;)V": "<init>(Ljava/util/List;Lnet/minecraft/class_42;)V",
    "net/minecraft/class_125.<init>([Lnet/minecraft/class_5341;Lnet/minecraft/class_5658;I)V": "<init>(Ljava/util/List;Lnet/minecraft/class_5658;I)V",
    "net/minecraft/class_141.<init>([Lnet/minecraft/class_5341;Lnet/minecraft/class_5658;Z)V": "<init>(Ljava/util/List;Lnet/minecraft/class_5658;Z)V",
    "net/minecraft/class_144.<init>([Lnet/minecraft/class_5341;Lnet/minecraft/class_2960;JLnet/minecraft/class_2591;)V":
        "<init>(Ljava/util/List;Lnet/minecraft/class_2960;JLnet/minecraft/class_6880;)V",
    "net/minecraft/class_149.<init>([Lnet/minecraft/class_5341;Lnet/minecraft/class_5658;Z)V": "<init>(Ljava/util/List;Lnet/minecraft/class_5658;Z)V",
    "net/minecraft/class_159.<init>([Lnet/minecraft/class_5341;Lnet/minecraft/class_2487;)V": "<init>(Ljava/util/List;Lnet/minecraft/class_2487;)V",
    "net/minecraft/class_3670.<init>([Lnet/minecraft/class_5341;Lnet/minecraft/class_2561;Lnet/minecraft/class_47$class_50;)V":
        "<init>(Ljava/util/List;Ljava/util/Optional;Ljava/util/Optional;)V",
    "net/minecraft/class_4488.<init>([Lnet/minecraft/class_5341;Lnet/minecraft/class_2248;Ljava/util/Set;)V":
        "<init>(Ljava/util/List;Lnet/minecraft/class_6880;Ljava/util/Set;)V",
    "net/minecraft/class_5592.<init>([Lnet/minecraft/class_5341;Ljava/util/List;Z)V": "<init>(Ljava/util/List;Ljava/util/List;Z)V",
    "net/minecraft/class_6662.<init>([Lnet/minecraft/class_5341;Lnet/minecraft/class_1842;)V": "<init>(Ljava/util/List;Lnet/minecraft/class_6880;)V",
    "net/minecraft/class_67.<init>(Lnet/minecraft/class_2960;II[Lnet/minecraft/class_5341;[Lnet/minecraft/class_117;)V":
        "<init>(Lnet/minecraft/class_2960;IILjava/util/List;Ljava/util/List;)V",
    "net/minecraft/class_7431.<init>([Lnet/minecraft/class_5341;Lnet/minecraft/class_6862;)V": "<init>(Ljava/util/List;Lnet/minecraft/class_6862;)V",
    "net/minecraft/class_77.<init>(Lnet/minecraft/class_1792;II[Lnet/minecraft/class_5341;[Lnet/minecraft/class_117;)V":
        "<init>(Lnet/minecraft/class_6880;IILjava/util/List;Ljava/util/List;)V",
    "net/minecraft/class_83.<init>(Lnet/minecraft/class_2960;II[Lnet/minecraft/class_5341;[Lnet/minecraft/class_117;)V":
        "<init>(Lnet/minecraft/class_2960;IILjava/util/List;Ljava/util/List;)V",
    "net/minecraft/class_8492.<init>([Lnet/minecraft/class_5341;Lnet/minecraft/class_2960;)V": "<init>(Ljava/util/List;Lnet/minecraft/class_2960;)V",
    "net/minecraft/class_8548.<init>([Lnet/minecraft/class_5341;)V": "<init>(Ljava/util/List;)V",
    "net/minecraft/class_8551.<init>([Lnet/minecraft/class_5341;)V": "<init>(Ljava/util/List;)V",
    "net/minecraft/class_91.<init>(Lnet/minecraft/class_6862;ZII[Lnet/minecraft/class_5341;[Lnet/minecraft/class_117;)V":
        "<init>(Lnet/minecraft/class_6862;ZIILjava/util/List;Ljava/util/List;)V",
    "net/minecraft/class_94.<init>([Lnet/minecraft/class_5341;Lnet/minecraft/class_1887;Lnet/minecraft/class_94$class_96;)V":
        "<init>(Ljava/util/List;Lnet/minecraft/class_6880;Lnet/minecraft/class_94$class_96;)V",
}.items():
    JUDGED[_k] = (_to, LOOT_CTOR)


def arg_types(desc):
    out, i = [], 1
    while desc[i] != ")":
        j = i
        while desc[j] == "[":
            j += 1
        j = desc.index(";", j) + 1 if desc[j] == "L" else j + 1
        out.append(desc[i:j])
        i = j
    return out


def judged():
    out = {}
    for k, (to, basis) in JUDGED.items():
        va = arg_types(k[k.index("("):])
        wa = arg_types(to[to.index("("):])
        # the k-th V argument of a type is the k-th W argument of it when both have equally many (order kept:
        # LootPool's rolls / bonusRolls); any other count gives -1
        args = []
        for i, t in enumerate(va):
            hits = [j for j, w in enumerate(wa) if w == t]
            args.append(hits[va[:i].count(t)] if len(hits) == va.count(t) else -1)
        out[k] = {"to": to, "args": args, "basis": basis}
    return out


def main():
    src, out = sys.argv[1], sys.argv[2]
    mt = json.load(open(src, encoding="utf-8"))["mixinTargets"]
    keep = {k: v for k, v in sorted(mt.items()) if k not in EXCLUDE}
    for k, v in judged().items():
        if k in keep:
            raise SystemExit("judged entry also emitted by gen-mixin-targets: " + k)
        keep[k] = v
    keep = dict(sorted(keep.items()))
    root = {"_smx1_mixinTargets_basis": "gen_smx1_mixintargets.py over Main gen-mixin-targets (V/W bytecode); regenerate, "
                                        "never hand-edit", "mixinTargets": keep}
    with open(out, "w", encoding="utf-8", newline="\n") as o:
        json.dump(root, o, indent=1)
        o.write("\n")
    print(len(keep), "entries (", len(mt), "from gen-mixin-targets,", len(JUDGED), "judged ) ->", out)


if __name__ == "__main__":
    main()
