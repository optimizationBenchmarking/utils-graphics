package test.junit.org.optimizationBenchmarking.utils.graphics.graphic;

import org.optimizationBenchmarking.utils.graphics.graphic.impl.imageioRaster.ImageIOBMPGraphicDriver;

/** Test the ImageIO BMP graphic driver */
public class ImageIOBMPGraphicDriverTest extends GraphicDriverTest {

  /** create the graphic driver test */
  public ImageIOBMPGraphicDriverTest() {
    super(ImageIOBMPGraphicDriver.getInstance());
  }
}
