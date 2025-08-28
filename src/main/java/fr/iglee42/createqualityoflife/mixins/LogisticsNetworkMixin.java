package fr.iglee42.createqualityoflife.mixins;

import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.packagerLink.LogisticsNetwork;
import fr.iglee42.createqualityoflife.utils.LogisticsNetworkExtension;
import fr.iglee42.createqualityoflife.utils.NetworkDestructionLevel;
import fr.iglee42.createqualityoflife.utils.NetworkPermission;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(value = LogisticsNetwork.class,remap = false)
public class LogisticsNetworkMixin implements LogisticsNetworkExtension {

    @Unique
    private String createQOL$name = "Unnamed Network";
    @Unique
    private NetworkDestructionLevel createQOL$destructionLevel = NetworkDestructionLevel.ALL;
    @Unique
    private Map<UUID,NetworkPermission> createQOL$permissions = new HashMap<>();

    @Override
    public String createQOL$getName() {
        return createQOL$name;
    }

    @Override
    public void createQOL$setName(String name) {
        this.createQOL$name = name;
        Create.LOGISTICS.markDirty();
    }

    @Override
    public NetworkDestructionLevel createQOL$getDestructionLevel() {
        return createQOL$destructionLevel;
    }

    @Override
    public void createQOL$setDestructionLevel(NetworkDestructionLevel level) {
        this.createQOL$destructionLevel = level;
        Create.LOGISTICS.markDirty();
    }

    @Override
    public Map<UUID, NetworkPermission> createQOL$getPlayersPermission() {
        return createQOL$permissions;
    }

    @Override
    public void createQOL$setPlayersPermission(Map<UUID, NetworkPermission> permissions) {
        this.createQOL$permissions = permissions;
        Create.LOGISTICS.markDirty();
    }

    @Inject(method = "read",at = @At("RETURN"), cancellable = true)
    private static void createQOL$addNewFieldsToRead(CompoundTag tag, CallbackInfoReturnable<LogisticsNetwork> cir){
        LogisticsNetwork network = cir.getReturnValue();
        if (tag.contains("Name")) ((LogisticsNetworkExtension)network).createQOL$setName(tag.getString("Name"));
        if (tag.contains("DestructionLevel")) ((LogisticsNetworkExtension)network).createQOL$setDestructionLevel(NetworkDestructionLevel.values()[tag.getByte("DestructionLevel")]);
        if (tag.contains("Permissions")) {
            Map<UUID, NetworkPermission> permissions = new HashMap<>();
            CompoundTag permissionTags = tag.getCompound("Permissions");
            permissionTags.getAllKeys().stream().map(k->{
                UUID uuid = UUID.fromString(k);
                NetworkPermission permission = NetworkPermission.values()[permissionTags.getInt(k)];
                return new AbstractMap.SimpleEntry<>(uuid,permission);
            }).forEach(e->permissions.put(e.getKey(),e.getValue()));
            //Remove Stored Players with the owner permission because it is managed by the owner field
            List<UUID> toRemove = new ArrayList<>();
            Map<UUID, NetworkPermission> newPerms = new HashMap<>(permissions);
            newPerms.keySet().stream().filter(k->newPerms.get(k).equals(NetworkPermission.OWNER) || newPerms.get(k).equals(NetworkPermission.NONE)).forEach(toRemove::add);
            toRemove.forEach(newPerms::remove);
            ((LogisticsNetworkExtension)network).createQOL$setPlayersPermission(newPerms);
        }
        cir.setReturnValue(network);
    }

    @Inject(method = "write",at = @At("RETURN"), cancellable = true)
    private void createQOL$addNewFieldsToWrite(CallbackInfoReturnable<CompoundTag> cir){
        CompoundTag tag = cir.getReturnValue();
        tag.putString("Name",createQOL$getName());
        tag.putByte("DestructionLevel", (byte) createQOL$getDestructionLevel().ordinal());
        CompoundTag perms = new CompoundTag();
        createQOL$getPlayersPermission().forEach((id,perm)->perms.putInt(id.toString(),perm.ordinal()));
        tag.put("Permissions",perms);
        cir.setReturnValue(tag);
    }
}
