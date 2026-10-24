"""Port log4j-core 2.19.0's FastDateParser and DateParser (dropped by 2.22.1, 1.20.5) into the 1.20.5 step layer.

  python3 port_log4j_datetime.py <log4j-core 2.19.0 source dir: .../org/apache/logging/log4j/core/util/datetime> <out dir>

The source of the version 1.20.4 shipped (log4j-core-2.19.0-sources.jar, Apache License 2.0), renamed into the layer's
rt package (CbLibLog4jFastDateParser, CbLibLog4jDateParser); every body as 2.19.0's.
"""
import sys

src, out = sys.argv[1:3]
NAMES = {"FastDateParser": "CbLibLog4jFastDateParser", "DateParser": "CbLibLog4jDateParser"}
for name, cb in NAMES.items():
    s = open(f"{src}/{name}.java", encoding="utf-8").read()
    s = s.replace("package org.apache.logging.log4j.core.util.datetime;",
                  "package top.dext.centurybridge.step.v1_20_5.rt;\n\n"
                  f"// log4j-core 2.19.0's org.apache.logging.log4j.core.util.datetime.{name} (2.22.1 dropped it), renamed by\n"
                  "// core/tools/port_log4j_datetime.py; otherwise 2.19.0's source.\n\n"
                  "import org.apache.logging.log4j.core.util.datetime.FastDateFormat;", 1)
    s = s.replace("FastDateParser", "\0F").replace("DateParser", NAMES["DateParser"]).replace("\0F", NAMES["FastDateParser"])
    open(f"{out}/{cb}.java", "w", encoding="utf-8", newline="\n").write(s)
    print(cb)
