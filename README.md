# storm-rawlog-manager
用Strom处理原始日志，提取一些规则事件。
## 主要的一些功能性代码
- 在日志文件中捕获延时的事件规则：[CaptureDelayAtomEventBolt](src/main/java/com/nl/event/bolt/CaptureDelayAtomEventBolt.java)
- hdfs的append存储试实时数据：[SdcStoreHdfsBolt](src/main/java/com/nl/event/bolt/SdcStoreHdfsBolt.java)
- 延时队列设计代码：[DelayEvent](src/main/java/com/nl/util/delay/bean/DelayEvent.java)
- storm yaml配置加载：[ConfigLoader](src/main/java/com/nl/util/config/bean/ConfigLoader.java)
