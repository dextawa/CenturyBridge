package top.dext.centurybridge.step.v1_20_5.rt;

import java.util.List;
import java.util.Map;

/**
 * 1.20.4's PotionBrewing statics on 1.20.5's brewing. 1.20.4 kept one brewing table for the whole game,
 * filled at bootstrap, and asked it through statics; 1.20.5 builds a table per world from its enabled
 * features (the server's, a client's from its connection) and asks the table. A 1.20.4 static asks the
 * running world's table -- the client's on its render thread, else the server's, else the client's --
 * and with no world the table 1.20.5 builds for vanilla's default features (1.20.4 had no
 * feature-gated brewing; the default features are 1.20.4's game). The answers are the table's own:
 * the brewing 1.20.5's brewing stand does, including what other mods added to it. One difference is
 * 1.20.5's own: it never brews a stack that holds no potion (1.20.4 turned a potion-less bottle into a
 * potion-less splash or lingering one with gunpowder or dragon's breath).
 */
public final class CbBrewing {

    private static final Map<net.minecraft.class_7699, net.minecraft.class_1845> FALLBACK =
        new java.util.concurrent.ConcurrentHashMap<>();

    /** the brewing table of the running world (see the class comment) */
    public static net.minecraft.class_1845 current() {
        net.minecraft.class_1937 level = CbRegistries.clientLevelOnThread();
        if (level != null) {
            return level.method_59547();
        }
        net.minecraft.server.MinecraftServer server = CbRegistries.server();
        if (server != null) {
            return server.method_59777();
        }
        level = CbRegistries.clientLevel();
        if (level != null) {
            return level.method_59547();
        }
        return fallback();
    }

    /** the table 1.20.5 builds for the default features, made once */
    private static net.minecraft.class_1845 fallback() {
        return FALLBACK.computeIfAbsent(net.minecraft.class_7701.field_40183, net.minecraft.class_1845::method_8076);
    }

    public static boolean isIngredient(net.minecraft.class_1799 stack) {
        return current().method_8077(stack);
    }

    public static boolean isContainerIngredient(net.minecraft.class_1799 stack) {
        return current().method_8079(stack);
    }

    public static boolean isPotionIngredient(net.minecraft.class_1799 stack) {
        return current().method_8069(stack);
    }

    /** isBrewablePotion(potion): the potion is what some mix brews (the empty potion never is) */
    public static boolean isBrewablePotion(net.minecraft.class_1842 potion) {
        return current().method_20361(CbHolders.potion(potion));
    }

    public static boolean hasMix(net.minecraft.class_1799 input, net.minecraft.class_1799 ingredient) {
        return current().method_8072(input, ingredient);
    }

    public static boolean hasContainerMix(net.minecraft.class_1799 input, net.minecraft.class_1799 ingredient) {
        return current().method_8070(input, ingredient);
    }

    public static boolean hasPotionMix(net.minecraft.class_1799 input, net.minecraft.class_1799 ingredient) {
        return current().method_8075(input, ingredient);
    }

    /** mix(ingredient, potion): what the table brews */
    public static net.minecraft.class_1799 mix(net.minecraft.class_1799 ingredient, net.minecraft.class_1799 potion) {
        return current().method_8078(ingredient, potion);
    }

    /**
     * bootStrap(): 1.20.4 filled its one table with vanilla's mixes (Bootstrap did, before any mod could ask).
     * Every 1.20.5 table is filled when it is built, so calling it again added nothing a 1.20.4 call could
     * observe -- the fallback table is made here, the tables of worlds by 1.20.5.
     */
    public static void bootStrap() {
        fallback();
    }

    /** the empty mix lists a 1.20.4 PotionBrewing subclass's instance holds (1.20.4's instances held nothing) */
    @SuppressWarnings("rawtypes")
    public static List emptyList() {
        return List.of();
    }

    private CbBrewing() {
    }
}
