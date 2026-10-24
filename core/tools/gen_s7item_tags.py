"""Wiring for 1.21.1's tag loading API on 1.21.2 (s7item, step 1.21.1 -> 1.21.2; rt/CbItemTags and the facades
TagManager, TagOutput).

  TagManager (+ LoadResult)       gone (tags load with the registries) -> facade, 1.21.1's reload on 1.21.2's TagLoader
  TagLoader(Function, dir)        the function as 1.21.2's element lookup (superCtorReshapes, new and super)
  TagLoader.loadAndBuild          build(load(..)); a subclass's override reached (adapterHooks)
  TagEntry.Lookup.element(id)     element(id, not required); a 1.21.1 implementation reached by 1.21.2's shape
  NetworkPayload size / applyToRegistry, TagOutput

  python gen_s7item_tags.py <out fragment.json>
"""
import json
import sys

RT = "top/dext/centurybridge/step/v1_21_2/rt/CbItemTags"
FAC = "top/dext/centurybridge/step/v1_21_2/facade/"
LOADER = "net/minecraft/class_3503"
LOOKUP = "net/minecraft/class_3497$class_7474"
PAYLOAD = "net/minecraft/class_6864$class_5748"
LOAD = "method_33176(Lnet/minecraft/class_3300;)Ljava/util/Map;"


def main():
    out = {
        "classRenames": {
            "net/minecraft/class_3505": FAC + "TagManager",
            "net/minecraft/class_3505$class_6863": FAC + "TagManager$LoadResult",
            "net/minecraft/class_6864$class_6865": FAC + "TagOutput",
        },
        "superCtorReshapes": {
            LOADER + ".<init>(Ljava/util/function/Function;Ljava/lang/String;)V": {
                "desc": "(Lnet/minecraft/class_3503$class_10272;Ljava/lang/String;)V",
                "ops": ["conv " + RT + "#lookup(Ljava/util/function/Function;)Lnet/minecraft/class_3503$class_10272;", "keep"]},
        },
        "instanceRedirects": {
            LOADER + "." + LOAD: RT,
            PAYLOAD + ".method_56531()I": RT,
            PAYLOAD + ".method_56532(Lnet/minecraft/class_2378;)V": RT,
            # an instance redirect, not interfaceCallRedirects: a call through a class implementing the lookup
            # (INVOKEVIRTUAL, the method inherited) resolves to the interface key as well (Chain.instanceRedirectHome)
            LOOKUP + ".method_43948(Lnet/minecraft/class_2960;)Ljava/lang/Object;": RT,
        },
        "superRedirects": {LOADER + "." + LOAD: RT + "#super_method_33176"},
        "adapterHooks": [{"under": LOADER, "adapter": RT + "$LegacyLoadAndBuild", "requires": [LOAD], "synth": [],
                          "basis": "a 1.21.1 TagLoader subclass overriding loadAndBuild: calls of it reach the override"}],
        "overrideHooks": [{
            "old": "method_43948(Lnet/minecraft/class_2960;)Ljava/lang/Object;",
            "new": "method_43948(Lnet/minecraft/class_2960;Z)Ljava/lang/Object;",
            "onlyUnder": LOOKUP, "args": [0],
            "basis": "TagEntry.Lookup.element is also told whether the entry is required since 1.21.2: a 1.21.1 lookup "
                     "is asked with the id, as 1.21.1 asked it (TagEntry decided on required itself)"}],
    }
    json.dump(out, open(sys.argv[1], "w", encoding="utf-8", newline="\n"), indent=1)


if __name__ == "__main__":
    main()
