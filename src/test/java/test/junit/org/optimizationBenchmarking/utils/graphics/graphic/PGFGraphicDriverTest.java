package test.junit.org.optimizationBenchmarking.utils.graphics.graphic;

import org.optimizationBenchmarking.utils.graphics.graphic.impl.pgf.PGFGraphicDriver;
import org.optimizationBenchmarking.utils.graphics.graphic.spec.IGraphicDriver;

/** Test the PGF graphic driver */
public class PGFGraphicDriverTest extends GraphicDriverTest {

  /** create the graphic driver test */
  public PGFGraphicDriverTest() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  protected IGraphicDriver getInstance() {
    return PGFGraphicDriver.getInstance();
  }
}
