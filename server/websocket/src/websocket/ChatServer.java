package websocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class ChatServer extends WebSocketServer {

    static int num = 0;
    static LinkedList<RoomUser> list = new LinkedList<RoomUser>();
    static LinkedList<RoomUser> list2 = new LinkedList<RoomUser>();
    static LinkedList<RoomUser> tmp = new LinkedList<RoomUser>();
    private String room;

    class RoomUser {

        String room = "0";
        WebSocket conn;
        String id;
        String num;
        String s = "0";

        public RoomUser(WebSocket conn, String room) {
            this.conn = conn;
            this.room = room;

        }

        public void setid(String id, String num) {
            this.id = id;
            this.num = num;
            if (!"Customer".equals(id)) {
                this.s = "2";
            }
        }

        public String getuser() {
            if ("Customer".equals(id)) {
                return "客戶 " + num;
            } else {
                return "客服人員 " + num;
            }

        }

        public void sets() {
            this.s = "1";
        }

        public void setsoff() {
            this.s = "0";
        }
    }

    public ChatServer(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
    }

    public ChatServer(InetSocketAddress address) {
        super(address);
    }

    public void onOpen(WebSocket conn, ClientHandshake handshake) {

        RoomUser user = new RoomUser(conn, Integer.toString(num));
        tmp.add(user);
        user.room = Integer.toString(num);

//        this.sendToRoom(user.room, " In " + num);
//        num++;
//        this.sendToAll(Integer.toString(num));
//        this.sendToAll("new connection: " + handshake.getResourceDescriptor());
//        System.out.println(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");
//                System.out.println(Integer.toString(num));
    }

    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        RoomUser user = this.findRoomUser(conn);
        if (user == null) {
        } else {
            if ("Customer".equals(user.id)) {  
                this.sendToAll("-sever,del,customer," + user.room + "," + user.num);
                this.sendToRoom("999", "[" + getDateTime() + "]" + user.getuser() + " 已離開！", "s");
                this.sendToRoom(user.room, "[" + getDateTime() + "]" + user.getuser() + " 已離開！", "customer");
                list.remove(user);
                tmp.remove(user);
                
              
                RoomCheck(user.room);

                Roomlist(conn);

            } else {

                if (user.room == "999") {

                    this.sendToAll("-sever,del,service," + user.room + "," + user.num);
                    this.sendToRoom("999", "[" + getDateTime() + "]" + user.getuser() + " 已離開！", "s");
                    list2.remove(user);
                    tmp.remove(user);
                    Roomlist(conn);
                } else {

                    sendToAll("-sever,edi,service," + user.room + "," + user.num);  //更改客服 給js
                    RoomCheck(user.room);
                    this.sendToAll("-sever,del,service," + user.room + "," + user.num);
                    this.sendToRoom("999", "[" + getDateTime() + "]" + user.getuser() + " 已離開！", "s");
                    this.sendToRoom(user.room, "[" + getDateTime() + "]" + user.getuser() + " 已離開！", "customer");

                    list.remove(user);
                    tmp.remove(user);
                    Roomlist(conn);
                }

            }
        }

    }

    public void onMessage(WebSocket conn, String message) {
        RoomUser user = this.findRoomUser(conn);

        if (message.equals("//servicerenter")) {  //客服 = 房號 = 999 編號 =num
            user.room = "999";
            user.setid("Servicer", Integer.toString(num));
            list2.add(user);

//            this.sendToRoom("999", "-sever,add,service," + user.num, "s");
//            this.sendToRoom(user.room, "[" + getDateTime() + "]" + "客服人員房間辣　Room:" + user.room, "s");
            this.sendToRoom(user.room, "[" + getDateTime() + "]" + user.getuser() + "已登入", "s");

            Roomlist(conn);
            num++;
            return;
        } else if (message.equals("//userenter")) {  //客戶 = 房號 = 編號

            user.room = Integer.toString(num); //房號
            user.setid("Customer", Integer.toString(num));
            list.add(user);

//            this.sendToRoom("999", "-sever,add,customer," + user.room, "s");
            this.sendToRoom(user.room, "[" + getDateTime() + "]" + user.getuser() + " 您好！", "customer");
            this.sendToRoom(user.room, "[" + getDateTime() + "]" + "正在幫你轉接客服人員中，請稍後... ", "customer");
            Roomlist(conn);

            num++;
            return;
        } else if (message.startsWith("r:")) {

            if (user.room == "999") {
                String room = message.substring(2);
                if (RoomCheck(room)) {

                    user.room = room;
                    list.add(user);
                    list2.remove(user);
                    sendToAll("-sever,edi,service," + user.room + "," + user.num);  //更改客服 給js
                    Roomlist(conn);

                    this.sendToRoom(user.room, "[" + getDateTime() + "]" + user.getuser() + "已連線....", "customer");
                    this.sendToRoom(user.room, "[" + getDateTime() + "]" + user.getuser() + "很高興為您服務", "customer");
                    return;
                } else {
                    RoomCheck(room);
                    this.sendToRoom(user.room, "[" + getDateTime() + "]" + "目前已有客服人員摟！", "ss");
                }
            } else {

            }

        } else if (message.equals("-server,home")) {
            if (user.room == "999") {
            } else {
                this.sendToRoom(user.room, "[" + getDateTime() + "]" + user.getuser() + " 已離開！", "customer");
                RoomCheck(user.room);

                user.room = "999";

                list.remove(user);
                list2.add(user);
                sendToAll("-sever,edi,service," + user.room + "," + user.num);  //更改客服 給js
//                this.sendToRoom(user.room, "[" + getDateTime() + "]" + "客服人員房間辣" , "s");
                Roomlist(conn);
                return;
            }

        } else {
            if ("0".equals(user.s)) {
                this.sendToRoom(user.room, "[" + getDateTime() + "]" + user.getuser() + ":" + message, "customer");
                this.sendToRoom(user.room, "正在幫你轉接客服人員請稍後...", "customer");
            }
            if ("1".equals(user.s)) {
                this.sendToRoom(user.room, "[" + getDateTime() + "]" + user.getuser() + ":" + message, "customer");
            }

            if ("2".equals(user.s) && "Servicer".equals(user.id)) {
                this.sendToRoom(user.room, "[" + getDateTime() + "]" + user.getuser() + ":" + message, "customer");
            }

            if ("999".equals(user.room)) {
                this.sendToRoom(user.room, "[" + getDateTime() + "]" + user.getuser() + ":" + message, "ss");
            }
        }

//        String conip = conn.getRemoteSocketAddress().getAddress().getHostAddress();
//        String username = "[" + conip + "] Room: " + user.room;
    }

    public void onFragment(WebSocket conn, Framedata fragment) {
//		System.out.println( "received fragment: " + fragment );
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        WebSocketImpl.DEBUG = true;
        int port = 8887; // 843 flash policy port
        try {
            port = Integer.parseInt(args[ 0]);
        } catch (Exception ex) {
        }

        ChatServer s = new ChatServer(port);
        s.start();
        //System.out.println("ChatServer started on port: " + s.getPort());

        BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String in = sysin.readLine();

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

    public void onError(WebSocket conn, Exception ex) {
//
//        ex.printStackTrace();
//        if (conn != null) {
//          
//        }
//      
    }

    public RoomUser findRoomUser(WebSocket conn) {
        for (int i = 0; i < tmp.size(); i++) {
            RoomUser user = tmp.get(i);
            if (user.conn == conn) {
                return user;
            }
        }
        return null;
    }

    public boolean RoomCheck(String num) {
        for (RoomUser u : list) {
            if ("Customer".equals(u.id) && (u.room == null ? num == null : u.room.equals(num))) {
                if (u.s == "0") {
                    u.sets();
                    sendToAll("-sever,edi,customer," + u.room + "," + u.num + ',' + u.s);
                    return true;  //檢查房間是否有客戶 有則將user.s 設成1 無則回傳Fasle
                } else if (u.s == "1") {
                    u.setsoff();
                    sendToAll("-sever,edi,customer," + u.room + "," + u.num + ',' + u.s);
                } else {

                }

            }
        }
        return false;
    }

    public void sendToRoom(String room, String text, String a) {
        if (a == "customer") {
            for (int i = 0; i < list.size(); i++) {
                RoomUser user = list.get(i);
                if (user.room.equals(room)) {
                    user.conn.send(text);
                }
            }
        } else {
            for (int i = 0; i < list2.size(); i++) {
                RoomUser user = list2.get(i);
                if (user.room.equals(room)) {
                    user.conn.send(text);
                }
            }
        }

    }

    public void Roomlist(WebSocket conn) {  //刷新列表　並且取得目前所有人員狀態
//        sendToRoom("999", "======目前時間" + getDateTime() + "======", "ss");
//        sendToRoom("999", "------" + "客戶清單" + "-----", "ss");
        for (RoomUser u : list) {
            if ("1".equals(u.s)) {
//                sendToRoom("999", "目前room:" + u.room + "已有客服人員", "ss");
                sendToAll("-sever,add,customer," + u.room + "," + u.num);  //新增客戶 給js
            } else if ("0".equals(u.s)) {
//                sendToRoom("999", "目前room:" + u.room + "缺乏客服人員", "ss");
                sendToAll("-sever,add,customer," + u.room + "," + u.num);  //新增客戶 給js
                sendToAll("-sever,edi,customer," + u.room + "," + u.num + ',' + "0");
            } else if ("Servicer".equals(u.id)) {
                sendToAll("-sever,add,service," + u.room + "," + u.num);  //新增客服 給js
//                sendToRoom("999", "" + u.getuser() + "正在" + u.room, "ss");
            } else {

            }
        }
//        sendToRoom("999", "-----" + "客服人員" + "-----", "ss");
        for (RoomUser u : list2) {
            sendToAll("-sever,add,service," + u.room + "," + u.num);  //新增客服 給js
//            this.sendToRoom("999", "-sever,add,service," + u.room + "," + u.num, "s");  //新增客服 給js
//            sendToRoom("999", u.getuser() + "閒置中", "ss");
        }
//        sendToRoom("999", " ========================", "ss");
    }

    public String getDateTime() {
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        Date date = new Date();
        String strDate = sdFormat.format(date);
        return strDate;
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
}