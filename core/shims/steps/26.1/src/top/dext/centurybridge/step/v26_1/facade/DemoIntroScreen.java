package top.dext.centurybridge.step.v26_1.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Options;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonLinks;
import net.minecraft.util.Util;

/**
 * 1.21.11's DemoIntroScreen (class_417) on 26.1. 26.1 dropped the class: ClientPacketListener.openDemoIntroScreen
 * builds the same help (demo.help.title, the movement/jump/inventory lines, demo.help.fullWrapped, the buy and
 * later buttons) as a PopupScreen. A mod's DemoIntroScreen (a subclass, or one it opens) gets 1.21.11's screen:
 * the bodies below are 1.21.11's (javap class_417), its members keep their intermediary names (a subclass
 * overrides and calls them; 26.1's init, extractBackground and extractRenderState go through them), public.
 * <p>
 * The panel: 1.21.11 drew textures/gui/demo_background.png (248x166), which 26.1's client jar no longer ships;
 * the panel is drawn with 26.1's popup background sprite (PopupScreen.BACKGROUND_SPRITE, popup/background), the
 * texture 26.1's demo help is drawn on, over the same rectangle. field_2447 keeps 1.21.11's value for mods that read it.
 */
@Environment(EnvType.CLIENT)
public class DemoIntroScreen extends Screen {
    /** DEMO_BACKGROUND_LOCATION (1.21.11's value) */
    public static final Identifier field_2447 = Identifier.withDefaultNamespace("textures/gui/demo_background.png");
    /** BACKGROUND_TEXTURE_WIDTH */
    public static final int field_52797 = 256;
    /** BACKGROUND_TEXTURE_HEIGHT */
    public static final int field_52798 = 256;
    /** 1.21.11's text colour (javap -constants class_417) */
    public static final int field_63895 = -14737633;
    /** PopupScreen.BACKGROUND_SPRITE (javap PopupScreen.&lt;clinit&gt;) */
    private static final Identifier PANEL = Identifier.withDefaultNamespace("popup/background");

    /** movementMessage */
    public MultiLineLabel field_26538 = MultiLineLabel.EMPTY;
    /** durationMessage */
    public MultiLineLabel field_26539 = MultiLineLabel.EMPTY;

    public DemoIntroScreen() {
        super(Component.translatable("demo.help.title"));
    }

    // ---- 26.1's screen methods, answered by 1.21.11's ----

    @Override
    protected void init() {
        this.method_25426();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        this.method_25420(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        this.method_25394(graphics, mouseX, mouseY, partialTick);
    }

    // ---- 1.21.11's members ----

    /** init() */
    public void method_25426() {
        this.addRenderableWidget(Button.builder(Component.translatable("demo.help.buy"), DemoIntroScreen::method_19811)
            .bounds(this.width / 2 - 116, this.height / 2 + 62 - 16, 114, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("demo.help.later"), this::method_19810)
            .bounds(this.width / 2 + 2, this.height / 2 + 62 - 16, 114, 20).build());
        Options options = this.minecraft.options;
        this.field_26538 = MultiLineLabel.create(this.font,
            this.method_75827(Component.translatable("demo.help.movementShort", options.keyUp.getTranslatedKeyMessage(),
                options.keyLeft.getTranslatedKeyMessage(), options.keyDown.getTranslatedKeyMessage(), options.keyRight.getTranslatedKeyMessage())),
            this.method_75827(Component.translatable("demo.help.movementMouse")),
            this.method_75827(Component.translatable("demo.help.jump", options.keyJump.getTranslatedKeyMessage())),
            this.method_75827(Component.translatable("demo.help.inventory", options.keyInventory.getTranslatedKeyMessage())));
        this.field_26539 = MultiLineLabel.create(this.font, Component.translatable("demo.help.fullWrapped").withoutShadow().withColor(field_63895), 218);
    }

    /** movementMessage(component) */
    public Component method_75827(MutableComponent component) {
        return component.withoutShadow().withColor(-11579569);
    }

    /** renderBackground(graphics, mouseX, mouseY, partialTick) */
    public void method_25420(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int x = (this.width - 248) / 2;
        int y = (this.height - 166) / 2;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, PANEL, x, y, 248, 166);
    }

    /** render(graphics, mouseX, mouseY, partialTick) */
    public void method_25394(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        int x = (this.width - 248) / 2 + 10;
        int y = (this.height - 166) / 2 + 8;
        ActiveTextCollector text = graphics.textRenderer();
        graphics.text(this.font, this.title, x, y, field_63895, false);
        y = this.field_26538.visitLines(TextAlignment.LEFT, x, y + 12, 12, text);
        this.field_26539.visitLines(TextAlignment.LEFT, x, y + 20, 9, text);
    }

    /** the later button: back to the game */
    public void method_19810(Button button) {
        this.minecraft.setScreen(null);
        this.minecraft.mouseHandler.grabMouse();
    }

    /** the buy button: opens the store page */
    public static void method_19811(Button button) {
        button.active = false;
        Util.getPlatform().openUri(CommonLinks.BUY_MINECRAFT_JAVA);
    }
}
