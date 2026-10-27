"""mixinTargets of step 1.20.2 -> 1.20.3 (s203): `Main gen-mixin-targets` over V/W bytecode (a V method W no longer declares
with V's descriptor, the one W method of that name, arguments one for one by type), minus EXCLUDE (judged from both
bodies not to hold V's code for V's callers only).

  On beta2 (Java there only), in a checkout with the engine compiled and this fragment absent:
    zcat core/segments/damage-mixin/1.20.2__1.20.3.tsv.gz > dm.tsv
    java -cp core/build/classes:<asm+gson> top.dext.centurybridge.Main gen-mixin-targets \
        data/jars/client-1.20.2-intermediary-code.jar data/jars/client-1.20.3-intermediary-code.jar \
        core/segments/steps/1.20.2__1.20.3.json dm.tsv mt-gen.json
  Here, from the repository root:
    python3 core/tools/gen_s203_mixintargets.py mt-gen.json core/segments/steps/1.20.2__1.20.3.json.d/s203-mixintargets-gen.json

Every emitted entry was compared against both bodies (javap -c -p; D:/tmp/s203/cmp.py prints them side by side with the
number of call sites in the class). A changed return type is left to MixinBridge's plan (it refuses a handler that reads
or sets the return value across it). Regenerate, never hand-edit.
"""
import json
import sys

# V method -> why gen-mixin-targets' same-name pick is not taken (javap of both bodies)
EXCLUDE = {
    "net/minecraft/class_156.method_27959()Ljava/util/concurrent/ExecutorService;":
        "Util.makeIoExecutor() -> makeIoExecutor(String, boolean): 1.20.3 builds both the IO and the download executor "
        "with it (two call sites in Util.<clinit>, 1.20.2 one), so a 1.20.2 injector there would also fire for the "
        "download executor 1.20.2 did not have",
    "net/minecraft/class_7877$class_7883.method_46802()Lnet/minecraft/class_7225$class_7226;":
        "RegistrySetBuilder.RegistryContents.buildAsLookup(): 1.20.2 answered an anonymous lookup over the contents; "
        "1.20.3's buildAsLookup(OwnerLookup) builds the entry map and a lookup through RegistrySetBuilder.lookupFromMap "
        "(no instruction in common)",
}


def main():
    src, out = sys.argv[1], sys.argv[2]
    mt = json.load(open(src, encoding="utf-8"))["mixinTargets"]
    keep = {k: v for k, v in sorted(mt.items()) if k not in EXCLUDE}
    root = {"_s203_mixinTargets_basis": "gen_s203_mixintargets.py over Main gen-mixin-targets (V/W bytecode); regenerate, "
                                        "never hand-edit", "mixinTargets": keep}
    with open(out, "w", encoding="utf-8", newline="\n") as o:
        json.dump(root, o, indent=1)
        o.write("\n")
    print(len(keep), "of", len(mt), "entries ->", out)


if __name__ == "__main__":
    main()
