package test.junit.org.optimizationBenchmarking.utils.graphics.graphic;

import org.optimizationBenchmarking.utils.graphics.graphic.impl.imageioRaster.ImageIOGIFGraphicDriver;
import org.optimizationBenchmarking.utils.graphics.graphic.spec.IGraphicDriver;

/** Test the ImageIO GIF graphic driver */
public class ImageIOGIFGraphicDriverTest extends GraphicDriverTest {

  /** create the graphic driver test */
  public ImageIOGIFGraphicDriverTest() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  protected IGraphicDriver getInstance() {
    return ImageIOGIFGraphicDriver.getInstance();
  }
}
