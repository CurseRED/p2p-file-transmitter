package mainWindow.model;

import java.io.*;
import java.net.*;
import java.util.LinkedList;

public class TcpPeer {

    private static final int BUFFER_SIZE = 1024;
    private static final int DEFAULT_PORT = 7777;
    private static final String DEFAULT_GROUP = "230.0.0.0";
    private LinkedList<PeerInfo> availablePeers;
    private LinkedList<Socket> clientSockets;
    private LinkedList<Socket> connectedSockets;
    private ServerSocket serverSocket;
    private MulticastReceiver multicastReceiver;
    private LinkedList<FileSender> fileSenders;
    private LinkedList<String> localFiles;
    private LinkedList<FileInfo> availableFiles;
    private int id;

    public TcpPeer() throws IOException {
        availablePeers = new LinkedList<>();
        clientSockets = new LinkedList<>();
        connectedSockets = new LinkedList<>();
        multicastReceiver = new MulticastReceiver();
        fileSenders = new LinkedList<>();
        availableFiles = new LinkedList<>();
    }

    public void start() throws IOException {
        multicastReceiver.start();
    }

    public void stop() throws IOException {
        multicastReceiver.setStoped();
        for (FileSender fileSender: fileSenders) {
            fileSender.setStoped();
        }
    }

    public void setLocalFiles(LinkedList<String> localFiles) {
        this.localFiles = localFiles;
    }

    public void startServerSocket() throws IOException {
        serverSocket = new ServerSocket(DEFAULT_PORT);
        ServerThread serverThread = new ServerThread();
        serverThread.start();
    }

    public void update() throws IOException {
        multicast();
        System.out.println(availablePeers);
        System.out.println(availableFiles);
        if (serverSocket == null) {
            availableFiles = getFileList();
        }
    }

    public void addPeer(PeerInfo peerInfo) {
        availablePeers.add(peerInfo);
    }

    public void addConnectedSocket(Socket socket) {
        clientSockets.add(socket);
    }

    public LinkedList<PeerInfo> getAvailablePeers() {
        return availablePeers;
    }

    /**
     * Sends datagram packet to all peers in the local network
     * @throws IOException
     */
    private void multicast() throws IOException {
        DatagramSocket socket = new DatagramSocket();
        InetAddress group = InetAddress.getByName(DEFAULT_GROUP);
        if (serverSocket != null) {
            byte[] buffer = objectToByteArray(serverSocket.getInetAddress());
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, DEFAULT_PORT);
            socket.send(packet);
        }
        socket.close();
    }


    /**
     * MulticastReceiver class listens for UDP multicasts from other peers
     * and when received a datagram adds it to availablePeers list
     */
    public class MulticastReceiver extends Thread {

        private MulticastSocket socket = null;
        private byte[] buf = new byte[BUFFER_SIZE];
        private boolean isStoped = false;

        public void setStoped() {
            isStoped = true;
        }

        public void run() {
            try {
                socket = new MulticastSocket(DEFAULT_PORT);
                InetAddress group = InetAddress.getByName(DEFAULT_GROUP);
                socket.joinGroup(group);
                while (!isStoped) {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    InetAddress inetAddress = null;
                    try {
                        inetAddress = (InetAddress) byteArrayToObject(packet.getData());
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    PeerInfo peerInfo = new PeerInfo(inetAddress, DEFAULT_PORT);
                    boolean flag = true;
                    for (PeerInfo info: availablePeers) {
                        if (info.equals(peerInfo)) {
                            flag = false;
                        }
                    }
                    if (flag) {
                        Socket socket = new Socket(peerInfo.getAddress(), peerInfo.getPort());
                        connectedSockets.add(socket);
                        addPeer(peerInfo);
                        multicast();
                    }
                }
                socket.leaveGroup(group);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                socket.close();
            }
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    private byte[] objectToByteArray(Object o) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(o);
        return byteArrayOutputStream.toByteArray();
    }

    private Object byteArrayToObject(byte[] arr) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arr);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        return objectInputStream.readObject();
    }

    public LinkedList<FileInfo> getFileList() throws IOException {
        LinkedList<FileInfo> availableFiles = new LinkedList<>();
        for (Socket socket: connectedSockets) {
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bw.write("!FILES\n");
            bw.flush();
            System.out.println("SENDED !FILES TO SERVER");
            String file = br.readLine();
            /*while ((file = br.readLine()) != null) {
                availableFiles.add(new FileInfo(file));
                System.out.println(file);
            }*/
            availableFiles.add(new FileInfo(file));
            br.close();
            bw.close();
        }
        return availableFiles;
    }

    public void downloadFile() {

    }

    public LinkedList<FileInfo> getAvailableFiles() {
        return availableFiles;
    }

    public class FileSender extends Thread {

        private boolean isStoped = false;
        private BufferedReader br;
        private BufferedWriter bw;

        public void setSocket(Socket socket) throws IOException {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        }

        public void setStoped() throws IOException {
            isStoped = true;
            br.close();
            bw.close();
        }

        public void run() {
            while (!isStoped) {
                try {
                    String msg = br.readLine();
                    System.out.println(msg);
                    if (msg.equals("!FILES")) {
                        for (String filename: localFiles) {
                            bw.write(filename + "\n");
                            bw.flush();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class ServerThread extends Thread {

        private boolean isStoped = false;

        public void setStoped() throws IOException {
            isStoped = true;
        }

        public void run() {
            while (!isStoped) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    clientSockets.add(clientSocket);
                    FileSender fileSender = new FileSender();
                    fileSender.setSocket(clientSocket);
                    fileSenders.add(fileSender);
                    fileSender.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
