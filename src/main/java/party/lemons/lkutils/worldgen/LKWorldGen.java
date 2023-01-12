package party.lemons.lkutils.worldgen;

import net.minecraft.core.Registry;
import party.lemons.lkutils.LKUtils;
import party.lemons.lkutils.worldgen.feature.LKOreFeature;

public class LKWorldGen
{
	public static void init()
	{
		Registry.register(Registry.FEATURE, LKUtils.id("ore"), new LKOreFeature(LKOreFeature.LKOreConfig.CODEC));
	}
}
