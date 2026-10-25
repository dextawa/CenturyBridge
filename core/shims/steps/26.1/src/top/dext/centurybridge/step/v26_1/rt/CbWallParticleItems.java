package top.dext.centurybridge.step.v26_1.rt;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

/**
 * 1.21.11 ItemParticleOption held the ItemStack it was built from and getItem() answered that stack; 26.1 holds an
 * ItemStackTemplate (immutable, non-empty) and getItem() answers it. The layer keeps 1.21.11's stack beside the
 * template (mixin/CbWallItemParticleStackMixin): the stack a 1.21.11 constructor took, or -- for an option 26.1 built
 * from a template -- one stack made from the template on first ask, so getItem() answers the same stack every time as
 * it did. 1.21.11 refused an empty stack in the constructor ("Empty stacks are not allowed"), as 26.1's template does.
 */
public final class CbWallParticleItems {

    private CbWallParticleItems() {
    }

    /** the 1.21.11 stack kept on an ItemParticleOption (mixin/CbWallItemParticleStackMixin) */
    public interface Stack {
        /** the stack a 1.21.11 constructor took, null when 26.1 built the option */
        ItemStack cb$stack();

        void cb$setStack(ItemStack stack);

        /** ItemParticleOption's own template, read from the field (not through a mod's getItem override) */
        ItemStackTemplate cb$template();
    }

    /** a mod subclass declaring 1.21.11's getItem() (the override the layer's hook makes 26.1's getItem call) */
    private static final ClassValue<Boolean> OVERRIDES = new ClassValue<>() {
        @Override
        protected Boolean computeValue(Class<?> type) {
            for (Class<?> c = type; c != null && c != ItemParticleOption.class; c = c.getSuperclass()) {
                for (var m : c.getDeclaredMethods()) {
                    if (m.getName().equals("getItem") && m.getParameterCount() == 0 && m.getReturnType() == ItemStack.class) {
                        return true;
                    }
                }
            }
            return false;
        }
    };

    private static ItemStackTemplate template(ItemStack stack) {
        if (stack.isEmpty()) {
            throw new IllegalArgumentException("Empty stacks are not allowed");
        }
        return ItemStackTemplate.fromNonEmptyStack(stack);
    }

    /** 1.21.11 new ItemParticleOption(type, stack) */
    public static ItemParticleOption itemParticle(ParticleType<ItemParticleOption> type, ItemStack stack) {
        ItemParticleOption option = new ItemParticleOption(type, template(stack));
        ((Stack) option).cb$setStack(stack);
        return option;
    }

    /** a 1.21.11 subclass's super(type, stack): the template 26.1's constructor takes */
    public static ItemStackTemplate superTemplate(ItemStack stack) {
        return template(stack);
    }

    /** post of a 1.21.11 subclass's super(type, stack): the stack its getItem() answers */
    public static void superStack(ItemParticleOption option, ItemStack stack) {
        ((Stack) option).cb$setStack(stack);
    }

    /** 1.21.11 ItemParticleOption.super.getItem() from a mod subclass: the kept stack */
    public static ItemStack superGetItem(ItemParticleOption option) {
        Stack s = (Stack) option;
        ItemStack stack = s.cb$stack();
        if (stack == null) {
            stack = s.cb$template().create();
            s.cb$setStack(stack);
        }
        return stack;
    }

    /**
     * 1.21.11 ItemParticleOption.getItem(): a mod subclass's own override answers through 26.1's getItem (the hook
     * hands back its stack's template), everything else answers the kept stack
     */
    public static ItemStack getItem(ItemParticleOption option) {
        if (option.getClass() != ItemParticleOption.class && OVERRIDES.get(option.getClass())) {
            return option.getItem().create();
        }
        return superGetItem(option);
    }

    /** 1.21.11 getItem() override of a mod subclass, unwrapped for 26.1's getItem() */
    public static ItemStackTemplate unwrap(ItemStack stack) {
        return template(stack);
    }
}
