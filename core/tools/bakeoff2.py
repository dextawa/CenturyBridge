"""Which healthy model actually lands verified bodies fastest?"""
import json, sys, time
sys.path.insert(0, 'core/tools')
import forge
from concurrent.futures import ThreadPoolExecutor, as_completed

orders = json.load(open('core/out-diff/orders-1.20.1-1.20.2.json', encoding='utf-8'))
# skip ones already forged
done = {r['id'] for r in json.load(open('core/out-diff/forged-a.json', encoding='utf-8'))}
todo = [o for o in orders if o['id'] not in done][:8]

for model in sys.argv[1].split(','):
    forge.MODEL = model
    t0 = time.time(); ok = 0; errs = []
    with ThreadPoolExecutor(max_workers=4) as p:
        futs = [p.submit(forge.call_model, forge.build_prompt(o, '1.20.2')) for o in todo]
        for f in as_completed(futs):
            try:
                body = forge.clean_body(f.result())
                if body and forge.looks_like_body(body): ok += 1
                else: errs.append('malformed')
            except Exception as e: errs.append(str(e)[:35])
    dt = time.time() - t0
    print(f"{model:26s} {ok}/{len(todo)} ok  {dt:5.0f}s  {ok/dt*60:.1f} bodies/min")
    for e in set(errs): print(f"    {errs.count(e)}x {e}")
