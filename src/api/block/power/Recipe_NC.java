package api.block.power;

import api.Item_void.GGItemStack;
import api.Item_void.GGItem_NC;
import api.type.Recipe;
import arc.struct.Seq;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;

public class Recipe_NC extends Recipe {
    public Seq<ItemStack> outputItem=new Seq<>();
    public Seq<LiquidStack> outputLiquid=new Seq<>();
    public Recipe_NC(Object...objects){
        for (int i = 0; i < objects.length / 3; i++){//0为输入
            if (objects[i * 3] instanceof GGItem_NC item && objects[i * 3 + 1] instanceof Integer count&&(objects[i * 3 + 2]instanceof Integer vc)){
                if (vc==0){
                    inputItem.add(new ItemStack(item, count));
                }else {
                    outputItem.add(new ItemStack(item,count));
                }
            }else if (objects[i * 3] instanceof Liquid liquid && objects[i * 3 + 1] instanceof Float count&&(objects[i * 3 + 2]instanceof Integer vc)){
                if (vc==0){inputLiquid.add(new LiquidStack(liquid, count));}
                else {
                    outputLiquid.add(new LiquidStack(liquid, count));
                }
            }
        }

        if (objects.length % 2 != 0 && objects[objects.length - 1] instanceof Float multiplier){
            boostScl = multiplier;
        }
    }
}
