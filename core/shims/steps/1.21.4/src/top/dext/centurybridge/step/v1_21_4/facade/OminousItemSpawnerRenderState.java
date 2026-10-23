package top.dext.centurybridge.step.v1_21_4.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.3 OminousItemSpawnerRenderState (class_10048, gone in 1.21.4): the spawner's item (a copy) and its model. 1.21.4 draws
 * the spawner from an ItemClusterRenderState; this is one, with 1.21.3's fields, and 1.21.4's OminousItemSpawnerRenderer makes
 * and fills it (CbStateOminousRendererMixin), so a 1.21.3 renderer subclass and layer see what they saw.
 */
@Environment(EnvType.CLIENT)
public class OminousItemSpawnerRenderState extends net.minecraft.class_10428 {
    /** itemModel */
    public net.minecraft.class_1087 field_53494;
    /** item */
    public net.minecraft.class_1799 field_53495 = net.minecraft.class_1799.field_8037;

    public OminousItemSpawnerRenderState() {
    }
}
