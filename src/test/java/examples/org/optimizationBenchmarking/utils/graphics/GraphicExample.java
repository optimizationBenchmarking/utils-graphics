package examples.org.optimizationBenchmarking.utils.graphics;

import java.nio.file.Path;

import org.optimizationBenchmarking.utils.graphics.EColorModel;
import org.optimizationBenchmarking.utils.graphics.PhysicalDimension;
import org.optimizationBenchmarking.utils.graphics.graphic.spec.Graphic;
import org.optimizationBenchmarking.utils.graphics.graphic.spec.IGraphicDriver;
import org.optimizationBenchmarking.utils.text.ETextCase;
import org.optimizationBenchmarking.utils.text.numbers.SimpleNumberAppender;
import org.optimizationBenchmarking.utils.tools.spec.IFileProducerListener;
import org.optimizationBenchmarking.utils.units.ELength;

import examples.org.optimizationBenchmarking.utils.tools.impl.FileProducerExample;

/** An example for graphics. */
public class GraphicExample extends FileProducerExample<IGraphicDriver> {

  /** the color model */
  private final EColorModel m_model;

  /** the dots per inch */
  private final int m_dpi;

  /** the quality */
  private final double m_quality;

  /** the physical size to choose */
  private final PhysicalDimension m_size;

  /**
   * Create the file producer example
   *
   * @param listener
   *          the file producer listener
   * @param destDir
   *          the destination dir
   * @param driver
   *          the graphic driver to use
   * @param size
   *          the physical size dimension
   * @param model
   *          the color model, {@code null} for default
   * @param dpi
   *          the dpi, {@code -1} for default
   * @param quality
   *          the quality, {@code -1} for default
   */
  protected GraphicExample(final IFileProducerListener listener,
      final Path destDir, final IGraphicDriver driver,
      final PhysicalDimension size, final EColorModel model, final int dpi,
      final double quality) {
    super(listener, destDir, driver);
    this.m_size = size;
    this.m_model = model;
    this.m_dpi = dpi;
    this.m_quality = quality;
  }

  /**
   * Get the color model to use
   *
   * @return the color model to use
   */
  protected final EColorModel getColorModel() {
    return ((this.m_model != null) ? this.m_model
        : this.getDefaultColorModel());
  }

  /**
   * Get the default color model
   *
   * @return the default color model
   */
  protected EColorModel getDefaultColorModel() {
    return EColorModel.ARGB_32_BIT;
  }

  /**
   * Get the DPI to use
   *
   * @return the DPI to use
   */
  protected final int getDotsPerInch() {
    return (this.m_dpi > 0) ? this.m_dpi : this.getDefaultDotsPerInch();
  }

  /**
   * Get the default DPI to use
   *
   * @return the default DPI to use
   */
  protected int getDefaultDotsPerInch() {
    return 300;
  }

  /**
   * Get the quality value to use
   *
   * @return the quality value to use
   */
  protected final double getQuality() {
    return (this.m_quality >= 0d) ? this.m_quality
        : this.getDefaultQuality();
  }

  /**
   * Get the default quality value to use
   *
   * @return the default quality value to use
   */
  protected double getDefaultQuality() {
    return 0.8d;
  }

  /**
   * Get the size of the graphic
   *
   * @return the size of the graphic
   */
  protected final PhysicalDimension getSize() {
    return ((this.m_size != null) ? this.m_size : this.getDefaultSize());
  }

  /**
   * Get the default size of the graphic
   *
   * @return the default size of the graphic
   */
  protected PhysicalDimension getDefaultSize() {
    return new PhysicalDimension(10d, 10d, ELength.CM);
  }

  /** {@inheritDoc} */
  @Override
  protected String getMainDocumentNameSuggestionBase() {
    String name;

    name = super.getMainDocumentNameSuggestionBase();
    if (this.m_size != null) {
      name += '_';
      name += this.m_size.toString();
    }
    if (this.m_model != null) {
      name += '_';
      name += this.m_model.toString();
    }
    if (this.m_dpi > 0) {
      name += '_';
      name += this.m_dpi;
      name += "dpi"; //$NON-NLS-1$
    }
    if (this.m_quality >= 0d) {
      name += '_';
      name += SimpleNumberAppender.INSTANCE.toString(this.m_quality,
          ETextCase.IN_SENTENCE);
    }
    return name;
  }

  /**
   * Create the graphic
   *
   * @return the graphic
   * @throws Throwable
   *           if graphic creation failed
   */
  protected Graphic createGraphic() throws Throwable {
    return this.getFileProducerTool().use()//
        .setBasePath(this.getDestDir())//
        .setFileProducerListener(this.getListener())//
        .setColorModel(this.getColorModel())//
        .setDotsPerInch(this.getDotsPerInch())//
        .setQuality(this.getQuality())//
        .setSize(this.getSize())//
        .setMainDocumentNameSuggestion(
            this.getMainDocumentNameSuggestion())
        .create();//
  }

  /**
   * Paint the graphic
   *
   * @param graphic
   *          the graphic to paint
   * @throws Throwable
   *           if something goes wrong
   */
  protected void paint(final Graphic graphic) throws Throwable {
    //
  }

  /** {@inheritDoc} */
  @Override
  protected final void perform() throws Throwable {
    try (final Graphic graphic = this.createGraphic()) {
      this.paint(graphic);
    }
  }
}
