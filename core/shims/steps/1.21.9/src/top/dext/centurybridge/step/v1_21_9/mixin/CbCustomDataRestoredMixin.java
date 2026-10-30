package top.dext.centurybridge.step.v1_21_9.mixin;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapEncoder;
import com.mojang.serialization.MapLike;
import java.util.UUID;
import net.minecraft.class_11352;
import net.minecraft.class_11362;
import net.minecraft.class_1297;
import net.minecraft.class_2378;
import net.minecraft.class_2487;
import net.minecraft.class_2520;
import net.minecraft.class_2586;
import net.minecraft.class_2960;
import net.minecraft.class_5321;
import net.minecraft.class_6880;
import net.minecraft.class_7225;
import net.minecraft.class_8942;
import net.minecraft.class_9279;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_9.rt.CbVanillaReads;

/**
 * 1.21.8 CustomData members 1.21.9 dropped when entity and block entity data moved to TypedEntityData (javap -c both):
 * parseEntityId / parseEntityType (the "id" entry, looked up in a registry), loadInto(entity) / loadInto(blockEntity,
 * registries) (merge the tag into the saved data and load it back, keeping the entity's UUID; for a block entity only
 * when it changes, rolling back on failure), update / read / read(ops) through map codecs, size, contains,
 * getUnsafe (CODEC_WITH_ID, a public static field, is rt/CbCustomData behind a field redirect). update wraps the encoded tag
 * with the constructor as 1.21.8 did (opened by the layer's access widener). 1.21.8's bodies over the same tag, every call they make
 * still in 1.21.9. 1.21.9 never calls them.
 */
@Mixin(value = class_9279.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbCustomDataRestoredMixin {

    @Unique
    private static final Logger cb$LOGGER = LogUtils.getLogger();

    @Shadow
    @Final
    private class_2487 field_49306;

    /** CustomData.parseEntityId() */
    public class_2960 method_65131() {
        return this.field_49306.method_67491("id", class_2960.field_25139).orElse(null);
    }

    /** CustomData.parseEntityType(registries, registryKey) */
    public <T> T method_65904(class_7225.class_7874 registries, class_5321<? extends class_2378<T>> registryKey) {
        class_2960 id = this.method_65131();
        if (id == null) {
            return null;
        }
        return registries.method_46759(registryKey)
            .flatMap(lookup -> ((class_7225.class_7226<T>) lookup).method_46746(class_5321.method_29179(registryKey, id)))
            .map(class_6880::comp_349)
            .orElse(null);
    }

    /** CustomData.loadInto(entity) */
    public void method_57445(class_1297 entity) {
        try (class_8942.class_11340 reporter = new class_8942.class_11340(entity.method_71370(), cb$LOGGER)) {
            class_11362 output = class_11362.method_71459(reporter, entity.method_56673());
            entity.method_5647(output);
            class_2487 tag = output.method_71475();
            UUID uuid = entity.method_5667();
            tag.method_10543(this.field_49306);
            entity.method_5651(class_11352.method_71417(reporter, entity.method_56673(), tag));
            entity.method_5826(uuid);
        }
    }

    /** CustomData.loadInto(blockEntity, registries) */
    public boolean method_57449(class_2586 blockEntity, class_7225.class_7874 registries) {
        try (class_8942.class_11340 reporter = new class_8942.class_11340(blockEntity.method_71402(), cb$LOGGER)) {
            class_11362 output = class_11362.method_71459(reporter, registries);
            blockEntity.method_71401(output);
            class_2487 tag = output.method_71475();
            class_2487 before = tag.method_10553();
            tag.method_10543(this.field_49306);
            if (!tag.equals(before)) {
                try {
                    blockEntity.method_58691(class_11352.method_71417(reporter, registries, tag));
                    blockEntity.method_5431();
                    return true;
                } catch (Exception failure) {
                    cb$LOGGER.warn("Failed to apply custom data to block entity at {}", blockEntity.method_11016(), failure);
                    try {
                        blockEntity.method_58691(class_11352.method_71417(reporter.method_54946(() -> "(rollback)"), registries, before));
                    } catch (Exception rollbackFailure) {
                        cb$LOGGER.warn("Failed to rollback block entity at {} after failure", blockEntity.method_11016(), rollbackFailure);
                    }
                }
            }
            return false;
        }
    }

    /** CustomData.update(ops, encoder, value) */
    public <T> DataResult<class_9279> method_57447(DynamicOps<class_2520> ops, MapEncoder<T> encoder, T value) {
        return encoder.encode(value, ops, ops.mapBuilder()).build(this.field_49306)
            .map(tag -> new class_9279((class_2487) tag));
    }

    /** CustomData.read(decoder) */
    public <T> DataResult<T> method_57446(MapDecoder<T> decoder) {
        return this.method_59980(net.minecraft.class_2509.field_11560, decoder);
    }

    /** CustomData.read(ops, decoder) */
    public <T> DataResult<T> method_59980(DynamicOps<class_2520> ops, MapDecoder<T> decoder) {
        MapLike<class_2520> map = ops.getMap(this.field_49306).getOrThrow();
        return decoder.decode(ops, map);
    }

    /** CustomData.size() */
    public int method_57444() {
        return this.field_49306.method_10546();
    }

    /** CustomData.contains(key) */
    public boolean method_57450(String key) {
        return this.field_49306.method_10545(key);
    }

    /** CustomData.getUnsafe() */
    public class_2487 method_57463() {
        return this.field_49306;
    }
}
