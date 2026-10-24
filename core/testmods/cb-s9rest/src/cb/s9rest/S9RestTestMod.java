package cb.s9rest;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1293;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1308;
import net.minecraft.class_1428;
import net.minecraft.class_1606;
import net.minecraft.class_1642;
import net.minecraft.class_1735;
import net.minecraft.class_1741;
import net.minecraft.class_1759;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1844;
import net.minecraft.class_1856;
import net.minecraft.class_1926;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_2487;
import net.minecraft.class_2561;
import net.minecraft.class_2627;
import net.minecraft.class_2680;
import net.minecraft.class_2692;
import net.minecraft.class_2735;
import net.minecraft.class_2960;
import net.minecraft.class_3218;
import net.minecraft.class_3270;
import net.minecraft.class_3417;
import net.minecraft.class_3481;
import net.minecraft.class_3489;
import net.minecraft.class_4208;
import net.minecraft.class_4482;
import net.minecraft.class_7368;
import net.minecraft.class_7677;
import net.minecraft.class_8054;
import net.minecraft.class_9280;
import net.minecraft.class_9334;
import net.minecraft.class_9817;
import net.minecraft.server.MinecraftServer;

/**
 * A 1.21.3-era server mod (intermediary names, Fabric Loader API only) for s9rest's bridges of the
 * 1.21.3 -> 1.21.4 step, run once the server is up. Each check prints "CB-S9TEST ok|FAIL ..."; the last line is
 * "CB-S9TEST DONE passed/checks".
 */
public class S9RestTestMod implements ModInitializer {

    static final List<String> FAILS = new ArrayList<>();
    static int checks;

    static void check(boolean ok, String what) {
        checks++;
        System.out.println("CB-S9TEST " + (ok ? "ok   " : "FAIL ") + what);
        if (!ok) {
            FAILS.add(what);
        }
    }

    static void run(String what, Runnable r) {
        try {
            r.run();
        } catch (Throwable e) {
            check(false, what + " threw " + e);
            e.printStackTrace(System.out);
        }
    }

    /** a 1.21.3 metadata section serializer of the mod's own (MetadataSectionSerializer) */
    static final class Section implements class_3270<Integer> {
        @Override
        public String method_14420() {
            return "cbtest";
        }

        @Override
        public Integer method_14421(JsonObject json) {
            return json.get("v").getAsInt();
        }
    }

    /** a subclass of 1.21.3's ClientboundMoveVehiclePacket overriding getX */
    static final class VehiclePacket extends class_2692 {
        VehiclePacket(class_1297 e) {
            super(e);
        }

        @Override
        public double method_11673() {
            return 42.0;
        }
    }

    /** a zombie writing LivingEntity.deathScore, a protected field of 1.21.3 */
    static final class ScoredZombie extends class_1642 {
        ScoredZombie(class_3218 level) {
            super(class_1299.field_6051, level);
        }

        void score(int s) {
            this.field_6232 = s;
        }

        int score() {
            return this.field_6232;
        }
    }

    @Override
    public void onInitialize() {
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
        }, "cb-s9rest");
        t.setDaemon(true);
        t.start();
    }

    static void start(MinecraftServer server) {
        class_3218 level = server.method_30002();
        class_2338 spawn = level.method_43126();

        run("custom model data", () -> {
            check(new class_9280(7).comp_2382() == 7, "new CustomModelData(7).value() == 7");
            check(class_9280.field_49307.comp_2382() == 0, "CustomModelData.DEFAULT.value() == 0");
        });
        run("potion color", () -> check(class_1844.method_8055(List.<class_1293>of()) == -13083194,
            "PotionContents.getColor(no effects) is 1.21.3's default -13083194"));
        run("foliage", () -> check(class_1926.method_8343() == -8345771, "FoliageColor.getBirchColor() == -8345771"));
        run("shulker aabb", () -> {
            class_238 box = class_1606.method_33347(1.0F, class_2350.field_11036, 0.0F, 0.5F);
            // 1.21.3: new AABB(0,0,0,1,1,1).expandTowards(0, 0.5, 0).contract(0, -1, 0) = (0,1,0)-(1,1.5,1)
            check(box.field_1323 == 0.0 && box.field_1322 == 1.0 && box.field_1321 == 0.0 && box.field_1320 == 1.0
                && Math.abs(box.field_1325 - 1.5) < 1e-9 && box.field_1324 == 1.0,
                "Shulker.getProgressDeltaAabb(1, UP, 0, 0.5) is 1.21.3's corner-based box (0,1,0)-(1,1.5,1): " + box);
        });
        run("bfs", () -> {
            int none = class_2338.method_49925(spawn, 4, 64, (p, c) -> c.accept(p.method_10084()), p -> false);
            AtomicInteger seen = new AtomicInteger();
            int some = class_2338.method_49925(spawn, 2, 64, (p, c) -> c.accept(p.method_10084()), p -> { seen.incrementAndGet(); return true; });
            check(none == 0, "breadthFirstTraversal with a predicate refusing everything visits 0 (" + none + ")");
            check(some == 3 && seen.get() == 3, "breadthFirstTraversal accepting, depth 2, one neighbour each: 3 visits (" + some + ")");
        });
        run("metadata", () -> {
            String json = "{\"cbtest\":{\"v\":5},\"cbcodec\":{\"n\":9}}";
            class_7368 meta;
            try {
                meta = class_7368.method_43042(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
            } catch (java.io.IOException e) {
                throw new RuntimeException(e);
            }
            Section section = new Section();
            Optional<Integer> v = meta.method_43041(section);
            check(v.isPresent() && v.get() == 5, "ResourceMetadata.getSection(a 1.21.3 serializer) reads its section: " + v);
            check(meta.method_43041(section).isPresent(), "the same serializer reads it again (one record per serializer)");
            class_7677<Integer> type = class_7677.method_45252("cbcodec", Codec.INT.fieldOf("n").codec());
            Optional<Integer> n = meta.method_43041(type);
            check(n.isPresent() && n.get() == 9, "ResourceMetadata.getSection(MetadataSectionType.fromCodec(..)): " + n);
            check("cbcodec".equals(type.method_14420()), "fromCodec type's getMetadataSectionName()");
            check(type.method_45251(9).get("n").getAsInt() == 9, "fromCodec type's toJson(9)");
        });
        run("tags", () -> {
            check(new class_1799(class_1802.field_8491).method_31573(class_3489.field_20344), "dandelion is in 1.21.3's #minecraft:flowers (item)");
            check(new class_1799(class_1802.field_17525).method_31573(class_3489.field_20343), "sunflower is in #minecraft:tall_flowers (item)");
            check(new class_1799(class_1802.field_41949).method_31573(class_3489.field_41892), "coast trim template is in #minecraft:trim_templates");
            check(class_2246.field_10583 != null && class_2246.field_10583.method_9564().method_26164(class_3481.field_20338),
                "sunflower block is in #minecraft:tall_flowers (block)");
        });
        run("clone stack", () -> {
            class_2338 pos = spawn.method_10086(3);
            level.method_17988(pos.method_10263() >> 4, pos.method_10260() >> 4, true);
            level.method_8501(pos, class_2246.field_10603.method_9564());
            if (level.method_8321(pos) instanceof class_2627 box) {
                box.method_5447(0, new class_1799(class_1802.field_20391, 3));
            }
            class_2680 state = level.method_8320(pos);
            class_1799 picked = class_2246.field_10603.method_9574(level, pos, state);
            check(picked.method_57826(class_9334.field_49622), "1.21.3 ShulkerBoxBlock.getCloneItemStack carries the box's contents");
            class_1799 stone = class_2246.field_10340.method_9574(level, pos, class_2246.field_10340.method_9564());
            check(stone.method_7909() == class_1802.field_20391, "Block.getCloneItemStack(stone) is a stone stack");
        });
        run("ingredient", () -> check(class_1856.method_8101(class_1802.field_20391).method_8105().size() == 1,
            "Ingredient.of(stone).items() is a list of one holder"));
        run("compass", () -> {
            class_4208 p = class_1759.method_43123(level);
            check(p != null && p.comp_2207() == level.method_27983(), "CompassItem.getSpawnPosition(overworld) is the overworld spawn");
        });
        run("equipment", () -> {
            class_2960 id = class_2960.method_60655("cbtest", "steel");
            class_1741 mat = new class_1741(5, Map.of(), 9, class_3417.field_14883, 0.0F, 0.0F, class_3489.field_54061, id);
            check(id.equals(mat.comp_3168()), "ArmorMaterial(.., modelId).modelId() round trip");
            class_8054 trim = class_8054.method_48438("cbsteel", class_1802.field_20391, 0.75F, class_2561.method_43470("x"), Map.of());
            check(trim.comp_1210() == 0.75F, "TrimMaterial.create(.., 0.75, ..).itemModelIndex() == 0.75");
        });
        run("vehicle packet", () -> {
            class_1428 chicken = new class_1428(class_1299.field_6132, level);
            chicken.method_5814(1.5, 70.0, -2.5);
            class_2692 plain = new class_2692(chicken);
            check(plain.method_11673() == 1.5 && plain.method_11670() == -2.5, "ClientboundMoveVehiclePacket(entity).getX/getZ");
            class_2692 sub = new VehiclePacket(chicken);
            check(sub.method_11673() == 42.0 && sub.method_11674() == 70.0, "a 1.21.3 subclass's getX override answers the call; getY stays the field");
            check(new class_2735(4).method_11803() == 4, "ClientboundSetHeldSlotPacket(4).getSlot()");
        });
        run("death score", () -> {
            ScoredZombie z = new ScoredZombie(level);
            check(z.score() == 0, "LivingEntity.deathScore starts at 0");
            z.score(-1);
            check(z.score() == -1, "LivingEntity.deathScore keeps a written value");
        });
        run("leash data", () -> {
            class_2487 tag = new class_2487();
            class_2487 leash = new class_2487();
            leash.method_25927("UUID", UUID.randomUUID());
            tag.method_10566("leash", leash);
            class_1308 pig = new net.minecraft.class_1452(class_1299.field_6093, level);
            Object data = ((class_9817) pig).method_60967(tag);
            check(data != null, "Leashable.readLeashData(tag) parses a leash holder UUID (1.21.3: answers it, sets nothing)");
            check(((class_9817) pig).method_60955() == null, "readLeashData did not attach the leash");
        });
        run("beehive", () -> {
            class_2338 pos = spawn.method_10086(5);
            level.method_8501(pos, class_2246.field_20422.method_9564());
            if (level.method_8321(pos) instanceof class_4482 hive) {
                class_1428 chicken = new class_1428(class_1299.field_6132, level);
                level.method_8649(chicken);
                hive.method_21848(chicken);
                check(hive.method_23903() == 1, "BeehiveBlockEntity.addOccupant(a chicken): 1.21.3 stores any entity (" + hive.method_23903() + ")");
                check(chicken.method_31481(), "the stored entity is discarded");
            } else {
                check(false, "a beehive block entity at " + pos);
            }
        });
        run("slot icon", () -> {
            class_1735 slot = new class_1735(new net.minecraft.class_1277(1), 0, 0, 0);
            Pair<class_2960, class_2960> icon = slot.method_7679();
            check(icon == null, "a plain Slot's getNoItemIcon() is null");
        });
        run("particles", () -> {
            int sent = level.method_14199(net.minecraft.class_2398.field_11248, 0.0, 70.0, 0.0, 3, 0.0, 0.0, 0.0, 0.0);
            check(sent == 0, "ServerLevel.sendParticles(options, ..) with nobody online sends to 0");
        });

        System.out.println("CB-S9TEST DONE " + (checks - FAILS.size()) + "/" + checks + (FAILS.isEmpty() ? "" : " FAILS: " + FAILS));
    }
}
