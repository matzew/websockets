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
package net.wessendorf.jax.helloworld;

import io.undertow.Undertow;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedBinaryMessage;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.CloseMessage;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import io.undertow.websockets.spi.WebSocketHttpExchange;

import static io.undertow.Handlers.path;
import static io.undertow.Handlers.websocket;

import java.io.IOException;
import java.util.logging.Logger;

public class EchoServer {

    private static final Logger logger = Logger.getLogger(EchoServer.class.getName());


    public static void main(String... args) {

        Undertow server = Undertow.builder()
                .addHttpListener(8080, "0.0.0.0")
                .setHandler(path()
                        .addPrefixPath("/echo", websocket(new WebSocketConnectionCallback() {

                            @Override
                            public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {

                                logger.info(String.format("Connection established from '%s'", channel.getPeerAddress()));

                                channel.getReceiveSetter().set(new AbstractReceiveListener() {

                                    @Override
                                    protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) {
                                        final String receivedTextPayload = message.getData();

                                        logger.info(String.format("Got text ('%s') from '%s'", receivedTextPayload, channel.getPeerAddress()));
                                        WebSockets.sendText(receivedTextPayload, channel, null);
                                    }

                                    @Override
                                    protected void onFullBinaryMessage(WebSocketChannel channel, BufferedBinaryMessage message) throws IOException {

                                        logger.info(String.format("Got binary from '%s'", channel.getPeerAddress()));
                                        WebSockets.sendBinary(message.getData().getResource(), channel, null);
                                    }

                                    @Override
                                    protected void  onCloseMessage(CloseMessage cm, WebSocketChannel channel) {
                                        logger.info(String.format("Close request from '%s'", channel.getPeerAddress()));
                                    }

                                });
                                channel.resumeReceives();

                            }
                        }))).build();


        server.start();

        try {
            Thread.currentThread().join();
        }
        catch (InterruptedException e) {
            logger.info("shutting down");
            server.stop();
        }

    }


}
