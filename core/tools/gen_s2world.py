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
    sup, itf = {}, {}
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if n.startswith("net/minecraft/") and n.endswith(".class"):
                c = classfile.read(z.read(n))
                if c:
                    sup[c.name] = c.super
                    itf[c.name] = list(c.interfaces)
    return sup, itf


def implementors(sup, itf, iface):
    """iface and every 1.20.2 class or interface that has it among its supertypes"""
    def has(c, seen):
        if c is None or c in seen:
            return False
        seen.add(c)
        if c == iface:
            return True
        return any(has(i, seen) for i in itf.get(c, [])) or has(sup.get(c), seen)
    return [iface] + sorted(c for c in sup if c != iface and has(c, set()))


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
    sup, itf = load_supers(stub)
    w = {
        "fieldRenames": {}, "methodRenames": {}, "classRenames": {}, "callReshapes": {},
        "superCtorReshapes": {}, "staticRedirects": {}, "instanceRedirects": {}, "fieldRedirects": {},
        "instanceFieldGetters": {}, "fieldGetters": {}, "superRedirects": {}, "covers": [], "overrideHooks": [],
        "adapterHooks": [], "abstractFills": [], "ledger": {},
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

    # ---------------------------------------------------------------- batch 2
    G = RT + "/Guard"
    BS, LV, BP, SL, RS = ("Lnet/minecraft/class_2680;", "Lnet/minecraft/class_1937;", "Lnet/minecraft/class_2338;",
                          "Lnet/minecraft/class_3218;", "Lnet/minecraft/class_5819;")

    def hook(old, new, under, basis, **extra):
        e = {"old": old, "new": new, "onlyUnder": MC + under, "basis": basis}
        e.update(extra)
        w["overrideHooks"].append(e)

    def fill(under, method, call, basis):
        w["abstractFills"].append({"under": MC + under, "method": method, "call": call, "basis": basis})

    # covers: 1.20.2 methods a mixin puts back on the class (their callers inside vanilla ask them again)
    w["covers"] += [MC + "class_2283.method_9759(Lnet/minecraft/class_1922;Lnet/minecraft/class_2338;)Lnet/minecraft/class_2680;",
                    MC + "class_1927.method_8349()Lnet/minecraft/class_1282;",
                    MC + "class_1657.method_7282(DDD)V"]

    # ChangeOverTimeBlock: onRandomTick is changeOverTime; applyChangeOverTime is getNextState + set
    cot = "(" + BS + SL + BP + RS + ")"
    for owner in implementors(sup, itf, MC + "class_5547"):
        w["methodRenames"][owner + ".method_33621" + cot + "V"] = "method_54764"
    w["instanceRedirects"][MC + "class_5547.method_33623" + cot + "V"] = RT + "/CbWorldBlocks#applyChangeOverTime"
    hook("method_33621" + cot + "V", "method_54764" + cot + "V", "class_5547",
         "onRandomTick is 1.20.3's changeOverTime: same random draw, same work")
    hook("method_33623" + cot + "V", "method_54765" + cot + "Ljava/util/Optional;", "class_5547",
         "a 1.20.2 applyChangeOverTime sets the block itself; its getNextState answers nothing left to set",
         bridge=RT + "/CbWorldBlocks.legacyNextState(L" + RT + "/CbWorldChangeOverTime;" + cot[1:] + "Ljava/util/Optional;",
         adapter=RT + "/CbWorldChangeOverTime", guard=G)

    # ContainerSingleItem: getFirstItem/setFirstItem/removeFirstItem -> getTheItem/setTheItem/removeTheItem
    CSI, ST = MC + "class_8181", "Lnet/minecraft/class_1799;"
    for old, new, desc, call in [("method_49274", "method_54079", "()" + ST, "getFirstItem"),
                                 ("method_49275", "method_54077", "(" + ST + ")V", "setFirstItem"),
                                 ("method_49276", "method_54099", "()" + ST, "removeFirstItem")]:
        w["instanceRedirects"][CSI + "." + old + desc] = RT + "/CbWorldContainers#" + call
        w["superRedirects"][CSI + "." + old + desc] = RT + "/CbWorldContainers#super" + call[0].upper() + call[1:]
        hook(old + desc, new + desc, "class_8181", "the 1.20.3 method of the same meaning")
    for name, desc, call in [("method_54079", "()" + ST, "getTheItem"), ("method_54077", "(" + ST + ")V", "setTheItem"),
                             ("method_54078", "(I)" + ST, "splitTheItem"),
                             ("method_54080", "()Lnet/minecraft/class_2586;", "getContainerBlockEntity")]:
        fill("class_8181", name + desc, RT + "/CbWorldContainers." + call + "(L" + CSI + ";" + desc[1:],
             "1.20.3 demands it; a 1.20.2 single-item container answers through slot 0 (CbWorldContainers)")

    # ScoreboardNameProvider: getScoreboardName(context) -> getScoreHolder(context)
    w["instanceRedirects"][MC + "class_5670.method_32476(Lnet/minecraft/class_47;)Ljava/lang/String;"] = RT + "/CbWorldScores#getScoreboardName"
    hook("method_32476(Lnet/minecraft/class_47;)Ljava/lang/String;", "method_55379(Lnet/minecraft/class_47;)Lnet/minecraft/class_9015;",
         "class_5670", "the provider's name, as a holder known by name only",
         unwrap=RT + "/CbWorldScores.nameHolder(Ljava/lang/String;)Lnet/minecraft/class_9015;")

    # Explosion constructors: particles and sound became arguments
    part = ["push " + RT + "/CbWorldLevels#explosionSmallParticles()Lnet/minecraft/class_2394;",
            "push " + RT + "/CbWorldLevels#explosionLargeParticles()Lnet/minecraft/class_2394;",
            "push " + RT + "/CbWorldLevels#explosionSound()Lnet/minecraft/class_3414;"]
    ctor(MC + "class_1927.<init>(" + LV + "Lnet/minecraft/class_1297;DDDFLjava/util/List;)V",
         "(" + LV + "Lnet/minecraft/class_1297;DDDFLjava/util/List;Lnet/minecraft/class_1927$class_4179;Lnet/minecraft/class_2394;Lnet/minecraft/class_2394;Lnet/minecraft/class_3414;)V",
         ["keep"] * 7 + ["push " + RT + "/CbWorldLevels#destroyWithDecay()Lnet/minecraft/class_1927$class_4179;"] + part,
         "1.20.2: DESTROY_WITH_DECAY, no fire, and the explosion particles and sound 1.20.2 always used")
    ctor(MC + "class_1927.<init>(" + LV + "Lnet/minecraft/class_1297;Lnet/minecraft/class_1282;Lnet/minecraft/class_5362;DDDFZLnet/minecraft/class_1927$class_4179;)V",
         "(" + LV + "Lnet/minecraft/class_1297;Lnet/minecraft/class_1282;Lnet/minecraft/class_5362;DDDFZLnet/minecraft/class_1927$class_4179;Lnet/minecraft/class_2394;Lnet/minecraft/class_2394;Lnet/minecraft/class_3414;)V",
         ["keep"] * 10 + part, "the explosion particles and sound 1.20.2 always used")

    # AABB(BlockPos, BlockPos): the corners themselves
    ctor(MC + "class_238.<init>(" + BP + BP + ")V", "(DDDDDD)V",
         ["conv:%d %s/CbWorldPhys#%s(Lnet/minecraft/class_2338;)D" % (i, RT, a) for i in (0, 1) for a in ("x", "y", "z")],
         "1.20.2 boxed the two corners as given (CbWorldPhys)")

    # jigsaw: pool aliases (1.20.3) -- 1.20.2 had none
    reshape(MC + "class_3778.method_30419(Lnet/minecraft/class_3195$class_7149;Lnet/minecraft/class_6880;Ljava/util/Optional;ILnet/minecraft/class_2338;ZLjava/util/Optional;I)Ljava/util/Optional;",
            "(Lnet/minecraft/class_3195$class_7149;Lnet/minecraft/class_6880;Ljava/util/Optional;ILnet/minecraft/class_2338;ZLjava/util/Optional;ILnet/minecraft/class_8891;)Ljava/util/Optional;",
            ["keep"] * 8 + ["push " + RT + "/CbWorldLevels#noPoolAliasLookup()Lnet/minecraft/class_8891;"],
            "1.20.2 jigsaw structures had no pool aliases: the empty lookup")
    ctor(MC + "class_5434.<init>(Lnet/minecraft/class_3195$class_7302;Lnet/minecraft/class_6880;Ljava/util/Optional;ILnet/minecraft/class_6122;ZLjava/util/Optional;I)V",
         "(Lnet/minecraft/class_3195$class_7302;Lnet/minecraft/class_6880;Ljava/util/Optional;ILnet/minecraft/class_6122;ZLjava/util/Optional;ILjava/util/List;)V",
         ["keep"] * 8 + ["push " + RT + "/CbWorldLevels#noPoolAliases()Ljava/util/List;"],
         "1.20.2 jigsaw structures had no pool aliases")

    # crafting
    w["fieldGetters"][MC + "class_7710.field_40252:Lnet/minecraft/class_3542$class_7292;"] = RT + "/CbWorldItems#craftingBookCategoryCodec"
    w["fieldGetters"][MC + "class_1740.field_41941:Lnet/minecraft/class_3542$class_7292;"] = RT + "/CbWorldItems#armorMaterialsCodec"
    w["classRenames"][MC + "class_8785"] = FAC + "/CraftingRecipeCodecs"
    # the recipe factory interfaces moved: same single method, same descriptor
    w["classRenames"][MC + "class_3957$class_3958"] = MC + "class_1874$class_3958"
    w["classRenames"][MC + "class_3972$class_3973$class_3974"] = MC + "class_3972$class_3974"
    pat = "conv:2,3,4 " + RT + "/CbWorldItems#shapedPattern(IILnet/minecraft/class_2371;)Lnet/minecraft/class_8957;"
    for tail in ("", "Z"):
        ops = ["keep:0", "keep:1", pat, "keep:5"] + (["keep:6"] if tail else [])
        ctor(MC + "class_1869.<init>(Ljava/lang/String;Lnet/minecraft/class_7710;IILnet/minecraft/class_2371;Lnet/minecraft/class_1799;" + tail + ")V",
             "(Ljava/lang/String;Lnet/minecraft/class_7710;Lnet/minecraft/class_8957;Lnet/minecraft/class_1799;" + tail + ")V",
             ops, "width, height and ingredients become a ShapedRecipePattern (CbWorldItems.shapedPattern)")
    w["instanceRedirects"][MC + "class_1856.method_8089(Z)Lcom/google/gson/JsonElement;"] = RT + "/CbWorldItems#ingredientToJson"

    # single override hooks
    hook("method_8283(" + LV + RS + BP + ")Lnet/minecraft/class_1297;", "method_8283(" + LV + BP + ")Lnet/minecraft/class_1297;",
         "class_1917", "the random 1.20.3 no longer passes is the level's, which 1.20.3 itself uses there",
         args=[0, 0, 1], wrap=RT + "/CbWorldEntities.levelRandom(" + LV + ")" + RS)
    hook("method_7843(Lnet/minecraft/class_1799;" + LV + "Lnet/minecraft/class_1657;)V",
         "method_54465(Lnet/minecraft/class_1799;" + LV + "Lnet/minecraft/class_1657;)V", "class_1792",
         "onCraftedBy(stack, level, player) is 1.20.3's onCraftedBy under a new name")
    hook("method_6981(Ljava/util/UUID;)V", "method_6981(Lnet/minecraft/class_1297;)V", "class_1542",
         "1.20.3 hands the thrower entity; the 1.20.2 override gets its id",
         wrap=RT + "/CbWorldEntities.throwerId(Lnet/minecraft/class_1297;)Ljava/util/UUID;")
    hook("method_7335([Lnet/minecraft/class_2960;)V", "method_7335(Ljava/util/List;)V", "class_1657",
         "the same recipe ids, as an array", wrap=RT + "/CbWorldEntities.recipeArray(Ljava/util/List;)[Lnet/minecraft/class_2960;")
    hook("method_7282(DDD)V", "method_54720(DDD)V", "class_3222",
         "ServerPlayer.travel calls 1.20.3's checkMovementStatistics; the override's super call reaches it directly",
         superCalls="rewrite")

    # TripWireHookBlock.calculateState went static: an override is found through an adapter (CbWorldTripWireHookMixin)
    w["adapterHooks"].append({"under": MC + "class_2537", "adapter": RT + "/CbWorldTripWireHook",
                              "requires": ["method_10776(" + LV + BP + BS + "ZZI" + BS + ")V"], "synth": [],
                              "basis": "1.20.3 calls the static; the mixins call a 1.20.2 hook's override through this"})
    # canBreatheUnderwater: final in 1.20.3 (the layer's access widener lifts it); converted entities answer the 1.20.2 way
    w["adapterHooks"].append({"under": MC + "class_1309", "adapter": RT + "/CbWorldLegacyEntity", "requires": [], "synth": [],
                              "basis": "marks the entity classes converted from 1.20.2 (CbWorldLivingEntityMixin)"})

    # what 1.20.3 newly demands of a 1.20.2 class
    fill("class_4970", "method_53969()Lcom/mojang/serialization/MapCodec;",
         RT + "/CbWorldBlocks.codec(Lnet/minecraft/class_4970;)Lcom/mojang/serialization/MapCodec;",
         "a 1.20.2 block has no codec: one that decodes to the block itself (CbWorldBlocks.codec)")
    fill("class_1937", "method_54719()Lnet/minecraft/class_8921;",
         RT + "/CbWorldLevels.tickRateManager(Lnet/minecraft/class_1937;)Lnet/minecraft/class_8921;",
         "a 1.20.2 level ran at 20 ticks a second (CbWorldLevels.tickRateManager)")
    fill("class_2484$class_2485", "method_15434()Ljava/lang/String;",
         RT + "/CbWorldBlocks.skullTypeName(Lnet/minecraft/class_2484$class_2485;)Ljava/lang/String;",
         "a 1.20.2 skull type has no serialized name (CbWorldBlocks.skullTypeName)")

    with open(out, "w", encoding="utf-8", newline="\n") as o:
        json.dump({k: v for k, v in w.items() if v}, o, indent=1)
        o.write("\n")
    print(out, {k: len(v) for k, v in w.items() if v})


if __name__ == "__main__":
    main()
