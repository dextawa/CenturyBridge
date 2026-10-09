package top.dext.centurybridge.facade.v1_21_4;

/**
 * Facade for 1.20.1 net/minecraft/class_2368 (a trivial AbstractGlassBlock subclass:
 * only a (Properties) constructor, all behaviour inherited), deleted at 1.20.3.
 * Corpus glass mods subclass it (ConnectedGlassBlock, FrostyGlassBlock, ...); some
 * also {@code new class_2368(props)}. Extends the class_4262 facade (hence the live
 * HalfTransparentBlock), concrete so both direct new and subclass extends resolve.
 * Reached via superRenames (extends) -- a ctorRedirect is added only if a corpus
 * mod is found to new it directly. Member names are intermediary tokens.
 */
public class class_2368 extends class_4262 {

    public class_2368(net.minecraft.class_4970.class_2251 properties) {
        super(properties);
    }
}
