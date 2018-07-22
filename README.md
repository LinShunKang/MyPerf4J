# MyPerf4J
> 一个针对高并发、低延迟应用设计的简单、快速且无侵入的Java方法性能监控和统计工具。
受 [perf4j](https://github.com/perf4j/perf4j) 和 [TProfiler](https://github.com/alibaba/TProfiler)启发而来。

MyPerf4J具有以下几个特性：
* 无侵入: 采用JavaAgent方式，对应用程序完全无侵入，无需修改应用代码
* 高性能: 性能消耗非常小，每次统计只花费73ns，可以在生产环境长期使用
* 低内存: 采用内存复用的方式，整个生命周期只产生极少的临时对象，不影响应用程序的GC
* 高精度: 采用纳秒来计算响应时间
* 高实时: 支持秒级监控，最低1s!

## 文档
* English [README](https://github.com/ThinkpadNC5/MyPerf4J/blob/develop/README.md) [Document](https://github.com/ThinkpadNC5/MyPerf4J/wiki/English-Doc)
*  [中文文档](https://github.com/ThinkpadNC5/MyPerf4J/wiki/Chinese-Doc)    
    
## 可视化
* 目前MyPerf4J已提供[Grafana Dashboard](https://grafana.com/dashboards/6991)进行数据展示
![Markdown](https://raw.githubusercontent.com/ThinkpadNC5/Pictures/master/Monitor_Page_V3_200FPS.gif)

> 想知道如何实现上述效果？请先按照[这里](https://github.com/ThinkpadNC5/MyPerf4J/wiki/%E5%BF%AB%E9%80%9F%E5%90%AF%E5%8A%A8)的描述启动应用，再按[这里](https://github.com/ThinkpadNC5/MyPerf4J/wiki/InfluxDB_)的描述进行安装配置即可。

## 快速启动
> 详细内容，请看[这里](https://github.com/ThinkpadNC5/MyPerf4J/wiki/%E5%BF%AB%E9%80%9F%E5%90%AF%E5%8A%A8)

## 问题
> 如果您遇到任何问题或有疑问，请您毫不犹豫的[提交Issue](https://github.com/ThinkpadNC5/MyPerf4J/issues/new) : )

## 更多信息
> 想更深入的了解MyPerf4J？请看[https://github.com/ThinkpadNC5/MyPerf4J/wiki/Chinese-Doc](https://github.com/ThinkpadNC5/MyPerf4J/wiki/Chinese-Doc)。
