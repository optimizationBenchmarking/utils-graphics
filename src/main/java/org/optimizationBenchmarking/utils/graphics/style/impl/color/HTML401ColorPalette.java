package org.optimizationBenchmarking.utils.graphics.style.impl.color;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.optimizationBenchmarking.utils.config.Configuration;
import org.optimizationBenchmarking.utils.error.ErrorUtils;
import org.optimizationBenchmarking.utils.error.RethrowMode;
import org.optimizationBenchmarking.utils.graphics.style.spec.IColorStyle;

/**
 * a palette based on the
 * <a href="http://www.w3.org/TR/REC-html40/types.html#h-6.5">HTML 4.01</a>
 * color names
 */
public final class HTML401ColorPalette extends ColorPalette {

  /** the serial version uid */
  private static final long serialVersionUID = 1L;

  /**
   * Get an instance of the html 401 palette
   *
   * @return the html 401 palette
   */
  public static final HTML401ColorPalette getInstance() {
    return __DefaultHTML401PaletteLoader.INSTANCE;
  }

  /**
   * instantiate
   *
   * @param data
   *          the data
   */
  HTML401ColorPalette(final ColorStyle[] data) {
    super(data);
  }

  /**
   * read resolve
   *
   * @return {@link #getInstance()}
   */
  private final Object readResolve() {
    return HTML401ColorPalette.getInstance();
  }

  /**
   * write replace
   *
   * @return {@link #getInstance()}
   */
  private final Object writeReplace() {
    return HTML401ColorPalette.getInstance();
  }

  /** the default palette builder */
  private static final class __DefaultHTML401PaletteBuilder
      extends ColorPaletteBuilder {
    /** the default palette builder */
    __DefaultHTML401PaletteBuilder() {
      super();
    }

    /** {@inheritDoc} */
    @Override
    protected final HTML401ColorPalette createPalette(
        final ArrayList<IColorStyle> data) {
      return new HTML401ColorPalette(
          data.toArray(new ColorStyle[data.size()]));
    }
  }

  /** the default html 401 palette loader */
  private static final class __DefaultHTML401PaletteLoader {
    /**
     * the globally shared instance of ta palette based on the
     * <a href="http://www.w3.org/TR/REC-html40/types.html#h-6.5">HTML
     * 4.01</a> color names
     */
    static final HTML401ColorPalette INSTANCE;

    static {
      final Logger logger;
      ColorPalette pal;

      pal = null;
      logger = Configuration.getGlobalLogger();
      try (
          final __DefaultHTML401PaletteBuilder cspb = new __DefaultHTML401PaletteBuilder()) {
        ColorPaletteXMLInput.getInstance().use().setLogger(logger)
            .setDestination(cspb)
            .addResource(HTML401ColorPalette.class, "html401.colorPalette") //$NON-NLS-1$
            .create().call();
        pal = cspb.getResult();
      } catch (final Throwable t) {
        ErrorUtils.logError(logger,
            "Error while loading the HTML 4.01 color palette. This palette will not be available.", //$NON-NLS-1$
            t, true, RethrowMode.AS_RUNTIME_EXCEPTION);
      }

      INSTANCE = ((HTML401ColorPalette) pal);
    }
  }
}
