package top.dext.centurybridge.facade.v1_21_4;

public interface class_5151 {
    net.minecraft.class_1304 method_7685();

    default net.minecraft.class_3414 method_31570() {
        throw new UnsupportedOperationException("CenturyBridge: equipment sound API is incompatible with Minecraft 1.21.4");
    }

    default net.minecraft.class_1269 method_48576(
            net.minecraft.class_1792 item,
            net.minecraft.class_1937 world,
            net.minecraft.class_1657 player,
            net.minecraft.class_1268 hand) {
        throw new UnsupportedOperationException("CenturyBridge: legacy equipment swap API is incompatible with Minecraft 1.21.4");
    }

    static class_5151 method_48957(net.minecraft.class_1799 stack) {
        throw new UnsupportedOperationException("CenturyBridge: legacy equippable item lookup API is incompatible with Minecraft 1.21.4");
    }
}