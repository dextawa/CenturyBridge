"""Wiring for 1.21.1's consumable item classes gone in 1.21.2 (MilkBucketItem, HoneyBottleItem, OminousBottleItem,
ChorusFruitItem, SuspiciousStewItem, EnchantedBookItem): classRenames to facades that give their items the
components 1.21.2's items of the same name have (rt/CbItemConsumables).

  python gen_s7item_consumables.py <out fragment.json>
"""
import json
import sys

FAC = "top/dext/centurybridge/step/v1_21_2/facade/"
CLASSES = [("class_1805", "MilkBucketItem"), ("class_4480", "HoneyBottleItem"), ("class_9462", "OminousBottleItem"),
           ("class_1757", "ChorusFruitItem"), ("class_1830", "SuspiciousStewItem"), ("class_1772", "EnchantedBookItem")]
USE = ".method_7836(Lnet/minecraft/class_1937;Lnet/minecraft/class_1657;Lnet/minecraft/class_1268;)Lnet/minecraft/class_1271;"
USE_GAP = ("use(level, player, hand) answered an InteractionResultHolder in 1.21.1 (startUsingInstantly); 1.21.2's "
           "Item.use has the same parameters, which Java cannot declare a second time on the facade. Calls take the "
           "Item.use instance redirect and a subclass's super.use(..) the Item.use hook's super bridge (CbItemResults); "
           "1.21.2's Item.use starts consuming an item with CONSUMABLE, which the facade gives its items")


def main():
    out = {"classRenames": {}, "facadeGaps": {}, "instanceRedirects": {}}
    for c, n in CLASSES:
        out["classRenames"]["net/minecraft/" + c] = FAC + n
    for c in ("class_1805", "class_4480", "class_9462"):
        out["facadeGaps"]["net/minecraft/" + c + USE] = USE_GAP
        out["instanceRedirects"]["net/minecraft/" + c + USE] = "top/dext/centurybridge/step/v1_21_2/rt/CbItemConsumables"
    json.dump(out, open(sys.argv[1], "w", encoding="utf-8", newline="\n"), indent=1)


if __name__ == "__main__":
    main()
