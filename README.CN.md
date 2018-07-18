# MyPerf4J
一个极快的Java接口性能监控和统计工具。受[perf4j](https://github.com/perf4j/perf4j)和[TProfiler](https://github.com/alibaba/TProfiler)启发而来。
致力于成为可在生产环境长时间使用的性能监控和统计工具！

## 多语言文档
* English [README](https://github.com/ThinkpadNC5/MyPerf4J/blob/develop/README.md) [WIKI](https://github.com/ThinkpadNC5/MyPerf4J/wiki/English-Doc)
* 中文  [WIKI](https://github.com/ThinkpadNC5/MyPerf4J/wiki/Chinese-Doc)

## 背景
* 我需要一个能统计接口响应时间的程序
* [perf4j](https://github.com/perf4j/perf4j)现有的统计结果不能满足我的需求

## 需求
* 能统计出接口的RPS、Avg、Min、Max、StdDev、TP90、TP95、TP99、TP999等性能指标
* 可配置，可以指定统计某些类、某些方法，也可以指定不统计某些类、某些方法
* 不占用过多的内存、不影响程序的正常响应
* 性能指标的处理可以定制化，例如：日志收集、上报给日志收集服务等

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
    |1|1000000000|13815816|
    |2|1000000000|16199712|
    |4|1000000000|33060632|
    |8|1000000000|55981416|

* 压测结论
    - 从压测结果来看：
        - MyPerf4J-ASM在单线程下每秒可支持138万次的方法调用！平均每次方法调用耗时72.3ns！！！能够满足绝大部分人的要求，不会对程序本身的响应时间造成影响！
    - 高性能原因：
        - MyPerf4J-ASM是通过ASM框架修改类的字节码，在方法前后插入两行方法，不产生多余的对象，在整个压测过程中不会触发任何的GC（除了代码中执行的`System.gc();`）！！！

## 使用
    
* 在JVM启动参数里加上： -javaagent:/your/path/to/MyPerf4J-ASM-${MyPerf4J-version}.jar

* 在JVM启动参数里加上：-DMyPerf4JPropFile=/your/path/to/myPerf4J.properties，并在`/your/path/to/myPerf4J.properties`中加入以下几个配置项：

    ```
    #配置PerfStatsProcessor，可不配置，用于自定义统计数据的处理
    #PerfStatsProcessor=cn.perf4j.demo.MyPerfStatsProcessor
    
    #配置备份Recorders的数量，默认为1，最小为1，最大为8，当需要在较小MillTimeSlice内统计大量方法性能数据时可配置大一些
    BackupRecordersCount=1
    
    #配置Record模式，可配置为accurate/rough
    RecorderMode=accurate
    
    #配置时间片，单位为ms，最小1s，最大600s
    MillTimeSlice=10000
    
    #需要监控的package，可配置多个，用英文';'分隔
    IncludePackages=cn.perf4j.demo
    
    #不需要监控的package，可配置多个，用英文';'分隔
    ExcludePackages=org.spring;
    
    #可配置多个方法名，用英文';'分隔
    ExcludeMethods=equals;hash
    
    #是否排除私有方法，true/false
    ExcludePrivateMethod=true
    
    #通用的方法执行时间阈值，单位为ms
    ProfilingTimeThreshold=1000
    
    #在一个时间片内，超过方法执行时间阈值的次数，仅在RecorderMode=accurate时有效
    ProfilingOutThresholdCount=10
    ```

* 执行命令 `mvn clean package`

* 运行你的程序

* 输出结果
    
    ```
    MyPerf4J Performance Statistics [2018-07-01 23:40:23, 2018-07-01 23:40:24]
    Api[2/3]                    RPS  Avg(ms)  Min(ms)  Max(ms)   StdDev     Count     TP50     TP90     TP95     TP99    TP999   TP9999  TP99999    TP100
    DemoServiceImpl.getId1  7454181     0.00        0        0     0.00   7454181        0        0        0        0        0        0        0        0
    DemoServiceImpl.getId2  7454180     0.00        0        0     0.00   7454180        0        0        0        0        0        0        0        0
    ```
    
    
## 可视化性能指标
* 目前MyPerf4J已提供[Grafana Dashboard](https://grafana.com/dashboards/6991)进行数据展示
![Markdown](https://raw.githubusercontent.com/ThinkpadNC5/Pictures/master/MyPerf4J_Grafana.jpeg)

## 更多信息
想更深入的了解MyPerf4J？请看[https://github.com/ThinkpadNC5/MyPerf4J/wiki/Chinese-Doc](https://github.com/ThinkpadNC5/MyPerf4J/wiki/Chinese-Doc)。