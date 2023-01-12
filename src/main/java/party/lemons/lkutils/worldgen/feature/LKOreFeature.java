package party.lemons.lkutils.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.BulkSectionAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class LKOreFeature extends Feature<LKOreFeature.LKOreConfig>
{
	public LKOreFeature(Codec<LKOreConfig> codec)
	{
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<LKOreConfig> ctx) {
		RandomSource random = ctx.random();
		BlockPos origin = ctx.origin();
		WorldGenLevel level = ctx.level();
		LKOreConfig cfg = ctx.config();

		float direction = random.nextFloat() * Mth.PI;
		float scale = (float)cfg.size / 8.0F;

		int i = Mth.ceil(((float)cfg.size / 16.0F * 2.0F + 1.0F) / 2.0F);
		double xDir1 = (double)origin.getX() + Math.sin(direction) * (double)scale;
		double xDir2 = (double)origin.getX() - Math.sin(direction) * (double)scale;
		double zDir1 = (double)origin.getZ() + Math.cos(direction) * (double)scale;
		double zDir2 = (double)origin.getZ() - Math.cos(direction) * (double)scale;

		double yDir1 = origin.getY() + random.nextInt(3) - 2;
		double yDir2 = origin.getY() + random.nextInt(3) - 2;
		int minX = origin.getX() - Mth.ceil(scale) - i;
		int yLevel = origin.getY() - 2 - i;
		int minZ = origin.getZ() - Mth.ceil(scale) - i;
		int maxSize = 2 * (Mth.ceil(scale) + i);
		int r = 2 * (2 + i);

		for(int xx = minX; xx <= minX + maxSize; ++xx) {
			for(int zz = minZ; zz <= minZ + maxSize; ++zz)
			{
				if (yLevel <= level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, xx, zz)) {
					return this.doPlace(level, random, cfg, xDir1, xDir2, zDir1, zDir2, yDir1, yDir2, minX, yLevel, minZ, maxSize, r);
				}
			}
		}

		return false;
	}

	protected boolean doPlace(WorldGenLevel level, RandomSource random, LKOreConfig cfg, double xDir1, double xDir2, double zDir1, double zDir2, double yDir1, double yDir2, int minX, int yLevel, int minZ, int maxSize, int n) {
		int o = 0;
		BitSet bitSet = new BitSet(maxSize * n * maxSize);
		BlockPos.MutableBlockPos genPos = new BlockPos.MutableBlockPos();
		int p = cfg.size;
		double[] ds = new double[p * 4];

		int q;
		double s;
		double t;
		double u;
		double v;
		for(q = 0; q < p; ++q) {
			float r = (float)q / (float)p;
			s = Mth.lerp(r, xDir1, xDir2);
			t = Mth.lerp(r, yDir1, yDir2);
			u = Mth.lerp(r, zDir1, zDir2);
			v = random.nextDouble() * (double)p / 16.0D;
			double w = ((double)(Mth.sin(3.1415927F * r) + 1.0F) * v + 1.0D) / 2.0D;
			ds[q * 4 + 0] = s;
			ds[q * 4 + 1] = t;
			ds[q * 4 + 2] = u;
			ds[q * 4 + 3] = w;
		}

		int x;
		for(q = 0; q < p - 1; ++q) {
			if (!(ds[q * 4 + 3] <= 0.0D)) {
				for(x = q + 1; x < p; ++x) {
					if (!(ds[x * 4 + 3] <= 0.0D)) {
						s = ds[q * 4 + 0] - ds[x * 4 + 0];
						t = ds[q * 4 + 1] - ds[x * 4 + 1];
						u = ds[q * 4 + 2] - ds[x * 4 + 2];
						v = ds[q * 4 + 3] - ds[x * 4 + 3];
						if (v * v > s * s + t * t + u * u) {
							if (v > 0.0D) {
								ds[x * 4 + 3] = -1.0D;
							} else {
								ds[q * 4 + 3] = -1.0D;
							}
						}
					}
				}
			}
		}

		BulkSectionAccess bulkSectionAccess = new BulkSectionAccess(level);

		try {
			for(x = 0; x < p; ++x) {
				s = ds[x * 4 + 3];
				if (!(s < 0.0D)) {
					t = ds[x * 4 + 0];
					u = ds[x * 4 + 1];
					v = ds[x * 4 + 2];
					int y = Math.max(Mth.floor(t - s), minX);
					int z = Math.max(Mth.floor(u - s), yLevel);
					int aa = Math.max(Mth.floor(v - s), minZ);
					int ab = Math.max(Mth.floor(t + s), y);
					int ac = Math.max(Mth.floor(u + s), z);
					int ad = Math.max(Mth.floor(v + s), aa);

					for(int xx = y; xx <= ab; ++xx) {
						double af = ((double)xx + 0.5D - t) / s;
						if (af * af < 1.0D) {
							for(int yy = z; yy <= ac; ++yy) {
								double ah = ((double)yy + 0.5D - u) / s;
								if (af * af + ah * ah < 1.0D) {
									for(int zz = aa; zz <= ad; ++zz) {
										double aj = ((double)zz + 0.5D - v) / s;
										if (af * af + ah * ah + aj * aj < 1.0D && !level.isOutsideBuildHeight(yy)) {
											int ak = xx - minX + (yy - yLevel) * maxSize + (zz - minZ) * maxSize * n;
											if (!bitSet.get(ak)) {
												bitSet.set(ak);
												genPos.set(xx, yy, zz);
												if (level.ensureCanWrite(genPos)) {
													LevelChunkSection levelChunkSection = bulkSectionAccess.getSection(genPos);
													if (levelChunkSection != null) {
														int finalX = SectionPos.sectionRelative(xx);
														int finalY = SectionPos.sectionRelative(yy);
														int finalZ = SectionPos.sectionRelative(zz);
														BlockState blockState = levelChunkSection.getBlockState(finalX, finalY, finalZ);
														Iterator<LKOreConfig.TargetBlockState> var57 = cfg.targetStates.iterator();

														while(var57.hasNext()) {
															LKOreConfig.TargetBlockState targetBlockState = var57.next();
															Objects.requireNonNull(bulkSectionAccess);
															if (canPlaceOre(blockState, bulkSectionAccess::getBlockState, random, cfg, targetBlockState, genPos)) {
																levelChunkSection.setBlockState(finalX, finalY, finalZ, targetBlockState.stateProvider.getState(random, genPos), false);
																++o;
																break;
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Throwable var60) {
			try {
				bulkSectionAccess.close();
			} catch (Throwable var59) {
				var60.addSuppressed(var59);
			}

			throw var60;
		}

		bulkSectionAccess.close();
		return o > 0;
	}

	public static boolean canPlaceOre(BlockState blockState, Function<BlockPos, BlockState> function, RandomSource randomSource, LKOreConfig oreConfiguration, LKOreConfig.TargetBlockState targetBlockState, BlockPos.MutableBlockPos mutableBlockPos) {
		if (!targetBlockState.target.test(blockState, randomSource)) {
			return false;
		} else if (shouldSkipAirCheck(randomSource, oreConfiguration.discardChanceOnAirExposure)) {
			return true;
		} else {
			return !isAdjacentToAir(function, mutableBlockPos);
		}
	}


	protected static boolean shouldSkipAirCheck(RandomSource randomSource, float f) {
		if (f <= 0.0F) {
			return true;
		} else if (f >= 1.0F) {
			return false;
		} else {
			return randomSource.nextFloat() >= f;
		}
	}

	public static class LKOreConfig implements FeatureConfiguration
	{
		public static final Codec<LKOreConfig> CODEC = RecordCodecBuilder.create(
				(instance) -> instance.group(
						Codec.list(LKOreConfig.TargetBlockState.CODEC).fieldOf("targets").forGetter((cfg) -> cfg.targetStates),
						Codec.intRange(0, 64).fieldOf("size").forGetter((cfg) -> cfg.size),
						Codec.floatRange(0.0F, 1.0F).fieldOf("discard_chance_on_air_exposure").forGetter((cfg) -> cfg.discardChanceOnAirExposure)
				).apply(instance, LKOreConfig::new));

		public final List<LKOreConfig.TargetBlockState> targetStates;
		public final int size;
		public final float discardChanceOnAirExposure;

		public LKOreConfig(List<LKOreConfig.TargetBlockState> list, int size, float discardChance)
		{
			this.size = size;
			this.targetStates = list;
			this.discardChanceOnAirExposure = discardChance;
		}

		public static class TargetBlockState
		{
			public static final Codec<LKOreConfig.TargetBlockState> CODEC = RecordCodecBuilder.create((instance) ->
					instance.group(
							RuleTest.CODEC.fieldOf("target").forGetter((target) -> target.target),
							BlockStateProvider.CODEC.fieldOf("state").forGetter((target) -> target.stateProvider)
					).apply(instance, TargetBlockState::new));

			public final RuleTest target;
			public final BlockStateProvider stateProvider;

			TargetBlockState(RuleTest ruleTest, BlockStateProvider stateProvider) {
				this.target = ruleTest;
				this.stateProvider = stateProvider;
			}
		}
	}
}
