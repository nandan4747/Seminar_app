package org.example.client_app;

import javafx.animation.ScaleTransition;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class Effects_ {
    ScaleTransition transition;
    public void Hover_effect(Region obj,Boolean coloring, String bg_color,String fg_color,String gradient_colour1 ,String gradient_colour2,double zoom,int sec){
        transition = new ScaleTransition(Duration.millis(sec),obj);
        transition.setToX(zoom);
        transition.setToY(zoom);
        if(coloring) {
            if (gradient_colour1.isEmpty() && gradient_colour2.isEmpty()) {
                obj.setStyle("-fx-background-color:" + bg_color + ";" + "-fx-text-fill:" + fg_color + ";");
            } else {
                obj.setStyle("-fx-background-color:linear-gradient(from 0% 0% to 100% 0%," + gradient_colour1 + "," + gradient_colour2 + ");" + "-fx-text-color:" + fg_color + ";");
            }
        }
        transition.play();
    }
    public void Cancel_Effect(Region obj,Boolean coloring,double x,double y,String color,String fg_color ,String gradient_color1,String gradient_color2,int sec){
        transition = new ScaleTransition(Duration.millis(sec),obj);
        transition.setToX(x);
        transition.setToY(y);
        transition.play();
        if(coloring) {
            if (gradient_color1.isEmpty() && gradient_color2.isEmpty()) {
                obj.setStyle("-fx-background-color:" + color + ";" + "-fx-text-fill:" + fg_color + ";");
            } else {
                obj.setStyle("-fx-background-color:linear-gradient(from 0% 0% to 100% 0%," + gradient_color1 + "," + gradient_color2 + ");" + "-fx-text-color:" + fg_color + ";");
            }
        }
    }
}
