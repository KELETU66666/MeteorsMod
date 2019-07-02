package net.meteor.common.climate;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class CrashedChunkSetData extends WorldSavedData {
	
	private final static String key = "metCrashedChunks";
	private static HandlerMeteor tempHandle;
	
	private HandlerMeteor metHandler;
	private CrashLocation cLoc = null;

	public CrashedChunkSetData(String s) {
		super(s);
	}
	
	public static CrashedChunkSetData forWorld(World world, HandlerMeteor metH) {
		tempHandle = metH;
		MapStorage storage = world.getPerWorldStorage();
		CrashedChunkSetData result = (CrashedChunkSetData)storage.getOrLoadData(CrashedChunkSetData.class, key);
		if (result == null) {
			result = new CrashedChunkSetData(key);
			storage.setData(key, result);
		}
		result.metHandler = metH;
		tempHandle = null;
		return result;
	}

	private ArrayList<CrashedChunkSet> loadCrashedChunks(NBTTagCompound tag, HandlerMeteor mHandler) {
		ArrayList<CrashedChunkSet> cList = new ArrayList<>();
		for (int i = 1; i <= 20; i++) {
			if (tag.hasKey("CCSet" + i)) {
				CrashedChunkSet ccSet = CrashedChunkSet.fromNBTString(tag.getString("CCSet" + i));
				if (ccSet != null) {
					cList.add(ccSet);
				}
			}
		}
		cLoc = CrashLocation.fromNBT(tag);
		return cList;
	}

	private NBTTagCompound saveCrashedChunks(NBTTagCompound tag) {
		Iterator<CrashedChunkSet> iter = metHandler.crashedChunks.iterator();
		int i = 1;
		while (iter.hasNext() && i <= 20) {
			CrashedChunkSet ccSet = iter.next();
			tag.setString("CCSet" + i, ccSet.toString());
			i++;
		}
		while (i < 21) {
			if (tag.hasKey("CCSet" + i)) {
				tag.removeTag("CCSet" + i);
			}
			i++;
		}
		
		MeteorForecast forecast = metHandler.getForecast();
		if (forecast.getLastCrashLocation() != null) {
			forecast.getLastCrashLocation().toNBT(tag);
		}

		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		if (metHandler != null) {
			metHandler.crashedChunks = loadCrashedChunks(tag, metHandler);
		} else {
			tempHandle.crashedChunks = loadCrashedChunks(tag, tempHandle);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		return saveCrashedChunks(tag);
	}
	
	@Override
	public boolean isDirty() {
		return true;
	}
	
	public CrashLocation getLoadedCrashLocation() {
		return cLoc;
	}
	
}
