module org.loudsheep.psio_project {
    requires javafx.fxml;
    requires com.google.gson;
    requires java.sql;
    requires javafx.controls;


    opens org.loudsheep.psio_project to javafx.fxml;
    exports org.loudsheep.psio_project;
    exports org.loudsheep.psio_project.frontend.controllers;
    opens org.loudsheep.psio_project.frontend.controllers to javafx.fxml;
    exports org.loudsheep.psio_project.frontend.controllers.forms;
    opens org.loudsheep.psio_project.frontend.controllers.forms to javafx.fxml;
}