package test.junit.org.optimizationBenchmarking.utils.graphics.styles.color;

import org.optimizationBenchmarking.utils.graphics.style.impl.color.HTML401ColorPalette;
import org.optimizationBenchmarking.utils.graphics.style.spec.IColorPalette;

import shared.junit.org.optimizationBenchmarking.utils.graphics.style.ColorPaletteTest;

/**
 * Test the default html 4.1 color palette.
 */
public class HTML401ColorPaletteTest extends ColorPaletteTest {

  /** create */
  public HTML401ColorPaletteTest() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  protected IColorPalette getInstance() {
    return HTML401ColorPalette.getInstance();
  }
}
