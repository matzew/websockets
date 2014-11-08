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
package net.wessendorf.jax.jsr356.client.simplified;

import net.wessendorf.websocket.SimpleWebSocketClient;
import net.wessendorf.websocket.WebSocketHandlerAdapter;

import java.net.URISyntaxException;
import java.util.logging.Logger;

public class    SimpleClient {

    private static final Logger logger = Logger.getLogger(SimpleClient.class.getName());

    public static void main(String... args) throws URISyntaxException {

        // create the WS object:
        final SimpleWebSocketClient client = new SimpleWebSocketClient("ws://localhost:8080/echo");


        // Attach the listeners that you need (-> Adapter ;-))
        client.setWebSocketHandler(new WebSocketHandlerAdapter() {
            @Override
            public void onOpen() {
                client.sendText("Hello dude!!"); // ship it!!
            }

            @Override
            public void onMessage(String message) {
                logger.info("Got: " + message);

            }
        });

        // connect
        client.connect();


        try {
            Thread.currentThread().join();
        }
        catch (InterruptedException e) {
            logger.info("shutting down");
            client.close();
        }
    }
}
