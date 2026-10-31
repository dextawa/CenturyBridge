package top.dext.centurybridge.step.v1_21_11.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4429;

/**
 * Realms UploadResult (class_4429): 1.21.10 a class with public final fields statusCode (field_20205, int) and
 * errorMessage (field_20206, String); 1.21.11 a record of the same two values in the same order, read through its
 * accessors comp_5175 / comp_5176 (javap -p both; the constructor (int, String) is unchanged). A 1.21.10 field read
 * becomes the accessor call (instanceFieldGetters, smx1-file-upload.json).
 */
@Environment(EnvType.CLIENT)
public final class CbUploadResults {
    private CbUploadResults() {
    }

    /** UploadResult.statusCode */
    public static int field_20205(class_4429 result) {
        return result.comp_5175();
    }

    /** UploadResult.errorMessage */
    public static String field_20206(class_4429 result) {
        return result.comp_5176();
    }
}
