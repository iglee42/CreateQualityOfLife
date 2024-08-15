package fr.iglee42.createqualityoflife.registries;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import com.simibubi.create.foundation.block.connected.CTType;
import com.simibubi.create.foundation.block.render.SpriteShiftEntry;
import com.simibubi.create.foundation.block.render.SpriteShifter;
import com.simibubi.create.foundation.utility.Couple;
import fr.iglee42.createqualityoflife.CreateQOL;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class ModSprites {

        public static final SpriteShiftEntry SHADOW_CHEST =
                get("block/shadow_radiance_chestplate", "block/shadow_radiance_chestplate");


        static {
            populateMaps();
        }

        private static void populateMaps() {
        }



        private static CTSpriteShiftEntry omni(String name) {
            return getCT(AllCTTypes.OMNIDIRECTIONAL, name);
        }

        private static CTSpriteShiftEntry horizontal(String name) {
            return getCT(AllCTTypes.HORIZONTAL, name);
        }

        private static CTSpriteShiftEntry vertical(String name) {
            return getCT(AllCTTypes.VERTICAL, name);
        }


        private static SpriteShiftEntry get(String originalLocation, String targetLocation) {
            return SpriteShifter.get(CreateQOL.asResource(originalLocation), CreateQOL.asResource(targetLocation));
        }

        private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName, String connectedTextureName) {
            return CTSpriteShifter.getCT(type, CreateQOL.asResource("block/" + blockTextureName),
                    CreateQOL.asResource("block/" + connectedTextureName + "_connected"));
        }

        private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName) {
            return getCT(type, blockTextureName, blockTextureName);
        }



}
