package net.meteor.client.model;

import net.minecraft.client.model.ModelCreeper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelAlienCreeper extends ModelCreeper
{
	public ModelAlienCreeper()
	{
		this(0.0F);
	}

	public ModelAlienCreeper(float f)
	{
		super(f);
		head.setTextureOffset(33, 6).addBox(-0.5F, -14F, -0.5F, 1, 6, 1);
		head.setTextureOffset(33, 1).addBox(-1F, -16F, -1F, 2, 2, 2);
	}
}