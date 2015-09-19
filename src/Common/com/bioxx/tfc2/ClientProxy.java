package com.bioxx.tfc2;

import java.io.File;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import com.bioxx.tfc2.api.Global;
import com.bioxx.tfc2.api.types.OreType;
import com.bioxx.tfc2.api.types.WoodType;
import com.bioxx.tfc2.api.util.KeyBindings;
import com.bioxx.tfc2.handlers.client.BackgroundMusicHandler;
import com.bioxx.tfc2.handlers.client.ClientRenderHandler;
import com.bioxx.tfc2.handlers.client.GuiHandler;
import com.bioxx.tfc2.handlers.client.KeyBindingHandler;

public class ClientProxy extends CommonProxy
{
	private static ModelResourceLocation freshwaterLocation = new ModelResourceLocation(Reference.getResID() + "liquids", "freshwater");
	private static ModelResourceLocation saltwaterLocation = new ModelResourceLocation(Reference.getResID() + "liquids", "saltwater");

	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		super.preInit(event);
		Item fresh = Item.getItemFromBlock(TFCBlocks.FreshWater);
		Item salt = Item.getItemFromBlock(TFCBlocks.SaltWater);
		Item saltstatic = Item.getItemFromBlock(TFCBlocks.SaltWaterStatic);
		Item freshstatic = Item.getItemFromBlock(TFCBlocks.FreshWaterStatic);
		ModelBakery.addVariantName(fresh);
		ModelBakery.addVariantName(salt);
		ModelBakery.addVariantName(saltstatic);
		ModelBakery.addVariantName(freshstatic);
		ModelLoader.setCustomMeshDefinition(fresh, new ItemMeshDefinition()
		{
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack)
			{
				return freshwaterLocation;
			}
		});
		ModelLoader.setCustomMeshDefinition(salt, new ItemMeshDefinition()
		{
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack)
			{
				return saltwaterLocation;
			}
		});
		ModelLoader.setCustomMeshDefinition(saltstatic, new ItemMeshDefinition()
		{
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack)
			{
				return saltwaterLocation;
			}
		});
		ModelLoader.setCustomMeshDefinition(freshstatic, new ItemMeshDefinition()
		{
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack)
			{
				return freshwaterLocation;
			}
		});
		ModelLoader.setCustomStateMapper(TFCBlocks.FreshWater, new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return freshwaterLocation;
			}
		});
		ModelLoader.setCustomStateMapper(TFCBlocks.SaltWater, new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return saltwaterLocation;
			}
		});
		ModelLoader.setCustomStateMapper(TFCBlocks.SaltWaterStatic, new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return saltwaterLocation;
			}
		});
		ModelLoader.setCustomStateMapper(TFCBlocks.FreshWaterStatic, new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return freshwaterLocation;
			}
		});
	}

	@Override
	public void init(FMLInitializationEvent event)
	{
		super.init(event);
		MinecraftForge.EVENT_BUS.register(new RenderOverlayHandler());
		MinecraftForge.EVENT_BUS.register(new ClientRenderHandler());
		MinecraftForge.EVENT_BUS.register(new BackgroundMusicHandler());
		for(int l = 0; l < Global.STONE_ALL.length; l++)
		{
			String stone = Core.textConvert(Global.STONE_ALL[l]);

			registerItemMesh(Item.getItemFromBlock(TFCBlocks.Dirt), l, new ModelResourceLocation(Reference.ModID + ":Dirt/" + stone, "inventory"));
			registerItemMesh(Item.getItemFromBlock(TFCBlocks.Grass), l, new ModelResourceLocation(Reference.ModID + ":Grass/" + stone + "/" + stone, "inventory"));
			registerItemMesh(Item.getItemFromBlock(TFCBlocks.Stone), l, new ModelResourceLocation(Reference.ModID + ":Stone/" + stone, "inventory"));
			registerItemMesh(Item.getItemFromBlock(TFCBlocks.Rubble), l, new ModelResourceLocation(Reference.ModID + ":Rubble/" + stone, "inventory"));
			registerItemMesh(Item.getItemFromBlock(TFCBlocks.Sand), l, new ModelResourceLocation(Reference.ModID + ":Sand/" + stone, "inventory"));
			registerItemMesh(Item.getItemFromBlock(TFCBlocks.Gravel), l, new ModelResourceLocation(Reference.ModID + ":Gravel/" + stone, "inventory"));
			ModelBakery.addVariantName(Item.getItemFromBlock(TFCBlocks.Dirt), Reference.ModID + ":Dirt/" + stone);
			ModelBakery.addVariantName(Item.getItemFromBlock(TFCBlocks.Grass), Reference.ModID + ":Grass/" + stone + "/" + stone);
			ModelBakery.addVariantName(Item.getItemFromBlock(TFCBlocks.Stone), Reference.ModID + ":Stone/" + stone);
			ModelBakery.addVariantName(Item.getItemFromBlock(TFCBlocks.Rubble), Reference.ModID + ":Rubble/" + stone);
			ModelBakery.addVariantName(Item.getItemFromBlock(TFCBlocks.Sand), Reference.ModID + ":Sand/" + stone);
			ModelBakery.addVariantName(Item.getItemFromBlock(TFCBlocks.Gravel), Reference.ModID + ":Gravel/" + stone);

			registerItemMesh(TFCItems.LooseRock, l, new ModelResourceLocation(Reference.ModID + ":LooseRock/" + stone, "inventory"));
			ModelBakery.addVariantName(TFCItems.LooseRock, Reference.ModID + ":LooseRock/" + stone);
		}
		for(int l = 0; l < 16; l++)
		{
			String wood = Core.textConvert(WoodType.values()[l].getName());

			registerItemMesh(Item.getItemFromBlock(TFCBlocks.Planks), l, new ModelResourceLocation(Reference.ModID + ":Wood/Planks/" + wood, "inventory"));
			ModelBakery.addVariantName(Item.getItemFromBlock(TFCBlocks.Planks), Reference.ModID + ":Wood/Planks/" + wood);
			registerItemMesh(Item.getItemFromBlock(TFCBlocks.Sapling), l, new ModelResourceLocation(Reference.ModID + ":Wood/Saplings/" + wood, "inventory"));
			ModelBakery.addVariantName(Item.getItemFromBlock(TFCBlocks.Sapling), Reference.ModID + ":Wood/Saplings/" + wood);
			registerItemMesh(Item.getItemFromBlock(TFCBlocks.LogVertical), l, new ModelResourceLocation(Reference.ModID + ":Wood/Logs/" + wood, "inventory"));
			ModelBakery.addVariantName(Item.getItemFromBlock(TFCBlocks.LogVertical), Reference.ModID + ":Wood/Logs/" + wood);
			ModelBakery.addVariantName(Item.getItemFromBlock(TFCBlocks.LogHorizontal), Reference.ModID + ":Wood/Logs/" + wood);
			ModelBakery.addVariantName(Item.getItemFromBlock(TFCBlocks.LogHorizontal2), Reference.ModID + ":Wood/Logs/" + wood);
			ModelBakery.addVariantName(Item.getItemFromBlock(TFCBlocks.LogNatural), Reference.ModID + ":Wood/Logs/" + wood);
			registerItemMesh(Item.getItemFromBlock(TFCBlocks.Leaves), l, new ModelResourceLocation(Reference.ModID + ":Wood/Leaves/" + wood, "inventory"));
			ModelBakery.addVariantName(Item.getItemFromBlock(TFCBlocks.Leaves), Reference.ModID + ":Wood/Saplings/" + wood);

		}

		for(int l = 16; l < 19; l++)
		{
			String wood = Core.textConvert(WoodType.values()[l].getName());

			registerItemMesh(Item.getItemFromBlock(TFCBlocks.Planks2), l, new ModelResourceLocation(Reference.ModID + ":Wood/Planks/" + wood, "inventory"));
			ModelBakery.addVariantName(Item.getItemFromBlock(TFCBlocks.Planks2), Reference.ModID + ":Wood/Planks/" + wood);
			registerItemMesh(Item.getItemFromBlock(TFCBlocks.Sapling2), l, new ModelResourceLocation(Reference.ModID + ":Wood/Saplings/" + wood, "inventory"));
			ModelBakery.addVariantName(Item.getItemFromBlock(TFCBlocks.Sapling2), Reference.ModID + ":Wood/Saplings/" + wood);
			registerItemMesh(Item.getItemFromBlock(TFCBlocks.LogVertical2), l, new ModelResourceLocation(Reference.ModID + ":Wood/Logs/" + wood, "inventory"));
			ModelBakery.addVariantName(Item.getItemFromBlock(TFCBlocks.LogVertical2), Reference.ModID + ":Wood/Logs/" + wood);
			ModelBakery.addVariantName(Item.getItemFromBlock(TFCBlocks.LogHorizontal3), Reference.ModID + ":Wood/Logs/" + wood);
			ModelBakery.addVariantName(Item.getItemFromBlock(TFCBlocks.LogNatural2), Reference.ModID + ":Wood/Logs/" + wood);
			registerItemMesh(Item.getItemFromBlock(TFCBlocks.Leaves2), l, new ModelResourceLocation(Reference.ModID + ":Wood/Leaves/" + wood, "inventory"));
			ModelBakery.addVariantName(Item.getItemFromBlock(TFCBlocks.Leaves2), Reference.ModID + ":Wood/Saplings/" + wood);
		}

		for(int l = 0; l < OreType.values().length; l++)
		{
			String ore = Core.textConvert(OreType.values()[l].getName());

			registerItemMesh(Item.getItemFromBlock(TFCBlocks.Ore), l, new ModelResourceLocation(Reference.ModID + ":Ore/" + ore, "inventory"));
			ModelBakery.addVariantName(Item.getItemFromBlock(TFCBlocks.Ore), Reference.ModID + ":Ore/" + ore);
		}

		registerItemMesh(TFCItems.StoneAxeHead, 0, new ModelResourceLocation(Reference.ModID + ":stoneaxehead", "inventory"));
		registerItemMesh(TFCItems.StoneAxe, 0, new ModelResourceLocation(Reference.ModID + ":stoneaxe", "inventory"));
	}

	@Override
	public void postInit(FMLPostInitializationEvent event)
	{
		super.postInit(event);

	}

	private void registerItemMesh(Item i, int meta, ModelResourceLocation mrl)
	{
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(i, meta, mrl);
	}

	@Override
	public File getMinecraftDir()
	{
		return Minecraft.getMinecraft().mcDataDir;
	}

	@Override
	public void registerKeys()
	{
		//KeyBindings.addKeyBinding(KeyBindingHandler.Key_CombatMode);
		//KeyBindings.addIsRepeating(false);
		//ClientRegistry.registerKeyBinding(KeyBindingHandler.Key_ToolMode);
		//ClientRegistry.registerKeyBinding(KeyBindingHandler.Key_LockTool);
		ClientRegistry.registerKeyBinding(KeyBindingHandler.Key_CombatMode);
		//uploadKeyBindingsToGame();
	}

	@Override
	public void registerKeyBindingHandler()
	{
		FMLCommonHandler.instance().bus().register(new KeyBindingHandler());
	}

	@Override
	public void uploadKeyBindingsToGame()
	{
		GameSettings settings = Minecraft.getMinecraft().gameSettings;
		KeyBinding[] tfcKeyBindings = KeyBindings.gatherKeyBindings();
		KeyBinding[] allKeys = new KeyBinding[settings.keyBindings.length + tfcKeyBindings.length];
		System.arraycopy(settings.keyBindings, 0, allKeys, 0, settings.keyBindings.length);
		System.arraycopy(tfcKeyBindings, 0, allKeys, settings.keyBindings.length, tfcKeyBindings.length);
		settings.keyBindings = allKeys;
		settings.loadOptions();
	}

	@Override
	public boolean isClientSide()
	{
		return true;
	}

	@Override
	public void registerGuiHandler()
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(TFC.instance, new GuiHandler());
		// Register Gui Event Handler
		MinecraftForge.EVENT_BUS.register(new GuiHandler());
	}

}
