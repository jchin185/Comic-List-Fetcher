package application.entity;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Entity implementation class for Entity: Issue
 * 
 * This class represents a comic book issue.
 *
 */
@Entity
public class Issue implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ISSUE_ID")
	private int id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "SERIES_ID", nullable = false)
	private Series series;

	@Column(name = "ISSUE_NUM")
	private double issueNumber;

	@Column(name = "RELEASE_DATE")
	private LocalDate releaseDate;

	@Column(name = "READ_STATUS")
	private ReadStatus readStatus;

	private static final long serialVersionUID = 1L;

	public Issue() {
		super();
	}

	/**
	 * @param series
	 * @param number
	 * @param releaseDate
	 * @param readStatus
	 */
	public Issue(Series series, double number, LocalDate releaseDate,
			ReadStatus readStatus) {
		super();
		this.series = series;
		this.issueNumber = number;
		this.releaseDate = releaseDate;
		this.readStatus = readStatus;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Series getSeries() {
		return series;
	}

	public void setSeries(Series series) {
		this.series = series;
	}

	public double getIssueNumber() {
		return this.issueNumber;
	}

	public void setIssueNumber(double number) {
		this.issueNumber = number;
	}
	public LocalDate getReleaseDate() {
		return this.releaseDate;
	}

	public void setReleaseDate(LocalDate releaseDate) {
		this.releaseDate = releaseDate;
	}

	public ReadStatus getReadStatus() {
		return readStatus;
	}
	public void setReadStatus(ReadStatus readStatus) {
		this.readStatus = readStatus;
	}

	public enum ReadStatus {
		READ, NOT_READ
	}

}
