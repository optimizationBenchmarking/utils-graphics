package test.junit.org.optimizationBenchmarking.utils.graphics.graphic;

import org.optimizationBenchmarking.utils.graphics.graphic.impl.freeHEP.FreeHEPPDFGraphicDriver;

/** Test the FreeHEP PDF graphic driver */
public class FreeHEPPDFGraphicDriverTest extends GraphicDriverTest {

  /** create the graphic driver test */
  public FreeHEPPDFGraphicDriverTest() {
    super(FreeHEPPDFGraphicDriver.getInstance());
  }
}
