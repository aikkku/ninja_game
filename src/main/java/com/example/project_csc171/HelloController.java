package com.example.project_csc171;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.scene.paint.Color;

public class HelloController implements Initializable {

    AnimationTimer gameLoop;

    @FXML
    private AnchorPane plane;

    @FXML
    private Rectangle ninja;

    @FXML
    private Rectangle column;

    @FXML
    private Rectangle time_back;

    @FXML
    private Text score;

    //todo:
    //   High score handled using files save in file and take from file

    Random r = new Random();
    double time = 0;
    int score_num = 0;
    ArrayList<Rectangle> obstacles = new ArrayList<>();

    Rectangle time_block = new Rectangle(14, 14, 190, 28);


    double time_decrease = 1.0;
    boolean moving_right = false;
    boolean moving_left = false;
    int movement_tracker = 0;
    int movement_tracker_obs = 0;
    boolean moving_obstacles = false;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        load();

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long l) {
                update();
            }
        };
        gameLoop.start();

        time_block.setFill(Color.RED);
        plane.getChildren().add(time_block);
    }

    @FXML
    void pressed(KeyEvent keyEvent) {
        score_num++;

        if(score_num % 5 == 0) time_decrease+=0.01;

        if(time_block.getWidth()+20 < 190) time_block.setWidth(time_block.getWidth()+20);
        else time_block.setWidth(190);

        if(keyEvent.getCode() == KeyCode.RIGHT && ninja.getX() < 200) {
            moving_right = true;
        }
        else if(keyEvent.getCode() == KeyCode.LEFT && ninja.getX() > 200) {
            moving_left = true;
        }
        create_obstacle();
        moving_obstacles = true;
    }

    //made with chatgpt
    private void move_obstacles() {
        for (int i = obstacles.size() - 1; i >= 0; i--) {
            Rectangle rectangle = obstacles.get(i);
            rectangle.setY(rectangle.getY() + 125.0/3);
            if (rectangle.getY() > plane.getHeight()) {
                obstacles.remove(i); // Safe because we're iterating backward
            }
            System.out.println("array size: " + obstacles.size());
        }
    }


    void go_right(){
        ninja.setX(ninja.getX()+(481.0/3));
        movement_tracker++;
//            System.out.println(plane.getWidth() - ninja.getWidth());
        if(movement_tracker==3) {
            moving_right = false;
            movement_tracker=0;
        }
    }

    void go_left(){
        ninja.setX(ninja.getX()-(481.0/3));
        movement_tracker++;
        if(movement_tracker==3) {
            moving_left = false;
            movement_tracker=0;
        }
    }

//    void move_column(){
//        column.setY(column.getY()+20);
//    }

    int randomness = r.nextInt(3);
    boolean randside = r.nextBoolean();
    void create_obstacle(){
        if(randside && randomness > 0) {
            randomness--;
            Rectangle robs = new Rectangle(column.getX() + column.getWidth(), 27.0-12.5, 200, 25);

            obstacles.add(robs);

            plane.getChildren().addAll(robs);
            System.out.println("right");
        } else if (randomness > 0){
            randomness--;
            Rectangle lobs = new Rectangle(column.getX() - 200, 27.0-12.5, 200, 25);
//            (125.0/2.0)+27.9-12.5
            obstacles.add(lobs);

            plane.getChildren().addAll(lobs);
            System.out.println("left");
        } else {
            randside = !randside;
            randomness = r.nextInt(3);

            Rectangle empt = new Rectangle(0, 27.0-12.5, 0, 0);

            obstacles.add(empt);

            plane.getChildren().addAll(empt);
        }
    }


    boolean collisionDetecion() {
        for(Rectangle rectangle: obstacles) {
            if(rectangle.getBoundsInParent().intersects(ninja.getBoundsInParent())){
                System.out.println("Touched");
                return true;
            }
        }
        return false;
    }


    //todo
    boolean isNinjaDead() {
        return false;
    }


    //happening once
    void load(){
        System.out.println("START!");
        ninja.setX(5);
    }



    //happening per frame
    void update(){
        time++;

        score.setText("" + score_num);

        if(collisionDetecion()) {
            resetGame();
        }

        if(moving_right) {
            ninja.setX(ninja.getX()+(481.0/3));
            movement_tracker++;
//            System.out.println(plane.getWidth() - ninja.getWidth());
            if(movement_tracker==3) {
                moving_right = false;
                movement_tracker=0;
            }
        }

        if(moving_left) {
            ninja.setX(ninja.getX()-(481.0/3));
            movement_tracker++;
            if(movement_tracker==3) {
                moving_left = false;
                movement_tracker=0;
            }
        }

        if(moving_obstacles) {
            move_obstacles();
            movement_tracker_obs++;
            if(movement_tracker_obs==3) {
                moving_obstacles=false;
                movement_tracker_obs=0;
            }
        }

        if(time_block.getWidth()>0) time_block.setWidth(time_block.getWidth()-time_decrease);
        else resetGame();

    }

    private void resetGame() {
        plane.getChildren().removeAll(obstacles);
        obstacles.clear();
        time = 0;
        score_num = 0;
        time_decrease = 1.0;
        movement_tracker = 0;
        movement_tracker_obs = 0;
        moving_obstacles = false;
        moving_right = false;
        moving_left = false;
        ninja.setX(5);
        time_block.setWidth(190);
    }
}