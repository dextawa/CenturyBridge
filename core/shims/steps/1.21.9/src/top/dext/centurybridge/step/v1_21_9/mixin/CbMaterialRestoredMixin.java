package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.class_1058;
import net.minecraft.class_310;
import net.minecraft.class_4730;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_9.rt.CbVanillaReads;

/**
 * Material.sprite(), which 1.21.9 dropped (javap: absent from 1.21.9's class_4730). 1.21.8 looked the atlas up by the
 * material's atlas location (Minecraft.getTextureAtlas) and asked it for the texture; 1.21.9 keeps that lookup as
 * AtlasManager.get(material) (method_73030: the sprite map, else the atlas of that location answering its sprite).
 * 1.21.9 never calls it.
 */
@Mixin(value = class_4730.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbMaterialRestoredMixin {

    /** Material.sprite() */
    public class_1058 method_24148() {
        return class_310.method_1551().method_72703().method_73030((class_4730) (Object) this);
    }
}
