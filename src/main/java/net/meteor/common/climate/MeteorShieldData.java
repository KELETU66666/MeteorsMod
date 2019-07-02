package net.meteor.common.climate;

import net.meteor.common.IMeteorShield;
import net.meteor.common.MeteorsMod;
import net.minecraft.util.math.BlockPos;

public class MeteorShieldData implements IMeteorShield
{
	private int x;
	private int y;
	private int z;
	private int powerLevel;
	private int range;
	private int cometX;
	private int cometZ;
	private int cometType = -1;
	private String owner;
	private boolean blockComets;

	public MeteorShieldData(int x, int y, int z, int p, String o, boolean bComets)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.powerLevel = p;
		this.range = powerLevel * MeteorsMod.instance.ShieldRadiusMultiplier;
		this.owner = o;
		this.blockComets = bComets;
	}

	public MeteorShieldData(BlockPos pos, int powerLevel, String owner, boolean bComets) {
		this(pos.getX(), pos.getY(), pos.getZ(), powerLevel, owner, bComets);
	}

	public boolean equals(Object o)
	{
		if (o == null) return false;
		IMeteorShield data = (IMeteorShield)o;
		return (this.x == data.getX()) && (this.y == data.getY()) && (this.z == data.getZ());
	}

	@Override
	public int getRange() {
		return range;
	}
	
	@Override
	public int getPowerLevel() {
		return powerLevel;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public int getZ() {
		return z;
	}

	@Override
	public boolean isTileEntity() {
		return false;
	}

	@Override
	public String getOwner() {
		return owner;
	}

	@Override
	public boolean getPreventComets() {
		return blockComets;
	}

}