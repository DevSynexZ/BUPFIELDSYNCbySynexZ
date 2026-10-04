package com.fieldsync.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class StatCard extends VBox {

    private final Label lblTitle = new Label();
    private final Label lblValue = new Label();
    private final Label lblSubtext = new Label();

    public StatCard(String title, String initialValue, String subtext) {
        this.getStyleClass().add("bento-card");
        this.setSpacing(8);
        this.setAlignment(Pos.TOP_LEFT);
        this.setPadding(new Insets(16));

        lblTitle.setText(title.toUpperCase());
        lblTitle.getStyleClass().add("card-subtitle");

        lblValue.setText(initialValue);
        lblValue.getStyleClass().add("kpi-value");

        lblSubtext.setText(subtext);
        lblSubtext.getStyleClass().add("kpi-trend");

        this.getChildren().addAll(lblTitle, lblValue, lblSubtext);
    }

    public void updateValue(String newValue) {
        lblValue.setText(newValue);
    }
}