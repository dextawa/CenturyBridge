#!/usr/bin/env python3
"""dup_members.py <jar|dir>...: classes declaring two methods (or two fields) with the same name and descriptor.

The JVM refuses such a class (ClassFormatError "Duplicate method name ... with signature ...") when it loads, and
nothing before that notices: javac never emits one, but a rename applied after compilation can -- e.g. past the
1.21.11 -> 26.1 wall RealmsError.method_21037 is errorCode()I, so a layer record with a component errorCode got a
second errorCode()I (1.20.2 CbNetLegacyRealmsError, a crash on the 26.1 title screen). climb_runtime.sh runs this on
every climbed layer jar at every release of the climb and fails when any is found.

Prints one line per duplicate (jar, class, kind, member) and exits 1 when there is any; dirs are walked for *.jar.
"""
import os
import struct
import sys
import zipfile


def duplicates(b):
    """(class name, [(kind, name, descriptor)]) of one class file, or None when it is not one"""
    if b[:4] != b'\xca\xfe\xba\xbe':
        return None
    n = struct.unpack('>H', b[8:10])[0]
    cp = [None] * n
    i, p = 1, 10
    while i < n:
        t = b[p]
        if t == 1:
            ln = struct.unpack('>H', b[p + 1:p + 3])[0]
            cp[i] = b[p + 3:p + 3 + ln].decode('utf-8', 'replace')
            p += 3 + ln
        elif t in (3, 4):
            p += 5
        elif t in (5, 6):
            p += 9
            i += 1
        elif t in (7, 8, 16, 19, 20):
            cp[i] = struct.unpack('>H', b[p + 1:p + 3])[0]
            p += 3
        elif t in (9, 10, 11, 12, 17, 18):
            p += 5
        elif t == 15:
            p += 4
        else:
            return None
        i += 1
    this = cp[cp[struct.unpack('>H', b[p + 2:p + 4])[0]]]
    p += 6
    p += 2 + 2 * struct.unpack('>H', b[p:p + 2])[0]
    out = []
    for kind in ('field', 'method'):
        count = struct.unpack('>H', b[p:p + 2])[0]
        p += 2
        seen = set()
        for _ in range(count):
            ni, di = struct.unpack('>HH', b[p + 2:p + 6])
            p += 6
            ac = struct.unpack('>H', b[p:p + 2])[0]
            p += 2
            for _ in range(ac):
                p += 6 + struct.unpack('>I', b[p + 2:p + 6])[0]
            key = (cp[ni], cp[di])
            if key in seen:
                out.append((kind,) + key)
            seen.add(key)
    return this, out


def jars(args):
    for a in args:
        if os.path.isdir(a):
            for root, _, files in os.walk(a):
                for f in sorted(files):
                    if f.endswith('.jar'):
                        yield os.path.join(root, f)
        else:
            yield a


def main():
    found = classes = 0
    for j in jars(sys.argv[1:]):
        with zipfile.ZipFile(j) as z:
            for e in z.namelist():
                if not e.endswith('.class'):
                    continue
                r = duplicates(z.read(e))
                if r is None:
                    continue
                classes += 1
                for kind, name, desc in r[1]:
                    found += 1
                    print(f"{j}\t{r[0]}\t{kind}\t{name}{desc if kind == 'method' else ':' + desc}")
    print(f"duplicate members: {found} in {classes} classes", file=sys.stderr)
    return 1 if found else 0


if __name__ == '__main__':
    sys.exit(main())
