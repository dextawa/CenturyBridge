package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4's pathfinding API on 1.20.5. 1.20.4 asked a node evaluator for path types over a BlockGetter
 * (getBlockPathType(level, x, y, z[, mob]); the evaluator's region was its protected level field);
 * 1.20.5 hands a PathfindingContext (the region, the mob's position, a server-side cache of raw types) and
 * keeps the context instead of the region.
 *
 * A context made here from a 1.20.4 BlockGetter is {@link Context}: the getter (a collision view of it
 * when it is not one already), the mob's position, and no cache -- 1.20.4 computed every type afresh,
 * and the server's cache holds the types of the world's own blocks, which a mod's getter need not be.
 */
public final class CbRestPath {

    /** NodeEvaluator's state 1.20.4 code reached through fields and protected methods (CbRestNodeEvaluatorMixin) */
    public interface Evaluator {
        net.minecraft.class_9316 cbRest$context();

        void cbRest$setContext(net.minecraft.class_9316 context);

        net.minecraft.class_1308 cbRest$mob();

        net.minecraft.class_9 cbRest$node(int x, int y, int z);

        int cbRest$entityWidth();
    }

    /** 1.20.4 NodeEvaluator's BlockGetter-shaped queries (CbRestNodeEvaluatorMixin's covers; a mod evaluator overrides them) */
    public interface LegacyGetter {
        net.minecraft.class_7 method_17(net.minecraft.class_1922 level, int x, int y, int z);

        net.minecraft.class_7 method_25(net.minecraft.class_1922 level, int x, int y, int z, net.minecraft.class_1308 mob);
    }

    /** 1.20.4 WalkNodeEvaluator.evaluateBlockPathType(level, pos, type) */
    public interface LegacyEvaluate {
        net.minecraft.class_7 method_61(net.minecraft.class_1922 level, net.minecraft.class_2338 pos, net.minecraft.class_7 type);
    }

    /** 1.20.4 WalkNodeEvaluator.getBlockPathTypes(level, x, y, z, types, current, mobPos) */
    public interface LegacyBoxTypes {
        net.minecraft.class_7 method_64(net.minecraft.class_1922 level, int x, int y, int z, java.util.EnumSet<net.minecraft.class_7> types,
                                        net.minecraft.class_7 current, net.minecraft.class_2338 mobPos);
    }

    /** 1.20.4 WalkNodeEvaluator.getBlockPathType(mob, pos) */
    public interface LegacyStartType {
        net.minecraft.class_7 method_63(net.minecraft.class_1308 mob, net.minecraft.class_2338 pos);
    }

    /** 1.20.4 WalkNodeEvaluator.getCachedBlockType(mob, x, y, z) */
    public interface LegacyCached {
        net.minecraft.class_7 method_29303(net.minecraft.class_1308 mob, int x, int y, int z);
    }

    /** 1.20.4 WalkNodeEvaluator.isDiagonalValid(node, a, b, diagonal) */
    public interface LegacyDiagonal {
        boolean method_29579(net.minecraft.class_9 node, net.minecraft.class_9 a, net.minecraft.class_9 b, net.minecraft.class_9 diagonal);
    }

    /** every 1.20.4 WalkNodeEvaluator member above, and 1.20.5's protected getCachedPathType (CbRestWalkNodeEvaluatorMixin) */
    public interface LegacyWalk extends LegacyEvaluate, LegacyBoxTypes, LegacyStartType, LegacyCached, LegacyDiagonal {
        net.minecraft.class_7 cbRest$cachedPathType(int x, int y, int z);
    }

    /**
     * Added to a mod WalkNodeEvaluator class that overrides 1.20.4's getBlockPathType(mob, pos) (adapter
     * hook; the cover is class_14's own)
     */
    public interface OwnStartType {
        net.minecraft.class_7 method_63(net.minecraft.class_1308 mob, net.minecraft.class_2338 pos);
    }

    /** canStartAt / getStartNode's type: 1.20.4's getBlockPathType(mob, pos) where the class overrides it, else 1.20.5's cached type */
    public static net.minecraft.class_7 startType(net.minecraft.class_14 self, int x, int y, int z) {
        if (self instanceof OwnStartType own) {
            return own.method_63(((Evaluator) self).cbRest$mob(), new net.minecraft.class_2338(x, y, z));
        }
        return ((LegacyWalk) self).cbRest$cachedPathType(x, y, z);
    }

    /** getCachedPathType(x, y, z) of a class overriding 1.20.4's getCachedBlockType(mob, x, y, z): the evaluator's mob */
    public static net.minecraft.class_7 cachedType(LegacyCached self, int x, int y, int z) {
        return self.method_29303(((Evaluator) self).cbRest$mob(), x, y, z);
    }

    /** getPathTypeWithinMobBB of a class overriding 1.20.4's getBlockPathTypes: its types for the mob's box */
    public static java.util.Set<net.minecraft.class_7> withinMobBB(LegacyBoxTypes self, net.minecraft.class_9316 context, int x, int y, int z) {
        java.util.EnumSet<net.minecraft.class_7> types = java.util.EnumSet.noneOf(net.minecraft.class_7.class);
        net.minecraft.class_1308 mob = ((Evaluator) self).cbRest$mob();
        self.method_64(level(context), x, y, z, types, net.minecraft.class_7.field_22, mob.method_24515());
        return types;
    }

    /** the same for a class overriding 1.20.4's evaluateBlockPathType (getBlockPathTypes calls it) */
    public static java.util.Set<net.minecraft.class_7> withinMobBB(LegacyEvaluate self, net.minecraft.class_9316 context, int x, int y, int z) {
        return withinMobBB((LegacyBoxTypes) self, context, x, y, z);
    }

    /**
     * getPathTypeOfMob of a class overriding 1.20.4's getBlockPathTypes / evaluateBlockPathType: 1.20.4's
     * getBlockPathType(level, x, y, z, mob) body over them -- fences and impassable rails first, then the
     * type of most malus (a negative one at once), open when the box's first cell is open, the best type
     * costs nothing and the mob is at most one block wide
     */
    public static net.minecraft.class_7 typeOfMob(LegacyBoxTypes self, net.minecraft.class_9316 context, int x, int y, int z,
                                                net.minecraft.class_1308 mob) {
        java.util.EnumSet<net.minecraft.class_7> types = java.util.EnumSet.noneOf(net.minecraft.class_7.class);
        net.minecraft.class_7 current = self.method_64(level(context), x, y, z, types, net.minecraft.class_7.field_22, mob.method_24515());
        if (types.contains(net.minecraft.class_7.field_10)) {
            return net.minecraft.class_7.field_10;
        }
        if (types.contains(net.minecraft.class_7.field_25418)) {
            return net.minecraft.class_7.field_25418;
        }
        net.minecraft.class_7 best = net.minecraft.class_7.field_22;
        for (net.minecraft.class_7 type : types) {
            if (mob.method_5944(type) < 0.0F) {
                return type;
            }
            if (mob.method_5944(type) >= mob.method_5944(best)) {
                best = type;
            }
        }
        if (current == net.minecraft.class_7.field_7 && mob.method_5944(best) == 0.0F && ((Evaluator) self).cbRest$entityWidth() <= 1) {
            return net.minecraft.class_7.field_7;
        }
        return best;
    }

    /** the same for a class overriding 1.20.4's evaluateBlockPathType */
    public static net.minecraft.class_7 typeOfMob(LegacyEvaluate self, net.minecraft.class_9316 context, int x, int y, int z,
                                                net.minecraft.class_1308 mob) {
        return typeOfMob((LegacyBoxTypes) self, context, x, y, z, mob);
    }

    /** the node and neighbours 1.20.5's three-node isDiagonalValid was asked about, per evaluator, for the one-node check after */
    private static final ThreadLocal<java.util.Map<Object, net.minecraft.class_9[]>> DIAGONAL =
        ThreadLocal.withInitial(java.util.IdentityHashMap::new);

    /**
     * isDiagonalValid(node, a, b) of a class overriding 1.20.4's four-node isDiagonalValid: true -- 1.20.4
     * found the diagonal node first and judged all four together, which the one-node check after does
     */
    public static boolean diagonalStart(LegacyDiagonal self, net.minecraft.class_9 node, net.minecraft.class_9 a, net.minecraft.class_9 b) {
        DIAGONAL.get().put(self, new net.minecraft.class_9[] {node, a, b});
        return true;
    }

    /**
     * isDiagonalValid(diagonal) of such a class: 1.20.4's isDiagonalValid(node, x-neighbour, z-neighbour,
     * diagonal), with the node and neighbours of the three-node call just before
     */
    public static boolean diagonalEnd(LegacyDiagonal self, net.minecraft.class_9 diagonal) {
        net.minecraft.class_9[] s = DIAGONAL.get().remove(self);
        if (s == null) {
            return false;
        }
        net.minecraft.class_9 node = s[0];
        net.minecraft.class_9 a = s[1];
        net.minecraft.class_9 b = s[2];
        if (a != null && a.field_40 == node.field_40) { // 1.20.4 passed the neighbour along x first, then the one along z
            net.minecraft.class_9 t = a;
            a = b;
            b = t;
        }
        return self.method_29579(node, a, b, diagonal);
    }

    /** 1.20.4 NodeEvaluator.getTargetFromNode(node), the shape a mod's evaluator overrides */
    public interface LegacyTarget {
        net.minecraft.class_4459 method_43568(net.minecraft.class_9 node);
    }

    /** a context over a 1.20.4 getter (see the class comment) */
    public static final class Context extends net.minecraft.class_9316 {
        private final net.minecraft.class_2338.class_2339 at = new net.minecraft.class_2338.class_2339();

        public Context(net.minecraft.class_1941 level, net.minecraft.class_1308 mob) {
            super(level, mob);
        }

        @Override
        public net.minecraft.class_7 method_57622(int x, int y, int z) {
            return top.dext.centurybridge.step.v1_20_5.mixin.CbRestPathInvoker.cbRest$pathTypeFromState(this.method_57621(), this.at.method_10103(x, y, z));
        }
    }

    /** the context 1.20.4's (getter, mob) stands for; null for a null getter */
    public static net.minecraft.class_9316 context(net.minecraft.class_1922 level, net.minecraft.class_1308 mob) {
        return level == null ? null : new Context(collisionView(level), mob);
    }

    /** the context of a 1.20.4 getter alone (1.20.4's static path type queries had no mob) */
    public static net.minecraft.class_9316 context(net.minecraft.class_1922 level) {
        return context(level, null);
    }

    /** a 1.20.5 context's getter, the 1.20.4 shape's BlockGetter (a mod evaluator's override hook) */
    public static net.minecraft.class_1922 level(net.minecraft.class_9316 context) {
        if (context == null) {
            return null;
        }
        net.minecraft.class_1941 level = context.method_57621();
        return level instanceof BlockView view ? view.getter : level;
    }

    /** 1.20.4 NodeEvaluator.level (the region it was prepared with): the region of its context, else null */
    public static net.minecraft.class_1950 field_20622(net.minecraft.class_8 evaluator) {
        net.minecraft.class_9316 context = ((Evaluator) evaluator).cbRest$context();
        return context != null && context.method_57621() instanceof net.minecraft.class_1950 region ? region : null;
    }

    /** a 1.20.4 write of NodeEvaluator.level: the evaluator's context made of that region, as 1.20.5's prepare makes it */
    public static void field_20622(net.minecraft.class_8 evaluator, net.minecraft.class_1950 region) {
        Evaluator e = (Evaluator) evaluator;
        e.cbRest$setContext(region == null ? null : new net.minecraft.class_9316(region, e.cbRest$mob()));
    }

    /** 1.20.4 WalkNodeEvaluator.getBlockPathTypeStatic(level, pos) */
    public static net.minecraft.class_9316 staticContext(net.minecraft.class_1922 level) {
        return context(level);
    }

    /**
     * 1.20.4 WalkNodeEvaluator.checkNeighbourBlocks(level, pos, type): 1.20.5's over the position's
     * coordinates. (1.20.4 moved the mutable position through the neighbours; its own callers set it again
     * after, 1.20.5's leaves it where it was.)
     */
    public static net.minecraft.class_7 method_59(net.minecraft.class_1922 level, net.minecraft.class_2338.class_2339 pos,
                                                net.minecraft.class_7 type) {
        return net.minecraft.class_14.method_59(context(level), pos.method_10263(), pos.method_10264(), pos.method_10260(), type);
    }

    /** getTargetNodeAt(x, y, z) of a mod evaluator overriding 1.20.4's getTargetFromNode(node): the node there, its target */
    public static net.minecraft.class_4459 targetAt(LegacyTarget self, double x, double y, double z) {
        return self.method_43568(((Evaluator) self).cbRest$node(net.minecraft.class_3532.method_15357(x),
            net.minecraft.class_3532.method_15357(y), net.minecraft.class_3532.method_15357(z)));
    }

    /** 1.20.4 NodeEvaluator.getTargetFromNode(node) as a super call: the node's target */
    public static net.minecraft.class_4459 superTarget(net.minecraft.class_8 evaluator, net.minecraft.class_9 node) {
        return new net.minecraft.class_4459(node);
    }

    /**
     * a BlockGetter 1.20.4 handed over that is no collision view: its blocks, fluids, block entities and
     * height; the collision queries a path type never makes are refused
     */
    static final class BlockView implements net.minecraft.class_1941 {
        final net.minecraft.class_1922 getter;

        BlockView(net.minecraft.class_1922 getter) {
            this.getter = getter;
        }

        @Override
        public net.minecraft.class_2586 method_8321(net.minecraft.class_2338 pos) {
            return this.getter.method_8321(pos);
        }

        @Override
        public net.minecraft.class_2680 method_8320(net.minecraft.class_2338 pos) {
            return this.getter.method_8320(pos);
        }

        @Override
        public net.minecraft.class_3610 method_8316(net.minecraft.class_2338 pos) {
            return this.getter.method_8316(pos);
        }

        @Override
        public int method_31605() {
            return this.getter.method_31605();
        }

        @Override
        public int method_31607() {
            return this.getter.method_31607();
        }

        @Override
        public net.minecraft.class_2784 method_8621() {
            throw new UnsupportedOperationException("a 1.20.4 BlockGetter has no world border");
        }

        @Override
        public net.minecraft.class_1922 method_22338(int x, int z) {
            throw new UnsupportedOperationException("a 1.20.4 BlockGetter has no chunks");
        }

        @Override
        public java.util.List<net.minecraft.class_265> method_20743(net.minecraft.class_1297 entity, net.minecraft.class_238 box) {
            throw new UnsupportedOperationException("a 1.20.4 BlockGetter has no entities");
        }
    }

    private static net.minecraft.class_1941 collisionView(net.minecraft.class_1922 level) {
        return level instanceof net.minecraft.class_1941 view ? view : new BlockView(level);
    }

    private CbRestPath() {
    }
}
