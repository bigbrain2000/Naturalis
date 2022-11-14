package com.chs.naturalis.model;

import java.util.Objects;

public class Product {
    private int id;
    private String name;
    private double price;
    private int quantity;
    private String description;
    private String category;


    public Product(int id) {
        this.id = id;

    }

    public Product(String name, double price, int quantity, String description, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.description = description;
        this.category = category;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id && Double.compare(product.price, price) == 0 && quantity == product.quantity && Objects.equals(name, product.name) && Objects.equals(description, product.description) && Objects.equals(category, product.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, quantity, description, category);
    }
}
