package top.dext.centurybridge.step.v26_1.rt;

import net.minecraft.world.entity.ai.ActivityData;
import net.minecraft.world.entity.ai.Brain;

/**
 * 1.21.11's <Mob>Ai.init<X>Activity(.., brain) statics, which 26.1 turned into ActivityData factories: see
 * core/tools/gen_wall_ai_activities.py, which generates this file -- do not edit.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class CbWallAiActivities {

    private CbWallAiActivities() {
    }

    /** 1.21.11 added the activity once per call; so does this */
    private static void add(Brain brain, ActivityData a) {
        brain.addActivity(a.activityType(), a.behaviorPriorityPairs(), a.conditions(), a.memoriesToEraseWhenStopped());
    }

    /** 1.21.11 CreakingAi.initCoreActivity (class_10280.method_64634(Lnet/minecraft/class_4095;)V) */
    public static void creakingAi_initCoreActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.monster.creaking.CreakingAi.initCoreActivity());
    }

    /** 1.21.11 CreakingAi.initIdleActivity (class_10280.method_64637(Lnet/minecraft/class_4095;)V) */
    public static void creakingAi_initIdleActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.monster.creaking.CreakingAi.initIdleActivity());
    }

    /** 1.21.11 CreakingAi.initFightActivity (class_10280.method_64638(Lnet/minecraft/class_10275;Lnet/minecraft/class_4095;)V) */
    public static void creakingAi_initFightActivity(net.minecraft.world.entity.monster.creaking.Creaking a0, Brain brain) {
        add(brain, net.minecraft.world.entity.monster.creaking.CreakingAi.initFightActivity(a0));
    }

    /** 1.21.11 HappyGhastAi.initCoreActivity (class_11192.method_70710(Lnet/minecraft/class_4095;)V) */
    public static void happyGhastAi_initCoreActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.animal.happyghast.HappyGhastAi.initCoreActivity());
    }

    /** 1.21.11 HappyGhastAi.initIdleActivity (class_11192.method_70712(Lnet/minecraft/class_4095;)V) */
    public static void happyGhastAi_initIdleActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.animal.happyghast.HappyGhastAi.initIdleActivity());
    }

    /** 1.21.11 HappyGhastAi.initPanicActivity (class_11192.method_70714(Lnet/minecraft/class_4095;)V) */
    public static void happyGhastAi_initPanicActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.animal.happyghast.HappyGhastAi.initPanicActivity());
    }

    /** 1.21.11 CopperGolemAi.initCoreActivity (class_11574.method_72488(Lnet/minecraft/class_4095;)V) */
    public static void copperGolemAi_initCoreActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.animal.golem.CopperGolemAi.initCoreActivity());
    }

    /** 1.21.11 CopperGolemAi.initIdleActivity (class_11574.method_72491(Lnet/minecraft/class_4095;)V) */
    public static void copperGolemAi_initIdleActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.animal.golem.CopperGolemAi.initIdleActivity());
    }

    /** 1.21.11 NautilusAi.initCoreActivity (class_12118.method_75179(Lnet/minecraft/class_4095;)V) */
    public static void nautilusAi_initCoreActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.animal.nautilus.NautilusAi.initCoreActivity());
    }

    /** 1.21.11 NautilusAi.initIdleActivity (class_12118.method_75181(Lnet/minecraft/class_4095;)V) */
    public static void nautilusAi_initIdleActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.animal.nautilus.NautilusAi.initIdleActivity());
    }

    /** 1.21.11 NautilusAi.initFightActivity (class_12118.method_75182(Lnet/minecraft/class_4095;)V) */
    public static void nautilusAi_initFightActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.animal.nautilus.NautilusAi.initFightActivity());
    }

    /** 1.21.11 ZombieNautilusAi.initCoreActivity (class_12120.method_75191(Lnet/minecraft/class_4095;)V) */
    public static void zombieNautilusAi_initCoreActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.animal.nautilus.ZombieNautilusAi.initCoreActivity());
    }

    /** 1.21.11 ZombieNautilusAi.initIdleActivity (class_12120.method_75192(Lnet/minecraft/class_4095;)V) */
    public static void zombieNautilusAi_initIdleActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.animal.nautilus.ZombieNautilusAi.initIdleActivity());
    }

    /** 1.21.11 ZombieNautilusAi.initFightActivity (class_12120.method_75193(Lnet/minecraft/class_4095;)V) */
    public static void zombieNautilusAi_initFightActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.animal.nautilus.ZombieNautilusAi.initFightActivity());
    }

    /** 1.21.11 HoglinAi.initCoreActivity (class_4835.method_24666(Lnet/minecraft/class_4095;)V) */
    public static void hoglinAi_initCoreActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.monster.hoglin.HoglinAi.initCoreActivity());
    }

    /** 1.21.11 HoglinAi.initIdleActivity (class_4835.method_24673(Lnet/minecraft/class_4095;)V) */
    public static void hoglinAi_initIdleActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.monster.hoglin.HoglinAi.initIdleActivity());
    }

    /** 1.21.11 HoglinAi.initFightActivity (class_4835.method_24676(Lnet/minecraft/class_4095;)V) */
    public static void hoglinAi_initFightActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.monster.hoglin.HoglinAi.initFightActivity());
    }

    /** 1.21.11 HoglinAi.initRetreatActivity (class_4835.method_24679(Lnet/minecraft/class_4095;)V) */
    public static void hoglinAi_initRetreatActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.monster.hoglin.HoglinAi.initRetreatActivity());
    }

    /** 1.21.11 PiglinAi.initCoreActivity (class_4838.method_24725(Lnet/minecraft/class_4095;)V) */
    public static void piglinAi_initCoreActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.monster.piglin.PiglinAi.initCoreActivity());
    }

    /** 1.21.11 PiglinAi.initIdleActivity (class_4838.method_24743(Lnet/minecraft/class_4095;)V) */
    public static void piglinAi_initIdleActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.monster.piglin.PiglinAi.initIdleActivity());
    }

    /** 1.21.11 PiglinAi.initFightActivity (class_4838.method_24751(Lnet/minecraft/class_4836;Lnet/minecraft/class_4095;)V) */
    public static void piglinAi_initFightActivity(net.minecraft.world.entity.monster.piglin.Piglin a0, Brain brain) {
        add(brain, net.minecraft.world.entity.monster.piglin.PiglinAi.initFightActivity(a0));
    }

    /** 1.21.11 PiglinAi.initCelebrateActivity (class_4838.method_24756(Lnet/minecraft/class_4095;)V) */
    public static void piglinAi_initCelebrateActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.monster.piglin.PiglinAi.initCelebrateActivity());
    }

    /** 1.21.11 PiglinAi.initAdmireItemActivity (class_4838.method_24760(Lnet/minecraft/class_4095;)V) */
    public static void piglinAi_initAdmireItemActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.monster.piglin.PiglinAi.initAdmireItemActivity());
    }

    /** 1.21.11 PiglinAi.initRetreatActivity (class_4838.method_24764(Lnet/minecraft/class_4095;)V) */
    public static void piglinAi_initRetreatActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.monster.piglin.PiglinAi.initRetreatActivity());
    }

    /** 1.21.11 PiglinAi.initRideHoglinActivity (class_4838.method_24768(Lnet/minecraft/class_4095;)V) */
    public static void piglinAi_initRideHoglinActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.monster.piglin.PiglinAi.initRideHoglinActivity());
    }

    /** 1.21.11 Zoglin.initCoreActivity (class_5136.method_26928(Lnet/minecraft/class_4095;)V) */
    public static void zoglin_initCoreActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.monster.Zoglin.initCoreActivity());
    }

    /** 1.21.11 Zoglin.initIdleActivity (class_5136.method_26929(Lnet/minecraft/class_4095;)V) */
    public static void zoglin_initIdleActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.monster.Zoglin.initIdleActivity());
    }

    /** 1.21.11 Zoglin.initFightActivity (class_5136.method_26930(Lnet/minecraft/class_4095;)V) */
    public static void zoglin_initFightActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.monster.Zoglin.initFightActivity());
    }

    /** 1.21.11 PiglinBruteAi.initCoreActivity (class_5420.method_30257(Lnet/minecraft/class_5419;Lnet/minecraft/class_4095;)V) (1.21.11's mob argument dropped: 26.1 builds the activity without it) */
    public static void piglinBruteAi_initCoreActivity(net.minecraft.world.entity.monster.piglin.PiglinBrute a0, Brain brain) {
        add(brain, net.minecraft.world.entity.monster.piglin.PiglinBruteAi.initCoreActivity());
    }

    /** 1.21.11 PiglinBruteAi.initIdleActivity (class_5420.method_30260(Lnet/minecraft/class_5419;Lnet/minecraft/class_4095;)V) (1.21.11's mob argument dropped: 26.1 builds the activity without it) */
    public static void piglinBruteAi_initIdleActivity(net.minecraft.world.entity.monster.piglin.PiglinBrute a0, Brain brain) {
        add(brain, net.minecraft.world.entity.monster.piglin.PiglinBruteAi.initIdleActivity());
    }

    /** 1.21.11 PiglinBruteAi.initFightActivity (class_5420.method_30262(Lnet/minecraft/class_5419;Lnet/minecraft/class_4095;)V) */
    public static void piglinBruteAi_initFightActivity(net.minecraft.world.entity.monster.piglin.PiglinBrute a0, Brain brain) {
        add(brain, net.minecraft.world.entity.monster.piglin.PiglinBruteAi.initFightActivity(a0));
    }

    /** 1.21.11 AxolotlAi.initPlayDeadActivity (class_5768.method_33246(Lnet/minecraft/class_4095;)V) */
    public static void axolotlAi_initPlayDeadActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.animal.axolotl.AxolotlAi.initPlayDeadActivity());
    }

    /** 1.21.11 AxolotlAi.initFightActivity (class_5768.method_33249(Lnet/minecraft/class_4095;)V) */
    public static void axolotlAi_initFightActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.animal.axolotl.AxolotlAi.initFightActivity());
    }

    /** 1.21.11 AxolotlAi.initCoreActivity (class_5768.method_33251(Lnet/minecraft/class_4095;)V) */
    public static void axolotlAi_initCoreActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.animal.axolotl.AxolotlAi.initCoreActivity());
    }

    /** 1.21.11 AxolotlAi.initIdleActivity (class_5768.method_33252(Lnet/minecraft/class_4095;)V) */
    public static void axolotlAi_initIdleActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.animal.axolotl.AxolotlAi.initIdleActivity());
    }

    /** 1.21.11 GoatAi.initCoreActivity (class_6054.method_35185(Lnet/minecraft/class_4095;)V) */
    public static void goatAi_initCoreActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.animal.goat.GoatAi.initCoreActivity());
    }

    /** 1.21.11 GoatAi.initIdleActivity (class_6054.method_35187(Lnet/minecraft/class_4095;)V) */
    public static void goatAi_initIdleActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.animal.goat.GoatAi.initIdleActivity());
    }

    /** 1.21.11 GoatAi.initLongJumpActivity (class_6054.method_35188(Lnet/minecraft/class_4095;)V) */
    public static void goatAi_initLongJumpActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.animal.goat.GoatAi.initLongJumpActivity());
    }

    /** 1.21.11 GoatAi.initRamActivity (class_6054.method_36288(Lnet/minecraft/class_4095;)V) */
    public static void goatAi_initRamActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.animal.goat.GoatAi.initRamActivity());
    }

    /** 1.21.11 FrogAi.initCoreActivity (class_7107.method_41372(Lnet/minecraft/class_4095;)V) */
    public static void frogAi_initCoreActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.animal.frog.FrogAi.initCoreActivity());
    }

    /** 1.21.11 FrogAi.initIdleActivity (class_7107.method_41374(Lnet/minecraft/class_4095;)V) */
    public static void frogAi_initIdleActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.animal.frog.FrogAi.initIdleActivity());
    }

    /** 1.21.11 FrogAi.initSwimActivity (class_7107.method_41376(Lnet/minecraft/class_4095;)V) */
    public static void frogAi_initSwimActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.animal.frog.FrogAi.initSwimActivity());
    }

    /** 1.21.11 FrogAi.initLaySpawnActivity (class_7107.method_41378(Lnet/minecraft/class_4095;)V) */
    public static void frogAi_initLaySpawnActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.animal.frog.FrogAi.initLaySpawnActivity());
    }

    /** 1.21.11 FrogAi.initJumpActivity (class_7107.method_41380(Lnet/minecraft/class_4095;)V) */
    public static void frogAi_initJumpActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.animal.frog.FrogAi.initJumpActivity());
    }

    /** 1.21.11 FrogAi.initTongueActivity (class_7107.method_41382(Lnet/minecraft/class_4095;)V) */
    public static void frogAi_initTongueActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.animal.frog.FrogAi.initTongueActivity());
    }

    /** 1.21.11 TadpoleAi.initCoreActivity (class_7111.method_41402(Lnet/minecraft/class_4095;)V) */
    public static void tadpoleAi_initCoreActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.animal.frog.TadpoleAi.initCoreActivity());
    }

    /** 1.21.11 TadpoleAi.initIdleActivity (class_7111.method_41403(Lnet/minecraft/class_4095;)V) */
    public static void tadpoleAi_initIdleActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.animal.frog.TadpoleAi.initIdleActivity());
    }

    /** 1.21.11 WardenAi.initCoreActivity (class_7261.method_42227(Lnet/minecraft/class_4095;)V) */
    public static void wardenAi_initCoreActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.monster.warden.WardenAi.initCoreActivity());
    }

    /** 1.21.11 WardenAi.initEmergeActivity (class_7261.method_42233(Lnet/minecraft/class_4095;)V) */
    public static void wardenAi_initEmergeActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.monster.warden.WardenAi.initEmergeActivity());
    }

    /** 1.21.11 WardenAi.initFightActivity (class_7261.method_42236(Lnet/minecraft/class_7260;Lnet/minecraft/class_4095;)V) */
    public static void wardenAi_initFightActivity(net.minecraft.world.entity.monster.warden.Warden a0, Brain brain) {
        add(brain, net.minecraft.world.entity.monster.warden.WardenAi.initFightActivity(a0));
    }

    /** 1.21.11 WardenAi.initDiggingActivity (class_7261.method_42237(Lnet/minecraft/class_4095;)V) */
    public static void wardenAi_initDiggingActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.monster.warden.WardenAi.initDiggingActivity());
    }

    /** 1.21.11 WardenAi.initIdleActivity (class_7261.method_42240(Lnet/minecraft/class_4095;)V) */
    public static void wardenAi_initIdleActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.monster.warden.WardenAi.initIdleActivity());
    }

    /** 1.21.11 WardenAi.initInvestigateActivity (class_7261.method_42242(Lnet/minecraft/class_4095;)V) */
    public static void wardenAi_initInvestigateActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.monster.warden.WardenAi.initInvestigateActivity());
    }

    /** 1.21.11 WardenAi.initSniffingActivity (class_7261.method_42244(Lnet/minecraft/class_4095;)V) */
    public static void wardenAi_initSniffingActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.monster.warden.WardenAi.initSniffingActivity());
    }

    /** 1.21.11 WardenAi.initRoarActivity (class_7261.method_42245(Lnet/minecraft/class_4095;)V) */
    public static void wardenAi_initRoarActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.monster.warden.WardenAi.initRoarActivity());
    }

    /** 1.21.11 AllayAi.initCoreActivity (class_7299.method_42663(Lnet/minecraft/class_4095;)V) */
    public static void allayAi_initCoreActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.animal.allay.AllayAi.initCoreActivity());
    }

    /** 1.21.11 AllayAi.initIdleActivity (class_7299.method_42666(Lnet/minecraft/class_4095;)V) */
    public static void allayAi_initIdleActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.animal.allay.AllayAi.initIdleActivity());
    }

    /** 1.21.11 CamelAi.initCoreActivity (class_7691.method_45369(Lnet/minecraft/class_4095;)V) */
    public static void camelAi_initCoreActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.animal.camel.CamelAi.initCoreActivity());
    }

    /** 1.21.11 CamelAi.initIdleActivity (class_7691.method_45370(Lnet/minecraft/class_4095;)V) */
    public static void camelAi_initIdleActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.animal.camel.CamelAi.initIdleActivity());
    }

    /** 1.21.11 SnifferAi.initCoreActivity (class_8155.method_49154(Lnet/minecraft/class_4095;)V) */
    public static void snifferAi_initCoreActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.animal.sniffer.SnifferAi.initCoreActivity());
    }

    /** 1.21.11 SnifferAi.initSniffingActivity (class_8155.method_49155(Lnet/minecraft/class_4095;)V) */
    public static void snifferAi_initSniffingActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.animal.sniffer.SnifferAi.initSniffingActivity());
    }

    /** 1.21.11 SnifferAi.initDigActivity (class_8155.method_49156(Lnet/minecraft/class_4095;)V) */
    public static void snifferAi_initDigActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.animal.sniffer.SnifferAi.initDigActivity());
    }

    /** 1.21.11 SnifferAi.initIdleActivity (class_8155.method_49157(Lnet/minecraft/class_4095;)V) */
    public static void snifferAi_initIdleActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.animal.sniffer.SnifferAi.initIdleActivity());
    }

    /** 1.21.11 BreezeAi.initCoreActivity (class_8950.method_55018(Lnet/minecraft/class_4095;)V) */
    public static void breezeAi_initCoreActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.monster.breeze.BreezeAi.initCoreActivity());
    }

    /** 1.21.11 BreezeAi.initFightActivity (class_8950.method_55019(Lnet/minecraft/class_8949;Lnet/minecraft/class_4095;)V) */
    public static void breezeAi_initFightActivity(net.minecraft.world.entity.monster.breeze.Breeze a0, Brain brain) {
        add(brain, net.minecraft.world.entity.monster.breeze.BreezeAi.initFightActivity(a0));
    }

    /** 1.21.11 BreezeAi.initIdleActivity (class_8950.method_55750(Lnet/minecraft/class_4095;)V) */
    public static void breezeAi_initIdleActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.monster.breeze.BreezeAi.initIdleActivity());
    }

    /** 1.21.11 ArmadilloAi.initCoreActivity (class_9071.method_55737(Lnet/minecraft/class_4095;)V) */
    public static void armadilloAi_initCoreActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.animal.armadillo.ArmadilloAi.initCoreActivity());
    }

    /** 1.21.11 ArmadilloAi.initIdleActivity (class_9071.method_55738(Lnet/minecraft/class_4095;)V) */
    public static void armadilloAi_initIdleActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.animal.armadillo.ArmadilloAi.initIdleActivity());
    }

    /** 1.21.11 ArmadilloAi.initScaredActivity (class_9071.method_55739(Lnet/minecraft/class_4095;)V) */
    public static void armadilloAi_initScaredActivity(Brain brain) {
        add(brain, net.minecraft.world.entity.animal.armadillo.ArmadilloAi.initScaredActivity());
    }
}
