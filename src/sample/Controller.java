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

    private Media media;

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

            fileChooser.setInitialDirectory(file.getParentFile());

            try {
                media = new Media(file.toURI().toURL().toString());

                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                }

                mediaPlayer = new MediaPlayer(media);

                mediaPlayer.setOnReady(() -> {
                    caption.setText(media.getMetadata().get("artist").toString());
                });

                mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                    if (mediaPlayer.totalDurationProperty().getValue() != null) { //TODO why this null happens during change of track?
                        progress.progressProperty().setValue(newValue.toSeconds() / mediaPlayer.totalDurationProperty().getValue().toSeconds());
                    }
                });

                mediaPlayer.setOnEndOfMedia(() -> {
                    mediaPlayer.stop();
                    progress.progressProperty().setValue(0);
                    playPause.setText("Play");
                    caption.setText("Stopped");
                });

                mediaPlayer.play();

                playPause.setText("Pause");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        }
    }
}
