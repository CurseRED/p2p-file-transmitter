package mainWindow.model;

import javafx.beans.property.SimpleStringProperty;

import java.net.Socket;

public class FileInfo {

    private SimpleStringProperty filename;

    public FileInfo(String filename) {
        this.filename = new SimpleStringProperty(filename);
    }

    public String getFilename() {
        return filename.get();
    }

    public SimpleStringProperty filenameProperty() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename.set(filename);
    }
}
