package test.junit.org.optimizationBenchmarking.utils.graphics.styles.color;

import org.optimizationBenchmarking.utils.graphics.style.impl.color.DefaultGrayPalette;
import org.optimizationBenchmarking.utils.graphics.style.spec.IColorPalette;

import shared.junit.org.optimizationBenchmarking.utils.graphics.style.ColorPaletteTest;

/**
 * Test the default gray palette.
 */
public class DefaultGrayPaletteTest extends ColorPaletteTest {

  /** create */
  public DefaultGrayPaletteTest() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  protected IColorPalette getInstance() {
    return DefaultGrayPalette.getInstance();
  }
}
