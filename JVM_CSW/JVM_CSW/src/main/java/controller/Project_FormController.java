package controller;

//import org.json.*;

//import com.mysql.jdbc.Connection;
//import com.mysql.jdbc.PreparedStatement;

import Persistance.Persistance;
import critical_path.TaskDAG;
import critpath.CriticalPath;
import critpath.DAG;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import kotlin.Function;
import kotlin.Pair;
import model.CriticalPathFactory;
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
import model.ChildrenFactory;
import model.ChildrenPairFactory;
import model.ProjectFactory;
import persistance.APersistance;
import persistance.FilePersistence;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static utils.Constants.*;

public class Project_FormController implements Initializable {

    @FXML
    private TextField ProjectName;

    @FXML
    private TextField TeamLeader;

    @FXML
    private TextField Email;

    @FXML
    private TextField StatusTxt;

    @FXML
    private TextField PhoneNumberTxt;

    @FXML
    private DatePicker Deadline;

    @FXML
    private Button SaveButton;

    @FXML
    private Button HomeButton;

    @FXML
    private Label lblToday;

    @FXML
    private Label lblUpcoming;

    @FXML
    private VBox vTaskItems;

    @FXML
    private Label lblGreeting;

    @FXML
    private TextField DurationTxt;

    @FXML
    private TextField ChildrenTxt;

    @FXML
    private TextField IdTxt;

    @FXML
    private Button RefreshBtn;

    @FXML
    private Button ChangeProject;

    @FXML
    private AnchorPane AnchorPanel;

    private ObservableList<CriticalPathFactory> listOfTasks;


    public Project_FormController() {
    }

    @FXML
    protected void SaveData(MouseEvent event) {
        //check if not empty
        if (ProjectName.getText().isEmpty() || TeamLeader.getText().isEmpty() || Email.getText().isEmpty()
                || Deadline.getValue().equals(null) || PhoneNumberTxt.getText().isEmpty() || DurationTxt.getText().isEmpty()) {
            lblGreeting.setTextFill(Color.TOMATO);
            lblGreeting.setText("Enter all details !!");
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
        StatusTxt.clear();
        ChildrenTxt.clear();
        DurationTxt.clear();
        IdTxt.clear();

    }

    private String saveData() {
        String DIRECTORIE_PATH = "./src/main/resources/data/" + ProjectName.getText() ;

        try {
            //final String PROJECT_ROOT = "./src/main/resources/data/project_root2/";
            ChildrenPairFactory ch = new ChildrenPairFactory(this.ChildrenTxt);
            ProjectFactory factory = new ProjectFactory(ProjectName.getText()
                    , StatusTxt.getText(), Email.getText(), PhoneNumberTxt.getText()
                    , TeamLeader.getText(), Deadline.getValue().toString(),
                    IdTxt.getText(), ChildrenTxt.getText(), ch.childs(),
                    Float.parseFloat(DurationTxt.getText()));

            CriticalPathFactory criticalPathFactory = new CriticalPathFactory(ProjectName.getText()
                    , StatusTxt.getText(), Email.getText(), PhoneNumberTxt.getText()
                    , TeamLeader.getText(), Deadline.getValue().toString(),
                    IdTxt.getText(), ChildrenTxt.getText(), ch.childs(),
                    Float.parseFloat(DurationTxt.getText()));

            FilePersistence file = new FilePersistence();
            Pair<ProjectFactory, List<CriticalPathFactory>> load_pair = file.loadProject(DIRECTORIE_PATH);

            if(load_pair.component1() != null) {

                List<CriticalPathFactory> list = new ArrayList<>();
                list.addAll(load_pair.component2());
                list.add(criticalPathFactory);
                file.saveProject(DIRECTORIE_PATH, factory, list);

            } else {
                file.saveProject(DIRECTORIE_PATH, factory, Arrays.asList(criticalPathFactory));
            }
            clearFields();
            return "Success";
        } catch(Exception ex){
            System.out.println(ex.getMessage());
            lblGreeting.setTextFill(Color.TOMATO);
            lblGreeting.setText(ex.getMessage());
            return "Exception";
        }





    }


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

    public final Task<List<CriticalPathFactory>> fetchList = new Task() {

        @Override
        protected List<CriticalPathFactory> call() throws Exception {
            List<CriticalPathFactory> list = null;
            try {

                BufferedReader url = new BufferedReader(new FileReader(Constants.PROJECTS_DATA));
                //System.out.println(url);
                list = new Gson().fromJson(url, new TypeToken<CriticalPathFactory>() {
                }.getType());
                //System.out.println(list);


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

    @FXML
    public void RefreshPage(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() == RefreshBtn) {
            try {
                Node node = (Node) mouseEvent.getSource();
                Stage stage = (Stage) node.getScene().getWindow();
                stage.close();
                Scene scene = new Scene(FXMLLoader.load(getClass().getResource(FXML_PROJECT_FORM)));
                stage.setScene(scene);
                stage.show();


            } catch (IOException ex) {

                System.err.println(ex.getMessage());
            }
        }
    }

    @FXML
    public void ChangeDirectorie(MouseEvent mouseEvent) {

        final FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./src/main/resources/data/"));

        Stage stage = (Stage) AnchorPanel.getScene().getWindow();

        File file = fileChooser.showOpenDialog(stage);
        String ChangetoDirectorie = file.getAbsolutePath();
        Constants.PROJECTS_DATA = ChangetoDirectorie;
        System.out.println(ChangetoDirectorie);
    }
}

