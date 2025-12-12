package org.example.lostandfound;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class onViewReportInfoController {

    public TextField user_fullname_txtField;
    public TextField user_srcode_txtField;
    public TextField user_email_txtField;
    public TextField user_contact_txtField;
    public TextArea item_description_txtArea;
    public ImageView item_image_Imgview;
    public TextArea item_location_txtArea;
    public DatePicker item_dateReported_Dpicker;
    public TextField reportID_txtField;

    public String report_item_id;
    public String user_fullname;
    public String user_srcode;


    public void onTakeScreenshotButton(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setHeaderText(null);

        Scene scene = ((Node)actionEvent.getSource()).getScene();
        WritableImage writableImage = new WritableImage((int) scene.getWidth(), (int) scene.getHeight());
        scene.snapshot(writableImage);

        String userHomeDir = System.getProperty("user.home");

        Path picturesDirPath = Paths.get(userHomeDir, "Pictures");
        File picturesDir = picturesDirPath.toFile();

        // Ensure the directory exists (create if necessary)
        if (!picturesDir.exists()) {
            picturesDir.mkdirs();
        }

        File outFile = new File(picturesDir, this.reportID_txtField.getText() + ".png");

        try {
            // Convert to BufferedImage and write to file
            ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", outFile);
            alert.setAlertType(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setContentText("Snapshot saved to: " + outFile.getAbsolutePath());
            alert.show();
        } catch (IOException ex) {
            System.out.println("Error saving snapshot: " + ex.getMessage());
        }
    }

    public void onReturnButton(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("contactScene.fxml"));
        Parent root = loader.load();

        contactSceneController cont = loader.getController();
        cont.user_fullname = this.user_fullname;
        cont.user_srcode = this.user_srcode;
        cont.report_item_id = this.report_item_id;


        // Get the stage information from the button's source node
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
