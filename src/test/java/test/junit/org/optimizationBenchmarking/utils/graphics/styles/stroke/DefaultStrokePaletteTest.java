package test.junit.org.optimizationBenchmarking.utils.graphics.styles.stroke;

import org.optimizationBenchmarking.utils.graphics.style.impl.stroke.DefaultStrokePalette;
import org.optimizationBenchmarking.utils.graphics.style.spec.IStrokePalette;

import shared.junit.org.optimizationBenchmarking.utils.graphics.style.StrokePaletteTest;

/**
 * Test the default stroke palette.
 */
public class DefaultStrokePaletteTest extends StrokePaletteTest {

  /** create */
  public DefaultStrokePaletteTest() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  protected IStrokePalette getInstance() {
    return DefaultStrokePalette.getInstance();
  }
}
