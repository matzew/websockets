/**
 * Copyright Matthias Weßendorf.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.wessendorf.jax.status;

import io.undertow.Undertow;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.StreamSourceFrameChannel;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import org.jboss.aerogear.unifiedpush.JavaSender;
import org.jboss.aerogear.unifiedpush.SenderClient;
import org.jboss.aerogear.unifiedpush.message.MessageResponseCallback;
import org.jboss.aerogear.unifiedpush.message.UnifiedMessage;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static io.undertow.Handlers.path;
import static io.undertow.Handlers.websocket;

public class NotificationDistributionServer {

    private static final Logger logger = Logger.getLogger(NotificationDistributionServer.class.getName());


    public static void main(String... args) {
        // Java7 bug...
        System.setProperty("jsse.enableSNIExtension", "false");


        final Set<WebSocketChannel> channels = new HashSet<>();

        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(path()
                        .addPrefixPath("/echo", websocket(new WebSocketConnectionCallback() {

                            @Override
                            public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {


                                // stash the clients
                                channels.add(channel);


                                channel.getReceiveSetter().set(new AbstractReceiveListener() {

                                    // we just need to remove the client, on disconnect

                                    @Override
                                    protected void onClose(WebSocketChannel webSocketChannel, StreamSourceFrameChannel channel) throws IOException {
                                        channels.remove(webSocketChannel);
                                    }
                                });



                                channel.resumeReceives();
                            }
                        }))).build();


        server.start();


        ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
        ses.scheduleAtFixedRate(
                new NotificationRunnable(channels),
                5, // let's wait some seconds
                2, // period between the updates
                TimeUnit.SECONDS);



        try {
            Thread.currentThread().join();
        }
        catch (InterruptedException e) {
            logger.info("shutting down");
            server.stop();
        }

    }


    private static class NotificationRunnable implements Runnable {
        private final Set<WebSocketChannel> channels;

        public NotificationRunnable(Set<WebSocketChannel> channels) {
            this.channels = channels;
        }

        public void run() {

            if (! channels.isEmpty()) {

                // we have online users.......
                logger.info("Processing {" + channels.size() + "} users");

                for(WebSocketChannel channel : channels) {
                    WebSockets.sendText(new Date().toString(), channel, null);
                }


            } else {
                // inform all the offline clients:

                JavaSender sender =
                        new SenderClient("https://summit-pushee.rhcloud.com/");


                UnifiedMessage unifiedMessage = new UnifiedMessage.Builder()
                        .pushApplicationId("8122513a-3f49-4346-856f-20d0fc23c499")
                        .masterSecret("730ce84c-6cb3-4bce-b30b-de4d983dd6b5")
                        .alert("Hello JAX folks!")
                        .sound("default")
                        .build();


                sender.send(unifiedMessage, new MessageResponseCallback() {
                    @Override
                    public void onComplete(int statusCode) {
                        logger.info("UPS told me: " + statusCode);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });




            }


        }
    }
}
