package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import application.entity.Publisher;
import application.entity.Series;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 * This class imports/exports a list of publishers/series. A custom extension,
 * "*.cblf", is used.
 * 
 * @author Jesse
 *
 */
public class FileManager {
	public FileManager() {

	}

	public static boolean importFile(Stage stage, DatabaseManager dbManager) {
		FileChooser fc = new FileChooser();
		fc.setTitle("Import a file.");
		fc.getExtensionFilters().add(
				new ExtensionFilter("Comicbook List Fetcher Files", "*.cblf"));
		File file = fc.showOpenDialog(stage);
		boolean success = false;
		if (file != null) {
			try (BufferedReader in = new BufferedReader(
					new FileReader(file));) {

				String line, pubName = null;
				while ((line = in.readLine()) != null) {
					System.out.println(line);
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

	public static boolean exportFile(Stage stage, DatabaseManager dbManager) {
		List<Publisher> pubList = dbManager.getPublisherList();
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(
				new ExtensionFilter("Comicbook List Fetcher Files", "*.cblf"));
		File file = fc.showSaveDialog(stage);
		boolean success = false;
		if (file != null) {
			try (FileWriter fw = new FileWriter(file);) {
				for (Publisher p : pubList) {
					fw.write("Publisher:" + p.getName() + "\n");
					System.out.print("Publisher:" + p.getName() + "\n");
					for (Series s : p.getSeriesList()) {
						fw.write(s.getName() + "\n");
						System.out.print(s.getName() + "\n");
					}
				}
				success = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return success;
	}
}
