# MyPerf4J
一个简单快速的Java接口性能监控和统计工具。受[perf4j](https://github.com/perf4j/perf4j)启发而来。

## 背景
* 我需要一个能统计接口响应时间的程序
* [perf4j](https://github.com/perf4j/perf4j)现有的统计结果不能满足我的需求

## 需求
* 能统计出接口的RPS、TP50、TP90、TP95、TP99、TP999、TP9999等性能指标
* 可以通过注解进行配置，注解可以配置到类和/或接口上
* 尽量不占用过多的内存、不影响程序的正常响应
* 性能指标的处理可以定制化，例如：日志收集、上报给日志收集服务等

## 设计
* 通过把所有的响应时间记录下来，便可以进行所有可能的分析，包括RPS、TP99等，那么如何高效的存储这些数据呢？
    - 采用K-V的形式即可，K为响应时间，V为对应的次数，这样内存的占用只和不同响应时间的个数有关，而和总请求次数无关
    - 如果单纯的使用Map来存储数据，必然会占用大量不必要的内存
    - 根据二八定律，绝大多数的接口响应时间在很小的范围内，这个很小的范围特别适合使用数组来存储，数组下标即为响应时间，对应元素即为该响应时间对应的数量；而少数的接口响应时间分布的范围则会比较大，适合用Map来存储；
    - 综上所述，核心数据结构为：数组+Map，将小于某一阈值的响应时间记录在数组中，将大于等于该阈值的响应时间记录到Map中
* 利用AOP进行响应时间的采集
* 通过注解的方式进行配置，并可以通过参数配置调优核心数据结构的内存占用
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
* 核心数据结构压测

| 线程数 | 每线程循环数| RPS |
|-------|-----|------|
|1|100000000|16666666|
|2|100000000|18181818|
|4|100000000|22222222|
|8|100000000|29629629|


* 整体压测 - 包括AOP
    - 只压测一个接口并且被压测接口的实现为空方法
    - 时间片为10s，每次压测中间停顿20s，并且执行`System.gc();`

| 线程数 | 每线程循环数| RPS |
|-------|-----|------|
|1|100000000|1431983|
|2|100000000|2400973|
|4|100000000|4569964|
|8|100000000|5843866|

* 压测结论
    - 从整体压测结果来看，在单线程下每秒可支持143万次的方法调用，平均每次方法调用耗时1.43us，能够满足绝大部分人的要求，不会对程序本身的响应时间造成影响
    - 通过对比核心数结构和整体压测的结果，核心数据结构本身并不是瓶颈，瓶颈在于切面及反射所带来的耗时

## 使用
* 引入Maven依赖

```
    <dependency>
        <groupId>MyPerf4J</groupId>
        <artifactId>MyPerf4J</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
```
* 在你想要分析性能的类或方法明上加上 @Profiler注解，同时对于不想进行性能分析的接口上加上 @NonProfiler

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
* 新建一个MyRecordProcessor类

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
* 在Spring配置文件中加入

```
    <bean id="myPerfStatsProcessor" class="cn.perf4j.test.profiler.MyPerfStatsProcessor"/>

    <bean id="asyncPerfStatsProcessor" class="cn.perf4j.AsyncPerfStatsProcessor">
        <constructor-arg index="0" ref="myPerfStatsProcessor"/>
    </bean>
```
* 输出结果

```
MyPerf4J Performance Statistics [2018-04-06 11:08:00, 2018-04-06 11:09:00]
Api                            RPS  Min(ms)  Max(ms)     TP50     TP90     TP95     TP99    TP999   TP9999  TP99999    TP100
ProfilerTestApiImpl.test1  1473961        0        4        0        1        2        3        4        4        4        4
ProfilerTestApiImpl.test3        0       -1       -1       -1       -1       -1       -1       -1       -1       -1       -1
```

## 关于rough模式与accurate模式
* rough模式
    - 精度略差，会丢弃响应时间超过指定阈值的记录
    - 更加节省内存，只使用数组来记录响应时间
    - 速度略快一些
    - 默认

* accurate模式
    - 精度高，会记录所有的响应时间
    - 相对耗费内存，使用数组+Map来记录响应时间
    - 速度略慢一些
    - 需要加入启动参数-DMyPerf4J.recorder.mode=accurate

* 建议
    - 对于内存敏感或精度要求不是特别高的应用，推荐使用rough模式
    - 对于内存不敏感且精度要求特别高的应用，推荐使用accurate模式
