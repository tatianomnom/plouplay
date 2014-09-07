package sample;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.util.Duration;

public class Controller {
    @FXML
    private ListView<String> playList;
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
        playList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    String selectedItem = playList.getSelectionModel().getSelectedItem();
                    openFile(new File(new URI(selectedItem)));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    protected void handlePlayPauseAction(ActionEvent event) {
        if (mediaPlayer != null) {
            toggleMediaPlayerPlayback(mediaPlayer);
            togglePlayPauseButton(playPause);
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

            openFile(file);

        }
    }

    private void openFile(File file) {
        try {
            media = new Media(file.toURI().toURL().toString());

            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }

            mediaPlayer = new MediaPlayer(media);

            mediaPlayer.setOnReady(() -> {
                caption.setText(media.getMetadata().get("artist").toString());
                if (!playList.getItems().contains(media.getSource())) {
                    playList.getItems().add(media.getSource());
                }
            });

            progress.setOnMouseClicked(event -> {
                double sceneX = event.getSceneX();
                double progressBarLength = progress.getWidth();
                double mousePositionFromProgressbarLeft = sceneX - progress.getLayoutX();
                double percent = mousePositionFromProgressbarLeft / progressBarLength;

                double totalMediaLength = mediaPlayer.totalDurationProperty().getValue().toSeconds();

                mediaPlayer.seek(new Duration(totalMediaLength * percent * 1000));
            });

            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                if (mediaPlayer.totalDurationProperty().getValue() != null) {
                    progress.progressProperty().setValue(newValue.toSeconds() / mediaPlayer.totalDurationProperty().getValue().toSeconds());
                }
            });

            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.stop();
                progress.progressProperty().setValue(0);
                togglePlayPauseButton(playPause);
                caption.setText("Stopped");
            });

            mediaPlayer.play();

            togglePlayPauseButton(playPause);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    //TODO or extend button with this method?
    private void togglePlayPauseButton(Button button) {
        switch (button.getText()) {
            case "Play":
                button.setText("Pause");
                break;
            case "Pause":
                button.setText("Play");
                break;
            default:
                throw new IllegalArgumentException("Not a Play/Pause button!");
        }
    }

    private void toggleMediaPlayerPlayback(MediaPlayer mediaPlayer) {
        if (mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.play();
        }
    }
}
