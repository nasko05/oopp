package client.utils;

import client.exceptions.ConnectionNotOpenException;
import client.exceptions.NullMessageModelClassType;
import client.exceptions.UnsupportedPayloadType;
import client.exceptions.WebSocketConnectionException;
import commons.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class WebSocketClientConfig {

    private StompSession stompSession = null;
    private UUID uuid;

    /**
     * Connects to a WebSocket server
     *
     * @param url url
     */
    public void connect(String url) throws WebSocketConnectionException {
        var client = new StandardWebSocketClient();
        var stomp = new WebSocketStompClient(client);
        stomp.setMessageConverter(new MappingJackson2MessageConverter());
        try {
            stompSession =
                stomp.connect(url, new StompSessionHandlerAdapter() {
                }).get();
            uuid = UUID.randomUUID();
        } catch (ExecutionException | InterruptedException | IllegalStateException e) {
            throw new WebSocketConnectionException(
                "Error connecting to WebSocket at server address " + url, e);
        }
    }

    /**
     * Method that checks is there is an open WebSocket connection
     *
     * @return state of the connection
     */
    public boolean isConnected() {
        return stompSession != null && stompSession.isConnected();
    }

    /**
     * Helper method that sends message to the WebSocket Server Configuration
     *
     * @param payload message
     * @param <T>     the type of the message
     */
    public <T> void sendToOthers(T payload) {
        try {
            if (payload instanceof Board) {
                this.send("/api/board", payload);
            } else if (payload instanceof TaskList) {
                this.send("/api/taskList", payload);
            } else if (payload instanceof Task) {
                this.send("/api/task", payload);
            } else if (payload instanceof SubTask) {
                this.send("/api/subtask", payload);
            } else if (payload instanceof Tag) {
                this.send("/api/tag", payload);
            }
        } catch (ConnectionNotOpenException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Sends payload to a given destination
     *
     * @param dest    WebSocket Endpoint
     * @param payload object to be sent
     * @param <T>     type of the object
     */
    private <T> void send(String dest, T payload) throws ConnectionNotOpenException {
        if (this.stompSession == null) {
            throw new ConnectionNotOpenException(
                "Cannot send payload, because stompSession is null");
        } else {
            var headers = new StompHeaders();
            headers.add("destination", dest);
            headers.add("uuid", String.valueOf(uuid));
//            headers.setId(String.valueOf(uuid));
            stompSession.send(headers, payload);
        }
    }

    /**
     * Subscribes to a given message broker
     *
     * @param destination WebSocket Endpoint
     * @param type        Type of the object to receive
     * @param consumer    Callback that handles payloads
     * @param <T>         type of the payload
     */
    public <T> void subscribe(String destination, Class<T> type, Consumer<T> consumer) {
        try {
            if (stompSession == null) {
                throw new WebSocketConnectionException(
                    "There is no open connection! Cannot subscribe!");
            }
        } catch (WebSocketConnectionException e) {
            System.err.println(e.getMessage());
        }
        stompSession.subscribe(destination, new StompSessionHandlerAdapter() {
            /**
             * Returns the payload type
             * @param headers the headers of a message
             * @return the type of the payload
             */
            @Override
            public @NotNull Type getPayloadType(@NotNull StompHeaders headers) {
                try {
                    if (type == null) {
                        throw new NullMessageModelClassType("Message model class is null");
                    } else {
                        return type;
                    }
                } catch (NullMessageModelClassType e) {
                    System.err.println(e.getMessage());
                } catch (Exception e) {
                    System.err.println("Exception occurred when giving type of payload!");
                    System.err.println("Model message class type is null");
                }
                return Object.class;
            }

            /**
             * Handles the frame
             * @param headers the headers of the frame
             * @param payload the payload, or {@code null} if there was no payload
             */
            @Override
            public void handleFrame(@NotNull StompHeaders headers, Object payload) {
                try {
                    var senderUUID = headers.get("uuid").get(0);
                    if (!type.isInstance(payload)) {
                        throw new UnsupportedPayloadType("Payload is not of supported type!");
                    } else if(!senderUUID.equals(String.valueOf(uuid))){
                        consumer.accept(type.cast(payload));
                    }
                } catch (UnsupportedPayloadType e) {
                    System.err.println("Unsupported type of payload received");
                    System.err.println("Ignoring payload!");
                } catch (Exception e) {
                    System.err.println("Exception has occurred when handling payload");
                    System.err.println("Payload type is " + payload.getClass().toString());
                    e.printStackTrace();
                }
            }
        });
    }

}
