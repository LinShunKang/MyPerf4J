# MyPerf4J
> A simple, fast, and non-intrusive Java method performance monitoring and statistical tool designed for high-concurrency, low-latency applications. Inspired by [perf4j](https://github.com/perf4j/perf4j) and [TProfiler](https://github.com/alibaba/TProfiler). 

MyPerf4J has the following features:
*  No intrusion: using JavaAgent mode, no intrusion to the application, no need to modify the application code.
*  High performance: Very low performance consumption, only 73ns per record, can be used in production environment for a long time.
*  Low memory: With memory multiplexing, only a small number of temporary objects are generated throughout the life cycle, and the GC of the application is not affected.
*  High precision: using nanoseconds to calculate response time.
*  Real-time: Supports second level monitoring, minimum 1s!

## Multilingual document
* English [Document](https://github.com/ThinkpadNC5/MyPerf4J/wiki/English-Doc)
* [中文README](https://github.com/ThinkpadNC5/MyPerf4J/blob/develop/README.CN.md) [中文文档](https://github.com/ThinkpadNC5/MyPerf4J/wiki/Chinese-Doc) 

## Visualization
* Currently, MyPerf4J has provided [Grafana Dashboard](https://grafana.com/dashboards/6991) for data display.
![Markdown](https://raw.githubusercontent.com/ThinkpadNC5/Pictures/master/Monitor_Page_V3_1_200FPS.gif)
![Markdown](https://raw.githubusercontent.com/ThinkpadNC5/Pictures/master/Monitor_Page_V4_200FPS.gif)

## Getting Started
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
#configurePerfStatsProcessor，can be configured without for configuration of custom statistics
#PerfStatsProcessor=cn.perf4j.demo.MyPerfStatsProcessor
    
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
  
    
## Issues
If you encounter any issues or if you have a question, don't hesitate to [create an issue](https://github.com/ThinkpadNC5/MyPerf4J/issues/new) : )

## More Information
For more information about the project, please see [https://github.com/ThinkpadNC5/MyPerf4J/wiki/English-Doc](https://github.com/ThinkpadNC5/MyPerf4J/wiki/English-Doc).
