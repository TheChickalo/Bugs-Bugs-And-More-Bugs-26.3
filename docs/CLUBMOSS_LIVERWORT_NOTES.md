# Clubmoss and Liverwort block-state update

- `clubmoss` stays the **same block ID** but is now a solid 16x16x16 cube, like vanilla Moss Block. Use `textures/block/clubmoss.png` as its full-face pixel art. No more carpet collision or tiny carpet model.
- `clubmoss_lowlying` is a new cross block, placed on top of the full `clubmoss` block. Its separate texture path is `textures/block/clubmoss_lowlying.png`, **16×16**, with transparent background.
- `clubmoss_sprig` remains registered for compatibility with prior saves; new vegetation will use `clubmoss_lowlying`.
- `liverwort` remains the same block ID but is now a four-section `LeafLitterBlock`, rotating and stacking exactly like fallen autumn leaves. It references `textures/block/liverwort.png`, **16×16**. The item count on breaking uses the same layered loot provider.
- Worldgen should replace the **surface soil** with the solid clubmoss block, then generate lowlying shoots above it, never stack an extra full block on top of the grass.
- Manual placement of `clubmoss_lowlying` checks `clubmoss` underfoot through `ClubmossSprigBlock`.

No actual PNGs are added; keep your pixel-art textures in the local working folder until you're ready to commit them.
