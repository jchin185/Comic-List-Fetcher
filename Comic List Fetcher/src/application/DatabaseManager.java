package application;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;

import application.entity.Issue;
import application.entity.Issue.ReadStatus;
import application.entity.Publisher;
import application.entity.Series;
import application.entity.Series.PriorityStatus;

/**
 * This class has various methods to interact with the database(e.g. addition,
 * deletion, query). In order to get an instance of this, use the static method
 * getSingleton().
 * 
 * @author Jesse
 *
 */
public class DatabaseManager {
	// entity manager factory object
	private EntityManagerFactory emf;

	// single entity manager object
	private EntityManager em;

	// singleton object of DatabaseManager
	private static DatabaseManager databasemanager = null;

	private final String persistenceName = "Comic List Fetcher";

	/**
	 * Constructor for class DatabaseManager
	 */
	public DatabaseManager() {
		emf = Persistence.createEntityManagerFactory(persistenceName);
	}

	/**
	 * Finds a publisher based on its name.
	 * 
	 * @param name
	 *            the name of the publisher
	 * @return the publisher if found otherwise null
	 */
	public Publisher findPubByName(String name) {
		Publisher p = null;
		String qlString = "SELECT p FROM Publisher p WHERE p.name = :pname";
		TypedQuery<Publisher> query = em.createQuery(qlString, Publisher.class);
		query.setParameter("pname", name);
		List<Publisher> pub = query.getResultList();
		if (pub.size() != 0) {
			p = pub.get(0);
		}
		return p;
	}

	/**
	 * Finds a series based on its name.
	 * 
	 * @param name
	 *            the name of the series
	 * @return the series if found, otherwise null
	 */
	public Series findSeriesByName(String name) {
		Series s = null;
		String qlString = "SELECT s FROM Series s WHERE s.name = :sname";
		TypedQuery<Series> query = em.createQuery(qlString, Series.class);
		query.setParameter("sname", name);
		List<Series> ser = query.getResultList();
		if (ser.size() != 0) {
			s = ser.get(0);
		}
		return s;
	}

	/**
	 * Finds an issue based on its series and issue number.
	 * 
	 * @param seriesName
	 *            the name of the series the issue belongs to
	 * @param issueNum
	 *            the issue number
	 * @return the issue if found, otherwise null
	 */
	public Issue findIssueBySeriesAndNum(String seriesName, double issueNum) {
		Issue i = null;
		Series s = this.findSeriesByName(seriesName);
		if (s != null) {
			String qlString = "SELECT i FROM Issue i WHERE i.series = :iser AND i.issueNumber = :inum";
			TypedQuery<Issue> query = em.createQuery(qlString, Issue.class);
			query.setParameter("iser", s);
			query.setParameter("inum", issueNum);
			List<Issue> iss = query.getResultList();
			if (iss.size() != 0) {
				i = iss.get(0);
			}
		}
		return i;
	}

	/**
	 * Gets a list of all publishers.
	 * 
	 * @return a list of all publishers
	 */
	public List<Publisher> getPublisherList() {
		String qlString = "SELECT p FROM Publisher p";
		TypedQuery<Publisher> query = em.createQuery(qlString, Publisher.class);
		return query.getResultList();
	}

	/**
	 * Gets a list of all series.
	 * 
	 * @return a list of all series
	 */
	public List<Series> getSeriesList() {
		String qlString = "SELECT s FROM Series s";
		TypedQuery<Series> query = em.createQuery(qlString, Series.class);
		return query.getResultList();
	}

	/**
	 * Gets a list of all issues.
	 * 
	 * @return a list of all issues
	 */
	public List<Issue> getIssueList() {
		String qlString = "SELECT i FROM Issue i";
		TypedQuery<Issue> query = em.createQuery(qlString, Issue.class);
		return query.getResultList();
	}
	/**
	 * Gets a list of issues with specified name.
	 * 
	 * @param searchCriteria
	 *            the string to look for
	 * @param exactMatch
	 *            if the search should look for exact matches only
	 * @return
	 */

	public List<Issue> findIssuesWith(String searchCriteria,
			boolean exactMatch) {
		String qlString;
		if (exactMatch) {
			qlString = "SELECT i FROM Issue i WHERE i.series.name = :iname";
			TypedQuery<Issue> query = em.createQuery(qlString, Issue.class);
			query.setParameter("iname", searchCriteria);
			return query.getResultList();
		} else {
			qlString = "SELECT i FROM Issue i WHERE i.series.name LIKE :iname";
			TypedQuery<Issue> query = em.createQuery(qlString, Issue.class);
			query.setParameter("iname", "%" + searchCriteria + "%");
			return query.getResultList();
		}
	}

	/**
	 * Adds a new publisher to the database.
	 * 
	 * @param pubName
	 *            the name of the publisher
	 * @return boolean if the publisher was added to the database
	 */
	public boolean addNewPub(String pubName) {
		Publisher newPub = new Publisher(pubName);
		boolean created = false;
		this.startTransaction();
		try {
			em.persist(newPub);
			this.commitTransaction();
			created = true;
		} catch (EntityExistsException e) {
			this.rollbackTransaction();
		}
		return created;
	}

	/**
	 * Adds a new series to the database with default priority low.
	 * 
	 * @param pubName
	 *            the name of the publisher of the series
	 * @param seriesName
	 *            the name of the series
	 * @return boolean if the series was added to the database
	 */
	public boolean addNewSeries(String pubName, String seriesName) {
		Publisher p = this.findPubByName(pubName);
		boolean created = false;
		if (p != null) {
			Series newSer = new Series(p, seriesName, PriorityStatus.LOW);
			this.startTransaction();
			try {
				em.persist(newSer);
				this.commitTransaction();
				created = true;
			} catch (EntityExistsException e) {
				this.rollbackTransaction();
			}
		}
		return created;
	}

	/**
	 * Adds a new series to the database with specified priority status.
	 * 
	 * @param pubName
	 *            the name of the publisher of the series
	 * @param seriesName
	 *            the name of the series
	 * @param status
	 *            the priority status of the series
	 * @return boolean if the series was added to the database
	 */
	public boolean addNewSeriesWithPriority(String pubName, String seriesName,
			PriorityStatus status) {
		Publisher p = this.findPubByName(pubName);
		boolean created = false;
		if (p != null) {
			Series newSer = new Series(p, seriesName, status);
			this.startTransaction();
			try {
				em.persist(newSer);
				this.commitTransaction();
				created = true;
			} catch (EntityExistsException e) {
				this.rollbackTransaction();
			}
		}
		return created;
	}

	/**
	 * Adds a new issue to the database.
	 * 
	 * @param seriesName
	 *            the name of the series
	 * @param num
	 *            the issue number
	 * @param relaseDate
	 *            the date the issue was released
	 * @return boolean if the issue was added to the database
	 */
	public boolean addNewIssue(String seriesName, double num,
			LocalDate releaseDate) {
		Series s = this.findSeriesByName(seriesName);
		boolean created = false;
		if (s != null) {
			this.startTransaction();
			Issue newIss = new Issue(s, num, releaseDate, ReadStatus.NOT_READ);
			try {
				em.persist(newIss);
				this.commitTransaction();
				created = true;
			} catch (EntityExistsException e) {
				this.rollbackTransaction();
			}
		}
		return created;
	}

	/**
	 * Changes the priority status of a series.
	 * 
	 * @param seriesName
	 *            the name of the series to change the priority of
	 * @param newStatus
	 *            the new status to change the priority to
	 * @return if the status was successfully changed
	 */
	public boolean changeSeriesPriority(String seriesName,
			PriorityStatus newStatus) {
		Series s = this.findSeriesByName(seriesName);
		boolean changed = false;
		if (s != null) {
			this.startTransaction();
			s.setPriorityStatus(newStatus);
			try {
				this.commitTransaction();
				changed = true;
			} catch (RollbackException e) {
				this.rollbackTransaction();
			}
		}
		return changed;
	}

	/**
	 * Changes the read status of an issue.
	 * 
	 * @param seriesName
	 *            the name of the series of the issue
	 * @param issueNum
	 *            the issue number
	 * @param newStatus
	 *            the new read status to change to
	 * @return if the read status was successfully changed
	 */
	public boolean changeIssueStatus(String seriesName, int issueNum,
			ReadStatus newStatus) {
		Series s = this.findSeriesByName(seriesName);
		Issue i = this.findIssueBySeriesAndNum(seriesName, issueNum);
		boolean changed = false;
		if (s != null && i != null) {
			this.startTransaction();
			i.setReadStatus(newStatus);
			try {
				this.commitTransaction();
				changed = true;
			} catch (RollbackException e) {
				this.rollbackTransaction();
			}
		}
		return changed;
	}

	/**
	 * Changes the read status of a list of issues.
	 * 
	 * @param issueList
	 *            the issue list to status
	 * @param newStatus
	 *            the new read status to change to
	 * @return if the read status of every issue was successfully changed
	 */
	public boolean changeIssueListStatus(List<Issue> issueList,
			ReadStatus newStatus) {
		boolean changed = false;

		this.startTransaction();
		for (Issue i : issueList) {
			i.setReadStatus(newStatus);
		}
		try {
			this.commitTransaction();
			changed = true;
		} catch (RollbackException e) {
			this.rollbackTransaction();
		}
		return changed;
	}

	/**
	 * Deletes a publisher from the database.
	 * 
	 * This will delete all series and issues associated with this publisher.
	 * 
	 * @param publisherName
	 *            the name of the publisher
	 * @return if the publisher and associated series/issues were successfully
	 *         deleted
	 */
	public boolean deletePublisher(String publisherName) {
		boolean deleted = false;
		this.startTransaction();
		Publisher publisher = this.findPubByName(publisherName);
		if (publisher != null) {
			em.remove(publisher);
		}
		try {
			this.commitTransaction();
			deleted = true;
		} catch (RollbackException e) {
			this.rollbackTransaction();
		}
		return deleted;
	}

	/**
	 * Deletes a list of issues.
	 * 
	 * @param issueList
	 *            the list of issues
	 * @return if the issue list was successfully deleted
	 */
	public boolean deleteIssueList(List<Issue> issueList) {
		boolean allDeleted = false;
		this.startTransaction();
		for (Issue i : issueList) {
			System.err.println("about to remove " + i.getSeries().getName());
			em.remove(i);
		}
		try {
			this.commitTransaction();
			allDeleted = true;
		} catch (RollbackException e) {
			this.rollbackTransaction();
		}
		return allDeleted;
	}

	/**
	 * Return a singleton of DatabaseManager
	 * 
	 * @return a singleton of class DatabaseManager
	 */
	public static DatabaseManager getSingleton() {
		if (databasemanager == null) {
			databasemanager = new DatabaseManager();
		}
		return databasemanager;
	}

	/**
	 * Start entity transaction
	 */
	private void startTransaction() {
		if (!em.getTransaction().isActive()) {
			// Begin transaction
			em.getTransaction().begin();
		}
	}

	/**
	 * Commit transaction
	 */
	private void commitTransaction() throws RollbackException {
		if (em.getTransaction().isActive()) {
			// Commit the transaction
			em.getTransaction().commit();
		}
	}

	/**
	 * Rollback transaction
	 */
	private void rollbackTransaction() {
		if (em.getTransaction().isActive()) {
			// Commit the transaction
			em.getTransaction().rollback();
		}
	}

	/**
	 * Create new EntityManager
	 */
	public void createEntityManager() {
		// Create a new EntityManager
		em = emf.createEntityManager();
	}

	/**
	 * Close the constructed EntityManager
	 */
	public void closeEntityManager() {
		// Close this EntityManager
		if (em == null)
			return;
		if (em.isOpen()) {
			em.close();
		}
	}
}
