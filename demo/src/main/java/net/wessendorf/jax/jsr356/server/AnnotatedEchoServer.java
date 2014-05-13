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
package net.wessendorf.jax.jsr356.server;

import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.logging.Logger;

@ServerEndpoint("/echo")
public class AnnotatedEchoServer {

    private static final Logger logger = Logger.getLogger(AnnotatedEchoServer.class.getName());

    @OnOpen
    public void connect(Session session) {
        logger.info("Da...");
    }

    @OnMessage
    public void receiveTextMessage(String message, Session client) {
        logger.info("Processing payload [" + message + "]");
        try {
            for (Session peer : client.getOpenSessions()) {

                if (! peer.equals(client)) {
                    peer.getBasicRemote().sendText(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnError
    public void errorHandler(Throwable throwable) {
        logger.severe("something went wrong: " + throwable);
    }
}
