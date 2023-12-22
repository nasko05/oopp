package client.components;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class TaskListButton extends Button {
    /**
     * Creates a new button which deletes/renames/adds a certain task list
     *
     * @param onClick  when clicked, the task list will be deleted/renamed
     * @param function the method to be called when button is clicked
     */
    public TaskListButton(String function, EventHandler<ActionEvent> onClick) {
        ImageView imageView = new ImageView(new Image("icons/" + function + "Button.png"));
        imageView.setFitHeight(25);
        imageView.setPreserveRatio(true);
        this.setGraphic(imageView);

        this.getStyleClass().add("buttonWithHandCursor");

        this.setPadding(Insets.EMPTY);

        this.setOnAction(onClick);
        this.getStyleClass().add(function + "TaskListButton");
        this.getStyleClass().add("TaskListButton");
    }

}