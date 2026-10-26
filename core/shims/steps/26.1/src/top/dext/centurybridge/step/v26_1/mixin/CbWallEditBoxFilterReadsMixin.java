package top.dext.centurybridge.step.v26_1.mixin;

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
import top.dext.centurybridge.step.v26_1.rt.CbWallEditBoxFilter;

/**
 * 1.21.11's filter tests (CbWallEditBoxFilterMixin): before 26.1's setValue, insertText and deleteCharsToPos change
 * the box, the text they would set is tested against the filter a mod set (insertText's: 26.1's own clamp to the room
 * left, surrogate-safe, of the filtered input, replacing the selection), and the call leaves the box untouched when
 * it refuses, as on 1.21.11. With no filter set nothing is tested and 26.1's bodies run as they are. Default priority:
 * injectors into 26.1 methods.
 */
@Environment(EnvType.CLIENT)
@Mixin(EditBox.class)
public abstract class CbWallEditBoxFilterReadsMixin {

    @Shadow
    private String value;

    @Shadow
    private int cursorPos;

    @Shadow
    private int highlightPos;

    @Shadow
    private int maxLength;

    @Unique
    private Predicate<String> cb$filterSet() {
        return ((CbWallEditBoxFilter) this).cb$filter();
    }

    @Inject(method = "setValue", at = @At("HEAD"), cancellable = true)
    private void cb$filterSetValue(String text, CallbackInfo ci) {
        Predicate<String> filter = cb$filterSet();
        if (filter != null && !filter.test(text)) {
            ci.cancel();
        }
    }

    @Inject(method = "insertText", at = @At("HEAD"), cancellable = true)
    private void cb$filterInsertText(String text, CallbackInfo ci) {
        Predicate<String> filter = cb$filterSet();
        if (filter == null) {
            return;
        }
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
        if (!filter.test(result)) {
            ci.cancel();
        }
    }

    @Inject(method = "deleteCharsToPos", at = @At("HEAD"), cancellable = true)
    private void cb$filterDeleteCharsToPos(int pos, CallbackInfo ci) {
        Predicate<String> filter = cb$filterSet();
        if (filter == null || this.value.isEmpty() || this.highlightPos != this.cursorPos) {
            return; // no filter, nothing to delete, or the selection goes through insertText("")
        }
        int start = Math.min(pos, this.cursorPos);
        int end = Math.max(pos, this.cursorPos);
        if (start == end) {
            return;
        }
        String result = new StringBuilder(this.value).delete(start, end).toString();
        if (!filter.test(result)) {
            ci.cancel();
        }
    }
}
