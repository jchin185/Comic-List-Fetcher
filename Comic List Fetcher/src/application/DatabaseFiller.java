package application;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class DatabaseFiller {

	public DatabaseFiller() {

	}

	public static void main(String[] args) {
		DatabaseManager dbManager = DatabaseManager.getSingleton();
		dbManager.createEntityManager();
		try (BufferedReader in = new BufferedReader(new FileReader(
				"C:\\Users\\Jesse\\workspace\\Comic List Fetcher\\src\\application\\List.txt"));) {

			String line, pubName = null;
			while ((line = in.readLine()) != null) {
				// System.out.println(line);
				if (line.startsWith("Publisher")) {
					dbManager.addNewPub(line.split(":")[1].trim());
					pubName = line.split(":")[1].trim();
				} else if (line.isEmpty()) {
					continue;
				} else {
					dbManager.addNewSeries(pubName, line.trim());
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dbManager.closeEntityManager();
		}

	}

}
