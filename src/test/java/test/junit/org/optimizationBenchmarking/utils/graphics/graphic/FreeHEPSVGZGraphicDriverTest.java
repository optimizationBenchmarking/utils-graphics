package test.junit.org.optimizationBenchmarking.utils.graphics.graphic;

import org.optimizationBenchmarking.utils.graphics.graphic.impl.freeHEP.FreeHEPSVGZGraphicDriver;
import org.optimizationBenchmarking.utils.graphics.graphic.spec.IGraphicDriver;

/** Test the FreeHEP SVGZ graphic driver */
public class FreeHEPSVGZGraphicDriverTest extends GraphicDriverTest {

  /** create the graphic driver test */
  public FreeHEPSVGZGraphicDriverTest() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  protected IGraphicDriver getInstance() {
    return FreeHEPSVGZGraphicDriver.getInstance();
  }
}
