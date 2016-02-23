package application;

import java.io.IOException;

import application.controller.AppOverviewController;
import application.controller.DeletePublisherController;
import application.controller.NewPublisherController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * This class controls the main flow of logic and manages the stages/scenes.
 * 
 * @author Jesse
 *
 */
public class MainApp extends Application {

	AppOverviewController controller;
	private Stage primaryStage;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Comic List Fetcher");
		this.setUpMainScene();
		primaryStage.setOnCloseRequest(t -> {
			controller.exit();
			Platform.exit();
			System.exit(0);
		});
	}

	public static void main(String[] args) {
		launch(args);
	}

	private void setUpMainScene() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(
					MainApp.class.getResource("display/AppOverview.fxml"));
			Scene scene = new Scene((AnchorPane) loader.load());
			primaryStage.setScene(scene);
			primaryStage.show();
			controller = loader.getController();
			controller.setMainApp(this);
			controller.setColumnBindings();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean showNewPublisherWindow() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(
					MainApp.class.getResource("display/NewPublisher.fxml"));
			AnchorPane pane = (AnchorPane) loader.load();
			Stage newPublisherStage = new Stage();
			newPublisherStage.setTitle("Add a new publisher.");
			newPublisherStage.setScene(new Scene(pane));
			newPublisherStage.initOwner(primaryStage);
			newPublisherStage.initModality(Modality.APPLICATION_MODAL);
			NewPublisherController controller = loader.getController();
			controller.setStage(newPublisherStage);
			newPublisherStage.showAndWait();

			return controller.getIsNewPublisherAdded();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean showDeletePublisherWindow() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(
					MainApp.class.getResource("display/DeletePublisher.fxml"));
			AnchorPane pane = (AnchorPane) loader.load();
			Stage newPublisherStage = new Stage();
			newPublisherStage.setTitle("Delete an existing publisher.");
			newPublisherStage.setScene(new Scene(pane));
			newPublisherStage.initOwner(primaryStage);
			newPublisherStage.initModality(Modality.APPLICATION_MODAL);
			DeletePublisherController controller = loader.getController();
			controller.setStage(newPublisherStage);
			newPublisherStage.showAndWait();

			return controller.getIsPublisherDeleted();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public Stage getPrimaryStage() {
		return this.primaryStage;
	}

}
