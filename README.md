# MyPerf4J
A Simple and Fast Performance Monitoring and Statistics for Java Code. Inspired by perf4j link: https://github.com/perf4j/perf4j

## 背景
* 我需要一个能统计接口响应时间的程序
* perf4j现有的统计结果不能满足我的需求

## 需求
* 能统计出接口的RPS、TP50、TP90、TP95、TP99、TP999、TP9999等性能指标
* 可以通过注解进行配置，注解可以配置到类和/或接口上
* 尽量不占用过多的内存、不影响程序的正常响应
* 性能指标的处理可以定制化，例如：日志收集、上报给日志收集服务等

## 设计
* 通过把所有的响应时间记录下来，便可以进行所有可能的分析，包括RPS、TP99等，那么如何高效的存储这些数据呢？
    - 采用K-V的形式即可，K为响应时间，V为对应的次数，这样内存的占用只和不同响应时间的个数有关，而和总请求次数无关
    - 如果单纯的使用Map来存储数据，必然会占用大量不必要的内存
    - 根据二八定律，绝大多数的接口相应时间在很小的范围内，这个很小的范围特别适合使用数组来存储，数组下标即为相应时间，对应元素即为该响应时间对应的数量；而少数的接口响应时间分布的范围则会比较大，适合用Map来存储；
    - 综上所述，核心数据结构为：数组+Map，将小于某一阈值的响应时间记录在数组中，将大于等于该阈值的响应时间记录到Map中
* 利用AOP进行响应时间的采集
* 通过注解的方式进行配置，并可以通过参数配置调优核心数据结构的内存占用
* 通过同步的方式采集响应时间，避免创建过多的Runnable对象影响程序的GC
* 通过异步的方式处理采集结果，避免影响接口的响应时间
* 把处理采集的结果的接口暴露出来，方便进行自定义的处理

## 压测结果
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
    - 从整体压测结果来看，在单线程下每秒可支持143万次的方法调用，平均每次方法调用耗时1.43us，能够满足绝大部分人的要求，不会对程序本身的相应时间造成影响
    - 通过对比核心数结构和整体压测的结果，核心数据结构本身并不是瓶颈，瓶颈在于切面及反射锁带来的耗时

## 使用
* 引入Maven依赖

```
    <dependency>
        <groupId>MyPerf4J</groupId>
        <artifactId>MyPerf4J</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
```
* 在你想要分析性能的类或方法明上加上 @Profiler
* 新建一个MyRecordProcessor类

``` 
import cn.perf4j.PerfStatsCalculator;
import cn.perf4j.Record;
import cn.perf4j.RecordProcessor;

import java.util.List;

/**
 * Created by LinShunkang on 2018/3/17
 */
public class MyRecordProcessor implements RecordProcessor {

    @Override
    public void process(String api, long startMilliTime, long stopMillTime, List<Record> sortedRecords) {
        //You can do anything your want to do :)
        System.out.println("MyRecordProcessor():" + PerfStatsCalculator.calPerfStat(api, startMilliTime, stopMillTime, sortedRecords));
    }
}
```
* 在Spring配置文件中加入

```
    <bean id="myRecordProcessor" class="cn.data.api.MyRecordProcessor"/>

    <bean id="asyncRecordProcessor" class="cn.perf4j.AsyncRecordProcessor">
        <constructor-arg index="0" ref="myRecordProcessor"/>
    </bean>
```
* 输出结果

```
MyRecordProcessor():PerfStats{api=YourApiImpl.get, [2018-03-18 00:03:00, 2018-03-18 00:04:00], RPS=55, TP50=0, TP90=4, TP95=4, TP99=4, TP999=4, TP9999=4, TP100=4, minTime=0, maxTime=4, totalCount=3399}
MyRecordProcessor():PerfStats{api=YourApiImpl.remove, [2018-03-18 00:03:00, 2018-03-18 00:04:00], RPS=0, TP50=-1, TP90=-1, TP95=-1, TP99=-1, TP999=-1, TP9999=-1, TP100=-1, minTime=-1, maxTime=-1, totalCount=0}
```


