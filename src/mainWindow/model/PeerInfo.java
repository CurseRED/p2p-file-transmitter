package mainWindow.model;

import java.net.InetAddress;

public class PeerInfo {

    private InetAddress address;
    private int port;

    public PeerInfo(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "PeerInfo{" +
                "address=" + address +
                ", port=" + port +
                '}';
    }

    public boolean equals(PeerInfo peerInfo) {
        if (address.equals(peerInfo.address) && port == peerInfo.port)
            return true;
        return false;
    }
}
