package top.dext.centurybridge.step.v26_1.rt;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import top.dext.centurybridge.step.v26_1.compat.fapi.gamerule.v1.CustomGameRuleCategory;
import top.dext.centurybridge.step.v26_1.mixin.CbFapiGameRuleCategoryAccess;

/**
 * 1.21.11's Fabric API custom game rule categories on 26.1, whose Fabric API removed CustomGameRuleCategory
 * and GameRuleBuilder.category(CustomGameRuleCategory) / the protected fabricCategory field: 26.1 vanilla
 * categories take any id instead (GameRuleCategory.register(Identifier), listed after the vanilla ones,
 * headed by label()).
 *
 * 1.21.11: category(custom) set the vanilla category to MISC and remembered the custom one; build() tied it
 * to the rule; the edit-rules screen listed such rules under the custom name, after the vanilla
 * categories. Here the custom category is a vanilla category of the same id (registered once, or the one
 * already registered under that id), so 26.1's screen lists the rule under it in the same place, and its
 * label() is the custom name (CbFapiGameRuleCategoryMixin). The one visible difference: the rule's
 * GameRule.category() is that category rather than MISC -- 26.1 groups the screen by category() and keeps
 * no other place a rule could carry a second one. 26.1 styles every category header bold yellow, the
 * custom one included (1.21.11 drew the custom name as given).
 */
public final class CbFapiGameRules {
    private CbFapiGameRules() {
    }

    /** builder -> its custom category: 1.21.11's GameRuleBuilder.fabricCategory */
    private static final Map<GameRuleBuilder<?>, CustomGameRuleCategory> FABRIC_CATEGORY = new WeakHashMap<>();
    /** the vanilla category standing for a custom one, and back (first custom category of the id) */
    private static final Map<Identifier, GameRuleCategory> DISPLAY = new HashMap<>();
    private static final Map<GameRuleCategory, CustomGameRuleCategory> CUSTOM = new HashMap<>();

    /** GameRuleBuilder.category(CustomGameRuleCategory), called on any builder: a mod subclass's override runs */
    @SuppressWarnings({"rawtypes"})
    public static GameRuleBuilder category(GameRuleBuilder builder, CustomGameRuleCategory category) {
        MethodHandle override = ModOverride.OF.get(builder.getClass());
        if (override == null) {
            return categoryBase(builder, category);
        }
        try {
            return (GameRuleBuilder) override.invokeExact(builder, category);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    /** a mod subclass's super.category(custom): 1.21.11's GameRuleBuilder body */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static GameRuleBuilder categoryBase(GameRuleBuilder builder, CustomGameRuleCategory category) {
        builder.category(category == null ? GameRuleCategory.MISC : display(category));
        synchronized (FABRIC_CATEGORY) {
            if (category == null) {
                FABRIC_CATEGORY.remove(builder);
            } else {
                FABRIC_CATEGORY.put(builder, category);
            }
        }
        return builder;
    }

    // the Fabric API subclasses are final: their covariant category(custom) is the base body
    public static GameRuleBuilder.BooleanRuleBuilder category(GameRuleBuilder.BooleanRuleBuilder builder, CustomGameRuleCategory category) {
        categoryBase(builder, category);
        return builder;
    }

    public static GameRuleBuilder.IntegerRuleBuilder category(GameRuleBuilder.IntegerRuleBuilder builder, CustomGameRuleCategory category) {
        categoryBase(builder, category);
        return builder;
    }

    public static GameRuleBuilder.DoubleRuleBuilder category(GameRuleBuilder.DoubleRuleBuilder builder, CustomGameRuleCategory category) {
        categoryBase(builder, category);
        return builder;
    }

    public static <E extends Enum<E>> GameRuleBuilder.EnumRuleBuilder<E> category(GameRuleBuilder.EnumRuleBuilder<E> builder, CustomGameRuleCategory category) {
        categoryBase(builder, category);
        return builder;
    }

    /** read of the protected GameRuleBuilder.fabricCategory */
    public static CustomGameRuleCategory getFabricCategory(GameRuleBuilder<?> builder) {
        synchronized (FABRIC_CATEGORY) {
            return FABRIC_CATEGORY.get(builder);
        }
    }

    /**
     * write of the protected GameRuleBuilder.fabricCategory: 1.21.11's build() tied whatever the field held
     * to the rule, the vanilla category untouched; null leaves the rule without a custom one, so a builder
     * this bridge had put in a custom category's stand-in goes back to MISC (what category(custom) had set)
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void setFabricCategory(GameRuleBuilder builder, CustomGameRuleCategory category) {
        if (category != null) {
            builder.category(display(category));
            synchronized (FABRIC_CATEGORY) {
                FABRIC_CATEGORY.put(builder, category);
            }
            return;
        }
        synchronized (FABRIC_CATEGORY) {
            FABRIC_CATEGORY.remove(builder);
        }
        GameRuleCategory current = vanillaCategory(builder);
        boolean standIn;
        synchronized (DISPLAY) {
            standIn = current != null && CUSTOM.containsKey(current);
        }
        if (standIn) {
            builder.category(GameRuleCategory.MISC);
        }
    }

    /** CustomGameRuleCategory.getCategory(rule) */
    public static CustomGameRuleCategory customCategoryOf(GameRule<?> rule) {
        synchronized (DISPLAY) {
            return CUSTOM.get(rule.category());
        }
    }

    /** the custom name heading a stand-in category on 26.1's screen, or null for any other category */
    public static Component customName(GameRuleCategory category) {
        CustomGameRuleCategory custom;
        synchronized (DISPLAY) {
            custom = CUSTOM.get(category);
        }
        return custom == null ? null : custom.getName();
    }

    private static GameRuleCategory display(CustomGameRuleCategory custom) {
        synchronized (DISPLAY) {
            GameRuleCategory vanilla = DISPLAY.get(custom.getId());
            if (vanilla == null) {
                GameRuleCategory same = new GameRuleCategory(custom.getId());
                // a category of that id another mod already registered is the same category (a record of its id)
                vanilla = CbFapiGameRuleCategoryAccess.cb$sortOrder().contains(same) ? same : GameRuleCategory.register(custom.getId());
                DISPLAY.put(custom.getId(), vanilla);
                CUSTOM.putIfAbsent(vanilla, custom);
            }
            return vanilla;
        }
    }

    private static GameRuleCategory vanillaCategory(GameRuleBuilder<?> builder) {
        try {
            return (GameRuleCategory) Fields.CATEGORY.invoke(builder);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    /** GameRuleBuilder's protected category field (a Fabric API class: no access widener reaches it) */
    private static final class Fields {
        static final MethodHandle CATEGORY;

        static {
            try {
                CATEGORY = MethodHandles.privateLookupIn(GameRuleBuilder.class, MethodHandles.lookup())
                    .findGetter(GameRuleBuilder.class, "category", GameRuleCategory.class);
            } catch (ReflectiveOperationException e) {
                throw new ExceptionInInitializerError(e);
            }
        }
    }

    /**
     * A mod class's own category(CustomGameRuleCategory) -- after the class rename its parameter is this
     * layer's CustomGameRuleCategory -- declared below the Fabric API classes, or null: 1.21.11 dispatched
     * the call virtually, so the override answers it.
     */
    private static final class ModOverride {
        static final ClassValue<MethodHandle> OF = new ClassValue<>() {
            @Override
            protected MethodHandle computeValue(Class<?> type) {
                for (Class<?> c = type; c != null && c != Object.class && !c.getName().startsWith("net.fabricmc.fabric."); c = c.getSuperclass()) {
                    Method m;
                    try {
                        m = c.getDeclaredMethod("category", CustomGameRuleCategory.class);
                    } catch (NoSuchMethodException e) {
                        continue;
                    }
                    if (Modifier.isStatic(m.getModifiers()) || Modifier.isPrivate(m.getModifiers())
                            || !GameRuleBuilder.class.isAssignableFrom(m.getReturnType())) {
                        continue;
                    }
                    try {
                        m.setAccessible(true);
                        return MethodHandles.lookup().unreflect(m)
                            .asType(MethodType.methodType(GameRuleBuilder.class, GameRuleBuilder.class, CustomGameRuleCategory.class));
                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException("cannot reach " + m, e);
                    }
                }
                return null;
            }
        };
    }
}
