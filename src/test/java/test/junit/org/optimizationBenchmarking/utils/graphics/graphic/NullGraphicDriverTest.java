package test.junit.org.optimizationBenchmarking.utils.graphics.graphic;

import org.optimizationBenchmarking.utils.graphics.graphic.impl.NullGraphicDriver;
import org.optimizationBenchmarking.utils.graphics.graphic.spec.IGraphicDriver;
import org.optimizationBenchmarking.utils.graphics.graphic.spec.IGraphicFormat;

/** Test the null graphic driver */
public class NullGraphicDriverTest extends GraphicDriverTest {

  /** create the graphic driver test */
  public NullGraphicDriverTest() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  protected IGraphicDriver getInstance() {
    return NullGraphicDriver.getInstance();
  }

  /** {@inheritDoc} */
  @Override
  protected IGraphicFormat getExpectedOutputFormat() {
    return null;
  }
}
