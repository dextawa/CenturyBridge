"""Wiring for 1.21.1's Registry surface on 1.21.2 (s7item, step 1.21.1 -> 1.21.2).

1.21.2 folded Registry into HolderLookup.RegistryLookup (see CbItemRegistries for the semantics).
Every 1.21.1 member of Registry, RegistryAccess, HolderGetter.Provider and HolderLookup.Provider that
1.21.2 no longer links is handled on each usage:

  member living on under another 1.21.2 member of the same meaning ("MOVED"):
    call       instanceRedirects -> CbItemRegistries.<old name>(receiver, args) calling the 1.21.2 member;
    override   overrideHooks: the mod class gets the 1.21.2 member forwarding to its 1.21.1 method; the
               method's super call becomes super's 1.21.2 member (superCalls rewrite);
  member that is gone ("GONE"):
    call       instanceRedirects -> CbItemRegistries.<old name>: the receiver's own override when its class
               has one (adapterHooks give such a class the Legacy* interface), else 1.21.1's body on 1.21.2;
    super call superRedirects -> CbItemRegistries.super_<old name>: 1.21.1's body;
  1.21.2 abstract member with no 1.21.1 counterpart ("FILLS"): abstractFills.
  Registry.getTags() (pairs -> sets, generic drift): a same-shape callReshape converting the stream back to
  pairs, and a convertHooks bridge for overrides; RegistryAccess.lookup(key) overrides likewise.
  HolderGetter.Provider lambdas (SAM lookup under its 1.21.1 intermediary name): lambdaAdapters.

  python gen_s7item_registries.py <out fragment.json>
"""
import json
import sys

RT = "top/dext/centurybridge/step/v1_21_2/rt/CbItemRegistries"
GUARD = "top/dext/centurybridge/step/v1_21_2/rt/Guard"
R = "net/minecraft/"
REG = R + "class_2378"
ACCESS = R + "class_5455"
GETTERS = R + "class_7871$class_7872"
PROVIDER = R + "class_7225$class_7874"

# (owner, 1.21.1 member, 1.21.2 member, basis)
MOVED = [
    (REG, "method_10223(Lnet/minecraft/class_2960;)Ljava/lang/Object;", "method_63535(Lnet/minecraft/class_2960;)Ljava/lang/Object;",
     "get(id): the value -- 1.21.2's getValue(id), its get(id) answering the holder"),
    (REG, "method_30517()Lnet/minecraft/class_5321;", "method_46765()Lnet/minecraft/class_5321;", "key(): RegistryLookup.key()"),
    (REG, "method_31138()Lcom/mojang/serialization/Lifecycle;", "method_46766()Lcom/mojang/serialization/Lifecycle;",
     "registryLifecycle(): RegistryLookup.registryLifecycle()"),
    (REG, "method_40264(Lnet/minecraft/class_5321;)Ljava/util/Optional;", "method_46746(Lnet/minecraft/class_5321;)Ljava/util/Optional;",
     "getHolder(key): get(key)"),
    (REG, "method_55841(Lnet/minecraft/class_2960;)Ljava/util/Optional;", "method_10223(Lnet/minecraft/class_2960;)Ljava/util/Optional;",
     "getHolder(id): get(id)"),
    (REG, "method_40270()Ljava/util/stream/Stream;", "method_42017()Ljava/util/stream/Stream;", "holders(): listElements()"),
    (REG, "method_40266(Lnet/minecraft/class_6862;)Ljava/util/Optional;", "method_46733(Lnet/minecraft/class_6862;)Ljava/util/Optional;",
     "getTag(tag): get(tag)"),
    (REG, "method_40273()Ljava/util/stream/Stream;", "method_46755()Ljava/util/stream/Stream;", "getTagNames(): listTagIds()"),
    (ACCESS, "method_33310(Lnet/minecraft/class_5321;)Ljava/util/Optional;", "method_46759(Lnet/minecraft/class_5321;)Ljava/util/Optional;",
     "RegistryAccess.registry(key): lookup(key), Optional<Registry<E>> in 1.21.2"),
    (ACCESS, "method_41201()Lcom/mojang/serialization/Lifecycle;", "method_62679()Lcom/mojang/serialization/Lifecycle;",
     "RegistryAccess.allRegistriesLifecycle(): HolderLookup.Provider.allRegistriesLifecycle(), the same reduction"),
    (GETTERS, "method_46750(Lnet/minecraft/class_5321;)Ljava/util/Optional;", "method_46759(Lnet/minecraft/class_5321;)Ljava/util/Optional;",
     "HolderGetter.Provider.lookup(key): the same method under 1.21.2's name"),
]
# (owner, 1.21.1 member, Legacy interface, basis)
GONE = [
    (REG, "method_40260(Lnet/minecraft/class_6862;)Lnet/minecraft/class_6885$class_6888;", "LegacyGetOrCreateTag",
     "getOrCreateTag(tag): the bound set, the registration set before freezing, else an empty named set"),
    (REG, "method_40257(Ljava/util/Map;)V", "LegacyBindTags",
     "bindTags(map): 1.21.2's prepareTagReload(new LoadResult(key, map)).apply(); bindTag per tag before freezing"),
    (REG, "method_40278()V", "LegacyResetTags", "resetTags(): every known tag bound to nothing"),
    (REG, "method_46770()Lnet/minecraft/class_7876;", "LegacyHolderOwner", "holderOwner(): the registry owns its holders"),
    (REG, "method_46771()Lnet/minecraft/class_7225$class_7226;", "LegacyAsLookup", "asLookup(): the registry is its lookup"),
    (REG, "method_46772()Lnet/minecraft/class_7225$class_7226;", "LegacyAsTagAddingLookup",
     "asTagAddingLookup(): a lookup whose tag lookups create missing tags"),
    (REG, "method_40290(Lnet/minecraft/class_5321;)Lnet/minecraft/class_6880$class_6883;", "LegacyGetHolderOrThrow",
     "getHolderOrThrow(key): get(key) or 1.21.1's IllegalStateException"),
    (GETTERS, "method_58561(Lnet/minecraft/class_5321;Lnet/minecraft/class_5321;)Ljava/util/Optional;", "LegacyProviderGet",
     "HolderGetter.Provider.get(registry, key): lookup(registry).flatMap(get(key))"),
    (PROVIDER, "method_46758()Lnet/minecraft/class_7871$class_7872;", "LegacyAsGetterLookup",
     "HolderLookup.Provider.asGetterLookup(): the provider, a getter provider in 1.21.2"),
]
# (under, 1.21.2 member, static, basis)
FILLS = [
    (REG, "method_62683(Lnet/minecraft/class_3503$class_6863;)Lnet/minecraft/class_2378$class_10106;", "fill_method_62683",
     "prepareTagReload(result): the class's 1.21.1 bindTags(map) applied when 1.21.2 applies the result"),
    (R + "class_2385", "method_62681(Lnet/minecraft/class_6862;Ljava/util/List;)V", "fill_method_62681",
     "WritableRegistry.bindTag(tag, holders): the class's 1.21.1 bindTags of that tag"),
    (R + "class_6885", "method_62680()Z", "fill_method_62680", "HolderSet.isBound(): a 1.21.1 holder set always had its contents"),
    (REG, "method_42020()Ljava/util/stream/Stream;", "fill_method_42020", "listTags(): the sets of the class's getTags()"),
]


def desc(member):
    return member[member.index("("):]


def name(member):
    return member[:member.index("(")]


def arity(member):
    d = desc(member)
    args, i = 0, 1
    while d[i] != ")":
        while d[i] == "[":
            i += 1
        i = d.index(";", i) + 1 if d[i] == "L" else i + 1
        args += 1
    return args


def receiver_desc(owner, member):
    return "(L" + owner + ";" + desc(member)[1:]


def main():
    out = {"instanceRedirects": {}, "superRedirects": {}, "overrideHooks": [], "adapterHooks": [], "abstractFills": [],
           "callReshapes": {}, "convertHooks": [], "lambdaAdapters": {}}
    for owner, old, new, why in MOVED:
        out["instanceRedirects"][owner + "." + old] = RT
        out["overrideHooks"].append({"old": old, "new": new, "onlyUnder": owner, "args": list(range(arity(old))),
                                     "frames": GUARD, "superCalls": "rewrite",
                                     "basis": "a 1.21.1 override of " + why + ": the class answers the 1.21.2 member, its super "
                                              "call reaching super's"})
    for owner, old, legacy, why in GONE:
        out["instanceRedirects"][owner + "." + old] = RT
        out["superRedirects"][owner + "." + old] = RT + "#super_" + name(old)
        out["adapterHooks"].append({"under": owner, "adapter": RT + "$" + legacy, "requires": [old], "synth": [],
                                    "basis": "a 1.21.1 class overriding " + why + ": calls of the gone member reach it"})
    for under, member, static, why in FILLS:
        out["abstractFills"].append({"under": under, "method": member, "call": RT + "." + static + receiver_desc(under, member),
                                     "basis": "a 1.21.1 implementation: " + why})
    # a RegistryAccess implementation declaring registry(key) and lookup(key) both: calls of registry reach its own
    out["adapterHooks"].append({"under": ACCESS, "adapter": RT + "$LegacyRegistry",
                                "requires": ["method_33310(Lnet/minecraft/class_5321;)Ljava/util/Optional;"], "synth": [],
                                "basis": "a 1.21.1 RegistryAccess implementation's registry(key): calls of it reach it"})
    tags = "method_40272()Ljava/util/stream/Stream;"
    out["callReshapes"][REG + "." + tags] = {
        "desc": "()Ljava/util/stream/Stream;", "ops": [],
        "ret": "conv " + RT + "#tagPairs(Ljava/util/stream/Stream;)Ljava/util/stream/Stream;",
        "basis": "Registry.getTags(): 1.21.1's stream of (key, set) pairs, from 1.21.2's stream of sets"}
    out["convertHooks"].append({"under": REG, "member": tags,
                                "ret": RT + "#tagSets(Ljava/util/stream/Stream;)Ljava/util/stream/Stream;",
                                "basis": "a 1.21.1 getTags() override answers pairs; 1.21.2 reads the sets"})
    lookup = "method_46759(Lnet/minecraft/class_5321;)Ljava/util/Optional;"
    out["convertHooks"].append({"under": ACCESS, "member": lookup,
                                "ret": RT + "#registryOf(Ljava/lang/Object;Ljava/util/Optional;)Ljava/util/Optional;",
                                "basis": "RegistryAccess.lookup(key): a 1.21.1 override answered Optional<RegistryLookup>, "
                                         "1.21.2 reads Optional<Registry> (a lookup standing for its registry)"})
    out["lambdaAdapters"][GETTERS + ".method_46750(Lnet/minecraft/class_5321;)Ljava/util/Optional;"] = {
        "iface": RT + "$LegacyProviderLambda",
        "adapt": RT + "#provider(L" + RT + "$LegacyProviderLambda;)L" + GETTERS + ";",
        "basis": "a 1.21.1 HolderGetter.Provider lambda implements lookup(key) under its 1.21.1 name"}
    json.dump(out, open(sys.argv[1], "w", encoding="utf-8", newline="\n"), indent=1)


if __name__ == "__main__":
    main()
