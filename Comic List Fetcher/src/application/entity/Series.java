package application.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * Entity implementation class for Entity: Series
 *
 */
@Entity
public class Series implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SERIES_ID")
	private int id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "PUB_ID", nullable = false)
	private Publisher publisher;

	@Column(name = "NAME")
	private String name;

	@OneToMany(mappedBy = "series", orphanRemoval = true)
	private List<Issue> issueList;

	@Column(name = "PRIORITY")
	private PriorityStatus priorityStatus;

	private static final long serialVersionUID = 1L;

	public Series() {
		super();
	}

	/**
	 * @param pub
	 * @param name
	 */
	public Series(Publisher pub, String name, PriorityStatus priorityStatus) {
		super();
		this.publisher = pub;
		this.name = name;
		this.issueList = new ArrayList<Issue>();
		this.priorityStatus = priorityStatus;
	}

	public Publisher getPublisher() {
		return publisher;
	}

	public void setPublisher(Publisher pub) {
		this.publisher = pub;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PriorityStatus getPriorityStatus() {
		return priorityStatus;
	}
	public void setPriorityStatus(PriorityStatus priorityStatus) {
		this.priorityStatus = priorityStatus;
	}

	public enum PriorityStatus {
		LOW, HIGH, NEW
	}

}
