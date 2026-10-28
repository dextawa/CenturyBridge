package top.dext.centurybridge.rt.v1_20_2;

/**
 * Answers of 1.20.2's advancement data generators turned back into what 1.20.1's answered, for the step's call reshapes
 * of their private helpers (a mod's @Invoker / @Shadow on them gets 1.20.1's answer type).
 */
public final class CbAdvDatagen {

    private CbAdvDatagen() {
    }

    /**
     * VanillaAdventureAdvancements.fireCountAndBystander: 1.20.1 answered the LightningStrikeTrigger instance, 1.20.2 the
     * criterion holding it (CbAdvCriteria.instanceOf remembers the criterion for the way back)
     */
    public static net.minecraft.class_6405.class_6406 lightningStrikeInstance(net.minecraft.class_175<?> criterion) {
        return (net.minecraft.class_6405.class_6406) CbAdvCriteria.instanceOf(criterion);
    }
}
