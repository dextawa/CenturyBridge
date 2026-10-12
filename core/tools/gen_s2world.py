"""gen_s2world.py: the s2world wiring fragment of climb step 1.20.2 -> 1.20.3 (owners under net/minecraft/world/).

  python gen_s2world.py <V code dump-free: 1.20.2 stub jar> <out fragment.json>

Hand-decided bridges (each with its basis, read off the 1.20.2 and 1.20.3 bytecode) plus the parts that
are one decision applied to many keys: a renamed method is renamed at every vanilla owner a 1.20.2 mod
can call it through (methodRenames match the call site's owner exactly), so those owners are read off
the 1.20.2 stub's class hierarchy rather than listed by hand.
"""
import json
import os
import sys
import zipfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import classfile  # noqa: E402

STEP = "top/dext/centurybridge/step/v1_20_3"
RT = STEP + "/rt"
FAC = STEP + "/facade"
MC = "net/minecraft/"


def L(c):
    return "L" + (c if "/" in c else MC + c) + ";"


def load_supers(jar):
    sup = {}
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if n.startswith("net/minecraft/") and n.endswith(".class"):
                c = classfile.read(z.read(n))
                if c:
                    sup[c.name] = c.super
    return sup


def with_subclasses(sup, root):
    out = [root]
    for c in sorted(sup):
        x = sup.get(c)
        while x:
            if x == root:
                out.append(c)
                break
            x = sup.get(x)
    return out


def main():
    stub, out = sys.argv[1], sys.argv[2]
    sup = load_supers(stub)
    w = {
        "fieldRenames": {}, "methodRenames": {}, "classRenames": {}, "callReshapes": {},
        "superCtorReshapes": {}, "staticRedirects": {}, "instanceRedirects": {}, "fieldRedirects": {},
        "instanceFieldGetters": {}, "covers": [], "overrideHooks": [], "ledger": {},
    }

    # ---------------------------------------------------------------- renames
    for old, new, what in [
        ("field_10261", "field_46282", "PUMPKIN"), ("field_10545", "field_46283", "MELON"),
        ("field_10331", "field_46284", "ATTACHED_PUMPKIN_STEM"), ("field_10150", "field_46285", "ATTACHED_MELON_STEM"),
        ("field_9984", "field_46286", "PUMPKIN_STEM"), ("field_10168", "field_46287", "MELON_STEM"),
    ]:
        w["fieldRenames"][MC + "class_2246." + old + ":Lnet/minecraft/class_2248;"] = new
    for old, new in [("field_8706", "field_46249"), ("field_8188", "field_46250")]:  # PUMPKIN_SEEDS, MELON_SEEDS
        w["fieldRenames"][MC + "class_1802." + old + ":Lnet/minecraft/class_1792;"] = new
    # TorchBlock.AABB / AABB_STANDING_OFFSET moved up to the new BaseTorchBlock (same values); a GETSTATIC
    # through TorchBlock resolves the superclass field
    w["fieldRenames"][MC + "class_2527.field_11618:Lnet/minecraft/class_265;"] = "field_46271"
    w["fieldRenames"][MC + "class_2527.field_31265:I"] = "field_46270"

    # PressurePlateBlock.Sensitivity moved to BlockSetType.PressurePlateSensitivity: same constants, same members
    w["classRenames"][MC + "class_2440$class_2441"] = MC + "class_8177$class_2441"
    for dead, fac in [
        ("class_4262", "AbstractGlassBlock"), ("class_2368", "GlassBlock"), ("class_2375", "GravelBlock"),
        ("class_2468", "SandBlock"), ("class_2511", "StemGrownBlock"), ("class_2411", "MelonBlock"),
        ("class_6377", "PowderSnowCauldronBlock"), ("class_2647", "AbstractTreeGrower"),
        ("class_2650", "AbstractMegaTreeGrower"), ("class_2662", "OakTreeGrower"), ("class_2652", "BirchTreeGrower"),
        ("class_2659", "SpruceTreeGrower"), ("class_2655", "JungleTreeGrower"), ("class_2654", "AcaciaTreeGrower"),
        ("class_2657", "DarkOakTreeGrower"), ("class_8175", "CherryTreeGrower"), ("class_6349", "AzaleaTreeGrower"),
        ("class_7382", "MangroveTreeGrower"),
    ]:
        w["classRenames"][MC + dead] = FAC + "/" + fac

    # vehicle damage/hurt state moved to the new VehicleEntity under new names (same synced data, same code)
    boat = [("method_7542", "(F)V", "method_54297"), ("method_7554", "()F", "method_54294"),
            ("method_7553", "(I)V", "method_54299"), ("method_7533", "()I", "method_54295"),
            ("method_7540", "(I)V", "method_54300"), ("method_7543", "()I", "method_54296"),
            ("method_42279", "(Lnet/minecraft/class_1282;)V", "method_7516")]
    cart = [("method_7520", "(F)V", "method_54297"), ("method_7521", "()F", "method_54294"),
            ("method_7509", "(I)V", "method_54299"), ("method_7507", "()I", "method_54295"),
            ("method_7524", "(I)V", "method_54300"), ("method_7522", "()I", "method_54296"),
            ("method_42670", "()Lnet/minecraft/class_1792;", "method_7557")]
    for root, table in [(MC + "class_1690", boat), (MC + "class_1688", cart)]:
        for owner in with_subclasses(sup, root):
            for old, desc, new in table:
                w["methodRenames"][owner + "." + old + desc] = new
        for old, desc, new in table:
            w["overrideHooks"].append({
                "old": old + desc, "new": new + desc, "onlyUnder": root,
                "basis": "1.20.3 moved the method to VehicleEntity under a new name; same arguments, same body"})
    # Level.explode(.., ExplosionInteraction) without particles: 1.20.3's method_55117 is exactly that shape
    for owner in with_subclasses(sup, MC + "class_1937"):
        w["methodRenames"][owner + ".method_8454(Lnet/minecraft/class_1297;Lnet/minecraft/class_1282;Lnet/minecraft/class_5362;DDDFZLnet/minecraft/class_1937$class_7867;)Lnet/minecraft/class_1927;"] = "method_55117"

    # ---------------------------------------------------------------- call reshapes
    def reshape(key, desc, ops, basis, ret=None):
        e = {"desc": desc, "ops": ops, "basis": basis}
        if ret:
            e["ret"] = ret
        w["callReshapes"][key] = e

    reshape(MC + "class_2248.method_9574(Lnet/minecraft/class_1922;Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;)Lnet/minecraft/class_1799;",
            "(Lnet/minecraft/class_4538;Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;)Lnet/minecraft/class_1799;",
            ["conv:0 " + RT + "/CbWorldBlocks#levelReader(Lnet/minecraft/class_1922;)Lnet/minecraft/class_4538;", "keep:1", "keep:2"],
            "the world narrowed to a LevelReader: Levels pass as they are, other BlockGetters as a LevelReader view (CbWorldBlocks.levelReader)")
    tick = "push " + RT + "/CbWorldItems#legacyTickRate()F"
    reshape(MC + "class_1292.method_5577(Lnet/minecraft/class_1293;F)Lnet/minecraft/class_2561;",
            "(Lnet/minecraft/class_1293;FF)Lnet/minecraft/class_2561;", ["keep:0", "keep:1", tick],
            "new tick-rate argument: 1.20.2 counted 20 ticks to the second")
    reshape(MC + "class_1844.method_47372(Lnet/minecraft/class_1799;Ljava/util/List;F)V",
            "(Lnet/minecraft/class_1799;Ljava/util/List;FF)V", ["keep:0", "keep:1", "keep:2", tick],
            "new tick-rate argument: 1.20.2 counted 20 ticks to the second")
    reshape(MC + "class_1844.method_8065(Ljava/util/List;Ljava/util/List;F)V",
            "(Ljava/util/List;Ljava/util/List;FF)V", ["keep:0", "keep:1", "keep:2", tick],
            "new tick-rate argument: 1.20.2 counted 20 ticks to the second")
    reshape(MC + "class_1937.method_46668(Lnet/minecraft/class_1297;Lnet/minecraft/class_1282;Lnet/minecraft/class_5362;DDDFZLnet/minecraft/class_1937$class_7867;Z)Lnet/minecraft/class_1927;",
            "(Lnet/minecraft/class_1297;Lnet/minecraft/class_1282;Lnet/minecraft/class_5362;DDDFZLnet/minecraft/class_1937$class_7867;ZLnet/minecraft/class_2394;Lnet/minecraft/class_2394;Lnet/minecraft/class_3414;)Lnet/minecraft/class_1927;",
            ["keep"] * 10 + ["push " + RT + "/CbWorldLevels#explosionSmallParticles()Lnet/minecraft/class_2394;",
                             "push " + RT + "/CbWorldLevels#explosionLargeParticles()Lnet/minecraft/class_2394;",
                             "push " + RT + "/CbWorldLevels#explosionSound()Lnet/minecraft/class_3414;"],
            "particles and sound became arguments: 1.20.2 always used EXPLOSION / EXPLOSION_EMITTER / GENERIC_EXPLODE")
    reshape(MC + "class_1657.method_7335([Lnet/minecraft/class_2960;)V", "(Ljava/util/List;)V",
            ["conv:0 " + RT + "/CbWorldEntities#recipeList([Lnet/minecraft/class_2960;)Ljava/util/List;"],
            "the recipe ids come as a List: the same ids, in order")
    reshape(MC + "class_1297.method_5659()Z", "(Lnet/minecraft/class_1927;)Z",
            ["push " + RT + "/CbWorldEntities#noExplosion()Lnet/minecraft/class_1927;"],
            "a 1.20.2 caller asks without an explosion; every 1.20.3 implementation ignores the argument")

    # ---------------------------------------------------------------- statics and instance redirects
    w["staticRedirects"][MC + "class_1688.method_7523(Lnet/minecraft/class_1937;DDDLnet/minecraft/class_1688$class_1689;)Lnet/minecraft/class_1688;"] = RT + "/CbWorldEntities#createMinecart"
    w["staticRedirects"][MC + "class_2246.method_52571(Lnet/minecraft/class_2960;Lnet/minecraft/class_2248;)Lnet/minecraft/class_2248;"] = RT + "/CbWorldBlocks#register"
    w["instanceRedirects"][MC + "class_2283.method_9759(Lnet/minecraft/class_1922;Lnet/minecraft/class_2338;)Lnet/minecraft/class_2680;"] = RT + "/CbWorldBlocks#getStateForPlacement"
    w["instanceRedirects"][MC + "class_2537.method_10776(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;ZZILnet/minecraft/class_2680;)V"] = RT + "/CbWorldBlocks#calculateState"
    w["instanceRedirects"][MC + "class_1542.method_6981(Ljava/util/UUID;)V"] = RT + "/CbWorldEntities#setThrower"
    w["instanceRedirects"][MC + "class_1917.method_8283(Lnet/minecraft/class_1937;Lnet/minecraft/class_5819;Lnet/minecraft/class_2338;)Lnet/minecraft/class_1297;"] = RT + "/CbWorldEntities#getOrCreateDisplayEntity"

    # ---------------------------------------------------------------- constructors
    P = "Lnet/minecraft/class_4970$class_2251;"

    def ctor(key, desc, ops, basis):
        w["superCtorReshapes"][key] = {"desc": desc, "ops": ops, "basis": basis}

    imap = "conv:1 " + RT + "/CbWorldBlocks#interactionMap(Ljava/util/Map;)Lnet/minecraft/class_5620$class_8821;"
    ctor(MC + "class_2275.<init>(" + P + "Ljava/util/Map;)V", "(" + P + "Lnet/minecraft/class_5620$class_8821;)V",
         ["keep:0", imap], "interactions as a named InteractionMap (CbWorldBlocks.interactionMap)")
    ctor(MC + "class_5556.<init>(" + P + "Ljava/util/function/Predicate;Ljava/util/Map;)V",
         "(Lnet/minecraft/class_1959$class_1963;Lnet/minecraft/class_5620$class_8821;" + P + ")V",
         ["conv:1 " + RT + "/CbWorldBlocks#legacyLayeredFill(Ljava/util/function/Predicate;)Lnet/minecraft/class_1959$class_1963;",
          imap.replace("conv:1", "conv:2"), "keep:0"],
         "the fill predicate goes to the cauldron (CbWorldLayeredCauldronMixin); interactions as an InteractionMap")
    ctor(MC + "class_5542.<init>(II" + P + ")V", "(FF" + P + ")V",
         ["conv:0 " + RT + "/CbWorldBlocks#intToFloat(I)F", "conv:1 " + RT + "/CbWorldBlocks#intToFloat(I)F", "keep:2"],
         "the same height and offset, as floats")
    ctor(MC + "class_2269.<init>(" + P + "Lnet/minecraft/class_8177;IZ)V", "(Lnet/minecraft/class_8177;I" + P + ")V",
         ["conv:1,3 " + RT + "/CbWorldBlocks#buttonType(Lnet/minecraft/class_8177;Z)Lnet/minecraft/class_8177;", "keep:2", "keep:0"],
         "the arrow flag moved into the BlockSetType (CbWorldBlocks.buttonType)")
    ctor(MC + "class_2440.<init>(Lnet/minecraft/class_2440$class_2441;" + P + "Lnet/minecraft/class_8177;)V", "(Lnet/minecraft/class_8177;" + P + ")V",
         ["conv:0,2 " + RT + "/CbWorldBlocks#plateType(Lnet/minecraft/class_2440$class_2441;Lnet/minecraft/class_8177;)Lnet/minecraft/class_8177;", "keep:1"],
         "the sensitivity moved into the BlockSetType (CbWorldBlocks.plateType)")
    for torch in ("class_2527", "class_2555"):
        ctor(MC + torch + ".<init>(" + P + "Lnet/minecraft/class_2394;)V", "(Lnet/minecraft/class_2400;" + P + ")V",
             ["conv:1 " + RT + "/CbWorldBlocks#torchFlame(Lnet/minecraft/class_2394;)Lnet/minecraft/class_2400;", "keep:0"],
             "the flame narrowed to SimpleParticleType; any other particle is kept by the torch (CbWorldTorchMixin)")
    ctor(MC + "class_2563.<init>(Lnet/minecraft/class_1291;" + P + ")V", "(Lnet/minecraft/class_1291;I" + P + ")V",
         ["keep:0", "push " + RT + "/CbWorldBlocks#witherRoseEffectSeconds()I", "keep:1"],
         "1.20.2 passed an 8 second effect to FlowerBlock")
    ctor(MC + "class_2473.<init>(Lnet/minecraft/class_2647;" + P + ")V", "(Lnet/minecraft/class_8813;" + P + ")V",
         ["conv:0 " + RT + "/CbWorldTrees#legacyGrower(Lnet/minecraft/class_2647;)Lnet/minecraft/class_8813;", "keep:1"],
         "the sapling keeps its 1.20.2 grower and grows through it (CbWorldTrees, CbWorldSaplingMixin)")
    ctor(MC + "class_7115.<init>(" + P + ")V", "(Lnet/minecraft/class_8813;" + P + ")V",
         ["push " + RT + "/CbWorldBlocks#mangroveGrower()Lnet/minecraft/class_8813;", "keep:0"],
         "1.20.2 grew a MangroveTreeGrower(0.85F): TreeGrower.MANGROVE is the same choice")
    ctor(MC + "class_2431.<init>(" + P + ")V", "(Lnet/minecraft/class_6017;" + P + ")V",
         ["push " + RT + "/CbWorldBlocks#noExperience()Lnet/minecraft/class_6017;", "keep:0"],
         "1.20.2's one-argument constructor dropped ConstantInt.of(0) experience")
    snd = "Lnet/minecraft/class_3414;"
    ctor(MC + "class_8177.<init>(Ljava/lang/String;ZLnet/minecraft/class_2498;" + snd * 8 + ")V",
         "(Ljava/lang/String;ZZZLnet/minecraft/class_8177$class_2441;Lnet/minecraft/class_2498;" + snd * 8 + ")V",
         ["keep:0", "keep:1", "keep:1", "keep:1",
          "conv:1 " + RT + "/CbWorldBlocks#plateSensitivityFor(Z)Lnet/minecraft/class_8177$class_2441;"]
         + ["keep:%d" % i for i in range(2, 11)],
         "1.20.2's set carried no wind-charge, arrow or plate setting (buttons and plates carried their own): "
         "a hand-openable set gets 1.20.2's wooden pairing (all on, any-entity plates), the others its stone one")
    stem_ops = ["conv:0,1 " + RT + "/CbWorldStems#legacyStem(Lnet/minecraft/class_2511;Ljava/util/function/Supplier;)Lnet/minecraft/class_5321;",
                "push " + RT + "/CbWorldStems#noBlock()Lnet/minecraft/class_5321;",
                "push " + RT + "/CbWorldStems#noItem()Lnet/minecraft/class_5321;", "keep:2"]
    for stem in ("class_2513", "class_2195"):
        ctor(MC + stem + ".<init>(Lnet/minecraft/class_2511;Ljava/util/function/Supplier;" + P + ")V",
             "(Lnet/minecraft/class_5321;Lnet/minecraft/class_5321;Lnet/minecraft/class_5321;" + P + ")V", stem_ops,
             "the stem keeps its 1.20.2 fruit and seed and works through them (CbWorldStems)")

    # ---------------------------------------------------------------- fields
    w["fieldRedirects"][MC + "class_5556.field_27880:Ljava/util/function/Predicate;"] = RT + "/CbWorldBlocks#LAYERED_RAIN"
    w["fieldRedirects"][MC + "class_5556.field_27881:Ljava/util/function/Predicate;"] = RT + "/CbWorldBlocks#LAYERED_SNOW"
    w["fieldRedirects"][MC + "class_1420.field_30268:F"] = RT + "/CbWorldEntities#FLAP_DEGREES_PER_TICK"
    w["fieldRedirects"][MC + "class_1420.field_28637:I"] = RT + "/CbWorldEntities#TICKS_PER_FLAP"
    w["instanceFieldGetters"][MC + "class_2527.field_22155:Lnet/minecraft/class_2394;"] = RT + "/CbWorldBlocks#flameParticle"

    # ---------------------------------------------------------------- covers (mixin-added 1.20.2 members)
    w["covers"] += [MC + "class_2445.method_10679()Lnet/minecraft/class_2513;",
                    MC + "class_2445.method_10680()Lnet/minecraft/class_2195;",
                    MC + "class_2513.method_10694()Lnet/minecraft/class_2511;"]

    # ---------------------------------------------------------------- override hooks
    w["overrideHooks"].append({
        "old": "method_9576(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;Lnet/minecraft/class_1657;)V",
        "new": "method_9576(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;Lnet/minecraft/class_1657;)Lnet/minecraft/class_2680;",
        "onlyUnder": MC + "class_2248",
        "bridge": RT + "/CbWorldBlocks.playerWillDestroy(L" + RT + "/CbWorldPlayerWillDestroy;Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;Lnet/minecraft/class_1657;)Lnet/minecraft/class_2680;",
        "adapter": RT + "/CbWorldPlayerWillDestroy",
        "basis": "playerWillDestroy now returns the state the break goes on with; a 1.20.2 override returned nothing and "
                 "1.20.2's caller went on with the state it passed, which the bridge answers"})
    w["overrideHooks"].append({
        "old": "method_5781()Lnet/minecraft/class_270;", "new": "method_5781()Lnet/minecraft/class_268;",
        "unwrap": RT + "/CbWorldEntities.playerTeam(Lnet/minecraft/class_270;)Lnet/minecraft/class_268;",
        "onlyUnder": MC + "class_1297",
        "basis": "getTeam answers a PlayerTeam since 1.20.3; the override's Team is one (CbWorldEntities.playerTeam)"})

    with open(out, "w", encoding="utf-8", newline="\n") as o:
        json.dump({k: v for k, v in w.items() if v}, o, indent=1)
        o.write("\n")
    print(out, {k: len(v) for k, v in w.items() if v})


if __name__ == "__main__":
    main()
