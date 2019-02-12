[简体中文](./README.md) | English

<h1 align="center">MyPerf4J</h1>

<div align="center">

A high performance, non-intrusive Java performance monitoring and statistical tool designed for high-concurrency, low-latency applications. 

[![GitHub (pre-)release](https://img.shields.io/github/release/LinShunKang/MyPerf4J/all.svg)](https://github.com/LinShunKang/MyPerf4J) [![Build Status](https://travis-ci.com/LinShunKang/MyPerf4J.svg?branch=develop)](https://travis-ci.com/LinShunKang/MyPerf4J) [![Coverage Status](https://coveralls.io/repos/github/LinShunKang/MyPerf4J/badge.svg?branch=develop)](https://coveralls.io/github/LinShunKang/MyPerf4J?branch=develop) [![GitHub issues](https://img.shields.io/github/issues/LinShunKang/MyPerf4J.svg)](https://github.com/LinShunKang/MyPerf4J/issues) [![GitHub closed issues](https://img.shields.io/github/issues-closed/LinShunKang/MyPerf4J.svg)](https://github.com/LinShunKang/MyPerf4J/issues?q=is%3Aissue+is%3Aclosed) [![GitHub](https://img.shields.io/github/license/LinShunKang/MyPerf4J.svg)](./LICENSE)

</div>

## Features
*  High performance: Very low performance consumption, only **73 nano seconds** per record, can be used in production environment for a long time.
*  No intrusion: Using **JavaAgent** mode, no intrusion to the application, no need to modify the application code.
*  Low memory: With memory multiplexing, only a small number of temporary objects are generated throughout the life cycle, and the GC of the application is not affected.
*  High precision: Using nanoseconds to calculate response time.
*  Real-time: Supports second level monitoring, minimum **1 second**

## Usage scenarios
* Quickly locate performance bottlenecks for Java applications in a development environment
* Long-term monitoring of performance metrics for Java applications in a production environment

## Multilingual document
* [English Doc](https://github.com/LinShunKang/MyPerf4J/wiki/English-Doc)
* [中文文档](https://github.com/LinShunKang/MyPerf4J/wiki/Chinese-Doc) 

## What does it monitor?
MyPerf4J collects dozens of metrics per application. All these metrics are collected and visualized in real-time.

This is a list of what it currently monitors:
* **[Method Metrics](https://grafana.com/dashboards/7766)**<br/>
[RPS, Count, Avg, Min, Max, StdDev, TP50, TP90, TP95, TP99, TP999, TP9999, TP99999, TP100](https://github.com/LinShunKang/MyPerf4J/wiki/Metrics#method-metrics)
![Markdown](https://raw.githubusercontent.com/LinShunKang/Objects/master/MyPerf4J-InfluxDB-Method_Show_Operation.gif)

- **[JVM Metrics](https://grafana.com/dashboards/8787)**<br/>
[Thread](https://github.com/LinShunKang/MyPerf4J/wiki/Metrics#jvm-thread-metrics)，[Memory](https://github.com/LinShunKang/MyPerf4J/wiki/Metrics#jvm-memory-metrics)，[ByteBuff](https://github.com/LinShunKang/MyPerf4J/wiki/Metrics#jvm-bytebuff-metrics)，[GC](https://github.com/LinShunKang/MyPerf4J/wiki/Metrics#jvm-gc-metrics)，[Class](https://github.com/LinShunKang/MyPerf4J/wiki/Metrics#jvm-class-metrics)
![Markdown](https://raw.githubusercontent.com/LinShunKang/Objects/master/MyPerf4J_JVM_Compressed.jpeg)
  
> Want to know how to achieve the above effect? Please start the application according to the description of [Quick Start](https://github.com/LinShunKang/MyPerf4J/blob/develop/README.EN.md#quick-start), and then follow the instructions in [here](https://github.com/LinShunKang/MyPerf4J/wiki/InfluxDB) to install and configure.
 
## Quick start
MyPerf4J adopts JavaAgent configuration mode, **transparent** access application, and the application code is completely **no-intrusive**.

### Build
* git clone git@github.com:LinShunKang/MyPerf4J.git
* mvn clean package
* Rename MyPerf4J-ASM-${MyPerf4J-version}.jar to MyPerf4J-ASM.jar

> If you are using JDK 7 or higher, you can try to download [MyPerf4J-ASM.jar](https://github.com/LinShunKang/Objects/blob/master/MyPerf4J-ASM-2.4.0.jar?raw=true) directly.

### Configure
Add the following two parameters to the JVM startup parameters
> -javaagent:/your/path/to/MyPerf4J-ASM.jar
> -DMyPerf4JPropFile=/your/path/to/myPerf4J.properties

Among them, the configuration of `MyPerf4JPropFile` is as follows:

```
#Application name
AppName=MyPerf4JTest

#Configure MetricsProcessors type 0:Output to stdout.log in a standard formatted structure 1:Output to disk in standard formatted structure  2:Output to disk in InfluxDB LineProtocol format
MetricsProcessorType=1

#Config metrics log file, option
MethodMetricsFile=/data/logs/MyPerf4J/method_metrics.log
ClassMetricsFile=/data/logs/MyPerf4J/class_metrics.log
GCMetricsFile=/data/logs/MyPerf4J/gc_metrics.log
MemMetricsFile=/data/logs/MyPerf4J/memory_metrics.log
BufPoolMetricsFile=/data/logs/MyPerf4J/buf_pool_metrics
ThreadMetricsFile=/data/logs/MyPerf4J/thread_metrics.log
    
#Configure TimeSlice, time unit: ms, min:1s, max:600s
MilliTimeSlice=60000
    
#Configure packages, separated with ';'
IncludePackages=cn.perf4j;org.myperf4j;cn.perf4j.demo1.[p1,p2,p3];cn.*.demo.*

#Configure show method params type
ShowMethodParams=true
```

### Run
* The output is to /data/logs/MyPerf4J/method_metrics.log:

  ```
    MyPerf4J Method Metrics [2019-02-11 20:29:22, 2019-02-11 20:29:23]
    Method[5]                           RPS  Avg(ms)  Min(ms)  Max(ms)   StdDev     Count     TP50     TP90     TP95     TP99    TP999   TP9999  TP99999    TP100
    DemoServiceImpl.getId1(long)     203674     0.00        0        0     0.00    203674        0        0        0        0        0        0        0        0
    DemoServiceImpl.getId2(long)     318386     0.00        0        2     0.00    318386        0        1        2        2        2        2        2        2
    DemoServiceImplV2.getId1(long)  1931153     0.00        0        0     0.00   1931153        0        0        0        0        0        0        0        0
    DemoServiceImplV2.getId3(long)  3862304     0.00        0        0     0.00   3862304        0        0        0        0        0        0        0        0
    Dao.doQuery()                   2134826     0.00        0        0     0.00   2134826        0        0        0        0        0        0        0        0
    ```

### Uninstall
Remove the following two parameters from the JVM startup parameters and restart to uninstall the tool.
> -javaagent:/your/path/to/MyPerf4J-ASM.jar
> -DMyPerf4JPropFile=/your/path/to/myPerf4J.properties

## Issues
If you encounter any issues or if you have a question, don't hesitate to [create an issue](https://github.com/LinShunKang/MyPerf4J/issues/new/choose) or [send email](mailto:linshunkang.chn@gmail.com) : )

## Inspired by
* [Perf4J](https://github.com/perf4j/perf4j)
* [TProfiler](https://github.com/alibaba/TProfiler)

## More Information
For more information about the project, please see [https://github.com/LinShunKang/MyPerf4J/wiki/English-Doc](https://github.com/LinShunKang/MyPerf4J/wiki/English-Doc).
