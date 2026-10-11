package cb.itemtest;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

/**
 * 1.20.1-era mod (intermediary names, fabric-api) exercising the item model bridges: a spawn
 * egg on vanilla's template_spawn_egg model with its two constructor colours, an item tinted
 * by a ColorProviderRegistry.ITEM provider, and an item whose model "overrides" switch on a
 * FabricModelPredicateProviderRegistry predicate (stack count >= 2). Once the player is in a
 * world, the integrated server puts them into hotbar slots 0-3 (the switch once with count 1,
 * once with count 2), so one screenshot shows every case; each hook logs once
 * ("CB-ITEMTEST ...").
 */
public class ItemTestMod implements ModInitializer, ClientModInitializer {

    static final String NS = "cb_itemtest";
    static final java.util.Set<String> SEEN = java.util.concurrent.ConcurrentHashMap.newKeySet();

    public static net.minecraft.class_1792 EGG;
    public static net.minecraft.class_1792 TINTED;
    public static net.minecraft.class_1792 SWITCH;

    static void once(String what) {
        if (SEEN.add(what)) {
            System.out.println("CB-ITEMTEST " + what);
        }
    }

    static net.minecraft.class_1792 reg(String id, net.minecraft.class_1792 item) {
        return net.minecraft.class_2378.method_10230(net.minecraft.class_7923.field_41178, new net.minecraft.class_2960(NS, id), item);
    }

    @Override
    public void onInitialize() {
        EGG = reg("egg", new net.minecraft.class_1826(net.minecraft.class_1299.field_6093, 0x3060FF, 0xFFE020,
            new net.minecraft.class_1792.class_1793()));
        TINTED = reg("tinted", new net.minecraft.class_1792(new net.minecraft.class_1792.class_1793()));
        SWITCH = reg("switch", new net.minecraft.class_1792(new net.minecraft.class_1792.class_1793()));
        once("registered items");
    }

    @Override
    public void onInitializeClient() {
        net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            once("tint provider asked (index " + tintIndex + ")");
            return tintIndex == 0 ? 0xFF4040 : -1;
        }, TINTED);
        net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry.register(SWITCH,
            new net.minecraft.class_2960(NS, "lit"), (stack, world, entity, seed) -> {
                once("predicate asked (count " + stack.method_7947() + ")");
                return stack.method_7947() >= 2 ? 1.0F : 0.0F;
            });
        Thread t = new Thread(() -> {
            net.minecraft.class_310 mc = net.minecraft.class_310.method_1551();
            for (int i = 0; i < 600; i++) {
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    return;
                }
                net.minecraft.class_1132 server = mc.method_1576();
                if (mc.field_1687 != null && mc.field_1724 != null && server != null) {
                    server.execute(() -> {
                        net.minecraft.class_3222 p = server.method_3760().method_14571().get(0);
                        net.minecraft.class_1661 inv = p.method_31548();
                        inv.method_5447(0, new net.minecraft.class_1799(EGG));
                        inv.method_5447(1, new net.minecraft.class_1799(TINTED));
                        inv.method_5447(2, new net.minecraft.class_1799(SWITCH, 1));
                        inv.method_5447(3, new net.minecraft.class_1799(SWITCH, 2));
                        once("filled hotbar");
                        once("egg colours " + Integer.toHexString(((net.minecraft.class_1826) EGG).method_8016(0)) + " "
                            + Integer.toHexString(((net.minecraft.class_1826) EGG).method_8016(1)));
                    });
                    return;
                }
            }
        }, "cb-itemtest");
        t.setDaemon(true);
        t.start();
    }
}
