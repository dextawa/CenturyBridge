package cb.swalln;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.AttackRange;

/**
 * A native 26.1 mod (never converted) whose mixins sit on the 26.1 methods the 26.1 step layer's canAttackType /
 * entityAttackRange routes read after: Mob.canAttack (a @Redirect of its target.is(GHAST) check), TargetingConditions.test
 * (a HEAD injector) and LivingEntity.getAttackRangeWith (a RETURN injector). With the layer on, each must still act
 * ("native.*"); with cb-swall (1.21.11, converted) beside it, that mod's mixins on the restored canAttackType /
 * entityAttackRange must change vanilla's answers ("mixed.*"); untagged mobs answer as 26.1 does ("unmixed.*").
 * Prints "CBTEST PASS|FAIL swalln.<name>" and "CBNATIVE DONE".
 */
public class Driver implements ModInitializer {
    public static int targetingTests;
    public static final AttackRange NATIVE_RANGE = new AttackRange(5.0F, 6.0F, 7.0F, 8.0F, 0.5F, 1.0F);
    public static final AttackRange MIXED_RANGE = new AttackRange(1.0F, 2.0F, 3.0F, 4.0F, 0.25F, 1.0F);

    private static final List<String> FAILS = new ArrayList<>();

    private static void check(String name, boolean ok, Object detail) {
        if (ok) {
            System.out.println("CBTEST PASS swalln." + name);
        } else {
            FAILS.add(name);
            System.out.println("CBTEST FAIL swalln." + name + " : " + detail);
        }
    }

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(Driver::onStarted);
    }

    private static <T extends LivingEntity> T make(ServerLevel level, EntityType<T> type, String tag) {
        T e = type.create(level, EntitySpawnReason.COMMAND);
        if (tag != null) {
            e.addTag(tag);
        }
        return e;
    }

    private static AttackRange w(LivingEntity e, ItemStack stack) {
        AttackRange r = stack.get(DataComponents.ATTACK_RANGE);
        return r != null ? r : AttackRange.defaultFor(e);
    }

    private static void onStarted(MinecraftServer server) {
        try {
            run(server);
        } catch (Throwable t) {
            check("driver", false, t);
        }
        System.out.println("CBNATIVE DONE fails=" + FAILS);
    }

    private static void run(MinecraftServer server) {
        ServerLevel level = server.overworld();
        LivingEntity villager = make(level, EntityType.VILLAGER, null);
        LivingEntity ghast = make(level, EntityType.GHAST, null);
        Mob zombie = make(level, EntityType.ZOMBIE, null);
        Mob zombieN = make(level, EntityType.ZOMBIE, "cbswalln");
        // entities built, not spawned: line of sight through the ground at the origin is not what is tested
        TargetingConditions combat = TargetingConditions.forCombat().ignoreLineOfSight();

        // 26.1's own answers, untagged
        check("unmixed.mobCanAttack", zombie.canAttack(villager) && !zombie.canAttack(ghast),
            zombie.canAttack(villager) + " " + zombie.canAttack(ghast));
        int before = targetingTests;
        boolean plain = combat.test(level, zombie, villager);
        check("unmixed.targeting", plain, plain);
        check("native.targetingHeadFires", targetingTests > before, targetingTests + " " + before);

        // the native @Redirect in Mob.canAttack acts with the layer's route on
        check("native.mobCanAttackRedirect", !zombieN.canAttack(villager) && !combat.test(level, zombieN, villager),
            zombieN.canAttack(villager));

        // the native RETURN injector on getAttackRangeWith acts for the active item
        zombie.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
        zombieN.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
        ItemStack held = zombie.getMainHandItem();
        AttackRange plainRange = zombie.getAttackRangeWith(held);
        check("unmixed.attackRange", held == zombie.getActiveItem() && plainRange.equals(w(zombie, held)), plainRange);
        AttackRange nRange = zombieN.getAttackRangeWith(zombieN.getMainHandItem());
        check("native.attackRangeReturn", zombieN.getMainHandItem() == zombieN.getActiveItem() && NATIVE_RANGE.equals(nRange), nRange);

        if (!FabricLoader.getInstance().isModLoaded("cb_swall")) {
            return;
        }
        // cb-swall's 1.21.11 mixins on the restored members change vanilla's answers
        Mob zombieV = make(level, EntityType.ZOMBIE, "cbswallv");
        check("mixed.mobRefusesVillager", !zombieV.canAttack(villager) && !combat.test(level, zombieV, villager),
            zombieV.canAttack(villager));
        check("mixed.mobAcceptsGhast", zombieV.canAttack(ghast), zombieV.canAttack(ghast));
        IronGolem golem = make(level, EntityType.IRON_GOLEM, null);
        IronGolem golemV = make(level, EntityType.IRON_GOLEM, "cbswallv");
        check("mixed.golemRefusesVillager", golem.canAttack(villager) && !golemV.canAttack(villager),
            golem.canAttack(villager) + " " + golemV.canAttack(villager));
        zombieV.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
        AttackRange vRange = zombieV.getAttackRangeWith(zombieV.getMainHandItem());
        check("mixed.attackRange", MIXED_RANGE.equals(vRange), vRange);
    }
}
