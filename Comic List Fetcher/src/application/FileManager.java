package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * This class imports/exports a list of publishers/series.
 * 
 * @author Jesse
 *
 */
public class FileManager {
	FileChooser fc;
	Stage stage;
	DatabaseManager dbManager;

	public FileManager(Stage stage) {
		fc = new FileChooser();
		this.stage = stage;
		dbManager = DatabaseManager.getSingleton();
	}

	public boolean importFile() {
		fc.setTitle("Import a file.");
		File f = fc.showOpenDialog(stage);
		boolean success = false;
		if (f != null) {
			try (BufferedReader in = new BufferedReader(new FileReader(f));) {

				String line, pubName = null;
				while ((line = in.readLine()) != null) {
					if (line.startsWith("Publisher")) {
						dbManager.addNewPub(line.split(":")[1].trim());
						pubName = line.split(":")[1].trim();
					} else if (line.isEmpty()) {
						continue;
					} else {
						dbManager.addNewSeries(pubName, line.trim());
					}
				}
				success = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return success;
	}

	public void exportFile() {

	}
}
