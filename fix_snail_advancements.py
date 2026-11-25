#!/usr/bin/env python3
"""Repair the four Snail V2 advancement files for Minecraft 26.3."""
import json
import shutil
import sys
from datetime import datetime
from pathlib import Path

FIXES = {'snail_ranching_101.json': {'parent': 'bugsbugsandmorebugs:living_pet_rock',
                             'display': {'icon': {'id': 'bugsbugsandmorebugs:snail_shell'},
                                         'title': {'translate': 'advancements.bugsbugsandmorebugs.snail_ranching_101.title'},
                                         'description': {'translate': 'advancements.bugsbugsandmorebugs.snail_ranching_101.description'},
                                         'frame': 'challenge',
                                         'hidden': False},
                             'criteria': {'bred': {'trigger': 'minecraft:bred_animals',
                                                   'conditions': {'child': {'type': 'minecraft:entity_properties',
                                                                            'entity': 'this',
                                                                            'predicate': {'minecraft:entity_type': 'bugsbugsandmorebugs:snail',
                                                                                          'minecraft:nbt': '{Variant:5}'}}}}},
                             'requirements': [['bred']]},
 'living_pet_rock.json': {'parent': 'bugsbugsandmorebugs:root',
                          'display': {'icon': {'id': 'minecraft:name_tag'},
                                      'title': {'translate': 'advancements.bugsbugsandmorebugs.living_pet_rock.title'},
                                      'description': {'translate': 'advancements.bugsbugsandmorebugs.living_pet_rock.description'},
                                      'frame': 'task',
                                      'hidden': False},
                          'criteria': {'named': {'trigger': 'minecraft:player_interacted_with_entity',
                                                 'conditions': {'item': {'items': 'minecraft:name_tag'},
                                                                'entity': {'type': 'minecraft:entity_properties',
                                                                           'entity': 'this',
                                                                           'predicate': {'minecraft:entity_type': 'bugsbugsandmorebugs:snail'}}}}},
                          'requirements': [['named']]},
 'was_it_really_worth_it.json': {'parent': 'bugsbugsandmorebugs:root',
                                 'display': {'icon': {'id': 'bugsbugsandmorebugs:escargot'},
                                             'title': {'translate': 'advancements.bugsbugsandmorebugs.was_it_really_worth_it.title'},
                                             'description': {'translate': 'advancements.bugsbugsandmorebugs.was_it_really_worth_it.description'},
                                             'frame': 'task',
                                             'hidden': True},
                                 'criteria': {'killed': {'trigger': 'minecraft:player_killed_entity',
                                                         'conditions': {'entity': {'type': 'minecraft:entity_properties',
                                                                                   'entity': 'this',
                                                                                   'predicate': {'minecraft:entity_type': 'bugsbugsandmorebugs:snail'}}}},
                                              'ate': {'trigger': 'minecraft:consume_item',
                                                      'conditions': {'item': {'items': 'bugsbugsandmorebugs:escargot'}}}},
                                 'requirements': [['killed'], ['ate']]},
 'recipes/food/escargot_from_smelting.json': {'parent': 'minecraft:recipes/root',
                                              'criteria': {'has_meat': {'trigger': 'minecraft:inventory_changed',
                                                                        'conditions': {'items': [{'items': 'bugsbugsandmorebugs:snail_meat'}]}},
                                                           'has_recipe': {'trigger': 'minecraft:recipe_unlocked',
                                                                          'conditions': {'recipes': 'bugsbugsandmorebugs:escargot_from_smelting'}}},
                                              'requirements': [['has_meat', 'has_recipe']],
                                              'rewards': {'recipes': ['bugsbugsandmorebugs:escargot_from_smelting']}}}


def main():
    root = Path(sys.argv[1] if len(sys.argv) > 1 else '.').resolve()
    base = root / 'src/main/resources/data/bugsbugsandmorebugs/advancement'
    if not (root / 'gradlew.bat').is_file() and not (root / 'gradlew').is_file():
        raise SystemExit('Run this from the mod project root (the folder containing gradlew.bat).')
    if not base.is_dir():
        raise SystemExit(f'Advancement directory not found: {base}')
    stamp = datetime.now().strftime('%Y%m%d_%H%M%S_%f')
    backup = root / '.snail_advancement_backups' / stamp
    for name, data in FIXES.items():
        # Also repair an existing generated copy, if datagen created one.
        paths = [base / name]
        generated = root / 'src/main/generated/data/bugsbugsandmorebugs/advancement' / name
        if generated.is_file():
            paths.append(generated)
        for path in paths:
            content = json.dumps(data, indent=2, ensure_ascii=False) + '\n'
            json.loads(content)
            if path.exists():
                if path.read_bytes() == content.encode('utf-8'):
                    print(f'Already fixed: {path.relative_to(root)}')
                    continue
                saved = backup / path.relative_to(root)
                saved.parent.mkdir(parents=True, exist_ok=True)
                shutil.copy2(path, saved)
            path.parent.mkdir(parents=True, exist_ok=True)
            path.write_text(content, encoding='utf-8', newline='\n')
            print(f'Fixed: {path.relative_to(root)}')
    if backup.exists():
        print(f'Backups: {backup}')
    print('Done. Run .\\gradlew.bat runClient from the project root.')

if __name__ == '__main__':
    main()
