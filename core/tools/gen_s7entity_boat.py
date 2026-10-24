"""gen_s7entity_boat.py: the boat family of climb step 1.21.1 -> 1.21.2 (s7entity).

  python gen_s7entity_boat.py <V code jar: client-1.21.1-intermediary-code.jar> <W code jar: client-1.21.2-intermediary-code.jar>

Writes core/segments/steps/1.21.1__1.21.2.json.d/s7entity-boat-gen.json.

1.21.2 split Boat into AbstractBoat (class_10255) and the wood-per-EntityType Boat / Raft (ChestBoat over
AbstractChestBoat, class_10256). Boat's members moved up to AbstractBoat and took new intermediary names. A
1.21.1 mod's Boat still extends 1.21.2's Boat, which extends AbstractBoat, so a call naming Boat resolves to the
moved member by its new name (methodRenames / fieldRenames), and a mod's override of the old name is the
override of the new one (overrideRenames, its super calls following). Boat.Status is AbstractBoat.Status, same
constants under the same intermediary names (classRenames).

1.21.1's Boat was every boat (rafts and chest boats too); 1.21.2's Boat is one kind of AbstractBoat, and
ChestBoat no longer extends it. So a converted mod names AbstractBoat / AbstractChestBoat where it named Boat /
ChestBoat (classRenames: instanceof, casts, descriptors and a mod's superclass all keep 1.21.1's meaning); its
`new Boat(..)` / `new ChestBoat(..)` builds 1.21.2's Boat / ChestBoat (ctorRedirects), its super(..) the abstract
class's constructor (superCtorReshapes), and AbstractBoat's abstract rideHeight gets 1.21.1's passenger height
(abstractFills). The members that died -- the variant, the overridable getDropItem / getPickResult, Boat.Type
(facade/BoatType), EntityType.BOAT / CHEST_BOAT -- are rt/CbEntityBoats' (its javadoc has the semantics).

The pairs below are the 1.21.1 / 1.21.2 members of the same mojang name; the generator holds each pair to the
same descriptor, the same access kind and an instruction-for-instruction identical body (javap -c opcodes), and
each constant to the same value, and refuses what does not hold.
"""
import json
import os
import re
import subprocess
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.dirname(os.path.dirname(HERE))
FRAGMENT = os.path.join(ROOT, "core/segments/steps/1.21.1__1.21.2.json.d/s7entity-boat-gen.json")
MC = "net/minecraft/"
BOAT = "class_1690"
ABSTRACT = "class_10255"
CHEST = "class_7264"
ABSTRACT_CHEST = "class_10256"

# (V owner, V name, W owner, W name, descriptor, mojang name, overridable)
METHODS = [
    (BOAT, "method_30959", ABSTRACT, "method_64486", "(Lnet/minecraft/class_1297;Lnet/minecraft/class_1297;)Z", "canVehicleCollide", False),
    (BOAT, "method_7537", ABSTRACT, "method_64494", "()Lnet/minecraft/class_3414;", "getPaddleSound", True),
    (BOAT, "method_7538", ABSTRACT, "method_64490", "(ZZ)V", "setPaddleState", True),
    (BOAT, "method_7551", ABSTRACT, "method_64485", "(IF)F", "getRowingTime", True),
    (BOAT, "method_7544", ABSTRACT, "method_64495", "()F", "getWaterLevelAbove", True),
    (BOAT, "method_7548", ABSTRACT, "method_64497", "()F", "getGroundFriction", True),
    (BOAT, "method_42280", ABSTRACT, "method_64499", "()F", "getSinglePassengerXOffset", True),
    (BOAT, "method_49182", ABSTRACT, "method_64488", "(Lnet/minecraft/class_1297;)Z", "hasEnoughSpaceFor", True),
    (BOAT, "method_7546", ABSTRACT, "method_64492", "(Lnet/minecraft/class_1297;)V", "clampRotation", True),
    (BOAT, "method_42281", ABSTRACT, "method_64500", "()I", "getMaxPassengers", True),
    (BOAT, "method_7535", ABSTRACT, "method_64487", "(ZZZZ)V", "setInput", True),
    (BOAT, "method_7556", ABSTRACT, "method_64491", "(I)Z", "getPaddleState", True),
    (BOAT, "method_7547", ABSTRACT, "method_64484", "(F)F", "getBubbleAngle", True),
    (CHEST, "method_42280", ABSTRACT_CHEST, "method_64499", "()F", "getSinglePassengerXOffset", True),
    (CHEST, "method_42281", ABSTRACT_CHEST, "method_64500", "()I", "getMaxPassengers", True),
    (CHEST, "method_42282", ABSTRACT_CHEST, "method_64501", "(Lnet/minecraft/class_1657;)V", "unpackLootTable", True),
]
RT = "top/dext/centurybridge/step/v1_21_2/rt/CbEntityBoats"
TYPE = "top/dext/centurybridge/step/v1_21_2/facade/BoatType"
ENTITY_TYPE = "Lnet/minecraft/class_1299;"
LEVEL = "Lnet/minecraft/class_1937;"
ITEM = "Lnet/minecraft/class_1792;"
STACK = "Lnet/minecraft/class_1799;"
# (V name, W name, descriptor, mojang name): Boat's public constants, now AbstractBoat's
FIELDS = [
    ("field_30697", "field_54427", "I", "PADDLE_LEFT"),
    ("field_30698", "field_54445", "I", "PADDLE_RIGHT"),
    ("field_30699", "field_54446", "D", "PADDLE_SOUND_TIME"),
    ("field_30700", "field_54447", "I", "BUBBLE_TIME"),
]


def javap(jar, cls, *flags):
    return subprocess.run(["javap", "-p", "-s", *flags, "-cp", jar, "net.minecraft." + cls],
                          capture_output=True, text=True, check=True).stdout.splitlines()


def method(jar, cls, name, desc):
    """(access line, opcode list) of a declared method, or None"""
    out = javap(jar, cls, "-c")
    for i, line in enumerate(out):
        if re.search(r"\b" + re.escape(name) + r"\(", line) and i + 1 < len(out) and out[i + 1].strip() == "descriptor: " + desc:
            ops = []
            for k in range(i + 2, len(out)):
                s = out[k].strip()
                if not s:
                    break
                m = re.match(r"\d+: (\w+)", s)
                if m:
                    ops.append(m.group(1))
            return line.strip(), ops
    return None


def constant(jar, cls, name):
    for line in javap(jar, cls, "-constants"):
        if re.search(r"\b" + name + r"\b", line) and "=" in line:
            return line.split("=", 1)[1].strip().rstrip(";")
    return None


def kind(access):
    return ("static" if " static " in " " + access else "instance",
            "public" if access.startswith("public") else "protected" if access.startswith("protected") else "other")


def main():
    vjar, wjar = sys.argv[1], sys.argv[2]
    frag = {"classRenames": {MC + BOAT: MC + ABSTRACT, MC + CHEST: MC + ABSTRACT_CHEST,
                             MC + BOAT + "$class_1691": MC + ABSTRACT + "$class_1691", MC + BOAT + "$class_1692": TYPE},
            "methodRenames": {}, "overrideRenames": [], "fieldGetters": {}, "instanceRedirects": {},
            "superRedirects": {}, "adapterHooks": [], "ctorRedirects": {}, "superCtorReshapes": {}, "abstractFills": []}
    for vo, vn, wo, wn, desc, moj, over in METHODS:
        a = method(vjar, vo, vn, desc)
        b = method(wjar, wo, wn, desc)
        assert a and b, f"{moj}: {vo}.{vn} or {wo}.{wn}{desc} missing"
        assert kind(a[0]) == kind(b[0]), f"{moj}: {a[0]} / {b[0]}"
        assert a[1] == b[1], f"{moj}: bodies differ"
        assert (" final " not in " " + b[0]) == over or not over, f"{moj}: final on 1.21.2"
        frag["methodRenames"][f"{MC}{vo}.{vn}{desc}"] = wn
        if over:
            frag["overrideRenames"].append({
                "old": vn + desc, "new": wn, "onlyUnder": MC + vo, "superCalls": "rename",
                "basis": f"1.21.1 {vo == BOAT and 'Boat' or 'ChestBoat'}.{moj} is 1.21.2 "
                         f"{wo == ABSTRACT and 'AbstractBoat' or 'AbstractChestBoat'}.{moj} (moved up, renamed; same body)"})
    for vn, wn, desc, moj in FIELDS:
        a, b = constant(vjar, BOAT, vn), constant(wjar, ABSTRACT, wn)
        assert a is not None and a == b, f"{moj}: {a} / {b}"
        frag["fieldGetters"][f"{MC}{BOAT}.{vn}:{desc}"] = RT
    # EntityType.BOAT / CHEST_BOAT
    frag["fieldGetters"][MC + "class_1299.field_6121:" + ENTITY_TYPE] = RT
    frag["fieldGetters"][MC + "class_1299.field_38096:" + ENTITY_TYPE] = RT
    # the variant
    vt = "L" + MC + BOAT + "$class_1692;"
    frag["instanceRedirects"][f"{MC}{BOAT}.method_47885(){vt}"] = RT + "#getVariant"
    frag["instanceRedirects"][f"{MC}{BOAT}.method_47884({vt})V"] = RT + "#setVariant"
    frag["superRedirects"][f"{MC}{BOAT}.method_47885(){vt}"] = RT + "#superGetVariant"
    frag["superRedirects"][f"{MC}{BOAT}.method_47884({vt})V"] = RT + "#superSetVariant"
    frag["adapterHooks"].append({"under": MC + BOAT, "adapter": RT + "$LegacySetVariant", "requires": [f"method_47884({vt})V"],
                                 "synth": [], "basis": "a mod boat's own 1.21.1 setVariant answers the calls CbEntityBoats serves"})
    # ChestBoat's own members a call names it by (the renamed class answers only what it declared)
    frag["instanceRedirects"][f"{MC}{CHEST}.method_7557(){ITEM}"] = RT + "#getDropItem"
    frag["instanceRedirects"][f"{MC}{CHEST}.method_7516(Lnet/minecraft/class_1282;)V"] = RT + "#method_7516"
    frag["adapterHooks"].append({"under": MC + BOAT, "adapter": RT + "$LegacyVariant", "requires": [f"method_47885(){vt}"],
                                 "synth": [], "basis": "a mod boat's own 1.21.1 getVariant answers the calls CbEntityBoats serves"})
    # getDropItem / getPickResult: final on 1.21.2's AbstractBoat
    frag["instanceRedirects"][f"{MC}{BOAT}.method_7557(){ITEM}"] = RT + "#getDropItem"
    frag["superRedirects"][f"{MC}{BOAT}.method_7557(){ITEM}"] = RT + "#superGetDropItem"
    frag["superRedirects"][f"{MC}{CHEST}.method_7557(){ITEM}"] = RT + "#superGetDropItem"
    frag["superRedirects"][f"{MC}{BOAT}.method_31480(){STACK}"] = RT + "#superGetPickResult"
    for name, desc, legacy, what in (("method_7557", "()" + ITEM, "LegacyDrop", "getDropItem"),
                                     ("method_31480", "()" + STACK, "LegacyPick", "getPickResult")):
        frag["overrideRenames"].append({
            "old": name + desc, "new": "cb$" + name, "onlyUnder": MC + BOAT, "adapter": RT + "$" + legacy,
            "basis": f"1.21.2's AbstractBoat.{what} is final: the mod's 1.21.1 override is renamed and answers through "
                     f"CbEntityBoats ({'the drop supplier its 1.21.1 constructor got' if what == 'getDropItem' else 'CbEntityBoatMixin on getPickResult'})"})
    # constructors
    sup = "Ljava/util/function/Supplier;"
    for owner, abstract, newf, oak in ((BOAT, ABSTRACT, "newBoat", "oakBoat"), (CHEST, ABSTRACT_CHEST, "newChestBoat", "oakChestBoat")):
        k1 = f"{MC}{owner}.<init>({ENTITY_TYPE}{LEVEL})V"
        k2 = f"{MC}{owner}.<init>({LEVEL}DDD)V"
        frag["ctorRedirects"][k1] = RT + "#" + newf
        frag["ctorRedirects"][k2] = RT + "#" + newf
        frag["superCtorReshapes"][k1] = {
            "desc": f"({ENTITY_TYPE}{LEVEL}{sup})V", "ops": ["keep", "keep", f"push {RT}#noDrop(){sup}"],
            "post": f"{RT}#built(L{MC}{owner};)V", "postArgs": [],
            "basis": "1.21.2's constructor takes the drop supplier: the boat's own 1.21.1 getDropItem (CbEntityBoats#built)"}
        frag["superCtorReshapes"][k2] = {
            "desc": f"({ENTITY_TYPE}{LEVEL}{sup})V",
            "ops": [f"push {RT}#{oak}(){ENTITY_TYPE}", "keep:0", f"push {RT}#noDrop(){sup}"],
            "post": f"{RT}#at(L{MC}{owner};DDD)V", "postArgs": [1, 2, 3],
            "basis": "1.21.1's (level, x, y, z) constructor was this(EntityType.BOAT / CHEST_BOAT, level) at x, y, z "
                     "(1.21.2: the OAK type, 1.21.1's starting variant; setInitialPos, same body)"}
    frag["abstractFills"].append({
        "under": MC + BOAT, "method": "method_64489(Lnet/minecraft/class_4048;)D",
        "call": f"{RT}.rideHeight(L{MC}{BOAT};Lnet/minecraft/class_4048;)D",
        "basis": "1.21.1 Boat's passenger height: dimensions.height / 3, a raft's (x 0.8888889) for BAMBOO"})
    # Boat.Status: the same enum, moved
    vs = [l for l in javap(vjar, BOAT + "$class_1691") if "static final" in l]
    ws = [l.replace(ABSTRACT, BOAT) for l in javap(wjar, ABSTRACT + "$class_1691") if "static final" in l]
    assert vs == ws, "Boat.Status constants differ"
    with open(FRAGMENT, "w", encoding="utf-8", newline="\n") as f:
        f.write(json.dumps(frag, indent=1, ensure_ascii=False) + "\n")
    print(f"methods {len(frag['methodRenames'])}, overrides {len(frag['overrideRenames'])}, fields {len(frag['fieldGetters'])}")


if __name__ == "__main__":
    main()
