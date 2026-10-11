package top.dext.centurybridge.facade.v1_21_4;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public abstract class class_883<T extends net.minecraft.class_1492>
        extends net.minecraft.class_9991<T> {

    public static final java.util.Map<net.minecraft.class_1299<?>, net.minecraft.class_2960> field_4650 =
            java.util.Collections.emptyMap();

    public class_883(
            net.minecraft.class_5617.class_5618 context,
            float shadowRadius,
            net.minecraft.class_5601 modelLayer
    ) {
        super(context, modelLayer, modelLayer, false);
    }

    public net.minecraft.class_2960 method_3894(T entity) {
        net.minecraft.class_2960 texture = field_4650.get(entity.method_5864());
        if (texture == null) {
            throw new UnsupportedOperationException(
                    "CenturyBridge: no compatible horse texture mapping for entity type "
                            + entity.method_5864()
            );
        }
        return texture;
    }

    public net.minecraft.class_2960 method_3931(net.minecraft.class_1297 entity) {
        @SuppressWarnings("unchecked")
        T typedEntity = (T) entity;
        return method_3894(typedEntity);
    }
}