package test.junit.org.optimizationBenchmarking.utils.graphics.graphic;

import org.optimizationBenchmarking.utils.graphics.graphic.impl.freeHEP.FreeHEPEPSGraphicDriver;
import org.optimizationBenchmarking.utils.graphics.graphic.spec.IGraphicDriver;

/** Test the FreeHEP EPS graphic driver */
public class FreeHEPEPSGraphicDriverTest extends GraphicDriverTest {

  /** create the graphic driver test */
  public FreeHEPEPSGraphicDriverTest() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  protected IGraphicDriver getInstance() {
    return FreeHEPEPSGraphicDriver.getInstance();
  }
}
