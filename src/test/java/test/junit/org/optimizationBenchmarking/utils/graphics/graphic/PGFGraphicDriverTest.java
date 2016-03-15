package test.junit.org.optimizationBenchmarking.utils.graphics.graphic;

import org.optimizationBenchmarking.utils.graphics.graphic.impl.pgf.PGFGraphicDriver;

/** Test the PGF graphic driver */
public class PGFGraphicDriverTest extends GraphicDriverTest {

  /** create the graphic driver test */
  public PGFGraphicDriverTest() {
    super(PGFGraphicDriver.getInstance());
  }
}
