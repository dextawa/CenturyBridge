package top.dext.centurybridge.facade.v1_20_2;

/**
 * Facade for 1.20.1 net/minecraft/class_69$class_70 (CompositeEntryBase.CompositeEntryConstructor)
 * with its 1.20.1 method: create(children[], conditions[]). The class survives in 1.20.2 with
 * create(List, List) instead, so a 1.20.1 lambda or implementation of it (the factory a composite
 * entry's serializer builds entries with) would implement a method 1.20.2 never calls; every 1.20.1
 * reference is renamed here. 1.20.1's only consumer, CompositeEntryBase.createSerializer, is
 * served by CbLootBridges.method_395.
 */
@FunctionalInterface
public interface class_69$class_70<T extends net.minecraft.class_69> {

    T create(net.minecraft.class_79[] children, net.minecraft.class_5341[] conditions);
}
