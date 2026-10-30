package top.dext.centurybridge.step.v1_21_5.rt;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.class_1263;
import net.minecraft.class_1269;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1452;
import net.minecraft.class_1496;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_2315;
import net.minecraft.class_2338;
import net.minecraft.class_2342;
import net.minecraft.class_238;
import net.minecraft.class_3419;
import net.minecraft.class_4985;
import net.minecraft.class_5712;
import top.dext.centurybridge.step.v1_21_5.facade.Saddleable;

/**
 * 1.21.4's saddle contract on 1.21.5 (facade/Saddleable). javap -c -p 1.21.4 class_1816 (SaddleItem.interactLivingEntity),
 * class_2357$10 (the dispenser's saddle behaviour), class_1452 / class_4985 / class_1496 (Pig / Strider / AbstractHorse
 * as Saddleables) and 1.21.5 class_10192.method_66700 (Equippable.equipOnTarget), class_10205.method_64106
 * (EquipmentDispenseItemBehavior.dispenseEquipment), class_1308.method_66672 (Mob.isSaddled), method_56991
 * (canUseSlot) of the same three entities.
 *
 * 1.21.5 equips a saddle through the saddle item's Equippable (slot SADDLE) on an entity whose type the component
 * allows and whose canUseSlot(SADDLE) holds; a mod's own 1.21.4 Saddleable entity is neither, so those two paths answer
 * "not equipped" for it. Where they do, the hooks run 1.21.4's bodies for it ({@link #useSaddle}, {@link #dispenseSaddle}).
 * On 1.21.4's vanilla Saddleables 1.21.5's own path stands, routed through a 1.21.4 override a converted subclass
 * declares ({@link #overrides}).
 */
public final class CbSaddles {
    private CbSaddles() {
    }

    /** the vanilla classes that were 1.21.4 Saddleables (their restored members are 1.21.5's slot logic) */
    public static boolean vanillaSaddleable(Object e) {
        return e instanceof class_1452 || e instanceof class_4985 || e instanceof class_1496;
    }

    private static final Map<String, Boolean> OVERRIDES = new ConcurrentHashMap<>();

    /**
     * whether the entity's class declares 1.21.4's member below the vanilla classes (which restore it, or answer through
     * 1.21.5's own path): a converted subclass overriding it, or a mod's own Saddleable, which declares it itself
     */
    public static boolean overrides(Object e, String name, Class<?>... params) {
        Class<?> c = e.getClass();
        return OVERRIDES.computeIfAbsent(c.getName() + "." + name, k -> {
            for (Class<?> k2 = c; k2 != null && !k2.getName().startsWith("net.minecraft."); k2 = k2.getSuperclass()) {
                try {
                    k2.getDeclaredMethod(name, params);
                    return true;
                } catch (NoSuchMethodException ignored) {
                    // keep climbing
                }
            }
            return false;
        });
    }

    /** a converted subclass's equipSaddle(stack, source) override */
    public static boolean overridesEquip(Object e) {
        return e instanceof Saddleable && overrides(e, "method_6576", class_1799.class, class_3419.class);
    }

    /**
     * 1.21.4's SaddleItem.interactLivingEntity(stack, player, target, hand) for a mod's own Saddleable: alive, not
     * saddled, saddleable -> on the server equipSaddle(one of the stack, NEUTRAL) and the EQUIP game event; SUCCESS on
     * both sides; otherwise PASS
     */
    public static class_1269 useSaddle(class_1799 stack, class_1657 player, class_1309 target) {
        Saddleable s = (Saddleable) target;
        if (target.method_5805() && !s.method_6725() && s.method_6765()) {
            if (!player.method_37908().method_8608()) {
                s.method_6576(stack.method_7971(1), class_3419.field_15254);
                target.method_37908().method_43275(target, class_5712.field_28739, target.method_19538());
            }
            return class_1269.field_5812;
        }
        return class_1269.field_5811;
    }

    /**
     * 1.21.4's dispenser saddle behaviour for a mod's own Saddleable: the first living entity in front of the dispenser
     * that is a Saddleable, alive, not saddled and saddleable gets equipSaddle(one of the stack, BLOCKS); whether one did
     */
    public static boolean dispenseSaddle(class_2342 source, class_1799 stack) {
        class_2338 pos = source.comp_1968().method_10093(source.comp_1969().method_11654(class_2315.field_10918));
        List<class_1309> found = source.comp_1967().method_8390(class_1309.class, new class_238(pos),
            e -> e instanceof Saddleable s && !vanillaSaddleable(e) && e.method_5805() && !s.method_6725() && s.method_6765());
        if (found.isEmpty()) {
            return false;
        }
        ((Saddleable) found.get(0)).method_6576(stack.method_7971(1), class_3419.field_15245);
        return true;
    }

    /** the slot a saddle equips into */
    public static boolean saddleSlot(class_1304 slot) {
        return slot == class_1304.field_55946;
    }

    /**
     * AbstractHorse.syncSaddleToClients(): 1.21.4 copied "inventory slot 0 holds a saddle" into the entity flags
     * isSaddled read (javap -c 1.21.4 class_1496.method_56679); 1.21.5's isSaddled reads the SADDLE equipment slot, which
     * the entity's equipment sync carries, so there is no separate state left to copy
     */
    public static void syncSaddleToClients(class_1496 horse) {
    }

    /**
     * AbstractHorse.getBodyArmorAccess(): 1.21.4's container view of the body armor slot (field_48830); 1.21.5's
     * Mob.createEquipmentSlotContainer(BODY) (method_66674) is the same one-slot view of that slot
     */
    public static class_1263 bodyArmorAccess(class_1496 horse) {
        return horse.method_66674(class_1304.field_48824);
    }
}
