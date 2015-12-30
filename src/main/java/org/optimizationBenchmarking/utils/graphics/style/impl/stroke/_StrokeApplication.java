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
   * @param g
   *          the graphic
   * @param c
   *          the stroke
   */
  _StrokeApplication(final Graphics g, final Stroke c) {
    super(g);

    final Graphics2D g2d;

    if (g instanceof Graphics2D) {
      g2d = ((Graphics2D) g);
      this.m_oldStroke = g2d.getStroke();
      g2d.setStroke(c);
    } else {
      this.m_oldStroke = null;
    }
  }

  /** {@inheritDoc} */
  @Override
  protected final void cleanUp(final Graphics g) {
    if ((this.m_oldStroke != null) && (g instanceof Graphics2D)) {
      ((Graphics2D) g).setStroke(this.m_oldStroke);
    }
  }
}
