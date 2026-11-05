package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.LlamaRenderer;
import net.minecraft.client.renderer.entity.RabbitRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.equine.Llama;
import net.minecraft.world.entity.animal.rabbit.Rabbit;

/**
 * 1.21.11 RabbitRenderer / LlamaRenderer per-variant texture constants (class_939 field_4768..4775, class_921
 * field_41631..41634, opened by access wideners). 26.1 keeps the same textures in per-variant maps
 * (RabbitRenderer.RABBIT_LOCATIONS + TOAST, LlamaRenderer.TEXTURES) and renamed the files: 1.21.11's
 * textures/entity/rabbit/brown.png is 26.1's rabbit_brown.png, caerbannog.png rabbit_caerbannog.png, llama/creamy.png
 * llama_creamy.png (javap of both clinits; client-26.1.jar ships only the new names). Each getter answers 26.1's
 * adult texture of the variant, the image the 1.21.11 identifier named.
 */
@Environment(EnvType.CLIENT)
public final class CbWallEntityTextures {
    private CbWallEntityTextures() {
    }

    private static Identifier rabbit(Rabbit.Variant v) {
        return RabbitRenderer.RABBIT_LOCATIONS.get(v);
    }

    private static Identifier llama(Llama.Variant v) {
        return LlamaRenderer.TEXTURES.get(v);
    }

    public static Identifier rabbitBrown() {
        return rabbit(Rabbit.Variant.BROWN);
    }

    public static Identifier rabbitWhite() {
        return rabbit(Rabbit.Variant.WHITE);
    }

    public static Identifier rabbitBlack() {
        return rabbit(Rabbit.Variant.BLACK);
    }

    public static Identifier rabbitGold() {
        return rabbit(Rabbit.Variant.GOLD);
    }

    public static Identifier rabbitSalt() {
        return rabbit(Rabbit.Variant.SALT);
    }

    public static Identifier rabbitWhiteSplotched() {
        return rabbit(Rabbit.Variant.WHITE_SPLOTCHED);
    }

    public static Identifier rabbitToast() {
        return RabbitRenderer.TOAST;
    }

    public static Identifier rabbitEvil() {
        return rabbit(Rabbit.Variant.EVIL);
    }

    public static Identifier llamaCreamy() {
        return llama(Llama.Variant.CREAMY);
    }

    public static Identifier llamaWhite() {
        return llama(Llama.Variant.WHITE);
    }

    public static Identifier llamaBrown() {
        return llama(Llama.Variant.BROWN);
    }

    public static Identifier llamaGray() {
        return llama(Llama.Variant.GRAY);
    }
}
