package top.dext.centurybridge.facade.v1_20_2;

/**
 * Facade for 1.20.1 net/minecraft/class_85$class_89 (LeafEntry.EntryConstructor, a protected
 * nested interface subclasses of LeafEntry implement as a lambda) with its 1.20.1 method:
 * build(weight, quality, conditions[], functions[]). 1.20.2 kept the interface with
 * build(int, int, List, List), so a 1.20.1 lambda would implement a method LeafEntry.Builder never
 * calls (AbstractMethodError when the entry is built); every 1.20.1 reference is renamed here and
 * its one consumer, LeafEntry.builder(factory), is served by CbLootBridges.method_434.
 */
@FunctionalInterface
public interface class_85$class_89 {

    net.minecraft.class_85 build(int weight, int quality, net.minecraft.class_5341[] conditions, net.minecraft.class_117[] functions);
}
