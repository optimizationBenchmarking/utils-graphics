package org.optimizationBenchmarking.utils.graphics.graphic.impl.abstr;

import java.nio.file.Path;
import java.util.logging.Logger;

import org.optimizationBenchmarking.utils.comparison.Compare;
import org.optimizationBenchmarking.utils.graphics.EColorModel;
import org.optimizationBenchmarking.utils.graphics.GraphicUtils;
import org.optimizationBenchmarking.utils.graphics.PhysicalDimension;
import org.optimizationBenchmarking.utils.graphics.graphic.spec.Graphic;
import org.optimizationBenchmarking.utils.graphics.graphic.spec.IGraphicBuilder;
import org.optimizationBenchmarking.utils.graphics.graphic.spec.IGraphicDriver;
import org.optimizationBenchmarking.utils.hash.HashUtils;
import org.optimizationBenchmarking.utils.text.Textable;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;
import org.optimizationBenchmarking.utils.tools.spec.IFileProducerListener;

/**
 * An class which can be used to store and re-use configurations for
 * creating graphics.
 */
public class GraphicConfiguration extends Textable {

  /** the minimum allowed dpi */
  private static final int MIN_DPI = 26;
  /** the maximum allowed dpi */
  static final int MAX_DPI = 10000;
  /** the minimum allowed quality value */
  private static final double MIN_QUALITY = 0d;
  /** the maximum allowed quality value */
  static final double MAX_QUALITY = 1d;

  /** the driver */
  IGraphicDriver m_driver;

  /** the color model */
  EColorModel m_colorModel;

  /** the dpi */
  int m_dpi;

  /** the quality */
  double m_quality;

  /** create the example */
  GraphicConfiguration() {
    super();
    this.m_dpi = (-1);
    this.m_quality = (-1d);
  }

  /**
   * make a copy of a given graphic configuration
   *
   * @param copy
   *          the graphic configuration to copy
   */
  protected GraphicConfiguration(final GraphicConfiguration copy) {
    super();

    GraphicConfiguration
        ._checkDriver(this.m_driver = copy.getGraphicDriver());

    this.m_colorModel = copy.m_colorModel;
    if (this.m_colorModel != null) {
      GraphicConfiguration._checkColorModel(this.m_colorModel);
    }

    this.m_quality = copy.m_quality;
    if (this.m_quality >= 0d) {
      GraphicConfiguration._checkQuality(this.m_quality);
    }

    this.m_dpi = copy.m_dpi;
    if (this.m_dpi > 0) {
      GraphicConfiguration._checkDotsPerInch(this.m_dpi);
    }
  }

  /**
   * Get the immutable version of this configuration
   *
   * @return the immutable version of this configuration
   */
  public GraphicConfiguration immutable() {

    GraphicConfiguration._checkDriver(this.m_driver);

    if (this.getClass() == GraphicConfiguration.class) {
      return this;
    }

    return new GraphicConfiguration(this);
  }

  /**
   * check whether a graphic driver is OK
   *
   * @param driver
   *          the driver
   */
  static final void _checkDriver(final IGraphicDriver driver) {
    if (driver == null) {
      throw new IllegalStateException(//
          "Graphic driver cannot be null."); //$NON-NLS-1$
    }
    driver.checkCanUse();
  }

  /**
   * check the color model
   *
   * @param colorModel
   *          the color model
   */
  static final void _checkColorModel(final EColorModel colorModel) {
    if (colorModel == null) {
      throw new IllegalArgumentException("Color model cannot be null."); //$NON-NLS-1$
    }
  }

  /**
   * check the dots per inch
   *
   * @param dotsPerInch
   *          the dots per inch
   */
  static final void _checkDotsPerInch(final int dotsPerInch) {
    if ((dotsPerInch < GraphicConfiguration.MIN_DPI)
        || (dotsPerInch > GraphicConfiguration.MAX_DPI)) {
      throw new IllegalArgumentException(//
          "Cannot create images with less than 1 pixel per MM (26 dot per inch) or more than 100000 dots per inch, since such images would be nonsense. You specified "//$NON-NLS-1$
              + dotsPerInch + "dpi, which is outside the sane range."); //$NON-NLS-1$
    }
  }

  /**
   * check a quality value
   *
   * @param quality
   *          the quality value
   */
  static final void _checkQuality(final double quality) {
    if ((quality < GraphicConfiguration.MIN_QUALITY)
        || (quality > GraphicConfiguration.MAX_QUALITY)
        || (quality != quality)) {
      throw new IllegalArgumentException(//
          "Graphic quality must be in [0,1], but you specified " //$NON-NLS-1$
              + quality);
    }
  }

  /**
   * Get the dots per inch
   *
   * @return the dots per inch
   */
  public final int getDotsPerInch() {
    return ((this.m_dpi > 0) ? this.m_dpi : GraphicUtils.getDefaultDPI());
  }

  /**
   * Has the dots-per-inch setting been provided?
   *
   * @return {@code true} if the dots-per-inch setting has been provided,
   *         {@code false} otherwise
   */
  public final boolean hasDotsPerInch() {
    return (this.m_dpi > 0);
  }

  /**
   * Get the graphics quality
   *
   * @return the graphics quality
   */
  public final double getQuality() {
    return ((this.m_quality >= 0d) ? this.m_quality
        : GraphicUtils.getDefaultQuality());
  }

  /**
   * Has the quality setting been provided?
   *
   * @return {@code true} if the quality has been provided, {@code false}
   *         otherwise
   */
  public final boolean hasQuality() {
    return (this.m_quality >= 0d);
  }

  /**
   * Get the color model
   *
   * @return the color model
   */
  public final EColorModel getColorModel() {
    return ((this.m_colorModel != null) ? this.m_colorModel
        : GraphicUtils.getDefaultColorModel());
  }

  /**
   * Has the color model been provided?
   *
   * @return {@code true} if the color model has been provided
   */
  public final boolean hasColorModel() {
    return (this.m_colorModel != null);
  }

  /**
   * Get the graphic driver
   *
   * @return the graphic driver
   */
  public final IGraphicDriver getGraphicDriver() {
    return this.m_driver;
  }

  /**
   * Create a graphic of the given size. This is a kitchen-sink method
   * where we throw in all the parameters normally to be passed to
   * {@link org.optimizationBenchmarking.utils.graphics.graphic.spec.IGraphicBuilder}
   * and not already defined in this configuration object. This kitchen
   * sink approach is not nice and maybe will be amended later. But for now
   * it will do.
   *
   * @param basePath
   *          the base path, i.e., the folder in which the graphic should
   *          be created
   * @param name
   *          the name of the graphics file (without extension)
   * @param size
   *          the size of the graphic
   * @param listener
   *          the listener to be notified when painting the graphic has
   *          been completed
   * @param logger
   *          the logger to use
   * @return the graphic
   */
  public final Graphic createGraphic(final Path basePath,
      final String name, final PhysicalDimension size,
      final IFileProducerListener listener, final Logger logger) {
    final IGraphicBuilder builder;

    GraphicConfiguration._checkDriver(this.m_driver);

    builder = this.m_driver.use();
    builder.setBasePath(basePath);
    builder.setMainDocumentNameSuggestion(name);
    builder.setSize(size);
    if (listener != null) {
      builder.setFileProducerListener(listener);
    }

    if (this.m_colorModel != null) {
      builder.setColorModel(this.m_colorModel);
    }

    if (this.m_dpi > 0) {
      builder.setDotsPerInch(this.m_dpi);
    }

    if (this.m_quality >= 0d) {
      builder.setQuality(this.m_quality);
    }
    if (logger != null) {
      builder.setLogger(logger);
    }
    return builder.create();
  }

  /** to string */
  @Override
  public void toText(final ITextOutput textOut) {

    if (this.m_driver != null) {
      textOut.append(this.m_driver.getClass().getSimpleName());
    }

    if (this.m_colorModel != null) {
      textOut.append('@');
      textOut.append(this.m_colorModel.name());
    }

    if (this.m_dpi > 0) {
      textOut.append('@');
      textOut.append(this.m_dpi);
      textOut.append("dpi"); //$NON-NLS-1$
    }

    if (this.m_quality >= 0d) {
      textOut.append('@');
      textOut.append('q');
      textOut.append(this.m_quality);
    }
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    int hc;

    hc = HashUtils.hashCode(this.m_driver);
    if (this.m_colorModel != null) {
      hc = HashUtils.combineHashes(hc,
          HashUtils.hashCode(this.m_colorModel));
    }
    if (this.m_dpi > 0) {
      hc = HashUtils.combineHashes(hc, HashUtils.hashCode(this.m_dpi));
    }
    if (this.m_quality >= 0) {
      hc = HashUtils.combineHashes(hc, HashUtils.hashCode(this.m_quality));
    }

    return hc;
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object o) {
    final GraphicConfiguration example;

    if (o == this) {
      return true;
    }
    if (o == null) {
      return false;
    }

    if (o instanceof GraphicConfiguration) {
      example = ((GraphicConfiguration) o);
      if (Compare.equals(this.m_driver, example.m_driver)) {
        if (Compare.equals(this.m_colorModel, example.m_colorModel)) {

          if (this.m_dpi > 0) {
            if (this.m_dpi != example.m_dpi) {
              return false;
            }
          } else {
            if (example.m_dpi > 0) {
              return false;
            }
          }

          if (this.m_quality >= 0d) {
            if (this.m_quality != example.m_quality) {
              return false;
            }
          } else {
            if (example.m_quality >= 0d) {
              return false;
            }
          }

          return true;
        }
      }
    }
    return false;
  }
}
