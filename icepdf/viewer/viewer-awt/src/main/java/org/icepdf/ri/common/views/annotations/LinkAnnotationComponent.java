/*
 * Copyright 2006-2017 ICEsoft Technologies Canada Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.icepdf.ri.common.views.annotations;

import org.icepdf.core.pobjects.Name;
import org.icepdf.core.pobjects.annotations.Annotation;
import org.icepdf.core.pobjects.annotations.LinkAnnotation;
import org.icepdf.ri.common.utility.annotation.properties.LinkAnnotationPanel;
import org.icepdf.ri.common.views.AbstractPageViewComponent;
import org.icepdf.ri.common.views.DocumentViewController;
import org.icepdf.ri.common.views.DocumentViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;

/**
 * The LinkAnnotationComponent encapsulates a LinkAnnotation objects.  It
 * also provides basic editing functionality such as resizing, moving and change
 * the border color and style.  The rollover effect can also be set to one
 * of the named states defined in the LinkAnnotation object. .
 * <br>
 * The Viewer RI implementation contains a LinkAnnotationPanel class which
 * can edit the various properties of this component.
 *
 * @see LinkAnnotationPanel
 * @since 5.0
 */
@SuppressWarnings("serial")
public class LinkAnnotationComponent extends AbstractAnnotationComponent {

    public LinkAnnotationComponent(Annotation annotation, DocumentViewController documentViewController,
                                   AbstractPageViewComponent pageViewComponent,
                                   DocumentViewModel documentViewModel) {
        super(annotation, documentViewController, pageViewComponent, documentViewModel);
        isShowInvisibleBorder = true;

        AnnotationPopup annotationPopup = new AnnotationPopup(this, documentViewController,
                getPageViewComponent(), documentViewModel);
        annotationPopup.buildGui();

        contextMenu = annotationPopup;
        // Add listener to components that can bring up popup menus.
        MouseListener popupListener = new LinkPopupListener(contextMenu);
        addMouseListener(popupListener);
    }

    private boolean isAnnotationEditable() {
        return ((documentViewModel.getViewToolMode() ==
                DocumentViewModel.DISPLAY_TOOL_SELECTION ||
                documentViewModel.getViewToolMode() ==
                        DocumentViewModel.DISPLAY_TOOL_LINK_ANNOTATION) &&
                !(annotation.getFlagReadOnly() || annotation.getFlagLocked() ||
                        annotation.getFlagInvisible() || annotation.getFlagHidden()));
    }

    public void paintComponent(Graphics g) {
        // sniff out tool bar state to set correct annotation border
        isEditable = isAnnotationEditable();

        // paint rollover effects.
        if (isMousePressed && !(documentViewModel.getViewToolMode() ==
                DocumentViewModel.DISPLAY_TOOL_SELECTION ||
                documentViewModel.getViewToolMode() ==
                        DocumentViewModel.DISPLAY_TOOL_LINK_ANNOTATION)) {
            Graphics2D gg2 = (Graphics2D) g;

            LinkAnnotation linkAnnotation = (LinkAnnotation) annotation;
            Name highlightMode = linkAnnotation.getHighlightMode();
            Rectangle2D rect = new Rectangle(0, 0, getWidth(), getHeight());
            if (LinkAnnotation.HIGHLIGHT_INVERT.equals(highlightMode)) {
                gg2.setColor(annotationHighlightColor);
                gg2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                        annotationHighlightAlpha));
                gg2.fillRect((int) rect.getX(),
                        (int) rect.getY(),
                        (int) rect.getWidth(),
                        (int) rect.getHeight());
            } else if (LinkAnnotation.HIGHLIGHT_OUTLINE.equals(highlightMode)) {
                gg2.setColor(annotationHighlightColor);
                gg2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                        annotationHighlightAlpha));
                gg2.drawRect((int) rect.getX(),
                        (int) rect.getY(),
                        (int) rect.getWidth(),
                        (int) rect.getHeight());
            } else if (LinkAnnotation.HIGHLIGHT_PUSH.equals(highlightMode)) {
                gg2.setColor(annotationHighlightColor);
                gg2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                        annotationHighlightAlpha));
                gg2.drawRect((int) rect.getX(),
                        (int) rect.getY(),
                        (int) rect.getWidth(),
                        (int) rect.getHeight());
            }
        }
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void resetAppearanceShapes() {

    }

    public class LinkPopupListener extends PopupListener {

        public LinkPopupListener(JPopupMenu contextMenu) {
            super(contextMenu);
        }

        protected void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger() && isAnnotationEditable()) {
                contextMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }
}
