package mainWindow.controller;

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class TcpPeer {

    private String peerName;
    private LinkedList<Socket> connectedPeers;
    private ServerSocket serverSocket;
    private File config;

    public TcpPeer(String peerName, File config) {
        this.peerName = peerName;
        this.config = config;
        startListener();
    }

    public TcpPeer(File config) {
        this.peerName = "Default Peer Name";
        startListener();
    }

    public void start() {

    }

    private void startListener() {
        Listener listener = new Listener();
        listener.start();
    }

    private class Listener extends Thread {

        private boolean isStoped = false;

        public void setStoped() {
            isStoped = true;
        }

        @Override
        public void run() {
            while (!isStoped) {
                // Listen
            }
        }
    }
}
