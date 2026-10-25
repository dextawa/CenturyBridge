package top.dext.centurybridge.step.v1_21_6.rt;

/**
 * Entity / Leashable members of 1.21.5 that 1.21.6 dropped or moved (javap of both):
 * <ul>
 * <li>Entity.canBeCollidedWith() became canBeCollidedWith(Entity other); every 1.21.5 override (boats, minecarts,
 *     shulkers) ignores the other entity in 1.21.6 too, and the one new override (HappyGhast) only instanceof-tests it,
 *     so a 1.21.5 caller, which names no other entity, asks with none.</li>
 * <li>Entity.resetFallDistanceIfFalling-style helper method_45318: when the vertical speed is above -0.5 and the fall
 *     distance above 1, the fall distance becomes 1 -- 1.21.5's body on 1.21.6's public field.</li>
 * <li>Entity.getLeashOffset() / getLeashOffset(float) moved onto Leashable with the same default body (0, eye height,
 *     bbWidth x 0.4); a Leashable answers its own (overrides included), any other entity Entity's 1.21.5 default.</li>
 * <li>Leashable.canHaveALeashAttachedToIt(): canBeLeashed() &amp;&amp; !isLeashed().</li>
 * </ul>
 */
public final class CbEntities {

    private CbEntities() {
    }

    /** the other entity a 1.21.5 canBeCollidedWith() caller names: none */
    public static net.minecraft.class_1297 noEntity() {
        return null;
    }

    /** Entity.method_45318() */
    public static void method_45318(net.minecraft.class_1297 e) {
        if (e.method_18798().method_10214() > -0.5D && e.field_6017 > 1.0D) {
            e.field_6017 = 1.0D;
        }
    }

    /** Entity.getLeashOffset(float) */
    public static net.minecraft.class_243 method_45321(net.minecraft.class_1297 e, float partialTick) {
        return e instanceof net.minecraft.class_9817 l ? l.method_45321(partialTick) : method_29919(e);
    }

    /** Entity.getLeashOffset() */
    public static net.minecraft.class_243 method_29919(net.minecraft.class_1297 e) {
        if (e instanceof net.minecraft.class_9817 l) {
            return l.method_29919();
        }
        return new net.minecraft.class_243(0.0D, e.method_5751(), e.method_17681() * 0.4F);
    }

    /** Leashable.canHaveALeashAttachedToIt() */
    public static boolean method_60969(net.minecraft.class_9817 l) {
        return l.method_5931() && !l.method_60953();
    }
}
