package top.dext.centurybridge.step.v1_20_3.rt;

/**
 * Answers of 1.20.2 methods whose code 1.20.3 keeps in a method answering another type (mixinTargets), turned into each
 * other for a mod's @Inject handler moved there (returnConverters): the handler reads and sets 1.20.2's answer.
 * <p>
 * Blocks' private factories (leaves, bed, pistonBase, stainedGlass, woodenButton, candleCake) answered the block class
 * they make; 1.20.3 declares them answering Block, and each still makes exactly that class (javap: one NEW of it,
 * returned). 1.20.2's answer is the same object, seen as its own class; 1.20.3's is that object.
 */
public final class CbMixinReturns {

    private CbMixinReturns() {
    }

    public static net.minecraft.class_2397 leavesV(net.minecraft.class_2248 b) {
        return (net.minecraft.class_2397) b;
    }

    public static net.minecraft.class_2248 leavesW(net.minecraft.class_2397 b) {
        return b;
    }

    public static net.minecraft.class_2244 bedV(net.minecraft.class_2248 b) {
        return (net.minecraft.class_2244) b;
    }

    public static net.minecraft.class_2248 bedW(net.minecraft.class_2244 b) {
        return b;
    }

    public static net.minecraft.class_2665 pistonV(net.minecraft.class_2248 b) {
        return (net.minecraft.class_2665) b;
    }

    public static net.minecraft.class_2248 pistonW(net.minecraft.class_2665 b) {
        return b;
    }

    public static net.minecraft.class_2506 stainedGlassV(net.minecraft.class_2248 b) {
        return (net.minecraft.class_2506) b;
    }

    public static net.minecraft.class_2248 stainedGlassW(net.minecraft.class_2506 b) {
        return b;
    }

    public static net.minecraft.class_2269 buttonV(net.minecraft.class_2248 b) {
        return (net.minecraft.class_2269) b;
    }

    public static net.minecraft.class_2248 buttonW(net.minecraft.class_2269 b) {
        return b;
    }

    public static net.minecraft.class_5544 candleCakeV(net.minecraft.class_2248 b) {
        return (net.minecraft.class_5544) b;
    }

    public static net.minecraft.class_2248 candleCakeW(net.minecraft.class_5544 b) {
        return b;
    }

    /**
     * ScoreContents.findTargetName(source): 1.20.2 answered the target's scoreboard name, 1.20.3 the ScoreHolder (an
     * entity, or ScoreHolder.forNameOnly(name)); a holder's scoreboard name is 1.20.2's answer, and a name stands for the
     * name-only holder 1.20.3 makes of it.
     */
    public static String scoreNameV(net.minecraft.class_9015 holder) {
        return holder == null ? null : holder.method_5820();
    }

    public static net.minecraft.class_9015 scoreNameW(String name) {
        return name == null ? null : net.minecraft.class_9015.method_55422(name);
    }

    /**
     * SignedMessageValidator.KeyBased.updateAndValidate(message): 1.20.2 answered whether the message was valid, 1.20.3
     * the message or null (javap: the same test, iconst_0 / iconst_1 became null / the argument).
     */
    public static boolean acceptedV(net.minecraft.class_7471 accepted, net.minecraft.class_7471 entry) {
        return accepted != null;
    }

    public static net.minecraft.class_7471 acceptedW(boolean accepted, net.minecraft.class_7471 entry) {
        return accepted ? entry : null;
    }
}
