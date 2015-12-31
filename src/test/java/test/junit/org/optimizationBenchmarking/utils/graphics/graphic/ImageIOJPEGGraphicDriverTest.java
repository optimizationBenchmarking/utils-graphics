package test.junit.org.optimizationBenchmarking.utils.graphics.graphic;

import org.optimizationBenchmarking.utils.graphics.graphic.impl.imageioRaster.ImageIOJPEGGraphicDriver;
import org.optimizationBenchmarking.utils.graphics.graphic.spec.IGraphicDriver;

/** Test the ImageIO JPEG graphic driver */
public class ImageIOJPEGGraphicDriverTest extends GraphicDriverTest {

  /** create the graphic driver test */
  public ImageIOJPEGGraphicDriverTest() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  protected IGraphicDriver getInstance() {
    return ImageIOJPEGGraphicDriver.getInstance();
  }
}
