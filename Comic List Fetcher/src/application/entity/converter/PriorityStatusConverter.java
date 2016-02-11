package application.entity.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import application.entity.Series.PriorityStatus;

@Converter(autoApply = true)
public class PriorityStatusConverter
		implements
			AttributeConverter<PriorityStatus, String> {

	public PriorityStatusConverter() {

	}

	@Override
	public String convertToDatabaseColumn(PriorityStatus attribute) {
		switch (attribute) {
			case LOW :
				return "LOW";
			case HIGH :
				return "HIGH";
			case NEW :
				return "NEW";
			default :
				throw new IllegalArgumentException(
						"Unknown value: " + attribute);
		}
	}

	@Override
	public PriorityStatus convertToEntityAttribute(String dbData) {
		switch (dbData) {
			case "LOW" :
				return PriorityStatus.LOW;
			case "HIGH" :
				return PriorityStatus.HIGH;
			case "NEW" :
				return PriorityStatus.NEW;
			default :
				throw new IllegalArgumentException("Unknown value: " + dbData);
		}

	}

}
