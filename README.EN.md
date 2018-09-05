# MyPerf4J
> A high performance and non-intrusive real-time Java performance monitoring and statistical tool designed for high-concurrency, low-latency applications. Inspired by [perf4j](https://github.com/perf4j/perf4j) and [TProfiler](https://github.com/alibaba/TProfiler). 

MyPerf4J has the following features:
*  No intrusion: using JavaAgent mode, no intrusion to the application, no need to modify the application code.
*  High performance: Very low performance consumption, only 73ns per record, can be used in production environment for a long time.
*  Low memory: With memory multiplexing, only a small number of temporary objects are generated throughout the life cycle, and the GC of the application is not affected.
*  High precision: using nanoseconds to calculate response time.
*  Real-time: Supports second level monitoring, minimum 1s!

## Multilingual document
* English [Document](https://github.com/ThinkpadNC5/MyPerf4J/wiki/English-Doc)
* [中文README](https://github.com/ThinkpadNC5/MyPerf4J/blob/develop/README.CN.md) [中文文档](https://github.com/ThinkpadNC5/MyPerf4J/wiki/Chinese-Doc) 

## What does it monitor?
MyPerf4J collects several thousands of metrics per application. All these metrics are collected and visualized in real-time.

This is a list of what it currently monitors:
* **[Method](https://grafana.com/dashboards/7766)**<br/>
RPS，Count，Avg，Min，Max，StdDev，TP50, TP90, TP95, TP99, TP999, TP9999, TP99999, TP100
![Markdown](https://raw.githubusercontent.com/ThinkpadNC5/Pictures/master/MyPerf4J-InfluxDB-Method_Show_Operation.gif)
![Markdown](https://raw.githubusercontent.com/ThinkpadNC5/Pictures/master/MyPerf4J-InfluxDB-Method_Just_Record.gif)

* **[JVM Thread](https://grafana.com/dashboards/7778)**<br/>
TotalStarted，Runnable，Blocked，Waiting，TimedWaiting，Terminated，Active，Peak，Daemon，New
![Markdown](https://raw.githubusercontent.com/ThinkpadNC5/Pictures/master/MyPerf4J-InfluxDB-JVM-Thread_Just_Record.gif)

* **[JVM Memory](https://grafana.com/dashboards/7775)**<br/>
HeapInit，HeapUsed，HeapCommitted，HeapMax，NonHeapInit，NonHeapUsed，NonHeapCommitted，NonHeapMax
![Markdown](https://raw.githubusercontent.com/ThinkpadNC5/Pictures/master/MyPerf4J-InfluxDB-JVM-Memory_Just_Record.gif)

* **[JVM GC](https://grafana.com/dashboards/7772)**<br/>
CollectCount，CollectTime
![Markdown](https://raw.githubusercontent.com/ThinkpadNC5/Pictures/master/MyPerf4J-InfluxDB-JVM-GC_Just_Record.gif)

* **[JVM Class](https://grafana.com/dashboards/7769)**<br/>
Total，Loaded，Unloaded
![Markdown](https://raw.githubusercontent.com/ThinkpadNC5/Pictures/master/MyPerf4J-InfluxDB-JVM-Class_Just_Record.gif)

> Want to know how to achieve the above effect? Please start the application according to the description of [Quick Start](https://github.com/ThinkpadNC5/MyPerf4J/blob/develop/README.EN.md#quick-start), and then follow the instructions in [here](https://github.com/ThinkpadNC5/MyPerf4J/wiki/InfluxDB) to install and configure.
 
## Quick start
MyPerf4J adopts JavaAgent configuration mode, **transparent** access application, and the application code is completely **no-intrusive**.

### Build
* git clone git@github.com:ThinkpadNC5/MyPerf4J.git
* mvn clean package
* Rename MyPerf4J-ASM-${MyPerf4J-version}.jar to MyPerf4J-ASM.jar

### Configure
Add the following two parameters to the JVM startup parameters
> -javaagent:/your/path/to/MyPerf4J-ASM.jar
> -DMyPerf4JPropFile=/your/path/to/myPerf4J.properties

Among them, the configuration of `MyPerf4JPropFile` is as follows:

```
#Application name
AppName=MyPerf4JTest

#Configure MetricsProcessors
#MethodMetricsProcessor=cn.myperf4j.base.metric.processor.influxdb.InfluxDBMethodMetricsProcessor
#ClassMetricsProcessor=cn.myperf4j.base.metric.processor.influxdb.InfluxDBJvmClassMetricsProcessor
#GCMetricsProcessor=cn.myperf4j.base.metric.processor.influxdb.InfluxDBJvmGCMetricsProcessor
#MemMetricsProcessor=cn.myperf4j.base.metric.processor.influxdb.InfluxDBJvmMemoryMetricsProcessor
#ThreadMetricsProcessor=cn.myperf4j.base.metric.processor.influxdb.InfluxDBJvmThreadMetricsProcessor

#Configure the number of backup Recorders. The default is 1, the minimum is 1, and the maximum is 8. When you need to count a large number of method performance data in a smaller MillTimeSlice, you can configure a larger number.
BackupRecordersCount=1
    
#configure RecordMode，accurate/rough
RecorderMode=accurate
    
#configure TimeSlice，time unit: ms，min:1s，max:600s
MillTimeSlice=60000
    
#configure packages，separated with ';'
IncludePackages=cn.perf4j;org.myperf4j
    
#configure packages，separated with ';'
ExcludePackages=org.spring;
    
#configure methods，separated with ';'
ExcludeMethods=equals;hash
    
#true/false
ExcludePrivateMethod=true
    
#General method execution time threshold in ms
ProfilingTimeThreshold=1000
    
#The number of times the method execution time threshold is exceeded in a time slice, valid only when RecorderMode=accurate
ProfilingOutThresholdCount=10
```

### Run
* The default output is to stdout.log:

    ```
    MyPerf4J Performance Statistics [2018-07-01 23:40:23, 2018-07-01 23:40:24]
    Api[2/3]                    RPS  Avg(ms)  Min(ms)  Max(ms)   StdDev     Count     TP50     TP90     TP95     TP99    TP999   TP9999  TP99999    TP100
    DemoServiceImpl.getId1  7454181     0.00        0        0     0.00   7454181        0        0        0        0        0        0        0        0
    DemoServiceImpl.getId2  7454180     0.00        0        0     0.00   7454180        0        0        0        0        0        0        0        0
    ```
    
### Uninstall
Remove the following two parameters from the JVM startup parameters and restart to uninstall the tool.
> -javaagent:/your/path/to/MyPerf4J-ASM.jar
> -DMyPerf4JPropFile=/your/path/to/myPerf4J.properties

## Issues
If you encounter any issues or if you have a question, don't hesitate to [create an issue](https://github.com/ThinkpadNC5/MyPerf4J/issues/new) : )

## More Information
For more information about the project, please see [https://github.com/ThinkpadNC5/MyPerf4J/wiki/English-Doc](https://github.com/ThinkpadNC5/MyPerf4J/wiki/English-Doc).
