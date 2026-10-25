package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * StateSwitchingButton's 1.20.1 texture sheet: initTextureValues(xTexStart, yTexStart, xDiffTex, yDiffTex,
 * texture) and the protected fields it sets, which 1.20.2 replaced with WidgetSprites. Set through the
 * 1.20.1 API, the button draws as 1.20.1 drew it -- the sheet region at (xTexStart, yTexStart), moved
 * right by xDiffTex when triggered and down by yDiffTex when hovered or focused, depth test off -- in
 * place of 1.20.2's sprite drawing, which draws nothing for a button whose sprites were never set.
 * A button given sprites (every 1.20.2 one) never has a sheet texture and draws as 1.20.2 does.
 */
@Mixin(value = net.minecraft.class_361.class, priority = top.dext.centurybridge.rt.v1_20_2.CbVanillaReads.PRIORITY)
public abstract class CbGuiStateSwitchingButtonBridge extends net.minecraft.class_339 implements top.dext.centurybridge.rt.v1_20_2.CbGuiRestoredAccess.StateSwitchingSheet {

    /** resourceLocation */
    protected net.minecraft.class_2960 field_2193;
    /** xTexStart */
    protected int field_2192;
    /** yTexStart */
    protected int field_2191;
    /** xDiffTex */
    protected int field_2190;
    /** yDiffTex */
    protected int field_2189;

    private CbGuiStateSwitchingButtonBridge() {
        super(0, 0, 0, 0, null);
    }

    /** 1.20.1 initTextureValues(xTexStart, yTexStart, xDiffTex, yDiffTex, texture) */
    public void method_1962(int xTexStart, int yTexStart, int xDiffTex, int yDiffTex, net.minecraft.class_2960 texture) {
        this.field_2192 = xTexStart;
        this.field_2191 = yTexStart;
        this.field_2190 = xDiffTex;
        this.field_2189 = yDiffTex;
        this.field_2193 = texture;
    }

    @Override
    public net.minecraft.class_2960 cb$sheet() {
        return this.field_2193;
    }

    @Override
    public int cb$xTexStart() {
        return this.field_2192;
    }

    @Override
    public int cb$yTexStart() {
        return this.field_2191;
    }

    @Override
    public int cb$xDiffTex() {
        return this.field_2190;
    }

    @Override
    public int cb$yDiffTex() {
        return this.field_2189;
    }
}
