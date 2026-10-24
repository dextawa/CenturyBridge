"""Wiring for s7item's generic drift on 1.21.1 -> 1.21.2: members whose erased descriptor stayed while their type
arguments changed (core/segments/damage-generic/1.21.1__1.21.2.tsv; converters in rt/CbItemGeneric).

  AdvancementRewards  constructor's recipes and recipes(): ids <-> recipe keys
  PlayerPredicate     recipes(): key -> unlocked map as id -> unlocked
  MappedRegistry      getTags() declared on MappedRegistry itself: 1.21.1's (key, set) pairs (CbItemRegistries)
  TagLoader           build(..) override: 1.21.1's Collection-valued map as 1.21.2's List-valued one
  TrimMaterial        armour overrides keyed by ArmorMaterial holder <-> by equipment model id

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
    json.dump(out, open(sys.argv[1], "w", encoding="utf-8", newline="\n"), indent=1)


if __name__ == "__main__":
    main()
