/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.mapsforge.map.layer.renderer;

import java.util.List;

import org.mapsforge.core.model.Point;
import org.mapsforge.map.android.graphics.AndroidGraphics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * A CanvasRasterer uses a Canvas for drawing.
 * 
 * @see <a href="http://developer.android.com/reference/android/graphics/Canvas.html">Canvas</a>
 */
class CanvasRasterer {
	private static final android.graphics.Paint PAINT_BITMAP_FILTER = createAndroidPaint();

	private static android.graphics.Paint createAndroidPaint() {
		return new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
	}

	private final Canvas canvas;
	private final Path path;
	private final Matrix symbolMatrix;

	CanvasRasterer() {
		this.canvas = new Canvas();
		this.symbolMatrix = new Matrix();
		this.path = new Path();
		this.path.setFillType(Path.FillType.EVEN_ODD);
	}

	void drawNodes(List<PointTextContainer> pointTextContainers) {
		for (int index = pointTextContainers.size() - 1; index >= 0; --index) {
			PointTextContainer pointTextContainer = pointTextContainers.get(index);

			if (pointTextContainer.paintBack != null) {
				Paint androidPaint = AndroidGraphics.getPaint(pointTextContainer.paintBack);
				this.canvas.drawText(pointTextContainer.text, (float) pointTextContainer.x,
						(float) pointTextContainer.y, androidPaint);
			}

			Paint androidPaint = AndroidGraphics.getPaint(pointTextContainer.paintFront);
			this.canvas.drawText(pointTextContainer.text, (float) pointTextContainer.x, (float) pointTextContainer.y,
					androidPaint);
		}
	}

	void drawSymbols(List<SymbolContainer> symbolContainers) {
		for (int index = symbolContainers.size() - 1; index >= 0; --index) {
			SymbolContainer symbolContainer = symbolContainers.get(index);

			Point point = symbolContainer.point;
			if (symbolContainer.alignCenter) {
				int pivotX = symbolContainer.symbol.getWidth() / 2;
				int pivotY = symbolContainer.symbol.getHeight() / 2;
				this.symbolMatrix.setRotate(symbolContainer.rotation, pivotX, pivotY);
				this.symbolMatrix.postTranslate((float) (point.x - pivotX), (float) (point.y - pivotY));
			} else {
				this.symbolMatrix.setRotate(symbolContainer.rotation);
				this.symbolMatrix.postTranslate((float) point.x, (float) point.y);
			}

			Bitmap androidBitmap = AndroidGraphics.getBitmap(symbolContainer.symbol);
			this.canvas.drawBitmap(androidBitmap, this.symbolMatrix, PAINT_BITMAP_FILTER);
		}
	}

	void drawWayNames(List<WayTextContainer> wayTextContainers) {
		for (int index = wayTextContainers.size() - 1; index >= 0; --index) {
			WayTextContainer wayTextContainer = wayTextContainers.get(index);
			this.path.rewind();

			double[] textCoordinates = wayTextContainer.coordinates;
			this.path.moveTo((float) textCoordinates[0], (float) textCoordinates[1]);
			for (int i = 2; i < textCoordinates.length; i += 2) {
				this.path.lineTo((float) textCoordinates[i], (float) textCoordinates[i + 1]);
			}

			Paint androidPaint = AndroidGraphics.getPaint(wayTextContainer.paint);
			this.canvas.drawTextOnPath(wayTextContainer.text, this.path, 0, 3, androidPaint);
		}
	}

	void drawWays(List<List<List<ShapePaintContainer>>> drawWays) {
		int levelsPerLayer = drawWays.get(0).size();

		for (int layer = 0, layers = drawWays.size(); layer < layers; ++layer) {
			List<List<ShapePaintContainer>> shapePaintContainers = drawWays.get(layer);

			for (int level = 0; level < levelsPerLayer; ++level) {
				List<ShapePaintContainer> wayList = shapePaintContainers.get(level);

				for (int index = wayList.size() - 1; index >= 0; --index) {
					ShapePaintContainer shapePaintContainer = wayList.get(index);
					this.path.rewind();

					switch (shapePaintContainer.shapeContainer.getShapeType()) {
						case CIRCLE:
							CircleContainer circleContainer = (CircleContainer) shapePaintContainer.shapeContainer;
							Point point = circleContainer.point;
							this.path.addCircle((float) point.x, (float) point.y, circleContainer.radius,
									Path.Direction.CCW);
							break;

						case WAY:
							WayContainer wayContainer = (WayContainer) shapePaintContainer.shapeContainer;
							Point[][] coordinates = wayContainer.coordinates;
							for (int j = 0; j < coordinates.length; ++j) {
								// make sure that the coordinates sequence is not empty
								Point[] points = coordinates[j];
								if (points.length >= 2) {
									Point immutablePoint = points[0];
									this.path.moveTo((float) immutablePoint.x, (float) immutablePoint.y);
									for (int i = 1; i < points.length; ++i) {
										immutablePoint = points[i];
										this.path.lineTo((float) immutablePoint.x, (float) immutablePoint.y);
									}
								}
							}
							break;
					}

					Paint androidPaint = AndroidGraphics.getPaint(shapePaintContainer.paint);
					this.canvas.drawPath(this.path, androidPaint);
				}
			}
		}
	}

	void fill(int color) {
		this.canvas.drawColor(color);
	}

	void setCanvasBitmap(Bitmap bitmap) {
		this.canvas.setBitmap(bitmap);
	}
}
