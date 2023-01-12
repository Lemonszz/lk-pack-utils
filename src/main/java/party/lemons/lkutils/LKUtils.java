package party.lemons.lkutils;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import party.lemons.lkutils.worldgen.LKWorldGen;

public class LKUtils implements ModInitializer
{
	public static final String MODID = "lkutils";

	@Override
	public void onInitialize()
	{
		LKWorldGen.init();
	}

	public static ResourceLocation id(String path){
		return new ResourceLocation(MODID, path);
	}
}
