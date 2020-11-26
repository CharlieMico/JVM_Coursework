package CriticalPathKotlin;

import model.ProjectFactory;
import utils.Constants;
import critical_path.TaskDAG;
import javafx.fxml.FXML;
import model.CriticalPathFactory;
import persistance.FilePersistence;

import java.awt.*;
import java.util.List;

/**
 * @author josed
 */
public class Kdemo {
    @FXML
    private TextArea CriticalPathArea;

    public Kdemo(){

    }

    public void kotlinDemo() {
        System.out.println("Kotlin Demo Start");
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
    } }

