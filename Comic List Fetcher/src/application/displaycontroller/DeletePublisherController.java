package application.displaycontroller;

import java.util.List;

import application.DatabaseManager;
import application.entity.Publisher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

public class DeletePublisherController {
	Stage stage;
	DatabaseManager dbManager;
	boolean isPublisherDeleted;

	@FXML
	ComboBox<String> publisherComboBox;

	@FXML
	Button deleteButton;
	@FXML
	Button deleteAllButton;
	@FXML
	Button cancelButton;

	public DeletePublisherController() {

	}

	@FXML
	public void initialize() {
		dbManager = DatabaseManager.getSingleton();
		isPublisherDeleted = false;
		List<Publisher> publisherList = dbManager.getPublisherList();
		ObservableList<String> nameList = FXCollections.observableArrayList();
		for (Publisher pub : publisherList) {
			nameList.add(pub.getName());
		}
		publisherComboBox.setItems(nameList);
	}

	@FXML
	private void handleDeleteButtonPushed() {
		System.err.println("Delete button pushed.");
		this.isPublisherDeleted = dbManager
				.deletePublisher(publisherComboBox.getValue());
		this.stage.close();
	}

	@FXML
	private void handleDeleteAllButtonPushed() {
		System.err.println("Delete all button pushed.");
		this.isPublisherDeleted = true;
	}

	@FXML
	private void handleCancelButtonPushed() {
		System.err.println("Cancel button pushed.");
		this.stage.close();
	}

	public boolean getIsPublisherDeleted() {
		return isPublisherDeleted;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

}
