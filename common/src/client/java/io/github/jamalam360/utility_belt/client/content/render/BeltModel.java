package io.github.jamalam360.utility_belt.client.content.render;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.LivingEntity;

public class BeltModel extends HumanoidModel<LivingEntity> {

	public BeltModel(ModelPart root) {
		super(root);
		this.setAllVisible(false);
		this.body.visible = true;
	}

	public static LayerDefinition createLayerDefinition() {
		MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild(
				PartNames.BODY,
				CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, 10.0F, -3.0F, 10.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
						.texOffs(0, 8).addBox(-4.0F, 11.0F, -4.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(10, 8).addBox(1.0F, 11.0F, -4.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.rotation(0.0F, 0.0F, 0.0F));
		body.addOrReplaceChild("pouch", CubeListBuilder.create().texOffs(18, 8).addBox(0.0F, -13.0F, -6.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, 1.5708F, 0.0F));
		return LayerDefinition.create(mesh, 64, 64);
	}
}
