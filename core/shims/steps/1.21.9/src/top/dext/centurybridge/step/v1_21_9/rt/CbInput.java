package top.dext.centurybridge.step.v1_21_9.rt;

import java.util.ArrayDeque;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11905;
import net.minecraft.class_11907;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_11910;
import net.minecraft.class_156;
import net.minecraft.class_310;
import net.minecraft.class_312;
import net.minecraft.class_3675;

/**
 * 1.21.9 input events (KeyEvent, CharacterEvent, MouseButtonEvent / MouseButtonInfo) for the 1.21.8 input API.
 *
 * 1.21.8 handed GUI listeners the raw GLFW values: keyPressed(key, scancode, modifiers), charTyped(char,
 * modifiers), mouseClicked(x, y, button) with no modifiers and no double-click flag; widgets asked the live
 * keyboard (Screen.hasShiftDown()) and timed double clicks themselves. 1.21.9 packs the values into records and
 * passes the modifiers and MouseHandler's double-click verdict along.
 *
 * Calls (callReshapes): a 1.21.8 call builds the 1.21.9 record from its values. What the old call lacks comes
 * from the input being handled: the innermost event a hooked 1.21.8 override was handed (pushed by the bridges
 * below, so a mod forwarding the click to its children passes vanilla's modifiers and double-click flag on);
 * outside any hooked call, from what 1.21.9 itself would see now -- the keyboard's modifier keys (the bits GLFW
 * reports for them) and MouseHandler's double-click rule (the same button again within 250 ms of the last
 * click it handled).
 *
 * Overrides (overrideHooks, bridge mode): vanilla calls the 1.21.9 shape; the bridge hands the old override the
 * values 1.21.8 passed (a codepoint outside the BMP as the two chars 1.21.8's KeyboardHandler sent, one call each).
 */
@Environment(EnvType.CLIENT)
public final class CbInput {

    private CbInput() {
    }

    // ------------------------------------------------------------------ the input being handled

    private static final ThreadLocal<ArrayDeque<class_11907>> CURRENT = ThreadLocal.withInitial(ArrayDeque::new);

    private static void push(class_11907 e) {
        CURRENT.get().push(e);
    }

    private static void pop() {
        CURRENT.get().pop();
    }

    private static class_11907 current() {
        return CURRENT.get().peek();
    }

    /** GLFW modifier bits for the modifier keys held now (shift 1, control 2, alt 4, super 8), as 1.21.9's callbacks report them */
    public static int liveModifiers() {
        class_310 mc = class_310.method_1551();
        if (mc == null || mc.method_22683() == null) {
            return 0;
        }
        var w = mc.method_22683();
        int m = 0;
        if (class_3675.method_15987(w, 340) || class_3675.method_15987(w, 344)) {
            m |= 1;
        }
        if (class_3675.method_15987(w, 341) || class_3675.method_15987(w, 345)) {
            m |= 2;
        }
        if (class_3675.method_15987(w, 342) || class_3675.method_15987(w, 346)) {
            m |= 4;
        }
        if (class_3675.method_15987(w, 343) || class_3675.method_15987(w, 347)) {
            m |= 8;
        }
        return m;
    }

    /** the modifiers of the input being handled, else the live ones */
    private static int modifiers() {
        class_11907 e = current();
        return e != null ? e.comp_4797() : liveModifiers();
    }

    /** the button of the click being handled (a hooked override's event, else MouseHandler's pressed button), else left */
    public static int currentButton() {
        class_11907 e = current();
        if (e instanceof class_11909 me) {
            return me.method_74245();
        }
        class_310 mc = class_310.method_1551();
        class_11910 active = mc == null ? null : mc.field_1729.field_1780;
        return active != null ? active.comp_4801() : 0;
    }

    private static int buttonModifiers(int button) {
        class_11907 e = current();
        if (e instanceof class_11909 me && me.method_74245() == button) {
            return me.comp_4797();
        }
        class_310 mc = class_310.method_1551();
        class_11910 active = mc == null ? null : mc.field_1729.field_1780;
        if (active != null && active.comp_4801() == button) {
            return active.comp_4797();
        }
        return modifiers();
    }

    // ------------------------------------------------------------------ call-side converters

    /** keyPressed / keyReleased(key, scancode, modifiers) */
    public static class_11908 cbKey(int key, int scancode, int mods) {
        return new class_11908(key, scancode, mods);
    }

    /** a (key, scancode) match (KeyMapping.matches, InputConstants.getKey: 1.21.9 reads key and scancode only) */
    public static class_11908 cbKeyCode(int key, int scancode) {
        return new class_11908(key, scancode, modifiers());
    }

    /** a key alone (TextFieldHelper / MultilineTextField.keyPressed(key)): 1.21.8 asked the live keyboard for the modifiers */
    public static class_11908 cbKeyFor(int key) {
        class_11907 e = current();
        if (e instanceof class_11908 ke && ke.comp_4795() == key) {
            return ke;
        }
        return new class_11908(key, 0, modifiers());
    }

    /** charTyped(char, modifiers) */
    public static class_11905 cbChar(char c, int mods) {
        return new class_11905(c, mods);
    }

    /** charTyped(char) (TextFieldHelper) */
    public static class_11905 cbCharFor(char c) {
        return new class_11905(c, modifiers());
    }

    /** a char where 1.21.9 takes a codepoint */
    public static int cbCodepoint(char c) {
        return c;
    }

    /** KeyboardHandler.charTyped(window, codepoint, modifiers): 1.21.9 passes the same two ints as a CharacterEvent */
    public static class_11905 cbCharEvent(int codepoint, int mods) {
        return new class_11905(codepoint, mods);
    }

    /** MouseHandler.onPress(window, button, action, modifiers): 1.21.9 passes button and modifiers as a MouseButtonInfo */
    public static class_11910 cbButtonInfo(int button, int mods) {
        return new class_11910(button, mods);
    }

    /** mouseClicked / mouseReleased / mouseDragged(x, y, button) */
    public static class_11909 cbClick(double x, double y, int button) {
        return new class_11909(x, y, new class_11910(button, buttonModifiers(button)));
    }

    /** onClick / onRelease / onDrag(x, y): the button of the click being handled */
    public static class_11909 cbClickAt(double x, double y) {
        return cbClick(x, y, currentButton());
    }

    /** isValidClickButton(button) */
    public static class_11910 cbButton(int button) {
        return new class_11910(button, buttonModifiers(button));
    }

    /** KeyMapping.matchesMouse(button): 1.21.9 reads the button only */
    public static class_11909 cbButtonEvent(int button) {
        return cbClick(0.0, 0.0, button);
    }

    /** the double-click flag 1.21.9's MouseHandler would pass for this button now */
    public static boolean cbDoubleClick(int button) {
        class_310 mc = class_310.method_1551();
        if (mc == null) {
            return false;
        }
        class_312 mh = mc.field_1729;
        return class_156.method_658() - mh.field_61506 < 250L && mh.field_61507 == button;
    }

    /** the double-click flag for the click being handled */
    public static boolean cbDoubleClickCurrent() {
        return cbDoubleClick(currentButton());
    }

    /** AbstractButton.onPress(): the input being handled, else one carrying the live modifiers */
    public static class_11907 cbCurrentInput() {
        class_11907 e = current();
        return e != null ? e : new class_11908(-1, 0, liveModifiers());
    }

    // ------------------------------------------------------------------ Screen's static keyboard helpers (gone in 1.21.9)

    /** Screen.hasControlDown(): 1.21.9's Minecraft.hasControlDown() reads the same keys (command on macOS) */
    public static boolean method_25441() {
        return class_310.method_1551().method_74188();
    }

    /** Screen.hasShiftDown() */
    public static boolean method_25442() {
        return class_310.method_1551().method_74187();
    }

    /** Screen.hasAltDown() */
    public static boolean method_25443() {
        return class_310.method_1551().method_74189();
    }

    private static boolean controlOnly(int key, int want) {
        return key == want && method_25441() && !method_25442() && !method_25443();
    }

    /** Screen.isCut(key) */
    public static boolean method_25436(int key) {
        return controlOnly(key, 88);
    }

    /** Screen.isPaste(key) */
    public static boolean method_25437(int key) {
        return controlOnly(key, 86);
    }

    /** Screen.isCopy(key) */
    public static boolean method_25438(int key) {
        return controlOnly(key, 67);
    }

    /** Screen.isSelectAll(key) */
    public static boolean method_25439(int key) {
        return controlOnly(key, 65);
    }

    // ------------------------------------------------------------------ override bridges

    /** keyPressed(III)Z */
    public interface LegacyKeyPressed {
        boolean method_25404(int key, int scancode, int mods);
    }

    public static boolean cbKeyPressed(LegacyKeyPressed self, class_11908 e) {
        push(e);
        try {
            return self.method_25404(e.comp_4795(), e.comp_4796(), e.comp_4797());
        } finally {
            pop();
        }
    }

    /** keyReleased(III)Z */
    public interface LegacyKeyReleased {
        boolean method_16803(int key, int scancode, int mods);
    }

    public static boolean cbKeyReleased(LegacyKeyReleased self, class_11908 e) {
        push(e);
        try {
            return self.method_16803(e.comp_4795(), e.comp_4796(), e.comp_4797());
        } finally {
            pop();
        }
    }

    /** charTyped(CI)Z: a supplementary codepoint as the two chars 1.21.8's KeyboardHandler sent */
    public interface LegacyCharTyped {
        boolean method_25400(char c, int mods);
    }

    public static boolean cbCharTyped(LegacyCharTyped self, class_11905 e) {
        push(new class_11908(-1, 0, e.comp_4794()));
        try {
            int cp = e.comp_4793();
            if (Character.isBmpCodePoint(cp)) {
                return self.method_25400((char) cp, e.comp_4794());
            }
            boolean any = false;
            for (char c : Character.toChars(cp)) {
                any |= self.method_25400(c, e.comp_4794());
            }
            return any;
        } finally {
            pop();
        }
    }

    /** mouseClicked(DDI)Z */
    public interface LegacyMouseClicked {
        boolean method_25402(double x, double y, int button);
    }

    public static boolean cbMouseClicked(LegacyMouseClicked self, class_11909 e, boolean doubleClick) {
        push(e);
        try {
            return self.method_25402(e.comp_4798(), e.comp_4799(), e.method_74245());
        } finally {
            pop();
        }
    }

    /** mouseReleased(DDI)Z */
    public interface LegacyMouseReleased {
        boolean method_25406(double x, double y, int button);
    }

    public static boolean cbMouseReleased(LegacyMouseReleased self, class_11909 e) {
        push(e);
        try {
            return self.method_25406(e.comp_4798(), e.comp_4799(), e.method_74245());
        } finally {
            pop();
        }
    }

    /** mouseDragged(DDIDD)Z */
    public interface LegacyMouseDragged {
        boolean method_25403(double x, double y, int button, double dx, double dy);
    }

    public static boolean cbMouseDragged(LegacyMouseDragged self, class_11909 e, double dx, double dy) {
        push(e);
        try {
            return self.method_25403(e.comp_4798(), e.comp_4799(), e.method_74245(), dx, dy);
        } finally {
            pop();
        }
    }

    /** AbstractWidget.onClick(DD)V */
    public interface LegacyOnClick {
        void method_25348(double x, double y);
    }

    public static void cbOnClick(LegacyOnClick self, class_11909 e, boolean doubleClick) {
        push(e);
        try {
            self.method_25348(e.comp_4798(), e.comp_4799());
        } finally {
            pop();
        }
    }

    /** AbstractWidget.onRelease(DD)V */
    public interface LegacyOnRelease {
        void method_25357(double x, double y);
    }

    public static void cbOnRelease(LegacyOnRelease self, class_11909 e) {
        push(e);
        try {
            self.method_25357(e.comp_4798(), e.comp_4799());
        } finally {
            pop();
        }
    }

    /** AbstractWidget.onDrag(DDDD)V */
    public interface LegacyOnDrag {
        void method_25349(double x, double y, double dx, double dy);
    }

    public static void cbOnDrag(LegacyOnDrag self, class_11909 e, double dx, double dy) {
        push(e);
        try {
            self.method_25349(e.comp_4798(), e.comp_4799(), dx, dy);
        } finally {
            pop();
        }
    }

    /** AbstractWidget.isValidClickButton(I)Z */
    public interface LegacyValidClickButton {
        boolean method_25351(int button);
    }

    public static boolean cbValidClickButton(LegacyValidClickButton self, class_11910 info) {
        return self.method_25351(info.comp_4801());
    }

    /** AbstractButton.onPress()V */
    public interface LegacyOnPress {
        void method_25306();
    }

    public static void cbOnPress(LegacyOnPress self, class_11907 input) {
        push(input);
        try {
            self.method_25306();
        } finally {
            pop();
        }
    }

    /** KeyMapping.matches(II)Z */
    public interface LegacyKeyMatches {
        boolean method_1417(int key, int scancode);
    }

    public static boolean cbKeyMatches(LegacyKeyMatches self, class_11908 e) {
        return self.method_1417(e.comp_4795(), e.comp_4796());
    }

    /** KeyMapping.matchesMouse(I)Z */
    public interface LegacyMouseMatches {
        boolean method_1433(int button);
    }

    public static boolean cbMouseMatches(LegacyMouseMatches self, class_11909 e) {
        return self.method_1433(e.method_74245());
    }

    /** TextFieldHelper.charTyped(C)Z */
    public interface LegacyHelperCharTyped {
        boolean method_16199(char c);
    }

    public static boolean cbHelperCharTyped(LegacyHelperCharTyped self, class_11905 e) {
        push(new class_11908(-1, 0, e.comp_4794()));
        try {
            int cp = e.comp_4793();
            if (Character.isBmpCodePoint(cp)) {
                return self.method_16199((char) cp);
            }
            boolean any = false;
            for (char c : Character.toChars(cp)) {
                any |= self.method_16199(c);
            }
            return any;
        } finally {
            pop();
        }
    }

    /** TextFieldHelper.keyPressed(I)Z */
    public interface LegacyHelperKeyPressed {
        boolean method_16202(int key);
    }

    public static boolean cbHelperKeyPressed(LegacyHelperKeyPressed self, class_11908 e) {
        push(e);
        try {
            return self.method_16202(e.comp_4795());
        } finally {
            pop();
        }
    }

    /** MultilineTextField.keyPressed(I)Z */
    public interface LegacyTextFieldKeyPressed {
        boolean method_44428(int key);
    }

    public static boolean cbTextFieldKeyPressed(LegacyTextFieldKeyPressed self, class_11908 e) {
        push(e);
        try {
            return self.method_44428(e.comp_4795());
        } finally {
            pop();
        }
    }

    /** CommandSuggestions.keyPressed(III)Z */
    public interface LegacySuggestionsKeyPressed {
        boolean method_23924(int key, int scancode, int mods);
    }

    public static boolean cbSuggestionsKeyPressed(LegacySuggestionsKeyPressed self, class_11908 e) {
        push(e);
        try {
            return self.method_23924(e.comp_4795(), e.comp_4796(), e.comp_4797());
        } finally {
            pop();
        }
    }

    /** CommandSuggestions.mouseClicked(DDI)Z */
    public interface LegacySuggestionsMouseClicked {
        boolean method_23922(double x, double y, int button);
    }

    public static boolean cbSuggestionsMouseClicked(LegacySuggestionsMouseClicked self, class_11909 e) {
        push(e);
        try {
            return self.method_23922(e.comp_4798(), e.comp_4799(), e.method_74245());
        } finally {
            pop();
        }
    }

    /** CommandSuggestions.SuggestionsList.keyPressed(III)Z */
    public interface LegacySuggestionsListKeyPressed {
        boolean method_2377(int key, int scancode, int mods);
    }

    public static boolean cbSuggestionsListKeyPressed(LegacySuggestionsListKeyPressed self, class_11908 e) {
        push(e);
        try {
            return self.method_2377(e.comp_4795(), e.comp_4796(), e.comp_4797());
        } finally {
            pop();
        }
    }

    /** AbstractScrollArea.updateScrolling(DDI)Z */
    public interface LegacyUpdateScrolling {
        boolean method_65505(double x, double y, int button);
    }

    public static boolean cbUpdateScrolling(LegacyUpdateScrolling self, class_11909 e) {
        push(e);
        try {
            return self.method_65505(e.comp_4798(), e.comp_4799(), e.method_74245());
        } finally {
            pop();
        }
    }

    /** AbstractContainerScreen.checkHotbarKeyPressed(II)Z */
    public interface LegacyHotbarKey {
        boolean method_2384(int key, int scancode);
    }

    public static boolean cbHotbarKey(LegacyHotbarKey self, class_11908 e) {
        push(e);
        try {
            return self.method_2384(e.comp_4795(), e.comp_4796());
        } finally {
            pop();
        }
    }

    /** AbstractContainerScreen.hasClickedOutside(DDIII)Z: the button of the click being handled */
    public interface LegacyClickedOutside {
        boolean method_2381(double x, double y, int left, int top, int button);
    }

    public static boolean cbClickedOutside(LegacyClickedOutside self, double x, double y, int left, int top) {
        return self.method_2381(x, y, left, top, currentButton());
    }

    /** RecipeBookPage.mouseClicked(DDIIIII)Z */
    public interface LegacyRecipePageClicked {
        boolean method_2632(double x, double y, int button, int left, int top, int width, int height);
    }

    public static boolean cbRecipePageClicked(LegacyRecipePageClicked self, class_11909 e, int left, int top, int width,
                                              int height, boolean doubleClick) {
        push(e);
        try {
            return self.method_2632(e.comp_4798(), e.comp_4799(), e.method_74245(), left, top, width, height);
        } finally {
            pop();
        }
    }

    /** Screen.isValidCharacterForName(String, C, I)Z: a supplementary codepoint is valid when both its chars are */
    public interface LegacyValidNameChar {
        boolean method_25414(String name, char c, int cursor);
    }

    public static boolean cbValidNameChar(LegacyValidNameChar self, String name, int cp, int cursor) {
        if (Character.isBmpCodePoint(cp)) {
            return self.method_25414(name, (char) cp, cursor);
        }
        for (char c : Character.toChars(cp)) {
            if (!self.method_25414(name, c, cursor)) {
                return false;
            }
        }
        return true;
    }
}
