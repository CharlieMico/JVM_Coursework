package controller;

//import org.json.*;

//import com.mysql.jdbc.Connection;
//import com.mysql.jdbc.PreparedStatement;

import utils.Constants;
import com.google.gson.Gson;
        import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
//import utils.ConnectionUtil;
import lombok.Cleanup;
import model.ProjectFactory;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static utils.Constants.FXML_HOME;
import static utils.Constants.PROJECTS_DATA;

public class Project_FormController implements Initializable {

    @FXML
    private TextField ProjectName;

    @FXML
    private TextField TeamLeader;

    @FXML
    private TextField Email;

    @FXML
    private TextField UrgencyLevel;

    @FXML
    private TextField PhoneNumberTxt;

    @FXML
    private DatePicker Deadline;

    @FXML
    private Button SaveButton;

    @FXML
    private Label lbl_Missing_Info;

    @FXML
    private Button HomeButton;

    @FXML
    private Label lblToday;

    @FXML
    private Label lblUpcoming;

    @FXML
    private VBox vTaskItems;

    private ObservableList<ProjectFactory> listOfTasks;


    public Project_FormController() {
    }

    @FXML
    protected void SaveData(MouseEvent event) {
        //check if not empty
        if (ProjectName.getText().isEmpty() || TeamLeader.getText().isEmpty() || Email.getText().isEmpty() || Deadline.getValue().equals(null)|| PhoneNumberTxt.getText().isEmpty() ){
            lbl_Missing_Info.setTextFill(Color.TOMATO);
            lbl_Missing_Info.setText("Enter all details !!");
        } else {

            saveData();
        }

    }

    private void clearFields() {
        ProjectName.clear();
        TeamLeader.clear();
        PhoneNumberTxt.clear();
        Email.clear();
        Deadline.setValue(null);
        UrgencyLevel.clear();
    }

    private String saveData() {
        List<ProjectFactory> list = null;
        try {
            //code to add to json

            ProjectFactory factory = new ProjectFactory(ProjectName.getText(),true,Email.getText(),PhoneNumberTxt.getText(),TeamLeader.getText(),Deadline.getValue().toString());
            BufferedReader url = new BufferedReader(new FileReader(Constants.PROJECTS_DATA));
            list = new Gson().fromJson(url, new TypeToken<List<ProjectFactory>>() {
            }.getType());
            list.add(factory);
            Gson gson = new Gson();

            String json = gson.toJson(list);


            System.out.println(json);


            FileWriter fileWriter = new FileWriter(PROJECTS_DATA);         // writing back to the file
            fileWriter.write(json);
            fileWriter.flush();

            Project_FormController p = new Project_FormController();
            clearFields();
            return "Success";

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            lbl_Missing_Info.setTextFill(Color.TOMATO);
            lbl_Missing_Info.setText(ex.getMessage());
            return "Exception";
        }
    }

  /*  @Override
    public void initialize(URL location, ResourceBundle resources) {

    }*/

    public void ReturnHome(MouseEvent event) {
        if (event.getSource() == HomeButton) {
            try {
                Node node = (Node) event.getSource();
                Stage stage = (Stage) node.getScene().getWindow();
                stage.close();
                Scene scene = new Scene(FXMLLoader.load(getClass().getResource(FXML_HOME)));
                stage.setScene(scene);
                stage.show();


            } catch (IOException ex) {

                System.err.println(ex.getMessage());
            }

        }
    }

    @FXML
    private void closeWindow(MouseEvent event) {
        System.exit(0);
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(fetchList);

        fetchList.setOnSucceeded((event) -> {

            listOfTasks = FXCollections.observableArrayList(fetchList.getValue());
            int size = listOfTasks.size();
            lblToday.setText("Today(" + size + ")");
            lblUpcoming.setText("Upcoming(" + 0 + ")");

            try { //load task items to vbox
                Node[] nodes = new Node[size];
                for (int i = 0; i < nodes.length; i++) {
                    //load specific item
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_ITEM_TASK));
                    TaskItemController controller = new TaskItemController();
                    loader.setController(controller);
                    nodes[i] = loader.load();
                    vTaskItems.getChildren().add(nodes[i]);
                    controller.setTask(listOfTasks.get(i));
                }

                // Optional
                for (int i = 0; i < nodes.length; i++) {
                    try {
                        nodes[i] = FXMLLoader.load(getClass().getResource(Constants.FXML_ITEM_TASK));
                        //vTaskItemsupcoming.getChildren().add(nodes[i]);
                    } catch (Exception e) {
                    }
                }
            } catch (Exception e) {
                System.err.println("Error Creating Tasks...");
                System.err.println(e.getMessage());
            }
        });
    }

    public final Task<List<ProjectFactory>> fetchList = new Task() {

        @Override
        protected List<ProjectFactory> call() throws Exception {
            List<ProjectFactory> list = null;
            try {

                BufferedReader url = new BufferedReader(new FileReader(Constants.PROJECTS_DATA));
                System.out.println(url);
                list = new Gson().fromJson(url, new TypeToken<List<ProjectFactory>>() {
                }.getType());
                System.out.println(list);


            } catch (Exception e) {
                e.printStackTrace();
            }
            return list;
        }

    };

    private static String readUrl(String urlString) throws Exception {

        @Cleanup
        BufferedReader reader = null;

        URL url = new URL(urlString);
        reader = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder buffer = new StringBuilder();
        int read;
        char[] chars = new char[1024];
        while ((read = reader.read(chars)) != -1) {
            buffer.append(chars, 0, read);
        }

        return buffer.toString();
    }

}

