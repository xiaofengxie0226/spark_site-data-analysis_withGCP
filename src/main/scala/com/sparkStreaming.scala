//package com
//
//import org.apache.spark.SparkConf
//import org.apache.spark.storage.StorageLevel
//import org.apache.spark.streaming.{Seconds, StreamingContext}
//import org.apache.spark.streaming.dstream.DStream
//import org.apache.spark.streaming.pubsub.{PubsubUtils, SparkGCPCredentials}
//
//import java.nio.charset.StandardCharsets
//
//object sparkStreaming {
//  def createContext(projectID: String
//                    , windowLength: String
//                    , slidingInterval: String
//                    , checkpointDirectory: String): StreamingContext = {
//    // [START stream_setup]
//    val sparkConf = new SparkConf().setAppName("SparkStreamingFromPubsub")
//    val ssc = new StreamingContext(sparkConf, Seconds(slidingInterval.toInt))
//    //Set the checkpoint directory
//    ssc.checkpoint(checkpointDirectory)
//
//    //Create Streaming
//    val messageStream: DStream[String] = PubsubUtils.createStream(
//      ssc = ssc,
//      project = projectID,
//      None,
//      subscription = "sparkMessage-sub",
//      credentials = SparkGCPCredentials.builder.build(),
//      storageLevel = StorageLevel.MEMORY_AND_DISK_SER_2
//    ).map(message => new String(message.getData(), StandardCharsets.UTF_8))
//
//    //process the stream
//
//
//    ssc
//  }
//}
