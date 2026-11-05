package top.dext.centurybridge.step.v26_1.rt;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.TextComponentTagVisitor;
import net.minecraft.network.chat.Component;

/**
 * 1.21.11 TextComponentTagVisitor's private static constants (class_5628, opened by access wideners). 26.1 moved the
 * syntax tokens into TextComponentTagVisitor.Token (each token's text) and the highlighting into RichStyling (javap of
 * both clinits: Token texts "[", "]", ";", "{", "}", ":", ",", "<...>", "b", "s", "I", "L", "f", "d", "B" are 1.21.11's
 * strings; 1.21.11 coloured keys AQUA, strings GREEN, numbers GOLD, number types RED and the folded marker GRAY, as
 * 26.1's RichStyling does). The type components answer RichStyling's own token components (the same text and style
 * 1.21.11 built). 1.21.11's INT_TYPE was "I" (the int array prefix) and LONG_TYPE "L" (26.1's long suffix and long
 * array prefix share it). 1.21.11's ELEMENT_SPACING " " and NEWLINE "\n" strings are the texts of 26.1's ELEMENT_SPACING
 * / NEWLINE literal components; the separators 1.21.11 composed from ELEMENT_SEPARATOR are composed the same way
 * (its concat recipes: "\u0001\n", "\u0001 "); NAME_VALUE_SEPARATOR ": " is 26.1's separator token then ELEMENT_SPACING.
 */
public final class CbWallTagVisitorFields {
    private CbWallTagVisitorFields() {
    }

    private static Component token(TextComponentTagVisitor.Token t) {
        return TextComponentTagVisitor.RichStyling.INSTANCE.token(t);
    }

    public static ChatFormatting syntaxHighlightingKey() {
        return ChatFormatting.AQUA;
    }

    public static ChatFormatting syntaxHighlightingString() {
        return ChatFormatting.GREEN;
    }

    public static ChatFormatting syntaxHighlightingNumber() {
        return ChatFormatting.GOLD;
    }

    public static ChatFormatting syntaxHighlightingNumberType() {
        return ChatFormatting.RED;
    }

    public static String listOpen() {
        return TextComponentTagVisitor.Token.LIST_OPEN.text;
    }

    public static String listClose() {
        return TextComponentTagVisitor.Token.LIST_CLOSE.text;
    }

    public static String listTypeSeparator() {
        return TextComponentTagVisitor.Token.LIST_TYPE_SEPARATOR.text;
    }

    public static String elementSpacing() {
        return " ";
    }

    public static String structOpen() {
        return TextComponentTagVisitor.Token.STRUCT_OPEN.text;
    }

    public static String structClose() {
        return TextComponentTagVisitor.Token.STRUCT_CLOSE.text;
    }

    public static String newline() {
        return "\n";
    }

    public static String nameValueSeparator() {
        return TextComponentTagVisitor.Token.NAME_VALUE_SEPARATOR.text + elementSpacing();
    }

    public static String elementSeparator() {
        return TextComponentTagVisitor.Token.ELEMENT_SEPARATOR.text;
    }

    public static String wrappedElementSeparator() {
        return elementSeparator() + newline();
    }

    public static String spacedElementSeparator() {
        return elementSeparator() + elementSpacing();
    }

    public static Component folded() {
        return token(TextComponentTagVisitor.Token.FOLDED);
    }

    public static Component byteType() {
        return token(TextComponentTagVisitor.Token.BYTE_SUFFIX);
    }

    public static Component shortType() {
        return token(TextComponentTagVisitor.Token.SHORT_SUFFIX);
    }

    public static Component intType() {
        return token(TextComponentTagVisitor.Token.INT_ARRAY_PREFIX);
    }

    public static Component longType() {
        return token(TextComponentTagVisitor.Token.LONG_SUFFIX);
    }

    public static Component floatType() {
        return token(TextComponentTagVisitor.Token.FLOAT_SUFFIX);
    }

    public static Component doubleType() {
        return token(TextComponentTagVisitor.Token.DOUBLE_SUFFIX);
    }

    public static Component byteArrayType() {
        return token(TextComponentTagVisitor.Token.BYTE_ARRAY_PREFIX);
    }
}
