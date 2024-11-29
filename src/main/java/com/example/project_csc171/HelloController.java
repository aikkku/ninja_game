package com.example.project_csc171;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.image.Image;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Scanner;

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

    Rectangle start_bg = new Rectangle(0, 0, 600, 867);
    Text start_text = new Text(0, 400, "Press ENTER to start the game...");

    double time_decrease = 1.0;
    boolean moving_right = false;
    boolean moving_left = false;
    int movement_tracker = 0;
    int movement_tracker_obs = 0;
    boolean moving_obstacles = false;
    int img_timer = 0;

    boolean game_on = false;

    String data;

    Image ninja_stable_1_l = new Image("/ninja_stable_1_l.png");
    Image ninja_stable_1_r = new Image("/ninja_stable_1_r.png");
    Image ninja_stable_2_l = new Image("/ninja_stable_2_l.png");
    Image ninja_stable_2_r = new Image("/ninja_stable_2_r.png");
    Image ninja_moving_l = new Image("/ninja_moving_l.png");
    Image ninja_moving_r = new Image("/ninja_moving_r.png");
    Image column_pic = new Image("/column.png");


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

        start_text.setFill(Color.WHITE);
        start_text.setFont(Font.loadFont("/Wewak.ttf", 45));
        start_text.setStroke(Color.RED);
        start_text.setStrokeWidth(0.5);
        plane.getChildren().add(time_block);
    }

    @FXML
    void pressed(KeyEvent keyEvent) {
        if(game_on){
            score_num++;

            if(score_num % 5 == 0) time_decrease+=0.01;

            if(time_block.getWidth()+20 < 190) time_block.setWidth(time_block.getWidth()+20);
            else time_block.setWidth(190);

            if(keyEvent.getCode() == KeyCode.RIGHT) {
                if(ninja.getX() < 200) {
                    ninja.setFill(new ImagePattern(ninja_moving_r));
                    moving_right = true;
                } else {
                    ninja.setFill(new ImagePattern(ninja_stable_2_l));
                    img_timer=10;
                }

            }
            else if(keyEvent.getCode() == KeyCode.LEFT) {
                if(ninja.getX() > 200){
                    ninja.setFill(new ImagePattern(ninja_moving_l));
                    moving_left = true;
                } else {
                    ninja.setFill(new ImagePattern(ninja_stable_2_r));
                    img_timer=10;
                }
            }
            create_obstacle();
            moving_obstacles = true;
        } else {
            if(keyEvent.getCode() == KeyCode.ENTER) {
                game_on = true;
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
                ninja.setFill(new ImagePattern(ninja_stable_1_r));
                removeStartScreen();
            }
        }
    }

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
        column.setFill(new ImagePattern(column_pic));
        ninja.setFill(new ImagePattern(ninja_stable_1_r));
        start_bg.setFill(Color.rgb(0,0,0,0.5));
        plane.getChildren().add(start_text);
        plane.getChildren().add(start_bg);
    }

    //happening per frame
    void update(){
        if(game_on){
            time++;

            if(img_timer>0){
                img_timer--;
            } else {
                if(ninja.getX()<10) ninja.setFill(new ImagePattern(ninja_stable_1_r));
                else if(ninja.getX()>470.0) ninja.setFill(new ImagePattern(ninja_stable_1_l));
            }

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
                    ninja.setFill(new ImagePattern(ninja_stable_2_l));
                    img_timer=10;
                }
            }

            if(moving_left) {
                ninja.setX(ninja.getX()-(481.0/3));
                movement_tracker++;
                if(movement_tracker==3) {
                    moving_left = false;
                    movement_tracker=0;
                    ninja.setFill(new ImagePattern(ninja_stable_2_r));
                    img_timer=10;
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
    }

    private void resetGame() {
        game_on = false;

        try {
            File myObj = new File("src/main/resources/scores.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                data = myReader.nextLine();
                System.out.println(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        setStartScreen();
    }

    void setStartScreen() {
        start_text.setVisible(true);
        start_bg.setVisible(true);
    }

    void removeStartScreen() {
        start_text.setVisible(false);
        start_bg.setVisible(false);
    }
}