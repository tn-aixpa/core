package it.smartcommunitylabdhub.core.models.converters.types;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import it.smartcommunitylabdhub.commons.annotations.common.ConverterType;
import it.smartcommunitylabdhub.commons.exceptions.CustomException;
import it.smartcommunitylabdhub.core.models.converters.interfaces.Converter;

@ConverterType(type = "datetime")
public class DateTimeConverter implements Converter<String, Date> {

    // Define the pattern
    private String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX";

    @Override
    public Date convert(String input) throws CustomException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime dateTime = LocalDateTime.parse(input, formatter);

        // Convert LocalDateTime to Date
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());

    }

    @Override
    public String reverseConvert(Date input) throws CustomException {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);

        // Format the Date object into a string
        return formatter.format(input);

    }

}
