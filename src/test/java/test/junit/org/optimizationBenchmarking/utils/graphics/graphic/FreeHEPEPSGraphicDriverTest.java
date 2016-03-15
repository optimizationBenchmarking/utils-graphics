package test.junit.org.optimizationBenchmarking.utils.graphics.graphic;

import org.optimizationBenchmarking.utils.graphics.graphic.impl.freeHEP.FreeHEPEPSGraphicDriver;

/** Test the FreeHEP EPS graphic driver */
public class FreeHEPEPSGraphicDriverTest extends GraphicDriverTest {

  /** create the graphic driver test */
  public FreeHEPEPSGraphicDriverTest() {
    super(FreeHEPEPSGraphicDriver.getInstance());
  }
}
