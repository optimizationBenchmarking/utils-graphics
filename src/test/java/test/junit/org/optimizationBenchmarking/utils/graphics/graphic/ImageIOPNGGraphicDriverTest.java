package test.junit.org.optimizationBenchmarking.utils.graphics.graphic;

import org.optimizationBenchmarking.utils.graphics.graphic.impl.imageioRaster.ImageIOPNGGraphicDriver;
import org.optimizationBenchmarking.utils.graphics.graphic.spec.IGraphicDriver;

/** Test the ImageIO PNG graphic driver */
public class ImageIOPNGGraphicDriverTest extends GraphicDriverTest {

  /** create the graphic driver test */
  public ImageIOPNGGraphicDriverTest() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  protected IGraphicDriver getInstance() {
    return ImageIOPNGGraphicDriver.getInstance();
  }
}
