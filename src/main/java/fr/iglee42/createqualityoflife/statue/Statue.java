package fr.iglee42.createqualityoflife.statue;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Dynamic;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.wrench.WrenchItem;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueHandler;
import fr.iglee42.createqualityoflife.registries.ModEntityDataSerializers;
import fr.iglee42.createqualityoflife.registries.ModEntityTypes;
import fr.iglee42.createqualityoflife.registries.ModItems;
import fr.iglee42.createqualityoflife.statue.animation.StatueAnimation;
import fr.iglee42.createqualityoflife.statue.animation.StatueAnimationFrame;
import fr.iglee42.createqualityoflife.statue.animation.StatuePartTable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Rotations;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class Statue extends LivingEntity {
    public static final float DEFAULT_SCALE = 1;
    public static final Rotations DEFAULT_ENTITY_ROTATIONS = new Rotations(0.0F, 0.0F, 0.0F);
    private static final Rotations DEFAULT_HEAD_POSE = new Rotations(0.0F, 0.0F, 0.0F);
    private static final Rotations DEFAULT_BODY_POSE = new Rotations(0.0F, 0.0F, 0.0F);
    private static final Rotations DEFAULT_LEFT_ARM_POSE = new Rotations(-10.0F, 0.0F, -10.0F);
    private static final Rotations DEFAULT_RIGHT_ARM_POSE = new Rotations(-15.0F, 0.0F, 10.0F);
    private static final Rotations DEFAULT_LEFT_LEG_POSE = new Rotations(-1.0F, 0.0F, -1.0F);
    private static final Rotations DEFAULT_RIGHT_LEG_POSE = new Rotations(1.0F, 0.0F, 1.0F);
    private static final EntityDimensions MARKER_DIMENSIONS = EntityDimensions.fixed(0.0F, 0.0F);
    private static final EntityDimensions BABY_DIMENSIONS = EntityType.ARMOR_STAND.getDimensions().scale(0.5F);
    public static final EntityDataAccessor<Boolean> DATA_INVULNERABLE = SynchedEntityData.defineId(Statue.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Byte> DATA_CLIENT_FLAGS = SynchedEntityData.defineId(Statue.class, EntityDataSerializers.BYTE);
    public static final EntityDataAccessor<Rotations> DATA_HEAD_POSE = SynchedEntityData.defineId(Statue.class, EntityDataSerializers.ROTATIONS);
    public static final EntityDataAccessor<Rotations> DATA_BODY_POSE = SynchedEntityData.defineId(Statue.class, EntityDataSerializers.ROTATIONS);
    public static final EntityDataAccessor<Rotations> DATA_LEFT_ARM_POSE = SynchedEntityData.defineId(Statue.class, EntityDataSerializers.ROTATIONS);
    public static final EntityDataAccessor<Rotations> DATA_RIGHT_ARM_POSE = SynchedEntityData.defineId(Statue.class, EntityDataSerializers.ROTATIONS);
    public static final EntityDataAccessor<Rotations> DATA_LEFT_LEG_POSE = SynchedEntityData.defineId(Statue.class, EntityDataSerializers.ROTATIONS);
    public static final EntityDataAccessor<Rotations> DATA_RIGHT_LEG_POSE = SynchedEntityData.defineId(Statue.class, EntityDataSerializers.ROTATIONS);
    public static final EntityDataAccessor<Rotations> DATA_GLOBAL_ROTATIONS = SynchedEntityData.defineId(Statue.class, EntityDataSerializers.ROTATIONS);
    public static final EntityDataAccessor<Byte> DATA_PLAYER_SKIN_CUSTOMISATION = SynchedEntityData.defineId(Statue.class, EntityDataSerializers.BYTE);
    public static final EntityDataAccessor<Optional<UUID>> DATA_OWNER = SynchedEntityData.defineId(Statue.class, EntityDataSerializers.OPTIONAL_UUID);
    public static final EntityDataAccessor<Optional<GameProfile>> DATA_PROFILE = SynchedEntityData.defineId(Statue.class, ModEntityDataSerializers.PROFILE_ENTITY_DATA_SERIALIZER);
    public static final EntityDataAccessor<Float> DATA_SCALE = SynchedEntityData.defineId(Statue.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Optional<StatueAnimation>> DATA_ANIMATION = SynchedEntityData.defineId(Statue.class, ModEntityDataSerializers.ANIMATION_DATA_SERIALIZER);
    public static final EntityDataAccessor<Integer> DATA_ANIMATION_PROGRESS = SynchedEntityData.defineId(Statue.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> DATA_ANIMATION_REVERSING = SynchedEntityData.defineId(Statue.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> DATA_ANIMATION_PLAYING = SynchedEntityData.defineId(Statue.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> DATA_SKIN = SynchedEntityData.defineId(Statue.class, EntityDataSerializers.INT);
    private static final Predicate<Entity> RIDABLE_MINECARTS = p_31582_ -> p_31582_ instanceof AbstractMinecart
            && ((AbstractMinecart)p_31582_).canBeRidden();
    private final NonNullList<ItemStack> handItems = NonNullList.withSize(2, ItemStack.EMPTY);
    private final NonNullList<ItemStack> armorItems = NonNullList.withSize(4, ItemStack.EMPTY);
    private boolean invisible;
    public long lastHit;
    private Rotations headPose = DEFAULT_HEAD_POSE;
    private Rotations bodyPose = DEFAULT_BODY_POSE;
    private Rotations leftArmPose = DEFAULT_LEFT_ARM_POSE;
    private Rotations rightArmPose = DEFAULT_RIGHT_ARM_POSE;
    private Rotations leftLegPose = DEFAULT_LEFT_LEG_POSE;
    private Rotations rightLegPose = DEFAULT_RIGHT_LEG_POSE;
    public float entityScaleO = DEFAULT_SCALE;
    public Rotations entityRotationsO = DEFAULT_ENTITY_ROTATIONS;

    public Statue(EntityType<? extends LivingEntity> p_31553_, Level p_31554_) {
        super(p_31553_, p_31554_);
    }

    public Statue(Level p_31556_, double p_31557_, double p_31558_, double p_31559_) {
        this(ModEntityTypes.STATUE.get(), p_31556_);
        this.setPos(p_31557_, p_31558_, p_31559_);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        this.entityScaleO = this.getEntityScale();
        this.entityRotationsO = this.getEntityRotations();
    }

    @Override
    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    private boolean hasPhysics() {
        return !this.isMarker() && !this.isNoGravity();
    }

    @Override
    public boolean isEffectiveAi() {
        return super.isEffectiveAi() && this.hasPhysics();
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_INVULNERABLE, false);
        this.entityData.define(DATA_CLIENT_FLAGS, (byte)0);
        this.entityData.define(DATA_HEAD_POSE, DEFAULT_HEAD_POSE);
        this.entityData.define(DATA_BODY_POSE, DEFAULT_BODY_POSE);
        this.entityData.define(DATA_LEFT_ARM_POSE, DEFAULT_LEFT_ARM_POSE);
        this.entityData.define(DATA_RIGHT_ARM_POSE, DEFAULT_RIGHT_ARM_POSE);
        this.entityData.define(DATA_LEFT_LEG_POSE, DEFAULT_LEFT_LEG_POSE);
        this.entityData.define(DATA_RIGHT_LEG_POSE, DEFAULT_RIGHT_LEG_POSE);
        this.entityData.define(DATA_GLOBAL_ROTATIONS, DEFAULT_ENTITY_ROTATIONS);
        this.entityData.define(DATA_PLAYER_SKIN_CUSTOMISATION, getAllModelParts());
        this.entityData.define(DATA_PROFILE, Optional.empty());
        this.entityData.define(DATA_OWNER, Optional.empty());
        this.entityData.define(DATA_SCALE, DEFAULT_SCALE);
        this.entityData.define(DATA_ANIMATION, Optional.empty());
        this.entityData.define(DATA_ANIMATION_PROGRESS, 0);
        this.entityData.define(DATA_ANIMATION_REVERSING, false);
        this.entityData.define(DATA_ANIMATION_PLAYING, false);
        this.entityData.define(DATA_SKIN, 0);
    }

    private static byte getAllModelParts() {
        byte value = 0;
        for (PlayerModelPart modelPart : PlayerModelPart.values()) {
            value |= modelPart.getMask();
        }
        return value;
    }

    @Override
    public Iterable<ItemStack> getHandSlots() {
        return this.handItems;
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return this.armorItems;
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        switch (slot.getType()) {
            case HAND:
                return this.handItems.get(slot.getIndex());
            case ARMOR:
                return this.armorItems.get(slot.getIndex());
            default:
                return ItemStack.EMPTY;
        }
    }

    @Override
    public void setItemSlot(EquipmentSlot p_31584_, ItemStack p_31585_) {
        this.verifyEquippedItem(p_31585_);
        switch (p_31584_.getType()) {
            case HAND:
                this.onEquipItem(p_31584_, this.handItems.set(p_31584_.getIndex(), p_31585_), p_31585_);
                break;
            case ARMOR:
                this.onEquipItem(p_31584_, this.armorItems.set(p_31584_.getIndex(), p_31585_), p_31585_);
        }
    }

    @Override
    public boolean canTakeItem(ItemStack p_31638_) {
        EquipmentSlot equipmentslot = this.getEquipmentSlotForItem(p_31638_);
        return this.getItemBySlot(equipmentslot).isEmpty();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        ListTag listtag = new ListTag();

        for (ItemStack itemstack : this.armorItems) {
            listtag.add(itemstack.save(new CompoundTag()));
        }

        nbt.put("ArmorItems", listtag);
        ListTag listtag1 = new ListTag();

        for (ItemStack itemstack1 : this.handItems) {
            listtag1.add(itemstack1.save(new CompoundTag()));
        }

        nbt.put("HandItems", listtag1);
        nbt.putBoolean("Invisible", this.isInvisible());
        nbt.putBoolean("Small", this.isSmall());
        nbt.putBoolean("SlimArms", this.isSlimArms());
        nbt.putBoolean("ShowArms", this.isShowArms());
        nbt.putFloat("Scale",getEntityScale());
        if (this.isMarker()) {
            nbt.putBoolean("Marker", this.isMarker());
        }

        getOwner().ifPresent(owner -> nbt.putUUID("Owner",owner));
        nbt.put("Pose", this.writePose());
        nbt.putByte("SkinParts", this.entityData.get(DATA_PLAYER_SKIN_CUSTOMISATION));
        this.entityData.get(DATA_PROFILE).ifPresent(profile -> {
            nbt.put("Profile", NbtUtils.writeGameProfile(new CompoundTag(),profile));
        });

        nbt.put("Rotations", getEntityRotations().save());
        nbt.putBoolean("Invulnerable",isInvulnerable());
        this.entityData.get(DATA_ANIMATION).ifPresent(animation -> {
            nbt.put("Animation", StatueAnimation.CODEC.encodeStart(NbtOps.INSTANCE, animation).getOrThrow(false,s->{}));
        });
        nbt.putInt("AnimationProgress",getAnimationProgress());
        nbt.putBoolean("AnimationReversing",isAnimationReversing());
        nbt.putBoolean("AnimationPlaying",isAnimationPlaying());
        nbt.putInt("Skin",getSkin());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("ArmorItems", 9)) {
            ListTag listtag = nbt.getList("ArmorItems", 10);

            for (int i = 0; i < this.armorItems.size(); i++) {
                CompoundTag compoundtag = listtag.getCompound(i);
                this.armorItems.set(i, ItemStack.of(compoundtag));
            }
        }

        if (nbt.contains("HandItems", 9)) {
            ListTag listtag1 = nbt.getList("HandItems", 10);

            for (int j = 0; j < this.handItems.size(); j++) {
                CompoundTag compoundtag2 = listtag1.getCompound(j);
                this.handItems.set(j, ItemStack.of(compoundtag2));
            }
        }

        this.setInvisible(nbt.getBoolean("Invisible"));
        this.setSmall(nbt.getBoolean("Small"));
        this.setShowArms(nbt.getBoolean("ShowArms"));
        this.setSlimArms(nbt.getBoolean("SlimArms"));
        this.setMarker(nbt.getBoolean("Marker"));
        this.setEntityScale(nbt.getFloat("Scale"));
        Optional<UUID> owner = Optional.empty();
        if (nbt.contains("Owner")) owner = Optional.of(nbt.getUUID("Owner"));
        setOwner(owner.orElse(null));
        this.noPhysics = !this.hasPhysics();
        CompoundTag compoundtag1 = nbt.getCompound("Pose");
        this.readPose(compoundtag1);
        this.entityData.set(DATA_PLAYER_SKIN_CUSTOMISATION,nbt.getByte("SkinParts"));

        Optional<GameProfile> optional = Optional.empty();
        if (nbt.contains("Profile", Tag.TAG_COMPOUND)) {
            optional = Optional.ofNullable(NbtUtils.readGameProfile(nbt.getCompound("Profile")));
        }
        setProfile(optional.orElse(null));

        if (nbt.contains("Rotations", Tag.TAG_LIST)) {
            Rotations entityRotations = new Rotations(nbt.getList("Rotations", Tag.TAG_FLOAT));
            this.setEntityRotations(entityRotations.getX(),entityRotations.getY(), entityRotations.getZ());
            this.entityRotationsO = this.getEntityRotations();
        }
        if (nbt.contains("Invulnerable"))setInvulnerable(nbt.getBoolean("Invulnerable"));
        Optional<Dynamic<?>> animationOptional = Optional.empty();
        if (nbt.contains("Animation", Tag.TAG_COMPOUND)) {
            animationOptional = Optional.of(new Dynamic<>(NbtOps.INSTANCE, nbt.get("Animation")));
        }
        if (animationOptional.isEmpty()) setAnimation(null);
        else animationOptional.map(StatueAnimation.CODEC::parse).flatMap(dataResult->dataResult.resultOrPartial(System.out::println)).ifPresent(this::setAnimation);
        setAnimationProgress(nbt.getInt("AnimationProgress"));
        setAnimationReversing(nbt.getBoolean("AnimationReversing"));
        setAnimationPlaying(nbt.getBoolean("AnimationPlaying"));
        setSkin(nbt.getInt("Skin"));
    }

    public void setSkin(int skin) {
        this.entityData.set(DATA_SKIN,skin);
    }

    public int getSkin(){
        return this.entityData.get(DATA_SKIN);
    }

    public void setAnimation(StatueAnimation statueAnimation) {
        this.entityData.set(DATA_ANIMATION,Optional.ofNullable(statueAnimation));
    }

    public Optional<StatueAnimation> getAnimation(){
        return this.entityData.get(DATA_ANIMATION);
    }

    public int getAnimationProgress(){
        return this.entityData.get(DATA_ANIMATION_PROGRESS);
    }

    public void setAnimationProgress(int progress){
        this.entityData.set(DATA_ANIMATION_PROGRESS,progress);
    }

    public boolean isAnimationReversing(){
        return this.entityData.get(DATA_ANIMATION_REVERSING);
    }

    public void setAnimationReversing(boolean reversing){
        this.entityData.set(DATA_ANIMATION_REVERSING,reversing);
    }

    public boolean isAnimationPlaying(){
        return this.entityData.get(DATA_ANIMATION_PLAYING);
    }

    public void setAnimationPlaying(boolean playing){
        this.entityData.set(DATA_ANIMATION_PLAYING,playing);
    }

    private void readPose(CompoundTag p_31658_) {
        ListTag listtag = p_31658_.getList("Head", 5);
        this.setHeadPose(listtag.isEmpty() ? DEFAULT_HEAD_POSE : new Rotations(listtag));
        ListTag listtag1 = p_31658_.getList("Body", 5);
        this.setBodyPose(listtag1.isEmpty() ? DEFAULT_BODY_POSE : new Rotations(listtag1));
        ListTag listtag2 = p_31658_.getList("LeftArm", 5);
        this.setLeftArmPose(listtag2.isEmpty() ? DEFAULT_LEFT_ARM_POSE : new Rotations(listtag2));
        ListTag listtag3 = p_31658_.getList("RightArm", 5);
        this.setRightArmPose(listtag3.isEmpty() ? DEFAULT_RIGHT_ARM_POSE : new Rotations(listtag3));
        ListTag listtag4 = p_31658_.getList("LeftLeg", 5);
        this.setLeftLegPose(listtag4.isEmpty() ? DEFAULT_LEFT_LEG_POSE : new Rotations(listtag4));
        ListTag listtag5 = p_31658_.getList("RightLeg", 5);
        this.setRightLegPose(listtag5.isEmpty() ? DEFAULT_RIGHT_LEG_POSE : new Rotations(listtag5));
    }

    private CompoundTag writePose() {
        CompoundTag compoundtag = new CompoundTag();
        if (!DEFAULT_HEAD_POSE.equals(this.headPose)) {
            compoundtag.put("Head", this.headPose.save());
        }

        if (!DEFAULT_BODY_POSE.equals(this.bodyPose)) {
            compoundtag.put("Body", this.bodyPose.save());
        }

        if (!DEFAULT_LEFT_ARM_POSE.equals(this.leftArmPose)) {
            compoundtag.put("LeftArm", this.leftArmPose.save());
        }

        if (!DEFAULT_RIGHT_ARM_POSE.equals(this.rightArmPose)) {
            compoundtag.put("RightArm", this.rightArmPose.save());
        }

        if (!DEFAULT_LEFT_LEG_POSE.equals(this.leftLegPose)) {
            compoundtag.put("LeftLeg", this.leftLegPose.save());
        }

        if (!DEFAULT_RIGHT_LEG_POSE.equals(this.rightLegPose)) {
            compoundtag.put("RightLeg", this.rightLegPose.save());
        }

        return compoundtag;
    }

    public float getEntityScale(){
        return entityData.get(DATA_SCALE);
    }

    public void setEntityScale(float scale){
        entityData.set(DATA_SCALE,scale);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity p_31564_) {}

    @Override
    protected void pushEntities() {
        for (Entity entity : this.level().getEntities(this, this.getBoundingBox(), RIDABLE_MINECARTS)) {
            if (this.distanceToSqr(entity) <= 0.2) {
                entity.push(this);
            }
        }
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 clickedPos, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (this.isMarker() || itemstack.is(Items.NAME_TAG)) {
            return InteractionResult.PASS;
        } else if (player.isSpectator()) {
            return InteractionResult.SUCCESS;
        } else if (player.level().isClientSide) {
            return InteractionResult.CONSUME;
        } else if (isInvulnerable() && hasOwner() && !player.getUUID().equals(getOwner().get())) {
            return InteractionResult.FAIL;
        } else {
            if (player.getItemInHand(hand).is(AllItems.WRENCH.asItem()) && getAnimation().isPresent()) {
                if (player.isCrouching()) {
                    setAnimationProgress(0);
                    setAnimationPlaying(true);
                } else {
                    setAnimationPlaying(!isAnimationPlaying());
                }
                ScrollValueHandler.wrenchCog.bump(30);
            } else {
                if (player.isCrouching()) {
                    NetworkHooks.openScreen((ServerPlayer) player, new MenuProvider() {
                        @Override
                        public @NotNull Component getDisplayName() {
                            return Statue.this.getDisplayName();
                        }

                        @Override
                        public @org.jetbrains.annotations.Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                            return new StatueMenu(i, player.getInventory(), Statue.this);
                        }
                    }, buf -> buf.writeInt(getId()));
                    return InteractionResult.SUCCESS;
                } else {
                    EquipmentSlot equipmentslot = this.getEquipmentSlotForItem(itemstack);
                    if (itemstack.isEmpty()) {
                        EquipmentSlot slot = this.getClickedSlot(clickedPos);
                        if (this.swapItem(slot, ItemStack.EMPTY)) {
                            return InteractionResult.SUCCESS;
                        }
                    } else {
                        if (this.swapItem(equipmentslot, itemstack)) {
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
            }
            return InteractionResult.PASS;
        }
    }

    private boolean swapItem(EquipmentSlot slot, ItemStack stack) {
        ItemStack stackInSlot = this.getItemBySlot(slot);
        if (!stackInSlot.isEmpty()){
            setItemSlot(slot,stack);
            return true;
        } else if (!stack.isEmpty()){
            this.setItemSlot(slot, stack.copyWithCount(1));
            return true;
        }
        return false;
    }

    @Override
    public void setInvulnerable(boolean invulnerable) {
        super.setInvulnerable(invulnerable);
        entityData.set(DATA_INVULNERABLE,invulnerable);
    }

    @Override
    public boolean isInvulnerable() {
        return entityData.get(DATA_INVULNERABLE);
    }

    private EquipmentSlot getClickedSlot(Vec3 p_31660_) {
        EquipmentSlot equipmentslot = EquipmentSlot.MAINHAND;
        boolean flag = this.isSmall();
        double d0 = p_31660_.y / (double)(this.getScale() * (isSmall() ? 0.5 : 1));
        EquipmentSlot equipmentslot1 = EquipmentSlot.FEET;
        if (d0 >= 0.1 && d0 < 0.1 + (flag ? 0.8 : 0.45) && this.hasItemInSlot(equipmentslot1)) {
            equipmentslot = EquipmentSlot.FEET;
        } else if (d0 >= 0.9 + (flag ? 0.3 : 0.0) && d0 < 0.9 + (flag ? 1.0 : 0.7) && this.hasItemInSlot(EquipmentSlot.CHEST)) {
            equipmentslot = EquipmentSlot.CHEST;
        } else if (d0 >= 0.4 && d0 < 0.4 + (flag ? 1.0 : 0.8) && this.hasItemInSlot(EquipmentSlot.LEGS)) {
            equipmentslot = EquipmentSlot.LEGS;
        } else if (d0 >= 1.6 && this.hasItemInSlot(EquipmentSlot.HEAD)) {
            equipmentslot = EquipmentSlot.HEAD;
        } else if (!this.hasItemInSlot(EquipmentSlot.MAINHAND) && this.hasItemInSlot(EquipmentSlot.OFFHAND)) {
            equipmentslot = EquipmentSlot.OFFHAND;
        }

        return equipmentslot;
    }



    public boolean hurt(DamageSource p_31579_, float p_31580_) {
        if (!this.level().isClientSide && !this.isRemoved()) {
            if (p_31579_.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                this.kill();
                return false;
            } else if (!this.isInvulnerableTo(p_31579_) && !this.invisible && !this.isMarker()) {
                if (p_31579_.is(DamageTypeTags.IS_EXPLOSION)) {
                    this.brokenByAnything(p_31579_);
                    this.kill();
                    return false;
                } else if (p_31579_.is(DamageTypeTags.IGNITES_ARMOR_STANDS)) {
                    if (this.isOnFire()) {
                        this.causeDamage(p_31579_, 0.15F);
                    } else {
                        this.setSecondsOnFire(5);
                    }

                    return false;
                } else if (p_31579_.is(DamageTypeTags.BURNS_ARMOR_STANDS) && this.getHealth() > 0.5F) {
                    this.causeDamage(p_31579_, 4.0F);
                    return false;
                } else {
                    boolean flag = p_31579_.getDirectEntity() instanceof AbstractArrow;
                    boolean flag1 = flag && ((AbstractArrow)p_31579_.getDirectEntity()).getPierceLevel() > 0;
                    boolean flag2 = "player".equals(p_31579_.getMsgId());
                    if (!flag2 && !flag) {
                        return false;
                    } else {
                        Entity entity = p_31579_.getEntity();
                        if (entity instanceof Player) {
                            Player player = (Player)entity;
                            if (!player.getAbilities().mayBuild) {
                                return false;
                            }
                        }
                        if (p_31579_.getEntity() instanceof Player player){
                            if (player.getMainHandItem().is(AllItems.WRENCH.asItem())){
                                this.playBrokenSound();
                                if (!p_31579_.isCreativePlayer()){
                                    this.brokenByPlayer( p_31579_);
                                }
                                this.showBreakingParticles();
                                this.kill();
                                return true;
                            }
                        }

                        if (p_31579_.isCreativePlayer()) {
                            this.playBrokenSound();
                            this.showBreakingParticles();
                            this.kill();
                            return flag1;
                        } else {
                            long i = this.level().getGameTime();
                            if (i - this.lastHit > 5L && !flag) {
                                this.level().broadcastEntityEvent(this, (byte)32);
                                this.gameEvent(GameEvent.ENTITY_DAMAGE, p_31579_.getEntity());
                                this.lastHit = i;
                            } else {
                                this.brokenByPlayer(p_31579_);
                                this.showBreakingParticles();
                                this.kill();
                            }

                            return true;
                        }
                    }
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void handleEntityEvent(byte p_31568_) {
        if (p_31568_ == 32) {
            if (this.level().isClientSide) {
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ARMOR_STAND_HIT, this.getSoundSource(), 0.3F, 1.0F, false);
                this.lastHit = this.level().getGameTime();
            }
        } else {
            super.handleEntityEvent(p_31568_);
        }
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double p_31574_) {
        double d0 = this.getBoundingBox().getSize() * 4.0;
        if (Double.isNaN(d0) || d0 == 0.0) {
            d0 = 4.0;
        }

        d0 *= 64.0;
        return p_31574_ < d0 * d0;
    }

    private void showBreakingParticles() {
        if (this.level() instanceof ServerLevel) {
            ((ServerLevel)this.level())
                    .sendParticles(
                            new BlockParticleOption(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.defaultBlockState()),
                            this.getX(),
                            this.getY(0.6666666666666666),
                            this.getZ(),
                            10,
                            (double)(this.getBbWidth() / 4.0F),
                            (double)(this.getBbHeight() / 4.0F),
                            (double)(this.getBbWidth() / 4.0F),
                            0.05
                    );
        }
    }

    private void causeDamage(DamageSource p_31649_, float p_31650_) {
        float f = this.getHealth();
        f -= p_31650_;
        if (f <= 0.5F) {
            this.brokenByAnything(p_31649_);
            this.kill();
        } else {
            this.setHealth(f);
            this.gameEvent(GameEvent.ENTITY_DAMAGE, p_31649_.getEntity());
        }
    }

    private void brokenByPlayer(DamageSource p_31647_) {
        ItemStack itemstack = new ItemStack(ModItems.STATUE.get());
        if (this.hasCustomName()) {
            itemstack.setHoverName(this.getCustomName());
        }
        Block.popResource(this.level(), this.blockPosition(), itemstack);
        this.brokenByAnything(p_31647_);
    }

    private void brokenByAnything(DamageSource p_31654_) {
        this.playBrokenSound();
        this.dropAllDeathLoot(p_31654_);
    }

    private void playBrokenSound() {
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), AllSoundEvents.CARDBOARD_SWORD.getMainEvent(), this.getSoundSource(), 1.0F, 0.5F);
    }

    @Override
    protected float tickHeadTurn(float p_31644_, float p_31645_) {
        this.yBodyRotO = this.yRotO;
        this.yBodyRot = this.getYRot();
        return 0.0F;
    }

    @Override
    public void travel(Vec3 p_31656_) {
        if (this.hasPhysics()) {
            super.travel(p_31656_);
        }
    }

    @Override
    public void setYBodyRot(float p_31670_) {
        this.yBodyRotO = this.yRotO = p_31670_;
        this.yHeadRotO = this.yHeadRot = p_31670_;
    }

    @Override
    public void setYHeadRot(float p_31668_) {
        this.yBodyRotO = this.yRotO = p_31668_;
        this.yHeadRotO = this.yHeadRot = p_31668_;
    }

    @Override
    public void tick() {
        super.tick();
        if (getCustomName() != null && getCustomName().getString().equals("Delta")) setSkin(1);
        else if (getSkin() != 0 && getSkin() != 2) setSkin(0);
        Rotations rotations = this.entityData.get(DATA_HEAD_POSE);
        if (!this.headPose.equals(rotations)) {
            this.setHeadPose(rotations);
        }

        Rotations rotations1 = this.entityData.get(DATA_BODY_POSE);
        if (!this.bodyPose.equals(rotations1)) {
            this.setBodyPose(rotations1);
        }

        Rotations rotations2 = this.entityData.get(DATA_LEFT_ARM_POSE);
        if (!this.leftArmPose.equals(rotations2)) {
            this.setLeftArmPose(rotations2);
        }

        Rotations rotations3 = this.entityData.get(DATA_RIGHT_ARM_POSE);
        if (!this.rightArmPose.equals(rotations3)) {
            this.setRightArmPose(rotations3);
        }

        Rotations rotations4 = this.entityData.get(DATA_LEFT_LEG_POSE);
        if (!this.leftLegPose.equals(rotations4)) {
            this.setLeftLegPose(rotations4);
        }

        Rotations rotations5 = this.entityData.get(DATA_RIGHT_LEG_POSE);
        if (!this.rightLegPose.equals(rotations5)) {
            this.setRightLegPose(rotations5);
        }

        if (level().isClientSide) return;

        tickAnimation();
    }

    public void tickAnimation(){
        getAnimation().ifPresent(animation -> {
            if (isAnimationPlaying()) {
                if (getAnimationProgress() < animation.getDuration() && !isAnimationReversing()) {
                    setAnimationProgress(getAnimationProgress() + 1);
                } else if (getAnimationProgress() > 0 && isAnimationReversing()) {
                    setAnimationProgress(getAnimationProgress() - 1);
                } else if (getAnimationProgress() >= animation.getDuration()) {
                    if (animation.canBeRevert())
                        setAnimationReversing(true);
                    else {
                        setAnimationProgress(animation.isLooping() ? 0 : getAnimationProgress());
                        setAnimationPlaying(animation.isLooping());
                    }
                } else if (getAnimationProgress() <= 0) {
                    setAnimationReversing(false);
                    setAnimationPlaying(animation.isLooping());
                }
                if (animation.getFrameForProgress(getAnimationProgress()) != null) {
                    StatueAnimationFrame frame = animation.getFrameForProgress(getAnimationProgress());
                    if (animation.getNextFrame(frame) == null) {
                        setEntityRotations(frame.getGlobal().getXRot(), frame.getGlobal().getYRot(), frame.getGlobal().getZRot());
                        setHeadPose(frame.getHead().toRotation());
                        setLeftArmPose(frame.getLeftArm().toRotation());
                        setRightArmPose(frame.getRightArm().toRotation());
                        setLeftLegPose(frame.getLeftLeg().toRotation());
                        setRightLegPose(frame.getRightLeg().toRotation());
                    } else {
                        StatueAnimationFrame nextFrame = animation.getNextFrame(frame);
                        float t = (getAnimationProgress() - frame.getTick()) / (float) (animation.getNextFrame(frame).getTick() - frame.getTick());
                        Rotations globalPart = frame.getGlobal().toRotation();
                        Rotations nextGlobalPart = nextFrame.getGlobal().toRotation();
                        setEntityRotations(Mth.lerp(t, globalPart.getX(), nextGlobalPart.getX()), Mth.lerp(t, globalPart.getY(), nextGlobalPart.getY()), Mth.lerp(t, globalPart.getZ(), nextGlobalPart.getZ()));

                        Rotations headPart = frame.getHead().toRotation();
                        Rotations nextHeadPart = nextFrame.getHead().toRotation();
                        setHeadPose(new Rotations(
                                Mth.lerp(t, headPart.getX(), nextHeadPart.getX()),
                                Mth.lerp(t, headPart.getY(), nextHeadPart.getY()),
                                Mth.lerp(t, headPart.getZ(), nextHeadPart.getZ())
                        ));

                        Rotations leftArmPart = frame.getLeftArm().toRotation();
                        Rotations nextLeftArmPart = nextFrame.getLeftArm().toRotation();
                        setLeftArmPose(new Rotations(
                                Mth.lerp(t, leftArmPart.getX(), nextLeftArmPart.getX()),
                                Mth.lerp(t, leftArmPart.getY(), nextLeftArmPart.getY()),
                                Mth.lerp(t, leftArmPart.getZ(), nextLeftArmPart.getZ())
                        ));

                        Rotations rightArmPart = frame.getRightArm().toRotation();
                        Rotations nextRightArmPart = nextFrame.getRightArm().toRotation();
                        setRightArmPose(new Rotations(
                                Mth.lerp(t, rightArmPart.getX(), nextRightArmPart.getX()),
                                Mth.lerp(t, rightArmPart.getY(), nextRightArmPart.getY()),
                                Mth.lerp(t, rightArmPart.getZ(), nextRightArmPart.getZ())
                        ));

                        Rotations leftLegPart = frame.getLeftLeg().toRotation();
                        Rotations nextLeftLegPart = nextFrame.getLeftLeg().toRotation();
                        setLeftLegPose(new Rotations(
                                Mth.lerp(t, leftLegPart.getX(), nextLeftLegPart.getX()),
                                Mth.lerp(t, leftLegPart.getY(), nextLeftLegPart.getY()),
                                Mth.lerp(t, leftLegPart.getZ(), nextLeftLegPart.getZ())
                        ));

                        Rotations rightLegPart = frame.getRightLeg().toRotation();
                        Rotations nextRightLegPart = nextFrame.getRightLeg().toRotation();
                        setRightLegPose(new Rotations(
                                Mth.lerp(t, rightLegPart.getX(), nextRightLegPart.getX()),
                                Mth.lerp(t, rightLegPart.getY(), nextRightLegPart.getY()),
                                Mth.lerp(t, rightLegPart.getZ(), nextRightLegPart.getZ())
                        ));
                    }
                }
            }
        });
    }

    @Override
    protected void updateInvisibilityStatus() {
        this.setInvisible(this.invisible);
    }

    @Override
    public void setInvisible(boolean p_31663_) {
        this.invisible = p_31663_;
        super.setInvisible(p_31663_);
    }

    @Override
    public boolean isBaby() {
        return this.isSmall();
    }

    @Override
    public void kill() {
        this.remove(Entity.RemovalReason.KILLED);
        this.gameEvent(GameEvent.ENTITY_DIE);
    }


    @Override
    public boolean ignoreExplosion() {
        return this.isInvisible() || this.isInvulnerable();
    }

    @Override
    public PushReaction getPistonPushReaction() {
        return this.isMarker() ? PushReaction.IGNORE : super.getPistonPushReaction();
    }

    @Override
    public boolean isIgnoringBlockTriggers() {
        return this.isMarker();
    }

    public void setSmall(boolean p_31604_) {
        this.entityData.set(DATA_CLIENT_FLAGS, this.setBit(this.entityData.get(DATA_CLIENT_FLAGS), 1, p_31604_));
    }

    public boolean isSmall() {
        return (this.entityData.get(DATA_CLIENT_FLAGS) & 1) != 0;
    }

    public void setShowArms(boolean p_31676_) {
        this.entityData.set(DATA_CLIENT_FLAGS, this.setBit(this.entityData.get(DATA_CLIENT_FLAGS), 4, p_31676_));
    }

    public boolean isShowArms() {
        return (this.entityData.get(DATA_CLIENT_FLAGS) & 4) != 0;
    }

    public void setSlimArms(boolean p_31676_) {
        this.entityData.set(DATA_CLIENT_FLAGS, this.setBit(this.entityData.get(DATA_CLIENT_FLAGS), 8, p_31676_));
    }

    public boolean isSlimArms() {
        return (this.entityData.get(DATA_CLIENT_FLAGS) & 8) != 0;
    }

    public void setMarker(boolean p_31682_) {
        this.entityData.set(DATA_CLIENT_FLAGS, this.setBit(this.entityData.get(DATA_CLIENT_FLAGS), 16, p_31682_));
    }

    public boolean isMarker() {
        return (this.entityData.get(DATA_CLIENT_FLAGS) & 16) != 0;
    }

    private byte setBit(byte value, int mask, boolean status) {
        if (status) {
            value = (byte)(value | mask);
        } else {
            value = (byte)(value & ~mask);
        }

        return value;
    }

    public void setOwner(UUID owner) {
        this.entityData.set(DATA_OWNER, owner == null ? Optional.empty() : Optional.of(owner));
    }

    public boolean hasOwner() {
        return this.entityData.get(DATA_OWNER).isPresent();
    }

    public Optional<UUID> getOwner() {
        return this.entityData.get(DATA_OWNER);
    }


    public void setHeadPose(Rotations p_31598_) {
        this.headPose = p_31598_;
        this.entityData.set(DATA_HEAD_POSE, p_31598_);
    }

    public void setBodyPose(Rotations p_31617_) {
        this.bodyPose = p_31617_;
        this.entityData.set(DATA_BODY_POSE, p_31617_);
    }

    public void setLeftArmPose(Rotations p_31624_) {
        this.leftArmPose = p_31624_;
        this.entityData.set(DATA_LEFT_ARM_POSE, p_31624_);
    }

    public void setRightArmPose(Rotations p_31629_) {
        this.rightArmPose = p_31629_;
        this.entityData.set(DATA_RIGHT_ARM_POSE, p_31629_);
    }

    public void setLeftLegPose(Rotations p_31640_) {
        this.leftLegPose = p_31640_;
        this.entityData.set(DATA_LEFT_LEG_POSE, p_31640_);
    }

    public void setRightLegPose(Rotations p_31652_) {
        this.rightLegPose = p_31652_;
        this.entityData.set(DATA_RIGHT_LEG_POSE, p_31652_);
    }

    public Rotations getHeadPose() {
        return this.headPose;
    }

    public Rotations getBodyPose() {
        return this.bodyPose;
    }

    public Rotations getLeftArmPose() {
        return this.leftArmPose;
    }

    public Rotations getRightArmPose() {
        return this.rightArmPose;
    }

    public Rotations getLeftLegPose() {
        return this.leftLegPose;
    }

    public Rotations getRightLegPose() {
        return this.rightLegPose;
    }

    @Override
    public boolean isPickable() {
        return super.isPickable() && !this.isMarker();
    }

    @Override
    public boolean skipAttackInteraction(Entity p_31687_) {
        return p_31687_ instanceof Player && !this.level().mayInteract((Player)p_31687_, this.blockPosition());
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    public LivingEntity.Fallsounds getFallSounds() {
        return new LivingEntity.Fallsounds(SoundEvents.ARMOR_STAND_FALL, SoundEvents.ARMOR_STAND_FALL);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_31636_) {
        return SoundEvents.ARMOR_STAND_HIT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ARMOR_STAND_BREAK;
    }

    @Override
    public void thunderHit(ServerLevel p_31576_, LightningBolt p_31577_) {
    }

    @Override
    public boolean isAffectedByPotions() {
        return false;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> p_31602_) {
        if (DATA_CLIENT_FLAGS.equals(p_31602_)) {
            this.refreshDimensions();
            this.blocksBuilding = !this.isMarker();
        }

        super.onSyncedDataUpdated(p_31602_);
    }

    @Override
    public boolean attackable() {
        return false;
    }


    private EntityDimensions getDimensionsMarker(boolean p_31684_) {
        if (p_31684_) {
            return MARKER_DIMENSIONS;
        } else {
            return this.isBaby() ? BABY_DIMENSIONS : this.getType().getDimensions();
        }
    }

    public boolean isPartShown(PlayerModelPart part) {
        return (this.getEntityData().get(DATA_PLAYER_SKIN_CUSTOMISATION) & part.getMask()) == part.getMask();
    }

    public void setPartVisibility(PlayerModelPart modelPart, boolean visible) {
        this.entityData.set(DATA_PLAYER_SKIN_CUSTOMISATION, setBit(this.entityData.get(DATA_PLAYER_SKIN_CUSTOMISATION), modelPart.getMask(), visible));
    }

    @Override
    public Vec3 getLightProbePosition(float p_31665_) {
        if (this.isMarker()) {
            AABB aabb = this.getDimensionsMarker(false).makeBoundingBox(this.position());
            BlockPos blockpos = this.blockPosition();
            int i = Integer.MIN_VALUE;

            for (BlockPos blockpos1 : BlockPos.betweenClosed(
                    BlockPos.containing(aabb.minX, aabb.minY, aabb.minZ), BlockPos.containing(aabb.maxX, aabb.maxY, aabb.maxZ)
            )) {
                int j = Math.max(this.level().getBrightness(LightLayer.BLOCK, blockpos1), this.level().getBrightness(LightLayer.SKY, blockpos1));
                if (j == 15) {
                    return Vec3.atCenterOf(blockpos1);
                }

                if (j > i) {
                    i = j;
                    blockpos = blockpos1.immutable();
                }
            }

            return Vec3.atCenterOf(blockpos);
        } else {
            return super.getLightProbePosition(p_31665_);
        }
    }

    public Optional<GameProfile> getProfile() {
        return this.entityData.get(DATA_PROFILE);
    }

    public void verifyAndSetProfile(@Nullable GameProfile gameProfile) {
        // check for max name length here as client will crash when value is exceeded
        if (gameProfile != null && (!gameProfile.isComplete() || gameProfile.getName().length() > 16)) {
            if (gameProfile.getName().length() > 16) {
                if (gameProfile.getId() != null) {
                    // will throw exception if both uuid and name are empty
                    gameProfile = new GameProfile(gameProfile.getId(), "");
                } else {
                    this.setOwner(null);
                    return;
                }
            }
            SkullBlockEntity.updateGameprofile(gameProfile, this::setProfile);
        } else {
            this.setProfile(gameProfile);
        }
    }


    public void setProfile(@Nullable GameProfile resolvableProfile) {
        this.entityData.set(DATA_PROFILE, Optional.ofNullable(resolvableProfile));
    }


    @Override
    public ItemStack getPickResult() {
        return new ItemStack(ModItems.STATUE.get());
    }

    @Override
    public boolean canBeSeenByAnyone() {
        return !this.isInvisible() && !this.isMarker();
    }

    @SuppressWarnings("unchecked")
    public static EntityType.Builder<?> build(EntityType.Builder<?> builder) {
        EntityType.Builder<Statue> entityBuilder = (EntityType.Builder<Statue>) builder;
        return entityBuilder.sized(0.5F, 1.975F);
    }

    public float getEntityXRotation() {
        return this.getEntityRotations().getX();
    }

    public Rotations getEntityRotations() {
        Rotations rotations = this.entityData.get(DATA_GLOBAL_ROTATIONS);
        return new Rotations(rotations.getX() - 180, rotations.getY() - 180, rotations.getZ() - 180);
    }

    public float getEntityZRotation() {
        return this.getEntityRotations().getZ();
    }

    public void setEntityXRotation(float rotationX) {
        this.setEntityRotations(rotationX,getYRot(), this.getEntityZRotation());
    }

    public void setEntityZRotation(float rotationZ) {
        this.setEntityRotations(this.getEntityXRotation(),getYRot(), rotationZ);
    }


    public void setEntityRotations(float rotationX, float rotationY, float rotationZ) {
        rotationX = Mth.clamp(rotationX + 180, 0.0F, 360.0F);
        rotationY = Mth.clamp(rotationY + 180, 0.0F, 360.0F);
        rotationZ = Mth.clamp(rotationZ + 180, 0.0F, 360.0F);
        this.entityData.set(DATA_GLOBAL_ROTATIONS, new Rotations(rotationX, rotationY, rotationZ));
    }

    @Override
    public float getYRot() {
        return getEntityRotations().getY();
    }

    @Override
    public void setYRot(float p_146923_) {
        this.setEntityRotations(this.getEntityXRotation(),p_146923_, getEntityZRotation());
    }
}