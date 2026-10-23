"""Wiring for 1.21.1's ArmorTrim members 1.21.2's record dropped (s7item, step 1.21.1 -> 1.21.2; rt/CbItemTrims),
and the ledger for subclassing it (1.21.2 made it a final record).

  python gen_s7item_trims.py <out fragment.json>
"""
import json
import sys

RT = "top/dext/centurybridge/step/v1_21_2/rt/CbItemTrims"
TRIM = "net/minecraft/class_8053"
MEMBERS = [
    "method_48424()Lnet/minecraft/class_6880;",
    "method_48431()Lnet/minecraft/class_6880;",
    "method_48434(Lnet/minecraft/class_6880;)Lnet/minecraft/class_2960;",
    "method_48436(Lnet/minecraft/class_6880;)Lnet/minecraft/class_2960;",
]
WHY = ("1.21.2 made ArmorTrim a final record (javap: public final class class_8053 extends Record): the TRIM component's "
       "codec and stream codec build class_8053 records and every reader of the component gets one, so a 1.21.1 "
       "subclass can neither load nor stand where 1.21.1 put its instance; a stand-in superclass would split the type "
       "at every DataComponents.TRIM read (ClassCastException). ArmorTrim's 1.21.1 members are bridged on the record "
       "(CbItemTrims)")


def main():
    out = {"instanceRedirects": {}, "superRedirects": {}, "ledger": {
        TRIM: "a 1.21.1 subclass of ArmorTrim: " + WHY,
        TRIM + ".equals(Ljava/lang/Object;)Z": "equals override of a 1.21.1 subclass of ArmorTrim (final on the record): " + WHY,
        TRIM + ".hashCode()I": "hashCode override of a 1.21.1 subclass of ArmorTrim (final on the record): " + WHY,
    }}
    for m in MEMBERS:
        out["instanceRedirects"][TRIM + "." + m] = RT
        out["superRedirects"][TRIM + "." + m] = RT
    json.dump(out, open(sys.argv[1], "w", encoding="utf-8", newline="\n"), indent=1)


if __name__ == "__main__":
    main()
