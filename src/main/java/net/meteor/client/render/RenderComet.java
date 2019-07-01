package net.meteor.client.render;

import net.meteor.client.model.ModelMeteor;
import net.meteor.common.entity.EntityComet;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class RenderComet extends Render<EntityComet> {

	private ModelMeteor modelMeteor;
	private int metID = 0;
	
	public RenderComet() {
		modelMeteor = new ModelMeteor();
	}
	
	@Override
	public void doRender(EntityComet entity, double d, double d1, double d2, float f, float f1) {
		renderMeteor((EntityComet)entity, d, d1, d2, f, f1);
	}

	public void renderMeteor(EntityComet entityComet, double d, double d1, double d2, float f, float f1) {
		this.metID = entityComet.meteorType.getID();
		GL11.glPushMatrix();
		GL11.glTranslatef((float)d, (float)d1, (float)d2);
		GL11.glRotatef(entityComet.prevRotationYaw + (entityComet.rotationYaw - entityComet.prevRotationYaw) * f1, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(entityComet.prevRotationPitch + (entityComet.rotationPitch - entityComet.prevRotationPitch) * f1, 0.0F, 0.0F, 1.0F);
		this.bindTexture(RenderMeteor.skins.get(metID));
		modelMeteor.renderWithSize(1, 0.0625F);
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityComet entity) {
		return RenderMeteor.skins.get(metID);
	}
	
}
