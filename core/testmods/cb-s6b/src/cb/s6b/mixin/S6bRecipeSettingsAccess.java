package cb.s6b.mixin;

import java.util.Map;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** a 1.21.5 mod's accessor on RecipeBookSettings' private states map and key table */
@Mixin(net.minecraft.class_5411.class)
public interface S6bRecipeSettingsAccess {

    @Accessor("field_25736")
    Map<?, ?> cbs6b$states();

    @Accessor("field_25735")
    static Map<?, ?> cbs6b$keys() {
        throw new AssertionError();
    }
}
