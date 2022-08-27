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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
        Publisher publisher = null;
        List<ApiFuture<String>> messageIdFutures = new ArrayList<>();

        try {
            /*
            Provides an executor service for processing messages.
            The default executorProvider` used by the publisher has a default thread count of 5 * the number of processors available to the Java virtual machine.
             */
            ExecutorProvider executorProvider = InstantiatingExecutorProvider.newBuilder().setExecutorThreadCount(5).build();
            //`setExecutorProvider` configures an executor for the publisher.
            publisher = Publisher.newBuilder(topicname).setExecutorProvider(executorProvider).build();

            /*
            schedule publishing one message at a time : messages get automatically batched
            Message to send -> ByteString -> PubsubMessage
             */
            for (int i = 0; i<10; i++){
                //CurrentDay in yyyy-MM-dd HH:mm:ss format
                Date now = new Date();
                SimpleDateFormat dFormate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dayNow = dFormate.format(now);

                MessageCreate.readJson();

                String message = dayNow + "," + "user_id" + "," + "pay_list" + i;
                ByteString data = ByteString.copyFromUtf8(message);
                PubsubMessage pubsubmessage = PubsubMessage.newBuilder().setData(data).build();

                // Once published, returns a server-assigned message id (unique within the topic)
                ApiFuture<String> messageIdFuture = publisher.publish(pubsubmessage);
                messageIdFutures.add(messageIdFuture);
            }
        } finally {
            if (publisher != null){
                // Wait on any pending publish requests.
                List<String> messageIds = ApiFutures.allAsList(messageIdFutures).get();
                System.out.println("Published " + messageIds.size() + " messages with concurrency control.");

                // When finished with the publisher, shutdown to free up resources.
                publisher.shutdown();
                publisher.awaitTermination(1, TimeUnit.MINUTES);
            }
        }
    }
}
