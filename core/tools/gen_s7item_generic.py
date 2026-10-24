"""Wiring for s7item's generic drift on 1.21.1 -> 1.21.2: members whose erased descriptor stayed while their type
arguments changed (core/segments/damage-generic/1.21.1__1.21.2.tsv; converters in rt/CbItemGeneric).

  AdvancementRewards  constructor's recipes and recipes(): ids <-> recipe keys
  PlayerPredicate     recipes(): key -> unlocked map as id -> unlocked
  MappedRegistry      getTags() declared on MappedRegistry itself: 1.21.1's (key, set) pairs (CbItemRegistries)
  TagLoader           build(..) override: 1.21.1's Collection-valued map as 1.21.2's List-valued one
  TrimMaterial        armour overrides keyed by ArmorMaterial holder <-> by equipment model id
  components          RECIPES / OMINOUS_BOTTLE_AMPLIFIER / FIRE_RESISTANT values at every get / set / update / remove /
                      builder site (callReshapes conv; genericWitnesses for the type fields; FIRE_RESISTANT reads as
                      DAMAGE_RESISTANT)

  python gen_s7item_generic.py <out fragment.json>
"""
import json
import sys

RT = "top/dext/centurybridge/step/v1_21_2/rt/CbItemGeneric"
LIST = "Ljava/util/List;"
MAP = "Ljava/util/Map;"


def conv(name, t):
    return "conv " + RT + "#" + name + "(" + t + ")" + t


def main():
    out = {"superCtorReshapes": {}, "callReshapes": {}, "convertHooks": []}
    rewards = "net/minecraft/class_170"
    out["superCtorReshapes"][rewards + ".<init>(I" + LIST + LIST + "Ljava/util/Optional;)V"] = {
        "desc": "(I" + LIST + LIST + "Ljava/util/Optional;)V",
        "ops": ["keep", "keep", conv("recipeKeys", LIST), "keep"],
        "basis": "AdvancementRewards' recipes: 1.21.1's ids as 1.21.2's recipe keys"}
    out["callReshapes"][rewards + ".comp_2027()" + LIST] = {
        "desc": "()" + LIST, "ops": [], "ret": conv("recipeIds", LIST),
        "basis": "AdvancementRewards.recipes(): 1.21.2's recipe keys as 1.21.1's ids (a record: no override)"}
    out["callReshapes"]["net/minecraft/class_4553.comp_1820()Lit/unimi/dsi/fastutil/objects/Object2BooleanMap;"] = {
        "desc": "()Lit/unimi/dsi/fastutil/objects/Object2BooleanMap;", "ops": [],
        "ret": conv("recipeIdMap", "Lit/unimi/dsi/fastutil/objects/Object2BooleanMap;"),
        "basis": "PlayerPredicate.recipes(): 1.21.2's key -> unlocked map as 1.21.1's id -> unlocked (a record)"}
    out["callReshapes"]["net/minecraft/class_2370.method_40272()Ljava/util/stream/Stream;"] = {
        "desc": "()Ljava/util/stream/Stream;", "ops": [],
        "ret": "conv top/dext/centurybridge/step/v1_21_2/rt/CbItemRegistries#tagPairs(Ljava/util/stream/Stream;)Ljava/util/stream/Stream;",
        "basis": "MappedRegistry.getTags() (declared there as well as on Registry, whose callReshape and convertHooks "
                 "are s7item-registries-gen's): 1.21.1's stream of (key, set) pairs"}
    out["convertHooks"].append({
        "under": "net/minecraft/class_3503", "member": "method_18242(" + MAP + ")" + MAP,
        "ret": RT + "#tagLists(" + MAP + ")" + MAP,
        "basis": "a 1.21.1 TagLoader.build(..) override answers Collection values; 1.21.2's loader reads Lists"})
    trim = "net/minecraft/class_8054"
    t = "Ljava/lang/String;Lnet/minecraft/class_6880;F" + MAP + "Lnet/minecraft/class_2561;"
    out["superCtorReshapes"][trim + ".<init>(" + t + ")V"] = {
        "desc": "(" + t + ")V", "ops": ["keep", "keep", "keep", conv("overridesByModel", MAP), "keep"],
        "basis": "TrimMaterial's armour overrides: 1.21.1's ArmorMaterial keys as 1.21.2's equipment model ids"}
    out["callReshapes"][trim + ".comp_1237()" + MAP] = {
        "desc": "()" + MAP, "ops": [], "ret": conv("overridesByMaterial", MAP),
        "basis": "TrimMaterial.overrideArmorMaterials(): 1.21.2's model ids as 1.21.1's ArmorMaterial holders (a record)"}
    c = "Ljava/lang/String;Lnet/minecraft/class_1792;FLnet/minecraft/class_2561;" + MAP
    out["callReshapes"][trim + ".method_48438(" + c + ")Lnet/minecraft/class_8054;"] = {
        "desc": "(" + c + ")Lnet/minecraft/class_8054;",
        "ops": ["keep", "keep", "keep", "keep", conv("overridesByModel", MAP)],
        "basis": "TrimMaterial.create(.., overrides): 1.21.1's ArmorMaterial keys as 1.21.2's equipment model ids"}
    components(out)
    json.dump(out, open(sys.argv[1], "w", encoding="utf-8", newline="\n"), indent=1)


def components(out):
    """component values whose type changed under a type a 1.21.1 mod names: converted at every site a value goes in
    or comes out (rt/CbItemComponentValues); the component type fields themselves pass unchanged (genericWitnesses)"""
    cv = "top/dext/centurybridge/step/v1_21_2/rt/CbItemComponentValues"
    T = "Lnet/minecraft/class_9331;"
    O = "Ljava/lang/Object;"
    to_v = "conv " + cv + "#toV(" + O + ")" + O
    to_w = "conv:0,1 " + cv + "#toW(" + T + O + ")" + O
    sites = {
        "net/minecraft/class_9322.method_57824(" + T + ")" + O: (["keep"], to_v),
        "net/minecraft/class_9322.method_57825(" + T + O + ")" + O: (["keep:0", to_w], to_v),
        "net/minecraft/class_9323.method_57829(" + T + ")" + O: (["keep"], to_v),
        "net/minecraft/class_9323.method_57830(" + T + O + ")" + O: (["keep:0", to_w], to_v),
        "net/minecraft/class_1799.method_57379(" + T + O + ")" + O: (["keep:0", to_w], to_v),
        "net/minecraft/class_1799.method_57381(" + T + ")" + O: (["keep"], to_v),
        "net/minecraft/class_1799.method_57368(" + T + O + "Ljava/util/function/UnaryOperator;)" + O:
            (["keep:0", to_w, "conv:0,2 " + cv + "#operator(" + T + "Ljava/util/function/UnaryOperator;)"
              "Ljava/util/function/UnaryOperator;"], to_v),
        "net/minecraft/class_1799.method_57367(" + T + O + O + "Ljava/util/function/BiFunction;)" + O:
            (["keep:0", to_w, "keep:2", "conv:0,3 " + cv + "#biFunction(" + T + "Ljava/util/function/BiFunction;)"
              "Ljava/util/function/BiFunction;"], to_v),
        "net/minecraft/class_9323$class_9324.method_57840(" + T + O + ")Lnet/minecraft/class_9323$class_9324;":
            (["keep:0", to_w], None),
        "net/minecraft/class_9326$class_9327.method_57854(" + T + O + ")Lnet/minecraft/class_9326$class_9327;":
            (["keep:0", to_w], None),
        "net/minecraft/class_9326.method_57845(" + T + ")Ljava/util/Optional;":
            (["keep"], "conv " + cv + "#toV(Ljava/util/Optional;)Ljava/util/Optional;"),
        "net/minecraft/class_1792$class_1793.method_57349(" + T + O + ")Lnet/minecraft/class_1792$class_1793;":
            (["keep:0", to_w], None),
    }
    for key, (ops, ret) in sites.items():
        entry = {"desc": key[key.index("("):], "ops": ops,
                 "basis": "a component value handed in / taken out: RECIPES ids <-> keys, OMINOUS_BOTTLE_AMPLIFIER "
                          "Integer <-> OminousBottleAmplifier, FIRE_RESISTANT's Unit <-> DamageResistant(is_fire) "
                          "(CbItemComponentValues)"}
        if ret:
            entry["ret"] = ret
        out["callReshapes"][key] = entry
    witnessed = {"by": sorted(sites), "basis": "the component type passes unchanged (the same 1.21.2 object); its values "
                 "convert at every site a mod hands one in or takes one out (CbItemComponentValues)"}
    out["genericWitnesses"] = {
        "net/minecraft/class_9334.field_49613:" + T: witnessed,
        "net/minecraft/class_9334.field_50238:" + T: witnessed,
    }
    out["fieldGetters"] = {"net/minecraft/class_9334.field_50076:" + T: cv}


if __name__ == "__main__":
    main()
