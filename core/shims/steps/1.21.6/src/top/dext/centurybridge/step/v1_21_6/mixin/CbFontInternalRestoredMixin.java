package top.dext.centurybridge.step.v1_21_6.mixin;

import net.minecraft.class_327.class_5232;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_6.rt.CbGuiText;
import top.dext.centurybridge.step.v1_21_6.rt.CbVanillaReads;

/**
 * 1.21.5's private Font.drawInternal(String, x, y, color, shadow, pose, buffers, mode, background, light, inverseDepth)
 * (method_27529), which 1.21.6 removed (javap of both), given back for the climbed 1.21.2 layer (CbRenderFontMixin's
 * 1.21.1 drawInBatch shadows it) and 1.21.5 mods' shadows / invokers.
 * <ul>
 * <li>1.21.5: adjustColor(color), renderText(...) drawing the string as it is -- no bidirectional shaping: its public
 *     callers shaped first (drawInBatch(String): isBidirectional() ? bidirectionalShaping(text) : text) --, and answered
 *     (int) the end x + 1 with a shadow.</li>
 * <li>1.21.6: prepareText(String, ...) shapes and then fills a PreparedTextBuilder (class_5232) through
 *     StringDecomposer.iterateFormatted(text, Style.EMPTY, builder), which visit(GlyphVisitor.forMultiBufferSource(
 *     buffers, pose, mode, light)) draws. So: the builder over the unshaped text, as 1.21.5 drew it.</li>
 * <li>The end x: x plus the string's width (StringSplitter.stringWidth, the advance 1.21.5's renderText summed).</li>
 * <li>inverseDepth: 1.21.6's glyphs keep their depth offsets themselves (BakedSheetGlyph; the step's CbFontDraw judged
 *     1.21.5's drawInBatch(..., inverseDepth) the same way), so it does not change the draw.</li>
 * </ul>
 * Nothing in 1.21.6 calls it. Priority below every mod's.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
@Mixin(value = net.minecraft.class_327.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbFontInternalRestoredMixin {

    /** drawInternal */
    private int method_27529(String text, float x, float y, int color, boolean shadow, org.joml.Matrix4f pose,
            net.minecraft.class_4597 buffers, net.minecraft.class_327.class_6415 mode, int background, int light, boolean inverseDepth) {
        net.minecraft.class_327 font = (net.minecraft.class_327) (Object) this;
        class_5232 builder = font.new class_5232(x, y, CbGuiText.adjust(color), background, shadow);
        net.minecraft.class_5223.method_27479(text, net.minecraft.class_2583.field_24360, builder);
        builder.method_71801(net.minecraft.class_327.class_11464.method_71799(buffers, pose, mode, light));
        return (int) (x + font.method_27527().method_27482(text)) + (shadow ? 1 : 0);
    }
}
