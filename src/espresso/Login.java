package espresso;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

abstract class Login extends StackPane {

    Database Database;
    LibCode Lib;
    
    public Login(Stage Stage, int Pass) {
        Database = new Database();
        Lib = new LibCode();

        if (Database.con == null) {
            //Database cannot be reached due to it being down/internet not allowing a connection(Looking at you eduroam)
           Lib.DatabaseError(Stage);
        } else {

            int textBoxSize = 200;
            GridPane grid = new GridPane();
            grid.setAlignment(Pos.CENTER);
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(30, 30, 30, 30));

            Label lblEmail = new Label("Email:");
            GridPane.setConstraints(lblEmail, 0, 0);

            TextField Email = new TextField();
            GridPane.setConstraints(Email, 1, 0);

            Label lblPassword = new Label("Password:");
            GridPane.setConstraints(lblPassword, 0, 1);

            PasswordField Password = new PasswordField();
            GridPane.setConstraints(Password, 1, 1);

            HBox Buttons = new HBox();
            Button Login = new Button("Login");
            Button Reg = new Button("Register");
            Login.setPrefWidth(textBoxSize / 2);
            Reg.setPrefWidth(textBoxSize / 2);
            Buttons.getChildren().addAll(Login, Reg);
            GridPane.setConstraints(Buttons, 1, 2);
            
            Email.setPrefWidth(textBoxSize);
            Password.setPrefWidth(textBoxSize);
            grid.getChildren().addAll(lblEmail, Email, lblPassword, Password, Buttons);

            if (Pass != 0){
                Label RegMessage = new Label("Register successful");
                RegMessage.setTextFill(Color.rgb(210, 39, 30));
                grid.setHalignment(RegMessage, HPos.CENTER);
                grid.setConstraints(RegMessage, 1, 3);
                grid.getChildren().add(RegMessage);
            }
            
            getChildren().add(grid);

            Login.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (!Lib.isEmailValid(Email.getText())) {
                        Lib.Popup("Error", "Email is invalid", Stage);
                    } else if (Password.getText().equals("")) {
                        Lib.Popup("Error", "Fields cannot be blank", Stage);
                    } else {
                        String tempString = Database.Login(Email.getText(), Password.getText());
                        if (!tempString.equals("Email or password incorrect!") && !tempString.equals("Error logging in!")) {
                            
                            Espresso Main = new Espresso(tempString);
                            try {
                                Main.start(Stage);
                            } catch (Exception ex) {
                                System.out.println("Error " + ex);
                            }
                        } else {
                            Lib.Popup("Error", tempString, Stage);
                        }
                    }
                }
            });

            Reg.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Scene scene = new Scene(new Register(Stage) {});
                    Stage.setScene(scene);
                    Stage.setTitle("Register");
                    Stage.show();//*/
                }
            });

        }
    }

}
