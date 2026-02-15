package content;

import arc.graphics.Color;
import mindustry.content.StatusEffects;
import mindustry.type.Liquid;

public class GG_Liquids {
    public static Liquid oo ,os,zaisuye;
    public static void GGLiquids(){
        zaisuye=new Liquid("zaisuye",Color.valueOf("b0ef35")){{
            this.coolant = true;
            boilPoint = 0.5f;
            this.temperature = 0.3F; // 初始化温度值为0.5
            this.heatCapacity = 1.5F; // 初始化热容量值为0.5
            this.viscosity = 0.999999F; // 初始化粘度值为0.5
            gasColor = Color.valueOf("b0ef35");
        }};
    oo=new Liquid("oxygen", Color.valueOf("f7e5e4")){{
    this.temperature=0.2f;
    this.heatCapacity=0.1f;
    this.coolant=false;
        this.incinerable = false; // 初始化可燃烧特：可燃烧
        this.moveThroughBlocks = true; // 初始化穿透方块特：不可穿过方块
        this.boilPoint = 0.1F; // 初始化沸点polluting
        this.gas=true;
        gasColor = Color.valueOf("FFFFFF");
        lightColor = Color.valueOf("fdfdfd").a(0.2f);
    }};
    os=new Liquid("polluting-oxygen",Color.valueOf("527723")){{
        this.temperature=0.2f;
        this.heatCapacity=0.1f;
        this.coolant=false;
        this.incinerable = false; // 初始化可燃烧特：可燃烧
        this.moveThroughBlocks = true; // 初始化穿透方块特：不可穿过方块
        this.boilPoint = 0.1F; // 初始化沸点polluting
        this.gas=true;
        gasColor = Color.valueOf("96ca53");
        lightColor = Color.valueOf("5c8724").a(0.2f);
    }};
    }
}
/*
 public 液体(String 名称, 颜色 颜色值) {
this.gas = false; // 初始化是否为气体：默认非气体
    this.gasColor = Color.lightGray.cpy(); // 初始化气体状态颜色：浅灰色的副本
    this.lightColor = Color.clear.cpy(); // 初始化发光颜色：透明色的副本
    this.temperature = 0.5F; // 初始化温度值为0.5
    this.heatCapacity = 0.5F; // 初始化热容量值为0.5
    this.viscosity = 0.5F; // 初始化粘度值为0.5
    this.blockReactive = true; // 初始化与方块反应特性：可与方块反应
    this.coolant = true; // 初始化冷却剂特性：可作为冷却剂
    this.moveThroughBlocks = false; // 初始化穿透方块特性：不可穿过方块
    this.incinerable = true; // 初始化可燃烧特性：可燃烧
    this.effect = StatusEffects.none; // 初始化接触时的状态效果：无效果
    this.particleEffect = Fx.none; // 初始化粒子效果：无粒子效果
    this.particleSpacing = 60.0F; // 初始化粒子间距为60.0
    this.boilPoint = 2.0F; // 初始化沸点为2.0
    this.capPuddles = true; // 初始化液池形成特性：可形成液池
    this.vaporEffect = Fx.vapor; // 初始化汽化时的特效：蒸汽特效
    this.canStayOn = new ObjectSet(); // 初始化可停留的液体集合（空集合）
    this.color = new Color(color); // 初始化液体颜色：传入颜色的副本*/