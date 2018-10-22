package espresso;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public abstract class TaskPopup extends StackPane {

    LibCode Lib = new LibCode();
    Database Database;

    public TaskPopup(Stage primaryStage, int Type, Stage Parent, String User_id, String Project_id) {
        int textBoxSize = 150;
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(30, 30, 30, 30));

        if (Type != 0) {
            //Create Task

            Database = new Database();
            String UserList = Database.GetAllUsers(User_id, 0);
            
            Label lblName = new Label("Name:");
            grid.add(lblName, 0, 0);
            
            TextField Name = new TextField();
            grid.add(Name, 1, 0);
            
            Label lblUser = new Label("User:");
            grid.add(lblUser, 0, 1);

            ComboBox User = new ComboBox();
            grid.add(User, 1, 1);
            
            for (int i = 0; i < UserList.split(";").length; i++) {
                String ID = Database.GetProjectUsersUDs(Project_id);
                for (int pi = 0; pi < ID.split(";").length; pi++) {
                    if (UserList.split(";")[i].split(",")[0].equals(ID.split(";")[pi])) {
                        User.getItems().add(UserList.split(";")[i].split(",")[0] + " " + UserList.split(";")[i].split(",")[1] + " " + UserList.split(";")[i].split(",")[2]);
                    }
                }
            }

            Label lblDueDate = new Label("Due Date:");
            grid.add(lblDueDate, 0, 2);
            
            DatePicker DueDate = new DatePicker();
            grid.add(DueDate, 1, 2);
            
            final Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {
                @Override
                public DateCell call(final DatePicker datePicker) {
                    return new DateCell() {
                        @Override
                        public void updateItem(LocalDate item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item.isBefore(LocalDate.now().plusDays(1))) {
                                setDisable(true);
                                setStyle("-fx-background-color: #ffc0cb;");
                            }
                            long Days = ChronoUnit.DAYS.between(LocalDate.now(), item);
                            if (Days != 1){
                                setTooltip(new Tooltip("Task time of: " + Days + " days"));
                            }else{
                                setTooltip(new Tooltip("Task time of: " + Days + " day"));
                            }
                        }
                    };
                }
            };
            DueDate.setDayCellFactory(dayCellFactory);
            
            Label lblDesc = new Label("Description");
            grid.add(lblDesc, 0, 3);
            
            TextArea Desc = new TextArea();
            grid.add(Desc, 0, 4, 2, 2);
            
            Label lblImportance = new Label("Importance");
            grid.add(lblImportance, 0, 7);
            
            ComboBox Importance = new ComboBox();
            grid.add(Importance, 1, 7);
            
            Importance.getItems().add("High");
            Importance.getItems().add("Medium");
            Importance.getItems().add("Low");
            
            Label lblApproval = new Label("Needs Approval?");
            grid.setHalignment(lblApproval, HPos.CENTER);
            grid.add(lblApproval, 0, 8, 2, 1);

            ToggleGroup group = new ToggleGroup();
            RadioButton Yes = new RadioButton("Yes");
            RadioButton No = new RadioButton("No");
            Yes.setToggleGroup(group);
            No.setToggleGroup(group);
            No.setSelected(true);
            grid.setHalignment(Yes, HPos.CENTER);
            grid.setHalignment(No, HPos.CENTER);
            grid.add(Yes, 0, 9);
            grid.add(No, 1, 9);
            
            Button CreateTask = new Button("Create Task");
            grid.setHalignment(CreateTask, HPos.LEFT);
            grid.add(CreateTask, 0, 10);
            
            Button Cancel = new Button("Cancel");
            grid.setHalignment(Cancel, HPos.RIGHT);
            grid.add(Cancel, 1, 10);
            
            Name.setPrefWidth(textBoxSize);
            User.setPrefWidth(textBoxSize);
            DueDate.setPrefWidth(textBoxSize);
            Desc.setPrefWidth(textBoxSize + 50);
            Desc.setPrefHeight(textBoxSize - 50);
            Importance.setPrefWidth(textBoxSize);
            CreateTask.setPrefWidth(textBoxSize - 50);
            Cancel.setPrefWidth(textBoxSize - 50);
            
            CreateTask.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (Name.getText().equals("") || Desc.getText().equals("") || DueDate.getValue() == null ||
                            Importance.getSelectionModel().getSelectedItem() == null || User.getSelectionModel().getSelectedItem() == null) {
                        Lib.Popup("Error", "Fields cannot be blank", primaryStage);
                    } else {
                        Date date = Date.from(Instant.from(DueDate.getValue().atStartOfDay(ZoneId.systemDefault())));
                        SimpleDateFormat mysqlDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String DueDateValue = mysqlDate.format(date);
                        String strImportance = "";
                        switch(Importance.getSelectionModel().getSelectedItem().toString()){
                            case "High":
                                strImportance = "High";
                                break;
                            case "Medium":
                                strImportance = "Medium";
                                break;
                            case "Low":
                                strImportance = "Low";
                                break;
                        }
                        String tempString;
                        if (group.getSelectedToggle() == Yes){
                            tempString = Database.CreateTask(User.getSelectionModel().getSelectedItem().toString().split(" ")[0], Project_id, Name.getText(), Desc.getText(),
                                    strImportance, DueDateValue, true);
                        }else{
                            tempString = Database.CreateTask(User.getSelectionModel().getSelectedItem().toString().split(" ")[0], Project_id, Name.getText(), Desc.getText(),
                                    strImportance, DueDateValue, false);
                        }
                       
                        if (!tempString.equals("Error")) {
                            Espresso Main = new Espresso(User_id, Project_id);
                            try {
                                Main.start(Parent);
                            } catch (Exception ex) {
                                System.out.println("Error " + ex);
                            }
                            primaryStage.close();
                        }else{
                            Lib.Popup("Error", "Error in creating task", primaryStage);
                        }
                    }
                }
            }); //*/
            
            Cancel.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    primaryStage.close();
                }
            });
            
        } else {
            //Delete Task
            Database = new Database();
            String TaskList = "", UserList = "";
            UserList = Database.GetProjectUsersUDs(Project_id);
            for (int i = 0; i < UserList.split(";").length; i++){
                String Temp = Database.GetTasks(UserList.split(";")[i], Project_id);
                if (!Temp.equals("No Tasks Found") && !Temp.equals("Error") ){
                    TaskList += Temp;
                }
            }

            Label lblHeader = new Label("Warning, cannot be undone");
            grid.setHalignment(lblHeader, HPos.CENTER);
            grid.add(lblHeader, 0, 0, 2, 1);
            
            Label lblTask = new Label("Task:");
            grid.add(lblTask, 0, 1);

            ComboBox Task = new ComboBox();
            grid.add(Task, 1, 1);
            
            if (!TaskList.equals("")) {
                for (int i = 0; i < TaskList.split(";").length; i++) {
                    Task.getItems().add(TaskList.split(";")[i].split(",")[6] + " " + TaskList.split(";")[i].split(",")[0]);
                }
            }else{
                primaryStage.close();
                Lib.Popup("Error", "No tasks found!", Parent);
            }

            Button DelTask = new Button("Delete Task");
            grid.setHalignment(DelTask, HPos.LEFT);
            grid.add(DelTask, 0, 2);
            
            Button Cancel = new Button("Cancel");
            grid.setHalignment(Cancel, HPos.RIGHT);
            grid.add(Cancel, 1, 2);
            
            Task.setPrefWidth(textBoxSize);
            DelTask.setPrefWidth(textBoxSize - 50);
            Cancel.setPrefWidth(textBoxSize - 50);
            
            Cancel.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    primaryStage.close();
                }
            });
            
            DelTask.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    Database.DeleteTask(Task.getSelectionModel().getSelectedItem().toString().split(" ")[0]);

                    Espresso Main = new Espresso(User_id);
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
