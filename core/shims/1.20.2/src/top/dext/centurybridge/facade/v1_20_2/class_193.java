package top.dext.centurybridge.facade.v1_20_2;

/**
 * Facade for 1.20.1 net/minecraft/class_193 (RequirementsStrategy): criterion names -> requirement
 * groups, an array of arrays (each group satisfied by any of its names, all groups needed). 1.20.2 made
 * it AdvancementRequirements.Strategy, answering the AdvancementRequirements record over the same array;
 * every 1.20.1 reference is renamed here. AND / OR are 1.20.1's own two (each criterion its own group /
 * all criteria in one group), the same two strategies 1.20.2 keeps (AdvancementRequirements.allOf /
 * anyOf). Builder.requirements(strategy) takes either kind (CbAdvProgress.strategyOf).
 */
@FunctionalInterface
public interface class_193 {

    /** AND: every criterion is its own requirement group */
    class_193 field_16882 = names -> net.minecraft.class_8782.method_53668(names).comp_1922();

    /** OR: one group with every criterion */
    class_193 field_1257 = names -> net.minecraft.class_8782.method_53674(names).comp_1922();

    /** createRequirements(names) */
    String[][] createRequirements(java.util.Collection<String> names);
}
