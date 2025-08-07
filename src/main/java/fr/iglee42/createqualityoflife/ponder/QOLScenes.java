package fr.iglee42.createqualityoflife.ponder;

import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.box.PackageStyles;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import com.simibubi.create.content.redstone.link.RedstoneLinkBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import com.simibubi.create.infrastructure.ponder.scenes.highLogistics.PonderHilo;
import fr.iglee42.createqualityoflife.blockentitites.EnderPackagerBlockEntity;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class QOLScenes {

    public static void enderPackager(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("ender_packager", "Transfer packages remotely");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        BlockPos packager1 = util.grid()
                .at(3, 2, 2);
        BlockPos packager2 = util.grid()
                .at(1, 2, 2);
        Selection packager1S = util.select()
                .position(packager1);
        Selection packager2S = util.select()
                .position(packager2);

        BlockPos lever = util.grid()
                .at(1, 3, 2);
        Selection scaff1 = util.select()
                .position(3, 1, 2);
        Selection scaff2 = util.select()
                .position(1, 1, 2);
        scene.idle(5);

        scene.world()
                .showSection(scaff1,Direction.DOWN);
        scene.world()
                .showSection(scaff2,Direction.DOWN);
        scene.idle(10);
        scene.world()
                .showSection(packager1S,Direction.DOWN);
        scene.world()
                .showSection(packager2S,Direction.DOWN);
        scene.idle(20);

        Vec3 packager1Vec = util.vector().blockSurface(packager1, Direction.NORTH)
                .add(0, 0, 0);
        Vec3 frontSlot = packager1Vec.add(0, .25, 0);
        Vec3 backSlot = packager1Vec.add(0, -.1, 0);

        Vec3 packager2Vec = util.vector().blockSurface(packager2, Direction.NORTH)
                .add(0, 0, 0);
        Vec3 frontSlot2 = packager2Vec.add(0, .25, 0);
        Vec3 backSlot2 = packager2Vec.add(0, -.1, 0);

        scene.addKeyframe();
        scene.idle(10);
        scene.overlay().showFilterSlotInput(frontSlot, Direction.SOUTH, 100);
        scene.overlay().showFilterSlotInput(backSlot, Direction.SOUTH, 100);
        scene.idle(10);

        scene.overlay().showText(50)
                .text("Placing items in the two slots can specify a Frequency")
                .placeNearTarget()
                .pointAt(backSlot);
        scene.idle(60);

        ItemStack iron = new ItemStack(Items.IRON_INGOT);
        ItemStack gold = new ItemStack(Items.GOLD_INGOT);
        ItemStack sapling = new ItemStack(Items.OAK_SAPLING);
        scene.overlay().showControls(frontSlot, Pointing.DOWN, 30).withItem(iron);
        scene.idle(7);
        scene.overlay().showControls(backSlot, Pointing.UP, 30).withItem(gold);
        scene.world().modifyBlockEntityNBT(packager1S, EnderPackagerBlockEntity.class,
                nbt -> nbt.put("FrequencyFirst", iron.saveOptional(scene.world().getHolderLookupProvider())));
        scene.idle(7);
        scene.world().modifyBlockEntityNBT(packager1S, EnderPackagerBlockEntity.class,
                nbt -> nbt.put("FrequencyLast", gold.saveOptional(scene.world().getHolderLookupProvider())));
        scene.idle(20);


        scene.overlay().showControls(backSlot2, Pointing.UP, 30).withItem(gold);
        scene.idle(7);
        scene.overlay().showControls(frontSlot2, Pointing.DOWN, 30).withItem(sapling);
        scene.world().modifyBlockEntityNBT(packager2S, EnderPackagerBlockEntity.class,
                nbt -> nbt.put("FrequencyLast", gold.saveOptional(scene.world().getHolderLookupProvider())));
        scene.idle(7);
        scene.world().modifyBlockEntityNBT(packager2S, EnderPackagerBlockEntity.class,
                nbt -> nbt.put("FrequencyFirst", sapling.saveOptional(scene.world().getHolderLookupProvider())));
        scene.idle(20);
        scene.overlay().showText(90)
                .attachKeyFrame()
                .text("Only the packagers with matching Frequencies will communicate")
                .placeNearTarget()
                .pointAt(packager2Vec);

        scene.idle(90);
        scene.overlay().showControls(frontSlot2, Pointing.DOWN, 30).withItem(iron);
        scene.idle(7);
        scene.world().modifyBlockEntityNBT(packager2S, EnderPackagerBlockEntity.class,
                nbt -> nbt.put("FrequencyFirst", iron.saveOptional(scene.world().getHolderLookupProvider())));

        scene.idle(30);
        scene.world()
                .showSection(util.select()
                        .position(lever), Direction.DOWN);

        scene.idle(20);

        scene.world()
                .toggleRedstonePower(util.select()
                        .fromTo(lever, packager2));
        scene.effects()
                .indicateRedstone(lever
                        .below());
        scene.idle(10);

        scene.overlay().showText(50)
                .text("Given redstone power, it will receive packages")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(packager2Vec);
        scene.idle(60);

        ItemStack box = PackageStyles.getDefaultBox()
                .copy();
        PackageItem.addAddress(box, "Warehouse");
        scene.overlay().showControls(packager1S.getCenter(),Pointing.DOWN,20)
                        .rightClick()
                        .withItem(box);
        scene.addKeyframe();
        scene.idle(40);

        scene.world()
                .modifyBlockEntity(packager1, EnderPackagerBlockEntity.class, be -> {
                    be.animationTicks = EnderPackagerBlockEntity.CYCLE;
                    be.animationInward = true;
                    be.heldBox = box;
                });
        scene.idle(10);
        scene.world()
                .modifyBlockEntity(packager1, EnderPackagerBlockEntity.class, be -> {
                    be.heldBox = ItemStack.EMPTY;
                });

        scene.idle(10);

        scene.world()
                .modifyBlockEntity(packager2, EnderPackagerBlockEntity.class, be -> {
                    be.animationTicks = EnderPackagerBlockEntity.CYCLE;
                    be.animationInward = false;
                    be.heldBox = ItemStack.EMPTY;
                });

        scene.world()
                .modifyBlockEntity(packager2, EnderPackagerBlockEntity.class, be -> {
                    be.heldBox = box;
                });

        scene.idle(30);

        scene.overlay().showText(120)
                .text("Inserted packages will be automatically sent if another ender packager can receive it")
                .placeNearTarget()
                .pointAt(packager2S.getCenter());

        scene.idle(120);
    }

    public static void enderPackagerAddresses(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("ender_packager_addresses", "Filter packages transfers with addresses");
        scene.configureBasePlate(0, 0, 7);
        scene.showBasePlate();

        BlockPos packager1 = util.grid()
                .at(5, 2, 2);
        BlockPos packager2 = util.grid()
                .at(3, 2, 2);
        BlockPos packager3 = util.grid()
                .at(1, 2, 2);
        Selection packager1S = util.select()
                .position(packager1);
        Selection packager2S = util.select()
                .position(packager2);
        Selection packager3S = util.select()
                .position(packager3);

        Selection scaff1 = util.select()
                .position(5, 1, 2);
        Selection scaff2 = util.select()
                .position(3, 1, 2);
        Selection scaff3 = util.select()
                .position(1, 1, 2);

        Selection lever1 = util.select()
                .position(3, 2, 3);
        Selection lever2 = util.select()
                .position(1, 2, 3);

        Selection sign1 = util.select()
                .position(3, 2, 1);
        Selection sign2 = util.select()
                .position(1, 2, 1);
        scene.idle(5);

        scene.world()
                .showSection(scaff1,Direction.DOWN);
        scene.world()
                .showSection(scaff2,Direction.DOWN);
        scene.world()
                .showSection(scaff3,Direction.DOWN);
        scene.idle(10);
        scene.world()
                .showSection(packager1S,Direction.DOWN);
        scene.world()
                .showSection(packager2S,Direction.DOWN);
        scene.world()
                .showSection(packager3S,Direction.DOWN);

        scene.idle(10);

        scene.world()
                .showSection(lever1,Direction.NORTH);
        scene.world()
                .showSection(lever2,Direction.NORTH);

        scene.effects().indicateRedstone(packager2);
        scene.effects().indicateRedstone(packager3);


        scene.idle(20);

        Vec3 packager1Vec = util.vector().blockSurface(packager1, Direction.UP)
                .add(0, 0, 0);

        Vec3 packager2Vec = util.vector().blockSurface(packager2, Direction.UP)
                .add(0, 0, 0);

        Vec3 packager3Vec = util.vector().blockSurface(packager3, Direction.UP)
                .add(0, 0, 0);

        ItemStack iron = new ItemStack(Items.IRON_INGOT);
        ItemStack gold = new ItemStack(Items.GOLD_INGOT);
        scene.overlay().showControls(packager1Vec.add(0,1.25,0), Pointing.DOWN, 30).withItem(iron);
        scene.overlay().showControls(packager1Vec, Pointing.DOWN, 30).withItem(gold);
        scene.world().modifyBlockEntityNBT(packager1S, EnderPackagerBlockEntity.class,
                nbt -> nbt.put("FrequencyFirst", iron.saveOptional(scene.world().getHolderLookupProvider())));
        scene.world().modifyBlockEntityNBT(packager1S, EnderPackagerBlockEntity.class,
                nbt -> nbt.put("FrequencyLast", gold.saveOptional(scene.world().getHolderLookupProvider())));

        scene.overlay().showControls(packager2Vec.add(0,1.25,0), Pointing.DOWN, 30).withItem(iron);
        scene.overlay().showControls(packager2Vec, Pointing.DOWN, 30).withItem(gold);
        scene.world().modifyBlockEntityNBT(packager2S, EnderPackagerBlockEntity.class,
                nbt -> nbt.put("FrequencyFirst", iron.saveOptional(scene.world().getHolderLookupProvider())));
        scene.world().modifyBlockEntityNBT(packager2S, EnderPackagerBlockEntity.class,
                nbt -> nbt.put("FrequencyLast", gold.saveOptional(scene.world().getHolderLookupProvider())));

        scene.overlay().showControls(packager3Vec.add(0,1.25,0), Pointing.DOWN, 30).withItem(iron);
        scene.overlay().showControls(packager3Vec, Pointing.DOWN, 30).withItem(gold);
        scene.world().modifyBlockEntityNBT(packager3S, EnderPackagerBlockEntity.class,
                nbt -> nbt.put("FrequencyFirst", iron.saveOptional(scene.world().getHolderLookupProvider())));
        scene.world().modifyBlockEntityNBT(packager3S, EnderPackagerBlockEntity.class,
                nbt -> nbt.put("FrequencyLast", gold.saveOptional(scene.world().getHolderLookupProvider())));
        scene.idle(40);

        scene.world().showIndependentSection(sign1,Direction.SOUTH);
        scene.idle(7);
        scene.world().showIndependentSection(sign2,Direction.SOUTH);
        scene.addKeyframe();
        scene.idle(15);

        scene.overlay()
                .showText(40)
                .text("Warehouse")
                .colored(PonderPalette.OUTPUT)
                .placeNearTarget()
                .pointAt(util.vector()
                        .blockSurface(util.grid()
                                .at(3, 3, 1), Direction.NORTH)
                        .add(-0.5, 0, 0));

        scene.overlay()
                .showText(40)
                .text("Factory")
                .colored(PonderPalette.OUTPUT)
                .placeNearTarget()
                .pointAt(util.vector()
                        .blockSurface(util.grid()
                                .at(1, 3, 1), Direction.NORTH)
                        .add(-0.5, 0, 0));
        scene.idle(50);

        scene.overlay()
                .showText(60)
                .text("When a sign is placed on an ender packager...")
                .placeNearTarget()
                .pointAt(util.vector()
                        .blockSurface(util.grid()
                                .at(3, 3, 1), Direction.NORTH)
                        .add(-0.5, 0, 0));
        scene.idle(70);

        scene.overlay()
                .showText(60)
                .text("The text will be used as a filter for packages (like filters in Frogports)")
                .placeNearTarget()
                .pointAt(util.vector()
                        .blockSurface(util.grid()
                                .at(1, 3, 1), Direction.NORTH)
                        .add(-0.5, 0, 0));

        scene.idle(70);

        ItemStack box = PackageStyles.getDefaultBox()
                .copy();
        PackageItem.addAddress(box, "Warehouse");
        scene.addKeyframe();
        scene.world()
                .modifyBlockEntity(packager1, EnderPackagerBlockEntity.class, be -> {
                    be.heldBox = box;
                });
        scene.overlay()
                .showText(40)
                .text("\u2192 Warehouse")
                .colored(PonderPalette.OUTPUT)
                .placeNearTarget()
                .pointAt(util.vector()
                        .blockSurface(util.grid()
                                .at(5, 2, 2), Direction.NORTH));
        scene.idle(60);

        scene.world()
                .modifyBlockEntity(packager1, EnderPackagerBlockEntity.class, be -> {
                    be.animationTicks = EnderPackagerBlockEntity.CYCLE;
                    be.animationInward = true;
                });
        scene.idle(10);
        scene.world()
                .modifyBlockEntity(packager1, EnderPackagerBlockEntity.class, be -> {
                    be.heldBox = ItemStack.EMPTY;
                });

        scene.idle(10);

        scene.world()
                .modifyBlockEntity(packager2, EnderPackagerBlockEntity.class, be -> {
                    be.animationTicks = EnderPackagerBlockEntity.CYCLE;
                    be.animationInward = false;
                    be.heldBox = ItemStack.EMPTY;
                });

        scene.world()
                .modifyBlockEntity(packager2, EnderPackagerBlockEntity.class, be -> {
                    be.heldBox = box;
                });

        scene.idle(30);

        scene.world()
                .modifyBlockEntity(packager1, EnderPackagerBlockEntity.class, be -> {
                    be.heldBox = box;
                });

        scene.idle(10);

        scene.overlay().showText(90)
                .text("If the ender packager which should receive the package is full...")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(packager2S.getCenter());
        scene.idle(100);

        scene.overlay().showText(70)
                .text("The sender will wait until the receiver is empty")
                .placeNearTarget()
                .pointAt(packager1S.getCenter());
        scene.idle(70);

        scene.overlay().showControls(packager2Vec,Pointing.DOWN,40)
                        .rightClick();
        scene.idle(30);
        scene.world()
                .modifyBlockEntity(packager2, EnderPackagerBlockEntity.class, be -> {
                    be.heldBox = ItemStack.EMPTY;
                });
        scene.idle(20);


        scene.world()
                .modifyBlockEntity(packager1, EnderPackagerBlockEntity.class, be -> {
                    be.animationTicks = EnderPackagerBlockEntity.CYCLE;
                    be.animationInward = true;
                });
        scene.idle(10);
        scene.world()
                .modifyBlockEntity(packager1, EnderPackagerBlockEntity.class, be -> {
                    be.heldBox = ItemStack.EMPTY;
                });

        scene.idle(10);

        scene.world()
                .modifyBlockEntity(packager2, EnderPackagerBlockEntity.class, be -> {
                    be.animationTicks = EnderPackagerBlockEntity.CYCLE;
                    be.animationInward = false;
                    be.heldBox = ItemStack.EMPTY;
                });

        scene.world()
                .modifyBlockEntity(packager2, EnderPackagerBlockEntity.class, be -> {
                    be.heldBox = box;
                });


    }
}
