package org.optimizationBenchmarking.utils.graphics.style.impl;

import org.optimizationBenchmarking.utils.error.ErrorUtils;
import org.optimizationBenchmarking.utils.graphics.EColorModel;
import org.optimizationBenchmarking.utils.graphics.style.impl.color.DefaultColorPalette;
import org.optimizationBenchmarking.utils.graphics.style.impl.color.DefaultGrayPalette;
import org.optimizationBenchmarking.utils.graphics.style.impl.stroke.DefaultStrokePalette;
import org.optimizationBenchmarking.utils.graphics.style.spec.IColorPalette;
import org.optimizationBenchmarking.utils.graphics.style.spec.IFontPalette;
import org.optimizationBenchmarking.utils.graphics.style.spec.IStrokePalette;

/** Accessors for default palettes. */
public final class DefaultPalettes {

  /** the forbidden constructor */
  private DefaultPalettes() {
    ErrorUtils.doNotCall();
  }

  /**
   * Get the default color palette for a given color model
   *
   * @param colorModel
   *          the color model
   * @return the default palette for this model
   */
  public static final IColorPalette getDefaultColorPalette(
      final EColorModel colorModel) {
    if ((colorModel != null) && (!(colorModel.isColor()))) {
      return DefaultGrayPalette.getInstance();
    }
    return DefaultColorPalette.getInstance();
  }

  /**
   * Get the default stroke palette
   *
   * @return the default stroke palette
   */
  public static final IStrokePalette getDefaultStrokePalette() {
    return DefaultStrokePalette.getInstance();
  }

  /**
   * Create a default style set basec on a font palette and defaults.
   *
   * @param fontPalette
   *          the font palette
   * @param colorModel
   *          the color model
   * @return the default palette for this model
   */
  public static final Styles createDefaultStyles(
      final IFontPalette fontPalette, final EColorModel colorModel) {
    return new Styles(fontPalette, //
        DefaultPalettes.getDefaultColorPalette(colorModel), //
        DefaultPalettes.getDefaultStrokePalette());
  }
}
