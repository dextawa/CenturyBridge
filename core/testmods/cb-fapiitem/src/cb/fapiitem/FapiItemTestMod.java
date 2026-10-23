package cb.fapiitem;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.item.v1.EquipmentSlotProvider;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.fabricmc.fabric.api.item.v1.FabricItemStack;
import net.fabricmc.fabric.api.util.TriState;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1887;
import net.minecraft.class_1893;
import net.minecraft.class_2378;
import net.minecraft.class_2960;
import net.minecraft.class_5321;
import net.minecraft.class_7923;
import net.minecraft.class_7924;
import net.minecraft.class_9285;
import net.minecraft.server.MinecraftServer;

/**
 * A 1.20.6-era server mod for Fabric API's item API on 1.21: canBeEnchantedWith overridden (with and
 * without FabricItem.super), the ALLOW_ENCHANTING event registered as a lambda, EnchantingContext's
 * 1.20.6 constants, FabricItem.getAttributeModifiers declared and called, an EquipmentSlotProvider
 * lambda. 1.21 Fabric API's own entry points are reached by reflection. Every check prints one
 * "CB-FAPITEST ok|FAIL ..." line; the last is "CB-FAPITEST DONE &lt;passed&gt;/&lt;checks&gt;".
 */
public class FapiItemTestMod implements ModInitializer {

    static final List<String> FAILS = new ArrayList<>();
    static int checks;

    static void check(boolean ok, String what) {
        checks++;
        System.out.println("CB-FAPITEST " + (ok ? "ok   " : "FAIL ") + what);
        if (!ok) {
            FAILS.add(what);
        }
    }

    static final class_9285 MODS = class_9285.method_57480().method_57486();

    /** sharpness also on this item, through FabricItem's default for the rest; its own attribute modifiers */
    public static class TItem extends class_1792 implements FabricItem {
        static EnchantingContext lastContext;

        public TItem() {
            super(new class_1792.class_1793());
        }

        @Override
        public boolean canBeEnchantedWith(class_1799 stack, class_1887 enchantment, EnchantingContext context) {
            lastContext = context;
            return enchantment == class_1893.field_9118 || FabricItem.super.canBeEnchantedWith(stack, enchantment, context);
        }

        @Override
        public class_9285 getAttributeModifiers(class_1799 stack) {
            return MODS;
        }

        class_9285 superModifiers(class_1799 stack) {
            return FabricItem.super.getAttributeModifiers(stack);
        }
    }

    static TItem ITEM;

    @Override
    public void onInitialize() {
        ITEM = class_2378.method_10230(class_7923.field_41178, class_2960.method_43902("cbfapiitem", "item"), new TItem());
        EnchantmentEvents.ALLOW_ENCHANTING.register((enchantment, stack, context) ->
            enchantment == class_1893.field_9121 && stack.method_31574(class_1802.field_8600) ? TriState.TRUE : TriState.DEFAULT);
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
        }, "cb-fapiitem");
        t.setDaemon(true);
        t.start();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    static void run(MinecraftServer server) {
        try {
            class_2378 enchantments = server.method_30611().method_30530(class_7924.field_41265);
            Object sharpness = ((Optional) enchantments.method_40264(class_5321.method_29179(class_7924.field_41265,
                class_2960.method_43902("minecraft", "sharpness")))).get();
            Object knockback = ((Optional) enchantments.method_40264(class_5321.method_29179(class_7924.field_41265,
                class_2960.method_43902("minecraft", "knockback")))).get();
            Class<?> holder = Class.forName("net.minecraft.class_6880");
            Object primary = EnchantingContext.class.getMethod("valueOf", String.class).invoke(null, "PRIMARY");
            Object acceptable = EnchantingContext.class.getMethod("valueOf", String.class).invoke(null, "ACCEPTABLE");

            // ---- EnchantingContext ----
            check(EnchantingContext.RANDOM_ENCHANTMENT == primary, "RANDOM_ENCHANTMENT is 1.21's PRIMARY (the enchanting table)");
            check(EnchantingContext.ANVIL == acceptable && EnchantingContext.ENCHANT_COMMAND == acceptable
                && EnchantingContext.LOOT_RANDOM_ENCHANTMENT == acceptable, "ANVIL, ENCHANT_COMMAND, LOOT_RANDOM_ENCHANTMENT are ACCEPTABLE");

            // ---- canBeEnchantedWith ----
            class_1799 stack = new class_1799(ITEM);
            Method stackCan = FabricItemStack.class.getMethod("canBeEnchantedWith", holder, EnchantingContext.class);
            check((boolean) stackCan.invoke(stack, sharpness, acceptable) && TItem.lastContext == acceptable,
                "1.21 ItemStack.canBeEnchantedWith(holder, ctx) reaches the mod's override with the object");
            check(!(boolean) stackCan.invoke(stack, knockback, acceptable), "knockback on the mod item: its super's answer (false)");
            check(((FabricItemStack) (Object) stack).canBeEnchantedWith(class_1893.field_9118, EnchantingContext.ANVIL),
                "the mod's own FabricItemStack.canBeEnchantedWith(enchantment, ANVIL)");
            check(((FabricItem) ITEM).canBeEnchantedWith(stack, class_1893.field_9118, EnchantingContext.ANVIL),
                "the mod's own FabricItem.canBeEnchantedWith(stack, enchantment, ANVIL)");

            // ---- ALLOW_ENCHANTING ----
            class_1799 stick = new class_1799(class_1802.field_8600);
            check((boolean) stackCan.invoke(stick, knockback, acceptable), "1.21 asks a stick for knockback: the mod's ALLOW_ENCHANTING lambda allows it");
            check(!(boolean) stackCan.invoke(stick, sharpness, acceptable), "sharpness on a stick: DEFAULT, vanilla's no");
            check(EnchantmentEvents.ALLOW_ENCHANTING.invoker().allowEnchanting(class_1893.field_9121, stick, EnchantingContext.ANVIL) == TriState.TRUE,
                "the mod's own call of the ALLOW_ENCHANTING invoker");

            // ---- getAttributeModifiers ----
            check(((FabricItem) ITEM).getAttributeModifiers(stack) == MODS, "FabricItem.getAttributeModifiers: the mod item's own");
            check(ITEM.superModifiers(stack) == ITEM.method_7844(), "FabricItem.super.getAttributeModifiers: the item's default");
            check(((FabricItem) class_1802.field_8600).getAttributeModifiers(stick) == class_1802.field_8600.method_7844(),
                "getAttributeModifiers on a vanilla item: its default");

            // ---- EquipmentSlotProvider ----
            EquipmentSlotProvider head = s -> class_1304.field_6169;
            Method slot = EquipmentSlotProvider.class.getMethod("getPreferredEquipmentSlot", class_1309.class, class_1799.class);
            check(slot.invoke(head, null, stick) == class_1304.field_6169, "1.21 getPreferredEquipmentSlot(entity, stack) on the mod's lambda");
            check(head.getPreferredEquipmentSlot(stick) == class_1304.field_6169, "the mod's own getPreferredEquipmentSlot(stack)");
        } catch (Throwable e) {
            e.printStackTrace(System.out);
            check(false, "threw " + e);
        }
        System.out.println("CB-FAPITEST DONE " + (checks - FAILS.size()) + "/" + checks);
    }
}
