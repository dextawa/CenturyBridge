"""What fraction of real work-order calls actually succeed right now?"""
import json, sys, time
sys.path.insert(0, 'core/tools')
import forge
from concurrent.futures import ThreadPoolExecutor, as_completed

orders = json.load(open('core/out-diff/orders-1.20.1-1.20.2.json', encoding='utf-8'))[:12]
codes = []
def one(o):
    t = time.time()
    try:
        forge.call_model(forge.build_prompt(o, '1.20.2'))
        return ('ok', time.time()-t)
    except Exception as e:
        return (str(e)[:40], time.time()-t)
t0 = time.time()
with ThreadPoolExecutor(max_workers=6) as p:
    for f in as_completed([p.submit(one, o) for o in orders]):
        codes.append(f.result())
ok = sum(1 for c,_ in codes if c=='ok')
avg = sum(d for _,d in codes)/len(codes)
print(f"{ok}/{len(codes)} ok in {time.time()-t0:.0f}s, avg latency {avg:.0f}s")
for c,d in codes:
    if c != 'ok': print(f"  {d:5.0f}s  {c}")
