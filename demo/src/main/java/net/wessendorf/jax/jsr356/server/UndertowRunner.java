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

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainer;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;
import org.xnio.ByteBufferSlicePool;

import javax.servlet.ServletException;

public class UndertowRunner {

    public static void main(String... args) throws ServletException {

        final ServletContainer container = ServletContainer.Factory.newInstance();

        DeploymentInfo builder = new DeploymentInfo()
                .setClassLoader(UndertowRunner.class.getClassLoader())
                .setContextPath("/")
                .addServletContextAttribute(WebSocketDeploymentInfo.ATTRIBUTE_NAME,
                        new WebSocketDeploymentInfo()
                                .setBuffers(new ByteBufferSlicePool(100, 1000))
                                .addEndpoint(AnnotatedEchoServer.class)
                )
                .setDeploymentName("servletContext.war");


        DeploymentManager manager = container.addDeployment(builder);
        manager.deploy();


        PathHandler path = Handlers.path(Handlers.redirect("/"))
                .addPrefixPath("/", manager.start());


        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(path)
                .build();
        server.start();


    }

}
