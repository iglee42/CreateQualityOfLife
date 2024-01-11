package fr.iglee42.createqualityoflife.registries;

import com.simibubi.create.content.legacy.NoGravMagicalDohickyItem;
import com.tterrag.registrate.util.entry.ItemEntry;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.items.PlayerPaperItem;
import fr.iglee42.createqualityoflife.utils.Features;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import static fr.iglee42.createqualityoflife.CreateQOL.REGISTRATE;

public class ModItems {

    static {
        REGISTRATE.setCreativeTab(ModCreativeModeTabs.MAIN_TAB);
    }

    public static ItemEntry<PlayerPaperItem> PLAYER_PAPER = REGISTRATE.item("player_paper", PlayerPaperItem::new)
            .properties(properties -> properties.stacksTo(1).rarity(Rarity.RARE))
            .register();

    public static final ItemEntry<NoGravMagicalDohickyItem> SHADOW_RADIANCE = REGISTRATE.item("shadow_radiance", NoGravMagicalDohickyItem::new)
                    .properties(p->p.rarity(Rarity.RARE))
                    .register();

    public static void register(){}
}
