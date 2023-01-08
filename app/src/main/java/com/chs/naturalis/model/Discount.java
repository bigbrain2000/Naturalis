package com.chs.naturalis.model;

import java.util.Objects;

/**
 * Model used for implementing the discount feature.
 */
public class Discount {

    private String name;

    public Discount() {
    }

    public Discount(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Discount discount = (Discount) o;
        return Objects.equals(name, discount.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
