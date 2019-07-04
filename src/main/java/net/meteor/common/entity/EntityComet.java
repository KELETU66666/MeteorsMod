package net.meteor.common.entity;

import io.netty.buffer.ByteBuf;
import net.meteor.common.EnumMeteor;
import net.meteor.common.crash.CrashComet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityComet extends Entity implements IEntityAdditionalSpawnData {
	
	public EnumMeteor meteorType;
	public int spawnPauseTicks = 0;

	public EntityComet(World par1World) {
		super(par1World);
		this.preventEntitySpawning = true;
		this.setSize(0.98F, 0.98F);
		//this.yOffset = (this.height / 2.0F);//TODO 1.12.2
		this.meteorType = EnumMeteor.METEORITE;
		
		this.motionX = (rand.nextDouble() - rand.nextDouble()) * 1.2D;
		this.motionZ = (rand.nextDouble() - rand.nextDouble()) * 1.2D;
		this.rotationYaw = (float)(Math.random() * 360D);
		this.rotationPitch = (float)(Math.random() * 360D);
	}
	
	public EntityComet(World world, double x, double z, EnumMeteor metType) {
		this(world);
		this.meteorType = metType;
		this.setPosition(x, 250.0D, z);
		this.prevPosX = x;
		this.prevPosY = 250.0D;
		this.prevPosZ = z;
	}

	@Override
	protected void entityInit() {}
	
	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return !this.isDead;
	}
	
	@Override
	public void onUpdate() {
		if (!this.getEntityWorld().provider.isSurfaceWorld()) {
			this.setDead();
			return;
		}
		
		prevRotationPitch = rotationPitch;
		prevRotationYaw = rotationYaw;
		rotationPitch = (float)((rotationPitch + 3D) % 360D);
		rotationYaw = (float)((rotationPitch + 3D) % 360D);
		
		if (this.spawnPauseTicks > 0) {
			this.spawnPauseTicks--;
			return;
		}
		
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		motionY -= 0.039999999105930328D;
		move(MoverType.SELF, motionX, motionY, motionZ);
		motionY *= 0.98000001907348633D;
		
		if (onGround) {
			setDead();
			if(!getEntityWorld().isRemote) {
				getEntityWorld().newExplosion(this, posX, posY, posZ, 0.5F, false, true);
				CrashComet crash = new CrashComet(this.meteorType);
				crash.generate(getEntityWorld(), rand, new BlockPos((int)posX, (int)posY, (int)posZ));
			}
		} else {
			getEntityWorld().spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, posX, posY + 2.5D, posZ, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		this.meteorType = EnumMeteor.getTypeFromID(nbttagcompound.getInteger("metTypeID"));
		this.spawnPauseTicks = nbttagcompound.getInteger("pauseTicks");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setInteger("metTypeID", this.meteorType.getID());
		nbttagcompound.setInteger("pauseTicks", this.spawnPauseTicks);
	}
	
//	@SideOnly(Side.CLIENT)
//	@Override//TODO 1.12.2
//	public float getShadowSize() {
//		return 0.0F;
//	}
	
	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeInt(this.meteorType.getID());
		buffer.writeInt(this.spawnPauseTicks);
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		this.meteorType = EnumMeteor.getTypeFromID(additionalData.readInt());
		this.spawnPauseTicks = additionalData.readInt();
	}

}
