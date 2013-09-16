/* 
 * Copyright (c) 2011, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package javafx.scene.chart;

/**
Builder class for javafx.scene.chart.ValueAxis
@see javafx.scene.chart.ValueAxis
@deprecated This class is deprecated and will be removed in the next version
* @since JavaFX 2.0
*/
@javax.annotation.Generated("Generated by javafx.builder.processor.BuilderProcessor")
@Deprecated
public abstract class ValueAxisBuilder<T extends java.lang.Number, B extends javafx.scene.chart.ValueAxisBuilder<T, B>> extends javafx.scene.chart.AxisBuilder<T, B> {
    protected ValueAxisBuilder() {
    }
    
    
    private int __set;
    public void applyTo(javafx.scene.chart.ValueAxis<T> x) {
        super.applyTo(x);
        int set = __set;
        if ((set & (1 << 0)) != 0) x.setLowerBound(this.lowerBound);
        if ((set & (1 << 1)) != 0) x.setMinorTickCount(this.minorTickCount);
        if ((set & (1 << 2)) != 0) x.setMinorTickLength(this.minorTickLength);
        if ((set & (1 << 3)) != 0) x.setMinorTickVisible(this.minorTickVisible);
        if ((set & (1 << 4)) != 0) x.setTickLabelFormatter(this.tickLabelFormatter);
        if ((set & (1 << 5)) != 0) x.setUpperBound(this.upperBound);
    }
    
    private double lowerBound;
    /**
    Set the value of the {@link javafx.scene.chart.ValueAxis#getLowerBound() lowerBound} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B lowerBound(double x) {
        this.lowerBound = x;
        __set |= 1 << 0;
        return (B) this;
    }
    
    private int minorTickCount;
    /**
    Set the value of the {@link javafx.scene.chart.ValueAxis#getMinorTickCount() minorTickCount} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B minorTickCount(int x) {
        this.minorTickCount = x;
        __set |= 1 << 1;
        return (B) this;
    }
    
    private double minorTickLength;
    /**
    Set the value of the {@link javafx.scene.chart.ValueAxis#getMinorTickLength() minorTickLength} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B minorTickLength(double x) {
        this.minorTickLength = x;
        __set |= 1 << 2;
        return (B) this;
    }
    
    private boolean minorTickVisible;
    /**
    Set the value of the {@link javafx.scene.chart.ValueAxis#isMinorTickVisible() minorTickVisible} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B minorTickVisible(boolean x) {
        this.minorTickVisible = x;
        __set |= 1 << 3;
        return (B) this;
    }
    
    private javafx.util.StringConverter<T> tickLabelFormatter;
    /**
    Set the value of the {@link javafx.scene.chart.ValueAxis#getTickLabelFormatter() tickLabelFormatter} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B tickLabelFormatter(javafx.util.StringConverter<T> x) {
        this.tickLabelFormatter = x;
        __set |= 1 << 4;
        return (B) this;
    }
    
    private double upperBound;
    /**
    Set the value of the {@link javafx.scene.chart.ValueAxis#getUpperBound() upperBound} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B upperBound(double x) {
        this.upperBound = x;
        __set |= 1 << 5;
        return (B) this;
    }
    
}