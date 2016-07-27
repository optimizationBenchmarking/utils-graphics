package examples.org.optimizationBenchmarking.utils.graphics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.optimizationBenchmarking.utils.MemoryUtils;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.graphics.graphic.impl.abstr.GraphicConfiguration;
import org.optimizationBenchmarking.utils.graphics.style.impl.stroke.DefaultStrokePalette;
import org.optimizationBenchmarking.utils.graphics.style.spec.IStrokePalette;

/**
 * An example used to illustrate the available stroke palettes.
 */
public class StrokePaletteExamples {

  /** the palettes to print */
  public static final ArrayListView<IStrokePalette> PALETTES = new ArrayListView<>(
      new IStrokePalette[] { //
          DefaultStrokePalette.getInstance() },
      false);

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
      for (final IStrokePalette palette : StrokePaletteExamples.PALETTES) {
        Thread.yield();
        new StrokePaletteExample(null, dir, //
            config.getGraphicDriver(), //
            null, config.getColorModel(), config.getDotsPerInch(),
            config.getQuality(), palette).run();
        Thread.yield();
      }
      MemoryUtils.fullGC();
    }
  }
}
