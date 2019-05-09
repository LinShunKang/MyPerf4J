简体中文 | [English](./README.EN.md)

<h1 align="center">MyPerf4J</h1>

<div align="center">

一个针对高并发、低延迟应用设计的高性能 Java 性能监控和统计工具。

[![GitHub (pre-)release](https://img.shields.io/github/release/LinShunKang/MyPerf4J/all.svg)](https://github.com/LinShunKang/MyPerf4J) [![Build Status](https://travis-ci.com/LinShunKang/MyPerf4J.svg?branch=develop)](https://travis-ci.com/LinShunKang/MyPerf4J) [![Coverage Status](https://coveralls.io/repos/github/LinShunKang/MyPerf4J/badge.svg?branch=develop)](https://coveralls.io/github/LinShunKang/MyPerf4J?branch=develop) [![GitHub issues](https://img.shields.io/github/issues/LinShunKang/MyPerf4J.svg)](https://github.com/LinShunKang/MyPerf4J/issues) [![GitHub closed issues](https://img.shields.io/github/issues-closed/LinShunKang/MyPerf4J.svg)](https://github.com/LinShunKang/MyPerf4J/issues?q=is%3Aissue+is%3Aclosed) [![GitHub](https://img.shields.io/github/license/LinShunKang/MyPerf4J.svg)](./LICENSE)

</div>

## 特性
* 高性能: 单线程支持每秒 **1000 万次** 响应时间的记录，每次记录只花费 **73 纳秒**
* 无侵入: 采用 **JavaAgent** 方式，对应用程序完全无侵入，无需修改应用代码
* 低内存: 采用内存复用的方式，整个生命周期只产生极少的临时对象，不影响应用程序的 GC
* 高精度: 采用纳秒来计算响应时间
* 高实时: 支持秒级监控，最低 **1 秒**

## 使用场景
* 在**开发环境**中快速定位 Java 应用程序的性能瓶颈
* 在**生产环境**中长期监控 Java 应用程序的性能指标

## 文档
* [English Doc](https://github.com/LinShunKang/MyPerf4J/wiki/English-Doc)
* [中文文档](https://github.com/LinShunKang/MyPerf4J/wiki/Chinese-Doc)    
    
## 监控指标
MyPerf4J 为每个应用收集数十个监控指标，所有的监控指标都是实时采集和展现的。

下面是 MyPerf4J 目前支持的监控指标列表:
- **[Method Metrics](https://grafana.com/dashboards/7766)**<br/>
[RPS，Count，Avg，Min，Max，StdDev，TP50, TP90, TP95, TP99, TP999, TP9999, TP99999, TP100](https://github.com/LinShunKang/MyPerf4J/wiki/%E6%8C%87%E6%A0%87#method-metrics)
![Markdown](https://raw.githubusercontent.com/LinShunKang/Objects/master/MyPerf4J-InfluxDB-Method_Show_Operation.gif)

- **[JVM Metrics](https://grafana.com/dashboards/8787)**<br/>
[Thread](https://github.com/LinShunKang/MyPerf4J/wiki/%E6%8C%87%E6%A0%87#jvm-thread-metrics)，[Memory](https://github.com/LinShunKang/MyPerf4J/wiki/%E6%8C%87%E6%A0%87#jvm-memory-metrics)，[ByteBuff](https://github.com/LinShunKang/MyPerf4J/wiki/%E6%8C%87%E6%A0%87#jvm-bytebuff-metrics)，[GC](https://github.com/LinShunKang/MyPerf4J/wiki/%E6%8C%87%E6%A0%87#jvm-gc-metrics)，[Class](https://github.com/LinShunKang/MyPerf4J/wiki/%E6%8C%87%E6%A0%87#jvm-class-metrics)
![Markdown](https://raw.githubusercontent.com/LinShunKang/Objects/master/MyPerf4J_JVM_Compressed.jpeg)

    > 想知道如何实现上述效果？请先按照[快速启动](https://github.com/LinShunKang/MyPerf4J#%E5%BF%AB%E9%80%9F%E5%90%AF%E5%8A%A8)的描述启动应用，再按照[这里](https://github.com/LinShunKang/MyPerf4J/wiki/InfluxDB_)的描述进行安装配置即可。

## 快速启动
MyPerf4J 采用 JavaAgent 配置方式，**透明化**接入应用，对应用代码完全**没有侵入**。

### 打包
* git clone git@github.com:LinShunKang/MyPerf4J.git
* mvn clean package
* 把 MyPerf4J-ASM-${MyPerf4J-version}.jar 重命名为 MyPerf4J-ASM.jar

> 可以尝试直接下载 [MyPerf4J-ASM.jar](https://github.com/LinShunKang/Objects/blob/master/MyPerf4J-ASM-2.5.0.jar?raw=true)

### 配置
在 JVM 启动参数里加上以下两个参数
* -javaagent:/your/path/to/MyPerf4J-ASM.jar
* -DMyPerf4JPropFile=/your/path/to/MyPerf4J.properties

> 形如：java -javaagent:/your/path/to/MyPerf4J-ASM.jar -DMyPerf4JPropFile=/your/path/to/MyPerf4J.properties -jar yourJar.jar

> 注意：使用 Windows 的同学，请注意修改路径格式，包括 `MyPerf4JPropFile` 中的文件路径 

其中，`MyPerf4JPropFile`的配置如下:

 ```
#应用名称
AppName=YourApplicationName

#MetricsProcessor类型，0:以标准格式化结构输出到stdout.log 1:以标准格式化结构输出到磁盘  2:以InfluxDB LineProtocol格式输出到磁盘
MetricsProcessorType=1

#配置各个Metrics日志的文件路径，可不配置
MethodMetricsFile=/your/path/to/log/method_metrics.log
ClassMetricsFile=/your/path/to/log/class_metrics.log
GCMetricsFile=/your/path/to/log/gc_metrics.log
MemMetricsFile=/your/path/to/log/memory_metrics.log
BufPoolMetricsFile=/your/path/to/log/buf_pool_metrics
ThreadMetricsFile=/your/path/to/log/thread_metrics.log

#配置Record模式，可配置为accurate/rough
RecorderMode=accurate
    
#配置时间片，单位为ms，最小1s，最大600s
MilliTimeSlice=10000
    
#需要监控的package，可配置多个，用英文';'分隔
IncludePackages=your.package.to.monitor;cn.perf4j.demo;cn.perf4j.demo1.[p1,p2,p3];cn.*.demo.*

#是否展示方法参数类型
ShowMethodParams=true
 ```
    
> 查看[配置文件模板](https://raw.githubusercontent.com/LinShunKang/Objects/master/jars/MyPerf4J.properties)。想了解更多的配置？请看[这里](https://github.com/LinShunKang/MyPerf4J/wiki/%E9%85%8D%E7%BD%AE)

> 注意：需要修改 `AppName`、`IncludePackages` 和 `xxxMetricsFile`

### 运行
* 输出结果，输出到 /your/path/to/log/method_metrics.log:

    ```
    MyPerf4J Method Metrics [2019-03-03 17:27:50, 2019-03-03 17:28:00]
    Method[5]                              Type      RPS  Avg(ms)  Min(ms)  Max(ms)   StdDev     Count     TP50     TP90     TP95     TP99    TP999   TP9999  TP99999    TP100
    DemoServiceImpl.getId1(long)        General    51317     0.00        0        1     0.00    513178        0        1        1        1        1        1        1        1
    DemoServiceImpl.getId2(long)        General   168637     0.00        0        4     0.00   1686377        0        1        2        3        4        4        4        4
    DemoServiceImplV2.getId1(long)      General      357     0.00        0        0     0.00      3570        0        0        0        0        0        0        0        0
    DemoServiceImplV2.getId3(long)      General      713     0.51        0        5     0.08      7138        0        1        2        3        4        5        5        5
    Dao.doQuery()                  DynamicProxy     1394     0.51        0        5     0.05     13944        0        1        2        3        4        5        5        5
    ```

### 卸载
在 JVM 启动参数中去掉以下两个参数，重启即可卸载此工具。
* -javaagent:/your/path/to/MyPerf4J-ASM.jar
* -DMyPerf4JPropFile=/your/path/to/MyPerf4J.properties

## 问题
如果您有任何问题、疑问或者建议，请您毫不犹豫的 [提交Issue](https://github.com/LinShunKang/MyPerf4J/issues/new/choose) 或者 [发送邮件](mailto:linshunkang.chn@gmail.com) : )

## 已知用户
如果您在使用 MyPerf4J，请告诉我，您的使用对我来说非常重要：[https://github.com/LinShunKang/MyPerf4J/issues/30](https://github.com/LinShunKang/MyPerf4J/issues/30) （按登记顺序排列）

<div align="center">
<img src="https://github.com/LinShunKang/Objects/blob/master/logos/Boss_300x300.png?raw=true" width="80" height="80"/>
&nbsp;&nbsp;&nbsp;
<img src="https://github.com/LinShunKang/Objects/blob/master/logos/Lever.jpeg?raw=true" width="240" height="80"/>
&nbsp;&nbsp;&nbsp;
<img src="https://github.com/LinShunKang/Objects/blob/master/logos/dianzhang_303x303.jpeg?raw=true" width="80" height="80"/>
</div>

## 参考项目
MyPerf4J 是受以下项目启发而来：
* [Perf4J](https://github.com/perf4j/perf4j)
* [TProfiler](https://github.com/alibaba/TProfiler)

## 更多信息
想更深入的了解 MyPerf4J？请看 [https://github.com/LinShunKang/MyPerf4J/wiki/Chinese-Doc](https://github.com/LinShunKang/MyPerf4J/wiki/Chinese-Doc)。