package lach_01298.qmd.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lach_01298.qmd.QMD;
import lach_01298.qmd.network.QMDPacketHandler;
import nc.Global;
import nc.ModCheck;
import nc.network.PacketHandler;
import nc.network.config.ConfigUpdatePacket;
import nc.radiation.RadSources;
import nc.recipe.ProcessorRecipeHandler;
import nc.util.Lang;
import nc.util.NCMath;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class QMDConfig {

	private static Configuration config = null;
	
	public static final String CATEGORY_PROCESSORS = "processors";
	public static final String CATEGORY_ACCELERATOR = "accelerator";
	public static final String CATEGORY_PARTICLE_CHAMBER = "particle_chamber";
	public static final String CATEGORY_OTHER = "other";
	
	public static int accelerator_linear_min_size;
	public static int accelerator_linear_max_size;
	public static int accelerator_ring_min_size;
	public static int accelerator_ring_max_size;
	
	public static int[] RF_cavity_voltage; //in keV
	public static double[] RF_cavity_efficiency;
	public static int[] RF_cavity_heat_generated;
	public static int[]  RF_cavity_base_power;
	
	public static double[] magnet_strength;
	public static double[] magnet_efficiency;
	public static int[] magnet_heat_generated;
	public static int[] magnet_base_power;
	
	
	public static int[] cooler_heat_removed;


	public static int beamAttenuationRate;

	public static int[] detector_base_power;
	public static double[] detector_efficiency;
	
	public static int minimium_accelerator_ring_input_particle_energy;
	
	
	
	public static Configuration getConfig()
	{
		return config;
	}

	public static void preInit()
	{
		File configFile = new File(Loader.instance().getConfigDir(), "qmd.cfg");
		config = new Configuration(configFile);
		syncFromFiles();

		
	}

	public static void clientPreInit()
	{
		
	}

	public static void syncFromFiles()
	{
		syncConfig(true, true);
	}

	public static void syncFromGui()
	{
		syncConfig(false, true);
	}

	public static void syncFromFields()
	{
		syncConfig(false, false);
	}

	private static void syncConfig(boolean loadFromFile, boolean setFromConfig)
	{
		if (loadFromFile) config.load();

		
		Property propertyAcceleratorLinearMinSize = config.get(CATEGORY_ACCELERATOR, "accelerator_linear_min_size", 6, Lang.localise("gui.qmd.config.accelerator.accelerator_linear_min_size.comment"), 6, 255);
		propertyAcceleratorLinearMinSize.setLanguageKey("gui.qmd.config.accelerator.accelerator_linear_min_size");
		Property propertyAcceleratorLinearMaxSize = config.get(CATEGORY_ACCELERATOR, "accelerator_linear_max_size", 100, Lang.localise("gui.qmd.config.accelerator.accelerator_linear_max_size.comment"), 6, 255);
		propertyAcceleratorLinearMaxSize.setLanguageKey("gui.qmd.config.accelerator.accelerator_linear_max_size");
		
		Property propertyAcceleratorRingMinSize = config.get(CATEGORY_ACCELERATOR, "accelerator_ring_min_size", 11, Lang.localise("gui.qmd.config.accelerator.accelerator_ring_min_size.comment"), 11, 255);
		propertyAcceleratorRingMinSize.setLanguageKey("gui.qmd.config.accelerator.accelerator_ring_min_size");
		Property propertyAcceleratorRingMaxSize = config.get(CATEGORY_ACCELERATOR, "accelerator_ring_max_size", 100, Lang.localise("gui.config.accelerator.accelerator_ring_max_size.comment"), 11, 255);
		propertyAcceleratorRingMaxSize.setLanguageKey("gui.qmd.config.accelerator.accelerator_ring_max_size");
		
		Property propertyBeamAttenuationRate = config.get(CATEGORY_ACCELERATOR, "beam_attenuation_rate", 5, Lang.localise("gui.qmd.config.accelerator.beam_attenuation_rate.comment"), 0, 255);
		propertyBeamAttenuationRate.setLanguageKey("gui.qmd.config.accelerator.beam_attenuation_rate");
		
		Property propertyRFCavityVoltage = config.get(CATEGORY_ACCELERATOR, "RF_cavity_voltage", new int[] {500, 1000, 2000}, Lang.localise("gui.qmd.config.accelerator.RF_cavity_voltage.comment"), 0, 2147483647);
		propertyRFCavityVoltage.setLanguageKey("gui.qmd.config.accelerator.RF_cavity_voltage");
		Property propertyRFCavityEfficiency = config.get(CATEGORY_ACCELERATOR, "RF_cavity_efficiency", new double[] {0.5D, 0.80D, 0.90D}, Lang.localise("gui.qmd.config.accelerator.RF_cavity_efficiency.comment"), 0D, 1D);
		propertyRFCavityEfficiency.setLanguageKey("gui.qmd.config.accelerator.RF_cavity_efficiency");
		Property propertyRFCavityHeatGenerated = config.get(CATEGORY_ACCELERATOR, "RF_cavity_heat_generated", new int[] {50, 100, 250}, Lang.localise("gui.qmd.config.accelerator.RF_cavity_heat_generated.comment"), 0, 32767);
		propertyRFCavityHeatGenerated.setLanguageKey("gui.qmd.config.accelerator.RF_cavity_heat_generated");
		Property propertyRFCavityBasePower = config.get(CATEGORY_ACCELERATOR, "RF_cavity_base_power", new int[] {2000, 4000, 8000}, Lang.localise("gui.qmd.config.accelerator.RF_cavity_base_power.comment"), 0, 32767);
		propertyRFCavityBasePower.setLanguageKey("gui.qmd.config.accelerator.RF_cavity_base_power");
		
		Property propertyMagnetStrength = config.get(CATEGORY_ACCELERATOR, "magnet_strength", new double[] {0.2D, 1D, 4D, 8D}, Lang.localise("gui.qmd.config.accelerator.magnet_strength.comment"), 0D, 100D);
		propertyMagnetStrength.setLanguageKey("gui.qmd.qmd.config.accelerator.magnet_strength");
		Property propertyMagnetEfficiency = config.get(CATEGORY_ACCELERATOR, "magnet_efficiency", new double[] {0.5, 0.60D, 0.80D, 0.90D}, Lang.localise("gui.qmd.config.accelerator.magnet_efficiency.comment"), 0D, 1D);
		propertyMagnetEfficiency.setLanguageKey("gui.qmd.qmd.config.accelerator.magnet_efficiency");
		Property propertyMagnetHeatGenerated = config.get(CATEGORY_ACCELERATOR, "magnet_heat_generated", new int[] {50, 100, 250,500}, Lang.localise("gui.qmd.config.accelerator.magnet_heat_generated.comment"),0, 32767);
		propertyMagnetHeatGenerated.setLanguageKey("gui.qmd.config.accelerator.magnet_heat_generated");
		Property propertyMagnetBasePower = config.get(CATEGORY_ACCELERATOR, "magnet_base_power", new int[] {2000, 4000, 8000, 16000}, Lang.localise("gui.qmd.config.accelerator.magnet_base_power.comment"), 0, 32767);
		propertyMagnetBasePower.setLanguageKey("gui.qmd.config.accelerator.magnet_base_power");
		
		Property propertyCoolerHeatRemoved = config.get(CATEGORY_ACCELERATOR, "cooler_heat_removed", new int[] {50, 55, 85, 75, 70, 100, 110, 95, 105, 115, 135, 60, 90, 190, 195, 80, 120, 65, 165, 125, 130, 140, 175, 170, 155, 160, 150, 145, 185, 200, 180, 205}, Lang.localise("gui.qmd.config.accelerator.cooler_heat_removed.comment"), 0, 32767);
		propertyCoolerHeatRemoved.setLanguageKey("gui.qmd.config.accelerator.cooler_heat_removed");
		
		
		Property propertyAcceleratorRingInputEnergy = config.get(CATEGORY_ACCELERATOR, "minimium_accelerator_ring_input_particle_energy", 5000, Lang.localise("gui.qmd.config.accelerator.minimium_accelerator_ring_input_particle_energy.comment"), 0, 2147483647);
		propertyAcceleratorRingInputEnergy.setLanguageKey("gui.qmd.config.accelerator.minimium_accelerator_ring_input_particle_energy");
		
		
		
		Property propertyDetectorEfficiency = config.get(CATEGORY_PARTICLE_CHAMBER, "detector_efficiency", new double[] {3.0D, 5.0D, 3.5D, 2.5D,1.5D}, Lang.localise("gui.qmd.config.particle_chamber.detector_efficiency.comment"), 0D, 100D);
		propertyMagnetEfficiency.setLanguageKey("gui.qmd.config.particle_chamber.detector_efficiency");
		Property propertyDetectorBasePower = config.get(CATEGORY_PARTICLE_CHAMBER, "detector_base_power", new int[] {200, 5000, 1000,200,100}, Lang.localise("gui.qmd.config.particle_chamber.detector_base_power.comment"), 0, 32767);
		propertyMagnetBasePower.setLanguageKey("gui.qmd.config.particle_chamber.detector_base_power");

	
		
		
		
		List<String> propertyOrderAccelerator = new ArrayList<String>();
		propertyOrderAccelerator.add(propertyAcceleratorLinearMinSize.getName());
		propertyOrderAccelerator.add(propertyAcceleratorLinearMaxSize.getName());
		propertyOrderAccelerator.add(propertyAcceleratorRingMinSize.getName());
		propertyOrderAccelerator.add(propertyAcceleratorRingMaxSize.getName());
		
		propertyOrderAccelerator.add(propertyBeamAttenuationRate.getName());
		
		propertyOrderAccelerator.add(propertyRFCavityVoltage.getName());
		propertyOrderAccelerator.add(propertyRFCavityEfficiency.getName());
		propertyOrderAccelerator.add(propertyRFCavityHeatGenerated.getName());
		propertyOrderAccelerator.add(propertyRFCavityBasePower.getName());
		
		propertyOrderAccelerator.add(propertyMagnetStrength.getName());
		propertyOrderAccelerator.add(propertyMagnetEfficiency.getName());
		propertyOrderAccelerator.add(propertyMagnetHeatGenerated.getName());
		propertyOrderAccelerator.add(propertyMagnetBasePower.getName());
		
		propertyOrderAccelerator.add(propertyCoolerHeatRemoved.getName());
		
		propertyOrderAccelerator.add(propertyAcceleratorRingInputEnergy.getName());
		
		config.setCategoryPropertyOrder(CATEGORY_ACCELERATOR, propertyOrderAccelerator);
		
		
		
		List<String> propertyOrderParticleChamber = new ArrayList<String>();
		propertyOrderParticleChamber.add(propertyDetectorEfficiency.getName());
		propertyOrderParticleChamber.add(propertyDetectorBasePower.getName());
		
		config.setCategoryPropertyOrder(CATEGORY_PARTICLE_CHAMBER, propertyOrderParticleChamber);
		
		
		if (setFromConfig) 
		{
			accelerator_linear_min_size = propertyAcceleratorLinearMinSize.getInt();
			accelerator_linear_max_size = propertyAcceleratorLinearMaxSize.getInt();
			accelerator_ring_min_size = propertyAcceleratorRingMinSize.getInt();
			accelerator_ring_max_size = propertyAcceleratorRingMaxSize.getInt();
			
			beamAttenuationRate = propertyBeamAttenuationRate.getInt();
			
			RF_cavity_voltage = readIntegerArrayFromConfig(propertyRFCavityVoltage);
			RF_cavity_efficiency = readDoubleArrayFromConfig(propertyRFCavityEfficiency);
			RF_cavity_heat_generated = readIntegerArrayFromConfig(propertyRFCavityHeatGenerated);
			RF_cavity_base_power = readIntegerArrayFromConfig(propertyRFCavityBasePower);
			
			magnet_strength = readDoubleArrayFromConfig(propertyMagnetStrength);
			magnet_efficiency = readDoubleArrayFromConfig(propertyMagnetEfficiency);
			magnet_heat_generated = readIntegerArrayFromConfig(propertyMagnetHeatGenerated);
			magnet_base_power = readIntegerArrayFromConfig(propertyMagnetBasePower);
			
			cooler_heat_removed = readIntegerArrayFromConfig(propertyCoolerHeatRemoved);
			
			minimium_accelerator_ring_input_particle_energy= propertyAcceleratorRingInputEnergy.getInt();
			
			
			detector_efficiency = readDoubleArrayFromConfig(propertyDetectorEfficiency);
			detector_base_power = readIntegerArrayFromConfig(propertyDetectorBasePower);
			
		}
		
		propertyAcceleratorLinearMinSize.set(accelerator_linear_min_size);
		propertyAcceleratorLinearMaxSize.set(accelerator_linear_max_size);
		propertyAcceleratorRingMinSize.set(accelerator_ring_min_size);
		propertyAcceleratorRingMaxSize.set(accelerator_ring_max_size);
		
		propertyBeamAttenuationRate.set(beamAttenuationRate);
		
		propertyRFCavityVoltage.set(RF_cavity_voltage);
		propertyRFCavityEfficiency.set(RF_cavity_efficiency);
		propertyRFCavityHeatGenerated.set(RF_cavity_heat_generated);
		propertyRFCavityBasePower.set(RF_cavity_base_power);
		
		propertyMagnetStrength.set(magnet_strength);
		propertyMagnetEfficiency.set(magnet_efficiency);
		propertyMagnetHeatGenerated.set(magnet_heat_generated);
		propertyMagnetBasePower.set(magnet_base_power);
		
		propertyCoolerHeatRemoved.set(cooler_heat_removed);

		propertyAcceleratorRingInputEnergy.set(minimium_accelerator_ring_input_particle_energy);
		

		propertyDetectorEfficiency.set(detector_efficiency);
		propertyDetectorBasePower.set(detector_base_power);
		

		if (config.hasChanged()) config.save();
	}
	
	
	
	
	private static boolean[] readBooleanArrayFromConfig(Property property) {
		int currentLength = property.getBooleanList().length;
		int defaultLength = property.getDefaults().length;
		if (currentLength == defaultLength) {
			return property.getBooleanList();
		}
		boolean[] newArray = new boolean[defaultLength];
		if (currentLength > defaultLength) {
			for (int i = 0; i < defaultLength; i++) {
				newArray[i] = property.getBooleanList()[i];
			}
		} else {
			for (int i = 0; i < currentLength; i++) {
				newArray[i] = property.getBooleanList()[i];
			}
			for (int i = currentLength; i < defaultLength; i++) {
				newArray[i] = property.setToDefault().getBooleanList()[i];
			}
		}
		return newArray;
	}
	
	private static int[] readIntegerArrayFromConfig(Property property) {
		int currentLength = property.getIntList().length;
		int defaultLength = property.getDefaults().length;
		if (currentLength == defaultLength) {
			return property.getIntList();
		}
		int[] newArray = new int[defaultLength];
		if (currentLength > defaultLength) {
			for (int i = 0; i < defaultLength; i++) {
				newArray[i] = property.getIntList()[i];
			}
		} else {
			for (int i = 0; i < currentLength; i++) {
				newArray[i] = property.getIntList()[i];
			}
			for (int i = currentLength; i < defaultLength; i++) {
				newArray[i] = property.setToDefault().getIntList()[i];
			}
		}
		return newArray;
	}
	
	private static double[] readDoubleArrayFromConfig(Property property) {
		int currentLength = property.getDoubleList().length;
		int defaultLength = property.getDefaults().length;
		if (currentLength == defaultLength) {
			return property.getDoubleList();
		}
		double[] newArray = new double[defaultLength];
		if (currentLength > defaultLength) {
			for (int i = 0; i < defaultLength; i++) {
				newArray[i] = property.getDoubleList()[i];
			}
		} else {
			for (int i = 0; i < currentLength; i++) {
				newArray[i] = property.getDoubleList()[i];
			}
			for (int i = currentLength; i < defaultLength; i++) {
				newArray[i] = property.setToDefault().getDoubleList()[i];
			}
		}
		return newArray;
	}
	
	
	

}
