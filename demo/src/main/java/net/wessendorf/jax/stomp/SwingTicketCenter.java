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
package net.wessendorf.jax.stomp;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import java.util.Properties;


public class SwingTicketCenter {

    //member:
    JTextField messagefield;
    Connection connection = null;
    InitialContext initialContext = null;

    public SwingTicketCenter() {
    }

    private void setupJMS() {
        try {
            Properties props = new Properties();
            props.put("java.naming.factory.initial","org.jnp.interfaces.NamingContextFactory");
            props.put("java.naming.provider.url", "jnp://localhost:1099");
            props.put("java.naming.factory.url.pkgs","org.jboss.naming:org.jnp.interfaces");
            initialContext =  new InitialContext(props);
        } catch (NamingException e) {
            e.printStackTrace();
        }

    }


    private void init(){
        this.setupJMS();

        // Basic form create
        JFrame frame = new JFrame("Service Center App");
        frame.setSize(300,300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Creating the grid
        JPanel panel = new JPanel(new GridBagLayout());
        frame.getContentPane().add(panel, BorderLayout.NORTH);
        GridBagConstraints c = new GridBagConstraints();

        // Create some elements
        JLabel label = new JLabel();
        label.setText("Message to engineer:");
        c.gridx = 0;
        c.gridy = 1;
        panel.add(label,c);

        // Create some elements
        messagefield = new JTextField(10);
        c.gridx = 1;
        c.gridy = 1;
        panel.add(messagefield,c);


        JButton loginInput = new JButton("Submit");
        c.gridx = 0;
        c.gridy = 2;
        loginInput.addActionListener(new LoginButton());
        panel.add(loginInput,c);


        frame.setVisible(true);
    }
    public static void main(String[] args){
        SwingTicketCenter form = new SwingTicketCenter();
        form.init();
    }

    class LoginButton implements ActionListener{

        public void actionPerformed(ActionEvent e){
            try {

                try{












                    Topic topic = (Topic)initialContext.lookup("/topic/chat");
                    ConnectionFactory cf = (ConnectionFactory)initialContext.lookup("/ConnectionFactory");
                    connection = cf.createConnection();
                    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                    MessageProducer producer = session.createProducer(topic);

                    TextMessage message = session.createTextMessage(messagefield.getText());
                    producer.send(message);

                    connection.start();


                    // clear:
                    messagefield.setText("");

                }
                finally
                {
                    connection.close();
                }

            }catch(Exception se) {
                se.printStackTrace();
            }


        }
    }
}