package Controller;


//import com.mysql.jdbc.Connection;
//import com.mysql.jdbc.PreparedStatement;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
//import utils.ConnectionUtil;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static Utils.Constants.FXML_HOME;

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
    private DatePicker Deadline;

    @FXML
    private Button SaveButton;

    @FXML
    private Label lbl_Missing_Info; // TODO: Make sure this is assigned a value at some point

    @FXML
    private Button HomeButton;


    public Project_FormController() {
    }

    @FXML
    protected void SaveData(MouseEvent event) {
        //check if not empty
        if (ProjectName.getText().isEmpty() || TeamLeader.getText().isEmpty() || Email.getText().isEmpty() || Deadline.getValue().equals(null)) {
            lbl_Missing_Info.setTextFill(Color.TOMATO);
            lbl_Missing_Info.setText("Enter all details");
        } else {

            saveData();
        }

    }

    private void clearFields() {
        ProjectName.clear();
        TeamLeader.clear();
        Email.clear();
    }

    private String saveData() {

        try {
            //code to add to json
            clearFields();
            return "Success";

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            lbl_Missing_Info.setTextFill(Color.TOMATO);
            lbl_Missing_Info.setText(ex.getMessage());
            return "Exception";
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

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
}
