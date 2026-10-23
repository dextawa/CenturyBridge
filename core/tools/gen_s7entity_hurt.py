"""gen_s7entity_hurt.py: the hurt / isInvulnerableTo family of climb step 1.21.1 -> 1.21.2 (s7entity).

  python gen_s7entity_hurt.py <V stub: client-1.21.1-intermediary.jar> <W stub: client-1.21.2-intermediary.jar>

Writes core/segments/steps/1.21.1__1.21.2.json.d/s7entity-hurt-gen.json. The runtime is rt/CbEntityHurt (+ the
mixins CbEntityHurtMixin, CbEntityHurtFinalMixin, CbEntityHurtInvoker); its javadoc has the semantics.

1.21.1 Entity.hurt(DamageSource, float)Z, overridable on both sides, became 1.21.2's hurtServer(ServerLevel,
DamageSource, float)Z (abstract on Entity, final on twelve classes) and hurtClient(DamageSource)Z, chosen by the
final hurtOrSimulate(DamageSource, float)Z. 1.21.1 isInvulnerableTo(DamageSource)Z became LivingEntity's
isInvulnerableTo(ServerLevel, DamageSource)Z; Entity's body is the final isInvulnerableToBase.

* calls of either old shape, through any entity class (players included): instance redirects keyed on Entity
  into CbEntityHurt (hurt -> hurtOrSimulate; isInvulnerableTo -> by side and kind).
* overrides of hurt, under Entity: an override hook to hurtServer (frames: the level vanilla passed) whose super
  call goes through CbEntityHurt.superHurt (server: super's hurtServer, client: super's hurtClient, under Entity
  itself 1.21.1 Entity.hurt's body); an adapter (no synth) so the client mixin and the final-class mixin reach it.
* overrides of isInvulnerableTo under LivingEntity: an override hook to the 1.21.2 shape whose super call goes
  through CbEntityHurt.superIsInvulnerableTo (client: 1.21.1's client answer); under any other entity an adapter
  (the base mixin hands isInvulnerableToBase to it) and a super redirect of Entity's shape to the base test.
* a concrete mod class extending Entity with no hurt of its own: hurtServer filled with 1.21.1 Entity.hurt's body.

The generator checks every shape against both stubs and refuses what does not hold.
"""
import json
import os
import sys
import zipfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import classfile  # noqa: E402

HERE = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.dirname(os.path.dirname(HERE))
FRAGMENT = os.path.join(ROOT, "core/segments/steps/1.21.1__1.21.2.json.d/s7entity-hurt-gen.json")
RT = "top/dext/centurybridge/step/v1_21_2/rt"
HURT = RT + "/CbEntityHurt"
GUARD = RT + "/Guard"
MC = "net/minecraft/"
ENTITY = MC + "class_1297"
LIVING = MC + "class_1309"
SRC = "Lnet/minecraft/class_1282;"
SL = "Lnet/minecraft/class_3218;"
OLD_HURT = ("method_5643", "(" + SRC + "F)Z")
OLD_INV = ("method_5679", "(" + SRC + ")Z")
HURT_SERVER = ("method_64397", "(" + SL + SRC + "F)Z")
HURT_CLIENT = ("method_5643", "(" + SRC + ")Z")
HURT_OR_SIMULATE = ("method_64420", "(" + SRC + "F)Z")
NEW_INV = ("method_5679", "(" + SL + SRC + ")Z")
INV_BASE = ("method_64421", "(" + SRC + ")Z")


def load(jar):
    out = {}
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if n.endswith(".class") and n.startswith(MC):
                c = classfile.read(z.read(n))
                out[c.name] = c
    return out


def under(H, c, t):
    while c:
        if c == t:
            return True
        c = H[c].super if c in H else None
    return False


def declares(H, c, shape):
    return any(m.name == shape[0] and m.desc == shape[1] for m in H[c].methods)


def method(H, c, shape):
    for m in H[c].methods:
        if m.name == shape[0] and m.desc == shape[1]:
            return m
    return None


def main():
    V = load(sys.argv[1])
    W = load(sys.argv[2])
    # the 1.21.2 shapes this family stands on
    assert method(W, ENTITY, HURT_SERVER).access & 0x400, "Entity.hurtServer is not abstract"
    assert method(W, ENTITY, HURT_OR_SIMULATE).access & classfile.ACC_FINAL
    assert method(W, ENTITY, HURT_CLIENT) and method(W, ENTITY, INV_BASE).access & classfile.ACC_FINAL
    assert method(W, LIVING, NEW_INV) and not declares(W, ENTITY, OLD_INV) and not declares(W, ENTITY, OLD_HURT)
    finals = sorted(c for c in W if declares(W, c, HURT_SERVER) and method(W, c, HURT_SERVER).access & classfile.ACC_FINAL)
    assert len(finals) == 12, finals
    hurt_owners = sorted(c for c in V if under(V, c, ENTITY) and declares(V, c, OLD_HURT))
    inv_owners = sorted(c for c in V if under(V, c, ENTITY) and declares(V, c, OLD_INV))
    for c in hurt_owners + inv_owners:
        assert c in W, f"{c} gone in 1.21.2"
    frag = {"instanceRedirects": {}, "overrideHooks": [], "adapterHooks": [], "superRedirects": {},
            "abstractFills": []}
    # keyed on Entity, which declared both: a call naming any class under it finds the key up its superclasses
    # (Chain.instanceRedirectHome), and the static takes the receiver as an Entity
    assert ENTITY in hurt_owners and ENTITY in inv_owners
    frag["instanceRedirects"][f"{ENTITY}.{OLD_HURT[0]}{OLD_HURT[1]}"] = HURT + "#hurt"
    frag["instanceRedirects"][f"{ENTITY}.{OLD_INV[0]}{OLD_INV[1]}"] = HURT + "#isInvulnerableTo"
    frag["overrideHooks"].append({
        "old": OLD_HURT[0] + OLD_HURT[1], "new": HURT_SERVER[0] + HURT_SERVER[1], "args": [1, 2],
        "onlyUnder": ENTITY, "frames": GUARD,
        "superBridge": f"{HURT}.superHurt(L{HURT}$Super;{SRC}F)Z",
        "superAccess": HURT + "$Super",
        "superAccessors": [HURT_SERVER[0] + HURT_SERVER[1], HURT_CLIENT[0] + HURT_CLIENT[1]],
        "basis": "1.21.1 hurt(source, amount) is 1.21.2's hurtServer on the server (the level vanilla passed is on "
                 "record for the super call); a super.hurt(..) is super's hurtServer / hurtClient by side, under "
                 "Entity itself (abstract hurtServer) 1.21.1 Entity.hurt's body (CbEntityHurt.superHurt). Not "
                 "synthesized under the " + str(len(finals)) + " classes whose hurtServer is final: "
                 "CbEntityHurtFinalMixin hands the call to the old hurt there",
    })
    frag["overrideHooks"].append({
        "old": OLD_INV[0] + OLD_INV[1], "new": NEW_INV[0] + NEW_INV[1], "args": [1],
        "onlyUnder": LIVING, "frames": GUARD,
        "superBridge": f"{HURT}.superIsInvulnerableTo(L{HURT}$SuperInvulnerable;{SRC})Z",
        "superAccess": HURT + "$SuperInvulnerable",
        "superAccessors": [NEW_INV[0] + NEW_INV[1]],
        "basis": "1.21.1 LivingEntity.isInvulnerableTo(source) is 1.21.2's isInvulnerableTo(level, source) on the "
                 "server; a super call on a client level answers 1.21.1's client answer of the vanilla class "
                 "above (CbEntityHurt.superIsInvulnerableTo), where 1.21.2's needs a ServerLevel",
    })
    # Fabric API's FakePlayer (a ServerPlayer): calls name it, and the stubs do not see its hierarchy -- the same
    # redirect and hook keyed on it (the static takes the receiver as a FakePlayer)
    fake = "net/fabricmc/fabric/api/entity/FakePlayer"
    frag["instanceRedirects"][f"{fake}.{OLD_INV[0]}{OLD_INV[1]}"] = HURT + "#isInvulnerableTo"
    inv_hook = dict(frag["overrideHooks"][-1])
    inv_hook["onlyUnder"] = fake
    inv_hook["basis"] = "as the LivingEntity hook, for a subclass of Fabric API's FakePlayer (outside the stubs' hierarchy)"
    frag["overrideHooks"].append(inv_hook)
    frag["adapterHooks"].append({
        "under": ENTITY, "adapter": HURT + "$Legacy", "requires": [OLD_HURT[0] + OLD_HURT[1]], "synth": [],
        "basis": "the mod class's own 1.21.1 hurt answers hurtOrSimulate on a client level (CbEntityHurtMixin) and "
                 "the final hurtServer of " + ", ".join(f.split("/")[-1] for f in finals) + " (CbEntityHurtFinalMixin)",
    })
    frag["adapterHooks"].append({
        "under": ENTITY, "adapter": HURT + "$LegacyInvulnerable", "requires": [OLD_INV[0] + OLD_INV[1]], "synth": [],
        "basis": "a non-living mod entity's 1.21.1 isInvulnerableTo answers isInvulnerableToBase, which 1.21.2's "
                 "vanilla entities ask where 1.21.1's asked isInvulnerableTo (CbEntityHurtMixin); a living one's "
                 "answers a 1.21.1 call on a client level (CbEntityHurt.isInvulnerableTo)",
    })
    # the super bridges make the super call themselves (frames read there); what they pass with no frame for
    # the object is the entity's own ServerLevel, recorded as the hooks' frameDefaults judgment
    own = RT + "/CbEntityLevels#serverLevel(L" + ENTITY + ";)" + SL
    frag["frameDefaults"] = {OLD_HURT[0] + OLD_HURT[1]: {"0": own}, OLD_INV[0] + OLD_INV[1]: {"0": own}}
    frag["superRedirects"][f"{ENTITY}.{OLD_INV[0]}{OLD_INV[1]}"] = HURT + "#superIsInvulnerableToBase"
    frag["abstractFills"].append({
        "under": ENTITY, "method": HURT_SERVER[0] + HURT_SERVER[1],
        "call": f"{HURT}.fillHurtServer(L{ENTITY};{SL}{SRC}F)Z",
        "basis": "a mod class extending Entity without a hurt of its own inherited 1.21.1 Entity.hurt: "
                 "invulnerable -> false, else markHurt and false",
    })
    with open(FRAGMENT, "w", encoding="utf-8", newline="\n") as f:
        f.write(json.dumps(frag, indent=1, ensure_ascii=False) + "\n")
    print(f"hurt owners {len(hurt_owners)}, isInvulnerableTo owners {len(inv_owners)}, finals {len(finals)}")


if __name__ == "__main__":
    main()
