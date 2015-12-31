package examples.org.optimizationBenchmarking.utils.graphics;

import org.optimizationBenchmarking.utils.error.ErrorUtils;
import org.optimizationBenchmarking.utils.graphics.style.impl.font.FontPaletteBuilder;
import org.optimizationBenchmarking.utils.graphics.style.impl.font.FontPaletteXMLInput;
import org.optimizationBenchmarking.utils.graphics.style.spec.IFontPalette;

/** a set of example font palettes */
public final class ExampleFontPalette {

  /** the fonts */
  public static final IFontPalette EXAMPLE_FONT_PALETTE = ExampleFontPalette
      .__load();

  /**
   * load the font palette
   *
   * @return the font palette
   */
  private static final IFontPalette __load() {
    try (final FontPaletteBuilder tb = new FontPaletteBuilder()) {
      FontPaletteXMLInput.getInstance().use().setDestination(tb)
          .addResource(ExampleFontPalette.class,
              "exampleFontPalette.fontPalette") //$NON-NLS-1$
          .create().call();
      return tb.getResult();
    } catch (final Throwable tt) {
      throw new RuntimeException(tt);
    }
  }

  /** the forbidden constructor */
  private ExampleFontPalette() {
    ErrorUtils.doNotCall();
  }

}
