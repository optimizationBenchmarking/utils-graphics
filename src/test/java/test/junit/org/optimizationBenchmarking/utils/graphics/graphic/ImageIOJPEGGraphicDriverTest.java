package test.junit.org.optimizationBenchmarking.utils.graphics.graphic;

import org.optimizationBenchmarking.utils.graphics.graphic.impl.imageioRaster.ImageIOJPEGGraphicDriver;

/** Test the ImageIO JPEG graphic driver */
public class ImageIOJPEGGraphicDriverTest extends GraphicDriverTest {

  /** create the graphic driver test */
  public ImageIOJPEGGraphicDriverTest() {
    super(ImageIOJPEGGraphicDriver.getInstance());
  }
}
