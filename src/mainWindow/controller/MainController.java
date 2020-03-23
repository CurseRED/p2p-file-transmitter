package mainWindow.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import mainWindow.model.FileInfo;
import mainWindow.model.Info;
import mainWindow.model.Main;
import mainWindow.model.PeerInfo;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class MainController {

    private Main model;

    @FXML
    private Button startServerSocket;
    @FXML
    private Button connectButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button downloadButton;
    @FXML
    private TableView<FileInfo> tableView;

    public TableView<FileInfo> getTableView() {
        return tableView;
    }

    @FXML
    private void onClickStartServerSocket() {
        try {
            model.getTcpPeer().startServerSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onClickConnectButton(ActionEvent event) {
        model.connectToAllAvailablePeers();
    }

    @FXML
    private void onClickUpdateButton(ActionEvent event) {
        try {
            model.update();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onClickDownloadButton(ActionEvent event) {
        model.initializeTable();
    }

    public void printAvailablePeers(LinkedList<PeerInfo> peers) {

    }

    public void printAvailableFiles(LinkedList<File> files) {
        
    }

    public void setModel(Main model) {
        this.model = model;
    }
}
