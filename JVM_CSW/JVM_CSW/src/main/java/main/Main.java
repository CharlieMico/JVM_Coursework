/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import critical_path.TaskDAG;
import critpath.CriticalPath;
import critpath.DAG;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import kotlin.jvm.internal.markers.KMutableSet;
import model.Task;
import persistance.FilePersistance;
import scala.Tuple2;
import scala.collection.immutable.Set;
import utils.Constants;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Too
 */
public class Main extends Application {

    // Define your offsets here
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage stage) throws Exception {

        testScala();
        testKotlin();

        URL path = getClass().getResource(Constants.FXML_HOME);
        if (path != null) {
            Parent root = FXMLLoader.load(path);
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle(Constants.APP_TITLE);
            stage.initStyle(StageStyle.TRANSPARENT);

            // Grab your root here
            root.setOnMousePressed((MouseEvent event) -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });

            // Move around here
            root.setOnMouseDragged((MouseEvent event) -> {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            });

            stage.show();
        } else {
            System.exit(-1);
        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);

    }

    /**
     * Tests the scala implementation before integrating into the core application, loads and then finds the critical
     * path of<T> the data stored at ./src/main/resources/data/UNQIUE_PROJECT_IDENTIFIER.json relative to the project root.
     */
    private void testScala() {
        // Load, copyed from HomeController.fetchList
        System.out.println("Scala Test Start");
        List<Task> list = null;
        try {
            BufferedReader url = new BufferedReader(new FileReader(Constants.TASK_FOLDER + "/UNQIUE_PROJECT_IDENTIFIER.json"));
            System.out.println(url);
            list = new Gson().fromJson(url, new TypeToken<List<Task>>() {
            }.getType());
            System.out.println(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Abandon ship if this data doesn't exist for whatever reason
        if(list != null) {
            // Translating from a single list to the Maps expected by the CriticalPath implementation
            // Any map is valid, HashMaps used primarily for ease of typing due to muscle memory.
            Map<String, Task> task_map = new HashMap<>();
            Map<String, List<String>> task_relation = new HashMap<>();
            list.forEach(task -> {
                task_map.put(task.getId(), task);
                task_relation.put(task.getId(), task.getChildren());
            });
            System.out.println(task_relation);
            // End Translating

            // Broken down for ease of reading
            DAG<String> stringDAG = CriticalPath.makeDAG(task_relation);
            List<Tuple2<String, Set<String>>> criticalPath;

            criticalPath = CriticalPath.findCriticalPath("task_1",
                    stringDAG,
                    task_map::get,
                    Task::getDuration
            );

            System.out.println(criticalPath);

            // -- MINIMUM LINE FOR SCALA INTEGRATION -- \\
            // List<Tuple2<String, Set<String>>> path = CriticalPath.findCriticalPath("Task 0", CriticalPath.makeDAG(task_relation), (String task_id) -> task_map.get(task_id), (TasksModel m) -> m.getDuration());

            // Just printing the CriticalPath found.
            for (Tuple2<String, Set<String>> item : criticalPath) {
                System.out.print("Start Point: " + item._1 + ", " + item._2.size() + " Children: [START]->" + item._1 + "->");
                item._2.foreach((e) -> {
                            System.out.print(e + "->");
                            return e;
                        }
                );
                System.out.println("[END]");
            }
        } else System.err.println("File not properally loaded");
        System.out.println("Scala Test End");
    }

    private void testKotlin() {
        System.out.println("Kotlin Critical Path Testing START");
//        FilePersistance file = new FilePersistance();
        List<Task> list = null;
        try {
            BufferedReader url = new BufferedReader(new FileReader(Constants.TASK_FOLDER + "/UNQIUE_PROJECT_IDENTIFIER.json"));
            System.out.println(url);
            list = new Gson().fromJson(url, new TypeToken<List<Task>>() {
            }.getType());
            System.out.println(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(list != null) {
            TaskDAG taskDAG = new TaskDAG(list);
            List<Task> crit_path = taskDAG.findCriticalPath("task_1");

            System.out.print("Start Point: task_1, " + crit_path.size() + " Children: [START]->");
            for(Task t : crit_path) {
                System.out.print(t.getId() + "->");
            }
            System.out.println("[END]");

        }
        System.out.println("Kotlin Critical Path Testing END");
    }
}
