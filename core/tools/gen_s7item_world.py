"""Wiring for 1.21.1's world/ticks and the smaller item families on 1.21.2 (s7item, step 1.21.1 -> 1.21.2).

  ticks              SerializableTickContainer.save(gameTime, idGetter) calls / supers / implementations / lambdas
                     (1.21.2: pack(gameTime)), ProtoChunkTicks.load and SavedTick.loadTickList / saveTick on ListTags,
                     LevelTicks(isTickable, profiler) (rt/CbItemTicks, CbItemLevelTicksMixin)

  items              ComplexItem / BookItem / ItemNameBlockItem facades (classRenames; RecipeBookCategories is s7rest's), MapItem.getUpdatePacket,
                     BannerPatternItem.getDisplayName, BundleItem(properties) (rt/CbItemSmall, CbItemComplexMixin)

  smithing templates SmithingTemplateItem's 1.21.1 constructor and factories (rt/CbItemSmithingTemplates,
                     CbItemSmithingTemplateMixin)

  potions            Potion(effects) / getName, PotionContents(potion, colour, effects), dust particle colours
                     (rt/CbItemPotions, CbItemPotionNameMixin)

  by entity type     BoatItem / BoatDispenseItemBehavior / MinecartItem constructors (1.21.2 takes the entity type),
                     ItemCombinerMenuSlotDefinition.hasSlot / getInputSlotIndexes, EntityCollisionContext(entity),
                     CollisionContext.getCollisionShape for 1.21.1 implementations

  misc               JukeboxPlayable.tryInsertIntoJukebox, FeatureFlags.BUNDLE, ItemStack.OPTIONAL_LIST_STREAM_CODEC
  triggers           advancement criteria builders taking the registry lookups, RecipeCrafted / RecipeUnlocked triggers by
                     recipe id (rt/CbItemTriggers)

  instruments        BuiltInRegistries.INSTRUMENT as a stand-in whose entries join each server's instrument registry;
                     Instrument's tick duration; InstrumentItem.create / setRandom; Instruments.bootstrap / GOAT_HORN_RANGE
                     (rt/CbItemInstruments, CbItemInstrumentLoaderMixin)

  python gen_s7item_world.py <out fragment.json>
"""
import json
import sys

GUARD = "top/dext/centurybridge/step/v1_21_2/rt/Guard"


def ticks(out):
    rt = "top/dext/centurybridge/step/v1_21_2/rt/CbItemTicks"
    stc = "net/minecraft/class_6761"
    save = "method_20463(JLjava/util/function/Function;)Lnet/minecraft/class_2520;"
    for owner in [stc, "net/minecraft/class_4296"]:
        out["instanceRedirects"][owner + "." + save] = rt + "#save"
    out["superRedirects"]["net/minecraft/class_4296." + save] = rt + "#superSave"
    out["adapterHooks"].append({
        "under": stc, "adapter": rt + "$LegacySave", "guard": GUARD,
        "requires": [save],
        "synth": [{"new": "method_61898(J)Ljava/util/List;",
                   "call": rt + ".pack(L" + rt + "$LegacySave;J)Ljava/util/List;"}],
        "basis": "a 1.21.1 tick container saved itself as NBT: 1.21.2's pack(gameTime) is its own save read back "
                 "(CbItemTicks.pack); the guard lets its super.save reach 1.21.2's pack"})
    out["lambdaAdapters"][stc + "." + save] = {
        "iface": rt + "$LegacySave",
        "adapt": rt + "#lambda(L" + rt + "$LegacySave;)Lnet/minecraft/class_6761;",
        "basis": "a 1.21.1 SerializableTickContainer lambda answers 1.21.2's pack(gameTime) with what it saves"}
    out["staticRedirects"]["net/minecraft/class_4296.method_39398(Lnet/minecraft/class_2499;Ljava/util/function/Function;"
                           "Lnet/minecraft/class_1923;)Lnet/minecraft/class_4296;"] = rt + "#load"
    out["staticRedirects"]["net/minecraft/class_6759.method_39406(Lnet/minecraft/class_2499;Ljava/util/function/Function;"
                           "Lnet/minecraft/class_1923;Ljava/util/function/Consumer;)V"] = rt + "#loadTickList"
    out["staticRedirects"]["net/minecraft/class_6759.method_39401(Lnet/minecraft/class_6760;Ljava/util/function/Function;J)"
                           "Lnet/minecraft/class_2487;"] = rt + "#saveTick"
    out["superCtorReshapes"]["net/minecraft/class_6757.<init>(Ljava/util/function/LongPredicate;"
                             "Ljava/util/function/Supplier;)V"] = {
        "desc": "(Ljava/util/function/LongPredicate;)V", "ops": ["keep", "drop"],
        "post": rt + "#keepProfiler(Lnet/minecraft/class_6757;Ljava/util/function/Supplier;)V", "postArgs": [1],
        "basis": "1.21.2's LevelTicks profiles on the thread's profiler; the 1.21.1 constructor's supplier is kept and "
                 "used for its tick (CbItemLevelTicksMixin)"}


def items(out):
    rt = "top/dext/centurybridge/step/v1_21_2/rt/CbItemSmall"
    fac = "top/dext/centurybridge/step/v1_21_2/facade/"
    out["classRenames"] = {
        "net/minecraft/class_1762": fac + "ComplexItem",
        "net/minecraft/class_1751": fac + "BookItem",
        "net/minecraft/class_1798": fac + "ItemNameBlockItem",
    }
    upd = "method_7757(Lnet/minecraft/class_1799;Lnet/minecraft/class_1937;Lnet/minecraft/class_1657;)Lnet/minecraft/class_2596;"
    out["instanceRedirects"]["net/minecraft/class_1806." + upd] = rt + "#getUpdatePacket"
    out["superRedirects"]["net/minecraft/class_1806." + upd] = rt + "#superGetUpdatePacket"
    out["adapterHooks"].append({
        "under": "net/minecraft/class_1806", "adapter": rt + "$Legacy_getUpdatePacket", "requires": [upd], "synth": [],
        "basis": "a 1.21.1 MapItem subclass's getUpdatePacket answers its calls and the server's inventory "
                 "synchronisation (CbItemComplexMixin)"})
    disp = "method_7703()Lnet/minecraft/class_5250;"
    out["instanceRedirects"]["net/minecraft/class_1745." + disp] = rt + "#getDisplayName"
    out["superRedirects"]["net/minecraft/class_1745." + disp] = rt + "#superGetDisplayName"
    out["adapterHooks"].append({
        "under": "net/minecraft/class_1745", "adapter": rt + "$Legacy_getDisplayName", "requires": [disp], "synth": [],
        "basis": "a 1.21.1 BannerPatternItem subclass's getDisplayName answers its calls"})
    out["superCtorReshapes"]["net/minecraft/class_5537.<init>(Lnet/minecraft/class_1792$class_1793;)V"] = {
        "desc": "(Lnet/minecraft/class_2960;Lnet/minecraft/class_2960;Lnet/minecraft/class_1792$class_1793;)V",
        "ops": ["push " + rt + "#bundleOpenFront()Lnet/minecraft/class_2960;",
                "push " + rt + "#bundleOpenBack()Lnet/minecraft/class_2960;", "keep"],
        "basis": "1.21.2's BundleItem takes the models of its open state (new in 1.21.2): a 1.21.1 bundle gets the plain "
                 "bundle's"}


def smithing_templates(out):
    rt = "top/dext/centurybridge/step/v1_21_2/rt/CbItemSmithingTemplates"
    st = "net/minecraft/class_8052"
    c = "Lnet/minecraft/class_2561;"
    old = "(" + c * 5 + "Ljava/util/List;Ljava/util/List;[Lnet/minecraft/class_7696;)V"
    new = "(" + c * 4 + "Ljava/util/List;Ljava/util/List;Lnet/minecraft/class_1792$class_1793;)V"
    out["superCtorReshapes"][st + ".<init>" + old] = {
        "desc": new,
        "ops": ["keep:0", "keep:1", "keep:3", "keep:4", "keep:5", "keep:6",
                "conv:7 " + rt + "#properties([Lnet/minecraft/class_7696;)Lnet/minecraft/class_1792$class_1793;"],
        "post": rt + "#keepUpgrade(Lnet/minecraft/class_8052;Lnet/minecraft/class_2561;)V", "postArgs": [2],
        "basis": "1.21.2's SmithingTemplateItem takes Item.Properties (1.21.1 built them from the feature flags) and no "
                 "upgrade description; the template keeps it for its tooltip (CbItemSmithingTemplateMixin)"}
    out["staticRedirects"][st + ".method_48418(Lnet/minecraft/class_5321;[Lnet/minecraft/class_7696;)Lnet/minecraft/class_8052;"] =         rt + "#createArmorTrimTemplate"
    out["staticRedirects"][st + ".method_48419(Lnet/minecraft/class_2960;[Lnet/minecraft/class_7696;)Lnet/minecraft/class_8052;"] =         rt + "#createArmorTrimTemplate"
    out["staticRedirects"][st + ".method_48420()Lnet/minecraft/class_8052;"] = rt + "#createNetheriteUpgradeTemplate"


def potions(out):
    rt = "top/dext/centurybridge/step/v1_21_2/rt/CbItemPotions"
    out["superCtorReshapes"]["net/minecraft/class_1842.<init>([Lnet/minecraft/class_1293;)V"] = {
        "desc": "(Ljava/lang/String;[Lnet/minecraft/class_1293;)V",
        "ops": ["push " + rt + "#unnamed()Ljava/lang/String;", "keep"],
        "basis": "1.21.1's Potion(effects) had no name (named by its registry path, CbItemPotionNameMixin)"}
    out["staticRedirects"]["net/minecraft/class_1842.method_8051(Ljava/util/Optional;Ljava/lang/String;)Ljava/lang/String;"] =         rt + "#getName"
    out["superCtorReshapes"]["net/minecraft/class_1844.<init>(Ljava/util/Optional;Ljava/util/Optional;Ljava/util/List;)V"] = {
        "desc": "(Ljava/util/Optional;Ljava/util/Optional;Ljava/util/List;Ljava/util/Optional;)V",
        "ops": ["keep", "keep", "keep", "push " + rt + "#noCustomName()Ljava/util/Optional;"],
        "basis": "1.21.2's PotionContents adds a custom name; 1.21.1's had none"}
    rgb = "conv " + rt + "#rgb(Lorg/joml/Vector3f;)I"
    out["superCtorReshapes"]["net/minecraft/class_2390.<init>(Lorg/joml/Vector3f;F)V"] = {
        "desc": "(IF)V", "ops": [rgb, "keep"],
        "basis": "1.21.2's DustParticleOptions takes its colour packed RGB (CbItemPotions.rgb)"}
    out["superCtorReshapes"]["net/minecraft/class_5743.<init>(Lorg/joml/Vector3f;Lorg/joml/Vector3f;F)V"] = {
        "desc": "(IIF)V", "ops": [rgb, rgb, "keep"],
        "basis": "1.21.2's DustColorTransitionOptions takes its colours packed RGB (CbItemPotions.rgb)"}
    out["fieldGetters"]["net/minecraft/class_2390.field_28272:Lorg/joml/Vector3f;"] = rt
    out["fieldGetters"]["net/minecraft/class_5743.field_28267:Lorg/joml/Vector3f;"] = rt


def entities_by_type(out):
    rt = "top/dext/centurybridge/step/v1_21_2/rt/CbItemSmall"
    bt = "Lnet/minecraft/class_1690$class_1692;"
    et = "Lnet/minecraft/class_1299;"
    out["superCtorReshapes"]["net/minecraft/class_1749.<init>(Z" + bt + "Lnet/minecraft/class_1792$class_1793;)V"] = {
        "desc": "(" + et + "Lnet/minecraft/class_1792$class_1793;)V",
        "ops": ["conv:0,1 " + rt + "#boatEntity(Z" + bt + ")" + et, "keep:2"],
        "basis": "1.21.2's BoatItem takes the boat entity type: the type's boat or chest boat (s7entity's BoatType)"}
    out["superCtorReshapes"]["net/minecraft/class_2967.<init>(" + bt + ")V"] = {
        "desc": "(" + et + ")V", "ops": ["conv " + rt + "#boatEntity(" + bt + ")" + et],
        "basis": "1.21.2's BoatDispenseItemBehavior takes the boat entity type"}
    out["superCtorReshapes"]["net/minecraft/class_2967.<init>(" + bt + "Z)V"] = {
        "desc": "(" + et + ")V", "ops": ["conv:0,1 " + rt + "#boatEntity(" + bt + "Z)" + et],
        "basis": "1.21.2's BoatDispenseItemBehavior takes the boat entity type: the type's boat or chest boat"}
    out["superCtorReshapes"]["net/minecraft/class_1808.<init>(Lnet/minecraft/class_1688$class_1689;"
                             "Lnet/minecraft/class_1792$class_1793;)V"] = {
        "desc": "(" + et + "Lnet/minecraft/class_1792$class_1793;)V",
        "ops": ["conv " + rt + "#minecartEntity(Lnet/minecraft/class_1688$class_1689;)" + et, "keep"],
        "basis": "1.21.2's MinecartItem takes the minecart entity type (s7entity's MinecartType)"}
    sd = "net/minecraft/class_8047"
    out["instanceRedirects"][sd + ".method_48365(I)Z"] = rt + "#hasSlot"
    out["instanceRedirects"][sd + ".method_48371()Ljava/util/List;"] = rt + "#getInputSlotIndexes"
    out["superCtorReshapes"]["net/minecraft/class_3727.<init>(Lnet/minecraft/class_1297;)V"] = {
        "desc": "(Lnet/minecraft/class_1297;Z)V", "ops": ["keep", "push " + rt + "#noFluidCollision()Z"],
        "basis": "1.21.2's EntityCollisionContext adds alwaysCollideWithFluid; 1.21.1's never did"}
    out["abstractFills"].append({
        "under": "net/minecraft/class_3726",
        "method": "method_62878(Lnet/minecraft/class_2680;Lnet/minecraft/class_1941;Lnet/minecraft/class_2338;)Lnet/minecraft/class_265;",
        "call": rt + ".collisionShape(Lnet/minecraft/class_3726;Lnet/minecraft/class_2680;Lnet/minecraft/class_1941;"
                     "Lnet/minecraft/class_2338;)Lnet/minecraft/class_265;",
        "basis": "1.21.2's CollisionContext demands getCollisionShape(state, level, pos); a 1.21.1 context answers the "
                 "state's collision shape in it, what 1.21.1's callers asked"})


def misc(out):
    rt = "top/dext/centurybridge/step/v1_21_2/rt/CbItemSmall"
    out["staticRedirects"]["net/minecraft/class_9792.method_60747(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;"
                           "Lnet/minecraft/class_1799;Lnet/minecraft/class_1657;)Lnet/minecraft/class_9062;"] = rt + "#tryInsertIntoJukebox"
    out["fieldGetters"]["net/minecraft/class_7701.field_40178:Lnet/minecraft/class_7696;"] = rt
    out["fieldGetters"]["net/minecraft/class_1799.field_48350:Lnet/minecraft/class_9139;"] = rt


def triggers(out):
    rt = "top/dext/centurybridge/step/v1_21_2/rt/CbItemTriggers"
    rl = "Lnet/minecraft/class_2960;"
    key = "Lnet/minecraft/class_5321;"
    adv = "Lnet/minecraft/class_175;"
    s = out["staticRedirects"]
    s["net/minecraft/class_2010$class_2012.method_8828(Lnet/minecraft/class_1935;)" + adv] = rt + "#usedItem"
    s["net/minecraft/class_2123$class_2125.method_9120(Lnet/minecraft/class_1935;)" + adv] = rt + "#shotCrossbow"
    s["net/minecraft/class_2148$class_2150.method_9170(Lnet/minecraft/class_1935;)" + adv] = rt + "#usedTotem"
    s["net/minecraft/class_2135$class_2137.method_43136(Lnet/minecraft/class_2248;Lnet/minecraft/class_1792;)" + adv] =         rt + "#walkOnBlockWithEquipment"
    s["net/minecraft/class_3735.method_58057(Lnet/minecraft/class_7871;)Lnet/minecraft/class_3735;"] = rt + "#captainPredicate"
    crafted = "net/minecraft/class_8508$class_8509"
    s[crafted + ".method_51352(" + rl + ")" + adv] = rt + "#craftedItem"
    s[crafted + ".method_51353(" + rl + "Ljava/util/List;)" + adv] = rt + "#craftedItem"
    s[crafted + ".method_58450(" + rl + ")" + adv] = rt + "#crafterCraftedItem"
    s["net/minecraft/class_2119.method_27847(" + rl + ")" + adv] = rt + "#unlocked"
    out["instanceRedirects"][crafted + ".comp_2087()" + rl] = rt + "#recipeId"
    out["instanceRedirects"]["net/minecraft/class_2119$class_2121.comp_2089()" + rl] = rt + "#recipe"
    conv = "conv " + rt + "#key(" + rl + ")" + key
    out["superCtorReshapes"][crafted + ".<init>(Ljava/util/Optional;" + rl + "Ljava/util/List;)V"] = {
        "desc": "(Ljava/util/Optional;" + key + "Ljava/util/List;)V", "ops": ["keep", conv, "keep"],
        "basis": "1.21.2 names the recipe by its key: the id in the recipe registry"}
    out["superCtorReshapes"]["net/minecraft/class_2119$class_2121.<init>(Ljava/util/Optional;" + rl + ")V"] = {
        "desc": "(Ljava/util/Optional;" + key + ")V", "ops": ["keep", conv],
        "basis": "1.21.2 names the recipe by its key: the id in the recipe registry"}
    old = "method_51350(Lnet/minecraft/class_3222;" + rl + "Ljava/util/List;)V"
    new = "method_51350(Lnet/minecraft/class_3222;" + key + "Ljava/util/List;)V"
    out["instanceRedirects"]["net/minecraft/class_8508." + old] = rt + "#trigger"
    out["overrideHooks"].append({
        "old": old, "new": new, "onlyUnder": "net/minecraft/class_8508",
        "adapter": rt + "$LegacyCrafted",
        "bridge": rt + ".triggerBridge(L" + rt + "$LegacyCrafted;Lnet/minecraft/class_3222;" + key + "Ljava/util/List;)V",
        "superBridge": rt + ".superTriggerBridge(L" + rt + "$SuperCrafted;Lnet/minecraft/class_3222;" + rl + "Ljava/util/List;)V",
        "superAccess": rt + "$SuperCrafted", "superAccessors": [new],
        "basis": "1.21.2 triggers RecipeCraftedTrigger with the recipe's key: a 1.21.1 subclass's trigger(player, id, "
                 "items) answers with its location; its super call is 1.21.2's body with the id's key"})


def frame_defaults(out):
    """frameDefaults for mechanical.json's frames hooks in s7item's area: the value a 1.21.1 override entered directly
    hands its rewritten super call (CbItemTriggers)"""
    rt = "top/dext/centurybridge/step/v1_21_2/rt/CbItemTriggers#"
    hg = ")Lnet/minecraft/class_7871;"
    b = "Lnet/minecraft/class_2048$class_2049;"
    out["frameDefaults"] = {
        "method_8921(Lnet/minecraft/class_1299;)Lnet/minecraft/class_2048$class_2049;":
            {"0": rt + "entityTypes(" + b + "Lnet/minecraft/class_1299;" + hg},
        "method_8922(Lnet/minecraft/class_6862;)Lnet/minecraft/class_2048$class_2049;":
            {"0": rt + "entityTypes(" + b + "Lnet/minecraft/class_6862;" + hg},
        "method_8980(Lnet/minecraft/class_3222;Ljava/util/Collection;)V":
            {"2": rt + "noWeapon(Lnet/minecraft/class_2076;Lnet/minecraft/class_3222;Ljava/util/Collection;)Lnet/minecraft/class_1799;"},
        "method_46034()Lnet/minecraft/class_5341$class_210;":
            {"0": rt + "entityTypes(Lnet/minecraft/class_7789;" + hg},
        "method_46030(Lnet/minecraft/class_5321;)Lnet/minecraft/class_5341$class_210;":
            {"0": rt + "entityTypes(Lnet/minecraft/class_7789;Lnet/minecraft/class_5321;" + hg},
    }


def instruments(out):
    rt = "top/dext/centurybridge/step/v1_21_2/rt/CbItemInstruments"
    out["fieldGetters"]["net/minecraft/class_7923.field_41166:Lnet/minecraft/class_2378;"] = rt
    out["fieldGetters"]["net/minecraft/class_7445.field_39125:I"] = rt
    out["fieldGetters"]["net/minecraft/class_7378.field_41535:Lnet/minecraft/class_7378$class_7380;"] =         "top/dext/centurybridge/step/v1_21_2/rt/CbItemTriggers"
    out["superCtorReshapes"]["net/minecraft/class_7444.<init>(Lnet/minecraft/class_6880;IF)V"] = {
        "desc": "(Lnet/minecraft/class_6880;FFLnet/minecraft/class_2561;)V",
        "ops": ["keep", "conv " + rt + "#seconds(I)F", "keep", "push " + rt + "#unnamed()Lnet/minecraft/class_2561;"],
        "basis": "1.21.2's Instrument counts its use duration in seconds and carries a description (made when the entry "
                 "joins a world: CbItemInstruments)"}
    out["instanceRedirects"]["net/minecraft/class_7444.comp_773()I"] = rt + "#comp_773"
    out["staticRedirects"]["net/minecraft/class_7445.method_43713(Lnet/minecraft/class_2378;)Lnet/minecraft/class_7444;"] =         rt + "#bootstrapInto"
    out["staticRedirects"]["net/minecraft/class_7430.method_43562(Lnet/minecraft/class_1799;Lnet/minecraft/class_6862;"
                           "Lnet/minecraft/class_5819;)V"] = rt + "#setRandom"
    out["staticRedirects"]["net/minecraft/class_7430.method_43558(Lnet/minecraft/class_1792;Lnet/minecraft/class_6880;)"
                           "Lnet/minecraft/class_1799;"] = rt + "#create"


def main():
    out = {"instanceRedirects": {}, "superRedirects": {}, "staticRedirects": {}, "adapterHooks": [],
           "overrideHooks": [], "lambdaAdapters": {}, "superCtorReshapes": {}, "fieldGetters": {}, "abstractFills": []}
    ticks(out)
    items(out)
    smithing_templates(out)
    potions(out)
    entities_by_type(out)
    misc(out)
    triggers(out)
    frame_defaults(out)
    instruments(out)
    json.dump(out, open(sys.argv[1], "w", encoding="utf-8", newline="\n"), indent=1)


if __name__ == "__main__":
    main()
