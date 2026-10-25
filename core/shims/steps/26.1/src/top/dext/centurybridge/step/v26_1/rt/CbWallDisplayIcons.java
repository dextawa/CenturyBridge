package top.dext.centurybridge.step.v26_1.rt;

import java.util.Optional;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.core.ClientAsset;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

/**
 * 1.21.11 DisplayInfo held its icon as the ItemStack it was built from and getIcon() answered that stack; 26.1 holds
 * an ItemStackTemplate. As with ItemParticleOption (CbWallParticleItems), the layer keeps 1.21.11's stack beside the
 * template (mixin/CbWallDisplayInfoIconMixin): the stack a 1.21.11 constructor took, or one stack made from the
 * template on first ask. 26.1's template cannot be empty, and neither could 1.21.11's icon travel (its codec was
 * ItemStack's strict one), so an empty icon is refused at construction with the reason.
 */
public final class CbWallDisplayIcons {

    private CbWallDisplayIcons() {
    }

    /** the 1.21.11 icon stack kept on a DisplayInfo (mixin/CbWallDisplayInfoIconMixin) */
    public interface Icon {
        ItemStack cb$icon();

        void cb$setIcon(ItemStack icon);

        /** DisplayInfo's own template, read from the field (not through a mod's getIcon override) */
        ItemStackTemplate cb$template();
    }

    /** a mod subclass declaring 1.21.11's getIcon() (the override the layer's hook makes 26.1's getIcon call) */
    private static final ClassValue<Boolean> OVERRIDES = new ClassValue<>() {
        @Override
        protected Boolean computeValue(Class<?> type) {
            for (Class<?> c = type; c != null && c != DisplayInfo.class; c = c.getSuperclass()) {
                for (var m : c.getDeclaredMethods()) {
                    if (m.getName().equals("getIcon") && m.getParameterCount() == 0 && m.getReturnType() == ItemStack.class) {
                        return true;
                    }
                }
            }
            return false;
        }
    };

    private static ItemStackTemplate template(ItemStack icon) {
        if (icon.isEmpty()) {
            throw new IllegalArgumentException("1.21.11 DisplayInfo icon is empty; 26.1's advancement icon is a non-empty item template");
        }
        return ItemStackTemplate.fromNonEmptyStack(icon);
    }

    /** 1.21.11 new DisplayInfo(icon, title, description, background, type, showToast, announceChat, hidden) */
    public static DisplayInfo displayInfo(ItemStack icon, Component title, Component description, Optional<ClientAsset.ResourceTexture> background,
                                          AdvancementType type, boolean showToast, boolean announceChat, boolean hidden) {
        DisplayInfo info = new DisplayInfo(template(icon), title, description, background, type, showToast, announceChat, hidden);
        ((Icon) info).cb$setIcon(icon);
        return info;
    }

    /** a 1.21.11 subclass's super(icon, ...): the template 26.1's constructor takes */
    public static ItemStackTemplate superTemplate(ItemStack icon) {
        return template(icon);
    }

    /** post of a 1.21.11 subclass's super(icon, ...): the stack its getIcon() answers */
    public static void superIcon(DisplayInfo info, ItemStack icon) {
        ((Icon) info).cb$setIcon(icon);
    }

    /** 1.21.11 DisplayInfo.super.getIcon() from a mod subclass: the kept stack */
    public static ItemStack superGetIcon(DisplayInfo info) {
        Icon i = (Icon) info;
        ItemStack icon = i.cb$icon();
        if (icon == null) {
            icon = i.cb$template().create();
            i.cb$setIcon(icon);
        }
        return icon;
    }

    /** 1.21.11 DisplayInfo.getIcon(): a mod subclass's own override answers through 26.1's getIcon, else the kept stack */
    public static ItemStack getIcon(DisplayInfo info) {
        if (info.getClass() != DisplayInfo.class && OVERRIDES.get(info.getClass())) {
            return info.getIcon().create();
        }
        return superGetIcon(info);
    }

    /** 1.21.11 getIcon() override of a mod subclass, unwrapped for 26.1's getIcon() */
    public static ItemStackTemplate unwrap(ItemStack icon) {
        return template(icon);
    }

    /** 1.21.11 Advancement.Builder.display(icon, title, description, background, type, showToast, announceChat, hidden) */
    public static Advancement.Builder display(Advancement.Builder builder, ItemStack icon, Component title, Component description,
                                              Identifier background, AdvancementType type, boolean showToast, boolean announceChat,
                                              boolean hidden) {
        return builder.display(displayInfo(icon, title, description, Optional.ofNullable(background).map(ClientAsset.ResourceTexture::new),
            type, showToast, announceChat, hidden));
    }
}
