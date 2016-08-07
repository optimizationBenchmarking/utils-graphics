package org.optimizationBenchmarking.utils.graphics.graphic.impl.abstr;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.GlyphVector;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.text.AttributedCharacterIterator;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.optimizationBenchmarking.utils.IScope;
import org.optimizationBenchmarking.utils.collections.ImmutableAssociation;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.collections.lists.ArraySetView;
import org.optimizationBenchmarking.utils.error.ErrorUtils;
import org.optimizationBenchmarking.utils.error.RethrowMode;
import org.optimizationBenchmarking.utils.graphics.GraphicUtils;
import org.optimizationBenchmarking.utils.graphics.graphic.impl.EGraphicFormat;
import org.optimizationBenchmarking.utils.graphics.graphic.spec.Graphic;
import org.optimizationBenchmarking.utils.io.encoding.StreamEncoding;
import org.optimizationBenchmarking.utils.text.TextUtils;
import org.optimizationBenchmarking.utils.tools.spec.IFileProducerListener;

/**
 * <p>
 * An abstract {@link java.awt.Graphics2D} implementation designed as
 * output context for graphics. Each graphic is set up so that 1 logical
 * unit equals
 * <code>1{@link org.optimizationBenchmarking.utils.units.ELength#POINT}</code>
 * .
 * </p>
 * <p>
 * A graphic can be implemented in form of a
 * {@link org.optimizationBenchmarking.utils.graphics.graphic.impl.abstr.GraphicProxy}
 * which forwards the calls to some library. The whole underlying
 * implementation becomes transparent if an implementation of
 * {@link org.optimizationBenchmarking.utils.graphics.graphic.spec.IGraphicDriver}
 * is used to create such wrappers.
 * </p>
 * <p>
 * Inspired by the
 * <a href="http://java.freehep.org/vectorgraphics">FreeHEP</a> library,
 * this class also provides {@code double}-precision routines which, by
 * default, try to reasonable map to the {@code int}-precision routines of
 * {@link java.awt.Graphics2D}. However, if an underlying device supports
 * high-precision output (like the
 * {@link org.optimizationBenchmarking.utils.graphics.graphic.impl.freeHEP}
 * freeHEP drivers} do), these routines may map to something better.
 * </p>
 */
public abstract class AbstractGraphic extends Graphic {

  /** the font attributes */
  private static final Map<TextAttribute, Object> FONT_ATTRIBUTES;

  static {
    FONT_ATTRIBUTES = new HashMap<>();
    AbstractGraphic.FONT_ATTRIBUTES.put(TextAttribute.KERNING,
        TextAttribute.KERNING_ON);
    AbstractGraphic.FONT_ATTRIBUTES.put(TextAttribute.LIGATURES,
        TextAttribute.LIGATURES_ON);
  }

  /** before an outline: {@value} */
  protected static final int BEFORE_OUTLINE = 1;
  /** before a fill: {@value} */
  protected static final int BEFORE_FILL = (AbstractGraphic.BEFORE_OUTLINE << 1);
  /** before something is cleared: {@value} */
  protected static final int BEFORE_CLEAR = (AbstractGraphic.BEFORE_FILL << 1);
  /** before an image is rendered: {@value} */
  protected static final int BEFORE_IMAGE = (AbstractGraphic.BEFORE_CLEAR << 1);
  /** before a 3d-rectangle is rendered: {@value} */
  protected static final int BEFORE_3D_RECT = (AbstractGraphic.BEFORE_IMAGE << 1);
  /** before a text is rendered: {@value} */
  protected static final int BEFORE_TEXT = (AbstractGraphic.BEFORE_3D_RECT << 1);
  /** before a graphic is rendered: {@value} */
  protected static final int BEFORE_CREATE_GRAPHIC = (AbstractGraphic.BEFORE_TEXT << 1);
  /** before an area is copied: {@value} */
  protected static final int BEFORE_COPY_AREA = (AbstractGraphic.BEFORE_CREATE_GRAPHIC << 1);
  /** the composite has changed: {@value} */
  protected static final int BEFORE_CHANGE_COMPOSITE = (AbstractGraphic.BEFORE_COPY_AREA << 1);
  /** the paint has changed: {@value} */
  protected static final int BEFORE_CHANGE_PAINT = (AbstractGraphic.BEFORE_CHANGE_COMPOSITE << 1);
  /** the color has changed: {@value} */
  protected static final int BEFORE_CHANGE_COLOR = (AbstractGraphic.BEFORE_CHANGE_PAINT << 1);
  /** the background color has changed: {@value} */
  protected static final int BEFORE_CHANGE_BACKGROUND_COLOR = (AbstractGraphic.BEFORE_CHANGE_COLOR << 1);
  /** the stroke has changed: {@value} */
  protected static final int BEFORE_CHANGE_STROKE = (AbstractGraphic.BEFORE_CHANGE_BACKGROUND_COLOR << 1);
  /** the font has changed: {@value} */
  protected static final int BEFORE_CHANGE_FONT = (AbstractGraphic.BEFORE_CHANGE_STROKE << 1);
  /** the rendering hits have changed: {@value} */
  protected static final int BEFORE_CHANGE_RENDERING_HINTS = (AbstractGraphic.BEFORE_CHANGE_FONT << 1);
  /** the transformation has changed: {@value} */
  protected static final int BEFORE_CHANGE_TRANSFORMATION = (AbstractGraphic.BEFORE_CHANGE_RENDERING_HINTS << 1);
  /** the clip has changed: {@value} */
  protected static final int BEFORE_CHANGE_CLIP = (AbstractGraphic.BEFORE_CHANGE_TRANSFORMATION << 1);
  /** the paint or xor mode has changed: {@value} */
  protected static final int BEFORE_CHANGE_PAINT_MODE = (AbstractGraphic.BEFORE_CHANGE_CLIP << 1);

  /** the full angle */
  private static final double FULL_ANGLE = (Math.PI + Math.PI);

  /** has we been closed ? */
  private volatile int m_state;

  /** the logger */
  private final Logger m_log;
  /**
   * the object to notify when we are closed, or {@code null} if none needs
   * to be notified
   */
  private final IFileProducerListener m_listener;

  /** the graphic path to which the graphic is written */
  private final Path m_path;

  /**
   * instantiate
   *
   * @param listener
   *          the object to notify when we are closed, or {@code null} if
   *          none needs to be notified
   * @param path
   *          the path associated with this object, or {@code null} if no
   *          file needs to be explicitly created
   * @param logger
   *          the logger
   */
  protected AbstractGraphic(final Logger logger,
      final IFileProducerListener listener, final Path path) {
    super();
    this.m_log = logger;
    this.m_path = path;
    this.m_listener = listener;
  }

  /**
   * The path associated with this graphic, or {@code null} if the graphic
   * is not associated with any path, e.g., if it is a Proxy graphic
   *
   * @return the graphic path
   */
  protected final Path getPath() {
    return this.m_path;
  }

  /**
   * Get the logger
   *
   * @return the logger
   */
  protected final Logger getLogger() {
    return this.m_log;
  }

  /**
   * Allow the automatic conversion to integer data types.
   *
   * @return {@code true} if the graphic should try to convert floating
   *         point coordinates to {@code int} as often as possible
   */
  protected boolean autoConvertCoordinatesToInt() {
    return true;
  }

  /** check the state of the graphic */
  protected final void checkClosed() {
    if (this.m_state > 1) {
      throw new IllegalStateException("Graphic "//$NON-NLS-1$
          + this.__name() + //
          " has already been closed."); //$NON-NLS-1$
    }
  }

  /** {@inheritDoc} */
  @Override
  public Rectangle2D getBounds() {
    return GraphicUtils.getBounds(this, false);
  }

  /**
   * Get the graphics format to which this graphic belongs
   *
   * @return the graphics format to which this graphic belongs
   */
  public abstract EGraphicFormat getGraphicFormat();

  /**
   * Close this graphic object. This method will be called one time by
   * {@link #close()}.
   */
  protected void onClose() {
    //
  }

  /**
   * get this graphic's id to a text output
   *
   * @return the name
   */
  private final String __name() {
    return (((((((((TextUtils.className(this)) + //
        '#') + System.identityHashCode(this)) + ' ') + '(') + //
        this.getGraphicFormat()) + '@') + this.m_path) + ')');
  }

  /** {@inheritDoc} */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public synchronized final void close() {
    final ArrayListView v;
    String s;

    if (this.m_state > 0) {
      return;
    }
    this.m_state = 1;

    s = null;
    if ((this.m_log != null) && (this.m_log.isLoggable(Level.FINEST))) {
      s = this.__name();
      this.m_log.finest("Now closing " + s); //$NON-NLS-1$
    }

    try {
      try {
        this.onClose();
      } finally {
        this.m_state = 2;
      }

      if (this.m_listener != null) {
        if (this.m_path != null) {
          v = new ArrayListView<>(
              new ImmutableAssociation[] { new ImmutableAssociation(
                  this.m_path, this.getGraphicFormat()) },
              false);
        } else {
          v = ArraySetView.EMPTY_SET_VIEW;
        }

        this.m_listener.onFilesFinalized(v);
      }
    } catch (final Throwable t) {
      if (s == null) {
        s = this.__name();
      }

      ErrorUtils.logError(this.m_log, ("Error when closing " //$NON-NLS-1$
          + s), t, false, RethrowMode.AS_RUNTIME_EXCEPTION);
    }

    if ((this.m_log != null) && (this.m_log.isLoggable(Level.FINEST))) {
      if (s == null) {
        s = this.__name();
      }
      this.m_log.finest(s + " closed."); //$NON-NLS-1$
    }
  }

  // the methods called before specific groups of modifications are applied

  /**
   * This method is invoked before something happens
   *
   * @param what
   *          what is going to happen?
   */
  protected void before(final int what) {
    this.checkClosed();
  }

  // basic Graphics2D functionality

  /**
   * Draw a 3-dimensional rectangle
   *
   * @param x
   *          the x-coordinate of the origin
   * @param y
   *          the y-coordinate of the origin
   * @param width
   *          the width
   * @param height
   *          the height
   * @param raised
   *          is the rectangle raised?
   */
  protected void doDraw3DRect(final int x, final int y, final int width,
      final int height, final boolean raised) {
    super.draw3DRect(x, y, width, height, raised);
  }

  /** {@inheritDoc} */
  @Override
  public final void draw3DRect(final int x, final int y, final int width,
      final int height, final boolean raised) {
    this.before(
        AbstractGraphic.BEFORE_3D_RECT | AbstractGraphic.BEFORE_OUTLINE);
    this.doDraw3DRect(x, y, width, height, raised);
  }

  /**
   * Fill a 3-dimensional rectangle
   *
   * @param x
   *          the x-coordinate of the origin
   * @param y
   *          the y-coordinate of the origin
   * @param width
   *          the width
   * @param height
   *          the height
   * @param raised
   *          is the rectangle raised?
   */
  protected void doFill3DRect(final int x, final int y, final int width,
      final int height, final boolean raised) {
    super.fill3DRect(x, y, width, height, raised);
  }

  /** {@inheritDoc} */
  @Override
  public final void fill3DRect(final int x, final int y, final int width,
      final int height, final boolean raised) {
    if ((width != 0) && (height != 0)) {
      this.before(
          AbstractGraphic.BEFORE_FILL | AbstractGraphic.BEFORE_3D_RECT);
      this.doFill3DRect(x, y, width, height, raised);
    }
  }

  /**
   * Draw a given shape.
   *
   * @param s
   *          the shape
   */
  protected abstract void doDraw(final Shape s);

  /** {@inheritDoc} */
  @Override
  public final void draw(final Shape s) {
    final Rectangle2D rect;
    final Line2D line;
    final Rectangle2D rectangle;
    final Point2D point;
    final Polygon poly;
    final RoundRectangle2D round;
    final Ellipse2D ellipse;

    if (s != null) {

      if (s instanceof Line2D) {
        line = ((Line2D) s);
        this.drawLine(line.getX1(), line.getY1(), line.getX2(),
            line.getY2());
        return;
      }

      if (s instanceof Rectangle2D) {
        rectangle = ((Rectangle2D) s);
        this.drawRect(rectangle.getX(), rectangle.getY(),
            rectangle.getWidth(), rectangle.getHeight());
        return;
      }

      if (s instanceof Polygon) {
        poly = ((Polygon) s);
        this.drawPolygon(poly.xpoints, poly.ypoints, poly.npoints);
        return;
      }

      if (s instanceof RoundRectangle2D) {
        round = ((RoundRectangle2D) s);
        this.drawRoundRect(round.getX(), round.getY(), round.getWidth(),
            round.getHeight(), round.getArcWidth(), round.getArcHeight());
        return;
      }

      if (s instanceof Point2D) {
        point = ((Point2D) s);
        this.drawLine(point.getX(), point.getY(), point.getX(),
            point.getY());
        return;
      }

      if (s instanceof Ellipse2D) {
        ellipse = ((Ellipse2D) s);
        if ((ellipse.getWidth() == 0d) || (ellipse.getHeight() == 0d)) {
          this.drawLine(ellipse.getMinX(), ellipse.getMinY(),
              ellipse.getMaxX(), ellipse.getMaxY());
          return;
        }
      }

      this.before(AbstractGraphic.BEFORE_OUTLINE);

      rect = s.getBounds2D();
      if (rect.isEmpty()) {
        this.doDrawLine(rect.getMinX(), rect.getMinY(), rect.getMaxX(),
            rect.getMaxY());
      } else {
        this.doDraw(s);
      }
    }
  }

  /**
   * Draw an image
   *
   * @param img
   *          the image
   * @param op
   *          the observer
   * @param x
   *          the x-coordinate
   * @param y
   *          the y-coordinate
   */
  protected abstract void doDrawImage(final BufferedImage img,
      final BufferedImageOp op, final int x, final int y);

  /** {@inheritDoc} */
  @Override
  public final void drawImage(final BufferedImage img,
      final BufferedImageOp op, final int x, final int y) {
    if ((img != null) && (img.getWidth() > 0) && (img.getHeight() > 0)) {
      this.before(AbstractGraphic.BEFORE_IMAGE);
      this.doDrawImage(img, op, x, y);
    }
  }

  /**
   * Draw a rendered image
   *
   * @param img
   *          the image
   * @param xform
   *          the transform
   */
  protected abstract void doDrawRenderedImage(final RenderedImage img,
      final AffineTransform xform);

  /** {@inheritDoc} */
  @Override
  public final void drawRenderedImage(final RenderedImage img,
      final AffineTransform xform) {
    if ((img != null) && (img.getWidth() > 0) && (img.getHeight() > 0)) {
      this.before(AbstractGraphic.BEFORE_IMAGE);
      this.doDrawRenderedImage(img, xform);
    }
  }

  /**
   * Draw a renderable image
   *
   * @param img
   *          the image
   * @param xform
   *          the transform
   */
  protected abstract void doDrawRenderableImage(final RenderableImage img,
      final AffineTransform xform);

  /** {@inheritDoc} */
  @Override
  public final void drawRenderableImage(final RenderableImage img,
      final AffineTransform xform) {
    if ((img != null) && (img.getWidth() > 0f) && (img.getHeight() > 0f)) {
      this.before(AbstractGraphic.BEFORE_IMAGE);
      this.doDrawRenderableImage(img, xform);
    }
  }

  /**
   * Draw a string
   *
   * @param str
   *          the string
   * @param x
   *          the x-coordinate
   * @param y
   *          the y-coordinate
   */
  protected abstract void doDrawString(final String str, final int x,
      final int y);

  /** {@inheritDoc} */
  @Override
  public final void drawString(final String str, final int x,
      final int y) {
    if ((str != null) && (str.length() > 0)) {
      this.before(AbstractGraphic.BEFORE_TEXT);
      this.doDrawString(str, x, y);
    }
  }

  /**
   * Draw a string
   *
   * @param str
   *          the string
   * @param x
   *          the x-coordinate
   * @param y
   *          the y-coordinate
   */
  protected abstract void doDrawString(final String str, final float x,
      final float y);

  /** {@inheritDoc} */
  @Override
  public void drawString(final String str, final float x, final float y) {
    if ((str != null) && (str.length() > 0)) {
      this.before(AbstractGraphic.BEFORE_TEXT);
      this.doDrawString(str, x, y);
    }
  }

  /**
   * Draw a string
   *
   * @param iterator
   *          the string
   * @param x
   *          the x-coordinate
   * @param y
   *          the y-coordinate
   */
  protected abstract void doDrawString(
      final AttributedCharacterIterator iterator, final int x,
      final int y);

  /** {@inheritDoc} */
  @Override
  public final void drawString(final AttributedCharacterIterator iterator,
      final int x, final int y) {
    if ((iterator != null)
        && (iterator.getEndIndex() > iterator.getBeginIndex())) {
      this.before(AbstractGraphic.BEFORE_TEXT);
      this.doDrawString(iterator, x, y);
    }
  }

  /**
   * Draw a string
   *
   * @param iterator
   *          the string
   * @param x
   *          the x-coordinate
   * @param y
   *          the y-coordinate
   */
  protected abstract void doDrawString(
      final AttributedCharacterIterator iterator, final float x,
      final float y);

  /** {@inheritDoc} */
  @Override
  public final void drawString(final AttributedCharacterIterator iterator,
      final float x, final float y) {
    if ((iterator != null)
        && (iterator.getEndIndex() > iterator.getBeginIndex())) {
      this.before(AbstractGraphic.BEFORE_TEXT);
      this.doDrawString(iterator, x, y);
    }
  }

  /**
   * Draw a glyph vector
   *
   * @param g
   *          the glyph vector
   * @param x
   *          the x-coordinate
   * @param y
   *          the y-coordinate
   */
  protected abstract void doDrawGlyphVector(final GlyphVector g,
      final float x, final float y);

  /** {@inheritDoc} */
  @Override
  public final void drawGlyphVector(final GlyphVector g, final float x,
      final float y) {
    if ((g != null) && (g.getNumGlyphs() > 0)) {
      this.before(AbstractGraphic.BEFORE_TEXT);
      this.doDrawGlyphVector(g, x, y);
    }
  }

  /**
   * Fill a shape
   *
   * @param s
   *          the shape
   */
  protected abstract void doFill(final Shape s);

  /** {@inheritDoc} */
  @Override
  public final void fill(final Shape s) {
    final Rectangle2D rectangle;
    final Polygon poly;
    final RoundRectangle2D round;
    final Ellipse2D ellipse;

    if (s != null) {

      if (s instanceof Line2D) {
        return;
      }

      if (s instanceof Rectangle2D) {
        rectangle = ((Rectangle2D) s);
        this.fillRect(rectangle.getX(), rectangle.getY(),
            rectangle.getWidth(), rectangle.getHeight());
        return;
      }

      if (s instanceof Polygon) {
        poly = ((Polygon) s);
        this.fillPolygon(poly.xpoints, poly.ypoints, poly.npoints);
        return;
      }

      if (s instanceof RoundRectangle2D) {
        round = ((RoundRectangle2D) s);
        this.fillRoundRect(round.getX(), round.getY(), round.getWidth(),
            round.getHeight(), round.getArcWidth(), round.getArcHeight());
        return;
      }

      if (s instanceof Point2D) {
        return;
      }

      if (s instanceof Ellipse2D) {
        ellipse = ((Ellipse2D) s);
        if ((ellipse.getWidth() == 0d) || (ellipse.getHeight() == 0d)) {
          return;
        }
      }

      if (!(s.getBounds2D().isEmpty())) {
        this.before(AbstractGraphic.BEFORE_FILL);
        this.doFill(s);
      }
    }
  }

  /**
   * Set the composite
   *
   * @param comp
   *          the composite
   */
  protected abstract void doSetComposite(final Composite comp);

  /** {@inheritDoc} */
  @Override
  public final void setComposite(final Composite comp) {
    if ((comp != null) && (!(comp.equals(this.getComposite())))) {
      this.before(AbstractGraphic.BEFORE_CHANGE_COMPOSITE);
      this.doSetComposite(comp);
    }
  }

  /**
   * Set the paint
   *
   * @param paint
   *          the paint
   */
  protected abstract void doSetPaint(final Paint paint);

  /** {@inheritDoc} */
  @Override
  public final void setPaint(final Paint paint) {
    if ((paint != null) && (!(paint.equals(this.getPaint())))) {
      this.before(AbstractGraphic.BEFORE_CHANGE_PAINT);
      this.doSetPaint(paint);
    }
  }

  /**
   * Set the stroke
   *
   * @param s
   *          the stroke
   */
  protected abstract void doSetStroke(final Stroke s);

  /** {@inheritDoc} */
  @Override
  public final void setStroke(final Stroke s) {
    if ((s != null) && (!(s.equals(this.getStroke())))) {
      this.before(AbstractGraphic.BEFORE_CHANGE_STROKE);
      this.doSetStroke(s);
    }
  }

  /**
   * Set a rendering hint
   *
   * @param hintKey
   *          the key
   * @param hintValue
   *          the value
   */
  protected abstract void doSetRenderingHint(final Key hintKey,
      final Object hintValue);

  /** {@inheritDoc} */
  @Override
  public final void setRenderingHint(final Key hintKey,
      final Object hintValue) {
    final Object oldVal;

    if (hintKey != null) {
      oldVal = this.getRenderingHint(hintKey);
      if ((oldVal != hintValue)
          && ((oldVal == null) || (!(oldVal.equals(hintValue))))) {
        this.before(AbstractGraphic.BEFORE_CHANGE_RENDERING_HINTS);
        this.doSetRenderingHint(hintKey, hintValue);
      }
    }
  }

  /**
   * Set the rendering hints
   *
   * @param hints
   *          the rendering hints
   */
  protected abstract void doSetRenderingHints(final Map<?, ?> hints);

  /** {@inheritDoc} */
  @Override
  public final void setRenderingHints(final Map<?, ?> hints) {
    if (hints != null) {
      if (!(this.getRenderingHints().equals(hints))) {
        this.before(AbstractGraphic.BEFORE_CHANGE_RENDERING_HINTS);
        this.doSetRenderingHints(hints);
      }
    }
  }

  /**
   * translate
   *
   * @param x
   *          the x movement
   * @param y
   *          the y movement
   */
  protected void doTranslate(final int x, final int y) {
    this.doTranslate(((double) x), ((double) y));
  }

  /** {@inheritDoc} */
  @Override
  public final void translate(final int x, final int y) {
    if ((x != 0) || (y != 0)) {
      this.before(AbstractGraphic.BEFORE_CHANGE_TRANSFORMATION);
      this.doTranslate(x, y);
    }
  }

  /**
   * translate
   *
   * @param x
   *          the x movement
   * @param y
   *          the y movement
   */
  protected void doTranslate(final double x, final double y) {
    final AffineTransform trafo;

    trafo = new AffineTransform();
    trafo.translate(x, y);
    this.doTransform(trafo);
  }

  /** {@inheritDoc} */
  @Override
  public final void translate(final double tx, final double ty) {
    if ((tx != 0d) || (ty != 0d)) {
      this.before(AbstractGraphic.BEFORE_CHANGE_TRANSFORMATION);
      this.doTranslate(tx, ty);
    }
  }

  /**
   * Rotate
   *
   * @param theta
   *          the angle
   */
  protected void doRotate(final double theta) {
    final AffineTransform trafo;

    trafo = new AffineTransform();
    trafo.rotate(theta);
    this.doTransform(trafo);
  }

  /** {@inheritDoc} */
  @Override
  public final void rotate(final double theta) {
    if ((theta != 0d) && ((theta % AbstractGraphic.FULL_ANGLE) != 0d)) {
      this.before(AbstractGraphic.BEFORE_CHANGE_TRANSFORMATION);
      this.doRotate(theta);
    }
  }

  /**
   * Rotate
   *
   * @param theta
   *          the angle
   * @param x
   *          the center x
   * @param y
   *          the center y
   */
  protected void doRotate(final double theta, final double x,
      final double y) {
    final AffineTransform trafo;

    trafo = new AffineTransform();
    trafo.rotate(theta, x, y);
    this.doTransform(trafo);
  }

  /** {@inheritDoc} */
  @Override
  public final void rotate(final double theta, final double x,
      final double y) {
    if ((theta != 0d) && ((theta % AbstractGraphic.FULL_ANGLE) != 0d)) {
      this.before(AbstractGraphic.BEFORE_CHANGE_TRANSFORMATION);
      this.doRotate(theta, x, y);
    }
  }

  /**
   * scale
   *
   * @param sx
   *          the x-scale
   * @param sy
   *          the y-scale
   */
  protected void doScale(final double sx, final double sy) {
    final AffineTransform trafo;

    trafo = new AffineTransform();
    trafo.scale(sx, sy);
    this.doTransform(trafo);
  }

  /** {@inheritDoc} */
  @Override
  public final void scale(final double sx, final double sy) {
    if ((sx != 1d) || (sy != 1d)) {
      this.before(AbstractGraphic.BEFORE_CHANGE_TRANSFORMATION);
      this.doScale(sx, sy);
    }
  }

  /**
   * shear
   *
   * @param shx
   *          the x-scale
   * @param shy
   *          the y-scale
   */
  protected void doShear(final double shx, final double shy) {
    final AffineTransform trafo;

    trafo = new AffineTransform();
    trafo.shear(shx, shy);
    this.doTransform(trafo);
  }

  /** {@inheritDoc} */
  @Override
  public final void shear(final double shx, final double shy) {
    if ((shx != 0d) || (shy != 0d)) {
      this.before(AbstractGraphic.BEFORE_CHANGE_TRANSFORMATION);
      this.doShear(shx, shy);
    }
  }

  /**
   * Transform
   *
   * @param Tx
   *          the transform
   */
  protected abstract void doTransform(final AffineTransform Tx);

  /** {@inheritDoc} */
  @Override
  public final void transform(final AffineTransform Tx) {
    if ((Tx != null) && (!(Tx.isIdentity()))) {
      this.before(AbstractGraphic.BEFORE_CHANGE_TRANSFORMATION);
      this.doTransform(Tx);
    }
  }

  /**
   * Set the transform
   *
   * @param Tx
   *          the transform
   */
  protected void doSetTransform(final AffineTransform Tx) {
    final AffineTransform trafo;

    trafo = ((AffineTransform) (this.getTransform().clone()));
    try {
      trafo.invert();
    } catch (final Throwable error) {
      RethrowMode.AS_RUNTIME_EXCEPTION.rethrow(//
          "Cannot emulate setting transform, as the current transform cannot be inverted.", //$NON-NLS-1$
          true, error);
    }
    trafo.concatenate(Tx);
    this.doTransform(trafo);
  }

  /** {@inheritDoc} */
  @Override
  public final void setTransform(final AffineTransform Tx) {
    if ((Tx != null) && (!(Tx.equals(this.getTransform())))) {
      this.before(AbstractGraphic.BEFORE_CHANGE_TRANSFORMATION);
      this.doSetTransform(Tx);
    }
  }

  /**
   * Set the background color
   *
   * @param color
   *          the color
   */
  protected abstract void doSetBackground(final Color color);

  /** {@inheritDoc} */
  @Override
  public final void setBackground(final Color color) {
    if ((color != null) && (!(color.equals(this.getBackground())))) {
      this.before(AbstractGraphic.BEFORE_CHANGE_BACKGROUND_COLOR);
      this.doSetBackground(color);
    }
  }

  /**
   * Clip
   *
   * @param s
   *          the shape to transform and intersect with the current clip
   */
  protected abstract void doClip(final Shape s);

  /** {@inheritDoc} */
  @Override
  public final void clip(final Shape s) {
    final Shape q;
    if (s != null) {
      q = this.getClip();
      if ((q == null) || (!(q.getBounds2D().isEmpty()))) {
        this.before(AbstractGraphic.BEFORE_CHANGE_CLIP);
        this.doClip(s);
      }
    }
  }

  /**
   * create a graphics context
   *
   * @return the graphics
   */
  protected Graphics doCreate() {
    final Rectangle2D rect;

    rect = this.getBounds();
    return this.doCreate(rect.getMinX(), rect.getMinY(), rect.getWidth(),
        rect.getHeight());
  }

  /**
   * Wrap a created graphics device
   *
   * @param graphics
   *          the created graphics device
   * @return the wrapped graphic
   */
  protected Graphics wrapCreatedGraphic(final Graphics graphics) {
    return graphics;
  }

  /** {@inheritDoc} */
  @Override
  public final Graphics create() {
    this.before(AbstractGraphic.BEFORE_CREATE_GRAPHIC);
    return this.wrapCreatedGraphic(this.doCreate());
  }

  /**
   * create a graphics
   *
   * @param x
   *          the x-coordinate
   * @param y
   *          the y-coordinate
   * @param width
   *          the width
   * @param height
   *          the height
   * @return the graphics
   */
  protected Graphics doCreate(final int x, final int y, final int width,
      final int height) {
    return this.wrapCreatedGraphic(super.create(x, y, width, height));
  }

  /** {@inheritDoc} */
  @Override
  public final Graphics create(final int x, final int y, final int width,
      final int height) {
    this.before(AbstractGraphic.BEFORE_CREATE_GRAPHIC);
    return this.doCreate(x, y, width, height);
  }

  /**
   * Set the color
   *
   * @param c
   *          the color
   */
  protected abstract void doSetColor(final Color c);

  /** {@inheritDoc} */
  @Override
  public final void setColor(final Color c) {
    if ((c != null) && (!(c.equals(this.getColor())))) {
      this.before(AbstractGraphic.BEFORE_CHANGE_COLOR);
      this.doSetColor(c);
    }
  }

  /** set the paint mode */
  protected abstract void doSetPaintMode();

  /** {@inheritDoc} */
  @Override
  public final void setPaintMode() {
    this.before(AbstractGraphic.BEFORE_CHANGE_PAINT_MODE);
    this.doSetPaintMode();
  }

  /**
   * Set the xor mode
   *
   * @param c1
   *          the color
   */
  protected abstract void doSetXORMode(final Color c1);

  /** {@inheritDoc} */
  @Override
  public final void setXORMode(final Color c1) {
    if (c1 != null) {
      this.before(AbstractGraphic.BEFORE_CHANGE_PAINT_MODE);
      this.doSetXORMode(c1);
    }
  }

  /**
   * Set the font
   *
   * @param font
   *          the font
   */
  protected abstract void doSetFont(final Font font);

  /** {@inheritDoc} */
  @Override
  public final void setFont(final Font font) {
    if ((font != null) && (!(font.equals(this.getFont())))) {
      this.before(AbstractGraphic.BEFORE_CHANGE_FONT);
      this.doSetFont(font);
    }
  }

  /**
   * Intersect a rectangle with a clip
   *
   * @param x
   *          the x-coordinate
   * @param y
   *          the y-coordinate
   * @param width
   *          the width
   * @param height
   *          the height
   */
  protected abstract void doClipRect(final int x, final int y,
      final int width, final int height);

  /** {@inheritDoc} */
  @Override
  public final void clipRect(final int x, final int y, final int width,
      final int height) {
    final Shape q;
    final Rectangle2D rect;

    q = this.getClip();
    if ((q == null) || //
        ((!(((rect = q.getBounds2D()).isEmpty()) || //
            (new Rectangle2D.Double(x, y, width, height)
                .contains(rect)))))) {
      this.before(AbstractGraphic.BEFORE_CHANGE_CLIP);
      this.doClipRect(x, y, width, height);
    }
  }

  /**
   * Set the clip
   *
   * @param x
   *          the x-coordinate of the clip rectangle
   * @param y
   *          the y-coordinate of the clip rectangle
   * @param width
   *          the width of the clip rectangle
   * @param height
   *          the height of the clip rectangle
   */
  protected void doSetClip(final int x, final int y, final int width,
      final int height) {
    this.doSetClip(new Rectangle(x, y, width, height));
  }

  /** {@inheritDoc} */
  @Override
  public final void setClip(final int x, final int y, final int width,
      final int height) {
    final Shape oldClip;

    oldClip = this.getClip();
    if ((oldClip == null)
        || (!(new Rectangle(x, y, width, height).equals(oldClip)))) {
      this.before(AbstractGraphic.BEFORE_CHANGE_CLIP);
      this.doSetClip(x, y, width, height);
    }
  }

  /**
   * Set the clip shape
   *
   * @param clip
   *          the clip shape
   */
  protected abstract void doSetClip(final Shape clip);

  /** {@inheritDoc} */
  @Override
  public final void setClip(final Shape clip) {
    final Shape oldClip;

    oldClip = this.getClip();

    if ((oldClip != clip)
        && ((clip == null) || (!(clip.equals(oldClip))))) {
      this.before(AbstractGraphic.BEFORE_CHANGE_CLIP);
      this.doSetClip(clip);
    }
  }

  /**
   * Copy an area
   *
   * @param x
   *          the x-coordinate of origin of the area to be copied
   * @param y
   *          the y-coordinate of origin of the area to be copied
   * @param width
   *          the width of the area to be copied
   * @param height
   *          the height of the area to be copied
   * @param dx
   *          the x-coordinate of origin of the destination area
   * @param dy
   *          the y-coordinate of origin of the destination area
   */
  protected abstract void doCopyArea(final int x, final int y,
      final int width, final int height, final int dx, final int dy);

  /** {@inheritDoc} */
  @Override
  public final void copyArea(final int x, final int y, final int width,
      final int height, final int dx, final int dy) {
    if ((width != 0) && (height != 0)) {
      this.before(AbstractGraphic.BEFORE_COPY_AREA);
      this.doCopyArea(x, y, width, height, dx, dy);
    }
  }

  /**
   * Draw a straight line between two points
   *
   * @param x1
   *          the x-coordinate of the first point
   * @param y1
   *          the y-coordinate of the first point
   * @param x2
   *          the x-coordinate of the second point
   * @param y2
   *          the y-coordinate of the second point
   */
  protected abstract void doDrawLine(final int x1, final int y1,
      final int x2, final int y2);

  /** {@inheritDoc} */
  @Override
  public final void drawLine(final int x1, final int y1, final int x2,
      final int y2) {
    this.before(AbstractGraphic.BEFORE_OUTLINE);
    this.doDrawLine(x1, y1, x2, y2);
  }

  /**
   * fill a rectangle
   *
   * @param x
   *          the x-coordinate of the origin of the rectangle
   * @param y
   *          the y-coordinate of the origin of the rectangle
   * @param width
   *          the width of the rectangle
   * @param height
   *          the height of the rectangle
   */
  protected abstract void doFillRect(final int x, final int y,
      final int width, final int height);

  /** {@inheritDoc} */
  @Override
  public final void fillRect(final int x, final int y, final int width,
      final int height) {
    if ((width != 0) && (height != 0)) {
      this.before(AbstractGraphic.BEFORE_FILL);
      this.doFillRect(x, y, width, height);
    }
  }

  /**
   * Draw a rectangle
   *
   * @param x
   *          the x-coordinate of the origin of the rectangle
   * @param y
   *          the y-coordinate of the origin of the rectangle
   * @param width
   *          the width of the rectangle
   * @param height
   *          the height of the rectangle
   */
  protected void doDrawRect(final int x, final int y, final int width,
      final int height) {
    final int x2, y2;

    x2 = (x + width);
    y2 = (y + height);
    this.doDrawLine(x, y, x2, y);
    this.doDrawLine(x2, y, x2, y2);
    this.doDrawLine(x2, y2, x, y2);
    this.doDrawLine(x, y2, x, y);
  }

  /** {@inheritDoc} */
  @Override
  public final void drawRect(final int x, final int y, final int width,
      final int height) {
    this.before(AbstractGraphic.BEFORE_OUTLINE);
    if ((width == 0) || (height == 0)) {
      this.doDrawLine(x, y, (x + width), (y + height));
    } else {
      this.doDrawRect(x, y, width, height);
    }
  }

  /**
   * Clear a rectangle
   *
   * @param x
   *          the x-coordinate of the origin of the rectangle
   * @param y
   *          the y-coordinate of the origin of the rectangle
   * @param width
   *          the width of the rectangle
   * @param height
   *          the height of the rectangle
   */
  protected void doClearRect(final int x, final int y, final int width,
      final int height) {
    final Color color, bg;

    color = this.getColor();
    try {
      bg = this.getBackground();
      this.setColor(bg);
      this.doFillRect(x, y, width, height);
    } finally {
      this.setColor(color);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void clearRect(final int x, final int y, final int width,
      final int height) {
    if ((width != 0) && (height != 0)) {
      this.before(AbstractGraphic.BEFORE_CLEAR);
      this.doClearRect(x, y, width, height);
    }
  }

  /**
   * draw a rectangle with rounded corners
   *
   * @param x
   *          the x-coordinate of the origin of the rectangle
   * @param y
   *          the y-coordinate of the origin of the rectangle
   * @param width
   *          the width of the rectangle
   * @param height
   *          the height of the rectangle
   * @param arcWidth
   *          the arc width
   * @param arcHeight
   *          the arc height
   */
  protected abstract void doDrawRoundRect(final int x, final int y,
      final int width, final int height, final int arcWidth,
      final int arcHeight);

  /** {@inheritDoc} */
  @Override
  public final void drawRoundRect(final int x, final int y,
      final int width, final int height, final int arcWidth,
      final int arcHeight) {
    this.before(AbstractGraphic.BEFORE_OUTLINE);

    if ((width == 0) || (height == 0)) {
      this.doDrawLine(x, y, (x + width), (y + height));
    } else {
      if ((arcWidth == 0) || (arcHeight == 0)) {
        this.doDrawRect(x, y, width, height);
      } else {
        this.doDrawRoundRect(x, y, width, height, arcWidth, arcHeight);
      }
    }
  }

  /**
   * fill a rectangle with rounded corners
   *
   * @param x
   *          the x-coordinate of the origin of the rectangle
   * @param y
   *          the y-coordinate of the origin of the rectangle
   * @param width
   *          the width of the rectangle
   * @param height
   *          the height of the rectangle
   * @param arcWidth
   *          the arc width
   * @param arcHeight
   *          the arc height
   */
  protected abstract void doFillRoundRect(final int x, final int y,
      final int width, final int height, final int arcWidth,
      final int arcHeight);

  /** {@inheritDoc} */
  @Override
  public final void fillRoundRect(final int x, final int y,
      final int width, final int height, final int arcWidth,
      final int arcHeight) {
    if ((width != 0) && (height != 0)) {
      this.before(AbstractGraphic.BEFORE_FILL);
      if ((arcWidth == 0) || (arcHeight == 0)) {
        this.doFillRect(x, y, width, height);
      } else {
        this.doFillRoundRect(x, y, width, height, arcWidth, arcHeight);
      }
    }
  }

  /**
   * Draw an oval
   *
   * @param x
   *          the start x-coordinate
   * @param y
   *          the start x-coordinate
   * @param width
   *          the width of the oval
   * @param height
   *          the height of the oval
   */
  protected abstract void doDrawOval(final int x, final int y,
      final int width, final int height);

  /** {@inheritDoc} */
  @Override
  public final void drawOval(final int x, final int y, final int width,
      final int height) {
    this.before(AbstractGraphic.BEFORE_OUTLINE);
    if ((width == 0) || (height == 0)) {
      this.doDrawLine(x, y, (x + width), (y + width));
    } else {
      this.doDrawOval(x, y, width, height);
    }
  }

  /**
   * Fill an oval
   *
   * @param x
   *          the start x-coordinate
   * @param y
   *          the start x-coordinate
   * @param width
   *          the width of the oval
   * @param height
   *          the height of the oval
   */
  protected abstract void doFillOval(final int x, final int y,
      final int width, final int height);

  /** {@inheritDoc} */
  @Override
  public final void fillOval(final int x, final int y, final int width,
      final int height) {
    if ((width != 0) && (height != 0)) {
      this.before(AbstractGraphic.BEFORE_FILL);
      this.doFillOval(x, y, width, height);
    }
  }

  /**
   * Draw an arc
   *
   * @param x
   *          the start x-coordinate
   * @param y
   *          the start y-coordinate
   * @param width
   *          the width
   * @param height
   *          the height
   * @param startAngle
   *          the start angle
   * @param arcAngle
   *          the arc angle
   */
  protected abstract void doDrawArc(final int x, final int y,
      final int width, final int height, final int startAngle,
      final int arcAngle);

  /** {@inheritDoc} */
  @Override
  public final void drawArc(final int x, final int y, final int width,
      final int height, final int startAngle, final int arcAngle) {
    this.before(AbstractGraphic.BEFORE_OUTLINE);
    if ((width == 0) || (height == 0) || (arcAngle == 0)) {
      this.doDrawLine(x, y, (x + width), (y + width));
    } else {
      this.doDrawArc(x, y, width, height, startAngle, arcAngle);
    }
  }

  /**
   * Fill an arc
   *
   * @param x
   *          the start x-coordinate
   * @param y
   *          the start y-coordinate
   * @param width
   *          the width
   * @param height
   *          the height
   * @param startAngle
   *          the start angle
   * @param arcAngle
   *          the arc angle
   */
  protected abstract void doFillArc(final int x, final int y,
      final int width, final int height, final int startAngle,
      final int arcAngle);

  /** {@inheritDoc} */
  @Override
  public final void fillArc(final int x, final int y, final int width,
      final int height, final int startAngle, final int arcAngle) {
    if ((width != 0) && (height != 0) && (arcAngle != 0)) {
      this.before(AbstractGraphic.BEFORE_FILL);
      this.doFillArc(x, y, width, height, startAngle, arcAngle);
    }
  }

  /**
   * draw a poly line
   *
   * @param xPoints
   *          the x-coordinates
   * @param yPoints
   *          the y-coordinates
   * @param nPoints
   *          the number of points
   */
  protected void doDrawPolyline(final int[] xPoints, final int[] yPoints,
      final int nPoints) {
    int curX, curY, oldX, oldY, i;

    if (nPoints > 0) {
      curX = xPoints[0];
      curY = yPoints[0];
      if (nPoints >= 2) {
        for (i = 1; i < nPoints; i++) {
          oldX = curX;
          oldY = curY;
          curX = xPoints[i];
          curY = yPoints[i];
          this.doDrawLine(oldX, oldY, curX, curY);
        }
      } else {
        this.doDrawLine(curX, curY, curX, curY);
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void drawPolyline(final int[] xPoints, final int[] yPoints,
      final int nPoints) {
    if ((xPoints != null) && (yPoints != null) && (nPoints > 0)) {
      this.before(AbstractGraphic.BEFORE_OUTLINE);
      this.doDrawPolyline(xPoints, yPoints, nPoints);
    }
  }

  /**
   * draw a polygon
   *
   * @param xPoints
   *          the x-coordinates
   * @param yPoints
   *          the y-coordinates
   * @param nPoints
   *          the number of points
   */
  protected void doDrawPolygon(final int[] xPoints, final int[] yPoints,
      final int nPoints) {
    int origX, origY, curX, curY, oldX, oldY, i;

    if (nPoints > 0) {
      origX = curX = xPoints[0];
      origY = curY = yPoints[0];
      if (nPoints >= 2) {
        for (i = 1; i < nPoints; i++) {
          oldX = curX;
          oldY = curY;
          curX = xPoints[i];
          curY = yPoints[i];
          this.doDrawLine(oldX, oldY, curX, curY);
        }
        if (nPoints > 2) {
          this.doDrawLine(curX, curY, origX, origY);
        }
      } else {
        this.doDrawLine(curX, curY, curX, curY);
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void drawPolygon(final int[] xPoints, final int[] yPoints,
      final int nPoints) {
    if ((xPoints != null) && (yPoints != null) && (nPoints > 0)) {
      this.before(AbstractGraphic.BEFORE_OUTLINE);
      this.doDrawPolyline(xPoints, yPoints, nPoints);
    }
  }

  /**
   * Draw a polygon
   *
   * @param p
   *          the polygon
   */
  protected void doDrawPolygon(final Polygon p) {
    this.doDrawPolygon(p.xpoints, p.ypoints, p.npoints);
  }

  /** {@inheritDoc} */
  @Override
  public final void drawPolygon(final Polygon p) {
    if ((p != null) && (p.xpoints != null) && (p.ypoints != null)
        && (p.npoints > 0)) {
      this.before(AbstractGraphic.BEFORE_OUTLINE);
      this.doDrawPolygon(p);
    }
  }

  /**
   * Fill a polygon
   *
   * @param xPoints
   *          the x-coordinates
   * @param yPoints
   *          the y-coordinates
   * @param nPoints
   *          the number of points
   */
  protected abstract void doFillPolygon(final int[] xPoints,
      final int[] yPoints, final int nPoints);

  /** {@inheritDoc} */
  @Override
  public final void fillPolygon(final int[] xPoints, final int[] yPoints,
      final int nPoints) {
    if ((xPoints != null) && (yPoints != null) && (nPoints > 2)) {
      if (!(new Polygon(xPoints, yPoints, nPoints).getBounds2D()
          .isEmpty())) {
        this.before(AbstractGraphic.BEFORE_FILL);
        this.doFillPolygon(xPoints, yPoints, nPoints);
      }
    }
  }

  /**
   * fill a polygon
   *
   * @param p
   *          the polygon
   */
  protected void doFillPolygon(final Polygon p) {
    this.doFillPolygon(p.xpoints, p.ypoints, p.npoints);
  }

  /** {@inheritDoc} */
  @Override
  public final void fillPolygon(final Polygon p) {
    if ((p != null) && (p.xpoints != null) && (p.ypoints != null)
        && (p.npoints > 2)) {
      if (!(p.getBounds2D().isEmpty())) {
        this.before(AbstractGraphic.BEFORE_FILL);
        this.doFillPolygon(p);
      }
    }
  }

  /**
   * Draw some characters
   *
   * @param data
   *          the data
   * @param offset
   *          the offset
   * @param length
   *          the length
   * @param x
   *          the x-coordinate
   * @param y
   *          the y-coordinate
   */
  protected void doDrawChars(final char[] data, final int offset,
      final int length, final int x, final int y) {
    this.doDrawString(String.valueOf(data, offset, length), x, y);
  }

  /** {@inheritDoc} */
  @Override
  public final void drawChars(final char[] data, final int offset,
      final int length, final int x, final int y) {
    if ((data != null) && (length > 0)) {
      this.before(AbstractGraphic.BEFORE_TEXT);
      this.doDrawChars(data, offset, length, x, y);
    }
  }

  /**
   * Draw some bytes
   *
   * @param data
   *          the data
   * @param offset
   *          the offset
   * @param length
   *          the length
   * @param x
   *          the x-coordinate
   * @param y
   *          the y-coordinate
   */
  protected void doDrawBytes(final byte[] data, final int offset,
      final int length, final int x, final int y) {
    this.doDrawString(
        new String(data, offset, length, //
            Charset.forName(StreamEncoding.getISO8859_1().getJavaName())), //
        x, y);
  }

  /** {@inheritDoc} */
  @Override
  public final void drawBytes(final byte[] data, final int offset,
      final int length, final int x, final int y) {
    if ((data != null) && (length > 0)) {
      this.before(AbstractGraphic.BEFORE_TEXT);
      this.doDrawBytes(data, offset, length, x, y);
    }
  }

  /**
   * Draw an image
   *
   * @param img
   *          the image
   * @param x
   *          the x-coordinate
   * @param y
   *          the y-coordinate
   * @param observer
   *          the observer
   * @return {@code true} if something is still changing, {@code false}
   *         otherwise
   */
  protected abstract boolean doDrawImage(final Image img, final int x,
      final int y, final ImageObserver observer);

  /** {@inheritDoc} */
  @Override
  public final boolean drawImage(final Image img, final int x, final int y,
      final ImageObserver observer) {
    if ((img != null) && (img.getWidth(observer) != 0)
        && (img.getHeight(observer) != 0)) {
      this.before(AbstractGraphic.BEFORE_IMAGE);
      return this.doDrawImage(img, x, y, observer);
    }
    return false;
  }

  /**
   * Draw an image
   *
   * @param img
   *          the image
   * @param x
   *          the x-coordinate
   * @param y
   *          the y-coordinate
   * @param width
   *          the image width
   * @param height
   *          the image height
   * @param observer
   *          the observer
   * @return {@code true} if something is still changing, {@code false}
   *         otherwise
   */
  protected abstract boolean doDrawImage(final Image img, final int x,
      final int y, final int width, final int height,
      final ImageObserver observer);

  /** {@inheritDoc} */
  @Override
  public final boolean drawImage(final Image img, final int x, final int y,
      final int width, final int height, final ImageObserver observer) {
    if ((img != null) && (width != 0) && (height != 0)) {
      this.before(AbstractGraphic.BEFORE_IMAGE);
      return this.doDrawImage(img, x, y, width, height, observer);
    }
    return false;
  }

  /**
   * Draw an image
   *
   * @param img
   *          the image
   * @param x
   *          the x-coordinate
   * @param y
   *          the y-coordinate
   * @param bgcolor
   *          the background color
   * @param observer
   *          the observer
   * @return {@code true} if something is still changing, {@code false}
   *         otherwise
   */
  protected abstract boolean doDrawImage(final Image img, final int x,
      final int y, final Color bgcolor, final ImageObserver observer);

  /** {@inheritDoc} */
  @Override
  public boolean drawImage(final Image img, final int x, final int y,
      final Color bgcolor, final ImageObserver observer) {
    if ((img != null) && (img.getWidth(observer) != 0)
        && (img.getHeight(observer) != 0)) {
      this.before(AbstractGraphic.BEFORE_IMAGE);
      return this.doDrawImage(img, x, y, bgcolor, observer);
    }
    return false;
  }

  /**
   * Draw an image
   *
   * @param img
   *          the image
   * @param x
   *          the x-coordinate
   * @param y
   *          the y-coordinate
   * @param width
   *          the image width
   * @param height
   *          the image height
   * @param bgcolor
   *          the background color
   * @param observer
   *          the observer
   * @return {@code true} if something is still changing, {@code false}
   *         otherwise
   */
  protected abstract boolean doDrawImage(final Image img, final int x,
      final int y, final int width, final int height, final Color bgcolor,
      final ImageObserver observer);

  /** {@inheritDoc} */
  @Override
  public final boolean drawImage(final Image img, final int x, final int y,
      final int width, final int height, final Color bgcolor,
      final ImageObserver observer) {
    if ((img != null) && (width != 0) && (height != 0)) {
      this.before(AbstractGraphic.BEFORE_IMAGE);
      return this.doDrawImage(img, x, y, width, height, bgcolor, observer);
    }
    return false;
  }

  /**
   * Draw an image
   *
   * @param img
   *          the image
   * @param dx1
   *          the destination rectangle start x coordinate
   * @param dy1
   *          the destination rectangle start y coordinate
   * @param dx2
   *          the destination rectangle end x coordinate
   * @param dy2
   *          the destination rectangle end y coordinate
   * @param sx1
   *          the source rectangle start x coordinate
   * @param sy1
   *          the source rectangle start y coordinate
   * @param sx2
   *          the source rectangle end x coordinate
   * @param sy2
   *          the source rectangle end y coordinate
   * @param observer
   *          the observer
   * @return {@code true} if something is still changing, {@code false}
   *         otherwise
   */
  protected abstract boolean doDrawImage(final Image img, final int dx1,
      final int dy1, final int dx2, final int dy2, final int sx1,
      final int sy1, final int sx2, final int sy2,
      final ImageObserver observer);

  /** {@inheritDoc} */
  @Override
  public final boolean drawImage(final Image img, final int dx1,
      final int dy1, final int dx2, final int dy2, final int sx1,
      final int sy1, final int sx2, final int sy2,
      final ImageObserver observer) {
    if ((dx2 != dx1) && (sy2 != sy1)) {
      this.before(AbstractGraphic.BEFORE_IMAGE);
      return this.doDrawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2,
          observer);
    }
    return false;
  }

  /**
   * Draw an image
   *
   * @param img
   *          the image
   * @param dx1
   *          the destination rectangle start x coordinate
   * @param dy1
   *          the destination rectangle start y coordinate
   * @param dx2
   *          the destination rectangle end x coordinate
   * @param dy2
   *          the destination rectangle end y coordinate
   * @param sx1
   *          the source rectangle start x coordinate
   * @param sy1
   *          the source rectangle start y coordinate
   * @param sx2
   *          the source rectangle end x coordinate
   * @param sy2
   *          the source rectangle end y coordinate
   * @param bgcolor
   *          the background color
   * @param observer
   *          the observer
   * @return {@code true} if something is still changing, {@code false}
   *         otherwise
   */
  protected abstract boolean doDrawImage(final Image img, final int dx1,
      final int dy1, final int dx2, final int dy2, final int sx1,
      final int sy1, final int sx2, final int sy2, final Color bgcolor,
      final ImageObserver observer);

  /** {@inheritDoc} */
  @Override
  public final boolean drawImage(final Image img, final int dx1,
      final int dy1, final int dx2, final int dy2, final int sx1,
      final int sy1, final int sx2, final int sy2, final Color bgcolor,
      final ImageObserver observer) {
    if ((dx2 != dx1) && (sy2 != sy1)) {
      this.before(AbstractGraphic.BEFORE_IMAGE);
      return this.doDrawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2,
          bgcolor, observer);
    }
    return false;
  }

  /**
   * Draw an image
   *
   * @param img
   *          the image
   * @param xform
   *          the transform
   * @param obs
   *          the observer
   * @return {@code true} if something is still changing, {@code false}
   *         otherwise
   */
  protected abstract boolean doDrawImage(final Image img,
      final AffineTransform xform, final ImageObserver obs);

  /** {@inheritDoc} */
  @Override
  public final boolean drawImage(final Image img,
      final AffineTransform xform, final ImageObserver obs) {
    if ((img != null) && (img.getWidth(obs) != 0)
        && (img.getHeight(obs) != 0)) {
      this.before(AbstractGraphic.BEFORE_IMAGE);
      return this.doDrawImage(img, xform, obs);
    }
    return false;
  }

  /**
   * Add some rendering hints
   *
   * @param hints
   *          the hints
   */
  protected abstract void doAddRenderingHints(final Map<?, ?> hints);

  /** {@inheritDoc} */
  @Override
  public final void addRenderingHints(final Map<?, ?> hints) {
    Object key, old, val;
    RenderingHints current;

    if (hints != null) {
      current = null;
      check: {
        for (final Map.Entry<?, ?> entry : hints.entrySet()) {
          if (entry != null) {
            key = entry.getKey();
            if (key != null) {

              if (key instanceof Key) {
                old = this.getRenderingHint((Key) key);
              } else {
                if (current == null) {
                  current = this.getRenderingHints();
                }
                old = current.get(key);
              }

              val = entry.getValue();
              if ((old != val)
                  && ((old == null) || (!(old.equals(val))))) {
                break check;
              }
            }
          }
        }
        return;
      }
      this.before(AbstractGraphic.BEFORE_CHANGE_RENDERING_HINTS);
      this.doAddRenderingHints(hints);
    }
  }

  // new functionality

  /**
   * convert a coordinate
   *
   * @param d
   *          the coordinate
   * @return the integer version
   */
  static final int _c(final double d) {
    return ((int) (d + 0.5d));
  }

  /**
   * convert a length
   *
   * @param d
   *          the coordinate
   * @return the integer version
   */
  static final int _l(final double d) {
    int r;

    r = AbstractGraphic._c(d);
    return ((r != 0) ? r : ((d < 0d) ? (-1) : 1));
  }

  /**
   * Draw a 3-d rectangle
   *
   * @param x
   *          the x-coordinate
   * @param y
   *          the y-coordinate
   * @param width
   *          the width
   * @param height
   *          the height
   * @param raised
   *          is it raised?
   */
  protected void doDraw3DRect(final double x, final double y,
      final double width, final double height, final boolean raised) {
    try (final __TranslatedAndScaled t = new __TranslatedAndScaled(x, y,
        width, height)) {
      this.draw3DRect(t.m_x, t.m_y, t.m_w, t.m_h, raised);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void draw3DRect(final double x, final double y,
      final double width, final double height, final boolean raised) {
    final int a, b, c, d;

    this.before(
        AbstractGraphic.BEFORE_3D_RECT | AbstractGraphic.BEFORE_OUTLINE);

    if (this.autoConvertCoordinatesToInt()) {
      c = ((int) width);
      if ((width >= Integer.MIN_VALUE) && (width <= Integer.MAX_VALUE)
          && (width == c)) {
        d = ((int) height);
        if ((height >= Integer.MIN_VALUE) && (height <= Integer.MAX_VALUE)
            && (height == d)) {
          a = ((int) x);
          if ((x >= Integer.MIN_VALUE) && (x <= Integer.MAX_VALUE)
              && (x == a)) {
            b = ((int) y);
            if ((y >= Integer.MIN_VALUE) && (y <= Integer.MAX_VALUE)
                && (y == b)) {
              this.doDraw3DRect(a, b, c, d, raised);
              return;
            }
          }
        }
      }
    }

    this.doDraw3DRect(x, y, width, height, raised);
  }

  /**
   * Fill a 3-d rectangle
   *
   * @param x
   *          the x-coordinate
   * @param y
   *          the y-coordinate
   * @param width
   *          the width
   * @param height
   *          the height
   * @param raised
   *          is it raised?
   */
  protected void doFill3DRect(final double x, final double y,
      final double width, final double height, final boolean raised) {
    try (final __TranslatedAndScaled t = new __TranslatedAndScaled(x, y,
        width, height)) {
      this.fill3DRect(t.m_x, t.m_y, t.m_w, t.m_h, raised);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void fill3DRect(final double x, final double y,
      final double width, final double height, final boolean raised) {
    final int a, b, c, d;

    c = ((int) width);
    if ((width >= Integer.MIN_VALUE) && (width <= Integer.MAX_VALUE)
        && (width == c)) {
      if (c == 0) {
        return;
      }

      d = ((int) height);
      if ((height >= Integer.MIN_VALUE) && (height <= Integer.MAX_VALUE)
          && (height == d)) {
        if (d == 0) {
          return;
        }
        if (this.autoConvertCoordinatesToInt()) {
          a = ((int) x);
          if ((x >= Integer.MIN_VALUE) && (x <= Integer.MAX_VALUE)
              && (x == a)) {

            b = ((int) y);
            if ((y >= Integer.MIN_VALUE) && (y <= Integer.MAX_VALUE)
                && (y == b)) {
              this.before(AbstractGraphic.BEFORE_3D_RECT
                  | AbstractGraphic.BEFORE_FILL);
              this.doFill3DRect(a, b, c, d, raised);
              return;
            }
          }

        }
      }
    }

    if ((width != 0d) && (height != 0d)) {
      this.before(
          AbstractGraphic.BEFORE_3D_RECT | AbstractGraphic.BEFORE_FILL);
      this.doFill3DRect(x, y, width, height, raised);
    }
  }

  /**
   * Draw a buffered image
   *
   * @param img
   *          the image
   * @param op
   *          the operation
   * @param x
   *          the x-coordinate
   * @param y
   *          the y-coordinate
   */
  protected void doDrawImage(final BufferedImage img,
      final BufferedImageOp op, final double x, final double y) {
    try (final __Translated t = new __Translated(x, y)) {
      this.drawImage(img, op, t.m_x, t.m_y);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void drawImage(final BufferedImage img,
      final BufferedImageOp op, final double x, final double y) {
    final int a, b;

    if ((img != null) && (img.getWidth() > 0) && (img.getHeight() > 0)) {
      this.before(AbstractGraphic.BEFORE_IMAGE);

      if (this.autoConvertCoordinatesToInt()) {
        a = ((int) x);
        if ((x >= Integer.MIN_VALUE) && (x <= Integer.MAX_VALUE)
            && (x == a)) {
          b = ((int) y);
          if ((y >= Integer.MIN_VALUE) && (y <= Integer.MAX_VALUE)
              && (y == b)) {
            this.doDrawImage(img, op, a, b);
            return;
          }
        }
      }

      this.doDrawImage(img, op, x, y);
    }
  }

  /**
   * Draw a string
   *
   * @param str
   *          the string
   * @param x
   *          the x-coordinate
   * @param y
   *          the y-coordinate
   */
  protected void doDrawString(final String str, final double x,
      final double y) {
    this.drawString(str, ((float) x), ((float) y));
  }

  /** {@inheritDoc} */
  @Override
  public final void drawString(final String str, final double x,
      final double y) {
    final int a, b;
    final float p, q;

    if ((str != null) && (str.length() > 0)) {
      this.before(AbstractGraphic.BEFORE_TEXT);

      if (this.autoConvertCoordinatesToInt()) {
        a = ((int) x);
        if ((x >= Integer.MIN_VALUE) && (x <= Integer.MAX_VALUE)
            && (x == a)) {
          b = ((int) y);
          if ((y >= Integer.MIN_VALUE) && (y <= Integer.MAX_VALUE)
              && (y == b)) {
            this.doDrawString(str, a, b);
            return;
          }
        }
      }

      p = ((float) x);
      if ((x <= Float.MAX_VALUE) && (x >= (-Float.MAX_VALUE))
          && (p == x)) {
        q = ((float) y);
        if ((y <= Float.MAX_VALUE) && (y >= (-Float.MAX_VALUE))
            && (q == y)) {
          this.doDrawString(str, p, q);
          return;
        }
      }

      this.doDrawString(str, x, y);
    }
  }

  /**
   * Draw a string
   *
   * @param iterator
   *          the string
   * @param x
   *          the x-coordinate
   * @param y
   *          the y-coordinate
   */
  protected void doDrawString(final AttributedCharacterIterator iterator,
      final double x, final double y) {
    this.doDrawString(iterator, ((float) x), ((float) y));
  }

  /** {@inheritDoc} */
  @Override
  public final void drawString(final AttributedCharacterIterator iterator,
      final double x, final double y) {
    final int a, b;
    final float p, q;

    if ((iterator != null)
        && (iterator.getEndIndex() > iterator.getBeginIndex())) {
      this.before(AbstractGraphic.BEFORE_TEXT);

      if (this.autoConvertCoordinatesToInt()) {
        a = ((int) x);
        if ((x >= Integer.MIN_VALUE) && (x <= Integer.MAX_VALUE)
            && (x == a)) {
          b = ((int) y);
          if ((y >= Integer.MIN_VALUE) && (y <= Integer.MAX_VALUE)
              && (y == b)) {
            this.doDrawString(iterator, a, b);
            return;
          }
        }
      }

      p = ((float) x);
      if ((x <= Float.MAX_VALUE) && (x >= (-Float.MAX_VALUE))
          && (p == x)) {
        q = ((float) y);
        if ((y <= Float.MAX_VALUE) && (y >= (-Float.MAX_VALUE))
            && (q == y)) {
          this.doDrawString(iterator, p, q);
          return;
        }
      }

      this.doDrawString(iterator, x, y);
    }
  }

  /**
   * Draw a glyph vector
   *
   * @param g
   *          the glyph vector
   * @param x
   *          the x-coordinate
   * @param y
   *          the y-coordinate
   */
  protected void doDrawGlyphVector(final GlyphVector g, final double x,
      final double y) {
    this.doDrawGlyphVector(g, ((float) x), ((float) y));
  }

  /** {@inheritDoc} */
  @Override
  public final void drawGlyphVector(final GlyphVector g, final double x,
      final double y) {
    final int a, b;
    final float p, q;

    if ((g != null) && (g.getNumGlyphs() > 0)) {
      this.before(AbstractGraphic.BEFORE_TEXT);

      if (this.autoConvertCoordinatesToInt()) {
        a = ((int) x);
        if ((x >= Integer.MIN_VALUE) && (x <= Integer.MAX_VALUE)
            && (x == a)) {
          b = ((int) y);
          if ((y >= Integer.MIN_VALUE) && (y <= Integer.MAX_VALUE)
              && (y == b)) {
            this.doDrawGlyphVector(g, a, b);
            return;
          }
        }
      }

      p = ((float) x);
      if ((x <= Float.MAX_VALUE) && (x >= (-Float.MAX_VALUE))
          && (p == x)) {
        q = ((float) y);
        if ((y <= Float.MAX_VALUE) && (y >= (-Float.MAX_VALUE))
            && (q == y)) {
          this.doDrawGlyphVector(g, p, q);
          return;
        }
      }

      this.doDrawGlyphVector(g, x, y);
    }
  }

  /**
   * create a graphics object
   *
   * @param x
   *          the x-coordinate
   * @param y
   *          the y-coordinate
   * @param width
   *          the width
   * @param height
   *          the height
   * @return the new graphics object
   */
  protected Graphics doCreate(final double x, final double y,
      final double width, final double height) {
    try (final __TranslatedAndScaled t = new __TranslatedAndScaled(x, y,
        width, height)) {
      return super.create(t.m_x, t.m_y, t.m_w, t.m_h);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final Graphics create(final double x, final double y,
      final double width, final double height) {
    final int a, b, c, d;

    this.before(AbstractGraphic.BEFORE_CREATE_GRAPHIC);

    if (this.autoConvertCoordinatesToInt()) {
      c = ((int) width);
      if ((width >= Integer.MIN_VALUE) && (width <= Integer.MAX_VALUE)
          && (width == c)) {
        d = ((int) height);
        if ((height >= Integer.MIN_VALUE) && (height <= Integer.MAX_VALUE)
            && (height == d)) {
          a = ((int) x);
          if ((x >= Integer.MIN_VALUE) && (x <= Integer.MAX_VALUE)
              && (x == a)) {
            b = ((int) y);
            if ((y >= Integer.MIN_VALUE) && (y <= Integer.MAX_VALUE)
                && (y == b)) {
              return this.doCreate(a, b, c, d);
            }
          }
        }
      }
    }

    return this.doCreate(x, y, width, height);
  }

  /**
   * clip the given rectangle
   *
   * @param x
   *          the x-coordinate
   * @param y
   *          the y-coordinate
   * @param width
   *          the width
   * @param height
   *          the height
   */
  protected void doClipRect(final double x, final double y,
      final double width, final double height) {
    this.doClip(new Rectangle2D.Double(x, y, width, height));
  }

  /** {@inheritDoc} */
  @Override
  public final void clipRect(final double x, final double y,
      final double width, final double height) {
    final Shape q;
    final Rectangle2D rect;
    final int a, b, c, d;

    q = this.getClip();
    if ((q == null) || //
        ((!(((rect = q.getBounds2D()).isEmpty()) || //
            (new Rectangle2D.Double(x, y, width, height)
                .contains(rect)))))) {
      this.before(AbstractGraphic.BEFORE_CHANGE_CLIP);

      if (this.autoConvertCoordinatesToInt()) {
        c = ((int) width);
        if ((width >= Integer.MIN_VALUE) && (width <= Integer.MAX_VALUE)
            && (width == c)) {
          d = ((int) height);
          if ((height >= Integer.MIN_VALUE)
              && (height <= Integer.MAX_VALUE) && (height == d)) {
            a = ((int) x);
            if ((x >= Integer.MIN_VALUE) && (x <= Integer.MAX_VALUE)
                && (x == a)) {
              b = ((int) y);
              if ((y >= Integer.MIN_VALUE) && (y <= Integer.MAX_VALUE)
                  && (y == b)) {
                this.doClipRect(a, b, c, d);
                return;
              }
            }
          }
        }
      }

      this.doClipRect(x, y, width, height);
    }
  }

  /**
   * set the clip to the given rectangle
   *
   * @param x
   *          the x-coordinate
   * @param y
   *          the y-coordinate
   * @param width
   *          the width
   * @param height
   *          the height
   */
  protected void doSetClip(final double x, final double y,
      final double width, final double height) {
    this.doSetClip(new Rectangle2D.Double(x, y, width, height));
  }

  /** {@inheritDoc} */
  @Override
  public final void setClip(final double x, final double y,
      final double width, final double height) {
    final Shape oldClip;
    final int a, b, c, d;

    oldClip = this.getClip();
    if ((oldClip == null) || (!(new Rectangle2D.Double(x, y, width, height)
        .equals(oldClip)))) {
      this.before(AbstractGraphic.BEFORE_CHANGE_CLIP);
      if (this.autoConvertCoordinatesToInt()) {
        c = ((int) width);
        if ((width >= Integer.MIN_VALUE) && (width <= Integer.MAX_VALUE)
            && (width == c)) {
          d = ((int) height);
          if ((height >= Integer.MIN_VALUE)
              && (height <= Integer.MAX_VALUE) && (height == d)) {
            a = ((int) x);
            if ((x >= Integer.MIN_VALUE) && (x <= Integer.MAX_VALUE)
                && (x == a)) {
              b = ((int) y);
              if ((y >= Integer.MIN_VALUE) && (y <= Integer.MAX_VALUE)
                  && (y == b)) {
                this.doSetClip(a, b, c, d);
                return;
              }
            }
          }
        }
      }

      this.doSetClip(x, y, width, height);
    }
  }

  /**
   * Copy some area
   *
   * @param x
   *          the x-coordinate
   * @param y
   *          the y-coordinate
   * @param width
   *          the width
   * @param height
   *          the height
   * @param dx
   *          the movement x
   * @param dy
   *          the movement y
   */
  protected void doCopyArea(final double x, final double y,
      final double width, final double height, final double dx,
      final double dy) {
    this.doCopyArea(AbstractGraphic._c(x), AbstractGraphic._c(y),
        AbstractGraphic._l(width), AbstractGraphic._l(height),
        AbstractGraphic._c(dx), AbstractGraphic._c(dy));
  }

  /** {@inheritDoc} */
  @Override
  public final void copyArea(final double x, final double y,
      final double width, final double height, final double dx,
      final double dy) {
    final int a, b, c, d, e, f;

    c = ((int) width);
    if ((width >= Integer.MIN_VALUE) && (width <= Integer.MAX_VALUE)
        && (width == c)) {
      if (c == 0) {
        return;
      }

      d = ((int) height);
      if ((height >= Integer.MIN_VALUE) && (height <= Integer.MAX_VALUE)
          && (height == d)) {
        if (d == 0) {
          return;
        }
        if (this.autoConvertCoordinatesToInt()) {
          a = ((int) x);
          if ((x >= Integer.MIN_VALUE) && (x <= Integer.MAX_VALUE)
              && (x == a)) {

            b = ((int) y);
            if ((y >= Integer.MIN_VALUE) && (y <= Integer.MAX_VALUE)
                && (y == b)) {

              e = ((int) dx);
              if ((dx >= Integer.MIN_VALUE) && (dx <= Integer.MAX_VALUE)
                  && (dx == e)) {

                f = ((int) dy);
                if ((dy >= Integer.MIN_VALUE) && (dy <= Integer.MAX_VALUE)
                    && (dy == f)) {
                  this.before(AbstractGraphic.BEFORE_COPY_AREA);
                  this.doCopyArea(a, b, c, d, e, f);
                  return;
                }
              }
            }
          }
        }
      }
    }

    if ((width != 0d) && (height != 0d)) {
      this.before(AbstractGraphic.BEFORE_COPY_AREA);
      this.doCopyArea(x, y, width, height, dx, dy);
    }
  }

  /**
   * draw a line
   *
   * @param x1
   *          the start x-coordinate
   * @param y1
   *          the start y-coordinate
   * @param x2
   *          the end x-coordinate
   * @param y2
   *          the end y-coordinate
   */
  protected void doDrawLine(final double x1, final double y1,
      final double x2, final double y2) {
    this.doDraw(new Line2D.Double(x1, y1, x2, y2));
  }

  /** {@inheritDoc} */
  @Override
  public final void drawLine(final double x1, final double y1,
      final double x2, final double y2) {
    final int a, b, c, d;

    this.before(AbstractGraphic.BEFORE_OUTLINE);

    if (this.autoConvertCoordinatesToInt()) {
      a = ((int) x1);
      if ((x1 >= Integer.MIN_VALUE) && (x1 <= Integer.MAX_VALUE)
          && (x1 == a)) {

        b = ((int) y1);
        if ((y1 >= Integer.MIN_VALUE) && (y1 <= Integer.MAX_VALUE)
            && (y1 == b)) {

          c = ((int) x2);
          if ((x2 >= Integer.MIN_VALUE) && (x2 <= Integer.MAX_VALUE)
              && (x2 == a)) {

            d = ((int) y2);
            if ((y2 >= Integer.MIN_VALUE) && (y2 <= Integer.MAX_VALUE)
                && (y2 == d)) {
              this.doDrawLine(a, b, c, d);
              return;
            }
          }
        }
      }
    }

    this.doDrawLine(x1, y1, x2, y2);
  }

  /**
   * fill a rectangle
   *
   * @param x
   *          the start x-coordinate
   * @param y
   *          the start y-coordinate
   * @param width
   *          the width
   * @param height
   *          the height
   */
  protected void doFillRect(final double x, final double y,
      final double width, final double height) {
    this.doFill(new Rectangle2D.Double(x, y, width, height));
  }

  /** {@inheritDoc} */
  @Override
  public final void fillRect(final double x, final double y,
      final double width, final double height) {
    final int a, b, c, d;

    c = ((int) width);
    if ((width >= Integer.MIN_VALUE) && (width <= Integer.MAX_VALUE)
        && (width == c)) {
      if (c == 0) {
        return;
      }
      d = ((int) height);
      if ((height >= Integer.MIN_VALUE) && (height <= Integer.MAX_VALUE)
          && (height == d)) {
        if (d == 0) {
          return;
        }
        if (this.autoConvertCoordinatesToInt()) {
          a = ((int) x);
          if ((x >= Integer.MIN_VALUE) && (x <= Integer.MAX_VALUE)
              && (x == a)) {
            b = ((int) y);
            if ((y >= Integer.MIN_VALUE) && (y <= Integer.MAX_VALUE)
                && (y == b)) {
              this.before(AbstractGraphic.BEFORE_FILL);
              this.doFillRect(a, b, c, d);
              return;
            }
          }
        }
      }
    }

    if ((width != 0d) && (height != 0d)) {
      this.before(AbstractGraphic.BEFORE_FILL);
      this.doFillRect(x, y, width, height);
    }
  }

  /**
   * draw a rectangle
   *
   * @param x
   *          the start x-coordinate
   * @param y
   *          the start y-coordinate
   * @param width
   *          the width
   * @param height
   *          the height
   */
  protected void doDrawRect(final double x, final double y,
      final double width, final double height) {
    this.doDraw(new Rectangle2D.Double(x, y, width, height));
  }

  /** {@inheritDoc} */
  @Override
  public final void drawRect(final double x, final double y,
      final double width, final double height) {
    final int a, b, c, d, e, f;

    this.before(AbstractGraphic.BEFORE_OUTLINE);

    if (this.autoConvertCoordinatesToInt()) {
      c = ((int) width);
      if ((width >= Integer.MIN_VALUE) && (width <= Integer.MAX_VALUE)
          && (width == c)) {
        d = ((int) height);
        if ((height >= Integer.MIN_VALUE) && (height <= Integer.MAX_VALUE)
            && (height == d)) {
          a = ((int) x);
          if ((x >= Integer.MIN_VALUE) && (x <= Integer.MAX_VALUE)
              && (x == a)) {
            b = ((int) y);
            if ((y >= Integer.MIN_VALUE) && (y <= Integer.MAX_VALUE)
                && (y == b)) {

              if ((c == 0) || (d == 0)) {
                e = (a + c);
                if (e == AbstractGraphic.__add(a, c)) {
                  f = (b + d);
                  if (f == AbstractGraphic.__add(b, d)) {
                    this.doDrawLine(a, b, e, f);
                    return;
                  }
                }
              }

              this.doDrawRect(a, b, c, d);
              return;
            }
          }
        }
      }
    }

    if ((width == 0d) || (height == 0d)) {
      this.doDrawLine(x, y, (x + width), (y + height));
    } else {
      this.doDrawRect(x, y, width, height);
    }
  }

  /**
   * clear a rectangle
   *
   * @param x
   *          the start x-coordinate
   * @param y
   *          the start y-coordinate
   * @param width
   *          the width
   * @param height
   *          the height
   */
  protected void doClearRect(final double x, final double y,
      final double width, final double height) {
    final Paint p;

    p = this.getPaint();
    try {
      this.setPaint(this.getBackground());
      this.fillRect(x, y, width, height);
    } finally {
      this.setPaint(p);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void clearRect(final double x, final double y,
      final double width, final double height) {
    final int a, b, c, d;

    c = ((int) width);
    if ((width >= Integer.MIN_VALUE) && (width <= Integer.MAX_VALUE)
        && (width == c)) {
      if (c == 0) {
        return;
      }
      d = ((int) height);
      if ((height >= Integer.MIN_VALUE) && (height <= Integer.MAX_VALUE)
          && (height == d)) {
        if (d == 0) {
          return;
        }
        if (this.autoConvertCoordinatesToInt()) {
          a = ((int) x);
          if ((x >= Integer.MIN_VALUE) && (x <= Integer.MAX_VALUE)
              && (x == a)) {
            b = ((int) y);
            if ((y >= Integer.MIN_VALUE) && (y <= Integer.MAX_VALUE)
                && (y == b)) {
              this.before(AbstractGraphic.BEFORE_CLEAR);
              this.doClearRect(a, b, c, d);
              return;
            }
          }
        }
      }
    }

    if ((width != 0d) && (height != 0d)) {
      this.before(AbstractGraphic.BEFORE_CLEAR);
      this.doClearRect(x, y, width, height);
    }
  }

  /**
   * draw a round rectangle
   *
   * @param x
   *          the start x-coordinate
   * @param y
   *          the start y-coordinate
   * @param width
   *          the width
   * @param height
   *          the height
   * @param arcWidth
   *          the arc width
   * @param arcHeight
   *          the arc height
   */
  protected void doDrawRoundRect(final double x, final double y,
      final double width, final double height, final double arcWidth,
      final double arcHeight) {
    this.doDraw(new RoundRectangle2D.Double(x, y, width, height, arcWidth,
        arcHeight));
  }

  /** {@inheritDoc} */
  @Override
  public final void drawRoundRect(final double x, final double y,
      final double width, final double height, final double arcWidth,
      final double arcHeight) {
    final int a, b, c, d, e, f, g, h;

    this.before(AbstractGraphic.BEFORE_OUTLINE);

    if (this.autoConvertCoordinatesToInt()) {
      c = ((int) width);
      if ((width >= Integer.MIN_VALUE) && (width <= Integer.MAX_VALUE)
          && (width == c)) {
        d = ((int) height);
        if ((height >= Integer.MIN_VALUE) && (height <= Integer.MAX_VALUE)
            && (height == d)) {
          a = ((int) x);
          if ((x >= Integer.MIN_VALUE) && (x <= Integer.MAX_VALUE)
              && (x == a)) {
            b = ((int) y);
            if ((y >= Integer.MIN_VALUE) && (y <= Integer.MAX_VALUE)
                && (y == b)) {

              e = ((int) arcWidth);
              if ((arcWidth >= Integer.MIN_VALUE)
                  && (arcWidth <= Integer.MAX_VALUE) && (arcWidth == e)) {

                f = ((int) arcHeight);
                if ((arcHeight >= Integer.MIN_VALUE)
                    && (arcHeight <= Integer.MAX_VALUE)
                    && (arcHeight == f)) {

                  if ((c == 0) || (d == 0)) {
                    g = (a + c);
                    if (g == AbstractGraphic.__add(a, c)) {
                      h = (b + d);
                      if (h == AbstractGraphic.__add(b, d)) {
                        this.doDrawLine(a, b, g, h);
                        return;
                      }
                    }
                    this.doDrawRect(a, b, c, d);
                    return;
                  }

                  if ((f == 0) || (e == 0)) {
                    this.doDrawRect(a, b, c, d);
                    return;
                  }

                  this.doDrawRoundRect(a, b, c, d, e, f);
                  return;
                }

              }
            }
          }
        }
      }
    }

    if ((width == 0d) || (height == 0d)) {
      this.doDrawLine(x, y, (x + width), (y + width));
    } else {
      if ((arcWidth == 0d) || (arcHeight == 0d)) {
        this.doDrawRect(x, y, width, height);
      } else {
        this.doDrawRoundRect(x, y, width, height, arcWidth, arcHeight);
      }
    }
  }

  /**
   * fill a round rectangle
   *
   * @param x
   *          the start x-coordinate
   * @param y
   *          the start y-coordinate
   * @param width
   *          the width
   * @param height
   *          the height
   * @param arcWidth
   *          the arc width
   * @param arcHeight
   *          the arc height
   */
  protected void doFillRoundRect(final double x, final double y,
      final double width, final double height, final double arcWidth,
      final double arcHeight) {
    this.doFill(new RoundRectangle2D.Double(x, y, width, height, arcWidth,
        arcHeight));
  }

  /** {@inheritDoc} */
  @Override
  public final void fillRoundRect(final double x, final double y,
      final double width, final double height, final double arcWidth,
      final double arcHeight) {
    final int a, b, c, d, e, f;

    c = ((int) width);
    if ((width >= Integer.MIN_VALUE) && (width <= Integer.MAX_VALUE)
        && (width == c)) {
      if (c == 0) {
        return;
      }
      d = ((int) height);
      if ((height >= Integer.MIN_VALUE) && (height <= Integer.MAX_VALUE)
          && (height == d)) {
        if (d == 0) {
          return;
        }
        if (this.autoConvertCoordinatesToInt()) {
          a = ((int) x);
          if ((x >= Integer.MIN_VALUE) && (x <= Integer.MAX_VALUE)
              && (x == a)) {
            b = ((int) y);
            if ((y >= Integer.MIN_VALUE) && (y <= Integer.MAX_VALUE)
                && (y == b)) {

              e = ((int) arcWidth);
              if ((arcWidth >= Integer.MIN_VALUE)
                  && (arcWidth <= Integer.MAX_VALUE) && (arcWidth == e)) {

                f = ((int) arcHeight);
                if ((arcHeight >= Integer.MIN_VALUE)
                    && (arcHeight <= Integer.MAX_VALUE)
                    && (arcHeight == f)) {

                  this.before(AbstractGraphic.BEFORE_FILL);

                  if ((f == 0) || (e == 0)) {
                    this.doFillRect(a, b, c, d);
                    return;
                  }

                  this.doFillRoundRect(a, b, c, d, e, f);
                  return;
                }

              }
            }
          }
        }
      }
    }

    if ((width == 0) || (height == 0)) {
      return;
    }
    this.before(AbstractGraphic.BEFORE_FILL);

    if ((arcWidth == 0) || (arcHeight == 0)) {
      this.doFillRect(x, y, width, height);
    } else {
      this.doFillRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

  }

  /**
   * Draw an oval
   *
   * @param x
   *          the x-coordinate
   * @param y
   *          the y-coordinate
   * @param width
   *          the width
   * @param height
   *          the height
   */
  protected void doDrawOval(final double x, final double y,
      final double width, final double height) {
    this.doDraw(new Ellipse2D.Double(x, y, width, height));
  }

  /** {@inheritDoc} */
  @Override
  public final void drawOval(final double x, final double y,
      final double width, final double height) {
    final int a, b, c, d, g, h;

    this.before(AbstractGraphic.BEFORE_OUTLINE);

    if (this.autoConvertCoordinatesToInt()) {
      c = ((int) width);
      if ((width >= Integer.MIN_VALUE) && (width <= Integer.MAX_VALUE)
          && (width == c)) {
        d = ((int) height);
        if ((height >= Integer.MIN_VALUE) && (height <= Integer.MAX_VALUE)
            && (height == d)) {
          a = ((int) x);
          if ((x >= Integer.MIN_VALUE) && (x <= Integer.MAX_VALUE)
              && (x == a)) {
            b = ((int) y);
            if ((y >= Integer.MIN_VALUE) && (y <= Integer.MAX_VALUE)
                && (y == b)) {

              if ((c == 0) || (d == 0)) {
                g = (a + c);
                if (g == AbstractGraphic.__add(a, c)) {
                  h = (b + d);
                  if (h == AbstractGraphic.__add(b, d)) {
                    this.doDrawLine(a, b, g, h);
                    return;
                  }
                }
                this.doDrawRect(a, b, c, d);
                return;
              }

              this.doDrawOval(a, b, c, d);
              return;
            }
          }
        }
      }
    }

    if ((width == 0) || (height == 0)) {
      this.doDrawLine(x, y, (x + width), (y + height));
    } else {
      this.doDrawOval(x, y, width, height);
    }
  }

  /**
   * Fill an oval
   *
   * @param x
   *          the x-coordinate
   * @param y
   *          the y-coordinate
   * @param width
   *          the width
   * @param height
   *          the height
   */
  protected void doFillOval(final double x, final double y,
      final double width, final double height) {
    this.doFill(new Ellipse2D.Double(x, y, width, height));
  }

  /** {@inheritDoc} */
  @Override
  public final void fillOval(final double x, final double y,
      final double width, final double height) {
    final int a, b, c, d;

    c = ((int) width);
    if ((width >= Integer.MIN_VALUE) && (width <= Integer.MAX_VALUE)
        && (width == c)) {
      if (c == 0) {
        return;
      }
      d = ((int) height);
      if ((height >= Integer.MIN_VALUE) && (height <= Integer.MAX_VALUE)
          && (height == d)) {
        if (d == 0) {
          return;
        }
        if (this.autoConvertCoordinatesToInt()) {
          a = ((int) x);
          if ((x >= Integer.MIN_VALUE) && (x <= Integer.MAX_VALUE)
              && (x == a)) {
            b = ((int) y);
            if ((y >= Integer.MIN_VALUE) && (y <= Integer.MAX_VALUE)
                && (y == b)) {
              this.doFillOval(a, b, c, d);
              return;
            }
          }
        }
      }
    }

    if ((width != 0) && (height != 0)) {
      this.doFillOval(x, y, width, height);
    }
  }

  /**
   * Draw an arc
   *
   * @param x
   *          the x-coordinate
   * @param y
   *          the y-coordinate
   * @param width
   *          the width
   * @param height
   *          the height
   * @param startAngle
   *          the starting angle
   * @param arcAngle
   *          the angle
   */
  protected void doDrawArc(final double x, final double y,
      final double width, final double height, final double startAngle,
      final double arcAngle) {
    this.doDraw(new Arc2D.Double(x, y, width, height, startAngle, arcAngle,
        Arc2D.OPEN));
  }

  /** {@inheritDoc} */
  @Override
  public final void drawArc(final double x, final double y,
      final double width, final double height, final double startAngle,
      final double arcAngle) {
    final int a, b, c, d, e, f, g, h;

    this.before(AbstractGraphic.BEFORE_OUTLINE);

    if (this.autoConvertCoordinatesToInt()) {
      c = ((int) width);
      if ((width >= Integer.MIN_VALUE) && (width <= Integer.MAX_VALUE)
          && (width == c)) {
        d = ((int) height);
        if ((height >= Integer.MIN_VALUE) && (height <= Integer.MAX_VALUE)
            && (height == d)) {
          a = ((int) x);
          if ((x >= Integer.MIN_VALUE) && (x <= Integer.MAX_VALUE)
              && (x == a)) {
            b = ((int) y);
            if ((y >= Integer.MIN_VALUE) && (y <= Integer.MAX_VALUE)
                && (y == b)) {

              f = ((int) arcAngle);
              if ((arcAngle >= Integer.MIN_VALUE)
                  && (arcAngle <= Integer.MAX_VALUE) && (arcAngle == f)) {

                if ((c == 0) || (d == 0) || (f == 0)) {
                  g = (a + c);
                  if (g == AbstractGraphic.__add(a, c)) {
                    h = (b + d);
                    if (h == AbstractGraphic.__add(b, d)) {
                      this.doDrawLine(a, b, g, h);
                      return;
                    }
                  }
                  this.doDrawRect(a, b, c, d);
                  return;
                }

                e = ((int) startAngle);
                if ((startAngle >= Integer.MIN_VALUE)
                    && (startAngle <= Integer.MAX_VALUE)
                    && (startAngle == e)) {
                  this.doDrawArc(a, b, c, d, e, f);
                  return;
                }
              }
            }
          }
        }
      }
    }

    if ((width == 0d) || (height == 0d) || (arcAngle == 0d)) {
      this.doDrawLine(x, y, (x + width), (y + width));
    } else {
      this.doDrawArc(x, y, width, height, startAngle, arcAngle);
    }
  }

  /**
   * Fill an arc
   *
   * @param x
   *          the x-coordinate
   * @param y
   *          the y-coordinate
   * @param width
   *          the width
   * @param height
   *          the height
   * @param startAngle
   *          the starting angle
   * @param arcAngle
   *          the angle
   */
  protected void doFillArc(final double x, final double y,
      final double width, final double height, final double startAngle,
      final double arcAngle) {
    this.doFill(new Arc2D.Double(x, y, width, height, startAngle, arcAngle,
        Arc2D.CHORD));
  }

  /** {@inheritDoc} */
  @Override
  public final void fillArc(final double x, final double y,
      final double width, final double height, final double startAngle,
      final double arcAngle) {
    final int a, b, c, d, e, f;

    c = ((int) width);
    if ((width >= Integer.MIN_VALUE) && (width <= Integer.MAX_VALUE)
        && (width == c)) {
      if (c == 0) {
        return;
      }
      d = ((int) height);
      if ((height >= Integer.MIN_VALUE) && (height <= Integer.MAX_VALUE)
          && (height == d)) {
        if (d == 0) {
          return;
        }
        if (this.autoConvertCoordinatesToInt()) {
          f = ((int) arcAngle);
          if ((arcAngle >= Integer.MIN_VALUE)
              && (arcAngle <= Integer.MAX_VALUE) && (arcAngle == f)) {
            if (f == 0) {
              return;
            }

            a = ((int) x);
            if ((x >= Integer.MIN_VALUE) && (x <= Integer.MAX_VALUE)
                && (x == a)) {
              b = ((int) y);
              if ((y >= Integer.MIN_VALUE) && (y <= Integer.MAX_VALUE)
                  && (y == b)) {
                e = ((int) startAngle);
                if ((startAngle >= Integer.MIN_VALUE)
                    && (startAngle <= Integer.MAX_VALUE)
                    && (startAngle == e)) {
                  this.before(AbstractGraphic.BEFORE_FILL);
                  this.doFillArc(a, b, c, d, e, f);
                  return;
                }
              }
            }
          }
        }
      }
    }

    if ((width != 0d) && (height != 0d) && (arcAngle != 0d)) {
      this.before(AbstractGraphic.BEFORE_FILL);
      this.doFillArc(x, y, width, height, startAngle, arcAngle);
    }
  }

  /** {@inheritDoc} */
  @Override
  public Shape createShape(final double[] xPoints, final double[] yPoints,
      final int nPoints, final boolean close) {
    final GeneralPath path;

    path = new GeneralPath(Path2D.WIND_EVEN_ODD);
    if (nPoints > 0) {
      path.moveTo(((float) (xPoints[0])), ((float) (yPoints[0])));
      for (int i = 1; i < nPoints; i++) {
        path.lineTo(((float) (xPoints[i])), ((float) (yPoints[i])));
      }
      if (close) {
        path.closePath();
      }
    }
    return path;
  }

  /**
   * Draw a poly-line
   *
   * @param xPoints
   *          the x-points
   * @param yPoints
   *          the y-points
   * @param nPoints
   *          the number of points
   */
  protected void doDrawPolyline(final double[] xPoints,
      final double[] yPoints, final int nPoints) {
    this.doDraw(this.createShape(xPoints, yPoints, nPoints, false));
  }

  /** {@inheritDoc} */
  @Override
  public final void drawPolyline(final double[] xPoints,
      final double[] yPoints, final int nPoints) {
    if ((xPoints != null) && (yPoints != null) && (nPoints > 0)) {
      switch (nPoints) {
        case 1: {
          this.drawLine(xPoints[0], yPoints[0], xPoints[0], yPoints[0]);
          return;
        }
        case 2: {
          this.drawLine(xPoints[0], yPoints[0], xPoints[1], yPoints[1]);
          return;
        }
        default: {
          this.before(AbstractGraphic.BEFORE_OUTLINE);
          this.doDrawPolyline(xPoints, yPoints, nPoints);
        }
      }
    }
  }

  /**
   * Draw a polygon
   *
   * @param xPoints
   *          the x-points
   * @param yPoints
   *          the y-points
   * @param nPoints
   *          the number of points
   */
  protected void doDrawPolygon(final double xPoints[],
      final double yPoints[], final int nPoints) {
    this.doDraw(this.createShape(xPoints, yPoints, nPoints, true));
  }

  /** {@inheritDoc} */
  @Override
  public final void drawPolygon(final double xPoints[],
      final double yPoints[], final int nPoints) {
    if ((xPoints != null) && (yPoints != null) && (nPoints > 0)) {
      switch (nPoints) {
        case 1: {
          this.drawLine(xPoints[0], yPoints[0], xPoints[0], yPoints[0]);
          return;
        }
        case 2: {
          this.drawLine(xPoints[0], yPoints[0], xPoints[1], yPoints[1]);
          return;
        }
        default: {
          this.before(AbstractGraphic.BEFORE_OUTLINE);
          this.doDrawPolygon(xPoints, yPoints, nPoints);
        }
      }
    }
  }

  /**
   * Draw a polygon
   *
   * @param xPoints
   *          the x-points
   * @param yPoints
   *          the y-points
   * @param nPoints
   *          the number of points
   */
  protected void doFillPolygon(final double xPoints[],
      final double yPoints[], final int nPoints) {
    this.doFill(this.createShape(xPoints, yPoints, nPoints, true));
  }

  /** {@inheritDoc} */
  @Override
  public final void fillPolygon(final double xPoints[],
      final double yPoints[], final int nPoints) {
    if ((xPoints != null) && (yPoints != null) && (nPoints > 2)) {
      this.before(AbstractGraphic.BEFORE_FILL);
      this.doFillPolygon(xPoints, yPoints, nPoints);
    }
  }

  /**
   * draw the given characters
   *
   * @param data
   *          the character data
   * @param offset
   *          the offset
   * @param length
   *          the length
   * @param x
   *          the x-coordinate
   * @param y
   *          the y-coordinate
   */
  protected void doDrawChars(final char data[], final int offset,
      final int length, final double x, final double y) {
    try (final __Translated t = new __Translated(x, y)) {
      this.doDrawChars(data, offset, length, t.m_x, t.m_y);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void drawChars(final char[] data, final int offset,
      final int length, final double x, final double y) {
    final int a, b;

    if ((data != null) && (length > 0)) {
      this.before(AbstractGraphic.BEFORE_TEXT);
      if (this.autoConvertCoordinatesToInt()) {
        a = ((int) x);
        if ((x >= Integer.MIN_VALUE) && (x <= Integer.MAX_VALUE)
            && (x == a)) {
          b = ((int) y);
          if ((y >= Integer.MIN_VALUE) && (y <= Integer.MAX_VALUE)
              && (y == b)) {
            this.doDrawChars(data, offset, length, a, b);
            return;
          }
        }
      }
      this.doDrawChars(data, offset, length, x, y);
    }
  }

  /**
   * draw an image
   *
   * @param img
   *          the image
   * @param x
   *          the x-coordinate
   * @param y
   *          the y-coordinate
   * @param observer
   *          the observer
   * @return the result
   */
  protected boolean doDrawImage(final Image img, final double x,
      final double y, final ImageObserver observer) {
    try (final __Translated t = new __Translated(x, y)) {
      return this.doDrawImage(img, t.m_x, t.m_y, observer);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final boolean drawImage(final Image img, final double x,
      final double y, final ImageObserver observer) {
    final int a, b;

    if ((img != null) && (img.getWidth(observer) != 0)
        && (img.getHeight(observer) != 0)) {
      this.before(AbstractGraphic.BEFORE_IMAGE);
      if (this.autoConvertCoordinatesToInt()) {
        a = ((int) x);
        if ((x >= Integer.MIN_VALUE) && (x <= Integer.MAX_VALUE)
            && (x == a)) {
          b = ((int) y);
          if ((y >= Integer.MIN_VALUE) && (y <= Integer.MAX_VALUE)
              && (y == b)) {
            return this.doDrawImage(img, a, b, observer);
          }
        }
      }

      return this.doDrawImage(img, x, y, observer);
    }
    return false;
  }

  /**
   * Create an affine transform for scaling an image
   *
   * @param image
   *          the image
   * @param width
   *          the width
   * @param height
   *          the height
   * @param observer
   *          the observer
   * @return the transform
   */
  private final AffineTransform __scale(final Image image,
      final double width, final double height,
      final ImageObserver observer) {
    final int w, h;
    final AffineTransform t;

    w = image.getWidth(observer);
    h = image.getHeight(observer);
    if ((w <= 0) || (h <= 0)) {
      throw new IllegalArgumentException(//
          "Image not fully loaded or empty."); //$NON-NLS-1$
    }

    t = new AffineTransform();
    if ((w != width) || (h != height)) {
      t.scale((width / w), (height / h));
    }
    return t;
  }

  /**
   * draw an image
   *
   * @param img
   *          the image
   * @param x
   *          the x-coordinate
   * @param y
   *          the y-coordinate
   * @param width
   *          the width
   * @param height
   *          the height
   * @param observer
   *          the observer
   * @return the result
   */
  protected boolean doDrawImage(final Image img, final double x,
      final double y, final double width, final double height,
      final ImageObserver observer) {
    final AffineTransform translate;
    final boolean b;

    translate = this.getTransform();
    try {
      this.translate(x, y);
      b = this.doDrawImage(img, this.__scale(img, width, height, observer),
          observer);
    } finally {
      this.setTransform(translate);
    }
    return b;
  }

  /** {@inheritDoc} */
  @Override
  public boolean drawImage(final Image img, final double x, final double y,
      final double width, final double height,
      final ImageObserver observer) {
    final int a, b, c, d;

    if ((img != null) && (img.getWidth(observer) != 0)
        && (img.getHeight(observer) != 0)) {
      c = ((int) width);
      if ((width >= Integer.MIN_VALUE) && (width <= Integer.MAX_VALUE)
          && (width == c)) {
        if (c == 0) {
          return false;
        }
        d = ((int) height);
        if ((height >= Integer.MIN_VALUE) && (height <= Integer.MAX_VALUE)
            && (height == d)) {
          if (d == 0) {
            return false;
          }
          if (this.autoConvertCoordinatesToInt()) {
            a = ((int) x);
            if ((x >= Integer.MIN_VALUE) && (x <= Integer.MAX_VALUE)
                && (x == a)) {
              b = ((int) y);
              if ((y >= Integer.MIN_VALUE) && (y <= Integer.MAX_VALUE)
                  && (y == b)) {
                this.before(AbstractGraphic.BEFORE_IMAGE);
                return this.doDrawImage(img, a, b, c, d, observer);
              }
            }
          }
        }
      }

      if ((width != 0d) && (height != 0d)) {
        this.before(AbstractGraphic.BEFORE_IMAGE);
        return this.doDrawImage(img, x, y, width, height, observer);
      }
    }
    return false;
  }

  /**
   * draw an image
   *
   * @param img
   *          the image
   * @param x
   *          the x-coordinate
   * @param y
   *          the y-coordinate
   * @param observer
   *          the observer
   * @param bgcolor
   *          the background color
   * @return the result
   */
  protected boolean doDrawImage(final Image img, final double x,
      final double y, final Color bgcolor, final ImageObserver observer) {
    try (final __Translated t = new __Translated(x, y)) {
      return this.doDrawImage(img, t.m_x, t.m_y, bgcolor, observer);
    }
  }

  /** {@inheritDoc} */
  @Override
  public boolean drawImage(final Image img, final double x, final double y,
      final Color bgcolor, final ImageObserver observer) {
    final int a, b;

    if ((img != null) && (img.getWidth(observer) != 0)
        && (img.getHeight(observer) != 0)) {
      this.before(AbstractGraphic.BEFORE_IMAGE);

      if (this.autoConvertCoordinatesToInt()) {
        a = ((int) x);
        if ((x >= Integer.MIN_VALUE) && (x <= Integer.MAX_VALUE)
            && (x == a)) {
          b = ((int) y);
          if ((y >= Integer.MIN_VALUE) && (y <= Integer.MAX_VALUE)
              && (y == b)) {
            return this.doDrawImage(img, a, b, bgcolor, observer);
          }
        }
      }

      return this.doDrawImage(img, x, y, bgcolor, observer);
    }
    return false;
  }

  /**
   * draw an image
   *
   * @param img
   *          the image
   * @param x
   *          the x-coordinate
   * @param y
   *          the y-coordinate
   * @param width
   *          the width
   * @param height
   *          the height
   * @param bgcolor
   *          the background color
   * @param observer
   *          the observer
   * @return the result
   */
  protected boolean doDrawImage(final Image img, final double x,
      final double y, final double width, final double height,
      final Color bgcolor, final ImageObserver observer) {
    final Paint p;

    p = this.getPaint();
    this.setPaint(bgcolor);
    try {
      this.fillRect(x, y, width, height);
    } finally {
      this.setPaint(p);
    }

    return this.doDrawImage(img, x, y, width, height, observer);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean drawImage(final Image img, final double x,
      final double y, final double width, final double height,
      final Color bgcolor, final ImageObserver observer) {
    final int a, b, c, d;

    if ((img != null) && (img.getWidth(observer) != 0)
        && (img.getHeight(observer) != 0)) {
      c = ((int) width);
      if ((width >= Integer.MIN_VALUE) && (width <= Integer.MAX_VALUE)
          && (width == c)) {
        if (c == 0) {
          return false;
        }
        d = ((int) height);
        if ((height >= Integer.MIN_VALUE) && (height <= Integer.MAX_VALUE)
            && (height == d)) {
          if (d == 0) {
            return false;
          }
          if (this.autoConvertCoordinatesToInt()) {
            a = ((int) x);
            if ((x >= Integer.MIN_VALUE) && (x <= Integer.MAX_VALUE)
                && (x == a)) {
              b = ((int) y);
              if ((y >= Integer.MIN_VALUE) && (y <= Integer.MAX_VALUE)
                  && (y == b)) {
                this.before(AbstractGraphic.BEFORE_IMAGE);
                return this.doDrawImage(img, a, b, c, d, bgcolor,
                    observer);
              }
            }
          }
        }
      }

      if ((width != 0d) && (height != 0d)) {
        this.before(AbstractGraphic.BEFORE_IMAGE);
        return this.doDrawImage(img, x, y, width, height, bgcolor,
            observer);
      }
    }
    return false;
  }

  /**
   * TODO Draw an image
   *
   * @param img
   *          the specified image to be drawn. This method does nothing if
   *          {@code img} is null.
   * @param dx1
   *          the x coordinate of the first corner of the destination
   *          rectangle.
   * @param dy1
   *          the y coordinate of the first corner of the destination
   *          rectangle.
   * @param dx2
   *          the x coordinate of the second corner of the destination
   *          rectangle.
   * @param dy2
   *          the y coordinate of the second corner of the destination
   *          rectangle.
   * @param sx1
   *          the x coordinate of the first corner of the source rectangle.
   * @param sy1
   *          the y coordinate of the first corner of the source rectangle.
   * @param sx2
   *          the x coordinate of the second corner of the source
   *          rectangle.
   * @param sy2
   *          the y coordinate of the second corner of the source
   *          rectangle.
   * @param observer
   *          object to be notified as more of the image is scaled and
   *          converted.
   * @return {@code false} if the image pixels are still changing;
   *         {@code true} otherwise.
   */
  protected boolean doDrawImage(final Image img, final double dx1,
      final double dy1, final double dx2, final double dy2,
      final double sx1, final double sy1, final double sx2,
      final double sy2, final ImageObserver observer) {
    return this.doDrawImage(img, AbstractGraphic._c(dx1),
        AbstractGraphic._c(dy1), AbstractGraphic._c(dx2),
        AbstractGraphic._c(dy2), AbstractGraphic._c(sx1),
        AbstractGraphic._c(sy1), AbstractGraphic._c(sx2),
        AbstractGraphic._c(sy2), observer);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean drawImage(final Image img, final double dx1,
      final double dy1, final double dx2, final double dy2,
      final double sx1, final double sy1, final double sx2,
      final double sy2, final ImageObserver observer) {
    final int a, b, c, d, e, f, g, h;

    if ((img != null) && (img.getWidth(observer) != 0)
        && (img.getHeight(observer) != 0)) {

      a = ((int) dx1);
      if ((dx1 >= Integer.MIN_VALUE) && (dx1 <= Integer.MAX_VALUE)
          && (dx1 == a)) {
        c = ((int) dx2);
        if ((dx2 >= Integer.MIN_VALUE) && (dx2 <= Integer.MAX_VALUE)
            && (dx2 == c)) {
          if (a == c) {
            return false;
          }

          b = ((int) dy1);
          if ((dy1 >= Integer.MIN_VALUE) && (dy1 <= Integer.MAX_VALUE)
              && (dy1 == b)) {
            d = ((int) dy2);
            if ((dy2 >= Integer.MIN_VALUE) && (dy2 <= Integer.MAX_VALUE)
                && (dy2 == d)) {
              if (b == d) {
                return false;
              }
              if (this.autoConvertCoordinatesToInt()) {
                e = ((int) sx1);
                if ((sx1 >= Integer.MIN_VALUE)
                    && (sx1 <= Integer.MAX_VALUE) && (sx1 == e)) {
                  g = ((int) sx2);
                  if ((sx2 >= Integer.MIN_VALUE)
                      && (sx2 <= Integer.MAX_VALUE) && (sx2 == g)) {
                    f = ((int) sy1);
                    if ((sy1 >= Integer.MIN_VALUE)
                        && (sy1 <= Integer.MAX_VALUE) && (sy1 == f)) {
                      h = ((int) sy2);
                      if ((sy2 >= Integer.MIN_VALUE)
                          && (sy2 <= Integer.MAX_VALUE) && (sy2 == h)) {

                        this.before(AbstractGraphic.BEFORE_IMAGE);
                        this.doDrawImage(img, a, b, c, d, e, f, g, h,
                            observer);
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }

      if ((dx2 != dx1) && (dy2 != dy1)) {
        this.before(AbstractGraphic.BEFORE_IMAGE);
        this.doDrawImage(img, sx1, sy1, sx2, sy2, dx1, dy1, dx2, dy2,
            observer);
      }
    }
    return false;
  }

  /**
   * TODO Draw an image
   *
   * @param img
   *          the specified image to be drawn. This method does nothing if
   *          {@code img} is null.
   * @param dx1
   *          the x coordinate of the first corner of the destination
   *          rectangle.
   * @param dy1
   *          the y coordinate of the first corner of the destination
   *          rectangle.
   * @param dx2
   *          the x coordinate of the second corner of the destination
   *          rectangle.
   * @param dy2
   *          the y coordinate of the second corner of the destination
   *          rectangle.
   * @param sx1
   *          the x coordinate of the first corner of the source rectangle.
   * @param sy1
   *          the y coordinate of the first corner of the source rectangle.
   * @param sx2
   *          the x coordinate of the second corner of the source
   *          rectangle.
   * @param sy2
   *          the y coordinate of the second corner of the source
   *          rectangle.
   * @param observer
   *          object to be notified as more of the image is scaled and
   *          converted.
   * @param bgcolor
   *          the background color
   * @return {@code false} if the image pixels are still changing;
   *         {@code true} otherwise.
   */
  protected boolean doDrawImage(final Image img, final double dx1,
      final double dy1, final double dx2, final double dy2,
      final double sx1, final double sy1, final double sx2,
      final double sy2, final Color bgcolor,
      final ImageObserver observer) {
    return this.doDrawImage(img, AbstractGraphic._c(dx1),
        AbstractGraphic._c(dy1), AbstractGraphic._c(dx2),
        AbstractGraphic._c(dy2), AbstractGraphic._c(sx1),
        AbstractGraphic._c(sy1), AbstractGraphic._c(sx2),
        AbstractGraphic._c(sy2), bgcolor, observer);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean drawImage(final Image img, final double dx1,
      final double dy1, final double dx2, final double dy2,
      final double sx1, final double sy1, final double sx2,
      final double sy2, final Color bgcolor,
      final ImageObserver observer) {
    final int a, b, c, d, e, f, g, h;

    if ((img != null) && (img.getWidth(observer) != 0)
        && (img.getHeight(observer) != 0)) {

      a = ((int) dx1);
      if ((dx1 >= Integer.MIN_VALUE) && (dx1 <= Integer.MAX_VALUE)
          && (dx1 == a)) {
        c = ((int) dx2);
        if ((dx2 >= Integer.MIN_VALUE) && (dx2 <= Integer.MAX_VALUE)
            && (dx2 == c)) {
          if (a == c) {
            return false;
          }

          b = ((int) dy1);
          if ((dy1 >= Integer.MIN_VALUE) && (dy1 <= Integer.MAX_VALUE)
              && (dy1 == b)) {
            d = ((int) dy2);
            if ((dy2 >= Integer.MIN_VALUE) && (dy2 <= Integer.MAX_VALUE)
                && (dy2 == d)) {
              if (b == d) {
                return false;
              }
              if (this.autoConvertCoordinatesToInt()) {
                e = ((int) sx1);
                if ((sx1 >= Integer.MIN_VALUE)
                    && (sx1 <= Integer.MAX_VALUE) && (sx1 == e)) {
                  g = ((int) sx2);
                  if ((sx2 >= Integer.MIN_VALUE)
                      && (sx2 <= Integer.MAX_VALUE) && (sx2 == g)) {
                    f = ((int) sy1);
                    if ((sy1 >= Integer.MIN_VALUE)
                        && (sy1 <= Integer.MAX_VALUE) && (sy1 == f)) {
                      h = ((int) sy2);
                      if ((sy2 >= Integer.MIN_VALUE)
                          && (sy2 <= Integer.MAX_VALUE) && (sy2 == h)) {

                        this.before(AbstractGraphic.BEFORE_IMAGE);
                        this.doDrawImage(img, a, b, c, d, e, f, g, h,
                            bgcolor, observer);
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }

      if ((dx2 != dx1) && (dy2 != dy1)) {
        this.before(AbstractGraphic.BEFORE_IMAGE);
        this.doDrawImage(img, sx1, sy1, sx2, sy2, dx1, dy1, dx2, dy2,
            bgcolor, observer);
      }
    }
    return false;
  }

  /**
   * Perform a saturating add, i.e., an addition which does not wrap signs
   * or overflows.
   *
   * @param a
   *          the first integer
   * @param b
   *          the second integer
   * @return the result
   */
  private static final int __add(final int a, final int b) {
    final long res;

    res = ((long) a) + ((long) b);
    if (res >= Integer.MAX_VALUE) {
      return Integer.MAX_VALUE;
    }
    if (res <= Integer.MIN_VALUE) {
      return Integer.MIN_VALUE;
    }
    return ((int) res);
  }

  /** a translated and scaled context */
  private final class __Translated implements IScope {
    /** the preserved original transform */
    private final AffineTransform m_orig;

    /** the x-coordinate to use */
    final int m_x;
    /** the y-coordinate to use */
    final int m_y;

    /**
     * Translate the coordinate system
     *
     * @param x
     *          the translation along the x-axis
     * @param y
     *          the translation along the y-axis
     */
    __Translated(final double x, final double y) {
      super();

      final int xx, yy;

      if ((x <= Integer.MIN_VALUE) || (x >= Integer.MAX_VALUE)
          || (x != x)) {
        throw new IllegalArgumentException("Invalid x-coordinate " + x); //$NON-NLS-1$
      }
      if ((y <= Integer.MIN_VALUE) || (y >= Integer.MAX_VALUE)
          || (y != y)) {
        throw new IllegalArgumentException("Invalid y-coordinate " + y); //$NON-NLS-1$
      }

      xx = AbstractGraphic._c(x);
      yy = AbstractGraphic._c(y);

      if ((xx == x) && (yy == y)) {
        this.m_orig = null;
        this.m_x = xx;
        this.m_y = yy;
      } else {
        this.m_orig = AbstractGraphic.this.getTransform();
        AbstractGraphic.this.translate(x, y);
        this.m_x = 0;
        this.m_y = 0;
      }
    }

    /** {@inheritDoc} */
    @Override
    public final void close() {
      if (this.m_orig != null) {
        AbstractGraphic.this.setTransform(this.m_orig);
      }
    }
  }

  /** a translated and scaled context */
  private final class __TranslatedAndScaled implements IScope {
    /** the preserved original transform */
    private final AffineTransform m_orig;

    /** the x-coordinate */
    final int m_x;
    /** the y-coordinate */
    final int m_y;

    /** the width */
    final int m_w;
    /** the height */
    final int m_h;

    /**
     * create
     *
     * @param x
     *          the starting x-coordinate
     * @param y
     *          the starting y-coordinate
     * @param w
     *          the width
     * @param h
     *          the height
     */
    __TranslatedAndScaled(final double x, final double y, final double w,
        final double h) {
      super();

      final int xx, yy;
      double sw, sh;
      int ww, hh;
      final boolean needsTranslate;
      boolean needsW, needsH, needsScale;

      if ((x <= Integer.MIN_VALUE) || (x >= Integer.MAX_VALUE)
          || (x != x)) {
        throw new IllegalArgumentException("Invalid x-coordinate " + x); //$NON-NLS-1$
      }
      if ((y <= Integer.MIN_VALUE) || (y >= Integer.MAX_VALUE)
          || (y != y)) {
        throw new IllegalArgumentException("Invalid y-coordinate " + y); //$NON-NLS-1$
      }
      if ((w <= Integer.MIN_VALUE) || (w >= Integer.MAX_VALUE)
          || (w != w)) {
        throw new IllegalArgumentException("Invalid width " + w); //$NON-NLS-1$
      }
      if ((h <= Integer.MIN_VALUE) || (h >= Integer.MAX_VALUE)
          || (h != h)) {
        throw new IllegalArgumentException("Invalid height " + h); //$NON-NLS-1$
      }

      xx = AbstractGraphic._c(x);
      yy = AbstractGraphic._c(y);

      needsTranslate = ((xx != x) || (yy != y));
      if (needsTranslate) {
        this.m_x = 0;
        this.m_y = 0;
      } else {
        this.m_x = xx;
        this.m_y = yy;
      }

      this.m_w = ww = AbstractGraphic._l(w);
      needsW = (ww != w);
      if (needsW) {
        sw = (w / ww);
      } else {
        sw = 1d;
      }

      this.m_h = hh = AbstractGraphic._l(h);
      needsH = (hh != h);
      if (needsH) {
        sh = (h / hh);
      } else {
        sh = 1d;
      }

      needsScale = false;
      if (needsW || needsH) {
        needsScale = ((Math.abs(sw / sh) - 1d) < 0.02d);
        if (!needsScale) {
          if (Math.abs(sw - 1d) < 0.01d) {
            needsScale = true;
          } else {
            sw = 1d;
          }
          if (Math.abs(sh - 1d) < 0.01d) {
            needsScale = true;
          } else {
            sh = 1d;
          }
        }
      }

      if (needsTranslate || needsScale) {
        this.m_orig = AbstractGraphic.this.getTransform();

        if (needsTranslate) {
          AbstractGraphic.this.translate(x, y);
        }
        if (needsScale) {
          AbstractGraphic.this.scale(sw, sh);
        }

      } else {
        this.m_orig = null;
      }

    }

    /** {@inheritDoc} */
    @Override
    public final void close() {
      if (this.m_orig != null) {
        AbstractGraphic.this.setTransform(this.m_orig);
      }
    }
  }
}
