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


RECIPE_OUTPUT = ("RecipeProvider's private datagen helper: the same builders saved to the output; 1.20.2's RecipeOutput "
                 "where 1.20.1 took a Consumer<FinishedRecipe>")
LOOT_REGISTER = ("the loot registry's type register: the same Registry.register of new <Type>(..) under the name; the "
                 "type wraps a Codec where 1.20.1 wrapped a JSON Serializer")
RECIPE_HOLDER = ("the same body reading the recipe through RecipeHolder.value() (1.20.2 hands the holder where 1.20.1 "
                 "handed the recipe)")

LOOT_CTOR = ("1.20.2 made the loot / predicate classes codec records: the one constructor holds 1.20.1's body "
             "(same field stores; arrays became Lists, Item / Block / BlockEntityType / Potion / Enchantment became Holders)")

# V method -> (W method of the same class, basis): judged by hand from both bodies (javap -c -p) where
# gen-mixin-targets' same-name rule does not apply (MixinCause target-private-similar). The argument map is
# computed below: V argument i -> the one W argument of exactly its type, else -1 (only handlers taking none of
# the target's arguments follow then). A third element gives the map read off the call sites instead, where the
# rule cannot tell equal types apart or must not pair them (a List whose element type changed is -1); each entry
# is -1 or a W argument of exactly the V argument's erased type.
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
    "net/minecraft/class_163.method_711(Ljava/util/Map;)V":
        ("method_53691(Ljava/util/Collection;)V",
         "AdvancementList.add(Map<ResourceLocation, Advancement.Builder>) -> AdvancementTree.addAll(Collection<AdvancementHolder>): "
         "the same topological insert of roots and tasks with the listener's onAddAdvancementRoot / onAddAdvancementTask; 1.20.2 "
         "builds the holders before the call and moved the per-entry insert into tryInsert (method_53692)"),
    "net/minecraft/class_2507.method_10626(Ljava/io/DataInput;ILnet/minecraft/class_2505;)Lnet/minecraft/class_2520;":
        ("method_10626(Ljava/io/DataInput;Lnet/minecraft/class_2505;)Lnet/minecraft/class_2520;",
         "NbtIo.readUnnamedTag: the same private method with its only caller read(DataInput, NbtAccounter); 1.20.2 dropped "
         "the depth (1.20.1's caller always passed 0; the accounter tracks depth now) and moved the guarded load into "
         "readTagSafe (method_52892), which it calls"),
    # smx1c 2026-10-27, MixinCause target-private-similar (javap -c -p of both bodies)
    "net/minecraft/class_8221.method_49761(Lcom/mojang/serialization/Dynamic;Ljava/lang/String;)Lcom/mojang/serialization/Dynamic;":
        ("method_52469(Lcom/mojang/serialization/Dynamic;Ljava/lang/String;)Ljava/util/stream/Stream;",
         "BlockEntitySignDoubleSidedEditableTextFix: the same four <prefix>1..4 line reads of the sign tag; 1.20.2 answers "
         "their stream and its caller builds the list 1.20.1 built here"),
    "net/minecraft/class_5646.method_32431(Ljava/lang/String;)Lnet/minecraft/class_5646;":
        ("method_53432(Ljava/lang/String;)Lnet/minecraft/class_5646$class_5648;",
         "ContextNbtProvider: the same \"block_entity\" test choosing BLOCK_ENTITY's getter or forEntity(EntityTarget.getByName); "
         "1.20.2 answers the getter and its caller wraps it in the provider"),
    "net/minecraft/class_1068.method_47438(Ljava/util/UUID;)Lnet/minecraft/class_1068$class_7921;":
        ("method_4648(Ljava/util/UUID;)Lnet/minecraft/class_8685;",
         "DefaultPlayerSkin: the same DEFAULT_SKINS[floorMod(uuid.hashCode(), length)] (similarity 1.00); the array holds "
         "PlayerSkin where it held the skin type"),
    "net/minecraft/class_310.method_51251(Lnet/minecraft/class_4341;Lnet/minecraft/class_4011;Lnet/minecraft/class_542$class_8495;)V":
        ("method_53528(Lnet/minecraft/class_310$class_8764;)V",
         "Minecraft: the first screen after loading -- QuickPlay.connect when quick play is enabled, else the title screen; "
         "1.20.2 takes the realms client and quick play data from the GameLoadCookie, no longer the reload instance, and "
         "moved the accessibility onboarding branch into its own method"),
    "net/minecraft/class_161$class_162.<init>(Z)V":
        ("<init>()V",
         "Advancement.Builder's constructor of a fresh builder: the same default stores (AdvancementRewards.EMPTY, an empty "
         "criteria map, the AND requirements strategy); 1.20.2 sets the telemetry flag through sendsTelemetryEvent()"),
    "net/minecraft/class_189.<init>(Ljava/lang/String;ILjava/lang/String;ILnet/minecraft/class_124;)V":
        ("<init>(Ljava/lang/String;ILjava/lang/String;Lnet/minecraft/class_124;)V",
         "FrameType -> AdvancementType's one enum constructor: the same name / chat format / advancements.toast.<name> stores; "
         "1.20.2 dropped the texture offset (sprites)"),
    "net/minecraft/class_2539.<init>(Ljava/lang/String;IILnet/minecraft/class_2539$class_4533;)V":
        ("<init>(Ljava/lang/String;ILjava/lang/String;Lnet/minecraft/class_2539$class_4533;)V",
         "ConnectionProtocol's one enum constructor: the same id and flow map stores; the numeric id became a name"),
    "net/minecraft/class_453.<init>(Ljava/lang/String;IIIIII)V":
        ("<init>(Ljava/lang/String;ILnet/minecraft/class_453$class_8670;Lnet/minecraft/class_453$class_8670;III)V",
         "AdvancementTabType's one enum constructor: the same width / height / max stores; the texture offsets became sprites"),
    "net/minecraft/class_5682$class_5771.<init>(Ljava/lang/String;IIIII)V":
        ("<init>(Ljava/lang/String;ILnet/minecraft/class_2960;II)V",
         "ClientBundleTooltip.Texture's one enum constructor: the same width / height stores; the texture offsets became a sprite"),
    "net/minecraft/class_7591$class_7592.<init>(Ljava/lang/String;IIIII)V":
        ("<init>(Ljava/lang/String;ILnet/minecraft/class_2960;II)V",
         "GuiMessageTag.Icon's one enum constructor: the same width / height stores; the texture offsets became a sprite"),
    # smx1c 2026-10-27, MixinCause target-private-reshape (same private method, other parameters; javap -c -p of both
    # bodies and their callers)
    "net/minecraft/class_337.method_41830(Lnet/minecraft/class_332;IILnet/minecraft/class_1259;II)V":
        ("method_41830(Lnet/minecraft/class_332;IILnet/minecraft/class_1259;I[Lnet/minecraft/class_2960;[Lnet/minecraft/class_2960;)V",
         "BossHealthOverlay.drawBar: the same background-by-colour and overlay-by-style blits between enableBlend / disableBlend; "
         "1.20.2 passes the sprite arrays where 1.20.1 passed the texture row (0 background, 5 progress)",
         [0, 1, 2, 3, 4, -1]),
    "net/minecraft/class_2535.method_36942(Lnet/minecraft/class_2596;Lnet/minecraft/class_7648;Lnet/minecraft/class_2539;Lnet/minecraft/class_2539;)V":
        ("method_36942(Lnet/minecraft/class_2596;Lnet/minecraft/class_7648;Z)V",
         "Connection.doSendPacket: the same channel write with the send listener; 1.20.2 no longer switches the protocol here "
         "(configuration phase) and takes whether to flush",
         [0, 1, -1, -1]),
    "net/minecraft/class_3008.method_12990(Lnet/minecraft/class_161;Ljava/util/List;)V":
        ("method_12990(Lnet/minecraft/class_8781;Ljava/util/List;)V",
         "AdvancementCommands.addChildren: the same recursive add of every child; nodes and a List of holders where 1.20.1 "
         "had advancements (the list's element type changed: not paired)",
         [-1, -1]),
    "net/minecraft/class_7689.method_45346(ZF)D":
        ("method_45346(ZFLnet/minecraft/class_4048;F)D",
         "Camel.getBodyAnchorAnimationYOffset: the same sitting / standing-up lerps; both callers pass the same boolean and "
         "partial tick (fconst_0 / fload_1) first, 1.20.2 adds the dimensions and scale",
         [0, 1]),
    "net/minecraft/class_329.method_37299(Lnet/minecraft/class_332;Lnet/minecraft/class_329$class_6411;IIIZZ)V":
        ("method_37299(Lnet/minecraft/class_332;Lnet/minecraft/class_329$class_6411;IIZZZ)V",
         "Gui.renderHeart: the same one heart blit chosen by type, blinking and half; 1.20.2 passes hardcore where 1.20.1 "
         "passed the texture row it implied",
         [0, 1, 2, 3, -1, 5, 6]),
    "net/minecraft/class_447.method_2282(Lnet/minecraft/class_332;IIII)V":
        ("method_2282(Lnet/minecraft/class_332;IILnet/minecraft/class_2960;)V",
         "StatsScreen.blitSlotIcon: the same one icon blit at x, y; a sprite where 1.20.1 took texture u / v",
         [0, 1, 2, -1, -1]),
    "net/minecraft/class_2446.method_34662(Ljava/util/function/Consumer;Lnet/minecraft/class_1935;Lnet/minecraft/class_1935;)V":
        ("method_34662(Lnet/minecraft/class_8790;Lnet/minecraft/class_1935;Lnet/minecraft/class_1935;)V",
         RECIPE_OUTPUT),
    "net/minecraft/class_2446.method_36232(Ljava/util/function/Consumer;Lnet/minecraft/class_1865;Ljava/util/List;Lnet/minecraft/class_7800;Lnet/minecraft/class_1935;FILjava/lang/String;Ljava/lang/String;)V":
        ("method_36232(Lnet/minecraft/class_8790;Lnet/minecraft/class_1865;Ljava/util/List;Lnet/minecraft/class_7800;Lnet/minecraft/class_1935;FILjava/lang/String;Ljava/lang/String;)V",
         RECIPE_OUTPUT),
    "net/minecraft/class_2446.method_36447(Ljava/util/function/Consumer;Lnet/minecraft/class_7800;Lnet/minecraft/class_1935;Lnet/minecraft/class_7800;Lnet/minecraft/class_1935;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V":
        ("method_36447(Lnet/minecraft/class_8790;Lnet/minecraft/class_7800;Lnet/minecraft/class_1935;Lnet/minecraft/class_7800;Lnet/minecraft/class_1935;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
         RECIPE_OUTPUT),
    "net/minecraft/class_2446.method_36448(Ljava/util/function/Consumer;Ljava/lang/String;Lnet/minecraft/class_1865;ILnet/minecraft/class_1935;Lnet/minecraft/class_1935;F)V":
        ("method_36448(Lnet/minecraft/class_8790;Ljava/lang/String;Lnet/minecraft/class_1865;ILnet/minecraft/class_1935;Lnet/minecraft/class_1935;F)V",
         RECIPE_OUTPUT),
    "net/minecraft/class_131.method_29323(Ljava/lang/String;Lnet/minecraft/class_5335;)Lnet/minecraft/class_5339;":
        ("method_29323(Ljava/lang/String;Lcom/mojang/serialization/Codec;)Lnet/minecraft/class_5339;",
         LOOT_REGISTER),
    "net/minecraft/class_217.method_29327(Ljava/lang/String;Lnet/minecraft/class_5335;)Lnet/minecraft/class_5342;":
        ("method_29327(Ljava/lang/String;Lcom/mojang/serialization/Codec;)Lnet/minecraft/class_5342;",
         LOOT_REGISTER),
    "net/minecraft/class_5652.method_32443(Ljava/lang/String;Lnet/minecraft/class_5335;)Lnet/minecraft/class_5650;":
        ("method_32443(Ljava/lang/String;Lcom/mojang/serialization/Codec;)Lnet/minecraft/class_5650;",
         LOOT_REGISTER),
    "net/minecraft/class_5659.method_32456(Ljava/lang/String;Lnet/minecraft/class_5335;)Lnet/minecraft/class_5657;":
        ("method_32456(Ljava/lang/String;Lcom/mojang/serialization/Codec;)Lnet/minecraft/class_5657;",
         LOOT_REGISTER),
    "net/minecraft/class_5671.method_32479(Ljava/lang/String;Lnet/minecraft/class_5335;)Lnet/minecraft/class_5669;":
        ("method_32479(Ljava/lang/String;Lcom/mojang/serialization/Codec;)Lnet/minecraft/class_5669;",
         LOOT_REGISTER),
    "net/minecraft/class_75.method_29317(Ljava/lang/String;Lnet/minecraft/class_5335;)Lnet/minecraft/class_5338;":
        ("method_29317(Ljava/lang/String;Lcom/mojang/serialization/Codec;)Lnet/minecraft/class_5338;",
         LOOT_REGISTER),
    "net/minecraft/class_1294.method_5594(ILjava/lang/String;Lnet/minecraft/class_1291;)Lnet/minecraft/class_1291;":
        ("method_5594(Ljava/lang/String;Lnet/minecraft/class_1291;)Lnet/minecraft/class_1291;",
         "MobEffects.register: the same Registry.register of the effect under its name; 1.20.2 registers by name only, "
         "without 1.20.1's raw id"),
    "net/minecraft/class_8005.method_48030(Lnet/minecraft/class_161;Lit/unimi/dsi/fastutil/Stack;Ljava/util/function/Predicate;Lnet/minecraft/class_8005$class_8006;)Z":
        ("method_48030(Lnet/minecraft/class_8781;Lit/unimi/dsi/fastutil/Stack;Ljava/util/function/Predicate;Lnet/minecraft/class_8005$class_8006;)Z",
         "AdvancementVisibilityEvaluator.evaluateVisibility: the same recursive visibility walk over the rule stack; a node "
         "and a Predicate of nodes where 1.20.1 had advancements (type argument changed: not paired); the stack and output are "
         "the same",
         [-1, 1, -1, 3]),
    "net/minecraft/class_2377.method_10217(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;I)V":
        ("method_10217(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;)V",
         "HopperBlock.checkPoweredState: the same hasNeighborSignal test and ENABLED setBlock; 1.20.2 no longer takes the "
         "block-update flags",
         [0, 1, 2, -1]),
    "net/minecraft/class_2487.method_10581(Lnet/minecraft/class_4614;Ljava/lang/String;Ljava/io/DataInput;ILnet/minecraft/class_2505;)Lnet/minecraft/class_2520;":
        ("method_10581(Lnet/minecraft/class_4614;Ljava/lang/String;Ljava/io/DataInput;Lnet/minecraft/class_2505;)Lnet/minecraft/class_2520;",
         "CompoundTag.readNamedTagData: the same TagType.load with the same crash report; 1.20.2 dropped the depth (the "
         "accounter tracks it), as NbtIo.readUnnamedTag above"),
    "net/minecraft/class_2609.method_11192(Lnet/minecraft/class_5455;Lnet/minecraft/class_1860;Lnet/minecraft/class_2371;I)Z":
        ("method_11192(Lnet/minecraft/class_5455;Lnet/minecraft/class_8786;Lnet/minecraft/class_2371;I)Z",
         RECIPE_HOLDER),
    "net/minecraft/class_2609.method_11203(Lnet/minecraft/class_5455;Lnet/minecraft/class_1860;Lnet/minecraft/class_2371;I)Z":
        ("method_11203(Lnet/minecraft/class_5455;Lnet/minecraft/class_8786;Lnet/minecraft/class_2371;I)Z",
         RECIPE_HOLDER),
    "net/minecraft/class_299.method_1400(Lnet/minecraft/class_1860;)Lnet/minecraft/class_314;":
        ("method_1400(Lnet/minecraft/class_8786;)Lnet/minecraft/class_314;",
         RECIPE_HOLDER),
    "net/minecraft/class_366.method_1984(Lnet/minecraft/class_1860;)V":
        ("method_1984(Lnet/minecraft/class_8786;)V",
         RECIPE_HOLDER),
    "net/minecraft/class_4209.method_22319(Lnet/minecraft/class_3218;Lnet/minecraft/class_2540;Lnet/minecraft/class_2960;)V":
        ("method_22319(Lnet/minecraft/class_3218;Lnet/minecraft/class_8710;)V",
         "DebugPackets.sendPacketToAllPlayers: the same ClientboundCustomPayloadPacket sent to every player of the level; "
         "1.20.2 takes the payload where 1.20.1 took the channel id and buffer"),
    "net/minecraft/class_4703.method_23806(Lnet/minecraft/class_4703$class_5243;Lnet/minecraft/class_2338;)Ljava/lang/String;":
        ("method_23806(Lnet/minecraft/class_8707$class_5243;Lnet/minecraft/class_2338;)Ljava/lang/String;",
         "BeeDebugRenderer.getPosDescription: the same rounded distance text from the bee's position; the bee info is "
         "BeeDebugPayload's record now"),
    "net/minecraft/class_8564.method_52189(JLnet/minecraft/class_2960;)Lnet/minecraft/class_6677;":
        ("method_52189(JLjava/util/Optional;)Lnet/minecraft/class_6677;",
         "RandomSequences' sequence factory: the same seed mix with the id's seed; 1.20.2 takes the id as an Optional "
         "(absent: the world seed alone)"),
    "net/minecraft/class_2985.method_48025(Lnet/minecraft/class_161;Ljava/util/Set;Ljava/util/Set;)V":
        ("method_48025(Lnet/minecraft/class_8781;Ljava/util/Set;Ljava/util/Set;)V",
         "PlayerAdvancements.updateTreeVisibility: the same AdvancementVisibilityEvaluator.evaluateVisibility call with the "
         "same predicate and output; a node and a Set of holders where 1.20.1 had advancements (element type changed: not "
         "paired); the id set is the same",
         [-1, -1, 2]),
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
    for k, (to, basis, *given) in JUDGED.items():
        va = arg_types(k[k.index("("):])
        wa = arg_types(to[to.index("("):])
        if given:
            args = list(given[0])
            if len(args) != len(va) or any(j != -1 and (j >= len(wa) or wa[j] != va[i]) for i, j in enumerate(args)):
                raise SystemExit("judged argument map does not fit " + k + " -> " + to + ": " + str(args))
            out[k] = {"to": to, "args": args, "basis": basis}
            continue
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
