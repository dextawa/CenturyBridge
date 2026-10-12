package top.dext.centurybridge.step.v1_20_3.rt;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * 1.20.2 registry-lookup API on 1.20.3: HolderLookup.Provider (which gained listRegistries) and
 * RegistrySetBuilder.buildPatch (which gained a cloner and answers both lookups).
 */
public final class CbRegistries {

    // ------------------------------------------------------------------ HolderLookup.Provider (class_7225$class_7874)

    /** a 1.20.2 HolderLookup.Provider lambda: lookup(ResourceKey) only, then the whole interface */
    @FunctionalInterface
    public interface CbLegacyProvider {
        Optional<net.minecraft.class_7225.class_7226<?>> method_46759(net.minecraft.class_5321<?> registry);
    }

    /** marks a 1.20.2 HolderLookup.Provider class given 1.20.3's listRegistries (adapterHooks) */
    public interface CbLegacyLookup {
    }

    /**
     * Every registry the game knows of -- the built-in registries and the data-driven ones RegistryDataLoader
     * loads (world generation and dimensions) -- in that order, each once.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Stream<net.minecraft.class_5321<? extends net.minecraft.class_2378<?>>> knownRegistries() {
        Stream<net.minecraft.class_5321<? extends net.minecraft.class_2378<?>>> builtin =
            (Stream) net.minecraft.class_7923.field_41167.method_42021().stream();
        Stream<net.minecraft.class_5321<? extends net.minecraft.class_2378<?>>> loaded = (Stream) Stream.concat(
            net.minecraft.class_7655.field_39968.stream(), net.minecraft.class_7655.field_39969.stream())
            .map(net.minecraft.class_7655.class_7657::comp_985);
        return Stream.concat(builtin, loaded).distinct();
    }

    /**
     * HolderLookup.Provider.listRegistries(), abstract since 1.20.3, for a 1.20.2 provider (adapterHooks,
     * guarded): what a vanilla class above it answers (the call again from here reaches it), else the
     * registries it can look up, by 1.20.2's own question -- lookup(key) present -- asked of every registry
     * the game knows of.
     */
    public static Stream<net.minecraft.class_5321<? extends net.minecraft.class_2378<?>>> cbListRegistries(net.minecraft.class_7225.class_7874 provider) {
        Stream<net.minecraft.class_5321<? extends net.minecraft.class_2378<?>>> inherited = provider.method_55282();
        return inherited != null ? inherited : lookedUp(provider);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Stream<net.minecraft.class_5321<? extends net.minecraft.class_2378<?>>> lookedUp(net.minecraft.class_7225.class_7874 provider) {
        return knownRegistries().filter(key -> provider.method_46759((net.minecraft.class_5321) key).isPresent());
    }

    /** a 1.20.2 HolderLookup.Provider lambda as the 1.20.3 interface */
    public static net.minecraft.class_7225.class_7874 cbProvider(CbLegacyProvider lookup) {
        return new net.minecraft.class_7225.class_7874() {
            @Override
            public Stream<net.minecraft.class_5321<? extends net.minecraft.class_2378<?>>> method_55282() {
                return lookedUp(this);
            }

            @Override
            @SuppressWarnings({"unchecked", "rawtypes"})
            public <T> Optional<net.minecraft.class_7225.class_7226<T>> method_46759(
                    net.minecraft.class_5321<? extends net.minecraft.class_2378<? extends T>> registry) {
                return (Optional) lookup.method_46759(registry);
            }
        };
    }

    // ------------------------------------------------------------------ RegistrySetBuilder.buildPatch (class_7877)

    /**
     * the cloner factory 1.20.3's RegistryPatchGenerator builds for buildPatch: every data-driven world
     * generation registry's codec
     */
    public static net.minecraft.class_8990.class_8991 cbClonerFactory() {
        net.minecraft.class_8990.class_8991 factory = new net.minecraft.class_8990.class_8991();
        net.minecraft.class_7655.field_39968.forEach(data -> data.method_54920(factory::method_55281));
        return factory;
    }

    /**
     * was RegistrySetBuilder.buildPatch(RegistryAccess, HolderLookup.Provider) (method_46781): the full lookup
     * -- the access's registries with the builder's entries patched in -- which 1.20.3 answers as the first
     * of its two (with the cloner its own generator uses). The call goes to the builder virtually, so a 1.20.2
     * subclass's override is reached (overrideHooks).
     */
    public static net.minecraft.class_7225.class_7874 method_46781(net.minecraft.class_7877 builder, net.minecraft.class_5455 access,
                                                                   net.minecraft.class_7225.class_7874 lookup) {
        return builder.method_46781(access, lookup, cbClonerFactory()).comp_2113();
    }

    /** a 1.20.2 RegistrySetBuilder subclass's buildPatch override */
    public interface CbLegacyPatchBuilder {
        net.minecraft.class_7225.class_7874 method_46781(net.minecraft.class_5455 access, net.minecraft.class_7225.class_7874 lookup);
    }

    /**
     * 1.20.3's buildPatch on a 1.20.2 subclass that overrides the 1.20.2 one: its full lookup, with the
     * patch-only lookup the builder's own (vanilla) buildPatch gives -- the call again from here reaches it
     * (the guard)
     */
    public static net.minecraft.class_7877.class_8993 cbBuildPatch(CbLegacyPatchBuilder builder, net.minecraft.class_5455 access,
                                                                  net.minecraft.class_7225.class_7874 lookup,
                                                                  net.minecraft.class_8990.class_8991 cloner) {
        net.minecraft.class_7877.class_8993 vanilla = ((net.minecraft.class_7877) builder).method_46781(access, lookup, cloner);
        return new net.minecraft.class_7877.class_8993(builder.method_46781(access, lookup), vanilla.comp_2114());
    }

    private CbRegistries() {
    }
}
