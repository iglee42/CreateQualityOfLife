package fr.iglee42.createqualityoflife.utils;

import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;

import java.util.Arrays;

public class ItemTooltips {

    public static enum Tooltip implements StringRepresentable {

        ENCHANTMENT("Enchantments"),
        OPTIONS("Abilities"),
        ATTRIBUTE_MODIFIERS("Attributes");

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

    public static ItemTooltips of(CompoundTag tag){
        ItemTooltips.Mutable mutable = new ItemTooltips.Mutable(DEFAULT);
        for (Tooltip t : Tooltip.values()) {
            if (tag.contains(t.getSerializedName())) {
                mutable.set(t, tag.getBoolean(t.getSerializedName()));
            }
        }
        return mutable.toImmutable();
    }

    public static final ItemTooltips DEFAULT = createDefaultTooltips();

    private static ItemTooltips createDefaultTooltips() {
        Object2BooleanOpenHashMap<Tooltip> map = new Object2BooleanOpenHashMap<>();
        Arrays.stream(Tooltip.values()).forEach(t->map.put(t,true));
        return new ItemTooltips(map);
    }

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

    public CompoundTag save(){
        CompoundTag tag = new CompoundTag();
        tooltips.forEach((t,b)-> tag.putBoolean(t.getSerializedName(),b));
        return tag;
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
