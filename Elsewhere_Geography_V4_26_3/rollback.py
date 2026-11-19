#!/usr/bin/env python3
"""Restores source files from .elsewhere_v4_backup; use only before further project edits."""
import json,shutil,sys
from pathlib import Path
root=Path.cwd()
back=root/'.elsewhere_v4_backup'
manifest=back/'manifest.json'
if not manifest.exists():
    print('No V4 backup manifest found in the current project directory.');sys.exit(1)
if '--confirm' not in sys.argv:
    print('This replaces current modified source files with their pre-V4 versions.')
    print('To proceed: py Elsewhere_Geography_V4_26_3\\rollback.py --confirm')
    sys.exit(0)
created=json.loads(manifest.read_text())['created']
for name in created:
    p=root/name
    if p.exists():p.unlink();print('Removed new file',name)
for p in back.rglob('*'):
    if p.is_file() and p!=manifest:
        target=root/p.relative_to(back)
        target.parent.mkdir(parents=True,exist_ok=True)
        shutil.copy2(p,target)
        print('Restored',target.relative_to(root))
print('Rollback complete. Consider regenerating data and then rebuilding.')
