package customerservicewsserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.LinkedList;
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

class RoomUser {

    String room;
    WebSocket conn;

    public RoomUser(WebSocket conn, String room) {
        this.conn = conn;
        this.room = room;
    }
}


public class CustomerServiceWsServer extends WebSocketServer {

    LinkedList<RoomUser> list = new LinkedList<RoomUser>();

    public CustomerServiceWsServer(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
    }

    public CustomerServiceWsServer(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        RoomUser user = new RoomUser(conn, "default");
        list.add(user);

        //this.sendToAll( "new connection: " + handshake.getResourceDescriptor() );
        //System.out.println(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        RoomUser user = this.findRoomUser(conn);
        list.remove(user);
        
        //System.out.println(conn + " has left the room!");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        RoomUser user = this.findRoomUser(conn);

        // ROOM:abcd
        if (message.startsWith("ROOM:")) {
            String room = message.substring(4);
            user.room = room;
            return;
        }

        this.sendToRoom(user.room, message);
        //this.sendToAll(message);

        //System.out.println(conn + ": " + message);
    }

    public void onFragment(WebSocket conn, Framedata fragment) {
        System.out.println("received fragment: " + fragment);
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        WebSocketImpl.DEBUG = true;
        int port = 8887; // 843 flash policy port
        try {
            port = Integer.parseInt(args[ 0]);
        } catch (Exception ex) {
        }
        CustomerServiceWsServer s = new CustomerServiceWsServer(port);
        s.start();
        System.out.println("ChatServer started on port: " + s.getPort());

        BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String in = sysin.readLine();
            s.sendToAll(in);
            if (in.equals("exit")) {
                s.stop();
                break;
            } else if (in.equals("restart")) {
                s.stop();
                s.start();
                break;
            }
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
        if (conn != null) {
            // some errors like port binding failed may not be assignable to a specific websocket
        }
    }

    /**
     * Sends <var>text</var> to all currently connected WebSocket clients.
     *
     * @param text The String to send across the network.
     * @throws InterruptedException When socket related I/O errors occur.
     */
    public void sendToAll(String text) {
        Collection<WebSocket> con = connections();
        synchronized (con) {
            for (WebSocket c : con) {
                c.send(text);
            }
        }
    }

    public void sendToRoom(String room, String text) {
        for (int i = 0; i < list.size(); i++) {
            RoomUser user = list.get(i);
            if (user.room.equals(room)) {
                user.conn.send(text);
            }
        }
    }

    public RoomUser findRoomUser(WebSocket conn) {
        for (int i = 0; i < list.size(); i++) {
            RoomUser user = list.get(i);
            if (user.conn == conn) {
                return user;
            }
        }
        return null;
    }
}
