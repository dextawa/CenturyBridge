package top.dext.centurybridge.step.v26_1.mixin;

import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.contents.data.EntityDataSource;
import net.minecraft.util.CompilableString;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** a EntityDataSource a 1.21.11 constructor made from text that does not parse yields no data, as 1.21.11's did (rt/CbWallDataSources) */
@Mixin(EntityDataSource.class)
public abstract class CbWallEntityDataSourceMixin {

    @Shadow
    @Final
    private CompilableString<EntitySelector> selector;

    @Inject(method = "getData", at = @At("HEAD"), cancellable = true)
    private void cb$uncompiled(CommandSourceStack source, CallbackInfoReturnable<Stream<CompoundTag>> cir) {
        if (this.selector.compiled() == null) {
            cir.setReturnValue(Stream.empty());
        }
    }
}
