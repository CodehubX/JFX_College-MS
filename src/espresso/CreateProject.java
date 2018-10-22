package espresso;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

public abstract class CreateProject extends StackPane{
    
    /*Code for the create project thing
       TextField Name = new TextField();
       TextArea Desc = new TextArea();
       TextField DueDate = new TextField();
       ListView lstUsers = new ListView();
       Button CreateProject = new Button();
       
       CreateProject.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
               if (Name.getText().equals("") || Desc.getText().equals("") || DueDate.getText().equals("") || lstUsers.getItems().isEmpty()){
                    Lib.Popup("Error", "Fields cannot be blank", primaryStage);
               }else{
                   //Special Case as all users also have to be added using a seprate query :D
                   String tempString = Database.CreateProject(Name.getText(), Desc.getText(), DueDate.getText());
                   //Have create Project return the projects id
                   
                   if (!tempString.equals("Error")){
                       for (int i = 0; i < lstUsers.getItems().size(); i++){
                           Database.AddUserToProject(lstUsers.getItems().get(i).toString(), tempString);
                       }
                   }else{
                       Lib.Popup("Error", "Error in creating project", primaryStage);
                   }
               }
            }
        });
    */
}
