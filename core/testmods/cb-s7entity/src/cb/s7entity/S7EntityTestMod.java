package cb.s7entity;

import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1282;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1311;
import net.minecraft.class_1542;
import net.minecraft.class_1642;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2378;
import net.minecraft.class_2487;
import net.minecraft.class_2945;
import net.minecraft.class_2960;
import net.minecraft.class_3218;
import net.minecraft.class_4048;
import net.minecraft.class_52;
import net.minecraft.class_5321;
import net.minecraft.class_7699;
import net.minecraft.class_7923;
import net.minecraft.class_7924;
import net.minecraft.class_8103;
import net.minecraft.server.MinecraftServer;

/**
 * A 1.21.1-era server mod (intermediary names, Fabric Loader API only) for s7entity's bridges of the
 * 1.21.1 -> 1.21.2 step: EntityType.Builder.build(String) and the EntityType constructor (ids baked at
 * construction in 1.21.2, derived from the registry in 1.21.1), entity loot tables, and the hurt /
 * isInvulnerableTo family -- a mod's own calls, vanilla's calls through /damage (1.21.2: hurtServer), a
 * Zombie subclass (hooked hurtServer, super through the bridge), ItemEntity subclasses (hurtServer final in
 * 1.21.2), a plain Entity subclass with and without hurt. Every check prints "CB-ENTTEST ok|FAIL ..."; the
 * last line is "CB-ENTTEST DONE <passed>/<checks>".
 */
public class S7EntityTestMod implements ModInitializer {

    static final List<String> FAILS = new ArrayList<>();
    static int checks;

    static void check(boolean ok, String what) {
        checks++;
        System.out.println("CB-ENTTEST " + (ok ? "ok   " : "FAIL ") + what);
        if (!ok) {
            FAILS.add(what);
        }
    }

    static class_1299<PlainEntity> PLAIN;
    static class_1299<HurtEntity> HURT;
    static class_1299<PlainEntity> CTOR;

    @Override
    public void onInitialize() {
        // built with an id that is not the one it registers under: 1.21.1 named the type by its registry key
        PLAIN = class_1299.class_1300.<PlainEntity>method_5903(PlainEntity::new, class_1311.field_17715)
            .method_17687(0.5f, 0.5f).method_5905("cb_s7entity:built_name");
        class_2378.method_10230(class_7923.field_41177, class_2960.method_60655("cb_s7entity", "plain"), PLAIN);
        HURT = class_1299.class_1300.<HurtEntity>method_5903(HurtEntity::new, class_1311.field_17715)
            .method_17687(0.5f, 0.5f).method_5905("hurt");
        class_2378.method_10230(class_7923.field_41177, class_2960.method_60655("cb_s7entity", "hurt"), HURT);
        CTOR = new class_1299<>(PlainEntity::new, class_1311.field_17715, true, true, false, false, ImmutableSet.of(),
            class_4048.method_18384(0.5f, 0.5f), 1.0f, 8, 3, class_7699.method_45397());
        class_2378.method_10230(class_7923.field_41177, class_2960.method_60655("cb_s7entity", "ctor"), CTOR);

        Thread t = new Thread(() -> {
            for (int i = 0; i < 600; i++) {
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    return;
                }
                Object game = FabricLoader.getInstance().getGameInstance();
                if (game instanceof MinecraftServer server && server.method_3806() && server.method_30002() != null) {
                    server.execute(() -> start(server));
                    return;
                }
            }
        }, "cb-s7entity");
        t.setDaemon(true);
        t.start();
    }

    static class_5321<class_52> loot(String ns, String path) {
        return class_5321.method_29179(class_7924.field_50079, class_2960.method_60655(ns, path));
    }

    static void start(MinecraftServer server) {
        try {
            types();
            // no player is online: force the spawn chunk, so the entities added there are visible to the level
            // (ServerLevel.getEntity) once it has loaded, a few ticks later
            class_3218 level = server.method_30002();
            class_2338 spawn = level.method_43126();
            level.method_17988(spawn.method_10263() >> 4, spawn.method_10260() >> 4, true);
        } catch (Throwable e) {
            check(false, "threw " + e);
            e.printStackTrace(System.out);
        }
        new Thread(() -> {
            try {
                Thread.sleep(4000L);
            } catch (InterruptedException e) {
                return;
            }
            server.execute(() -> {
                try {
                    hurt(server);
                    boats(server);
                } catch (Throwable e) {
                    check(false, "threw " + e);
                    e.printStackTrace(System.out);
                }
                System.out.println("CB-ENTTEST DONE " + (checks - FAILS.size()) + "/" + checks);
            });
        }, "cb-s7entity-2").start();
    }

    static void types() {
        check("entity.cb_s7entity.plain".equals(PLAIN.method_5882()), "build(String): descriptionId from the registry key: " + PLAIN.method_5882());
        check("entity.cb_s7entity.hurt".equals(HURT.method_5882()), "build(\"hurt\"): descriptionId " + HURT.method_5882());
        check("entity.cb_s7entity.ctor".equals(CTOR.method_5882()), "new EntityType(..): descriptionId " + CTOR.method_5882());
        check(PLAIN.method_5897().getString().equals("entity.cb_s7entity.plain"), "getDescription() is translatable(descriptionId): " + PLAIN.method_5897().getString());
        check(loot("cb_s7entity", "entities/plain").equals(PLAIN.method_16351()), "getDefaultLootTable() of a built type: " + PLAIN.method_16351());
        check(loot("cb_s7entity", "entities/ctor").equals(CTOR.method_16351()), "getDefaultLootTable() of a constructed type: " + CTOR.method_16351());
        check(loot("minecraft", "entities/zombie").equals(class_1299.field_6051.method_16351()), "getDefaultLootTable() of ZOMBIE: " + class_1299.field_6051.method_16351());
        check(class_7923.field_41177.method_10221(PLAIN).toString().equals("cb_s7entity:plain"), "the type registered under its key");
    }

    /** vanilla's /damage on one entity, found by its UUID (ServerLevel.getEntity: any entity the level holds) */
    static void damage(MinecraftServer server, class_1297 target, String rest) {
        server.method_3734().method_44252(server.method_3739(), "damage " + target.method_5845() + " " + rest);
    }

    static void hurt(MinecraftServer server) {
        class_3218 level = server.method_30002();
        class_2338 spawn = level.method_43126();
        double x = spawn.method_10263() + 0.5, y = spawn.method_10264() + 2, z = spawn.method_10260() + 0.5;
        class_1282 generic = level.method_48963().method_48830();
        class_1282 fall = level.method_48963().method_48827();

        // a Zombie subclass: hurt hooked to hurtServer, super through the bridge with vanilla's level
        ModZombie zombie = new ModZombie(class_1299.field_6051, level);
        zombie.method_5814(x, y, z);
        zombie.method_5780("cbz");
        check(level.method_8649(zombie), "a ModZombie can be added");
        check(level.method_14190(zombie.method_5667()) == zombie, "the level holds it (getEntity by UUID)");
        float h0 = zombie.method_6032();
        damage(server, zombie, "3");
        check(zombie.calls == 1 && zombie.lastAmount == 3.0f, "/damage 3 reached the mod's hurt (calls=" + zombie.calls + ", amount=" + zombie.lastAmount + ")");
        check(zombie.superAnswer && Math.abs(zombie.method_6032() - (h0 - 3.0f)) < 0.01f, "its super.hurt took the damage: health " + h0 + " -> " + zombie.method_6032());
        boolean direct = zombie.method_5643(generic, 2.0f);
        check(zombie.calls == 2 && !direct, "the mod's own hurt(src, 2) call reached its override; invulnerable time -> false (" + direct + ")");
        check(zombie.method_5679(fall), "isInvulnerableTo(fall) answers the mod's override");
        check(!zombie.method_5679(generic), "isInvulnerableTo(generic) answers super (vanilla: false)");
        float h1 = zombie.method_6032();
        zombie.field_6008 = 0;
        damage(server, zombie, "4 minecraft:fall");
        check(zombie.method_6032() == h1, "/damage 4 fall: vanilla asked the mod's isInvulnerableTo, no damage (" + h1 + " -> " + zombie.method_6032() + ")");
        check(loot("cb_s7entity", "entities/custom_zombie").equals(zombie.method_5989()), "Mob.getLootTable() answers the mod's getDefaultLootTable: " + zombie.method_5989());

        // ItemEntity subclasses: hurtServer is final in 1.21.2
        ModItem item = new ModItem(level, x + 2, y, z, new class_1799(class_1802.field_8477));
        item.method_5780("cbi");
        check(level.method_8649(item), "a ModItem can be added");
        damage(server, item, "10");
        check(item.calls == 1, "/damage 10 on an ItemEntity subclass reached the mod's hurt (calls=" + item.calls + ")");
        check(item.superAnswer && item.method_31481(), "its super.hurt destroyed the item (super=" + item.superAnswer + ", removed=" + item.method_31481() + ")");
        ShieldItem shielded = new ShieldItem(level, x - 2, y, z, new class_1799(class_1802.field_8477));
        shielded.method_5780("cbs");
        level.method_8649(shielded);
        damage(server, shielded, "10");
        check(!shielded.method_31481(), "an ItemEntity subclass invulnerable by its own isInvulnerableTo survives /damage 10 (vanilla asked it)");

        // plain Entity subclasses
        PlainEntity plain = new PlainEntity(PLAIN, level);
        check(!plain.method_5643(generic, 1.0f), "an Entity subclass without hurt: 1.21.1 Entity.hurt answers false");
        HurtEntity he = new HurtEntity(HURT, level);
        he.method_5814(x, y, z + 2);
        he.method_5780("cbh");
        level.method_8649(he);
        damage(server, he, "5");
        check(he.calls == 1 && he.lastAmount == 5.0f && !he.superAnswer, "/damage 5 on an Entity subclass reached its hurt; super (Entity's) false (calls=" + he.calls + ")");
        check(he.method_5643(generic, 1.0f) && he.calls == 2, "its own hurt call answers its override (true)");
        check(!he.method_5679(generic), "isInvulnerableTo(generic) on an Entity subclass: false");
        he.method_5684(true);
        check(he.method_5679(generic), "isInvulnerableTo(generic) of an invulnerable Entity subclass: true");
        check(!he.method_5679(level.method_48963().method_48829()), "... but not to out-of-world (bypasses invulnerability)");
        zombie.method_31472();
        he.method_31472();
        shielded.method_31472();
    }

    static String typeId(class_1297 e) {
        return class_7923.field_41177.method_10221(e.method_5864()).toString();
    }

    static void boats(MinecraftServer server) {
        class_3218 level = server.method_30002();
        class_2338 spawn = level.method_43126();
        double x = spawn.method_10263() + 0.5, y = spawn.method_10264() + 2, z = spawn.method_10260() + 4.5;

        net.minecraft.class_1690 boat = new net.minecraft.class_1690(level, x, y, z);
        check(boat.method_47885() == net.minecraft.class_1690.class_1692.field_7727, "new Boat(level, x, y, z) is an OAK boat: " + boat.method_47885());
        check(boat.method_7557() == class_1802.field_8533, "its getDropItem: oak boat");
        boat.method_47884(net.minecraft.class_1690.class_1692.field_7729);
        check(boat.method_47885() == net.minecraft.class_1690.class_1692.field_7729, "setVariant(BIRCH): getVariant answers BIRCH");
        check(typeId(boat).equals("minecraft:birch_boat"), "... and the boat is a birch boat to the game: " + typeId(boat));
        check(boat.method_7557() == class_1802.field_8442 && boat.method_31480().method_7909() == class_1802.field_8442,
            "... dropping and picking a birch boat (" + boat.method_7557() + ", " + boat.method_31480() + ")");
        check(level.method_8649(boat), "the birch boat can be added");
        boat.method_47884(net.minecraft.class_1690.class_1692.field_7728);
        check(typeId(boat).equals("minecraft:spruce_boat") && level.method_14190(boat.method_5667()) == boat,
            "setVariant(SPRUCE) on a boat in the level: spruce, still held by the level (" + typeId(boat) + ")");

        net.minecraft.class_7264 chest = new net.minecraft.class_7264(level, x + 2, y, z);
        net.minecraft.class_1690 asBoat = chest;
        class_1297 asEntity = chest;
        check(asEntity instanceof net.minecraft.class_1690, "a ChestBoat is a Boat (1.21.1's hierarchy)");
        asBoat.method_47884(net.minecraft.class_1690.class_1692.field_40161);
        check(typeId(chest).equals("minecraft:bamboo_chest_raft") && asBoat.method_7557() == class_1802.field_40225,
            "ChestBoat.setVariant(BAMBOO): a bamboo chest raft dropping one (" + typeId(chest) + ", " + asBoat.method_7557() + ")");

        ModBoat mod = new ModBoat(class_1299.field_6121, level);
        check(mod.method_7557() == class_1802.field_8477, "a mod Boat's own getDropItem answers: " + mod.method_7557());
        check(mod.method_31480().method_7909() == class_1802.field_8477, "... and the pick result follows it: " + mod.method_31480());
        check(!mod.method_7556(0) && mod.paddleCalls == 1, "its getPaddleState override (super: false) answers: calls=" + mod.paddleCalls);
        check(mod.method_47885() == net.minecraft.class_1690.class_1692.field_7727, "a mod Boat of EntityType.BOAT: OAK");

        check(net.minecraft.class_1690.class_1692.method_7561("birch") == net.minecraft.class_1690.class_1692.field_7729
            && net.minecraft.class_1690.class_1692.field_7729.method_7560() == net.minecraft.class_2246.field_10148,
            "Boat.Type.byName(\"birch\") is BIRCH, its planks birch planks");
        check(net.minecraft.class_1690.class_1692.method_7558(99) == net.minecraft.class_1690.class_1692.field_7727, "Boat.Type.byId(99): OAK (ZERO strategy)");
        check(net.minecraft.class_1690.method_30959(boat, chest), "Boat.canVehicleCollide(boat, chestBoat)");
        class_1297 made = class_1299.field_6121.method_5883(level);
        check(made instanceof net.minecraft.class_1690 b && b.method_47885() == net.minecraft.class_1690.class_1692.field_7727,
            "EntityType.BOAT.create(level): an oak boat (" + (made == null ? null : typeId(made)) + ")");
        boat.method_31472();
    }

    public static class ModBoat extends net.minecraft.class_1690 {
        int paddleCalls;

        public ModBoat(class_1299<? extends net.minecraft.class_1690> type, class_1937 level) {
            super(type, level);
        }

        @Override
        public net.minecraft.class_1792 method_7557() {
            return class_1802.field_8477;
        }

        @Override
        public boolean method_7556(int side) {
            paddleCalls++;
            return super.method_7556(side);
        }
    }

    public static class PlainEntity extends class_1297 {
        public PlainEntity(class_1299<?> type, class_1937 level) {
            super(type, level);
        }

        @Override
        protected void method_5693(class_2945.class_9222 builder) {
        }

        @Override
        protected void method_5749(class_2487 tag) {
        }

        @Override
        protected void method_5652(class_2487 tag) {
        }
    }

    public static class HurtEntity extends PlainEntity {
        int calls;
        float lastAmount;
        boolean superAnswer = true;

        public HurtEntity(class_1299<?> type, class_1937 level) {
            super(type, level);
        }

        @Override
        public boolean method_5643(class_1282 source, float amount) {
            calls++;
            lastAmount = amount;
            superAnswer = super.method_5643(source, amount);
            return true;
        }
    }

    public static class ModZombie extends class_1642 {
        int calls;
        float lastAmount;
        boolean superAnswer;

        public ModZombie(class_1299<? extends class_1642> type, class_1937 level) {
            super(type, level);
        }

        @Override
        public boolean method_5643(class_1282 source, float amount) {
            calls++;
            lastAmount = amount;
            superAnswer = super.method_5643(source, amount);
            return superAnswer;
        }

        @Override
        public boolean method_5679(class_1282 source) {
            return source.method_48789(class_8103.field_42250) || super.method_5679(source);
        }

        @Override
        protected class_5321<class_52> method_5991() {
            return loot("cb_s7entity", "entities/custom_zombie");
        }
    }

    public static class ModItem extends class_1542 {
        int calls;
        boolean superAnswer;

        public ModItem(class_1937 level, double x, double y, double z, class_1799 stack) {
            super(level, x, y, z, stack);
        }

        @Override
        public boolean method_5643(class_1282 source, float amount) {
            calls++;
            superAnswer = super.method_5643(source, amount);
            return superAnswer;
        }
    }

    public static class ShieldItem extends class_1542 {
        public ShieldItem(class_1937 level, double x, double y, double z, class_1799 stack) {
            super(level, x, y, z, stack);
        }

        @Override
        public boolean method_5679(class_1282 source) {
            return true;
        }
    }
}
