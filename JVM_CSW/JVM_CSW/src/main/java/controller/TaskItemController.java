/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.util.ResourceBundle;

import model.ProjectFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import utils.Constants;

/**
 * FXML Controller class
 *
 * @author Too
 */
public class TaskItemController implements Initializable {

    @FXML
    private ImageView iconSelect;

    @FXML
    private Label lblTaskName;

    @FXML
    private Button btnInfo;

    @FXML
    private Label lblTeamLeader;

    @FXML
    private Label lblEmail;

    @FXML
    private Label lblTlf;

    @FXML
    private Label lblDeadline;

    @FXML
    private Label lblChildren;

    @FXML
    private Label lblDuration;


    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void setTask(ProjectFactory model) {
        ContextMenu menu = new ContextMenu();
        System.out.println(model.toString());
        lblTaskName.setText(model.getName());
        lblTeamLeader.setText(model.getTeamLeader());
        lblEmail.setText(model.getEmail());
        lblTlf.setText(model.getTlf());
        lblDeadline.setText(model.getDeadline());
        lblChildren.setText(model.getChild());
        lblDuration.setText(String.valueOf(model.getDuration()));


        if (model.getStatus()) {
            btnInfo.setText("Complete");
            iconSelect.setImage(new Image(getClass().getResourceAsStream(Constants.ICON_CHECK_FILL)));
            menu.getItems().add(new MenuItem("Set Task InComplete"));
        } else {
            btnInfo.setText("InComplete");
            iconSelect.setImage(new Image(getClass().getResourceAsStream(Constants.ICON_CHECK_UNFILL)));
            menu.getItems().add(new MenuItem("Set Task Complete"));
        }

        lblTaskName.setContextMenu(menu);
    }

}
