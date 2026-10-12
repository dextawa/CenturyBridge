package top.dext.centurybridge.step.v1_20_3.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Facade for 1.20.2's net/minecraft/class_370$class_371 (SystemToast.SystemToastIds), an enum that 1.20.3
 * replaced by SystemToast.SystemToastId, a plain class of constants. The eight 1.20.2 constants, under
 * their 1.20.2 names and order (name(), valueOf, values() as before), each standing for its 1.20.3 id;
 * TUTORIAL_HINT, which 1.20.3 dropped, is an id of its own with 1.20.2's 5 s display time. The fields
 * field_NNNN are the intermediary names mods refer to the constants by.
 */
@Environment(EnvType.CLIENT)
public enum class_370$class_371 {
    TUTORIAL_HINT,
    NARRATOR_TOGGLE,
    WORLD_BACKUP,
    PACK_LOAD_FAILURE,
    WORLD_ACCESS_FAILURE,
    PACK_COPY_FAILURE,
    PERIODIC_NOTIFICATION,
    UNSECURE_SERVER_WARNING(10000L);

    public static final class_370$class_371 field_2218 = TUTORIAL_HINT;
    public static final class_370$class_371 field_2219 = NARRATOR_TOGGLE;
    public static final class_370$class_371 field_2220 = WORLD_BACKUP;
    public static final class_370$class_371 field_21809 = PACK_LOAD_FAILURE;
    public static final class_370$class_371 field_23774 = WORLD_ACCESS_FAILURE;
    public static final class_370$class_371 field_25445 = PACK_COPY_FAILURE;
    public static final class_370$class_371 field_36445 = PERIODIC_NOTIFICATION;
    public static final class_370$class_371 field_39915 = UNSECURE_SERVER_WARNING;

    /** displayTime: how long the toast shows, in milliseconds */
    public final long field_39340;

    private net.minecraft.class_370.class_9037 id;

    class_370$class_371() {
        this(5000L);
    }

    class_370$class_371(long displayTime) {
        this.field_39340 = displayTime;
    }

    /** the 1.20.3 id this constant stands for */
    public net.minecraft.class_370.class_9037 cb$id() {
        if (id == null) {
            id = switch (this) {
                case TUTORIAL_HINT -> new net.minecraft.class_370.class_9037(field_39340);
                case NARRATOR_TOGGLE -> net.minecraft.class_370.class_9037.field_47583;
                case WORLD_BACKUP -> net.minecraft.class_370.class_9037.field_47584;
                case PACK_LOAD_FAILURE -> net.minecraft.class_370.class_9037.field_47585;
                case WORLD_ACCESS_FAILURE -> net.minecraft.class_370.class_9037.field_47586;
                case PACK_COPY_FAILURE -> net.minecraft.class_370.class_9037.field_47587;
                case PERIODIC_NOTIFICATION -> net.minecraft.class_370.class_9037.field_47588;
                case UNSECURE_SERVER_WARNING -> net.minecraft.class_370.class_9037.field_47589;
            };
        }
        return id;
    }

    /** the constant a 1.20.3 id stands for, or null for an id 1.20.2 did not have */
    public static class_370$class_371 cb$of(net.minecraft.class_370.class_9037 id) {
        for (class_370$class_371 c : values()) {
            if (c.cb$id() == id) {
                return c;
            }
        }
        return null;
    }
}
