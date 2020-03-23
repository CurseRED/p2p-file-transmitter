package mainWindow.model;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import mainWindow.controller.MainController;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;

public class Main extends Application {

    private static TcpPeer tcpPeer;
    private MainController controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainWindow/view/mainLayout.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 640, 480));
        primaryStage.show();
        File file = new File("config.cfg");
        //configInitialize(file);
        initializeController(controller);
        tcpPeer = new TcpPeer();
        tcpPeer.setLocalFiles(configInitialize(new File("./config.cfg")));
        tcpPeer.start();
    }

    public void update() throws IOException {
        getTcpPeer().update();
        initializeTable();
    }

    public void initializeTable() {
        ObservableList<FileInfo> files = FXCollections.observableList(getTcpPeer().getAvailableFiles());
        controller.getTableView().setItems(files);
        TableColumn<FileInfo, String> column = new TableColumn<>("Файл");
        column.setCellValueFactory(new PropertyValueFactory<FileInfo, String>("filename"));
        controller.getTableView().getColumns().add(column);
    }

    public TcpPeer getTcpPeer() {
        return tcpPeer;
    }

    private void initializeController(MainController controller) {
        controller.setModel(this);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        tcpPeer.stop();
        Platform.exit();
        System.exit(0);
    }

    /**
     * Method initializes config file that contains filesand directories
     * that people allowed to download from user computer
     * @param file config file directory
     */
    private LinkedList<String> configInitialize(File file) {
        LinkedList<String> list = new LinkedList<>();
        try {
            boolean result = file.createNewFile();
            if (!result) {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String line;
                while ((line = br.readLine()) != null) {
                    list.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void connectToAllAvailablePeers() {
        LinkedList<PeerInfo> infoList = tcpPeer.getAvailablePeers();
        for (PeerInfo info: infoList) {
            try {
                Socket socket = new Socket(info.getAddress(), info.getPort());
                tcpPeer.addConnectedSocket(socket);
            } catch (IOException e) {
                System.out.println("Socket is unavailable!");
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
