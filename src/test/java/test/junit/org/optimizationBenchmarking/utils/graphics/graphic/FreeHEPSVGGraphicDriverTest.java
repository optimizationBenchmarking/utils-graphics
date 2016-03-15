package test.junit.org.optimizationBenchmarking.utils.graphics.graphic;

import org.optimizationBenchmarking.utils.graphics.graphic.impl.freeHEP.FreeHEPSVGGraphicDriver;

/** Test the FreeHEP SVG graphic driver */
public class FreeHEPSVGGraphicDriverTest extends GraphicDriverTest {

  /** create the graphic driver test */
  public FreeHEPSVGGraphicDriverTest() {
    super(FreeHEPSVGGraphicDriver.getInstance());
  }
}
