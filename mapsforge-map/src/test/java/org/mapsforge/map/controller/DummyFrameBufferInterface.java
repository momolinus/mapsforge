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
package org.mapsforge.map.controller;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.model.Dimension;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.map.viewinterfaces.FrameBufferInterface;

class DummyFrameBufferInterface implements FrameBufferInterface {
	Dimension dimension;
	float lastDiffX;
	float lastDiffY;
	Dimension lastMapViewDimension;
	float lastScaleFactor;

	@Override
	public void adjustMatrix(float diffX, float diffY, float scaleFactor, Dimension mapViewDimension) {
		this.lastDiffX = diffX;
		this.lastDiffY = diffY;
		this.lastScaleFactor = scaleFactor;
		this.lastMapViewDimension = mapViewDimension;
	}

	@Override
	public void frameFinished(MapPosition mapPosition) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Bitmap getDrawingBitmap() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setDimension(Dimension dimension) {
		this.dimension = dimension;
	}
}
