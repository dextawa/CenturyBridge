package top.dext.centurybridge.step.v1_21_2.mixin;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import net.minecraft.class_4309;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** SimpleJsonResourceReloadListener's ops, codec and directory (private): how a listener parses its files (CbRestServer) */
@Mixin(class_4309.class)
public interface CbRestJsonListenerAccessor {

    @Accessor("field_54055")
    DynamicOps<JsonElement> cb$ops();

    @Accessor("field_54056")
    Codec<?> cb$codec();

    @Accessor("field_19380")
    String cb$directory();
}
