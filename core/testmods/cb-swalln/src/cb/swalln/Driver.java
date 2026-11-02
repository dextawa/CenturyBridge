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
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;

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
    /** calls of 26.1's runValidation(source, key, value) on vanilla's three types (mixin/LootRunValidationMixin) */
    /** trade sets 26.1's AbstractVillager.addOffersFromTradeSet was called with (mixin/TradeSetOffersMixin) */
    public static final List<String> TRADE_SETS = java.util.Collections.synchronizedList(new ArrayList<>());
    public static final java.util.concurrent.atomic.AtomicInteger VANILLA_VALIDATIONS = new java.util.concurrent.atomic.AtomicInteger();
    public static final AttackRange NATIVE_RANGE = new AttackRange(5.0F, 6.0F, 7.0F, 8.0F, 0.5F, 1.0F);
    public static final AttackRange MIXED_RANGE = new AttackRange(1.0F, 2.0F, 3.0F, 4.0F, 0.25F, 1.0F);

    private static final List<String> FAILS = new ArrayList<>();
    /** the entity Brain.Provider.makeBrain is building for (mixin/BrainProviderNativeMixin) */
    public static final ThreadLocal<LivingEntity> BUILDING = new ThreadLocal<>();

    private static boolean registered(LivingEntity e, MemoryModuleType<?> memory) {
        return e.getBrain().checkMemory(memory, MemoryStatus.REGISTERED);
    }

    /**
     * Piglins with the 26.1 layer's brain-list route (CbWallBrainExtrasMixin) installed: the native @ModifyArg on the
     * same Brain.<init> call still acts, a piglin barters (admires a gold ingot handed to it) and angers (hurt by a
     * player) as 26.1's does; with cb-swall beside it, the memory that mod appended to 1.21.11's Piglin.MEMORY_TYPES
     * is registered as well, beside the native one.
     */
    private static void piglins(ServerLevel level) {
        Piglin piglin = make(level, EntityType.PIGLIN, null);
        Hoglin hoglin = make(level, EntityType.HOGLIN, null);
        check("native.brainProviderArg", registered(piglin, MemoryModuleType.LIKED_NOTEBLOCK_POSITION)
            && registered(piglin, MemoryModuleType.ADMIRING_ITEM) && !registered(hoglin, MemoryModuleType.LIKED_NOTEBLOCK_POSITION), "");
        if (FabricLoader.getInstance().isModLoaded("cb_swall")) {
            check("mixed.piglinUnion", registered(piglin, MemoryModuleType.LIKED_PLAYER)
                && registered(piglin, MemoryModuleType.LIKED_NOTEBLOCK_POSITION), "");
        }

        Player player = FakePlayer.get(level);
        piglin.setPos(0.5, 100, 0.5);
        player.setPos(1.5, 100, 0.5);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GOLD_INGOT, 2));
        InteractionResult barter = piglin.mobInteract(player, InteractionHand.MAIN_HAND);
        check("vanilla.piglinBarter", barter.consumesAction() && piglin.getOffhandItem().is(Items.GOLD_INGOT)
            && piglin.getBrain().hasMemoryValue(MemoryModuleType.ADMIRING_ITEM) && player.getMainHandItem().getCount() == 1,
            barter + " " + piglin.getOffhandItem() + " " + player.getMainHandItem());

        Piglin angry = make(level, EntityType.PIGLIN, null);
        angry.setPos(0.5, 100, 0.5);
        boolean hurt = angry.hurtServer(level, level.damageSources().playerAttack(player), 1.0F);
        check("vanilla.piglinAnger", hurt && angry.getBrain().getMemory(MemoryModuleType.ANGRY_AT).equals(java.util.Optional.of(player.getUUID())),
            hurt + " " + angry.getBrain().getMemory(MemoryModuleType.ANGRY_AT));
    }

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

    /** native 26.1 control of cb-swall's modResourcePack case: the same pack, written against 26.1 */
    static final class NativeModPack implements net.fabricmc.fabric.api.resource.ModResourcePack {
        @Override
        public net.fabricmc.loader.api.metadata.ModMetadata getFabricModMetadata() {
            return net.fabricmc.loader.api.FabricLoader.getInstance().getModContainer("cb_swalln").orElseThrow().getMetadata();
        }

        @Override
        public net.fabricmc.fabric.api.resource.ModResourcePack createOverlay(String overlay) {
            return this;
        }

        @Override
        public net.minecraft.server.packs.resources.IoSupplier<java.io.InputStream> getRootResource(String... path) {
            return null;
        }

        @Override
        public net.minecraft.server.packs.resources.IoSupplier<java.io.InputStream> getResource(net.minecraft.server.packs.PackType type, net.minecraft.resources.Identifier id) {
            return null;
        }

        @Override
        public void listResources(net.minecraft.server.packs.PackType type, String ns, String prefix, net.minecraft.server.packs.PackResources.ResourceOutput out) {
        }

        @Override
        public java.util.Set<String> getNamespaces(net.minecraft.server.packs.PackType type) {
            return java.util.Set.of("cb_swalln_pack");
        }

        @Override
        public <T> T getMetadataSection(net.minecraft.server.packs.metadata.MetadataSectionType<T> section) {
            return null;
        }

        @Override
        public net.minecraft.server.packs.PackLocationInfo location() {
            return null;
        }

        @Override
        public void close() {
        }
    }

    private static void onStarted(MinecraftServer server) {
        try {
            run(server);
        } catch (Throwable t) {
            check("driver", false, t);
        }
        try {
            net.minecraft.server.packs.PackResources p = new NativeModPack();
            java.util.Set<String> ns = p.getNamespaces(net.minecraft.server.packs.PackType.SERVER_DATA);
            check("modResourcePack.namespaces", ns.contains("cb_swalln_pack"), ns);
        } catch (Throwable t) {
            check("modResourcePack", false, t);
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

        piglins(level);
        loot(server);
        trades(level);

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

    /**
     * 26.1 loot validation with the layer's CbWallLootDataTypeHooks on runValidation: vanilla's three types still validate
     * through it and the native HEAD injector counts them; the problems vanilla's types report over the loaded loot are
     * printed ("CBNATIVE lootProblems=") to compare layer on / off. With cb-swall, its 1.21.11 type's own validator runs
     * from 26.1's runValidation(source, lookup) and reports through the source.
     */
    private static void loot(MinecraftServer server) {
        try {
            net.minecraft.core.HolderLookup.Provider lookup = server.reloadableRegistries().lookup();
            net.minecraft.util.ProblemReporter.Collector collector = new net.minecraft.util.ProblemReporter.Collector();
            net.minecraft.world.level.storage.loot.ValidationContextSource source =
                new net.minecraft.world.level.storage.loot.ValidationContextSource(collector, lookup);
            int before = VANILLA_VALIDATIONS.get();
            net.minecraft.world.level.storage.loot.LootDataType.values().forEach(t -> validateAll(t, source, lookup));
            int[] problems = {0};
            collector.forEach((path, problem) -> problems[0]++);
            System.out.println("CBNATIVE lootProblems=" + problems[0] + " validations=" + (VANILLA_VALIDATIONS.get() - before));
            check("native.lootRunValidationHead", VANILLA_VALIDATIONS.get() - before > 100, VANILLA_VALIDATIONS.get() - before);
        } catch (Throwable t) {
            check("native.lootRunValidationHead", false, t);
        }
        if (!FabricLoader.getInstance().isModLoaded("cb_swall")) {
            return;
        }
        try {
            Class<?> mod = Class.forName("cb.swall.SwallTestMod");
            @SuppressWarnings("unchecked")
            net.minecraft.world.level.storage.loot.LootDataType<net.minecraft.world.level.storage.loot.predicates.LootItemCondition> type =
                (net.minecraft.world.level.storage.loot.LootDataType<net.minecraft.world.level.storage.loot.predicates.LootItemCondition>) mod.getField("CUSTOM_LOOT_TYPE").get(null);
            java.util.concurrent.atomic.AtomicInteger calls = (java.util.concurrent.atomic.AtomicInteger) mod.getField("LOOT_VALIDATOR_CALLS").get(null);
            net.minecraft.core.MappedRegistry<net.minecraft.world.level.storage.loot.predicates.LootItemCondition> reg =
                new net.minecraft.core.MappedRegistry<>(type.registryKey(), com.mojang.serialization.Lifecycle.stable());
            // two distinct values (killedByPlayer() is a singleton; a registry refuses one value under two keys)
            reg.register(net.minecraft.resources.ResourceKey.create(type.registryKey(), net.minecraft.resources.Identifier.fromNamespaceAndPath("cb_swalln", "n1")),
                net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition.randomChance(0.25F).build(), net.minecraft.core.RegistrationInfo.BUILT_IN);
            reg.register(net.minecraft.resources.ResourceKey.create(type.registryKey(), net.minecraft.resources.Identifier.fromNamespaceAndPath("cb_swalln", "n2")),
                net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition.randomChance(0.5F).build(), net.minecraft.core.RegistrationInfo.BUILT_IN);
            reg.freeze();
            net.minecraft.util.ProblemReporter.Collector collector = new net.minecraft.util.ProblemReporter.Collector();
            net.minecraft.world.level.storage.loot.ValidationContextSource source =
                new net.minecraft.world.level.storage.loot.ValidationContextSource(collector, server.reloadableRegistries().lookup());
            int before = calls.get();
            int vanillaBefore = VANILLA_VALIDATIONS.get();
            type.runValidation(source, reg);
            java.util.List<String> seen = new java.util.ArrayList<>();
            collector.forEach((path, problem) -> seen.add(problem.description()));
            check("mixed.lootCustomValidator", calls.get() - before == 2 && seen.contains("cb_swall checked n1") && seen.contains("cb_swall checked n2")
                && VANILLA_VALIDATIONS.get() == vanillaBefore, (calls.get() - before) + " " + seen);
        } catch (Throwable t) {
            check("mixed.lootCustomValidator", false, t);
        }
    }

    /**
     * 26.1's own updateTrades for a profession no 1.21.11 mod edits (toolsmith: cb-swall edits only the farmer): a
     * native injector on addOffersFromTradeSet fires, with the layer on and off. The wandering trader's pools: printed
     * (cb-swall adds a pool, after which the layer draws 1.21.11's way and this injector does not run)
     */
    private static void trades(ServerLevel level) {
        net.minecraft.world.entity.npc.villager.Villager v = make(level, EntityType.VILLAGER, null);
        int before = TRADE_SETS.size();
        v.setVillagerData(v.getVillagerData().withProfession(level.registryAccess(),
            net.minecraft.world.entity.npc.villager.VillagerProfession.TOOLSMITH).withLevel(1));
        int offers = v.getOffers().size();
        List<String> seen = new ArrayList<>(TRADE_SETS.subList(before, TRADE_SETS.size()));
        check("native.tradeSetOffersFires", seen.size() == 1 && seen.get(0).contains("toolsmith") && offers > 0, seen + " offers=" + offers);
        int w = TRADE_SETS.size();
        net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader t = make(level, EntityType.WANDERING_TRADER, null);
        int wares = t.getOffers().size();
        System.out.println("CBNATIVE wanderingTradeSets=" + TRADE_SETS.subList(w, TRADE_SETS.size()) + " wares=" + wares);
    }

    private static <T extends net.minecraft.world.level.storage.loot.Validatable> void validateAll(net.minecraft.world.level.storage.loot.LootDataType<T> type,
            net.minecraft.world.level.storage.loot.ValidationContextSource source, net.minecraft.core.HolderLookup.Provider lookup) {
        type.runValidation(source, lookup.lookupOrThrow(type.registryKey()));
    }
}
