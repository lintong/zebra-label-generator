package com.github.mortonl.zebra.elements.fonts;

import com.github.mortonl.zebra.formatting.Orientation;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FontTest
{
    private final PrintDensity testDpi = PrintDensity.DPI_203;
    private final LabelSize testSize = LabelSize.LABEL_4X6;

    @Test
    void testToZplString()
    {
        Font font = Font
            .builder()
            .fontName("A")
            .orientation(Orientation.NORMAL)
            .heightMm(3.0)
            .widthMm(4.0)
            .build();

        String zplString = font.toZplString(testDpi);

        assertEquals("^AA,N,24,32", zplString);
    }

    @Test
    void testValidateInContext_ValidFont()
    {
        Font font = Font
            .builder()
            .fontName("A")
            .heightMm(3.0)
            .widthMm(4.0)
            .build();

        assertDoesNotThrow(() -> font.validateInContext(testSize, testDpi));
    }

    @ParameterizedTest(name = "Font name {0} should be rejected")
    @NullSource
    @ValueSource(strings = {""})
    void testValidateInContext_InvalidFontName(String invalidName)
    {
        Font font = Font
            .builder()
            .fontName(invalidName)
            .build();

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> font.validateInContext(testSize, testDpi)
        );
        assertEquals("Font name cannot be null or empty", exception.getMessage());
    }

    @ParameterizedTest(name = "{2}")
    @CsvSource({
        "height,0.1,Font height below minimum (0.10mm) should be rejected,Font height 0.10mm is too small. Minimum height is 1.25mm for 203 DPI / 8 dots per mm",
        "height,4001.0,Font height above maximum (4001mm) should be rejected,Font height 4001.00mm is too large. Maximum height is 4000.00mm for 203 DPI / 8 dots per mm",
        "width,0.1,Font width below minimum (0.10mm) should be rejected,Font width 0.10mm is too small. Minimum width is 1.25mm for 203 DPI / 8 dots per mm",
        "width,4001.0,Font width above maximum (4001mm) should be rejected,Font width 4001.00mm is too large. Maximum width is 4000.00mm for 203 DPI / 8 dots per mm"
    })
    void testValidateInContext_InvalidDimensions(
        String dimension,
        double invalidValue,
        String testDescription,
        String expectedMessage
    )
    {
        Font font = Font
            .builder()
            .fontName("A")
            .heightMm(dimension.equals("height") ? invalidValue : 2.0)
            .widthMm(dimension.equals("width") ? invalidValue : 2.0)
            .build();

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> font.validateInContext(testSize, testDpi),
            testDescription
        );

        assertEquals(expectedMessage, exception.getMessage());
    }

    @ParameterizedTest(name = "{2}")
    @CsvSource({
        "height,1.25,Minimum allowed font height (1.25mm) should be valid",
        "height,4000.0,Maximum allowed font height (4000mm) should be valid",
        "width,1.25,Minimum allowed font width (1.25mm) should be valid",
        "width,4000.0,Maximum allowed font width (4000mm) should be valid"
    })
    void testValidateInContext_ValidBoundaryDimensions(
        String dimension,
        double boundaryValue,
        String testDescription
    )
    {
        Font font = Font
            .builder()
            .fontName("A")
            .heightMm(dimension.equals("height") ? boundaryValue : 2.0)
            .widthMm(dimension.equals("width") ? boundaryValue : 2.0)
            .build();

        assertDoesNotThrow(
            () -> font.validateInContext(testSize, testDpi),
            testDescription
        );
    }
}
