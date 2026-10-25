package cb.smx4n.mixin;

import java.util.Map;
import net.minecraft.class_1060;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_1060.class)
public interface TextureManagerAccess {
    @Accessor("field_5286")
    Map<?, ?> cb$byPath();
}
