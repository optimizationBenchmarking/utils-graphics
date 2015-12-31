package test.junit.org.optimizationBenchmarking.utils.graphics.graphic;

import org.optimizationBenchmarking.utils.graphics.graphic.impl.imageioRaster.ImageIOBMPGraphicDriver;
import org.optimizationBenchmarking.utils.graphics.graphic.spec.IGraphicDriver;

/** Test the ImageIO BMP graphic driver */
public class ImageIOBMPGraphicDriverTest extends GraphicDriverTest {

  /** create the graphic driver test */
  public ImageIOBMPGraphicDriverTest() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  protected IGraphicDriver getInstance() {
    return ImageIOBMPGraphicDriver.getInstance();
  }
}
