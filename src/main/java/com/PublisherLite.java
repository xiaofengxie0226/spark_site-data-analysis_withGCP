//package com;
//
//import com.google.api.core.ApiFuture;
//import com.google.api.core.ApiFutures;
//import com.google.api.gax.rpc.ApiException;
//import com.google.cloud.pubsublite.CloudRegion;
//import com.google.cloud.pubsublite.CloudRegionOrZone;
//import com.google.cloud.pubsublite.CloudZone;
//import com.google.cloud.pubsublite.MessageMetadata;
//import com.google.cloud.pubsublite.ProjectNumber;
//import com.google.cloud.pubsublite.TopicName;
//import com.google.cloud.pubsublite.TopicPath;
//import com.google.cloud.pubsublite.cloudpubsub.Publisher;
//import com.google.cloud.pubsublite.cloudpubsub.PublisherSettings;
//import com.google.protobuf.ByteString;
//import com.google.pubsub.v1.PubsubMessage;
//
//import java.text.SimpleDateFormat;
//import java.util.*;
//import java.util.concurrent.ExecutionException;
//
//public class PublisherLite {
//    public static void main(String[] args) throws Exception {
//        // developer: Replace these variables before running the sample.
//        String cloudRegion = "us-central1";
//        char zoneId = 'b';
//        // Choose an existing topic for the publish example to work.
//        String topicId = "ad-plat";
//        long projectNumber = Long.parseLong("271982357043");
//        //100/times
//        int messageCount = 100;
//        // True if using a regional location. False if using a zonal location.
//        // https://cloud.google.com/pubsub/lite/docs/topics
//        boolean regional = false;
//
//        publisherExample(cloudRegion, zoneId, projectNumber, topicId, messageCount, regional);
//    }
//
//    // Publish messages to a topic.
//    public static void publisherExample(
//            String cloudRegion,
//            char zoneId,
//            long projectNumber,
//            String topicId,
//            int messageCount,
//            boolean regional)  throws ApiException, ExecutionException, InterruptedException {
//
//        CloudRegionOrZone location;
//        if (regional) {
//            location = CloudRegionOrZone.of(CloudRegion.of(cloudRegion));
//        } else {
//            location = CloudRegionOrZone.of(CloudZone.of(CloudRegion.of(cloudRegion), zoneId));
//        }
//
//        TopicPath topicPath =
//                TopicPath.newBuilder()
//                        .setProject(ProjectNumber.of(projectNumber))
//                        .setLocation(location)
//                        .setName(TopicName.of(topicId))
//                        .build();
//
//        Publisher publisher = null;
//        List<ApiFuture<String>> futures = new ArrayList<>();
//
//        try {
//            PublisherSettings publisherSettings =
//                    PublisherSettings.newBuilder().setTopicPath(topicPath).build();
//
//            publisher = Publisher.create(publisherSettings);
//
//            // Start the publisher. Upon successful starting, its state will become RUNNING.
//            publisher.startAsync().awaitRunning();
//
//            for (int i = 0; i < messageCount; i++) {
//                //create message
//                //CurrentDay in yyyy-MM-dd HH:mm:ss format
//                Date now = new Date();
//                SimpleDateFormat dFormate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                String dayNow = dFormate.format(now);
//
//                //user_id
//                Random rnd = new Random();
//                int userIndex = rnd.nextInt(1000) + 1;
//                String user_id = "M" + userIndex;
//
//                //Advertising_platform
//                List<String> Advertising_platform = new ArrayList<>
//                        (Arrays.asList("Google", "Yahoo", "Youtube", "Bing", "Bilibili", "Twitter", "Instagram"));
//                int adIndex = rnd.nextInt(7);
//                String ad_platform = Advertising_platform.get(adIndex);
//
//                String message = dayNow + "," + user_id + "," + ad_platform;
//
//                // Convert the message to a byte string.
//                ByteString data = ByteString.copyFromUtf8(message);
//                PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();
//
//                // Publish a message. Messages are automatically batched.
//                ApiFuture<String> future = publisher.publish(pubsubMessage);
//                futures.add(future);
//            }
//        } finally {
//            ArrayList<MessageMetadata> metadata = new ArrayList<>();
//            List<String> ackIds = ApiFutures.allAsList(futures).get();
//            for (String id : ackIds) {
//                // Decoded metadata contains partition and offset.
//                metadata.add(MessageMetadata.decode(id));
//            }
//            System.out.println(metadata + "\nPublished " + ackIds.size() + " messages.");
//
//            if (publisher != null) {
//                // Shut down the publisher.
//                publisher.stopAsync().awaitTerminated();
//                System.out.println("Publisher is shut down.");
//            }
//        }
//    }
//}
