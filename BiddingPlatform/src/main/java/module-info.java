module com.bidding {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    opens com.bidding.ui to javafx.graphics, javafx.fxml;
    opens com.bidding.model to java.base;

    exports com.bidding.ui;
    exports com.bidding.model;
    exports com.bidding.service;
    exports com.bidding.util;
    exports com.bidding.enums;
}
