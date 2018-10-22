package espresso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

public class Espresso extends Application {

    private Database Database;
    private LibCode Lib;
    private String User_id, User_name, User_type, User_email, User_date, Current_Project_id;
    final private DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
    final private DateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy");
    private Stage PrimaryStage;

    public Espresso() {
        //Required for when the program first runs
        //If we dont decided to implament something then
        //Lets force em to login each time this is run, set to something to skip login
        User_id = "0";
        Current_Project_id = "0";
    }

    public Espresso(String user_id) {
        //Pass the user_id after login so that it'll work
        User_id = user_id;
        Current_Project_id = "0";
    }

    public Espresso(String user_id, String project_id) {
        //Pass the user_id after login so that it'll work
        User_id = user_id;
        Current_Project_id = project_id;
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        Database = new Database();
        Lib = new LibCode();
        PrimaryStage = stage;

        if (Database.con == null) {
            //Database cannot be reached due to it being down/internet not allowing a connection(Looking at you eduroam)

            Lib.DatabaseError(stage);
        } else {
            //We Assume that the user_id is being set and saved somewhere at login
            String userData = Database.GetUser(User_id);
            if (userData.equals("Error") || userData.equals("No User Found")) {
                //If the user id isnt set somehow/is set wrong, we won't load here and instead will send them to login

                Scene scene = new Scene(new Login(stage, 0) {
                });
                stage.setScene(scene);
                stage.setTitle("Login");
                stage.show();//*/
                // Lib.Popup("Error", "Not logged in, logging out...", stage);
            } else {
                User_name = userData.split(",")[1];
                User_email = userData.split(",")[2];
                User_date = userData.split(",")[3];
                User_type = userData.split(",")[4].replace(";", "");

                BorderPane root = new BorderPane();
                MenuBar menuBar = new MenuBar();
                menuBar.setId("MenuBar");

                Menu fileMenu = new Menu("Project Options");
                MenuItem newProjectItem = new MenuItem("New Project");
                MenuItem DeleteProjectItem = new MenuItem("Delete Selected Project");
                MenuItem exitMenuItem = new MenuItem("Exit Application");

                newProjectItem.setOnAction((event) -> {

                    if (!User_type.equals("User")) {
                        Stage dialog = new Stage();
                        dialog.initModality(Modality.APPLICATION_MODAL);
                        dialog.initOwner(PrimaryStage);
                        StackPane dialogBox = new ProjectPopup(dialog, 1, PrimaryStage, User_id) {
                        };
                        Scene dialogScene = new Scene(dialogBox);

                        dialog.setTitle("Add Project");
                        dialog.setResizable(false);
                        dialog.setScene(dialogScene);
                        dialog.show();
                    } else {

                        JOptionPane.showMessageDialog(null, "Only managers have access to this opton." + JOptionPane.INFORMATION_MESSAGE);

                    }

                });

                DeleteProjectItem.setOnAction((event) -> {

                    if (!User_type.equals("User")) {
                        Stage dialog = new Stage();
                        dialog.initModality(Modality.APPLICATION_MODAL);
                        dialog.initOwner(PrimaryStage);
                        StackPane dialogBox = new ProjectPopup(dialog, 0, PrimaryStage, User_id) {
                        };
                        Scene dialogScene = new Scene(dialogBox);

                        dialog.setTitle("Delete Project");
                        dialog.setResizable(false);
                        dialog.setScene(dialogScene);
                        dialog.show();

                    } else {

                        JOptionPane.showMessageDialog(null, "Only managers have access to this opton." + JOptionPane.INFORMATION_MESSAGE);

                    }
                });

                exitMenuItem.setOnAction(actionEvent -> Platform.exit());

                fileMenu.getItems().addAll(newProjectItem, DeleteProjectItem, new SeparatorMenuItem(), exitMenuItem);

                Menu About = new Menu("Task Options");
                MenuItem NewTaskmenuitem = new MenuItem("New Task");
                MenuItem DeleteTaskmenuitem = new MenuItem("Delete Task");

                NewTaskmenuitem.setOnAction((event) -> {

                    if (!User_type.equals("User")) {
                        Stage dialog = new Stage();
                        dialog.initModality(Modality.APPLICATION_MODAL);
                        dialog.initOwner(PrimaryStage);
                        StackPane dialogBox = new TaskPopup(dialog, 1, PrimaryStage, User_id, Current_Project_id) {
                        };
                        Scene dialogScene = new Scene(dialogBox);

                        dialog.setTitle("Add Task");
                        dialog.setResizable(false);
                        dialog.setScene(dialogScene);
                        dialog.show();
                    } else {

                        JOptionPane.showMessageDialog(null, "Only managers have access to this opton." + JOptionPane.INFORMATION_MESSAGE);

                    }
                });

                DeleteTaskmenuitem.setOnAction((event) -> {

                    if (!User_type.equals("User")) {
                        Stage dialog = new Stage();
                        dialog.initModality(Modality.APPLICATION_MODAL);
                        dialog.initOwner(PrimaryStage);
                        StackPane dialogBox = new TaskPopup(dialog, 0, PrimaryStage, User_id, Current_Project_id) {
                        };
                        Scene dialogScene = new Scene(dialogBox);

                        dialog.setTitle("Delete Task");
                        dialog.setResizable(false);
                        dialog.setScene(dialogScene);
                        dialog.show();
                    } else {

                        JOptionPane.showMessageDialog(null, "Only managers have access to this opton." + JOptionPane.INFORMATION_MESSAGE);

                    }

                });

                About.getItems().addAll(NewTaskmenuitem, DeleteTaskmenuitem);

                Menu User = new Menu("User Options");
                MenuItem NewUsertmenuitem = new MenuItem("Add New User to this Project");
                MenuItem DeleteUsermenuitem = new MenuItem("Remove User From Project");

                NewUsertmenuitem.setOnAction((event) -> {

                    if (!User_type.equals("User")) {
                        Stage dialog = new Stage();
                        dialog.initModality(Modality.APPLICATION_MODAL);
                        dialog.initOwner(PrimaryStage);
                        StackPane dialogBox = new UserPopup(dialog, 1, PrimaryStage, User_id, Current_Project_id, "Add Users to project", null) {
                        };
                        Scene dialogScene = new Scene(dialogBox);

                        dialog.setTitle("Add User");
                        dialog.setResizable(false);
                        dialog.setScene(dialogScene);
                        dialog.show();

                    } else {

                        JOptionPane.showMessageDialog(null, "Only managers have access to this opton." + JOptionPane.INFORMATION_MESSAGE);

                    }

                });

                DeleteUsermenuitem.setOnAction((event) -> {

                    if (!User_type.equals("User")) {
                        Stage dialog = new Stage();
                        dialog.initModality(Modality.APPLICATION_MODAL);
                        dialog.initOwner(PrimaryStage);
                        StackPane dialogBox = new UserPopup(dialog, 0, PrimaryStage, User_id, Current_Project_id, "Remove Users from project", null) {
                        };
                        Scene dialogScene = new Scene(dialogBox);

                        dialog.setTitle("Remove User");
                        dialog.setResizable(false);
                        dialog.setScene(dialogScene);
                        dialog.show();

                    } else {

                        JOptionPane.showMessageDialog(null, "Only managers have access to this opton." + JOptionPane.INFORMATION_MESSAGE);

                    }

                });

                User.getItems().addAll(NewUsertmenuitem, DeleteUsermenuitem);

                menuBar.getMenus().addAll(fileMenu, About, User);

                TabPane pane = CreateTabs();

                if (pane != null) {
                    pane.setSide(Side.TOP);
                    root.setTop(menuBar);
                    root.setCenter(pane);
                    Scene scene = new Scene(root);
                    scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
                    stage.setTitle("Espresso " + User_type + ": " + User_name);
                    stage.setScene(scene);
                    stage.show();
                    stage.toFront();

                    Rectangle2D ScreenBounds = Screen.getPrimary().getVisualBounds();
                    stage.setX((ScreenBounds.getWidth() - stage.getWidth()) / 2);
                    stage.setY((ScreenBounds.getHeight() - stage.getHeight()) / 4);

                } else {
                    //Don't think this should ever run

                    Scene scene = new Scene(new Label("Error 404: Error not found"));
                    stage.setTitle("Espresso " + User_type + ": " + User_name);
                    stage.setScene(scene);
                    stage.show();
                }
            }
        }
    }

    // Database.CreateUser("Daniel", "Email@Email.com", "Password", "Admin");
    // Database.CreateTask("5","5","Fuck sake", "Description", "Not very", "9999-12-31 23:59:59", false);
    //Database.CreateProject("Hug Sharks", "Give many sharks hugs", "9999-12-31 23:59:59");
    //Database.AddUserToProject("6", "1");
    /*  Function that creates a Tab pane */
 /*Each iteration creates a new tab and populates it using createborderpane() function*/
    private TabPane CreateTabs() {

        TabPane tabpane = new TabPane();
        tabpane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        String Temp = Database.GetProjectsForUser(User_id);

        if (Temp.replace(";", "").equals("Error") || Temp.equals("No Projects Found")) {
            Tab tab = new Tab();
            tab.setText("No Projects Found");
            tab.setContent(createBorderPane("0"));
            tabpane.getTabs().add(tab);
            return tabpane;
        }

        boolean tempBool = false;
        for (int i = 0; i < Temp.split(";").length; i++) {
            if (!Temp.split(";")[i].equals("Project Not Found")) {
                if (!tempBool && Current_Project_id.equals("0")) {
                    Current_Project_id = Temp.split(";")[i].split(",")[0];
                }
                Tab tab = new Tab();
                tab.setText(Temp.split(";")[i].split(",")[1] + "  (" + Temp.split(";")[i].split(",")[0] + ")");
                tab.setContent(createBorderPane(Temp.split(";")[i].split(",")[0]));
                tabpane.getTabs().add(tab);

                if (!Current_Project_id.equals("0") && Current_Project_id.equals(Temp.split(";")[i].split(",")[0])) {
                    tabpane.getSelectionModel().select(tab);
                }

                tempBool = true;
            }
        }

        if (tempBool) {
            tabpane.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
                Current_Project_id = newTab.getText().split("\\(")[1].replace(")", "");
            });

            return tabpane;
        } else {
            Tab tab = new Tab();
            tab.setText("No Projects Found");
            tab.setContent(createBorderPane("0"));
            tabpane.getTabs().add(tab);
            return tabpane;
        }
    }

    private BorderPane createBorderPane(String Project_id) {

        if (Project_id.equals("0")) {
            return (BorderPane) ErrorPane(0);
        }

        BorderPane borderPane = new BorderPane();
        borderPane.setId("BorderPane");
        String Temp = Database.GetTasks(User_id, Project_id);

        //Temp = "Error";
        if (Temp.equals("No Tasks Found") || Temp.equals("Error")) {
            borderPane.setRight((VBox) ErrorPane(1));
            borderPane.setCenter(new Center(Project_id, PrimaryStage, User_id, User_type) {
            });
            borderPane.setLeft(UserPane(Project_id));
            return borderPane;
        }
        /* TreeItem<String> ti = new TreeItem<>("Tasks");
       ti.setExpanded(true);
       
       TreeItem Task;
       for (int i = 0; i < Temp.split(";").length; i++) {
           Task = new TreeItem<>();
           Task.setValue(Temp.split(";")[i].split(",")[0]);
           ti.getChildren().add(Task);
       }*/

        TreeItem<String> rootNode = new TreeItem<>();
        rootNode.setExpanded(true);

        for (int i = 0; i < Temp.split(";").length; i++) {
            TreeItem<String> taskName = new TreeItem<>(Temp.split(";")[i].split(",")[0]);
            try {
                taskName.getChildren().addAll(
                        new TreeItem<>(Temp.split(";")[i].split(",")[1]),
                        new TreeItem<>("Importance: " + Temp.split(";")[i].split(",")[2]),
                        new TreeItem<>("Start Date: " + outputFormat.format(inputFormat.parse(Temp.split(";")[i].split(",")[3].split(" ")[0])).replace("-", "/")),
                        new TreeItem<>("Due Date: " + outputFormat.format(inputFormat.parse(Temp.split(";")[i].split(",")[4].split(" ")[0])).replace("-", "/"))
                );
            } catch (Exception e) {/* ¯\_(ツ)_/¯ */
            }

            if (Temp.split(";")[i].split(",")[5].equals("true")) {
                taskName.getChildren().add(new TreeItem<>("Approval Needed!"));
            }

            rootNode.getChildren().add(taskName);
        }

        TreeView<String> treeView = new TreeView<>(rootNode);
        treeView.setShowRoot(false);
        VBox VBoxRight = new VBox();
        VBoxRight.setId("TaskList");

        HBox header = new HBox();
        header.setPrefWidth(treeView.getPrefWidth());
        Label TaskLabel = new Label("User Tasks");
        header.setAlignment(Pos.CENTER);
        header.getChildren().add(TaskLabel);
        VBoxRight.getChildren().add(header);
        VBoxRight.getChildren().add(treeView);

        VBoxRight.getChildren().add(ButtonsBox(0));
        VBoxRight.getChildren().add(ButtonsBox(1));

        borderPane.setRight(VBoxRight);
        borderPane.setCenter(new Center(Project_id, PrimaryStage, User_id, User_type) {
        });
        borderPane.setLeft(UserPane(Project_id));
        return borderPane;
    }

    private VBox UserPane(String Project_id) {

        VBox TempBox = new VBox();
        TempBox.setId("UserList");
        String Temp = Database.GetUsersInProject(Project_id);

        //Temp = "Error";
        if (Temp.equals("Error") || Temp.equals("No Users")) {
            return (VBox) ErrorPane(2);
        }

        TreeItem<String> rootNode = new TreeItem<>();
        rootNode.setExpanded(true);

        for (int i = 0; i < Temp.split(";").length; i++) {
            TreeItem<String> taskName = new TreeItem<>(Temp.split(";")[i].split(",")[1]);
            try {
                if (!User_type.equals("User")) {
                    taskName.getChildren().addAll(
                            new TreeItem<>("Email: " + Temp.split(";")[i].split(",")[0]),
                            new TreeItem<>("User Type: " + Temp.split(";")[i].split(",")[2]),
                            new TreeItem<>("Start Date: " + outputFormat.format(inputFormat.parse(Temp.split(";")[i].split(",")[3].split(" ")[0])).replace("-", "/"))
                    );
                }
            } catch (Exception e) {/* ¯\_(ツ)_/¯ */
            }

            rootNode.getChildren().add(taskName);
        }
        TreeView<String> treeView = new TreeView<>(rootNode);
        treeView.setShowRoot(false);

        /* for (int i = 0; i < Temp.split(";").length; i++){
           Label = new Label("  " + Temp.split(";")[i].split(",")[1]);
           TempBox.getChildren().add(Label);
       }   */
        HBox header = new HBox();
        header.setPrefWidth(treeView.getPrefWidth());
        Label TaskLabel = new Label("Project Users");
        header.setAlignment(Pos.CENTER);
        header.getChildren().add(TaskLabel);
        TempBox.getChildren().add(header);
        TempBox.getChildren().add(treeView);

        TempBox.getChildren().add(ButtonsBox(2));

        return TempBox;
    }

    //0 == task, 1 == Project, 2 == User
    private HBox ButtonsBox(int Type) {
        HBox buttons = new HBox();

        buttons.setPadding(new Insets(5, 5, 5, 5));

        Button btnAdd, btnDel;
        DropShadow shadowEffect = new DropShadow();
        buttons.setAlignment(Pos.CENTER);

        switch (Type) {
            case 0:
                if (!User_type.equals("User")) {
                    btnAdd = new Button("Add Task");
                    btnDel = new Button("Delete Task");

                    btnAdd.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e) {
                            //Code for adding task popup

                            Stage dialog = new Stage();
                            dialog.initModality(Modality.APPLICATION_MODAL);
                            dialog.initOwner(PrimaryStage);
                            StackPane dialogBox = new TaskPopup(dialog, 1, PrimaryStage, User_id, Current_Project_id) {
                            };
                            Scene dialogScene = new Scene(dialogBox);

                            dialog.setTitle("Add Task");
                            dialog.setResizable(false);
                            dialog.setScene(dialogScene);
                            dialog.show();
                        }
                    });

                    btnDel.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e) {
                            //Code for deleting task popup

                            Stage dialog = new Stage();
                            dialog.initModality(Modality.APPLICATION_MODAL);
                            dialog.initOwner(PrimaryStage);
                            StackPane dialogBox = new TaskPopup(dialog, 0, PrimaryStage, User_id, Current_Project_id) {
                            };
                            Scene dialogScene = new Scene(dialogBox);

                            dialog.setTitle("Delete Task");
                            dialog.setResizable(false);
                            dialog.setScene(dialogScene);
                            dialog.show();
                        }
                    });

                } else {
                    btnAdd = new Button("Complete Task");
                    btnDel = null;

                    btnAdd.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e) {
                            //Code for uploading task popup
                            Stage dialog = new Stage();
                            dialog.initModality(Modality.APPLICATION_MODAL);
                            dialog.initOwner(PrimaryStage);
                            StackPane dialogBox = new MainUserPopups(dialog, 0, PrimaryStage, User_id, Current_Project_id, "0") {
                            };
                            Scene dialogScene = new Scene(dialogBox);

                            dialog.setTitle("Upload Task");
                            dialog.setResizable(false);
                            dialog.setScene(dialogScene);
                            dialog.show();
                        }
                    });
                }
                break;
            case 1:
                if (!User_type.equals("User")) {
                    btnAdd = new Button("Add Project");
                    btnDel = new Button("Delete Project");

                    btnAdd.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e) {
                            //Code for adding project popup

                            Stage dialog = new Stage();
                            dialog.initModality(Modality.APPLICATION_MODAL);
                            dialog.initOwner(PrimaryStage);
                            StackPane dialogBox = new ProjectPopup(dialog, 1, PrimaryStage, User_id) {
                            };
                            Scene dialogScene = new Scene(dialogBox);

                            dialog.setTitle("Add Project");
                            dialog.setResizable(false);
                            dialog.setScene(dialogScene);
                            dialog.show();
                        }
                    });

                    btnDel.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e) {
                            //Code for deleting projects popup
                            Stage dialog = new Stage();
                            dialog.initModality(Modality.APPLICATION_MODAL);
                            dialog.initOwner(PrimaryStage);
                            StackPane dialogBox = new ProjectPopup(dialog, 0, PrimaryStage, User_id) {
                            };
                            Scene dialogScene = new Scene(dialogBox);

                            dialog.setTitle("Delete Project");
                            dialog.setResizable(false);
                            dialog.setScene(dialogScene);
                            dialog.show();
                        }
                    });

                } else {
                    btnAdd = new Button("Request Approval");
                    btnDel = null;

                    btnAdd.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e) {
                            //Code for requesting managers approval on a thing
                            Stage dialog = new Stage();
                            dialog.initModality(Modality.APPLICATION_MODAL);
                            dialog.initOwner(PrimaryStage);
                            StackPane dialogBox = new MainUserPopups(dialog, 1, PrimaryStage, User_id, Current_Project_id, "0") {
                            };
                            Scene dialogScene = new Scene(dialogBox);

                            dialog.setTitle("Request Approval");
                            dialog.setResizable(false);
                            dialog.setScene(dialogScene);
                            dialog.show();
                        }
                    });
                }
                break;
            case 2:
                if (!User_type.equals("User")) {
                    btnAdd = new Button("Add User");
                    btnDel = new Button("Remove User");

                    btnAdd.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e) {
                            //Code for adding user popup
                            Stage dialog = new Stage();
                            dialog.initModality(Modality.APPLICATION_MODAL);
                            dialog.initOwner(PrimaryStage);
                            StackPane dialogBox = new UserPopup(dialog, 1, PrimaryStage, User_id, Current_Project_id, "Add Users to project", null) {
                            };
                            Scene dialogScene = new Scene(dialogBox);

                            dialog.setTitle("Add User");
                            dialog.setResizable(false);
                            dialog.setScene(dialogScene);
                            dialog.show();
                        }
                    });

                    btnDel.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e) {
                            //Code for removing users popup
                            Stage dialog = new Stage();
                            dialog.initModality(Modality.APPLICATION_MODAL);
                            dialog.initOwner(PrimaryStage);
                            StackPane dialogBox = new UserPopup(dialog, 0, PrimaryStage, User_id, Current_Project_id, "Remove Users from project", null) {
                            };
                            Scene dialogScene = new Scene(dialogBox);

                            dialog.setTitle("Remove User");
                            dialog.setResizable(false);
                            dialog.setScene(dialogScene);
                            dialog.show();
                        }
                    });

                } else {
                    btnAdd = null;
                    btnDel = null;
                }
                break;
            default:
                btnAdd = null;
                btnDel = null;
        }

        if (btnAdd == null || btnDel == null) {
            if (btnAdd == null && btnDel == null) {
                //Something
            } else {
                btnAdd.setPrefWidth(150);
                btnAdd.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        btnAdd.setEffect(shadowEffect);
                    }
                });

                btnAdd.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        btnAdd.setEffect(null);
                    }
                });
                buttons.getChildren().add(btnAdd);
            }
        } else {
            btnAdd.setPrefWidth(120);
            btnDel.setPrefWidth(120);

            btnAdd.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    btnAdd.setEffect(shadowEffect);
                }
            });

            btnAdd.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    btnAdd.setEffect(null);
                }
            });

            btnDel.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    btnDel.setEffect(shadowEffect);
                }
            });

            btnDel.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    btnDel.setEffect(null);
                }
            });

            buttons.getChildren().add(btnAdd);
            buttons.getChildren().add(btnDel);
        }

        return buttons;
    }

    private Object ErrorPane(int Type) {
        Object temp = null;
        //Code for if no projects are found
        switch (Type) {
            case 0:
                BorderPane pane = new BorderPane();
                VBox VBoxRight1 = new VBox();
                VBox VBoxLeft1 = new VBox();

                HBox headerRight1 = new HBox();
                Label TaskLabel1 = new Label("No User Tasks Found");
                headerRight1.setAlignment(Pos.CENTER);
                headerRight1.getChildren().add(TaskLabel1);
                VBoxRight1.getChildren().add(headerRight1);

                HBox headerLeft1 = new HBox();
                Label UserLabel1 = new Label("No Users found");
                headerLeft1.setAlignment(Pos.CENTER);
                headerLeft1.getChildren().add(UserLabel1);
                VBoxLeft1.getChildren().add(headerLeft1);

                TreeItem<String> rootNode1 = new TreeItem<>();
                TreeView<String> treeView1 = new TreeView<>(rootNode1);
                TreeView<String> treeView21 = new TreeView<>(rootNode1);

                treeView1.setShowRoot(false);
                treeView21.setShowRoot(false);

                VBoxRight1.getChildren().add(treeView1);
                VBoxLeft1.getChildren().add(treeView21);

                VBoxRight1.getChildren().add(ButtonsBox(0));
                VBoxRight1.getChildren().add(ButtonsBox(1));
                VBoxLeft1.getChildren().add(ButtonsBox(2));

                pane.setRight(VBoxRight1);
                pane.setCenter(new Center("0", PrimaryStage, User_id, User_type) {
                });
                pane.setLeft(VBoxLeft1);
                temp = pane;
                break;
            case 1:
                //No tasks are found, but users are found

                VBox VBoxRight2 = new VBox();

                HBox headerRight2 = new HBox();
                Label TaskLabel2 = new Label("No User Tasks Found");
                headerRight2.setAlignment(Pos.CENTER);
                headerRight2.getChildren().add(TaskLabel2);
                VBoxRight2.getChildren().add(headerRight2);

                TreeItem<String> rootNode2 = new TreeItem<>();
                TreeView<String> treeView2 = new TreeView<>(rootNode2);

                treeView2.setShowRoot(false);

                VBoxRight2.getChildren().add(treeView2);

                VBoxRight2.getChildren().add(ButtonsBox(0));
                VBoxRight2.getChildren().add(ButtonsBox(1));

                temp = VBoxRight2;
                break;
            case 2:
                //No Users were found but users are found
                //Due to a logic error this never can actually run
                //Since a user needs to be in a project in order to view the page without users...

                VBox VBoxLeft3 = new VBox();

                HBox headerLeft3 = new HBox();
                Label UserLabel3 = new Label("No Project Users found");
                headerLeft3.setAlignment(Pos.CENTER);
                headerLeft3.getChildren().add(UserLabel3);
                VBoxLeft3.getChildren().add(headerLeft3);

                TreeItem<String> rootNode3 = new TreeItem<>();
                TreeView<String> treeView3 = new TreeView<>(rootNode3);

                treeView3.setShowRoot(false);
                VBoxLeft3.getChildren().add(treeView3);

                VBoxLeft3.getChildren().add(ButtonsBox(2));
                temp = VBoxLeft3;
                break;
        }
        return temp;
    }

}

abstract class Center extends StackPane {

    boolean Auth = false;
    Stage Stage;
    Database Database;
    String Current_Project_id, Current_User_id;
    final private DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
    final private DateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy");

    public Center(String Project_id, Stage stage, String User_id, String User_type) {
        Stage = stage;
        setPrefWidth(600);
        Current_Project_id = Project_id;
        Current_User_id = User_id;
        VBox vBoxCentre = new VBox();

        FlowPane TasksPendingAuth = new FlowPane();
        // TasksPendingAuth.setPrefHeight(200);
        TasksPendingAuth.setPrefWrapLength(600);
        //TasksPendingAuth.setPrefHeight(200);
        TasksPendingAuth.setPrefWidth(600);
        TasksPendingAuth.setVgap(10);
        TasksPendingAuth.setHgap(10);
        TasksPendingAuth.setPadding(new Insets(10, 10, 10, 10));

        TasksPendingAuth.setId("SubmittedList");

        FlowPane TasksFallingDue = new FlowPane();
        //TasksFallingDue.setPrefHeight(200);
        TasksFallingDue.setPrefWidth(600);
        TasksFallingDue.setVgap(5);
        TasksFallingDue.setHgap(5);
        TasksFallingDue.setPadding(new Insets(10, 10, 10, 10));
        TasksFallingDue.setId("DueTaskList");

        Database = new Database();
        String UserList;

        UserList = Database.GetProjectUsersUDs(Project_id);
        for (int i = 0; i < UserList.split(";").length; i++) {
            String Temp = Database.GetTasks(UserList.split(";")[i], Project_id);
            if (!Temp.equals("No Tasks Found") && !Temp.equals("Error")) {

                for (int pi = 0; pi < Temp.split(";").length; pi++) {
                    BorderPane tempPane = CenterTaskPanes(Temp.split(";")[pi], User_type);
                    if (Auth) {
                        TasksPendingAuth.getChildren().add(tempPane);
                    } else {
                        TasksFallingDue.getChildren().add(tempPane);
                    }
                }

            }
        }
        vBoxCentre.getChildren().addAll(TasksFallingDue, TasksPendingAuth);

        getChildren().add(vBoxCentre);
    }

    public BorderPane CenterTaskPanes(String task, String User_type) {
        BorderPane mainPane = new BorderPane();
        //mainPane.setPrefWidth(150);
        //mainPane.setPrefHeight(-1);
        mainPane.setStyle("-fx-border-color: black");
        VBox TaskBox = new VBox();

        //TaskBox.setPrefWidth();
        for (int i = 0; i < task.split(",").length - 2; i++) {
            Text Label = new Text();
            Label.setWrappingWidth(140);
            
            if (i != 3 && i != 4) {
                switch (i) {
                    case 5:
                        if (task.split(",")[i].equals("true")) {
                            Label.setText("Needs Approval");
                        } else {
                            Label.setText("No Approval Needed");
                        }
                        break;
                    case 2:
                        Label.setText("Importance: " + task.split(",")[i]);
                        break;
                    default:
                        Label.setText(task.split(",")[i]);
                }
            } else {
                try {
                    if (i == 3) {
                        Label.setText("Start: " + outputFormat.format(inputFormat.parse(task.split(",")[i].split(" ")[0])).replace("-", "/"));
                    } else {
                        Label.setText("Due: " + outputFormat.format(inputFormat.parse(task.split(",")[i].split(" ")[0])).replace("-", "/"));
                    }
                } catch (Exception ex) {
                    Label.setText("Date Error");
                }
            }
            TaskBox.getChildren().add(Label);
        }

        String User = Database.GetUser(task.split(",")[7]);
        
         Text Label = new Text(User.split(",")[1]);
         Label.setWrappingWidth(140);
        TaskBox.getChildren().add(Label);
        
        Label = new Text(User.split(",")[2]);
        Label.setWrappingWidth(140);
        TaskBox.getChildren().add(Label);

        if (task.split(",")[5].equals("true")) {
            Auth = true;
        } else {
            Auth = false;
        }
        
        TaskBox.setStyle("-fx-padding: 2 5 2 5;");
        
        TaskBox.setMinHeight(120);
        TaskBox.setMaxHeight(Double.MAX_VALUE);
        TaskBox.setMinWidth(150);
        TaskBox.setMaxWidth(Double.MAX_VALUE);
        TaskBox.setPrefHeight(170);
        mainPane.getChildren().add(TaskBox);
        
        mainPane.setPrefHeight(TaskBox.prefHeight(-1) - 20);
        mainPane.setPrefWidth(TaskBox.prefWidth(-1) - 20);
        
        mainPane.setMinWidth(150);
        mainPane.setMaxWidth(Double.MAX_VALUE);
        mainPane.setMinHeight(120);
        mainPane.setMaxHeight(Double.MAX_VALUE);
        
        mainPane.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                
                if (task.split(",")[5].equals("true") && !User_type.equals("User")) {
                    Stage dialog = new Stage();
                    dialog.initModality(Modality.APPLICATION_MODAL);
                    dialog.initOwner(Stage);
                    StackPane dialogBox = new MainUserPopups(dialog, 2, Stage, Current_User_id, Current_Project_id, task.split(",")[6]) {
                    };
                    Scene dialogScene = new Scene(dialogBox);

                    dialog.setTitle("Approve Task");
                    dialog.setResizable(false);
                    dialog.setScene(dialogScene);
                    dialog.show();
                }
            }
        });

        return mainPane;
    }

}
