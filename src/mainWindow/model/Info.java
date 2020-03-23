package mainWindow.model;

import javafx.beans.property.SimpleStringProperty;

public class Info {

    private SimpleStringProperty filename;

    public Info(String filename) {
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
