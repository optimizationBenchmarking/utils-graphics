package org.optimizationBenchmarking.utils.graphics.style.impl.color;

import java.awt.Color;
import java.awt.Graphics;

import org.optimizationBenchmarking.utils.graphics.style.impl.StyleApplication;

/** the color application */
final class _ColorApplication extends StyleApplication {

  /** the color */
  private final Color m_oldColor;

  /**
   * the style
   *
   * @param g
   *          the graphic
   * @param c
   *          the color
   */
  _ColorApplication(final Graphics g, final Color c) {
    super(g);
    this.m_oldColor = g.getColor();
    g.setColor(c);
  }

  /** {@inheritDoc} */
  @Override
  protected final void cleanUp(final Graphics g) {
    g.setColor(this.m_oldColor);
  }
}
