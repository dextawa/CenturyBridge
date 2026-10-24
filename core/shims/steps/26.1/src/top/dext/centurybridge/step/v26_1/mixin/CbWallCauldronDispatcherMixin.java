package top.dext.centurybridge.step.v26_1.mixin;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_1.rt.CbWallCauldrons;

/**
 * A dispatcher's 1.21.11 state (rt/CbWallCauldrons): a map a mod built itself answers alone, as 1.21.11's
 * cauldron asked only it; items a mod put through a 1.21.11 map are answered before 26.1's tags and items.
 */
@Mixin(CauldronInteraction.Dispatcher.class)
public abstract class CbWallCauldronDispatcherMixin implements CbWallCauldrons.DispatcherState {

    @Unique
    private final Map<Item, CauldronInteraction> cb$overrides = new HashMap<>();

    @Unique
    private Map<Item, CauldronInteraction> cb$backing;

    @Override
    public Map<Item, CauldronInteraction> cb$overrides() {
        return this.cb$overrides;
    }

    @Override
    public Map<Item, CauldronInteraction> cb$backing() {
        return this.cb$backing;
    }

    @Override
    public void cb$setBacking(Map<Item, CauldronInteraction> map) {
        this.cb$backing = map;
    }

    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    private void cb$get(ItemStack stack, CallbackInfoReturnable<CauldronInteraction> cir) {
        if (this.cb$backing != null) {
            cir.setReturnValue(this.cb$backing.get(stack.getItem()));
            return;
        }
        CauldronInteraction override = this.cb$overrides.get(stack.getItem());
        if (override != null) {
            cir.setReturnValue(override);
        }
    }
}
