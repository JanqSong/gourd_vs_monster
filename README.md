* [葫芦娃大战妖精](#葫芦娃大战妖精)
  * [1 功能介绍](#1-功能介绍)
    * [1\.1 联机对战](#11-联机对战)
      * [1\.1\.1 联机方式介绍](#111-联机方式介绍)
      * [1\.1\.2 游戏逻辑介绍](#112-游戏逻辑介绍)
      * [1\.1\.3 网络协议设计](#113-网络协议设计)
    * [1\.2 本地回放](#12-本地回放)
    * [1\.3 关于游戏](#13-关于游戏)
  * [2 代码分析](#2-代码分析)
    * [2\.1 包com\.sjq\.gourd\.ai](#21-包comsjqgourdai)
      * [2\.1\.1 内容](#211-内容)
      * [2\.1\.2 接口介绍](#212-接口介绍)
      * [2\.1\.3 ai开发过程](#213-ai开发过程)
      * [2\.1\.4 可扩展性](#214-可扩展性)
    * [2\.2 包 com\.sjq\.gourd\.bullet](#22-包-comsjqgourdbullet)
      * [2\.2\.1 内容](#221-内容)
      * [2\.2\.2 Bullet子弹类的设计](#222-bullet子弹类的设计)
      * [2\.2\.3 子弹的各种状态](#223-子弹的各种状态)
    * [2\.3 包com\.sjq\.gourd\.collision](#23-包comsjqgourdcollision)
      * [2\.3\.1 内容](#231-内容)
    * [2\.4 包com\.sjq\.gourd\.Creature](#24-包comsjqgourdcreature)
      * [2\.4\.1 内容](#241-内容)
      * [2\.4\.2 包内类的关系](#242-包内类的关系)
      * [2\.4\.3 Creature类的设计](#243-creature类的设计)
      * [2\.4\.4 Creature子类的设计](#244-creature子类的设计)
      * [2\.4\.5 CreatureFactory类的设计](#245-creaturefactory类的设计)
      * [2\.4\.6 CreatureStateWithClockand CreatureState](#246-creaturestatewithclockand-creaturestate)
      * [2\.4\.7 ImagePosition类的设计](#247-imageposition类的设计)
    * [2\.5 包com\.sjq\.gourd\.Equipment](#25-包comsjqgourdequipment)
      * [2\.5\.1 内容](#251-内容)
      * [2\.5\.2 各个类的关系](#252-各个类的关系)
      * [2\.5\.3 Equipment抽象类的设计](#253-equipment抽象类的设计)
      * [2\.5\.4 Equipment子类设计](#254-equipment子类设计)
      * [2\.5\.5 EquipmentFactory装备工厂设计](#255-equipmentfactory装备工厂设计)
    * [2\.6 包com\.sjq\.gourd\.constant](#26-包comsjqgourdconstant)
      * [2\.6\.1 内容](#261-内容)
  * [3 游戏测试](#3-游戏测试)
    * [3\.1 单元测试](#31-单元测试)
    * [3\.2 胜率测试](#32-胜率测试)
      * [3\.2\.1 预期目标](#321-预期目标)
      * [3\.2\.2 游戏测试数据](#322-游戏测试数据)
      * [3\.2\.3 游戏测试文件](#323-游戏测试文件)
  * [4 开发过程](#4-开发过程)
    * [4\.1 需求分析与策划](#41-需求分析与策划)
    * [4\.2 模块划分与任务分配](#42-模块划分与任务分配)
    * [4\.3 单机版实现](#43-单机版实现)
    * [4\.4 联机同步实现](#44-联机同步实现)
    * [4\.5 功能和文档完善](#45-功能和文档完善)
  * [5 写给玩家](#5-写给玩家)
    * [5\.1 玩法说明](#51-玩法说明)
    * [5\.2 葫芦娃阵营详细数据](#52-葫芦娃阵营详细数据)
    * [5\.3 妖精阵营详细数据](#53-妖精阵营详细数据)
    * [5\.4 装备详细数据](#54-装备详细数据)

# 葫芦娃大战妖精



## 1 功能介绍

### 1.1 联机对战

#### 1.1.1 联机方式介绍

#### 1.1.2 游戏逻辑介绍

#### 1.1.3 网络协议设计

### 1.2 本地回放

### 1.3 关于游戏

## 2 代码分析

### 2.1 包`com.sjq.gourd.ai`

#### 2.1.1 内容

这个包内主要包括一个接口`AiInterface`,和两个实现该接口的类`FoolAi`和`FirstGenerationAi`，这个接口的设计模式是委托模式,与`Creature`类的关系是聚合，`Creature`将方向的选择与攻击功能交给`AiInterface`来实现

#### 2.1.2 接口介绍

```java
public interface AiInterface {
    //观测
    public Creature observe(Creature myCreature, HashMap<Integer,Creature> enemies);
    //移动方式
    public void moveMod(Creature myCreature, HashMap<Integer,Creature> enemies);
    //攻击模式
    public Bullet aiAttack(Creature myCreature, HashMap<Integer,Creature> enemies);
}
```

该接口定义了三个功能,分别是观测敌军选取攻击目标,基于观测选择移动方式(设置方向),基于观测攻击目标产生子弹

#### 2.1.3 `ai`开发过程

`FoolAi`采用完全随机的选取方向，观测的第一优先级生物是最近的生物,攻击最近的生物。缺点有两点,其一是生物每一帧的方向都可能会发生改变,导致图片频繁抖动。其二是选取最近的生物攻击会导致总体上生物会越来越趋向合在一起,不美观，操作难。

`FirstGenerationAi`综合距离，血量，我方攻击力，敌方防御力等多项因素设置第一优先攻击目标,根据攻击距离以及与攻击目标之间的关系设置方向,并在每次设置方向后锁定方向一段时间,解决了图片抖动问题,使得生物在自动攻击与移动时相对分散且较为智能

#### 2.1.4 可扩展性

这个`ai`接口的可扩展性好,只要实现三个方法就行,可继承`FirstGenerationAi`类实现第二代`ai`，也可通过实现接口的方式为任何一个生物设计`ai`战斗方式



### 2.2 包 `com.sjq.gourd.bullet`

#### 2.2.1 内容

这个包里面包含了子弹类`Bullet`和子弹状态`BulletState`

#### 2.2.2 `Bullet`子弹类的设计

子弹分为近战子弹和远程子弹

近战子弹为各种爪痕，远程子弹为各种不同颜色不同半径的圆球

对于远程子弹而言，它是追踪的，当目标生物死亡或者发生碰撞时，它会消失

对于近战子弹而言，它是打出即碰撞的，没有运行轨迹，是打出必定命中的

任何子弹在且仅在命中目标时会给发出子弹的生物回复蓝量

将子弹内部逻辑 移动`move()`  绘制`draw()`检测碰撞封装成一个方法`update()`

在每一帧内执行这个`update`方法,该方法返回一个可能存在的碰撞`Collision`

#### 2.2.3 子弹的各种状态

子弹包含各种各样的状态，NONE为普通子弹，其他的各种在碰撞时会给目标生物附加各种状态



### 2.3 包`com.sjq.gourd.collision`

#### 2.3.1 内容

这个包内仅包含一个类`Collision`碰撞类

对于每一个`Collision`对象而言,每当其产生时，就会调用碰撞方法执行碰撞，给目标生物扣血并附加状态，回复攻击者的蓝量



### 2.4 包`com.sjq.gourd.Creature`

#### 2.4.1 内容

这个包内包含三个方面的内容:

1.`Creature`类和`Creature子类`以及产生它们的对象的工厂类`CreatureFactory`

2.`CreatureState`生物状态枚举和`CreatureStateWithClock`生物状态时钟

3.`ImagePosition`

#### 2.4.2 包内类的关系

`Creature`是包括葫芦娃阵营的九个生物和妖精阵营的六个生物的父类

`CreatureFactory`是生产`Creature`子类对象的工厂

15种子类和父类`Creature`的构造器都是默认访问权限,包外的生物对象仅能在工厂内创建,体现了面向对象的封装特性

#### 2.4.3 `Creature`类的设计

 `Creature`类的主要功能被封装在`update()`和`notMyCampUpdate()`两个方法中，分别用来更新本地我的阵营和敌方阵营的生物信息

`Creature`内部的主要功能包括

生物图像绘制`draw()`

生物移动`move()`

生物攻击`ai`对象实现

生物状态信息更新`setCreatureState()`

生物技能实现`QAction(),EAction(),RAction()`

生物信息显示`showMessage()`

......

由于这部分内容过多，这里不详细介绍，具体可见`Creature.java`,此类中有详细注释

#### 2.4.4 `Creature`子类的设计

这里以`SnakeMonster`为例,介绍一下Creature子类的设计方法

```java
public class SnakeMonster extends Creature {
	//数据域:包括技能标志位,技能使用时间,技能效果时长,技能冷却时间,技能属性改变量等信息

    public SnakeMonster(...) {
        super(...)
    }

    @Override
    //重写父类update()方法,加入技能的使用和技能状态的更新
    public ArrayList<Bullet> update() {
        ...
    }
	
    //更新技能状态，技能持续时间是否结束
    private void updateActionState() {
        ...
    }

    //重写父类的QER三项技能
    @Override
    public ArrayList<Bullet> qAction() {
        ...
    }

    @Override
    public void eAction() {
        ...
    }

    @Override
    public void rAction() {
        ...
    }

    //技能失效时调用相关方法
    private void disposeQAction() {
        ...
    }

    private void disposeEAction() {
        ...
    }

    private void disposeRAction() {
        ...
    }
}
```

只要子类的行为方式和父类不相同的地方，就可以重写父类方法

但父类方法没有必要定义成`abstract`因为有些子类会重写它，而另一些不会(不是所有生物都有三个技能)

#### 2.4.5 `CreatureFactory`类的设计

`CreatureFactory`中有两个对外接口

其一是`hasNext()`询问此阵营是否还会有下一个对象

其二是`next()`返回下一个`Creature`对象

通过这个工厂的设计，我们将生物对象和外部分离开来，使它具有良好的封装性

#### 2.4.6 `CreatureStateWithClock`and `CreatureState`

`CreatureState` 是个枚举类型,其中包含了多种多样的生物状态,还包括了生物技能状态

`CreatureStateWithClock`是一个含有时钟的状态类

它可以通过`update()`方法对状态时钟进行实时更新

这个类帮助`Creature`实现了各种`buff`和`debuff`功能，已经这些功能的倒计时,同时也实现了技能的`cd`倒计时

#### 2.4.7 `ImagePosition`类的设计

这个类表示的是一个位置信息,`Creature`和`Bullet`以及`Equipment`都会用到它

### 2.5 包`com.sjq.gourd.Equipment`

#### 2.5.1 内容

这个包内包含了各种装备的信息以及装备生成工厂

#### 2.5.2 各个类的关系

1.`Equipment`是一个抽象类,游戏中存在的5种装备都继承自这个类并且实现了这个类中的抽象方法

2.`EquipmentFactory`是装备对象的生成工厂

#### 2.5.3 `Equipment`抽象类的设计

这个类中有四个较为重要的方法

`draw()`绘制装备,属于各个装备的共有方法

`dispose()`擦除装备,属于各个装备的共有方法

`takeEffect()`装备生效,属于具体装备的方法

`giveUpTakeEffect()`装备移除生效,属于具体装备的方法

因此后两者被设计成抽象方法,`Equipment`被设计成抽象类

与`Creature`不同的是，`Equipment`的每个装备都需要重写`takeEffect()`和`giveUpTakeEffect()`方法,所以将其设计为抽象类

`Creature`类的子类只有部分会重写`update()``QAction()`等方法,所以Creature没有被设计为抽象类

#### 2.5.4 `Equipment`子类设计

针对每个`Equipment`子类,我们重写`takeEffect()`和`giveUpTakeEffect()`方法

在装备生效和装备失效时调用它们



#### 2.5.5 `EquipmentFactory`装备工厂设计

`EquipmentFactory`与`CreatureFactory`的设计原则是一样的

基于实现包内的装备与外部完全分离,将装备的产生完全封装在`EquipmentFactory`中

同是`hasNext()`和`next()`方法,其使用方式并无多大区别

内部实现与思想略有区别

`EquipmentFactory`的`hasNext()`是综合基于对窗口中装备上限显示以及装备显示间隔的考量

`CreatureFactory`的`hasNext()`是因为阵营的生物个数是恒定的

### 2.6 包`com.sjq.gourd.constant`

#### 2.6.1 内容

这个包中包含了游戏过程中各种常量信息,以及很多静态数据

1.各种长度信息,窗口大小信息等

2.各种时间信息,帧时间信息,`ai`方向锁定时间信息等

3.各种常量参数,胜败状态，方向状态，爪痕状态等

4.各种生物及装备的`ID`

5.静态加载的图片信息

## 3 游戏测试

### 3.1 单元测试

### 3.2 胜率测试

#### 3.2.1 预期目标

由于葫芦娃阵营的生物较多，人为操控技能释放难度高于妖精阵营，故而预期要达到纯`ai`操作胜率六四开，葫芦娃6，妖精4

#### 3.2.2 游戏测试数据

当前数据下共进行了511局,妖精胜利213局,葫芦娃胜利298局,双方都可以使用技能，都可以拾取装备,完全`ai`控制

妖精胜率41.68%,葫芦娃胜率58.32%，几乎符合预期

#### 3.2.3 游戏测试文件

游戏测试文件在`jlog`文件夹下，此数据是在单机情况下测试的

详细测试代码请看`github`地址 https://github.com/JansonSong/gourd_vs_monster/tree/dev/gourd_vs_monster/src/main/java/com/sjq/gourd/localtest

## 4 开发过程

### 4.1 需求分析与策划

耗时约`1day`

### 4.2 模块划分与任务分配

耗时约`1day`

### 4.3 单机版实现

耗时约`10days` 详见 https://github.com/JansonSong/gourd_vs_monster/tree/dev

### 4.4 联机同步实现

耗时约`8days` 详见 https://github.com/JansonSong/gourd_vs_monster/tree/online

### 4.5 功能和文档完善

耗时约`3days`



## 5 写给玩家

### 5.1 玩法说明

游戏分为两个阵营,葫芦娃阵营和妖精阵营，葫芦娃阵营有9个生物，妖精阵营有6个生物

玩家每个时刻最多只能操控唯一的生物,其他生物由`ai`控制

当玩家为**葫芦娃阵营**时，可通过主键盘数字键1~9进行对操控目标的选取

当玩家为**妖精阵营**时,可通过主键盘数字键1~6进行对操控目标的选取

操控生物选取完毕后,屏幕侧面会显示你当前操控生物的各项信息,你可以通过`WSAD`键进行上下左右的移动

你可以通过鼠标左键点击敌方目标进行攻击,一旦选取敌方目标,当前操控生物就会自动对选定的敌方目标进行攻击(无须反复点击,只要在攻击范围内就会自动攻击),并且此时敌方目标的信息会被显示在侧边栏

一旦你通过数字键更换了当前操控生物,此生物就会自动由`ai`接管

**葫芦娃阵营**除了穿山甲之外都有且仅有一个技能Q

**妖精阵营**蛇精和蝎子精都有`QER`三个技能，其他小妖精只有R技能



**葫芦娃阵营** 大娃，二娃，三娃，四娃，五娃，六娃，七娃，爷爷，穿山甲

**妖精阵营** 蛇精，蝎子精，蜈蚣精，蝙蝠精，鳄鱼精，蛤蟆精



两个玩家，双方各自控制一方，使用主键盘数字键选择控制生物，对于每个玩家而言，己方阵营的每个生物有着唯一的控制编号



### 5.2 葫芦娃阵营详细数据

| 控制编号 | 名称   | 攻击类型 | 攻击效果 | 基础生命 | 基础魔法 | 基础攻击 | 基础防御 | 基础攻速 | 基础移速 | 攻击范围 | 技能及其描述                                                 |
| -------- | ------ | -------- | -------- | -------- | -------- | -------- | -------- | -------- | -------- | -------- | ------------------------------------------------------------ |
| 1        | 大娃   | 近战     | 爪痕1    | 3500     | 100      | 120      | 40       | 0.5      | 10       | 80       | 技能Q(能屈能伸)：消耗所有蓝量,变成原来的一半大小,增加移速5,降低攻击力20,攻击范围增加100，持续五秒,该技能五秒内最多使用一次 |
| 2        | 二娃   | 远程     | 橙色子弹 | 3000     | 150      | 80       | 35       | 0.5      | 10       | 400      | 技能Q(振奋人心):消耗所有蓝量,给2.0*当前攻击范围的范围内的所有友军增加时长五秒的振奋buff,该技能20秒内最多使用一次 |
| 3        | 三娃   | 近战     | 爪痕1    | 4500     | 100      | 150      | 60       | 0.4      | 10       | 80       | 技能Q(无敌金身):消耗所有蓝量,大幅度提高攻击力(50)和防御力(100)，移速减少3,若移速不足3,减少到0,持续五秒,该技能五秒内最多使用一次 |
| 4        | 四娃   | 远程     | 红色子弹 | 3000     | 150      | 80       | 40       | 0.5      | 12       | 400      | 技能Q(火焰之神的降临):消耗所有的蓝量,给1.5*当前射程范围内的所有敌军发射一颗带有"火焰之子"特效的子弹,当该子弹命中敌人之后,造成瞬间伤害与四娃普通子弹伤害相同，并给目标附加3秒钟的灼烧效果,该技能没有CD限制,蓝满即可释放 |
| 5        | 五娃   | 远程     | 蓝色子弹 | 3000     | 150      | 80       | 40       | 0.5      | 12       | 400      | 技能Q(冰霜之神的降临):消耗所有的蓝量,给1.5*当前射程范围内的所有敌军发射一颗带有"冰霜之心"特效的子弹,当该子弹命中敌人之后,造成瞬间伤害与五娃普通子弹伤害相同，并给目标附加2秒钟的冰冻效果,该技能没有CD限制,蓝满即可释放 |
|          | 六娃   | 近战     | 爪痕1    | 3500     | 100      | 150      | 20       | 1.0      | 12       | 90.0     | 技能Q(你看不见我):消耗所有蓝量,隐身,移速提升5，攻击力降低20,防御力提升100,该技能持续5秒,5秒内最多使用一次 |
|          | 七娃   | 远程     | 紫色子弹 | 2500     | 100      | 80       | 20       | 0.9      | 15       | 600      | 技能Q(看我法宝):5秒内提升基础攻速的300%，攻击范围提升400,5秒内最多使用一次 |
| 8        | 爷爷   | 远程     | 绿色子弹 | 3000     | 200      | 75       | 10       | 0.5      | 12       | 300      | 被动(山神的庇佑)：爷爷不会以妖精为攻击目标,只会以葫芦娃为治疗目标,每颗普通子弹都带有“治愈之神”的特效,每颗子弹的治疗值为爷爷的攻击力<br />技能Q(山神的祝福):爷爷对1.5*当前射程范围内的所有友军发射一颗带有“超大号治愈之神”特效的子弹,命中时对目标治疗1000生命值,并使得目标获得时长5秒的治愈buff |
| 9        | 穿山甲 | 近战     | 爪痕1    | 3000     | 100      | 75       | 60       | 0.5      | 12       | 100.0    | 没有技能                                                     |

### 5.3 妖精阵营详细数据

| 控制编号 | 名称   | 攻击类型 | 攻击效果 | 基础生命 | 基础魔法 | 基础攻击 | 基础防御 | 基础攻速 | 基础移速 | 攻击范围 | 技能及其描述                                                 |
| -------- | ------ | -------- | -------- | -------- | -------- | -------- | -------- | -------- | -------- | -------- | ------------------------------------------------------------ |
| 1        | 蛇精   | 远程     | 黑色子弹 | 5000     | 200      | 120      | 30       | 0.7      | 15       | 500      | Q技能(剧毒之牙):消耗50%最大蓝量,向2.0*攻击范围内的所有敌方放射一颗带有“剧毒之牙”特效的子弹,命中时对目标造成等同蛇精普通子弹的伤害,并附加“重伤”效果，该技能每五秒只能使用一次<br />E技能(复活之术):消耗100%最大蓝量,如果己方有妖精死掉了,就复活一只妖精,复活优先级：蝎子精>蜈蚣精>蝙蝠精>鳄鱼精>蛤蟆精。同样死掉的情况下,复活优先级高的,由该技能复活的妖精蓝量为0，蝎子精血量为满血的一半,其他妖精血量为满血,妖精复活位置为其上一次死掉的位置。如果没有妖精死亡,则视为空技能,只扣蓝量,没有效果，该技能每五秒只能使用一次<br />R技能(金身护罩)：消耗50%最大蓝量,瞬间加600血量,在五秒内防御力提升30,该技能每五秒只能使用一次 |
| 2        | 蝎子精 | 近战     | 爪痕3    | 7500     | 150      | 150      | 55       | 0.5      | 10       | 100      | Q技能(致残之爪):消耗100%最大蓝量,对5.0*当前近战攻击范围内的所有敌方进行致残爪击，并在爪击命中时给目标附加“残废”效果,效果持续5秒,该技能五秒内只能使用一次<br />E技能(狂暴之心):消耗50%最大蓝量,在五秒内,移速增加10,防御增加20，攻击增加30，攻速增加100%基础攻速,攻击范围增加80，血量增加600,该技能每五秒只能使用一次<br />R技能(同命)：与消耗100%最大蓝量,在效果持续的10秒内,与敌方血量最低的固定(不会实时变动)三个生物绑定,每当蝎子精掉血时,绑定生物掉相同血量,该技能每10秒只能使用一次 |
| 3        | 蜈蚣精 | 近战     | 爪痕4    | 3500     | 100      | 75       | 30       | 0.5      | 8        | 80       | R技能(复活吧!女王大人)：触发条件1.蛇精死掉了 2.四个小怪全部活着 3.四个小怪蓝量都是满的 4.按下R键<br />效果:四个小怪消耗全部的当前血量和全部的所有蓝量,死亡后复活蛇精,蛇精复活时满血满蓝 |
| 4        | 蝙蝠精 | 远程     | 褐色子弹 | 3500     | 100      | 70       | 15       | 1.0      | 18       | 600      | 同上                                                         |
| 5        | 鳄鱼精 | 近战     | 爪痕2    | 3500     | 100      | 100      | 50       | 0.5      | 8        | 80       | 同上                                                         |
| 6        | 蛤蟆精 | 远程     | 黄色子弹 | 3500     | 100      | 75       | 40       | 0.5      | 10       | 500      | 同上                                                         |

### 5.4 装备详细数据

| 装备名称   | 装备效果                                                     | 装备备注 |
| ---------- | ------------------------------------------------------------ | -------- |
| 玉如意     | 所有生物都可以拾取,所有生物都可以使用<br />对葫芦娃而言是增加8移速,增加20攻击力<br />对于妖精而言是增加20防御,并瞬间恢复1000生命值 |          |
| 玉簪       | 所有生物都可以拾取,但只有蛇精才可以使用<br />蛇精拾取瞬间恢复1500血量，增加5移速，增加200%基础攻速,增加攻击力20,增加防御力20<br />其他生物可以拾取，但没有作用 |          |
| 魔镜       | 对于任何远程攻击生物,都可拾取并装备,增加远程攻击射程200<br />对葫芦娃方生物有额外效果,可在拾取瞬间恢复500生命值<br />近战生物可拾取但无装备效果 |          |
| 刚柔阴阳剑 | 任何生物都可以拾取并装备，增加基础攻速100%,增加攻击力20      |          |
| 百宝锦囊   | 任何生物都可拾取，但无法装备,效果是拾取瞬间恢复500生命值     |          |

