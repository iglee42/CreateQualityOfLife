package fr.iglee42.createqualityoflife.statue;

import java.util.function.Consumer;

import fr.iglee42.createqualityoflife.registries.QOLEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;

public class StatueItem extends Item {
    public StatueItem(Item.Properties p_40503_) {
        super(p_40503_);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Direction direction = ctx.getClickedFace();
        if (direction == Direction.DOWN) {
            return InteractionResult.FAIL;
        } else {
            Level level = ctx.getLevel();
            BlockPlaceContext blockplacecontext = new BlockPlaceContext(ctx);
            BlockPos blockpos = blockplacecontext.getClickedPos();
            ItemStack itemstack = ctx.getItemInHand();
            Vec3 vec3 = Vec3.atBottomCenterOf(blockpos);
            AABB aabb = QOLEntityTypes.STATUE.get().getDimensions().makeBoundingBox(vec3.x(), vec3.y(), vec3.z());
            if (level.noCollision(null, aabb) && level.getEntities(null, aabb).isEmpty()) {
                if (level instanceof ServerLevel serverlevel) {
                    Consumer<Statue> consumer = EntityType.createDefaultStackConfig(serverlevel, itemstack, ctx.getPlayer());
                    Statue statue = QOLEntityTypes.STATUE.get().create(serverlevel, itemstack.getTag(), consumer, blockpos, MobSpawnType.SPAWN_EGG, true, true);
                    if (statue == null) {
                        return InteractionResult.FAIL;
                    }
                    if (ctx.getPlayer() != null && !(ctx.getPlayer() instanceof FakePlayer)) statue.setOwner(ctx.getPlayer().getUUID());

                    float f = (float)Mth.floor((Mth.wrapDegrees(ctx.getRotation() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
                    statue.setEntityRotations(0,f,0);
                    serverlevel.addFreshEntityWithPassengers(statue);
                    level.playSound(
                        null, statue.getX(), statue.getY(), statue.getZ(), SoundEvents.ARMOR_STAND_PLACE, SoundSource.BLOCKS, 0.75F, 0.8F
                    );
                    statue.gameEvent(GameEvent.ENTITY_PLACE, ctx.getPlayer());
                }

                itemstack.shrink(1);
                return InteractionResult.sidedSuccess(level.isClientSide);
            } else {
                return InteractionResult.FAIL;
            }
        }
    }
}
