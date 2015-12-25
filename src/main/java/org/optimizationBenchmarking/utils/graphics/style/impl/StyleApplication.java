package org.optimizationBenchmarking.utils.graphics.style.impl;

import java.awt.Graphics2D;

import org.optimizationBenchmarking.utils.IScope;

/** application of a style */
public class StyleApplication implements IScope {

  /** the graphic used */
  private final Graphics2D m_g;

  /**
   * the style
   *
   * @param g
   *          the graphic
   */
  protected StyleApplication(final Graphics2D g) {
    super();
    if (g == null) {
      throw new IllegalArgumentException(//
          "The graphic cannot be null."); //$NON-NLS-1$
    }
    this.m_g = g;
  }

  /**
   * clean up the graphic object
   *
   * @param g
   *          the graphic
   */
  protected void cleanUp(final Graphics2D g) {
    //
  }

  /** {@inheritDoc} */
  @Override
  public final void close() {
    this.cleanUp(this.m_g);
  }
}
