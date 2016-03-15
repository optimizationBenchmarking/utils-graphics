package test.junit.org.optimizationBenchmarking.utils.graphics.graphic;

import org.optimizationBenchmarking.utils.graphics.graphic.impl.freeHEP.FreeHEPSVGZGraphicDriver;

/** Test the FreeHEP SVGZ graphic driver */
public class FreeHEPSVGZGraphicDriverTest extends GraphicDriverTest {

  /** create the graphic driver test */
  public FreeHEPSVGZGraphicDriverTest() {
    super(FreeHEPSVGZGraphicDriver.getInstance());
  }
}
