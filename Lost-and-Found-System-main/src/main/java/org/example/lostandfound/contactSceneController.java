package org.example.lostandfound;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.Properties;

public class contactSceneController {

    public String report_item_id;
    public String user_fullname;
    public String user_srcode;

    public void onViewReportInfoButton(ActionEvent actionEvent) throws SQLException, IOException {
        System.out.println(report_item_id);

        ItemInfo info = null;

        String user_fullname = "";
        String user_email = "";
        String user_contact = "";

        // retrieve info from db
        AppProperties db_properties = new AppProperties("database");
        String db_url = db_properties.prop.getProperty("url") + db_properties.prop.getProperty("db_name");

        try(Connection conn = DriverManager.getConnection(db_url,
                db_properties.prop.getProperty("user"),
                db_properties.prop.getProperty("password"))) {

            String get_item_post_query = "SELECT * FROM item_post WHERE item_id = ?";
            try (PreparedStatement get_item_post_pstmt = conn.prepareStatement(get_item_post_query))
            {
                get_item_post_pstmt.setString(1, this.report_item_id);

                // execute query
                try (ResultSet rs = get_item_post_pstmt.executeQuery())
                {
                    if (rs.next())
                    {
                        info = new ItemInfo(rs.getString("item_id"), rs.getString("description"), rs.getString("location_of"),
                                rs.getObject("date_of", LocalDate.class), rs.getObject("date_posted", LocalDate.class), rs.getString("image_attach"),
                                rs.getString("report_type"), rs.getString("user_srcode"));
                    }
                    rs.close();
                    get_item_post_pstmt.close();
                } catch (SQLException e)
                {
                    System.out.println("Error: " + e.getMessage());
                }
            }
            catch (SQLException e)
            {
                System.out.println("Error: " + e.getMessage());
            }

            // get user info
            String get_user_info_query = "SELECT user_fullname, user_email, user_contact_number FROM user_info WHERE sr_code = ?";
            try (PreparedStatement get_user_info_pstmt = conn.prepareStatement(get_user_info_query))
            {
                get_user_info_pstmt.setString(1, info.target_user_srcode);

                // execute query
                try (ResultSet rs = get_user_info_pstmt.executeQuery())
                {
                    if (rs.next())
                    {
                        user_fullname = rs.getString("user_fullname");
                        user_email = rs.getString("user_email");
                        user_contact = rs.getString("user_contact_number");

                        System.out.println(user_fullname + " " + user_email + " " + user_contact);
                    }
                    rs.close();
                    get_user_info_pstmt.close();
                } catch (SQLException e)
                {
                    System.out.println("Error: " + e.getMessage());
                }
            }
            catch (SQLException e)
            {
                System.out.println("Error: " + e.getMessage());
            }
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("onViewReportInfo.fxml"));
        Parent root = loader.load();

        onViewReportInfoController cont = loader.getController();
        cont.user_srcode = this.user_srcode;
        cont.user_fullname = this.user_fullname;
        cont.report_item_id = this.report_item_id;

        cont.user_fullname_txtField.setText(user_fullname);
        cont.user_srcode_txtField.setText(info.target_user_srcode);
        cont.user_email_txtField.setText(user_email);
        cont.user_contact_txtField.setText(user_contact);

        cont.reportID_txtField.setText(this.report_item_id);
        cont.item_description_txtArea.setText(info.description);
        cont.item_location_txtArea.setText(info.location_of);
        cont.item_dateReported_Dpicker.setValue(info.date_of);

        AppProperties filestorage_props = new AppProperties("filestorage");
        String imageUrl = filestorage_props.prop.getProperty("url") + filestorage_props.prop.getProperty("bucket_name") + "/" + info.image_attach;

        Image image = new Image(imageUrl);
        cont.item_image_Imgview.setImage(image);

        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void onReturnButton(ActionEvent actionEvent) throws IOException, SQLException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("viewReportedListItemScene.fxml"));
        Parent root = loader.load();

        viewListOfReportedItemsSceneController cont = loader.getController();
        cont.user_fullname = this.user_fullname;
        cont.user_srcode = this.user_srcode;
        cont.viewReportedItemListButton();

        // Get the stage information from the button's source node
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
