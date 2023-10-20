module com.example.ginrummy {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires lombok;
    requires java.rmi;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.xml;

    opens com.ginrummy to javafx.fxml;
    exports com.ginrummy;
    exports com.ginrummy.Enums;
    opens com.ginrummy.Enums to javafx.fxml;
    exports com.ginrummy.Interfaces;
    opens com.ginrummy.Interfaces to javafx.fxml;
    exports com.ginrummy.Models;
    opens com.ginrummy.Models to javafx.fxml;
    exports com.ginrummy.ChatFeatureRMI;
    opens com.ginrummy.ChatFeatureRMI to javafx.fxml;
}