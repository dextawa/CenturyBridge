"""Re-forge rejected bodies with the compiler error fed back."""
import json, os, sys
sys.path.insert(0, 'core/tools')
import forge
from concurrent.futures import ThreadPoolExecutor, as_completed

retry_path, to_version, merge_into = sys.argv[1], sys.argv[2], sys.argv[3]
bad = json.load(open(retry_path, encoding='utf-8'))
if not bad:
    print("nothing to retry"); raise SystemExit

print(f"retrying {len(bad)} rejected bodies")
fixed = []
with ThreadPoolExecutor(max_workers=forge.WORKERS) as pool:
    futs = {pool.submit(forge.call_model,
                        forge.build_prompt(r, to_version, r.get('compileError',''))): r
            for r in bad}
    for n, f in enumerate(as_completed(futs), 1):
        r = futs[f]
        try:
            body = forge.clean_body(f.result())
            if not forge.looks_like_body(body):
                body = forge.clean_body(forge.call_model(forge.build_prompt(
                    r, to_version,
                    "You emitted a signature or prose. Emit ONLY the statements "
                    "that go inside the braces.")))
            fixed.append({**{k: v for k, v in r.items() if k != 'compileError'},
                          'body': body})
        except Exception as e:
            print(f"  still failing: {r['owner']}.{r['name']} -- {e}")
        if n % 10 == 0: print(f"  {n}/{len(bad)}", flush=True)

# Merge in place: a retried body replaces its rejected version, and every
# other record is preserved untouched. Losing untouched records here silently
# un-forges work that already passed -- rebuild the list, do not filter it.
main = json.load(open(merge_into, encoding='utf-8'))
before = len(main)
patch = {r['id']: r for r in fixed}
def better(new, old):
    # A retry only wins if it actually produced something. An empty answer is
    # not an improvement on an existing body, and neither is a rewrite of a
    # body that already compiled -- the record carries no compileError in that
    # case, so it was never a candidate for retry to begin with.
    if not new.get('body'):
        return old
    if old.get('body') and not old.get('compileError'):
        return old
    return new


merged = [better(patch.pop(r['id'], r), r) if r['id'] in patch or True else r
          for r in main]
merged.extend(patch.values())          # retried orders absent from the file
assert len(merged) >= before, f"merge lost records: {before} -> {len(merged)}"
json.dump(merged, open(merge_into, 'w', encoding='utf-8'),
          indent=1, ensure_ascii=False)
print(f"merged {len(fixed)} retried into {merge_into} "
      f"({before} -> {len(merged)} records)")
