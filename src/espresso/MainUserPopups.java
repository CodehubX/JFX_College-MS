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

public abstract class MainUserPopups extends StackPane {

    LibCode Lib = new LibCode();
    Database Database;

    public MainUserPopups(Stage primaryStage, int Type, Stage Parent, String User_id, String Project_id, String Task_id) {
        int textBoxSize = 150;
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(30, 30, 30, 30));

        if (Type == 0) {
            //Finish Tasks            
            Label lblTasks = new Label("Tasks:");
            grid.add(lblTasks, 0, 1);

            ComboBox Tasks = new ComboBox();
            grid.add(Tasks, 1, 1);

            Database = new Database();
            String TaskList = "", UserList;
            String Temp = Database.GetTasks(User_id, Project_id);
            if (!Temp.equals("No Tasks Found") && !Temp.equals("Error")) {
                TaskList += Temp;
            }
            
            if (!TaskList.equals("")) {
                for (int i = 0; i < TaskList.split(";").length; i++) {
                    Tasks.getItems().add(TaskList.split(";")[i].split(",")[6] + " " + TaskList.split(";")[i].split(",")[0]);
                }
            }else{
                primaryStage.close();
                Lib.Popup("Error", "No tasks found!", Parent);
            }
            
            Button FinishTask = new Button("Upload Task");
            grid.setHalignment(FinishTask, HPos.LEFT);
            grid.add(FinishTask, 0, 2);

            Button Cancel = new Button("Close");
            grid.setHalignment(Cancel, HPos.RIGHT);
            grid.add(Cancel, 1, 2);
            
            Label lblWarning = new Label("A file must be choosen to complete a task");
            Label lblWarningTwo = new Label("File cannot be over 4MB in size");
            grid.add(lblWarning, 0, 3);
            grid.add(lblWarningTwo, 0, 4);

            Tasks.setPrefWidth(textBoxSize);
            FinishTask.setPrefWidth(textBoxSize - 50);
            Cancel.setPrefWidth(textBoxSize - 50);

            Cancel.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    primaryStage.close();
                }
            });

            FinishTask.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    if (Tasks.getSelectionModel().getSelectedItem() != null){
                        String Temp = Database.UploadFile(Tasks.getSelectionModel().getSelectedItem().toString().split(" ")[0], primaryStage);
                        if (!Temp.equals("Error")){
                            Database.FinishTask(Tasks.getSelectionModel().getSelectedItem().toString().split(" ")[0]);
                            
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
        } else if (Type == 1) {
            //Code for approval
            Database = new Database();
            String TaskList = "", UserList = "";
            UserList = Database.GetProjectUsersUDs(Project_id);
            for (int i = 0; i < UserList.split(";").length; i++){
                String Temp = Database.GetTasks(UserList.split(";")[i], Project_id);
                if (!Temp.equals("No Tasks Found") && !Temp.equals("Error") ){
                    TaskList += Temp;
                }
            }

            Label lblHeader = new Label("Ask for approval");
            grid.setHalignment(lblHeader, HPos.CENTER);
            grid.add(lblHeader, 0, 0, 2, 1);
            
            Label lblTask = new Label("Task:");
            grid.add(lblTask, 0, 1);

            ComboBox Task = new ComboBox();
            grid.add(Task, 1, 1);
            
            if (!TaskList.equals("")) {
                for (int i = 0; i < TaskList.split(";").length; i++) {
                    if (TaskList.split(";")[i].split(",")[5].equals("true")){
                        Task.getItems().add(TaskList.split(";")[i].split(",")[6] + " " + TaskList.split(";")[i].split(",")[0]);
                    }
                }
            }else{
                primaryStage.close();
                Lib.Popup("Error", "No tasks found!", Parent);
            }

            Button btnTask = new Button("Request Approval");
            grid.setHalignment(btnTask, HPos.LEFT);
            grid.add(btnTask, 0, 2);
            
            Button Cancel = new Button("Cancel");
            grid.setHalignment(Cancel, HPos.RIGHT);
            grid.add(Cancel, 1, 2);
            
            Task.setPrefWidth(textBoxSize);
            btnTask.setPrefWidth(textBoxSize - 50);
            Cancel.setPrefWidth(textBoxSize - 50);
            
            Cancel.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    primaryStage.close();
                }
            });
            
            btnTask.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    
                    //Should let the manager know an employee is requesting approval
                    //due to limitations this is being left blank
                    
                    Espresso Main = new Espresso(User_id, Project_id);
                    try {
                        Main.start(Parent);
                    } catch (Exception ex) {
                        System.out.println("Error " + ex);
                    }
                    primaryStage.close();
                }
            });
        } else if (Type == 2) {
            
            Label lblHeader = new Label("Approve task?");
            grid.setHalignment(lblHeader, HPos.CENTER);
            grid.add(lblHeader, 0, 0, 2, 1);
            
            Button Task = new Button("Approve");
            grid.setHalignment(Task, HPos.LEFT);
            grid.add(Task, 0, 2);
            
            Button Cancel = new Button("Cancel");
            grid.setHalignment(Cancel, HPos.RIGHT);
            grid.add(Cancel, 1, 2);
            
            lblHeader.setPrefWidth(textBoxSize);
            Task.setPrefWidth(textBoxSize - 50);
            Cancel.setPrefWidth(textBoxSize - 50);
            
             Cancel.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    primaryStage.close();
                }
            });
            
            Task.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    //approve a task
                    Database = new Database();
                    System.out.println(Database.ApproveTask(Task_id));
                    System.out.println(User_id);
                    Espresso Main = new Espresso(User_id, Project_id);
                    try {
                        Main.start(Parent);
                    } catch (Exception ex) {
                        System.out.println("Error " + ex);
                    }
                    primaryStage.close();
                }
            });
        }

        getChildren().add(grid);
    }
}
