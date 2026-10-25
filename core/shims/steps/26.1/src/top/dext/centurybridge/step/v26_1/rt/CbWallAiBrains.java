package top.dext.centurybridge.step.v26_1.rt;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.ActivityData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.animal.allay.AllayAi;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.animal.armadillo.ArmadilloAi;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.axolotl.AxolotlAi;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.camel.CamelAi;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.FrogAi;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.animal.frog.TadpoleAi;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.goat.GoatAi;
import net.minecraft.world.entity.animal.golem.CopperGolem;
import net.minecraft.world.entity.animal.golem.CopperGolemAi;
import net.minecraft.world.entity.animal.happyghast.HappyGhast;
import net.minecraft.world.entity.animal.happyghast.HappyGhastAi;
import net.minecraft.world.entity.animal.nautilus.Nautilus;
import net.minecraft.world.entity.animal.nautilus.NautilusAi;
import net.minecraft.world.entity.animal.nautilus.ZombieNautilus;
import net.minecraft.world.entity.animal.nautilus.ZombieNautilusAi;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.animal.sniffer.SnifferAi;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.monster.breeze.BreezeAi;
import net.minecraft.world.entity.monster.creaking.Creaking;
import net.minecraft.world.entity.monster.creaking.CreakingAi;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.hoglin.HoglinAi;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.piglin.PiglinBruteAi;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.monster.warden.WardenAi;
import net.minecraft.world.entity.schedule.Activity;

/**
 * 1.21.11's <Mob>Ai.makeBrain(..) and brainProvider() statics, which 26.1 replaced by getActivities and each mob's
 * BRAIN_PROVIDER: see core/tools/gen_wall_ai_brains.py, which generates this file -- do not edit.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class CbWallAiBrains {

    private CbWallAiBrains() {
    }

    /** each activity the brain does not have yet (Brain.activityRequirements is keyed by activity) */
    private static <E extends LivingEntity> void addMissing(Brain<E> brain, List<ActivityData<E>> activities) {
        Map<Activity, ?> have = brain.activityRequirements;
        for (ActivityData<E> a : activities) {
            if (!have.containsKey(a.activityType())) {
                brain.addActivity(a.activityType(), a.behaviorPriorityPairs(), a.conditions(), a.memoriesToEraseWhenStopped());
            }
        }
    }

    /** 1.21.11 CreakingAi.brainProvider() (class_10280.method_64632): Creaking's BRAIN_PROVIDER */
    public static Brain.Provider<Creaking> creakingAi_brainProvider() {
        return Creaking.BRAIN_PROVIDER;
    }

    /** 1.21.11 CreakingAi.makeBrain (class_10280.method_64639(Lnet/minecraft/class_10275;Lnet/minecraft/class_4095;)Lnet/minecraft/class_4095;) */
    public static Brain<?> creakingAi_makeBrain(Creaking mob, Brain<Creaking> brain) {
        addMissing(brain, CreakingAi.getActivities(mob));
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    /** 1.21.11 HappyGhastAi.brainProvider() (class_11192.method_70705): HappyGhast's BRAIN_PROVIDER */
    public static Brain.Provider<HappyGhast> happyGhastAi_brainProvider() {
        return HappyGhast.BRAIN_PROVIDER;
    }

    /** 1.21.11 HappyGhastAi.makeBrain (class_11192.method_70707(Lnet/minecraft/class_4095;)Lnet/minecraft/class_4095;) */
    public static Brain<?> happyGhastAi_makeBrain(Brain<HappyGhast> brain) {
        addMissing(brain, HappyGhastAi.getActivities());
        brain.setCoreActivities(Set.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    /** 1.21.11 CopperGolemAi.brainProvider() (class_11574.method_72479): CopperGolem's BRAIN_PROVIDER */
    public static Brain.Provider<CopperGolem> copperGolemAi_brainProvider() {
        return CopperGolem.BRAIN_PROVIDER;
    }

    /** 1.21.11 CopperGolemAi.makeBrain (class_11574.method_72481(Lnet/minecraft/class_4095;)Lnet/minecraft/class_4095;) */
    public static Brain<?> copperGolemAi_makeBrain(Brain<CopperGolem> brain) {
        addMissing(brain, CopperGolemAi.getActivities());
        brain.setCoreActivities(Set.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    /** 1.21.11 NautilusAi.brainProvider() (class_12118.method_75167): Nautilus's BRAIN_PROVIDER */
    public static Brain.Provider<Nautilus> nautilusAi_brainProvider() {
        return Nautilus.BRAIN_PROVIDER;
    }

    /** 1.21.11 NautilusAi.makeBrain (class_12118.method_75172(Lnet/minecraft/class_4095;)Lnet/minecraft/class_4095;) */
    public static Brain<?> nautilusAi_makeBrain(Brain<Nautilus> brain) {
        addMissing(brain, NautilusAi.getActivities());
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    /** 1.21.11 ZombieNautilusAi.brainProvider() (class_12120.method_75185): ZombieNautilus's BRAIN_PROVIDER */
    public static Brain.Provider<ZombieNautilus> zombieNautilusAi_brainProvider() {
        return ZombieNautilus.BRAIN_PROVIDER;
    }

    /** 1.21.11 ZombieNautilusAi.makeBrain (class_12120.method_75188(Lnet/minecraft/class_4095;)Lnet/minecraft/class_4095;) */
    public static Brain<?> zombieNautilusAi_makeBrain(Brain<ZombieNautilus> brain) {
        addMissing(brain, ZombieNautilusAi.getActivities());
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    /** 1.21.11 HoglinAi.makeBrain (class_4835.method_24668(Lnet/minecraft/class_4095;)Lnet/minecraft/class_4095;) */
    public static Brain<?> hoglinAi_makeBrain(Brain<Hoglin> brain) {
        addMissing(brain, HoglinAi.getActivities());
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    /** 1.21.11 PiglinAi.makeBrain (class_4838.method_24732(Lnet/minecraft/class_4836;Lnet/minecraft/class_4095;)Lnet/minecraft/class_4095;) */
    public static Brain<?> piglinAi_makeBrain(Piglin mob, Brain<Piglin> brain) {
        addMissing(brain, PiglinAi.getActivities(mob));
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    /** 1.21.11 PiglinBruteAi.makeBrain (class_5420.method_30252(Lnet/minecraft/class_5419;Lnet/minecraft/class_4095;)Lnet/minecraft/class_4095;) */
    public static Brain<?> piglinBruteAi_makeBrain(PiglinBrute mob, Brain<PiglinBrute> brain) {
        addMissing(brain, PiglinBruteAi.getActivities(mob));
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    /** 1.21.11 AxolotlAi.makeBrain (class_5768.method_33243(Lnet/minecraft/class_4095;)Lnet/minecraft/class_4095;) */
    public static Brain<?> axolotlAi_makeBrain(Brain<Axolotl> brain) {
        addMissing(brain, AxolotlAi.getActivities());
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    /** 1.21.11 GoatAi.makeBrain (class_6054.method_35183(Lnet/minecraft/class_4095;)Lnet/minecraft/class_4095;) */
    public static Brain<?> goatAi_makeBrain(Brain<Goat> brain) {
        addMissing(brain, GoatAi.getActivities());
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    /** 1.21.11 FrogAi.makeBrain (class_7107.method_41368(Lnet/minecraft/class_4095;)Lnet/minecraft/class_4095;) */
    public static Brain<?> frogAi_makeBrain(Brain<Frog> brain) {
        addMissing(brain, FrogAi.getActivities());
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    /** 1.21.11 TadpoleAi.makeBrain (class_7111.method_41400(Lnet/minecraft/class_4095;)Lnet/minecraft/class_4095;) */
    public static Brain<?> tadpoleAi_makeBrain(Brain<Tadpole> brain) {
        addMissing(brain, TadpoleAi.getActivities());
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    /** 1.21.11 WardenAi.makeBrain (class_7261.method_42230(Lnet/minecraft/class_7260;Lcom/mojang/serialization/Dynamic;)Lnet/minecraft/class_4095;) */
    public static Brain<?> wardenAi_makeBrain(Warden mob, Dynamic<?> dynamic) {
        Brain<Warden> brain = Warden.BRAIN_PROVIDER.makeBrain(mob, CbWallBrain.toPacked(dynamic));
        addMissing(brain, WardenAi.getActivities(mob));
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    /** 1.21.11 AllayAi.makeBrain (class_7299.method_42660(Lnet/minecraft/class_4095;)Lnet/minecraft/class_4095;) */
    public static Brain<?> allayAi_makeBrain(Brain<Allay> brain) {
        addMissing(brain, AllayAi.getActivities());
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    /** 1.21.11 CamelAi.brainProvider() (class_7691.method_45363): Camel's BRAIN_PROVIDER */
    public static Brain.Provider<Camel> camelAi_brainProvider() {
        return Camel.BRAIN_PROVIDER;
    }

    /** 1.21.11 CamelAi.makeBrain (class_7691.method_45365(Lnet/minecraft/class_4095;)Lnet/minecraft/class_4095;) */
    public static Brain<?> camelAi_makeBrain(Brain<Camel> brain) {
        addMissing(brain, CamelAi.getActivities());
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    /** 1.21.11 SnifferAi.makeBrain (class_8155.method_49152(Lnet/minecraft/class_4095;)Lnet/minecraft/class_4095;) */
    public static Brain<?> snifferAi_makeBrain(Brain<Sniffer> brain) {
        addMissing(brain, SnifferAi.getActivities());
        brain.setCoreActivities(Set.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    /** 1.21.11 BreezeAi.makeBrain (class_8950.method_55016(Lnet/minecraft/class_8949;Lnet/minecraft/class_4095;)Lnet/minecraft/class_4095;) */
    public static Brain<?> breezeAi_makeBrain(Breeze mob, Brain<Breeze> brain) {
        addMissing(brain, BreezeAi.getActivities(mob));
        brain.setCoreActivities(Set.of(Activity.CORE));
        brain.setDefaultActivity(Activity.FIGHT);
        brain.useDefaultActivity();
        return brain;
    }

    /** 1.21.11 ArmadilloAi.brainProvider() (class_9071.method_55728): Armadillo's BRAIN_PROVIDER */
    public static Brain.Provider<Armadillo> armadilloAi_brainProvider() {
        return Armadillo.BRAIN_PROVIDER;
    }

    /** 1.21.11 ArmadilloAi.makeBrain (class_9071.method_55731(Lnet/minecraft/class_4095;)Lnet/minecraft/class_4095;) */
    public static Brain<?> armadilloAi_makeBrain(Brain<Armadillo> brain) {
        addMissing(brain, ArmadilloAi.getActivities());
        brain.setCoreActivities(Set.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }
}
