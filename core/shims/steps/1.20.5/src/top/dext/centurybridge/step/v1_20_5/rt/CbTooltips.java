package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4's tooltip level on 1.20.5: Block.appendHoverText(stack, BlockGetter, tooltip, flag) and
 * Item.appendHoverText(stack, Level, tooltip, flag) became (stack, Item.TooltipContext, tooltip,
 * flag). Shared by every area of this step.
 *
 * <p>Level or BlockGetter -> context: null is TooltipContext.EMPTY (1.20.5's "no level": no
 * registries, tick rate 20, no map data; 1.20.4 passed a null level there, the creative search
 * tooltips for one); a Level is TooltipContext.of(level), the context 1.20.5 itself builds for a
 * level; any other BlockGetter -- which no 1.20.5 context can carry -- is a {@link GetterContext}
 * that keeps it, answering registries and map data from it where it can.
 *
 * <p>Context -> Level or BlockGetter, for a mod's 1.20.4 override: a {@link GetterContext} gives
 * back its getter; TooltipContext.of(level) is the anonymous class Item$TooltipContext$2
 * (class_1792$class_9635$2), whose only state is the level in its package-private field_51354, read
 * through the CbTooltipLevelAccessor mixin; any other context (EMPTY, of(registries)) has no level:
 * null, what 1.20.4 passed for "no level". The accessor names an anonymous class: a later release
 * that renumbers it shows up as a missing class in that step's damage table when this layer climbs.
 */
public final class CbTooltips {

    /** the level of TooltipContext.of(level), through the CbTooltipLevelAccessor mixin */
    public interface LevelContext {
        net.minecraft.class_1937 cb$level();
    }

    /** a context carrying a BlockGetter that is not a Level */
    public static final class GetterContext implements net.minecraft.class_1792.class_9635 {
        private final net.minecraft.class_1922 getter;

        public GetterContext(net.minecraft.class_1922 getter) {
            this.getter = getter;
        }

        public net.minecraft.class_1922 getter() {
            return getter;
        }

        /** a level reader's own registries, else the running world's */
        @Override
        public net.minecraft.class_7225.class_7874 method_59527() {
            return getter instanceof net.minecraft.class_4538 reader ? reader.method_30349() : CbRegistries.current();
        }

        /** the default tick rate, as TooltipContext.EMPTY answers: only a Level has a tick rate manager */
        @Override
        public float method_59531() {
            return 20.0F;
        }

        /** map data lives in a level: none */
        @Override
        public net.minecraft.class_22 method_59529(net.minecraft.class_9209 id) {
            return null;
        }
    }

    /** Level -> context (Item.appendHoverText) */
    public static net.minecraft.class_1792.class_9635 fromLevel(net.minecraft.class_1937 level) {
        return net.minecraft.class_1792.class_9635.method_59528(level); // of(null) is EMPTY
    }

    /** BlockGetter -> context (Block.appendHoverText) */
    public static net.minecraft.class_1792.class_9635 fromBlockGetter(net.minecraft.class_1922 getter) {
        if (getter == null) {
            return net.minecraft.class_1792.class_9635.field_51353;
        }
        return getter instanceof net.minecraft.class_1937 level
            ? net.minecraft.class_1792.class_9635.method_59528(level) : new GetterContext(getter);
    }

    /** context -> Level, or null when it has none */
    public static net.minecraft.class_1937 level(net.minecraft.class_1792.class_9635 context) {
        if (context instanceof LevelContext lc) {
            return lc.cb$level();
        }
        return context instanceof GetterContext gc && gc.getter() instanceof net.minecraft.class_1937 level ? level : null;
    }

    /** context -> BlockGetter, or null when it has none */
    public static net.minecraft.class_1922 blockGetter(net.minecraft.class_1792.class_9635 context) {
        if (context instanceof GetterContext gc) {
            return gc.getter();
        }
        return context instanceof LevelContext lc ? lc.cb$level() : null;
    }

    private CbTooltips() {
    }
}
