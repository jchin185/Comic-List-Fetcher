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
import javafx.stage.Stage;

/**
 * This class imports/exports a list of publishers/series.
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

	public static boolean exportFile(Stage stage, DatabaseManager dbManager) {
		List<Publisher> pubList = dbManager.getPublisherList();
		FileChooser fc = new FileChooser();
		File f = fc.showSaveDialog(stage);
		if (f != null) {
			try {
				FileWriter fw = new FileWriter(f);
				for (Publisher p : pubList) {
					fw.write("Publisher:" + p.getName() + "\n");
					System.out.print("Publisher:" + p.getName() + "\n");
					for (Series s : p.getSeriesList()) {
						fw.write(s.getName() + "\n");
						System.out.print(s.getName() + "\n");
					}
				}
				fw.close();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
