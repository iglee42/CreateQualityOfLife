package fr.iglee42.createqualityoflife.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class ItemTooltips {

    public static enum Tooltip implements StringRepresentable {

        ENCHANTMENT("Enchantments"),
        OPTIONS("Abilities"),
        ATTRIBUTE_MODIFIERS("Attributes");

        public static final Codec<Tooltip> CODEC = StringRepresentable.fromValues(Tooltip::values);
        public static final IntFunction<Tooltip> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final StreamCodec<ByteBuf, Tooltip> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Enum::ordinal);

        private final String displayName;
        Tooltip(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase();
        }
    }

    public static final ItemTooltips DEFAULT = createDefaultTooltips();

    private static ItemTooltips createDefaultTooltips() {
        Object2BooleanOpenHashMap<Tooltip> map = new Object2BooleanOpenHashMap<>();
        Arrays.stream(Tooltip.values()).forEach(t->map.put(t,true));
        return new ItemTooltips(map);
    }

    private static final Codec<Object2BooleanOpenHashMap<Tooltip>> MAP_CODEC = Codec.unboundedMap(Tooltip.CODEC, Codec.BOOL)
        .xmap(Object2BooleanOpenHashMap::new, Function.identity());
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemTooltips> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.map(Object2BooleanOpenHashMap::new, Tooltip.STREAM_CODEC, ByteBufCodecs.BOOL),
        p_340784_ -> p_340784_.tooltips,
        ItemTooltips::new
    );
    public static final Codec<ItemTooltips> CODEC = MAP_CODEC.xmap(
            ItemTooltips::new,
            t->t.tooltips
    );
    final Object2BooleanOpenHashMap<Tooltip> tooltips;

    ItemTooltips(Object2BooleanOpenHashMap<Tooltip> p_341287_) {
        this.tooltips = p_341287_;
    }

    public boolean isEnable(Tooltip tooltip) {
        return this.tooltips.getBoolean(tooltip);
    }

    public int size() {
        return this.tooltips.size();
    }

    public boolean isEmpty() {
        return this.tooltips.isEmpty();
    }


    @Override
    public boolean equals(Object p_331697_) {
        if (this == p_331697_) {
            return true;
        } else {
            return p_331697_ instanceof ItemTooltips itemenchantments && this.tooltips.equals(itemenchantments.tooltips);
        }
    }

    @Override
    public int hashCode() {
        int i = this.tooltips.hashCode();
        return 31 * i;
    }

    @Override
    public String toString() {
        return "ItemTooltips{tooltips=" + this.tooltips + "}";
    }

    public static class Mutable {
        private final Object2BooleanOpenHashMap<Tooltip> tooltips = new Object2BooleanOpenHashMap<>();

        public Mutable(ItemTooltips tooltips) {
            this.tooltips.putAll(tooltips.tooltips);
        }

        public void set(Tooltip tooltip, boolean enable) {
                this.tooltips.put(tooltip, enable);
        }

        public boolean isEnable(Tooltip tooltip) {
            return this.tooltips.getOrDefault(tooltip,true);
        }

        public ItemTooltips toImmutable() {
            return new ItemTooltips(this.tooltips);
        }
    }
}
