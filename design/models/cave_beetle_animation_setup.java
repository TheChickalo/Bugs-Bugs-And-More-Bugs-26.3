// Reference snippets for Mojang mappings, Minecraft 1.20.1 - 1.21.1. Not compiled or tested.
// Merge into your own classes and add imports. Later versions use render states instead.

/* ---------- 1. Model class (the one Blockbench exports) ----------
 * Change "extends EntityModel<T>" to "extends HierarchicalModel<T>", delete the exported
 * renderToBuffer (HierarchicalModel has one), keep createBodyLayer() exactly as exported,
 * and replace the constructor, fields and setupAnim with this.                           */

private final ModelPart root;

public cave_beetle(ModelPart root) {
    this.root = root;
}

@Override
public ModelPart root() {
    return this.root;
}

@Override
public void setupAnim(T beetle, float limbSwing, float limbSwingAmount,
                      float ageInTicks, float netHeadYaw, float headPitch) {
    // Required: animations are offsets and pile up every frame without this.
    this.root().getAllParts().forEach(ModelPart::resetPose);

    // Dig and walk both move the front legs, so never run them together.
    if (!beetle.digState.isStarted()) {
        // 6.5 keeps planted feet matched to the ground. 10 reaches full stride quickly.
        this.animateWalk(cave_beetleAnimation.walk, limbSwing, limbSwingAmount, 6.5F, 10.0F);
    }
    this.animate(beetle.idleState, cave_beetleAnimation.idle, ageInTicks);
    this.animate(beetle.investigateState, cave_beetleAnimation.investigate, ageInTicks);
    this.animate(beetle.chewState, cave_beetleAnimation.chew, ageInTicks);
    this.animate(beetle.digState, cave_beetleAnimation.dig, ageInTicks);
}

/* ---------- 2. Entity class ---------- */

public static final int NONE = 0, INVESTIGATE = 1, CHEW = 2, DIG = 3;

private static final EntityDataAccessor<Integer> ACTION =
        SynchedEntityData.defineId(CaveBeetle.class, EntityDataSerializers.INT);

public final AnimationState idleState = new AnimationState();
public final AnimationState investigateState = new AnimationState();
public final AnimationState chewState = new AnimationState();
public final AnimationState digState = new AnimationState();

// 1.21 - 1.21.1:
@Override
protected void defineSynchedData(SynchedEntityData.Builder builder) {
    super.defineSynchedData(builder);
    builder.define(ACTION, NONE);
}
// 1.20.1 instead:
//   protected void defineSynchedData() {
//       super.defineSynchedData();
//       this.entityData.define(ACTION, NONE);
//   }

/** Call on the server from your AI goals. Stop navigation before INVESTIGATE, CHEW or DIG,
 *  and set NONE again when the goal ends. */
public void setAction(int action) {
    this.entityData.set(ACTION, action);
}

@Override
public void tick() {
    super.tick();
    if (this.level().isClientSide) {
        int action = this.entityData.get(ACTION);
        run(this.idleState, action == NONE);
        run(this.investigateState, action == INVESTIGATE);
        run(this.chewState, action == CHEW);
        run(this.digState, action == DIG);
    }
}

private void run(AnimationState state, boolean active) {
    if (active) state.startIfStopped(this.tickCount);
    else state.stop();
}