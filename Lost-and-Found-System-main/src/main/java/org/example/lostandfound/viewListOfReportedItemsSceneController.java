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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;

public class viewListOfReportedItemsSceneController {


    public ScrollPane onViewReportedItemList_ScrollPane;

    public String user_fullname;
    public String user_srcode;

    public void onViewReportedItemListButton(ActionEvent actionEvent) {}

    public void viewReportedItemListButton() throws SQLException {

        ArrayList<ItemInfo> post_infos = new ArrayList<>();

        AppProperties db_properties = new AppProperties("database");
        String db_url = db_properties.prop.getProperty("url") + db_properties.prop.getProperty("db_name");

        try(Connection conn = DriverManager.getConnection(db_url,
                db_properties.prop.getProperty("user"),
                db_properties.prop.getProperty("password"))) {

            String get_status_query = "SELECT * FROM item_post WHERE status = ?";
            try (PreparedStatement get_status_pstmt = conn.prepareStatement(get_status_query))
            {
                get_status_pstmt.setString(1, "approved");

                try (ResultSet rs = get_status_pstmt.executeQuery())
                {
                    while (rs.next())
                    {
                        String item_id = rs.getString("item_id");
                        String description = rs.getString("description");
                        String locaion_of = rs.getString("location_of");
                        LocalDate date_posted = rs.getObject("date_posted", LocalDate.class);
                        String report_type = rs.getString("report_type");
                        String image_attach = rs.getString("image_attach");
                        String user_srcode = rs.getString("user_srcode");
                        ItemInfo info = new ItemInfo(item_id, description, locaion_of, null, date_posted, image_attach, report_type, user_srcode);
                        post_infos.add(info);
                    }
                }
                catch (SQLException e)
                {
                    System.out.println("Error: " + e.getMessage());
                }
            }

            // get post info
            VBox viewListItemPost_vbox = new VBox();
            for (int i=0; i<post_infos.size(); i++)
            {
                VBox subVBox = new VBox();

                ItemInfo item_info = post_infos.get(i);

                TextArea description_Txtarea = new TextArea();
                description_Txtarea.setText(item_info.description);
                description_Txtarea.setPrefHeight(100);
                description_Txtarea.setWrapText(true);

                TextArea location_of_Txtarea = new TextArea();
                location_of_Txtarea.setText(item_info.location_of);
                location_of_Txtarea.setPrefHeight(50);
                location_of_Txtarea.setWrapText(true);

                DatePicker datePicker = new DatePicker();
                datePicker.setValue(item_info.date_posted);

                AppProperties filestorage_props = new AppProperties("filestorage");
                String imageUrl = filestorage_props.prop.getProperty("url") + filestorage_props.prop.getProperty("bucket_name") + "/" + item_info.image_attach;

                Image image = new Image(imageUrl);
                ImageView image_view = new ImageView(image);
                image_view.setFitWidth(200);
                image_view.setFitHeight(200);
                image_view.setPreserveRatio(true);

                Label reportType_lbl = new Label("Report Type: " + item_info.report_type + " item");
                reportType_lbl.setFont(new Font("Arial", 16));
                Label descrip_lbl = new Label("Description:");
                Label location_lbl = new Label("Location:");

                subVBox.getChildren().addAll(new Label("Date posted: "), datePicker, image_view, reportType_lbl, descrip_lbl, description_Txtarea, location_lbl, location_of_Txtarea);
                subVBox.setSpacing(3);
                subVBox.setAlignment(Pos.CENTER);
                viewListItemPost_vbox.getChildren().add(subVBox);

                System.out.println("Post display");
                String btn_name = "Contact the admin";
                Button btn = new Button(btn_name);
                btn.setId(String.valueOf(i));

                btn.setOnAction(event -> {
                    Button clicked_btn = (Button) event.getSource();
                    int btn_id = Integer.parseInt(clicked_btn.getId());
                    ItemInfo found_report_info = post_infos.get(btn_id);

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("contactScene.fxml"));
                    Parent root = null;

                    try {
                        root = loader.load();

                        contactSceneController contact_scene_controller = loader.getController();
                        contact_scene_controller.report_item_id = found_report_info.item_id;
                        contact_scene_controller.user_fullname = this.user_fullname;
                        contact_scene_controller.user_srcode = this.user_srcode;
                        // Get the stage information from the button's source node
                        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        stage.show();

                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
                subVBox.getChildren().add(btn);
            }
            viewListItemPost_vbox.setSpacing(50);
            viewListItemPost_vbox.setPadding(new Insets(0, 5, 0, 5));
            this.onViewReportedItemList_ScrollPane.setContent(viewListItemPost_vbox);
            this.onViewReportedItemList_ScrollPane.setPannable(true);
        }
        catch (SQLException e)
        {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void onReturnButton(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("userDashboardScene.fxml"));
        Parent root = loader.load();

        UserDashboardSceneController user_dashboard = loader.getController();
        user_dashboard.user_full_name_lbl.setText(this.user_fullname);
        user_dashboard.user_srcode_lbl.setText(this.user_srcode);

        // Get the stage information from the button's source node
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}

