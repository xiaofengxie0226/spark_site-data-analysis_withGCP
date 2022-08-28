package com;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class PublisherLocal {
    public static void main(String[] args) throws Exception {
        //GCP Variables
        String project_id = "sha-dev-356212";
        String topic_id = "sparkMessage";
        publisher(project_id,topic_id);
    }

    public static void publisher(String project_id, String topic_id)
            throws IOException, ExecutionException,InterruptedException {

        TopicName topicname = TopicName.of(project_id, topic_id);
        List<ApiFuture<String>> messageIdFutures = new ArrayList<>();

        List<String> Advertising_platform = new ArrayList<>
                (Arrays.asList("Google", "Yahoo", "Youtube", "Bing", "Bilibili", "Twitter", "Instagram"));

        /*
        pub random bunk of messages in 1-minute
        1~1000 messages in a second
         */
        Random rnd = new Random();
        /*
        Provides an executor service for processing messages.
        The default executorProvider` used by the publisher has a default thread count of 5 * the number of processors available to the Java virtual machine.
        */
        ExecutorProvider executorProvider = InstantiatingExecutorProvider.newBuilder().setExecutorThreadCount(5).build();
        //`setExecutorProvider` configures an executor for the publisher.
        Publisher publisher = Publisher.newBuilder(topicname).setExecutorProvider(executorProvider).build();
        for (int t = 0; t < 60; t++) {
            Thread.sleep(1000); // time sleep 1 sec
            try {
                for (int i = 0; i < rnd.nextInt(1000) + 1; i++) {
                /*
                schedule publishing one message at a time : messages get automatically batched
                Message to send -> ByteString -> PubsubMessage
                 */
                //CurrentDay in yyyy-MM-dd HH:mm:ss format
                Date now = new Date();
                SimpleDateFormat dFormate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dayNow = dFormate.format(now);

                //user_id
                int userIndex = rnd.nextInt(1000) + 1;
                String user_id = "M" + userIndex;

                //Advertising_platform
                int adIndex = rnd.nextInt(7);
                String ad_platform = Advertising_platform.get(adIndex);

                String message = dayNow + "," + user_id + "," + ad_platform;
                ByteString data = ByteString.copyFromUtf8(message);
                PubsubMessage pubsubmessage = PubsubMessage.newBuilder().setData(data).build();

                // Once published, returns a server-assigned message id (unique within the topic)
                ApiFuture<String> messageIdFuture = publisher.publish(pubsubmessage);
                messageIdFutures.add(messageIdFuture);
                }
            } finally {
                // Wait on any pending publish requests.
                List<String> messageIds = ApiFutures.allAsList(messageIdFutures).get();
                System.out.println("Published " + messageIds.size() + " messages with concurrency control.");
                messageIdFutures.clear();
            }
            // When finished with the publisher, shutdown to free up resources.
        }
        publisher.shutdown();
        publisher.awaitTermination(1, TimeUnit.MINUTES);

    }
}
