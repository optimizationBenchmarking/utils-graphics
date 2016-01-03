package org.optimizationBenchmarking.utils.graphics.style.impl.color;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import org.optimizationBenchmarking.utils.comparison.Compare;
import org.optimizationBenchmarking.utils.graphics.GraphicUtils;
import org.optimizationBenchmarking.utils.graphics.style.impl.StylePalette;
import org.optimizationBenchmarking.utils.graphics.style.spec.IColorPalette;
import org.optimizationBenchmarking.utils.graphics.style.spec.IColorStyle;
import org.optimizationBenchmarking.utils.hash.HashUtils;

/**
 * The implementation of the color palette interface.
 */
public class ColorPalette extends StylePalette<IColorStyle>
    implements IColorPalette {

  /** the serial version uid */
  private static final long serialVersionUID = 1L;

  /** the black color */
  static final ColorStyle BLACK = new ColorStyle("black", //$NON-NLS-1$
      Color.BLACK.getRGB());

  /** the white color */
  static final ColorStyle WHITE = new ColorStyle("white", //$NON-NLS-1$
      Color.WHITE.getRGB());

  /**
   * create the palette
   *
   * @param data
   *          the palette data
   */
  ColorPalette(final ColorStyle[] data) {
    super(data);

    final String black, white;
    String name;
    int i, j;

    black = ColorPalette.BLACK.getID();
    white = ColorPalette.WHITE.getID();
    for (i = data.length; (--i) > 0;) {
      name = data[i].getID();

      if (name.equalsIgnoreCase(black) || //
          name.equalsIgnoreCase(white)) {
        throw new IllegalArgumentException(//
            "The ID of the " + i + //$NON-NLS-1$
                "th color is '" + name + //$NON-NLS-1$
                "' which is not permitted."); //$NON-NLS-1$
      }

      for (j = i; (--j) >= 0;) {
        if (name.equalsIgnoreCase(data[j].getID())) {
          throw new IllegalArgumentException(//
              "No two colors in a palette can have the same ID, but ID '"//$NON-NLS-1$
                  + name + "' of the " + i + //$NON-NLS-1$
                  "th color equals ID '" + //$NON-NLS-1$
                  data[j].getID() + "' of the " + j //$NON-NLS-1$
                  + "th color.");//$NON-NLS-1$
        }
      }
    }

  }

  /** {@inheritDoc} */
  @Override
  public final IColorStyle getBlack() {
    return ColorPalette.BLACK;
  }

  /** {@inheritDoc} */
  @Override
  public final IColorStyle getWhite() {
    return ColorPalette.WHITE;
  }

  /** {@inheritDoc} */
  @Override
  public final void initializeWithDefaults(final Graphics graphics) {
    final Rectangle2D boundingRectangle;
    final Rectangle rectangle;
    final Graphics2D graphics2D;
    final int negOffset, posOffset, size;
    final Color white, black;

    white = this.getWhite().getColor();

    if (graphics instanceof Graphics2D) {
      graphics2D = ((Graphics2D) graphics);
      graphics2D.setBackground(white);
      graphics2D.setPaint(white);
    } else {
      graphics2D = null;
    }
    graphics.setColor(white);

    boundingRectangle = GraphicUtils.getBounds(graphics);

    if (graphics2D != null) {
      graphics2D.fill(boundingRectangle);
    } else {
      if (boundingRectangle instanceof Rectangle) {
        rectangle = ((Rectangle) boundingRectangle);
      } else {
        size = ((Integer.MAX_VALUE - 4) | 1);
        posOffset = (size >>> 1);
        negOffset = (-posOffset);
        rectangle = new Rectangle(//
            ((int) (Math.max(negOffset,
                Math.min(posOffset, boundingRectangle.getX())))), //
            ((int) (Math.max(negOffset,
                Math.min(posOffset, boundingRectangle.getY())))), //
            ((int) (Math.max(0,
                Math.min(Integer.MAX_VALUE,
                    boundingRectangle.getWidth())))), //
            ((int) (Math.max(0, Math.min(Integer.MAX_VALUE,
                boundingRectangle.getHeight())))));
      }
      graphics.fillRect(rectangle.x, rectangle.y, rectangle.width,
          rectangle.height);
    }

    black = this.getBlack().getColor();
    graphics.setColor(black);
    if (graphics2D != null) {
      graphics2D.setPaint(black);
    }
  }

  /**
   * get the rgb difference
   *
   * @param a
   *          the first rgb value
   * @param b
   *          the second rgb value
   * @return the difference
   */
  private static final int __rgbDiff(final int a, final int b) {
    int s, x;

    x = ((a & 0xff) - (b & 0xff));
    s = (x * x);

    x = (((a >>> 8) & 0xff) - ((b >>> 8) & 0xff));
    s += (x * x);

    x = (((a >>> 16) & 0xff) - ((b >>> 16) & 0xff));
    s += (x * x);

    x = (((a >>> 24) & 0xff) - ((b >>> 24) & 0xff));

    return (s + (x * x));
  }

  /** {@inheritDoc} */
  @Override
  public final IColorStyle getMostSimilarColor(final Color color) {
    if (color == null) {
      return this.getBlack();
    }
    return this.getMostSimilarColor(color.getRGB() & 0xffffff);
  }

  /** {@inheritDoc} */
  @Override
  public final IColorStyle getMostSimilarColor(final int rgb) {
    IColorStyle best, cur;
    int bestDiff, curDiff;

    best = this.getBlack();
    finder: {
      bestDiff = ColorPalette.__rgbDiff(rgb, best.getColor().getRGB());

      if (bestDiff > 0) {
        cur = this.getWhite();
        curDiff = ColorPalette.__rgbDiff(rgb, cur.getColor().getRGB());
        if (curDiff < bestDiff) {
          best = cur;
          if (curDiff <= 0) {
            break finder;
          }
          bestDiff = curDiff;
        }

        for (final IColorStyle cs : this.m_data) {
          curDiff = ColorPalette.__rgbDiff(rgb, cs.getColor().getRGB());
          if (curDiff < bestDiff) {
            best = cs;
            if (curDiff <= 0) {
              break finder;
            }
            bestDiff = curDiff;
          }
        }

      }
    }

    return best;
  }

  /** {@inheritDoc} */
  @Override
  protected final int calcHashCode() {
    return HashUtils.combineHashes(//
        HashUtils.combineHashes(super.calcHashCode(), //
            HashUtils.hashCode(this.getWhite())), //
        HashUtils.hashCode(this.getBlack()));
  }

  /** {@inheritDoc} */
  @Override
  public final boolean equals(final Object o) {
    final ColorPalette other;
    if (o == this) {
      return true;
    }
    if (o instanceof ColorPalette) {
      other = ((ColorPalette) o);
      return (Compare.equals(this.getBlack(), other.getBlack()) && //
          Compare.equals(this.getWhite(), other.getWhite()) && //
          super.equals(o));
    }
    return false;
  }
}
