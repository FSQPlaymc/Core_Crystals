package api.Item_void;

import arc.math.Mathf;
import arc.struct.Seq;
import content.GGItems;
import mindustry.type.ItemStack;

public class GGItemStack implements Comparable<GGItemStack>{
    public static final GGItemStack[] empty = {};

    //public Item item;
    public GGItem_NC GG_NC_item;
    public int amount = 0;

    public GGItemStack(GGItem_NC GG_NC_item, int amount){
        if(GG_NC_item == null) GG_NC_item = GGItems.U;
        this.GG_NC_item = GG_NC_item;
        this.amount = amount;
    }

    //serialization only
    public GGItemStack(){
        //prevent nulls.
        GG_NC_item = GGItems.U;
    }

    public GGItemStack set(GGItem_NC item, int amount){
        this.GG_NC_item = item;
        this.amount = amount;
        return this;
    }

    public GGItemStack copy(){
        return new GGItemStack(GG_NC_item, amount);
    }

    public boolean equals(GGItemStack other){
        return other != null && other.GG_NC_item == GG_NC_item && other.amount == amount;
    }

    public static GGItemStack[] mult(GGItemStack[] stacks, float amount){
        var copy = new GGItemStack[stacks.length];
        for(int i = 0; i < copy.length; i++){
            copy[i] = new GGItemStack(stacks[i].GG_NC_item, Mathf.round(stacks[i].amount * amount));
        }
        return copy;
    }

    public static GGItemStack[] with(Object... items){
        var stacks = new GGItemStack[items.length / 2];
        for(int i = 0; i < items.length; i += 2){
            stacks[i / 2] = new GGItemStack((GGItem_NC)items[i], ((Number)items[i + 1]).intValue());
        }
        return stacks;
    }

    public static Seq<GGItemStack> list(Object... items){
        Seq<GGItemStack> stacks = new Seq<>(items.length / 2);
        for(int i = 0; i < items.length; i += 2){
            stacks.add(new GGItemStack((GGItem_NC)items[i], ((Number)items[i + 1]).intValue()));
        }
        return stacks;
    }

    public static GGItemStack[] copy(GGItemStack[] stacks){
        var out = new GGItemStack[stacks.length];
        for(int i = 0; i < out.length; i++){
            out[i] = stacks[i].copy();
        }
        return out;
    }

    @Override
    public int compareTo(GGItemStack itemStack){
        return GG_NC_item.compareTo(itemStack.GG_NC_item);
    }

    @Override
    public boolean equals(Object o){
        return this == o || (o instanceof GGItemStack stack && stack.amount == amount && GG_NC_item == stack.GG_NC_item);
    }

    @Override
    public String toString(){
        return GG_NC_item + ": " + amount;
    }
}
