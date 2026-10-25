#!/usr/bin/env python3
"""client_input.py "<sequence>": real X input for a headless client test (boot_client_remote.sh runs it inside
xvfb-run when the client log shows "input ready: <sequence>"). XTest through ctypes (libX11 + libXtst), no tools.

Sequence: commands separated by ';', coordinates in root-window pixels (the client window fills the screen):
  key [shift+|ctrl+]<keysym>   press and release a key (keysym as X names it: a, F9, Return)
  char U+<hex>                 type one Unicode character (a spare keycode is mapped to its keysym)
  move <x> <y>                 move the pointer
  down <b> / up <b>            press / release mouse button b (1 left, 2 middle, 3 right)
  click <b> / shiftclick <b>   press and release b (with Shift held)
  drag <x> <y> <steps>         move to x, y in that many steps (a button held with down drags)
  sleep <ms>
"""
import ctypes
import sys
import time

x11 = ctypes.cdll.LoadLibrary("libX11.so.6")
xt = ctypes.cdll.LoadLibrary("libXtst.so.6")
x11.XOpenDisplay.restype = ctypes.c_void_p
x11.XOpenDisplay.argtypes = [ctypes.c_char_p]
x11.XStringToKeysym.restype = ctypes.c_ulong
x11.XStringToKeysym.argtypes = [ctypes.c_char_p]
x11.XKeysymToKeycode.restype = ctypes.c_ubyte
x11.XKeysymToKeycode.argtypes = [ctypes.c_void_p, ctypes.c_ulong]
x11.XFlush.argtypes = [ctypes.c_void_p]
x11.XSync.argtypes = [ctypes.c_void_p, ctypes.c_int]
x11.XDisplayKeycodes.argtypes = [ctypes.c_void_p, ctypes.POINTER(ctypes.c_int), ctypes.POINTER(ctypes.c_int)]
x11.XChangeKeyboardMapping.argtypes = [ctypes.c_void_p, ctypes.c_int, ctypes.c_int, ctypes.POINTER(ctypes.c_ulong), ctypes.c_int]
xt.XTestFakeKeyEvent.argtypes = [ctypes.c_void_p, ctypes.c_uint, ctypes.c_int, ctypes.c_ulong]
xt.XTestFakeButtonEvent.argtypes = [ctypes.c_void_p, ctypes.c_uint, ctypes.c_int, ctypes.c_ulong]
xt.XTestFakeMotionEvent.argtypes = [ctypes.c_void_p, ctypes.c_int, ctypes.c_int, ctypes.c_int, ctypes.c_ulong]

STEP = 0.15
d = x11.XOpenDisplay(None)
if not d:
    sys.exit("cannot open display")
pos = [0, 0]


def sync():
    x11.XSync(d, 0)
    time.sleep(STEP)


def keycode(name):
    sym = x11.XStringToKeysym(name.encode())
    code = x11.XKeysymToKeycode(d, sym)
    if not code:
        raise ValueError("no keycode for " + name)
    return code


def key(code, press):
    xt.XTestFakeKeyEvent(d, code, 1 if press else 0, 0)
    sync()


def button(b, press):
    xt.XTestFakeButtonEvent(d, b, 1 if press else 0, 0)
    sync()


def move(x, y):
    pos[0], pos[1] = x, y
    xt.XTestFakeMotionEvent(d, -1, x, y, 0)
    sync()


def run(cmd):
    p = cmd.split()
    if not p:
        return
    op = p[0]
    if op == "key":
        *mods, name = p[1].split("+")
        held = [keycode({"shift": "Shift_L", "ctrl": "Control_L"}[m]) for m in mods]
        for m in held:
            key(m, True)
        k = keycode(name)
        key(k, True)
        key(k, False)
        for m in reversed(held):
            key(m, False)
    elif op == "char":
        cp = int(p[1][2:], 16)
        lo, hi = ctypes.c_int(), ctypes.c_int()
        x11.XDisplayKeycodes(d, ctypes.byref(lo), ctypes.byref(hi))
        spare = hi.value
        syms = (ctypes.c_ulong * 1)(0x01000000 | cp)
        x11.XChangeKeyboardMapping(d, spare, 1, syms, 1)
        sync()
        key(spare, True)
        key(spare, False)
    elif op == "move":
        move(int(p[1]), int(p[2]))
    elif op in ("down", "up"):
        button(int(p[1]), op == "down")
    elif op in ("click", "shiftclick"):
        shift = keycode("Shift_L")
        if op == "shiftclick":
            key(shift, True)
        button(int(p[1]), True)
        button(int(p[1]), False)
        if op == "shiftclick":
            key(shift, False)
    elif op == "drag":
        x, y, n = int(p[1]), int(p[2]), int(p[3])
        x0, y0 = pos
        for i in range(1, n + 1):
            move(x0 + (x - x0) * i // n, y0 + (y - y0) * i // n)
    elif op == "sleep":
        time.sleep(int(p[1]) / 1000)
    else:
        raise ValueError("unknown command " + op)


for c in sys.argv[1].split(";"):
    run(c.strip())
    print("done:", c.strip(), flush=True)
x11.XFlush(d)
