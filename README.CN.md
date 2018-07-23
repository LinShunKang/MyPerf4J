# MyPerf4J
> 一个针对高并发、低延迟应用设计的简单、快速且无侵入的Java方法性能监控和统计工具。
受 [perf4j](https://github.com/perf4j/perf4j) 和 [TProfiler](https://github.com/alibaba/TProfiler)启发而来。

MyPerf4J具有以下几个特性：
* 无侵入: 采用JavaAgent方式，对应用程序完全无侵入，无需修改应用代码
* 高性能: 性能消耗非常小，每次统计只花费73ns，可以在生产环境长期使用
* 低内存: 采用内存复用的方式，整个生命周期只产生极少的临时对象，不影响应用程序的GC
* 高精度: 采用纳秒来计算响应时间
* 高实时: 支持秒级监控，最低1s!

## 文档
* English [README](https://github.com/ThinkpadNC5/MyPerf4J/blob/develop/README.md) [Document](https://github.com/ThinkpadNC5/MyPerf4J/wiki/English-Doc)
*  [中文文档](https://github.com/ThinkpadNC5/MyPerf4J/wiki/Chinese-Doc)    
    
## 可视化
* 目前MyPerf4J已提供[Grafana Dashboard](https://grafana.com/dashboards/6991)进行数据展示
![Markdown](https://raw.githubusercontent.com/ThinkpadNC5/Pictures/master/Monitor_Page_V3_1_200FPS.gif)
![Markdown](https://raw.githubusercontent.com/ThinkpadNC5/Pictures/master/Monitor_Page_V4_200FPS.gif)

    > 想知道如何实现上述效果？请先按照[快速启动](https://github.com/ThinkpadNC5/MyPerf4J#%E5%BF%AB%E9%80%9F%E5%90%AF%E5%8A%A8)的描述启动应用，再按照[这里](https://github.com/ThinkpadNC5/MyPerf4J/wiki/InfluxDB_)的描述进行安装配置即可。

## 快速启动
MyPerf4J采用JavaAgent配置方式，**透明化**接入应用，对应用代码完全**没有侵入**。

### 打包
* git clone git@github.com:ThinkpadNC5/MyPerf4J.git
* mvn clean package
* 把 MyPerf4J-ASM-${MyPerf4J-version}.jar 重命名为 MyPerf4J-ASM.jar

### 配置
在JVM启动参数里加上以下两个参数
> -javaagent:/your/path/to/MyPerf4J-ASM-${MyPerf4J-version}.jar

> -DMyPerf4JPropFile=/your/path/to/myPerf4J.properties

其中，`MyPerf4JPropFile`的配置如下:

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
        
> 想了解更多的配置？请看[这里](https://github.com/ThinkpadNC5/MyPerf4J/wiki/%E9%85%8D%E7%BD%AE)

### 运行
* 输出结果，默认输出到stdout.log:

    ```
    MyPerf4J Performance Statistics [2018-07-01 23:40:23, 2018-07-01 23:40:24]
    Api[2/3]                    RPS  Avg(ms)  Min(ms)  Max(ms)   StdDev     Count     TP50     TP90     TP95     TP99    TP999   TP9999  TP99999    TP100
    DemoServiceImpl.getId1  7454181     0.00        0        0     0.00   7454181        0        0        0        0        0        0        0        0
    DemoServiceImpl.getId2  7454180     0.00        0        0     0.00   7454180        0        0        0        0        0        0        0        0
    ```

    > 想**定制**你的输出结果？请看[这里](https://github.com/ThinkpadNC5/MyPerf4J/wiki/%E6%89%A9%E5%B1%95)

### 卸载
在JVM启动参数中去掉以下两个参数，重启即可卸载此工具。
> -javaagent:/your/path/to/MyPerf4J-ASM.jar

> -DMyPerf4JPropFile=/your/path/to/myPerf4J.properties

## 问题
> 如果您遇到任何问题或有疑问，请您毫不犹豫的[提交Issue](https://github.com/ThinkpadNC5/MyPerf4J/issues/new) : )

## 更多信息
> 想更深入的了解MyPerf4J？请看[https://github.com/ThinkpadNC5/MyPerf4J/wiki/Chinese-Doc](https://github.com/ThinkpadNC5/MyPerf4J/wiki/Chinese-Doc)。
