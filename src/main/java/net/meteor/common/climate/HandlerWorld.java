package net.meteor.common.climate;

import net.meteor.common.MeteorsMod;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HandlerWorld {
	
	public static final String METEORS_FALL_GAMERULE = "meteorsFall";
	public static final String SUMMON_METEORS_GAMERULE = "summonMeteors";
	
	private HandlerMeteorTick worldTickHandler = new HandlerMeteorTick();
	
	public HandlerWorld() {
		FMLCommonHandler.instance().bus().register(worldTickHandler);
	}
	
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		if (!event.getWorld().isRemote) {
			addRules(event.getWorld());
			int dim = event.getWorld().provider.getDimension();
			HandlerMeteor metHandler = new HandlerMeteor(event, worldTickHandler);
			MeteorsMod.proxy.metHandlers.put(dim, metHandler);
		}
	}
	
	private void addRules(World world) {
		GameRules rules = world.getGameRules();
		addRule(rules, METEORS_FALL_GAMERULE, "true");
		addRule(rules, SUMMON_METEORS_GAMERULE, "true");
	}
	
	private void addRule(GameRules rules, String key, String val) {
		if (!rules.hasRule(key)) {
			rules.addGameRule(key, val, GameRules.ValueType.ANY_VALUE);
		}
	}

}
