package application.containers;

import java.io.Serializable;

/**
 * Obalová třída pro efektivní aktualizaci celočíselné hodnoty uložené v mapě.
 * 
 * @author Petr Kozler
 */
public class MutableInt implements Serializable {

    /**
     * celočíselná hodnota
     */
    private int value;
    
    /**
     * Vytvoří obal pro celočíselnou hodnotu inicializovanou na nulu.
     */
    public MutableInt() {
        this(0);
    }
    
    /**
     * Vytvoří obal pro zadanou celočíselnou hodnotu.
     * 
     * @param value hodnota
     */
    public MutableInt(int value) {
        this.value = value;
    }

    /**
     * Inkrementuje uloženou hodnotu.
     */
    public void inc() {
        ++value;
    }
    
    /**
     * Dekrementuje uloženou hodnotu.
     */
    public void dec() {
        --value;
    }
    
    /**
     * Přičte zadanou hodnotu k uložené.
     * 
     * @param value hodnota
     */
    public void add(int value) {
        this.value += value;
    }

    /**
     * Odečte zadanou hodnotu od uložené
     * 
     * @param value hodnota
     */
    public void sub(int value) {
        this.value -= value;
    }

    /**
     * Vynásobí zadanou hodnotu s uloženou.
     * 
     * @param value hodnota
     */
    public void mul(int value) {
        this.value *= value;
    }

    /**
     * Vydělí uloženou hodnotu zadanou hodnotou.
     * 
     * @param value hodnota
     */
    public void div(int value) {
        this.value /= value;
    }

    /**
     * Určí zbytek po dělení uložené hodnoty zadanou hodnotou.
     * 
     * @param value hodnota
     */
    public void mod(int value) {
        this.value %= value;
    }

    /**
     * Vrátí uloženou hodnotu.
     * 
     * @return hodnota
     */
    public int get() {
        return value;
    }
    
    /**
     * Vrátí uloženou hodnotu jako desetinné číslo.
     * 
     * @return hodnota ve formátu desetinného čísla
     */
    public double getDecimal() {
        return (double) this.value;
    }

    /**
     * Vrátí podíl uložené a zadané hodnoty jako desetinné číslo.
     * 
     * @param value hodnota
     * @return hodnota ve formátu desetinného čísla
     */
    public double getDecimal(int value) {
        return ((double) this.value / (double) value);
    }

    /**
     * Vytvoří textovou reprezentaci číselné hodnoty.
     * 
     * @return textová reprezentace
     */
    @Override
    public String toString() {
        return Integer.toString(value);
    }
    
}
