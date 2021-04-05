package com.innerCat.pillBox.objects;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.util.Objects;

/**
 * The type Item.
 */
//table name is 'items'
@Entity(tableName = "items")
public class Item  {

    //set the primary key to auto generate and increment
    @PrimaryKey(autoGenerate = true)
    //placeholder id
    private int id = 0;
    private String name;
    private LocalDate lastUsed;
    @ColumnInfo(defaultValue = "0")
    private int stock = 0;
    private int viewHolderPosition;
    private boolean showInWidget = false;
    @Ignore
    private Refill expiringRefill = null;

    /**
     * Instantiates a new Item.
     *
     * @param name the name
     */
    public Item(String name) {
        this.name = name;
    }

    /**
     * Instantiates a new Item.
     *
     * @param name  the name
     * @param stock the stock
     */
    @Ignore
    public Item(String name, int stock, boolean showInWidget) {
        this.name = name;
        this.stock = stock;
        this.showInWidget = showInWidget;
    }

    /**
     * Instantiates a new Item.
     *
     * @param id           the id
     * @param name         the name
     * @param stock        the stock
     * @param showInWidget the show in widget
     */
    @Ignore
    public Item(int id, String name, int stock, boolean showInWidget) {
        this.id = id;
        this.name = name;
        this.stock = stock;
        this.showInWidget = showInWidget;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId( int id ) {
        this.id = id;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName( String name ) {
        this.name = name;
    }

    /**
     * Gets last updated.
     *
     * @return the last updated
     */
    public LocalDate getLastUsed() {
        return lastUsed;
    }

    /**
     * Sets last updated.
     *
     * @param lastUsed the last updated
     */
    public void setLastUsed( LocalDate lastUsed ) {
        this.lastUsed = lastUsed;
    }

    /**
     * Gets stock.
     *
     * @return the stock
     */
    public int getStock() {
        return stock;
    }

    /**
     * Sets stock.
     *
     * @param stock the stock
     */
    public void setStock( int stock ) {
        this.stock = stock;
    }

    /**
     * Decrements the stock and sets the last used to now
     */
    public void decrementStock() {
        if (this.stock > 0) {
            setLastUsed(LocalDate.now());
            this.stock = this.stock - 1;
        }
    }

    /**
     * Decrement stock by an amount
     *
     * @param num the num
     */
    public void decrementStockBy(int num) {
        this.stock = this.stock-num;
        if (this.stock < 0) {
            this.stock = 0;
        }
    }

    /**
     * Gets view holder position.
     *
     * @return the view holder position
     */
    public int getViewHolderPosition() {
        return viewHolderPosition;
    }

    /**
     * Sets view holder position.
     *
     * @param viewHolderPosition the view holder position
     */
    public void setViewHolderPosition( int viewHolderPosition ) {
        this.viewHolderPosition = viewHolderPosition;
    }

    /**
     * Is show in widget boolean.
     *
     * @return the boolean
     */
    public boolean getShowInWidget() {
        return showInWidget;
    }

    /**
     * Sets show in widget.
     *
     * @param showInWidget the show in widget
     */
    public void setShowInWidget( boolean showInWidget ) {
        this.showInWidget = showInWidget;
    }

    /**
     * Refill.
     *
     * @param refillAmount the refill amount
     */
    public void refillByAmount( int refillAmount) {
        this.stock = this.stock+refillAmount;
    }


    /**
     * Gets expiring refill.
     *
     * @return the expiring refill
     */
    public Refill getExpiringRefill() {
        return expiringRefill;
    }

    /**
     * Sets expiring refill.
     *
     * @param expiringRefill the expiring refill
     */
    public void setExpiringRefill( Refill expiringRefill ) {
        this.expiringRefill = expiringRefill;
    }

    @Override
    public boolean equals( Object o ) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id == item.id &&
                stock == item.stock &&
                Objects.equals(name, item.name) &&
                Objects.equals(lastUsed, item.lastUsed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, lastUsed, stock);
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastUpdated=" + lastUsed +
                ", stock=" + stock +
                '}';
    }
}
