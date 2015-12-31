package test.junit.org.optimizationBenchmarking.utils.graphics.graphic;

import org.optimizationBenchmarking.utils.graphics.graphic.impl.freeHEP.FreeHEPEMFGraphicDriver;
import org.optimizationBenchmarking.utils.graphics.graphic.spec.IGraphicDriver;

/** Test the FreeHEP EMF graphic driver */
public class FreeHEPEMFGraphicDriverTest extends GraphicDriverTest {

  /** create the graphic driver test */
  public FreeHEPEMFGraphicDriverTest() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  protected IGraphicDriver getInstance() {
    return FreeHEPEMFGraphicDriver.getInstance();
  }
}
