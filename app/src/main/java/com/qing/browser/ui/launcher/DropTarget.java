package com.qing.browser.ui.launcher;

import android.graphics.Rect;

/**
 * Interface defining an object that can receive a drag.
 *
 */
public interface DropTarget {

    /**
     * Handle an object being dropped on the DropTarget
     * 
     * @param source DragSource where the drag started
     * @param x X coordinate of the drop location
     * @param y Y coordinate of the drop location
     * @param xOffset Horizontal offset with the object being dragged where the original
     *          touch happened
     * @param yOffset Vertical offset with the object being dragged where the original
     *          touch happened
     * @param dragView The DragView that's being dragged around on screen.
     * @param dragInfo Data associated with the object being dragged
     * 
     */
    void onDrop(DragSource source, int x, int y, int xOffset, int yOffset,
            DragView dragView, Object dragInfo);
    
    void onDragEnter(DragSource source, int x, int y, int xOffset, int yOffset,
            DragView dragView, Object dragInfo);

    void onDragOver(DragSource source, int x, int y, int xOffset, int yOffset,
            DragView dragView, Object dragInfo);

    void onDragExit(DragSource source, int x, int y, int xOffset, int yOffset,
            DragView dragView, Object dragInfo);

    /**
     * Check if a drop action can occur at, or near, the requested location.
     * This may be called repeatedly during a drag, so any calls should return
     * quickly.
     * 
     * @param source DragSource where the drag started
     * @param x X coordinate of the drop location
     * @param y Y coordinate of the drop location
     * @param xOffset Horizontal offset with the object being dragged where the
     *            original touch happened
     * @param yOffset Vertical offset with the object being dragged where the
     *            original touch happened
     * @param dragView The DragView that's being dragged around on screen.
     * @param dragInfo Data associated with the object being dragged
     * @return True if the drop will be accepted, false otherwise.
     */
    boolean acceptDrop(DragSource source, int x, int y, int xOffset, int yOffset,
            DragView dragView, Object dragInfo);

    /**
     * Estimate the surface area where this object would land if dropped at the
     * given location.
     * 
     * @param source DragSource where the drag started
     * @param x X coordinate of the drop location
     * @param y Y coordinate of the drop location
     * @param xOffset Horizontal offset with the object being dragged where the
     *            original touch happened
     * @param yOffset Vertical offset with the object being dragged where the
     *            original touch happened
     * @param dragView The DragView that's being dragged around on screen.
     * @param dragInfo Data associated with the object being dragged
     * @param recycle {@link Rect} object to be possibly recycled.
     * @return Estimated area that would be occupied if object was dropped at
     *         the given location. Should return null if no estimate is found,
     *         or if this target doesn't provide estimations.
     */
    Rect estimateDropLocation(DragSource source, int x, int y, int xOffset, int yOffset,
            DragView dragView, Object dragInfo, Rect recycle);

    // These methods are implemented in Views
    void getHitRect(Rect outRect);
    void getLocationOnScreen(int[] loc);
    int getLeft();
    int getTop();
}
