package sample;

import java.io.File;
import java.net.MalformedURLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;

public class Controller {
    @FXML
    private ProgressBar progress;
    @FXML
    private Button playPause;
    @FXML
    private Button open;
    @FXML
    private Label caption;

    private FileChooser fileChooser;

    private MediaPlayer mediaPlayer;

    @FXML
    protected void initialize() {
    }

    @FXML
    protected void handlePlayPauseAction(ActionEvent event) {
        if (mediaPlayer != null) {
            if (playPause.getText().equals("Play")) {
                mediaPlayer.play();
                playPause.setText("Pause");
            } else {
                mediaPlayer.pause();
                playPause.setText("Play");
            }
        }
    }

    @FXML
    protected void handleOpenFileAction(ActionEvent event) {
        if (fileChooser == null) {
            fileChooser = new FileChooser();

            FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("MP3 files (*.mp3)", "*.mp3");
            fileChooser.getExtensionFilters().add(extFilterPNG);

            String currentDir = System.getProperty("user.home") + File.separator;

            fileChooser.setInitialDirectory(new File(currentDir));
        }

        File file = fileChooser.showOpenDialog(null);

        if (file != null) {

            Media media;

            try {
                media = new Media(file.toURI().toURL().toString());
                mediaPlayer = new MediaPlayer(media);

                mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) ->
                        progress.progressProperty().setValue(newValue.toSeconds() / mediaPlayer.totalDurationProperty().getValue().toSeconds()));

                mediaPlayer.play();
                playPause.setText("Pause");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        }
    }
}
