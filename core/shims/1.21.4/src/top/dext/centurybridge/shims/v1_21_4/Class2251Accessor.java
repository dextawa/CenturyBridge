package top.dext.centurybridge.shims.v1_21_4;

import java.util.Optional;
import net.minecraft.class_2248;
import net.minecraft.class_4970;
import net.minecraft.class_5321;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Key plumbing on Block Settings for the pending-key repair (see CbPending). */
@Mixin(class_4970.class_2251.class)
public interface Class2251Accessor {

    @Accessor("field_54006")
    class_5321<class_2248> cbGetKey();

    @Accessor("field_54006")
    void cbSetKey(class_5321<class_2248> key);

    @Invoker("method_63505")
    Optional<class_5321<net.minecraft.class_52>> cbLootTableKey();

    @Invoker("method_63506")
    String cbTranslationKey();
}
