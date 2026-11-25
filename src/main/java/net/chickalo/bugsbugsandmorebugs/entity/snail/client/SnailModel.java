package net.chickalo.bugsbugsandmorebugs.entity.snail.client;

import net.chickalo.bugsbugsandmorebugs.entity.snail.SnailPose;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import java.util.EnumMap;

/** Generated from the editable Blockbench source. */
public class SnailModel extends EntityModel<SnailRenderState> {
    private final EnumMap<SnailPose, KeyframeAnimation> animations = new EnumMap<>(SnailPose.class);
    public SnailModel(ModelPart root) {
        super(root);
        for (SnailPose pose : SnailPose.values()) animations.put(pose, SnailAnimations.definition(pose).bake(root));
    }
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part0 = mesh.getRoot().addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition part1 = part0.addOrReplaceChild("shell", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-6.0F, -10.0F, -7.0F, 12.0F, 10.0F, 14.0F, new CubeDeformation(0.0F))
                .texOffs(0, 24).addBox(-5.0F, -12.0F, -6.0F, 10.0F, 2.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(0, 38).addBox(-4.0F, -14.0F, -5.0F, 8.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 3.0F));
        PartDefinition part2 = part0.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition part3 = part2.addOrReplaceChild("foot_front", CubeListBuilder.create()
                .texOffs(0, 50).addBox(-5.0F, -1.0F, -12.0F, 10.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, 0.0F));
        PartDefinition part4 = part2.addOrReplaceChild("foot_back", CubeListBuilder.create()
                .texOffs(0, 50).addBox(-5.0F, -1.0F, 0.0F, 10.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, 0.0F));
        PartDefinition part5 = part2.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(32, 24).addBox(-4.0F, -4.0F, -8.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, -4.0F));
        PartDefinition part6 = part5.addOrReplaceChild("left_eye", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-0.5F, -3.0F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(6, 1).addBox(-0.5F, -5.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, -4.0F, -5.5F));
        PartDefinition part7 = part5.addOrReplaceChild("right_eye", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-0.5F, -3.0F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(6, 1).addBox(-0.5F, -5.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, -4.0F, -5.5F));
        return LayerDefinition.create(mesh, 64, 64);
    }
    @Override public void setupAnim(SnailRenderState state) {
        root().getAllParts().forEach(ModelPart::resetPose);
        animations.get(state.pose).apply(state.animation, state.ageInTicks, 1.0F);
        if (state.climbing) {
            ModelPart climbingRoot = root().getChild("root");
            climbingRoot.xRot -= (float)(Math.PI/2);
            climbingRoot.z -= state.babySnail ? 3.0F : 6.0F;
        }
        if (state.babySnail) {
            ModelPart snail = root().getChild("root");
            snail.xScale = snail.yScale = snail.zScale = 0.5F;
        }
    }
}
