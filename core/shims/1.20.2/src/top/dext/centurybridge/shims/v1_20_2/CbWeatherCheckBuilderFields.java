package top.dext.centurybridge.shims.v1_20_2;

import java.util.Optional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** WeatherCheck.Builder's two checks: a 1.20.1 setRaining(null) / setThundering(null) leaves one unset. */
@Mixin(net.minecraft.class_227.class_6165.class)
public interface CbWeatherCheckBuilderFields {

    @Accessor("field_31873")
    void cb$setRaining(Optional<Boolean> raining);

    @Accessor("field_31874")
    void cb$setThundering(Optional<Boolean> thundering);
}
