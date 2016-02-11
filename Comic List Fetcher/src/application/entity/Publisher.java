package application.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Entity implementation class for Entity: Publisher
 *
 */
@Entity
public class Publisher implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PUB_ID")
	private int id;

	@Column(name = "NAME")
	private String name;

	@OneToMany(mappedBy = "publisher", orphanRemoval = true)
	private List<Series> seriesList;

	private static final long serialVersionUID = 1L;

	public Publisher() {
		super();
	}

	/**
	 * @param id
	 * @param name
	 */
	public Publisher(String name) {
		super();
		this.setName(name);
		this.seriesList = new ArrayList<Series>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String name() {
		return name;
	}

	public List<Series> getSeriesList() {
		return seriesList;
	}
	public void setSeriesList(List<Series> series) {
		this.seriesList = series;
	}

}
