package org.optimizationBenchmarking.utils.graphics.style.impl.color;

import java.awt.Color;

/**
 * The minimum default palette of Java, supporting all the colors defined
 * in {@linkplain java.awt.Color}.
 */
public final class JavaDefaultColorPalette extends ColorPalette {

  /** the serial version uid */
  private static final long serialVersionUID = 1L;

  /** the light-gray color */
  private static final ColorStyle LIGHT_GRAY = new ColorStyle("light gray", //$NON-NLS-1$
      Color.LIGHT_GRAY.getRGB());

  /** the gray color */
  private static final ColorStyle GRAY = new ColorStyle("gray", //$NON-NLS-1$
      Color.GRAY.getRGB());

  /** the dark-gray color */
  private static final ColorStyle DARK_GRAY = new ColorStyle("dark gray", //$NON-NLS-1$
      Color.DARK_GRAY.getRGB());

  /** the red color */
  private static final ColorStyle RED = new ColorStyle("red", //$NON-NLS-1$
      Color.RED.getRGB());

  /** the pink color */
  private static final ColorStyle PINK = new ColorStyle("pink", //$NON-NLS-1$
      Color.PINK.getRGB());

  /** the orange */
  private static final ColorStyle ORANGE = new ColorStyle("orange", //$NON-NLS-1$
      Color.ORANGE.getRGB());

  /** the yellow */
  private static final ColorStyle YELLOW = new ColorStyle("yellow", //$NON-NLS-1$
      Color.YELLOW.getRGB());

  /** the green */
  private static final ColorStyle GREEN = new ColorStyle("green", //$NON-NLS-1$
      Color.GREEN.getRGB());

  /** the magenta */
  private static final ColorStyle MAGENTA = new ColorStyle("magenta", //$NON-NLS-1$
      Color.MAGENTA.getRGB());

  /** the cyan */
  private static final ColorStyle CYAN = new ColorStyle("cyan", //$NON-NLS-1$
      Color.CYAN.getRGB());

  /** the blue */
  private static final ColorStyle BLUE = new ColorStyle("blue", //$NON-NLS-1$
      Color.BLUE.getRGB());

  /** the globally shared instance of the default palette */
  private static final JavaDefaultColorPalette INSTANCE = new JavaDefaultColorPalette();

  /** instantiate */
  private JavaDefaultColorPalette() {
    super(new ColorStyle[] { JavaDefaultColorPalette.RED,
        JavaDefaultColorPalette.BLUE, JavaDefaultColorPalette.MAGENTA,
        JavaDefaultColorPalette.GREEN, JavaDefaultColorPalette.PINK,
        JavaDefaultColorPalette.CYAN, JavaDefaultColorPalette.GRAY,
        JavaDefaultColorPalette.ORANGE, JavaDefaultColorPalette.DARK_GRAY,
        JavaDefaultColorPalette.YELLOW,
        JavaDefaultColorPalette.LIGHT_GRAY, });
  }

  /**
   * Get an instance of the default java color palette
   *
   * @return the default java color palette
   */
  public static final JavaDefaultColorPalette getInstance() {
    return JavaDefaultColorPalette.INSTANCE;
  }

  /**
   * read resolve
   *
   * @return {@link #getInstance()}
   */
  private final Object readResolve() {
    return JavaDefaultColorPalette.getInstance();
  }

  /**
   * write replace
   *
   * @return {@link #getInstance()}
   */
  private final Object writeReplace() {
    return JavaDefaultColorPalette.getInstance();
  }

}
