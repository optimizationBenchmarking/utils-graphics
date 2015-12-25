package test.junit.org.optimizationBenchmarking.utils.graphics.styles.color;

import org.optimizationBenchmarking.utils.graphics.style.impl.color.DefaultColorPalette;
import org.optimizationBenchmarking.utils.graphics.style.spec.IColorPalette;

import shared.junit.org.optimizationBenchmarking.utils.graphics.style.ColorPaletteTest;

/**
 * Test the default color palette.
 */
public class DefaultColorPaletteTest extends ColorPaletteTest {

  /** create */
  public DefaultColorPaletteTest() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  protected IColorPalette getInstance() {
    return DefaultColorPalette.getInstance();
  }
}
