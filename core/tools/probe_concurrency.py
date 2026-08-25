"""Measure what the endpoint actually sustains before picking a worker count."""
import os, sys, time, json
sys.path.insert(0, 'core/tools')
import forge
from concurrent.futures import ThreadPoolExecutor, as_completed

orders = json.load(open('core/out-diff/orders-1.20.1-1.20.2.json', encoding='utf-8'))
probe = orders[:int(sys.argv[2])]
n = int(sys.argv[1])
t0 = time.time()
ok = err = 0
errs = []
with ThreadPoolExecutor(max_workers=n) as pool:
    futs = [pool.submit(forge.call_model, forge.build_prompt(o, '1.20.2')) for o in probe]
    for f in as_completed(futs):
        try:
            f.result(); ok += 1
        except Exception as e:
            err += 1; errs.append(str(e)[:60])
dt = time.time() - t0
print(f"workers={n} sent={len(probe)} ok={ok} err={err} wall={dt:.0f}s throughput={ok/dt*60:.1f}/min")
for e in errs[:3]: print("  err:", e)
