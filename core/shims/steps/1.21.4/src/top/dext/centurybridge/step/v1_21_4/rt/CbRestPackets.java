package top.dext.centurybridge.step.v1_21_4.rt;

import net.minecraft.class_1297;
import net.minecraft.class_243;
import net.minecraft.class_2692;
import net.minecraft.class_2735;
import net.minecraft.class_2833;

/**
 * 1.21.3's game packets that 1.21.4 turned into records: ClientboundMoveVehiclePacket (class_2692),
 * ServerboundMoveVehiclePacket (class_2833) and ClientboundSetHeldSlotPacket (class_2735).
 * <p>
 * 1.21.3 read their values through getters (getX/getY/getZ/getYRot/getXRot, getSlot) that 1.21.4 replaced
 * by the record's accessors (position(), yRot(), xRot(), slot()). Three contracts of 1.21.3 are kept:
 * <ul>
 * <li>a mod's call of a getter answers what the getter answered: a mod subclass's own override when it has
 *     one (the virtual call of 1.21.3), otherwise the packet's value ({@code x(p)} etc.);</li>
 * <li>vanilla's handler read the getters, so a mod subclass's override is what the handler sees
 *     (CbRestServerVehicleMixin, CbRestClientPacketsMixin redirect the handlers' accessor calls through
 *     here), while the codec wrote the fields, so the bytes on the wire are the constructor's values
 *     (1.21.4's codec reads the accessors, which a 1.21.3 mod cannot override: it never knew them);</li>
 * <li>a mod's super.getX() answers the field ({@code rawX(p)} etc.: the record accessor).</li>
 * </ul>
 * A mod subclass that overrides a getter gets the matching interface below (adapterHooks), whose method is
 * the getter under its 1.21.3 intermediary name, so the mod's own method implements it.
 */
public final class CbRestPackets {

    private CbRestPackets() {
    }

    // ---- ClientboundMoveVehiclePacket (class_2692) ----

    public interface ClientVehicleX { double method_11673(); }
    public interface ClientVehicleY { double method_11674(); }
    public interface ClientVehicleZ { double method_11670(); }
    public interface ClientVehicleYRot { float method_11675(); }
    public interface ClientVehicleXRot { float method_11671(); }

    /** 1.21.3 new ClientboundMoveVehiclePacket(entity): the entity's position (getX/getY/getZ). */
    public static class_243 clientVehiclePos(class_1297 entity) {
        return entity.method_19538();
    }

    public static float yRot(class_1297 entity) {
        return entity.method_36454();
    }

    public static float xRot(class_1297 entity) {
        return entity.method_36455();
    }

    public static double method_11673(class_2692 p) {
        return (Object) p instanceof ClientVehicleX a ? a.method_11673() : p.comp_3347().field_1352;
    }

    public static double method_11674(class_2692 p) {
        return (Object) p instanceof ClientVehicleY a ? a.method_11674() : p.comp_3347().field_1351;
    }

    public static double method_11670(class_2692 p) {
        return (Object) p instanceof ClientVehicleZ a ? a.method_11670() : p.comp_3347().field_1350;
    }

    public static float method_11675(class_2692 p) {
        return (Object) p instanceof ClientVehicleYRot a ? a.method_11675() : p.comp_3348();
    }

    public static float method_11671(class_2692 p) {
        return (Object) p instanceof ClientVehicleXRot a ? a.method_11671() : p.comp_3349();
    }

    public static double rawX(class_2692 p) {
        return p.comp_3347().field_1352;
    }

    public static double rawY(class_2692 p) {
        return p.comp_3347().field_1351;
    }

    public static double rawZ(class_2692 p) {
        return p.comp_3347().field_1350;
    }

    public static float rawYRot(class_2692 p) {
        return p.comp_3348();
    }

    public static float rawXRot(class_2692 p) {
        return p.comp_3349();
    }

    /** the position 1.21.3's handler read through getX/getY/getZ */
    public static class_243 position(class_2692 p) {
        if ((Object) p instanceof ClientVehicleX || (Object) p instanceof ClientVehicleY || (Object) p instanceof ClientVehicleZ) {
            return new class_243(method_11673(p), method_11674(p), method_11670(p));
        }
        return p.comp_3347();
    }

    // ---- ServerboundMoveVehiclePacket (class_2833) ----

    public interface ServerVehicleX { double method_12279(); }
    public interface ServerVehicleY { double method_12280(); }
    public interface ServerVehicleZ { double method_12276(); }
    public interface ServerVehicleYRot { float method_12281(); }
    public interface ServerVehicleXRot { float method_12277(); }

    /** 1.21.3 new ServerboundMoveVehiclePacket(entity): the entity's lerp target (lerpTargetX/Y/Z). */
    public static class_243 serverVehiclePos(class_1297 entity) {
        return new class_243(entity.method_53830(), entity.method_53827(), entity.method_53828());
    }

    /**
     * 1.21.4's packet also carries the vehicle's onGround, which 1.21.3's did not; 1.21.4's own
     * ServerboundMoveVehiclePacket.fromEntity(entity) takes entity.onGround(), and so does this.
     */
    public static boolean onGround(class_1297 entity) {
        return entity.method_24828();
    }

    public static double method_12279(class_2833 p) {
        return (Object) p instanceof ServerVehicleX a ? a.method_12279() : p.comp_3350().field_1352;
    }

    public static double method_12280(class_2833 p) {
        return (Object) p instanceof ServerVehicleY a ? a.method_12280() : p.comp_3350().field_1351;
    }

    public static double method_12276(class_2833 p) {
        return (Object) p instanceof ServerVehicleZ a ? a.method_12276() : p.comp_3350().field_1350;
    }

    public static float method_12281(class_2833 p) {
        return (Object) p instanceof ServerVehicleYRot a ? a.method_12281() : p.comp_3351();
    }

    public static float method_12277(class_2833 p) {
        return (Object) p instanceof ServerVehicleXRot a ? a.method_12277() : p.comp_3352();
    }

    public static double rawX(class_2833 p) {
        return p.comp_3350().field_1352;
    }

    public static double rawY(class_2833 p) {
        return p.comp_3350().field_1351;
    }

    public static double rawZ(class_2833 p) {
        return p.comp_3350().field_1350;
    }

    public static float rawYRot(class_2833 p) {
        return p.comp_3351();
    }

    public static float rawXRot(class_2833 p) {
        return p.comp_3352();
    }

    /** the position 1.21.3's handler read through getX/getY/getZ */
    public static class_243 position(class_2833 p) {
        if ((Object) p instanceof ServerVehicleX || (Object) p instanceof ServerVehicleY || (Object) p instanceof ServerVehicleZ) {
            return new class_243(method_12279(p), method_12280(p), method_12276(p));
        }
        return p.comp_3350();
    }

    // ---- ClientboundSetHeldSlotPacket (class_2735) ----

    public interface HeldSlot { int method_11803(); }

    public static int method_11803(class_2735 p) {
        return (Object) p instanceof HeldSlot a ? a.method_11803() : p.comp_3325();
    }

    public static int rawSlot(class_2735 p) {
        return p.comp_3325();
    }

    // ---- particles ----

    /**
     * 1.21.4's particle packet and ServerLevel.sendParticles took a second flag, alwaysShow (the client then
     * ignores the particles option). 1.21.3 had none: its client always passed false there
     * (ClientLevel.addParticle(options, force, ...) -> LevelRenderer.addParticle(options, force, false, ...)),
     * so a 1.21.3 particle packet is one with alwaysShow false.
     */
    public static boolean notAlwaysShown() {
        return false;
    }

    /**
     * 1.21.3's ServerLevel.sendParticles(options, x, y, z, count, dx, dy, dz, speed) sent without the force
     * flag (its sendParticles(player, false, ..) and a packet with overrideLimiter false); 1.21.4's
     * sendParticles(options, false, false, ..) is the same, the no-flag overload (method_65096) passes these.
     */
    public static boolean noForce() {
        return false;
    }

    // ---- ClientboundPlayerInfoUpdatePacket.Entry ----

    /**
     * 1.21.4's Entry carries showHat, written only for the UPDATE_HAT action that 1.21.3 did not have. A
     * client that was never told keeps the hat shown (PlayerInfo's initial showHat is true), so an entry a
     * 1.21.3 mod built carries that same "nothing said" value.
     */
    public static boolean hatNotSaid() {
        return true;
    }
}
