package api.more_factory;

import mindustry.gen.Building;

// 资源消耗接口，定义验证和消耗的标准方法
public interface Consume {
    // 验证当前建筑是否满足消耗条件
    boolean valid(Building build);
    
    // 执行实际的资源消耗操作
    void consume(Building build);
}
