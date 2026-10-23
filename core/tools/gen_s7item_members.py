"""Wiring for 1.21.1's overridable Item members that 1.21.2 turned into components or final methods
(s7item, step 1.21.1 -> 1.21.2; semantics in CbItemMembers).

Every member gets, for each usage:
  call        instanceRedirects keyed on Item -> CbItemMembers.<name>: the receiver's own override when its
              class has one (the Legacy interface), else what 1.21.1's Item answered, on 1.21.2;
  super call  superRedirects -> CbItemMembers.super_<name>: 1.21.1's Item body;
  override    adapterHooks under Item giving the class the Legacy interface: every call of the member reaches
              the override, and where 1.21.2 reads what 1.21.1 asked the item (enchanting, the anvil, the
              item's components, a consumption's sound, the stack's name) the mixins ask it.
ItemStack's members that forwarded to them are instance redirects of their own.

  python gen_s7item_members.py <out fragment.json>
"""
import json
import sys

RT = "top/dext/centurybridge/step/v1_21_2/rt/CbItemMembers"
ITEM = "net/minecraft/class_1792"
STACK = "net/minecraft/class_1799"

# (member, Legacy interface or None when only a call/super, basis)
MEMBERS = [
    ("method_7837()I", "LegacyEnchantmentValue", "getEnchantmentValue(): the ENCHANTABLE component"),
    ("method_7870(Lnet/minecraft/class_1799;)Z", "LegacyEnchantable", "isEnchantable(stack): ItemStack.isEnchantable"),
    ("method_7878(Lnet/minecraft/class_1799;Lnet/minecraft/class_1799;)Z", "LegacyRepairItem",
     "isValidRepairItem(stack, ingredient): the REPAIRABLE component (anvil)"),
    ("method_7844()Lnet/minecraft/class_9285;", "LegacyDefaultModifiers",
     "getDefaultAttributeModifiers(): the ATTRIBUTE_MODIFIERS component"),
    ("method_21831()Lnet/minecraft/class_3414;", "LegacyDrinkingSound", "getDrinkingSound(): the consumption's sound"),
    ("method_21830()Lnet/minecraft/class_3414;", "LegacyEatingSound", "getEatingSound(): the consumption's sound"),
    ("method_7866(Lnet/minecraft/class_1799;)Ljava/lang/String;", "LegacyDescriptionId",
     "getDescriptionId(stack): the stack's name (Item.getName(stack))"),
    ("method_7848()Lnet/minecraft/class_2561;", "LegacyDescription", "getDescription(): Item.getName()"),
    ("method_7869()Ljava/lang/String;", "LegacyOrCreateDescriptionId",
     "getOrCreateDescriptionId(): 1.21.2's final getDescriptionId() asks it (CbItemMembersMixin)"),
    ("method_16698()Z", "LegacyComplex", "isComplex()"),
    ("method_7857()Z", "LegacyHasRemaining", "hasCraftingRemainingItem(): the remainder stack"),
]
CALL_ONLY = [
    (ITEM, "method_7858()Lnet/minecraft/class_1792;", "getCraftingRemainingItem(): the remainder stack's item, null for none"),
    (STACK, "method_7922()Ljava/lang/String;", "ItemStack.getDescriptionId(): the item's getDescriptionId(stack)"),
    (STACK, "method_21832()Lnet/minecraft/class_3414;", "ItemStack.getDrinkingSound(): the item's"),
    (STACK, "method_21833()Lnet/minecraft/class_3414;", "ItemStack.getEatingSound(): the item's"),
    (STACK, "method_7979(Lnet/minecraft/class_1309;Lnet/minecraft/class_1657;)Z",
     "ItemStack.hurtEnemy(target, player): 1.21.2's hurtEnemy(target, attacker)"),
    (STACK, "method_59979(Lnet/minecraft/class_1309;Lnet/minecraft/class_1657;)V",
     "ItemStack.postHurtEnemy(target, player): 1.21.2's postHurtEnemy(target, attacker)"),
]


def main():
    out = {"instanceRedirects": {}, "superRedirects": {}, "adapterHooks": [], "fieldRedirects": {}}
    for member, legacy, why in MEMBERS:
        name = member[:member.index("(")]
        out["instanceRedirects"][ITEM + "." + member] = RT
        out["superRedirects"][ITEM + "." + member] = RT + "#super_" + name
        out["adapterHooks"].append({"under": ITEM, "adapter": RT + "$" + legacy, "requires": [member], "synth": [],
                                    "basis": "a 1.21.1 item overriding " + why + ": 1.21.2's reads ask it (CbItemMembers)"})
    for owner, member, why in CALL_ONLY:
        out["instanceRedirects"][owner + "." + member] = RT
    # getDescriptionId() is final in 1.21.2: a 1.21.1 override is renamed and the final method hands the call
    # to it (CbItemMembersMixin); its super.getDescriptionId() reaches Item's body (the description id)
    out["overrideRenames"] = [{"old": "method_7876()Ljava/lang/String;", "new": "cb$getDescriptionId", "onlyUnder": ITEM,
                               "adapter": RT + "$LegacyDescriptionIdOverride",
                               "basis": "Item.getDescriptionId() is final since 1.21.2: a 1.21.1 override is renamed and "
                                        "reached through the adapter by the mixin on the final method"}]
    out["superRedirects"][ITEM + ".method_7876()Ljava/lang/String;"] = RT + "#super_method_7876"
    # ItemStack.ITEM_NON_AIR_CODEC (1.21.1) is 1.21.2's Item.CODEC: the item registry's holder codec
    # validated against air, the same body
    out["fieldRedirects"][STACK + ".field_47312:Lcom/mojang/serialization/Codec;"] = ITEM + "#field_54952"
    json.dump(out, open(sys.argv[1], "w", encoding="utf-8", newline="\n"), indent=1)


if __name__ == "__main__":
    main()
