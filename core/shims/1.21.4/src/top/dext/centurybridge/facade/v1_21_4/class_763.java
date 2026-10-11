package top.dext.centurybridge.facade.v1_21_4;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class class_763 {
    public final it.unimi.dsi.fastutil.ints.Int2ObjectMap<net.minecraft.class_1091> field_4129;
    private final it.unimi.dsi.fastutil.ints.Int2ObjectMap<net.minecraft.class_1087> field_4130;
    private final net.minecraft.class_1092 field_4128;

    private static final java.util.Map<net.minecraft.class_1792, Integer> CENTURYBRIDGE_IDS =
            java.util.Collections.synchronizedMap(new java.util.IdentityHashMap<>());
    private static final java.util.concurrent.atomic.AtomicInteger CENTURYBRIDGE_NEXT_ID =
            new java.util.concurrent.atomic.AtomicInteger();

    public class_763(net.minecraft.class_1092 arg0) {
        this.field_4128 = java.util.Objects.requireNonNull(arg0, "modelManager");
        this.field_4129 = new it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap<>();
        this.field_4130 = new it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap<>();
    }

    public net.minecraft.class_1087 method_3308(net.minecraft.class_1799 arg0) {
        java.util.Objects.requireNonNull(arg0, "stack");
        return this.method_3304(arg0.method_7909());
    }

    public net.minecraft.class_1087 method_3304(net.minecraft.class_1792 arg0) {
        java.util.Objects.requireNonNull(arg0, "item");
        return this.field_4130.get(method_3306(arg0));
    }

    private static int method_3306(net.minecraft.class_1792 arg0) {
        java.util.Objects.requireNonNull(arg0, "item");
        synchronized (CENTURYBRIDGE_IDS) {
            Integer id = CENTURYBRIDGE_IDS.get(arg0);
            if (id == null) {
                id = CENTURYBRIDGE_NEXT_ID.getAndIncrement();
                CENTURYBRIDGE_IDS.put(arg0, id);
            }
            return id;
        }
    }

    public void method_3309(net.minecraft.class_1792 arg0, net.minecraft.class_1091 arg1) {
        java.util.Objects.requireNonNull(arg0, "item");
        java.util.Objects.requireNonNull(arg1, "modelId");
        this.field_4129.put(method_3306(arg0), arg1);
    }

    public net.minecraft.class_1092 method_3303() {
        return this.field_4128;
    }

    public void method_3310() {
        throw new UnsupportedOperationException(
                "CenturyBridge: rebuilding legacy item-model caches requires the deleted 1.20.1 model-manager API"
        );
    }
}