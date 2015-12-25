package test.junit.org.optimizationBenchmarking.utils.graphics.styles.color;

import org.optimizationBenchmarking.utils.graphics.style.impl.color.JavaDefaultColorPalette;
import org.optimizationBenchmarking.utils.graphics.style.spec.IColorPalette;

import shared.junit.org.optimizationBenchmarking.utils.graphics.style.ColorPaletteTest;

/**
 * Test the default java color palette.
 */
public class JavaDefaultColorPaletteTest extends ColorPaletteTest {

  /** create */
  public JavaDefaultColorPaletteTest() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  protected IColorPalette getInstance() {
    return JavaDefaultColorPalette.getInstance();
  }
}
