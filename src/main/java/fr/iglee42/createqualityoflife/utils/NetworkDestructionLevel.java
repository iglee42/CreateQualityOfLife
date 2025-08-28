package fr.iglee42.createqualityoflife.utils;

import com.simibubi.create.Create;
import fr.iglee42.createqualityoflife.CreateQOLLang;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;
import java.util.function.BiPredicate;

public enum NetworkDestructionLevel implements StringRepresentable {

    ALL((network,player)->true),
    MEMBERS(Create.LOGISTICS::mayInteract),
    ADMINS(Create.LOGISTICS::mayAdministrate)
    ;


    private final BiPredicate<UUID, Player> canDestroy;

    NetworkDestructionLevel(BiPredicate<UUID, Player> canDestroy) {
        this.canDestroy = canDestroy;
    }

    public NetworkDestructionLevel next(){
        int nextOrdinal = (this.ordinal() + 1) % values().length;
        return values()[nextOrdinal];
    }

    public boolean canDestroy(UUID networkId, Player player){
        return canDestroy.test(networkId,player);
    }

    public Component getName() {
        return CreateQOLLang.translate("gui.stock_manager.destruction_level."+getSerializedName()).component();
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase();
    }
}
