/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import critpath.*;
import Model.TasksModel;
import Utils.Constants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

// Don't like importing these, needs fixing later, but not massively important.
import scala.Tuple2;
import scala.collection.immutable.Set;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.util.*;

/**
 * @author Too
 */
public class Main extends Application {

    // Define your offsets here
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage stage) throws Exception {
        // -- Scala Testing START -- \\
        testScala();
        // --  Scala Testing END  -- \\


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

    private void testScala() {
        // Load, copyed from HomeController.fetchList
        List<TasksModel> list = null;
        try {

            BufferedReader url = new BufferedReader(new FileReader(Constants.PROJECTS_DATA));
            System.out.println(url);
            list = new Gson().fromJson(url, new TypeToken<List<TasksModel>>() {
            }.getType());



        } catch (Exception e) {
            e.printStackTrace();
        }
        if(list != null) {
            // Translating from a single list to the Maps expected by the CriticalPath implementation
            Map<String, TasksModel> task_map = new HashMap<>();
            Map<String, List<String>> task_relation = new HashMap<>();
            list.forEach(task -> {
                task_map.put(task.getId(), task);
                task_relation.put(task.getId(), task.getChildren());
            });
            // End Translating
//             Needs moving to kotlin for loading the data (spec calls for kotlin to handle persistance)
//            task_map.put("Task 0", new TasksModel("Task 0", true, 1, new ArrayList<>()));
//            task_map.put("Task 1", new TasksModel("Task 1", false, 2, new ArrayList<>()));
//            task_map.put("Task 2", new TasksModel("Task 2", false, 1, new ArrayList<>()));
//            task_map.put("Task 3", new TasksModel("Task 3", false, 15, new ArrayList<>()));
//            task_map.put("Task 10", new TasksModel("Task 10", false, 2, new ArrayList<>()));
////             Might want to merge these later, but for now this is fine
//            task_relation.put("Task 0", Arrays.asList("Task 1", "Task 2", "Task 10"));
//            task_relation.put("Task 2", Arrays.asList("Task 3"));
//        List<Tuple2<String, List<String>>> taskRelations = new ArrayList<>();
//        List<String> t = new ArrayList<>();
//        t.add("Task 1");
//        t.add("Task 2");
//        taskRelations.add(new Tuple2<>("Task 0", t));
            DAG<String> stringDAG = CriticalPath.makeDAG(task_relation);

            List<Tuple2<String, Set<String>>> criticalPath;
            criticalPath = CriticalPath.findCriticalPath("task_1",
                    stringDAG,
                    task_map::get
            );

            // -- MINIMUM LINE FOR SCALA INTEGRATION -- \\
            // List<Tuple2<String, Set<String>>> path = CriticalPath.findCriticalPath("Task 0", CriticalPath.makeDAG(task_relation), (String task_id) -> task_map.get(task_id));

            System.out.println(criticalPath.size());
            for (Tuple2<String, Set<String>> item : criticalPath) {
                System.out.print(item._1 + ", " + item._2.size() + " Children: [START]->" + item._1 + "->");
                item._2.foreach((e) -> {
                            System.out.print(e + "->");
                            return e;
                        }
                );
                System.out.println("[END]");
            }

//        List<Set<String>> l = CriticalPath.findCriticalPath(CriticalPath.makeDAG(taskRelations), taskList);
            // End Scala testing
        }
    }


}
