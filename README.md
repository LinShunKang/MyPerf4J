# MyPerf4J
A extremely fast performance monitoring and statistics for Java code. Inspired by [perf4j](https://github.com/perf4j/perf4j) and [TProfiler](https://github.com/alibaba/TProfiler).
Committed to becoming a performance monitoring and statistics tool that can be used for a long time in a production environment!

## Multilingual document
* English [WIKI](https://github.com/ThinkpadNC5/MyPerf4J/wiki/English-Wiki)
* 中文 [README](https://github.com/ThinkpadNC5/MyPerf4J/blob/develop/README.CN.md) [WIKI](https://github.com/ThinkpadNC5/MyPerf4J/wiki/Chinese-Wiki)


## Background
* I need a program that can measure the response time of method.
* The existing statistics of [perf4j](https://github.com/perf4j/perf4j) cannot meet my needs.

## Requirements
* Statistics on the performance indicators of the method such as RPS, Avg, Min, Max, StdDev, TP50, TP90, TP95, TP99, TP999 and TP9999, etc.
* It can be configured by properties.
* Does not take up too much memory, does not affect the normal response of the program.
* The processing of performance indicators can be customized, for example: log collection, reporting to log collection services, etc.

## Memory
* Prerequisites
    - There are 1024 interfaces on the service that need to be monitored.
    - Most of the response time of each interface is within 300ms, and there are 100 different response times larger than 300ms.
    - Non-core data structures occupy 2MB.
* Rough Mode
    - Only record requests with response time less than 1000ms.
    - 2 * 1024 * (1000 * 4B) + 2MB ≈ 10MB
* Accurate Mode
    - Record all response times.
    - 2 * 1024 * (300 * 4B + 100 * 90B) + 2MB ≈ 22MB 

## Benchmark
* Test Platform
    - OS: macOS High Sierra 10.13.3
    - JDK: 1.8.0_161
    - JVM options: -server -Xmx4G -Xms4G -Xmn2G
    - CPU: Intel(R) Core(TM) i7-7920HQ CPU@3.10GHz

* Test way
    - Run empty methods.
    - In order to avoid the performance degradation caused by the high competition of multiple threads due to the high execution speed of the empty method, eight empty methods are executed by polling, and then the RPS of the eight methods is added to obtain the result.
    - The time slice is 10s, each press pauses for 20s, and executes `System.gc();`  

* MyPerf4J-ASM
    
    | Threads | Number of loops per thread | RPS |
    |-------|-----|------|
    |1|1000000000|13815816|
    |2|1000000000|16199712|
    |4|1000000000|33060632|
    |8|1000000000|55981416|

* Summary
    - From the benchmark results
        - MyPerf4J-ASM can support 13.81 million method calls per second in a single thread. The average time per method call is 72.3ns, which can meet the requirements of most people, and does not affect the response time of the program itself.
    - Reason for high performance
        - MyPerf4J-ASM modifies the bytecode of the class through the ASM framework, inserting two lines of methods before and after the method, without generating redundant objects, and not triggering any GC in the whole process of the benchmark (except for the `System.gc();` executed in the code).

## Usage

* Add VM options:  `-javaagent:/your/path/to/MyPerf4J-ASM-${MyPerf4J-version}.jar`
* Add VM options: `-DMyPerf4JPropFile=/your/path/to/myPerf4J.properties`, and add properties in `/your/path/to/myPerf4J.properties`

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

* Execute command `mvn clean package`

* Run your application

* Performance Statistics

    ```
    2018-07-01 23:40:24.2 [MyPerf4J] INFO RecorderMaintainer.roundRobinProcessor finished!!! cost: 0ms
    2018-07-01 23:40:24.3 [MyPerf4J] INFO RecorderMaintainer.backgroundProcessor finished!!! cost: 1ms
    MyPerf4J Performance Statistics [2018-07-01 23:40:23, 2018-07-01 23:40:24]
    Api[2/3]                    RPS  Avg(ms)  Min(ms)  Max(ms)   StdDev     Count     TP50     TP90     TP95     TP99    TP999   TP9999  TP99999    TP100
    UserServiceImpl.getId1  7454181     0.00        0        0     0.00   7454181        0        0        0        0        0        0        0        0
    UserServiceImpl.getId2  7454180     0.00        0        0     0.00   7454180        0        0        0        0        0        0        0        0
    ```
