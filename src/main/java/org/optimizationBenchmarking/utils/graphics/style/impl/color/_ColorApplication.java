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
   * @param graphics
   *          the graphic
   * @param color
   *          the color
   */
  _ColorApplication(final Graphics graphics, final Color color) {
    super(graphics);
    this.m_oldColor = graphics.getColor();
    graphics.setColor(color);
  }

  /** {@inheritDoc} */
  @Override
  protected final void cleanUp(final Graphics graphics) {
    graphics.setColor(this.m_oldColor);
  }
}
