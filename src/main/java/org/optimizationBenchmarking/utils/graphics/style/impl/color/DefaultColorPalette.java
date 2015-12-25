package org.optimizationBenchmarking.utils.graphics.style.impl.color;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.optimizationBenchmarking.utils.config.Configuration;
import org.optimizationBenchmarking.utils.error.ErrorUtils;
import org.optimizationBenchmarking.utils.error.RethrowMode;
import org.optimizationBenchmarking.utils.graphics.style.spec.IColorStyle;

/** the default color palette */
public final class DefaultColorPalette extends ColorPalette {

  /** the serial version uid */
  private static final long serialVersionUID = 1L;

  /**
   * instantiate
   *
   * @param data
   *          the data
   */
  DefaultColorPalette(final ColorStyle[] data) {
    super(data);
  }

  /**
   * read resolve
   *
   * @return {@link #getInstance()}
   */
  private final Object readResolve() {
    return DefaultColorPalette.getInstance();
  }

  /**
   * write replace
   *
   * @return {@link #getInstance()}
   */
  private final Object writeReplace() {
    return DefaultColorPalette.getInstance();
  }

  /**
   * Get an instance of the default color palette
   *
   * @return the default color palette
   */
  public static final DefaultColorPalette getInstance() {
    return __DefaultColorPaletteLoader.INSTANCE;
  }

  /** the default palette builder */
  private static final class __DefaultColorPaletteBuilder
      extends ColorPaletteBuilder {
    /** the default palette builder */
    __DefaultColorPaletteBuilder() {
      super();
    }

    /** {@inheritDoc} */
    @Override
    protected final DefaultColorPalette createPalette(
        final ArrayList<IColorStyle> data) {
      return new DefaultColorPalette(data.toArray(//
          new ColorStyle[data.size()]));
    }
  }

  /** the default color palette loader */
  private static final class __DefaultColorPaletteLoader {

    /** the globally shared instance of the default color palette */
    static final DefaultColorPalette INSTANCE;

    static {
      final Logger logger;
      ColorPalette pal;

      pal = null;
      logger = Configuration.getGlobalLogger();
      try (
          final __DefaultColorPaletteBuilder cspb = new __DefaultColorPaletteBuilder()) {
        ColorPaletteXMLInput.getInstance().use().setLogger(logger)
            .setDestination(cspb)
            .addResource(DefaultColorPalette.class,
                "defaultColor.colorPalette") //$NON-NLS-1$
            .create().call();
        pal = cspb.getResult();
      } catch (final Throwable t) {
        ErrorUtils.logError(logger, //
            "Error while loading the default color palette. This will have severe (as in 'deadly') implications on graphics or documents depending on it.", //$NON-NLS-1$ ,
            t, true, RethrowMode.AS_RUNTIME_EXCEPTION);

      }

      INSTANCE = ((DefaultColorPalette) pal);
    }
  }
}
