package top.dext.centurybridge.step.v1_21_11.rt;

import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4871;

/** 1.21.10's PendingInvite (Realms, client-only) fields read from 1.21.11's record components */
@Environment(EnvType.CLIENT)
public final class CbLegacyRealms {

    /** invitationId */
    public static String field_22580(class_4871 invite) {
        return invite.comp_4999();
    }

    /** worldName */
    public static String field_22581(class_4871 invite) {
        return invite.comp_5000();
    }

    /** worldOwnerName */
    public static String field_22582(class_4871 invite) {
        return invite.comp_5001();
    }

    /** worldOwnerUuid */
    public static UUID field_22583(class_4871 invite) {
        return invite.comp_5002();
    }

    /** date: 1.21.10 a Date, 1.21.11 an Instant (the same moment) */
    public static java.util.Date field_22584(class_4871 invite) {
        return invite.comp_5003() == null ? null : java.util.Date.from(invite.comp_5003());
    }

    private CbLegacyRealms() {
    }

    // 1.21.10 RealmsSlotUpdateDto (class_11308) private fields (a mod's AW): 1.21.11 made it a record whose canonical
    // constructor takes the same values in the same order the 1.21.10 constructor stored them (javap of both: the
    // (int, RealmsWorldOptions, boolean) constructor fills field_60208 / 60211 / 60213 .. 60221 and comp_5012 .. 5022
    // from the same sources), so each field is the component at its position.

    /** RealmsSlotUpdateDto.field_60211 */
    public static int field_60211(net.minecraft.class_11308 dto) {
        return dto.comp_5013();
    }

    /** RealmsSlotUpdateDto.field_60213 */
    public static boolean field_60213(net.minecraft.class_11308 dto) {
        return dto.comp_5014();
    }

    /** RealmsSlotUpdateDto.field_60214 */
    public static int field_60214(net.minecraft.class_11308 dto) {
        return dto.comp_5015();
    }

    /** RealmsSlotUpdateDto.field_60215 */
    public static int field_60215(net.minecraft.class_11308 dto) {
        return dto.comp_5016();
    }

    /** RealmsSlotUpdateDto.field_60216 */
    public static String field_60216(net.minecraft.class_11308 dto) {
        return dto.comp_5017();
    }

    /** RealmsSlotUpdateDto.field_60217 */
    public static String field_60217(net.minecraft.class_11308 dto) {
        return dto.comp_5018();
    }

    /** RealmsSlotUpdateDto.field_60218 */
    public static net.minecraft.class_4877.class_8842 field_60218(net.minecraft.class_11308 dto) {
        return dto.comp_5019();
    }

    /** RealmsSlotUpdateDto.field_60219 */
    public static long field_60219(net.minecraft.class_11308 dto) {
        return dto.comp_5020();
    }

    /** RealmsSlotUpdateDto.field_60220 */
    public static String field_60220(net.minecraft.class_11308 dto) {
        return dto.comp_5021();
    }

    /** RealmsSlotUpdateDto.field_60221 */
    public static boolean field_60221(net.minecraft.class_11308 dto) {
        return dto.comp_5022();
    }
}
