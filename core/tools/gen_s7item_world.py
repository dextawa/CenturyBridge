"""Wiring for 1.21.1's world/ticks and the smaller item families on 1.21.2 (s7item, step 1.21.1 -> 1.21.2).

  ticks              SerializableTickContainer.save(gameTime, idGetter) calls / supers / implementations / lambdas
                     (1.21.2: pack(gameTime)), ProtoChunkTicks.load and SavedTick.loadTickList / saveTick on ListTags,
                     LevelTicks(isTickable, profiler) (rt/CbItemTicks, CbItemLevelTicksMixin)

  items              ComplexItem / BookItem / ItemNameBlockItem / RecipeBookCategories facades (classRenames), MapItem.getUpdatePacket,
                     BannerPatternItem.getDisplayName, BundleItem(properties) (rt/CbItemSmall, CbItemComplexMixin)

  smithing templates SmithingTemplateItem's 1.21.1 constructor and factories (rt/CbItemSmithingTemplates,
                     CbItemSmithingTemplateMixin)

  potions            Potion(effects) / getName, PotionContents(potion, colour, effects), dust particle colours
                     (rt/CbItemPotions, CbItemPotionNameMixin)

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
        "net/minecraft/class_314": fac + "RecipeBookCategories",
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


def main():
    out = {"instanceRedirects": {}, "superRedirects": {}, "staticRedirects": {}, "adapterHooks": [],
           "overrideHooks": [], "lambdaAdapters": {}, "superCtorReshapes": {}, "fieldGetters": {}}
    ticks(out)
    items(out)
    smithing_templates(out)
    potions(out)
    json.dump(out, open(sys.argv[1], "w", encoding="utf-8", newline="\n"), indent=1)


if __name__ == "__main__":
    main()
