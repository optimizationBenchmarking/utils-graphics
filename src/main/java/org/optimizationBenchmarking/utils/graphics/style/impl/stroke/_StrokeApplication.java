package org.optimizationBenchmarking.utils.graphics.style.impl.stroke;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import org.optimizationBenchmarking.utils.graphics.style.impl.StyleApplication;

/** the stroke application */
final class _StrokeApplication extends StyleApplication {

  /** the stroke */
  private final Stroke m_oldStroke;

  /**
   * the style
   *
   * @param graphics
   *          the graphic
   * @param stroke
   *          the stroke
   */
  _StrokeApplication(final Graphics graphics, final Stroke stroke) {
    super(graphics);

    final Graphics2D g2d;

    if (graphics instanceof Graphics2D) {
      g2d = ((Graphics2D) graphics);
      this.m_oldStroke = g2d.getStroke();
      g2d.setStroke(stroke);
    } else {
      this.m_oldStroke = null;
    }
  }

  /** {@inheritDoc} */
  @Override
  protected final void cleanUp(final Graphics graphics) {
    if ((this.m_oldStroke != null) && (graphics instanceof Graphics2D)) {
      ((Graphics2D) graphics).setStroke(this.m_oldStroke);
    }
  }
}
