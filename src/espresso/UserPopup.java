package espresso;

import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public abstract class UserPopup extends StackPane {

    LibCode Lib = new LibCode();
    Database Database;

    public UserPopup(Stage primaryStage, int Type, Stage Parent, String User_id, String Project_id, String Header, ListView List) {
        int textBoxSize = 150;
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(30, 30, 30, 30));

        Label lblHeader = new Label(Header);
        grid.setHalignment(lblHeader, HPos.CENTER);
        grid.add(lblHeader, 0, 0, 2, 1);

        Database = new Database();
        String UserList = Database.GetAllUsers(User_id, 1);

        Label lblUser = new Label("User:");
        grid.add(lblUser, 0, 1);

        ComboBox User = new ComboBox();
        grid.add(User, 1, 1);

        ArrayList<String> tempList = new ArrayList<>();
        for (int i = 0; i < UserList.split(";").length; i++) {
            if (List != null && Type == 0) {
                for (int pi = 0; pi < List.getItems().size(); pi++) {
                    if ((UserList.split(";")[i].split(",")[0] + " " + UserList.split(";")[i].split(",")[1] + " " + UserList.split(";")[i].split(",")[2]).equals(List.getItems().get(pi))) {
                        User.getItems().add(UserList.split(";")[i].split(",")[0] + " " + UserList.split(";")[i].split(",")[1] + " " + UserList.split(";")[i].split(",")[2]);
                    }
                }
            } else if (Type != 0 && List == null) {
                String ID = Database.GetProjectUsersUDs(Project_id);
                for (int pi = 0; pi < ID.split(";").length; pi++) {
                    if (UserList.split(";")[i].split(",")[0].equals(ID.split(";")[pi])) {
                        tempList.add(UserList.split(";")[i]);
                    }
                }
            } else if (Type != 0) {
                User.getItems().add(UserList.split(";")[i].split(",")[0] + " " + UserList.split(";")[i].split(",")[1] + " " + UserList.split(";")[i].split(",")[2]);
            } else {
                String ID = Database.GetProjectUsersUDs(Project_id);
                for (int pi = 0; pi < ID.split(";").length; pi++) {
                    if (UserList.split(";")[i].split(",")[0].equals(ID.split(";")[pi])) {
                        User.getItems().add(UserList.split(";")[i].split(",")[0] + " " + UserList.split(";")[i].split(",")[1] + " " + UserList.split(";")[i].split(",")[2]);
                    }
                }
            }
        }
        
        if (Type != 0 && List == null){
            boolean pass;
            for (int i = 0; i < UserList.split(";").length; i++) {
                pass = false;
                for (int pi = 0; pi < tempList.size(); pi++){
                    if (UserList.split(";")[i].equals(tempList.get(pi))){
                        pass = true;
                    }
                }
                
                if (!pass){
                    User.getItems().add(UserList.split(";")[i].split(",")[0] + " " + UserList.split(";")[i].split(",")[1] + " " + UserList.split(";")[i].split(",")[2]);
                }
            }
        }

        if (Type != 0) {
            //Add User            

            Button CreateUser = new Button("Add User");
            grid.setHalignment(CreateUser, HPos.LEFT);
            grid.add(CreateUser, 0, 2);

            Button Cancel = new Button("Close");
            grid.setHalignment(Cancel, HPos.RIGHT);
            grid.add(Cancel, 1, 2);

            User.setPrefWidth(textBoxSize);
            CreateUser.setPrefWidth(textBoxSize - 50);
            Cancel.setPrefWidth(textBoxSize - 50);

            Cancel.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    primaryStage.close();
                }
            });

            CreateUser.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    if (Parent == null) {
                        //Add Data to list
                        if (!List.getItems().contains(User.getSelectionModel().getSelectedItem()) && User.getSelectionModel().getSelectedItem() != null) {
                            List.getItems().add(User.getSelectionModel().getSelectedItem());
                        }
                    } else {
                        if (User.getSelectionModel().getSelectedItem() != null ) {
                            Database.AddUserToProject(User.getSelectionModel().getSelectedItem().toString().split(" ")[0], Project_id);

                            Espresso Main = new Espresso(User_id, Project_id);
                            try {
                                Main.start(Parent);
                            } catch (Exception ex) {
                                System.out.println("Error " + ex);
                            }
                            primaryStage.close();
                        }
                    }
                }
            });
        } else {
            //Remove User
            Button DelUser = new Button("Remove User");
            grid.setHalignment(DelUser, HPos.LEFT);
            grid.add(DelUser, 0, 2);

            Button Cancel = new Button("Close");
            grid.setHalignment(Cancel, HPos.RIGHT);
            grid.add(Cancel, 1, 2);

            User.setPrefWidth(textBoxSize);
            DelUser.setPrefWidth(textBoxSize - 50);
            Cancel.setPrefWidth(textBoxSize - 50);

            Cancel.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    primaryStage.close();
                }
            });

            DelUser.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    if (Parent == null) {
                        //Add Data to list
                        if (User.getSelectionModel().getSelectedItem() != null) {
                            List.getItems().remove(User.getSelectionModel().getSelectedItem());
                        }
                    } else {
                        //Delete from the database and reset
                        if (User.getSelectionModel().getSelectedItem() != null) {
                            Database.RemoveUserFromProject(User.getSelectionModel().getSelectedItem().toString().split(" ")[0], Project_id);

                            Espresso Main = new Espresso(User_id, Project_id);
                            try {
                                Main.start(Parent);
                            } catch (Exception ex) {
                                System.out.println("Error " + ex);
                            }
                            primaryStage.close();
                        }
                    }
                }
            });
        }

        getChildren().add(grid);
    }
}
