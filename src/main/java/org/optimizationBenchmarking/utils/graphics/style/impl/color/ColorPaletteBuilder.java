package org.optimizationBenchmarking.utils.graphics.style.impl.color;

import java.util.ArrayList;

import org.optimizationBenchmarking.utils.graphics.style.impl.StylePaletteBuilder;
import org.optimizationBenchmarking.utils.graphics.style.spec.IColorStyle;

/**
 * A builder for color palettes.
 */
public class ColorPaletteBuilder
    extends StylePaletteBuilder<IColorStyle, ColorPalette> {

  /** create */
  public ColorPaletteBuilder() {
    super(null);
    this.open();
  }

  /** {@inheritDoc} */
  @Override
  public final synchronized ColorStyleBuilder add() {
    return ((ColorStyleBuilder) (super.add()));
  }

  /** {@inheritDoc} */
  @Override
  protected final ColorStyleBuilder createElementBuilder() {
    return new ColorStyleBuilder(this);
  }

  /** {@inheritDoc} */
  @Override
  protected ColorPalette createPalette(final ArrayList<IColorStyle> data) {
    return new ColorPalette(data.toArray(new ColorStyle[data.size()]));
  }

  /** {@inheritDoc} */
  @Override
  protected final void beforeAdd(final IColorStyle element) {
    final int rgb;

    rgb = (element.getColor().getRGB() & 0xffffff);
    if ((rgb == 0) || (rgb == ColorPalette.BLACK.getRGB())) {
      throw new IllegalArgumentException(//
          "Cannot add black to the palette."); //$NON-NLS-1$
    }

    if ((rgb == 0xffffff) || (rgb == ColorPalette.WHITE.getRGB())) {
      throw new IllegalArgumentException(//
          "Cannot add white to the palette."); //$NON-NLS-1$
    }
  }
}
