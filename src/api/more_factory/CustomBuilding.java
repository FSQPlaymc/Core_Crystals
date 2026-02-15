// 自定义建筑类，继承自mindustry的Building并添加电力支持
package api.more_factory;

import mindustry.gen.Building;

public class CustomBuilding extends Building {
    public PowerLink power = new PowerLink(); // 初始化电力连接
    
    @Override
    public void created() {
        super.created();
        // 初始化电力网络（最大存储1000单位电力，可根据需求调整）
        power.init(1000f);
    }
}
    