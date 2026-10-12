package top.dext.centurybridge.rt.v1_20_2;

/**
 * The 1.20.1 Scoreboard.getObjective(name) shape (gone in 1.20.2). Added to a converted scoreboard
 * subclass that overrides it (adapterHooks): calls and the vanilla lookups 1.20.1 routed through it
 * reach the override again (CbScoreboard, CbScoreboardObjectiveCalls).
 */
public interface CbLegacyScoreboardObjective {

    /** getObjective(name) */
    net.minecraft.class_266 method_1165(String name);
}
