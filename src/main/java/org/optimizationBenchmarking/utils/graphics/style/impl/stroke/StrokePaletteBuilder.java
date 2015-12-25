package org.optimizationBenchmarking.utils.graphics.style.impl.stroke;

import java.util.ArrayList;

import org.optimizationBenchmarking.utils.graphics.style.impl.StylePaletteBuilder;
import org.optimizationBenchmarking.utils.graphics.style.impl.StylePaletteElementBuilder;
import org.optimizationBenchmarking.utils.graphics.style.spec.IStrokeStyle;
import org.optimizationBenchmarking.utils.hierarchy.BuilderFSM;
import org.optimizationBenchmarking.utils.text.textOutput.MemoryTextOutput;

/**
 * A builder for stroke palettes.
 */
public class StrokePaletteBuilder
    extends StylePaletteBuilder<IStrokeStyle, StrokePalette> {

  /** did we set the default stroke */
  private static final int FLAG_HAS_DEFAULT = (StylePaletteBuilder.FLAG_HAS_ELEMENTS << 1);
  /** did we set the thin stroke */
  private static final int FLAG_HAS_THIN = (StrokePaletteBuilder.FLAG_HAS_DEFAULT << 1);
  /** did we set the thick stroke */
  private static final int FLAG_HAS_FAT = (StrokePaletteBuilder.FLAG_HAS_THIN << 1);

  /** the default stroke */
  private StrokeStyle m_default;

  /** the thin stroke */
  private StrokeStyle m_thin;
  /** the thick stroke */
  private StrokeStyle m_thick;

  /** the counter */
  private int m_count;

  /** create */
  public StrokePaletteBuilder() {
    super(null);
    this.open();
  }

  /** {@inheritDoc} */
  @Override
  protected void fsmFlagsAppendName(final int flagValue,
      final int flagIndex, final MemoryTextOutput append) {
    switch (flagValue) {
      case FLAG_HAS_DEFAULT: {
        append.append("hasDefaultStroke"); //$NON-NLS-1$
        return;
      }
      case FLAG_HAS_THIN: {
        append.append("hasThinStroke"); //$NON-NLS-1$
        return;
      }
      case FLAG_HAS_FAT: {
        append.append("hasThickStroke"); //$NON-NLS-1$
        return;
      }
      default: {
        super.fsmFlagsAppendName(flagValue, flagIndex, append);
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public synchronized final StrokeStyleBuilder add() {
    return ((StrokeStyleBuilder) (super.add()));
  }

  /**
   * Set the default stroke
   *
   * @param style
   *          the default stroke
   */
  public synchronized final void setDefaultStroke(
      final StrokeStyle style) {
    this.fsmStateAssert(BuilderFSM.STATE_OPEN);
    this.fsmFlagsAssertFalse(StylePaletteBuilder.FLAG_HAS_ELEMENTS);

    StrokePalette._checkStrokes(this.m_thin, true, style, false,
        this.m_thick, true);
    this.m_default = style;
    this.fsmFlagsSet(StrokePaletteBuilder.FLAG_HAS_DEFAULT);
  }

  /**
   * Set the default stroke
   *
   * @return the builder for the default stroke
   */
  public synchronized final StrokeStyleBuilder setDefaultStroke() {
    this.fsmStateAssert(BuilderFSM.STATE_OPEN);
    this.fsmFlagsAssertFalse(StylePaletteBuilder.FLAG_HAS_ELEMENTS);
    return new StrokeStyleBuilder(this, 0, "strokeDefault"); //$NON-NLS-1$
  }

  /**
   * Set the thin stroke
   *
   * @param style
   *          the thin stroke
   */
  public synchronized final void setThinStroke(final StrokeStyle style) {
    this.fsmStateAssert(BuilderFSM.STATE_OPEN);
    this.fsmFlagsAssertFalse(StylePaletteBuilder.FLAG_HAS_ELEMENTS);
    StrokePalette._checkStrokes(style, false, this.m_default, true,
        this.m_thick, true);
    this.m_thin = style;
    this.fsmFlagsSet(StrokePaletteBuilder.FLAG_HAS_THIN);
  }

  /**
   * Set the thin stroke
   *
   * @return the builder for the thin stroke
   */
  public synchronized final StrokeStyleBuilder setThinStroke() {
    this.fsmStateAssert(BuilderFSM.STATE_OPEN);
    this.fsmFlagsAssertFalse(StylePaletteBuilder.FLAG_HAS_ELEMENTS);
    return new StrokeStyleBuilder(this, 1, "strokeThin"); //$NON-NLS-1$
  }

  /**
   * Set the thick stroke
   *
   * @param style
   *          the thick stroke
   */
  public synchronized final void setThickStroke(final StrokeStyle style) {
    this.fsmStateAssert(BuilderFSM.STATE_OPEN);
    this.fsmFlagsAssertFalse(StylePaletteBuilder.FLAG_HAS_ELEMENTS);
    StrokePalette._checkStrokes(this.m_thin, true, this.m_default, true,
        style, false);
    this.m_thick = style;
    this.fsmFlagsSet(StrokePaletteBuilder.FLAG_HAS_FAT);
  }

  /**
   * Set the thick stroke
   *
   * @return the builder for the thick stroke
   */
  public synchronized final StrokeStyleBuilder setThickStroke() {
    this.fsmStateAssert(BuilderFSM.STATE_OPEN);
    this.fsmFlagsAssertFalse(StylePaletteBuilder.FLAG_HAS_ELEMENTS);
    return new StrokeStyleBuilder(this, 2, "strokeThick"); //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  protected final StrokeStyleBuilder createElementBuilder() {
    this.fsmFlagsAssertTrue(StrokePaletteBuilder.FLAG_HAS_DEFAULT
        | StrokePaletteBuilder.FLAG_HAS_FAT
        | StrokePaletteBuilder.FLAG_HAS_THIN);
    return new StrokeStyleBuilder(this, 3,
        "stroke" + Integer.toHexString(this.m_count++));//$NON-NLS-1$
  }

  /**
   * Create the stroke palette
   *
   * @param def
   *          the default stroke
   * @param thin
   *          the thin stroke
   * @param thick
   *          the thick stroke
   * @param data
   *          the data
   * @return the palette
   */
  protected StrokePalette createPalette(final StrokeStyle def,
      final StrokeStyle thin, final StrokeStyle thick,
      final StrokeStyle[] data) {
    this.fsmFlagsAssertTrue(StrokePaletteBuilder.FLAG_HAS_DEFAULT
        | StrokePaletteBuilder.FLAG_HAS_FAT
        | StrokePaletteBuilder.FLAG_HAS_THIN
        | StylePaletteBuilder.FLAG_HAS_ELEMENTS);
    return new StrokePalette(def, thin, thick, data);
  }

  /** {@inheritDoc} */
  @Override
  protected final StrokePalette createPalette(
      final ArrayList<IStrokeStyle> data) {
    return this.createPalette(//
        this.m_default, this.m_thin, this.m_thick, //
        data.toArray(new StrokeStyle[data.size()]));
  }

  /** {@inheritDoc} */
  @Override
  protected final void processElementBuilder(
      final StylePaletteElementBuilder<IStrokeStyle> child) {
    final StrokeStyleBuilder ssb;

    ssb = ((StrokeStyleBuilder) child);
    switch (ssb.m_type) {
      case 0: {
        this.setDefaultStroke((StrokeStyle) (ssb.getResult()));
        break;
      }
      case 1: {
        this.setThinStroke((StrokeStyle) (ssb.getResult()));
        break;
      }
      case 2: {
        this.setThickStroke((StrokeStyle) (ssb.getResult()));
        break;
      }
      case 3: {
        this.add(ssb.getResult());
        break;
      }
      default: {
        throw new IllegalArgumentException(child.toString());
      }
    }
  }

}
