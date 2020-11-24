package CriticalPathScala;

import utils.Constants;
import critpath.CriticalPath;
import javafx.fxml.FXML;
import model.CriticalPathFactory;
import persistance.FilePersistence;
import scala.Tuple2;
import scala.collection.immutable.Set;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author josed
 */
public class Sdemo {

    @FXML
    private TextArea CriticalPathArea;

    public Sdemo(){

    }

    public void scalaDemo() {
        System.out.println("Scala Demo Start");
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

           /* CriticalPathArea.setText("Start Point: " + item._1 + "," + item._2.size() + "Children: [START]->"+item._1+"->");
            item._2.foreach((e)->{
                CriticalPathArea.setText(e + "->");
                return e;
                    }

                    );*/

            System.out.print("Start Point: " + item._1 + ", " + item._2.size() + " Children: [START]->" + item._1 + "->");
            item._2.foreach((e) -> {
                        System.out.print(e + "->");
                        return e;
                    }
            );
            System.out.println("[END]");
            //CriticalPathArea.setText("[END]");
        }
        System.out.println("Scala Demo End");
    }
}
