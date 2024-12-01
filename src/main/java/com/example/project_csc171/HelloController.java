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
import javafx.scene.image.Image;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;

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

//    Rectangle start_bg = new Rectangle(0, 0, 600, 867);



    double time_decrease = 1.0;
    boolean moving_right = false;
    boolean moving_left = false;
    int movement_tracker = 0;
    int movement_tracker_obs = 0;
    boolean moving_obstacles = false;
    int img_timer = 0;

    boolean game_on = false;



//    Text start_text = new Text(0, 400, "Press ENTER to start the game...");
    @FXML
    private Text leaderboard_title;

    @FXML
    private Text start_text;

    @FXML
    private Text leaderboard;

    @FXML
    private Text player_name;

    @FXML
    private Text player_score;

    @FXML
    private Rectangle start_bg;

    @FXML
    private Text leaderboard_subtitle;

    String player_name_text="";
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

        time_block.setFill(Color.RED);
        plane.getChildren().add(time_block);
    }

    @FXML
    void pressed(KeyEvent keyEvent) {
        if(game_on){

            if(keyEvent.getCode() == KeyCode.RIGHT) {
                score_num++;

                if(score_num % 5 == 0) time_decrease+=0.01;

                if(time_block.getWidth()+20 < 190) time_block.setWidth(time_block.getWidth()+20);
                else time_block.setWidth(190);

                if(ninja.getX() < 200) {
                    ninja.setFill(new ImagePattern(ninja_moving_r));
                    moving_right = true;
                } else {
                    ninja.setFill(new ImagePattern(ninja_stable_2_l));
                    img_timer=10;
                }
                create_obstacle();
                moving_obstacles = true;
            }
            else if(keyEvent.getCode() == KeyCode.LEFT) {
                score_num++;

                if(score_num % 5 == 0) time_decrease+=0.01;

                if(time_block.getWidth()+20 < 190) time_block.setWidth(time_block.getWidth()+20);
                else time_block.setWidth(190);

                if(ninja.getX() > 200){
                    ninja.setFill(new ImagePattern(ninja_moving_l));
                    moving_left = true;
                } else {
                    ninja.setFill(new ImagePattern(ninja_stable_2_r));
                    img_timer=10;
                }
                create_obstacle();
                moving_obstacles = true;
            }

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
            }else{
                if(player_name_text.length()<3){
                    handleKeyPress(keyEvent);
                    player_name.setText(player_name_text);
                    if(player_name_text.length()==3){
                        String leaderboard_text = make_leaderboard();
                        leaderboard.setText(leaderboard_text);
                        leaderboard_title.setVisible(true);
                        leaderboard.setVisible(true);
                    }
                }
            }
        }
    }

    //todo:
    //  1. make it appear properly
    //  2. add players info to file of scores.txt
    //  3. start screen appear over everything
    //  4. disable from adding info in the beginning of game (check if it the score zero)

    //todo:
    //  1. add chainsaws
    //  2. music
    //  3. sounds

    String make_leaderboard() {
        String[] tmp = data.split(" ");
        ArrayList<String> leaderboardList = new ArrayList<>(Arrays.asList(tmp));
        Map<Integer, String> sortedPlayerScores = createSortedPlayerMap(leaderboardList);
        sortedPlayerScores.put(score_num, player_name_text);
        // Output only the top 3 players
        System.out.println("Top 3 Players:");

        String leaderboard_text="TOP 3:";
        int player_place=0;

        int count = 1;
        for (Map.Entry<Integer, String> entry : sortedPlayerScores.entrySet()) {
            if (player_name_text.equals(entry.getValue())) {
                player_place = count;
            }

            if (count++ < 4) {
                leaderboard_text+="\n" + entry.getValue() + "..." + entry.getKey();
                System.out.println("Player: " + entry.getValue() + ", Score: " + entry.getKey());
            }
        }

        leaderboard_text+="\n" + "Player is #"+String.valueOf(player_place);
        return leaderboard_text;
    }

    Map<Integer, String> createSortedPlayerMap(ArrayList<String> playerData) {
        // Use a TreeMap with a custom comparator for descending order
        TreeMap<Integer, String> scoreMap = new TreeMap<>(Collections.reverseOrder());

        for (String data : playerData) {
            // Extract the player's name (first three letters)
            String playerName = data.substring(0, 3);

            // Extract the score (remaining characters) and parse as an integer
            int score = Integer.parseInt(data.substring(3));

            // Put the score and player name into the map
            scoreMap.put(score, playerName);
        }

        return scoreMap;
    }



    void handleKeyPress(KeyEvent keyEvent) {
        KeyCode code = keyEvent.getCode();

        if (code.isLetterKey()) {
            player_name_text += code.getName().toUpperCase();
            System.out.println("Updated string: " + player_name_text);
        } else if (keyEvent.getCode() == KeyCode.BACK_SPACE) {
            player_name_text = player_name_text.substring(0, player_name_text.length() - 1);
            System.out.println("Updated string: " + player_name_text);
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



        setStartScreen();
    }

    void setStartScreen() {
        player_score.setText(String.valueOf(score_num));

        leaderboard_subtitle.setVisible(true);

        player_name.setVisible(true);
        player_score.setVisible(true);
        start_text.setVisible(true);
        start_bg.setVisible(true);
    }

    void removeStartScreen() {
        leaderboard.setVisible(false);
        leaderboard_title.setVisible(false);
        leaderboard_subtitle.setVisible(false);

        player_name.setVisible(false);
        player_score.setVisible(false);
        start_text.setVisible(false);
        start_bg.setVisible(false);

        player_name_text="";
    }
}