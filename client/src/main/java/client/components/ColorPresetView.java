package client.components;

import client.scenes.ColorManagementController;
import commons.ColorEntity;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class ColorPresetView extends HBox implements Component<ColorEntity>{
    private ColorEntity associatedPreset;
    private final ColorManagementController colorManagementController;
    private ColorPicker presetBgColorPicker;
    private ColorPicker presetFontColorPicker;

    /**
     * Constructor
     *
     * @param associatedPreset   associated color preset
     * @param colorManagementController the colorManagement controller
     */
    public ColorPresetView(ColorEntity associatedPreset,
                           ColorManagementController colorManagementController){
        super();
        this.associatedPreset = associatedPreset;
        this.colorManagementController = colorManagementController;
        this.createOverview(associatedPreset);
    }

    /**
     * create overview for a color preset
     *
     * @param newPreset object to be displayed
     */
    public void createOverview(ColorEntity newPreset){
        this.associatedPreset = newPreset;
        Parent content = loadNode("/client.components/colorPresetComponent.fxml");
        this.getChildren().clear();
        this.getChildren().add(content);
        setMargin(content, new Insets(5, 5, 5, 5));
        presetBgColorPicker = (ColorPicker) this.lookup("#presetBgColorPicker");
        presetFontColorPicker = (ColorPicker) this.lookup("#presetFontColorPicker");
        Button deletePresetButton = (Button) this.lookup("#deletePresetButton");

        presetBgColorPicker.setOnAction(event -> {
            associatedPreset.setBackGroundColor("#" +
                    presetBgColorPicker.getValue().toString().substring(2,8));
            colorManagementController.presetColorChanged(associatedPreset);
        });
        presetFontColorPicker.setOnAction(event -> {
            associatedPreset.setFontColor("#" +
                    presetFontColorPicker.getValue().toString().substring(2,8));
            colorManagementController.presetColorChanged(associatedPreset);
        });
        deletePresetButton.setOnMouseClicked(event ->
            colorManagementController.deletePresetClicked(associatedPreset)
        );

        setColor(newPreset);
    }

    /**
     * update overview for a color preset
     *
     * @param updatedPreset object to be displayed
     */
    public void updateOverview(ColorEntity updatedPreset) {
        this.associatedPreset = updatedPreset;
        setColor(updatedPreset);
    }

    /**
     * Utility method that sets values to color picker
     * @param newPreset new preset which values will be displayed
     */
    public void setColor(ColorEntity newPreset){
        presetBgColorPicker.setValue(Color.web(newPreset.getBackGroundColor()));
        presetFontColorPicker.setValue(Color.web(newPreset.getFontColor()));
    }
}
