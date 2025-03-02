package com.example.project_csc171;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.image.Image;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import javafx.util.Duration;

import javafx.scene.paint.Color;

public class HelloController implements Initializable {
    public ImageView bg;
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
    int img_timer = 0;

    boolean game_on = false;

    boolean chill = false;


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

    Image ninja_stable_1_l = new Image("/ninja_stable_1_l.png");
    Image ninja_stable_1_r = new Image("/ninja_stable_1_r.png");
    Image ninja_stable_2_l = new Image("/ninja_stable_2_l.png");
    Image ninja_stable_2_r = new Image("/ninja_stable_2_r.png");
    Image ninja_moving_l = new Image("/ninja_moving_l.png");
    Image ninja_moving_r = new Image("/ninja_moving_r.png");

    Image chill_l = new Image("/chill_l.png");
    Image chill_r = new Image("/chill_r.png");
    Image chill_moving = new Image("/chill_moving.png");

    Image column_pic = new Image("/column.png");
    Image chainsaw_l = new Image("/chainsaw_l.png");
    Image chainsaw_r = new Image("/chainsaw_r.png");

    URL sound1 = getClass().getResource("/pics.audio/woosh.mp3");
    URL sound2 = getClass().getResource("/pics.audio/loose.mp3");

    Media wooshm = new Media(sound1.toExternalForm());
    Media loosem = new Media(sound2.toExternalForm());

    MediaPlayer woosh = new MediaPlayer(wooshm);
    MediaPlayer loose = new MediaPlayer(loosem);


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
        if(game_on){
            if(keyEvent.getCode() == KeyCode.RIGHT) {
                score_num++;

                woosh.play();

                if(score_num % 5 == 0) time_decrease+=0.01;

                if(score_num % 50 == 0 && score_num != 0) toNight();
                else if(score_num % 100 == 0) toDay();

                if(time_block.getWidth()+20 < 190) time_block.setWidth(time_block.getWidth()+20);
                else time_block.setWidth(190);

                if(ninja.getX() < 200) {
                    play_woosh();
                    if(!chill) ninja.setFill(new ImagePattern(ninja_moving_r));
                    else ninja.setFill(new ImagePattern(chill_moving));
                    moving_right = true;
                } else {
                    if(!chill) ninja.setFill(new ImagePattern(ninja_stable_2_l));
                    else ninja.setFill(new ImagePattern(chill_l));
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
                    play_woosh();
                    if(!chill) ninja.setFill(new ImagePattern(ninja_moving_l));
                    else ninja.setFill(new ImagePattern(chill_moving));
                    moving_left = true;
                } else {
                    if(!chill) ninja.setFill(new ImagePattern(ninja_stable_2_r));
                    else ninja.setFill(new ImagePattern(chill_r));
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
                if(!chill) ninja.setFill(new ImagePattern(ninja_stable_1_r));
                else ninja.setFill(new ImagePattern(chill_r));
                removeStartScreen();
            } else if(keyEvent.getCode() == KeyCode.TAB){
                chill = !chill;
                if(chill) {
                    ninja.setWidth(ninja.getWidth()+20);
                    ninja.setHeight(ninja.getHeight()+20);
                }
                if(!chill) {
                    ninja.setWidth(ninja.getWidth()-20);
                    ninja.setHeight(ninja.getHeight()-20);
                }
            } else{
                if(player_name_text.length()<3 && score_num > 0){
                    handleKeyPress(keyEvent);
                    player_name.setText(player_name_text);
                    if(player_name_text.length()==3){
                        String leaderboard_text = make_leaderboard();
                        leaderboard.setText(leaderboard_text);
                        leaderboard_title.setVisible(true);
                        leaderboard.setVisible(true);
                        leaderboard.toFront();
                        leaderboard_title.toFront();
                    }
                }
            }
        }
    }

    void toDay(){
        bg = new ImageView("pics.audio/2.png");
        System.out.println("DAY");
    }

    void toNight(){
        bg = new ImageView("pics.audio/4.png");
        System.out.println("NIGHT");
    }

    String make_leaderboard() {
        String data = "";
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

        System.out.println(data);

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
        String leaderboard_subtitle_text = "Player is #"+String.valueOf(player_place);

        leaderboard_subtitle.setText(leaderboard_subtitle_text);

        String tofile = "";
        for (Map.Entry<Integer, String> entry : sortedPlayerScores.entrySet()) {
            tofile += entry.getValue() + "" + entry.getKey() + " ";
        }

        try {
            FileWriter myWriter = new FileWriter("src/main/resources/scores.txt");
            myWriter.write(tofile);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return leaderboard_text;
    }

    Map<Integer, String> createSortedPlayerMap(ArrayList<String> playerData) {
        TreeMap<Integer, String> scoreMap = new TreeMap<>(Collections.reverseOrder());

        for (String data : playerData) {
            String playerName = data.substring(0, 3);
            int score = Integer.parseInt(data.substring(3));
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


    void move_obstacles() {
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
            Rectangle robs = new Rectangle(column.getX() + column.getWidth() - 5, 27.0-12.5, 200, 50);
            robs.setFill(new ImagePattern(chainsaw_r));

            obstacles.add(robs);

            plane.getChildren().addAll(robs);
            System.out.println("right");
        } else if (randomness > 0){
            randomness--;
            Rectangle lobs = new Rectangle(column.getX() - 200 + 5, 27.0-12.5, 200, 50);
            lobs.setFill(new ImagePattern(chainsaw_l));

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


    //happening once
    void load(){
        System.out.println("START!");
        ninja.setX(5);
        column.setFill(new ImagePattern(column_pic));
        if(!chill) ninja.setFill(new ImagePattern(ninja_stable_1_r));
        else ninja.setFill(new ImagePattern(chill_r));
    }

    //happening per frame
    void update(){
        if(game_on){
            time++;

            if(img_timer>0){
                img_timer--;
            } else {
                if(ninja.getX()<10) {
                    if(!chill) ninja.setFill(new ImagePattern(ninja_stable_1_r));
                    else ninja.setFill(new ImagePattern(chill_r));
                }
                else if(ninja.getX()>470.0) {
                    if(!chill) ninja.setFill(new ImagePattern(ninja_stable_1_l));
                    else ninja.setFill(new ImagePattern(chill_l));
                }
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
                    if(!chill) ninja.setFill(new ImagePattern(ninja_stable_2_l));
                    else ninja.setFill(new ImagePattern(chill_l));
                    img_timer=10;
                }
            }

            if(moving_left) {
                ninja.setX(ninja.getX()-(481.0/3));
                movement_tracker++;
                if(movement_tracker==3) {
                    moving_left = false;
                    movement_tracker=0;
                    if(!chill) ninja.setFill(new ImagePattern(ninja_stable_2_r));
                    else ninja.setFill(new ImagePattern(chill_r));
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

        play_loose();

        setStartScreen();
    }

    void setStartScreen() {
        player_score.setText(String.valueOf(score_num));

        leaderboard_subtitle.setVisible(true);

        player_name.setVisible(true);
        player_score.setVisible(true);
        start_text.setVisible(true);
        start_bg.setVisible(true);

        start_bg.toFront();

        leaderboard_subtitle.toFront();
        player_name.toFront();
        player_score.toFront();
        start_text.toFront();


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

    void play_woosh() {
        if (woosh != null) {
            woosh.stop();
            woosh.seek(Duration.ZERO);
            woosh.play();
        }
    }

    void play_loose() {
        if (loose != null) {
            loose.stop(); // Stop current playback
            loose.seek(Duration.ZERO); // Reset to start
            loose.play();
        }
    }

}