package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10297;
import net.minecraft.class_10298;
import net.minecraft.class_299;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** ClientRecipeBook.known (private): the display entries the server sent, read by the 1.21.1 recipe-book API */
@Environment(EnvType.CLIENT)
@Mixin(class_299.class)
public interface CbRestClientRecipeBookAccessor {

    @Accessor("field_54810")
    Map<class_10298, class_10297> cb$known();
}
