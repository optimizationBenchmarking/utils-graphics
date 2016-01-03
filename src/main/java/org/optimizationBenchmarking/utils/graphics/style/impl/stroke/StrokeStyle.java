package org.optimizationBenchmarking.utils.graphics.style.impl.stroke;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Stroke;

import org.optimizationBenchmarking.utils.graphics.GraphicUtils;
import org.optimizationBenchmarking.utils.graphics.style.impl.StyleApplication;
import org.optimizationBenchmarking.utils.graphics.style.spec.IStrokeStyle;
import org.optimizationBenchmarking.utils.text.ETextCase;
import org.optimizationBenchmarking.utils.text.TextUtils;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/**
 * A stroke style.
 */
public final class StrokeStyle extends BasicStroke
    implements IStrokeStyle {

  /** the name of the stroke */
  private final String m_name;

  /** is this a default stroke? */
  boolean m_isDefault;

  /** the id */
  private final String m_id;

  /**
   * Constructs a new {@code StrokeStyle} with the specified attributes.
   *
   * @param width
   *          the width of this {@code StrokeStyle}. The width must be
   *          greater than or equal to {@code 0.0f}. If width is set to
   *          {@code 0.0f}, the stroke is rendered as the thinnest possible
   *          line for the target device and the antialias hint setting.
   * @param dash
   *          the array representing the dashing pattern
   * @param name
   *          the name of the style
   * @param id
   *          the id
   */
  StrokeStyle(final float width, final float dash[], final String name,
      final String id) {
    this(width, GraphicUtils.getDefaultStrokeEndCap(),
        GraphicUtils.getDefaultStrokeJoin(),
        GraphicUtils.getDefaultStrokeMiterLimit(width), dash, 0f, name,
        id);
  }

  /**
   * Constructs a new {@code StrokeStyle} with the specified attributes.
   *
   * @param width
   *          the width of this {@code StrokeStyle}. The width must be
   *          greater than or equal to {@code 0.0f}. If width is set to
   *          {@code 0.0f}, the stroke is rendered as the thinnest possible
   *          line for the target device and the antialias hint setting.
   * @param cap
   *          the decoration of the ends of a {@code StrokeStyle}
   * @param join
   *          the decoration applied where path segments meet
   * @param miterLimit
   *          the limit to trim the miter join. The miterlimit must be
   *          greater than or equal to {@code 1.0f}.
   * @param dash
   *          the array representing the dashing pattern
   * @param dashPhase
   *          the offset to start the dashing pattern
   * @param name
   *          the name of the style
   * @param id
   *          the unique id
   */
  public StrokeStyle(final float width, final int cap, final int join,
      final float miterLimit, final float dash[], final float dashPhase,
      final String name, final String id) {
    super(width, cap, join, miterLimit, dash, dashPhase);

    this.m_name = TextUtils.normalize(name);
    if (this.m_name == null) {
      throw new IllegalArgumentException(//
          "Stroke name must not be empty or null, but is '" + //$NON-NLS-1$
              name + "'.");//$NON-NLS-1$
    }

    this.m_id = TextUtils.prepare(id);
    if (this.m_id == null) {
      throw new IllegalArgumentException(//
          "Stroke id must not be empty or null, but is '" + //$NON-NLS-1$
              id + "'.");//$NON-NLS-1$
    }
  }

  /**
   * Get the name of this stroke style
   *
   * @return the name of this stroke style
   */
  @Override
  public final String toString() {
    return this.m_name;
  }

  /** {@inheritDoc} */
  @Override
  public final StyleApplication applyTo(final Graphics graphics) {
    return new _StrokeApplication(graphics, this);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean appendDescription(final ETextCase textCase,
      final ITextOutput dest, final boolean omitDefaults) {
    final ETextCase t;

    if (omitDefaults && this.m_isDefault) {
      return false;
    }

    t = ((textCase == null) ? ETextCase.IN_SENTENCE : textCase);
    t.appendWords(this.m_name, dest);
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public final String getID() {
    return this.m_id;
  }

  /** {@inheritDoc} */
  @Override
  public final Stroke getStroke() {
    return this;
  }
}
