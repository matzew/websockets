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

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

public class ProgrammaticClient extends Endpoint {

    private static final Logger logger = Logger.getLogger(ProgrammaticClient.class.getName());

    @Override
    public void onOpen(final Session session, EndpointConfig endpointConfig) {

        try {
            session.getBasicRemote().sendText("Echo.....");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // add text handler:
        session.addMessageHandler(new MessageHandler.Whole<String>() {

            @Override
            public void onMessage(String text) {
                logger.info("Receiving message: " + text);
                try {
                    // thanks for the fish!
                    session.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    public static void main(String... args) {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        try {
            // ws://localhost:8080/echo
            container.connectToServer(ProgrammaticClient.class, null, new URI("ws://localhost:8080/echo"));

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
