module org.chiches.asycsyyc {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens org.chiches.asycsyyc to javafx.fxml;
    exports org.chiches.asycsyyc;
}