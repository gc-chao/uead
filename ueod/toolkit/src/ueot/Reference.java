
package ueot;

/**
 * Abstract base class for reference objects. <br>
 * This class defines the operations common to all reference objects. <br>
 */
public abstract class Reference<T>
{
    /** The actual value. */
    public T value;

    /**
     * The default constructor with no parameter.
     */
    public Reference() { }

    /**
     * The default constructor with value parameter.
     * @param value the actual value
     */
    public Reference(T value)
    {
        this.value = value;
    }
}