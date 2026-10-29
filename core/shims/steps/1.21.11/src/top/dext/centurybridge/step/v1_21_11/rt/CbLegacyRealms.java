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
}
