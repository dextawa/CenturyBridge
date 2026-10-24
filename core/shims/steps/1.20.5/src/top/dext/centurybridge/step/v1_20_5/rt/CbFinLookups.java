package top.dext.centurybridge.step.v1_20_5.rt;

import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.class_2378;
import net.minecraft.class_5321;
import net.minecraft.class_5455;
import net.minecraft.class_6862;
import net.minecraft.class_6880;
import net.minecraft.class_6885;
import net.minecraft.class_7157;
import net.minecraft.class_7225;
import net.minecraft.class_7699;
import top.dext.centurybridge.step.v1_20_5.facade.CommandBuildContextConfigurable;
import top.dext.centurybridge.step.v1_20_5.facade.CommandBuildContextPolicy;
import top.dext.centurybridge.step.v1_20_5.facade.HolderLookupDelegate;

/**
 * CommandBuildContext and HolderLookup between 1.20.4 and 1.20.5 (area s4fin).
 *
 * 1.20.4's CommandBuildContext had one method, holderLookup(registry key) -> HolderLookup; 1.20.5's is
 * a HolderLookup.Provider (lookup(key) -> Optional&lt;RegistryLookup&gt;, listRegistries()). The interface
 * gets holderLookup back as a default answering lookupOrThrow(key) (CbFinBuildContextMixin), and a
 * 1.20.4 implementation -- a class or a method reference -- answers the Provider methods through its
 * own holderLookup. 1.20.4's configurable(access, flags) context (missing tags created or failed per
 * policy) is rebuilt on 1.20.5's registries, which still give both lookups (asLookup /
 * asTagAddingLookup). HolderLookup.filterFeatures(predicate) and the two Delegate classes are back as
 * 1.20.4 had them.
 */
public final class CbFinLookups {

    private CbFinLookups() {
    }

    // ------------------------------------------------------------------ HolderLookup -> RegistryLookup

    /**
     * A 1.20.4 lookup as the RegistryLookup 1.20.5 asks for: itself when it is one; otherwise a view
     * answering the 1.20.4 lookup's elements and tags, with the registry key it was asked for and the
     * lifecycle given (1.20.4's HolderLookup had none: stable).
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> class_7225.class_7226<T> cbAsRegistryLookup(class_5321<? extends class_2378<? extends T>> key,
            Lifecycle lifecycle, class_7225<T> lookup) {
        if (lookup instanceof class_7225.class_7226 registryLookup) {
            return registryLookup;
        }
        return new RegistryLookupView<>(key, lifecycle, lookup);
    }

    private record RegistryLookupView<T>(class_5321<? extends class_2378<? extends T>> key, Lifecycle lifecycle,
            class_7225<T> lookup) implements class_7225.class_7226<T> {
        @Override
        public class_5321<? extends class_2378<? extends T>> method_46765() {
            return key;
        }

        @Override
        public Lifecycle method_46766() {
            return lifecycle;
        }

        @Override
        public Optional<class_6880.class_6883<T>> method_46746(class_5321<T> id) {
            return lookup.method_46746(id);
        }

        @Override
        public Stream<class_6880.class_6883<T>> method_42017() {
            return lookup.method_42017();
        }

        @Override
        public Optional<class_6885.class_6888<T>> method_46733(class_6862<T> tag) {
            return lookup.method_46733(tag);
        }

        @Override
        public Stream<class_6885.class_6888<T>> method_42020() {
            return lookup.method_42020();
        }
    }

    // ------------------------------------------------------------------ CommandBuildContext

    /** a 1.20.4 CommandBuildContext's own holderLookup, as a method reference compiled against 1.20.4 builds it */
    public interface LegacyBuildContext {
        <T> class_7225<T> method_41699(class_5321<? extends class_2378<T>> key);
    }

    /**
     * Provider.listRegistries() of a 1.20.4 context, which could only be asked for a key: the running
     * game's registries (the keys a 1.20.4 command context was asked for in that game).
     */
    public static Stream<class_5321<? extends class_2378<?>>> cbRegistryKeys() {
        return CbRegistries.currentAccess().method_55282();
    }

    /** Provider.lookup(key) of a 1.20.4 context: its holderLookup(key) as a registry lookup */
    public static <T> Optional<class_7225.class_7226<T>> cbLookup(class_5321<? extends class_2378<? extends T>> key,
            class_7225<T> legacy) {
        return Optional.of(cbAsRegistryLookup(key, Lifecycle.stable(), legacy));
    }

    /** a method reference to holderLookup compiled as a 1.20.4 CommandBuildContext (lambdaAdapters) */
    public static class_7157 cbBuildContext(LegacyBuildContext legacy) {
        return new AdaptedBuildContext(legacy);
    }

    private record AdaptedBuildContext(LegacyBuildContext legacy) implements class_7157 {
        /** holderLookup(key), the 1.20.4 shape (CbFinBuildContextMixin declares it on the interface) */
        public <T> class_7225<T> method_41699(class_5321<? extends class_2378<T>> key) {
            return legacy.method_41699(key);
        }

        @Override
        public Stream<class_5321<? extends class_2378<?>>> method_55282() {
            return cbRegistryKeys();
        }

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public <T> Optional<class_7225.class_7226<T>> method_46759(class_5321<? extends class_2378<? extends T>> key) {
            return cbLookup(key, (class_7225<T>) legacy.method_41699((class_5321) key));
        }
    }

    /** was CommandBuildContext.configurable(access, flags): missing tags fail until the policy says otherwise */
    public static CommandBuildContextConfigurable method_46723(class_5455 access, class_7699 flags) {
        return new Configurable(access, flags);
    }

    private static final class Configurable implements CommandBuildContextConfigurable {
        private final class_5455 access;
        private final class_7699 flags;
        private CommandBuildContextPolicy policy = CommandBuildContextPolicy.field_37826;

        Configurable(class_5455 access, class_7699 flags) {
            this.access = access;
            this.flags = flags;
        }

        @Override
        public void method_46724(CommandBuildContextPolicy policy) {
            this.policy = policy;
        }

        private <T> class_7225.class_7226<T> lookup(class_2378<T> registry) {
            class_7225.class_7226<T> base = policy == CommandBuildContextPolicy.field_37824 ? registry.method_46772() : registry.method_46771();
            return base.method_45919(flags);
        }

        /** holderLookup(key): 1.20.4's body (registryOrThrow, the policy's lookup, filtered by the flags) */
        @Override
        public <T> class_7225<T> method_41699(class_5321<? extends class_2378<T>> key) {
            return lookup(access.method_30530(key));
        }

        @Override
        public Stream<class_5321<? extends class_2378<?>>> method_55282() {
            return access.method_55282();
        }

        @Override
        public <T> Optional<class_7225.class_7226<T>> method_46759(class_5321<? extends class_2378<? extends T>> key) {
            return access.<T>method_33310(key).map(this::lookup);
        }
    }

    // ------------------------------------------------------------------ HolderLookup.filterFeatures / RegistryLookup.filterFeatures

    /** HolderLookup.filterFeatures(predicate), 1.20.4's default: a delegate whose elements pass the predicate */
    public static <T> class_7225<T> cbFilterElements(class_7225<T> lookup, Predicate<T> predicate) {
        return new HolderLookupDelegate<T>(lookup) {
            @Override
            public Optional<class_6880.class_6883<T>> method_46746(class_5321<T> id) {
                return this.field_40933.method_46746(id).filter(h -> predicate.test(h.comp_349()));
            }

            @Override
            public Stream<class_6880.class_6883<T>> method_42017() {
                return this.field_40933.method_42017().filter(h -> predicate.test(h.comp_349()));
            }
        };
    }

    /** a 1.20.4 RegistryLookup's filterFeatures(flags), answering a HolderLookup (the adapter the override hook adds) */
    public interface LegacyFilterFeatures {
        class_7225<?> method_45919(class_7699 flags);
    }

    /**
     * 1.20.5 asks a registry lookup for filterFeatures(flags) as a RegistryLookup; a 1.20.4 override
     * answered a HolderLookup, which keeps the lookup's own key and lifecycle when it is not a
     * registry lookup itself.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static class_7225.class_7226<?> cbFilterFeatures(LegacyFilterFeatures self, class_7699 flags) {
        class_7225.class_7226 owner = (class_7225.class_7226) self;
        return cbAsRegistryLookup(owner.method_46765(), owner.method_46766(), (class_7225) self.method_45919(flags));
    }
}
