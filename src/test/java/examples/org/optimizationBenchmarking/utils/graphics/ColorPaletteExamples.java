package examples.org.optimizationBenchmarking.utils.graphics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.optimizationBenchmarking.utils.MemoryUtils;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.graphics.graphic.impl.abstr.GraphicConfiguration;
import org.optimizationBenchmarking.utils.graphics.style.impl.color.DefaultColorPalette;
import org.optimizationBenchmarking.utils.graphics.style.impl.color.DefaultGrayPalette;
import org.optimizationBenchmarking.utils.graphics.style.impl.color.HTML401ColorPalette;
import org.optimizationBenchmarking.utils.graphics.style.impl.color.JavaDefaultColorPalette;
import org.optimizationBenchmarking.utils.graphics.style.spec.IColorPalette;

/**
 * An example used to illustrate the available color palettes.
 */
public class ColorPaletteExamples {

  /** the palettes to print */
  public static final ArrayListView<IColorPalette> PALETTES = new ArrayListView<>(
      new IColorPalette[] { //
          DefaultColorPalette.getInstance(),
          JavaDefaultColorPalette.getInstance(),
          DefaultGrayPalette.getInstance(),
          HTML401ColorPalette.getInstance() },
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
      for (final IColorPalette palette : ColorPaletteExamples.PALETTES) {
        Thread.yield();
        new ColorPaletteExample(null, dir, //
            config.getGraphicDriver(), //
            null, config.getColorModel(), config.getDotsPerInch(),
            config.getQuality(), palette).run();
        Thread.yield();
      }
      MemoryUtils.fullGC();
    }
  }
}
