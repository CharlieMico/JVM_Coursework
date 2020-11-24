package controller;

import utils.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import model.ProjectFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoadItems  {


/*
        @Override
        public void initialize (URL url, ResourceBundle rb){
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

        };*/

    }



