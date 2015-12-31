package examples.org.optimizationBenchmarking.utils.graphics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.optimizationBenchmarking.utils.MemoryUtils;
import org.optimizationBenchmarking.utils.graphics.graphic.impl.abstr.GraphicConfiguration;

/**
 * An example used to illustrate the available drawing methods
 */
public class DrawingExamples {

  /**
   * run the examples
   *
   * @param args
   *          the arguments
   * @throws IOException
   *           if i/o fails
   */
  public static final void main(final String[] args) throws IOException {
    final Path dir;

    if ((args != null) && (args.length > 0)) {
      dir = Paths.get(args[0]);
    } else {
      dir = Files.createTempDirectory("graphics"); //$NON-NLS-1$
    }

    for (final GraphicConfiguration config : ExampleGraphicConfigurations.CONFIGURATIONS) {
      Thread.yield();
      new DrawingExample(null, dir, //
          config.getGraphicDriver(), //
          null, config.getColorModel(), config.getDotsPerInch(),
          config.getQuality()).run();
      MemoryUtils.fullGC();
    }
  }
}
