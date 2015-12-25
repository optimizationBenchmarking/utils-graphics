package org.optimizationBenchmarking.utils.graphics.style.impl.font;

import java.awt.Graphics;

import org.optimizationBenchmarking.utils.comparison.EComparison;
import org.optimizationBenchmarking.utils.graphics.EFontFamily;
import org.optimizationBenchmarking.utils.graphics.style.impl.StylePalette;
import org.optimizationBenchmarking.utils.graphics.style.spec.IFontPalette;
import org.optimizationBenchmarking.utils.graphics.style.spec.IFontStyle;
import org.optimizationBenchmarking.utils.hash.HashUtils;

/**
 * A palette of font styles.
 */
public class FontPalette extends StylePalette<IFontStyle>
    implements IFontPalette {

  /** the serial version uid */
  private static final long serialVersionUID = 1L;

  /** the default font */
  private final FontStyle m_default;
  /** the emph font */
  private final FontStyle m_emph;
  /** the code font */
  private final FontStyle m_code;

  /**
   * create the palette
   *
   * @param def
   *          the default font style
   * @param emph
   *          the emphasize font
   * @param code
   *          the code font style
   * @param data
   *          the palette data
   */
  protected FontPalette(final FontStyle def, final FontStyle emph,
      final FontStyle code, final FontStyle[] data) {
    super(data);

    FontPalette._checkDefaultFont(def);
    FontPalette._checkEmphFont(emph);
    FontPalette._checkCodeFont(code);

    this.m_default = def;
    def.m_palette = this;

    this.m_emph = emph;
    emph.m_palette = this;

    this.m_code = code;
    code.m_palette = this;

    for (final FontStyle f : data) {
      f.m_palette = this;
    }
  }

  /**
   * Check the default font style
   *
   * @param s
   *          the default font style
   */
  static final void _checkDefaultFont(final FontStyle s) {
    if (s == null) {
      throw new IllegalArgumentException(//
          "The default font style cannot be null."); //$NON-NLS-1$
    }
  }

  /**
   * Check the code font style
   *
   * @param s
   *          the code font style
   */
  static final void _checkCodeFont(final FontStyle s) {
    final EFontFamily f;

    if (s == null) {
      throw new IllegalArgumentException(//
          "The code font style cannot be null."); //$NON-NLS-1$
    }

    if ((f = s.getFamily()) != EFontFamily.MONOSPACED) {
      throw new IllegalArgumentException(//
          "The code font family must be " + //$NON-NLS-1$
              EFontFamily.MONOSPACED + " but is " + f);//$NON-NLS-1$

    }
  }

  /**
   * Check the emphasize font style
   *
   * @param s
   *          the code emphasize style
   */
  static final void _checkEmphFont(final FontStyle s) {
    if (s == null) {
      throw new IllegalArgumentException(//
          "The emphasize font style cannot be null."); //$NON-NLS-1$
    }

    if (s.getFamily() == EFontFamily.MONOSPACED) {
      throw new IllegalArgumentException(//
          "The emphasize font family must not be " + //$NON-NLS-1$
              EFontFamily.MONOSPACED + " but is.");//$NON-NLS-1$

    }
  }

  /** {@inheritDoc} */
  @Override
  public final IFontStyle getMostSimilarFont(final EFontFamily family,
      final boolean bold, final boolean italic, final boolean underlined,
      final float size) {
    float bestDiff, curDiff;
    FontStyle best;

    best = this.m_default;
    bestDiff = best._dist(family, bold, italic, underlined, size);
    if (bestDiff == 0) {
      return best;
    }

    curDiff = this.m_emph._dist(family, bold, italic, underlined, size);
    if (curDiff < bestDiff) {
      best = this.m_emph;
      if (curDiff <= 0) {
        return best;
      }
      bestDiff = curDiff;
    }

    curDiff = this.m_code._dist(family, bold, italic, underlined, size);
    if (curDiff < bestDiff) {
      best = this.m_code;
      if (curDiff <= 0) {
        return best;
      }
      bestDiff = curDiff;
    }

    for (final FontStyle fs : ((FontStyle[]) (this.m_data))) {
      curDiff = fs._dist(family, bold, italic, underlined, size);
      if (curDiff < bestDiff) {
        best = fs;
        if (curDiff <= 0) {
          return best;
        }
      }
    }

    return best;
  }

  /** {@inheritDoc} */
  @Override
  public final IFontStyle getDefaultFont() {
    return this.m_default;
  }

  /** {@inheritDoc} */
  @Override
  public final IFontStyle getEmphFont() {
    return this.m_emph;
  }

  /** {@inheritDoc} */
  @Override
  public final IFontStyle getCodeFont() {
    return this.m_code;
  }

  /** {@inheritDoc} */
  @Override
  public final void initializeWithDefaults(final Graphics graphics) {
    graphics.setFont(this.m_default.getFont());
  }

  /** {@inheritDoc} */
  @Override
  protected final int calcHashCode() {
    return HashUtils.combineHashes(
        HashUtils.combineHashes(super.calcHashCode(),
            HashUtils.hashCode(this.m_default)),
        HashUtils.combineHashes(HashUtils.hashCode(this.m_emph),
            HashUtils.hashCode(this.m_code)));
  }

  /** {@inheritDoc} */
  @Override
  public final boolean equals(final Object o) {
    final FontPalette other;
    if (o == this) {
      return true;
    }
    if (o instanceof FontPalette) {
      other = ((FontPalette) o);
      return (EComparison.equals(this.m_default, other.m_default) && //
          EComparison.equals(this.m_emph, other.m_emph) && //
          EComparison.equals(this.m_code, other.m_code) && //
          super.equals(o));
    }
    return false;
  }
}
