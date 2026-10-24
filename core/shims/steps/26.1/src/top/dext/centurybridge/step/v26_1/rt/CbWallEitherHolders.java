package top.dext.centurybridge.step.v26_1.rt;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import top.dext.centurybridge.step.v26_1.facade.EitherHolder;

/**
 * 1.21.11's JukeboxPlayable / InstrumentComponent / ProvidesTrimMaterial held an EitherHolder; 26.1's hold the
 * holder. A holder passes as it is; a key is the value the registries in use hold under it, as 1.21.11 resolved
 * it when the component was used -- 26.1's component cannot wait, so a key given while no registries are loaded
 * (an item's default components at registration; 26.1 names those through Item.Properties' delayed components)
 * has nothing to resolve against and is refused with the reason.
 */
public final class CbWallEitherHolders {

    private CbWallEitherHolders() {
    }

    public static <T> Holder<T> holder(EitherHolder<T> either) {
        return either.comp_3636.map(holder -> holder, CbWallEitherHolders::resolve);
    }

    private static <T> Holder<T> resolve(ResourceKey<T> key) {
        return CbWallRegistries.access().get(key).orElseThrow(() -> new IllegalStateException(
            "1.21.11 EitherHolder key " + key + " names nothing in the registries in use"));
    }

    /** a 26.1 component's holder as 1.21.11's EitherHolder */
    public static <T> EitherHolder<T> either(Holder<T> holder) {
        return new EitherHolder<>(holder);
    }
}
