package examples.org.optimizationBenchmarking.utils.graphics;

import java.util.LinkedHashSet;

import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.error.ErrorUtils;
import org.optimizationBenchmarking.utils.graphics.EColorModel;
import org.optimizationBenchmarking.utils.graphics.graphic.impl.EGraphicFormat;
import org.optimizationBenchmarking.utils.graphics.graphic.impl.abstr.GraphicConfiguration;
import org.optimizationBenchmarking.utils.graphics.graphic.impl.abstr.GraphicConfigurationBuilder;
import org.optimizationBenchmarking.utils.graphics.graphic.impl.freeHEP.FreeHEPEMFGraphicDriver;
import org.optimizationBenchmarking.utils.graphics.graphic.impl.freeHEP.FreeHEPEPSGraphicDriver;
import org.optimizationBenchmarking.utils.graphics.graphic.impl.freeHEP.FreeHEPPDFGraphicDriver;
import org.optimizationBenchmarking.utils.graphics.graphic.impl.freeHEP.FreeHEPSVGGraphicDriver;
import org.optimizationBenchmarking.utils.graphics.graphic.impl.freeHEP.FreeHEPSVGZGraphicDriver;
import org.optimizationBenchmarking.utils.graphics.graphic.impl.imageioRaster.ImageIOBMPGraphicDriver;
import org.optimizationBenchmarking.utils.graphics.graphic.impl.imageioRaster.ImageIOGIFGraphicDriver;
import org.optimizationBenchmarking.utils.graphics.graphic.impl.imageioRaster.ImageIOJPEGGraphicDriver;
import org.optimizationBenchmarking.utils.graphics.graphic.impl.imageioRaster.ImageIOPNGGraphicDriver;
import org.optimizationBenchmarking.utils.graphics.graphic.impl.pgf.PGFGraphicDriver;
import org.optimizationBenchmarking.utils.graphics.graphic.spec.IGraphicDriver;

/**
 * A set of examples for graphics configurations. Each object in
 * {@link #CONFIGURATIONS} represents one fully-configured graphic setup,
 * including a
 * {@link org.optimizationBenchmarking.utils.graphics.graphic.spec.IGraphicDriver
 * graphic driver} and its configurations. We can use the objects here to
 * generate example graphics and figures to demonstrate the capabilities of
 * our graphics package.
 */
public final class ExampleGraphicConfigurations {

  /** the shared instances */
  public static final ArrayListView<GraphicConfiguration> CONFIGURATIONS = //
  ExampleGraphicConfigurations.__make();

  /**
   * make the graphics configurations
   *
   * @return the graphics configurations
   */
  private static final ArrayListView<GraphicConfiguration> __make() {
    final LinkedHashSet<GraphicConfiguration> examples;
    GraphicConfigurationBuilder builder;
    IGraphicDriver d;

    examples = new LinkedHashSet<>();

    d = FreeHEPEPSGraphicDriver.getInstance();
    if (d.canUse()) {
      builder = new GraphicConfigurationBuilder();
      builder.setGraphicDriver(d);
      examples.add(builder.immutable());
    }

    d = FreeHEPPDFGraphicDriver.getInstance();
    if (d.canUse()) {
      builder = new GraphicConfigurationBuilder();
      builder.setGraphicDriver(d);
      examples.add(builder.immutable());
    }

    d = PGFGraphicDriver.getInstance();
    if (d.canUse()) {
      builder = new GraphicConfigurationBuilder();
      builder.setGraphicDriver(d);
      examples.add(builder.immutable());
    }

    d = FreeHEPSVGZGraphicDriver.getInstance();
    if (d.canUse()) {
      builder = new GraphicConfigurationBuilder();
      builder.setGraphicDriver(d);
      examples.add(builder.immutable());
    }

    d = FreeHEPSVGGraphicDriver.getInstance();
    if (d.canUse()) {
      builder = new GraphicConfigurationBuilder();
      builder.setGraphicDriver(d);
      examples.add(builder.immutable());
    }

    d = FreeHEPEMFGraphicDriver.getInstance();
    if (d.canUse()) {
      builder = new GraphicConfigurationBuilder();
      builder.setGraphicDriver(d);
      examples.add(builder.immutable());
    }

    d = ImageIOPNGGraphicDriver.getInstance();
    if (d.canUse()) {
      builder = new GraphicConfigurationBuilder();
      builder.setGraphicDriver(d);
      examples.add(builder.immutable());

      builder.setDotsPerInch(256);

      builder.setColorModel(EColorModel.ARGB_32_BIT);
      examples.add(builder.immutable());
      builder.setColorModel(EColorModel.RGB_24_BIT);
      examples.add(builder.immutable());
      builder.setColorModel(EColorModel.RGB_16_BIT);
      examples.add(builder.immutable());
      builder.setColorModel(EColorModel.RGB_15_BIT);
      examples.add(builder.immutable());
      builder.setColorModel(EColorModel.GRAY_16_BIT);
      examples.add(builder.immutable());
      builder.setColorModel(EColorModel.GRAY_8_BIT);
      examples.add(builder.immutable());
    }

    d = ImageIOJPEGGraphicDriver.getInstance();
    if (d.canUse()) {
      builder = new GraphicConfigurationBuilder();
      builder.setGraphicDriver(d);
      examples.add(builder.immutable());

      builder.setDotsPerInch(256);
      builder.setColorModel(EColorModel.RGB_24_BIT);
      builder.setQuality(0d);
      examples.add(builder.immutable());
      builder.setQuality(1d);
      examples.add(builder.immutable());

      builder.setDotsPerInch(333);
      builder.setColorModel(EColorModel.RGB_24_BIT);
      builder.setQuality(0d);
      examples.add(builder.immutable());
      builder.setQuality(1d);
      examples.add(builder.immutable());

      builder.setDotsPerInch(256);
      builder.setColorModel(EColorModel.RGB_15_BIT);
      builder.setQuality(0d);
      examples.add(builder.immutable());
      builder.setQuality(1d);
      examples.add(builder.immutable());

      builder.setDotsPerInch(333);
      builder.setColorModel(EColorModel.RGB_15_BIT);
      builder.setQuality(0d);
      examples.add(builder.immutable());
      builder.setQuality(1d);
      examples.add(builder.immutable());

      builder.setDotsPerInch(32);
      builder.setColorModel(EColorModel.RGB_15_BIT);
      builder.setQuality(0d);
      examples.add(builder.immutable());
      builder.setQuality(1d);
      examples.add(builder.immutable());
    }

    d = ImageIOGIFGraphicDriver.getInstance();
    if (d.canUse()) {
      builder = new GraphicConfigurationBuilder();
      builder.setGraphicDriver(d);
      examples.add(builder.immutable());

      builder.setColorModel(EColorModel.ARGB_32_BIT);
      builder.setDotsPerInch(600);
      examples.add(builder.immutable());
    }

    d = EGraphicFormat.NULL.getDefaultDriver();
    if (d.canUse()) {
      builder = new GraphicConfigurationBuilder();
      builder.setGraphicDriver(d);
      examples.add(builder.immutable());
    }

    d = ImageIOBMPGraphicDriver.getInstance();
    if (d.canUse()) {
      builder = new GraphicConfigurationBuilder();
      builder.setGraphicDriver(d);
      examples.add(builder.immutable());

      builder.setColorModel(EColorModel.RGB_15_BIT);
      builder.setDotsPerInch(2048);
      examples.add(builder.immutable());

      builder.setColorModel(EColorModel.RGB_15_BIT);
      builder.setDotsPerInch(1024);
      examples.add(builder.immutable());

      builder.setColorModel(EColorModel.RGB_15_BIT);
      builder.setDotsPerInch(512);
      examples.add(builder.immutable());

      builder.setColorModel(EColorModel.RGB_15_BIT);
      builder.setDotsPerInch(256);
      examples.add(builder.immutable());

      builder.setColorModel(EColorModel.RGB_15_BIT);
      builder.setDotsPerInch(128);
      examples.add(builder.immutable());

      builder.setColorModel(EColorModel.RGB_15_BIT);
      builder.setDotsPerInch(64);
      examples.add(builder.immutable());

      builder.setColorModel(EColorModel.RGB_15_BIT);
      builder.setDotsPerInch(32);
      examples.add(builder.immutable());
    }

    for (final EGraphicFormat format : EGraphicFormat.INSTANCES) {
      d = format.getDefaultDriver();
      if (d.canUse()) {
        builder = new GraphicConfigurationBuilder();
        builder.setGraphicDriver(d);
        examples.add(builder.immutable());
      }
    }

    return ArrayListView.collectionToView(examples);
  }

  /** the forbidden constructor */
  private ExampleGraphicConfigurations() {
    ErrorUtils.doNotCall();
  }
}
