package com.bioxx.tfc2;

import java.io.File;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.bioxx.tfc2.api.TFCFluids;
import com.bioxx.tfc2.api.ore.OreConfig;
import com.bioxx.tfc2.api.ore.OreConfig.VeinType;
import com.bioxx.tfc2.api.ore.OreRegistry;
import com.bioxx.tfc2.api.types.OreType;
import com.bioxx.tfc2.api.types.StoneType;
import com.bioxx.tfc2.core.FluidTFC;
import com.bioxx.tfc2.handlers.CreateSpawnHandler;
import com.bioxx.tfc2.handlers.ServerTickHandler;
import com.bioxx.tfc2.handlers.WorldLoadHandler;
import com.bioxx.tfc2.world.WorldProviderSurface;
import com.bioxx.tfc2.world.generators.WorldGenGrass;
import com.bioxx.tfc2.world.generators.WorldGenTreeTest;

public class CommonProxy
{

	public void preInit(FMLPreInitializationEvent event)
	{
		GameRegistry.registerWorldGenerator(new WorldGenTreeTest(), 0);
		GameRegistry.registerWorldGenerator(new WorldGenGrass(), 0);

		DimensionManager.unregisterDimension(0);
		DimensionManager.unregisterProviderType(0);
		DimensionManager.registerProviderType(0, WorldProviderSurface.class, true);
		DimensionManager.registerDimension(0, 0);

		ResourceLocation still = Core.CreateRes(Reference.getResID()+"blocks/water_still");
		ResourceLocation flow = Core.CreateRes(Reference.getResID()+"blocks/water_flow");
		TFCFluids.SALTWATER = new FluidTFC("saltwater", still, flow).setBaseColor(0xff001945);
		TFCFluids.FRESHWATER = new FluidTFC("freshwater", still, flow).setBaseColor(0xff001945);
		FluidRegistry.registerFluid(TFCFluids.SALTWATER);
		FluidRegistry.registerFluid(TFCFluids.FRESHWATER);
		TFCBlocks.LoadBlocks();
		TFCBlocks.RegisterBlocks();
		TFCFluids.SALTWATER.setBlock(TFCBlocks.SaltWater).setUnlocalizedName(TFCBlocks.SaltWater.getUnlocalizedName());
		TFCFluids.FRESHWATER.setBlock(TFCBlocks.FreshWater).setUnlocalizedName(TFCBlocks.FreshWater.getUnlocalizedName());

		setupOre();
	}

	public void init(FMLInitializationEvent event)
	{

	}

	public void postInit(FMLPostInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(new CreateSpawnHandler());
		MinecraftForge.EVENT_BUS.register(new WorldLoadHandler());
		FMLCommonHandler.instance().bus().register(new ServerTickHandler());
	}

	protected void setupOre()
	{
		OreRegistry.getInstance().registerOre(OreType.Bismuthinite.getName(), new OreConfig(VeinType.Seam, TFCBlocks.Ore, OreType.Bismuthinite, /*wMin*/2, /*wMax*/3, /*hMin*/1, /*hMax*/1), StoneType.getForSubTypes(StoneType.SubType.IgneousExtrusive, StoneType.SubType.Sedimentary));
		OreRegistry.getInstance().registerOre(OreType.Anthracite.getName(), new OreConfig(VeinType.Layer, TFCBlocks.Ore, OreType.Anthracite, /*wMin*/0, /*wMax*/0, /*hMin*/1, /*hMax*/3).setNoiseVertical(1).setMinSeamLength(2).setMaxSeamLength(8), new StoneType[] {StoneType.Chert, StoneType.Dolomite, StoneType.Limestone});
		OreRegistry.getInstance().registerOre(OreType.Lignite.getName(), new OreConfig(VeinType.Layer, TFCBlocks.Ore, OreType.Lignite, /*wMin*/0, /*wMax*/0, /*hMin*/1, /*hMax*/3).setNoiseVertical(1).setMinSeamLength(2).setMaxSeamLength(8), new StoneType[] {StoneType.Shale, StoneType.Claystone});
		OreRegistry.getInstance().registerOre(OreType.Cassiterite.getName(), new OreConfig(VeinType.Seam, TFCBlocks.Ore, OreType.Cassiterite, /*wMin*/1, /*wMax*/2, /*hMin*/1, /*hMax*/1).setRarity(1).setSubSeamRarity(3).setMinSeamLength(20).setMaxSeamLength(40), StoneType.getForSubTypes(StoneType.SubType.IgneousIntrusive));
		OreRegistry.getInstance().registerOre(OreType.Tetrahedrite.getName(), new OreConfig(VeinType.Seam, TFCBlocks.Ore, OreType.Tetrahedrite, /*wMin*/1, /*wMax*/1, /*hMin*/1, /*hMax*/3), StoneType.getForSubTypes(StoneType.SubType.Metamorphic));
		OreRegistry.getInstance().registerOre(OreType.Sphalerite.getName(), new OreConfig(VeinType.Seam, TFCBlocks.Ore, OreType.Sphalerite, /*wMin*/2, /*wMax*/3, /*hMin*/1, /*hMax*/1), StoneType.getForSubTypes(StoneType.SubType.Metamorphic));
		OreRegistry.getInstance().registerOre(OreType.Garnierite.getName(), new OreConfig(VeinType.Seam, TFCBlocks.Ore, OreType.Garnierite, /*wMin*/1, /*wMax*/1, /*hMin*/1, /*hMax*/1).setRarity(3), StoneType.getForSubTypes(StoneType.SubType.IgneousIntrusive));
		OreRegistry.getInstance().registerOre(OreType.Hematite.getName(), new OreConfig(VeinType.Seam, TFCBlocks.Ore, OreType.Hematite, /*wMin*/2, /*wMax*/3, /*hMin*/1, /*hMax*/2).setRarity(1).setSubSeamRarity(5), StoneType.getForSubTypes(StoneType.SubType.IgneousExtrusive));
		OreRegistry.getInstance().registerOre(OreType.Magnetite.getName(), new OreConfig(VeinType.Seam, TFCBlocks.Ore, OreType.Magnetite, /*wMin*/1, /*wMax*/2, /*hMin*/3, /*hMax*/4).setSubSeamRarity(5), new StoneType[] {StoneType.Chert, StoneType.Dolomite, StoneType.Claystone});
		OreRegistry.getInstance().registerOre(OreType.Limonite.getName(), new OreConfig(VeinType.Seam, TFCBlocks.Ore, OreType.Limonite, /*wMin*/2, /*wMax*/3, /*hMin*/1, /*hMax*/2).setRarity(1), new StoneType[] {StoneType.Shale, StoneType.Limestone});
		OreRegistry.getInstance().registerOre(OreType.Malachite.getName(), new OreConfig(VeinType.Seam, TFCBlocks.Ore, OreType.Malachite, /*wMin*/1, /*wMax*/1, /*hMin*/1, /*hMax*/1).setRarity(20).setMinSeamLength(3).setMaxSeamLength(10), new StoneType[] {StoneType.Marble});
		OreRegistry.getInstance().registerOre(OreType.NativeGold.getName(), new OreConfig(VeinType.Seam, TFCBlocks.Ore, OreType.NativeGold, /*wMin*/1, /*wMax*/1, /*hMin*/1, /*hMax*/1).setRarity(8).setMinSeamLength(4).setMaxSeamLength(8), StoneType.getForSubTypes(StoneType.SubType.IgneousIntrusive, StoneType.SubType.IgneousExtrusive));
		OreRegistry.getInstance().registerOre(OreType.Galena.getName(), new OreConfig(VeinType.Seam, TFCBlocks.Ore, OreType.Galena, /*wMin*/1, /*wMax*/2, /*hMin*/1, /*hMax*/2).setRarity(2), StoneType.getForSubTypes(StoneType.SubType.Metamorphic, StoneType.SubType.IgneousExtrusive));
	}

	public void setupFluids()
	{
		//FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluid(TFCFluids.LAVA.getName()), new ItemStack(TFCItems.BlueSteelBucketLava), new ItemStack(TFCItems.BlueSteelBucketEmpty));
	}

	public File getMinecraftDir()
	{
		return FMLCommonHandler.instance().getMinecraftServerInstance().getFile("");/*new File(".");*/
	}

	public void registerKeys()
	{

	}

	public void registerKeyBindingHandler()
	{

	}

	public void uploadKeyBindingsToGame()
	{

	}
}
