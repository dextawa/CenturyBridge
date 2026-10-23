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
                  for k in ("methodRenames", "callReshapes", "superCtorReshapes", "ctorRedirects", "staticRedirects",
                            "instanceRedirects", "fieldRedirects", "fieldGetters", "instanceFieldGetters",
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


def main():
    v = load(sys.argv[1])
    w = load(sys.argv[2])
    f = Fragment(v, w)
    update_shape(f)
    shapes(f)
    use_item_on(f)
    names_and_loot(f)
    schedule_tick(f)
    f.write()


if __name__ == "__main__":
    main()
