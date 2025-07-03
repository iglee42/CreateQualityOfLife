package fr.iglee42.createqualityoflife.registries;

import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.statue.Statue;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class QOLEntityTypes {

	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, CreateQOL.MODID);


	public static final DeferredHolder<EntityType<?>,EntityType<Statue>> STATUE = ENTITIES.register("statue",()->EntityType.Builder.<Statue>of(Statue::new, MobCategory.MISC).sized(0.5F, 1.975F).eyeHeight(1.7775F).clientTrackingRange(10).build("statue"));

	public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
		event.put(STATUE.get(), Statue.createAttributes()
			.build());
	}

}
