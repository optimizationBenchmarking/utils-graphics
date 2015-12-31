package test.junit.org.optimizationBenchmarking.utils.graphics.graphic;

import org.optimizationBenchmarking.utils.graphics.graphic.impl.freeHEP.FreeHEPPDFGraphicDriver;
import org.optimizationBenchmarking.utils.graphics.graphic.spec.IGraphicDriver;

/** Test the FreeHEP PDF graphic driver */
public class FreeHEPPDFGraphicDriverTest extends GraphicDriverTest {

  /** create the graphic driver test */
  public FreeHEPPDFGraphicDriverTest() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  protected IGraphicDriver getInstance() {
    return FreeHEPPDFGraphicDriver.getInstance();
  }
}
