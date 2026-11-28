package net.chickalo.bugsbugsandmorebugs.entity.cavebeetle.client;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.chickalo.bugsbugsandmorebugs.entity.cavebeetle.CaveBeetlePose;
import java.util.EnumMap;

/** Generated from V5; walk is driven by travel, actions by synchronized animation states. */
public final class CaveBeetleModel extends EntityModel<CaveBeetleRenderState> {
    private final EnumMap<CaveBeetlePose, KeyframeAnimation> animations = new EnumMap<>(CaveBeetlePose.class);
    public CaveBeetleModel(ModelPart root) {
        super(root);
        for (CaveBeetlePose pose : CaveBeetlePose.values()) animations.put(pose, CaveBeetleAnimations.definition(pose).bake(root));
    }
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition p0 = mesh.getRoot().addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition p1 = p0.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 22).addBox(-6.0F, -3.0F, -9.0F, 12.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, 0.0F));
        PartDefinition p2 = p1.addOrReplaceChild("abdomen", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -4.0F, 0.0F, 14.0F, 7.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -2.0F));
        PartDefinition p3 = p1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 34).addBox(-4.0F, -2.5F, -6.0F, 8.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, -9.0F));
        PartDefinition p4 = p3.addOrReplaceChild("mandible_right", CubeListBuilder.create().texOffs(52, 22).addBox(-1.5F, -0.5F, -3.0F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, 2.0F, -6.0F));
        PartDefinition p5 = p3.addOrReplaceChild("antenna_right", CubeListBuilder.create().texOffs(38, 22).addBox(-0.5F, -0.5F, -6.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -2.0F, -5.5F, -0.41888F, -0.55851F, 0.0F));
        PartDefinition p6 = p5.addOrReplaceChild("antenna_tip_right", CubeListBuilder.create().texOffs(38, 29).addBox(-0.5F, -0.5F, -6.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(0.0F, 0.0F, -6.0F, 0.59341F, -0.13963F, 0.0F));
        PartDefinition p7 = p3.addOrReplaceChild("mandible_left", CubeListBuilder.create().texOffs(52, 22).mirror().addBox(-0.5F, -0.5F, -3.0F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-2.5F, 2.0F, -6.0F));
        PartDefinition p8 = p3.addOrReplaceChild("antenna_left", CubeListBuilder.create().texOffs(38, 22).mirror().addBox(-0.5F, -0.5F, -6.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-3.0F, -2.0F, -5.5F, -0.41888F, 0.55851F, 0.0F));
        PartDefinition p9 = p8.addOrReplaceChild("antenna_tip_left", CubeListBuilder.create().texOffs(38, 29).mirror().addBox(-0.5F, -0.5F, -6.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(-0.01F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, -6.0F, 0.59341F, 0.13963F, 0.0F));
        PartDefinition p10 = p0.addOrReplaceChild("leg_front_right", CubeListBuilder.create(), PartPose.offsetAndRotation(5.5F, -4.5F, -6.5F, 0.0F, 0.69813F, 0.0F));
        PartDefinition p11 = p10.addOrReplaceChild("leg_front_right_upper", CubeListBuilder.create().texOffs(28, 40).addBox(0.0F, -1.0F, -1.0F, 8.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.46496F));
        PartDefinition p12 = p11.addOrReplaceChild("leg_front_right_lower", CubeListBuilder.create().texOffs(28, 36).addBox(0.0F, -1.0F, -1.0F, 9.0F, 2.0F, 2.0F, new CubeDeformation(-0.02F)), PartPose.offsetAndRotation(8.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.46538F));
        PartDefinition p13 = p0.addOrReplaceChild("leg_front_left", CubeListBuilder.create(), PartPose.offsetAndRotation(-5.5F, -4.5F, -6.5F, 0.0F, -0.69813F, 0.0F));
        PartDefinition p14 = p13.addOrReplaceChild("leg_front_left_upper", CubeListBuilder.create().texOffs(28, 40).mirror().addBox(-8.0F, -1.0F, -1.0F, 8.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.46496F));
        PartDefinition p15 = p14.addOrReplaceChild("leg_front_left_lower", CubeListBuilder.create().texOffs(28, 36).mirror().addBox(-9.0F, -1.0F, -1.0F, 9.0F, 2.0F, 2.0F, new CubeDeformation(-0.02F)).mirror(false), PartPose.offsetAndRotation(-8.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.46538F));
        PartDefinition p16 = p0.addOrReplaceChild("leg_middle_right", CubeListBuilder.create(), PartPose.offset(6.0F, -4.5F, -2.0F));
        PartDefinition p17 = p16.addOrReplaceChild("leg_middle_right_upper", CubeListBuilder.create().texOffs(28, 40).addBox(0.0F, -1.0F, -1.0F, 8.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.51749F));
        PartDefinition p18 = p17.addOrReplaceChild("leg_middle_right_lower", CubeListBuilder.create().texOffs(28, 36).addBox(0.0F, -1.0F, -1.0F, 9.0F, 2.0F, 2.0F, new CubeDeformation(-0.02F)), PartPose.offsetAndRotation(8.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.62071F));
        PartDefinition p19 = p0.addOrReplaceChild("leg_middle_left", CubeListBuilder.create(), PartPose.offset(-6.0F, -4.5F, -2.0F));
        PartDefinition p20 = p19.addOrReplaceChild("leg_middle_left_upper", CubeListBuilder.create().texOffs(28, 40).mirror().addBox(-8.0F, -1.0F, -1.0F, 8.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.51749F));
        PartDefinition p21 = p20.addOrReplaceChild("leg_middle_left_lower", CubeListBuilder.create().texOffs(28, 36).mirror().addBox(-9.0F, -1.0F, -1.0F, 9.0F, 2.0F, 2.0F, new CubeDeformation(-0.02F)).mirror(false), PartPose.offsetAndRotation(-8.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.62071F));
        PartDefinition p22 = p0.addOrReplaceChild("leg_rear_right", CubeListBuilder.create(), PartPose.offsetAndRotation(5.5F, -4.5F, 2.5F, 0.0F, -0.83776F, 0.0F));
        PartDefinition p23 = p22.addOrReplaceChild("leg_rear_right_upper", CubeListBuilder.create().texOffs(28, 40).addBox(0.0F, -1.0F, -1.0F, 8.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.46496F));
        PartDefinition p24 = p23.addOrReplaceChild("leg_rear_right_lower", CubeListBuilder.create().texOffs(28, 36).addBox(0.0F, -1.0F, -1.0F, 9.0F, 2.0F, 2.0F, new CubeDeformation(-0.02F)), PartPose.offsetAndRotation(8.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.46538F));
        PartDefinition p25 = p0.addOrReplaceChild("leg_rear_left", CubeListBuilder.create(), PartPose.offsetAndRotation(-5.5F, -4.5F, 2.5F, 0.0F, 0.83776F, 0.0F));
        PartDefinition p26 = p25.addOrReplaceChild("leg_rear_left_upper", CubeListBuilder.create().texOffs(28, 40).mirror().addBox(-8.0F, -1.0F, -1.0F, 8.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.46496F));
        PartDefinition p27 = p26.addOrReplaceChild("leg_rear_left_lower", CubeListBuilder.create().texOffs(28, 36).mirror().addBox(-9.0F, -1.0F, -1.0F, 9.0F, 2.0F, 2.0F, new CubeDeformation(-0.02F)).mirror(false), PartPose.offsetAndRotation(-8.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.46538F));
        return LayerDefinition.create(mesh, 64, 64);
    }
    @Override public void setupAnim(CaveBeetleRenderState state) {
        root().getAllParts().forEach(ModelPart::resetPose);
        // Action poses lock movement; never add walking offsets to a digging pose.
        if (!state.digState.isStarted() && !state.investigateState.isStarted() && !state.chewState.isStarted()) {
            animations.get(CaveBeetlePose.WALK).applyWalk(state.walkAnimationPos, state.walkAnimationSpeed, 6.5F, 10.0F);
        }
        animations.get(CaveBeetlePose.IDLE).apply(state.idleState, state.ageInTicks, 1.0F);
        animations.get(CaveBeetlePose.INVESTIGATE).apply(state.investigateState, state.ageInTicks, 1.0F);
        animations.get(CaveBeetlePose.CHEW).apply(state.chewState, state.ageInTicks, 1.0F);
        animations.get(CaveBeetlePose.DIG).apply(state.digState, state.ageInTicks, 1.0F);
        ModelPart bodyRoot = root().getChild("root");
        bodyRoot.xScale = bodyRoot.yScale = bodyRoot.zScale = 0.85F;
    }
}
