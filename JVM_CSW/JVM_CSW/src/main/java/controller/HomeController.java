/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import kotlin.Pair;
import model.CriticalPathFactory;
import model.ProjectFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Cleanup;
import persistance.FilePersistence;
import utils.Constants;

import static utils.Constants.PROJECTS_DATA;


public class HomeController implements Initializable {

    @FXML
    private Button CreateProjectBtn;


    @FXML
    private Label lblProjectName;

    @FXML
    private Label lblUpcoming;

    @FXML
    private VBox vTaskItems;

    @FXML
    private Button CriticalPathButton;

    @FXML
    private AnchorPane AnchorPanel;

    @FXML
    private Button RefreshBtn;

    private ObservableList<ProjectFactory> listOfTasks;

    @FXML
    private void closeWindow(MouseEvent event) {
        System.exit(0);
    }

    @FXML
    public void handleButtonAction(MouseEvent event) {
        if (event.getSource() == CreateProjectBtn) {
            try {
                Node node = (Node) event.getSource();
                Stage stage = (Stage) node.getScene().getWindow();
                stage.close();
                Scene scene = new Scene(FXMLLoader.load(getClass().getResource(Constants.FXML_PROJECT_FORM)));
                stage.setScene(scene);
                stage.show();


            } catch (IOException ex) {

                System.err.println(ex.getMessage());
            }
        }
    }

    @FXML
    public void loadCriticalPathWindow(MouseEvent mouseEvent) {

        if (mouseEvent.getSource() == CriticalPathButton) {
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


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(fetchList);

        fetchList.setOnSucceeded((event) -> {

            listOfTasks = FXCollections.observableArrayList(fetchList.getValue());
            int size = listOfTasks.size();
            lblProjectName.setText("Projects: (" + size + ")");
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

                final String PROJECT_ROOT = PROJECTS_DATA.equals("") ? "./src/main/resources/data/project_root2/" : PROJECTS_DATA;
                FilePersistence file = new FilePersistence();
                List<Pair<ProjectFactory, List<CriticalPathFactory>>> pairs;
                if(PROJECT_ROOT.endsWith(".json"))
                    pairs = file.loadAllProjectsFromIndex(PROJECT_ROOT);
                else if(new File(PROJECT_ROOT).isDirectory())
                    pairs = file.loadAllProjects(PROJECT_ROOT);
                else pairs = new ArrayList<>();
                list = pairs.stream().map(Pair::component1).collect(Collectors.toList());
                System.out.println("Testing");
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
    public void ChangeDirectorie(MouseEvent mouseEvent) {

        final DirectoryChooser directoryChooser = new DirectoryChooser();
        final FileChooser fileChooser = new FileChooser();

        Stage stage = (Stage) AnchorPanel.getScene().getWindow();

        File file = fileChooser.showOpenDialog(stage);
        if(file != null) { // File is null if the file selection is canceled by the user
            String ChangetoDirectorie = file.getAbsolutePath();
            Constants.PROJECTS_DATA = ChangetoDirectorie;
            System.out.println(ChangetoDirectorie);
        }

    }

    @FXML
    public void RefreshPage(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() == RefreshBtn) {
            try {
                Node node = (Node) mouseEvent.getSource();
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
}
