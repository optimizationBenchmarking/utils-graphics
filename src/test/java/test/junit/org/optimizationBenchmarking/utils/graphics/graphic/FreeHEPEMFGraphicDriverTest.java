package test.junit.org.optimizationBenchmarking.utils.graphics.graphic;

import org.optimizationBenchmarking.utils.graphics.graphic.impl.freeHEP.FreeHEPEMFGraphicDriver;

/** Test the FreeHEP EMF graphic driver */
public class FreeHEPEMFGraphicDriverTest extends GraphicDriverTest {

  /** create the graphic driver test */
  public FreeHEPEMFGraphicDriverTest() {
    super(FreeHEPEMFGraphicDriver.getInstance());
  }
}
