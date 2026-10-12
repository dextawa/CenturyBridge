package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4 container and named block entity API on 1.20.5.
 *
 * <p>BaseContainerBlockEntity: 1.20.5 implements the Container methods itself on two new abstract
 * ones, getItems() / setItems(list), which 1.20.5 also uses to move a container's items to and from
 * the item stack's CONTAINER component. A 1.20.4 subclass kept its items behind its own Container
 * methods (getContainerSize / getItem / setItem): getItems() is a list that reads and writes
 * through them, setItems(list) sets each slot (abstractFills).
 *
 * <p>setCustomName(name): 1.20.4's BaseContainerBlockEntity and BannerBlockEntity set their name
 * through it, and placing a custom-named item called it; 1.20.5 dropped it and assigns the name
 * from the CUSTOM_NAME component in applyImplicitComponents. A mod's call sets the name (through the
 * mod's override where its class has one); 1.20.5's assignment of a name goes through that
 * override (CbBlockNameMixin) as the placement's setCustomName did in 1.20.4.
 */
public final class CbBlockContainers {

    /** the name field of BaseContainerBlockEntity / BannerBlockEntity (CbBlockNameMixin's accessors) */
    public interface Named {
        void cb$setName(net.minecraft.class_2561 name);
    }

    /** 1.20.4's setCustomName(name) a mod block entity class overrides */
    public interface LegacyCustomName {
        void method_17488(net.minecraft.class_2561 name);
    }

    /** 1.20.4's BannerBlockEntity.setCustomName(name) a mod banner class overrides */
    public interface LegacyBannerName {
        void method_16842(net.minecraft.class_2561 name);
    }

    /** the slots of a 1.20.4 container as the NonNullList 1.20.5 asks getItems() for */
    public static final class Slots extends net.minecraft.class_2371<net.minecraft.class_1799> {
        Slots(net.minecraft.class_1263 container) {
            super(new java.util.AbstractList<net.minecraft.class_1799>() {
                @Override
                public net.minecraft.class_1799 get(int i) {
                    return container.method_5438(i);
                }

                @Override
                public net.minecraft.class_1799 set(int i, net.minecraft.class_1799 stack) {
                    net.minecraft.class_1799 previous = container.method_5438(i);
                    container.method_5447(i, stack);
                    return previous;
                }

                @Override
                public int size() {
                    return container.method_5439();
                }
            }, net.minecraft.class_1799.field_8037);
        }
    }

    /** getItems() of a 1.20.4 BaseContainerBlockEntity subclass (abstractFills) */
    public static net.minecraft.class_2371<net.minecraft.class_1799> getItems(net.minecraft.class_2624 self) {
        return new Slots(self);
    }

    /** setItems(list) of such a subclass: each slot set through its own setItem */
    public static void setItems(net.minecraft.class_2624 self, net.minecraft.class_2371<net.minecraft.class_1799> items) {
        int n = Math.min(items.size(), self.method_5439());
        for (int i = 0; i < n; i++) {
            self.method_5447(i, items.get(i));
        }
    }

    /** BaseContainerBlockEntity.setCustomName(name) called on any container */
    public static void method_17488(net.minecraft.class_2624 self, net.minecraft.class_2561 name) {
        if (self instanceof LegacyCustomName legacy) {
            legacy.method_17488(name);
        } else {
            ((Named) self).cb$setName(name);
        }
    }

    /** 1.20.4's BaseContainerBlockEntity.setCustomName body (a mod's super call) */
    public static void superSetCustomName(net.minecraft.class_2624 self, net.minecraft.class_2561 name) {
        ((Named) self).cb$setName(name);
    }

    /** BannerBlockEntity.setCustomName(name) called on any banner */
    public static void method_16842(net.minecraft.class_2573 self, net.minecraft.class_2561 name) {
        if (self instanceof LegacyBannerName legacy) {
            legacy.method_16842(name);
        } else {
            ((Named) self).cb$setName(name);
        }
    }

    /** 1.20.4's BannerBlockEntity.setCustomName body (a mod's super call) */
    public static void superSetBannerName(net.minecraft.class_2573 self, net.minecraft.class_2561 name) {
        ((Named) self).cb$setName(name);
    }

    private CbBlockContainers() {
    }
}
