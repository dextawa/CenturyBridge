package top.dext.centurybridge.step.v1_21_4.mixin;

import java.util.function.BiConsumer;
import net.minecraft.class_10186;
import net.minecraft.class_10206;
import net.minecraft.class_10394;
import net.minecraft.class_5321;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** EquipmentAssetProvider.bootstrap (private): 1.21.3's EquipmentModels.bootstrap answers it (CbRestEquipmentClient). */
@Mixin(class_10206.class)
public interface CbRestEquipmentAssetsInvoker {

    @Invoker("method_65424")
    static void cb$bootstrap(BiConsumer<class_5321<class_10394>, class_10186> output) {
        throw new AssertionError();
    }
}
