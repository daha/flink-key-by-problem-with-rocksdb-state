package org.example

import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks
import org.apache.flink.streaming.api.watermark.Watermark

class TimestampExtractor extends AssignerWithPunctuatedWatermarks[Event] {
  override def checkAndGetNextWatermark(lastElement: Event,
                                        extractedTimestamp: Long): Watermark = {
    new Watermark(lastElement.timestamp - 1)
  }

  override def extractTimestamp(element: Event, recordTimestamp: Long): Long = {
    element.timestamp
  }

}
