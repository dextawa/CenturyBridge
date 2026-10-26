package cb.smx6n.mixin;

import java.util.Map;
import net.minecraft.class_1092;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_1092.class)
public interface ModelManagerAccess {
    @Accessor("field_5408")
    Map<?, ?> cb$bakedRegistry();
}
