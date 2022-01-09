module com.example.map226mariaalexandra {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires org.apache.pdfbox;


    opens com.example.map226mariaalexandra to javafx.fxml;
    opens socialnetwork.domain to javafx.fxml;


    exports com.example.map226mariaalexandra;
    exports socialnetwork.domain;

}