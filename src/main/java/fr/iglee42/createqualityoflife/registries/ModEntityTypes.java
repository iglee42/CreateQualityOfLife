package fr.iglee42.createqualityoflife.registries;

import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.statue.Statue;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntityTypes {

	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, CreateQOL.MODID);


	public static final RegistryObject<EntityType<Statue>> STATUE = ENTITIES.register("statue",()->EntityType.Builder.<Statue>of(Statue::new, MobCategory.MISC).sized(0.5F, 1.975F).clientTrackingRange(10).build("statue"));

	public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
		event.put(STATUE.get(), Statue.createLivingAttributes()
			.build());
	}

}
