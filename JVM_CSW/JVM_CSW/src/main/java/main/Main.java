/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
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
import kotlin.Pair;
import model.ChildrenPairFactory;
import model.CriticalPathFactory;
import model.ProjectFactory;
import persistance.FilePersistence;
import scala.Tuple2;
import scala.collection.immutable.Set;
import utils.Constants;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import data.UltimateTest;

/**
 * @author Too
 */
public class Main extends Application {

    // Define your offsets here
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage stage) throws Exception {

        System.out.println(UltimateTest.INSTANCE.a(""));


//        System.out.println("Test");
//
//        final String PROJECT_ROOT = "./src/main/resources/data/project_root2/";
//
//        FilePersistence file = new FilePersistence();
//
//        // Dummy data
//        ProjectFactory dummy_project = new ProjectFactory(
//                "some_name",
//                "true",
//                "some@email",
//                "1234",
//                "some leader",
//                "2015-02-02",
//                "some_id",
//                "",
//                new ArrayList<>(),
//                1
//        );
//        TaskDAG taskDAG = new TaskDAG(new ArrayList<>());
//        DAG<CriticalPathFactory> scalaDAG = CriticalPath.makeDAG(new HashMap<>());
//
//        ArrayList<CriticalPathFactory> children = new ArrayList<>();
//        Function<String, CriticalPathFactory> emptyTask = (String s) -> new CriticalPathFactory(s, new ArrayList<>(), 1);
//
//        for(int i = 0; i < 10; i++) children.add(emptyTask.apply("task_" + i));
////        children.forEach((CriticalPathFactory c) -> taskDAG.extend(c, children));
////        children.forEach((CriticalPathFactory c) -> scalaDAG.extend(c, (Set<CriticalPathFactory>) children));
//
//
//        // Load
//        List<Pair<ProjectFactory, List<CriticalPathFactory>>> projects = file.loadAllProjects(PROJECT_ROOT);
//        System.out.println(projects.size());
//
//        // Add manually created data
////        projects.add(new Pair<>(dummy_project, taskDAG.toList()));
//
//
//        // Displaying contents of projects
//        projects
//            .stream().filter(Objects::nonNull)
//            .forEach((Pair<ProjectFactory, List<CriticalPathFactory>> e) -> {
//                if(e.component1() == null || e.component2() == null) return;
//                System.out.println(e.component1().getId());
//                e.component2().forEach((CriticalPathFactory a) -> System.out.println("---" + a.getId()));
//            });
//
//        HashMap<ProjectFactory, Pair<TaskDAG, DAG<CriticalPathFactory>>> dag_map = new HashMap<>();
//        projects.stream().filter(Objects::nonNull)
//            .forEach((Pair<ProjectFactory, List<CriticalPathFactory>> e) -> {
//                if(e.component1() == null || e.component2() == null) return;
//            });
//
//
//        // Update project index (so load works)
//        List<ProjectFactory> index = new ArrayList<>();
//        projects.forEach(e ->index.add(e.component1()));
//        System.out.println(index.size());
//
//        // Might want to merge these, as index is derived from projects and logically these should always want to be called together
//        file.saveProjectIndex(PROJECT_ROOT, index);
//        file.saveAllProjects(PROJECT_ROOT, projects);
//
//        projects.forEach(e -> new TaskDAG(e.component2()).findCriticalPath(null));

//        testScala();

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
        List<CriticalPathFactory> list = null;
        try {

            BufferedReader url = new BufferedReader(new FileReader(Constants.TRIAL_FILE));
            System.out.println(url);
            list = new Gson().fromJson(url, new TypeToken<List<CriticalPathFactory>>() {
            }.getType());



        } catch (Exception e) {
            e.printStackTrace();
        }
        // Abandon ship if this data doesn't exist for whatever reason
        if(list != null) {
            // Translating from a single list to the Maps expected by the CriticalPath implementation
            // Any map is valid, HashMaps used primarily for ease of typing due to muscle memory.
            Map<String, CriticalPathFactory> task_map = new HashMap<>();
            Map<String, List<String>> task_relation = new HashMap<>();
            list.forEach(task -> {
                task_map.put(task.getId(), task);

                task_relation.put(task.getId(), task.getChildren());
            });
            // End Translating

            // Broken down for ease of reading
            DAG<String> stringDAG = CriticalPath.makeDAG(task_relation);
            List<Tuple2<String, Set<String>>> criticalPath;

            criticalPath = CriticalPath.findCriticalPath("task_1",
                    stringDAG,
                    task_map::get,
                    CriticalPathFactory::getDuration
            );

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
        }
    }


}
