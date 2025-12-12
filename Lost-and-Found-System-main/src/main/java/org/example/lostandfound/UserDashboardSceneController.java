package org.example.lostandfound;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Properties;

public class UserDashboardSceneController {

    public Label user_full_name_lbl;
    public Label user_srcode_lbl;
    public ScrollPane viewMyReports_ScrllPane;

    private boolean isViewMyReportsPressed  = false;

    @FXML
    protected void onReportLostItemButton(ActionEvent event) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("reportLostItemScene.fxml"));
        Parent root = loader.load();

        reportLostItemSceneController repLostCont = loader.getController();
        repLostCont.target_user_fullname = user_full_name_lbl.getText();
        repLostCont.target_user_srcode = user_srcode_lbl.getText();

        // Get the stage information from the button's source node
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void onReportFoundItemButton(ActionEvent event) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("reportFoundItemScene.fxml"));
        Parent root = loader.load();

        reportFoundItemSceneController repFoundCont = loader.getController();
        repFoundCont.target_user_fullname = user_full_name_lbl.getText();
        repFoundCont.target_user_srcode = user_srcode_lbl.getText();

        // Get the stage information from the button's source node
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void onViewItemButton(ActionEvent event) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("viewReportedListItemScene.fxml"));
        Parent root = loader.load();

        viewListOfReportedItemsSceneController cont = loader.getController();
        cont.user_fullname =  user_full_name_lbl.getText();
        cont.user_srcode =  user_srcode_lbl.getText();
        cont.viewReportedItemListButton();

        // Get the stage information from the button's source node
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void onLogoutSubmitButton(ActionEvent event) throws IOException
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Lost and Found Portal");
        alert.setHeaderText("Log out of your account?");
        alert.setContentText("You can log back in at any time.");

        // Customize the dialog pane
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: white;");

        // Custom buttons
        ButtonType logoutButton = new ButtonType("Log Out", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(logoutButton, cancelButton);

        // Style buttons
        Button logoutBtn = (Button) dialogPane.lookupButton(logoutButton);
        logoutBtn.setStyle(
                "-fx-background-color: #800000; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold;"
        );

        Button cancelBtn = (Button) dialogPane.lookupButton(cancelButton);
        cancelBtn.setStyle(
                "-fx-background-color: #E4E6EB; " +
                        "-fx-text-fill: black;"
        );

        alert.showAndWait().ifPresent(response -> {
            if (response == logoutButton) {
                System.out.println("Logging out...");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("loginScene.fxml"));
                Parent root = null;
                try {
                    root = loader.load();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                // Get the stage information from the button's source node
                Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
        });
    }

    public void onCheckMessageButton(ActionEvent actionEvent) throws SQLException {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setHeaderText(null);

        AppProperties db_properties = new AppProperties("database");
        String db_url = db_properties.prop.getProperty("url") + db_properties.prop.getProperty("db_name");

        try(Connection conn = DriverManager.getConnection(db_url,
                db_properties.prop.getProperty("user"),
                db_properties.prop.getProperty("password"))) {

            String get_status_query = "SELECT user_notification FROM user_info WHERE sr_code = ?";
            try (PreparedStatement get_status_pstmt = conn.prepareStatement(get_status_query)) {
                get_status_pstmt.setString(1, this.user_srcode_lbl.getText());

                try (ResultSet rs = get_status_pstmt.executeQuery()) {
                    if (rs.next()) {
                        String notification = rs.getString("user_notification");
                        alert.setAlertType(Alert.AlertType.INFORMATION);
                        alert.setTitle("Information");
                        alert.setWidth(400);
                        alert.setHeight(300);
                        alert.setContentText(notification);
                        alert.show();
                    }
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void onViewMyReportsButton(ActionEvent actionEvent) {

        this.isViewMyReportsPressed = !this.isViewMyReportsPressed;

        if (this.isViewMyReportsPressed)
        {
            ArrayList<ItemInfo> post_infos = new ArrayList<>();

            AppProperties db_properties = new AppProperties("database");
            String db_url = db_properties.prop.getProperty("url") + db_properties.prop.getProperty("db_name");

            try(Connection conn = DriverManager.getConnection(db_url,
                    db_properties.prop.getProperty("user"),
                    db_properties.prop.getProperty("password"))) {

                String get_status_query = "SELECT item_id, description, date_of FROM item_post WHERE user_srcode = ?";
                try (PreparedStatement get_status_pstmt = conn.prepareStatement(get_status_query)) {
                    get_status_pstmt.setString(1, this.user_srcode_lbl.getText());

                    try (ResultSet rs = get_status_pstmt.executeQuery()) {
                        while (rs.next()) {
                            String item_id = rs.getString("item_id");
                            String description = rs.getString("description");
                            LocalDate date_of = rs.getObject("date_of", LocalDate.class);
                            ItemInfo info = new ItemInfo(item_id, description, null, date_of, null, null, null, null);
                            post_infos.add(info);
                        }
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
            catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            VBox viewListItemPost_vbox = new VBox();
            for (int i=0; i<post_infos.size(); i++)
            {
                VBox subVBox = new VBox();

                ItemInfo item_info = post_infos.get(i);

                Label report_id = new Label("Id: " + item_info.item_id);
                Label date_reported = new Label("Date reported: ");
                DatePicker datePicker = new DatePicker();
                datePicker.setValue(item_info.date_of);

                TextArea description_Txtarea = new TextArea();
                description_Txtarea.setText(item_info.description);
                description_Txtarea.setPrefWidth(300);
                description_Txtarea.setPrefHeight(100);
                description_Txtarea.setWrapText(true);
                description_Txtarea.setStyle("-fx-text-fill: black;");

                subVBox.getChildren().addAll(report_id, date_reported, datePicker, description_Txtarea);
                subVBox.setSpacing(3);
                subVBox.setAlignment(Pos.CENTER);
                viewListItemPost_vbox.getChildren().add(subVBox);
            }
            viewListItemPost_vbox.setSpacing(20);
            viewListItemPost_vbox.setPadding(new Insets(0, 5, 0, 5));
            this.viewMyReports_ScrllPane.setContent(viewListItemPost_vbox);
            this.viewMyReports_ScrllPane.setPannable(true);
        } else {
            this.viewMyReports_ScrllPane.setContent(null);
            this.viewMyReports_ScrllPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            this.viewMyReports_ScrllPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        }
    }
}
