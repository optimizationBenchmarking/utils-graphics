package test.junit.org.optimizationBenchmarking.utils.graphics.graphic;

import org.junit.Ignore;
import org.optimizationBenchmarking.utils.graphics.style.impl.color.DefaultColorPalette;
import org.optimizationBenchmarking.utils.graphics.style.impl.color.DefaultGrayPalette;
import org.optimizationBenchmarking.utils.graphics.style.impl.color.JavaDefaultColorPalette;
import org.optimizationBenchmarking.utils.graphics.style.spec.IColorPalette;
import org.optimizationBenchmarking.utils.graphics.style.spec.IFontPalette;

import examples.org.optimizationBenchmarking.utils.graphics.ExampleFontPalette;
import shared.junit.org.optimizationBenchmarking.utils.graphics.graphic.AbstractGraphicDriverTest;

/** The base class for graphic driver tests. */
@Ignore
public abstract class GraphicDriverTest extends AbstractGraphicDriverTest {

  /** create the graphic driver test */
  protected GraphicDriverTest() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  protected IColorPalette getGrayPalette() {
    return DefaultGrayPalette.getInstance();
  }

  /** {@inheritDoc} */
  @Override
  protected IColorPalette getDefaultColorPalette() {
    return DefaultColorPalette.getInstance();
  }

  /** {@inheritDoc} */
  @Override
  protected IColorPalette getSmallColorPalette() {
    return JavaDefaultColorPalette.getInstance();
  }

  /** {@inheritDoc} */
  @Override
  protected IFontPalette getFontPalette() {
    return ExampleFontPalette.EXAMPLE_FONT_PALETTE;
  }
}
