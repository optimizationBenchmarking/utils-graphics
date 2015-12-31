package test.junit.org.optimizationBenchmarking.utils.graphics.graphic;

import org.optimizationBenchmarking.utils.graphics.graphic.impl.text.TextGraphicDriver;
import org.optimizationBenchmarking.utils.graphics.graphic.spec.IGraphicDriver;
import org.optimizationBenchmarking.utils.graphics.graphic.spec.IGraphicFormat;

/** Test the text graphic driver */
public class TextGraphicDriverTest extends GraphicDriverTest {

  /** create the graphic driver test */
  public TextGraphicDriverTest() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  protected IGraphicDriver getInstance() {
    return TextGraphicDriver.getInstance();
  }

  /** {@inheritDoc} */
  @Override
  protected IGraphicFormat getExpectedOutputFormat() {
    return null;
  }
}
