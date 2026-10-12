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

    def ctor(key, desc, ops, basis, post=None, post_args=None):
        e = {"desc": desc, "ops": ops, "basis": basis}
        if post:
            e["post"], e["postArgs"] = post, post_args
        w["superCtorReshapes"][key] = e

    imap = "conv:1 " + RT + "/CbWorldBlocks#interactionMap(Ljava/util/Map;)Lnet/minecraft/class_5620$class_8821;"
    ctor(MC + "class_2275.<init>(" + P + "Ljava/util/Map;)V", "(" + P + "Lnet/minecraft/class_5620$class_8821;)V",
         ["keep:0", imap], "interactions as a named InteractionMap (CbWorldBlocks.interactionMap)")
    ctor(MC + "class_5556.<init>(" + P + "Ljava/util/function/Predicate;Ljava/util/Map;)V",
         "(Lnet/minecraft/class_1959$class_1963;Lnet/minecraft/class_5620$class_8821;" + P + ")V",
         ["conv:1 " + RT + "/CbWorldBlocks#fillPrecipitation(Ljava/util/function/Predicate;)Lnet/minecraft/class_1959$class_1963;",
          imap.replace("conv:1", "conv:2"), "keep:0"],
         "the fill predicate goes to the constructed cauldron (CbWorldLayeredCauldronMixin); interactions as an InteractionMap",
         post=RT + "/CbWorldBlocks#legacyLayeredCauldron(Lnet/minecraft/class_5556;Ljava/util/function/Predicate;)V", post_args=[1])
    ctor(MC + "class_5542.<init>(II" + P + ")V", "(FF" + P + ")V",
         ["conv:0 " + RT + "/CbWorldBlocks#intToFloat(I)F", "conv:1 " + RT + "/CbWorldBlocks#intToFloat(I)F", "keep:2"],
         "the same height and offset, as floats")
    ctor(MC + "class_2269.<init>(" + P + "Lnet/minecraft/class_8177;IZ)V", "(Lnet/minecraft/class_8177;I" + P + ")V",
         ["conv:1,3 " + RT + "/CbWorldBlocks#buttonType(Lnet/minecraft/class_8177;Z)Lnet/minecraft/class_8177;", "keep:2", "keep:0"],
         "the arrow flag moved into the BlockSetType (CbWorldBlocks.buttonType)")
    ctor(MC + "class_2440.<init>(Lnet/minecraft/class_2440$class_2441;" + P + "Lnet/minecraft/class_8177;)V", "(Lnet/minecraft/class_8177;" + P + ")V",
         ["conv:0,2 " + RT + "/CbWorldBlocks#plateType(Lnet/minecraft/class_2440$class_2441;Lnet/minecraft/class_8177;)Lnet/minecraft/class_8177;", "keep:1"],
         "the sensitivity moved into the BlockSetType (CbWorldBlocks.plateType)")
    for torch, post in (("class_2527", "legacyTorchFlame"), ("class_2555", "legacyWallTorchFlame")):
        ctor(MC + torch + ".<init>(" + P + "Lnet/minecraft/class_2394;)V", "(Lnet/minecraft/class_2400;" + P + ")V",
             ["conv:1 " + RT + "/CbWorldBlocks#torchFlame(Lnet/minecraft/class_2394;)Lnet/minecraft/class_2400;", "keep:0"],
             "the flame narrowed to SimpleParticleType; any other particle is kept by the constructed torch (CbWorldTorchMixin)",
             post=RT + "/CbWorldBlocks#" + post + "(Lnet/minecraft/" + torch + ";Lnet/minecraft/class_2394;)V", post_args=[1])
    ctor(MC + "class_2563.<init>(Lnet/minecraft/class_1291;" + P + ")V", "(Lnet/minecraft/class_1291;I" + P + ")V",
         ["keep:0", "push " + RT + "/CbWorldBlocks#witherRoseEffectSeconds()I", "keep:1"],
         "1.20.2 passed an 8 second effect to FlowerBlock")
    ctor(MC + "class_2473.<init>(Lnet/minecraft/class_2647;" + P + ")V", "(Lnet/minecraft/class_8813;" + P + ")V",
         ["conv:0 " + RT + "/CbWorldTrees#legacyGrower(Lnet/minecraft/class_2647;)Lnet/minecraft/class_8813;", "keep:1"],
         "the sapling keeps its 1.20.2 grower and grows through it (CbWorldTrees, CbWorldSaplingMixin)",
         post=RT + "/CbWorldTrees#attachGrower(Lnet/minecraft/class_2473;Lnet/minecraft/class_2647;)V", post_args=[0])
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
    stem_ops = ["push " + RT + "/CbWorldStems#noBlock()Lnet/minecraft/class_5321;",
                "push " + RT + "/CbWorldStems#noBlock()Lnet/minecraft/class_5321;",
                "push " + RT + "/CbWorldStems#noItem()Lnet/minecraft/class_5321;", "keep:2"]
    for stem, post in (("class_2513", "attachStem"), ("class_2195", "attachAttachedStem")):
        ctor(MC + stem + ".<init>(Lnet/minecraft/class_2511;Ljava/util/function/Supplier;" + P + ")V",
             "(Lnet/minecraft/class_5321;Lnet/minecraft/class_5321;Lnet/minecraft/class_5321;" + P + ")V", stem_ops,
             "the constructed stem keeps its 1.20.2 fruit and seed and works through them (CbWorldStems)",
             post=RT + "/CbWorldStems#" + post + "(Lnet/minecraft/" + stem + ";Lnet/minecraft/class_2511;Ljava/util/function/Supplier;)V",
             post_args=[0, 1])

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

    # ---------------------------------------------------------------- batch 3
    w.setdefault("ctorRedirects", {})
    w.setdefault("lambdaAdapters", {})
    ITEM, ET, LE = "Lnet/minecraft/class_1799;", "Lnet/minecraft/class_1299;", "Lnet/minecraft/class_1309;"

    # arrows: 1.20.3's constructors take the stack the arrow is picked up as
    AA, AR, SA = MC + "class_1665", MC + "class_1667", MC + "class_1679"
    nop = "push " + RT + "/CbWorldArrows#noPickupItem()" + ITEM
    for args in (ET + LV, ET + "DDD" + LV, ET + LE + LV):
        n = len(classfile.split_args("(" + args + ")V")[0])
        ctor(AA + ".<init>(" + args + ")V", "(" + args + ITEM + ")V", ["keep"] * n + [nop],
             "a 1.20.2 subclass had no pickup stack: it answered getPickupItem itself (abstract in 1.20.2)")
    for args in (LV + "DDD", LV + LE):
        n = len(classfile.split_args("(" + args + ")V")[0])
        ctor(AR + ".<init>(" + args + ")V", "(" + args + ITEM + ")V",
             ["keep"] * n + ["push " + RT + "/CbWorldArrows#arrowStack()" + ITEM],
             "the plain arrow 1.20.3's own Arrow(EntityType, Level) passes; a 1.20.2 subclass is picked up the 1.20.2 way "
             "(CbWorldLegacyArrow, CbWorldArrowMixin)")
        w["ctorRedirects"][AR + ".<init>(" + args + ")V"] = RT + "/CbWorldArrows#newArrow"
        ctor(SA + ".<init>(" + args + ")V", "(" + args + ITEM + ")V",
             ["keep"] * n + ["push " + RT + "/CbWorldArrows#spectralArrowStack()" + ITEM],
             "1.20.2's SpectralArrow was always picked up as one spectral arrow: the stack it now copies")
    w["adapterHooks"].append({"under": AR, "adapter": RT + "/CbWorldLegacyArrow", "requires": [], "synth": [],
                              "basis": "marks the Arrow classes converted from 1.20.2 (CbWorldArrowMixin)"})

    # AttributeModifier: the name supplier and getName()
    ctor(MC + "class_1322.<init>(Ljava/util/UUID;Ljava/util/function/Supplier;DLnet/minecraft/class_1322$class_1323;)V",
         "(Ljava/util/UUID;Ljava/lang/String;DLnet/minecraft/class_1322$class_1323;)V",
         ["keep", "drop", "push " + RT + "/CbWorldAttributes#placeholderName()Ljava/lang/String;", "keep", "keep"],
         "the name supplier goes to the constructed modifier, asked whenever 1.20.2 asked it (CbWorldAttributeModifierMixin)",
         post=RT + "/CbWorldAttributes#attachName(Lnet/minecraft/class_1322;Ljava/util/function/Supplier;)V", post_args=[1])
    w["covers"].append(MC + "class_1322.method_6185()Ljava/lang/String;")

    # RandomizableContainerBlockEntity: the loot table methods are 1.20.3's RandomizableContainer defaults
    RCBE = MC + "class_2621"
    w["staticRedirects"][RCBE + ".method_11287(Lnet/minecraft/class_1922;Lnet/minecraft/class_5819;Lnet/minecraft/class_2338;Lnet/minecraft/class_2960;)V"] = RT + "/CbWorldContainers#setLootTable"
    loot = [("method_11283", "(Lnet/minecraft/class_2487;)Z", "method_54871", "tryLoadLootTable"),
            ("method_11286", "(Lnet/minecraft/class_2487;)Z", "method_54872", "trySaveLootTable"),
            ("method_11289", "(Lnet/minecraft/class_1657;)V", "method_54873", "unpackLootTable"),
            ("method_11285", "(Lnet/minecraft/class_2960;J)V", "method_54867", "setLootTable(table, seed)")]
    for owner in with_subclasses(sup, RCBE):
        for old, desc, new, _ in loot:
            w["methodRenames"][owner + "." + old + desc] = new
        w["fieldRenames"][owner + ".field_31353:Ljava/lang/String;"] = "field_47154"
        w["fieldRenames"][owner + ".field_31352:Ljava/lang/String;"] = "field_47155"
    for old, desc, new, what in loot:
        hook(old + desc, new + desc, "class_2621",
             what + " is RandomizableContainer's default of that name in 1.20.3: same body over the same fields")

    # SignBlockEntity.getTextFacingPlayer
    w["covers"].append(MC + "class_2625.method_49844(Lnet/minecraft/class_1657;)Lnet/minecraft/class_8242;")

    # StructureBlockEntity: 1.20.2's loading methods back (covers); 1.20.2 subclasses asked the 1.20.2 way
    SBE = MC + "class_2633"
    w["covers"] += [SBE + ".method_11368(Lnet/minecraft/class_3218;Z)Z",
                    SBE + ".method_21864(Lnet/minecraft/class_3218;ZLnet/minecraft/class_3499;)Z",
                    SBE + ".method_21865()Ljava/lang/String;"]
    w["instanceRedirects"][SBE + ".method_11376(Lnet/minecraft/class_3218;)Z"] = RT + "/CbWorldBlocks#loadStructure"
    w["adapterHooks"].append({"under": SBE, "adapter": RT + "/CbWorldLegacyStructureBlock", "requires": [], "synth": [],
                              "basis": "marks the StructureBlockEntity classes converted from 1.20.2 (CbWorldStructureBlockMixin, "
                                       "CbWorldStructurePacketMixin)"})

    # world storage
    LSA = MC + "class_32$class_5143"
    w["instanceRedirects"][LSA + ".method_27013(Lcom/mojang/serialization/DynamicOps;Lnet/minecraft/class_7712;Lnet/minecraft/class_2378;Lcom/mojang/serialization/Lifecycle;)Lcom/mojang/datafixers/util/Pair;"] = RT + "/CbWorldStorage#getDataTag"
    w["instanceRedirects"][LSA + ".method_29584()Lnet/minecraft/class_34;"] = RT + "/CbWorldStorage#getSummary"
    w["instanceRedirects"][LSA + ".method_29585()Lnet/minecraft/class_7712;"] = RT + "/CbWorldStorage#getDataConfiguration"
    w["staticRedirects"][MC + "class_31.method_29029(Lcom/mojang/serialization/Dynamic;Lcom/mojang/datafixers/DataFixer;ILnet/minecraft/class_2487;Lnet/minecraft/class_1940;Lnet/minecraft/class_5315;Lnet/minecraft/class_31$class_7729;Lnet/minecraft/class_5285;Lcom/mojang/serialization/Lifecycle;)Lnet/minecraft/class_31;"] = RT + "/CbWorldStorage#parsePrimaryLevelData"

    # LevelSummary: askToOpenWorld is isDowngrade; markVersionInList comes back (a cover)
    for owner in with_subclasses(sup, MC + "class_34"):
        w["methodRenames"][owner + ".method_260()Z"] = "method_54550"
    hook("method_260()Z", "method_54550()Z", "class_34",
         "askToOpenWorld (the world is from a newer version) is 1.20.3's isDowngrade, asked where 1.20.2 asked it")
    hook("method_256()Z", "method_54549()Z", "class_34",
         "1.20.3's world list asks shouldBackup() where 1.20.2's asked markVersionInList()")
    w["covers"].append(MC + "class_34.method_256()Z")

    # ValidationContext: problems go to a ProblemReporter
    VC = MC + "class_58"
    ctor(VC + ".<init>(Lnet/minecraft/class_176;Lnet/minecraft/class_8489;)V",
         "(Lnet/minecraft/class_8942;Lnet/minecraft/class_176;Lnet/minecraft/class_8489;)V",
         ["push " + RT + "/CbWorldLoot#newCollector()Lnet/minecraft/class_8942;", "keep:0", "keep:1"],
         "1.20.2's context started an empty problem collection of its own: 1.20.3's Collector")
    ctor(VC + ".<init>(Lcom/google/common/collect/Multimap;Ljava/util/function/Supplier;Lnet/minecraft/class_176;Lnet/minecraft/class_8489;Ljava/util/Set;)V",
         "(Lnet/minecraft/class_8942;Lnet/minecraft/class_176;Lnet/minecraft/class_8489;Ljava/util/Set;)V",
         ["conv:0,1 " + RT + "/CbWorldLoot#problems(Lcom/google/common/collect/Multimap;Ljava/util/function/Supplier;)Lnet/minecraft/class_8942;",
          "keep:2", "keep:3", "keep:4"],
         "the problems multimap and context path become a reporter into them (CbWorldLoot); the constructor is opened by "
         "the layer's access widener")
    w["covers"].append(VC + ".method_361()Lcom/google/common/collect/Multimap;")

    # objectives: no display auto-update, default number format
    SB, OBJ, SC, HOLD = MC + "class_269", MC + "class_266", MC + "class_267", "Lnet/minecraft/class_9015;"
    obj_defaults = ["push " + RT + "/CbWorldScores#noDisplayAutoUpdate()Z",
                    "push " + RT + "/CbWorldScores#defaultNumberFormat()Lnet/minecraft/class_9022;"]
    OBJ_ARGS = "Ljava/lang/String;Lnet/minecraft/class_274;Lnet/minecraft/class_2561;Lnet/minecraft/class_274$class_275;"
    ctor(OBJ + ".<init>(Lnet/minecraft/class_269;" + OBJ_ARGS + ")V", "(Lnet/minecraft/class_269;" + OBJ_ARGS + "ZLnet/minecraft/class_9022;)V",
         ["keep"] * 5 + obj_defaults, "1.20.2 objectives had no display auto-update and the default number format")
    reshape(SB + ".method_1168(" + OBJ_ARGS + ")Lnet/minecraft/class_266;",
            "(" + OBJ_ARGS + "ZLnet/minecraft/class_9022;)Lnet/minecraft/class_266;", ["keep"] * 4 + obj_defaults,
            "1.20.2 objectives had no display auto-update and the default number format")

    # Score: the stored 1.20.3 score is the 1.20.2 Score (CbWorldScoreMixin)
    w["covers"] += [SC + "." + m for m in ("method_1122()Lnet/minecraft/class_269;", "method_1124(I)V", "method_1126()I",
                                          "method_1127()Lnet/minecraft/class_266;", "method_1128(I)V",
                                          "method_1129()Ljava/lang/String;", "method_1130()V", "method_1132()V")]
    ctor(SC + ".<init>(Lnet/minecraft/class_269;Lnet/minecraft/class_266;Ljava/lang/String;)V", "()V", ["drop", "drop", "drop"],
         "the scoreboard, objective and owner go to the constructed score (CbWorldScoreMixin)",
         post=RT + "/CbWorldScores#attachNew(Lnet/minecraft/class_267;Lnet/minecraft/class_269;Lnet/minecraft/class_266;Ljava/lang/String;)V",
         post_args=[0, 1, 2])
    w["fieldRedirects"][SC + ".field_1413:Ljava/util/Comparator;"] = RT + "/CbWorldScores#SCORE_COMPARATOR"

    # Scoreboard: the owner-name methods come back (covers); the notifications take holders
    w["covers"] += [SB + "." + m for m in ("method_1180(Ljava/lang/String;Lnet/minecraft/class_266;)Lnet/minecraft/class_267;",
                                          "method_1166(Ljava/lang/String;)Ljava/util/Map;",
                                          "method_1183(Ljava/lang/String;Lnet/minecraft/class_266;)Z",
                                          "method_1155(Ljava/lang/String;Lnet/minecraft/class_266;)V",
                                          "method_1162(Lnet/minecraft/class_274;Ljava/lang/String;Ljava/util/function/Consumer;)V")]
    name_holder = "conv:0 " + RT + "/CbWorldScores#nameHolder(Ljava/lang/String;)" + HOLD
    for owner in (SB, MC + "class_2995"):
        reshape(owner + ".method_1152(Ljava/lang/String;)V", "(" + HOLD + ")V", [name_holder],
                "the owner name, as the holder 1.20.3 keys scores by")
        reshape(owner + ".method_1190(Ljava/lang/String;Lnet/minecraft/class_266;)V", "(" + HOLD + "Lnet/minecraft/class_266;)V",
                [name_holder, "keep:1"], "the owner name, as the holder 1.20.3 keys scores by")
        reshape(owner + ".method_1176(Lnet/minecraft/class_267;)V", "(" + HOLD + "Lnet/minecraft/class_266;Lnet/minecraft/class_267;)V",
                ["conv:0 " + RT + "/CbWorldScores#holderOf(Lnet/minecraft/class_267;)" + HOLD,
                 "conv:0 " + RT + "/CbWorldScores#objectiveOf(Lnet/minecraft/class_267;)Lnet/minecraft/class_266;", "keep:0"],
                "the holder and objective the score is stored under (opened by the layer's access widener)")
    hook("method_1152(Ljava/lang/String;)V", "method_1152(" + HOLD + ")V", "class_269",
         "the removed holder's name, as 1.20.2 named it", wrap=RT + "/CbWorldScores.holderName(" + HOLD + ")Ljava/lang/String;")
    hook("method_1190(Ljava/lang/String;Lnet/minecraft/class_266;)V", "method_1190(" + HOLD + "Lnet/minecraft/class_266;)V", "class_269",
         "the holder's name, as 1.20.2 named it", wrap=RT + "/CbWorldScores.holderName(" + HOLD + ")Ljava/lang/String;")
    hook("method_1176(Lnet/minecraft/class_267;)V", "method_1176(" + HOLD + "Lnet/minecraft/class_266;Lnet/minecraft/class_267;)V",
         "class_269", "the changed score, which learns the holder and objective it is stored under first",
         bridge=RT + "/CbWorldScores.scoreChangedBridge(L" + RT + "/CbWorldScoreChangeListener;" + HOLD
                + "Lnet/minecraft/class_266;Lnet/minecraft/class_267;)V",
         adapter=RT + "/CbWorldScoreChangeListener")
    # same name and erased type, other element types: a 1.20.2 call gets 1.20.2's elements
    w["instanceRedirects"][SB + ".method_1184(Lnet/minecraft/class_266;)Ljava/util/Collection;"] = RT + "/CbWorldScores#getPlayerScoresByObjective"
    w["instanceRedirects"][SB + ".method_1178()Ljava/util/Collection;"] = RT + "/CbWorldScores#getTrackedPlayers"

    # OperationArgument.Operation: apply(Score, Score) -> apply(ScoreAccess, ScoreAccess)
    OP = MC + "class_2218$class_2219"
    OLD_APPLY, NEW_APPLY = "apply(Lnet/minecraft/class_267;Lnet/minecraft/class_267;)V", "apply(Lnet/minecraft/class_9014;Lnet/minecraft/class_9014;)V"
    acc = RT + "/CbWorldScores#access(Lnet/minecraft/class_267;)Lnet/minecraft/class_9014;"
    reshape(OP + "." + OLD_APPLY, NEW_APPLY[len("apply"):], ["conv:0 " + acc, "conv:1 " + acc],
            "each 1.20.2 score as a ScoreAccess over it (CbWorldScoreAccessView)")
    hook(OLD_APPLY, NEW_APPLY, "class_2218$class_2219", "each ScoreAccess as a 1.20.2 Score over it (CbWorldAccessScore)",
         wrap=RT + "/CbWorldScores.scoreOf(Lnet/minecraft/class_9014;)Lnet/minecraft/class_267;")
    w["lambdaAdapters"][OP + "." + OLD_APPLY] = {
        "iface": RT + "/CbWorldScoreOperation",
        "adapt": RT + "/CbWorldScores#operation(L" + RT + "/CbWorldScoreOperation;)L" + OP + ";"}

    # ChestBoat.method_7549: not a member any 1.20.2 mod can reference (see the ledger text)
    w["ledger"][MC + "class_7264.method_7549()V"] = (
        "stub artifact, not 1.20.2 API: CB's remap-jar (JarRemapper.remapToIntermediary) named ChestBoat's own K()V -- "
        "ContainerEntity.clearItemStacks, method_42273 in Fabric's intermediary -- after Boat's private K()V (controlBoat, "
        "method_7549), climbing to a private superclass method before the interface. A 1.20.2 mod calls clearItemStacks "
        "as method_42273, which 1.20.3 has, and Boat.controlBoat only through its own access widener, which still applies "
        "(Boat.method_7549()V is unchanged). The 1.20.3 stub has the same artifact (ChestBoat G()V named method_7534).")

    with open(out, "w", encoding="utf-8", newline="\n") as o:
        json.dump({k: v for k, v in w.items() if v}, o, indent=1)
        o.write("\n")
    print(out, {k: len(v) for k, v in w.items() if v})


if __name__ == "__main__":
    main()
