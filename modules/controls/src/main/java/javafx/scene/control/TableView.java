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

package javafx.scene.control;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.sun.javafx.scene.control.Logging;
import javafx.beans.DefaultProperty;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.collections.transformation.SortedList;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.util.Callback;

import com.sun.javafx.collections.MappingChange;
import com.sun.javafx.collections.NonIterableChange;
import com.sun.javafx.collections.annotations.ReturnsUnmodifiableCollection;
import com.sun.javafx.css.converters.SizeConverter;
import com.sun.javafx.scene.control.ReadOnlyUnbackedObservableList;
import com.sun.javafx.scene.control.TableColumnComparatorBase.TableColumnComparator;
import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.TableViewSkinBase;

/**
 * The TableView control is designed to visualize an unlimited number of rows
 * of data, broken out into columns. A TableView is therefore very similar to the
 * {@link ListView} control, with the addition of support for columns. For an
 * example on how to create a TableView, refer to the 'Creating a TableView'
 * control section below.
 *
 * <p>The TableView control has a number of features, including:
 * <ul>
 * <li>Powerful {@link TableColumn} API:
 *   <ul>
 *   <li>Support for {@link TableColumn#cellFactoryProperty() cell factories} to
 *      easily customize {@link Cell cell} contents in both rendering and editing
 *      states.
 *   <li>Specification of {@link #minWidthProperty() minWidth}/
 *      {@link #prefWidthProperty() prefWidth}/{@link #maxWidthProperty() maxWidth},
 *      and also {@link TableColumn#resizableProperty() fixed width columns}.
 *   <li>Width resizing by the user at runtime.
 *   <li>Column reordering by the user at runtime.
 *   <li>Built-in support for {@link TableColumn#getColumns() column nesting}
 *   </ul>
 * <li>Different {@link #columnResizePolicyProperty() resizing policies} to 
 *      dictate what happens when the user resizes columns.
 * <li>Support for {@link #getSortOrder() multiple column sorting} by clicking 
 *      the column header (hold down Shift keyboard key whilst clicking on a 
 *      header to sort by multiple columns).
 * </ul>
 * </p>
 *
 * <p>Note that TableView is intended to be used to visualize data - it is not
 * intended to be used for laying out your user interface. If you want to lay
 * your user interface out in a grid-like fashion, consider the 
 * {@link javafx.scene.layout.GridPane} layout instead.</p>
 *
 * <h2>Creating a TableView</h2>
 *
 * <p>Creating a TableView is a multi-step process, and also depends on the
 * underlying data model needing to be represented. For this example we'll use
 * an ObservableList<Person>, as it is the simplest way of showing data in a 
 * TableView. The <code>Person</code> class will consist of a first
 * name and last name properties. That is:
 * 
 * <pre>
 * {@code
 * public class Person {
 *     private StringProperty firstName;
 *     public void setFirstName(String value) { firstNameProperty().set(value); }
 *     public String getFirstName() { return firstNameProperty().get(); }
 *     public StringProperty firstNameProperty() { 
 *         if (firstName == null) firstName = new SimpleStringProperty(this, "firstName");
 *         return firstName; 
 *     }
 * 
 *     private StringProperty lastName;
 *     public void setLastName(String value) { lastNameProperty().set(value); }
 *     public String getLastName() { return lastNameProperty().get(); }
 *     public StringProperty lastNameProperty() { 
 *         if (lastName == null) lastName = new SimpleStringProperty(this, "lastName");
 *         return lastName; 
 *     } 
 * }}</pre>
 * 
 * <p>Firstly, a TableView instance needs to be defined, as such:
 * 
 * <pre>
 * {@code
 * TableView<Person> table = new TableView<Person>();}</pre>
 *
 * <p>With the basic table defined, we next focus on the data model. As mentioned,
 * for this example, we'll be using a ObservableList<Person>. We can immediately
 * set such a list directly in to the TableView, as such:
 *
 * <pre>
 * {@code
 * ObservableList<Person> teamMembers = getTeamMembers();
 * table.setItems(teamMembers);}</pre>
 * 
 * <p>With the items set as such, TableView will automatically update whenever
 * the <code>teamMembers</code> list changes. If the items list is available
 * before the TableView is instantiated, it is possible to pass it directly into
 * the constructor. 
 * 
 * <p>At this point we now have a TableView hooked up to observe the 
 * <code>teamMembers</code> observableList. The missing ingredient 
 * now is the means of splitting out the data contained within the model and 
 * representing it in one or more {@link TableColumn TableColumn} instances. To 
 * create a two-column TableView to show the firstName and lastName properties,
 * we extend the last code sample as follows:
 * 
 * <pre>
 * {@code
 * ObservableList<Person> teamMembers = ...;
 * table.setItems(teamMembers);
 * 
 * TableColumn<Person,String> firstNameCol = new TableColumn<Person,String>("First Name");
 * firstNameCol.setCellValueFactory(new PropertyValueFactory("firstName"));
 * TableColumn<Person,String> lastNameCol = new TableColumn<Person,String>("Last Name");
 * lastNameCol.setCellValueFactory(new PropertyValueFactory("lastName"));
 * 
 * table.getColumns().setAll(firstNameCol, lastNameCol);}</pre>
 * 
 * <p>With the code shown above we have fully defined the minimum properties
 * required to create a TableView instance. Running this code (assuming the
 * people ObservableList is appropriately created) will result in a TableView being
 * shown with two columns for firstName and lastName. Any other properties of the
 * Person class will not be shown, as no TableColumns are defined.
 * 
 * <h3>TableView support for classes that don't contain properties</h3>
 *
 * <p>The code shown above is the shortest possible code for creating a TableView
 * when the domain objects are designed with JavaFX properties in mind 
 * (additionally, {@link javafx.scene.control.cell.PropertyValueFactory} supports
 * normal JavaBean properties too, although there is a caveat to this, so refer 
 * to the class documentation for more information). When this is not the case, 
 * it is necessary to provide a custom cell value factory. More information
 * about cell value factories can be found in the {@link TableColumn} API 
 * documentation, but briefly, here is how a TableColumn could be specified:
 * 
 * <pre>
 * {@code
 * firstNameCol.setCellValueFactory(new Callback<CellDataFeatures<Person, String>, ObservableValue<String>>() {
 *     public ObservableValue<String> call(CellDataFeatures<Person, String> p) {
 *         // p.getValue() returns the Person instance for a particular TableView row
 *         return p.getValue().firstNameProperty();
 *     }
 *  });
 * }}</pre>
 * 
 * <h3>TableView Selection / Focus APIs</h3>
 * <p>To track selection and focus, it is necessary to become familiar with the
 * {@link SelectionModel} and {@link FocusModel} classes. A TableView has at most
 * one instance of each of these classes, available from 
 * {@link #selectionModelProperty() selectionModel} and 
 * {@link #focusModelProperty() focusModel} properties respectively.
 * Whilst it is possible to use this API to set a new selection model, in
 * most circumstances this is not necessary - the default selection and focus
 * models should work in most circumstances.
 * 
 * <p>The default {@link SelectionModel} used when instantiating a TableView is
 * an implementation of the {@link MultipleSelectionModel} abstract class. 
 * However, as noted in the API documentation for
 * the {@link MultipleSelectionModel#selectionModeProperty() selectionMode}
 * property, the default value is {@link SelectionMode#SINGLE}. To enable 
 * multiple selection in a default TableView instance, it is therefore necessary
 * to do the following:
 * 
 * <pre>
 * {@code 
 * tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);}</pre>
 *
 * <h3>Customizing TableView Visuals</h3>
 * <p>The visuals of the TableView can be entirely customized by replacing the 
 * default {@link #rowFactoryProperty() row factory}. A row factory is used to
 * generate {@link TableRow} instances, which are used to represent an entire
 * row in the TableView. 
 * 
 * <p>In many cases, this is not what is desired however, as it is more commonly
 * the case that cells be customized on a per-column basis, not a per-row basis.
 * It is therefore important to note that a {@link TableRow} is not a 
 * {@link TableCell}. A  {@link TableRow} is simply a container for zero or more
 * {@link TableCell}, and in most circumstances it is more likely that you'll 
 * want to create custom TableCells, rather than TableRows. The primary use case
 * for creating custom TableRow instances would most probably be to introduce
 * some form of column spanning support.
 * 
 * <p>You can create custom {@link TableCell} instances per column by assigning 
 * the appropriate function to the TableColumn
 * {@link TableColumn#cellFactoryProperty() cell factory} property.
 * 
 * <p>See the {@link Cell} class documentation for a more complete
 * description of how to write custom Cells.
 *
 * <h3>Sorting</h3>
 * <p>Prior to JavaFX 8.0, the TableView control would treat the
 * {@link #getItems() items} list as the view model, meaning that any changes to
 * the list would be immediately reflected visually. TableView would also modify
 * the order of this list directly when a user initiated a sort. This meant that
 * (again, prior to JavaFX 8.0) it was not possible to have the TableView return
 * to an unsorted state (after iterating through ascending and descending
 * orders).</p>
 *
 * <p>Starting with JavaFX 8.0 (and the introduction of {@link SortedList}), it
 * is now possible to have the collection return to the unsorted state when
 * there are no columns as part of the TableView
 * {@link #getSortOrder() sort order}. To do this, you must create a SortedList
 * instance, and bind its
 * {@link javafx.collections.transformation.SortedList#comparatorProperty() comparator}
 * property to the TableView {@link #comparatorProperty() comparator} property,
 * list so:</p>
 *
 * <pre>
 * {@code
 * // create a SortedList based on the provided ObservableList
 * SortedList sortedList = new SortedList(FXCollections.observableArrayList(2, 1, 3));
 *
 * // create a TableView with the sorted list set as the items it will show
 * final TableView<Integer> tableView = new TableView<>(sortedList);
 *
 * // bind the sortedList comparator to the TableView comparator
 * sortedList.comparatorProperty().bind(tableView.comparatorProperty());
 *
 * // Don't forget to define columns!
 * }</pre>
 *
 * @see TableColumn
 * @see TablePosition
 * @param <S> The type of the objects contained within the TableView items list.
 * @since JavaFX 2.0
 */
@DefaultProperty("items")
public class TableView<S> extends Control {
    
    /***************************************************************************
     *                                                                         *
     * Static properties and methods                                           *
     *                                                                         *
     **************************************************************************/

    // strings used to communicate via the TableView properties map between
    // the control and the skin. Because they are private here, the strings
    // are also duplicated in the TableViewSkin class - so any changes to these
    // strings must also be duplicated there
    static final String SET_CONTENT_WIDTH = "TableView.contentWidth";
    
    /**
     * <p>Very simple resize policy that just resizes the specified column by the
     * provided delta and shifts all other columns (to the right of the given column)
     * further to the right (when the delta is positive) or to the left (when the
     * delta is negative).
     *
     * <p>It also handles the case where we have nested columns by sharing the new space,
     * or subtracting the removed space, evenly between all immediate children columns.
     * Of course, the immediate children may themselves be nested, and they would
     * then use this policy on their children.
     */
    public static final Callback<ResizeFeatures, Boolean> UNCONSTRAINED_RESIZE_POLICY = new Callback<ResizeFeatures, Boolean>() {
        @Override public String toString() {
            return "unconstrained-resize";
        }
        
        @Override public Boolean call(ResizeFeatures prop) {
            double result = TableUtil.resize(prop.getColumn(), prop.getDelta());
            return Double.compare(result, 0.0) == 0;
        }
    };

    /**
     * <p>Simple policy that ensures the width of all visible leaf columns in 
     * this table sum up to equal the width of the table itself.
     * 
     * <p>When the user resizes a column width with this policy, the table automatically
     * adjusts the width of the right hand side columns. When the user increases a
     * column width, the table decreases the width of the rightmost column until it
     * reaches its minimum width. Then it decreases the width of the second
     * rightmost column until it reaches minimum width and so on. When all right
     * hand side columns reach minimum size, the user cannot increase the size of
     * resized column any more.
     */
    public static final Callback<ResizeFeatures, Boolean> CONSTRAINED_RESIZE_POLICY = new Callback<ResizeFeatures, Boolean>() {

        private boolean isFirstRun = true;
        
        @Override public String toString() {
            return "constrained-resize";
        }
        
        @Override public Boolean call(ResizeFeatures prop) {
            TableView<?> table = prop.getTable();
            List<? extends TableColumnBase<?,?>> visibleLeafColumns = table.getVisibleLeafColumns();
            Boolean result = TableUtil.constrainedResize(prop, 
                                               isFirstRun, 
                                               table.contentWidth,
                                               visibleLeafColumns);
            isFirstRun = ! isFirstRun ? false : ! result;
            return result;
        }
    };
    
    /**
     * The default {@link #sortPolicyProperty() sort policy} that this TableView
     * will use if no other policy is specified. The sort policy is a simple 
     * {@link Callback} that accepts a TableView as the sole argument and expects
     * a Boolean response representing whether the sort succeeded or not. A Boolean
     * response of true represents success, and a response of false (or null) will
     * be considered to represent failure.
     * @since JavaFX 8.0
     */
    public static final Callback<TableView, Boolean> DEFAULT_SORT_POLICY = new Callback<TableView, Boolean>() {
        @Override public Boolean call(TableView table) {
            try {
                ObservableList<?> itemsList = table.getItems();
                if (itemsList instanceof SortedList) {
                    // it is the responsibility of the SortedList to bind to the
                    // comparator provided by the TableView. However, we don't
                    // want to fail the sort (which would put the UI in an
                    // inconsistent state), so we return true here, but only if
                    // the SortedList has its comparator bound to the TableView
                    // comparator property.
                    SortedList sortedList = (SortedList) itemsList;
                    boolean comparatorsBound = sortedList.comparatorProperty().
                            isEqualTo(table.comparatorProperty()).get();

                    if (! comparatorsBound) {
                        // this isn't a good situation to be in, so lets log it
                        // out in case the developer is unaware
                        if (Logging.getControlsLogger().isEnabled()) {
                            String s = "TableView items list is a SortedList, but the SortedList " +
                                    "comparator should be bound to the TableView comparator for " +
                                    "sorting to be enabled (e.g. " +
                                    "sortedList.comparatorProperty().bind(tableView.comparatorProperty());).";
                            Logging.getControlsLogger().info(s);
                        }
                    }
                    return comparatorsBound;
                }

                Comparator comparator = table.getComparator();
                if (comparator == null) {
                    return true;
                }

                // otherwise we attempt to do a manual sort, and if successful
                // we return true
                FXCollections.sort(itemsList, comparator);
                return true;
            } catch (UnsupportedOperationException e) {
                // TODO might need to support other exception types including:
                // ClassCastException - if the class of the specified element prevents it from being added to this list
                // NullPointerException - if the specified element is null and this list does not permit null elements
                // IllegalArgumentException - if some property of this element prevents it from being added to this list

                // If we are here the list does not support sorting, so we gracefully 
                // fail the sort request and ensure the UI is put back to its previous
                // state. This is handled in the code that calls the sort policy.
                
                return false;
            }
        }
    };
    
    
    
    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a default TableView control with no content.
     * 
     * <p>Refer to the {@link TableView} class documentation for details on the
     * default state of other properties.
     */
    public TableView() {
        this(FXCollections.<S>observableArrayList());
    }

    /**
     * Creates a TableView with the content provided in the items ObservableList.
     * This also sets up an observer such that any changes to the items list
     * will be immediately reflected in the TableView itself.
     * 
     * <p>Refer to the {@link TableView} class documentation for details on the
     * default state of other properties.
     * 
     * @param items The items to insert into the TableView, and the list to watch
     *          for changes (to automatically show in the TableView).
     */
    public TableView(ObservableList<S> items) {
        getStyleClass().setAll(DEFAULT_STYLE_CLASS);

        // we quite happily accept items to be null here
        setItems(items);

        // install default selection and focus models
        // it's unlikely this will be changed by many users.
        setSelectionModel(new TableViewArrayListSelectionModel<S>(this));
        setFocusModel(new TableViewFocusModel<S>(this));

        // we watch the columns list, such that when it changes we can update
        // the leaf columns and visible leaf columns lists (which are read-only).
        getColumns().addListener(weakColumnsObserver);

        // watch for changes to the sort order list - and when it changes run
        // the sort method.
        getSortOrder().addListener(new ListChangeListener<TableColumn<S,?>>() {
            @Override public void onChanged(Change<? extends TableColumn<S,?>> c) {
                doSort(TableUtil.SortEventType.SORT_ORDER_CHANGE, c);
            }
        });

        // We're watching for changes to the content width such
        // that the resize policy can be run if necessary. This comes from
        // TreeViewSkin.
        getProperties().addListener(new MapChangeListener<Object, Object>() {
            @Override
            public void onChanged(Change<? extends Object, ? extends Object> c) {
                if (c.wasAdded() && SET_CONTENT_WIDTH.equals(c.getKey())) {
                    if (c.getValueAdded() instanceof Number) {
                        setContentWidth((Double) c.getValueAdded());
                    }
                    getProperties().remove(SET_CONTENT_WIDTH);
                }
            }
        });

        isInited = true;
    }

    
    
    /***************************************************************************
     *                                                                         *
     * Instance Variables                                                      *
     *                                                                         *
     **************************************************************************/    

    // this is the only publicly writable list for columns. This represents the
    // columns as they are given initially by the developer.
    private final ObservableList<TableColumn<S,?>> columns = FXCollections.observableArrayList();

    // Finally, as convenience, we also have an observable list that contains
    // only the leaf columns that are currently visible.
    private final ObservableList<TableColumn<S,?>> visibleLeafColumns = FXCollections.observableArrayList();
    private final ObservableList<TableColumn<S,?>> unmodifiableVisibleLeafColumns = FXCollections.unmodifiableObservableList(visibleLeafColumns);
    
    
    // Allows for multiple column sorting based on the order of the TableColumns
    // in this observableArrayList. Each TableColumn is responsible for whether it is
    // sorted using ascending or descending order.
    private ObservableList<TableColumn<S,?>> sortOrder = FXCollections.observableArrayList();

    // width of VirtualFlow minus the vbar width
    private double contentWidth;
    
    // Used to minimise the amount of work performed prior to the table being
    // completely initialised. In particular it reduces the amount of column
    // resize operations that occur, which slightly improves startup time.
    private boolean isInited = false;
    
    
    
    /***************************************************************************
     *                                                                         *
     * Callbacks and Events                                                    *
     *                                                                         *
     **************************************************************************/
    
    private final ListChangeListener<TableColumn<S,?>> columnsObserver = new ListChangeListener<TableColumn<S,?>>() {
        @Override public void onChanged(Change<? extends TableColumn<S,?>> c) {
            // We don't maintain a bind for leafColumns, we simply call this update
            // function behind the scenes in the appropriate places.
            updateVisibleLeafColumns();
            
            // Fix for RT-15194: Need to remove removed columns from the 
            // sortOrder list.
            List<TableColumn<S,?>> toRemove = new ArrayList<TableColumn<S,?>>();
            while (c.next()) {
                final List<? extends TableColumn<S, ?>> removed = c.getRemoved();
                final List<? extends TableColumn<S, ?>> added = c.getAddedSubList();
                
                if (c.wasRemoved()) {
                    toRemove.addAll(removed);
                    for (TableColumn<S,?> tc : removed) {
                        tc.setTableView(null);
                    }
                }
                
                if (c.wasAdded()) {
                    toRemove.removeAll(added);
                    for (TableColumn<S,?> tc : added) {
                        tc.setTableView(TableView.this);
                    }
                }
                
                // set up listeners
                TableUtil.removeColumnsListener(removed, weakColumnsObserver);
                TableUtil.addColumnsListener(added, weakColumnsObserver);
                
                TableUtil.removeTableColumnListener(c.getRemoved(),
                        weakColumnVisibleObserver,
                        weakColumnSortableObserver,
                        weakColumnSortTypeObserver,
                        weakColumnComparatorObserver);
                TableUtil.addTableColumnListener(c.getAddedSubList(),
                        weakColumnVisibleObserver,
                        weakColumnSortableObserver,
                        weakColumnSortTypeObserver,
                        weakColumnComparatorObserver);
            }
            
            sortOrder.removeAll(toRemove);
        }
    };
    
    private final InvalidationListener columnVisibleObserver = new InvalidationListener() {
        @Override public void invalidated(Observable valueModel) {
            updateVisibleLeafColumns();
        }
    };
    
    private final InvalidationListener columnSortableObserver = new InvalidationListener() {
        @Override public void invalidated(Observable valueModel) {
            Object col = ((Property<?>)valueModel).getBean();
            if (! getSortOrder().contains(col)) return;
            doSort(TableUtil.SortEventType.COLUMN_SORTABLE_CHANGE, col);
        }
    };

    private final InvalidationListener columnSortTypeObserver = new InvalidationListener() {
        @Override public void invalidated(Observable valueModel) {
            Object col = ((Property<?>)valueModel).getBean();
            if (! getSortOrder().contains(col)) return;
            doSort(TableUtil.SortEventType.COLUMN_SORT_TYPE_CHANGE, col);
        }
    };
    
    private final InvalidationListener columnComparatorObserver = new InvalidationListener() {
        @Override public void invalidated(Observable valueModel) {
            Object col = ((Property<?>)valueModel).getBean();
            if (! getSortOrder().contains(col)) return;
            doSort(TableUtil.SortEventType.COLUMN_COMPARATOR_CHANGE, col);
        }
    };
    
    /* proxy pseudo-class state change from selectionModel's cellSelectionEnabledProperty */
    private final InvalidationListener cellSelectionModelInvalidationListener = new InvalidationListener() {
        @Override public void invalidated(Observable o) {
            final boolean isCellSelection = ((BooleanProperty)o).get();
            pseudoClassStateChanged(PSEUDO_CLASS_CELL_SELECTION,  isCellSelection);
            pseudoClassStateChanged(PSEUDO_CLASS_ROW_SELECTION,  !isCellSelection);
        }
    };
    
    
    private final WeakInvalidationListener weakColumnVisibleObserver = 
            new WeakInvalidationListener(columnVisibleObserver);
    
    private final WeakInvalidationListener weakColumnSortableObserver = 
            new WeakInvalidationListener(columnSortableObserver);
    
    private final WeakInvalidationListener weakColumnSortTypeObserver = 
            new WeakInvalidationListener(columnSortTypeObserver);
    
    private final WeakInvalidationListener weakColumnComparatorObserver = 
            new WeakInvalidationListener(columnComparatorObserver);
    
    private final WeakListChangeListener<TableColumn<S,?>> weakColumnsObserver = 
            new WeakListChangeListener<TableColumn<S,?>>(columnsObserver);
    
    private final WeakInvalidationListener weakCellSelectionModelInvalidationListener = 
            new WeakInvalidationListener(cellSelectionModelInvalidationListener);
    
    /***************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/


    // --- Items
    /**
     * The underlying data model for the TableView. Note that it has a generic
     * type that must match the type of the TableView itself.
     */
    public final ObjectProperty<ObservableList<S>> itemsProperty() { return items; }
    private ObjectProperty<ObservableList<S>> items = 
        new SimpleObjectProperty<ObservableList<S>>(this, "items") {
            WeakReference<ObservableList<S>> oldItemsRef;

            // need to override set() rather than invalidated() as we need to
            // be notified of all changes (even when the item is the same, such
            // as in the case of a new empty items list replacing an old (but
            // equal) empty items list
            @Override public void set(ObservableList<S> newValue) {
                super.set(newValue);

                ObservableList<S> oldItems = oldItemsRef == null ? null : oldItemsRef.get();

                // FIXME temporary fix for RT-15793. This will need to be
                // properly fixed when time permits
                if (getSelectionModel() instanceof TableViewArrayListSelectionModel) {
                    ((TableViewArrayListSelectionModel<S>)getSelectionModel()).updateItemsObserver(oldItems, getItems());
                }
                if (getFocusModel() != null) {
                    getFocusModel().updateItemsObserver(oldItems, getItems());
                }
                if (getSkin() instanceof TableViewSkin) {
                    TableViewSkin<S> skin = (TableViewSkin<S>) getSkin();
                    skin.updateTableItems(oldItems, getItems());
                }

                oldItemsRef = new WeakReference<ObservableList<S>>(getItems());
            }
        };
    public final void setItems(ObservableList<S> value) { itemsProperty().set(value); }
    public final ObservableList<S> getItems() {return items.get(); }
    
    
    // --- Table menu button visible
    private BooleanProperty tableMenuButtonVisible;
    /**
     * This controls whether a menu button is available when the user clicks
     * in a designated space within the TableView, within which is a radio menu
     * item for each TableColumn in this table. This menu allows for the user to
     * show and hide all TableColumns easily.
     */
    public final BooleanProperty tableMenuButtonVisibleProperty() {
        if (tableMenuButtonVisible == null) {
            tableMenuButtonVisible = new SimpleBooleanProperty(this, "tableMenuButtonVisible");
        }
        return tableMenuButtonVisible;
    }
    public final void setTableMenuButtonVisible (boolean value) {
        tableMenuButtonVisibleProperty().set(value);
    }
    public final boolean isTableMenuButtonVisible() {
        return tableMenuButtonVisible == null ? false : tableMenuButtonVisible.get();
    }
    
    
    // --- Column Resize Policy
    private ObjectProperty<Callback<ResizeFeatures, Boolean>> columnResizePolicy;
    public final void setColumnResizePolicy(Callback<ResizeFeatures, Boolean> callback) {
        columnResizePolicyProperty().set(callback);
    }
    public final Callback<ResizeFeatures, Boolean> getColumnResizePolicy() {
        return columnResizePolicy == null ? UNCONSTRAINED_RESIZE_POLICY : columnResizePolicy.get();
    }

    /**
     * This is the function called when the user completes a column-resize
     * operation. The two most common policies are available as static functions
     * in the TableView class: {@link #UNCONSTRAINED_RESIZE_POLICY} and
     * {@link #CONSTRAINED_RESIZE_POLICY}.
     */
    public final ObjectProperty<Callback<ResizeFeatures, Boolean>> columnResizePolicyProperty() {
        if (columnResizePolicy == null) {
            columnResizePolicy = new SimpleObjectProperty<Callback<ResizeFeatures, Boolean>>(this, "columnResizePolicy", UNCONSTRAINED_RESIZE_POLICY) {
                private Callback<ResizeFeatures, Boolean> oldPolicy;
                
                @Override protected void invalidated() {
                    if (isInited) {
                        get().call(new ResizeFeatures(TableView.this, null, 0.0));
                        refresh();
                
                        if (oldPolicy != null) {
                            PseudoClass state = PseudoClass.getPseudoClass(oldPolicy.toString());
                            pseudoClassStateChanged(state, false);
                        }
                        if (get() != null) {
                            PseudoClass state = PseudoClass.getPseudoClass(get().toString());
                            pseudoClassStateChanged(state, true);
                        }
                        oldPolicy = get();
                    }
                }
            };
        }
        return columnResizePolicy;
    }
    
    
    // --- Row Factory
    private ObjectProperty<Callback<TableView<S>, TableRow<S>>> rowFactory;

    /**
     * A function which produces a TableRow. The system is responsible for
     * reusing TableRows. Return from this function a TableRow which
     * might be usable for representing a single row in a TableView.
     * <p>
     * Note that a TableRow is <b>not</b> a TableCell. A TableRow is
     * simply a container for a TableCell, and in most circumstances it is more
     * likely that you'll want to create custom TableCells, rather than
     * TableRows. The primary use case for creating custom TableRow
     * instances would most probably be to introduce some form of column
     * spanning support.
     * <p>
     * You can create custom TableCell instances per column by assigning the
     * appropriate function to the cellFactory property in the TableColumn class.
     */
    public final ObjectProperty<Callback<TableView<S>, TableRow<S>>> rowFactoryProperty() {
        if (rowFactory == null) {
            rowFactory = new SimpleObjectProperty<Callback<TableView<S>, TableRow<S>>>(this, "rowFactory");
        }
        return rowFactory;
    }
    public final void setRowFactory(Callback<TableView<S>, TableRow<S>> value) {
        rowFactoryProperty().set(value);
    }
    public final Callback<TableView<S>, TableRow<S>> getRowFactory() {
        return rowFactory == null ? null : rowFactory.get();
    }
    
    
    // --- Placeholder Node
    private ObjectProperty<Node> placeholder;
    /**
     * This Node is shown to the user when the table has no content to show.
     * This may be the case because the table model has no data in the first
     * place, that a filter has been applied to the table model, resulting
     * in there being nothing to show the user, or that there are no currently
     * visible columns.
     */
    public final ObjectProperty<Node> placeholderProperty() {
        if (placeholder == null) {
            placeholder = new SimpleObjectProperty<Node>(this, "placeholder");
        }
        return placeholder;
    }
    public final void setPlaceholder(Node value) {
        placeholderProperty().set(value);
    }
    public final Node getPlaceholder() {
        return placeholder == null ? null : placeholder.get();
    }


    // --- Selection Model
    private ObjectProperty<TableViewSelectionModel<S>> selectionModel 
            = new SimpleObjectProperty<TableViewSelectionModel<S>>(this, "selectionModel") {

        TableViewSelectionModel<S> oldValue = null;
        
        @Override protected void invalidated() {
            
            if (oldValue != null) {
                oldValue.cellSelectionEnabledProperty().removeListener(weakCellSelectionModelInvalidationListener);
            }
            
            oldValue = get();
            
            if (oldValue != null) {
                oldValue.cellSelectionEnabledProperty().addListener(weakCellSelectionModelInvalidationListener);
                // fake an invalidation to ensure updated pseudo-class state
                weakCellSelectionModelInvalidationListener.invalidated(oldValue.cellSelectionEnabledProperty());
            } 
        }
    };
    
    /**
     * The SelectionModel provides the API through which it is possible
     * to select single or multiple items within a TableView, as  well as inspect
     * which items have been selected by the user. Note that it has a generic
     * type that must match the type of the TableView itself.
     */
    public final ObjectProperty<TableViewSelectionModel<S>> selectionModelProperty() {
        return selectionModel;
    }
    public final void setSelectionModel(TableViewSelectionModel<S> value) {
        selectionModelProperty().set(value);
    }

    public final TableViewSelectionModel<S> getSelectionModel() {
        return selectionModel.get();
    }

    
    // --- Focus Model
    private ObjectProperty<TableViewFocusModel<S>> focusModel;
    public final void setFocusModel(TableViewFocusModel<S> value) {
        focusModelProperty().set(value);
    }
    public final TableViewFocusModel<S> getFocusModel() {
        return focusModel == null ? null : focusModel.get();
    }
    /**
     * Represents the currently-installed {@link TableViewFocusModel} for this
     * TableView. Under almost all circumstances leaving this as the default
     * focus model will suffice.
     */
    public final ObjectProperty<TableViewFocusModel<S>> focusModelProperty() {
        if (focusModel == null) {
            focusModel = new SimpleObjectProperty<TableViewFocusModel<S>>(this, "focusModel");
        }
        return focusModel;
    }
    
    
//    // --- Span Model
//    private ObjectProperty<SpanModel<S>> spanModel 
//            = new SimpleObjectProperty<SpanModel<S>>(this, "spanModel") {
//
//        @Override protected void invalidated() {
//            ObservableList<String> styleClass = getStyleClass();
//            if (getSpanModel() == null) {
//                styleClass.remove(CELL_SPAN_TABLE_VIEW_STYLE_CLASS);
//            } else if (! styleClass.contains(CELL_SPAN_TABLE_VIEW_STYLE_CLASS)) {
//                styleClass.add(CELL_SPAN_TABLE_VIEW_STYLE_CLASS);
//            }
//        }
//    };
//
//    public final ObjectProperty<SpanModel<S>> spanModelProperty() {
//        return spanModel;
//    }
//    public final void setSpanModel(SpanModel<S> value) {
//        spanModelProperty().set(value);
//    }
//
//    public final SpanModel<S> getSpanModel() {
//        return spanModel.get();
//    }
    
    // --- Editable
    private BooleanProperty editable;
    public final void setEditable(boolean value) {
        editableProperty().set(value);
    }
    public final boolean isEditable() {
        return editable == null ? false : editable.get();
    }
    /**
     * Specifies whether this TableView is editable - only if the TableView, the
     * TableColumn (if applicable) and the TableCells within it are both 
     * editable will a TableCell be able to go into their editing state.
     */
    public final BooleanProperty editableProperty() {
        if (editable == null) {
            editable = new SimpleBooleanProperty(this, "editable", false);
        }
        return editable;
    }


    // --- Fixed cell size
    private DoubleProperty fixedCellSize;

    /**
     * Sets the new fixed cell size for this control. Any value greater than
     * zero will enable fixed cell size mode, whereas a zero or negative value
     * (or Region.USE_COMPUTED_SIZE) will be used to disabled fixed cell size
     * mode.
     *
     * @param value The new fixed cell size value, or -1 (or Region.USE_COMPUTED_SIZE)
     *                  to disable.
     * @since JavaFX 8.0
     */
    public final void setFixedCellSize(double value) {
        fixedCellSizeProperty().set(value);
    }

    /**
     * Returns the fixed cell size value, which may be -1 to represent fixed cell
     * size mode is disabled, or a value greater than zero to represent the size
     * of all cells in this control.
     *
     * @return A double representing the fixed cell size of this control, or -1
     *      if fixed cell size mode is disabled.
     * @since JavaFX 8.0
     */
    public final double getFixedCellSize() {
        return fixedCellSize == null ? Region.USE_COMPUTED_SIZE : fixedCellSize.get();
    }
    /**
     * Specifies whether this control has cells that are a fixed height (of the
     * specified value). If this value is -1 (i.e. {@link Region#USE_COMPUTED_SIZE}),
     * then all cells are individually sized and positioned. This is a slow
     * operation. Therefore, when performance matters and developers are not
     * dependent on variable cell sizes it is a good idea to set the fixed cell
     * size value. Generally cells are around 24px, so setting a fixed cell size
     * of 24 is likely to result in very little difference in visuals, but a
     * improvement to performance.
     *
     * <p>To set this property via CSS, use the -fx-fixed-cell-size property.
     * This should not be confused with the -fx-cell-size property. The difference
     * between these two CSS properties is that -fx-cell-size will size all
     * cells to the specified size, but it will not enforce that this is the
     * only size (thus allowing for variable cell sizes, and preventing the
     * performance gains from being possible). Therefore, when performance matters
     * use -fx-fixed-cell-size, instead of -fx-cell-size. If both properties are
     * specified in CSS, -fx-fixed-cell-size takes precedence.</p>
     * @since JavaFX 8.0
     */
    public final DoubleProperty fixedCellSizeProperty() {
        if (fixedCellSize == null) {
            fixedCellSize = new StyleableDoubleProperty(Region.USE_COMPUTED_SIZE) {
                @Override public CssMetaData<TableView<?>,Number> getCssMetaData() {
                    return StyleableProperties.FIXED_CELL_SIZE;
                }

                @Override public Object getBean() {
                    return TableView.this;
                }

                @Override public String getName() {
                    return "fixedCellSize";
                }
            };
        }
        return fixedCellSize;
    }


    // --- Editing Cell
    private ReadOnlyObjectWrapper<TablePosition<S,?>> editingCell;
    private void setEditingCell(TablePosition<S,?> value) {
        editingCellPropertyImpl().set(value);
    }
    public final TablePosition<S,?> getEditingCell() {
        return editingCell == null ? null : editingCell.get();
    }

    /**
     * Represents the current cell being edited, or null if
     * there is no cell being edited.
     */
    public final ReadOnlyObjectProperty<TablePosition<S,?>> editingCellProperty() {
        return editingCellPropertyImpl().getReadOnlyProperty();
    }

    private ReadOnlyObjectWrapper<TablePosition<S,?>> editingCellPropertyImpl() {
        if (editingCell == null) {
            editingCell = new ReadOnlyObjectWrapper<TablePosition<S,?>>(this, "editingCell");
        }
        return editingCell;
    }

    
    // --- Comparator (built via sortOrder list, so read-only)
    /**
     * The comparator property is a read-only property that is representative of the
     * current state of the {@link #getSortOrder() sort order} list. The sort
     * order list contains the columns that have been added to it either programmatically
     * or via a user clicking on the headers themselves.
     * @since JavaFX 8.0
     */
    private ReadOnlyObjectWrapper<Comparator<S>> comparator;
    private void setComparator(Comparator<S> value) {
        comparatorPropertyImpl().set(value);
    }
    public final Comparator<S> getComparator() {
        return comparator == null ? null : comparator.get();
    }
    public final ReadOnlyObjectProperty<Comparator<S>> comparatorProperty() {
        return comparatorPropertyImpl().getReadOnlyProperty();
    }
    private ReadOnlyObjectWrapper<Comparator<S>> comparatorPropertyImpl() {
        if (comparator == null) {
            comparator = new ReadOnlyObjectWrapper<Comparator<S>>(this, "comparator");
        }
        return comparator;
    }
    
    
    // --- sortPolicy
    /**
     * The sort policy specifies how sorting in this TableView should be performed.
     * For example, a basic sort policy may just call 
     * {@code FXCollections.sort(tableView.getItems())}, whereas a more advanced
     * sort policy may call to a database to perform the necessary sorting on the
     * server-side.
     * 
     * <p>TableView ships with a {@link TableView#DEFAULT_SORT_POLICY default
     * sort policy} that does precisely as mentioned above: it simply attempts
     * to sort the items list in-place.
     * 
     * <p>It is recommended that rather than override the {@link TableView#sort() sort}
     * method that a different sort policy be provided instead.
     * @since JavaFX 8.0
     */
    private ObjectProperty<Callback<TableView<S>, Boolean>> sortPolicy;
    public final void setSortPolicy(Callback<TableView<S>, Boolean> callback) {
        sortPolicyProperty().set(callback);
    }
    @SuppressWarnings("unchecked") 
    public final Callback<TableView<S>, Boolean> getSortPolicy() {
        return sortPolicy == null ? 
                (Callback<TableView<S>, Boolean>)(Object) DEFAULT_SORT_POLICY : 
                sortPolicy.get();
    }
    @SuppressWarnings("unchecked")
    public final ObjectProperty<Callback<TableView<S>, Boolean>> sortPolicyProperty() {
        if (sortPolicy == null) {
            sortPolicy = new SimpleObjectProperty<Callback<TableView<S>, Boolean>>(
                    this, "sortPolicy", (Callback<TableView<S>, Boolean>)(Object) DEFAULT_SORT_POLICY) {
                @Override protected void invalidated() {
                    sort();
                }
            };
        }
        return sortPolicy;
    }
    
    
    // onSort
    /**
     * Called when there's a request to sort the control.
     * @since JavaFX 8.0
     */
    private ObjectProperty<EventHandler<SortEvent<TableView<S>>>> onSort;
    
    public void setOnSort(EventHandler<SortEvent<TableView<S>>> value) {
        onSortProperty().set(value);
    }
    
    public EventHandler<SortEvent<TableView<S>>> getOnSort() {
        if( onSort != null ) {
            return onSort.get();
        }
        return null;
    }
    
    public ObjectProperty<EventHandler<SortEvent<TableView<S>>>> onSortProperty() {
        if( onSort == null ) {
            onSort = new ObjectPropertyBase<EventHandler<SortEvent<TableView<S>>>>() {
                @Override protected void invalidated() {
                    EventType<SortEvent<TableView<S>>> eventType = SortEvent.sortEvent();
                    EventHandler<SortEvent<TableView<S>>> eventHandler = get();
                    setEventHandler(eventType, eventHandler);
                }
                
                @Override public Object getBean() {
                    return TableView.this;
                }

                @Override public String getName() {
                    return "onSort";
                }
            };
        }
        return onSort;
    }

    
    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/
    /**
     * The TableColumns that are part of this TableView. As the user reorders
     * the TableView columns, this list will be updated to reflect the current
     * visual ordering.
     *
     * <p>Note: to display any data in a TableView, there must be at least one
     * TableColumn in this ObservableList.</p>
     */
    public final ObservableList<TableColumn<S,?>> getColumns() {
        return columns;
    }
    
    /**
     * The sortOrder list defines the order in which {@link TableColumn} instances
     * are sorted. An empty sortOrder list means that no sorting is being applied
     * on the TableView. If the sortOrder list has one TableColumn within it, 
     * the TableView will be sorted using the 
     * {@link TableColumn#sortTypeProperty() sortType} and
     * {@link TableColumn#comparatorProperty() comparator} properties of this
     * TableColumn (assuming 
     * {@link TableColumn#sortableProperty() TableColumn.sortable} is true).
     * If the sortOrder list contains multiple TableColumn instances, then
     * the TableView is firstly sorted based on the properties of the first 
     * TableColumn. If two elements are considered equal, then the second
     * TableColumn in the list is used to determine ordering. This repeats until
     * the results from all TableColumn comparators are considered, if necessary.
     * 
     * @return An ObservableList containing zero or more TableColumn instances.
     */
    public final ObservableList<TableColumn<S,?>> getSortOrder() {
        return sortOrder;
    }
    
    /**
     * Scrolls the TableView so that the given index is visible within the viewport.
     * @param index The index of an item that should be visible to the user.
     */
    public void scrollTo(int index) {
       ControlUtils.scrollToIndex(this, index);
    }
    
    /**
     * Scrolls the TableView so that the given object is visible within the viewport.
     * @param object The object that should be visible to the user.
     * @since JavaFX 8.0
     */
    public void scrollTo(S object) {
        if( getItems() != null ) {
            int idx = getItems().indexOf(object);
            if( idx >= 0 ) {
                ControlUtils.scrollToIndex(this, idx);        
            }
        }
    }
    
    /**
     * Called when there's a request to scroll an index into view using {@link #scrollTo(int)}
     * or {@link #scrollTo(Object)}
     * @since JavaFX 8.0
     */
    private ObjectProperty<EventHandler<ScrollToEvent<Integer>>> onScrollTo;
    
    public void setOnScrollTo(EventHandler<ScrollToEvent<Integer>> value) {
        onScrollToProperty().set(value);
    }
    
    public EventHandler<ScrollToEvent<Integer>> getOnScrollTo() {
        if( onScrollTo != null ) {
            return onScrollTo.get();
        }
        return null;
    }
    
    public ObjectProperty<EventHandler<ScrollToEvent<Integer>>> onScrollToProperty() {
        if( onScrollTo == null ) {
            onScrollTo = new ObjectPropertyBase<EventHandler<ScrollToEvent<Integer>>>() {
                @Override
                protected void invalidated() {
                    setEventHandler(ScrollToEvent.scrollToTopIndex(), get());
                }
                @Override
                public Object getBean() {
                    return TableView.this;
                }

                @Override
                public String getName() {
                    return "onScrollTo";
                }
            };
        }
        return onScrollTo;
    }
    
    /**
     * Scrolls the TableView so that the given column is visible within the viewport.
     * @param column The column that should be visible to the user.
     * @since JavaFX 8.0
     */
    public void scrollToColumn(TableColumn<S, ?> column) {
        ControlUtils.scrollToColumn(this, column);
    }
    
    /**
     * Scrolls the TableView so that the given index is visible within the viewport.
     * @param columnIndex The index of a column that should be visible to the user.
     * @since JavaFX 8.0
     */
    public void scrollToColumnIndex(int columnIndex) {
        if( getColumns() != null ) {
            ControlUtils.scrollToColumn(this, getColumns().get(columnIndex));
        }
    }
    
    /**
     * Called when there's a request to scroll a column into view using {@link #scrollToColumn(TableColumn)} 
     * or {@link #scrollToColumnIndex(int)}
     * @since JavaFX 8.0
     */
    private ObjectProperty<EventHandler<ScrollToEvent<TableColumn<S, ?>>>> onScrollToColumn;
    
    public void setOnScrollToColumn(EventHandler<ScrollToEvent<TableColumn<S, ?>>> value) {
        onScrollToColumnProperty().set(value);
    }
    
    public EventHandler<ScrollToEvent<TableColumn<S, ?>>> getOnScrollToColumn() {
        if( onScrollToColumn != null ) {
            return onScrollToColumn.get();
        }
        return null;
    }
    
    public ObjectProperty<EventHandler<ScrollToEvent<TableColumn<S, ?>>>> onScrollToColumnProperty() {
        if( onScrollToColumn == null ) {
            onScrollToColumn = new ObjectPropertyBase<EventHandler<ScrollToEvent<TableColumn<S, ?>>>>() {
                @Override protected void invalidated() {
                    EventType<ScrollToEvent<TableColumn<S, ?>>> type = ScrollToEvent.scrollToColumn();
                    setEventHandler(type, get());
                }
                
                @Override public Object getBean() {
                    return TableView.this;
                }

                @Override public String getName() {
                    return "onScrollToColumn";
                }
            };
        }
        return onScrollToColumn;
    }
    
    /**
     * Applies the currently installed resize policy against the given column,
     * resizing it based on the delta value provided.
     */
    public boolean resizeColumn(TableColumn<S,?> column, double delta) {
        if (column == null || Double.compare(delta, 0.0) == 0) return false;

        boolean allowed = getColumnResizePolicy().call(new ResizeFeatures<S>(TableView.this, column, delta));
        if (!allowed) return false;

        // This fixes the issue where if the column width is reduced and the
        // table width is also reduced, horizontal scrollbars will begin to
        // appear at the old width. This forces the VirtualFlow.maxPrefBreadth
        // value to be reset to -1 and subsequently recalculated. Of course
        // ideally we'd just refreshView, but for the time-being no such function
        // exists.
        refresh();
        return true;
    }

    /**
     * Causes the cell at the given row/column view indexes to switch into
     * its editing state, if it is not already in it, and assuming that the 
     * TableView and column are also editable.
     */
    public void edit(int row, TableColumn<S,?> column) {
        if (!isEditable() || (column != null && ! column.isEditable())) {
            return;
        }

        if (row < 0 && column == null) {
            setEditingCell(null);
        } else {
            setEditingCell(new TablePosition<>(this, row, column));
        }
    }
    
    /**
     * Returns an unmodifiable list containing the currently visible leaf columns.
     */
    @ReturnsUnmodifiableCollection
    public ObservableList<TableColumn<S,?>> getVisibleLeafColumns() {
        return unmodifiableVisibleLeafColumns;
    }
    
    /**
     * Returns the position of the given column, relative to all other 
     * visible leaf columns.
     */
    public int getVisibleLeafIndex(TableColumn<S,?> column) {
        return visibleLeafColumns.indexOf(column);
    }

    /**
     * Returns the TableColumn in the given column index, relative to all other
     * visible leaf columns.
     */
    public TableColumn<S,?> getVisibleLeafColumn(int column) {
        if (column < 0 || column >= visibleLeafColumns.size()) return null;
        return visibleLeafColumns.get(column);
    }

    /** {@inheritDoc} */
    @Override protected Skin<?> createDefaultSkin() {
        return new TableViewSkin<S>(this);
    }
    
    /**
     * The sort method forces the TableView to re-run its sorting algorithm. More 
     * often than not it is not necessary to call this method directly, as it is
     * automatically called when the {@link #getSortOrder() sort order}, 
     * {@link #sortPolicyProperty() sort policy}, or the state of the 
     * TableColumn {@link TableColumn#sortTypeProperty() sort type} properties 
     * change. In other words, this method should only be called directly when
     * something external changes and a sort is required.
     * @since JavaFX 8.0
     */
    public void sort() {
        final ObservableList<? extends TableColumnBase<S,?>> sortOrder = getSortOrder();
        
        // update the Comparator property
        final Comparator<S> oldComparator = getComparator();

        setComparator(sortOrder.isEmpty() ? null : new TableColumnComparator(sortOrder));

        // fire the onSort event and check if it is consumed, if
        // so, don't run the sort
        SortEvent<TableView<S>> sortEvent = new SortEvent<>(TableView.this, TableView.this);
        fireEvent(sortEvent);
        if (sortEvent.isConsumed()) {
            // if the sort is consumed we could back out the last action (the code
            // is commented out right below), but we don't as we take it as a 
            // sign that the developer has decided to handle the event themselves.
            
            // sortLock = true;
            // TableUtil.handleSortFailure(sortOrder, lastSortEventType, lastSortEventSupportInfo);
            // sortLock = false;
            return;
        }

        // get the sort policy and run it
        Callback<TableView<S>, Boolean> sortPolicy = getSortPolicy();
        if (sortPolicy == null) return;
        Boolean success = sortPolicy.call(this);
        
        if (success == null || ! success) {
            // the sort was a failure. Need to backout if possible
            sortLock = true;
            TableUtil.handleSortFailure(sortOrder, lastSortEventType, lastSortEventSupportInfo);
            setComparator(oldComparator);
            sortLock = false;
        }
    }
    
    

    /***************************************************************************
     *                                                                         *
     * Private Implementation                                                  *
     *                                                                         *
     **************************************************************************/
    
    private boolean sortLock = false;
    private TableUtil.SortEventType lastSortEventType = null;
    private Object[] lastSortEventSupportInfo = null;
    
    private void doSort(final TableUtil.SortEventType sortEventType, final Object... supportInfo) {
        if (sortLock) {
            return;
        }
        
        this.lastSortEventType = sortEventType;
        this.lastSortEventSupportInfo = supportInfo;
        sort();
        this.lastSortEventType = null;
        this.lastSortEventSupportInfo = null;
    }

    /**
     * Call this function to force the TableView to re-evaluate itself. This is
     * useful when the underlying data model is provided by a TableModel, and
     * you know that the data model has changed. This will force the TableView
     * to go back to the dataProvider and get the row count, as well as update
     * the view to ensure all sorting is still correct based on any changes to
     * the data model.
     */
    private void refresh() {
        getProperties().put(TableViewSkinBase.REFRESH, Boolean.TRUE);
    }


    // --- Content width
    private void setContentWidth(double contentWidth) {
        this.contentWidth = contentWidth;
        if (isInited) {
            // sometimes the current column resize policy will have to modify the
            // column width of all columns in the table if the table width changes,
            // so we short-circuit the resize function and just go straight there
            // with a null TableColumn, which indicates to the resize policy function
            // that it shouldn't actually do anything specific to one column.
            getColumnResizePolicy().call(new ResizeFeatures<S>(TableView.this, null, 0.0));
            refresh();
        }
    }
    
    /**
     * Recomputes the currently visible leaf columns in this TableView.
     */
    private void updateVisibleLeafColumns() {
        // update visible leaf columns list
        List<TableColumn<S,?>> cols = new ArrayList<TableColumn<S,?>>();
        buildVisibleLeafColumns(getColumns(), cols);
        visibleLeafColumns.setAll(cols);

        // sometimes the current column resize policy will have to modify the
        // column width of all columns in the table if the table width changes,
        // so we short-circuit the resize function and just go straight there
        // with a null TableColumn, which indicates to the resize policy function
        // that it shouldn't actually do anything specific to one column.
        getColumnResizePolicy().call(new ResizeFeatures<S>(TableView.this, null, 0.0));
        refresh();
    }

    private void buildVisibleLeafColumns(List<TableColumn<S,?>> cols, List<TableColumn<S,?>> vlc) {
        for (TableColumn<S,?> c : cols) {
            if (c == null) continue;

            boolean hasChildren = ! c.getColumns().isEmpty();

            if (hasChildren) {
                buildVisibleLeafColumns(c.getColumns(), vlc);
            } else if (c.isVisible()) {
                vlc.add(c);
            }
        }
    }
    

    
    /***************************************************************************
     *                                                                         *
     * Stylesheet Handling                                                     *
     *                                                                         *
     **************************************************************************/

    private static final String DEFAULT_STYLE_CLASS = "table-view";

    private static final PseudoClass PSEUDO_CLASS_CELL_SELECTION =
            PseudoClass.getPseudoClass("cell-selection");
    private static final PseudoClass PSEUDO_CLASS_ROW_SELECTION =
            PseudoClass.getPseudoClass("row-selection");

    /** @treatAsPrivate */
    private static class StyleableProperties {
        private static final CssMetaData<TableView<?>,Number> FIXED_CELL_SIZE =
                new CssMetaData<TableView<?>,Number>("-fx-fixed-cell-size",
                                                    SizeConverter.getInstance(),
                                                    Region.USE_COMPUTED_SIZE) {

                    @Override public Double getInitialValue(TableView<?> node) {
                        return node.getFixedCellSize();
                    }

                    @Override public boolean isSettable(TableView<?> n) {
                        return n.fixedCellSize == null || !n.fixedCellSize.isBound();
                    }

                    @Override public StyleableProperty<Number> getStyleableProperty(TableView<?> n) {
                        return (StyleableProperty<Number>) n.fixedCellSizeProperty();
                    }
                };

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables =
                    new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
            styleables.add(FIXED_CELL_SIZE);
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    /**
     * @return The CssMetaData associated with this class, which may include the
     * CssMetaData of its super classes.
     * @since JavaFX 8.0
     */
    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }

    /**
     * {@inheritDoc}
     * @since JavaFX 8.0
     */
    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }
    


    /***************************************************************************
     *                                                                         *
     * Support Interfaces                                                      *
     *                                                                         *
     **************************************************************************/

     /**
      * An immutable wrapper class for use in the TableView 
     * {@link TableView#columnResizePolicyProperty() column resize} functionality.
      * @since JavaFX 2.0
      */
     public static class ResizeFeatures<S> extends ResizeFeaturesBase<S> {
        private TableView<S> table;

        /**
         * Creates an instance of this class, with the provided TableView, 
         * TableColumn and delta values being set and stored in this immutable
         * instance.
         * 
         * @param table The TableView upon which the resize operation is occurring.
         * @param column The column upon which the resize is occurring, or null
         *      if this ResizeFeatures instance is being created as a result of a
         *      TableView resize operation.
         * @param delta The amount of horizontal space added or removed in the 
         *      resize operation.
         */
        public ResizeFeatures(TableView<S> table, TableColumn<S,?> column, Double delta) {
            super(column, delta);
            this.table = table;
        }
        
        /**
         * Returns the column upon which the resize is occurring, or null
         * if this ResizeFeatures instance was created as a result of a
         * TableView resize operation.
         */
        @Override public TableColumn<S,?> getColumn() { 
            return (TableColumn<S,?>) super.getColumn(); 
        }
        
        /**
         * Returns the TableView upon which the resize operation is occurring.
         */
        public TableView<S> getTable() { 
            return table; 
        }
    }



    /***************************************************************************
     *                                                                         *
     * Support Classes                                                         *
     *                                                                         *
     **************************************************************************/

     
    /**
     * A simple extension of the {@link SelectionModel} abstract class to
     * allow for special support for TableView controls.
     * @since JavaFX 2.0
     */
    public static abstract class TableViewSelectionModel<S> extends TableSelectionModel<S> {

        /***********************************************************************
         *                                                                     *
         * Private fields                                                      *
         *                                                                     *
         **********************************************************************/

        private final TableView<S> tableView;



        /***********************************************************************
         *                                                                     *
         * Constructors                                                        *
         *                                                                     *
         **********************************************************************/

        /**
         * Builds a default TableViewSelectionModel instance with the provided
         * TableView.
         * @param tableView The TableView upon which this selection model should
         *      operate.
         * @throws NullPointerException TableView can not be null.
         */
        public TableViewSelectionModel(final TableView<S> tableView) {
            if (tableView == null) {
                throw new NullPointerException("TableView can not be null");
            }

            this.tableView = tableView;
        }



        /***********************************************************************
         *                                                                     *
         * Abstract API                                                        *
         *                                                                     *
         **********************************************************************/

        /**
         * A read-only ObservableList representing the currently selected cells 
         * in this TableView. Rather than directly modify this list, please
         * use the other methods provided in the TableViewSelectionModel.
         */
        public abstract ObservableList<TablePosition> getSelectedCells();


        /***********************************************************************
         *                                                                     *
         * Generic (type erasure) bridging                                     *
         *                                                                     *
         **********************************************************************/

        // --- isSelected
        /** {@inheritDoc} */
        @Override public boolean isSelected(int row, TableColumnBase<S, ?> column) {
            return isSelected(row, (TableColumn<S,?>)column);
        }

        /**
         * Convenience function which tests whether the given row and column index
         * is currently selected in this table instance.
         */
        public abstract boolean isSelected(int row, TableColumn<S, ?> column);


        // --- select
        /** {@inheritDoc} */
        @Override public void select(int row, TableColumnBase<S, ?> column) {
            select(row, (TableColumn<S,?>)column);
        }

        /**
         * Selects the cell at the given row/column intersection.
         */
        public abstract void select(int row, TableColumn<S, ?> column);


        // --- clearAndSelect
        /** {@inheritDoc} */
        @Override public void clearAndSelect(int row, TableColumnBase<S,?> column) {
            clearAndSelect(row, (TableColumn<S,?>) column);
        }

        /**
         * Clears all selection, and then selects the cell at the given row/column
         * intersection.
         */
        public abstract void clearAndSelect(int row, TableColumn<S,?> column);


        // --- clearSelection
        /** {@inheritDoc} */
        @Override public void clearSelection(int row, TableColumnBase<S,?> column) {
            clearSelection(row, (TableColumn<S,?>) column);
        }

        /**
         * Removes selection from the specified row/column position (in view indexes).
         * If this particular cell (or row if the column value is -1) is not selected,
         * nothing happens.
         */
        public abstract void clearSelection(int row, TableColumn<S, ?> column);



        /***********************************************************************
         *                                                                     *
         * Public API                                                          *
         *                                                                     *
         **********************************************************************/

        /**
         * Returns the TableView instance that this selection model is installed in.
         */
        public TableView<S> getTableView() {
            return tableView;
        }

        /**
         * Convenience method that returns getTableView().getItems().
         * @return The items list of the current TableView.
         */
        protected List<S> getTableModel()  {
            return tableView.getItems();
        }

        /** {@inheritDoc} */
        @Override protected S getModelItem(int index) {
            if (index < 0 || index > getItemCount()) return null;
            return tableView.getItems().get(index);
        }

        /** {@inheritDoc} */
        @Override protected int getItemCount() {
            return getTableModel().size();
        }

        /** {@inheritDoc} */
        @Override public void focus(int row) {
            focus(row, null);
        }

        /** {@inheritDoc} */
        @Override public int getFocusedIndex() {
            return getFocusedCell().getRow();
        }



        /***********************************************************************
         *                                                                     *
         * Private implementation                                              *
         *                                                                     *
         **********************************************************************/

        void focus(int row, TableColumn<S,?> column) {
            focus(new TablePosition<>(getTableView(), row, column));
        }

        void focus(TablePosition<S,?> pos) {
            if (getTableView().getFocusModel() == null) return;

            getTableView().getFocusModel().focus(pos.getRow(), pos.getTableColumn());
        }

        TablePosition<S,?> getFocusedCell() {
            if (getTableView().getFocusModel() == null) {
                return new TablePosition<>(getTableView(), -1, null);
            }
            return getTableView().getFocusModel().getFocusedCell();
        }
    }
    
    

    /**
     * A primitive selection model implementation, using a List<Integer> to store all
     * selected indices.
     */
    // package for testing
    static class TableViewArrayListSelectionModel<S> extends TableViewSelectionModel<S> {
        
        private int itemCount = 0;

        /***********************************************************************
         *                                                                     *
         * Constructors                                                        *
         *                                                                     *
         **********************************************************************/

        public TableViewArrayListSelectionModel(final TableView<S> tableView) {
            super(tableView);
            this.tableView = tableView;
//            this.selectedIndicesBitSet = new BitSet();
            
            updateItemCount();
            
            cellSelectionEnabledProperty().addListener(new InvalidationListener() {
                @Override public void invalidated(Observable o) {
                    isCellSelectionEnabled();
                    clearSelection();
                }
            });
            
            final MappingChange.Map<TablePosition<S,?>,S> cellToItemsMap = new MappingChange.Map<TablePosition<S,?>, S>() {
                @Override public S map(TablePosition<S,?> f) {
                    return getModelItem(f.getRow());
                }
            };
            
            final MappingChange.Map<TablePosition<S,?>,Integer> cellToIndicesMap = new MappingChange.Map<TablePosition<S,?>, Integer>() {
                @Override public Integer map(TablePosition<S,?> f) {
                    return f.getRow();
                }
            };
            
            selectedCells = FXCollections.<TablePosition<S,?>>observableArrayList();
            selectedCells.addListener(new ListChangeListener<TablePosition<S,?>>() {
                @Override public void onChanged(final Change<? extends TablePosition<S,?>> c) {
                    // RT-29313: because selectedIndices and selectedItems represent
                    // row-based selection, we need to update the
                    // selectedIndicesBitSet when the selectedCells changes to 
                    // ensure that selectedIndices and selectedItems return only
                    // the correct values (and only once). The issue identified
                    // by RT-29313 is that the size and contents of selectedIndices
                    // and selectedItems can not simply defer to the
                    // selectedCells as selectedCells may be representing 
                    // multiple cells from one row (e.g. selectedCells of 
                    // [(0,1), (1,1), (1,2), (1,3)] should result in 
                    // selectedIndices of [0,1], not [0,1,1,1]).
                    // An inefficient solution would rebuild the selectedIndicesBitSet
                    // every time the change happens, but we can do better than
                    // that. Inefficient solution:
                    //
                    // selectedIndicesBitSet.clear();
                    // for (int i = 0; i < selectedCells.size(); i++) {
                    //     final TablePosition<S,?> tp = selectedCells.get(i);
                    //     final int row = tp.getRow();
                    //     selectedIndicesBitSet.set(row);
                    // }
                    // 
                    // A more efficient solution:
                    final List<Integer> newlySelectedRows = new ArrayList<Integer>();
                    final List<Integer> newlyUnselectedRows = new ArrayList<Integer>();
                    
                    while (c.next()) {
                        if (c.wasRemoved()) {
                            List<? extends TablePosition<S,?>> removed = c.getRemoved();
                            for (int i = 0; i < removed.size(); i++) {
                                final TablePosition<S,?> tp = removed.get(i);
                                final int row = tp.getRow();
                                
                                if (selectedIndices.get(row)) {
                                    selectedIndices.clear(row);
                                    newlyUnselectedRows.add(row);
                                }
                            }
                        }
                        if (c.wasAdded()) {
                            List<? extends TablePosition<S,?>> added = c.getAddedSubList();
                            for (int i = 0; i < added.size(); i++) {
                                final TablePosition<S,?> tp = added.get(i);
                                final int row = tp.getRow();
                                
                                if (! selectedIndices.get(row)) {
                                    selectedIndices.set(row);
                                    newlySelectedRows.add(row);
                                }
                            }
                        }
                    }
                    c.reset();
                    
                    // when the selectedCells observableArrayList changes, we manually call
                    // the observers of the selectedItems, selectedIndices and
                    // selectedCells lists.
                    
                    // create an on-demand list of the removed objects contained in the
                    // given rows
                    selectedItems.callObservers(new MappingChange<TablePosition<S,?>, S>(c, cellToItemsMap, selectedItems));
                    c.reset();

                    // Fix for RT-31577 - the selectedItems list was going to
                    // empty, but the selectedItem property was staying non-null.
                    // There is a unit test for this, so if a more elegant solution
                    // can be found in the future and this code removed, the unit
                    // test will fail if it isn't fixed elsewhere.
                    // makeAtomic toggle added to resolve RT-32618
                    if (! makeAtomic && selectedItems.isEmpty() && getSelectedItem() != null) {
                        setSelectedItem(null);
                    }
                    
                    final ReadOnlyUnbackedObservableList<Integer> selectedIndicesSeq = 
                            (ReadOnlyUnbackedObservableList<Integer>)getSelectedIndices();

                    if (! newlySelectedRows.isEmpty() && newlyUnselectedRows.isEmpty()) {
                        // need to come up with ranges based on the actualSelectedRows, and
                        // then fire the appropriate number of changes. We also need to
                        // translate from a desired row to select to where that row is 
                        // represented in the selectedIndices list. For example,
                        // we may have requested to select row 5, and the selectedIndices
                        // list may therefore have the following: [1,4,5], meaning row 5
                        // is in position 2 of the selectedIndices list
                        Change<Integer> change = createRangeChange(selectedIndicesSeq, newlySelectedRows);
                        selectedIndicesSeq.callObservers(change);
                    } else {
                        selectedIndicesSeq.callObservers(new MappingChange<TablePosition<S,?>, Integer>(c, cellToIndicesMap, selectedIndicesSeq));
                        c.reset();
                    }

                    selectedCellsSeq.callObservers(new MappingChange<TablePosition<S,?>, TablePosition<S,?>>(c, MappingChange.NOOP_MAP, selectedCellsSeq));
                    c.reset();
                }
            });

            selectedItems = new ReadOnlyUnbackedObservableList<S>() {
                @Override public S get(int i) {
                    return getModelItem(getSelectedIndices().get(i));
                }

                @Override public int size() {
                    return getSelectedIndices().size();
                }
            };
            
            selectedCellsSeq = new ReadOnlyUnbackedObservableList<TablePosition<S,?>>() {
                @Override public TablePosition<S,?> get(int i) {
                    return selectedCells.get(i);
                }

                @Override public int size() {
                    return selectedCells.size();
                }
            };


            /*
             * The following listener is used in conjunction with
             * SelectionModel.select(T obj) to allow for a developer to select
             * an item that is not actually in the data model. When this occurs,
             * we actively try to find an index that matches this object, going
             * so far as to actually watch for all changes to the items list,
             * rechecking each time.
             */

            // watching for changes to the items list content
            ObservableList<S> items = getTableView().getItems();
            if (items != null) {
                items.addListener(weakItemsContentListener);
            }
        }
        
        private final TableView<S> tableView;
        
        final ListChangeListener<S> itemsContentListener = new ListChangeListener<S>() {
            @Override public void onChanged(Change<? extends S> c) {
                updateItemCount();

                List<S> items = getTableModel();

                while (c.next()) {
                    final S selectedItem = getSelectedItem();
                    final int selectedIndex = getSelectedIndex();
                    
                    if (items == null || items.isEmpty()) {
                        clearSelection();
                    } else if (getSelectedIndex() == -1 && getSelectedItem() != null) {
                        int newIndex = items.indexOf(getSelectedItem());
                        if (newIndex != -1) {
                            setSelectedIndex(newIndex);
                        }
                    } else if (c.wasRemoved() && 
                            c.getRemovedSize() == 1 && 
                            ! c.wasAdded() && 
                            selectedItem != null && 
                            selectedItem.equals(c.getRemoved().get(0))) {
                        // Bug fix for RT-28637
                        if (getSelectedIndex() < getItemCount()) {
                            S newSelectedItem = getModelItem(selectedIndex);
                            if (! selectedItem.equals(newSelectedItem)) {
                                setSelectedItem(newSelectedItem);
                            }
                        }
                    }
                }

                updateSelection(c);
            }
        };
        
        final WeakListChangeListener<S> weakItemsContentListener 
                = new WeakListChangeListener<S>(itemsContentListener);
        
        private void updateItemsObserver(ObservableList<S> oldList, ObservableList<S> newList) {
            // the items list has changed, we need to observe
            // the new list, and remove any observer we had from the old list
            if (oldList != null) {
                oldList.removeListener(weakItemsContentListener);
            }
            if (newList != null) {
                newList.addListener(weakItemsContentListener);
            }
            
            updateItemCount();

            // when the items list totally changes, we should clear out
            // the selection
            setSelectedIndex(-1);
        }
        

        /***********************************************************************
         *                                                                     *
         * Observable properties (and getters/setters)                         *
         *                                                                     *
         **********************************************************************/
        
        // the only 'proper' internal observableArrayList, selectedItems and selectedIndices
        // are both 'read-only and unbacked'.
        private final ObservableList<TablePosition<S,?>> selectedCells;

        // used to represent the _row_ backing data for the selectedCells
        private final ReadOnlyUnbackedObservableList<S> selectedItems;
        @Override public ObservableList<S> getSelectedItems() {
            return selectedItems;
        }

        private final ReadOnlyUnbackedObservableList<TablePosition<S,?>> selectedCellsSeq;
        @Override public ObservableList<TablePosition> getSelectedCells() {
            return (ObservableList<TablePosition>)(Object)selectedCellsSeq;
        }


        /***********************************************************************
         *                                                                     *
         * Internal properties                                                 *
         *                                                                     *
         **********************************************************************/

        private int previousModelSize = 0;
        
        // Listen to changes in the tableview items list, such that when it 
        // changes we can update the selected indices list to refer to the 
        // new indices.
        private void updateSelection(ListChangeListener.Change<? extends S> c) {
            c.reset();
            while (c.next()) {
                if (c.wasReplaced()) {
                    if (c.getList().isEmpty()) {
                        // the entire items list was emptied - clear selection
                        clearSelection();
                    } else {
                        int index = getSelectedIndex();
                        
                        if (previousModelSize == c.getRemovedSize()) {
                            // all items were removed from the model
                            clearSelection();
                        } else if (index < getItemCount() && index >= 0) {
                            // Fix for RT-18969: the list had setAll called on it
                            // Use of makeAtomic is a fix for RT-20945
                            makeAtomic = true;
                            clearSelection(index);
                            makeAtomic = false;
                            select(index);
                        } else {
                            // Fix for RT-22079
                            clearSelection();
                        }
                    }
                } else if (c.wasAdded() || c.wasRemoved()) {
                    int position = c.getFrom();
                    int shift = c.wasAdded() ? c.getAddedSize() : -c.getRemovedSize();
                    
                    if (position < 0) return;
                    if (shift == 0) return;
                    
                    List<TablePosition<S,?>> newIndices = new ArrayList<TablePosition<S,?>>(selectedCells.size());
        
                    for (int i = 0; i < selectedCells.size(); i++) {
                        final TablePosition<S,?> old = selectedCells.get(i);
                        final int oldRow = old.getRow();
                        final int newRow = oldRow < position ? oldRow : oldRow + shift;
                        
                        // Special case for RT-28637 (See unit test in TableViewTest).
                        // Essentially the selectedItem was correct, but selectedItems
                        // was empty.
                        if (oldRow == 0 && shift == -1) {
                            newIndices.add(new TablePosition<>(getTableView(), 0, old.getTableColumn()));
                            continue;
                        }
                        
                        if (newRow < 0) continue;
                        newIndices.add(new TablePosition<>(getTableView(), newRow, old.getTableColumn()));
                    }
                    
                    quietClearSelection();
                    
                    // Fix for RT-22079
                    for (int i = 0; i < newIndices.size(); i++) {
                        TablePosition<S,?> tp = newIndices.get(i);
                        select(tp.getRow(), tp.getTableColumn());
                    }
                } else if (c.wasPermutated()) {
                    // General approach:
                    //   -- detected a sort has happened
                    //   -- Create a permutation lookup map (1)
                    //   -- dump all the selected indices into a list (2)
                    //   -- clear the selected items / indexes (3)
                    //   -- create a list containing the new indices (4)
                    //   -- for each previously-selected index (5)
                    //     -- if index is in the permutation lookup map
                    //       -- add the new index to the new indices list
                    //   -- Perform batch selection (6)

                    final int oldSelectedIndex = getSelectedIndex();

                    // (1)
                    int length = c.getTo() - c.getFrom();
                    HashMap<Integer, Integer> pMap = new HashMap<Integer, Integer> (length);
                    for (int i = c.getFrom(); i < c.getTo(); i++) {
                        pMap.put(i, c.getPermutation(i));
                    }

                    // (2)
                    List<TablePosition<S,?>> selectedIndices =
                            new ArrayList<TablePosition<S,?>>((ObservableList<TablePosition<S,?>>)(Object)getSelectedCells());


                    // (3)
                    clearSelection();

                    // (4)
                    List<TablePosition<S,?>> newIndices = new ArrayList<TablePosition<S,?>>(getSelectedIndices().size());

                    // (5)
                    for (int i = 0; i < selectedIndices.size(); i++) {
                        TablePosition<S,?> oldIndex = selectedIndices.get(i);

                        if (pMap.containsKey(oldIndex.getRow())) {
                            Integer newIndex = pMap.get(oldIndex.getRow());
                            newIndices.add(new TablePosition<>(oldIndex.getTableView(), newIndex, oldIndex.getTableColumn()));
                        }
                    }

                    // (6)
                    quietClearSelection();
                    selectedCells.setAll(newIndices);
                    selectedCellsSeq.callObservers(new NonIterableChange.SimpleAddChange<>(0, newIndices.size(), selectedCellsSeq));

                    if (oldSelectedIndex >= 0 && oldSelectedIndex < itemCount) {
                        setSelectedIndex(c.getPermutation(oldSelectedIndex));
                    }
                }
            }
            
            previousModelSize = getItemCount();
        }

        /***********************************************************************
         *                                                                     *
         * Public selection API                                                *
         *                                                                     *
         **********************************************************************/

        @Override public void clearAndSelect(int row) {
            clearAndSelect(row, null);
        }

        @Override public void clearAndSelect(int row, TableColumn<S,?> column) {
            // RT-32411 We used to call quietClearSelection() here, but this
            // resulted in the selectedItems and selectedIndices lists never
            // reporting that they were empty.
            // makeAtomic toggle added to resolve RT-32618
            makeAtomic = true;
            clearSelection();
            makeAtomic = false;

            // and select
            select(row, column);
        }

        @Override public void select(int row) {
            select(row, null);
        }

        @Override
        public void select(int row, TableColumn<S,?> column) {
            if (row < 0 || row >= getItemCount()) return;

            // if I'm in cell selection mode but the column is null, I don't want
            // to select the whole row instead...
            if (isCellSelectionEnabled() && column == null) return;
//            
//            // If I am not in cell selection mode (so I want to select rows only),
//            // if a column is given, I return
//            if (! isCellSelectionEnabled() && column != null) return;

            TablePosition<S,?> pos = new TablePosition<>(getTableView(), row, column);
            
            if (getSelectionMode() == SelectionMode.SINGLE) {
                quietClearSelection();
            }

            if (! selectedCells.contains(pos)) {
                selectedCells.add(pos);
            }

            updateSelectedIndex(row);
            focus(row, column);
        }

        @Override public void select(S obj) {
            if (obj == null && getSelectionMode() == SelectionMode.SINGLE) {
                clearSelection();
                return;
            }
            
            // We have no option but to iterate through the model and select the
            // first occurrence of the given object. Once we find the first one, we
            // don't proceed to select any others.
            S rowObj = null;
            for (int i = 0; i < getItemCount(); i++) {
                rowObj = getModelItem(i);
                if (rowObj == null) continue;

                if (rowObj.equals(obj)) {
                    if (isSelected(i)) {
                        return;
                    }

                    if (getSelectionMode() == SelectionMode.SINGLE) {
                        quietClearSelection();
                    }

                    select(i);
                    return;
                }
            }

            // if we are here, we did not find the item in the entire data model.
            // Even still, we allow for this item to be set to the give object.
            // We expect that in concrete subclasses of this class we observe the
            // data model such that we check to see if the given item exists in it,
            // whilst SelectedIndex == -1 && SelectedItem != null.
            setSelectedItem(obj);
        }

        @Override public void selectIndices(int row, int... rows) {
            if (rows == null) {
                select(row);
                return;
            }

            /*
             * Performance optimisation - if multiple selection is disabled, only
             * process the end-most row index.
             */
            int rowCount = getItemCount();

            if (getSelectionMode() == SelectionMode.SINGLE) {
                quietClearSelection();

                for (int i = rows.length - 1; i >= 0; i--) {
                    int index = rows[i];
                    if (index >= 0 && index < rowCount) {
                        select(index);
                        break;
                    }
                }

                if (selectedCells.isEmpty()) {
                    if (row > 0 && row < rowCount) {
                        select(row);
                    }
                }
            } else {
                int lastIndex = -1;
                Set<TablePosition<S,?>> positions = new LinkedHashSet<TablePosition<S,?>>();

                if (row >= 0 && row < rowCount) {
                    TablePosition<S,Object> tp = new TablePosition<S,Object>(getTableView(), row, null);
                    
                    // refer to the multi-line comment below for the justification for the following
                    // code.
                    boolean match = false;
                    for (int j = 0; j < selectedCells.size(); j++) {
                        TablePosition<S,?> selectedCell = selectedCells.get(j);
                        if (selectedCell.getRow() == row) {
                            match = true;
                            break;
                        }
                    }
                    if (! match) {
                        positions.add(tp);
                        lastIndex = row;
                    }
                }

                outer: for (int i = 0; i < rows.length; i++) {
                    int index = rows[i];
                    if (index < 0 || index >= rowCount) continue;
                    lastIndex = index;
                    
                    // we need to manually check all selected cells to see whether this index is already
                    // selected. This is because selectIndices is inherently row-based, but there may
                    // be a selected cell where the column is non-null. If we were to simply do a
                    // selectedCells.contains(pos), then we would not find the match and duplicate the
                    // row selection. This leads to bugs such as RT-29930.
                    for (int j = 0; j < selectedCells.size(); j++) {
                        TablePosition<S,?> selectedCell = selectedCells.get(j);
                        if (selectedCell.getRow() == index) continue outer;
                    }
                    
                    // if we are here then we have successfully gotten through the for-loop above
                    TablePosition<S,Object> pos = new TablePosition<S,Object>(getTableView(), index, null);
                    positions.add(pos);
                }
                
                selectedCells.addAll(positions);
                
                if (lastIndex != -1) {
                    select(lastIndex);
                }
            }
        }

        @Override public void selectAll() {
            if (getSelectionMode() == SelectionMode.SINGLE) return;

            quietClearSelection();

            if (isCellSelectionEnabled()) {
                List<TablePosition<S,?>> indices = new ArrayList<TablePosition<S,?>>();
                TableColumn<S,?> column;
                TablePosition<S,?> tp = null;
                for (int col = 0; col < getTableView().getVisibleLeafColumns().size(); col++) {
                    column = getTableView().getVisibleLeafColumns().get(col);
                    for (int row = 0; row < getItemCount(); row++) {
                        tp = new TablePosition<>(getTableView(), row, column);
                        indices.add(tp);
                    }
                }
                selectedCells.setAll(indices);
                
                if (tp != null) {
                    select(tp.getRow(), tp.getTableColumn());
                    focus(tp.getRow(), tp.getTableColumn());
                }
            } else {
                List<TablePosition<S,?>> indices = new ArrayList<TablePosition<S,?>>();
                for (int i = 0; i < getItemCount(); i++) {
                    indices.add(new TablePosition<>(getTableView(), i, null));
                }
                selectedCells.setAll(indices);
                
                int focusedIndex = getFocusedIndex();
                if (focusedIndex == -1) {
                    select(getItemCount() - 1);
                    focus(indices.get(indices.size() - 1));
                } else {
                    select(focusedIndex);
                    focus(focusedIndex);
                }
            }
        }

        @Override public void clearSelection(int index) {
            clearSelection(index, null);
        }

        @Override
        public void clearSelection(int row, TableColumn<S,?> column) {
            TablePosition<S,?> tp = new TablePosition<>(getTableView(), row, column);

            boolean csMode = isCellSelectionEnabled();
            
            for (TablePosition pos : getSelectedCells()) {
                if ((! csMode && pos.getRow() == row) || (csMode && pos.equals(tp))) {
                    selectedCells.remove(pos);

                    // give focus to this cell index
                    focus(row);

                    return;
                }
            }
        }

        @Override public void clearSelection() {
            if (! makeAtomic) {
                updateSelectedIndex(-1);
                focus(-1);
            }

            quietClearSelection();
        }

        private void quietClearSelection() {
            selectedCells.clear();
        }

        @Override public boolean isSelected(int index) {
            return isSelected(index, null);
        }

        @Override
        public boolean isSelected(int row, TableColumn<S,?> column) {
            // When in cell selection mode, we currently do NOT support selecting
            // entire rows, so a isSelected(row, null) 
            // should always return false.
            if (isCellSelectionEnabled() && (column == null)) return false;
            
            for (TablePosition tp : getSelectedCells()) {
                boolean columnMatch = ! isCellSelectionEnabled() || 
                        (column == null && tp.getTableColumn() == null) || 
                        (column != null && column.equals(tp.getTableColumn()));
                
                if (tp.getRow() == row && columnMatch) {
                    return true;
                }
            }
            return false;
        }

        @Override public boolean isEmpty() {
            return selectedCells.isEmpty();
        }

        @Override public void selectPrevious() {
            if (isCellSelectionEnabled()) {
                // in cell selection mode, we have to wrap around, going from
                // right-to-left, and then wrapping to the end of the previous line
                TablePosition<S,?> pos = getFocusedCell();
                if (pos.getColumn() - 1 >= 0) {
                    // go to previous row
                    select(pos.getRow(), getTableColumn(pos.getTableColumn(), -1));
                } else if (pos.getRow() < getItemCount() - 1) {
                    // wrap to end of previous row
                    select(pos.getRow() - 1, getTableColumn(getTableView().getVisibleLeafColumns().size() - 1));
                }
            } else {
                int focusIndex = getFocusedIndex();
                if (focusIndex == -1) {
                    select(getItemCount() - 1);
                } else if (focusIndex > 0) {
                    select(focusIndex - 1);
                }
            }
        }

        @Override public void selectNext() {
            if (isCellSelectionEnabled()) {
                // in cell selection mode, we have to wrap around, going from
                // left-to-right, and then wrapping to the start of the next line
                TablePosition<S,?> pos = getFocusedCell();
                if (pos.getColumn() + 1 < getTableView().getVisibleLeafColumns().size()) {
                    // go to next column
                    select(pos.getRow(), getTableColumn(pos.getTableColumn(), 1));
                } else if (pos.getRow() < getItemCount() - 1) {
                    // wrap to start of next row
                    select(pos.getRow() + 1, getTableColumn(0));
                }
            } else {
                int focusIndex = getFocusedIndex();
                if (focusIndex == -1) {
                    select(0);
                } else if (focusIndex < getItemCount() -1) {
                    select(focusIndex + 1);
                }
            }
        }

        @Override public void selectAboveCell() {
            TablePosition<S,?> pos = getFocusedCell();
            if (pos.getRow() == -1) {
                select(getItemCount() - 1);
            } else if (pos.getRow() > 0) {
                select(pos.getRow() - 1, pos.getTableColumn());
            }
        }

        @Override public void selectBelowCell() {
            TablePosition<S,?> pos = getFocusedCell();

            if (pos.getRow() == -1) {
                select(0);
            } else if (pos.getRow() < getItemCount() -1) {
                select(pos.getRow() + 1, pos.getTableColumn());
            }
        }

        @Override public void selectFirst() {
            TablePosition<S,?> focusedCell = getFocusedCell();

            if (getSelectionMode() == SelectionMode.SINGLE) {
                quietClearSelection();
            }

            if (getItemCount() > 0) {
                if (isCellSelectionEnabled()) {
                    select(0, focusedCell.getTableColumn());
                } else {
                    select(0);
                }
            }
        }

        @Override public void selectLast() {
            TablePosition<S,?> focusedCell = getFocusedCell();

            if (getSelectionMode() == SelectionMode.SINGLE) {
                quietClearSelection();
            }

            int numItems = getItemCount();
            if (numItems > 0 && getSelectedIndex() < numItems - 1) {
                if (isCellSelectionEnabled()) {
                    select(numItems - 1, focusedCell.getTableColumn());
                } else {
                    select(numItems - 1);
                }
            }
        }

        @Override
        public void selectLeftCell() {
            if (! isCellSelectionEnabled()) return;

            TablePosition<S,?> pos = getFocusedCell();
            if (pos.getColumn() - 1 >= 0) {
                select(pos.getRow(), getTableColumn(pos.getTableColumn(), -1));
            }
        }

        @Override
        public void selectRightCell() {
            if (! isCellSelectionEnabled()) return;

            TablePosition<S,?> pos = getFocusedCell();
            if (pos.getColumn() + 1 < getTableView().getVisibleLeafColumns().size()) {
                select(pos.getRow(), getTableColumn(pos.getTableColumn(), 1));
            }
        }



        /***********************************************************************
         *                                                                     *
         * Support code                                                        *
         *                                                                     *
         **********************************************************************/
        
        private TableColumn<S,?> getTableColumn(int pos) {
            return getTableView().getVisibleLeafColumn(pos);
        }
        
//        private TableColumn<S,?> getTableColumn(TableColumn<S,?> column) {
//            return getTableColumn(column, 0);
//        }

        // Gets a table column to the left or right of the current one, given an offset
        private TableColumn<S,?> getTableColumn(TableColumn<S,?> column, int offset) {
            int columnIndex = getTableView().getVisibleLeafIndex(column);
            int newColumnIndex = columnIndex + offset;
            return getTableView().getVisibleLeafColumn(newColumnIndex);
        }

        private void updateSelectedIndex(int row) {
            setSelectedIndex(row);
            setSelectedItem(getModelItem(row));
        }
        
        /** {@inheritDoc} */
        @Override protected int getItemCount() {
            return itemCount;
        }

        private void updateItemCount() {
            if (tableView == null) {
                itemCount = -1;
            } else {
                List<S> items = getTableModel();
                itemCount = items == null ? -1 : items.size();
            }
        }
    }
    
    
    
    
    /**
     * A {@link FocusModel} with additional functionality to support the requirements
     * of a TableView control.
     * 
     * @see TableView
     * @since JavaFX 2.0
     */
    public static class TableViewFocusModel<S> extends TableFocusModel<S, TableColumn<S, ?>> {

        private final TableView<S> tableView;

        private final TablePosition<S,?> EMPTY_CELL;

        /**
         * Creates a default TableViewFocusModel instance that will be used to
         * manage focus of the provided TableView control.
         * 
         * @param tableView The tableView upon which this focus model operates.
         * @throws NullPointerException The TableView argument can not be null.
         */
        public TableViewFocusModel(final TableView<S> tableView) {
            if (tableView == null) {
                throw new NullPointerException("TableView can not be null");
            }

            this.tableView = tableView;
            
            if (tableView.getItems() != null) {
                this.tableView.getItems().addListener(weakItemsContentListener);
            }

            TablePosition<S,?> pos = new TablePosition<>(tableView, -1, null);
            setFocusedCell(pos);
            EMPTY_CELL = pos;
        }
        
        // Listen to changes in the tableview items list, such that when it
        // changes we can update the focused index to refer to the new indices.
        private final ListChangeListener<S> itemsContentListener = new ListChangeListener<S>() {
            @Override public void onChanged(Change<? extends S> c) {
                c.next();
                if (c.getFrom() > getFocusedIndex()) return;
                c.reset();
                boolean added = false;
                boolean removed = false;
                int addedSize = 0;
                int removedSize = 0;
                while (c.next()) {
                    added |= c.wasAdded();
                    removed |= c.wasRemoved();
                    addedSize += c.getAddedSize();
                    removedSize += c.getRemovedSize();
                }
                if (added && ! removed) {
                    focus(getFocusedIndex() + addedSize);
                } else if (!added && removed) {
                    focus(getFocusedIndex() - removedSize);
                }
            }
        };
        
        private WeakListChangeListener<S> weakItemsContentListener 
                = new WeakListChangeListener<S>(itemsContentListener);
        
        private void updateItemsObserver(ObservableList<S> oldList, ObservableList<S> newList) {
            // the tableview items list has changed, we need to observe
            // the new list, and remove any observer we had from the old list
            if (oldList != null) oldList.removeListener(weakItemsContentListener);
            if (newList != null) newList.addListener(weakItemsContentListener);
        }

        /** {@inheritDoc} */
        @Override protected int getItemCount() {
            if (tableView.getItems() == null) return -1;
            return tableView.getItems().size();
        }

        /** {@inheritDoc} */
        @Override protected S getModelItem(int index) {
            if (tableView.getItems() == null) return null;

            if (index < 0 || index >= getItemCount()) return null;

            return tableView.getItems().get(index);
        }

        /**
         * The position of the current item in the TableView which has the focus.
         */
        private ReadOnlyObjectWrapper<TablePosition> focusedCell;
        public final ReadOnlyObjectProperty<TablePosition> focusedCellProperty() {
            return focusedCellPropertyImpl().getReadOnlyProperty();
        }
        private void setFocusedCell(TablePosition value) { focusedCellPropertyImpl().set(value);  }
        public final TablePosition getFocusedCell() { return focusedCell == null ? EMPTY_CELL : focusedCell.get(); }

        private ReadOnlyObjectWrapper<TablePosition> focusedCellPropertyImpl() {
            if (focusedCell == null) {
                focusedCell = new ReadOnlyObjectWrapper<TablePosition>(EMPTY_CELL) {
                    private TablePosition old;
                    @Override protected void invalidated() {
                        if (get() == null) return;

                        if (old == null || !old.equals(get())) {
                            setFocusedIndex(get().getRow());
                            setFocusedItem(getModelItem(getValue().getRow()));
                            
                            old = get();
                        }
                    }

                    @Override
                    public Object getBean() {
                        return TableViewFocusModel.this;
                    }

                    @Override
                    public String getName() {
                        return "focusedCell";
                    }
                };
            }
            return focusedCell;
        }


        /**
         * Causes the item at the given index to receive the focus.
         *
         * @param row The row index of the item to give focus to.
         * @param column The column of the item to give focus to. Can be null.
         */
        @Override public void focus(int row, TableColumn<S,?> column) {
            if (row < 0 || row >= getItemCount()) {
                setFocusedCell(EMPTY_CELL);
            } else {
                setFocusedCell(new TablePosition<>(tableView, row, column));
            }
        }

        /**
         * Convenience method for setting focus on a particular row or cell
         * using a {@link TablePosition}.
         * 
         * @param pos The table position where focus should be set.
         */
        public void focus(TablePosition pos) {
            if (pos == null) return;
            focus(pos.getRow(), pos.getTableColumn());
        }


        /***********************************************************************
         *                                                                     *
         * Public API                                                          *
         *                                                                     *
         **********************************************************************/

        /**
         * Tests whether the row / cell at the given location currently has the
         * focus within the TableView.
         */
        @Override public boolean isFocused(int row, TableColumn<S,?> column) {
            if (row < 0 || row >= getItemCount()) return false;

            TablePosition cell = getFocusedCell();
            boolean columnMatch = column == null || column.equals(cell.getTableColumn());

            return cell.getRow() == row && columnMatch;
        }

        /**
         * Causes the item at the given index to receive the focus. This does not
         * cause the current selection to change. Updates the focusedItem and
         * focusedIndex properties such that <code>focusedIndex = -1</code> unless
         * <pre><code>0 <= index < model size</code></pre>.
         *
         * @param index The index of the item to get focus.
         */
        @Override public void focus(int index) {
            if (index < 0 || index >= getItemCount()) {
                setFocusedCell(EMPTY_CELL);
            } else {
                setFocusedCell(new TablePosition<>(tableView, index, null));
            }
        }

        /**
         * Attempts to move focus to the cell above the currently focused cell.
         */
        @Override public void focusAboveCell() {
            TablePosition cell = getFocusedCell();

            if (getFocusedIndex() == -1) {
                focus(getItemCount() - 1, cell.getTableColumn());
            } else if (getFocusedIndex() > 0) {
                focus(getFocusedIndex() - 1, cell.getTableColumn());
            }
        }

        /**
         * Attempts to move focus to the cell below the currently focused cell.
         */
        @Override public void focusBelowCell() {
            TablePosition cell = getFocusedCell();
            if (getFocusedIndex() == -1) {
                focus(0, cell.getTableColumn());
            } else if (getFocusedIndex() != getItemCount() -1) {
                focus(getFocusedIndex() + 1, cell.getTableColumn());
            }
        }

        /**
         * Attempts to move focus to the cell to the left of the currently focused cell.
         */
        @Override public void focusLeftCell() {
            TablePosition cell = getFocusedCell();
            if (cell.getColumn() <= 0) return;
            focus(cell.getRow(), getTableColumn(cell.getTableColumn(), -1));
        }

        /**
         * Attempts to move focus to the cell to the right of the the currently focused cell.
         */
        @Override public void focusRightCell() {
            TablePosition cell = getFocusedCell();
            if (cell.getColumn() == getColumnCount() - 1) return;
            focus(cell.getRow(), getTableColumn(cell.getTableColumn(), 1));
        }



         /***********************************************************************
         *                                                                     *
         * Private Implementation                                              *
         *                                                                     *
         **********************************************************************/

        private int getColumnCount() {
            return tableView.getVisibleLeafColumns().size();
        }

        // Gets a table column to the left or right of the current one, given an offset
        private TableColumn<S,?> getTableColumn(TableColumn<S,?> column, int offset) {
            int columnIndex = tableView.getVisibleLeafIndex(column);
            int newColumnIndex = columnIndex + offset;
            return tableView.getVisibleLeafColumn(newColumnIndex);
        }
    }

}