package controller;

import CriticalPathKotlin.Kdemo;
import CriticalPathScala.Sdemo;
import critical_path.TaskDAG;
import critpath.CriticalPath;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import model.CriticalPathFactory;
import persistance.FilePersistence;
import scala.Tuple2;
import scala.collection.immutable.Set;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.ProjectFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



/**
 * @author josed
 */


public class CriticalPathController implements Initializable {

    @FXML
    private Button KotlinButton;

    @FXML
    private Button ScalaButton;


    @FXML
    private Label lblToday;

    @FXML
    private Label lblUpcoming;

    @FXML
    private VBox vTaskItems;
    @FXML
    private Button HomeButton1;

    @FXML
    private TextArea CriticalPathArea;

    @FXML
    private Button RefreshBtn;

    @FXML
    private Button ChangeProjectBtn;

    @FXML
    private AnchorPane AnchorPanel;


    private ObservableList<CriticalPathFactory> listOfTasks;

    @FXML
    private void closeWindow(MouseEvent event) {
        System.exit(0);
    }



    @FXML
    public void LoadScala(MouseEvent mouseEvent) {

        System.out.println("Scala Demo Start");
        CriticalPathArea.clear();
        CriticalPathArea.setText("Scala Demo Start");
        List<CriticalPathFactory> task_list = new FilePersistence().loadTasks(Constants.PROJECTS_DATA);
        //List<ProjectFactory> project_list   = new FilePersistence().loadProjects(Constants.PROJECTS_DATA);

        Map<String, CriticalPathFactory> task_map = new HashMap<>();
        Map<String, List<String>> task_relation = new HashMap<>();
        task_list.forEach(task -> {
            task_map.put(task.getId(), task);

            task_relation.put(task.getId(), task.getChildren());
        });

        List<Tuple2<String, Set<String>>> criticalPath = CriticalPath.findCriticalPath("task_1",
                CriticalPath.makeDAG(task_relation),
                task_map::get,
                CriticalPathFactory::getDuration);

        for (Tuple2<String, Set<String>> item : criticalPath) {





            System.out.print("Start Point: " + item._1 + ", " + item._2.size() + " Children: [START]->" + item._1 + "->");
            CriticalPathArea.setText(CriticalPathArea.getText()+"\n"+item._1 + ", " + String.valueOf(item._2.size()) +
                    " Children: [START]-> " + item._1 + "->" );
            item._2.foreach((e) -> {
                        System.out.print(e + "->");
                CriticalPathArea.setText(CriticalPathArea.getText() + e + "->");
                        return e;
                    }
            );
            System.out.println("[END]");
            CriticalPathArea.setText(CriticalPathArea.getText() + "\n" + "[END]" + "\n" + "Scala Demo End");

        }
        System.out.println("Scala Demo End");



    }

    @FXML
    public void LoadKotlin(MouseEvent mouseEvent) {

        System.out.println("Kotlin Demo Start");
        CriticalPathArea.clear();
        CriticalPathArea.setText("Kotlin Demo Start");
        List<CriticalPathFactory> task_list = new FilePersistence().loadTasks(Constants.PROJECTS_DATA);

        //List<ProjectFactory> project_list   = new FilePersistence().loadProjects(Constants.PROJECTS_DATA);

        TaskDAG graph = new TaskDAG(task_list);

        List<CriticalPathFactory> path = graph.findCriticalPath("task_1");
        CriticalPathArea.setText(CriticalPathArea.getText() + "Start Point: task_1, "+ "\n" + String.valueOf(path.size() + " Children: [START]->") );


        System.out.print("Start Point: task_1, " + path.size() + " Children: [START]->");
        for (CriticalPathFactory node : path) {
            CriticalPathArea.setText(CriticalPathArea.getText() + node.getId() + "->");
            System.out.print(node.getId() + "->");
        }
        System.out.println("[END]");
        CriticalPathArea.setText(CriticalPathArea.getText()+"\n"+"[END]"+"\n"+"Kotlin Demo End");
        System.out.println("Kotlin Demo End");
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
                System.out.println(url);
                list = new Gson().fromJson(url, new TypeToken<List<CriticalPathFactory>>() {
                }.getType());
                //System.out.println(list);


            } catch (Exception e) {
                e.printStackTrace();
            }
            return list;
        }


    };

    public void ReturnHome(MouseEvent event) {
        if (event.getSource() == HomeButton1) {
            try {
                Node node = (Node) event.getSource();
                Stage stage = (Stage) node.getScene().getWindow();
                stage.close();
                Scene scene = new Scene(FXMLLoader.load(getClass().getResource(Constants.FXML_HOME)));
                stage.setScene(scene);
                stage.show();


            } catch (IOException ex) {

                System.err.println(ex.getMessage());
            }

        }
    }

    @FXML
    public void RefreshPage(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() == RefreshBtn) {
            try {
                Node node = (Node) mouseEvent.getSource();
                Stage stage = (Stage) node.getScene().getWindow();
                stage.close();
                Scene scene = new Scene(FXMLLoader.load(getClass().getResource(Constants.FXML_Critical_PATH)));
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
