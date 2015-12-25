package test.junit.org.optimizationBenchmarking.utils.graphics.styles.font;

import org.optimizationBenchmarking.utils.graphics.style.impl.font.FontPaletteBuilder;
import org.optimizationBenchmarking.utils.graphics.style.impl.font.FontPaletteXMLInput;
import org.optimizationBenchmarking.utils.graphics.style.spec.IFontPalette;

import shared.junit.org.optimizationBenchmarking.utils.graphics.style.FontPaletteTest;

/**
 * Test the example font palette.
 */
public class ExampleFontPaletteTest extends FontPaletteTest {

  /** create */
  public ExampleFontPaletteTest() {
    super(ExampleFontPaletteTest.__load(), true);
  }

  /**
   * load the font palette
   *
   * @return the font palette
   */
  private static final IFontPalette __load() {
    try (final FontPaletteBuilder tb = new FontPaletteBuilder()) {
      FontPaletteXMLInput.getInstance().use().setDestination(tb)
          .addResource(ExampleFontPaletteTest.class,
              "exampleFontPalette.fontPalette") //$NON-NLS-1$
          .create().call();
      return tb.getResult();
    } catch (final Throwable tt) {
      throw new RuntimeException(tt);
    }
  }
}
