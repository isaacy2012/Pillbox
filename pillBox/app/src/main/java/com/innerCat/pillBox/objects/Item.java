package com.innerCat.pillBox.objects;

import static java.time.temporal.ChronoUnit.DAYS;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.innerCat.pillBox.util.Assertions;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * The type Item.
 */
//table name is 'items'
@Entity(tableName = "items")
public class Item implements Serializable {

    /**
     * The Id.
     */
//set the primary key to auto generate and increment
    @PrimaryKey(autoGenerate = true)
    //placeholder id
    private int id = 0;
    /**
     * The Name.
     */
    private String name;
    /**
     * The Last used.
     */
    private LocalDate lastUsed;

    /**
     * The Last used time.
     */
    private LocalTime lastUsedTime;
    /**
     * The Stock.
     */
    @ColumnInfo(defaultValue = "0")
    private int rawStock = 0;
    /**
     * The View holder position.
     */
    private int viewHolderPosition;
    /**
     * The Show in widget.
     */
    private boolean showInWidget = false;
    /**
     * The Color.
     */
    private int color = ColorItem.NO_COLOR;
    /**
     * The Auto dec start date.
     */
    private LocalDate autoDecStartDate = null;
    /**
     * How many to decrement each day that it is decremented
     */
    private int autoDecPerDay = 0;
    /**
     * Decremented every n days
     */
    private int autoDecNDays = 0;
    /**
     * The Expiring refill.
     */
    @Ignore
    private Refill expiringRefill = null;

    /**
     * Instantiates a new Item.
     *
     * @param name the name
     */
    public Item( String name ) {
        this.name = name;
    }

    /**
     * Instantiates a new Item.
     *
     * @param name             the name
     * @param rawStock         the stock
     * @param color            the color
     * @param showInWidget     the show in widget
     * @param autoDecStartDate the auto dec start date
     * @param autoDecPerDay    the auto dec per day
     * @param autoDecNDays     the auto dec n days
     */
    @Ignore
    public Item( String name,
                 int rawStock,
                 int color,
                 boolean showInWidget,
                 LocalDate autoDecStartDate,
                 int autoDecPerDay,
                 int autoDecNDays ) {
        this.name = name;
        this.rawStock = rawStock;
        this.showInWidget = showInWidget;
        this.color = color;
        this.autoDecStartDate = autoDecStartDate;
        this.autoDecPerDay = autoDecPerDay;
        this.autoDecNDays = autoDecNDays;
    }


    /**
     * Instantiates a new Item when there is no autodecrement
     *
     * @param name         the name
     * @param rawStock     the stock
     * @param color        the color
     * @param showInWidget the show in widget
     */
    @Ignore
    public Item( String name, int rawStock, int color, boolean showInWidget ) {
        this.name = name;
        this.rawStock = rawStock;
        this.showInWidget = showInWidget;
        this.color = color;
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
     * Gets last used time.
     *
     * @return the last used time
     */
    public LocalTime getLastUsedTime() {
        return lastUsedTime;
    }

    /**
     * Sets last used time.
     *
     * @param lastUsedTime the last used time
     */
    public void setLastUsedTime(LocalTime lastUsedTime) {
        this.lastUsedTime = lastUsedTime;
    }

    /**
     * Gets stock.
     *
     * @return the stock
     */
    public int getRawStock() {
        return rawStock;
    }

    /**
     * Flatten auto dec if.
     *
     * @param newAutoDec the new auto dec
     * @param perDay     the per day
     * @param nDays      the n days
     */
    public void flattenAndSetAutoDecIf( boolean newAutoDec, int perDay, int nDays) {
        boolean diffAutoDec = newAutoDec != (this.autoDecStartDate != null);
        boolean diffPerDay = perDay != this.autoDecPerDay;
        boolean diffNDays = nDays != this.autoDecNDays;
        //if there is a change
        if (diffAutoDec || diffPerDay || diffNDays) {
            this.rawStock = getCalculatedStock();
            if (newAutoDec) {
                this.autoDecStartDate = LocalDate.now();
                Assertions.assertTrue(perDay > 0);
                Assertions.assertTrue(nDays > 0);
                this.autoDecPerDay = perDay;
                this.autoDecNDays = nDays;
            } else {
                this.autoDecStartDate = null;
                this.autoDecPerDay = 0;
                this.autoDecNDays = 0;
            }
        }
    }

    /**
     * Gets calculated stock.
     *
     * @return the calculated stock
     */
    public int getCalculatedStock() {
        if (autoDecStartDate == null) {
            return rawStock;
        } else {
            long diff = DAYS.between(autoDecStartDate, LocalDate.now());
            long daysTaken = diff / autoDecNDays;
            long times = (autoDecPerDay * daysTaken);
            return (int) (rawStock - times);
        }
    }

    /**
     * Sets stock.
     *
     * @param rawStock the stock
     */
    public void setRawStock( int rawStock ) {
        this.rawStock = rawStock;
    }

    /**
     * Decrements the stock and sets the last used to now
     */
    public void decrementStock() {
        if (this.rawStock > 0) {
            setLastUsed(LocalDate.now());
            setLastUsedTime(LocalTime.now());
            this.rawStock = this.rawStock - 1;
        }
    }

    /**
     * Decrement stock by an amount
     *
     * @param num the num
     */
    public void decrementStockBy( int num ) {
        this.rawStock = this.rawStock - num;
        if (this.rawStock < 0) {
            this.rawStock = 0;
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
    public void refillByAmount( int refillAmount ) {
        //reset stock to 0 first by incrementing rawStock by the calculatedStock amount
        if (getCalculatedStock() < 0) {
            this.rawStock -= getCalculatedStock();
        }
        this.rawStock = this.rawStock + refillAmount;
    }

    /**
     * Gets color.
     *
     * @return the color
     */
    public int getColor() {
        return color;
    }

    /**
     * Sets color.
     *
     * @param color the color
     */
    public void setColor( int color ) {
        this.color = color;
    }

    /**
     * Gets auto dec n days.
     *
     * @return the auto dec n days
     */
    public int getAutoDecNDays() {
        return autoDecNDays;
    }

    /**
     * Is auto dec boolean.
     *
     * @return the boolean
     */
    public boolean isAutoDec() {
        return autoDecStartDate != null;
    }

    /**
     * Sets auto dec n days.
     *
     * @param autoDecNDays the auto dec n days
     */
    public void setAutoDecNDays( int autoDecNDays ) {
        this.autoDecNDays = autoDecNDays;
    }

    /**
     * Gets auto dec start date.
     *
     * @return the auto dec start date
     */
    public LocalDate getAutoDecStartDate() {
        return autoDecStartDate;
    }

    /**
     * Gets auto dec per day.
     *
     * @return the auto dec per day
     */
    public int getAutoDecPerDay() {
        return autoDecPerDay;
    }

    /**
     * Sets auto dec per day.
     *
     * @param autoDecPerDay the auto dec per day
     */
    public void setAutoDecPerDay( int autoDecPerDay ) {
        this.autoDecPerDay = autoDecPerDay;
    }

    /**
     * Sets auto dec start date.
     *
     * @param autoDecStartDate the auto dec start date
     */
    public void setAutoDecStartDate( LocalDate autoDecStartDate ) {
        this.autoDecStartDate = autoDecStartDate;
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
                rawStock == item.rawStock &&
                Objects.equals(name, item.name) &&
                Objects.equals(lastUsed, item.lastUsed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, lastUsed, rawStock);
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastUpdated=" + lastUsed +
                ", rawStock=" + rawStock +
                ", calculatedStock=" + getCalculatedStock() +
                '}';
    }
}

