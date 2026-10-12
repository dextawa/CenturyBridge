package top.dext.centurybridge.rt.v1_20_2;

/**
 * 1.20.1 PathNavigation.isInLiquid() (protected, method_6351), answered again by every navigation
 * through a mixin (1.20.1 body: the mob is in water or lava). 1.20.2 asks the mob
 * (Entity.isInLiquid) wherever 1.20.1's navigations asked themselves; see {@link CbNavigation}.
 * The cb$ methods are public trampolines into the navigation's protected members.
 */
public interface CbLegacyNavigation {

    /** 1.20.1 isInLiquid() */
    boolean method_6351();

    /** isInLiquid(), called virtually from inside PathNavigation */
    boolean cb$legacyIsInLiquid();

    /** the navigation's mob */
    net.minecraft.class_1308 cb$navMob();

    /** 1.20.2 canUpdatePath(), called virtually */
    boolean cb$canUpdatePath();

    /** 1.20.1 AmphibiousPathNavigation.canMoveDirectly(from, to): isInLiquid() and a clear way */
    boolean cb$amphibiousCanMoveDirectly(net.minecraft.class_243 from, net.minecraft.class_243 to);
}
