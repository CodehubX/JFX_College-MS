package espresso;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LibCode {

    void Popup(String Title, String Message, Stage Stage) {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(Stage);

        //code which sets the width of the dialog according to the size of the Message or title
        int Width = 150;
        if (Message.length() >= Title.length()) {
            if (Message.length() >= 10) {
                for (int i = 0; i < Math.round(Message.length() / 10); i++) {
                    Width += 50;
                }
            }
        } else {
            if (Title.length() >= 10) {
                for (int i = 0; i < Math.round(Title.length() / 10); i++) {
                    Width += 50;
                }
            }
        }

        //The dialog code
        StackPane dialogBox = new StackPane();
        Label Text = new Label();
        Text.setPrefWidth(Width);
        Text.setText(Message);
        dialogBox.getChildren().add(Text);
        //dialogBox.setStyle("-fx-border-color: black; -fx-background-radius: 6px, 0px;");
        Scene dialogScene = new Scene(dialogBox, Text.prefWidth(-1) + 20, Text.prefHeight(-1) + 50);

        //Scene Code
        dialog.setTitle(Title);
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setResizable(false);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    boolean isEmailValid(String email) {
        //Checks if the Email used is valid

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    void DatabaseError(Stage Stage) {
        StackPane pane = new StackPane();
        pane.getChildren().add(new Label("Cannot open applaction as database cannot be reached"));
        Scene scene = new Scene(pane, 400, 100);
        Stage.setScene(scene);
        Stage.setTitle("Database Error");
        Stage.show();//*/
    }
}
