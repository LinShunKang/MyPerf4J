# MyPerf4J
A Simple Fast Performance Monitoring and Statistics for Java Code. Inspired by [perf4j](https://github.com/perf4j/perf4j).

## Background
* I need a program that can measure the response time of interfaces.
* The existing statistics of [perf4j](https://github.com/perf4j/perf4j) cannot meet my needs.

## Requirements
* Statistics on the performance indicators of the interface such as RPS, TP50, TP90, TP95, TP99, TP999, and TP9999, etc.
* It can be configured by annotations, and annotations can be configured to class and / or method.
* Try not to take up too much memory, does not affect the normal response of the program.
* The processing of performance indicators can be customized, for example: log collection, reporting to log collection services, etc.

## Design
* By recording all response times, all possible analyses can be performed, including RPS, TP99, etc. How can we store these data efficiently?
    - In the form of K-V, K is the response time, and V is the corresponding number of times, so the memory occupancy is only related to the number of different response times, regardless of the total number of requests.
    - If we simply use Map to store data, it will take up a lot of unnecessary memory.
    - According to Pareto principle, most of the of interface response time is in a very small range. This small range is particularly suitable for storage using arrays. The array subscript is the response time, and the corresponding element is the number corresponding to the response time. A small number of interface response time distribution will be relatively large, suitable for storage with Map;
    - In summary, the core data structure is: array + Map, the response time less than a certain threshold is recorded in the array, and the response time greater than or equal to the threshold is recorded in the Map.
* Using AOP for response time acquisition
* Configuration through annotations and tuning of memory usage of core data structures through parameter configuration.
* It can collect response time by synchronous way, avoid creating too many Runnable objects, and affect GC of program.
* Acquire the collection result asynchronously to avoid affecting the response time of the interface.
* Expose the interface for processing the collected results to facilitate customized processing.

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
    
* The core data structure benchmark

| Threads | Number of loops per thread | RPS |
|-------|-----|------|
|1|100000000|16666666|
|2|100000000|18181818|
|4|100000000|22222222|
|8|100000000|29629629|

* Overall benchmark
    - Measure only one interface and the implementation of the pressure measurement interface is an empty method.
    - The time slice is 10s, each press pauses for 20s, and executes `System.gc();`

| Threads | Number of loops per thread | RPS |
|-------|-----|------|
|1|100000000|1431983|
|2|100000000|2400973|
|4|100000000|4569964|
|8|100000000|5843866|

* Summary
    - From the overall benchmark results, we can support 1.43 million method calls per second in a single thread. The average time per method call is 1.43 us, which can meet the requirements of most people, and does not affect the response time of the program itself.
    - By comparing the core data structure and the overall pressure measurement results, the core data structure itself is not a bottleneck. The bottleneck is the time taken by the AOP and reflection.

## Usage
* Add maven dependency

```
    <dependency>
        <groupId>MyPerf4J</groupId>
        <artifactId>MyPerf4J</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
```
* Add @Profiler annotation to the class or method you want to analyze performance, and add @NonProfiler annotation to the method you do not want to analyze

```
package cn.perf4j.test.profiler;

import cn.perf4j.aop.NonProfiler;
import cn.perf4j.aop.Profiler;

/**
 * Created by LinShunkang on 2018/3/11
 */
@Profiler(mostTimeThreshold = 10)
public class ProfilerTestApiImpl implements ProfilerTestApi {

    @Override
    @Profiler(mostTimeThreshold = 20)
    public String test1(String aaa) {
        return null;
    }

    @Override
    @NonProfiler
    public int test2() {
        return 0;
    }

    @Override
    public void test3(String aaa, String bbb) {
    }
}
```
* Create a new class and implements PerfStatsProcessor

``` 
package cn.perf4j.test.profiler;

import cn.perf4j.PerfStats;
import cn.perf4j.PerfStatsProcessor;
import cn.perf4j.util.PerfStatsFormatter;

import java.util.List;

/**
 * Created by LinShunkang on 2018/3/16
 */
public class MyPerfStatsProcessor implements PerfStatsProcessor {

    @Override
    public void process(List<PerfStats> perfStatsList, long startMillis, long stopMillis) {
        //You can do anything you want to do :)
        System.out.println(PerfStatsFormatter.getFormatStr(perfStatsList, startMillis, stopMillis));
    }
}
```
* Add these beans in your Spring configuration file

```
    <bean id="myPerfStatsProcessor" class="cn.perf4j.test.profiler.MyPerfStatsProcessor"/>

    <bean id="asyncPerfStatsProcessor" class="cn.perf4j.AsyncPerfStatsProcessor">
        <constructor-arg index="0" ref="myPerfStatsProcessor"/>
    </bean>
```
* Performance Statistics

```
MyPerf4J Performance Statistics [2018-04-06 11:08:00, 2018-04-06 11:09:00]
Api                            RPS  Min(ms)  Max(ms)     TP50     TP90     TP95     TP99    TP999   TP9999  TP99999    TP100
ProfilerTestApiImpl.test1  1473961        0        4        0        1        2        3        4        4        4        4
ProfilerTestApiImpl.test3        0       -1       -1       -1       -1       -1       -1       -1       -1       -1       -1
```

## About Rough Mode and Accurate Mode
* Rough Mode
    - The accuracy is slightly worse, and it will discard the record whose response time exceeds the specified threshold.
    - It saves more memory, and only uses array to record response time.
    - The speed is a little faster.
    - Default mode.

* Accurate Mode
    - High accuracy, records all response times.
    - It consumes relatively memory and uses array +Map to record response time.
    - The speed is slightly slower.
    - Need to add startup parameters -DMyPerf4J.recorder.mode=accurate.

* Suggestions
    - For memory-sensitive or precision applications that are not particularly demanding, Rough Mode is recommended.
    - The Accurate Mode is recommended for applications that are insensitive to memory and require high accuracy.
