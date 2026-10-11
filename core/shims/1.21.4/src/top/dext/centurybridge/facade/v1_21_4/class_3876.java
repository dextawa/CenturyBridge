package top.dext.centurybridge.facade.v1_21_4;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class class_3876 extends net.minecraft.class_517 {
    private static final net.minecraft.class_2561 field_26596;

    public class_3876() {
        super(null, field_26596, java.util.List.of());
    }

    @Override
    protected net.minecraft.class_2561 method_17064() {
        return field_26596;
    }

    protected java.util.Set<net.minecraft.class_1792> method_17065() {
        throw new UnsupportedOperationException("CenturyBridge: furnace fuel item discovery is incompatible with Minecraft 1.21.4");
    }

    static {
        field_26596 = net.minecraft.class_2561.method_43471("gui.recipebook.toggleRecipes.smeltable");
    }
}