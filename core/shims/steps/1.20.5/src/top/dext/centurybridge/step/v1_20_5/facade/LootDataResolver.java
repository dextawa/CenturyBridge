package top.dext.centurybridge.step.v1_20_5.facade;

import java.util.Optional;

/**
 * 1.20.4's LootDataResolver: loot data elements by id. 1.20.5 resolves them through a registry lookup
 * (HolderGetter.Provider) instead; rt/CbLootData turns one into the other both ways. The four default
 * methods are 1.20.4's bodies.
 */
public interface LootDataResolver {

    <T> T getElement(LootDataId<T> id);

    default <T> T getElement(net.minecraft.class_8490<T> type, net.minecraft.class_2960 location) {
        return getElement(new LootDataId<>(type, location));
    }

    default <T> Optional<T> getElementOptional(LootDataId<T> id) {
        return Optional.ofNullable(getElement(id));
    }

    default <T> Optional<T> getElementOptional(net.minecraft.class_8490<T> type, net.minecraft.class_2960 location) {
        return getElementOptional(new LootDataId<>(type, location));
    }

    default net.minecraft.class_52 getLootTable(net.minecraft.class_2960 location) {
        return getElementOptional(net.minecraft.class_8490.field_44498, location).orElse(net.minecraft.class_52.field_948);
    }
}
