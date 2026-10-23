package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;

/**
 * GameEvent (a record in 1.20.5, reopened for 1.20.4 subclasses by the layer's access widener): 1.20.4's
 * builtInRegistryHolder() and is(tag), back for a subclass's override and super call -- the event's
 * reference in the game event registry, and whether that reference is in the tag.
 */
@Mixin(net.minecraft.class_5712.class)
public abstract class CbFinGameEventMixin {

    /** was builtInRegistryHolder() */
    public net.minecraft.class_6880.class_6883<net.minecraft.class_5712> method_40157() {
        return net.minecraft.class_7923.field_41171.method_40269((net.minecraft.class_5712) (Object) this);
    }

    /** was is(tag) */
    public boolean method_40156(net.minecraft.class_6862<net.minecraft.class_5712> tag) {
        return this.method_40157().method_40220(tag);
    }
}
