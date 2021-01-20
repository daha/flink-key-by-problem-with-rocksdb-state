package org.example

import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

class AggregationFunction
    extends ProcessWindowFunction[Event, Output, Key, TimeWindow] {
  override def process(key: Key,
                       context: Context,
                       elements: Iterable[Event],
                       out: Collector[Output]): Unit = {
    out.collect(Output(key, elements.size))
  }
}
