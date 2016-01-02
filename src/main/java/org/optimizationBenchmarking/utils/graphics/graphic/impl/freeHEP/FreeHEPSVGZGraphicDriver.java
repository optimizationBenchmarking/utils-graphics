package org.optimizationBenchmarking.utils.graphics.graphic.impl.freeHEP;

import java.awt.Dimension;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.logging.Logger;

import org.optimizationBenchmarking.utils.error.RethrowMode;
import org.optimizationBenchmarking.utils.graphics.GraphicUtils;
import org.optimizationBenchmarking.utils.graphics.graphic.impl.EGraphicFormat;
import org.optimizationBenchmarking.utils.graphics.graphic.impl.abstr.AbstractGraphicDriver;
import org.optimizationBenchmarking.utils.graphics.graphic.impl.abstr.GraphicBuilder;
import org.optimizationBenchmarking.utils.graphics.graphic.spec.Graphic;
import org.optimizationBenchmarking.utils.io.paths.PathUtils;
import org.optimizationBenchmarking.utils.text.TextUtils;

/**
 * A driver which creates
 * <a href="http://en.wikipedia.org/wiki/Scalable_Vector_Graphics">SVGZ</a>
 * (graphics).
 */
public final class FreeHEPSVGZGraphicDriver extends AbstractGraphicDriver {
  /** the properties */
  private final Map<Object, Object> m_props;

  /** the error */
  private final Throwable m_error;

  /** the hidden constructor */
  FreeHEPSVGZGraphicDriver() {
    super(EGraphicFormat.SVGZ);

    Map<Object, Object> o;
    Throwable error;

    error = null;
    try {
      o = FreeHEPSVGGraphicDriver._initialize(true);
    } catch (final Throwable t) {
      o = null;
      error = t;
    }
    this.m_props = o;
    this.m_error = error;
  }

  /** {@inheritDoc} */
  @Override
  public final void checkCanUse() {
    if (this.m_error != null) {
      throw new UnsupportedOperationException(
          ("Cannot use " + //$NON-NLS-1$
              TextUtils.className(FreeHEPSVGZGraphicDriver.class)),
          this.m_error);
    }
    super.checkCanUse();
  }

  /** {@inheritDoc} */
  @Override
  public final boolean canUse() {
    return ((this.m_props != null) && (this.m_error == null));
  }

  /**
   * get the instance of the FreeHEP SVGZ driver
   *
   * @return the instance of the FreeHEP SVGZ driver
   */
  public static final FreeHEPSVGZGraphicDriver getInstance() {
    return __FreeHEPSVGZGraphicDriverLoader.INSTANCE;
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return ("FreeHEP-based SVGZ Driver"); //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @SuppressWarnings("resource")
  @Override
  protected final Graphic createGraphic(final GraphicBuilder builder) {
    final org.freehep.util.UserProperties up;
    final _FreeHEPSVGGraphic g;
    final Dimension dim;
    final Path path;
    final Logger logger;
    OutputStream stream;

    up = new org.freehep.util.UserProperties();
    up.putAll(this.m_props);

    dim = AbstractGraphicDriver.getIntegerSizeInPoints(builder.getSize());

    path = this.makePath(builder.getBasePath(),
        builder.getMainDocumentNameSuggestion());
    try {
      stream = PathUtils.openOutputStream(path);
    } catch (final Throwable thro) {
      RethrowMode.AS_RUNTIME_EXCEPTION.rethrow((((//
      "Error while opening OutputStream '") //$NON-NLS-1$
          + path) + "' for FreeHEPSVGZGraphic."), //$NON-NLS-1$
          true, thro);
      return null; // we'll never get here
    }

    logger = builder.getLogger();
    synchronized (org.freehep.graphicsio.svg.SVGGraphics2D.class) {
      g = new _FreeHEPSVGGraphic(stream, dim, logger);
      g.setProperties(up);
      g.setClip(0, 0, dim.width, dim.height);
      GraphicUtils.applyDefaultRenderingHints(g);
      g.startExport();
    }

    return new _FreeHEPSVGGraphicWrapper(g, logger,
        builder.getFileProducerListener(), path, dim.width, dim.height,
        ((EGraphicFormat) (this.getFileType())));
  }

  /** the loader class */
  private static final class __FreeHEPSVGZGraphicDriverLoader {
    /**
     * the globally shared instance of the
     * <a href="http://en.wikipedia.org/wiki/Scalable_Vector_Graphics" >
     * SVGZ</a> graphic driver
     */
    static final FreeHEPSVGZGraphicDriver INSTANCE = new FreeHEPSVGZGraphicDriver();
  }
}
