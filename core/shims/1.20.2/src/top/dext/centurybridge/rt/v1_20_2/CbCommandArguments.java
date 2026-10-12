package top.dext.centurybridge.rt.v1_20_2;

/**
 * 1.20.1 command-argument results on 1.20.2's: the argument readers that now return a wrapper
 * entry hand converted code what the 1.20.1 reader returned.
 */
public final class CbCommandArguments {

    /** IdentifierArgument.getAdvancement: 1.20.2 returns the AdvancementEntry, 1.20.1 the advancement in it */
    public static net.minecraft.class_161 advancement(net.minecraft.class_8779 entry) {
        return entry == null ? null : entry.comp_1920();
    }

    private CbCommandArguments() {
    }
}
