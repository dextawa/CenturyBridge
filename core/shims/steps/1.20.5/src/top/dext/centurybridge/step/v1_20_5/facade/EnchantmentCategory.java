package top.dext.centurybridge.step.v1_20_5.facade;

/**
 * EnchantmentCategory (gone in 1.20.5, which keys an enchantment's items on an item tag instead):
 * 1.20.4's fourteen categories, constants under 1.20.4's intermediary names in 1.20.4's order, each
 * answering canEnchant(item) by 1.20.4's rule. The rules look at the item's class as 1.20.4 had it,
 * so the classes 1.20.5 moved under one of them are kept out: 1.20.5's AnimalArmorItem (1.20.4's
 * HorseArmorItem, a plain item) now extends ArmorItem, and its wool carpets are Equipable.
 */
public enum EnchantmentCategory {
    /** ARMOR */
    field_9068 {
        @Override
        public boolean method_8177(net.minecraft.class_1792 item) {
            return armor(item);
        }
    },
    /** ARMOR_FEET */
    field_9079 {
        @Override
        public boolean method_8177(net.minecraft.class_1792 item) {
            return armor(item) && ((net.minecraft.class_1738) item).method_7685() == net.minecraft.class_1304.field_6166;
        }
    },
    /** ARMOR_LEGS */
    field_9076 {
        @Override
        public boolean method_8177(net.minecraft.class_1792 item) {
            return armor(item) && ((net.minecraft.class_1738) item).method_7685() == net.minecraft.class_1304.field_6172;
        }
    },
    /** ARMOR_CHEST */
    field_9071 {
        @Override
        public boolean method_8177(net.minecraft.class_1792 item) {
            return armor(item) && ((net.minecraft.class_1738) item).method_7685() == net.minecraft.class_1304.field_6174;
        }
    },
    /** ARMOR_HEAD */
    field_9080 {
        @Override
        public boolean method_8177(net.minecraft.class_1792 item) {
            return armor(item) && ((net.minecraft.class_1738) item).method_7685() == net.minecraft.class_1304.field_6169;
        }
    },
    /** WEAPON */
    field_9074 {
        @Override
        public boolean method_8177(net.minecraft.class_1792 item) {
            return item instanceof net.minecraft.class_1829;
        }
    },
    /** DIGGER */
    field_9069 {
        @Override
        public boolean method_8177(net.minecraft.class_1792 item) {
            return item instanceof net.minecraft.class_1766;
        }
    },
    /** FISHING_ROD */
    field_9072 {
        @Override
        public boolean method_8177(net.minecraft.class_1792 item) {
            return item instanceof net.minecraft.class_1787;
        }
    },
    /** TRIDENT */
    field_9073 {
        @Override
        public boolean method_8177(net.minecraft.class_1792 item) {
            return item instanceof net.minecraft.class_1835;
        }
    },
    /** BREAKABLE: the item can be depleted (its own 1.20.4 canBeDepleted where it overrides it) */
    field_9082 {
        @Override
        public boolean method_8177(net.minecraft.class_1792 item) {
            return top.dext.centurybridge.step.v1_20_5.rt.CbItemProps.canBeDepleted(item);
        }
    },
    /** BOW */
    field_9070 {
        @Override
        public boolean method_8177(net.minecraft.class_1792 item) {
            return item instanceof net.minecraft.class_1753;
        }
    },
    /** WEARABLE: the item, or the block it places, is Equipable */
    field_9078 {
        @Override
        public boolean method_8177(net.minecraft.class_1792 item) {
            return equipable(item) || equipable(net.minecraft.class_2248.method_9503(item));
        }
    },
    /** CROSSBOW */
    field_9081 {
        @Override
        public boolean method_8177(net.minecraft.class_1792 item) {
            return item instanceof net.minecraft.class_1764;
        }
    },
    /** VANISHABLE: the item, or the block it places, is Vanishable -- or the item can be depleted */
    field_23747 {
        @Override
        public boolean method_8177(net.minecraft.class_1792 item) {
            return vanishable(item) || vanishable(net.minecraft.class_2248.method_9503(item)) || field_9082.method_8177(item);
        }
    };

    /** canEnchant(item) */
    public abstract boolean method_8177(net.minecraft.class_1792 item);

    /** an ArmorItem in 1.20.4's sense */
    private static boolean armor(net.minecraft.class_1792 item) {
        return item instanceof net.minecraft.class_1738 && !(item instanceof net.minecraft.class_4059);
    }

    /** Equipable in 1.20.4's sense (an item or a block) */
    private static boolean equipable(Object o) {
        return o instanceof net.minecraft.class_5151 && !(o instanceof net.minecraft.class_4059) && !(o instanceof net.minecraft.class_5815);
    }

    /**
     * Vanishable in 1.20.4's sense: a class implementing it (a 1.20.4 mod's), every Equipable (1.20.4's
     * Equipable extended Vanishable), and the vanilla classes 1.20.4 declared it on -- bows, compasses,
     * crossbows, digging tools, fishing rods, swords and tridents.
     */
    private static boolean vanishable(Object o) {
        return o instanceof Vanishable || equipable(o)
            || o instanceof net.minecraft.class_1753 || o instanceof net.minecraft.class_1759 || o instanceof net.minecraft.class_1764
            || o instanceof net.minecraft.class_1766 || o instanceof net.minecraft.class_1787 || o instanceof net.minecraft.class_1829
            || o instanceof net.minecraft.class_1835;
    }
}
