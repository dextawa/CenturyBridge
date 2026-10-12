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
 * entityData.define(..) calls alike reach that one data object, as in 1.20.4.
 */
public final class CbRestEntityData {

    /** the entity's data items (CbRestEntityDataAccessor) */
    public interface Items {
        net.minecraft.class_2945.class_2946<?>[] cbRest$items();
    }

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
            define(this.entity.method_5841(), accessor, value);
            return this;
        }
    }

    private static volatile java.lang.reflect.Method define;
    private static volatile boolean looked;

    /**
     * 1.20.4's define(accessor, value) on a data object: the step's SynchedEntityData.define bridge (area
     * s4net), a public member it adds to SynchedEntityData, which makes 1.20.4's checks and fills the
     * slot. Where that bridge is not there, 1.20.4's checks are made here in its order and with its
     * messages, and an id that passes them is refused: 1.20.5's data object cannot take a new one.
     */
    private static void define(net.minecraft.class_2945 data, net.minecraft.class_2940<?> accessor, Object value) {
        java.lang.reflect.Method m = bridge();
        if (m != null) {
            try {
                m.invoke(data, accessor, value);
                return;
            } catch (java.lang.reflect.InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof RuntimeException r) {
                    throw r;
                }
                if (cause instanceof Error err) {
                    throw err;
                }
                throw new RuntimeException(cause);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
        int id = accessor.comp_2327();
        if (id > 254) {
            throw new IllegalArgumentException("Data value id is too big with " + id + "! (Max is 254)");
        }
        net.minecraft.class_2945.class_2946<?>[] items = ((Items) data).cbRest$items();
        if (id < items.length && items[id] != null) {
            throw new IllegalArgumentException("Duplicate id value for " + id + "!");
        }
        if (net.minecraft.class_2943.method_12719(accessor.comp_2328()) < 0) {
            throw new IllegalArgumentException("Unregistered serializer " + accessor.comp_2328() + " for " + id + "!");
        }
        throw new UnsupportedOperationException("1.20.5 fixes an entity's tracked data when it is built; 1.20.4 code defined id "
            + id + " later, which needs the step's SynchedEntityData.define bridge");
    }

    private static java.lang.reflect.Method bridge() {
        if (!looked) {
            try {
                define = net.minecraft.class_2945.class.getMethod("method_12784", net.minecraft.class_2940.class, Object.class);
            } catch (NoSuchMethodException e) {
                define = null;
            }
            looked = true;
        }
        return define;
    }

    private CbRestEntityData() {
    }
}
