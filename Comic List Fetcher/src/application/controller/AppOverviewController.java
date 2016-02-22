package application.controller;

import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

import application.DatabaseManager;
import application.FileManager;
import application.MainApp;
import application.WebPageParser;
import application.entity.Issue;
import application.entity.Issue.ReadStatus;
import application.entity.Series.PriorityStatus;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * This class controls the main flow of logic for the GUI elements.
 * 
 * @author Jesse
 *
 */
public class AppOverviewController {
	private MainApp app;
	private DatabaseManager dbManager;

	private String lastSearchTerm;
	private boolean lastIsExactMatch;

	private ObservableList<Issue> issueList;

	@FXML
	private MenuItem importItem;
	@FXML
	private MenuItem exportItem;
	@FXML
	private MenuItem closeItem;

	@FXML
	private DatePicker datePicker;
	@FXML
	private Button updateButton;
	@FXML
	private Label updateLabel;

	@FXML
	private TextField searchBox;
	@FXML
	private Button searchButton;
	@FXML
	private CheckBox exactMatchCheckbox;
	@FXML
	private Label searchLabel;

	@FXML
	private Button newPubButton;
	@FXML
	private Button newSeriesButton;
	@FXML
	private Button newIssueButton;
	@FXML
	private Button delPubButton;
	@FXML
	private Button delSeriesButton;
	@FXML
	private Button delIssueButton;

	@FXML
	private TableView<Issue> issueTable;
	@FXML
	private TableColumn<Issue, String> publisherColumn;
	@FXML
	private TableColumn<Issue, String> seriesColumn;
	@FXML
	private TableColumn<Issue, String> issueColumn;
	@FXML
	private TableColumn<Issue, String> dateColumn;
	@FXML
	private TableColumn<Issue, PriorityStatus> priorityColumn;
	@FXML
	private TableColumn<Issue, ReadStatus> readColumn;

	@FXML
	private Button markReadButton;
	@FXML
	private Button editButton;
	@FXML
	private Button deleteButton;
	@FXML
	private Button deleteAllButton;

	public AppOverviewController() {

	}

	@FXML
	private void initialize() {
		dbManager = DatabaseManager.getSingleton();
		dbManager.createEntityManager();
		lastSearchTerm = "";
		lastIsExactMatch = false;
		this.setUp();
		issueList = FXCollections.observableArrayList(dbManager.getIssueList());
		issueTable.setItems(issueList);
	}

	private void setUp() {

		issueTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		publisherColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(
				data.getValue().getSeries().getPublisher().getName()));
		seriesColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(
				data.getValue().getSeries().getName()));
		issueColumn.setCellValueFactory(
				data -> new ReadOnlyStringWrapper(new DecimalFormat("###")
						.format((data.getValue().getIssueNumber()))));
		dateColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(
				data.getValue().getReleaseDate().toString()));
		priorityColumn.setCellValueFactory(
				data -> new ReadOnlyObjectWrapper<PriorityStatus>(
						data.getValue().getSeries().getPriorityStatus()));
		readColumn.setCellValueFactory(
				data -> new ReadOnlyObjectWrapper<ReadStatus>(
						data.getValue().getReadStatus()));

		// probably should use css instead
		publisherColumn.setStyle("-fx-alignment: CENTER;");
		seriesColumn.setStyle("-fx-alignment: CENTER;");
		issueColumn.setStyle("-fx-alignment: CENTER;");
		dateColumn.setStyle("-fx-alignment: CENTER;");
		priorityColumn.setStyle("-fx-alignment: CENTER;");
		readColumn.setStyle("-fx-alignment: CENTER;");
	}

	@FXML
	private void handleImportSelected() {
		System.err.println("Import item.");

		boolean b = FileManager.importFile(app.getPrimaryStage(), dbManager);
		System.out.println(b);
	}

	@FXML
	private void handleExportSelected() {
		System.err.println("Export item.");
		boolean b = FileManager.exportFile(app.getPrimaryStage(), dbManager);
		System.out.println(b);
	}

	@FXML
	private void handleCloseSelected() {
		System.err.println("Exit item.");
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setHeaderText("Quit?");
		alert.setTitle("Quit?");
		alert.setContentText("Are you sure you want to quit?");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			exit();
			Platform.exit();
			System.exit(0);
		}
	}

	@FXML
	private void handleDateSelected() {
		System.out.println("Date selected.");
		LocalDate date = datePicker.getValue();
		if (date.getDayOfWeek() != DayOfWeek.WEDNESDAY) {
			System.err.println("non wednesday selected");
			datePicker.setValue(
					date.with(TemporalAdjusters.previous(DayOfWeek.WEDNESDAY)));
		}
	}

	@FXML
	private void handleUpdateButtonPushed() {
		System.out.println("Update Button pushed.");
		if (datePicker.getValue() != null) {
			System.out.println(datePicker.getValue().toString());
			List<String> list = WebPageParser.parsePage(datePicker.getValue());
			if (list == null) {
				updateLabel
						.setText("Could not connect or invalid date selected.");
			} else {
				for (String s : list) {
					// 2nd printing of old issues
					if (s.contains("2nd Printing")) {
						continue;
					}
					String publisher = s.split(",")[1];
					if (dbManager.findPubByName(publisher) != null) {
						String issue = s.split(",")[2];
						int poundIndex = issue.indexOf("#");
						if (poundIndex != -1) {
							int parenIndex = issue.indexOf("(");
							String series = issue.substring(0, poundIndex)
									.trim();
							double num = 1;
							try {
								if (parenIndex == -1) {
									num = Double.parseDouble(
											issue.substring(poundIndex + 1));
								} else {
									num = Double.parseDouble(issue.substring(
											poundIndex + 1, parenIndex).trim());
								}
							} catch (NumberFormatException e) {
								System.err.println("Error adding " + s);
								// add option to add manually
							}
							if (dbManager.findIssueBySeriesAndNum(series,
									num) == null) {
								System.err.println("TO BE ADDED " + publisher
										+ " " + series + " " + num);
								dbManager.addNewIssue(series, num,
										datePicker.getValue());
							}
						}
					}
				}
				System.out.println("DONE");
				updateLabel.setText("Update success!");
				issueList = FXCollections
						.observableArrayList(dbManager.getIssueList());
				issueTable.setItems(issueList);
			}
		} else {
			System.err.println("no date selected.");
			updateLabel.setText("No date selected.");
		}
		updateLabel.setVisible(true);
	}

	@FXML
	private void handleSearchPushed() {
		System.err.println("Search textfield/button pushed");
		String searchCriteria = searchBox.getText();
		boolean checked = exactMatchCheckbox.isSelected();
		List<Issue> searchResults = dbManager.findIssuesWith(searchCriteria,
				checked);
		issueList = FXCollections.observableArrayList(searchResults);
		if (searchResults.isEmpty()) {
			searchLabel.setText("No issues found.");
		} else {
			searchLabel.setText(searchResults.size() + " result(s) found!");
		}
		lastSearchTerm = searchCriteria;
		lastIsExactMatch = checked;
		searchLabel.setVisible(true);
		issueTable.setItems(issueList);
	}

	@FXML
	private void handleAddNewPublisherButtonPushed() {
		System.err.println("Add new publisher button pushed.");
		app.showNewPublisherWindow();
	}

	@FXML
	private void handleAddNewSeriesButtonPushed() {
		System.err.println("Add new series button pushed.");
	}

	@FXML
	private void handleAddNewIssueButtonPushed() {
		System.err.println("Add new issue button pushed.");
	}

	@FXML
	private void handleDelPublisherButtonPushed() {
		System.err.println("Delete publisher button pushed.");
		if (this.app.showDeletePublisherWindow()) {
			issueList = FXCollections
					.observableArrayList(dbManager.getIssueList());
			issueTable.setItems(issueList);
		}
	}

	@FXML
	private void handleDelSeriesButtonPushed() {
		System.err.println("Delete series button pushed.");
	}

	@FXML
	private void handleDelIssueButtonPushed() {
		System.err.println("Delete issue button pushed.");
	}

	@FXML
	private void handleMarkReadButtonPushed() {
		System.err.println("Mark read button pushed");
		List<Issue> selectedList = issueTable.getSelectionModel()
				.getSelectedItems();
		dbManager.changeIssueListStatus(selectedList, ReadStatus.READ);
		issueList.clear();
		List<Issue> searchResults = dbManager.findIssuesWith(lastSearchTerm,
				lastIsExactMatch);
		issueList = FXCollections.observableArrayList(searchResults);
		issueTable.setItems(issueList);
	}

	@FXML
	private void handleDeleteButtonPushed() {
		System.err.println("Delete button pushed.");
		List<Issue> selectedList = issueTable.getSelectionModel()
				.getSelectedItems();
		dbManager.deleteIssueList(selectedList);
		issueList.removeAll(selectedList);
		issueTable.setItems(issueList);
	}

	@FXML
	private void handleDeleteAllButtonPushed() {
		System.err.println("Delete all button pushed.");
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Delete all issues.");
		alert.setHeaderText(
				"You are about to delete all currently shown issues");
		alert.setContentText(
				"Are you sure you want to delete all currently shown issues?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			if (dbManager.deleteIssueList(issueList)) {
				issueList.clear();
			} else {

			}
		} else {
			return;
		}

		issueTable.setItems(issueList);
	}

	public void setMainApp(MainApp app) {
		this.app = app;
	}

	public void exit() {
		dbManager.closeEntityManager();
	}

	public void setColumnBindings() {
		publisherColumn.prefWidthProperty()
				.bind(issueTable.widthProperty().multiply(0.20));
		seriesColumn.prefWidthProperty()
				.bind(issueTable.widthProperty().multiply(0.25));
		issueColumn.prefWidthProperty()
				.bind(issueTable.widthProperty().multiply(0.15));
		dateColumn.prefWidthProperty()
				.bind(issueTable.widthProperty().multiply(0.20));
		priorityColumn.prefWidthProperty()
				.bind(issueTable.widthProperty().multiply(0.10));
		readColumn.prefWidthProperty()
				.bind(issueTable.widthProperty().multiply(0.10));
	}
}
