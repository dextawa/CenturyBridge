"""Wiring for small 1.21.1 -> 1.21.2 changes in s7item's area (rt/CbItemLocks; CriteriaTriggers).

  LockCode          1.21.1's key-string lock on 1.21.2's item-predicate lock (new / key() / addToTag / fromTag)
  CriteriaTriggers  KILLED_BY_CROSSBOW is 1.21.2's KILLED_BY_ARROW (the same KilledByArrowTrigger)
  Direction / Shapes / AABB  fromDelta, getFaceShape (rt/CbItemGeometry); getNormal, getBottomCenter renamed

  python gen_s7item_misc.py <out fragment.json>
"""
import json
import sys

LOCKS = "top/dext/centurybridge/step/v1_21_2/rt/CbItemLocks"
LOCK = "net/minecraft/class_1273"


def main():
    out = {
        "ctorRedirects": {LOCK + ".<init>(Ljava/lang/String;)V": LOCKS + "#newLockCode"},
        "instanceRedirects": {
            LOCK + ".comp_2371()Ljava/lang/String;": LOCKS,
            LOCK + ".method_5474(Lnet/minecraft/class_2487;)V": LOCKS,
        },
        "staticRedirects": {LOCK + ".method_5473(Lnet/minecraft/class_2487;)Lnet/minecraft/class_1273;": LOCKS},
        "staticRedirects_geometry": None,
        "fieldRedirects": {"net/minecraft/class_174.field_1197:Lnet/minecraft/class_2076;": "net/minecraft/class_174#field_54206"},
    }
    out.pop("staticRedirects_geometry")
    GEO = "top/dext/centurybridge/step/v1_21_2/rt/CbItemGeometry"
    out["staticRedirects"]["net/minecraft/class_2350.method_50026(III)Lnet/minecraft/class_2350;"] = GEO
    out["staticRedirects"]["net/minecraft/class_259.method_16344(Lnet/minecraft/class_265;Lnet/minecraft/class_2350;)Lnet/minecraft/class_265;"] = GEO
    # renamed with the same body (javap): AABB.getBottomCenter, Direction.getNormal (getUnitVec3i)
    out["methodRenames"] = {
        "net/minecraft/class_238.method_61124()Lnet/minecraft/class_243;": "method_64033",
        "net/minecraft/class_2350.method_10163()Lnet/minecraft/class_2382;": "method_62675",
    }
    # a 1.21.1 AABB subclass overriding getBottomCenter: renamed to 1.21.2's name, so 1.21.2's calls reach it
    out["overrideRenames"] = [{"old": "method_61124()Lnet/minecraft/class_243;", "new": "method_64033",
                               "onlyUnder": "net/minecraft/class_238",
                               "basis": "AABB.getBottomCenter has 1.21.2's name method_64033 (same body): a subclass's override takes it"}]
    json.dump(out, open(sys.argv[1], "w", encoding="utf-8", newline="\n"), indent=1)


if __name__ == "__main__":
    main()
