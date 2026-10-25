package top.dext.centurybridge.step.v26_1.mixin;

import java.util.Objects;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.util.StringUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;

/**
 * 1.21.11 EditBox.setFilter(Predicate) (class_342.method_1890) and the filter it set (class_342.field_2104, default:
 * any non-null text): 1.21.11 tested the text the box would hold before setValue, insertText and deleteCharsToPos
 * changed it, and left the box untouched (no change, no responder call) when the filter refused. 26.1 has no filter;
 * the same three tests run here, on the text 26.1's bodies would set (insertText's: 26.1's own clamp to the room
 * left, surrogate-safe, of the filtered input, replacing the selection).
 */
@Environment(EnvType.CLIENT)
@Mixin(EditBox.class)
public abstract class CbWallEditBoxFilterMixin {

    @Shadow
    private String value;

    @Shadow
    private int cursorPos;

    @Shadow
    private int highlightPos;

    @Shadow
    private int maxLength;

    @Unique
    private Predicate<String> cb$filter = Objects::nonNull;

    /** 1.21.11 EditBox.setFilter(filter) */
    @CbVanillaReads("setValue / insertText / deleteCharsToPos test the text against it here")
    public void setFilter(Predicate<String> filter) {
        this.cb$filter = filter;
    }

    @Inject(method = "setValue", at = @At("HEAD"), cancellable = true)
    private void cb$filterSetValue(String text, CallbackInfo ci) {
        if (!this.cb$filter.test(text)) {
            ci.cancel();
        }
    }

    @Inject(method = "insertText", at = @At("HEAD"), cancellable = true)
    private void cb$filterInsertText(String text, CallbackInfo ci) {
        int start = Math.min(this.cursorPos, this.highlightPos);
        int end = Math.max(this.cursorPos, this.highlightPos);
        int room = this.maxLength - this.value.length() - (start - end);
        if (room <= 0) {
            return; // 26.1's body changes nothing either
        }
        String inserted = StringUtil.filterText(text);
        if (room < inserted.length()) {
            if (Character.isHighSurrogate(inserted.charAt(room - 1))) {
                room--;
            }
            inserted = inserted.substring(0, room);
        }
        String result = new StringBuilder(this.value).replace(start, end, inserted).toString();
        if (!this.cb$filter.test(result)) {
            ci.cancel();
        }
    }

    @Inject(method = "deleteCharsToPos", at = @At("HEAD"), cancellable = true)
    private void cb$filterDeleteCharsToPos(int pos, CallbackInfo ci) {
        if (this.value.isEmpty() || this.highlightPos != this.cursorPos) {
            return; // nothing to delete, or the selection goes through insertText("")
        }
        int start = Math.min(pos, this.cursorPos);
        int end = Math.max(pos, this.cursorPos);
        if (start == end) {
            return;
        }
        String result = new StringBuilder(this.value).delete(start, end).toString();
        if (!this.cb$filter.test(result)) {
            ci.cancel();
        }
    }
}
