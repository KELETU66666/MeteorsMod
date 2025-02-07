package net.meteor.common.item;

import java.util.List;

import net.meteor.common.MeteorsMod;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class ItemFrezariteSpade extends ItemSpade
{
	public ItemFrezariteSpade(Item.ToolMaterial toolMaterial)
	{
		super(toolMaterial);
		this.setCreativeTab(MeteorsMod.meteorTab);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		tooltip.add("\2473" + I18n.translateToLocal("enchantment.frezSpade.one"));
		tooltip.add("\2473" + I18n.translateToLocal("enchantment.frezSpade.two"));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasEffect(ItemStack par1ItemStack)
	{
		return true;
	}

	@Override
	public int getItemEnchantability()
	{
		return 0;
	}
	
}