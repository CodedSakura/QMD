package lach_01298.qmd.machine.block;

import static nc.block.property.BlockProperties.ACTIVE;
import static nc.block.property.BlockProperties.FACING_HORIZONTAL;

import java.util.Random;

import lach_01298.qmd.block.QMDBlocks;
import lach_01298.qmd.machine.tile.TileOreLeacher;
import nc.block.tile.BlockSidedTile;
import nc.block.tile.IActivatable;
import nc.block.tile.ITileType;
import nc.tab.NCTabs;
import nc.util.BlockHelper;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockOreLeacher extends BlockSidedTile implements IActivatable
{

	public BlockOreLeacher()
	{
		super(Material.IRON);
		setCreativeTab(NCTabs.MACHINE);

	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileOreLeacher();
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		EnumFacing enumfacing = EnumFacing.byIndex(meta & 7);
		if (enumfacing.getAxis() == EnumFacing.Axis.Y)
		{
			enumfacing = EnumFacing.NORTH;
		}
		return getDefaultState().withProperty(FACING_HORIZONTAL, enumfacing).withProperty(ACTIVE,
				Boolean.valueOf((meta & 8) > 0));
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		int i = state.getValue(FACING_HORIZONTAL).getIndex();
		if (state.getValue(ACTIVE).booleanValue())
			i |= 8;
		return i;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] { FACING_HORIZONTAL, ACTIVE });
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
			float hitZ, int meta, EntityLivingBase placer)
	{
		return getDefaultState().withProperty(FACING_HORIZONTAL, placer.getHorizontalFacing().getOpposite())
				.withProperty(ACTIVE, Boolean.valueOf(false));
	}

	@Override
	public void setState(boolean isActive, TileEntity tile)
	{
		World world = tile.getWorld();
		BlockPos pos = tile.getPos();
		IBlockState state = world.getBlockState(pos);
		if (!world.isRemote && state.getBlock() == QMDBlocks.oreLeacher)
		{
			if (isActive != state.getValue(ACTIVE))
			{
				world.setBlockState(pos, state.withProperty(ACTIVE, isActive), 2);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand)
	{
		if (!state.getValue(ACTIVE))
			return;
		BlockHelper.spawnParticleOnProcessor(state, world, pos, rand, state.getValue(FACING_HORIZONTAL),"smoke");
		BlockHelper.spawnParticleOnProcessor(state, world, pos, rand, state.getValue(FACING_HORIZONTAL),"reddust");

	}


}