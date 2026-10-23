package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4 Entity.defineSynchedData() outside Entity's constructor: a 1.20.4 mod calling it on an
 * entity (the redirected call), or a 1.20.4 defineSynchedData() body entered directly -- the mod's
 * own call of its override, not Entity's constructor through the synthesized defineSynchedData(Builder)
 * -- whose super.defineSynchedData() is rewritten to 1.20.5's super.defineSynchedData(builder).
 *
 * 1.20.4 ran the entity's defines against the entity's data object, define by define: an id defined
 * already threw "Duplicate id value for N!" (so a second run of an entity's own chain threw at its
 * first define), an id not defined yet was added. 1.20.5 defines into a builder before the data
 * object exists, and its data object is fixed once built. The builder handed to the defines here is
 * {@link Redefine}: each define on it is 1.20.4's define on the entity's data object, in the order
 * the defines run -- the vanilla chain's builder.define(..) calls and a 1.20.4 body's own
 * entityData.define(..) calls alike reach that one data object, as in 1.20.4. 1.20.4's define there is
 * the step's SynchedEntityData bridge (area s4net: CbNetEntityDataAccess, with 1.20.4's checks and
 * messages, growing the data object for a new id).
 */
public final class CbRestEntityData {

    /** 1.20.4 Entity.defineSynchedData() called on an entity: its defines, each 1.20.4's define on its data */
    public static void method_5693(net.minecraft.class_1297 entity) {
        ((CbRestEntityApi) entity).cbRest$defineSynchedData(redefining(entity));
    }

    /**
     * the builder for an entity's defines outside its constructor (also the hook's frame default: what a
     * directly entered 1.20.4 body's super.defineSynchedData() hands vanilla's defines)
     */
    public static net.minecraft.class_2945.class_9222 redefining(net.minecraft.class_1297 entity) {
        return new Redefine(entity);
    }

    /** a builder whose every define is 1.20.4's define on the entity's (built) data object */
    static final class Redefine extends net.minecraft.class_2945.class_9222 {

        private final net.minecraft.class_1297 entity;

        Redefine(net.minecraft.class_1297 entity) {
            super(entity);
            this.entity = entity;
        }

        @Override
        public <T> net.minecraft.class_2945.class_9222 method_56912(net.minecraft.class_2940<T> accessor, T value) {
            ((CbNetEntityDataAccess) (Object) this.entity.method_5841()).method_12784(accessor, value);
            return this;
        }
    }

    private CbRestEntityData() {
    }
}
