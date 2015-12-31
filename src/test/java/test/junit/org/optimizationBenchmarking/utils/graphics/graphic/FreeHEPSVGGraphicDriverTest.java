package test.junit.org.optimizationBenchmarking.utils.graphics.graphic;

import org.optimizationBenchmarking.utils.graphics.graphic.impl.freeHEP.FreeHEPSVGGraphicDriver;
import org.optimizationBenchmarking.utils.graphics.graphic.spec.IGraphicDriver;

/** Test the FreeHEP SVG graphic driver */
public class FreeHEPSVGGraphicDriverTest extends GraphicDriverTest {

  /** create the graphic driver test */
  public FreeHEPSVGGraphicDriverTest() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  protected IGraphicDriver getInstance() {
    return FreeHEPSVGGraphicDriver.getInstance();
  }
}
