package top.dext.centurybridge.step.v1_21.rt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import top.dext.centurybridge.step.v1_21.facade.class_1887;

/**
 * 1.20.6's EnchantmentHelper statics a 1.20.6 mod calls and 1.21 no longer has in that shape (each wired
 * as a staticRedirect of the same name and descriptor). 1.20.6's bodies, over 1.20.6's enchantment objects
 * (facades, see CbEnchantments) and 1.21's item data:
 * <ul>
 * <li>an item's level of an enchantment is 1.21's (the item's ENCHANTMENTS component, the holder of
 *     the enchantment's key in the world's registry);</li>
 * <li>an entity's level of an enchantment is 1.20.6's: the highest item level over the enchantment's
 *     own getSlotItems, which a mod's enchantment may override;</li>
 * <li>the effect sums (protection, damage bonus) and the post-attack effects ask every enchantment
 *     object, as 1.20.6 did: a mod's its own overrides, a vanilla one 1.20.6's own class's body;</li>
 * <li>the vanilla-enchantment shortcuts (getMobLooting, hasSilkTouch ...) are 1.20.6's level reads of
 *     the vanilla enchantment of the same key;</li>
 * <li>enchanting a stack picks among the enchantments of the world's registry with 1.20.6's rules
 *     (feature flags, treasure, discoverable, canEnchant and isPrimaryItem, the cost window), 1.20.6's
 *     random draws and its compatibility test.</li>
 * </ul>
 * Each needs a world's enchantments (see CbEnchantments.current), as a 1.21 holder does.
 */
public final class CbEnchantmentHelper {

    private CbEnchantmentHelper() {
    }

    /** an item's enchantments as 1.20.6 iterated them: (enchantment object, level) of the ENCHANTMENTS component */
    private interface Visitor {
        void accept(class_1887 enchantment, int level);
    }

    private static void forEach(net.minecraft.class_1799 stack, Visitor visitor) {
        net.minecraft.class_9304 enchantments = stack.method_57825(net.minecraft.class_9334.field_49633, net.minecraft.class_9304.field_49385);
        for (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<net.minecraft.class_6880<net.minecraft.class_1887>> e : enchantments.method_57539()) {
            visitor.accept(CbEnchantments.cbEnchantment(e.getKey()), e.getIntValue());
        }
    }

    private static void forEach(Iterable<net.minecraft.class_1799> stacks, Visitor visitor) {
        for (net.minecraft.class_1799 stack : stacks) {
            forEach(stack, visitor);
        }
    }

    /** getItemEnchantmentLevel(Enchantment, ItemStack) */
    public static int method_8225(class_1887 enchantment, net.minecraft.class_1799 stack) {
        return net.minecraft.class_1890.method_8225(CbEnchantments.cbHolder(enchantment), stack);
    }

    /** getSweepingDamageRatio(int) */
    public static float method_58448(int level) {
        return 1.0F - 1.0F / (float) (level + 1);
    }

    /** getDamageProtection(Iterable&lt;ItemStack&gt;, DamageSource): the sum of every enchantment's getDamageProtection */
    public static int method_8219(Iterable<net.minecraft.class_1799> stacks, net.minecraft.class_1282 source) {
        int[] sum = {0};
        forEach(stacks, (e, level) -> sum[0] += e.method_8181(level, source));
        return sum[0];
    }

    /** getDamageBonus(ItemStack, EntityType): the sum of every enchantment's getDamageBonus */
    public static float method_8218(net.minecraft.class_1799 stack, net.minecraft.class_1299<?> type) {
        float[] sum = {0.0F};
        forEach(stack, (e, level) -> sum[0] += e.method_8196(level, type));
        return sum[0];
    }

    /** getSweepingDamageRatio(LivingEntity) */
    public static float method_8217(net.minecraft.class_1309 entity) {
        int level = method_8203(CbEnchVanilla.field_9115(), entity);
        return level > 0 ? method_58448(level) : 0.0F;
    }

    /** calculateArmorBreach(Entity, float) */
    public static float method_58676(net.minecraft.class_1297 entity, float armorEffectiveness) {
        if (entity instanceof net.minecraft.class_1309 living) {
            int level = method_8203(CbEnchVanilla.field_50158(), living);
            if (level > 0) {
                return top.dext.centurybridge.step.v1_21.facade.class_9467.method_58672((float) level, armorEffectiveness);
            }
        }
        return armorEffectiveness;
    }

    /** doPostHurtEffects(LivingEntity user, Entity attacker): every enchantment of the user's slots (and its main hand again when a player attacks) */
    public static void method_8210(net.minecraft.class_1309 user, net.minecraft.class_1297 attacker) {
        Visitor v = (e, level) -> e.method_8178(user, attacker, level);
        if (user != null) {
            forEach(user.method_56675(), v);
        }
        if (attacker instanceof net.minecraft.class_1657) {
            forEach(user.method_6047(), v);
        }
    }

    /** doPostDamageEffects(LivingEntity attacker, Entity target): every enchantment of the attacker's slots (and its main hand again when it is a player) */
    public static void method_8213(net.minecraft.class_1309 attacker, net.minecraft.class_1297 target) {
        Visitor v = (e, level) -> e.method_8189(attacker, target, level);
        if (attacker != null) {
            forEach(attacker.method_56675(), v);
        }
        if (attacker instanceof net.minecraft.class_1657) {
            forEach(attacker.method_6047(), v);
        }
    }

    /** doPostItemStackHurtEffects(LivingEntity attacker, Entity target, ItemEnchantments) */
    public static void method_58677(net.minecraft.class_1309 attacker, net.minecraft.class_1297 target, net.minecraft.class_9304 enchantments) {
        for (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<net.minecraft.class_6880<net.minecraft.class_1887>> e : enchantments.method_57539()) {
            CbEnchantments.cbEnchantment(e.getKey()).method_58675(attacker, target, e.getIntValue());
        }
    }

    /** getEnchantmentLevel(Enchantment, LivingEntity): the highest level over the enchantment's own slot items */
    public static int method_8203(class_1887 enchantment, net.minecraft.class_1309 entity) {
        Collection<net.minecraft.class_1799> items = enchantment.method_8185(entity).values();
        if (items == null) {
            return 0;
        }
        int best = 0;
        for (net.minecraft.class_1799 stack : items) {
            int level = method_8225(enchantment, stack);
            if (level > best) {
                best = level;
            }
        }
        return best;
    }

    /** getSneakingSpeedBonus(LivingEntity) */
    public static float method_42304(net.minecraft.class_1309 entity) {
        return (float) method_8203(CbEnchVanilla.field_38223(), entity) * 0.15F;
    }

    /** getKnockbackBonus(LivingEntity) */
    public static int method_8205(net.minecraft.class_1309 entity) {
        return method_8203(CbEnchVanilla.field_9121(), entity);
    }

    /** getFireAspect(LivingEntity) */
    public static int method_8199(net.minecraft.class_1309 entity) {
        return method_8203(CbEnchVanilla.field_9124(), entity);
    }

    /** getRespiration(LivingEntity) */
    public static int method_8211(net.minecraft.class_1309 entity) {
        return method_8203(CbEnchVanilla.field_9127(), entity);
    }

    /** getDepthStrider(LivingEntity) */
    public static int method_8232(net.minecraft.class_1309 entity) {
        return method_8203(CbEnchVanilla.field_9128(), entity);
    }

    /** getBlockEfficiency(LivingEntity) */
    public static int method_8234(net.minecraft.class_1309 entity) {
        return method_8203(CbEnchVanilla.field_9131(), entity);
    }

    /** getFishingLuckBonus(ItemStack) */
    public static int method_8223(net.minecraft.class_1799 stack) {
        return method_8225(CbEnchVanilla.field_9114(), stack);
    }

    /** getFishingSpeedBonus(ItemStack) */
    public static int method_8215(net.minecraft.class_1799 stack) {
        return method_8225(CbEnchVanilla.field_9100(), stack);
    }

    /** getMobLooting(LivingEntity) */
    public static int method_8226(net.minecraft.class_1309 entity) {
        return method_8203(CbEnchVanilla.field_9110(), entity);
    }

    /** hasAquaAffinity(LivingEntity) */
    public static boolean method_8200(net.minecraft.class_1309 entity) {
        return method_8203(CbEnchVanilla.field_9105(), entity) > 0;
    }

    /** hasFrostWalker(LivingEntity) */
    public static boolean method_8216(net.minecraft.class_1309 entity) {
        return method_8203(CbEnchVanilla.field_9122(), entity) > 0;
    }

    /** hasSoulSpeed(LivingEntity) */
    public static boolean method_25951(net.minecraft.class_1309 entity) {
        return method_8203(CbEnchVanilla.field_23071(), entity) > 0;
    }

    /** hasBindingCurse(ItemStack) */
    public static boolean method_8224(net.minecraft.class_1799 stack) {
        return method_8225(CbEnchVanilla.field_9113(), stack) > 0;
    }

    /** hasVanishingCurse(ItemStack) */
    public static boolean method_8221(net.minecraft.class_1799 stack) {
        return method_8225(CbEnchVanilla.field_9109(), stack) > 0;
    }

    /** hasSilkTouch(ItemStack) */
    public static boolean method_49189(net.minecraft.class_1799 stack) {
        return method_8225(CbEnchVanilla.field_9099(), stack) > 0;
    }

    /** getLoyalty(ItemStack) */
    public static int method_8206(net.minecraft.class_1799 stack) {
        return method_8225(CbEnchVanilla.field_9120(), stack);
    }

    /** getRiptide(ItemStack) */
    public static int method_8202(net.minecraft.class_1799 stack) {
        return method_8225(CbEnchVanilla.field_9104(), stack);
    }

    /** hasChanneling(ItemStack) */
    public static boolean method_8228(net.minecraft.class_1799 stack) {
        return method_8225(CbEnchVanilla.field_9117(), stack) > 0;
    }

    /** getRandomItemWith(Enchantment, LivingEntity) */
    public static Map.Entry<net.minecraft.class_1304, net.minecraft.class_1799> method_8204(class_1887 enchantment, net.minecraft.class_1309 entity) {
        return method_24365(enchantment, entity, stack -> true);
    }

    /** getRandomItemWith(Enchantment, LivingEntity, Predicate): a random slot item carrying the enchantment that passes the test */
    public static Map.Entry<net.minecraft.class_1304, net.minecraft.class_1799> method_24365(class_1887 enchantment, net.minecraft.class_1309 entity,
            Predicate<net.minecraft.class_1799> test) {
        Map<net.minecraft.class_1304, net.minecraft.class_1799> items = enchantment.method_8185(entity);
        if (items.isEmpty()) {
            return null;
        }
        List<Map.Entry<net.minecraft.class_1304, net.minecraft.class_1799>> found = new ArrayList<>();
        for (Map.Entry<net.minecraft.class_1304, net.minecraft.class_1799> e : items.entrySet()) {
            net.minecraft.class_1799 stack = e.getValue();
            if (!stack.method_7960() && method_8225(enchantment, stack) > 0 && test.test(stack)) {
                found.add(e);
            }
        }
        return found.isEmpty() ? null : found.get(entity.method_59922().method_43048(found.size()));
    }

    /** enchantItem(FeatureFlagSet, RandomSource, ItemStack, int, boolean) */
    public static net.minecraft.class_1799 method_8233(net.minecraft.class_7699 flags, net.minecraft.class_5819 random, net.minecraft.class_1799 stack,
            int level, boolean treasure) {
        List<net.minecraft.class_1889> picked = method_8230(flags, random, stack, level, treasure);
        if (stack.method_31574(net.minecraft.class_1802.field_8529)) {
            stack = new net.minecraft.class_1799(net.minecraft.class_1802.field_8598);
        }
        for (net.minecraft.class_1889 instance : picked) {
            stack.method_7978(instance.field_9093, instance.field_9094);
        }
        return stack;
    }

    /** selectEnchantment(FeatureFlagSet, RandomSource, ItemStack, int, boolean): 1.20.6's draws */
    public static List<net.minecraft.class_1889> method_8230(net.minecraft.class_7699 flags, net.minecraft.class_5819 random, net.minecraft.class_1799 stack,
            int level, boolean treasure) {
        List<net.minecraft.class_1889> picked = new ArrayList<>();
        int value = stack.method_7909().method_7837();
        if (value <= 0) {
            return picked;
        }
        level += 1 + random.method_43048(value / 4 + 1) + random.method_43048(value / 4 + 1);
        float spread = (random.method_43057() + random.method_43057() - 1.0F) * 0.15F;
        level = net.minecraft.class_3532.method_15340(Math.round((float) level + (float) level * spread), 1, Integer.MAX_VALUE);
        List<net.minecraft.class_1889> available = method_8229(flags, level, stack, treasure);
        if (!available.isEmpty()) {
            net.minecraft.class_6011.method_34986(random, available).ifPresent(picked::add);
            while (random.method_43048(50) <= level) {
                if (!picked.isEmpty()) {
                    filterCompatible(available, net.minecraft.class_156.method_20793(picked));
                }
                if (available.isEmpty()) {
                    break;
                }
                net.minecraft.class_6011.method_34986(random, available).ifPresent(picked::add);
                level /= 2;
            }
        }
        return picked;
    }

    /** 1.20.6's filterCompatibleEnchantments: drop what is not compatible with the last pick, by the objects' isCompatibleWith */
    private static void filterCompatible(List<net.minecraft.class_1889> available, net.minecraft.class_1889 last) {
        class_1887 lastEnchantment = CbEnchantments.cbEnchantment(last.field_9093);
        available.removeIf(i -> !lastEnchantment.method_8188(CbEnchantments.cbEnchantment(i.field_9093)));
    }

    /** isEnchantmentCompatible(Collection&lt;Holder&lt;Enchantment&gt;&gt;, Enchantment): every enchantment held is compatible with it */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static boolean method_8201(Collection<net.minecraft.class_6880<class_1887>> holders, class_1887 enchantment) {
        for (Object o : holders) {
            net.minecraft.class_6880 h = (net.minecraft.class_6880) o;
            Object v = h.comp_349();
            class_1887 held = v instanceof class_1887 legacy ? legacy : CbEnchantments.cbEnchantment(h);
            if (!held.method_8188(enchantment)) {
                return false;
            }
        }
        return true;
    }

    /**
     * getAvailableEnchantmentResults(FeatureFlagSet, int, ItemStack, boolean): for every enchantment of the
     * world (1.20.6 iterated BuiltInRegistries.ENCHANTMENT) enabled by the flags, not a treasure unless
     * treasure is allowed, discoverable, and for a book anything, else enchantable on the stack with the
     * stack a primary item: the highest level whose cost window holds the level.
     */
    public static List<net.minecraft.class_1889> method_8229(net.minecraft.class_7699 flags, int level, net.minecraft.class_1799 stack, boolean treasure) {
        List<net.minecraft.class_1889> out = new ArrayList<>();
        boolean book = stack.method_31574(net.minecraft.class_1802.field_8529);
        net.minecraft.class_2378<net.minecraft.class_1887> world = CbEnchantments.current();
        if (world == null) {
            throw new IllegalStateException("no world's enchantments are loaded: there are no enchantments to choose from");
        }
        java.util.Iterator<net.minecraft.class_6880.class_6883<net.minecraft.class_1887>> it = world.method_40270().iterator();
        while (it.hasNext()) {
            net.minecraft.class_6880.class_6883<net.minecraft.class_1887> holder = it.next();
            class_1887 e = CbEnchantments.facadeFor(holder.method_40237());
            if (!e.method_45382(flags) || e.method_8193() && !treasure || !e.method_25950()) {
                continue;
            }
            if (!book && !(e.method_8192(stack) && e.method_58444(stack))) {
                continue;
            }
            for (int i = e.method_8183(); i > e.method_8187() - 1; --i) {
                if (level >= e.method_8182(i) && level <= e.method_20742(i)) {
                    out.add(new net.minecraft.class_1889(holder, i));
                    break;
                }
            }
        }
        return out;
    }
}
