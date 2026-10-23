package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.class_10179;
import net.minecraft.class_1299;
import net.minecraft.class_1308;
import net.minecraft.class_5321;

/**
 * Fabric API's entity members of 1.21.1 on 1.21.2's Fabric API.
 *
 * <ul>
 * <li>FabricEntityTypeBuilder.build(): 1.21.1's built its vanilla EntityType.Builder and called build(null)
 *     (build(String), no key); 1.21.2's takes the key. Built under a placeholder and remade with the real key when the
 *     type registers (CbEntityTypes, the vanilla Builder's default key functions -- the ones Fabric API's builder
 *     leaves in place). A mod subclass's own build() answers a call.</li>
 * <li>FabricEntityType.Builder.build() (injected into EntityType.Builder): 1.21.1's was build(null), the vanilla
 *     builder's build(String) (CbEntityTypes#build).</li>
 * <li>ServerLivingEntityEvents.MobConversion.onConversion(previous, converted, keepEquipment): 1.21.2's takes the
 *     ConversionParams; 1.21.1's boolean is its keepEquipment, and 1.21.1's convertTo copied canPickUpLoot exactly
 *     when it kept the equipment (ConversionParams.single(previous, keep, keep)).</li>
 * </ul>
 */
public final class CbEntityFapi {

    /** a mod builder subclass's own 1.21.1 build() (adapter hook) */
    public interface LegacyBuild {
        class_1299<?> build();
    }

    /** the 1.21.1 MobConversion shape, for lambdas compiled against it */
    @FunctionalInterface
    public interface LegacyConversion {
        void onConversion(class_1308 previous, class_1308 converted, boolean keepEquipment);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static class_1299 build(FabricEntityTypeBuilder builder) {
        if (builder instanceof LegacyBuild legacy) {
            return legacy.build();
        }
        return superBuild(builder);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static class_1299 superBuild(FabricEntityTypeBuilder builder) {
        class_5321<class_1299<?>> key = CbEntityTypes.placeholder();
        class_1299 type = builder.build(key);
        CbEntityTypes.pendingWithDefaults(type, key);
        return type;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static class_1299 build(FabricEntityTypeBuilder.Living builder) {
        return build((FabricEntityTypeBuilder) builder);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static class_1299 build(FabricEntityTypeBuilder.Mob builder) {
        return build((FabricEntityTypeBuilder) builder);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static class_1299 superBuild(FabricEntityTypeBuilder.Living builder) {
        return superBuild((FabricEntityTypeBuilder) builder);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static class_1299 superBuild(FabricEntityTypeBuilder.Mob builder) {
        return superBuild((FabricEntityTypeBuilder) builder);
    }

    /** FabricEntityType.Builder.build(): the vanilla builder's 1.21.1 build(null) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static class_1299 fabricBuild(FabricEntityType.Builder builder) {
        return CbEntityTypes.build((class_1299.class_1300) builder, null);
    }

    // ------------------------------------------------------------------------------------------ MobConversion

    public static boolean keepEquipment(class_10179 params) {
        return params.comp_3145();
    }

    public static void onConversion(ServerLivingEntityEvents.MobConversion listener, class_1308 previous, class_1308 converted,
                                    boolean keepEquipment) {
        listener.onConversion(previous, converted, class_10179.method_63607(previous, keepEquipment, keepEquipment));
    }

    public static ServerLivingEntityEvents.MobConversion conversion(LegacyConversion legacy) {
        return (previous, converted, params) -> legacy.onConversion(previous, converted, params.comp_3145());
    }

    private CbEntityFapi() {
    }
}
