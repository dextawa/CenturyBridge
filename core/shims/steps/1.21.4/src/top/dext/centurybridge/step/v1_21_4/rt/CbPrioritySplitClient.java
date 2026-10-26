package top.dext.centurybridge.step.v1_21_4.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * What a restoring layer mixin (at {@link CbVanillaReads#PRIORITY}) shares with the injectors into 1.21.4's own
 * methods that were split off it into a default-priority {@code <name>Hooks} mixin: the state and restored members both
 * use, reached through the restorer (its own state; its restored members called virtually from the restorer's own
 * code, so a subclass's override still answers).
 */
@Environment(EnvType.CLIENT)
public final class CbPrioritySplitClient {
    private CbPrioritySplitClient() {
    }

    /** CbDataModelTemplateMixin: 1.21.3's create(id, mapping, output) through create(.., jsonFactory) and createBaseTemplate */
    public interface ModelTemplateCreate {
        net.minecraft.class_2960 cb$v1_21_4$legacyCreate(net.minecraft.class_2960 id, net.minecraft.class_4944 mapping,
                java.util.function.BiConsumer<net.minecraft.class_2960, java.util.function.Supplier<com.google.gson.JsonElement>> output);
    }

    /** CbModelBlockModelDeserializerMixin: 1.21.3's getOverrides(context, json), called virtually */
    public interface ModelOverrides {
        java.util.List<top.dext.centurybridge.step.v1_21_4.facade.ItemOverride> cb$v1_21_4$getOverrides(com.google.gson.JsonDeserializationContext context,
                com.google.gson.JsonObject json);
    }

    /** CbItemRendererMixin: 1.21.3's renderStatic body, getModel then render, called virtually */
    public interface ItemRenderStatic {
        void cb$v1_21_4$renderStatic(net.minecraft.class_1309 entity, net.minecraft.class_1799 stack, net.minecraft.class_811 context, boolean leftHand,
                net.minecraft.class_4587 pose, net.minecraft.class_4597 buffers, net.minecraft.class_1937 level, int light, int overlay, int seed);
    }

    /** CbGuiScrollWidgetMixin: 1.21.3's clicked(x, y), called virtually */
    public interface WidgetClicked {
        boolean cb$v1_21_4$askClicked(double x, double y);
    }

    /** CbGuiScrollTextAreaMixin: 1.21.3's withinContentAreaPoint(x, y), called virtually */
    public interface TextAreaContentPoint {
        boolean cb$v1_21_4$contentAreaPoint(double x, double y);
    }

    /** CbRenderDispatchMixin: a dispatcher built with 1.21.3's Suppliers takes its renderers from them (reload) */
    public interface DispatchSuppliers {
        void cb$v1_21_4$askSuppliers();
    }

    /** CbModelBlockModelMixin: the texture map as set (null until asked or set), the root model and 1.21.3's bake */
    public interface BlockModelLegacy {
        java.util.Map<String, com.mojang.datafixers.util.Either<net.minecraft.class_4730, String>> cb$v1_21_4$textureMapIfSet();

        net.minecraft.class_793 cb$v1_21_4$rootModel();

        net.minecraft.class_1087 cb$v1_21_4$bake(net.minecraft.class_7775 baker,
                java.util.function.Function<net.minecraft.class_4730, net.minecraft.class_1058> sprites, net.minecraft.class_3665 state);
    }
}
