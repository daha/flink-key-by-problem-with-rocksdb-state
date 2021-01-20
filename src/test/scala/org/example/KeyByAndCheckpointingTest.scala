package org.example

import org.apache.flink.contrib.streaming.state.RocksDBStateBackend
import org.apache.flink.runtime.state.StateBackend
import org.apache.flink.runtime.state.filesystem.FsStateBackend
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.datastream.DataStreamUtils
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
import org.apache.flink.streaming.api.windowing.time.Time
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.JavaConverters.asScalaIteratorConverter

class KeyByAndCheckpointingTest extends AnyFlatSpec with Matchers {

  private val statePath = "file:///tmp/flink-state"

  "keyBy without checkpointing" should "use hashCode in the key" in {
    verify(None)
  }

  "keyBy with FsStateBackend" should "use hashCode in the key" in {
    val stateBackend: StateBackend =
      new FsStateBackend(statePath, true)
    verify(Some(stateBackend))
  }

  "keyBy with RocksDBStateBackend" should "use hashCode in the key" in {
    val stateBackend: StateBackend =
      new RocksDBStateBackend(statePath, true)
    verify(Some(stateBackend))
  }

  private def verify(stateBackend: Option[StateBackend]): Unit = {
    val env: StreamExecutionEnvironment =
      StreamExecutionEnvironment.getExecutionEnvironment
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    env.setParallelism(1)

    stateBackend match {
      case Some(sb) =>
        env.enableCheckpointing(600000)
        env.setStateBackend(sb)
      case None => // No checkpointing or state backend
    }

    val events = Seq(
      Event(1611145655000L, Key(1, 1)),
      Event(1611145655000L, Key(1, 2))
    )

    val resultStream = env
      .fromCollection(events)
      .assignTimestampsAndWatermarks(new TimestampExtractor)
      .keyBy(m => m.key)
      .timeWindow(
        Time.minutes(10),
        Time.minutes(10)
      )
      .process(new AggregationFunction)

    val result = DataStreamUtils
      .collect(resultStream.javaStream)
      .asScala
      .toSeq

    result shouldBe Seq(Output(Key(1, 1), 2))
  }
}
