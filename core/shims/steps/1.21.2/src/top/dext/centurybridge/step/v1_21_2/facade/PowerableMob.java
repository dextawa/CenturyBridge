package top.dext.centurybridge.step.v1_21_2.facade;

/**
 * 1.21.1 PowerableMob (net/minecraft/class_4582), the interface of mobs with a power swirl (Creeper charged, WitherBoss
 * below half health), which 1.21.2 removed (its renderers read render state instead). Creeper and WitherBoss implement
 * it again (CbEntityPowerableMixin), so instanceof, casts and calls behave as in 1.21.1.
 */
public interface PowerableMob {
    /** isPowered() */
    boolean method_6872();
}
