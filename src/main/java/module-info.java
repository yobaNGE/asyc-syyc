module org.chiches.asycsyyc {
    requires javafx.controls;
    requires javafx.fxml;
    requires aparapi;


    opens org.chiches.asycsyyc to javafx.fxml, aparapi;
    exports org.chiches.asycsyyc;
}