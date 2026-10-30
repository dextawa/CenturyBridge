package top.dext.centurybridge.step.v1_21_9.rt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.class_9279;

/**
 * 1.21.8 CustomData.CODEC_WITH_ID (public static final; 1.21.9 dropped it with the entity data that moved to
 * TypedEntityData): CODEC requiring an "id" entry, 1.21.8's validate body (javap -c) read off 1.21.9's copyTag. A
 * converted mod's get-static of class_9279.field_49304 reads this field (fieldRedirects, s9d-customdata.json); a mixin
 * cannot merge a public static field into CustomData.
 */
public final class CbCustomData {
    public static final Codec<class_9279> CODEC_WITH_ID = class_9279.field_49303.validate(
        data -> data.method_57461().method_10558("id").isPresent() ? DataResult.success(data)
            : DataResult.error(() -> "Missing id for entity in: " + data));

    private CbCustomData() {
    }
}
