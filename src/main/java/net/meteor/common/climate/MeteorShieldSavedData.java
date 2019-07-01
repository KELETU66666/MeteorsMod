package net.meteor.common.climate;

import net.meteor.common.IMeteorShield;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class MeteorShieldSavedData extends WorldSavedData {
	
	private final static String key = "meteorShields";
	private static ShieldManager tempHandle;
	
	private ShieldManager manager;
	private boolean loaded;

	public MeteorShieldSavedData(String s) {
		super(s);
		this.loaded = false;
	}
	
	public static MeteorShieldSavedData forWorld(World world, ShieldManager man) {
		tempHandle = man;
		MapStorage storage = world.getPerWorldStorage();
		MeteorShieldSavedData result = (MeteorShieldSavedData)storage.getOrLoadData(MeteorShieldSavedData.class, key);
		if (result == null) {
			result = new MeteorShieldSavedData(key);
			storage.setData(key, result);
		}
		result.manager = man;
		tempHandle = null;
		return result;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		if (loaded) return;
		if (manager == null) manager = tempHandle;
		int numShields = nbt.getInteger("numShields");
		for (int i = 0; i < numShields; i++) {
			NBTTagCompound sNBT = nbt.getCompoundTag("s" + i);
			int x = sNBT.getInteger("x");
			int y = sNBT.getInteger("y");
			int z = sNBT.getInteger("z");
			int power = sNBT.getInteger("power");
			String owner = sNBT.getString("owner");
			boolean bComets = sNBT.getBoolean("blockComets");
			
			manager.addShield(new MeteorShieldData(x, y, z, power, owner, bComets));
		}
		loaded = true;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		int numShields = manager.meteorShields.size();
		nbt.setInteger("numShields", numShields);
		for (int i = 0; i < numShields; i++) {
			NBTTagCompound sNBT = new NBTTagCompound();
			IMeteorShield shield = manager.meteorShields.get(i);
			sNBT.setInteger("x", shield.getX());
			sNBT.setInteger("y", shield.getY());
			sNBT.setInteger("z", shield.getZ());
			sNBT.setInteger("power", shield.getPowerLevel());
			sNBT.setString("owner", shield.getOwner());
			sNBT.setBoolean("blockComets", shield.getPreventComets());
			nbt.setTag("s" + i, sNBT);
		}
		return nbt;
	}
	
	@Override
	public boolean isDirty() {
		return true;
	}

}
