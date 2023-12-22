package client.components;

import client.scenes.MainCtrl;
import client.scenes.TaskViewController;
import commons.Board;
import commons.Tag;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class TagView extends HBox implements Component<Tag> {
    private final TaskViewController taskViewController;
    private final MainCtrl mainCtrl;
    private Tag associatedTag;
    private Label label;
    private HBox content;

    /**
     * Constructor
     *
     * @param taskViewController associated taskViewController
     * @param associatedTag      associated Tag
     * @param mainCtrl           instance of main control
     */
    public TagView(TaskViewController taskViewController,
                   Tag associatedTag, MainCtrl mainCtrl) {
        super();
        this.taskViewController = taskViewController;
        this.associatedTag = associatedTag;
        this.mainCtrl = mainCtrl;
        this.createOverview(associatedTag);
    }

    /**
     * Constructor (for board list of tags)
     *
     * @param associatedTag tag
     * @param mainCtrl      mainctrl
     */
    public TagView(Tag associatedTag, MainCtrl mainCtrl) {
        super();
        this.associatedTag = associatedTag;
        this.mainCtrl = mainCtrl;
        this.createOverview(associatedTag);
        this.taskViewController = null;
    }

    /**
     * Create a view of a tag
     *
     * @param tag tag that the tag belongs to
     */
    public void createOverview(Tag tag) {
        this.associatedTag = tag;

        content = loadNode("/client.components/tagComponent.fxml");
        this.getChildren().clear();
        this.getChildren().add(content);
        this.label = (Label) this.lookup("#tagText");
        Button editButton = (Button) this.lookup("#editButton");
        Button deleteButton = (Button) this.lookup("#closeButton");
        this.label.setText(tag.getDescription());
        this.setStyle(tag);
        content.setPrefWidth(188);
        this.setPrefWidth(188);
        FlowPane.setMargin(this, new Insets(5, 2, 5, 2));
        VBox.setMargin(this, new Insets(5, 5, 5, 5));
        // TODO: separate these 2 methods into the service, cause they are too big
        if (taskViewController == null) {
            deleteButton.setOnMouseClicked(event -> {
                var rights = mainCtrl.getBoardViewById(tag.getBoardId()).isWriteAccess();
                var inAdmin = mainCtrl.isInAdmin();
                if(!rights && !inAdmin){
                    mainCtrl.showError("You do not have the right write access");
                } else {
                    mainCtrl.getServerUtils().deleteTagFromBoard(tag);
                    Board newBoard = mainCtrl.getServerUtils().getBoardByID(tag.getBoardId());
                    mainCtrl.getBoardViewById(tag.getBoardId())
                        .updateOverview(newBoard);
                    mainCtrl.sendToOthers(newBoard);
                }
            });
        } else {
            deleteButton.setOnMouseClicked(event -> {
                if (!taskViewController.isWriteAccess()) {
                    taskViewController.showError("You do not have the right write access");
                } else {
                    taskViewController.removeTagClicked(associatedTag);
                    mainCtrl.getServerUtils().removeTag(tag.getId(),
                        taskViewController.getCurrentTask().getId());
                    taskViewController.updateOnChange();
                }
            });
        }

        editButton.setOnMouseClicked(event -> {
                var rights = mainCtrl.getBoardViewById(tag.getBoardId()).isWriteAccess();
                var inAdmin = mainCtrl.isInAdmin();
                if (!rights && !inAdmin) {
                    mainCtrl.showError("You do not have the right write access");
                } else {
                    mainCtrl.showEditTagScene(tag, taskViewController, null);
                }
            }
        );
    }

    /**
     * Update the current overview
     *
     * @param newTag tag
     */
    public void updateOverview(Tag newTag) {
        this.label.setText(newTag.getDescription());
        this.setStyle(newTag);
        this.associatedTag = newTag;
    }

    /**
     * Utility method that sets new style
     *
     * @param newTag new tag with new style
     */
    private void setStyle(Tag newTag) {
        String style = "";
        style += "-fx-background-color: " + newTag.getTagColor() + ";";
        style += "-fx-background-radius: 10";
        this.setStyle(style);
        this.label.setStyle(style);
        content.setStyle(style);
        this.label.setTextFill(Color.web(newTag.getTagFontColor()));
    }
}
