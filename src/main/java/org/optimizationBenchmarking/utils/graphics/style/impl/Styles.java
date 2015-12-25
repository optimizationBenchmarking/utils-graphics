package org.optimizationBenchmarking.utils.graphics.style.impl;

import java.awt.Graphics;

import org.optimizationBenchmarking.utils.collections.maps.StringMap;
import org.optimizationBenchmarking.utils.graphics.style.spec.IColorPalette;
import org.optimizationBenchmarking.utils.graphics.style.spec.IColorStyle;
import org.optimizationBenchmarking.utils.graphics.style.spec.IFontPalette;
import org.optimizationBenchmarking.utils.graphics.style.spec.IFontStyle;
import org.optimizationBenchmarking.utils.graphics.style.spec.IStrokePalette;
import org.optimizationBenchmarking.utils.graphics.style.spec.IStrokeStyle;
import org.optimizationBenchmarking.utils.graphics.style.spec.IStyles;

/**
 * A set of styles.
 */
public final class Styles implements IStyles {

  /** the owning style set */
  private final Styles m_owner;

  /** the style set definition */
  private final _StyleSetDefinition m_def;

  /** the used fonts */
  private int m_usedFonts;
  /** the used colors */
  private int m_usedColors;
  /** the used strokes */
  private int m_usedStrokes;
  /** the named colors */
  private StringMap<IColorStyle> m_namedColors;
  /** the named strokes */
  private StringMap<IStrokeStyle> m_namedStrokes;
  /** the named fonts */
  private StringMap<IFontStyle> m_namedFonts;

  /**
   * Create the style set
   *
   * @param fonts
   *          the font palette
   * @param strokes
   *          the stroke palette
   * @param colors
   *          the color palette
   */
  public Styles(final IFontPalette fonts, final IColorPalette colors,
      final IStrokePalette strokes) {
    this(fonts, colors, strokes, null);
  }

  /**
   * Create the style set
   *
   * @param fonts
   *          the font palette
   * @param strokes
   *          the stroke palette
   * @param colors
   *          the color palette
   * @param owner
   *          the owning style set
   */
  private Styles(final IFontPalette fonts, final IColorPalette colors,
      final IStrokePalette strokes, final Styles owner) {
    super();

    if (owner == null) {
      this.m_def = new _StyleSetDefinition(fonts, colors, strokes);
    } else {
      this.m_def = owner.m_def;
    }

    this.m_owner = owner;
  }

  /**
   * Create a style set from an owning style set
   *
   * @param owner
   *          the owning style set
   */
  public Styles(final Styles owner) {
    this(null, null, null, owner);
    this.m_usedColors = owner.m_usedColors;
    this.m_usedFonts = owner.m_usedFonts;
    this.m_usedStrokes = owner.m_usedStrokes;
  }

  /** {@inheritDoc} */
  @Override
  public synchronized final IFontStyle allocateFont() {
    final int index;
    index = this.m_usedFonts;
    this.m_usedFonts = ((index + 1) % this.m_def.m_fontsSize);
    return this.m_def.m_fonts.get(index);
  }

  /** {@inheritDoc} */
  @Override
  public synchronized final IColorStyle allocateColor() {
    final int index;

    index = this.m_usedColors;
    this.m_usedColors = ((index + 1) % this.m_def.m_colorsSize);
    return this.m_def.m_colors.get(index);
  }

  /** {@inheritDoc} */
  @Override
  public synchronized final IStrokeStyle allocateStroke() {
    final int index;
    index = this.m_usedStrokes;
    this.m_usedStrokes = ((index + 1) % this.m_def.m_strokesSize);
    return this.m_def.m_strokes.get(index);
  }

  /**
   * Obtain the color of a given name. If {@code allocateIfUndefined} is
   * {@code true} and no color is defined for the given name yet, then one
   * will be allocated. If {@code allocateIfUndefined} is {@code false} and
   * no color is defined for the given name yet, {@code null} will be
   * returned.
   *
   * @param name
   *          the name
   * @param allocateIfUndefined
   *          should the color be allocated if it is still undefined?
   * @return the color for the given name, or {@code null} if none was
   *         allocated for the name and {@code allocateIfUndefined} is
   *         {@code false}
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public synchronized final IColorStyle getColor(final String name,
      final boolean allocateIfUndefined) {
    IColorStyle color;
    Styles owner;

    if (this.m_namedColors == null) {
      findMapToCopy: {
        for (owner = this.m_owner; (owner != null); owner = owner.m_owner) {
          synchronized (owner) {
            if (owner.m_namedColors != null) {
              if (allocateIfUndefined) {
                this.m_namedColors = ((StringMap) (//
                owner.m_namedColors.clone()));
                break findMapToCopy;
              }

              return owner.m_namedColors.get(name);
            }
          }
        }
        if (!allocateIfUndefined) {
          return null;
        }
        this.m_namedColors = new StringMap<>();
      }
    }

    color = this.m_namedColors.get(name);
    if (color != null) {
      return color;
    }

    if (allocateIfUndefined) {
      color = this.allocateColor();
      this.m_namedColors.put(name, color);
      return color;
    }

    return null;
  }

  /**
   * Obtain the font of a given name. If {@code allocateIfUndefined} is
   * {@code true} and no font is defined for the given name yet, then one
   * will be allocated. If {@code allocateIfUndefined} is {@code false} and
   * no font is defined for the given name yet, {@code null} will be
   * returned.
   *
   * @param name
   *          the name
   * @param allocateIfUndefined
   *          should the font be allocated if it is still undefined?
   * @return the font for the given name, or {@code null} if none was
   *         allocated for the name and {@code allocateIfUndefined} is
   *         {@code false}
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public synchronized final IFontStyle getFont(final String name,
      final boolean allocateIfUndefined) {
    IFontStyle font;
    Styles owner;

    if (this.m_namedFonts == null) {
      findMapToCopy: {
        for (owner = this.m_owner; (owner != null); owner = owner.m_owner) {
          synchronized (owner) {
            if (owner.m_namedFonts != null) {
              if (allocateIfUndefined) {
                this.m_namedFonts = ((StringMap) (//
                owner.m_namedFonts.clone()));
                break findMapToCopy;
              }

              return owner.m_namedFonts.get(name);
            }
          }
        }
        if (!allocateIfUndefined) {
          return null;
        }
        this.m_namedFonts = new StringMap<>();
      }
    }

    font = this.m_namedFonts.get(name);
    if (font != null) {
      return font;
    }

    if (allocateIfUndefined) {
      font = this.allocateFont();
      this.m_namedFonts.put(name, font);
      return font;
    }

    return null;
  }

  /**
   * Obtain the stroke of a given name. If {@code allocateIfUndefined} is
   * {@code true} and no stroke is defined for the given name yet, then one
   * will be allocated. If {@code allocateIfUndefined} is {@code false} and
   * no stroke is defined for the given name yet, {@code null} will be
   * returned.
   *
   * @param name
   *          the name
   * @param allocateIfUndefined
   *          should the stroke be allocated if it is still undefined?
   * @return the stroke for the given name, or {@code null} if none was
   *         allocated for the name and {@code allocateIfUndefined} is
   *         {@code false}
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public synchronized final IStrokeStyle getStroke(final String name,
      final boolean allocateIfUndefined) {
    IStrokeStyle stroke;
    Styles owner;

    if (this.m_namedStrokes == null) {
      findMapToCopy: {
        for (owner = this.m_owner; (owner != null); owner = owner.m_owner) {
          synchronized (owner) {
            if (owner.m_namedStrokes != null) {
              if (allocateIfUndefined) {
                this.m_namedStrokes = ((StringMap) (//
                owner.m_namedStrokes.clone()));
                break findMapToCopy;
              }

              return owner.m_namedStrokes.get(name);
            }
          }
        }
        if (!allocateIfUndefined) {
          return null;
        }
        this.m_namedStrokes = new StringMap<>();
      }
    }

    stroke = this.m_namedStrokes.get(name);
    if (stroke != null) {
      return stroke;
    }

    if (allocateIfUndefined) {
      stroke = this.allocateStroke();
      this.m_namedStrokes.put(name, stroke);
      return stroke;
    }

    return null;
  }

  /** {@inheritDoc} */
  @Override
  public final IStrokeStyle getDefaultStroke() {
    return this.m_def.m_strokes.getDefaultStroke();
  }

  /** {@inheritDoc} */
  @Override
  public final IStrokeStyle getThinStroke() {
    return this.m_def.m_strokes.getThinStroke();
  }

  /** {@inheritDoc} */
  @Override
  public final IStrokeStyle getThickStroke() {
    return this.m_def.m_strokes.getThickStroke();
  }

  /** {@inheritDoc} */
  @Override
  public final IFontStyle getDefaultFont() {
    return this.m_def.m_fonts.getDefaultFont();
  }

  /** {@inheritDoc} */
  @Override
  public final IFontStyle getEmphFont() {
    return this.m_def.m_fonts.getEmphFont();
  }

  /** {@inheritDoc} */
  @Override
  public final IFontStyle getCodeFont() {
    return this.m_def.m_fonts.getCodeFont();
  }

  /** {@inheritDoc} */
  @Override
  public final IColorStyle getBlack() {
    return this.m_def.m_colors.getBlack();
  }

  /** {@inheritDoc} */
  @Override
  public final IColorStyle getWhite() {
    return this.m_def.m_colors.getWhite();
  }

  /** {@inheritDoc} */
  @Override
  public final void initializeWithDefaults(final Graphics graphics) {
    this.m_def.m_fonts.initializeWithDefaults(graphics);
    this.m_def.m_colors.initializeWithDefaults(graphics);
    this.m_def.m_strokes.initializeWithDefaults(graphics);
  }
}
