package espresso;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.swing.filechooser.FileSystemView;

public class Database {

    //class for handling all database code
    Connection con;
    // int User_id;

    public Database() {
        //Constructer :D
        //  User_id = id;

        try {
            con = DriverManager.getConnection("jdbc:mysql://den1.mysql3.gear.host/javaproject?autoReconnect=true&useSSL=false", "javaproject", "Lo4PvE?Yr-89");
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    void close() {
        try {
            con.close();
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    String CreateUser(String Name, String Email, String Password, String Type) {
        try {
            PreparedStatement Query = con.prepareStatement("insert into users(user_name, user_email, user_pass, user_date, user_type) values (?,?,?,NOW(),?);");
            Query.setString(1, Name);
            Query.setString(2, Email);
            Query.setString(3, Password);
            //Query.setString(4, "NOW()");
            Query.setString(4, Type);
            Query.executeUpdate();
        } catch (Exception ex) {
            return "Error";
        }
        return "User Created";
    }

    String CreateTask(String user_id, String project_id, String Name, String Desc, String Importance, String DueDate, boolean approval) {
        try {
            // Query.execute("insert into task(user_id, project_id, task_name, task_desc, task_status, task_importance, task_date, task_date_due, approval) " + "values (" + user_id + "," + project_id + ",'" + Name + "','" + Desc + "','active','" + Importance + "',NOW(),'" + DueDate + "','" + approval + "');");
            PreparedStatement Query = con.prepareStatement("insert into task(user_id, project_id, task_name, task_desc, task_status, task_importance, task_date, task_date_due, task_approval) " + "values (?,?,?,?,?,?,NOW(),?,?);");
            Query.setString(1, user_id);
            Query.setString(2, project_id);
            Query.setString(3, Name);
            Query.setString(4, Desc);
            Query.setString(5, "active");
            Query.setString(6, Importance);
            //Query.setString(7, "NOW()");
            Query.setString(7, DueDate);
            Query.setString(8, String.valueOf(approval));
            Query.executeUpdate();
        } catch (Exception ex) {
            return "Error";
        }
        return "Task Created";
    }

    String CreateProject(String Name, String Desc, String DueDate) {
        try {
            PreparedStatement Query = con.prepareStatement("insert into project(project_name, project_desc, project_status, project_date, project_date_due) values(?,?,'active',NOW(),?);");
            Query.setString(1, Name);
            Query.setString(2, Desc);
            Query.setString(3, DueDate);
            Query.executeUpdate();
            Statement Query2 = con.createStatement();
            ResultSet Result = Query2.executeQuery("select last_insert_id();");
            if (Result.first()) {
                return String.valueOf(Result.getInt(1));
            }
        } catch (Exception ex) {
            return "Error";
        }
        return "Project Created";
    }

    String AddUserToProject(String User_id, String Project_id) {
        try {
            PreparedStatement Query = con.prepareStatement("insert into project_user(project_id, user_id) values(?,?);");
            Query.setString(1, Project_id);
            Query.setString(2, User_id);
            Query.executeUpdate();
        } catch (Exception ex) {
            return "Error " + ex;
        }
        return "User Added!";
    }

    String Login(String Email, String Password) {
        String Temp;
        try {
            Statement Query = con.createStatement();
            ResultSet Result = Query.executeQuery("select user_id from users where (user_email='" + Email + "'AND user_pass='" + Password + "');");
            if (Result.first()) {
                Temp = String.valueOf(Result.getInt(1));
            } else {
                return "Username or password incorrect!";
            }
        } catch (Exception ex) {
            return "Error";
        }
        return Temp;
    }

    String GetUserProjectsIDs(String User_id) {
        String Temp = "";
        try {
            Statement Query = con.createStatement();
            ResultSet Result = Query.executeQuery("select * from project_user where (user_id=" + User_id + ")");
            if (Result.isBeforeFirst()) {
                while (Result.next()) {
                    Temp += (String.valueOf(Result.getInt(2)) + "," + String.valueOf(Result.getInt(3)) + ";");
                }
            } else {
                return "No Projects Found";
            }
        } catch (Exception ex) {
            return "Error";
        }
        return Temp;
    }
    
    String GetProjectUsersUDs(String Project_id) {
        String Temp = "";
        try {
            Statement Query = con.createStatement();
            ResultSet Result = Query.executeQuery("select user_id from project_user where project_id=" + Project_id + ";");
            if (Result.isBeforeFirst()) {
                while (Result.next()) {
                    Temp += (String.valueOf(Result.getInt(1)) + ";");
                }
            } else {
                return "No Users";
            }
        } catch (Exception ex) {
            return "Error " + ex;
        }
        return Temp;
    }

    String GetTasks(String User_id, String Project_id) {
        String Temp = "";
        try {
            Statement Query = con.createStatement();
            ResultSet Result = Query.executeQuery("select * from task where (user_id = " + User_id + " AND project_id = " + Project_id + " AND task_status = 'active');");
            if (Result.isBeforeFirst()) {
                while (Result.next()) {
                    Temp += (Result.getString(4) + "," + Result.getString(5) + ","
                            + Result.getString(6) + "," + Result.getString(8) + ","
                            + Result.getString(9) + "," + Result.getString(10) + ","
                            + Result.getInt(1) + "," + Result.getInt(2) + ";");
                }
            } else {
                return "No Tasks Found";
            }
        } catch (Exception ex) {
            return "Error";
        }
        return Temp;
    }

    String GetUsersInProject(String Project_id) {
        String Temp = "";
        try {
            Statement Query = con.createStatement();
            ResultSet Result = Query.executeQuery("select user_id from project_user where project_id=" + Project_id + ";");
            String Users = "";
            if (Result.isBeforeFirst()) {
                while (Result.next()) {
                    Users += (String.valueOf(Result.getInt(1)) + ";");
                }
            } else {
                return "No Users";
            }
            //now we need to return all the data
            Statement Query2 = con.createStatement();
            ResultSet Result2;
            for (int i = 0; i < Users.split(";").length; i++) {
                Result2 = Query2.executeQuery("select user_email, user_name, user_type, user_date from users where user_id=" + Users.split(";")[i] + "");
                Result2.first();
                Temp += (Result2.getString(1) + "," + Result2.getString(2)
                        + "," + Result2.getString(3) + "," + Result2.getString(4) + ";");
            }
        } catch (Exception ex) {
            return "Error";
        }
        return Temp;
    }

    //These two look close
    //This one returns just projects ids, sepreated by a ;
    //The other returns all project data
    String GetProjectPerUser(String User_id) {
        String Temp = "";
        try {
            Statement Query = con.createStatement();
            ResultSet Result = Query.executeQuery("select project_id from project_user where user_id=" + User_id + ";");
            String Projects = "";
            if (Result.isBeforeFirst()) {
                while (Result.next()) {
                    Temp += (String.valueOf(Result.getInt(1)) + ";");
                }
            } else {
                return "No Projects";
            }
        } catch (Exception ex) {
            return "Error";
        }
        return Temp;
    }
    
    String GetProjectsForUser(String User_id) {
        String Temp = "", IDs, TempProject;
        IDs = GetUserProjectsIDs(User_id);
        if (!IDs.equals("No Projects Found") || !IDs.equals("Error")) {
            if (IDs.split(";").length != 0) {
                for (int i = 0; i < IDs.split(";").length; i++) {
                    TempProject = GetProject(IDs.split(";")[i].split(",")[0]) + ";";
                    if (!TempProject.equals("Project Not Found") || !TempProject.equals("Error")) {
                        Temp += TempProject;
                    } else {
                        return "No Projects Found";
                    }
                }
            } else {
                return "No Projects Found";
            }
        }
        return Temp;
    }

    String GetProject(String Project_id) {
        String Temp = "";
        try {
            Statement Query = con.createStatement();
            ResultSet Result = Query.executeQuery("select * from project where (project_id=" + Project_id + " AND project_status='active');");
            if (Result.first()) {
                Temp = (Result.getString(1) + "," + Result.getString(2) + "," + Result.getString(3) + ","
                        + Result.getString(4) + "," + Result.getString(5) + ","
                        + Result.getString(6));
            } else {
                return "Project Not Found";
            }
        } catch (Exception ex) {
            return "Error";
        }
        return Temp;
    }

    String GetUser(String User_id) {
        String Temp = "";
        try {
            Statement Query = con.createStatement();
            ResultSet Result = Query.executeQuery("select * from users where (user_id=" + User_id + ");");
            if (Result.isBeforeFirst()) {
                while (Result.next()) {
                    Temp += (Result.getInt(1) + "," + Result.getString(2) + ","
                            + Result.getString(3) + "," + Result.getString(5) + ","
                            + Result.getString(6) + ";");
                }
            } else {
                return "No User Found";
            }
        } catch (Exception ex) {
            return "Error";
        }
        return Temp;
    }

    String UploadFile(String task_id, Stage stage) {
        try {
            FileChooser Chooser = new FileChooser();
            Chooser.setTitle("Choose a file");
            File file = Chooser.showOpenDialog(stage);
            if (file != null) {
                PreparedStatement Query = con.prepareStatement("insert into files(task_id, file_file, file_name, file_date) values (?,?,?, NOW());");
                Query.setString(1, task_id);
                Query.setBlob(2, new FileInputStream(file));
                Query.setString(3, file.getName());
                Query.executeUpdate();
            }else{
                return "Error";
            }
        } catch (Exception ex) {
            return "Error";
        }

        return "Uploaded";
    }

    String RetriveFile(String task_id) {
        String Temp = "";

        try {
            Statement Query = con.createStatement();
            ResultSet Result = Query.executeQuery("select * from files where task_id = " + task_id + "");
            if (Result.isBeforeFirst()) {
                while (Result.next()) {

                    Statement Query2 = con.createStatement();
                    ResultSet Result2 = Query2.executeQuery("select task_name from task where task_id = " + task_id + "");

                    String taskName;
                    if (Result2.isBeforeFirst()) {
                        Result2.first();
                        taskName = Result2.getString(1);
                    } else {
                        taskName = Result.getString(2);
                    }

                    String path = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "\\Espresso\\";
                    String directoryName = path.concat("Task (" + taskName + ")");
                    String fileName = Result.getString(4);

                    Temp = directoryName;
                    File directory = new File(directoryName);
                    if (!directory.exists()) {
                        directory.mkdirs();
                    }

                    File file = new File(directoryName + "/" + fileName);
                    FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
                    BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
                    bufferWriter.write(Result.getString(3));
                    bufferWriter.close();
                }
            } else {
                return "No Files Found";
            }
        } catch (Exception ex) {
            return "Error " + ex;
        }

        return "File(s) downloaded to " + Temp;
    }
    
    String GetAllUsers(String User_id, int type){
        String Temp = "";
        try {
            Statement Query = con.createStatement();
            ResultSet Result;
            if (type == 0){
                Result = Query.executeQuery("select * from users;");
            }else{
                Result = Query.executeQuery("select * from users where (user_id <> " + User_id + ");");
            }
            if (Result.isBeforeFirst()) {
                while (Result.next()) {
                    Temp += (Result.getInt(1) + "," + Result.getString(2) + "," + Result.getString(3) + ";");
                }
            } else {
                return "No Users Found";
            }
        } catch (Exception ex) {
            return "Error";
        }
        return Temp;
    }
    
    String DeleteProject(String Project_id) {
        String Temp = "";
        try {
            PreparedStatement Query = con.prepareStatement("update project set project_status='delete' where (project_id=" + Project_id + ");");
            Query.executeUpdate();
        } catch (Exception ex) {
            return "Error " + ex;
        }
        return Temp;
    }
    
    String RemoveUserFromProject(String User_id, String Project_id) {
        String Temp = "";
        try {
            //In this case deleteing from the database is okay, as there isnt another option
            //Would be better to make it invisable however
            PreparedStatement Query = con.prepareStatement("delete from project_user where (project_id = " + Project_id + " AND user_id = " + User_id + ");");
            Query.executeUpdate();
        } catch (Exception ex) {
            return "Error " + ex;
        }
        return Temp;
    }
    
    String DeleteTask(String Task_id) {
        String Temp = "";
        try {
            PreparedStatement Query = con.prepareStatement("update task set task_status='delete' where (task_id=" + Task_id + ");");
            Query.executeUpdate();
        } catch (Exception ex) {
            return "Error " + ex;
        }
        return Temp;
    }
    
    String FinishTask(String Task_id) {
        String Temp = "";
        try {
            PreparedStatement Query = con.prepareStatement("update task set task_status='done' where (task_id=" + Task_id + ");");
            Query.executeUpdate();
        } catch (Exception ex) {
            return "Error " + ex;
        }
        return Temp;
    }
    
    String ApproveTask(String Task_id) {
        String Temp = "";
        try {
            PreparedStatement Query = con.prepareStatement("update task set task_approval='false' where (task_id=" + Task_id + ");");
            Query.executeUpdate();
        } catch (Exception ex) {
            return "Error " + ex;
        }
        return Temp;
    }
}
