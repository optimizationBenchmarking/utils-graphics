package org.optimizationBenchmarking.utils.graphics.style.impl;

import java.awt.Graphics;

import org.optimizationBenchmarking.utils.IScope;

/**
 * A scoped application of a style. In their constructor, sub-classes will
 * setup the provided {@link java.awt.Graphics} with their style-specific
 * settings after storing the corresponding current setup. In the
 * {@link #cleanUp(Graphics)} method, they restore of saved state.
 */
public abstract class StyleApplication implements IScope {

  /** the graphic used */
  private final Graphics m_graphics;

  /** has the {@link #close()} method already been called? */
  private boolean m_closed;

  /**
   * the style
   *
   * @param graphics
   *          the graphics
   */
  protected StyleApplication(final Graphics graphics) {
    super();
    if (graphics == null) {
      throw new IllegalArgumentException(//
          "The graphics cannot be null."); //$NON-NLS-1$
    }
    this.m_graphics = graphics;
  }

  /**
   * clean up the graphic object
   *
   * @param graphics
   *          the graphics
   */
  protected void cleanUp(final Graphics graphics) {
    // nothing
  }

  /** {@inheritDoc} */
  @Override
  public final void close() {
    final boolean done;
    done = this.m_closed;
    this.m_closed = true;
    if (!done) {
      this.cleanUp(this.m_graphics);
    }
  }
}
