package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4 bodies of entity methods 1.20.5 made abstract, removed, or stopped calling, for mod classes
 * written against 1.20.4 (the abstract fills and the arrow deflection hook of this step).
 */
public final class CbRestMobs {

    /**
     * Added to a mod arrow class that overrides 1.20.4's AbstractArrow.deflect(): 1.20.4 called it when
     * the arrow hit an entity that deflects arrows (the breeze); 1.20.5 deflects through the target's
     * ProjectileDeflection instead (mixin/CbRestProjectileMixin hands that to the mod's method).
     */
    public interface LegacyDeflect {
        void method_55052();
    }

    /** Animal.isFood(stack): 1.20.4's body, which 1.20.5 made abstract -- wheat */
    public static boolean isFood(net.minecraft.class_1429 animal, net.minecraft.class_1799 stack) {
        return stack.method_31574(net.minecraft.class_1802.field_8861);
    }

    /**
     * AbstractArrow.getDefaultPickupItem(), which 1.20.5 made abstract: 1.20.4 had no default pickup item
     * of its own -- an arrow kept the stack it was constructed with, and loading one without an item
     * tag left that stack as it was. So the default is the arrow's current pickup stack: 1.20.5's load
     * then sets the stack the arrow already has. While 1.20.5's constructor asks for it the arrow holds
     * none yet (the constructor taking a stack sets that one right after): the empty stack.
     */
    public static net.minecraft.class_1799 defaultPickupItem(net.minecraft.class_1665 arrow) {
        net.minecraft.class_1799 stack = arrow.method_54759();
        return stack != null ? stack : net.minecraft.class_1799.field_8037;
    }

    /**
     * ContainerEntity.getBoundingBox(), which 1.20.5 added to the interface (its own defaults measure
     * reach from it): an entity has its own; 1.20.4's ContainerEntity located a container by its
     * position() alone, so for an implementor that is no entity the box is that point.
     */
    public static net.minecraft.class_238 boundingBox(net.minecraft.class_7265 container) {
        net.minecraft.class_243 pos = container.method_19538();
        return new net.minecraft.class_238(pos, pos);
    }

    // ---------------------------------------------------------------- players and living entities

    /** Player.isValidUsername(name): 1.20.4's body (at most 16 characters, none a control, space or non-ASCII one) */
    public static boolean method_53968(String name) {
        if (name.length() > 16) {
            return false;
        }
        return name.chars().filter(c -> c <= 32 || c >= 127).findAny().isEmpty();
    }

    /** Player.getPickRange(creative): 1.20.4's body (1.20.5 reads the player's interaction range attributes) */
    public static float method_54292(boolean creative) {
        return creative ? 5.0F : 4.5F;
    }

    /**
     * EntityType.updateCustomEntityTag(level, player, entity, itemTag): 1.20.4's body -- the item tag's
     * EntityTag merged into the entity's saved data, which it then loads (the entity's UUID set before the
     * load, so an EntityTag naming one gives it); 1.20.5 takes the entity data component instead.
     */
    public static void method_5881(net.minecraft.class_1937 level, net.minecraft.class_1657 player, net.minecraft.class_1297 entity,
                                   net.minecraft.class_2487 tag) {
        if (tag == null || !tag.method_10573("EntityTag", 10)) {
            return;
        }
        net.minecraft.server.MinecraftServer server = level.method_8503();
        if (server == null || entity == null) {
            return;
        }
        if (!level.field_9236 && entity.method_5833()
                && (player == null || !server.method_3760().method_14569(player.method_7334()))) {
            return;
        }
        net.minecraft.class_2487 saved = entity.method_5647(new net.minecraft.class_2487());
        java.util.UUID id = entity.method_5667();
        saved.method_10543(tag.method_10562("EntityTag"));
        entity.method_5826(id);
        entity.method_5651(saved);
    }

    /**
     * Added to a mod class overriding 1.20.4's LivingEntity.broadcastBreakEvent(hand): 1.20.4 called it
     * when the item in a hand broke (1.20.5 calls broadcastBreakEvent(slot) for every slot)
     */
    public interface LegacyBreakEvent {
        void method_20236(net.minecraft.class_1268 hand);
    }

    /**
     * the synthesized broadcastBreakEvent(slot) of such a class (guarded): a hand slot is the mod's
     * broadcastBreakEvent(hand), another slot -- and the call back from that method's super call -- the
     * class's superclass implementation
     */
    public static void breakEvent(LegacyBreakEvent self, net.minecraft.class_1304 slot) {
        if (slot == net.minecraft.class_1304.field_6173) {
            self.method_20236(net.minecraft.class_1268.field_5808);
        } else if (slot == net.minecraft.class_1304.field_6171) {
            self.method_20236(net.minecraft.class_1268.field_5810);
        } else {
            ((net.minecraft.class_1309) self).method_20235(slot);
        }
    }

    /**
     * Added to a mod player class overriding 1.20.4's Player.disableShield(becauseOfAxe): 1.20.4 called
     * disableShield(true) when an attacker able to disable shields hit the raised shield; 1.20.5 calls
     * disableShield() there
     */
    public interface LegacyShield {
        void method_7284(boolean becauseOfAxe);
    }

    /** the synthesized disableShield() of such a class (guarded): the mod's disableShield(true) */
    public static void disableShield(LegacyShield self) {
        self.method_7284(true);
    }

    private CbRestMobs() {
    }
}
