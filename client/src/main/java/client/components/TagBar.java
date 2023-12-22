package client.components;

import commons.Tag;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import java.util.List;

public class TagBar extends GridPane {
    final int borderRadius = 10;

    /**
     * Creates a bar, which displays the tags of a task, in the overview
     * @param tags the tags to display
     * @param height the expected height for the tagBar
     */
    public TagBar (List<Tag> tags, int height) {
        this.setAlignment(Pos.CENTER);
        this.setMaxHeight(height);

        for(int i = 0; i < tags.size(); i++) {
            HBox box = new HBox();
            box.setPrefHeight(height);

            // Calculate the corners for this rectangle (they have to be rounded if they are on
            // the side
            double[] corners = {0, 0, 0, 0};
            if(i == 0) corners[0] = corners[3] = borderRadius;
            if(i == tags.size() - 1) corners[1] = corners[2] = borderRadius;
            CornerRadii radii = new CornerRadii(corners[0], corners[1], corners[2], corners[3],
                    false);

            // Set the background of this box
            BackgroundFill fill = new BackgroundFill(
                    Color.web(tags.get(i).getTagColor()),
                    radii,
                    Insets.EMPTY
            );

            box.setBackground(new Background(fill));
            box.setBorder(new Border(new BorderStroke(Color.BLACK,
                    BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
            this.add(box, i, 0);

            // Set the width of this box
            ColumnConstraints constraint = new ColumnConstraints();
            constraint.setPercentWidth(100.0 / (1.0 * tags.size()));
            this.getColumnConstraints().add(constraint);
        }

        // If no tags are present insert an empty transparent box
        if(tags.size() == 0) {
            HBox box = new HBox();
            box.setPrefHeight(height);

            box.setBackground(new Background(new BackgroundFill(
                    new Color(0, 0, 0, 0), CornerRadii.EMPTY, Insets.EMPTY)));

            box.getChildren().add(new Label("No tags"));

            this.add(box, 0, 0);
        }

        this.getStyleClass().add("TagBar");
    }

}