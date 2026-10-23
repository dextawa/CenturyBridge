"""gen_s7entity.py: the s7entity wiring fragment of climb step 1.21.1 -> 1.21.2 (owners under
net/minecraft/world/entity/), family by family.

  [CB_DATA=<data dir>] python gen_s7entity.py <V stub: client-1.21.1-intermediary.jar> <W stub: client-1.21.2-intermediary.jar>

Writes core/segments/steps/1.21.1__1.21.2.json.d/s7entity-gen.json, rt/CbEntityLevelsGen.java and the
mixin/CbEntityLevelsInvoker*.java invokers of the 1.21.2 layer (and lists them in its mixin config).
Every decision was judged against the 1.21.1 and 1.21.2 bytecode (javap of both code jars); the table
carries the basis of each, and the generator checks every shape it names against the stubs.

Family "threaded level". 1.21.2 threads the entity's ServerLevel through its server-side logic: kill(),
customServerAiStep(), doHurtTarget(target), spawnAtLocation(..), the drop methods, the piglin and sensor
statics. The new shape takes a ServerLevel that the 1.21.1 caller never held.

* Call side (a mod calling the old shape): an instance redirect (a static redirect for a static member)
  into rt/CbEntityLevelsGen. That bridge makes the 1.21.2 call with the level the 1.21.1 body used: the
  entity's own level(), the level of the entity argument whose level() the 1.21.1 body read, or the
  Level the caller passed. On the server, that level is a ServerLevel.
  A 1.21.1 caller could also reach these on a client level (a client-side entity). Every 1.21.2 body
  needs the ServerLevel, so there the bridge answers what 1.21.1's vanilla bodies answered on a client
  level. For most members that is the no-op answer, because their 1.21.1 effects were server-only
  (spawnAtLocation's isClientSide test, hurt's isClientSide test that kill/doHurtTarget went through,
  the server-only AI tick that reached the rest). The few that did more on a client have a hand-written
  client half in rt/CbEntityLevels (client None in the table).
* Override side: the hooks are mechanical.json's (frames + superCalls rewrite). Each gets a frameDefaults
  judgment for its ServerLevel argument: the entity's own level as a ServerLevel. A mod's own super call
  outside a hooked call is the super of the 1.21.1 shape, which ran with the entity's level.

Protected 1.21.2 members (customServerAiStep, dropEquipment, ...) are reached through the generated
invokers, which call them virtually, so a mod's override (hooked to the new shape) answers.
"""
import json
import os
import sys
import zipfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import classfile  # noqa: E402

STEP = "top/dext/centurybridge/step/v1_21_2"
RT = STEP + "/rt"
MIX = STEP + "/mixin"
LEVELS = RT + "/CbEntityLevels"
MC = "net/minecraft/"
HERE = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.dirname(os.path.dirname(HERE))
FRAGMENT = os.path.join(ROOT, "core/segments/steps/1.21.1__1.21.2.json.d/s7entity-gen.json")
MECHANICAL = os.path.join(ROOT, "core/segments/steps/1.21.1__1.21.2.json.d/mechanical.json")
NAMES = os.path.join(os.environ.get("CB_DATA", os.path.join(ROOT, "data")), "mappings/inter-to-mojang-classes.tsv")
SRC = os.path.join(ROOT, "core/shims/steps/1.21.2/src")
MIXINS = os.path.join(ROOT, "core/shims/steps/1.21.2/resources/cb-step-1.21.2.mixins.json")
SL = "Lnet/minecraft/class_3218;"
LEVEL = "Lnet/minecraft/class_1937;"

ENTITY = "class_1297"
LIVING = "class_1309"
MOB = "class_1308"


def load(jar):
    out = {}
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if n.startswith(MC) and n.endswith(".class"):
                c = classfile.read(z.read(n), False)
                if c:
                    out[c.name] = c
    return out


def supers(H, c):
    out, todo = [], [c]
    while todo:
        x = todo.pop(0)
        if x in out or x not in H:
            continue
        out.append(x)
        todo.append(H[x].super)
        todo.extend(H[x].interfaces)
    return out


def method(H, owner, name, desc):
    for s in supers(H, owner):
        for m in H[s].methods:
            if m.name == name and m.desc == desc:
                return s, m
    return None, None


def jtype(d):
    """a field descriptor as Java source (intermediary names, nested classes with dots)"""
    dims = 0
    while d.startswith("["):
        dims += 1
        d = d[1:]
    prim = {"Z": "boolean", "B": "byte", "C": "char", "S": "short", "I": "int", "J": "long", "F": "float", "D": "double", "V": "void"}
    t = prim.get(d) or d[1:-1].replace("/", ".").replace("$", ".")
    return t + "[]" * dims


# ------------------------------------------------------------------------------------------------ the table
# (V owner, name, V desc, "inst"|"static", level source, client answer, basis)
#   level source: "self" (the receiver's own level), "arg:<i>" (old argument i is an entity whose level the
#                 1.21.1 body read), "lvl:<i>" (old argument i is the Level itself), "dragon" (a dragon phase:
#                 its dragon's level), "target" (TargetingConditions.test: the target's level)
#   client answer: the Java expression answered on a client level ("" for void); None: a hand-written
#                 client half rt/CbEntityLevels.<name>OnClient(receiver, old arguments)
T = [
    # Entity --------------------------------------------------------------------------------------------
    (ENTITY, "method_5768", "()V", "inst", "self", "",
     "kill(): Entity removed itself (KILLED) and emitted ENTITY_DIE on any level; LivingEntity's went through "
     "hurt(genericKill), which did nothing on a client level (isClientSide test); 1.21.2's takes the server level"),
    (ENTITY, "method_5706", "(Lnet/minecraft/class_1935;)Lnet/minecraft/class_1542;", "inst", "self", "null",
     "spawnAtLocation(item): ended in spawnAtLocation(stack, offset), which answered null on a client level"),
    (ENTITY, "method_5870", "(Lnet/minecraft/class_1935;I)Lnet/minecraft/class_1542;", "inst", "self", "null",
     "spawnAtLocation(item, offset): as above"),
    (ENTITY, "method_5775", "(Lnet/minecraft/class_1799;)Lnet/minecraft/class_1542;", "inst", "self", "null",
     "spawnAtLocation(stack): as above"),
    (ENTITY, "method_5699", "(Lnet/minecraft/class_1799;F)Lnet/minecraft/class_1542;", "inst", "self", "null",
     "spawnAtLocation(stack, offset): null for an empty stack or a client level, else the dropped item (1.21.2: "
     "the same body on the server level; HangingEntity's override had no client test -- see CLIENT_EFFECTS)"),
    (ENTITY, "method_36971", "(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;)Z", "inst", "lvl:0", "true",
     "mayInteract(level, pos): the level the caller passed (1.21.2 takes it as a ServerLevel); every 1.21.1 "
     "caller passed a server level; Entity's answered true, Projectile's asked the mob-griefing rule of the "
     "level it got, ServerPlayer's the spawn protection: on a client level Entity's answer"),
    # LivingEntity ---------------------------------------------------------------------------------------
    (LIVING, "method_6121", "(Lnet/minecraft/class_1297;)Z", "inst", "self", "false",
     "doHurtTarget(target): LivingEntity's set the last hurt mob and answered false; Mob's hurt the target "
     "through Entity.hurt, which did nothing on a client level (LivingEntity.hurt's isClientSide test)"),
    (LIVING, "method_6110", "()I", "inst", "self", "0",
     "getBaseExperienceReward(): read only through getExperienceReward(level, killer), on the server; "
     "LivingEntity's answered 0"),
    (LIVING, "method_60699", "(Lnet/minecraft/class_1297$class_5529;)V", "inst", "self", "",
     "triggerOnDeathMobEffects(reason): called from LivingEntity.remove only for KILLED/DISCARDED, on a "
     "server level (1.21.2 guards it with instanceof ServerLevel); on a client level the effects stay"),
    (LIVING, "method_16078", "()V", "inst", "self", "",
     "dropEquipment(): the drops all went through spawnAtLocation, which dropped nothing on a client level"),
    (LIVING, "method_23883", "(Lnet/minecraft/class_1297;)V", "inst", "self", "",
     "dropExperience(attacker): did nothing unless level() was a ServerLevel"),
    (LIVING, "method_16077", "(Lnet/minecraft/class_1282;Z)V", "inst", "self", "",
     "dropFromLootTable(source, recentlyHit): cast level() to ServerLevel (a client level: ClassCastException); "
     "1.21.2's takes the server level. On a client level nothing drops"),
    (LIVING, "method_6074", "(Lnet/minecraft/class_1282;F)V", "inst", "self", "",
     "actuallyHurt(source, amount): reached from hurt, which returned before it on a client level"),
    # Mob ------------------------------------------------------------------------------------------------
    (MOB, "method_24523", "(Lnet/minecraft/class_1799;)Lnet/minecraft/class_1799;", "inst", "self", "net.minecraft.class_1799.field_8037",
     "equipItemIfPossible(stack): called from pickUpItem on the server (aiStep, server only); its drop of the "
     "replaced item went through spawnAtLocation. On a client level: EMPTY, nothing equipped"),
    (MOB, "method_20820", "(Lnet/minecraft/class_1799;)Z", "inst", "self", "false",
     "wantsToPickUp(stack): asked from aiStep's pickup scan, which ran on the server only"),
    (MOB, "method_60973", "()V", "inst", "self", "",
     "dropPreservedEquipment(): dropped through spawnAtLocation, nothing on a client level"),
    (MOB, "method_60972", "(Ljava/util/function/Predicate;)Ljava/util/Set;", "inst", "self", "java.util.Set.of()",
     "dropPreservedEquipment(predicate): cleared the slots and spawned the stacks; on a client level the "
     "spawn did nothing -- answers no slot dropped"),
    (MOB, "method_5949", "(Lnet/minecraft/class_1542;)V", "inst", "self", "",
     "pickUpItem(item): reached from aiStep's pickup scan, on the server only"),
    (MOB, "method_5958", "()V", "inst", "self", "",
     "customServerAiStep(): ticked from Mob.serverAiStep, server only; most overrides cast level() to "
     "ServerLevel"),
    # vehicles, horses, decorations -----------------------------------------------------------------------
    ("class_1496", "method_6746", "()V", "inst", "self", "",
     "AbstractHorse.followMommy(): called from aiStep's server branch only"),
    ("class_1688", "method_7504", "()D", "inst", "self", None,
     "AbstractMinecart.getMaxSpeed(): see CLIENT_EFFECTS (1.21.1's answered a speed on any level)"),
    ("class_1688", "method_7512", "()V", "inst", "self", "",
     "AbstractMinecart.comeOffTrack(): called from the server branch of tick only (1.21.1 and 1.21.2)"),
    ("class_8836", "method_7516", "(Lnet/minecraft/class_1282;)V", "inst", "self", "",
     "VehicleEntity.destroy(source): called from hurt's server branch (1.21.1 hurt returned early on a client)"),
    ("class_8836", "method_54298", "(Lnet/minecraft/class_1792;)V", "inst", "self", "",
     "VehicleEntity.destroy(item): kill() and a drop through spawnAtLocation; reached from destroy(source)"),
    ("class_9691", "method_6889", "(Lnet/minecraft/class_1297;)V", "inst", "self", "",
     "BlockAttachedEntity.dropItem(breaker): called on the server when the attachment breaks"),
    ("class_1510", "method_6816", "(Lnet/minecraft/class_1508;Lnet/minecraft/class_1282;F)Z", "inst", "self", "false",
     "EnderDragon.hurt(part, source, amount): reached from EnderDragonPart.hurt; its damage went through "
     "reallyHurt -> Mob.hurt, which did nothing on a client level"),
    ("class_1510", "method_6828", "(Lnet/minecraft/class_1511;Lnet/minecraft/class_2338;Lnet/minecraft/class_1282;)V", "inst", "self", "",
     "EnderDragon.onCrystalDestroyed(..): called from EndCrystal.hurt's server branch"),
    ("class_1588", "method_7076", "(Lnet/minecraft/class_1657;)Z", "inst", "self", "true",
     "Monster.isPreventingPlayerRest(player): asked by the bed (server); Monster's answered true on any level, "
     "ZombifiedPiglin's isAngryAt -- on a client level Monster's answer"),
    # interfaces -------------------------------------------------------------------------------------------
    ("class_5354", "method_29515", "(Lnet/minecraft/class_1309;)Z", "inst", "arg:0", "false",
     "NeutralMob.isAngryAt(target): read the target's level for the universal-anger rule; asked by the AI "
     "(server)"),
    ("class_5354", "method_29923", "(Lnet/minecraft/class_1937;)Z", "inst", "lvl:0", "false",
     "NeutralMob.isAngryAtAllPlayers(level): the rule of the level the caller passed; a server level"),
    ("class_5354", "method_29516", "(Lnet/minecraft/class_1657;)V", "inst", "arg:0", "",
     "NeutralMob.playerDied(player): the dead player's level; ServerPlayer.die calls it"),
    ("class_1521", "method_6855", "()V", "inst", "dragon", "",
     "DragonPhaseInstance.doServerTick(): ticked by EnderDragon.aiStep's server branch; the dragon's level"),
    ("class_7265", "method_42283", "(Lnet/minecraft/class_1282;Lnet/minecraft/class_1937;Lnet/minecraft/class_1297;)V", "inst", "lvl:1", None,
     "ContainerEntity.chestVehicleDestroyed(source, level, entity): see CLIENT_EFFECTS"),
    # statics ---------------------------------------------------------------------------------------------
    ("class_5762", "method_35175", "(Lnet/minecraft/class_5762;Lnet/minecraft/class_1309;)V", "static", "arg:0", "",
     "Axolotl.onStopAttacking(axolotl, target): the axolotl's level (its brain, server)"),
    ("class_4835", "method_24672", "(Lnet/minecraft/class_4760;Lnet/minecraft/class_1309;)V", "static", "arg:0", "",
     "HoglinAi.wasHurtBy(hoglin, attacker): from Hoglin.hurt's server branch"),
    ("class_5137", "method_26950", "(Lnet/minecraft/class_1309;Lnet/minecraft/class_1309;)Z", "static", "arg:0", "false",
     "HoglinBase.hurtAndThrowTarget(attacker, target): hurt the target through LivingEntity.hurt -- false on a "
     "client level, and so no throw"),
    ("class_6067", "method_43544", "(Lnet/minecraft/class_1308;Lnet/minecraft/class_6067;Lnet/minecraft/class_1542;)V", "static", "arg:0", "",
     "InventoryCarrier.pickUpItem(mob, carrier, item): from pickUpItem, server only"),
    ("class_9817", "method_60965", "(Lnet/minecraft/class_1297;)V", "static", "arg:0", None,
     "Leashable.tickLeash(entity): see CLIENT_EFFECTS"),
    ("class_4838", "method_24733", "(Lnet/minecraft/class_1657;Z)V", "static", "arg:0", "",
     "PiglinAi.angerNearbyPiglins(player, angerOnlyIfCanSee): the player's level; called on the server "
     "(chest/block break, ContainerEntity on its server branch)"),
    ("class_4838", "method_24728", "(Lnet/minecraft/class_4836;Lnet/minecraft/class_1657;Lnet/minecraft/class_1268;)Lnet/minecraft/class_1269;", "static", "arg:0", None,
     "PiglinAi.mobInteract(piglin, player, hand): see CLIENT_EFFECTS"),
    ("class_4838", "method_24726", "(Lnet/minecraft/class_4836;Lnet/minecraft/class_1542;)V", "static", "arg:0", "",
     "PiglinAi.pickUpItem(piglin, item): server only (pickUpItem)"),
    ("class_4838", "method_24741", "(Lnet/minecraft/class_4836;Z)V", "static", "arg:0", "",
     "PiglinAi.stopHoldingOffHandItem(piglin, barter): from the admiring behaviour, server only"),
    ("class_4838", "method_25948", "(Lnet/minecraft/class_4836;)V", "static", "arg:0", "",
     "PiglinAi.cancelAdmiring(piglin): from the piglin's brain, server only"),
    ("class_4838", "method_24724", "(Lnet/minecraft/class_4836;Lnet/minecraft/class_1309;)V", "static", "arg:0", "",
     "PiglinAi.wasHurtBy(piglin, attacker): from Piglin.hurt's server branch"),
    ("class_4838", "method_24759", "(Lnet/minecraft/class_5418;Lnet/minecraft/class_1309;)V", "static", "arg:0", "",
     "PiglinAi.maybeRetaliate(piglin, target): from wasHurtBy, server only"),
    ("class_4838", "method_24742", "(Lnet/minecraft/class_5418;Lnet/minecraft/class_1309;)V", "static", "arg:0", "",
     "PiglinAi.broadcastAngerTarget(piglin, target): server only"),
    ("class_4838", "method_29945", "(Lnet/minecraft/class_5418;)V", "static", "arg:0", "",
     "PiglinAi.broadcastUniversalAnger(piglin): server only"),
    ("class_4838", "method_24750", "(Lnet/minecraft/class_5418;Lnet/minecraft/class_1309;)V", "static", "arg:0", "",
     "PiglinAi.setAngerTarget(piglin, target): server only"),
    ("class_5420", "method_30251", "(Lnet/minecraft/class_5419;Lnet/minecraft/class_1309;)V", "static", "arg:0", "",
     "PiglinBruteAi.wasHurtBy(brute, attacker): from PiglinBrute.hurt's server branch"),
    ("class_4148", "method_30954", "(Lnet/minecraft/class_1309;Lnet/minecraft/class_1309;)Z", "static", "arg:0", "false",
     "Sensor.isEntityTargetable(entity, target): the sensing entity's level; sensors tick on the server"),
    ("class_4148", "method_36982", "(Lnet/minecraft/class_1309;Lnet/minecraft/class_1309;)Z", "static", "arg:0", "false",
     "Sensor.isEntityAttackable(entity, target): as above"),
    ("class_4148", "method_37456", "(Lnet/minecraft/class_1309;Lnet/minecraft/class_1309;)Z", "static", "arg:0", "false",
     "Sensor.isEntityAttackableIgnoringLineOfSight(entity, target): as above"),
    ("class_6045", "method_35148", "(Lnet/minecraft/class_1309;Lnet/minecraft/class_1309;)Z", "inst", "arg:0", "false",
     "NearestVisibleLivingEntitySensor.isMatchingEntity(entity, target): asked by the sensor's doTick, on the server "
     "(sensors tick in the brain, server only); the sensing entity's level"),
    ("class_1676", "method_54457", "(Lnet/minecraft/class_1937;)Z", "inst", "lvl:0", "false",
     "Projectile.mayBreak(level): the rule of the level the caller passed (impact handling, server)"),
]



def moj_names():
    cm = {}
    for line in open(NAMES, encoding="utf-8"):
        f = line.rstrip("\n").split("\t")
        if len(f) >= 3 and f[0] == "C":
            cm[f[1]] = f[2]
    return cm


def frame_defaults(W, cm):
    """frameDefaults for every mechanical hook of this area whose missing argument is a ServerLevel"""
    out = {}
    mech = json.load(open(MECHANICAL, encoding="utf-8"))
    for h in mech.get("overrideHooks", []):
        under = h["onlyUnder"]
        top = under.split("$")[0]
        if not cm.get(top, "").startswith(MC + "world/entity/"):
            continue
        nargs, _ = classfile.split_args(h["new"][h["new"].index("("):])
        passed = set(h["args"])
        for j, t in enumerate(nargs):
            if j in passed or t != SL:
                continue
            ent = MC + ENTITY
            if ent in supers(W, under):
                ref = LEVELS + "#serverLevel(Lnet/minecraft/class_1297;)" + SL
                why = "the entity's own level, the level the 1.21.1 shape's super ran with"
            else:
                ref = None
            if ref:
                out.setdefault(h["old"], {})[str(j)] = ref
    return out


def main():
    V = load(sys.argv[1])
    W = load(sys.argv[2])
    cm = moj_names()
    frag = {"instanceRedirects": {}, "staticRedirects": {}, "frameDefaults": frame_defaults(W, cm)}
    bodies = []
    invokers = {}
    adapters = []
    for owner, name, vdesc, kind, lvl, client, basis in T:
        o = MC + owner
        vo, vm = method(V, o, name, vdesc)
        assert vm is not None and vo == o, f"V has no {o}.{name}{vdesc} declared there"
        ws, wm = None, None
        for s_ in supers(W, o):
            for m in W[s_].methods:
                if m.name == name and SL in m.desc:
                    ws, wm = s_, m
                    break
            if wm:
                break
        assert wm, f"W has no ServerLevel shape of {o}.{name}"
        wargs, wret = classfile.split_args(wm.desc)
        vargs, vret = classfile.split_args(vdesc)
        k = wargs.index(SL)
        if lvl.startswith("lvl:"):
            i = int(lvl[4:])
            assert vargs[i] == LEVEL, f"{o}.{name}: argument {i} is no Level"
            kept = [j for j in range(len(vargs)) if j != i]
        else:
            kept = list(range(len(vargs)))
        assert wargs[:k] + wargs[k + 1:] == [vargs[j] for j in kept], f"{o}.{name}: {wm.desc} is not {vdesc} with a ServerLevel"
        assert wret == vret or wret == "V", f"{o}.{name}: return {vret} -> {wret}"
        static = kind == "static"
        assert bool(vm.access & classfile.ACC_STATIC) == static and bool(wm.access & classfile.ACC_STATIC) == static
        key = f"{o}.{name}{vdesc}"
        (frag["staticRedirects"] if static else frag["instanceRedirects"])[key] = LEVELS + "Gen#" + name
        params = ([] if static else [("self", "L" + o + ";")]) + [(f"a{i}", t) for i, t in enumerate(vargs)]
        sig = ", ".join(f"{jtype(t)} {n}" for n, t in params)
        if lvl == "self":
            lev = "self.method_37908()"
        elif lvl == "dragon":
            lev = "CbEntityLevels.dragonLevel(self)"
        elif lvl.startswith("arg:"):
            lev = f"a{int(lvl[4:])}.method_37908()"
        else:
            lev = f"a{int(lvl[4:])}"
        call_args = [f"a{j}" for j in kept]
        call_args = call_args[:k] + ["sl"] + call_args[k:]
        if wm.access & classfile.ACC_PUBLIC:
            target = (jtype("L" + ws + ";") + "." + name) if static else ("self." + name)
        else:
            inv = "top.dext.centurybridge.step.v1_21_2.mixin." + invoker_name(ws)
            invokers.setdefault(ws, {})[name + wm.desc] = (name, wm.desc, static)
            target = (f"{inv}.cb${name}") if static else (f"(({inv}) self).cb${name}")
        if owner == "class_9817" and name == "method_60965":
            # <E extends Entity & Leashable> tickLeash(level, E): the 1.21.1 shape had the same bound, so the
            # argument is a Leashable entity; javac needs the intersection spelled out
            call_args = ["sl", "(net.minecraft.class_1297 & net.minecraft.class_9817) a0"]
        call = f"{target}({', '.join(call_args)})"
        ret = jtype(vret)
        out = [f"    /** {basis} */", f"    public static {ret} {name}({sig}) {{",
               f"        if (!({lev} instanceof net.minecraft.class_3218 sl)) {{"]
        legacy = None
        if not static and not (vm.access & classfile.ACC_FINAL):
            # a mod class declaring the old shape gets this interface (adapterHooks, no synth): on a client
            # level its own override answers, as the virtual call did in 1.21.1
            legacy = f"Legacy_{name}"
            adapters.append((o, legacy, name, vdesc))
            out.append(f"            if (self instanceof {legacy} legacy) {{")
            out.append(f"                {'return ' if ret != 'void' else ''}legacy.{name}({', '.join(f'a{i}' for i in range(len(vargs)))});")
            if ret == "void":
                out.append("                return;")
            out.append("            }")
        if client is None:
            out.append(f"            {'return ' if ret != 'void' else ''}CbEntityLevels.{name}OnClient({', '.join(n for n, _ in params)});")
            if ret == "void":
                out.append("            return;")
        else:
            out.append(f"            return{(' ' + client) if client else ''};")
        out.append("        }")
        if vret == "V" and wret != "V":
            out.append(f"        {call};")
        else:
            out.append(f"        {'return ' if vret != 'V' else ''}{call};")
        out.append("    }")
        bodies.append("\n".join(out))
    frag["adapterHooks"] = [{"under": o, "adapter": LEVELS + "Gen$" + legacy, "requires": [name + vdesc], "synth": [],
                              "basis": "a mod class overriding the 1.21.1 shape answers a call through it on a client level, "
                                       "where the bridge cannot make the 1.21.2 call (CbEntityLevelsGen)"}
                             for o, legacy, name, vdesc in adapters]
    for o, legacy, name, vdesc in adapters:
        args, r = classfile.split_args(vdesc)
        ps = ", ".join(f"{jtype(t)} a{i}" for i, t in enumerate(args))
        bodies.append(f"    /** the 1.21.1 {name}{vdesc} a mod class of {o} declares */\n"
                      f"    public interface {legacy} {{\n        {jtype(r)} {name}({ps});\n    }}")
    write_java(bodies)
    write_invokers(invokers)
    write_fragment(frag)


def invoker_name(owner):
    return "CbEntityLevelsInvoker" + owner[len(MC):].replace("class_", "").replace("$", "_")


def write_fragment(frag):
    with open(FRAGMENT, "w", encoding="utf-8", newline="\n") as o:
        json.dump({k: v for k, v in frag.items() if v}, o, indent=1)
        o.write("\n")
    print(FRAGMENT, {k: len(v) for k, v in frag.items() if v})


def write_java(bodies):
    path = os.path.join(SRC, LEVELS + "Gen.java")
    head = ("package top.dext.centurybridge.step.v1_21_2.rt;\n\n"
            "/**\n"
            " * Generated by core/tools/gen_s7entity.py (do not edit): the 1.21.1 shapes of the members 1.21.2 threads the\n"
            " * entity's ServerLevel through. Each calls the 1.21.2 shape with the level the 1.21.1 body used and, on a\n"
            " * client level, answers what the 1.21.1 body answered there (the generator's table gives the basis).\n"
            " */\n"
            "public final class CbEntityLevelsGen {\n\n")
    with open(path, "w", encoding="utf-8", newline="\n") as o:
        o.write(head + "\n\n".join(bodies) + "\n\n    private CbEntityLevelsGen() {\n    }\n}\n")
    print(path)


def write_invokers(invokers):
    names = []
    for owner, ms in sorted(invokers.items()):
        name = invoker_name(owner)
        names.append(name)
        lines = ["package top.dext.centurybridge.step.v1_21_2.mixin;", "",
                 "import org.spongepowered.asm.mixin.Mixin;", "import org.spongepowered.asm.mixin.gen.Invoker;", "",
                 "/** Generated by core/tools/gen_s7entity.py (do not edit): protected 1.21.2 shapes CbEntityLevelsGen calls. */",
                 f"@Mixin({jtype('L' + owner + ';')}.class)", f"public interface {name} {{"]
        for _, (n, d, static) in sorted(ms.items()):
            args, r = classfile.split_args(d)
            ps = ", ".join(f"{jtype(t)} a{i}" for i, t in enumerate(args))
            lines += ["", f"    @Invoker(\"{n}\")"]
            if static:
                lines += [f"    static {jtype(r)} cb${n}({ps}) {{", "        throw new AssertionError();", "    }"]
            else:
                lines.append(f"    {jtype(r)} cb${n}({ps});")
        lines.append("}")
        with open(os.path.join(SRC, MIX, name + ".java"), "w", encoding="utf-8", newline="\n") as o:
            o.write("\n".join(lines) + "\n")
    cfg = json.load(open(MIXINS, encoding="utf-8"))
    cfg["mixins"] = sorted(set(m for m in cfg["mixins"] if not m.startswith("CbEntityLevelsInvoker")) | set(names))
    with open(MIXINS, "w", encoding="utf-8", newline="\n") as o:
        json.dump(cfg, o, indent=2)
        o.write("\n")
    print("invokers:", names)


if __name__ == "__main__":
    main()
