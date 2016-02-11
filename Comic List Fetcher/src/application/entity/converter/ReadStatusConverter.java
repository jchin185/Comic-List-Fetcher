package application.entity.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import application.entity.Issue.ReadStatus;

@Converter(autoApply = true)
public class ReadStatusConverter
		implements
			AttributeConverter<ReadStatus, String> {

	public ReadStatusConverter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String convertToDatabaseColumn(ReadStatus attribute) {
		switch (attribute) {
			case READ :
				return "READ";
			case NOT_READ :
				return "NOT READ";
			default :
				throw new IllegalArgumentException(
						"Unknown value: " + attribute);
		}
	}

	@Override
	public ReadStatus convertToEntityAttribute(String dbData) {
		switch (dbData) {
			case "READ" :
				return ReadStatus.READ;
			case "NOT READ" :
				return ReadStatus.NOT_READ;
			default :
				throw new IllegalArgumentException("Unknown value: " + dbData);
		}
	}

}
