package com.bioxx.tfc2.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.Reference;
import com.bioxx.tfc2.api.properties.PropertyItem;
import com.bioxx.tfc2.rendering.particles.ParticleAnvil;
import com.bioxx.tfc2.tileentities.TileAnvil;

public class BlockAnvil extends BlockTerra implements ITileEntityProvider
{
	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	public static final PropertyItem INVENTORY = new PropertyItem();

	public static final AxisAlignedBB AABB_EW = new AxisAlignedBB(0.19,0,0,0.81,0.63,1);
	public static final AxisAlignedBB AABB_NS = new AxisAlignedBB(0,0,0.19,1,0.63,0.81);

	public BlockAnvil()
	{
		super(Material.GRASS, FACING);
		this.setCreativeTab(CreativeTabs.TOOLS);
		this.isBlockContainer = true;
		setSoundType(SoundType.GROUND);
	}

	/*******************************************************************************
	 * 1. Content
	 *******************************************************************************/

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, net.minecraft.util.EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if(worldIn.isRemote)
			return false;
		worldIn.getMinecraftServer().getPlayerList().sendToAllNearExcept(null, pos.getX(), pos.getY(), pos.getZ(), 200, worldIn.provider.getDimension(), worldIn.getTileEntity(pos).getDescriptionPacket());
		return true;
	}

	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
	{
		IBlockState soil = worldIn.getBlockState(pos.down());
		return soil.getBlock().isSideSolid(soil, worldIn, pos.down(), EnumFacing.UP);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing()));

	}

	/*******************************************************************************
	 * 2. Rendering
	 *******************************************************************************/

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand)
	{
		TileAnvil te = (TileAnvil)world.getTileEntity(pos);

		/*if(rand.nextInt(5) == 0)
		{
			Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleStrikeCrit(world, pos.getX()+Math.floor(4+rand.nextInt(4)*2)/16f, pos.getY()+0.71, pos.getZ()+Math.floor(4+rand.nextInt(4)*2)/16f));
		}
		else if(rand.nextInt(5) == 0)
		{
			Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleStrike(world, pos.getX()+Math.floor(4+rand.nextInt(4)*2)/16f, pos.getY()+0.71, pos.getZ()+Math.floor(4+rand.nextInt(4)*2)/16f));
		}*/
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Block.EnumOffsetType getOffsetType()
	{
		return Block.EnumOffsetType.NONE;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		if(state.getValue(FACING) == EnumFacing.NORTH || state.getValue(FACING) == EnumFacing.SOUTH)
			return AABB_NS;
		return AABB_EW;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World worldIn, BlockPos pos)
	{
		if(state.getValue(FACING) == EnumFacing.NORTH || state.getValue(FACING) == EnumFacing.SOUTH)
			return AABB_NS;
		return AABB_EW;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	/*******************************************************************************
	 * 3. Blockstate 
	 *******************************************************************************/

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		TileAnvil te = (TileAnvil) world.getTileEntity(pos);
		if(te != null)
			return te.writeExtendedBlockState((IExtendedBlockState) state);
		return state;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new ExtendedBlockState(this, new IProperty[]{FACING}, new IUnlistedProperty[]{INVENTORY});
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta & 3));
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state)
	{
		int i = 0;
		i = i | ((EnumFacing)state.getValue(FACING)).getHorizontalIndex();
		return i;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) 
	{
		return new TileAnvil();
	}

	@Override
	public Item getItemDropped(IBlockState paramIBlockState, Random paramRandom, int paramInt)
	{
		return null;
	}

	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
	{
		return true;
	}

	/*******************************************************************************
	 * Particles 
	 *******************************************************************************/

	public static class ParticleStrike extends ParticleAnvil
	{
		static final ResourceLocation TEX = Core.CreateRes(Reference.ModID+":textures/particles/strike.png");
		protected ParticleStrike(World worldIn, double posXIn, double posYIn, double posZIn) 
		{
			super(worldIn, posXIn, posYIn, posZIn);
		}

		@Override
		protected ResourceLocation getTexture() 
		{
			return TEX;
		}

	}

	public static class ParticleStrikeCrit extends ParticleAnvil
	{
		static final ResourceLocation TEX = Core.CreateRes(Reference.ModID+":textures/particles/strike_crit.png");
		protected ParticleStrikeCrit(World worldIn, double posXIn, double posYIn, double posZIn) 
		{
			super(worldIn, posXIn, posYIn, posZIn);
		}

		@Override
		protected ResourceLocation getTexture() 
		{
			return TEX;
		}

	}
}