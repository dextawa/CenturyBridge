package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Style's raw fields and constructor, as 1.20.2's serializers and FORMATTING_CODEC read and built them:
 * the getters 1.20.3 has fold an unset (null) flag into false, and the constructor is private.
 */
@Mixin(net.minecraft.class_2583.class)
public interface CbStyleAccess {

    @Accessor("field_11855")
    net.minecraft.class_5251 cb$color();

    @Accessor("field_11856")
    Boolean cb$bold();

    @Accessor("field_11852")
    Boolean cb$italic();

    @Accessor("field_11851")
    Boolean cb$underlined();

    @Accessor("field_11857")
    Boolean cb$strikethrough();

    @Accessor("field_11861")
    Boolean cb$obfuscated();

    @Accessor("field_11853")
    net.minecraft.class_2558 cb$clickEvent();

    @Accessor("field_11858")
    net.minecraft.class_2568 cb$hoverEvent();

    @Accessor("field_11859")
    String cb$insertion();

    @Accessor("field_24361")
    net.minecraft.class_2960 cb$font();

    @Invoker("<init>")
    static net.minecraft.class_2583 cb$new(net.minecraft.class_5251 color, Boolean bold, Boolean italic, Boolean underlined,
                                           Boolean strikethrough, Boolean obfuscated, net.minecraft.class_2558 click,
                                           net.minecraft.class_2568 hover, String insertion, net.minecraft.class_2960 font) {
        throw new AssertionError("mixin invoker");
    }
}
