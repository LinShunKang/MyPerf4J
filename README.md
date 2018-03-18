# MyPerf4J
A Simple and Fast Performance Monitoring and Statistics for Java Code. Inspired by perf4j link: https://github.com/perf4j/perf4j
## 背景
* 我需要一个能统计接口响应时间的程序
* perf4j现有的统计数据不能满足我的需求

## 需求
* 能统计出接口的RPS、TP50、TP90、TP95、TP99等性能指标
* 可以通过注解进行配置，注解可以配置到类和/或接口上，并可以通过参数配置调优内存占用
* 尽量不占用过多的内存、不影响程序的正常响应
* 性能指标的处理可以定制化，例如：日志收集、上报给日志收集服务等

## 设计
* 通过把所有的响应时间记录下来，便可以进行所有可能的分析，包括RPS、TP99等，那么如何高效的存储这些数据呢？
    - 采用K-V的形式即可，K为响应时间，V为对应的次数，这样内存的占用只和不同响应时间的次数有关，而和总请求次数无关
    - 核心数据结构为数组+Map，根据二八定律，绝大多数的接口相应时间在很小的范围内，这个很小的范围特别适合使用数组来存储；而少数的接口响应时间分布的范围则会比较大，适合用Map来存储
* 利用AOP进行响应时间的采集
* 异步处理采集结果，避免影响接口的正常响应
* 把处理采集的结果的接口暴露出来，方便进行自定义的处理

## 进度
* 核心数据结构开发完成
* AOP相关功能开发完成
* 性能指标统计功能开发完成

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
MyRecordProcessor():PerfStats{api=UserSocialApiImpl.getAll, [2018-03-18 00:03:00, 2018-03-18 00:04:00], RPS=55, TP50=0, TP90=4, TP95=4, TP99=4, TP999=4, TP9999=4, TP100=4, minTime=0, maxTime=4, totalCount=3399}
MyRecordProcessor():PerfStats{api=UserSocialApiImpl.remove, [2018-03-18 00:03:00, 2018-03-18 00:04:00], RPS=0, TP50=-1, TP90=-1, TP95=-1, TP99=-1, TP999=-1, TP9999=-1, TP100=-1, minTime=-1, maxTime=-1, totalCount=0}
```

