package fr.iglee42.createqualityoflife.blockentitites.renderers;

import com.jozufozu.flywheel.core.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import fr.iglee42.createqualityoflife.blockentitites.InventoryLinkerBlockEntity;
import fr.iglee42.createqualityoflife.registries.ModPartialModels;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class InventoryLinkerRenderer extends SafeBlockEntityRenderer<InventoryLinkerBlockEntity> {
    public InventoryLinkerRenderer(BlockEntityRendererProvider.Context context) {
        super();
    }

    @Override
    protected void renderSafe(InventoryLinkerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        PartialModel model = be.getPlayerPaperItemStack().isEmpty() ? ModPartialModels.INVENTORY_LINKER : ModPartialModels.INVENTORY_LINKER_ON;
        SuperByteBuffer buffer = CachedBufferer.partial(model, be.getBlockState());
        VertexConsumer vb = bufferSource.getBuffer(RenderType.solid());
        KineticBlockEntityRenderer.renderRotatingBuffer(be,buffer,ms,vb,light);
    }




}
