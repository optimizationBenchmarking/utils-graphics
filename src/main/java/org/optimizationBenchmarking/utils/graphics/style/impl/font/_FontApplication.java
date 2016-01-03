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
   * @param graphics
   *          the graphic
   * @param font
   *          the font
   */
  _FontApplication(final Graphics graphics, final Font font) {
    super(graphics);
    this.m_oldFont = graphics.getFont();
    graphics.setFont(font);
  }

  /** {@inheritDoc} */
  @Override
  protected final void cleanUp(final Graphics graphics) {
    graphics.setFont(this.m_oldFont);
  }
}
