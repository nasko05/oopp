package client.components;

import commons.SubTask;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class SubtaskBar extends GridPane {

    /**
     * Creates a container which stores the information of the subtasks of a task,
     * in the overview
     * @param subTasks the subtasks that should be displayed in the bar
     * @param height the height of the subtask bar
     */
    public SubtaskBar(List<SubTask> subTasks, double height) {

        // Set the alignment
        this.setAlignment(Pos.CENTER);

        // Set sizes for the columns
        ColumnConstraints column1 = new ColumnConstraints();
        ColumnConstraints column2 = new ColumnConstraints();
        column1.setPercentWidth(80);
        column2.setPercentWidth(20);
        this.getColumnConstraints().add(column1);
        this.getColumnConstraints().add(column2);

        int finishedSubtasks = (int) subTasks.stream().filter(SubTask::isChecked).count();

        // Add the progress bar
        if(subTasks.size() != 0) {
            VBox container = new VBox();
            ProgressBar progressBar = new ProgressBar();
            progressBar.setProgress(finishedSubtasks / (1.0 * subTasks.size()));
            container.getChildren().add(progressBar);
            container.setPrefWidth(Double.MAX_VALUE);
            progressBar.setPrefWidth(Double.MAX_VALUE);
            progressBar.setPrefHeight(height);
            this.add(container, 0, 0);

            container.getStyleClass().add("ProgressBarContainer");
            progressBar.getStyleClass().add("ProgressBar");
        }

        // Add the label "x / y" for finished subtasks
        String message = "" + finishedSubtasks + " / " + subTasks.size();
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-weight: 800");
        messageLabel.setAlignment(Pos.CENTER);
        HBox labelContainer = new HBox();
        labelContainer.getChildren().add(messageLabel);
        labelContainer.setAlignment(Pos.CENTER_RIGHT);

        this.add(labelContainer, 1, 0);
    }
}
