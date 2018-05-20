# MyPerf4J
一个极快的Java接口性能监控和统计工具。受[perf4j](https://github.com/perf4j/perf4j)和[TProfiler](https://github.com/alibaba/TProfiler)启发而来。
致力于成为可在生产环境长时间使用的性能监控和统计工具！

## 多语言文档
* English [README.md](https://github.com/ThinkpadNC5/MyPerf4J/blob/develop/README.md)
* 中文 

## 背景
* 我需要一个能统计接口响应时间的程序
* [perf4j](https://github.com/perf4j/perf4j)现有的统计结果不能满足我的需求

## 需求
* 能统计出接口的RPS、Avg、Min、Max、StdDev、TP90、TP95、TP99、TP999等性能指标
* 可以通过注解进行配置，注解可以配置到类和/或方法上
* 尽量不占用过多的内存、不影响程序的正常响应
* 性能指标的处理可以定制化，例如：日志收集、上报给日志收集服务等

## 设计
* 通过把所有的响应时间记录下来，便可以进行所有可能的分析，包括RPS、TP99等，那么如何高效的存储这些数据呢？
    - 采用K-V的形式即可，K为响应时间，V为对应的次数，这样内存的占用只和不同响应时间的个数有关，而和总请求次数无关
    - 如果单纯的使用Map来存储数据，必然会占用大量不必要的内存
    - 根据二八定律，绝大多数的接口响应时间在很小的范围内，这个很小的范围特别适合使用数组来存储，数组下标即为响应时间，对应元素即为该响应时间对应的数量；而少数的接口响应时间分布的范围则会比较大，适合用Map来存储；
    - 综上所述，核心数据结构为：数组+Map，将小于某一阈值的响应时间记录在数组中，将大于等于该阈值的响应时间记录到Map中
* 利用AOP进行响应时间的采集，利用[ASM](http://asm.ow2.io/)实现AOP，提高性能
* 通过注解/配置文件的方式进行配置，并可以通过参数配置调优核心数据结构的内存占用
* 通过同步的方式采集响应时间，避免创建过多的Runnable对象影响程序的GC
* 通过异步的方式处理采集结果，避免影响接口的响应时间
* 把处理采集的结果的接口暴露出来，方便进行自定义的处理

## 内存
* 前提条件
    - 服务上有1024个需要监控的接口
    - 每个接口的绝大部分响应时间在300ms以内，并且有100个不相同的大于300ms的响应时间
    - 不开启指针压缩
    - 非核心数据结构占用2MB
* rough模式
    - 只记录响应时间小于1000ms的请求
    - 2 * 1024 * (1000 * 4B) + 2MB ≈ 10MB
* accurate模式
    - 记录所有的响应时间
    - 2 * 1024 * (300 * 4B + 100 * 90B) + 2MB ≈ 22MB 

## 压测
* 配置说明
    - 操作系统 macOS High Sierra 10.13.3
    - JDK 1.8.0_161
    - JVM参数 -server -Xmx4G -Xms4G -Xmn2G
    - 机器配置 
        - CPU Intel(R) Core(TM) i7-7920HQ CPU@3.10GHz
        - RAM 16GB 2133MHz LPDDR3

* 测试方法
    - 对空方法进行压测 
    - 为了避免由于空方法执行过快导致多个线程高度竞争资源（竞争AbstractRecorder中的AtomicIntegerArray）进而导致压测结果出现明显的性能下降，通过轮询的方式执行8个空方法，然后把8个方法的RPS相加得出结果
    - 时间片为10s，每次压测中间停顿20s，并且执行`System.gc();`

* MyPerf4J-ASM

| 线程数 | 每线程循环数| RPS |
|-------|-----|------|
|1|500000000|11767056|
|2|500000000|14972208|
|4|500000000|28561824|
|8|500000000|45966888|

* 压测结论
    - 从压测结果来看：
        - MyPerf4J-ASM在单线程下每秒可支持1176万次的方法调用，平均每次方法调用耗时84ns，能够满足绝大部分人的要求，不会对程序本身的响应时间造成影响
    - 高性能原因：
        - MyPerf4J-ASM是通过ASM框架修改类的字节码，在方法前后插入两行方法，不产生多余的对象，在整个压测过程中不会触发任何的GC（除了代码中执行的`System.gc();`）

## 使用
    
* 在JVM启动参数里加上： -javaagent:/your/path/to/MyPerf4J-ASM-${MyPerf4J-version}.jar

* 在JVM启动参数里加上：-DMyPerf4JPropFile=/your/path/to/myPerf4J.properties，并在`/your/path/to/myPerf4J.properties`中加入以下几个配置项：

```
#配置PerfStatsProcessor，可不配置
PerfStatsProcessor=cn.perf4j.demo.MyPerfStatsProcessor

#配置Record模式，可配置为accurate/rough
RecorderMode=accurate

#配置时间片，单位为ms，最小30s，最大600s
MillTimeSlice=30000

#需要监控的package，可配置多个，用英文';'分隔
IncludePackages=cn.perf4j;org.myperf4j

#不需要监控的package，可配置多个，用英文';'分隔
ExcludePackages=org.spring;

#是否开启debug日志，可配置为true/false
Debug.PrintDebugLog=true

#可配置为byPorfiler/byPackage
ProfilingType=byProfiler

#可配置多个方法名，用英文';'分隔
ExcludeMethods=equals;hash

#是否排除私有方法，true/false
ExcludePrivateMethod=true

#可配置多个ClassLoader，用英文';'分隔
ExcludeClassLoaders=
```

* 在`pom.xml`文件中增加依赖
    
    ```
    <dependencies>
        <dependency>
            <groupId>MyPerf4J</groupId>
            <artifactId>MyPerf4J-Base</artifactId>
            <version>${MyPerf4J-version}</version>
        </dependency>
    </dependencies>
    ```

* 当配置`ProfilingType=byProfiler`时，在你想要分析性能的类或方法明上加上 `@Profiler`注解，同时对于不想进行性能分析的方法上加上 `@NonProfiler`；当配置`ProfilingType=byPackage`时，MyPerf4J会对`IncludePackages`指定package里排除`ExcludePackages`指定package的所有类进行性能分析。

```
package cn.perf4j.demo;

import cn.myperf4j.base.annotation.NonProfiler;
import cn.myperf4j.base.annotation.Profiler;

import java.util.concurrent.TimeUnit;

/**
 * Created by LinShunkang on 2018/4/7
 */
@Profiler(mostTimeThreshold = 2000, outThresholdCount = 200)
public class UserServiceImpl implements UserService {

    private long f1;

    @Override
    @Profiler(mostTimeThreshold = 3000, outThresholdCount = 300)
    public long getId1(long id) throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(4);
        return id + 100;
    }

    private long privateGetId1() {
        return 1L;
    }

    @Override
    public long getId2(long id) {
        return id + 2;
    }

    @Override
    @NonProfiler
    public long getId3(long id) {
        return 0;
    }

    public long getF1() {
        return f1;
    }

    public void setF1(long f1) {
        this.f1 = f1;
    }
}
```

* 如果需要对性能统计结果进行自定义处理，可以新建一个声明PerfStatsProcessor接口的类

``` 
package cn.perf4j.demo;


import cn.myperf4j.base.PerfStats;
import cn.myperf4j.core.util.PerfStatsFormatter;
import cn.myperf4j.base.PerfStatsProcessor;

import java.util.List;

/**
 * Created by LinShunkang on 2018/4/9
 */
public class MyPerfStatsProcessor implements PerfStatsProcessor {

    @Override
    public void process(List<PerfStats> perfStatsList, long startMillis, long stopMillis) {
        //You can do anything you want to do :)
        System.out.println(PerfStatsFormatter.getFormatStr(perfStatsList, startMillis, stopMillis));
    }

}
```

* 执行命令 `mvn clean package`

* 运行你的程序

* 输出结果

```
MyPerf4J Performance Statistics [2018-05-20 12:33:00, 2018-05-20 12:33:30]
Api                         RPS  Avg(ms)  Min(ms)  Max(ms)   StdDev     Count     TP50     TP90     TP95     TP99    TP999   TP9999  TP99999    TP100
UserServiceImpl.getId1       47     4.47        4        7     0.07      1475        4        5        7        7        7        7        7        7
UserServiceImpl.getId2       47     0.00        0        0     0.00      1475        0        0        0        0        0        0        0        0
```

## 关于Rough模式与Accurate模式
* Rough模式
    - 精度略差，会丢弃响应时间超过指定阈值的记录
    - 更加节省内存，只使用数组来记录响应时间
    - 速度略快一些
    - 默认

* Accurate模式
    - 精度高，会记录所有的响应时间
    - 相对耗费内存，使用数组+Map来记录响应时间
    - 速度略慢一些
    - 需要在`/your/path/to/myPerf4J.properties`加入配置：RecorderMode=accurate 

* 建议
    - 对于内存敏感或精度要求不是特别高的应用，推荐使用Rough模式
    - 对于内存不敏感且精度要求特别高的应用，推荐使用Accurate模式

## 关于MyPerf4J-ASM
* MyPerf4J-ASM
    * 利用[ASM](http://asm.ow2.io/)来实现切面的织入
    * 速度极快！
    * 在每次记录时间时不产生任何对象，只有在计算性能指标时会产生少量的临时对象，基本不影响程序本身的GC


