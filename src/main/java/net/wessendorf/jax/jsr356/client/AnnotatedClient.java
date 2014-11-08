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
package net.wessendorf.jax.jsr356.client;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

@ClientEndpoint
public class AnnotatedClient {

    private static final Logger logger = Logger.getLogger(AnnotatedClient.class.getName());

    @OnOpen
    public void connect(Session session) {
        logger.info("Connected");
        try {
            session.getBasicRemote().sendText("Howdy!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void receiveMessage(String message, Session session) throws IOException {

        logger.info("Received: " + message);

        for (Session peer : session.getOpenSessions()) {
           if (! peer.equals(session)) {
                peer.getBasicRemote().sendText(message);
            }
        }
    }

    public static void main(String... args) throws IOException {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        try {

            Session session  = container.connectToServer(new AnnotatedClient(), new URI("ws://localhost:8080/echo"));


        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (DeploymentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Thread.currentThread().join();
        }
        catch (InterruptedException e) {
            // clean up......
        }

    }

}
