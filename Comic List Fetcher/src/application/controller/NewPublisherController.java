package application.controller;

import java.util.List;

import application.DatabaseManager;
import application.entity.Publisher;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class NewPublisherController {
	Stage stage;
	DatabaseManager dbManager;
	boolean newPublisherAdded;

	@FXML
	private TextField inputField;

	@FXML
	private Button addButton;
	@FXML
	private Button viewPublishersButton;
	@FXML
	private Button cancelButton;

	public NewPublisherController() {

	}

	@FXML
	private void initialize() {
		dbManager = DatabaseManager.getSingleton();
		newPublisherAdded = false;
	}

	@FXML
	private void handleAddButtonPushed() {
		System.err.println("Add publisher button pushed.");
		String publisherName = inputField.getText().trim();
		if (publisherName.isEmpty() || publisherName == null
				|| dbManager.findPubByName(publisherName) != null) {
			Alert errorAlert = new Alert(AlertType.ERROR);
			errorAlert.setTitle("Bad publisher name.");
			errorAlert.setHeaderText(
					"You have input nothing or an existing publisher.");
			errorAlert.setContentText(
					"Double check your input or click on the 'View Publishers' button to see existing publishers.");

			errorAlert.showAndWait();
		} else {
			System.err.println("Added new publisher " + publisherName);
			this.newPublisherAdded = dbManager.addNewPub(publisherName);
			this.stage.close();
		}
	}

	@FXML
	private void handleViewPublishersButtonPushed() {
		System.err.println("View other publishers button pushed.");
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.initModality(Modality.NONE);
		alert.setTitle("Existing Publishers");
		alert.setHeaderText("These are the currently existing publishers.");

		List<Publisher> publisherList = dbManager.getPublisherList();
		if (publisherList.isEmpty()) {
			alert.setContentText("There are currently no publishers.");
		} else {
			StringBuilder publisherNames = new StringBuilder();
			for (Publisher p : publisherList) {
				publisherNames.append(p.getName() + "\n");
			}
			alert.setContentText(publisherNames.toString());
		}

		alert.show();
	}

	@FXML
	private void handleCancelButtonPushed() {
		System.err.println("Cancel button pushed.");
		this.stage.close();
	}

	public boolean getIsNewPublisherAdded() {
		return this.newPublisherAdded;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

}
