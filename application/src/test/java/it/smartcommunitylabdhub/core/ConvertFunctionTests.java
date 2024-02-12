package it.smartcommunitylabdhub.core;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.converters.interfaces.Converter;

@SpringBootTest
class ConvertFunctionTests {

    @Mock
    private Converter<String, Integer> functionConverter;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConvert() {
        // Set up test data
        String input = "1";

        // Perform the conversion
        Integer output = ConversionUtils.convert(
                input, // Source object // Mocked CommandFactory instance
                "integer");

        // Assert the output is of type Integer
        Assertions.assertEquals(Integer.class, output.getClass());
        Assertions.assertEquals(1, output);
    }

    @Test
    void testReverse() {
        Integer input = 1;

        String output = ConversionUtils.reverse(
                input,
                "integer");

        // Assert that result is equal to integer.
        Assertions.assertEquals(String.class, output.getClass());
        Assertions.assertEquals("1", output);
    }

    @Test
    void testListConvert() {
        List<String> strings = new ArrayList<>();
        strings.add("1");
        strings.add("2");

        List<Integer> integers = (List<Integer>) ConversionUtils.convertIterable(strings, "integer",
                Integer.class);

        Assertions.assertEquals(2, integers.size()); // Assert the size of the converted list
        Assertions.assertEquals(1, integers.get(0)); // Assert the first element in the list
        Assertions.assertEquals(2, integers.get(1)); // Assert the second element in the list

    }

    @Test
    void testListRevert() {
        List<Integer> integers = new ArrayList<>();
        integers.add(1);
        integers.add(2);

        List<String> strings = (List<String>) ConversionUtils.reverseIterable(
                integers,
                "integer",
                String.class);

        Assertions.assertEquals(2, strings.size()); // Assert the size of the converted list
        Assertions.assertEquals(String.class, strings.get(0).getClass()); // Assert the first element in the list
        Assertions.assertEquals(String.class, strings.get(1).getClass()); // Assert the second element in the list

    }
}
