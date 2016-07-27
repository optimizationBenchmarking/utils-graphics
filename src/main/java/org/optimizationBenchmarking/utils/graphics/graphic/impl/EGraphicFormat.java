package org.optimizationBenchmarking.utils.graphics.graphic.impl;

import org.optimizationBenchmarking.utils.collections.lists.ArraySetView;
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
import org.optimizationBenchmarking.utils.graphics.graphic.impl.text.TextGraphicDriver;
import org.optimizationBenchmarking.utils.graphics.graphic.spec.IGraphicDriver;
import org.optimizationBenchmarking.utils.graphics.graphic.spec.IGraphicFormat;
import org.optimizationBenchmarking.utils.text.ETextFileType;
import org.optimizationBenchmarking.utils.text.TextUtils;

/**
 * An enumeration with graphics formats.
 */
public enum EGraphicFormat implements IGraphicFormat {

  /** A graphics driver which discards all output */
  NULL(TextUtils.NULL_STRING, false, null, null) {
    /** {@inheritDoc} */
    @Override
    public final IGraphicDriver getDefaultDriver() {
      return NullGraphicDriver.getInstance();
    }
  },
  /**
   * The
   * <a href="http://en.wikipedia.org/wiki/Encapsulated_PostScript">EPS</a>
   * format is a
   * <a href="http://en.wikipedia.org/wiki/Vector_graphics">vector
   * graphic</a> format based on the PostScript language which is
   * understood by many printers. EPS files may be a bit larger than
   * {@link #PDF}s and EPS viewers are less wide-spread.
   */
  EPS("Encapsulated PostScript", true, //$NON-NLS-1$
      "eps", "image/eps") { //$NON-NLS-1$//$NON-NLS-2$
    /** {@inheritDoc} */
    @Override
    public final IGraphicDriver getDefaultDriver() {
      return FreeHEPEPSGraphicDriver.getInstance();
    }
  },

  /**
   * The
   * <a href="http://en.wikipedia.org/wiki/Portable_Document_Format">PDF
   * </a> format is a
   * <a href="http://en.wikipedia.org/wiki/Vector_graphics">vector
   * graphic</a> format which is maybe the most wide-spread format for
   * platform-independent documents and vector images. Viewers are
   * available on almost all platforms.
   */
  PDF("Portable Document Format", true, //$NON-NLS-1$
      "pdf", "application/pdf") { //$NON-NLS-1$//$NON-NLS-2$
    /** {@inheritDoc} */
    @Override
    public final IGraphicDriver getDefaultDriver() {
      return FreeHEPPDFGraphicDriver.getInstance();
    }
  },

  /**
   * The Portable Graphic Format (PGF)
   * <sup><a href="http://en.wikipedia.org/wiki/PGF/TikZ">1</a>,
   * <a href="http://ctan.org/pkg/pgf">2</a></sup>. PGF is a
   * <a href="http://en.wikipedia.org/wiki/LaTeX">LaTeX</a> format, meaning
   * that such figures can readily be included into LaTeX documents via
   * {@code \input}.
   * <p>
   * Note: Large PGF graphics may make LaTeX fail due to out-of-memory. It
   * seems that lualatex can dynamically allocate more memory if necessary
   * (this is why we currently priorize using a lualatex-based tool chain
   * in the LaTeX tool). see http://tex.stackexchange.com/questions/7953
   * </p>
   */
  PGF("Portable Graphics Format", false, //$NON-NLS-1$
      "tex", "application/x-latex") { //$NON-NLS-1$//$NON-NLS-2$
    /** {@inheritDoc} */
    @Override
    public final IGraphicDriver getDefaultDriver() {
      return PGFGraphicDriver.getInstance();
    }
  },

  /**
   * The
   * <a href="http://en.wikipedia.org/wiki/Scalable_Vector_Graphics">SVG
   * </a> format is a
   * <a href="http://en.wikipedia.org/wiki/Vector_graphics">vector
   * graphic</a> format intended for the web. It is based on XML and
   * browser support is now increasing, although not all browsers can
   * display SVGs properly.
   */
  SVG("Scalable Vector Graphics", true, //$NON-NLS-1$
      "svg", "image/svg+xml") { //$NON-NLS-1$//$NON-NLS-2$
    /** {@inheritDoc} */
    @Override
    public final IGraphicDriver getDefaultDriver() {
      return FreeHEPSVGGraphicDriver.getInstance();
    }
  },

  /**
   * The
   * <a href="http://en.wikipedia.org/wiki/Scalable_Vector_Graphics">SVG
   * </a> format is the ZIP-compressed version of {@link #SVG}.
   */
  SVGZ("Compressed Scalable Vector Graphics", SVG.m_isVector, //$NON-NLS-1$
      "svgz", SVG.m_mime) { //$NON-NLS-1$
    /** {@inheritDoc} */
    @Override
    public final IGraphicDriver getDefaultDriver() {
      return FreeHEPSVGZGraphicDriver.getInstance();
    }
  },

  /**
   * The <a href="http://en.wikipedia.org/wiki/Windows_Metafile">EMF</a>
   * format is a
   * <a href="http://en.wikipedia.org/wiki/Vector_graphics">vector
   * graphic</a> format developed by Microsoft and mainly supported on
   * Microsoft Windows.
   */
  EMF("Enhanced Metafile", true, //$NON-NLS-1$
      "emf", "image/x-emf") { //$NON-NLS-1$//$NON-NLS-2$
    /** {@inheritDoc} */
    @Override
    public final IGraphicDriver getDefaultDriver() {
      return FreeHEPEMFGraphicDriver.getInstance();
    }
  },

  /**
   * The <a href="http://en.wikipedia.org/wiki/JPEG">JPEG</a> format is a
   * lossy <a href="http://en.wikipedia.org/wiki/Raster_graphics">raster
   * graphics</a> image format. This format is mainly suitable for photos.
   */
  JPEG("JPEG Image", false, //$NON-NLS-1$
      "jpg", "image/jpeg") { //$NON-NLS-1$//$NON-NLS-2$
    /** {@inheritDoc} */
    @Override
    public final IGraphicDriver getDefaultDriver() {
      return ImageIOJPEGGraphicDriver.getInstance();
    }
  },

  /**
   * The
   * <a href="http://en.wikipedia.org/wiki/Portable_Network_Graphics">PNG
   * </a> format is a lossless
   * <a href="http://en.wikipedia.org/wiki/Raster_graphics">raster
   * graphics</a> format, something like an extended and improved version
   * of {@link #GIF}.
   */
  PNG("Portable Network Graphics", false, //$NON-NLS-1$
      "png", "image/png") { //$NON-NLS-1$//$NON-NLS-2$
    /** {@inheritDoc} */
    @Override
    public final IGraphicDriver getDefaultDriver() {
      return ImageIOPNGGraphicDriver.getInstance();
    }
  },

  /**
   * The
   * <a href="http://en.wikipedia.org/wiki/Graphics_Interchange_Format">
   * GIF</a> format is a
   * <a href="http://en.wikipedia.org/wiki/Raster_graphics">raster
   * graphics</a> format supporting at most 256 colors. For many
   * applications, it has been superseded by {@link #PNG}.
   */
  GIF("Graphics Interchange Format", false, //$NON-NLS-1$
      "gif", "image/gif") { //$NON-NLS-1$//$NON-NLS-2$
    /** {@inheritDoc} */
    @Override
    public final IGraphicDriver getDefaultDriver() {
      return ImageIOGIFGraphicDriver.getInstance();
    }
  },

  /**
   * The <a href="http://en.wikipedia.org/wiki/BMP_file_format">BMP</a>
   * format is lossless
   * <a href="http://en.wikipedia.org/wiki/Raster_graphics">raster
   * graphics</a> format. It is mainly supported on Microsoft Windows
   * systems and not as far-spred as {@link #PNG}.
   */
  BMP("Bitmap Image Format", false, //$NON-NLS-1$
      "bmp", "image/bmp") { //$NON-NLS-1$//$NON-NLS-2$
    /** {@inheritDoc} */
    @Override
    public final IGraphicDriver getDefaultDriver() {
      return ImageIOBMPGraphicDriver.getInstance();
    }
  },

  /**
   * A graphic format not intended to actually produce graphics, but
   * instead for creating text files containing, e.g.,
   * {@link org.optimizationBenchmarking.utils.text.ETextFileType#CSV}
   * data. This is intended to allow specialized chart drivers to export
   * the chart data instead of rendering them.
   */
  TEXT(ETextFileType.TXT.getName(), true, //
      ETextFileType.TXT.getDefaultSuffix(), //
      ETextFileType.TXT.getMIMEType()) {
    /** {@inheritDoc} */
    @Override
    public final IGraphicDriver getDefaultDriver() {
      return TextGraphicDriver.getInstance();
    }
  },

  ;

  /** The set of graphic formats. */
  public static final ArraySetView<EGraphicFormat> INSTANCES = //
  new ArraySetView<>(EGraphicFormat.values(), false);

  /** is this a vector graphic format? */
  private final boolean m_isVector;

  /** the file type's name */
  private final String m_name;

  /** the suffix for files of this type */
  private final String m_suffix;

  /** the mime type */
  private final String m_mime;

  /**
   * Create the graphics format specifier
   *
   * @param name
   *          the file type's name
   * @param isVector
   *          is this a vector graphic?
   * @param suffix
   *          the file suffix
   * @param mime
   *          the mime type
   */
  private EGraphicFormat(final String name, final boolean isVector,
      final String suffix, final String mime) {
    this.m_name = name;
    this.m_isVector = isVector;
    this.m_suffix = suffix;
    this.m_mime = mime;
  }

  /** {@inheritDoc} */
  @Override
  public IGraphicDriver getDefaultDriver() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isVectorFormat() {
    return this.m_isVector;
  }

  /** {@inheritDoc} */
  @Override
  public final String getDefaultSuffix() {
    return this.m_suffix;
  }

  /** {@inheritDoc} */
  @Override
  public final String getMIMEType() {
    return this.m_mime;
  }

  /** {@inheritDoc} */
  @Override
  public final String getName() {
    return this.m_name;
  }
}
