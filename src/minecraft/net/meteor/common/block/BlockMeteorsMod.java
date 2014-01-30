package net.meteor.common.block;

import net.meteor.common.LangLocalization;
import net.meteor.common.MeteorsMod;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockMeteorsMod extends Block
{
	public BlockMeteorsMod(int id, Material par2Material)
	{
		super(id, par2Material);
		this.setCreativeTab(MeteorsMod.meteorTab);
	}
	
	@Override
	public Block setTextureName(String s) {
		return super.setTextureName(MeteorsMod.MOD_ID + ":" + s);
	}

	@Override
	public String getLocalizedName()
	{
		return LangLocalization.get(this.getUnlocalizedName() + ".name");
	}
}