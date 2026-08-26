package top.dext.centurybridge.facade.v1_21_4;

public interface class_8489 {
    <T> T getElement(net.minecraft.class_5321<T> key);

    default <T> T getElement(
            net.minecraft.class_8490<T> type,
            net.minecraft.class_2960 id) {
        throw new UnsupportedOperationException(
                "CenturyBridge: lookup by legacy loot data type and identifier is unavailable in 1.21.4");
    }

    default <T> java.util.Optional<T> getElementOptional(
            net.minecraft.class_5321<T> key) {
        throw new UnsupportedOperationException(
                "CenturyBridge: lookup by registry key is unavailable in 1.21.4");
    }

    default <T> java.util.Optional<T> getElementOptional(
            net.minecraft.class_8490<T> type,
            net.minecraft.class_2960 id) {
        return java.util.Optional.ofNullable(this.getElement(type, id));
    }

    default net.minecraft.class_52 getLootTable(net.minecraft.class_2960 id) {
        throw new UnsupportedOperationException(
                "CenturyBridge: legacy loot table lookup is unavailable in 1.21.4");
    }
}