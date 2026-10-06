package net.chickalo.bugsbugsandmorebugs.entity.client;

import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class AntWorkerModel extends EntityModel<AntWorkerRenderState> {
    private final ModelPart mesosoma;
    private final ModelPart head;
    private final ModelPart antennae_left;
    private final ModelPart antennae_right;
    private final ModelPart gaster;
    private final ModelPart left_legF;
    private final ModelPart left_legM;
    private final ModelPart left_legB;
    private final ModelPart right_legF;
    private final ModelPart right_legM;
    private final ModelPart right_legB;

    private final KeyframeAnimation walkingAnimation;
    private final KeyframeAnimation idlingAnimation;
    private final KeyframeAnimation attackingAnimation;

    public AntWorkerModel(ModelPart root) {
        super(root);

        this.mesosoma = root.getChild("mesosoma");
        this.head = this.mesosoma.getChild("head");
        this.antennae_left = this.head.getChild("antennae_left");
        this.antennae_right = this.head.getChild("antennae_right");
        this.gaster = this.mesosoma.getChild("gaster");
        this.left_legF = this.mesosoma.getChild("left_legF");
        this.left_legM = this.mesosoma.getChild("left_legM");
        this.left_legB = this.mesosoma.getChild("left_legB");
        this.right_legF = this.mesosoma.getChild("right_legF");
        this.right_legM = this.mesosoma.getChild("right_legM");
        this.right_legB = this.mesosoma.getChild("right_legB");

        this.walkingAnimation = AntWorkerAnimations.WALK.bake(root);
        this.idlingAnimation = AntWorkerAnimations.IDLE.bake(root);
        this.attackingAnimation = AntWorkerAnimations.ATTACK.bake(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition mesosoma = partdefinition.addOrReplaceChild(
                "mesosoma",
                CubeListBuilder.create()
                        .texOffs(0, 10)
                        .addBox(
                                -2.0F, 0.0F, -1.0F,
                                4.0F, 1.0F, 2.0F,
                                new CubeDeformation(0.0F)
                        ),
                PartPose.offsetAndRotation(
                        0.0F, 19.5F, -0.5F,
                        0.0F, -1.5708F, 0.0F
                )
        );

        PartDefinition head = mesosoma.addOrReplaceChild(
                "head",
                CubeListBuilder.create(),
                PartPose.offset(-1.0F, 0.0F, 0.0F)
        );

        PartDefinition cube_r1 = head.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create()
                        .texOffs(0, 5)
                        .addBox(
                                -2.0F, -2.0F, -1.0F,
                                3.0F, 2.0F, 3.0F,
                                new CubeDeformation(0.0F)
                        ),
                PartPose.offsetAndRotation(
                        -1.0F, 1.5F, -0.5F,
                        0.0F, 0.0F, -0.1745F
                )
        );

        PartDefinition antennae_left = head.addOrReplaceChild(
                "antennae_left",
                CubeListBuilder.create()
                        .texOffs(19, 6)
                        .addBox(
                                -4.0F, -4.0F, -0.5F,
                                3.0F, 3.0F, 0.0F,
                                new CubeDeformation(0.0F)
                        ),
                PartPose.offset(
                        -1.0F, 1.5F, -0.5F
                )
        );

        PartDefinition antennae_right = head.addOrReplaceChild(
                "antennae_right",
                CubeListBuilder.create()
                        .texOffs(19, 9)
                        .addBox(
                                -2.0F, -3.0F, 1.0F,
                                3.0F, 3.0F, 0.0F,
                                new CubeDeformation(0.0F)
                        ),
                PartPose.offset(
                        -3.0F, 0.5F, 0.0F
                )
        );

        PartDefinition gaster = mesosoma.addOrReplaceChild(
                "gaster",
                CubeListBuilder.create(),
                PartPose.offset(1.5F, 0.0F, 0.0F)
        );

        PartDefinition cube_r2 = gaster.addOrReplaceChild(
                "cube_r2",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(
                                -3.0F, -2.0F, -1.0F,
                                4.0F, 2.0F, 3.0F,
                                new CubeDeformation(0.0F)
                        ),
                PartPose.offsetAndRotation(
                        2.5F, 2.0F, -0.5F,
                        0.0F, 0.0F, 0.2182F
                )
        );

        PartDefinition left_legF = mesosoma.addOrReplaceChild(
                "left_legF",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, 0.0F, -1.0F)
        );

        PartDefinition cube_r3 = left_legF.addOrReplaceChild(
                "cube_r3",
                CubeListBuilder.create()
                        .texOffs(12, 5)
                        .addBox(
                                -3.2637F, -1.894F, -4.7015F,
                                1.0F, 5.0F, 1.0F,
                                new CubeDeformation(0.0F)
                        ),
                PartPose.offsetAndRotation(
                        3.0F, 4.5F, 1.5F,
                        -0.6981F, 0.4363F, 0.0F
                )
        );

        PartDefinition left_legM = mesosoma.addOrReplaceChild(
                "left_legM",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, 0.0F, -1.0F)
        );

        PartDefinition cube_r4 = left_legM.addOrReplaceChild(
                "cube_r4",
                CubeListBuilder.create()
                        .texOffs(0, 13)
                        .addBox(
                                -3.3065F, -2.6212F, -3.3788F,
                                1.0F, 5.0F, 1.0F,
                                new CubeDeformation(0.0F)
                        ),
                PartPose.offsetAndRotation(
                        3.0F, 4.5F, 1.5F,
                        -0.7854F, -0.2182F, 0.0F
                )
        );

        PartDefinition left_legB = mesosoma.addOrReplaceChild(
                "left_legB",
                CubeListBuilder.create(),
                PartPose.offset(1.0F, 0.0F, -1.0F)
        );

        PartDefinition cube_r5 = left_legB.addOrReplaceChild(
                "cube_r5",
                CubeListBuilder.create()
                        .texOffs(12, 11)
                        .addBox(
                                -2.3801F, -3.8261F, -2.399F,
                                1.0F, 5.0F, 1.0F,
                                new CubeDeformation(0.0F)
                        ),
                PartPose.offsetAndRotation(
                        2.0F, 4.5F, 1.5F,
                        -0.6981F, -0.9163F, 0.0F
                )
        );

        PartDefinition right_legF = mesosoma.addOrReplaceChild(
                "right_legF",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, 0.0F, 1.0F)
        );

        PartDefinition cube_r6 = right_legF.addOrReplaceChild(
                "cube_r6",
                CubeListBuilder.create()
                        .texOffs(8, 13)
                        .addBox(
                                -3.2637F, -1.894F, 3.7015F,
                                1.0F, 5.0F, 1.0F,
                                new CubeDeformation(0.0F)
                        ),
                PartPose.offsetAndRotation(
                        3.0F, 4.5F, -1.5F,
                        0.6981F, -0.4363F, 0.0F
                )
        );

        PartDefinition right_legM = mesosoma.addOrReplaceChild(
                "right_legM",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, 0.0F, 1.0F)
        );

        PartDefinition cube_r7 = right_legM.addOrReplaceChild(
                "cube_r7",
                CubeListBuilder.create()
                        .texOffs(16, 0)
                        .addBox(
                                -3.3065F, -2.6212F, 2.3788F,
                                1.0F, 5.0F, 1.0F,
                                new CubeDeformation(0.0F)
                        ),
                PartPose.offsetAndRotation(
                        3.0F, 4.5F, -1.5F,
                        0.7854F, 0.2182F, 0.0F
                )
        );

        PartDefinition right_legB = mesosoma.addOrReplaceChild(
                "right_legB",
                CubeListBuilder.create(),
                PartPose.offset(1.0F, 0.0F, 1.0F)
        );

        PartDefinition cube_r8 = right_legB.addOrReplaceChild(
                "cube_r8",
                CubeListBuilder.create()
                        .texOffs(4, 13)
                        .addBox(
                                -2.3801F, -3.8261F, 1.399F,
                                1.0F, 5.0F, 1.0F,
                                new CubeDeformation(0.0F)
                        ),
                PartPose.offsetAndRotation(
                        2.0F, 4.5F, -1.5F,
                        0.6981F, 0.9163F, 0.0F
                )
        );

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(AntWorkerRenderState state) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.applyHeadRotation(state.yRot, state.xRot);

        this.walkingAnimation.applyWalk(
                state.walkAnimationPos,
                state.walkAnimationSpeed,
                2.0F,
                2.5F
        );

        this.idlingAnimation.apply(
                state.idleAnimationState,
                state.ageInTicks,
                1.0F
        );

        // Uncomment when you want to apply the attack animation.
        // this.attackingAnimation.apply(...);
    }

    private void applyHeadRotation(float headYaw, float headPitch) {
        headYaw = Mth.clamp(headYaw, -30.0F, 30.0F);
        headPitch = Mth.clamp(headPitch, -25.0F, 45.0F);

        this.head.yRot = headYaw * ((float) Math.PI / 180.0F);
        this.head.xRot = headPitch * ((float) Math.PI / 180.0F);
    }
}