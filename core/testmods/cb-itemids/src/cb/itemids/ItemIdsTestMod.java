package cb.itemids;

import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1271;
import net.minecraft.class_1657;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1937;
import net.minecraft.class_2248;
import net.minecraft.class_2378;
import net.minecraft.class_2561;
import net.minecraft.class_2588;
import net.minecraft.class_2960;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_4970;
import net.minecraft.class_52;
import net.minecraft.class_5321;
import net.minecraft.class_7923;
import net.minecraft.class_8791;
import net.minecraft.class_9062;
import net.minecraft.class_9334;
import net.minecraft.server.MinecraftServer;

/**
 * A 1.21.1-era server mod (intermediary names, Fabric Loader API only) for the s7item bridges of the
 * 1.21.1 -> 1.21.2 step:
 * <ul>
 * <li>items, a block item and blocks constructed before they are registered (1.21.2 wants their id at
 *     construction: CbPendingIds), one Properties object shared by two of each, a block without loot
 *     table; their description ids, item names and loot tables after registration, as 1.21.1 derived them;</li>
 * <li>Item.use answering an InteractionResultHolder (success with a new stack, pass, fail, consume), seen
 *     through ServerPlayerGameMode.useItem and through the 1.21.1 call shapes;</li>
 * <li>InteractionResult's enum surface (constants, values, ordinal, name, shouldSwing, indicateItemUse,
 *     sidedSuccess) and ItemInteractionResult's.</li>
 * </ul>
 * Every check prints one "CB-IDTEST ok|FAIL ..." line; the last is "CB-IDTEST DONE &lt;passed&gt;/&lt;checks&gt;".
 *
 * <p>Build against 1.21.1:
 * <pre>
 *   javac --release 21 -cp "client-1.21.1-intermediary.jar:fabric-loader.jar:&lt;1.21.1 libraries&gt;" -d out \
 *         core/testmods/cb-itemids/src/cb/itemids/*.java
 *   cp core/testmods/cb-itemids/res/fabric.mod.json out/ &amp;&amp; (cd out &amp;&amp; jar cf cb-itemids.jar .)
 * </pre>
 */
public class ItemIdsTestMod implements ModInitializer {

    static final List<String> FAILS = new ArrayList<>();
    static int checks;

    static void check(boolean ok, String what) {
        checks++;
        System.out.println("CB-IDTEST " + (ok ? "ok   " : "FAIL ") + what);
        if (!ok) {
            FAILS.add(what);
        }
    }

    /** a 1.21.2 no-argument method of BlockBehaviour, by reflection */
    static Object w(Object o, String name) {
        try {
            return class_4970.class.getMethod(name).invoke(o);
        } catch (ReflectiveOperationException e) {
            return e;
        }
    }

    static String key(class_2561 c) {
        return c != null && c.method_10851() instanceof class_2588 t ? t.method_11022() : String.valueOf(c);
    }

    /** answers what the test sets: the 1.21.1 holder shapes */
    public static class UseItem extends class_1792 {
        static int mode;
        static class_1799 replacement;

        public UseItem(class_1792.class_1793 p) {
            super(p);
        }

        @Override
        public class_1271<class_1799> method_7836(class_1937 level, class_1657 player, class_1268 hand) {
            class_1799 held = player.method_5998(hand);
            return switch (mode) {
                case 0 -> class_1271.method_22427(replacement);          // success, new stack
                case 1 -> class_1271.method_22430(held);                 // pass
                case 2 -> class_1271.method_22431(held);                 // fail
                case 3 -> class_1271.method_29237(held, level.field_9236); // sidedSuccess: consume on the server
                default -> super.method_7836(level, player, hand);       // vanilla's (no food: pass)
            };
        }
    }

    static class_1792 ITEM_A, ITEM_B, BLOCK_ITEM, USE;
    static class_2248 BLOCK_A, BLOCK_B, BLOCK_NOLOOT;

    static <T> T reg(class_2378<? super T> r, String path, T v) {
        class_2378.method_10230(r, class_2960.method_60655("cbitemids", path), v);
        return v;
    }

    @Override
    public void onInitialize() {
        // construct first, register later -- and share one Properties between two of each
        class_1792.class_1793 shared = new class_1792.class_1793();
        class_1792 a = new class_1792(shared);
        class_1792 b = new class_1792(shared);
        class_4970.class_2251 bs = class_4970.class_2251.method_9637();
        class_2248 ba = new class_2248(bs);
        class_2248 bb = new class_2248(bs);
        class_2248 nl = new class_2248(class_4970.class_2251.method_9637().method_42327());
        class_1792 bi = new class_1747(ba, new class_1792.class_1793());
        UseItem use = new UseItem(new class_1792.class_1793());
        // the block item registers before its block
        BLOCK_ITEM = reg(class_7923.field_41178, "block_a", bi);
        ITEM_A = reg(class_7923.field_41178, "item_a", a);
        ITEM_B = reg(class_7923.field_41178, "item_b", b);
        USE = reg(class_7923.field_41178, "use_item", use);
        BLOCK_A = reg(class_7923.field_41175, "block_a", ba);
        BLOCK_B = reg(class_7923.field_41175, "block_b", bb);
        BLOCK_NOLOOT = reg(class_7923.field_41175, "block_noloot", nl);
        Thread t = new Thread(() -> {
            for (int i = 0; i < 600; i++) {
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    return;
                }
                Object game = FabricLoader.getInstance().getGameInstance();
                if (game instanceof MinecraftServer server && server.method_3806() && server.method_30002() != null) {
                    server.execute(() -> run(server));
                    return;
                }
            }
        }, "cb-itemids");
        t.setDaemon(true);
        t.start();
    }

    static void run(MinecraftServer server) {
        try {
            ids();
            results();
            use(server);
        } catch (Throwable e) {
            e.printStackTrace();
            check(false, "no exception: " + e);
        }
        System.out.println("CB-IDTEST DONE " + (checks - FAILS.size()) + "/" + checks + (FAILS.isEmpty() ? "" : " FAILED " + FAILS));
    }

    static void ids() {
        check("item.cbitemids.item_a".equals(ITEM_A.method_7876()), "item description id from its registry id: " + ITEM_A.method_7876());
        check("item.cbitemids.item_b".equals(ITEM_B.method_7876()), "second item of the same Properties: " + ITEM_B.method_7876());
        class_1799 sa = new class_1799(ITEM_A);
        check("item.cbitemids.item_a".equals(key(sa.method_7964())), "stack hover name: " + key(sa.method_7964()));
        check("item.cbitemids.item_b".equals(key(ITEM_B.method_57347().method_57829(class_9334.field_50239))), "ITEM_NAME of the second item");
        // Block.getDescriptionId / getLootTable changed shape in 1.21.2 (another step area's bridges): read
        // 1.21.2's own BlockBehaviour.getDescriptionId() / getLootTable() -> Optional by reflection
        check("block.cbitemids.block_a".equals(w(BLOCK_A, "method_63499")), "block description id: " + w(BLOCK_A, "method_63499"));
        check("block.cbitemids.block_b".equals(w(BLOCK_B, "method_63499")), "second block of the same Properties: " + w(BLOCK_B, "method_63499"));
        check("block.cbitemids.block_a".equals(BLOCK_ITEM.method_7876()), "block item (registered before its block) takes its block's id: " + BLOCK_ITEM.method_7876());
        check("block.cbitemids.block_a".equals(key(new class_1799(BLOCK_ITEM).method_7964())), "block item stack name: " + key(new class_1799(BLOCK_ITEM).method_7964()));
        Object loot = w(BLOCK_A, "method_26162");
        check(loot instanceof Optional<?> o && o.isPresent() && ((class_5321<?>) o.get()).method_29177().toString().equals("cbitemids:blocks/block_a"),
            "block loot table: " + loot);
        Object nloot = w(BLOCK_NOLOOT, "method_26162");
        check(nloot instanceof Optional<?> o && o.isEmpty(), "noLootTable block (1.21.2: no table): " + nloot);
        check(class_7923.field_41178.method_10221(ITEM_A).toString().equals("cbitemids:item_a"), "registry id of the item");
        check(class_1802.field_8477.method_7876().equals("item.minecraft.diamond"), "vanilla item untouched");
    }

    static void results() {
        class_1269[] v = class_1269.values();
        check(v.length == 6, "InteractionResult.values() has 1.21.1's six constants");
        check(v[0] == class_1269.field_5812 && v[4] == class_1269.field_5811 && v[5] == class_1269.field_5814,
            "values() order SUCCESS..PASS, FAIL");
        check(class_1269.field_21466.ordinal() == 2 && class_1269.field_5811.ordinal() == 4, "ordinal()");
        check("CONSUME_PARTIAL".equals(class_1269.field_33562.name()), "name()");
        check(class_1269.valueOf("FAIL") == class_1269.field_5814, "valueOf");
        check(class_1269.field_5812.method_23665() && class_1269.field_21466.method_23665() && class_1269.field_33562.method_23665()
            && class_1269.field_51370.method_23665() && !class_1269.field_5811.method_23665() && !class_1269.field_5814.method_23665(), "consumesAction");
        check(class_1269.field_5812.method_23666() && class_1269.field_51370.method_23666() && !class_1269.field_21466.method_23666(), "shouldSwing");
        check(class_1269.field_5812.method_36360() && class_1269.field_21466.method_36360() && !class_1269.field_33562.method_36360()
            && !class_1269.field_51370.method_36360(), "indicateItemUse");
        check(class_1269.method_29236(true) == class_1269.field_5812 && class_1269.method_29236(false) == class_1269.field_21466, "sidedSuccess");
        int sw = switch (class_1269.field_5814) {
            case field_5812 -> 0;
            case field_5814 -> 5;
            default -> -1;
        };
        check(sw == 5, "switch over InteractionResult");
        check(class_9062.field_47731.method_55645() == class_1269.field_5811 && class_9062.field_47728.method_55645() == class_1269.field_5812,
            "ItemInteractionResult.result()");
        check(class_9062.field_47729.method_55643() && !class_9062.field_47731.method_55643(), "ItemInteractionResult.consumesAction");
        check(class_9062.method_55644(false) == class_9062.field_47729 && class_9062.values().length == 6, "ItemInteractionResult sidedSuccess / values");
    }

    static void use(MinecraftServer server) {
        class_3218 level = server.method_30002();
        class_3222 player = new class_3222(server, level, new GameProfile(UUID.randomUUID(), "cbidtest"), class_8791.method_53821());
        UseItem.mode = 0;
        UseItem.replacement = new class_1799(class_1802.field_8477, 3);
        player.method_6122(class_1268.field_5808, new class_1799(USE));
        class_1269 r = player.field_13974.method_14256(player, level, player.method_5998(class_1268.field_5808), class_1268.field_5808);
        check(r.method_23665(), "use -> success consumes the action through the game mode");
        check(player.method_5998(class_1268.field_5808).method_31574(class_1802.field_8477) && player.method_5998(class_1268.field_5808).method_7947() == 3,
            "use -> success(new stack) puts the stack in the hand");
        UseItem.mode = 1;
        class_1799 held = new class_1799(USE);
        player.method_6122(class_1268.field_5808, held);
        class_1271<class_1799> h = held.method_7913(level, player, class_1268.field_5808);
        check(h.method_5467() == class_1269.field_5811 && h.method_5466() == held, "ItemStack.use -> pass holder with the held stack");
        UseItem.mode = 2;
        check(USE.method_7836(level, player, class_1268.field_5808).method_5467() == class_1269.field_5814, "Item.use called by the mod -> fail");
        UseItem.mode = 3;
        class_1271<class_1799> c = USE.method_7836(level, player, class_1268.field_5808);
        check(c.method_5467() == class_1269.field_21466, "sidedSuccess(held, server) -> consume");
        UseItem.mode = 4;
        class_1269 vr = player.field_13974.method_14256(player, level, player.method_5998(class_1268.field_5808), class_1268.field_5808);
        check(!vr.method_23665(), "the mod's super.use -> vanilla's (pass for a plain item): " + vr);
        class_1271<class_1799> dh = new class_1799(class_1802.field_8477).method_7913(level, player, class_1268.field_5808);
        check(dh.method_5467() == class_1269.field_5811, "a vanilla item's use as a 1.21.1 holder: pass");
        player.method_6122(class_1268.field_5808, class_1799.field_8037);
    }
}
