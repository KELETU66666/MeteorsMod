package net.meteor.common.tileentity;

import net.meteor.common.FreezerRecipes;
import net.meteor.common.FreezerRecipes.FreezerRecipe;
import net.meteor.common.FreezerRecipes.RecipeType;
import net.meteor.common.MeteorItems;
import net.meteor.common.block.FreezingMachineBlock;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

// This class is sort of a mirror image of a vanilla furnace,
// but has some new attributes such as fluid handling and other recipes.
public class TileEntityFreezingMachine extends TileEntityNetworkBase implements ISidedInventory, IFluidHandler, ITickable {

	private static int[] acccessibleSlots = {0, 1, 2, 3, 4};
	//slot 0 = freeze item in
	//slot 1 = freeze item fuel
	//slot 2 = freeze output
	//Slot 3 = liquid in
	//Slot 4 = liquid out
	public static final int FREEZE_ITEM_IN = 0;
	public static final int FREEZE_ITEM_FUEL = 1;
	public static final int FREEZE_ITEM_OUT = 2;
	public static final int FLUID_IN = 3;
	public static final int FLUID_OUT = 4;


	private NonNullList<ItemStack> inv = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
	private ItemStack lastKnownItem = null;
	private FluidTank tank = new FluidTank(1000 * 10);
	private RecipeType acceptedRecipeType = RecipeType.either;

	private int burnTime;
	private int currentItemBurnTime;
	private int cookTime;

	@Override
	public int getSizeInventory() {
		return 5;
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : this.inv)
		{
			if (!itemstack.isEmpty())
			{
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inv.get(slot);
	}

	@Override
	public void clear()
	{
		this.inv.clear();
	}


	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		return ItemStackHelper.getAndSplit(this.inv, slot, amount);
	}

	/**
	 * Removes a stack from the given slot and returns it.
	 */
	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		return ItemStackHelper.getAndRemove(this.inv, index);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack item) {
		
		this.inv.set(slot, item);
		
		if (item.getCount() > this.getInventoryStackLimit())
		{
			item.setCount(this.getInventoryStackLimit());
		}

		if (slot == FLUID_IN) {
			checkFluidContainer();
		}
		
	}
	
	private void checkFluidContainer() {
		ItemStack item = inv.get(FLUID_IN);

		if (!item.isEmpty() && item.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
			if (FluidUtil.getFluidContained(item) != null) {
				FluidStack fluid = FluidUtil.getFluidContained(item);
				if (fluid != null && (fluid.isFluidEqual(tank.getFluid()) || tank.getFluidAmount() == 0)) {
					if (tank.fill(fluid, false) == fluid.amount) {

						// Try to insert it into the bottom slot
						ItemStack emptyContainer;
						IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(item.copy());
						fluidHandler.drain(fluid.copy(), true);
						emptyContainer = fluidHandler.getContainer();

						if (inv.get(FLUID_OUT).isEmpty()) {
							tank.fill(fluid, true);
							inv.set(FLUID_OUT, emptyContainer);
							decrStackSize(FLUID_IN, 1);
							markDirty();
							IBlockState state = getWorld().getBlockState(this.getPos());
							getWorld().notifyBlockUpdate(getPos(), state, state, 3);

						} else if (inv.get(FLUID_OUT).isItemEqual(emptyContainer) && inv.get(FLUID_OUT).getCount() + 1 <= inv.get(FLUID_OUT).getMaxStackSize()) {
							tank.fill(fluid, true);
							inv.get(FLUID_OUT).grow(1);
							decrStackSize(FLUID_IN, 1);
							markDirty();
							IBlockState state = getWorld().getBlockState(this.getPos());
							getWorld().notifyBlockUpdate(getPos(), state, state, 3);
						}

					}
				}
			} else {
				FluidStack fluidInTank = tank.getFluid();
				if (fluidInTank != null) {
					FluidActionResult fluidActionResult = FluidUtil.tryFillContainer(item, tank, 1000, null, false);
					if (fluidActionResult.isSuccess()) {
						ItemStack filledContainer = fluidActionResult.getResult();
						if (inv.get(FLUID_OUT) == ItemStack.EMPTY) {
							tank.drain(FluidUtil.getFluidContained(filledContainer).amount, true);
							inv.set(FLUID_OUT, filledContainer);
							decrStackSize(FLUID_IN, 1);
							markDirty();
							IBlockState state = getWorld().getBlockState(this.getPos());
							getWorld().notifyBlockUpdate(getPos(), state, state, 3);
						} else if (inv.get(FLUID_OUT).isItemEqual(filledContainer) && inv.get(FLUID_OUT).getCount() + 1 <= inv.get(FLUID_OUT).getMaxStackSize()) {
							tank.drain(FluidUtil.getFluidContained(filledContainer).amount, true);
							inv.get(FLUID_OUT).grow(1);
							decrStackSize(FLUID_IN, 1);
							markDirty();
							IBlockState state = getWorld().getBlockState(this.getPos());
							getWorld().notifyBlockUpdate(getPos(), state, state, 3);
						}
					}
				}
			}
		}
	}

	@Override
	public String getName() {
		return "Freezer";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		NBTTagList nbttaglist = nbt.getTagList("Items", 10);
		this.inv.clear();

		for (int i = 0; i < nbttaglist.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			byte b0 = nbttagcompound1.getByte("Slot");

			if (b0 >= 0 && b0 < this.inv.size())
			{
				this.inv.set(b0, new ItemStack(nbttagcompound1));
			}
		}

		this.burnTime = nbt.getShort("BurnTime");
		this.cookTime = nbt.getShort("CookTime");
		this.currentItemBurnTime = nbt.getShort("ItemFreezeTime");
		this.acceptedRecipeType = RecipeType.values()[nbt.getShort("acceptedRecipeType")];
		tank.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setShort("BurnTime", (short)this.burnTime);
		nbt.setShort("CookTime", (short)this.cookTime);
		nbt.setShort("ItemFreezeTime", (short)this.currentItemBurnTime);
		nbt.setShort("acceptedRecipeType", (short)this.acceptedRecipeType.getID());
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < this.inv.size(); ++i)
		{
			if (this.inv.get(i) != ItemStack.EMPTY)
			{
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte)i);
				this.inv.get(i).writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}

		nbt.setTag("Items", nbttaglist);
		tank.writeToNBT(nbt);
		return nbt;
	}


	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
	{
		readFromNBT(pkt.getNbtCompound());
	}


	@Override
	public NBTTagCompound getUpdateTag()
	{
		return writeToNBT(super.getUpdateTag());
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player)
	{
		if (this.world.getTileEntity(this.pos) != this)
		{
			return false;
		}
		else
		{
			return player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
		}
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack item) {
		if (slot == FLUID_IN) {
			return item.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
		}
		if (slot == FREEZE_ITEM_OUT) {
			return false;
		}
		if (slot == FREEZE_ITEM_FUEL) {
			return getItemFreezeTime(item) <= 0;
		}
		return true;
	}

	@Override
	public int getField(int id) {
		switch (id)
		{
			case 0:
				return this.burnTime;
			case 1:
				return this.currentItemBurnTime;
			case 2:
				return this.cookTime;
			case 3:
				if(tank != null && tank.getFluid() != null) {
					return tank.getFluid().amount;
				}
				return 0;
			case 4:
				return this.getRecipeMode().getID();
			default:
				return 0;
		}
	}

	@Override
	public void setField(int id, int value)
	{
		switch (id)
		{
			case 0:
				this.burnTime = value;
				break;
			case 1:
				this.currentItemBurnTime = value;
				break;
			case 2:
				this.cookTime = value;
				break;
			case 3:
				if (tank != null && tank.getFluid() != null) {
					tank.getFluid().amount = value;
				}
				break;
			case 4:
				this.setRecipeMode(RecipeType.values()[value]);
				break;
		}
	}


	@Override
	public int getFieldCount() {
		return 5;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return acccessibleSlots;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack item, EnumFacing direction) {

		if (item.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
			return slot == FLUID_IN;
		}
		else {
			return slot == FREEZE_ITEM_FUEL;
		}
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack item, EnumFacing side) {
		return slot == FREEZE_ITEM_OUT || slot == FLUID_OUT;
	}

	@SideOnly(Side.CLIENT)
	public int getCookProgressScaled(int i)
	{
		return this.cookTime * i / 200;
	}

	@SideOnly(Side.CLIENT)
	public int getBurnTimeRemainingScaled(int i)
	{
		if (this.currentItemBurnTime == 0)
		{
			this.currentItemBurnTime = 200;
		}

		return this.burnTime * i / this.currentItemBurnTime;
	}
	
	public RecipeType getRecipeMode() {
		return this.acceptedRecipeType;
	}
	
	public void setRecipeMode(RecipeType type) {
		this.acceptedRecipeType = type;
	}

	public boolean isFreezing() {
		return this.burnTime > 0;
	}

	@Override
	public void update() {
		boolean flag = isFreezing();
		boolean flag1 = false;

		if (this.isFreezing())
		{
			--this.burnTime;
		}

		if (!this.getWorld().isRemote)
		{
			
			if (!inv.get(FLUID_IN).isEmpty()) {
				checkFluidContainer();
			}

			if (this.isFreezing() || !this.inv.get(FREEZE_ITEM_FUEL).isEmpty())
			{
				if (!this.isFreezing() && this.canFreeze())
				{
					this.currentItemBurnTime = this.burnTime = getItemFreezeTime(this.inv.get(FREEZE_ITEM_FUEL));

					if (this.isFreezing())
					{
						flag1 = true;

						if (!this.inv.get(FREEZE_ITEM_FUEL).isEmpty())
						{
							this.inv.get(FREEZE_ITEM_FUEL).shrink(1);

							if (this.inv.get(FREEZE_ITEM_FUEL).getCount() == 0)
							{
								this.inv.set(FREEZE_ITEM_FUEL, inv.get(FREEZE_ITEM_FUEL).getItem().getContainerItem(inv.get(FREEZE_ITEM_FUEL)));
							}
						}
					}
				}

				if (this.isFreezing() && this.canFreeze())
				{
					++this.cookTime;

					if (this.cookTime == 200)
					{
						this.cookTime = 0;
						this.freezeItem();
						flag1 = true;
					}
				}
				else
				{
					this.cookTime = 0;
				}
			} else {
				this.cookTime = 0;
			}

			if (flag != this.isFreezing())
			{
				flag1 = true;
				FreezingMachineBlock.setState(this.isFreezing(), this.world, this.pos);
			}
		}

		if (flag1)
		{
			this.markDirty();
			IBlockState state = getWorld().getBlockState(this.getPos());
			getWorld().notifyBlockUpdate(getPos(), state, state, 3);
		}
	}



	public static int getItemFreezeTime(ItemStack itemStack) {
		if (itemStack != null) {
			Item item = itemStack.getItem();
			if (item == MeteorItems.itemFrezaCrystal) {
				return 1600;
			}
			if (item == Item.getItemFromBlock(Blocks.ICE)) {
				return 200;
			}
			if (item == Item.getItemFromBlock(Blocks.PACKED_ICE)) {
				return 400;
			}
		}
		return 0;
	}

	private boolean canFreeze() {
		FreezerRecipe recipe = FreezerRecipes.instance().getFreezingResult(this.inv.get(FREEZE_ITEM_IN), tank.getFluid(), this.acceptedRecipeType);
		if (recipe == null)
			return false;

		ItemStack result = recipe.getResult(inv.get(FREEZE_ITEM_IN));
		if (this.inv.get(FREEZE_ITEM_OUT).isEmpty()) {
			if (this.lastKnownItem == null) {
				this.lastKnownItem = result;
			} else if (!result.isItemEqual(this.lastKnownItem)) {
				this.cookTime = 0;
				this.lastKnownItem = result;
				this.markDirty();
				IBlockState state = getWorld().getBlockState(this.getPos());
				getWorld().notifyBlockUpdate(getPos(), state, state, 3);
			}
			return true;
		}
		if (!this.inv.get(FREEZE_ITEM_OUT).isItemEqual(result)) return false;
		int resultSize = inv.get(FREEZE_ITEM_OUT).getCount() + result.getCount();
		if (resultSize <= getInventoryStackLimit() && resultSize <= this.inv.get(FREEZE_ITEM_OUT).getMaxStackSize()) {
			if (this.lastKnownItem == null) {
				this.lastKnownItem = result;
			} else if (!result.isItemEqual(this.lastKnownItem)) {
				this.cookTime = 0;
				this.lastKnownItem = result;
				this.markDirty();
				IBlockState state = getWorld().getBlockState(this.getPos());
				getWorld().notifyBlockUpdate(getPos(), state, state, 3);
			}
			return true;
		}
		return false;
	}

	public void freezeItem() {
		if (this.canFreeze())
		{
			FreezerRecipe recipe = FreezerRecipes.instance().getFreezingResult(this.inv.get(FREEZE_ITEM_IN), tank.getFluid(), this.acceptedRecipeType);

			if (this.inv.get(FREEZE_ITEM_OUT) == ItemStack.EMPTY)
			{
				this.inv.set(FREEZE_ITEM_OUT, recipe.getResult(inv.get(FREEZE_ITEM_IN)).copy());
			}
			else if (this.inv.get(FREEZE_ITEM_OUT).getItem() == recipe.getResult(inv.get(FREEZE_ITEM_IN)).getItem())
			{
				this.inv.get(FREEZE_ITEM_OUT).grow(recipe.getResult(inv.get(FREEZE_ITEM_IN)).getCount());
			}

			if (recipe.requiresItem()) {
				this.inv.get(FREEZE_ITEM_IN).shrink(1);

				if (this.inv.get(FREEZE_ITEM_IN).getCount() <= 0)
				{
					this.inv.set(FREEZE_ITEM_IN, ItemStack.EMPTY);
				}
			}

			if (recipe.requiresFluid()) {
				tank.drain(recipe.getFluidAmount(), true);

				if (tank.getFluidAmount() == 0) {
					IBlockState state = getWorld().getBlockState(this.getPos());
					getWorld().notifyBlockUpdate(getPos(), state, state, 3);
					}
			}

		}
	}

	/* IFluidHandler */
	@Override
	public int fill(FluidStack resource, boolean doFill)
	{
		if (tank.getFluidAmount() == 0) {
			IBlockState state = getWorld().getBlockState(this.getPos());
			getWorld().notifyBlockUpdate(getPos(), state, state, 3);
		}
		return tank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain)
	{
		if (resource == null || !resource.isFluidEqual(tank.getFluid()))
		{
			IBlockState state = getWorld().getBlockState(this.getPos());
			getWorld().notifyBlockUpdate(getPos(), state, state, 3);
			return null;
		}
		return tank.drain(resource.amount, doDrain);
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain)
	{
		if (doDrain && tank.getFluidAmount() - maxDrain <= 0) {
			IBlockState state = getWorld().getBlockState(this.getPos());
			getWorld().notifyBlockUpdate(getPos(), state, state, 3);
		}
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		return tank.getTankProperties();
	}

	public FluidTankInfo getTankInfo() {
		return tank.getInfo();
	}

	@Override
	public void onButtonPress(int id) {
		if (id == 0) {
			
			int i = this.acceptedRecipeType.getID();
			if (i == 3) {
				i = 0;
			} else {
				i++;
			}
			this.acceptedRecipeType = RecipeType.values()[i];
		}
	}

}
