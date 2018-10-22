package espresso;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public abstract class ProjectPopup extends StackPane {

    LibCode Lib = new LibCode();
    Database Database;

    public ProjectPopup(Stage primaryStage, int Type, Stage Parent, String User_id) {
        int textBoxSize = 200;
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(30, 30, 30, 30));

        if (Type != 0) {
            //Create Project
        //  grid.add(scenetitle, 0, 0, 4, 1);

            Label lblName = new Label("Name:");
            grid.add(lblName, 0, 0);
            
            TextField Name = new TextField();
            grid.add(Name, 1, 0);

            Label lblDueDate = new Label("Due Date:");
            grid.add(lblDueDate, 0, 1);
            
            DatePicker DueDate = new DatePicker();
            grid.add(DueDate, 1, 1);
            
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
                                setTooltip(new Tooltip("Project time of: " + Days + " days"));
                            }else{
                                setTooltip(new Tooltip("Project time of: " + Days + " day"));
                            }
                        }
                    };
                }
            };
            DueDate.setDayCellFactory(dayCellFactory);
            
            Label lblDesc = new Label("Description");
            grid.add(lblDesc, 0, 2);
            
            TextArea Desc = new TextArea();
            grid.add(Desc, 0, 3, 2, 2);
            
            Label lblUsers = new Label("Users");
            grid.add(lblUsers, 0, 6);
            
            ListView lstUsers = new ListView();
            grid.add(lstUsers, 0, 7, 2, 2);
            
            Button AddUser = new Button("Add User");
            grid.setHalignment(AddUser, HPos.LEFT);
            grid.add(AddUser, 0, 10);
            
            Button RemoveUser = new Button("Remove User");
            grid.setHalignment(RemoveUser, HPos.RIGHT);
            grid.add(RemoveUser, 1, 10);
            
            Button CreateProject = new Button("Create Project");
            grid.setHalignment(CreateProject, HPos.LEFT);
            grid.add(CreateProject, 0, 12);
            
            Button Cancel = new Button("Cancel");
            grid.setHalignment(Cancel, HPos.RIGHT);
            grid.add(Cancel, 1, 12);
            
            Name.setPrefWidth(textBoxSize);
            DueDate.setPrefWidth(textBoxSize);
            Desc.setPrefWidth(textBoxSize + 50);
            Desc.setPrefHeight(textBoxSize - 50);
            lstUsers.setPrefWidth(textBoxSize + 50);
            lstUsers.setPrefHeight(textBoxSize - 50);
            AddUser.setPrefWidth(textBoxSize - 50);
            RemoveUser.setPrefWidth(textBoxSize - 50);
            CreateProject.setPrefWidth(textBoxSize - 50);
            Cancel.setPrefWidth(textBoxSize - 50);
            
            CreateProject.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (Name.getText().equals("") || Desc.getText().equals("") || DueDate.getValue() == null || lstUsers.getItems().isEmpty()) {
                        Lib.Popup("Error", "Fields cannot be blank", primaryStage);
                    } else {
                        Database = new Database();
                        Date date = Date.from(Instant.from(DueDate.getValue().atStartOfDay(ZoneId.systemDefault())));
                        SimpleDateFormat mysqlDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String DueDateValue = mysqlDate.format(date);
                        //Special Case as all users also have to be added using a seprate query :D
                        String tempString = Database.CreateProject(Name.getText(), Desc.getText(), DueDateValue);
                        //Have create Project return the projects id
                        if (!tempString.equals("Error")) {
                            //Add Manager
                            Database.AddUserToProject(User_id, tempString);
                            //Add Users
                            for (int i = 0; i < lstUsers.getItems().size(); i++) {
                                Database.AddUserToProject(lstUsers.getItems().get(i).toString().split(" ")[0], tempString);
                            }
                            //Close Popup + Open new project
                            Espresso Main = new Espresso(User_id);
                            try {
                               Main.start(Parent);
                            } catch (Exception ex) {
                                System.out.println("Error " + ex);
                            }
                            primaryStage.close();
                        } else {
                            Lib.Popup("Error", "Error in creating project", primaryStage);
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
            
            AddUser.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    Stage dialog = new Stage();
                    dialog.initModality(Modality.APPLICATION_MODAL);
                    dialog.initOwner(Parent);
                    StackPane dialogBox = new UserPopup(dialog, 1, null, User_id, "0", "Add Users to list", lstUsers) {};
                    Scene dialogScene = new Scene(dialogBox);

                    dialog.setTitle("Add User");
                    dialog.setResizable(false);
                    dialog.setScene(dialogScene);
                    dialog.show();
                }
            });
            
            RemoveUser.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    Stage dialog = new Stage();
                    dialog.initModality(Modality.APPLICATION_MODAL);
                    dialog.initOwner(Parent);
                    StackPane dialogBox = new UserPopup(dialog, 0, null, User_id, "0", "Remove Users from list", lstUsers) {};
                    Scene dialogScene = new Scene(dialogBox);

                    dialog.setTitle("Remove User");
                    dialog.setResizable(false);
                    dialog.setScene(dialogScene);
                    dialog.show();
                }
            });
            
        } else {
            //Delete Project
            Database = new Database();
            String ProjectList = Database.GetProjectsForUser(User_id);

            Label lblHeader = new Label("Warning, cannot be undone");
            grid.setHalignment(lblHeader, HPos.CENTER);
            grid.add(lblHeader, 0, 0, 2, 1);
            
            Label lblUser = new Label("User:");
            grid.add(lblUser, 0, 1);

            ComboBox Project = new ComboBox();
            grid.add(Project, 1, 1);
            
            for (int i = 0; i < ProjectList.split(";").length; i++) {
                if (!ProjectList.split(";")[i].equals("Project Not Found")) {
                     Project.getItems().add(ProjectList.split(";")[i].split(",")[0] + " " + ProjectList.split(";")[i].split(",")[1]);
                }
            }

            Button DelProject = new Button("Delete Project");
            grid.setHalignment(DelProject, HPos.LEFT);
            grid.add(DelProject, 0, 2);
            
            Button Cancel = new Button("Cancel");
            grid.setHalignment(Cancel, HPos.RIGHT);
            grid.add(Cancel, 1, 2);
            
            Project.setPrefWidth(textBoxSize);
            DelProject.setPrefWidth(textBoxSize - 50);
            Cancel.setPrefWidth(textBoxSize - 50);
            
            Cancel.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    primaryStage.close();
                }
            });
            
            DelProject.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    Database.DeleteProject(Project.getSelectionModel().getSelectedItem().toString().split(" ")[0]);

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
