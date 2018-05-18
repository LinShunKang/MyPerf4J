# MyPerf4J
一个极快的Java接口性能监控和统计工具。受[perf4j](https://github.com/perf4j/perf4j)和[TProfiler](https://github.com/alibaba/TProfiler)启发而来。

## 多语言文档
* English [README.md](https://github.com/ThinkpadNC5/MyPerf4J/blob/develop/README.md)
* 中文 

## 背景
* 我需要一个能统计接口响应时间的程序
* [perf4j](https://github.com/perf4j/perf4j)现有的统计结果不能满足我的需求

## 需求
* 能统计出接口的RPS、Avg、Min、Max、StdDev、TP90、TP95、TP99、TP999等性能指标
* 可以通过注解进行配置，注解可以配置到类和/或方法上
* 尽量不占用过多的内存、不影响程序的正常响应
* 性能指标的处理可以定制化，例如：日志收集、上报给日志收集服务等

## 设计
* 通过把所有的响应时间记录下来，便可以进行所有可能的分析，包括RPS、TP99等，那么如何高效的存储这些数据呢？
    - 采用K-V的形式即可，K为响应时间，V为对应的次数，这样内存的占用只和不同响应时间的个数有关，而和总请求次数无关
    - 如果单纯的使用Map来存储数据，必然会占用大量不必要的内存
    - 根据二八定律，绝大多数的接口响应时间在很小的范围内，这个很小的范围特别适合使用数组来存储，数组下标即为响应时间，对应元素即为该响应时间对应的数量；而少数的接口响应时间分布的范围则会比较大，适合用Map来存储；
    - 综上所述，核心数据结构为：数组+Map，将小于某一阈值的响应时间记录在数组中，将大于等于该阈值的响应时间记录到Map中
* 利用AOP进行响应时间的采集，包括[AspectJ](https://github.com/eclipse/org.aspectj)和[ASM](http://asm.ow2.io/)
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
* 测试方法
    - 对空方法进行压测 
    - 为了避免由于空方法执行过快导致多个线程高度竞争资源（竞争AbstractRecorder中的AtomicIntegerArray）进而导致压测结果出现明显的性能下降，通过轮询的方式执行8个空方法，然后把8个方法的RPS相加得出结果
    - 时间片为10s，每次压测中间停顿20s，并且执行`System.gc();`

* MyPerf4J-AspectJ

| 线程数 | 每线程循环数| RPS |
|-------|-----|------|
|1|500000000|3114928|
|2|500000000|5407560|
|4|500000000|9620888|
|8|500000000|11682240|

* MyPerf4J-ASM

| 线程数 | 每线程循环数| RPS |
|-------|-----|------|
|1|500000000|11767056|
|2|500000000|14972208|
|4|500000000|28561824|
|8|500000000|45966888|

* 压测结论
    - 从压测结果来看：
        - MyPerf4J-AspectJ在单线程下每秒可支持311万次的方法调用，平均每次方法调用耗时321ns
        - MyPerf4J-ASM在单线程下每秒可支持1176万次的方法调用，平均每次方法调用耗时84ns
        - 无论是MyPerf4J-AspectJ还是MyPerf4J-ASM都能够满足绝大部分人的要求，不会对程序本身的响应时间造成影响
    - 性能差异原因：
        - MyPerf4J-AspectJ在每次方法调用时都会生成一个JoinPoint对象，随着调用次数的增加JVM会不断的触发YoungGC
        - MyPerf4J-ASM是通过ASM框架修改类的字节码，在方法前后插入两行方法，不产生多余的对象，在整个压测过程中不会触发任何的GC（除了代码中执行的`System.gc();`）

## 使用
* 使用MyPerf4J-ASM
    
    * 在JVM启动参数里加上： -javaagent:/your/path/to/MyPerf4J-ASM-1.2.jar

* 使用MyPerf4J-AspectJ    
    * 在`pom.xml`引入Maven依赖
    
    ```
        <dependency>
            <groupId>MyPerf4J</groupId>
            <artifactId>MyPerf4J-AspectJ</artifactId>
            <version>1.2</version>
        </dependency>
    ```
    
    * 在`pom.xml`加入build plugin
    
    ```
        <build>
            <plugins>
                <plugin>
                    <groupId>org.codehaus.mojo</groupId>
                    <artifactId>aspectj-maven-plugin</artifactId>
                    <version>1.10</version>
                    <configuration>
                        <complianceLevel>1.7</complianceLevel>
                        <source>1.7</source>
                        <aspectLibraries>
                            <aspectLibrary>
                                <groupId>MyPerf4J</groupId>
                                <artifactId>MyPerf4J-AspectJ</artifactId>
                            </aspectLibrary>
                        </aspectLibraries>
                    </configuration>
                    <executions>
                        <execution>
                            <goals>
                                <goal>compile</goal>
                                <goal>test-compile</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
            </plugins>
        </build>
    ```

* 在JVM启动参数里加上：-DMyPerf4JPropFile=/your/path/to/myPerf4J.properties，并在`/your/path/to/myPerf4J.properties`中加入以下几个配置项

```
#配置PerfStatsProcessor，可不配置
PerfStatsProcessor=cn.perf4j.test.profiler.MyPerfStatsProcessor

#配置Record模式，可配置为accurate/rough
RecorderMode=accurate

#配置时间片，单位为ms，最小30s，最大600s
MillTimeSlice=60000

#需要监控的package，可配置多个，用英文';'分隔
IncludePackages=cn.perf4j;org.myperf4j

#不需要监控的package，可配置多个，用英文';'分隔
ExcludePackages=org.spring;

#是否开启debug日志，可配置为true/false
Debug.PrintDebugLog=true

#可配置为byPorfiler/byPackage，仅针对MyPerf4J-ASM
ASM.ProfilingType=byProfiler

#可配置多个方法名，用英文';'分隔，仅针对MyPerf4J-ASM
ASM.ExcludeMethods=equals;hash

#是否排除私有方法，true/false，仅针对MyPerf4J-ASM
ASM.ExcludePrivateMethod=true

#可配置多个ClassLoader，用英文';'分隔，仅针对MyPerf4J-ASM
ASM.ExcludeClassLoaders=
```

* 在你想要分析性能的类或方法明上加上 `@Profiler`注解，同时对于不想进行性能分析的方法上加上 `@NonProfiler`

```
package cn.perf4j.test.profiler;

import cn.myperf4j.core.annotation.NonProfiler;
import cn.myperf4j.core.annotation.Profiler;

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

* 新建一个MyRecordProcessor类并声明PerfStatsProcessor接口

``` 
package cn.perf4j.test.profiler;

import cn.myperf4j.core.PerfStats;
import cn.myperf4j.core.PerfStatsProcessor;
import cn.myperf4j.core.util.PerfStatsFormatter;

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

* 执行命令 `mvn clean package`

* 运行你的程序

* 输出结果

```
MyPerf4J Performance Statistics [2018-04-06 11:08:00, 2018-04-06 11:09:00]
Api                            RPS  Min(ms)  Max(ms)     TP50     TP90     TP95     TP99    TP999   TP9999  TP99999    TP100
ProfilerTestApiImpl.test1  1473961        0        4        0        1        2        3        4        4        4        4
ProfilerTestApiImpl.test3        0       -1       -1       -1       -1       -1       -1       -1       -1       -1       -1
```

## 关于Rough模式与Accurate模式
* Rough模式
    - 精度略差，会丢弃响应时间超过指定阈值的记录
    - 更加节省内存，只使用数组来记录响应时间
    - 速度略快一些
    - 默认

* Accurate模式
    - 精度高，会记录所有的响应时间
    - 相对耗费内存，使用数组+Map来记录响应时间
    - 速度略慢一些
    - 需要在`/your/path/to/myPerf4J.properties`加入配置：RecorderMode=accurate 

* 建议
    - 对于内存敏感或精度要求不是特别高的应用，推荐使用Rough模式
    - 对于内存不敏感且精度要求特别高的应用，推荐使用Accurate模式

## 关于MyPerf4J-AspectJ与MyPerf4J-ASM
* MyPerf4J-AspectJ
    * 利用[AspectJ](https://github.com/eclipse/org.aspectj)实现切面的织入
    * 从技术角度来说更加成熟
    * 速度稍慢，会产生不必要的对象而影响程序本身的GC
* MyPerf4J-ASM
    * 利用[ASM](http://asm.ow2.io/)来实现切面的织入
    * 从技术角度来说，由于是自己编写修改字节码的逻辑，存在一定的风险
    * 速度极快！不产生任何不必要的对象，不影响程序本身的GC


