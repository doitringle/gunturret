package nowhed.ringlesgunturret;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import nowhed.ringlesgunturret.util.ModRecipeGenerator;
import nowhed.ringlesgunturret.util.ModTagGenerator;

public class RinglesGunTurretDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(ModTagGenerator::new);
		pack.addProvider(ModRecipeGenerator::new);
	}
}
