package org.optimizationBenchmarking.utils.graphics.style.impl;

import java.util.Iterator;

import org.optimizationBenchmarking.utils.graphics.style.spec.IStyle;
import org.optimizationBenchmarking.utils.hierarchy.BuilderFSM;

/**
 * A builder for a palette element.
 *
 * @param <ET>
 *          the element type
 */
public abstract class StylePaletteElementBuilder<ET extends IStyle>
    extends BuilderFSM<ET> {

  /**
   * Create the palette element builder
   *
   * @param owner
   *          the owner
   */
  protected StylePaletteElementBuilder(
      final StylePaletteBuilder<ET, ? extends StylePalette<ET>> owner) {
    super(owner);
  }

  /**
   * load a string
   *
   * @param strings
   *          the strings
   */
  public abstract void fromStrings(final Iterator<String> strings);
}
