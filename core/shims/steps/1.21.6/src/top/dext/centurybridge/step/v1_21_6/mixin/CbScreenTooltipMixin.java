package top.dext.centurybridge.step.v1_21_6.mixin;

/**
 * Screen's 1.21.5 tooltip-for-next-render-pass members and its no-argument renderBlurredBackground, which 1.21.6
 * removed (javap of both):
 * <ul>
 * <li>1.21.5 kept one deferred tooltip per screen: setTooltipForNextRenderPass(lines, positioner, override) set it when
 *     none was set or override held; the List / Component / Tooltip overloads went through it (DefaultTooltipPositioner,
 *     override true; Tooltip.splitTooltip; tooltip.toCharSequence with the caller's flag); clearTooltipForNextRenderPass
 *     dropped it. renderWithTooltip drew it after render, at the mouse, through renderTooltip.</li>
 * <li>1.21.6 keeps the deferred tooltip in GuiGraphics (setTooltipForNextFrame) and renderWithTooltip ends with
 *     renderDeferredTooltip. So a tooltip a 1.21.5 caller deferred is handed to the frame's GuiGraphics in
 *     renderWithTooltip just before that call, at the mouse, as 1.21.5's renderTooltip there -- 1.21.6's renderTooltip
 *     of the same lines and positioner -- with "replace" = the override it was set with. renderWithTooltip itself runs
 *     unchanged.</li>
 * <li>renderBlurredBackground() was minecraft.gameRenderer.processBlurEffect(); 1.21.6 keeps processBlurEffect (its GUI
 *     renderer calls it before the stratum a screen marked with renderBlurredBackground(GuiGraphics)).</li>
 * <li>renderWithTooltip's GuiGraphics is recorded for its duration (CbScreens.screenFrame): the step's frameDefaults
 *     hand it to a 1.21.5 override's rewritten super call that has no frame (CbScreens.screenGraphics / blurGraphics).</li>
 * </ul>
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
@org.spongepowered.asm.mixin.Mixin(net.minecraft.class_437.class)
public abstract class CbScreenTooltipMixin {

    @org.spongepowered.asm.mixin.Shadow
    protected net.minecraft.class_310 field_22787;

    @org.spongepowered.asm.mixin.Shadow
    protected net.minecraft.class_327 field_22793;

    @org.spongepowered.asm.mixin.Unique
    private java.util.List<net.minecraft.class_5481> cb$tooltip;

    @org.spongepowered.asm.mixin.Unique
    private net.minecraft.class_8000 cb$tooltipPositioner;

    @org.spongepowered.asm.mixin.Unique
    private boolean cb$tooltipOverride;

    /** setTooltipForNextRenderPass(List&lt;FormattedCharSequence&gt;) */
    public void method_47414(java.util.List<net.minecraft.class_5481> lines) {
        this.method_47942(lines, net.minecraft.class_8001.field_41687, true);
    }

    /** setTooltipForNextRenderPass(List&lt;FormattedCharSequence&gt;, ClientTooltipPositioner, boolean override) */
    public void method_47942(java.util.List<net.minecraft.class_5481> lines, net.minecraft.class_8000 positioner, boolean override) {
        if (this.cb$tooltip == null || override) {
            this.cb$tooltip = lines;
            this.cb$tooltipPositioner = positioner;
            this.cb$tooltipOverride = override;
        }
    }

    /** setTooltipForNextRenderPass(Component) */
    public void method_47415(net.minecraft.class_2561 text) {
        this.method_47414(net.minecraft.class_7919.method_47406(this.field_22787, text));
    }

    /** setTooltipForNextRenderPass(Tooltip, ClientTooltipPositioner, boolean override) */
    public void method_47412(net.minecraft.class_7919 tooltip, net.minecraft.class_8000 positioner, boolean override) {
        this.method_47942(tooltip.method_47405(this.field_22787), positioner, override);
    }

    /** clearTooltipForNextRenderPass() */
    protected void method_59840() {
        this.cb$tooltip = null;
    }

    /** renderBlurredBackground() */
    protected void method_57734() {
        this.field_22787.field_1773.method_57796();
    }

    @org.spongepowered.asm.mixin.injection.Inject(method = "method_47413(Lnet/minecraft/class_332;IIF)V",
        at = @org.spongepowered.asm.mixin.injection.At("HEAD"))
    private void cb$screenFrameStart(net.minecraft.class_332 graphics, int mouseX, int mouseY, float partialTick,
            org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        top.dext.centurybridge.step.v1_21_6.rt.CbScreens.screenFrame(graphics);
    }

    @org.spongepowered.asm.mixin.injection.Inject(method = "method_47413(Lnet/minecraft/class_332;IIF)V",
        at = @org.spongepowered.asm.mixin.injection.At("RETURN"))
    private void cb$screenFrameEnd(net.minecraft.class_332 graphics, int mouseX, int mouseY, float partialTick,
            org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        top.dext.centurybridge.step.v1_21_6.rt.CbScreens.screenFrame(null);
    }

    @org.spongepowered.asm.mixin.injection.Inject(method = "method_47413(Lnet/minecraft/class_332;IIF)V",
        at = @org.spongepowered.asm.mixin.injection.At(value = "INVOKE", target = "Lnet/minecraft/class_332;method_71279()V"))
    private void cb$deferredTooltip(net.minecraft.class_332 graphics, int mouseX, int mouseY, float partialTick,
            org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        if (this.cb$tooltip != null) {
            graphics.method_51436(this.field_22793, this.cb$tooltip, this.cb$tooltipPositioner, mouseX, mouseY, this.cb$tooltipOverride);
            this.cb$tooltip = null;
        }
    }
}
