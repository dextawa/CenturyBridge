package cb.s6b;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1282;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1642;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2487;
import net.minecraft.class_2561;
import net.minecraft.class_2595;
import net.minecraft.class_2945;
import net.minecraft.class_3218;
import net.minecraft.class_5455;
import net.minecraft.class_8942;
import net.minecraft.server.MinecraftServer;

/**
 * A 1.21.5-era server mod (intermediary names, Fabric Loader API only) for s6b's bridges of the 1.21.5 -> 1.21.6
 * step: the CompoundTag save data on 1.21.6's ValueInput / ValueOutput (a Zombie subclass and a plain Entity
 * subclass with 1.21.5 read/addAdditionalSaveData overrides, saved and loaded through 1.21.5 calls that reach
 * vanilla's 1.21.6 path; a chest's saveWithId / loadWithComponents), Component.Serializer, ProblemReporter strings.
 * Every check prints "CB-S6B ok|FAIL ..."; the last line is "CB-S6B DONE <passed>/<checks>".
 */
public class S6bTestMod implements ModInitializer {

    static final List<String> FAILS = new ArrayList<>();
    static int checks;
    static int saves;
    static int reads;
    static Boolean v1Dedicated;

    static void check(boolean ok, String what) {
        checks++;
        System.out.println("CB-S6B " + (ok ? "ok   " : "FAIL ") + what);
        if (!ok) {
            FAILS.add(what);
        }
    }

    /** a 1.21.5 Zombie subclass with its own save data, calling super */
    static class CbZombie extends class_1642 {
        int marker = -1;

        CbZombie(class_1299<? extends class_1642> type, class_1937 level) {
            super(type, level);
        }

        @Override
        public void method_5652(class_2487 tag) {
            super.method_5652(tag);
            tag.method_10569("cb_s6b", marker);
            // what vanilla saved before is in the same tag
            if (tag.method_10545("Health")) {
                saves++;
            }
        }

        @Override
        public void method_5749(class_2487 tag) {
            super.method_5749(tag);
            marker = tag.method_68083("cb_s6b", -2);
            reads++;
        }
    }

    /** a plain 1.21.5 Entity implementing the abstract save methods itself */
    static class PlainEntity extends class_1297 {
        String note = "";

        PlainEntity(class_1299<?> type, class_1937 level) {
            super(type, level);
        }

        @Override
        protected void method_5693(class_2945.class_9222 builder) {
        }

        @Override
        public boolean method_64397(class_3218 level, class_1282 source, float amount) {
            return false;
        }

        @Override
        protected void method_5749(class_2487 tag) {
            note = tag.method_68564("cb_note", "missing");
        }

        @Override
        protected void method_5652(class_2487 tag) {
            tag.method_10582("cb_note", note);
        }
    }

    /** a 1.21.5 block entity overriding fabric-rendering-data-attachment-v1's getRenderAttachmentData */
    static class DataChest extends class_2595 {
        DataChest() {
            super(class_2338.field_10980, class_2246.field_10034.method_9564());
        }

        public Object getRenderAttachmentData() {
            return "cb-data";
        }
    }

    @Override
    public void onInitialize() {
        // removed Fabric API modules: fabric-commands-v0 and fabric-command-api-v1 (this mod depends on both)
        net.fabricmc.fabric.api.registry.CommandRegistry.INSTANCE.register(false,
            d -> d.register(net.minecraft.class_2170.method_9247("cbv0")));
        net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback.EVENT.register((d, dedicated) -> {
            v1Dedicated = dedicated;
            d.register(net.minecraft.class_2170.method_9247("cbv1"));
        });
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
        }, "cb-s6b");
        t.setDaemon(true);
        t.start();
    }

    static void start(MinecraftServer server) {
        class_3218 level = server.method_30002();
        try {
            entities(level);
        } catch (Throwable e) {
            e.printStackTrace();
            check(false, "entities threw " + e);
        }
        try {
            chest(level);
        } catch (Throwable e) {
            e.printStackTrace();
            check(false, "chest threw " + e);
        }
        try {
            text(level.method_30349());
        } catch (Throwable e) {
            e.printStackTrace();
            check(false, "text threw " + e);
        }
        try {
            fapi(server, level);
        } catch (Throwable e) {
            e.printStackTrace();
            check(false, "fapi threw " + e);
        }
        try {
            tail(level);
        } catch (Throwable e) {
            e.printStackTrace();
            check(false, "tail threw " + e);
        }
        try {
            problems();
        } catch (Throwable e) {
            e.printStackTrace();
            check(false, "problems threw " + e);
        }
        System.out.println("CB-S6B DONE " + (checks - FAILS.size()) + "/" + checks + (FAILS.isEmpty() ? "" : " FAILS " + FAILS));
    }

    static void entities(class_3218 level) {
        CbZombie z = new CbZombie(class_1299.field_6051, level);
        z.marker = 42;
        z.method_6033(7.0F);
        class_2487 tag = z.method_5647(new class_2487());
        check(tag.method_68083("cb_s6b", -1) == 42, "Zombie subclass: saveWithoutId carries the mod's addAdditionalSaveData");
        check(tag.method_10545("Health"), "Zombie subclass: vanilla's save data in the same tag");
        check(saves == 1, "Zombie subclass: the mod's override saw vanilla's data after its super call (" + saves + ")");
        CbZombie z2 = new CbZombie(class_1299.field_6051, level);
        z2.method_5651(tag);
        check(z2.marker == 42, "Zombie subclass: load gives the mod's readAdditionalSaveData its value (" + z2.marker + ")");
        check(Math.abs(z2.method_6032() - 7.0F) < 1e-4, "Zombie subclass: vanilla's read through the mod's super call (health " + z2.method_6032() + ")");
        check(reads == 1, "Zombie subclass: read once (" + reads + ")");

        class_2487 direct = new class_2487();
        z.method_5652(direct);
        check(direct.method_68083("cb_s6b", -1) == 42 && direct.method_10545("Health"),
            "Zombie subclass: a direct 1.21.5 addAdditionalSaveData call reaches the mod's override and vanilla's");

        PlainEntity p = new PlainEntity(class_1299.field_6051, level);
        p.note = "hello";
        class_2487 pt = p.method_5647(new class_2487());
        check("hello".equals(pt.method_68564("cb_note", "")), "plain Entity: saved through vanilla's saveWithoutId");
        check(pt.method_10545("Pos"), "plain Entity: vanilla's Entity data in the same tag");
        PlainEntity p2 = new PlainEntity(class_1299.field_6051, level);
        p2.method_5651(pt);
        check("hello".equals(p2.note), "plain Entity: loaded through vanilla's load (" + p2.note + ")");
    }

    static void chest(class_3218 level) {
        class_2595 chest = new class_2595(class_2338.field_10980, class_2246.field_10034.method_9564());
        class_2487 tag = new class_2487();
        tag.method_10582("CustomName", "Box");
        chest.method_58690(tag, level.method_30349());
        class_2561 name = chest.method_5797();
        check(name != null && "Box".equals(name.getString()), "chest: loadWithComponents reads the custom name (" + name + ")");
        class_2487 saved = chest.method_38243(level.method_30349());
        check("minecraft:chest".equals(saved.method_68564("id", "")), "chest: saveWithId writes the id");
        check(saved.method_10545("CustomName"), "chest: saveWithId writes the custom name");
    }

    static void text(class_5455 registries) {
        class_2561 hi = class_2561.method_43470("hi");
        String json = class_2561.class_2562.method_10867(hi, registries);
        check("\"hi\"".equals(json), "Component.Serializer.toJson (" + json + ")");
        class_2561 back = class_2561.class_2562.method_10877("{\"text\":\"yo\"}", registries);
        check(back != null && "yo".equals(back.getString()), "Component.Serializer.fromJson");
        // 1.21.5: JsonParser gives a JsonNull (not null) for "null", which the codec refuses with a JsonParseException
        boolean threw = false;
        try {
            class_2561.class_2562.method_10877("null", registries);
        } catch (com.google.gson.JsonParseException e) {
            threw = true;
        }
        check(threw, "Component.Serializer.fromJson(\"null\") throws JsonParseException as 1.21.5 did");
    }

    static void fapi(MinecraftServer server, class_3218 level) {
        com.mojang.brigadier.tree.RootCommandNode<?> root = server.method_3734().method_9235().getRoot();
        check(root.getChild("cbv0") != null, "fabric-commands-v0: CommandRegistry.register reaches the dispatcher");
        check(root.getChild("cbv1") != null, "fabric-command-api-v1: CommandRegistrationCallback reaches the dispatcher");
        check(Boolean.TRUE.equals(v1Dedicated), "fabric-command-api-v1: dedicated is true on a dedicated server (" + v1Dedicated + ")");
        Object none = ((net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView) level)
            .getBlockEntityRenderAttachment(new class_2338(0, -60, 0));
        check(none == null, "rendering-data-attachment-v1: a level is a RenderAttachedBlockView; no block entity -> null");
        DataChest chest = new DataChest();
        check("cb-data".equals(((net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity) chest).getRenderAttachmentData()),
            "rendering-data-attachment-v1: the mod's getRenderAttachmentData through the interface");
        check(((net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity) new class_2595(class_2338.field_10980,
            class_2246.field_10034.method_9564())).getRenderAttachmentData() == null,
            "rendering-data-attachment-v1: a vanilla block entity answers null");
        // ClientTags is @Environment(CLIENT) in both releases: the server only proves the dependency resolves
        // (1.21.6's fabric-tag-api-v1 provides fabric-client-tags-api-v1 itself); the call is a client check
        // (client_checks/s6b.md). fabric-keybindings-v0 is client-only as well: this mod loading proves its
        // dependency (">=0.2.0") is met by the layer.
        check(net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("fabric-client-tags-api-v1"),
            "fabric-client-tags-api-v1: dependency resolves natively (fabric-tag-api-v1 provides it)");
        check(net.fabricmc.loader.api.FabricLoader.getInstance().getModContainer("cb_s6b").get().getMetadata()
            .getDependencies().stream().anyMatch(d -> d.getModId().equals("centurybridge_step_1_21_6")),
            "fabric-keybindings-v0 / rendering-data-attachment-v1 / commands: dependencies renamed onto the layer");
    }

    /** a 1.21.5 FlyingMob subclass */
    static class CbFlyer extends net.minecraft.class_1307 {
        CbFlyer(class_1299<? extends net.minecraft.class_1307> type, class_1937 level) {
            super(type, level);
        }
    }

    static void tail(class_3218 level) {
        // FlyingMob: 1.21.5's travel in the air is moveRelative(0.02) then x0.91; a vanilla ghast is a FlyingMob
        CbFlyer flyer = new CbFlyer(class_1299.field_6107, level);
        flyer.method_5814(0.5, 300.0, 0.5);
        flyer.method_18799(net.minecraft.class_243.field_1353);
        flyer.method_36456(0.0F);
        flyer.method_6091(new net.minecraft.class_243(0.0, 0.0, 1.0));
        double dz = flyer.method_18798().field_1350;
        check(Math.abs(dz - 0.02 * 0.91) < 1.0E-6, "FlyingMob.travel in air: z speed " + dz + " = 0.02 x 0.91");
        check(!flyer.method_6101(), "FlyingMob.onClimbable false");
        Object ghast = new net.minecraft.class_1571(class_1299.field_6107, level);
        check(ghast instanceof net.minecraft.class_1307, "a vanilla Ghast is instanceof FlyingMob, as in 1.21.5");
        check(flyer instanceof net.minecraft.class_1307, "the mod's flyer is instanceof FlyingMob");

        // AttributeInstance save / load (1.21.5's CompoundTag form)
        net.minecraft.class_1324 inst = new net.minecraft.class_1324(net.minecraft.class_5134.field_23716, i -> { });
        inst.method_6192(30.0);
        net.minecraft.class_2960 mid = net.minecraft.class_2960.method_60655("cb", "m");
        inst.method_26837(new net.minecraft.class_1322(mid, 2.0, net.minecraft.class_1322.class_1323.field_6328));
        class_2487 at = inst.method_26839();
        check(at.method_68563("base", -1.0) == 30.0 && at.method_10545("id") && at.method_10545("modifiers"),
            "AttributeInstance.save writes id, base 30 and modifiers: " + at);
        net.minecraft.class_1324 back = new net.minecraft.class_1324(net.minecraft.class_5134.field_23716, i -> { });
        back.method_26833(at);
        check(back.method_6201() == 30.0 && back.method_6196(mid) && back.method_6194() == 32.0,
            "AttributeInstance.load restores base 30 and the +2 modifier (value " + back.method_6194() + ")");
        check("id".equals(net.minecraft.class_1324.field_52007), "AttributeInstance.ID_FIELD");

        // AbstractHorse.setStanding(boolean): the flag only
        net.minecraft.class_1498 horse = new net.minecraft.class_1498(class_1299.field_6139, level);
        horse.method_6737(true);
        boolean up = horse.method_6736();
        horse.method_6737(false);
        check(up && !horse.method_6736(), "AbstractHorse.setStanding(true/false) sets and clears standing");

        // SimpleContainer / PlayerEnderChestContainer ListTag forms, Abilities CompoundTag form
        net.minecraft.class_7225.class_7874 regs = level.method_30349();
        net.minecraft.class_1277 box = new net.minecraft.class_1277(3);
        box.method_5447(1, new net.minecraft.class_1799(net.minecraft.class_1802.field_8600, 5));
        net.minecraft.class_2499 items = box.method_7660(regs);
        net.minecraft.class_1277 box2 = new net.minecraft.class_1277(3);
        box2.method_7659(items, regs);
        check(items.size() == 1 && box2.method_5438(0).method_7909() == net.minecraft.class_1802.field_8600 && box2.method_5438(0).method_7947() == 5,
            "SimpleContainer.createTag / fromTag round trip (compacted, as 1.21.5): " + items);
        net.minecraft.class_1730 ender = new net.minecraft.class_1730();
        ender.method_5447(4, new net.minecraft.class_1799(net.minecraft.class_1802.field_8600, 2));
        net.minecraft.class_2499 slots = ender.method_7660(regs);
        net.minecraft.class_1730 ender2 = new net.minecraft.class_1730();
        ender2.method_7659(slots, regs);
        check(slots.size() == 1 && slots.method_68582(0).method_68562("Slot", (byte) -1) == 4 && ender2.method_5438(4).method_7947() == 2,
            "PlayerEnderChestContainer.createTag / fromTag keep the slot: " + slots);
        net.minecraft.class_1656 ab = new net.minecraft.class_1656();
        ab.field_7479 = true;
        ab.method_7248(0.2F);
        class_2487 abt = new class_2487();
        ab.method_7251(abt);
        net.minecraft.class_1656 ab2 = new net.minecraft.class_1656();
        ab2.method_7249(abt);
        check(abt.method_68568("abilities").method_68566("flying", false) && ab2.field_7479 && ab2.method_7252() == 0.2F && ab2.field_7476,
            "Abilities.addSaveData / loadSaveData: " + abt);

        // Entity / Leashable members
        net.minecraft.class_1542 item = new net.minecraft.class_1542(level, 0.0, 300.0, 0.0, new net.minecraft.class_1799(net.minecraft.class_1802.field_8600));
        check(!item.method_30948(), "Entity.canBeCollidedWith(): an item entity is not collidable");
        net.minecraft.class_243 off = item.method_45321(1.0F);
        check(off.field_1352 == 0.0 && Math.abs(off.field_1351 - item.method_5751()) < 1.0E-6 && Math.abs(off.field_1350 - item.method_17681() * 0.4F) < 1.0E-6,
            "Entity.getLeashOffset(float) of a non-leashable entity: " + off);
        item.field_6017 = 5.0;
        item.method_18799(net.minecraft.class_243.field_1353);
        item.method_45318();
        check(item.field_6017 == 1.0, "Entity.method_45318 caps the fall distance at 1 when not falling fast: " + item.field_6017);
        net.minecraft.class_10730 cow = new net.minecraft.class_10730(class_1299.field_6085, level);
        check(((net.minecraft.class_9817) cow).method_60969(), "Leashable.canHaveALeashAttachedToIt() of a fresh cow");
        check(item.method_5770() == level, "Entity.getCommandSenderWorld()");

        // ClientboundChangeDifficultyPacket accessors
        net.minecraft.class_2632 pkt = new net.minecraft.class_2632(net.minecraft.class_1267.field_5807, true);
        check(pkt.method_11342() == net.minecraft.class_1267.field_5807 && pkt.method_11340(),
            "ClientboundChangeDifficultyPacket.getDifficulty / isLocked");
    }

    static void problems() {
        class_8942.class_8943 collector = new class_8942.class_8943();
        collector.method_54946(".pools").method_54946("[0]").method_54947("bad entry");
        String report = collector.method_59906().orElse("");
        check(report.contains("bad entry") && report.contains(".pools[0]"), "ProblemReporter: 1.21.5 strings path and report (" + report + ")");
        check(collector.method_54948().containsValue("bad entry"), "ProblemReporter.Collector.get()");
        check(new class_8942.class_8943().method_59906().isEmpty(), "ProblemReporter: empty report is empty");
    }
}
