package net.meteor.common.crash;

import java.util.ArrayList;
import java.util.Random;

import net.meteor.common.EnumMeteor;
import net.meteor.common.MeteorsMod;
import net.meteor.common.SBAPI;
import net.minecraft.block.Block;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.init.Blocks;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class CrashKreknorite extends CrashMeteorite
{
	public CrashKreknorite(int Size, Explosion expl, EnumMeteor metType)
	{
		super(Size, expl, metType);
	}

	@Override
    public void afterCraterFormed(World world, Random random, int i, int j, int k) {
		if (this.crashSize >= MeteorsMod.instance.MinMeteorSizeForPortal) {
			createPortal(world, i, j, k, random.nextBoolean());
		}
		int blazes = random.nextInt(3);
		ArrayList<ChunkPosition> arraylist = new ArrayList(this.explosion.affectedBlockPositions);
		for (int j1 = arraylist.size() - 1; (j1 >= 0) && 
				(blazes > 0); j1--)
		{
			ChunkPosition chunkposition1 = arraylist.get(j1);
			int l = chunkposition1.chunkPosX;
			int j11 = chunkposition1.chunkPosY;
			int l1 = chunkposition1.chunkPosZ;
			boolean j2 = world.isAirBlock(l, j11, l1);
			Block k2 = world.getBlock(l, j11 - 1, l1);
			if (j2 && k2.isOpaqueCube() && (random.nextInt(10) > 4)) {
				EntityBlaze blaze = new EntityBlaze(world);
				blaze.setPositionAndRotation(l, j11, l1, 0.0F, 0.0F);
				world.spawnEntityInWorld(blaze);
				blazes--;
			}
		}
	}

	private void createPortal(World world, int i, int j, int k, boolean directionFlag) {
		if (directionFlag) {
			//Base
			SBAPI.generateCuboid(world, i + 1, j + 1, k, i, j + 1, k, Blocks.nether_brick);
			SBAPI.generateCuboid(world, i + 2, j + 2, k + 1, i - 1, j + 2, k - 1, Blocks.nether_brick);
			SBAPI.generateCuboid(world, i + 3, j + 3, k + 2, i - 2, j + 3, k - 2, Blocks.nether_brick);
			//Portal
			SBAPI.generateCuboid(world, i + 2, j + 3, k, i - 1, j + 7, k, Blocks.obsidian, 0, true);
			//Columns
			SBAPI.generateCuboid(world, i + 3, j + 4, k + 2, i + 3, j + 8, k + 2, Blocks.nether_brick);
			SBAPI.generateCuboid(world, i + 3, j + 4, k - 2, i + 3, j + 8, k - 2, Blocks.nether_brick);
			SBAPI.generateCuboid(world, i - 2, j + 4, k - 2, i - 2, j + 8, k - 2, Blocks.nether_brick);
			SBAPI.generateCuboid(world, i - 2, j + 4, k + 2, i - 2, j + 8, k + 2, Blocks.nether_brick);
			//First level of roof
			SBAPI.generateCuboid(world, i + 3, j + 8, k + 3, i - 2, j + 8, k + 3, Blocks.nether_brick);
			SBAPI.generateCuboid(world, i + 3, j + 8, k - 3, i - 2, j + 8, k - 3, Blocks.nether_brick);
			SBAPI.generateCuboid(world, i + 4, j + 8, k + 2, i + 4, j + 8, k - 2, Blocks.nether_brick);
			SBAPI.generateCuboid(world, i - 3, j + 8, k + 2, i - 3, j + 8, k - 2, Blocks.nether_brick);
			//Second level of roof
			SBAPI.generateCuboid(world, i + 3, j + 9, k + 2, i - 2, j + 9, k - 2, Blocks.nether_brick);
			world.setBlock(i + 3, j + 9, k + 2, Blocks.air, 0, 3);
			world.setBlock(i + 3, j + 9, k - 2, Blocks.air, 0, 3);
			world.setBlock(i - 2, j + 9, k - 2, Blocks.air, 0, 3);
			world.setBlock(i - 2, j + 9, k + 2, Blocks.air, 0, 3);
			SBAPI.generateCuboid(world, i + 2, j + 9, k + 1, i - 1, j + 9, k - 1, Blocks.glowstone);
			//Top level of roof
			SBAPI.generateCuboid(world, i + 2, j + 10, k + 1, i - 1, j + 10, k - 1, Blocks.nether_brick);
		} else {
			//Base
			SBAPI.generateCuboid(world, i, j + 1, k + 1, i, j + 1, k, Blocks.nether_brick);
			SBAPI.generateCuboid(world, i + 1, j + 2, k + 2, i - 1, j + 2, k - 1, Blocks.nether_brick);
			SBAPI.generateCuboid(world, i + 2, j + 3, k + 3, i - 2, j + 3, k - 2, Blocks.nether_brick);
			//Portal
			SBAPI.generateCuboid(world, i, j + 3, k + 2, i, j + 7, k - 1, Blocks.obsidian, 0, true);
			//Columns
			SBAPI.generateCuboid(world, i - 2, j + 4, k + 3, i - 2, j + 8, k + 3, Blocks.nether_brick);
			SBAPI.generateCuboid(world, i - 2, j + 4, k - 2, i - 2, j + 8, k - 2, Blocks.nether_brick);
			SBAPI.generateCuboid(world, i + 2, j + 4, k - 2, i + 2, j + 8, k - 2, Blocks.nether_brick);
			SBAPI.generateCuboid(world, i + 2, j + 4, k + 3, i + 2, j + 8, k + 3, Blocks.nether_brick);
			//First level of roof
			SBAPI.generateCuboid(world, i + 3, j + 8, k + 3, i + 3, j + 8, k - 2, Blocks.nether_brick);
			SBAPI.generateCuboid(world, i - 3, j + 8, k + 3, i - 3, j + 8, k - 2, Blocks.nether_brick);
			SBAPI.generateCuboid(world, i + 2, j + 8, k + 4, i - 2, j + 8, k + 4, Blocks.nether_brick);
			SBAPI.generateCuboid(world, i + 2, j + 8, k - 3, i - 2, j + 8, k - 3, Blocks.nether_brick);
			//Second level of roof
			SBAPI.generateCuboid(world, i + 2, j + 9, k + 3, i - 2, j + 9, k - 2, Blocks.nether_brick);
			world.setBlock(i - 2, j + 9, k + 3, Blocks.air, 0, 3);
			world.setBlock(i - 2, j + 9, k - 2, Blocks.air, 0, 3);
			world.setBlock(i + 2, j + 9, k - 2, Blocks.air, 0, 3);
			world.setBlock(i + 2, j + 9, k + 3, Blocks.air, 0, 3);
			SBAPI.generateCuboid(world, i + 1, j + 9, k + 2, i - 1, j + 9, k - 1, Blocks.glowstone);
			//Top level of roof
			SBAPI.generateCuboid(world, i + 1, j + 10, k + 2, i - 1, j + 10, k - 1, Blocks.nether_brick);
		}
		world.setBlock(i, j + 4, k, Blocks.fire, 0, 3);
	}

}