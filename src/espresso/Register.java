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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public abstract class Register extends StackPane{
    
    Database Database;
    LibCode Lib;
    Stage primaryStage;
    
    public Register(Stage Stage){
        primaryStage = Stage;
        Database = new Database();
        Lib = new LibCode();

        int textBoxSize = 200;
        //GRID PANEL STARTS
        //Creates GridPane object and assigns it to the variable names grid.
        GridPane grid = new GridPane();
        //Alignment property changes the default position of the grid from the top left of the scene to the center. 
        grid.setAlignment(Pos.CENTER);
        //Gap property manages spacing between rows+columns
        grid.setHgap(10);
        grid.setVgap(10);
        //Padding property manages space around edges of grid pane.
        grid.setPadding(new Insets(30, 30, 30, 30));

        //Creates text object that cannot be edited.
        //Sets the text to Welcome, and assigns it to a variable named scenetitle. 
        Text scenetitle = new Text("Register Account");
        grid.setHalignment(scenetitle, HPos.CENTER);
        //uses the setFont() method to set the font family, weight, and size of the scenetitle                  variable.

        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        //The grid.add() method adds the scenetitle variable to the layout grid. 
        grid.add(scenetitle, 0, 0, 4, 1);

        //FirstName Label
        Label lblName = new Label("Name");
        grid.add(lblName, 0, 1);

        //Firstname TextField
        TextField Name = new TextField();
        grid.add(Name, 1, 1);

        //Email Label
        Label lblEmail = new Label("Email:");
        grid.add(lblEmail, 0, 2);

        //Email TextField
        TextField Email = new TextField();
        grid.add(Email, 1, 2);

        //Password Label
        Label lblPassword = new Label("Password:");
        grid.add(lblPassword, 0, 3);

        //Password TextField
        PasswordField Password = new PasswordField();
        grid.add(Password, 1, 3);

        
        //RePassword Label
        Label lblRePassword = new Label("Confirm Password:");
        grid.add(lblRePassword, 0, 4);
        
        //RePassword TextBox
        PasswordField RePassword = new PasswordField();
        grid.add(RePassword, 1, 4);
        
        //Type Label
        Label lblType = new Label("User Type:");
        grid.add(lblType, 0, 5);

        //Type Control
        HBox radBox = new HBox();
        ToggleGroup group = new ToggleGroup();
        RadioButton User = new RadioButton("User");
        RadioButton Manager = new RadioButton("Manager");
        User.setToggleGroup(group);
        Manager.setToggleGroup(group);
        User.setSelected(true);
        User.setPrefWidth(textBoxSize / 2);
        Manager.setPrefWidth(textBoxSize / 2);
        radBox.getChildren().addAll(User, Manager);
        grid.add(radBox, 1, 5);
        
        
        Name.setPrefWidth(textBoxSize);
        Email.setPrefWidth(textBoxSize);
        Password.setPrefWidth(textBoxSize);
        RePassword.setPrefWidth(textBoxSize);

        Button Register = new Button("Register");
        
       // final Text actiontarget = new Text();
        //grid.add(actiontarget, 1, 7); 
        
        Register.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (Email.getText().equals("") || Name.getText().equals("") || Password.getText().equals("") || RePassword.getText().equals("")){
                    Lib.Popup("Error", "Fields cannot be blank", primaryStage);
                }else if(!Lib.isEmailValid(Email.getText())){
                    Lib.Popup("Error", "Email is invalid", primaryStage);
                }else if(!(Password.getText().equals(RePassword.getText()))){
                    Lib.Popup("Error", "Passwords do not match", primaryStage);
                }else if(Password.getText().length() < 6){
                    Lib.Popup("Error", "Password must be above 6 characters", primaryStage);
                }else{
                    String Type;
                    if (User.isSelected()){
                        Type = "User";
                    }else{
                        Type = "Manager";
                    }
                    String tempString = Database.CreateUser(Name.getText(), Email.getText(), Password.getText(), Type);
                    if (!tempString.equals("Error in creating user")){
                        //Code for registering in needs to go here - page transfer depending on things 
                        Scene scene = new Scene(new Login(Stage, 1) {});
                        Stage.setScene(scene);
                        Stage.setTitle("Login");
                        Stage.show();//*/
                    }else{
                        Lib.Popup("Error", tempString, primaryStage);
                    }
                }
            }
        });
        
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.CENTER);
        hbBtn.getChildren().add(Register);
        grid.add(hbBtn, 1, 6);
        
        getChildren().add(grid);
    }
}
