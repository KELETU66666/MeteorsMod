package net.meteor.client.render;

import net.meteor.client.model.ModelAlienCreeper;
import net.meteor.client.render.layers.LayerAlienCreeperCharge;
import net.meteor.common.entity.EntityAlienCreeper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderAlienCreeper extends RenderLiving<EntityAlienCreeper> {

	public static final Factory FACTORY = new Factory();
	private static final ResourceLocation CREEPER_TEXTURE = new ResourceLocation("meteors", "textures/entities/meteorcreeper.png");

	public RenderAlienCreeper(RenderManager renderManager)
	{
		super(renderManager, new ModelAlienCreeper(), 0.5F);
		this.addLayer(new LayerAlienCreeperCharge(this));
	}

	private void updateCreeperScale(EntityAlienCreeper entitycreeper, float partialTickTime)
	{
		float f1 = entitycreeper.getCreeperFlashIntensity(partialTickTime);
		float f2 = 1.0F + MathHelper.sin(f1 * 100F) * f1 * 0.01F;
		if (f1 < 0.0F)
		{
			f1 = 0.0F;
		}
		if (f1 > 1.0F)
		{
			f1 = 1.0F;
		}
		f1 *= f1;
		f1 *= f1;
		float f3 = (1.0F + f1 * 0.4F) * f2;
		float f4 = (1.0F + f1 * 0.1F) / f2;
		GlStateManager.scale(f3, f4, f3);
	}

	private int updateCreeperColorMultiplier(EntityAlienCreeper entityCreeper, float lightBrightness, float partialTickTime)
	{
		float var5 = entityCreeper.getCreeperFlashIntensity(partialTickTime);

		if ((int)(var5 * 10.0F) % 2 == 0)
		{
			return 0;
		}
		else
		{
			int var6 = (int)(var5 * 0.2F * 255.0F);

			if (var6 < 0)
			{
				var6 = 0;
			}

			if (var6 > 255)
			{
				var6 = 255;
			}

			short var7 = 255;
			short var8 = 255;
			short var9 = 255;
			return var6 << 24 | var7 << 16 | var8 << 8 | var9;
		}
	}

	@Override
	protected void preRenderCallback(EntityAlienCreeper entityliving, float partialTickTime)
	{
		updateCreeperScale(entityliving, partialTickTime);
	}

	@Override
	protected int getColorMultiplier(EntityAlienCreeper entityliving, float lightBrightness, float partialTickTime)
	{
		return updateCreeperColorMultiplier(entityliving, lightBrightness, partialTickTime);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityAlienCreeper entity) {
		return CREEPER_TEXTURE;
	}

	public static class Factory implements IRenderFactory<EntityAlienCreeper> {
		@Override
		public Render<? super EntityAlienCreeper> createRenderFor(RenderManager manager) {
			return new RenderAlienCreeper(manager);
		}
	}
}