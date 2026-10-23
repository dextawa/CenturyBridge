"""gen_s7level.py: the s7level wiring fragment of climb step 1.21.1 -> 1.21.2 (owners whose Mojang name is under
net/minecraft/world/level/).

  python gen_s7level.py <V stub: client-1.21.1-intermediary.jar> <W stub: client-1.21.2-intermediary.jar>

Writes core/segments/steps/1.21.1__1.21.2.json.d/s7level-gen.json. Each family is decided once, from the 1.21.1 and
1.21.2 bytecode (the basis of each entry says what was read), and its keys are read off the stubs: every class that
declares the member, every subclass a call can name.
"""
import json
import os
import sys
import zipfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import classfile  # noqa: E402

STEP = "top/dext/centurybridge/step/v1_21_2"
RT = STEP + "/rt"
FAC = STEP + "/facade"
MC = "net/minecraft/"
G = RT + "/Guard"
HERE = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.dirname(os.path.dirname(HERE))
FRAGMENT = os.path.join(ROOT, "core/segments/steps/1.21.1__1.21.2.json.d/s7level-gen.json")


def L(c):
    return "L" + (c if "/" in c else MC + c) + ";"


def load(jar):
    out = {}
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if n.startswith("net/minecraft/") and n.endswith(".class"):
                c = classfile.read(z.read(n), False)
                if c:
                    out[c.name] = c
    return out


class Fragment:
    def __init__(self, v, w):
        self.v, self.w_stub = v, w
        self.w = {k: ({} if k not in ("overrideHooks", "overrideRenames", "adapterHooks", "abstractFills",
                                      "convertHooks", "covers") else [])
                  for k in ("classRenames", "instanceOfRedirects", "checkCastRedirects", "facadeGaps", "superCtorReshapes", "superRenames", "methodRenames", "callReshapes", "superCtorReshapes", "ctorRedirects", "staticRedirects",
                            "instanceRedirects", "fieldRedirects", "fieldGetters", "instanceFieldGetters", "instanceFieldSetters",
                            "superRedirects", "covers", "overrideHooks", "overrideRenames", "frameDefaults",
                            "convertHooks", "adapterHooks", "abstractFills", "lambdaAdapters", "genericWitnesses",
                            "ledger")}

    def put(self, section, key, value):
        assert key not in self.w[section], (section, key)
        self.w[section][key] = value

    def reshape(self, key, desc, ops, basis, ret=None):
        e = {"desc": desc, "ops": ops, "basis": basis}
        if ret:
            e["ret"] = ret
        self.put("callReshapes", key, e)

    def hook(self, **e):
        self.w["overrideHooks"].append(e)

    def declarers(self, name, desc, stub=None):
        """every class of the stub declaring name+desc (non-static)"""
        stub = stub or self.v
        return sorted(c for c, k in stub.items()
                      if any(m.name == name and m.desc == desc and not m.access & classfile.ACC_STATIC for m in k.methods))

    def write(self):
        with open(FRAGMENT, "w", encoding="utf-8", newline="\n") as o:
            json.dump({k: v for k, v in self.w.items() if v}, o, indent=1)
            o.write("\n")
        print(FRAGMENT, {k: len(v) for k, v in self.w.items() if v})


# ------------------------------------------------------------------------------------------- updateShape
BS, DIR, POS, LA, LR, STA, RND = L("class_2680"), L("class_2350"), L("class_2338"), L("class_1936"), L("class_4538"), \
    L("class_10225"), L("class_5819")
BB, BSB = MC + "class_4970", MC + "class_4970$class_4971"
SH = RT + "/CbLevelShapes"


def update_shape(f):
    old = "method_9559(" + BS + DIR + BS + LA + POS + POS + ")" + BS
    new = "method_9559(" + BS + LR + STA + POS + DIR + POS + BS + RND + ")" + BS
    rnd = "conv:3 " + SH + "#random(" + LA + ")" + RND
    ops = ["keep:0", "keep:3 " + LR, "keep:3 " + STA, "keep:4", "keep:1", "keep:5", "keep:2", rnd]
    # keyed where 1.21.1 declared it; a call naming any subclass finds it up the hierarchy
    f.reshape(BB + "." + old, new[len("method_9559"):], ops,
              "1.21.2's updateShape takes the level as reader and tick access and the level's random -- what every "
              "1.21.2 caller passes with one LevelAccessor (CbLevelShapes)")
    f.hook(old=old, new=new, args=[0, 4, 6, 1, 3, 5], onlyUnder=BB, narrow=True, frames=G, superCalls="rewrite",
           basis="vanilla asks a 1.21.1 block's updateShape in 1.21.2's shape: every caller's reader is a LevelAccessor "
                 "(narrow; any other reader goes to super), and the old method's super call gets back the tick access "
                 "and random vanilla passed")
    sig = "(L" + BB + ";" + BS + DIR + BS + LA + POS + POS + ")"
    f.put("frameDefaults", old, {"2": SH + "#tickAccess" + sig + STA, "7": SH + "#superRandom" + sig + RND})
    # BlockStateBase.updateShape(direction, neighborState, level, pos, neighborPos)
    sold = "method_26191(" + DIR + BS + LA + POS + POS + ")" + BS
    snew = "method_26191(" + LR + STA + POS + DIR + POS + BS + RND + ")" + BS
    f.reshape(BSB + "." + sold, snew[len("method_26191"):],
              ["keep:2 " + LR, "keep:2 " + STA, "keep:3", "keep:0", "keep:4", "keep:1", "conv:2 " + SH + "#random(" + LA + ")" + RND],
              "BlockState.updateShape: the level as reader and tick access, its random (CbLevelShapes)")
    f.hook(old=sold, new=snew, args=[3, 5, 0, 2, 4], onlyUnder=BSB, narrow=True, frames=G, superCalls="rewrite",
           basis="a 1.21.1 BlockStateBase subclass's updateShape, asked in 1.21.2's shape (as Block.updateShape)")
    ssig = "(L" + BSB + ";" + DIR + BS + LA + POS + POS + ")"
    f.put("frameDefaults", sold, {"1": SH + "#stateTickAccess" + ssig + STA, "6": SH + "#stateRandom" + ssig + RND})


# ------------------------------------------------------------------------------------------- shape getters
BG, VS, VEC = L("class_1922"), L("class_265"), L("class_243")


def shapes(f):
    for name, ret, legacy, bridge, sup in (
            ("method_9571", VS, "LegacyOcclusion", "occlusionShape", "SuperOcclusion"),
            ("method_9505", "I", "LegacyLightBlock", "lightBlock", "SuperLightBlock"),
            ("method_9579", "Z", "LegacySkylight", "propagatesSkylightDown", "SuperSkylight")):
        old = name + "(" + BS + BG + POS + ")" + ret
        new = name + "(" + BS + ")" + ret
        f.hook(old=old, new=new, onlyUnder=BB, adapter=SH + "$" + legacy,
               bridge=SH + "." + bridge + "(L" + SH + "$" + legacy + ";" + BS + ")" + ret,
               superBridge=SH + ".super" + bridge[0].upper() + bridge[1:] + "(L" + SH + "$" + sup + ";" + BS + BG + POS + ")" + ret,
               superAccess=SH + "$" + sup, superAccessors=[new],
               basis="1.21.2 asks the state-only shape; 1.21.1's BlockStateBase.Cache asked a block's three-argument "
                     "one with EmptyBlockGetter.INSTANCE and BlockPos.ZERO, and so does the bridge; the old method's "
                     "super call reaches super's 1.21.2 shape (CbLevelShapes)")
        f.put("instanceRedirects", BB + "." + old, SH)
    f.put("instanceRedirects", BB + ".method_42326(" + BS + BG + POS + ")Z", SH)
    for name, args, ret, legacy, bridge, sup, nargs in (
            ("method_26201", BG + POS, VS, "LegacyStateOcclusion", "stateOcclusionShape", "SuperStateOcclusion", ""),
            ("method_26173", BG + POS + DIR, VS, "LegacyStateFaceOcclusion", "stateFaceOcclusionShape", "SuperStateFaceOcclusion", DIR),
            ("method_26193", BG + POS, "I", "LegacyStateLightBlock", "stateLightBlock", "SuperStateLightBlock", ""),
            ("method_26167", BG + POS, "Z", "LegacyStateSkylight", "statePropagatesSkylightDown", "SuperStateSkylight", ""),
            ("method_26216", BG + POS, "Z", "LegacyStateSolidRender", "stateSolidRender", "SuperStateSolidRender", ""),
            ("method_26226", BG + POS, VEC, "LegacyStateOffset", "stateOffset", "SuperStateOffset", POS)):
        old = name + "(" + args + ")" + ret
        new = name + "(" + nargs + ")" + ret
        f.hook(old=old, new=new, onlyUnder=BSB, adapter=SH + "$" + legacy,
               bridge=SH + "." + bridge + "(L" + SH + "$" + legacy + ";" + nargs + ")" + ret,
               superBridge=SH + ".super" + bridge[0].upper() + bridge[1:] + "(L" + SH + "$" + sup + ";" + args + ")" + ret,
               superAccess=SH + "$" + sup, superAccessors=[new],
               basis="a 1.21.1 BlockStateBase subclass's level-and-position shape query, asked as 1.21.1's cache asked "
                     "it (EmptyBlockGetter.INSTANCE, BlockPos.ZERO; getOffset with the real position); its super call "
                     "reaches 1.21.2's (CbLevelShapes)")
        f.put("instanceRedirects", BSB + "." + old, SH)
    # BlockBehaviour.OffsetFunction.evaluate(state, level, pos) -> evaluate(state, pos)
    oold = "evaluate(" + BS + BG + POS + ")" + VEC
    f.put("lambdaAdapters", BB + "$class_8176." + oold,
          {"iface": SH + "$LegacyOffsetFunction",
           "adapt": SH + "#offsetFunction(L" + SH + "$LegacyOffsetFunction;)L" + BB + "$class_8176;",
           "basis": "a 1.21.1 offset function lambda, as 1.21.2's evaluate(state, pos): 1.21.2 asks without a level"})
    f.w["adapterHooks"].append({
        "under": BB + "$class_8176", "adapter": SH + "$LegacyOffsetFunction", "requires": [oold],
        "synth": [{"new": "evaluate(" + BS + POS + ")" + VEC,
                   "call": SH + ".evaluate(L" + SH + "$LegacyOffsetFunction;" + BS + POS + ")" + VEC}],
        "basis": "a 1.21.1 class implementing OffsetFunction is asked 1.21.2's evaluate(state, pos) (CbLevelShapes)"})


# ------------------------------------------------------------------------------------------- useItemOn
STACK, LVL, PLAYER, HAND, HIT = L("class_1799"), L("class_1937"), L("class_1657"), L("class_1268"), L("class_3965")
IIR, IR = L("class_9062"), L("class_1269")
FIIR = FAC + "/ItemInteractionResult"


def use_item_on(f):
    to_r = FIIR + ".toResult(L" + FIIR + ";)" + IR
    from_r = "conv " + FIIR + "#fromResult(" + IR + ")" + IIR
    LU = RT + "/CbLevelUse"
    for owner, name, args, sup, bridge in (
            (BB, "method_55765", STACK + BS + LVL + POS + PLAYER + HAND + HIT, "Super", "superUseItemOn"),
            (BSB, "method_55780", STACK + LVL + PLAYER + HAND + HIT, "SuperState", "superStateUseItemOn")):
        old = name + "(" + args + ")" + IIR
        new = name + "(" + args + ")" + IR
        f.reshape(owner + "." + old, new[len(name):], ["keep"] * args.count(";"),
                  "useItemOn answers an InteractionResult since 1.21.2: 1.21.1's ItemInteractionResult of it "
                  "(ItemInteractionResult.fromResult)", ret=from_r)
        f.hook(old=old, new=new, onlyUnder=owner, unwrap=to_r,
               superBridge=LU + "." + bridge + "(L" + LU + "$" + sup + ";" + args + ")" + IIR,
               superAccess=LU + "$" + sup, superAccessors=[new],
               basis="vanilla asks a 1.21.1 block's useItemOn for an InteractionResult: its answer converted "
                     "(PASS_TO_DEFAULT is TRY_WITH_EMPTY_HAND, after which 1.21.2 goes on to useWithoutItem as 1.21.1 "
                     "did); its super call reaches super's 1.21.2 useItemOn (CbLevelUse)")


# ------------------------------------------------------------------------------------------- descriptions, loot
LB = RT + "/CbLevelBlocks"
LT = RT + "/CbLevelLoot"
RK = L("class_5321")


def names_and_loot(f):
    old = "method_9539()Ljava/lang/String;"
    f.put("instanceRedirects", MC + "class_2248." + old, LB)
    f.w["overrideRenames"].append({
        "old": old, "new": "cb$getDescriptionId", "onlyUnder": MC + "class_2248", "adapter": LB + "$Legacy",
        "basis": "BlockBehaviour.getDescriptionId is final since 1.21.2: a 1.21.1 block's override is renamed and the "
                 "final method hands the call to it (CbLevelBlockNameMixin); its super call answers the 1.21.2 id"})
    f.put("instanceRedirects", BB + ".method_26162()" + RK, LT)
    f.put("instanceRedirects", BB + "$class_2251.method_16228(" + L("class_2248") + ")" + L("class_4970$class_2251"), LT)
    f.put("instanceFieldGetters", BB + ".field_23156:" + RK, LT)
    f.put("fieldGetters", MC + "class_39.field_844:" + RK, LT)
    for fld in ("field_869", "field_814", "field_224", "field_461", "field_385", "field_702", "field_629", "field_878",
                "field_806", "field_365", "field_285", "field_394", "field_489", "field_607", "field_716", "field_778"):
        f.put("fieldGetters", MC + "class_39." + fld + ":" + RK, LT)


# ------------------------------------------------------------------------------------------- scheduled ticks
def schedule_tick(f):
    # LevelAccessor's four scheduleTick defaults moved to ScheduledTickAccess (LevelAccessor's superinterface)
    # under new intermediary names, same descriptors and bodies (getBlockTicks/getFluidTicks().schedule(createTick))
    TP, FL, BLK = L("class_1953"), L("class_3611"), L("class_2248")
    for old, new, d in (("method_39280", "method_64311", "(" + POS + BLK + "I" + TP + ")V"),
                        ("method_39279", "method_64310", "(" + POS + BLK + "I)V"),
                        ("method_39282", "method_64313", "(" + POS + FL + "I" + TP + ")V"),
                        ("method_39281", "method_64312", "(" + POS + FL + "I)V")):
        f.put("methodRenames", MC + "class_1936." + old + d, new)
        f.w["overrideRenames"].append({
            "old": old + d, "new": new, "onlyUnder": MC + "class_1936", "superCalls": "rename",
            "basis": "scheduleTick is ScheduledTickAccess's since 1.21.2: the same default body, under its new name"})


# ------------------------------------------------------------------------------------------- neighbor updates
NB = RT + "/CbLevelNeighbors"
BLK, ORI = L("class_2248"), L("class_9904")
LEVEL, NU = MC + "class_1937", MC + "class_7165"


def neighbors(f):
    # Block.neighborChanged(state, level, pos, block, fromPos, moved) -> (.., Orientation, moved)
    args_old = BS + LVL + POS + BLK + POS + "Z"
    args_new = BS + LVL + POS + BLK + ORI + "Z"
    old = "method_9612(" + args_old + ")V"
    new = "method_9612(" + args_new + ")V"
    f.hook(old=old, new=new, onlyUnder=BB, adapter=NB + "$Legacy",
           bridge=NB + ".neighborChanged(L" + NB + "$Legacy;" + args_new + ")V",
           superBridge=NB + ".superNeighborChanged(L" + NB + "$Super;" + args_old + ")V",
           superAccess=NB + "$Super", superAccessors=[new],
           basis="1.21.2 tells a block an Orientation where 1.21.1 told it the neighbor's position: the 1.21.1 override "
                 "gets the source the update was made for (recorded where 1.21.2 starts it, CbLevelNeighbors); its super "
                 "call reaches super's 1.21.2 neighborChanged with the orientation vanilla passed")
    f.put("instanceRedirects", BB + "." + old, NB)
    # BlockStateBase.handleNeighborChanged(level, pos, block, fromPos, moved)
    sa_old = LVL + POS + BLK + POS + "Z"
    sa_new = LVL + POS + BLK + ORI + "Z"
    sold = "method_26181(" + sa_old + ")V"
    snew = "method_26181(" + sa_new + ")V"
    f.hook(old=sold, new=snew, onlyUnder=BSB, adapter=NB + "$LegacyState",
           bridge=NB + ".handleNeighborChanged(L" + NB + "$LegacyState;" + sa_new + ")V",
           superBridge=NB + ".superHandleNeighborChanged(L" + NB + "$SuperState;" + sa_old + ")V",
           superAccess=NB + "$SuperState", superAccessors=[snew],
           basis="BlockState.handleNeighborChanged, as Block.neighborChanged (CbLevelNeighbors)")
    f.put("instanceRedirects", BSB + "." + sold, NB)
    # Level.neighborChanged(pos, block, fromPos) / (state, pos, block, fromPos, moved)
    for name, a_old, a_new, legacy, sup, bridge in (
            ("method_8492", POS + BLK + POS, POS + BLK + ORI, "LegacyLevel", "SuperLevel", "levelNeighborChanged"),
            ("method_41410", BS + POS + BLK + POS + "Z", BS + POS + BLK + ORI + "Z", "LegacyLevelFull", "SuperLevelFull",
             "levelNeighborChangedFull")):
        o = name + "(" + a_old + ")V"
        n = name + "(" + a_new + ")V"
        f.put("instanceRedirects", LEVEL + "." + o, NB)
        f.hook(old=o, new=n, onlyUnder=LEVEL, adapter=NB + "$" + legacy,
               bridge=NB + "." + bridge + "(L" + NB + "$" + legacy + ";" + a_new + ")V",
               superBridge=NB + ".super" + bridge[0].upper() + bridge[1:] + "(L" + NB + "$" + sup + ";" + a_old + ")V",
               superAccess=NB + "$" + sup, superAccessors=[n],
               basis="a 1.21.1 Level subclass's neighborChanged override, told the source its caller named "
                     "(CbLevelNeighbors)")
    # NeighborUpdater.neighborChanged(pos, block, fromPos) / (state, pos, block, fromPos, moved), interface methods
    for name, a_old, a_new, legacy, sup, bridge in (
            ("method_41704", POS + BLK + POS, POS + BLK + ORI, "LegacyUpdater", "SuperUpdater", "updaterNeighborChanged"),
            ("method_41703", BS + POS + BLK + POS + "Z", BS + POS + BLK + ORI + "Z", "LegacyUpdaterFull", "SuperUpdaterFull",
             "updaterNeighborChangedFull")):
        o = name + "(" + a_old + ")V"
        n = name + "(" + a_new + ")V"
        f.put("instanceRedirects", NU + "." + o, NB)
        f.hook(old=o, new=n, onlyUnder=NU, adapter=NB + "$" + legacy,
               bridge=NB + "." + bridge + "(L" + NB + "$" + legacy + ";" + a_new + ")V",
               superBridge=NB + ".super" + bridge[0].upper() + bridge[1:] + "(L" + NB + "$" + sup + ";" + a_old + ")V",
               superAccess=NB + "$" + sup, superAccessors=[n],
               basis="a 1.21.1 NeighborUpdater implementation's neighborChanged, told the source its caller named "
                     "(CbLevelNeighbors)")
    f.put("staticRedirects", NU + ".method_41708(" + LVL + BS + POS + BLK + POS + "Z)V", NB)
    # Level.updateNeighborsAt(pos, block) -> updateNeighborsAt(pos, block) is method_61271 now; method_8452 took an orientation
    f.put("methodRenames", LEVEL + ".method_8452(" + POS + BLK + ")V", "method_61271")
    f.put("callReshapes", LEVEL + ".method_8508(" + POS + BLK + DIR + ")V",
          {"desc": "(" + POS + BLK + DIR + ORI + ")V",
           "ops": ["keep:0", "keep:1", "keep:2", "push " + NB + "#noOrientation()" + ORI],
           "basis": "updateNeighborsAtExceptFromFacing takes an Orientation since 1.21.2; none is what 1.21.2 passes "
                    "for an update without one (MultiNeighborUpdate works it out when the experiments are on)"})
    f.put("callReshapes", NU + ".method_41705(" + POS + BLK + DIR + ")V",
          {"desc": "(" + POS + BLK + DIR + ORI + ")V",
           "ops": ["keep:0", "keep:1", "keep:2", "push " + NB + "#noOrientation()" + ORI],
           "basis": "NeighborUpdater.updateNeighborsAtExceptFromFacing, as Level's"})
    # the mechanical hooks of Level.updateNeighborsAt / updateNeighborsAtExceptFromFacing and NeighborUpdater's
    # (mechanical.json) carry the orientation; entered directly, their super call passes none, which is what 1.21.2
    # passes there itself: ServerLevel hands it to MultiNeighborUpdate, which computes initialOrientation(level,
    # skipped direction's opposite, null) when it has none -- the value Level.updateNeighborsAt(pos, block) computes
    why = "zero: no orientation -- 1.21.2's MultiNeighborUpdate computes the initial one itself when it is handed none"
    f.put("frameDefaults", "method_8452(" + POS + BLK + ")V", {"2": why})
    f.put("frameDefaults", "method_8508(" + POS + BLK + DIR + ")V", {"3": why})
    f.put("frameDefaults", "method_41705(" + POS + BLK + DIR + ")V", {"3": why})


# ------------------------------------------------------------------------------------------- explosions
EX = MC + "class_1927"
LX = RT + "/CbLevelExplosions"


def explosions(f):
    f.put("superRenames", EX, FAC + "/Explosion")
    ent, lst, bi, po, snd = L("class_1297"), "Ljava/util/List;", L("class_1927$class_4179"), L("class_2394"), L("class_6880")
    for args in (LVL + ent + "DDDF" + lst + bi + po + po + snd,
                 LVL + ent + "DDDFZ" + bi + lst,
                 LVL + ent + "DDDFZ" + bi,
                 LVL + ent + L("class_1282") + L("class_5362") + "DDDFZ" + bi + po + po + snd):
        f.put("ctorRedirects", EX + ".<init>(" + args + ")V", LX)
    f.put("staticRedirects", EX + ".method_17752(" + L("class_243") + ent + ")F", LX)
    for m in ("method_8348()V", "method_8350(Z)V", "method_46667()Z", "method_8351()Ljava/util/Map;", "method_8352()V",
              "method_8346()Ljava/util/List;", "method_55112()" + po, "method_55113()" + po, "method_55114()" + snd):
        f.put("instanceRedirects", EX + "." + m, LX)
    ds, calc, ia, v3 = L("class_1282"), L("class_5362"), L("class_1937$class_7867"), L("class_243")
    for m in ("method_8437(" + ent + "DDDF" + ia + ")",
              "method_8537(" + ent + "DDDFZ" + ia + ")",
              "method_46407(" + ent + ds + calc + v3 + "FZ" + ia + ")",
              "method_55117(" + ent + ds + calc + "DDDFZ" + ia + ")",
              "method_8454(" + ent + ds + calc + "DDDFZ" + ia + po + po + snd + ")",
              "method_46668(" + ent + ds + calc + "DDDFZ" + ia + "Z" + po + po + snd + ")"):
        f.put("instanceRedirects", LEVEL + "." + m + L("class_1927"), LX)
    # a 1.21.1 Level subclass's explode overrides: vanilla asks 1.21.2's void shapes
    EXR = L("class_1927")
    full = ent + ds + calc + "DDDFZ" + ia + po + po + snd
    shapes_ = (("method_8437", ent + "DDDF" + ia, "8437"), ("method_8537", ent + "DDDFZ" + ia, "8537"),
               ("method_46407", ent + ds + calc + v3 + "FZ" + ia, "46407"),
               ("method_55117", ent + ds + calc + "DDDFZ" + ia, "55117"), ("method_8454", full, "8454"))
    old46668 = "method_46668(" + ent + ds + calc + "DDDFZ" + ia + "Z" + po + po + snd + ")" + EXR
    for name, a, n in shapes_:
        old = name + "(" + a + ")" + EXR
        new = name + "(" + a + ")V"
        h = dict(old=old, new=new, onlyUnder=LEVEL, adapter=LX + "$Legacy" + n,
                 bridge=LX + ".explode" + n + "(L" + LX + "$Legacy" + n + ";" + a + ")V",
                 superBridge=LX + ".superExplode" + n + "(L" + LX + "$Super" + n + ";" + a + ")" + EXR,
                 superAccess=LX + "$Super" + n, superAccessors=[new],
                 basis="1.21.2's explode answers nothing: vanilla asking a 1.21.1 level's override runs it and drops its "
                       "explosion; its super call answers the one 1.21.1's Level/ServerLevel dispatch made "
                       "(CbLevelExplosions)")
        if n == "8454":
            h["shadows"] = [old46668]
        f.hook(**h)
    f.hook(old=old46668, new="method_8454(" + full + ")V", onlyUnder=LEVEL, adapter=LX + "$Legacy46668",
           bridge=LX + ".explode46668(L" + LX + "$Legacy46668;" + full + ")V",
           superBridge=LX + ".superExplode46668(L" + LX + "$Super8454;" + old46668[len("method_46668("):-len(EXR) - 1] + ")" + EXR,
           superAccess=LX + "$Super8454", superAccessors=["method_8454(" + full + ")V"],
           basis="1.21.1's Level.method_8454 called this.method_46668(.., true, ..) (ServerLevel's: false, then the "
                 "packets): a level overriding only method_46668 is asked 1.21.2's method_8454 as 1.21.1's inherited "
                 "one would have asked it; method_8454's own hook takes precedence in a class overriding both")
    f.w["adapterHooks"].append({
        "under": LEVEL, "adapter": LX + "$Legacy46668", "requires": [old46668], "synth": [],
        "basis": "a level overriding 1.21.1's method_46668 is reached through it by the method_8454 bridges (1.21.1's "
                 "this.method_46668 dispatch), also in a class whose own method_8454 hook takes the new shape"})
    # ExplosionDamageCalculator.getEntityDamageAmount(explosion, entity) -> (.., seenPercent)
    xe = L("class_1927") + ent
    f.reshape(MC + "class_5362.method_55115(" + xe + ")F", "(" + xe + "F)F",
              ["keep:0", "keep:1", "conv:0,1 " + LX + "#seenPercent(" + xe + ")F"],
              "getEntityDamageAmount takes the seen percent since 1.21.2; 1.21.1's computed it as "
              "Explosion.getSeenPercent(explosion.center(), entity)")
    f.put("frameDefaults", "method_55115(" + xe + ")F",
          {"2": LX + "#seenPercent(" + calc + xe + ")F"})


# ------------------------------------------------------------------------------------------- carving steps
LC = RT + "/CbLevelCarving"


def carving(f):
    C, MASK, CP = L("class_2893$class_2894"), L("class_6643"), L("class_1923")
    f.put("classRenames", MC + "class_2893$class_2894", FAC + "/GenerationStepCarving")
    f.put("classRenames", MC + "class_5851", FAC + "/CarvingMaskPlacement")
    f.put("fieldGetters", MC + "class_6798.field_35751:" + L("class_6798"), LC)
    PB, BU = MC + "class_5485$class_7868", MC + "class_5485$class_5495"
    gen_args = L("class_3233") + "J" + L("class_7138") + L("class_4543") + L("class_5138") + L("class_2791")
    # (owner, name, old args, new args, return, legacy, bridge, super interface, super bridge)
    fams = ((MC + "class_2794", "method_12108", gen_args + C, gen_args, "V", "ApplyCarvers", "applyCarvers"),
            (MC + "class_2839", "method_12025", C, "", MASK, "CarvingMask", "carvingMask"),
            (MC + "class_2839", "method_28510", C, "", MASK, "OrCreateCarvingMask", "orCreateCarvingMask"),
            (MC + "class_2839", "method_12307", C + MASK, MASK, "V", "SetCarvingMask", "setCarvingMask"),
            (MC + "class_5444", "method_30459", CP + C, CP, MASK, "ContextCarvingMask", "contextCarvingMask"),
            (PB, "method_46675", C + L("class_6880"), L("class_6880"), "L" + PB + ";", "PlainAddCarver", "plainAddCarver"),
            (BU, "method_30991", C + L("class_5321"), L("class_5321"), "L" + BU + ";", "AddCarver", "addCarver"))
    for owner, name, old_a, new_a, ret, n, br in fams:
        old = name + "(" + old_a + ")" + ret
        new = name + "(" + new_a + ")" + ret
        f.put("instanceRedirects", owner + "." + old, LC)
        f.hook(old=old, new=new, onlyUnder=owner, adapter=LC + "$Legacy" + n,
               bridge=LC + "." + br + "(L" + LC + "$Legacy" + n + ";" + new_a + ")" + ret,
               superBridge=LC + ".super" + br[0].upper() + br[1:] + "(L" + LC + "$Super" + n + ";" + old_a + ")" + ret,
               superAccess=LC + "$Super" + n, superAccessors=[new],
               basis="1.21.2 keeps one carving step, 1.21.1's AIR: vanilla asks a 1.21.1 override for it; the "
                     "override's super call reaches 1.21.2's for AIR and 1.21.1's second step, LIQUID, otherwise "
                     "(CbLevelCarving)")
    f.put("instanceRedirects", MC + "class_5485.method_30976(" + C + ")Ljava/lang/Iterable;", LC)


# ------------------------------------------------------------------------------------------- chunk state
LCH = RT + "/CbLevelChunks"


def chunk_state(f):
    CA = MC + "class_2791"
    for m in ("method_12008(Z)V", "method_39294(" + L("class_6749") + ")V", "method_12029(SI)V",
              "method_39296()" + L("class_2791$class_6745")):
        f.put("instanceRedirects", CA + "." + m, LCH)
    f.put("instanceRedirects", MC + "class_2818.method_12221()V", LCH)
    f.put("instanceRedirects", MC + "class_1922.method_8315()I", LCH)
    for c in ("comp_239", "comp_240"):
        f.put("instanceRedirects", MC + "class_2791$class_6745." + c + "()" + L("class_6761"), LCH)
    f.put("ctorRedirects", MC + "class_2791$class_6745.<init>(" + L("class_6761") + L("class_6761") + ")V", LCH)
    f.put("instanceFieldGetters", CA + ".field_34537:Z", LCH)
    f.put("instanceFieldSetters", CA + ".field_34537:Z", LCH)
    sup = dict(superBridge=LCH + ".superSetUnsaved(L" + LCH + "$SuperSetUnsaved;Z)V", superAccess=LCH + "$SuperSetUnsaved",
               superAccessors=["method_65063()V", "method_65064()Z"])
    f.hook(old="method_12008(Z)V", new="method_65063()V", onlyUnder=CA, adapter=LCH + "$LegacySetUnsaved",
           bridge=LCH + ".markUnsaved(L" + LCH + "$LegacySetUnsaved;)V", **sup,
           basis="1.21.2 marks a chunk unsaved with markUnsaved(): a 1.21.1 chunk's setUnsaved override is asked "
                 "setUnsaved(true); its super call is markUnsaved / tryMarkSaved (CbLevelChunks)")
    f.hook(old="method_12008(Z)V", new="method_65064()Z", onlyUnder=CA, adapter=LCH + "$LegacySetUnsaved",
           bridge=LCH + ".tryMarkSaved(L" + LCH + "$LegacySetUnsaved;)Z", **sup,
           basis="1.21.2 saves through tryMarkSaved(): a 1.21.1 chunk's setUnsaved override is asked setUnsaved(false), "
                 "the answer whether it was unsaved (CbLevelChunks)")
    SL = "Lit/unimi/dsi/fastutil/shorts/ShortList;"
    f.hook(old="method_12029(SI)V", new="method_12029(" + SL + "I)V", onlyUnder=CA,
           adapter=LCH + "$LegacyAddPackedPostProcess",
           bridge=LCH + ".addPackedPostProcess(L" + LCH + "$LegacyAddPackedPostProcess;" + SL + "I)V",
           superBridge=LCH + ".superAddPackedPostProcess(L" + LCH + "$SuperAddPackedPostProcess;SI)V",
           superAccess=LCH + "$SuperAddPackedPostProcess", superAccessors=["method_12029(" + SL + "I)V"],
           basis="1.21.2 adds post-process positions a list at a time: a 1.21.1 override is asked each in turn; its "
                 "super call adds the list of one (CbLevelChunks)")
    f.w["adapterHooks"].append({
        "under": CA, "adapter": LCH + "$LegacySetBlendingData", "requires": ["method_39294(" + L("class_6749") + ")V"],
        "synth": [], "basis": "nothing in 1.21.1's or 1.21.2's vanilla code calls setBlendingData: a 1.21.1 override "
                              "is reached by its mod's calls, which find it through this adapter (CbLevelChunks)"})
    f.put("superRedirects", CA + ".method_39294(" + L("class_6749") + ")V", LCH + "#setBlendingData")
    f.w["adapterHooks"].append({
        "under": MC + "class_1922", "adapter": LCH + "$LegacyMaxLightLevel", "requires": ["method_8315()I"],
        "synth": [], "basis": "1.21.2's lighting no longer asks getMaxLightLevel (fixed at 15): a 1.21.1 override is "
                              "reached by its mod's calls, which find it through this adapter (CbLevelChunks)"})
    f.put("superRedirects", MC + "class_1922.method_8315()I", LCH + "#maxLightLevel")


# ------------------------------------------------------------------------------------------- chunk storage
LST = RT + "/CbLevelStorage"


def chunk_storage(f):
    POS, TAG, CF, SUP = L("class_1923"), L("class_2487"), "Ljava/util/concurrent/CompletableFuture;", "Ljava/util/function/Supplier;"
    SRS = MC + "class_3977"
    f.reshape(SRS + ".method_17910(" + POS + TAG + ")" + CF, "(" + POS + SUP + ")" + CF,
              ["keep:0", "conv:1 " + LST + "#supplier(" + TAG + ")" + SUP],
              "SimpleRegionStorage.write takes a supplier of the tag since 1.21.2: the tag 1.21.1's caller handed")
    f.hook(old="method_17910(" + POS + TAG + ")" + CF, new="method_17910(" + POS + SUP + ")" + CF, onlyUnder=SRS,
           adapter=LST + "$LegacyWrite", bridge=LST + ".write(L" + LST + "$LegacyWrite;" + POS + SUP + ")" + CF,
           superBridge=LST + ".superWrite(L" + LST + "$SuperWrite;" + POS + TAG + ")" + CF,
           superAccess=LST + "$SuperWrite", superAccessors=["method_17910(" + POS + SUP + ")" + CF],
           basis="vanilla writes through write(pos, supplier) since 1.21.2: a 1.21.1 override gets the supplied tag; "
                 "its super call supplies the tag it passes (CbLevelStorage)")
    FN, BIF, COD = "Ljava/util/function/Function;", "Ljava/util/function/BiFunction;", "Lcom/mojang/serialization/Codec;"
    old = "(" + L("class_9172") + FN + FN + L("class_5455") + L("class_9820") + L("class_5539") + ")V"
    f.put("superCtorReshapes", MC + "class_4180.<init>" + old, {
        "desc": "(" + L("class_9172") + COD + FN + BIF + FN + L("class_5455") + L("class_9820") + L("class_5539") + ")V",
        "ops": ["keep:0", "conv:1 " + LST + "#packedCodec(" + FN + ")" + COD,
                "conv:1,3 " + LST + "#packer(" + FN + L("class_5455") + ")" + FN,
                "conv:1 " + LST + "#unpacker(" + FN + ")" + BIF, "keep:2", "keep:3", "keep:4", "keep:5"],
        "basis": "SectionStorage keeps sections in a packed form since 1.21.2: the packed form is the section's data, "
                 "parsed and written with 1.21.1's per-section codec (CbLevelStorage)"})
    CS = MC + "class_2852"
    f.put("staticRedirects", CS + ".method_12395(" + L("class_3218") + L("class_4153") + L("class_9240") + POS + TAG + ")"
          + L("class_2839"), LST)
    f.put("staticRedirects", CS + ".method_12410(" + L("class_3218") + L("class_2791") + ")" + TAG, LST)
    why = ("1.21.1's ChunkSerializer was a static utility (no state, no instance methods); 1.21.2 reuses its "
           "intermediary name class_2852 for the final record SerializableChunkData, so no object of that type can "
           "stand for a ChunkSerializer instance and no class can extend it; its static API is bridged")
    f.put("ledger", CS + ".<init>", why)
    f.put("ledger", CS, why)
    WGC = MC + "class_9312"
    f.put("instanceRedirects", WGC + ".comp_2808()" + L("class_3906"), LST)
    f.put("superCtorReshapes", WGC + ".<init>(" + L("class_3218") + L("class_2794") + L("class_3485") + L("class_3227")
          + L("class_3906") + ")V", {
        "desc": "(" + L("class_3218") + L("class_2794") + L("class_3485") + L("class_3227") + "Ljava/util/concurrent/Executor;"
                + L("class_2818$class_10368") + ")V",
        "ops": ["keep:0", "keep:1", "keep:2", "keep:3",
                "conv:4 " + LST + "#executor(" + L("class_3906") + ")Ljava/util/concurrent/Executor;",
                "conv:0 " + LST + "#unsavedListener(" + L("class_3218") + ")" + L("class_2818$class_10368")],
        "basis": "WorldGenContext takes the main-thread executor and the chunks' unsaved listener since 1.21.2: the "
                 "executor of 1.21.1's mailbox (CbRestExecutors) and the level ChunkMap's listener (CbLevelStorage)"})


# ------------------------------------------------------------------------------------------- fluids and light
LFL = RT + "/CbLevelFluids"
LLI = RT + "/CbLevelLight"


def fluids_light(f):
    SL, FS, RS = L("class_3218"), L("class_3610"), L("class_5819")
    srv = "conv:0 " + LFL + "#server(" + LVL + ")" + SL
    st = "conv:0,1 " + LFL + "#stateAt(" + LVL + POS + ")" + BS
    why = ("takes the server level since 1.21.2 (and the block state at the position, which its callers read off the "
           "level): 1.21.1 ran fluids on server levels only (CbLevelFluids)")
    for owner, name, old_a, new_a, ret, ops in (
            ("class_3611", "method_15778", LVL + POS + FS, SL + POS + BS + FS, "V", [srv, "keep:1", st, "keep:2"]),
            ("class_3609", "method_15725", LVL + POS + FS, SL + POS + BS + FS, "V", [srv, "keep:1", st, "keep:2"]),
            ("class_3609", "method_15727", LVL + POS + BS, SL + POS + BS, FS, [srv, "keep:1", "keep:2"]),
            ("class_3609", "method_15737", LVL, SL, "Z", [srv]),
            ("class_3609", "method_15726", LVL + POS + BS, SL + POS + BS, "Ljava/util/Map;", [srv, "keep:1", "keep:2"]),
            ("class_3611", "method_15792", LVL + POS + FS + RS, SL + POS + FS + RS, "V", [srv, "keep:1", "keep:2", "keep:3"]),
            ("class_3610", "method_15770", LVL + POS, SL + POS + BS, "V", [srv, "keep:1", st]),
            ("class_3610", "method_15757", LVL + POS + RS, SL + POS + RS, "V", [srv, "keep:1", "keep:2"])):
        f.reshape(MC + owner + "." + name + "(" + old_a + ")" + ret, "(" + new_a + ")" + ret, ops, name + " " + why)
    LE, SH_, DIRS = MC + "class_3558", L("class_265"), DIR
    lwhy = ("1.21.2 asks a block's light properties of the state alone, as BlockStateBase's cache answered them for "
            "1.21.1 (CbLevelLight)")
    f.reshape(LE + ".method_51561(" + BG + POS + BS + BS + ")Z", "(" + BS + BS + ")Z", ["keep:2", "keep:3"], lwhy)
    f.reshape(LE + ".method_20049(" + BG + BS + POS + BS + POS + DIRS + "I)I", "(" + BS + BS + DIRS + "I)I",
              ["keep:1", "keep:3", "keep:5", "keep:6"], lwhy)
    f.reshape(LE + ".method_51562(" + BG + POS + BS + DIRS + ")" + SH_, "(" + BS + DIRS + ")" + SH_, ["keep:2", "keep:3"], lwhy)
    f.reshape(LE + ".method_50017(" + BS + POS + ")I", "(" + BS + ")I", ["keep:0"], lwhy)
    f.reshape(LE + ".method_50016(J" + BS + "J" + BS + DIRS + ")Z", "(" + BS + BS + DIRS + ")Z", ["keep:1", "keep:3", "keep:4"], lwhy)
    for old, new, n, br, sup_old in (
            ("method_50017(" + BS + POS + ")I", "method_50017(" + BS + ")I", "Opacity", "opacity", BS + POS),
            ("method_50016(J" + BS + "J" + BS + DIRS + ")Z", "method_50016(" + BS + BS + DIRS + ")Z", "ShapeOccludes",
             "shapeOccludes", "J" + BS + "J" + BS + DIRS)):
        nargs, ret = new[new.index("("):new.index(")") + 1], new[new.index(")") + 1:]
        f.hook(old=old, new=new, onlyUnder=LE, adapter=LLI + "$Legacy" + n,
               bridge=LLI + "." + br + "(L" + LLI + "$Legacy" + n + ";" + nargs[1:-1] + ")" + ret,
               superBridge=LLI + ".super" + n + "(L" + LLI + "$Super" + n + ";" + sup_old + ")" + ret,
               superAccess=LLI + "$Super" + n, superAccessors=[new],
               basis="a 1.21.1 light engine's override, asked in 1.21.2's state-only shape: the origin as its position; "
                     "its super call reaches 1.21.2's (CbLevelLight)")
    occ = "method_20710(" + BS + "J" + DIRS + ")" + SH_
    f.put("instanceRedirects", LE + "." + occ, LLI)
    f.put("superRedirects", LE + "." + occ, LLI + "#occlusionShape")
    f.w["adapterHooks"].append({"under": LE, "adapter": LLI + "$LegacyOcclusionShape", "requires": [occ], "synth": [],
                                "basis": "1.21.2's engine asks the static occlusion shape: a 1.21.1 override is reached "
                                         "by its mod's calls, through this adapter (CbLevelLight)"})
    LLE, SEC = MC + "class_3568", L("class_4076")
    f.put("instanceRedirects", LLE + ".method_51559(" + SEC + ")Z", LFL)
    f.hook(old="method_51559(" + SEC + ")Z", new="method_62874(J)Z", onlyUnder=LLE,
           adapter=LFL + "$LegacyLightOnInSection", bridge=LFL + ".lightOnInColumn(L" + LFL + "$LegacyLightOnInSection;J)Z",
           superBridge=LFL + ".superLightOnInSection(L" + LFL + "$SuperLightOnInColumn;" + SEC + ")Z",
           superAccess=LFL + "$SuperLightOnInColumn", superAccessors=["method_62874(J)Z"],
           basis="1.21.2 asks lightOnInColumn(column key); 1.21.1's lightOnInSection looked up the section's column "
                 "(SectionPos.getZeroNode): a 1.21.1 override is asked for the column's bottom section (CbLevelFluids)")


# ------------------------------------------------------------------------------------------- Level-wide API
LLV = RT + "/CbLevelLevels"


def level_api(f):
    GR, PF, SUPP, RM, RA = L("class_1928"), L("class_3695"), "Ljava/util/function/Supplier;", L("class_1863"), L("class_10286")
    for m, n, base in (("method_8450()" + GR, "GameRules", "gameRules"), ("method_16107()" + PF, "Profiler", "profiler"),
                       ("method_24367()" + SUPP, "ProfilerSupplier", "profilerSupplier")):
        f.put("instanceRedirects", LEVEL + "." + m, LLV)
        f.put("superRedirects", LEVEL + "." + m, LLV + "#" + base)
        f.w["adapterHooks"].append({"under": LEVEL, "adapter": LLV + "$Legacy" + n, "requires": [m], "synth": [],
                                    "basis": "1.21.2 no longer asks a level this (CbLevelLevels): a 1.21.1 override is "
                                             "reached by its mod's calls, through this adapter"})
    f.put("instanceRedirects", LEVEL + ".method_8433()" + RM, LLV)
    f.hook(old="method_8433()" + RM, new="method_8433()" + RA, onlyUnder=LEVEL,
           unwrap=LLV + ".recipeAccess(" + RM + ")" + RA,
           basis="1.21.2 asks a level its recipeAccess(), which a RecipeManager is: a 1.21.1 level's getRecipeManager "
                 "answers it (CbLevelLevels)")
    f.w["adapterHooks"].append({"under": LEVEL, "adapter": LLV + "$LegacyRecipeManager", "requires": ["method_8433()" + RM],
                                "synth": [], "basis": "a 1.21.1 level's getRecipeManager, reached by its mod's calls"})
    f.put("instanceRedirects", MC + "class_5217.method_146()" + GR, LLV)
    f.w["adapterHooks"].append({"under": MC + "class_5217", "adapter": LLV + "$LegacyDataGameRules",
                                "requires": ["method_146()" + GR], "synth": [],
                                "basis": "1.21.2's LevelData has no getGameRules (ServerLevelData keeps it): a 1.21.1 "
                                         "implementation's is reached by its mod's calls, through this adapter"})
    FF = L("class_7699")
    push = "push " + LLV + "#defaultFeatures()" + FF
    f.put("superCtorReshapes", MC + "class_1928.<init>()V", {"desc": "(" + FF + ")V", "ops": [push],
          "basis": "GameRules is made for a feature set since 1.21.2: the default one (1.21.1 had no gated rules)"})
    f.put("superCtorReshapes", MC + "class_1928.<init>(Lcom/mojang/serialization/DynamicLike;)V",
          {"desc": "(" + FF + "Lcom/mojang/serialization/DynamicLike;)V", "ops": [push, "keep"],
           "basis": "GameRules is made for a feature set since 1.21.2: the default one (1.21.1 had no gated rules)"})
    f.put("instanceRedirects", MC + "class_1928.method_27325()" + GR, LLV)
    f.put("staticRedirects", MC + "class_1928.method_20744(" + L("class_1928$class_4311") + ")V", LLV)
    WLD, RKL, RAC, HDT = L("class_5269"), L("class_5321"), L("class_5455"), L("class_6880")
    f.put("superCtorReshapes", LEVEL + ".<init>(" + WLD + RKL + RAC + HDT + SUPP + "ZZJI)V", {
        "desc": "(" + WLD + RKL + RAC + HDT + "ZZJI)V",
        "ops": ["keep", "keep", "keep", "keep", "drop", "keep", "keep", "keep", "keep"],
        "post": LLV + "#constructed(L" + LEVEL + ";" + SUPP + ")V", "postArgs": [4],
        "basis": "Level no longer takes a profiler supplier (1.21.2 has the thread's): the level keeps the one its "
                 "1.21.1 constructor call handed, for its getProfiler (CbLevelLevels)"})
    full = L("class_1297") + L("class_1282") + L("class_5362") + "DDDFZ" + L("class_1937$class_7867") + L("class_2394")         + L("class_2394") + L("class_6880")
    f.w["abstractFills"].append({"under": LEVEL, "method": "method_8454(" + full + ")V",
                                 "call": RT + "/CbLevelExplosions.fill8454(L" + LEVEL + ";" + full + ")V",
                                 "basis": "explode is abstract since 1.21.2; 1.21.1's Level made the explosion itself"})
    f.w["abstractFills"].append({"under": LEVEL, "method": "method_61269()" + L("class_9895"),
                                 "call": LLV + ".fuelValues(L" + LEVEL + ";)" + L("class_9895"),
                                 "basis": "fuelValues is new and abstract in 1.21.2: the vanilla burn times for the "
                                          "level's registries and features"})


# ------------------------------------------------------------------------------------------- EntityGetter targeting
LEN = RT + "/CbLevelEntities"


def entity_getter(f):
    EG, TC, LE_, PL, AB, LST_, CLS = (MC + "class_1924", L("class_4051"), L("class_1309"), L("class_1657"), L("class_238"),
                                      "Ljava/util/List;", "Ljava/lang/Class;")
    for m, n, base in (("method_18462(" + TC + LE_ + ")" + PL, "NearestPlayer", "nearestPlayer"),
                       ("method_18463(" + TC + LE_ + "DDD)" + PL, "NearestPlayerAt", "nearestPlayerAt"),
                       ("method_18461(" + TC + "DDD)" + PL, "NearestPlayerPos", "nearestPlayerPos"),
                       ("method_21726(" + CLS + TC + LE_ + "DDD" + AB + ")" + LE_, "NearestEntityOfClass", "nearestEntityOfClass"),
                       ("method_18468(" + LST_ + TC + LE_ + "DDD)" + LE_, "NearestEntity", "nearestEntity"),
                       ("method_18464(" + TC + LE_ + AB + ")" + LST_, "NearbyPlayers", "nearbyPlayers"),
                       ("method_18466(" + CLS + TC + LE_ + AB + ")" + LST_, "NearbyEntities", "nearbyEntities")):
        f.put("instanceRedirects", EG + "." + m, LEN)
        f.put("superRedirects", EG + "." + m, LEN + "#" + base)
        f.w["adapterHooks"].append({"under": EG, "adapter": LEN + "$Legacy" + n, "requires": [m], "synth": [],
                                    "basis": "1.21.2 moved this default to ServerEntityGetter: a 1.21.1 implementation's "
                                             "override is reached by the calls 1.21.1's defaults made, through this "
                                             "adapter (CbLevelEntities)"})


# ------------------------------------------------------------------------------------------- loot params, moss
LLP = RT + "/CbLevelLootParams"


def loot_params_moss(f):
    LP, P, O = MC + "class_8567", L("class_169"), "Ljava/lang/Object;"
    for m, n, base in (("method_51865(" + P + ")Z", "HasParam", "hasParam"),
                       ("method_51867(" + P + ")" + O, "GetParameter", "getParameter"),
                       ("method_51868(" + P + ")" + O, "GetOptionalParameter", "getOptionalParameter"),
                       ("method_51869(" + P + ")" + O, "GetParamOrNull", "getParamOrNull")):
        f.put("instanceRedirects", LP + "." + m, LLP)
        f.put("superRedirects", LP + "." + m, LLP + "#" + base)
        f.w["adapterHooks"].append({"under": LP, "adapter": LLP + "$Legacy" + n, "requires": [m], "synth": [],
                                    "basis": "1.21.2 reads the parameters off its ContextMap: a 1.21.1 subclass's "
                                             "override is reached by the calls, through this adapter (CbLevelLootParams)"})
    MAP = "Ljava/util/Map;"
    f.put("superCtorReshapes", LP + ".<init>(" + L("class_3218") + MAP + MAP + "F)V", {
        "desc": "(" + L("class_3218") + L("class_10352") + MAP + "F)V",
        "ops": ["keep", "conv " + LLP + "#contextMap(" + MAP + ")" + L("class_10352"), "keep", "keep"],
        "basis": "LootParams holds a ContextMap since 1.21.2: 1.21.1's parameter map as one (CbLevelLootParams)"})
    f.put("methodRenames", MC + "class_47.method_296(" + P + ")" + O, "method_65013")
    f.put("classRenames", MC + "class_5807", FAC + "/MossBlock")


# ------------------------------------------------------------------------------------------- block entities
LBE = RT + "/CbLevelBlockEntities"


def block_entities(f):
    ST, MAP, OPT = L("class_1799"), "Ljava/util/Map;", "Ljava/util/Optional;"
    FU = MC + "class_2609"
    for m in ("method_56120()V", "method_11196()" + MAP, "method_11195(" + ST + ")Z"):
        f.put("staticRedirects", FU + "." + m, LBE)
    srv = "conv:0 " + LBE + "#server(" + LVL + ")" + L("class_3218")
    f.reshape(FU + ".method_31651(" + LVL + POS + BS + L("class_2609") + ")V",
              "(" + L("class_3218") + POS + BS + L("class_2609") + ")V", [srv, "keep:1", "keep:2", "keep:3"],
              "AbstractFurnaceBlockEntity.serverTick takes the server level since 1.21.2 (CbLevelBlockEntities)")
    f.put("instanceRedirects", FU + ".method_11200(" + ST + ")I", LBE)
    f.put("frameDefaults", "method_11200(" + ST + ")I", {"0": LBE + "#fuelValues(L" + FU + ";" + ST + ")" + L("class_9895")})
    CF, LE_ = MC + "class_3924", L("class_1309")
    f.put("staticRedirects", CF + ".method_31666(" + LVL + POS + BS + L("class_3924") + ")V", LBE)
    f.put("instanceRedirects", CF + ".method_17502(" + ST + ")" + OPT, LBE)
    f.put("superRedirects", CF + ".method_17502(" + ST + ")" + OPT, LBE + "#cookableRecipe")
    f.w["adapterHooks"].append({"under": CF, "adapter": LBE + "$LegacyCookableRecipe",
                                "requires": ["method_17502(" + ST + ")" + OPT], "synth": [],
                                "basis": "1.21.2's campfire block looks the recipe up itself: a 1.21.1 override is "
                                         "reached by its mod's calls, through this adapter (CbLevelBlockEntities)"})
    f.put("instanceRedirects", CF + ".method_17503(" + LE_ + ST + "I)Z", LBE)
    f.hook(old="method_17503(" + LE_ + ST + "I)Z", new="method_17503(" + L("class_3218") + LE_ + ST + ")Z", onlyUnder=CF,
           adapter=LBE + "$LegacyPlaceFood",
           bridge=LBE + ".placeFoodIn(L" + LBE + "$LegacyPlaceFood;" + L("class_3218") + LE_ + ST + ")Z",
           superBridge=LBE + ".superPlaceFood(L" + LBE + "$SuperPlaceFood;" + LE_ + ST + "I)Z",
           superAccess=LBE + "$SuperPlaceFood", superAccessors=["method_17503(" + L("class_3218") + LE_ + ST + ")Z"],
           basis="1.21.2's placeFood looks the cook time up itself: a 1.21.1 override is asked with the cook time of the "
                 "recipe 1.21.2 finds (1.21.1's CampfireBlock passed it); its super call is 1.21.1's body (CbLevelBlockEntities)")
    BR, PL, DIRS = MC + "class_8174", L("class_1657"), DIR
    f.put("instanceRedirects", BR + ".method_49215(J" + PL + DIRS + ")Z", LBE)
    f.put("instanceRedirects", BR + ".method_49219()V", LBE)
    f.put("instanceRedirects", BR + ".method_49217(" + PL + ")V", LBE)
    f.put("superRedirects", BR + ".method_49217(" + PL + ")V", LBE + "#unpackLootTable")
    f.w["adapterHooks"].append({"under": BR, "adapter": LBE + "$LegacyUnpackLootTable",
                                "requires": ["method_49217(" + PL + ")V"], "synth": [],
                                "basis": "1.21.2's unpackLootTable is private: a 1.21.1 override is asked where 1.21.2's "
                                         "brush() unpacks (CbLevelBrushableMixin) and by its mod's calls"})
    sig = "(L" + BR + ";J" + PL + DIRS + ")"
    f.put("frameDefaults", "method_49215(J" + PL + DIRS + ")Z",
          {"1": LBE + "#brushLevel" + sig + L("class_3218"), "4": LBE + "#brushStack" + sig + ST})
    f.put("frameDefaults", "method_49219()V", {"0": LBE + "#resetLevel(L" + BR + ";)" + L("class_3218")})
    TS, TSC = MC + "class_8963", L("class_8965")
    tail = L("class_8966") + "II" + L("class_8963$class_8964") + L("class_8962") + L("class_8962$class_9198")
    conv = "conv " + LBE + "#configHolder(" + TSC + ")" + L("class_6880")
    f.put("superCtorReshapes", TS + ".<init>(" + TSC + TSC + tail + ")V", {
        "desc": "(" + L("class_6880") + L("class_6880") + tail + ")V",
        "ops": [conv, conv, "keep", "keep", "keep", "keep", "keep", "keep"],
        "basis": "TrialSpawner takes its configs as holders since 1.21.2: 1.21.1's values as direct holders"})
    f.reshape(TS + ".method_55162(" + LVL + ")Z", "(" + L("class_3218") + ")Z", [srv],
              "TrialSpawner.canSpawnInLevel takes the server level since 1.21.2")
    f.put("staticRedirects", MC + "class_3751$class_4991.method_26401(Ljava/lang/String;)" + OPT, LBE)


# ------------------------------------------------------------------------------------------- state properties
LSP = RT + "/CbLevelStateProps"


def state_props(f):
    PR, COL, LIS, STR = MC + "class_2769", "Ljava/util/Collection;", "Ljava/util/List;", "Ljava/lang/String;"
    f.reshape(PR + ".method_11898()" + COL, "()" + LIS, [],
              "Property.getPossibleValues answers a List since 1.21.2: 1.21.1's caller takes it as the Collection it is",
              ret="keep")
    f.hook(old="method_11898()" + COL, new="method_11898()" + LIS, onlyUnder=PR,
           unwrap=LSP + ".possibleValues(" + COL + ")" + LIS,
           basis="1.21.2 asks a property for a List of its values: a 1.21.1 subclass's collection in its order, each "
                 "value once (CbLevelStateProps)")
    f.w["abstractFills"].append({"under": PR, "method": "method_64218(Ljava/lang/Comparable;)I",
                                 "call": LSP + ".internalIndex(L" + PR + ";Ljava/lang/Comparable;)I",
                                 "basis": "getInternalIndex is new and abstract in 1.21.2: the value's position in "
                                          "the property's values"})
    DP, EP = MC + "class_2753", MC + "class_2754"
    f.put("classRenames", DP, EP)
    f.put("instanceOfRedirects", DP, LSP + "#isDirectionProperty")
    f.put("checkCastRedirects", DP, LSP + "#castDirectionProperty")
    for m in ("method_35305(" + STR + ")", "method_11844(" + STR + "Ljava/util/function/Predicate;)",
              "method_11845(" + STR + "[" + DIR + ")", "method_11843(" + STR + COL + ")"):
        f.put("staticRedirects", DP + "." + m + L("class_2753"), LSP)
    f.reshape(EP + ".method_11847(" + STR + "Ljava/lang/Class;" + COL + ")" + L("class_2754"),
              "(" + STR + "Ljava/lang/Class;" + LIS + ")" + L("class_2754"),
              ["keep:0", "keep:1", "conv:2 " + LSP + "#list(" + COL + ")" + LIS],
              "EnumProperty.create takes a List since 1.21.2: the values in order, each once (1.21.1's ImmutableSet)")
    fin = ("1.21.2 made {0} final; a 1.21.1 subclass cannot load under it, and a facade subclass of Property in its "
           "place is refused by wiring-check (superRename onto a type that survives as final): the mod's {0}-typed "
           "fields, parameters and the vanilla APIs taking {0} would hold a non-{0} instance and fail verification. "
           "Calls, fields and factories of the class are bridged; only subclassing it is not")
    for c, n in (("class_2746", "BooleanProperty"), ("class_2758", "IntegerProperty"), ("class_2754", "EnumProperty")):
        f.put("ledger", MC + c, fin.format(n))
        f.put("ledger", MC + c + ".<init>", fin.format(n))
    f.put("ledger", DP, fin.format("EnumProperty (DirectionProperty is EnumProperty<Direction> there)"))
    f.put("ledger", DP + ".<init>", fin.format("EnumProperty (DirectionProperty is EnumProperty<Direction> there)"))
    f.put("staticRedirects", MC + "class_2688.method_28495(" + COL + "Ljava/lang/Object;)Ljava/lang/Object;", LSP)


# ------------------------------------------------------------------------------------------- remaining level API
LMI = RT + "/CbLevelMisc"


def level_misc(f):
    SL, EX, BIC = L("class_3218"), L("class_1927"), "Ljava/util/function/BiConsumer;"
    srv = "conv:1 " + LBE + "#server(" + LVL + ")" + SL
    why = "takes the server level since 1.21.2: 1.21.1 ran it on server levels (CbLevelBlockEntities.server)"
    f.reshape(BB + ".method_55124(" + BS + LVL + POS + EX + BIC + ")V", "(" + BS + SL + POS + EX + BIC + ")V",
              ["keep:0", srv, "keep:2", "keep:3", "keep:4"], "onExplosionHit " + why)
    f.reshape(BSB + ".method_55225(" + LVL + POS + EX + BIC + ")V", "(" + SL + POS + EX + BIC + ")V",
              ["conv:0 " + LBE + "#server(" + LVL + ")" + SL, "keep:1", "keep:2", "keep:3"], "onExplosionHit " + why)
    f.reshape(MC + "class_2248.method_9586(" + LVL + POS + EX + ")V", "(" + SL + POS + EX + ")V",
              ["conv:0 " + LBE + "#server(" + LVL + ")" + SL, "keep:1", "keep:2"], "wasExploded " + why)
    f.reshape(MC + "class_8886.method_54475(" + LVL + L("class_9694") + ")Ljava/util/Optional;",
              "(" + SL + L("class_9694") + ")Ljava/util/Optional;",
              ["conv:0 " + LBE + "#server(" + LVL + ")" + SL, "keep:1"], "CrafterBlock.getPotentialResults " + why)
    for m in ("method_48162(" + POS + ")" + L("class_1959$class_1963"), "method_33599(" + POS + ")Z",
              "method_39927(" + POS + ")Z", "method_39928(" + POS + ")Z"):
        f.put("instanceRedirects", MC + "class_1959." + m, LMI)
    f.reshape(MC + "class_2248.method_9607(" + BS + BG + POS + DIR + POS + ")Z", "(" + BS + BS + DIR + ")Z",
              ["keep:0", "conv:1,4 " + LMI + "#stateAt(" + BG + POS + ")" + BS, "keep:3"],
              "Block.shouldRenderFace asks with the neighbour state since 1.21.2: the state at the neighbour position")
    B = MC + "class_2246"
    f.put("staticRedirects", B + ".method_9492(Ljava/lang/String;" + L("class_2248") + ")" + L("class_2248"), LMI)
    f.put("staticRedirects", B + ".method_52571(" + L("class_5321") + L("class_2248") + ")" + L("class_2248"), LMI)
    f.put("staticRedirects", B + ".method_26979()V", LMI)
    PS, AX = MC + "class_2424", L("class_2350$class_2351")
    pargs = L("class_1936") + POS + AX

    def pc(n, r):
        return "conv:0,1,2 " + LMI + "#" + n + "(" + pargs + ")" + r
    f.put("ctorRedirects", PS + ".<init>(" + pargs + ")V", LMI)
    f.put("superCtorReshapes", PS + ".<init>(" + pargs + ")V", {
        "desc": "(" + AX + "I" + DIR + POS + "II)V",
        "ops": [pc("portalAxis", AX), pc("portalBlocks", "I"), pc("portalRightDir", DIR), pc("portalBottomLeft", POS),
                pc("portalWidth", "I"), pc("portalHeight", "I")],
        "post": LMI + "#remember(L" + PS + ";" + L("class_1936") + ")V", "postArgs": [0],
        "basis": "PortalShape's 1.21.2 constructor takes the computed shape (opened by the layer's access widener): "
                 "the shape 1.21.2's findAnyShape computes from 1.21.1's level, position and axis; the level is kept "
                 "for createPortalBlocks (CbLevelMisc)"})
    f.put("instanceRedirects", PS + ".method_10363()V", LMI)
    PROV = L("class_7225$class_7874")
    f.put("instanceRedirects", MC + "class_18.method_17919(Ljava/io/File;" + PROV + ")V", LMI)
    f.put("superCtorReshapes", MC + "class_26.<init>(Ljava/io/File;Lcom/mojang/datafixers/DataFixer;" + PROV + ")V", {
        "desc": "(Ljava/nio/file/Path;Lcom/mojang/datafixers/DataFixer;" + PROV + ")V",
        "ops": ["conv " + LMI + "#path(Ljava/io/File;)Ljava/nio/file/Path;", "keep", "keep"],
        "basis": "DimensionDataStorage takes the directory as a Path since 1.21.2"})
    f.put("instanceRedirects", MC + "class_1950.method_37233()" + L("class_3695"), LMI)
    LA_ = L("class_1936")
    f.reshape(MC + "class_4863.method_24948(" + LA_ + ")" + BS, "(" + RND + ")" + BS,
              ["conv:0 " + LMI + "#random(" + LA_ + ")" + RND],
              "GrowingPlantBlock.getStateForPlacement takes the level random since 1.21.2")
    f.reshape(MC + "class_2230.method_9430(" + BS + LA_ + POS + ")V", "(" + BS + BG + STA + RND + POS + ")V",
              ["keep:0", "keep:1 " + BG, "keep:1 " + STA, "conv:1 " + LMI + "#random(" + LA_ + ")" + RND, "keep:2"],
              "tryScheduleDieTick takes the level as reader and tick access and its random since 1.21.2")
    cold = "method_9430(" + BS + LA_ + POS + ")V"
    f.hook(old=cold, new="method_9430(" + BS + BG + STA + RND + POS + ")V", args=[0, 1, 4], onlyUnder=MC + "class_2230",
           narrow=True, frames=G, superCalls="rewrite",
           basis="1.21.2 asks a coral tryScheduleDieTick with reader, tick access and random: every caller reader is "
                 "a LevelAccessor (narrow); the old method super call gets back the tick access and random vanilla passed")
    csig = "(Ljava/lang/Object;" + BS + LA_ + POS + ")"
    f.put("frameDefaults", cold, {"2": LMI + "#coralTicks" + csig + STA, "3": LMI + "#coralRandom" + csig + RND})
    RULE = L("class_6568$class_6569")
    f.put("superCtorReshapes", MC + "class_6582.<init>(Ljava/util/List;)V", {
        "desc": "([" + RULE + ")V", "ops": ["conv " + LMI + "#rules(Ljava/util/List;)[" + RULE],
        "basis": "MaterialRuleList holds an array since 1.21.2"})
    f.put("instanceRedirects", MC + "class_6582.comp_437()Ljava/util/List;", LMI)
    TT = MC + "class_5454"
    V3, POST = L("class_243"), L("class_5454$class_9823")
    f.put("superCtorReshapes", TT + ".<init>(" + SL + V3 + V3 + "FFZ" + POST + ")V", {
        "desc": "(" + SL + V3 + V3 + "FFZZLjava/util/Set;" + POST + ")V",
        "ops": ["keep", "keep", "keep", "keep", "keep", "keep", "push " + LMI + "#notPassenger()Z",
                "push " + LMI + "#noRelatives()Ljava/util/Set;", "keep"],
        "basis": "TeleportTransition adds asPassenger and relative movement since 1.21.2: neither, as 1.21.2 own "
                 "shorter constructors pass"})
    f.put("classRenames", MC + "class_2248$class_2249", FAC + "/BlockStatePairKey")
    f.put("instanceFieldSetters", BB + ".field_23156:" + RK, LT)
    f.put("superRedirects", BB + ".method_42326(" + BS + BG + POS + ")Z", SH + "#occlusionFullBlock")
    f.w["adapterHooks"].append({"under": BB, "adapter": SH + "$LegacyOcclusionFullBlock",
                                "requires": ["method_42326(" + BS + BG + POS + ")Z"], "synth": [],
                                "basis": "no vanilla code of either release asks isOcclusionShapeFullBlock: a 1.21.1 "
                                         "override is reached by its mod calls, through this adapter"})
    FN = "Ljava/util/function/Function;"
    tail = BS + LA_ + POS + "Ljava/util/function/BiPredicate;)" + L("class_4732$class_4734")
    f.reshape(MC + "class_4732.method_24173(" + L("class_2591") + FN + FN + L("class_2753") + tail,
              "(" + L("class_2591") + FN + FN + L("class_2769") + tail,
              ["keep:0", "keep:1", "keep:2", "keep:3 " + L("class_2769"), "keep:4", "keep:5", "keep:6", "keep:7"],
              "DoubleBlockCombiner.combineWithNeigbour takes any Property of Direction since 1.21.2; 1.21.1 "
              "DirectionProperty is one")


# ------------------------------------------------------------------------------------------- Fabric API: block settings
def fabric_block_settings(f):
    f.put("classRenames", "net/fabricmc/fabric/api/object/builder/v1/block/FabricBlockSettings", FAC + "/FabricBlockSettings")


# ------------------------------------------------------------------------------------------- palette copy, block entity types
def palette_and_be_types(f):
    PAL, PR = L("class_2837"), L("class_2835")
    old = "method_39956()" + PAL
    for c in f.declarers("method_39956", "()" + PAL):
        f.put("instanceRedirects", c + "." + old, RT + "/CbLevelPalette")
    # the override hook itself is mechanical.json's (copy() asked as copy(PaletteResize), frames + superCalls)
    f.put("frameDefaults", old, {"0": RT + "/CbLevelPalette#resize(" + PAL + ")" + PR})
    RO, PC = L("class_7522"), L("class_2841")
    f.w["abstractFills"].append({"under": MC + "class_7522", "method": "method_39957()" + PC,
                                 "call": RT + "/CbLevelPalette.copy(" + RO + ")" + PC})
    BET, SUP = MC + "class_2591", L("class_2591$class_5559")
    key = BET + ".<init>(" + SUP + "Ljava/util/Set;Lcom/mojang/datafixers/types/Type;)V"
    f.put("ctorRedirects", key, RT + "/CbLevelBlockEntityTypes")
    f.put("superCtorReshapes", key, {
        "desc": "(" + SUP + "Ljava/util/Set;)V", "ops": ["keep", "keep", "drop"],
        "basis": "BlockEntityType: 1.21.1 stored the datafixer type in a field nothing read; 1.21.2's constructor "
                 "(protected again by the layer's access widener) takes the factory and blocks alone"})
    f.put("classRenames", BET + "$class_2592", FAC + "/BlockEntityTypeBuilder")


# ------------------------------------------------------------------------------------------- FlowingFluid spreading
LFW = RT + "/CbLevelFlowing"


def flowing(f):
    FF, BGT, DR, FST, FLU = MC + "class_3609", L("class_1922"), L("class_2350"), L("class_3610"), L("class_3611")
    cst = "method_15738(" + BGT + POS + BS + DR + POS + BS + FST + FLU + ")Z"
    f.put("instanceRedirects", FF + "." + cst, LFW)
    f.put("superRedirects", FF + "." + cst, LFW + "#superCanSpreadTo")
    f.w["adapterHooks"].append({"under": FF, "adapter": LFW + "$LegacyCanSpreadTo", "requires": [cst], "synth": [],
                                "basis": "1.21.2 dropped canSpreadTo and asks its checks inline: a 1.21.1 override decides "
                                         "where 1.21.1 asked it, spreading down and to the sides (CbLevelFlowingFluidMixin)"})
    LR_, S2O, S2B = L("class_4538"), "Lit/unimi/dsi/fastutil/shorts/Short2ObjectMap;", "Lit/unimi/dsi/fastutil/shorts/Short2BooleanMap;"
    head = LR_ + POS + "I" + DR + BS
    old = "method_15742(" + head + POS + S2O + S2B + ")I"
    new = "method_15742(" + head + L("class_3609$class_9900") + ")I"
    f.put("instanceRedirects", FF + "." + old, LFW)
    f.hook(old=old, new=new, onlyUnder=FF, adapter=LFW + "$LegacySlopeDistance",
           bridge=LFW + ".slopeDistanceIn(L" + LFW + "$LegacySlopeDistance;" + head + L("class_3609$class_9900") + ")I",
           superBridge=LFW + ".superSlopeDistance(L" + LFW + "$SuperSlopeDistance;" + head + POS + S2O + S2B + ")I",
           superAccess=LFW + "$SuperSlopeDistance", superAccessors=[new],
           basis="getSlopeDistance keeps the source position and caches in a SpreadContext since 1.21.2: a 1.21.1 "
                 "override is asked with the context's origin and fresh caches, its super call with a context at its "
                 "source position (CbLevelFlowing)")


def main():
    v = load(sys.argv[1])
    w = load(sys.argv[2])
    f = Fragment(v, w)
    update_shape(f)
    shapes(f)
    use_item_on(f)
    names_and_loot(f)
    schedule_tick(f)
    neighbors(f)
    explosions(f)
    carving(f)
    chunk_state(f)
    chunk_storage(f)
    fluids_light(f)
    level_api(f)
    entity_getter(f)
    loot_params_moss(f)
    block_entities(f)
    state_props(f)
    level_misc(f)
    fabric_block_settings(f)
    palette_and_be_types(f)
    flowing(f)
    f.write()


if __name__ == "__main__":
    main()
