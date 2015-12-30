package org.optimizationBenchmarking.utils.graphics.style.impl.font;

import java.awt.Font;
import java.awt.Graphics;

import org.optimizationBenchmarking.utils.graphics.style.impl.StyleApplication;

/** the font application */
final class _FontApplication extends StyleApplication {

  /** the font */
  private final Font m_oldFont;

  /**
   * the style
   *
   * @param g
   *          the graphic
   * @param c
   *          the font
   */
  _FontApplication(final Graphics g, final Font c) {
    super(g);
    this.m_oldFont = g.getFont();
    g.setFont(c);
  }

  /** {@inheritDoc} */
  @Override
  protected final void cleanUp(final Graphics g) {
    g.setFont(this.m_oldFont);
  }
}
