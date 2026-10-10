package top.dext.centurybridge.facade.v1_20_4;

/**
 * Facade for 1.20.1 net/minecraft/class_2368 (GlassBlock: a concrete AbstractGlassBlock
 * whose only member is the (Properties) constructor), deleted at 1.20.3 when vanilla
 * glass became plain class_8923 (TransparentBlock). Extends the class_4262 facade, hence
 * TransparentBlock, and keeps the public constructor so both direct construction
 * ({@code new GlassBlock(props)}) and subclassing resolve through the classRename.
 * Member names are intermediary tokens.
 */
public class class_2368 extends class_4262 {

    public class_2368(net.minecraft.class_4970.class_2251 properties) {
        super(properties);
    }
}
