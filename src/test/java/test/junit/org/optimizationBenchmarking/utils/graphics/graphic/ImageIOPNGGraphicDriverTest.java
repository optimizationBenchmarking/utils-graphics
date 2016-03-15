package test.junit.org.optimizationBenchmarking.utils.graphics.graphic;

import org.optimizationBenchmarking.utils.graphics.graphic.impl.imageioRaster.ImageIOPNGGraphicDriver;

/** Test the ImageIO PNG graphic driver */
public class ImageIOPNGGraphicDriverTest extends GraphicDriverTest {

  /** create the graphic driver test */
  public ImageIOPNGGraphicDriverTest() {
    super(ImageIOPNGGraphicDriver.getInstance());
  }
}
