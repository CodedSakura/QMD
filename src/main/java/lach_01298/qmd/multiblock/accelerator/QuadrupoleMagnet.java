package lach_01298.qmd.multiblock.accelerator;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import lach_01298.qmd.multiblock.accelerator.tile.IAcceleratorComponent;
import nc.multiblock.fission.tile.IFissionComponent;

public class QuadrupoleMagnet
{
	private final Accelerator accelerator;
	private int id;
	
	private final Long2ObjectMap<IAcceleratorComponent> componentMap = new Long2ObjectOpenHashMap<>();
	private int componentCount;
	
	
	public QuadrupoleMagnet(Accelerator accelerator, int id)
	{
		this.accelerator = accelerator;
		this.id = id;
	}
	
	public int getId() 
	{
		return id;
	}
	
	public void setId(int newId) 
	{
		id = newId;
	}
	
	public Long2ObjectMap<IAcceleratorComponent> getComponentMap()
	{
		return componentMap;
	}
	

	
	
}
