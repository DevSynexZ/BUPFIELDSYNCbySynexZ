// package com.fieldsync;

// import javafx.application.Application;
// import javafx.scene.Scene;
// import javafx.scene.control.Label;
// import javafx.scene.layout.StackPane;
// import javafx.stage.Stage;

// public class Main extends Application {

//     @Override
//     public void start(Stage primaryStage) {
//         Label label = new Label("FieldSync Java - System Ready");
//         StackPane root = new StackPane(label);
//         Scene scene = new Scene(root, 600, 400);

//         primaryStage.setTitle("FieldSync Java - Campus Field Reservation System");
//         primaryStage.setScene(scene);
//         primaryStage.show();
//     }

//     // public static void main(String[] args) {
//     //     launch(args);
//     // }
//     public static void main(String[] args) {
//         App.main(args);
//     }
// }


package com.fieldsync;

public class Main {
    public static void main(String[] args) {
        // Launches JavaFX Application cleanly without JVM pre-flight module errors
        App.main(args);
    }
}